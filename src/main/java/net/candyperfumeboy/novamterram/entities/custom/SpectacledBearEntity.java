package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class SpectacledBearEntity extends AbstractNTBear implements NeutralMob {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(SpectacledBearEntity.class, EntityDataSerializers.INT);
    // The one player this bear trusts (synced so the client knows too).
    private static final EntityDataAccessor<java.util.Optional<UUID>> DATA_TRUSTED_PLAYER = SynchedEntityData.defineId(SpectacledBearEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    // What a fully trusting bear does when its player is not riding it.
    private static final int WANDER = 0, FOLLOW = 1, STAY = 2;
    private int mode = WANDER;

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private int remainingPersistentAngerTime;
    @Nullable private UUID persistentAngerTarget;
    private int climbEnergy = 1000;

    public SpectacledBearEntity(EntityType<? extends SpectacledBearEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, 0);
        builder.define(DATA_TRUSTED_PLAYER, java.util.Optional.empty());

    }

    @Override
    protected boolean canDance() { return true; }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Goat.class, 10.0F, 1.2D, 1.5D));
        this.goalSelector.addGoal(2, new SpectacledPounceGoal(this));
        this.goalSelector.addGoal(2, new StayGoal(this));
        this.goalSelector.addGoal(5, new FollowTrustedPlayerGoal(this));
        this.goalSelector.addGoal(7, new TreeNestGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.1D, stack -> this.isFood(stack), false));
        this.goalSelector.addGoal(6, new BromeliadGrazeGoal(this, 1.0D, 12));
        this.goalSelector.addGoal(8, new BearDanceGoal(this));
        this.goalSelector.addGoal(9, new LedgeRestingGoal(this, 0.8D, 20));
        this.goalSelector.addGoal(10, new ScratchTreeGoal(this, 1.0D, 12));
        this.goalSelector.addGoal(11, new MountainForageGoal(this));
        this.goalSelector.addGoal(13, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(14, new RandomLookAroundGoal(this));

        // Added to every bear: age is not known yet here, and the goal checks isBaby() itself.
        this.goalSelector.addGoal(3, new BabyScavengerGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
            this.handleClimbing();
            // Cubs climb off again after a while (and once they grow up).
            if (this.getVehicle() instanceof SpectacledBearEntity && (!this.isBaby() || this.getRandom().nextInt(600) == 0)) {
                this.stopRiding();
            }

            if (this.isBaby() && this.getTarget() == null && this.getRandom().nextInt(500) == 0) {
                List<SpectacledBearEntity> parents = this.level().getEntitiesOfClass(SpectacledBearEntity.class, this.getBoundingBox().inflate(5.0D));
                for (SpectacledBearEntity parent : parents) {
                    if (!parent.isBaby() && parent.getPassengers().isEmpty()) {
                        this.startRiding(parent);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

        // Hold forward into a wall or trunk to climb. Let go to slide down slowly.
        if (this.isInWater()) return;
        Vec3 motion = this.getDeltaMovement();
        if (this.horizontalCollision && player.zza > 0.0F) {
            this.setDeltaMovement(motion.x, 0.25D, motion.z);
        } else if (!this.onGround() && motion.y < -0.08D && this.isTouchingWall()) {
            this.setDeltaMovement(motion.x, -0.08D, motion.z);
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        return new Vec3(player.xxa * 0.35F, 0.0D, player.zza * 0.65F);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.6D);
    }

    private boolean isTouchingWall() {
        return !this.level().noCollision(this, this.getBoundingBox().inflate(0.1D, -0.1D, 0.1D));
    }

    // Climbers land well: the first few blocks of damage are ignored (this covers hopping out of a nest).
    @Override
    protected int calculateFallDamage(float fallDistance, float damageMultiplier) {
        return Math.max(0, super.calculateFallDamage(fallDistance, damageMultiplier) - 7);
    }

    private void handleClimbing() {
        if (this.getControllingPassenger() != null) {
            // Ridden: the rider's game moves the bear, so only track the state and forgive the fall.
            boolean onWall = !this.onGround() && this.isTouchingWall();
            this.setClimbing(onWall);
            if (onWall) {
                this.setStanding(false);
                this.resetFallDistance();
            }
            return;
        }
        boolean isMovingForward = this.getDeltaMovement().lengthSqr() > 0.001D;
        if (this.horizontalCollision && isMovingForward && this.climbEnergy > 0 && !this.isSleepyTime()) {
            this.setDeltaMovement(this.getDeltaMovement().x, 0.25D, this.getDeltaMovement().z);
            this.setClimbing(true);
            this.setStanding(false);
            this.resetFallDistance();
            if (this.tickCount % 20 == 0) this.climbEnergy--;
        } else {
            this.setClimbing(false);
            if (this.onGround() && this.climbEnergy < 1000) this.climbEnergy++;
        }
    }

    @Override
    public void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger)) return;
        double vOffset = (passenger instanceof SpectacledBearEntity) ? 0.3D : 0.9D;
        double bOffset = (passenger instanceof SpectacledBearEntity) ? -0.4D : 0.1D;
        float radian = this.yBodyRot * ((float)Math.PI / 180F);
        moveFunction.accept(passenger, this.getX() + (bOffset * Mth.sin(radian)), this.getY() + vOffset, this.getZ() - (bOffset * Mth.cos(radian)));
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        // Only a player steers. A riding cub must not take over (that would freeze the mother).
        return this.getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    public boolean isFood(ItemStack stack) { return stack.is(NTBlocks.BROMELIAD.get().asItem()); }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.STEP_HEIGHT, 1.5D);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getControllingPassenger() != null && player.isSecondaryUseActive()) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        if (this.isFood(stack)) return this.handleBromeliadFeeding(player, stack);
        if (this.getTrustLevel() >= 100 && this.trustsPlayer(player) && !this.isBaby()) {
            if (!player.isSecondaryUseActive()) {
                // Right click: ride
                if (!this.level().isClientSide) {
                    this.ejectPassengers(); // a cub on its back makes room
                    player.startRiding(this);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (hand == InteractionHand.MAIN_HAND && stack.isEmpty()) {
                // Sneak + right click with an empty hand: wander -> follow -> stay
                if (!this.level().isClientSide) this.cycleMode(player);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.mobInteract(player, hand);
    }

    private void cycleMode(Player player) {
        this.mode = (this.mode + 1) % 3;
        this.getNavigation().stop();
        String message = switch (this.mode) {
            case FOLLOW -> "The bear will follow you";
            case STAY -> "The bear will stay here";
            default -> "The bear is free to wander";
        };
        player.displayClientMessage(Component.literal(message), true);
    }

    private InteractionResult handleBromeliadFeeding(Player player, ItemStack stack) {
        if (this.level().isClientSide) return InteractionResult.CONSUME;
        this.climbEnergy = 1000;
        int trustBefore = this.getTrustLevel();
        // The first player to feed it becomes the only one who can earn its trust.
        if (this.getTrustedPlayer() == null) this.setTrustedPlayer(player.getUUID());
        if (this.trustsPlayer(player)) this.setTrustLevel(this.getTrustLevel() + 20);
        this.showTrustFeedback(player, trustBefore);
        if (this.getAge() == 0 && this.canFallInLove()) this.setInLove(player);
        this.playSound(SoundEvents.PANDA_EAT, 1.0F, 1.0F);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return InteractionResult.SUCCESS;
    }

    // Hearts = trust went up, big burst = ready to ride, smoke = it already trusts someone else.
    private void showTrustFeedback(Player player, int trustBefore) {
        if (!(this.level() instanceof ServerLevel server)) return;
        double y = this.getY() + this.getBbHeight();
        if (!this.trustsPlayer(player)) {
            server.sendParticles(ParticleTypes.SMOKE, this.getX(), y, this.getZ(), 8, 0.4D, 0.2D, 0.4D, 0.01D);
        } else if (trustBefore < 100 && this.getTrustLevel() >= 100) {
            server.sendParticles(ParticleTypes.HEART, this.getX(), y, this.getZ(), 12, 0.7D, 0.4D, 0.7D, 0.0D);
            server.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), y - 0.5D, this.getZ(), 20, 0.8D, 0.5D, 0.8D, 0.0D);
            this.playSound(SoundEvents.PLAYER_LEVELUP, 0.6F, 1.2F);
        } else if (this.getTrustLevel() > trustBefore) {
            server.sendParticles(ParticleTypes.HEART, this.getX(), y, this.getZ(), 3, 0.4D, 0.2D, 0.4D, 0.0D);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag c) {
        super.addAdditionalSaveData(c);
        this.addPersistentAngerSaveData(c);
        c.putInt("Variant", this.getVariant());
        if (this.getTrustedPlayer() != null) c.putUUID("TrustedPlayer", this.getTrustedPlayer());
        c.putInt("ClimbEnergy", this.climbEnergy);
        c.putInt("BearMode", this.mode);

    }

    @Override
    public void readAdditionalSaveData(CompoundTag c) {
        super.readAdditionalSaveData(c);
        this.readPersistentAngerSaveData(this.level(), c);
        if (c.contains("Variant")) this.setVariant(c.getInt("Variant"));
        if (c.hasUUID("TrustedPlayer")) this.setTrustedPlayer(c.getUUID("TrustedPlayer"));
        if (c.contains("ClimbEnergy")) this.climbEnergy = c.getInt("ClimbEnergy");
        this.mode = c.getInt("BearMode");
        this.setNoGravity(false); // in case the world was saved while it was climbing to a nest

    }

    @Override public void startPersistentAngerTimer() { this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random)); }
    @Override public void setRemainingPersistentAngerTime(int t) { this.remainingPersistentAngerTime = t; }
    @Override public int getRemainingPersistentAngerTime() { return this.remainingPersistentAngerTime; }
    @Override public void setPersistentAngerTarget(@Nullable UUID t) { this.persistentAngerTarget = t; }
    @Nullable @Override public UUID getPersistentAngerTarget() { return this.persistentAngerTarget; }

    @Nullable public UUID getTrustedPlayer() { return this.entityData.get(DATA_TRUSTED_PLAYER).orElse(null); }
    public void setTrustedPlayer(@Nullable UUID id) { this.entityData.set(DATA_TRUSTED_PLAYER, java.util.Optional.ofNullable(id)); }
    public boolean trustsPlayer(Player player) { return player.getUUID().equals(this.getTrustedPlayer()); }
    public int getVariant() { return this.entityData.get(DATA_VARIANT); }
    public void setVariant(int v) { this.entityData.set(DATA_VARIANT, v); }

    // Gives naturally spawned, spawn egg and den bears one of the four textures.
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty,
                                                                   net.minecraft.world.entity.MobSpawnType spawnType, @Nullable net.minecraft.world.entity.SpawnGroupData data) {
        if (spawnType != net.minecraft.world.entity.MobSpawnType.BREEDING) this.setVariant(this.random.nextInt(4));
        return super.finalizeSpawn(level, difficulty, spawnType, data);
    }

    @Nullable @Override public AgeableMob getBreedOffspring(ServerLevel l, AgeableMob p) {
        SpectacledBearEntity baby = NTEntities.SPECTACLED_BEAR.get().create(l);
        if (baby != null && p instanceof SpectacledBearEntity parent) {
            baby.setVariant(this.random.nextBoolean() ? this.getVariant() : parent.getVariant());
        }
        return baby;
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.POLAR_BEAR_AMBIENT; }
    // Higher than a polar bear, so spectacled bears have their own voice.
    @Override public float getVoicePitch() { return super.getVoicePitch() * 1.35F; }
    @Override protected SoundEvent getHurtSound(DamageSource ds) { return SoundEvents.POLAR_BEAR_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.POLAR_BEAR_DEATH; }
    @Override protected void playStepSound(BlockPos p, BlockState b) { this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F); }

    static class SpectacledPounceGoal extends Goal {
        private final SpectacledBearEntity bear;
        public SpectacledPounceGoal(SpectacledBearEntity bear) { this.bear = bear; }
        @Override public boolean canUse() {
            LivingEntity t = bear.getTarget();
            return t != null && bear.isClimbing() && bear.getY() > t.getY() + 1.5D && bear.distanceToSqr(t) < 16.0D;
        }
        @Override public void start() {
            LivingEntity t = bear.getTarget();
            if (t != null) {
                Vec3 dir = new Vec3(t.getX() - bear.getX(), 0.0D, t.getZ() - bear.getZ()).normalize().scale(0.8D);
                bear.setDeltaMovement(dir.x, 0.4D, dir.z);
            }
        }
    }

    static class StayGoal extends Goal {
        private final SpectacledBearEntity bear;
        public StayGoal(SpectacledBearEntity bear) { this.bear = bear; this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP)); }
        @Override public boolean canUse() {
            return bear.mode == STAY && bear.getTarget() == null && !bear.isInWater() && bear.getControllingPassenger() == null;
        }
        @Override public void start() { bear.getNavigation().stop(); }
    }

    static class FollowTrustedPlayerGoal extends Goal {
        private final SpectacledBearEntity bear;
        private Player player;
        private int ticks;
        public FollowTrustedPlayerGoal(SpectacledBearEntity bear) { this.bear = bear; this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override public boolean canUse() {
            if (bear.mode != FOLLOW || bear.getTrustedPlayer() == null || bear.isLeashed() || bear.getControllingPassenger() != null) return false;
            player = bear.level().getPlayerByUUID(bear.getTrustedPlayer());
            return player != null && !player.isSpectator() && bear.distanceToSqr(player) > 64.0D;
        }
        @Override public boolean canContinueToUse() {
            return bear.mode == FOLLOW && player.isAlive() && player.level() == bear.level()
                    && bear.getControllingPassenger() == null && bear.distanceToSqr(player) > 16.0D;
        }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void start() { ticks = 0; }
        @Override public void stop() { player = null; bear.getNavigation().stop(); }

        @Override public void tick() {
            bear.getLookControl().setLookAt(player);
            if (ticks++ % 10 != 0) return;
            // Left far behind: catch up, like a tamed wolf does.
            if (bear.distanceToSqr(player) > 576.0D && player.onGround() && teleportNearPlayer()) return;
            bear.getNavigation().moveTo(player, 1.3D);
        }

        private boolean teleportNearPlayer() {
            for (int attempt = 0; attempt < 12; attempt++) {
                BlockPos spot = player.blockPosition().offset(bear.getRandom().nextInt(7) - 3, bear.getRandom().nextInt(3) - 1,
                        bear.getRandom().nextInt(7) - 3);
                if (bear.level().getBlockState(spot.below()).isFaceSturdy(bear.level(), spot.below(), Direction.UP)
                        && bear.level().getFluidState(spot).isEmpty()
                        && bear.level().noCollision(bear, bear.getBoundingBox().move(spot.getX() + 0.5D - bear.getX(),
                        spot.getY() - bear.getY(), spot.getZ() + 0.5D - bear.getZ()))) {
                    bear.moveTo(spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D, bear.getYRot(), bear.getXRot());
                    bear.getNavigation().stop();
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * At night an adult scrambles up into a tree, flattens a 3 x 3 platform of leaves on top of the canopy
     * and sleeps there. It remembers the nest and uses it again.
     */
    static class TreeNestGoal extends Goal {
        private static final String NEST = "NTNest";
        private final SpectacledBearEntity bear;
        private BlockPos nest;
        private boolean climbing;
        private boolean arrived;
        private int ticks;
        private long nextTry;
        public TreeNestGoal(SpectacledBearEntity bear) { this.bear = bear; this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP)); }

        @Override public boolean canUse() {
            Level level = bear.level();
            if (!level.isNight() || bear.isBaby() || bear.getTarget() != null || bear.isLeashed() || bear.isPassenger()
                    || bear.getControllingPassenger() != null || level.getGameTime() < nextTry) return false;
            nextTry = level.getGameTime() + 600L;
            nest = rememberedNest();
            if (nest == null && level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)) {
                nest = findTreeTop();
            }
            return nest != null;
        }

        private BlockPos rememberedNest() {
            if (!bear.getPersistentData().contains(NEST)) return null;
            BlockPos pos = BlockPos.of(bear.getPersistentData().getLong(NEST));
            return pos.distSqr(bear.blockPosition()) < 256 && isUsable(pos) ? pos : null;
        }

        private BlockPos findTreeTop() {
            for (int attempt = 0; attempt < 40; attempt++) {
                int x = bear.getBlockX() + bear.getRandom().nextInt(21) - 10;
                int z = bear.getBlockZ() + bear.getRandom().nextInt(21) - 10;
                BlockPos top = new BlockPos(x, bear.level().getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1, z);
                int height = top.getY() - bear.getBlockY();
                if (height >= 2 && height <= 9 && isUsable(top)) return top;
            }
            return null;
        }

        // Leaves in the middle, nothing but leaves or air in the platform, and head room above it.
        private boolean isUsable(BlockPos center) {
            if (!bear.level().getBlockState(center).is(BlockTags.LEAVES)) return false;
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-1, 0, -1), center.offset(1, 0, 1))) {
                BlockState state = bear.level().getBlockState(pos);
                if (!(state.isAir() || state.is(BlockTags.LEAVES))) return false;
                if (!bear.level().isEmptyBlock(pos.above()) || !bear.level().isEmptyBlock(pos.above(2))) return false;
            }
            return true;
        }

        private void buildNest() {
            BlockState leaves = bear.level().getBlockState(nest);
            if (leaves.hasProperty(LeavesBlock.PERSISTENT)) leaves = leaves.setValue(LeavesBlock.PERSISTENT, true);
            for (BlockPos pos : BlockPos.betweenClosed(nest.offset(-1, 0, -1), nest.offset(1, 0, 1))) {
                if (bear.level().isEmptyBlock(pos)) bear.level().setBlock(pos, leaves, 3);
            }
            bear.getPersistentData().putLong(NEST, nest.asLong());
        }

        @Override public boolean canContinueToUse() {
            if (!bear.level().isNight() || bear.getTarget() != null || bear.getControllingPassenger() != null) return false;
            if (arrived) return bear.distanceToSqr(nest.getX() + 0.5D, nest.getY() + 1, nest.getZ() + 0.5D) < 6.0D;
            return ticks < 600;
        }

        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void start() { ticks = 0; climbing = false; arrived = false; }

        @Override public void tick() {
            ticks++;
            if (arrived) {
                bear.getNavigation().stop();
                bear.setSleepyTime(true);
                return;
            }
            if (!climbing) {
                double dx = nest.getX() + 0.5D - bear.getX();
                double dz = nest.getZ() + 0.5D - bear.getZ();
                if (dx * dx + dz * dz > 9.0D) {
                    if (ticks % 10 == 1) bear.getNavigation().moveTo(nest.getX() + 0.5D, bear.getY(), nest.getZ() + 0.5D, 1.0D);
                    return;
                }
                if (bear.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)) buildNest();
                bear.getNavigation().stop();
                climbing = true;
                bear.noPhysics = true; // lets it pass through branches on the way up
                bear.setNoGravity(true);
            }
            Vec3 goal = new Vec3(nest.getX() + 0.5D, nest.getY() + 1.0D, nest.getZ() + 0.5D);
            Vec3 left = goal.subtract(bear.position());
            bear.setDeltaMovement(Vec3.ZERO);
            if (left.length() < 0.2D) {
                bear.setPos(goal.x, goal.y, goal.z);
                letGo();
                arrived = true;
            } else {
                Vec3 step = bear.position().add(left.normalize().scale(0.15D));
                bear.setPos(step.x, step.y, step.z);
            }
        }

        private void letGo() {
            climbing = false;
            bear.noPhysics = false;
            bear.setNoGravity(false);
            bear.resetFallDistance();
        }

        @Override public void stop() {
            letGo();
            bear.setSleepyTime(false);
            nextTry = bear.level().getGameTime() + 200L;
        }
    }

    static class BearDanceGoal extends Goal {
        private final SpectacledBearEntity bear;
        public BearDanceGoal(SpectacledBearEntity bear) { this.bear = bear; this.setFlags(EnumSet.of(Flag.MOVE)); }
        @Override public boolean canUse() { return bear.isDancing() && bear.getControllingPassenger() == null; }
        @Override public void start() { bear.getNavigation().stop(); }
        @Override public void tick() { bear.setStanding(true); }
        @Override public void stop() { bear.setStanding(false); }
    }

    static class BromeliadGrazeGoal extends MoveToBlockGoal {
        private final SpectacledBearEntity bear;
        public BromeliadGrazeGoal(SpectacledBearEntity bear, double s, int r) { super(bear, s, r); this.bear = bear; }
        @Override public double acceptedDistance() { return 2.0D; }
        @Override protected boolean isValidTarget(LevelReader l, BlockPos p) { return l.getBlockState(p).is(NTBlocks.BROMELIAD.get()); }
        @Override public boolean canUse() { return this.bear.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING) && super.canUse(); }
        @Override public void tick() {
            super.tick();
            if (this.isReachedTarget() && this.bear.level().getBlockState(this.blockPos).is(NTBlocks.BROMELIAD.get())) {
                this.bear.level().destroyBlock(this.blockPos, false);
                this.bear.heal(4.0F);
                this.bear.climbEnergy = 1000;
                this.spreadSeeds();
                this.nextStartTick = 1200; // don't start on the new one straight away
            }
        }

        // One eaten, one planted a few blocks away, so bears never strip an area or multiply flowers.
        private void spreadSeeds() {
            BlockState flower = NTBlocks.BROMELIAD.get().defaultBlockState();
            for (int attempt = 0; attempt < 30; attempt++) {
                BlockPos spot = this.blockPos.offset(bear.getRandom().nextInt(11) - 5, bear.getRandom().nextInt(3) - 1,
                        bear.getRandom().nextInt(11) - 5);
                if (spot.distSqr(this.blockPos) >= 4 && bear.level().isEmptyBlock(spot) && flower.canSurvive(bear.level(), spot)) {
                    bear.level().setBlock(spot, flower, 3);
                    return;
                }
            }
        }
    }

    static class MountainForageGoal extends Goal {
        private static final String NEXT = "NTNextForage";
        private final SpectacledBearEntity bear;
        private int timer;
        public MountainForageGoal(SpectacledBearEntity bear) { this.bear = bear; }
        @Override public boolean canUse() {
            return !bear.isBaby() && bear.getY() > 80
                    && bear.level().getGameTime() >= bear.getPersistentData().getLong(NEXT)
                    && bear.getRandom().nextInt(1000) == 0;
        }
        @Override public boolean canContinueToUse() { return timer > 0; }
        @Override public void start() { this.timer = 20; }
        @Override public void tick() {
            if (--this.timer == 0) {
                bear.spawnAtLocation(Items.POTATO);
                // At most one potato per bear per Minecraft day, so it can't be farmed.
                bear.getPersistentData().putLong(NEXT, bear.level().getGameTime() + 24000L);
            }
        }
    }

    static class ScratchTreeGoal extends MoveToBlockGoal {
        private final SpectacledBearEntity bear;
        private int timer;
        public ScratchTreeGoal(SpectacledBearEntity b, double s, int r) { super(b, s, r); this.bear = b; }
        @Override protected boolean isValidTarget(LevelReader l, BlockPos p) { return l.getBlockState(p).is(BlockTags.LOGS); }
        // Walk up to the trunk itself, not to the space on top of it.
        @Override protected BlockPos getMoveToTarget() { return this.blockPos; }
        @Override public double acceptedDistance() { return 2.2D; }
        @Override public void start() { super.start(); this.timer = 0; }
        @Override public boolean canContinueToUse() { return this.timer <= 60 && super.canContinueToUse(); }
        @Override public void tick() {
            super.tick();
            if (this.isReachedTarget()) {
                if (this.timer++ % 20 == 0) {
                    bear.level().playSound(null, blockPos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 0.5F, 1.0F);
                    if (bear.level() instanceof ServerLevel sl) {
                        sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, bear.level().getBlockState(blockPos)), bear.getX(), bear.getY() + 1.2, bear.getZ(), 5, 0.2, 0.2, 0.2, 0.1);
                    }
                }
                bear.setStanding(true);
            }
        }
        @Override public void stop() {
            super.stop();
            bear.setStanding(false);
            this.nextStartTick = 2400; // wait a few minutes before scratching again
        }
    }

    static class LedgeRestingGoal extends MoveToBlockGoal {
        private final SpectacledBearEntity bear;
        public LedgeRestingGoal(SpectacledBearEntity b, double s, int r) { super(b, s, r); this.bear = b; }
        @Override protected boolean isValidTarget(LevelReader l, BlockPos p) {
            return p.getY() > 90 && l.getBlockState(p).isFaceSturdy(l, p, Direction.UP)
                    && l.isEmptyBlock(p.above()) && l.isEmptyBlock(p.above(2));
        }
        @Override public boolean canUse() { return !bear.level().isDay() && bear.getControllingPassenger() == null && super.canUse(); }
        @Override public boolean canContinueToUse() { return !bear.level().isDay() && bear.getControllingPassenger() == null && super.canContinueToUse(); }
        @Override public void tick() { super.tick(); if (this.isReachedTarget()) bear.setSleepyTime(true); }
        @Override public void stop() { super.stop(); bear.setSleepyTime(false); }
    }

    static class BabyScavengerGoal extends Goal {
        private final SpectacledBearEntity baby;
        private ItemEntity food;
        private static final Ingredient FOODS = Ingredient.of(Items.POTATO, Items.SWEET_BERRIES, Items.HONEYCOMB);
        public BabyScavengerGoal(SpectacledBearEntity b) { this.baby = b; this.setFlags(EnumSet.of(Flag.MOVE)); }
        @Override public boolean canUse() {
            if (!baby.isBaby() || baby.isPassenger() || baby.getRandom().nextInt(10) != 0) return false;
            List<ItemEntity> items = baby.level().getEntitiesOfClass(ItemEntity.class, baby.getBoundingBox().inflate(6.0D), i -> FOODS.test(i.getItem()));
            food = items.isEmpty() ? null : items.get(0);
            return food != null;
        }
        @Override public boolean canContinueToUse() { return food != null && food.isAlive() && baby.isBaby() && baby.distanceToSqr(food) < 100.0D; }
        @Override public void tick() {
            if (baby.getNavigation().isDone()) baby.getNavigation().moveTo(food, 1.2D);
            if (baby.distanceToSqr(food) < 2.5D) {
                food.getItem().shrink(1);
                if (food.getItem().isEmpty()) food.discard();
                baby.heal(2.0F);
                baby.playSound(SoundEvents.PANDA_EAT, 1.0F, 1.0F);
                food = null;
            }
        }
    }
}