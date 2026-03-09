package net.candyperfumeboy.novamterram.entities.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class HyenaEntity extends TamableAnimal {

    // Data Tracker for the "Aggressive/Frenzied" state
    private static final EntityDataAccessor<Boolean> IS_AGGRESSIVE = SynchedEntityData.defineId(HyenaEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Predicate<ItemEntity> SCAVENGE_ITEMS = (item) -> {
        return !item.hasPickUpDelay() && item.isAlive() &&
                (item.getItem().is(Items.ROTTEN_FLESH) ||
                        item.getItem().is(Items.BEEF) ||
                        item.getItem().is(Items.BONE));
    };

    public HyenaEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    // --- DATA HANDLING ---
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_AGGRESSIVE, false);
    }

    public boolean isFrenzied() {
        return this.entityData.get(IS_AGGRESSIVE);
    }

    public void setFrenzied(boolean aggressive) {
        this.entityData.set(IS_AGGRESSIVE, aggressive);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsFrenzied", this.isFrenzied());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFrenzied(compound.getBoolean("IsFrenzied"));
    }

    // --- ATTRIBUTES ---
    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    // --- AI GOALS ---
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new HyenaScavengeGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1D, Ingredient.of(Items.ROTTEN_FLESH, Items.BONE), false));
        this.goalSelector.addGoal(7, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        // TARGETING
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, true));

        // NEW: Aggressive Hyenas target players automatically
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (entity) -> this.isFrenzied() && !this.isTame()));
    }

    // --- SPAWN LOGIC (5% chance to be aggressive) ---
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if (this.random.nextFloat() < 0.05F) { // 5% Chance
            this.setFrenzied(true);
        }
        return super.finalizeSpawn(world, difficulty, spawnType, spawnGroupData);
    }

    // --- PARTICLES & TICK LOGIC ---
    @Override
    public void aiStep() {
        super.aiStep();
        // Spawn red smoke/angry particles if frenzied and not tamed
        if (this.level().isClientSide && this.isFrenzied() && !this.isTame()) {
            if (this.random.nextInt(5) == 0) {
                this.level().addParticle(ParticleTypes.ANGRY_VILLAGER, this.getRandomX(0.5D), this.getRandomY() + 0.5D, this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    // --- COMBAT (Pack Tactics & Bone Crusher) ---
    @Override
    public boolean doHurtTarget(Entity target) {
        float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);

        // Pack Strength
        List<HyenaEntity> nearbyHyenas = this.level().getEntitiesOfClass(HyenaEntity.class, this.getBoundingBox().inflate(8.0D));
        damage += Math.min(nearbyHyenas.size() - 1, 4);

        // Frenzy Boost: +2 damage if naturally aggressive
        if (this.isFrenzied() && !this.isTame()) damage += 2.0F;

        boolean flag = target.hurt(this.damageSources().mobAttack(this), damage);

        if (flag && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            if (this.random.nextFloat() < 0.15F) {
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
                this.playSound(SoundEvents.WOLF_GROWL, 1.0F, 0.8F);
            }
        }
        return flag;
    }

    // --- INTERACTIONS ---
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (this.isTame()) {
            // HEALING
            if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                this.heal((float)itemstack.getFoodProperties(this).nutrition());
                if (!player.getAbilities().instabuild) itemstack.shrink(1);
                this.playSound(SoundEvents.FOX_EAT, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }

            // BONE-MEAL UTILITY
            if (this.isOrderedToSit() && itemstack.is(Items.BONE)) {
                if (!player.getAbilities().instabuild) itemstack.shrink(1);
                this.playSound(SoundEvents.SKELETON_STEP, 1.0F, 0.5F);
                if (!this.level().isClientSide) {
                    BlockPos.betweenClosedStream(this.blockPosition().offset(-1, -1, -1), this.blockPosition().offset(1, 1, 1)).forEach(pos -> {
                        BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), this.level(), pos, player);
                    });
                    player.displayClientMessage(Component.literal("Bone crushed into fertilizer!"), true);
                }
                return InteractionResult.SUCCESS;
            }

            // TOGGLE SITTING
            if (this.isOwnedBy(player) && !this.isFood(itemstack)) {
                boolean isNowSitting = !this.isOrderedToSit();
                this.setOrderedToSit(isNowSitting);
                this.navigation.stop();
                if (!this.level().isClientSide) {
                    player.displayClientMessage(Component.literal(isNowSitting ? "Hyena is waiting." : "Hyena is following!"), true);
                }
                return InteractionResult.SUCCESS;
            }
        }
        // TAMING (Harder to tame if Frenzied!)
        else if (itemstack.is(Items.BONE) || itemstack.is(Items.ROTTEN_FLESH)) {
            int chance = itemstack.is(Items.BONE) ? (this.isFrenzied() ? 5 : 3) : (this.isFrenzied() ? 10 : 6);

            if (!player.getAbilities().instabuild) itemstack.shrink(1);

            if (!this.level().isClientSide) {
                if (this.random.nextInt(chance) == 0) {
                    this.tame(player);
                    this.setFrenzied(false); // Taming cures the frenzy!
                    this.setOrderedToSit(true);
                    this.level().broadcastEntityEvent(this, (byte)7);
                    player.displayClientMessage(Component.literal("The beast is calmed and tamed!"), true);
                } else {
                    this.level().broadcastEntityEvent(this, (byte)6);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    // --- SCAVENGER GOAL ---
    class HyenaScavengeGoal extends Goal {
        private final HyenaEntity hyena;
        public HyenaScavengeGoal(HyenaEntity hyena) { this.hyena = hyena; }

        @Override
        public boolean canUse() {
            if (this.hyena.getHealth() >= this.hyena.getMaxHealth()) return false;
            List<ItemEntity> list = this.hyena.level().getEntitiesOfClass(ItemEntity.class, this.hyena.getBoundingBox().inflate(8.0D, 4.0D, 8.0D), SCAVENGE_ITEMS);
            return !list.isEmpty();
        }

        @Override
        public void tick() {
            List<ItemEntity> list = this.hyena.level().getEntitiesOfClass(ItemEntity.class, this.hyena.getBoundingBox().inflate(8.0D, 4.0D, 8.0D), SCAVENGE_ITEMS);
            if (!list.isEmpty()) {
                ItemEntity targetItem = list.get(0);
                this.hyena.getNavigation().moveTo(targetItem, 1.2F);
                if (this.hyena.distanceToSqr(targetItem) < 1.5D) {
                    this.hyena.heal(3.0F);
                    targetItem.getItem().shrink(1);
                    this.hyena.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.ROTTEN_FLESH) || pStack.is(Items.BEEF) || pStack.is(Items.PORKCHOP);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        HyenaEntity baby = (HyenaEntity) this.getType().create(serverLevel);
        if (baby != null && this.isTame()) {
            baby.setOwnerUUID(this.getOwnerUUID());
            baby.setTame(true, true);
        }
        return baby;
    }
}