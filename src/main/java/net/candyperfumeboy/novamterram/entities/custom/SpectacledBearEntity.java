package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.block.NTBlocks;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class SpectacledBearEntity extends AbstractNTBear implements NeutralMob {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(SpectacledBearEntity.class, EntityDataSerializers.INT);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private int remainingPersistentAngerTime;
    @Nullable private UUID persistentAngerTarget;

    private int climbEnergy = 1000;
    private float climbRotation = 0;
    private float climbRotationO = 0;

    public SpectacledBearEntity(EntityType<? extends SpectacledBearEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Goat.class, 10.0F, 1.2D, 1.5D));
        this.goalSelector.addGoal(2, new SpectacledPounceGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.1D, stack -> this.isFood(stack), false));
        this.goalSelector.addGoal(6, new BromeliadGrazeGoal(this, 1.0D, 12));
        this.goalSelector.addGoal(8, new BearDanceGoal(this));
        this.goalSelector.addGoal(9, new LedgeRestingGoal(this, 0.8D, 20));
        this.goalSelector.addGoal(10, new ScratchTreeGoal(this, 1.0D, 12));
        this.goalSelector.addGoal(11, new MountainForageGoal(this));
        this.goalSelector.addGoal(12, new CuriousLookGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(13, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(14, new RandomLookAroundGoal(this));

        if (this.isBaby()) this.goalSelector.addGoal(3, new BabyScavengerGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public boolean isFood(ItemStack stack) { return stack.is(NTBlocks.BROMELIAD.get().asItem()); }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.STEP_HEIGHT, 1.5D);
    }

    @Override
    public void tick() {
        super.tick();
        this.climbRotationO = this.climbRotation;

        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
            this.handleClimbing();
        }
    }

    private void handleClimbing() {
        if (this.horizontalCollision && this.climbEnergy > 0 && !this.isSleepyTime()) {
            this.setDeltaMovement(this.getDeltaMovement().x, 0.18D, this.getDeltaMovement().z);
            this.climbRotation = Mth.lerp(0.2F, this.climbRotation, 35.0F);
            if (this.tickCount % 20 == 0) this.climbEnergy--;
        } else {
            this.climbRotation = Mth.lerp(0.2F, this.climbRotation, 0.0F);
            if (this.onGround() && this.climbEnergy < 1000) this.climbEnergy++;
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isFood(stack)) return this.handleBromeliadFeeding(player, stack);

        if (this.getTrustLevel() >= 100 && !this.isBaby() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) player.startRiding(this);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    private InteractionResult handleBromeliadFeeding(Player player, ItemStack stack) {
        if (this.level().isClientSide) return InteractionResult.CONSUME;
        this.climbEnergy = 1000;
        this.setTrustLevel(this.getTrustLevel() + 20);
        if (this.getAge() == 0 && this.canFallInLove()) this.setInLove(player);
        this.playSound(SoundEvents.PANDA_EAT, 1.0F, 1.0F);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override public void addAdditionalSaveData(CompoundTag c) { super.addAdditionalSaveData(c); this.addPersistentAngerSaveData(c); c.putInt("Variant", this.getVariant()); c.putInt("ClimbEnergy", this.climbEnergy); }
    @Override public void readAdditionalSaveData(CompoundTag c) { super.readAdditionalSaveData(c); this.readPersistentAngerSaveData(this.level(), c); if (c.contains("Variant")) this.setVariant(c.getInt("Variant")); this.climbEnergy = c.getInt("ClimbEnergy"); }
    @Override public void startPersistentAngerTimer() { this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random)); }
    @Override public void setRemainingPersistentAngerTime(int t) { this.remainingPersistentAngerTime = t; }
    @Override public int getRemainingPersistentAngerTime() { return this.remainingPersistentAngerTime; }
    @Override public void setPersistentAngerTarget(@Nullable UUID t) { this.persistentAngerTarget = t; }
    @Nullable @Override public UUID getPersistentAngerTarget() { return this.persistentAngerTarget; }

    public int getVariant() { return this.entityData.get(DATA_VARIANT); }
    public void setVariant(int v) { this.entityData.set(DATA_VARIANT, v); }
    @Nullable @Override public AgeableMob getBreedOffspring(ServerLevel l, AgeableMob p) {
        SpectacledBearEntity baby = NTEntities.SPECTACLED_BEAR.get().create(l);
        if (baby != null && p instanceof SpectacledBearEntity parent) baby.setVariant(this.random.nextBoolean() ? this.getVariant() : parent.getVariant());
        return baby;
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.POLAR_BEAR_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource ds) { return SoundEvents.POLAR_BEAR_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.POLAR_BEAR_DEATH; }
    @Override protected void playStepSound(BlockPos p, BlockState b) { this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F); }

    /* --- INNER CLASSES --- */
    static class SpectacledPounceGoal extends Goal {
        private final SpectacledBearEntity bear;
        public SpectacledPounceGoal(SpectacledBearEntity bear) { this.bear = bear; }
        @Override public boolean canUse() { LivingEntity t = bear.getTarget(); return t != null && bear.climbRotation > 15.0F && bear.getY() > t.getY() + 1.5D && bear.distanceToSqr(t) < 16.0D; }
        @Override public void start() { LivingEntity t = bear.getTarget(); if (t != null) { Vec3 dir = new Vec3(t.getX() - bear.getX(), 0.0D, t.getZ() - bear.getZ()).normalize().scale(0.8D); bear.setDeltaMovement(dir.x, 0.4D, dir.z); } }
    }
    static class BearDanceGoal extends Goal {
        private final SpectacledBearEntity bear;
        public BearDanceGoal(SpectacledBearEntity bear) { this.bear = bear; }
        @Override public boolean canUse() { return bear.isDancing(); }
        @Override public void tick() { bear.setStanding(true); bear.getNavigation().stop(); }
        @Override public void stop() { bear.setStanding(false); }
    }
    static class BromeliadGrazeGoal extends MoveToBlockGoal {
        private final SpectacledBearEntity bear;
        public BromeliadGrazeGoal(SpectacledBearEntity bear, double s, int r) { super(bear, s, r); this.bear = bear; }
        @Override protected boolean isValidTarget(LevelReader l, BlockPos p) { return l.getBlockState(p).is(NTBlocks.BROMELIAD.get()); }
        @Override public void tick() { super.tick(); if (this.isReachedTarget()) { this.bear.level().destroyBlock(this.blockPos, false); this.bear.heal(4.0F); this.bear.climbEnergy = 1000; this.stop(); } }
    }
    static class MountainForageGoal extends Goal {
        private final SpectacledBearEntity bear;
        private int timer;
        public MountainForageGoal(SpectacledBearEntity bear) { this.bear = bear; }
        @Override public boolean canUse() { return !bear.isBaby() && bear.getRandom().nextInt(1000) == 0 && bear.getY() > 80; }
        @Override public void start() { this.timer = 40; }
        @Override public void tick() { if (--this.timer <= 0) bear.spawnAtLocation(Items.POTATO); }
    }
    static class ScratchTreeGoal extends MoveToBlockGoal {
        private final SpectacledBearEntity bear;
        private int timer;
        public ScratchTreeGoal(SpectacledBearEntity b, double s, int r) { super(b, s, r); this.bear = b; }
        @Override protected boolean isValidTarget(LevelReader l, BlockPos p) { return l.getBlockState(p).is(BlockTags.LOGS); }
        @Override public void tick() { super.tick(); if (this.isReachedTarget()) { if (this.timer++ % 10 == 0) { bear.level().playSound(null, blockPos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F); if (bear.level() instanceof ServerLevel sl) sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, bear.level().getBlockState(blockPos)), bear.getX(), bear.getY() + 1.2, bear.getZ(), 5, 0.2, 0.2, 0.2, 0.1); } bear.setStanding(true); if (this.timer > 60) this.stop(); } }
        @Override public void stop() { super.stop(); bear.setStanding(false); this.timer = 0; }
    }
    static class LedgeRestingGoal extends MoveToBlockGoal {
        private final SpectacledBearEntity bear;
        public LedgeRestingGoal(SpectacledBearEntity b, double s, int r) { super(b, s, r); this.bear = b; }
        @Override protected boolean isValidTarget(LevelReader l, BlockPos p) { return p.getY() > 90 && l.isEmptyBlock(p.above()); }
        @Override public boolean canUse() { return super.canUse() && !bear.level().isDay(); }
        @Override public void tick() { super.tick(); if (this.isReachedTarget()) bear.setSleepyTime(true); }
        @Override public void stop() { super.stop(); bear.setSleepyTime(false); }
    }
    static class CuriousLookGoal extends LookAtPlayerGoal {
        private final SpectacledBearEntity bear;
        public CuriousLookGoal(SpectacledBearEntity b, Class<? extends LivingEntity> t, float d) { super(b, t, d); this.bear = b; }
        @Override public void start() { super.start(); bear.setStanding(true); }
        @Override public void stop() { super.stop(); bear.setStanding(false); }
    }
    static class BabyScavengerGoal extends Goal {
        private final SpectacledBearEntity baby;
        private static final Ingredient FOODS = Ingredient.of(Items.POTATO, Items.SWEET_BERRIES, Items.HONEYCOMB);
        public BabyScavengerGoal(SpectacledBearEntity b) { this.baby = b; }
        @Override public boolean canUse() { return baby.isBaby() && !baby.isPassenger() && !baby.level().getEntitiesOfClass(ItemEntity.class, baby.getBoundingBox().inflate(6.0D), i -> FOODS.test(i.getItem())).isEmpty(); }
        @Override public void tick() { List<ItemEntity> items = baby.level().getEntitiesOfClass(ItemEntity.class, baby.getBoundingBox().inflate(6.0D), i -> FOODS.test(i.getItem())); if (!items.isEmpty()) { baby.getNavigation().moveTo(items.get(0), 1.2D); if (baby.distanceToSqr(items.get(0)) < 2.0D) baby.pickUpItem(items.get(0)); } }
    }
}