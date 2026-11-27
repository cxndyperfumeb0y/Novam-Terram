package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.neoforged.neoforge.event.EventHooks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * The Faun: a shy forest deer that watches over its surroundings.
 *
 * - Bucks and does. Bucks shed their antlers now and then, and sometimes spar.
 * - Freezes when a stranger watches it, and bolts if they get close or keep staring.
 * - Sounds the alarm for monsters, and the whole herd runs when one is hurt.
 * - Feed it apples to earn its trust. A trusted faun brings one gift a day.
 * - Eating a flower gives it a crown, and nearby friends slowly heal.
 * - Sleeps lying down at night while one herd member keeps watch.
 * - Does guard fawns, and herds follow a leading buck.
 * - Coats vary by biome, with a very rare white stag.
 * - Can lead a friend to hives, mushrooms or flowers, and helps plants grow while crowned.
 */
public class FaunEntity extends Animal {
    private static final EntityDataAccessor<Boolean> IS_BUCK = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_FLORAL = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_ANTLERS = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_HIDING = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RESTING = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SPARRING = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> COAT = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CROWN_COLOR = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.INT);

    private static final byte KICK_EVENT = 70;
    private static final Ingredient FOOD = Ingredient.of(Items.APPLE, Items.WHEAT);
    private static final int DAY = 24000;

    private final Set<UUID> trusted = new HashSet<>();
    private int floralTimer;
    private int shedTimer;    // time until antlers drop
    private int regrowTimer;  // time until they grow back
    private int alarmReadyAt;
    private int spookedUntil;
    private Vec3 spookedFrom = Vec3.ZERO;
    private int grazeTicks;   // client animation
    private int kickTicks;    // client animation

    // Blessing: how much this faun may still grow today
    private BlockState eatenFlower = Blocks.DANDELION.defaultBlockState();
    private long nextBlessingAt;
    private int flowersToPlant;
    private int plantsToGrow;

    // Guiding a friend
    @Nullable
    private BlockPos guideTarget;
    @Nullable
    private UUID guideFor;
    private int nextGuideAt;

    public FaunEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.shedTimer = randomShedTime();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SAFE_FALL_DISTANCE, 6.0D);
    }

    @Override
    protected void registerGoals() {
        // Danger first. A doe with a fawn stands her ground before anything else.
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FaunGoals.ProtectFawnGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.8D));
        this.goalSelector.addGoal(2, new FaunGoals.SpookedGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 12.0F, 1.4D, 1.8D,
                entity -> entity instanceof Player player && player.isSprinting() && !player.isCreative()
                        && !player.isSpectator() && !this.trusts(player.getUUID())));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 12.0F, 1.4D, 1.8D,
                entity -> entity instanceof Wolf wolf && !wolf.isTame()));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, HyenaEntity.class, 12.0F, 1.4D, 1.8D,
                entity -> entity instanceof HyenaEntity hyena && !hyena.isTame()));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, BrownBearEntity.class, 14.0F, 1.4D, 1.8D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, SpectacledBearEntity.class, 12.0F, 1.4D, 1.8D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, PolarBear.class, 12.0F, 1.4D, 1.8D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Monster.class, 8.0F, 1.3D, 1.7D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, SilverFoxEntity.class, 6.0F, 1.1D, 1.4D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, DesertFoxEntity.class, 6.0F, 1.1D, 1.4D));
        this.goalSelector.addGoal(4, new FaunGoals.FreezeGoal(this));

        // Family and friends
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new FaunGoals.GuideGoal(this));
        this.goalSelector.addGoal(7, new TemptGoal(this, 1.1D, FOOD, false));
        this.goalSelector.addGoal(7, new FollowParentGoal(this, 1.2D));
        this.goalSelector.addGoal(8, new FaunGoals.GiftGoal(this));

        // Daily life
        this.goalSelector.addGoal(9, new FaunGoals.SparGoal(this));
        this.goalSelector.addGoal(10, new FaunGoals.ShelterGoal(this));
        this.goalSelector.addGoal(10, new FaunGoals.TreatGoal(this));
        this.goalSelector.addGoal(11, new FaunGoals.RestGoal(this));
        this.goalSelector.addGoal(12, new EatBlockGoal(this));
        this.goalSelector.addGoal(13, new FaunGoals.HerdGoal(this));
        this.goalSelector.addGoal(14, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(15, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(16, new RandomLookAroundGoal(this));
    }

    // --- TICK ---

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) {
            if (this.grazeTicks > 0) this.grazeTicks--;
            if (this.kickTicks > 0) this.kickTicks--;
            if (this.isFloral() && this.random.nextInt(8) == 0) {
                this.level().addParticle(ParticleTypes.CHERRY_LEAVES, this.getRandomX(0.5D),
                        this.getRandomY() + 0.5D, this.getRandomZ(0.5D), 0, 0.02, 0);
            }
            return;
        }
        if (!this.isAlive()) return;

        tickAntlers();

        if (this.floralTimer > 0) {
            if (--this.floralTimer == 0) this.setFloral(false);
            if (this.tickCount % 40 == 0) healFriends();
            if (this.tickCount % 100 == 0) blessPlants();
        }

        // Spread out over time so a big herd doesn't all check on the same tick
        if ((this.tickCount + this.getId()) % 40 == 0 && !this.isBaby() && this.tickCount >= this.alarmReadyAt) {
            checkForDanger();
        }
    }

    private int randomShedTime() {
        return DAY * (5 + this.random.nextInt(5)); // 5-9 days
    }

    private void tickAntlers() {
        if (!this.isBuck() || this.isBaby()) return;
        if (this.hasAntlers()) {
            if (--this.shedTimer <= 0) {
                this.setHasAntlers(false);
                this.regrowTimer = DAY * 3;
                this.spawnAtLocation(new ItemStack(Items.BONE_MEAL, 2));
                this.playSound(SoundEvents.ITEM_BREAK, 1.0F, 0.9F);
            }
        } else if (--this.regrowTimer <= 0) {
            this.setHasAntlers(true);
            this.shedTimer = randomShedTime();
            this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0F, 1.1F);
        }
    }

    // --- DANGER ---

    static boolean isThreat(Mob mob) {
        if (mob instanceof Wolf wolf) return !wolf.isTame();
        if (mob instanceof HyenaEntity hyena) return !hyena.isTame();
        // Calm endermen and zombified piglins are no reason to panic
        return mob instanceof Enemy && !(mob instanceof NeutralMob && mob.getTarget() == null);
    }

    private void checkForDanger() {
        // Line of sight stops monsters in caves underneath setting it off
        List<Mob> threats = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(16.0D),
                mob -> mob.isAlive() && isThreat(mob) && this.hasLineOfSight(mob));
        if (threats.isEmpty()) return;

        this.alarmReadyAt = this.tickCount + 800;
        this.playSound(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(0).value(), 1.5F, 1.5F);
        Vec3 from = threats.get(0).position();
        this.spook(from, 60);
        this.spookHerd(from);

        // Friends get a warning and can see what was spotted
        boolean friendNear = false;
        for (Player player : this.level().players()) {
            if (this.trusts(player.getUUID()) && player.distanceToSqr(this) < 576.0D) {
                player.displayClientMessage(Component.translatable("message.novamterram.faun.alarm"), true);
                friendNear = true;
            }
        }
        if (friendNear) {
            for (Mob threat : threats) {
                threat.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false), this);
            }
        }
    }

    /** Wakes this faun and sends it running away from a spot. */
    void spook(Vec3 from, int ticks) {
        this.spookedFrom = from;
        this.spookedUntil = this.tickCount + ticks;
        this.setResting(false);
    }

    void spookHerd(Vec3 from) {
        for (FaunEntity other : this.nearbyFauns(16.0D)) {
            other.spook(from, 60);
        }
    }

    boolean isSpooked() {
        return this.tickCount < this.spookedUntil;
    }

    Vec3 spookedFrom() {
        return this.spookedFrom;
    }

    List<FaunEntity> nearbyFauns(double range) {
        return this.level().getEntitiesOfClass(FaunEntity.class, this.getBoundingBox().inflate(range),
                other -> other != this && other.isAlive());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide) {
            Vec3 from = source.getSourcePosition() != null ? source.getSourcePosition() : this.position();
            this.spook(from, 100);
            this.spookHerd(from);
            // Hitting a faun that trusted you loses its trust
            if (source.getEntity() instanceof Player player && this.trusted.remove(player.getUUID())) {
                player.displayClientMessage(Component.translatable("message.novamterram.faun.trust_lost"), true);
            }
        }
        return hurt;
    }

    // --- FOOD, TRUST & BREEDING ---

    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD.test(stack);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // Sneak + empty hand asks a trusted faun to show you something
        if (hand == InteractionHand.MAIN_HAND && stack.isEmpty() && player.isSecondaryUseActive() && !this.isBaby()) {
            if (this.level().isClientSide) return InteractionResult.SUCCESS;
            if (!this.trusts(player.getUUID())) return InteractionResult.PASS;
            startGuiding(player);
            return InteractionResult.SUCCESS;
        }
        if (!this.isFood(stack)) return super.mobInteract(player, hand);

        // Only the server knows who is trusted, so the client just swings the arm
        if (this.level().isClientSide) return InteractionResult.SUCCESS;

        if (!this.trusts(player.getUUID())) {
            if (!stack.is(Items.APPLE)) {
                // Wheat can't buy trust, but a faun that already likes the taste can still be bred
                return super.mobInteract(player, hand);
            }
            if (!player.getAbilities().instabuild) stack.shrink(1);
            this.playSound(SoundEvents.HORSE_EAT, 1.0F, 1.0F);
            if (this.random.nextInt(3) == 0) {
                this.trusted.add(player.getUUID());
                hearts(ParticleTypes.HEART);
                player.displayClientMessage(Component.translatable("message.novamterram.faun.trust_success"), true);
            } else {
                hearts(ParticleTypes.SMOKE);
                player.displayClientMessage(Component.translatable("message.novamterram.faun.trust_fail"), true);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.getHealth() < this.getMaxHealth()) {
            this.heal(4.0F);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            this.playSound(SoundEvents.HORSE_EAT, 1.0F, 1.0F);
            hearts(ParticleTypes.HAPPY_VILLAGER);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand); // breeding and helping fawns grow
    }

    private void hearts(net.minecraft.core.particles.SimpleParticleType particle) {
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(particle, this.getX(), this.getY() + 1.6D, this.getZ(), 7, 0.4D, 0.3D, 0.4D, 0.02D);
        }
    }

    public boolean trusts(UUID uuid) {
        return this.trusted.contains(uuid);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        FaunEntity baby = NTEntities.FAUN.get().create(level);
        if (baby != null) {
            baby.setSex(this.random.nextBoolean());
            // The coat comes from a parent. White passes on only half as often.
            FaunCoat coat = this.getCoat();
            if (parent instanceof FaunEntity other) {
                coat = this.random.nextBoolean() ? this.getCoat() : other.getCoat();
                if (coat == FaunCoat.WHITE && this.random.nextBoolean()) {
                    coat = this.getCoat() == FaunCoat.WHITE ? other.getCoat() : this.getCoat();
                }
            }
            baby.setCoat(coat);
            // Fawns trust whoever their parents trust
            baby.trusted.addAll(this.trusted);
            if (parent instanceof FaunEntity other) baby.trusted.addAll(other.trusted);
        }
        return baby;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData data) {
        if (this.random.nextInt(100) == 0) {
            this.setCoat(FaunCoat.WHITE);
            this.setSex(true); // the white stag
        } else {
            this.setCoat(FaunCoat.forBiome(level.getBiome(this.blockPosition()), this.blockPosition()));
            this.setSex(this.random.nextBoolean());
        }
        return super.finalizeSpawn(level, difficulty, spawnType, data);
    }

    private void setSex(boolean buck) {
        this.setBuck(buck);
        this.setHasAntlers(buck);
    }

    // --- FLOWERS & GRAZING ---

    void eatFlower(BlockState flower) {
        this.setFloral(true);
        this.floralTimer = 1200;
        this.heal(4.0F);
        this.eatenFlower = flower.getBlock().defaultBlockState();
        this.entityData.set(CROWN_COLOR, FaunFlowers.crownColor(flower));

        // Refills once a day, so keeping a faun fed can't farm plants
        if (this.level().getGameTime() >= this.nextBlessingAt) {
            this.nextBlessingAt = this.level().getGameTime() + DAY;
            this.flowersToPlant = 1; // replaces the one it ate
            this.plantsToGrow = 2;
        }
    }

    // A crowned faun nudges nearby saplings along and leaves a new flower behind
    private void blessPlants() {
        if ((this.flowersToPlant <= 0 && this.plantsToGrow <= 0) || !(this.level() instanceof ServerLevel server)
                || !EventHooks.canEntityGrief(server, this)) return;

        for (int attempt = 0; attempt < 24; attempt++) {
            BlockPos pos = this.blockPosition().offset(this.random.nextInt(9) - 4,
                    this.random.nextInt(3) - 1, this.random.nextInt(9) - 4);
            BlockState state = server.getBlockState(pos);

            if (this.plantsToGrow > 0 && state.is(BlockTags.SAPLINGS)
                    && state.getBlock() instanceof BonemealableBlock plant
                    && plant.isValidBonemealTarget(server, pos, state)) {
                if (plant.isBonemealSuccess(server, this.random, pos, state)) {
                    plant.performBonemeal(server, this.random, pos, state);
                }
                server.levelEvent(1505, pos, 10); // bone meal sparkles
                this.plantsToGrow--;
                return;
            }
            if (this.flowersToPlant > 0 && state.isAir() && server.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)
                    && this.eatenFlower.canSurvive(server, pos)) {
                server.setBlockAndUpdate(pos, this.eatenFlower);
                server.levelEvent(1505, pos, 10);
                this.flowersToPlant--;
                return;
            }
        }
    }

    // --- GUIDING ---

    private void startGuiding(Player player) {
        if (this.tickCount < this.nextGuideAt) {
            player.displayClientMessage(Component.translatable("message.novamterram.faun.guide_wait"), true);
            return;
        }
        BlockPos found = FaunFlowers.findSomethingToShow(this.level(), this.blockPosition(), this.random);
        if (found == null) {
            this.nextGuideAt = this.tickCount + 600;
            player.displayClientMessage(Component.translatable("message.novamterram.faun.guide_none"), true);
            return;
        }
        this.nextGuideAt = this.tickCount + 6000; // 5 minutes
        this.guideTarget = found;
        this.guideFor = player.getUUID();
        this.setResting(false);
        player.displayClientMessage(Component.translatable("message.novamterram.faun.guide_start"), true);
    }

    @Nullable
    BlockPos guideTarget() {
        return this.guideTarget;
    }

    @Nullable
    Player guideFollower() {
        return this.guideFor == null ? null : this.level().getPlayerByUUID(this.guideFor);
    }

    void stopGuiding() {
        this.guideTarget = null;
        this.guideFor = null;
    }

    // --- KICKING ---

    void kick(LivingEntity target) {
        if (target.hurt(this.damageSources().mobAttack(this), 3.0F)) {
            target.knockback(0.9D, this.getX() - target.getX(), this.getZ() - target.getZ());
        }
        this.playSound(SoundEvents.GOAT_RAM_IMPACT, 0.8F, 1.1F);
        this.level().broadcastEntityEvent(this, KICK_EVENT);
    }

    /** 0 to 1 and back while kicking. */
    public float getKickAmount() {
        return this.kickTicks <= 0 ? 0.0F : Mth.sin(this.kickTicks / 10.0F * Mth.PI);
    }

    private void healFriends() {
        for (Player player : this.level().players()) {
            if (this.trusts(player.getUUID()) && player.distanceToSqr(this) < 64.0D) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, true, true), this);
            }
        }
    }

    // Called when it finishes eating grass
    @Override
    public void ate() {
        super.ate();
        this.heal(2.0F);
        if (this.isBaby()) this.ageUp(30);
    }

    // Event 10 means "start the eating animation"
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 10) {
            this.grazeTicks = 40;
        } else if (id == KICK_EVENT) {
            this.kickTicks = 10;
        } else {
            super.handleEntityEvent(id);
        }
    }

    /** 0 = head up, 1 = head fully down. */
    public float getGrazeAmount(float partialTick) {
        if (this.grazeTicks <= 0) return 0.0F;
        float left = Math.max(this.grazeTicks - partialTick, 0.0F);
        return Math.min(1.0F, Math.min(left / 8.0F, (40.0F - left) / 8.0F));
    }

    // --- DATA ---

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_BUCK, false);
        builder.define(IS_FLORAL, false);
        builder.define(HAS_ANTLERS, false);
        builder.define(IS_HIDING, false);
        builder.define(IS_RESTING, false);
        builder.define(IS_SPARRING, false);
        builder.define(COAT, 0);
        builder.define(CROWN_COLOR, FaunFlowers.DEFAULT_COLOR);
    }

    public FaunCoat getCoat() { return FaunCoat.byIndex(this.entityData.get(COAT)); }
    public void setCoat(FaunCoat coat) { this.entityData.set(COAT, coat.ordinal()); }
    public int getCrownColor() { return this.entityData.get(CROWN_COLOR); }
    public boolean isBuck() { return this.entityData.get(IS_BUCK); }
    public void setBuck(boolean b) { this.entityData.set(IS_BUCK, b); }
    public boolean isFloral() { return this.entityData.get(IS_FLORAL); }
    public void setFloral(boolean b) { this.entityData.set(IS_FLORAL, b); }
    public boolean hasAntlers() { return this.entityData.get(HAS_ANTLERS); }
    public void setHasAntlers(boolean b) { this.entityData.set(HAS_ANTLERS, b); }
    public boolean isHiding() { return this.entityData.get(IS_HIDING); }
    public void setHiding(boolean b) { this.entityData.set(IS_HIDING, b); }
    public boolean isResting() { return this.entityData.get(IS_RESTING); }
    public void setResting(boolean b) { this.entityData.set(IS_RESTING, b); }
    public boolean isSparring() { return this.entityData.get(IS_SPARRING); }
    public void setSparring(boolean b) { this.entityData.set(IS_SPARRING, b); }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("IsBuck", this.isBuck());
        nbt.putBoolean("HasAntlers", this.hasAntlers());
        nbt.putInt("ShedCooldown", this.shedTimer);
        nbt.putInt("RegrowTimer", this.regrowTimer);
        nbt.putInt("FloralTimer", this.floralTimer);
        nbt.putString("Coat", this.getCoat().id());
        nbt.putInt("CrownColor", this.getCrownColor());
        nbt.putString("EatenFlower", BuiltInRegistries.BLOCK.getKey(this.eatenFlower.getBlock()).toString());
        nbt.putLong("NextBlessing", this.nextBlessingAt);
        nbt.putInt("FlowersToPlant", this.flowersToPlant);
        nbt.putInt("PlantsToGrow", this.plantsToGrow);
        ListTag list = new ListTag();
        for (UUID uuid : this.trusted) list.add(NbtUtils.createUUID(uuid));
        nbt.put("Trusted", list);
    }

    // Same names as before, so fauns already in your world keep their sex and friends
    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setBuck(nbt.getBoolean("IsBuck"));
        this.setHasAntlers(nbt.contains("HasAntlers") ? nbt.getBoolean("HasAntlers") : this.isBuck());
        if (nbt.contains("ShedCooldown")) this.shedTimer = nbt.getInt("ShedCooldown");
        this.regrowTimer = nbt.getInt("RegrowTimer");
        this.floralTimer = nbt.getInt("FloralTimer");
        this.setFloral(this.floralTimer > 0);
        this.setCoat(FaunCoat.byId(nbt.getString("Coat")));
        if (nbt.contains("CrownColor")) this.entityData.set(CROWN_COLOR, nbt.getInt("CrownColor"));
        if (nbt.contains("EatenFlower")) {
            ResourceLocation id = ResourceLocation.tryParse(nbt.getString("EatenFlower"));
            if (id != null) this.eatenFlower = BuiltInRegistries.BLOCK.get(id).defaultBlockState();
        }
        this.nextBlessingAt = nbt.getLong("NextBlessing");
        this.flowersToPlant = nbt.getInt("FlowersToPlant");
        this.plantsToGrow = nbt.getInt("PlantsToGrow");
        this.trusted.clear();
        for (Tag tag : nbt.getList("Trusted", Tag.TAG_INT_ARRAY)) this.trusted.add(NbtUtils.loadUUID(tag));
    }

    // --- SOUNDS ---

    @Override
    protected SoundEvent getAmbientSound() { return this.isResting() || this.isHiding() ? null : SoundEvents.GOAT_AMBIENT; }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.GOAT_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.GOAT_DEATH; }
    @Override
    public float getVoicePitch() { return super.getVoicePitch() * 1.3F; } // so it doesn't sound just like a goat
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.HORSE_STEP, 0.1F, 1.3F); }
}
