package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.entities.custom.HyenaGoals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Hyena behaviour. The custom AI goals live in {@link HyenaGoals}. */
public class HyenaEntity extends TamableAnimal {

    private static final EntityDataAccessor<Boolean> DATA_FRENZIED =
            SynchedEntityData.defineId(HyenaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final ResourceLocation PACK_BONUS_ID =
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "hyena_pack_bonus");
    private static final EntityDataAccessor<Integer> DATA_COLLAR =
            SynchedEntityData.defineId(HyenaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_RESTING =
            SynchedEntityData.defineId(HyenaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_STALKING =
            SynchedEntityData.defineId(HyenaEntity.class, EntityDataSerializers.BOOLEAN);

    private static final byte LAUGH_EVENT = 80;
    private static final String BONE_DAY_KEY = "NTHyenaBoneDay";

    private int laughTicks;     // client animation
    private int nextCallTime;   // pack call cooldown
    private int nextHuntTime;   // full belly after catching prey
    private int chewTicks;      // time spent with stolen food

    public HyenaEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    // --- DATA ---

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FRENZIED, false);
        builder.define(DATA_COLLAR, DyeColor.RED.getId());
        builder.define(DATA_RESTING, false);
        builder.define(DATA_STALKING, false);
    }

    public boolean isFrenzied() {
        return this.entityData.get(DATA_FRENZIED);
    }

