package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class BrownBearEntity extends AbstractNTBear implements NeutralMob {
    private static final EntityDataAccessor<Integer> DATA_VARIANCE = SynchedEntityData.defineId(BrownBearEntity.class, EntityDataSerializers.INT);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private int remainingPersistentAngerTime;
    @Nullable private UUID persistentAngerTarget;
    private int warningSoundTicks;

    public BrownBearEntity(EntityType<? extends BrownBearEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANCE, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BrownBearMeleeAttackGoal());
        this.goalSelector.addGoal(1, new BrownBearPanicGoal());
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, stack -> this.isFood(stack), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(4, new EatHoneyGoal(this, 1.1D, 16));
        this.goalSelector.addGoal(5, new HarvestBerriesGoal(this, 1.0D, 12));
        this.goalSelector.addGoal(6, new CaveRestingGoal(this, 0.8D, 20));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        if (this.isBaby()) this.goalSelector.addGoal(3, new BabyScavengerGoal(this));

        this.targetSelector.addGoal(1, new BrownBearHurtByTargetGoal());
        this.targetSelector.addGoal(2, new BrownBearAttackPlayersGoal());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Fox.class, 10, true, true, null));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Salmon.class, 10, true, true, null));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        // Uses Vanilla Fish Tag and Fox Food (berries) Tag
        return stack.is(ItemTags.FISHES) || stack.is(ItemTags.FOX_FOOD);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.warningSoundTicks > 0) this.warningSoundTicks--;
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }
        if (this.isInWater()) {
            this.setPose(Pose.SWIMMING);
        } else if (this.isStanding()) {
            this.setPose(Pose.STANDING);
        } else {
            this.setPose(Pose.STANDING);
        }
    }

    /* --- ANGER LOGIC --- */
    @Override public void addAdditionalSaveData(CompoundTag c) { super.addAdditionalSaveData(c); this.addPersistentAngerSaveData(c); }
    @Override public void readAdditionalSaveData(CompoundTag c) { super.readAdditionalSaveData(c); this.readPersistentAngerSaveData(this.level(), c); }
    @Override public void startPersistentAngerTimer() { this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random)); }
    @Override public void setRemainingPersistentAngerTime(int t) { this.remainingPersistentAngerTime = t; }
    @Override public int getRemainingPersistentAngerTime() { return this.remainingPersistentAngerTime; }
    @Override public void setPersistentAngerTarget(@Nullable UUID target) { this.persistentAngerTarget = target; }
    @Nullable @Override public UUID getPersistentAngerTarget() { return this.persistentAngerTarget; }

    /* --- BREEDING & SOUNDS --- */
    @Nullable @Override public AgeableMob getBreedOffspring(ServerLevel l, AgeableMob p) { return NTEntities.BROWN_BEAR.get().create(l); }
    @Override protected SoundEvent getAmbientSound() { return this.isBaby() ? SoundEvents.POLAR_BEAR_AMBIENT_BABY : SoundEvents.POLAR_BEAR_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource d) { return SoundEvents.POLAR_BEAR_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.POLAR_BEAR_DEATH; }
    @Override protected void playStepSound(BlockPos p, BlockState b) { this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F); }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.0F, this.getVoicePitch());
            this.warningSoundTicks = 40;
        }
    }

    /* --- GOAL CLASSES --- */
    class BrownBearMeleeAttackGoal extends MeleeAttackGoal {
        public BrownBearMeleeAttackGoal() { super(BrownBearEntity.this, 1.25D, true); }
        @Override public void tick() {
            super.tick();
            LivingEntity enemy = this.mob.getTarget();
            if (enemy != null && this.mob.distanceToSqr(enemy) <= 4.0D) {
                if (this.getTicksUntilNextAttack() <= 10) { BrownBearEntity.this.setStanding(true); BrownBearEntity.this.playWarningSound(); }
            } else { BrownBearEntity.this.setStanding(false); }
        }
        @Override public void stop() { BrownBearEntity.this.setStanding(false); super.stop(); }
    }

    class BrownBearAttackPlayersGoal extends NearestAttackableTargetGoal<Player> {
        public BrownBearAttackPlayersGoal() { super(BrownBearEntity.this, Player.class, 20, true, true, null); }
        @Override public boolean canUse() {
            if (BrownBearEntity.this.isBaby()) return false;
            return super.canUse();
        }
    }

    class BrownBearHurtByTargetGoal extends HurtByTargetGoal {
        public BrownBearHurtByTargetGoal() { super(BrownBearEntity.this); }
        @Override public void start() { super.start(); if (BrownBearEntity.this.isBaby()) { this.alertOthers(); this.stop(); } }
    }

    class BrownBearPanicGoal extends PanicGoal {
        public BrownBearPanicGoal() { super(BrownBearEntity.this, 2.0D); }
        @Override public boolean canUse() { return (BrownBearEntity.this.isBaby() || BrownBearEntity.this.isOnFire()) && super.canUse(); }
    }

    static class EatHoneyGoal extends MoveToBlockGoal {
        private final BrownBearEntity bear;
        public EatHoneyGoal(BrownBearEntity b, double s, int r) { super(b, s, r); this.bear = b; }
        @Override protected boolean isValidTarget(LevelReader l, BlockPos p) {
            BlockState state = l.getBlockState(p);
            return state.is(BlockTags.BEEHIVES) && state.hasProperty(BeehiveBlock.HONEY_LEVEL) && state.getValue(BeehiveBlock.HONEY_LEVEL) > 0;
        }
        @Override public void tick() {
            super.tick();
            if (this.isReachedTarget()) {
                BlockState state = bear.level().getBlockState(this.blockPos);
                bear.level().setBlock(this.blockPos, state.setValue(BeehiveBlock.HONEY_LEVEL, 0), 3);
                bear.heal(4.0F);
                bear.playSound(SoundEvents.HONEY_BLOCK_BREAK, 1.0F, 1.0F);
                bear.setStanding(true);
                this.stop();
            }
        }
        @Override public void stop() { super.stop(); bear.setStanding(false); }
    }

    static class HarvestBerriesGoal extends MoveToBlockGoal {
        private final BrownBearEntity bear;
        public HarvestBerriesGoal(BrownBearEntity b, double s, int r) { super(b, s, r); this.bear = b; }
        @Override protected boolean isValidTarget(LevelReader l, BlockPos p) {
            BlockState state = l.getBlockState(p);
            return state.is(Blocks.SWEET_BERRY_BUSH) && state.getValue(SweetBerryBushBlock.AGE) >= 2;
        }
        @Override public void tick() {
            super.tick();
            if (this.isReachedTarget()) {
                bear.level().setBlock(this.blockPos, bear.level().getBlockState(this.blockPos).setValue(SweetBerryBushBlock.AGE, 1), 2);
                bear.heal(2.0F);
                bear.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
                this.stop();
            }
        }
    }

    static class CaveRestingGoal extends MoveToBlockGoal {
        private final BrownBearEntity bear;
        public CaveRestingGoal(BrownBearEntity b, double s, int r) { super(b, s, r); this.bear = b; }
        @Override protected boolean isValidTarget(LevelReader l, BlockPos p) { return l.isEmptyBlock(p.above()) && !l.canSeeSky(p); }
        @Override public boolean canUse() { return super.canUse() && (bear.level().isNight() || bear.level().isRaining()); }
        @Override public void tick() { super.tick(); if (this.isReachedTarget()) bear.setSleepyTime(true); }
        @Override public void stop() { super.stop(); bear.setSleepyTime(false); }
    }

    static class BabyScavengerGoal extends Goal {
        private final BrownBearEntity baby;
        public BabyScavengerGoal(BrownBearEntity b) { this.baby = b; }
        @Override public boolean canUse() {
            return baby.isBaby() && !baby.isPassenger() && !baby.level().getEntitiesOfClass(ItemEntity.class, baby.getBoundingBox().inflate(6.0D), i -> i.getItem().is(ItemTags.FOX_FOOD)).isEmpty();
        }
        @Override public void tick() {
            List<ItemEntity> items = baby.level().getEntitiesOfClass(ItemEntity.class, baby.getBoundingBox().inflate(6.0D), i -> i.getItem().is(ItemTags.FOX_FOOD));
            if (!items.isEmpty()) {
                baby.getNavigation().moveTo(items.get(0), 1.2D);
                if (baby.distanceToSqr(items.get(0)) < 2.0D) baby.pickUpItem(items.get(0));
            }
        }
    }
}