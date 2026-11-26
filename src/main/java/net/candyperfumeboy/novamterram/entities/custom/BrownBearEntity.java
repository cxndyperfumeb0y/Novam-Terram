package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class BrownBearEntity extends AbstractNTBear implements NeutralMob {
    public static final TagKey<EntityType<?>> BEAR_PREY = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("novamterram", "bear_prey"));

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;
    private int warningSoundTicks;
    // A player who ignored the warning and may now be attacked.
    @Nullable
    private Player warnedPlayer;

    public BrownBearEntity(EntityType<? extends BrownBearEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BrownBearMeleeAttackGoal());
        this.goalSelector.addGoal(1, new BrownBearPanicGoal());
        this.goalSelector.addGoal(1, new WarnIntruderGoal());
        this.goalSelector.addGoal(2, new BearFishingGoal(this)); // Replaced DiveGoal
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, stack -> this.isFood(stack), false));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(5, new EatHoneyGoal(this, 1.1D, 16));
        this.goalSelector.addGoal(6, new HarvestBerriesGoal(this, 1.0D, 12));
        this.goalSelector.addGoal(5, new RaidCampGoal(this, 1.0D, 12));
        this.goalSelector.addGoal(7, new CaveRestingGoal(this, 0.8D, 20));
        this.goalSelector.addGoal(8, new HibernateGoal(this)); // when no cave was found
        this.goalSelector.addGoal(9, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));

        // Added to every bear: age is not known yet here, and the goal checks isBaby() itself.
        this.goalSelector.addGoal(3, new BabyScavengerGoal(this));

        this.targetSelector.addGoal(1, new BrownBearHurtByTargetGoal());
        this.targetSelector.addGoal(2, new BrownBearAttackPlayersGoal());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        // Fish are hunted by BearFishingGoal.
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.warningSoundTicks > 0) this.warningSoundTicks--;
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }
    }

    /* --- NEW FISHING GOAL --- */
    class BearFishingGoal extends Goal {
        private final BrownBearEntity bear;
        private LivingEntity target;
        private int chaseTicks;
        private long nextFishTime;

        public BearFishingGoal(BrownBearEntity bear) {
            this.bear = bear;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (bear.isBaby() || bear.level().getGameTime() < nextFishTime || bear.getRandom().nextInt(20) != 0)
                return false;
            this.target = bear.level().getNearestEntity(
                    bear.level().getEntitiesOfClass(LivingEntity.class, bear.getBoundingBox().inflate(10.0D),
                            e -> e.getType().is(BEAR_PREY)),
                    TargetingConditions.forNonCombat().range(10.0D), bear, bear.getX(), bear.getY(), bear.getZ());
            return this.target != null;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive() && chaseTicks < 200 && bear.distanceToSqr(target) < 144.0D;
        }

        @Override
        public void start() {
            chaseTicks = 0;
            bear.setDiving(true);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            bear.getLookControl().setLookAt(target);
            if (chaseTicks++ % 10 == 0) bear.getNavigation().moveTo(target, 1.8D);
            if (bear.distanceToSqr(target) < 6.0D
                    && target.hurt(bear.damageSources().mobAttack(bear), 6.0F)
                    && target.isDeadOrDying() && bear.getRandom().nextFloat() < 0.25F) {
                bear.spawnAtLocation(Items.SALMON); // sometimes there are scraps left over
            }
        }

        @Override
        public void stop() {
            bear.setDiving(false);
            bear.getNavigation().stop();
            nextFishTime = bear.level().getGameTime() + 1200; // one catch attempt per minute
            target = null;
        }
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
        return stack.is(ItemTags.FISHES) || stack.is(ItemTags.FOX_FOOD);
    }

    /* --- ANGER & BREEDING LOGIC --- */
    @Override
    public void addAdditionalSaveData(CompoundTag c) {
        super.addAdditionalSaveData(c);
        this.addPersistentAngerSaveData(c);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag c) {
        super.readAdditionalSaveData(c);
        this.readPersistentAngerSaveData(this.level(), c);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void setRemainingPersistentAngerTime(int t) {
        this.remainingPersistentAngerTime = t;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel l, AgeableMob p) {
        return NTEntities.BROWN_BEAR.get().create(l);
    }

    /* --- SOUNDS --- */
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isBaby() ? SoundEvents.POLAR_BEAR_AMBIENT_BABY : SoundEvents.POLAR_BEAR_AMBIENT;
    }

    // Deeper than a polar bear, so brown bears sound different (cubs are still squeakier).
    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.85F;
    }

    // About 1 in 4 naturally spawned pairs is a mother with a cub (the first of a group is always an adult).
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty,
                                                                   net.minecraft.world.entity.MobSpawnType spawnType, @Nullable net.minecraft.world.entity.SpawnGroupData data) {
        if (data == null) data = new AgeableMob.AgeableMobGroupData(0.25F);
        return super.finalizeSpawn(level, difficulty, spawnType, data);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource d) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos p, BlockState b) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.0F, this.getVoicePitch());
            this.warningSoundTicks = 40;
        }
    }

    /* --- GOAL CLASSES --- */
    class BrownBearMeleeAttackGoal extends MeleeAttackGoal {
        public BrownBearMeleeAttackGoal() {
            super(BrownBearEntity.this, 1.25D, true);
        }

        @Override
        public void tick() {
            super.tick();
            LivingEntity enemy = this.mob.getTarget();
            if (enemy != null && this.mob.distanceToSqr(enemy) <= 4.0D) {
                if (this.getTicksUntilNextAttack() <= 10) {
                    BrownBearEntity.this.setStanding(true);
                    BrownBearEntity.this.playWarningSound();
                }
            } else {
                BrownBearEntity.this.setStanding(false);
            }
        }

        @Override
        public void stop() {
            BrownBearEntity.this.setStanding(false);
            super.stop();
        }
    }

    class BrownBearAttackPlayersGoal extends NearestAttackableTargetGoal<Player> {
        public BrownBearAttackPlayersGoal() {
            super(BrownBearEntity.this, Player.class, 20, true, true, null);
        }

        // Defensive only: attacks once WarnIntruderGoal has been ignored. Being hit is handled by HurtByTarget.
        @Override
        public boolean canUse() {
            Player intruder = BrownBearEntity.this.warnedPlayer;
            if (intruder == null) return false;
            BrownBearEntity.this.warnedPlayer = null;
            this.target = intruder;
            return intruder.isAlive() && !intruder.isCreative() && !intruder.isSpectator();
        }

        @Override
        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.5D;
        }
    }

    private boolean hasCubNearby() {
        return !this.level().getEntitiesOfClass(BrownBearEntity.class,
                this.getBoundingBox().inflate(8.0D, 4.0D, 8.0D), BrownBearEntity::isBaby).isEmpty();
    }

    /**
     * Stands up and growls first. Backing away ends it; staying about 3 seconds or coming very close does not.
     */
    class WarnIntruderGoal extends Goal {
        private Player intruder;
        private int ticks;
        private boolean done;

        public WarnIntruderGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            BrownBearEntity bear = BrownBearEntity.this;
            if (bear.isBaby() || bear.getTarget() != null || bear.getRandom().nextInt(5) != 0
                    || bear.level().getDifficulty() == Difficulty.PEACEFUL) return false;
            intruder = bear.level().getNearestPlayer(bear.getX(), bear.getY(), bear.getZ(), 12.0D,
                    EntitySelector.NO_CREATIVE_OR_SPECTATOR);
            return intruder != null && bear.hasLineOfSight(intruder) && bear.hasCubNearby();
        }

        @Override
        public boolean canContinueToUse() {
            return !done && intruder.isAlive() && BrownBearEntity.this.getTarget() == null
                    && BrownBearEntity.this.distanceToSqr(intruder) < 144.0D;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            ticks = 0;
            done = false;
            BrownBearEntity.this.setSleepyTime(false);
            BrownBearEntity.this.getNavigation().stop();
        }

        @Override
        public void tick() {
            BrownBearEntity bear = BrownBearEntity.this;
            bear.setStanding(true);
            bear.getLookControl().setLookAt(intruder, 30.0F, 30.0F);
            if (ticks % 25 == 0) bear.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.2F, bear.getVoicePitch());
            ticks++;
            double distance = bear.distanceToSqr(intruder);
            if (distance < 16.0D || (ticks >= 60 && distance < 100.0D)) {
                bear.warnedPlayer = intruder;
                done = true;
            }
        }

        @Override
        public void stop() {
            BrownBearEntity.this.setStanding(false);
            intruder = null;
        }
    }

    // A player who is not sneaking, close enough to wake a resting bear.
    private boolean isDisturbed() {
        Player player = this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 5.0D,
                EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        return player != null && !player.isShiftKeyDown();
    }

    private boolean isSnowedOn() {
        BlockPos pos = this.blockPosition();
        return this.level().isRaining()
                && this.level().getBiome(pos).value().getPrecipitationAt(pos) == Biome.Precipitation.SNOW;
    }

    /**
     * Light hibernation: dozes where it is while snow falls, or late at night.
     */
    static class HibernateGoal extends Goal {
        private final BrownBearEntity bear;

        public HibernateGoal(BrownBearEntity bear) {
            this.bear = bear;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        private boolean sleepWeather() {
            long time = bear.level().getDayTime() % 24000L;
            return bear.isSnowedOn() || (time > 15000L && time < 22500L);
        }

        private boolean calm() {
            return bear.getTarget() == null && bear.hurtTime == 0 && !bear.isInWater() && !bear.isPassenger()
                    && !bear.isDisturbed();
        }

        @Override
        public boolean canUse() {
            return bear.getRandom().nextInt(10) == 0 && bear.onGround() && sleepWeather() && calm();
        }

        @Override
        public boolean canContinueToUse() {
            return sleepWeather() && calm();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        } // so the healing timer is reliable

        @Override
        public void start() {
            bear.getNavigation().stop();
            bear.setSleepyTime(true);
        }

        @Override
        public void tick() {
            if (bear.tickCount % 100 == 0) bear.heal(1.0F);
        }

        @Override
        public void stop() {
            bear.setSleepyTime(false);
        }
    }

    /**
     * Takes one piece of food from a campfire, or from a chest left under the open sky.
     */
    static class RaidCampGoal extends MoveToBlockGoal {
        private final BrownBearEntity bear;
        private boolean taken;

        public RaidCampGoal(BrownBearEntity bear, double speed, int range) {
            super(bear, speed, range, 3);
            this.bear = bear;
        }

        private static boolean isCampFood(ItemStack stack) {
            // Plain meat, fish, fruit and bread only. Golden apples and renamed items are never taken.
            return !stack.isEmpty() && !stack.has(DataComponents.CUSTOM_NAME)
                    && (stack.is(ItemTags.MEAT) || stack.is(ItemTags.FISHES) || stack.is(Items.SWEET_BERRIES)
                    || stack.is(Items.GLOW_BERRIES) || stack.is(Items.APPLE) || stack.is(Items.BREAD));
        }

        @Override
        public boolean canUse() {
            return !bear.isBaby() && bear.getTarget() == null
                    && bear.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)
                    && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            this.taken = false;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.taken && super.canContinueToUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof CampfireBlock) {
                return level.getBlockEntity(pos) instanceof CampfireBlockEntity fire
                        && fire.getItems().stream().anyMatch(RaidCampGoal::isCampFood);
            }
            // check the block first: looking up a block entity at every position is much slower
            return state.getBlock() instanceof ChestBlock && findInChest(level, pos) >= 0;
        }

        // Slot with food, or -1. Unopened loot chests are left alone so their loot is not rolled early.
        private static int findInChest(LevelReader level, BlockPos pos) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (!(entity instanceof ChestBlockEntity chest) || chest.getLootTable() != null || !level.canSeeSky(pos.above()))
                return -1;
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                if (isCampFood(chest.getItem(slot))) return slot;
            }
            return -1;
        }

        // Stop beside the block instead of climbing on top of a fire.
        @Override
        protected BlockPos getMoveToTarget() {
            return this.blockPos;
        }

        @Override
        public double acceptedDistance() {
            return 2.5D;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.taken || !this.isReachedTarget() || !isValidTarget(bear.level(), this.blockPos)) return;

            if (bear.level().getBlockEntity(this.blockPos) instanceof CampfireBlockEntity fire) {
                for (int slot = 0; slot < fire.getItems().size(); slot++) {
                    if (isCampFood(fire.getItems().get(slot))) {
                        fire.getItems().set(slot, ItemStack.EMPTY);
                        fire.setChanged();
                        BlockState state = fire.getBlockState();
                        bear.level().sendBlockUpdated(this.blockPos, state, state, 3);
                        break;
                    }
                }
            } else if (bear.level().getBlockEntity(this.blockPos) instanceof ChestBlockEntity chest) {
                chest.removeItem(findInChest(bear.level(), this.blockPos), 1);
                chest.setChanged();
                bear.playSound(SoundEvents.CHEST_OPEN, 0.8F, 0.8F);
            }
            bear.heal(4.0F);
            bear.playSound(SoundEvents.PANDA_EAT, 1.0F, 0.8F);
            this.taken = true; // one item per visit
            this.nextStartTick = 6000; // leaves the camp alone for about five minutes
        }
    }

    class BrownBearHurtByTargetGoal extends HurtByTargetGoal {
        public BrownBearHurtByTargetGoal() {
            super(BrownBearEntity.this);
        }

        @Override
        public void start() {
            super.start();
            if (BrownBearEntity.this.isBaby()) {
                this.alertOthers();
                this.stop();
            }
        }
    }

    class BrownBearPanicGoal extends PanicGoal {
        public BrownBearPanicGoal() {
            super(BrownBearEntity.this, 2.0D);
        }

        @Override
        public boolean canUse() {
            return (BrownBearEntity.this.isBaby() || BrownBearEntity.this.isOnFire()) && super.canUse();
        }
    }

    static class EatHoneyGoal extends MoveToBlockGoal {
        private final BrownBearEntity bear;

        public EatHoneyGoal(BrownBearEntity b, double s, int r) {
            super(b, s, r, 4);
            this.bear = b;
        }

        @Override
        protected boolean isValidTarget(LevelReader l, BlockPos p) {
            BlockState s = l.getBlockState(p);
            return s.is(BlockTags.BEEHIVES) && s.hasProperty(BeehiveBlock.HONEY_LEVEL) && s.getValue(BeehiveBlock.HONEY_LEVEL) > 0;
        }

        @Override
        public boolean canUse() {
            return this.bear.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING) && super.canUse();
        }

        // Nests hang above the ground, so the bear only has to get close and reach up.
        @Override
        protected BlockPos getMoveToTarget() {
            return this.blockPos;
        }

        @Override
        public double acceptedDistance() {
            return 3.0D;
        }

        @Override
        public void tick() {
            super.tick();
            BlockState s = bear.level().getBlockState(this.blockPos);
            if (this.isReachedTarget() && isValidTarget(bear.level(), this.blockPos)) {
                bear.level().setBlock(this.blockPos, s.setValue(BeehiveBlock.HONEY_LEVEL, 0), 3);
                bear.heal(4.0F);
                bear.playSound(SoundEvents.HONEY_BLOCK_BREAK, 1.0F, 1.0F);
            }
        }
    }

    static class HarvestBerriesGoal extends MoveToBlockGoal {
        private final BrownBearEntity bear;

        public HarvestBerriesGoal(BrownBearEntity b, double s, int r) {
            super(b, s, r);
            this.bear = b;
        }

        @Override
        public double acceptedDistance() {
            return 2.0D;
        }

        @Override
        protected boolean isValidTarget(LevelReader l, BlockPos p) {
            BlockState s = l.getBlockState(p);
            return s.is(Blocks.SWEET_BERRY_BUSH) && s.getValue(SweetBerryBushBlock.AGE) >= 2;
        }

        @Override
        public boolean canUse() {
            return this.bear.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING) && super.canUse();
        }

        @Override
        public void tick() {
            super.tick();
            BlockState s = bear.level().getBlockState(this.blockPos);
            if (this.isReachedTarget() && s.is(Blocks.SWEET_BERRY_BUSH) && s.getValue(SweetBerryBushBlock.AGE) >= 2) {
                bear.level().setBlock(this.blockPos, s.setValue(SweetBerryBushBlock.AGE, 1), 2);
                bear.heal(2.0F);
                bear.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
            }
        }
    }

    static class CaveRestingGoal extends MoveToBlockGoal {
        private final BrownBearEntity bear;

        public CaveRestingGoal(BrownBearEntity b, double s, int r) {
            super(b, s, r);
            this.bear = b;
        }

        private boolean restTime() {
            return bear.level().isNight() || bear.level().isRaining();
        }

        @Override
        protected boolean isValidTarget(LevelReader l, BlockPos p) {
            return l.getBlockState(p).isFaceSturdy(l, p, Direction.UP) && l.isEmptyBlock(p.above()) && l.isEmptyBlock(p.above(2))
                    && !l.canSeeSky(p.above());
        }

        // Check the time first so the block search only runs when it is needed.
        @Override
        public boolean canUse() {
            return restTime() && !bear.isDisturbed() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return restTime() && !bear.isDisturbed() && bear.hurtTime == 0 && super.canContinueToUse();
        }

        @Override
        public void tick() {
            super.tick();
            if (this.isReachedTarget()) bear.setSleepyTime(true);
        }

        @Override
        public void stop() {
            super.stop();
            bear.setSleepyTime(false);
        }
    }

    static class BabyScavengerGoal extends Goal {
        private final BrownBearEntity baby;
        private ItemEntity food;

        public BabyScavengerGoal(BrownBearEntity b) {
            this.baby = b;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!baby.isBaby() || baby.isPassenger() || baby.getRandom().nextInt(10) != 0) return false;
            List<ItemEntity> items = baby.level().getEntitiesOfClass(ItemEntity.class, baby.getBoundingBox().inflate(6.0D), i -> i.getItem().is(ItemTags.FOX_FOOD));
            food = items.isEmpty() ? null : items.get(0);
            return food != null;
        }

        @Override
        public boolean canContinueToUse() {
            return food != null && food.isAlive() && baby.isBaby() && baby.distanceToSqr(food) < 100.0D;
        }

        @Override
        public void tick() {
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