    public void setFrenzied(boolean frenzied) {
        this.entityData.set(DATA_FRENZIED, frenzied);
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLLAR));
    }

    public void setCollarColor(DyeColor color) {
        this.entityData.set(DATA_COLLAR, color.getId());
    }

    public boolean isResting() {
        return this.entityData.get(DATA_RESTING);
    }

    void setResting(boolean resting) {
        this.entityData.set(DATA_RESTING, resting);
    }

    public boolean isStalking() {
        return this.entityData.get(DATA_STALKING);
    }

    void setStalking(boolean stalking) {
        this.entityData.set(DATA_STALKING, stalking);
    }

    public int getLaughTicks() {
        return this.laughTicks;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsFrenzied", this.isFrenzied());
        compound.putInt("CollarColor", this.getCollarColor().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFrenzied(compound.getBoolean("IsFrenzied"));
        if (compound.contains("CollarColor")) {
            this.setCollarColor(DyeColor.byId(compound.getInt("CollarColor")));
        }
    }

    // --- AI ---

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D) { // only pups flee
            @Override
            public boolean canUse() {
                return HyenaEntity.this.isBaby() && super.canUse();
            }
        });
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new HyenaGoals.Stalk(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(4, new HyenaGoals.StealFood(this));
        this.goalSelector.addGoal(5, new HyenaGoals.Scavenge(this));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new TemptGoal(this, 1.1D, stack -> stack.is(Items.BONE) || this.isFood(stack), false));
        this.goalSelector.addGoal(9, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(10, new HyenaGoals.RestInShade(this));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(13, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        // Players who crowd a wild pup
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 20, true, false,
                this::isThreatToPup));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, true));
        // Wild adults also hunt zombies
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Zombie.class, 10, true, false,
                this::isHuntableZombie));
        // Wild, frenzied adults hunt players
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                player -> this.isFrenzied() && !this.isTame() && !this.isBaby()));
        // Wild adults catch rabbits and chickens at night
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Animal.class, 10, true, false,
                this::isHuntablePrey));
    }

    // --- FRIEND OR FOE ---

    private boolean isHuntableZombie(LivingEntity target) {
        if (this.isTame() || this.isBaby()) return false;
        if (target instanceof ZombifiedPiglin) return false; // neutral, leave them be
        // Don't ruin a villager the player is curing
        return !(target instanceof ZombieVillager villager && villager.isConverting());
    }

    /** Your own side: pets with the same owner, or (for wild hyenas) other wild hyenas. */
    private boolean isPackmate(LivingEntity other) {
        if (this.isTame()) {
            return this.getOwnerUUID() != null && other instanceof OwnableEntity pet
                    && this.getOwnerUUID().equals(pet.getOwnerUUID());
        }
        return other instanceof HyenaEntity hyena && !hyena.isTame();
    }

    private static boolean isPrey(LivingEntity target) {
        return target instanceof Rabbit || target instanceof Chicken;
    }

    // Night only, one catch per 5 minutes, and never babies or named animals
    private boolean isHuntablePrey(LivingEntity target) {
        return isPrey(target) && !this.isTame() && !this.isBaby() && this.level().isNight()
                && this.tickCount >= this.nextHuntTime && !target.isBaby() && !target.hasCustomName();
    }

    private boolean isThreatToPup(LivingEntity player) {
        if (this.isTame() || this.isBaby()) return false;
        return !this.level().getEntitiesOfClass(HyenaEntity.class, this.getBoundingBox().inflate(8.0D),
                pup -> pup.isBaby() && !pup.isTame() && pup.distanceToSqr(player) < 16.0D).isEmpty();
    }

    // Checked by every targeting goal, including while a fight is already going on
    @Override
    public boolean canAttack(LivingEntity target) {
        return !this.isBaby() && !this.isPackmate(target) && super.canAttack(target);
    }

    // What the pack will join in on when the owner fights
    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof Creeper || target instanceof Ghast || target instanceof ArmorStand) return false;
        if (target instanceof OwnableEntity pet && pet.getOwnerUUID() != null) return false; // anyone's pet
        if (target instanceof Player victim && owner instanceof Player attacker) return attacker.canHarmPlayer(victim);
        return !this.isPackmate(target);
    }

    // --- PACK CALL ---

    private List<HyenaEntity> wildAdultsNearby() {
        return this.level().getEntitiesOfClass(HyenaEntity.class, this.getBoundingBox().inflate(16.0D),
                other -> other != this && other.isAlive() && !other.isTame() && !other.isBaby());
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        boolean fresh = target != null && this.getTarget() == null;
        super.setTarget(target);
        if (fresh && this.getTarget() == target && !this.level().isClientSide) {
            this.setResting(false);
            this.callPack(target);
        }
    }

    // A wild hyena that picks a fight laughs, and idle wild hyenas nearby join in
    private void callPack(LivingEntity target) {
        if (this.isTame() || this.isBaby() || isPrey(target) || this.tickCount < this.nextCallTime) return;
        this.nextCallTime = this.tickCount + 200;
        this.laugh();
        for (HyenaEntity other : this.wildAdultsNearby()) {
            if (other.getTarget() == null && other.canAttack(target)) {
                other.nextCallTime = other.tickCount + 200; // stops the call echoing forever
                other.setTarget(target);
            }
        }
    }

    private void laugh() {
        this.playSound(SoundEvents.WITCH_CELEBRATE, 1.0F, this.getVoicePitch());
        this.level().broadcastEntityEvent(this, LAUGH_EVENT);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == LAUGH_EVENT) {
            this.laughTicks = 20;
        } else {
            super.handleEntityEvent(id);
        }
    }

    // --- SPAWNING & TICKING ---

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        // 5% of adults spawn frenzied
        if (!this.isBaby() && this.random.nextFloat() < 0.05F) {
            this.setFrenzied(true);
        }
        return data;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide && this.laughTicks > 0) this.laughTicks--;
        if (this.level().isClientSide && this.isFrenzied() && !this.isTame() && this.random.nextInt(20) == 0) {
            this.level().addParticle(ParticleTypes.ANGRY_VILLAGER,
                    this.getRandomX(0.5D), this.getRandomY() + 0.5D, this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        // Called packmates have no targeting goal of their own, so let go of lost targets here
        LivingEntity target = this.getTarget();
        if (target != null && this.tickCount % 20 == 0
                && (!target.isAlive() || this.distanceToSqr(target) > 1600.0D || !this.canAttack(target))) {
            this.setTarget(null);
        }

        // Eat stolen food after a while
        ItemStack mouth = this.getMainHandItem();
        if (!this.isTame() && !mouth.isEmpty() && ++this.chewTicks >= 400) {
            var food = mouth.getFoodProperties(this);
            this.heal(food != null ? food.nutrition() : 2.0F);
            this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.chewTicks = 0;
        }

        if (this.tickCount % 100 == 0) this.tryDigUpBone();
    }

    void holdStolenFood(ItemStack stack) {
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
        this.setDropChance(EquipmentSlot.MAINHAND, 2.0F); // always dropped on death
        this.chewTicks = 0;
    }

    private void dropStolenFood() {
        ItemStack mouth = this.getMainHandItem();
        if (!mouth.isEmpty()) {
            this.spawnAtLocation(mouth.copy());
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    // A tamed hyena near its owner sometimes digs up a bone.
    // Limited to one per day PER PLAYER, so owning many hyenas doesn't make a farm.
    private void tryDigUpBone() {
        if (!this.isTame() || this.isBaby() || this.isOrderedToSit() || this.getTarget() != null || !this.onGround()) return;
        if (!(this.getOwner() instanceof Player owner) || this.distanceToSqr(owner) > 144.0D) return;
        BlockState ground = this.level().getBlockState(this.blockPosition().below());
        if (!ground.is(BlockTags.DIRT) && !ground.is(BlockTags.SAND)) return;
        if (this.random.nextInt(24) != 0) return; // about every 2 minutes of the right conditions

        long today = this.level().getGameTime() / 24000L;
        CompoundTag saved = owner.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        if (saved.contains(BONE_DAY_KEY) && saved.getLong(BONE_DAY_KEY) == today) return;
        saved.putLong(BONE_DAY_KEY, today);
        owner.getPersistentData().put(Player.PERSISTED_NBT_TAG, saved);

        this.spawnAtLocation(Items.BONE);
        this.playSound(SoundEvents.GRAVEL_BREAK, 1.0F, 1.0F);
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground),
                    this.getX(), this.getY() + 0.1D, this.getZ(), 20, 0.25D, 0.05D, 0.25D, 0.05D);
        }
    }

    // --- COMBAT ---

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide && this.isAlive()) {
            this.setOrderedToSit(false); // a hit pet stands up, like a wolf
            this.setResting(false);
            if (!this.isTame()) this.dropStolenFood();

            // Adults come to defend a hurt pup
            if (this.isBaby() && !this.isTame() && source.getEntity() instanceof LivingEntity attacker) {
                for (HyenaEntity adult : this.wildAdultsNearby()) {
                    if (adult.canAttack(attacker)) adult.setTarget(attacker);
                }
            }
        }
        return hurt;
    }

    @Override
    public boolean killedEntity(ServerLevel level, LivingEntity victim) {
        if (isPrey(victim)) {
            this.nextHuntTime = this.tickCount + 6000;
            this.heal(4.0F);
        }
        return super.killedEntity(level, victim);
    }

    // +1 damage per packmate nearby (up to 4), +2 while frenzied.
    // Added as a temporary bonus so knockback and other normal attack behaviour still work.
    @Override
    public boolean doHurtTarget(Entity target) {
        AttributeInstance damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        int pack = this.level().getEntitiesOfClass(HyenaEntity.class, this.getBoundingBox().inflate(8.0D),
                other -> other != this && other.isAlive() && !other.isBaby() && this.isPackmate(other)).size();
        double bonus = Math.min(pack, 4) + (this.isFrenzied() && !this.isTame() ? 2 : 0);

        damage.addOrUpdateTransientModifier(
                new AttributeModifier(PACK_BONUS_ID, bonus, AttributeModifier.Operation.ADD_VALUE));
        boolean hit = super.doHurtTarget(target);
        damage.removeModifier(PACK_BONUS_ID);

        if (hit && target instanceof LivingEntity living) {
            if (this.random.nextFloat() < 0.25F) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1), this);
            }
            if (this.random.nextFloat() < 0.15F) {
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1), this);
                this.playSound(SoundEvents.WOLF_GROWL, 1.0F, 0.8F);
            }
        }
        return hit;
    }

    // --- INTERACTIONS ---

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean client = this.level().isClientSide;

        if (!this.isTame()) {
            if (stack.is(Items.BONE) || stack.is(Items.ROTTEN_FLESH)) {
                if (!client) this.tryTame(player, stack);
                return InteractionResult.sidedSuccess(client);
            }
            // Wild hyenas can't be bred
            return this.isFood(stack) ? InteractionResult.PASS : super.mobInteract(player, hand);
        }

        if (this.isFood(stack)) {
            if (this.getHealth() < this.getMaxHealth()) {
                if (!client) {
                    var food = stack.getFoodProperties(this);
                    this.heal(food != null ? food.nutrition() : 2.0F);
                    stack.consume(1, player);
                    this.playSound(SoundEvents.FOX_EAT, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(client);
            }
            return super.mobInteract(player, hand); // full health: breeding
        }

        if (!this.isOwnedBy(player)) {
            return super.mobInteract(player, hand);
        }

        // Dye the collar
        if (stack.getItem() instanceof DyeItem dye && dye.getDyeColor() != this.getCollarColor()) {
            if (!client) {
                this.setCollarColor(dye.getDyeColor());
                stack.consume(1, player);
            }
            return InteractionResult.sidedSuccess(client);
        }

        // Sneak + bone on a sitting hyena: crush it into fertilizer
        if (stack.is(Items.BONE) && player.isShiftKeyDown() && this.isOrderedToSit()) {
            if (!client) this.crushBone(player, stack);
            return InteractionResult.sidedSuccess(client);
        }

        if (!client) {
            boolean sit = !this.isOrderedToSit();
            this.setOrderedToSit(sit);
            this.jumping = false;
            this.navigation.stop();
            this.setTarget(null);
            player.displayClientMessage(Component.translatable(
                    sit ? "message.novamterram.hyena.waiting" : "message.novamterram.hyena.following"), true);
        }
        return InteractionResult.sidedSuccess(client);
    }

    private void tryTame(Player player, ItemStack stack) {
        // Bones work better than rotten flesh; frenzied hyenas are harder
        int odds = stack.is(Items.BONE) ? (this.isFrenzied() ? 5 : 3) : (this.isFrenzied() ? 10 : 6);
        stack.consume(1, player);

        if (this.random.nextInt(odds) == 0) {
            this.dropStolenFood();
            this.tame(player);
            this.setResting(false);
            this.setFrenzied(false); // taming cures the frenzy
            this.setTarget(null);
            this.navigation.stop();
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte) 7);
            player.displayClientMessage(Component.translatable("message.novamterram.hyena.trust_success"), true);
        } else {
            this.level().broadcastEntityEvent(this, (byte) 6);
            player.displayClientMessage(Component.translatable("message.novamterram.hyena.trust_fail"), true);
        }
    }

    // One bone fertilizes up to 3 nearby plants: the same value as crafting it into bone meal
    private void crushBone(Player player, ItemStack bone) {
        int used = 0;
        for (BlockPos pos : BlockPos.betweenClosed(this.blockPosition().offset(-1, -1, -1),
                this.blockPosition().offset(1, 1, 1))) {
            if (used >= 3) break;
            if (BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), this.level(), pos, player)) {
                this.level().levelEvent(1505, pos, 15); // green growth particles
                used++;
            }
        }
        if (used > 0) { // nothing to grow = bone is kept
            bone.consume(1, player);
            this.playSound(SoundEvents.SKELETON_STEP, 1.0F, 0.5F);
        }
    }

    // --- BREEDING ---

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.MEAT);
    }

    @Override
    public boolean canMate(Animal other) {
        return other != this && other instanceof HyenaEntity mate
                && this.isTame() && mate.isTame() && !mate.isInSittingPose()
                && this.isInLove() && mate.isInLove();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        HyenaEntity baby = NTEntities.HYENA.get().create(level);
        if (baby != null && this.isTame()) {
            baby.setOwnerUUID(this.getOwnerUUID());
            baby.setTame(true, true);
            baby.setCollarColor(otherParent instanceof HyenaEntity mate && this.random.nextBoolean()
                    ? mate.getCollarColor() : this.getCollarColor());
        }
        return baby;
    }

    // --- SOUNDS (vanilla placeholders) ---

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isResting()) return null;
        // Mostly yips, now and then a "laugh"
        return this.random.nextInt(5) == 0 ? SoundEvents.WITCH_CELEBRATE : SoundEvents.FOX_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.85F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 200;
    }
}
