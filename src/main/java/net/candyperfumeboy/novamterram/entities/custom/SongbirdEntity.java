package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class SongbirdEntity extends Parrot {

    private static final Ingredient FOOD_ITEMS = Ingredient.of(
            Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS,
            Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS
    );

    // Synced so the client knows which texture to draw
    private static final EntityDataAccessor<Integer> DATA_COLOR =
            SynchedEntityData.defineId(SongbirdEntity.class, EntityDataSerializers.INT);

    public SongbirdEntity(EntityType<? extends Parrot> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Parrot.createAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.FLYING_SPEED, 0.48F)
                .add(Attributes.MOVEMENT_SPEED, 0.22F);
    }

    // --- COLOUR ---

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLOR, 0);
    }

    public SongbirdColor getSongbirdColor() {
        return SongbirdColor.byIndex(this.entityData.get(DATA_COLOR));
    }

    public void setSongbirdColor(SongbirdColor color) {
        this.entityData.set(DATA_COLOR, color.ordinal());
    }

    // Saved by name, so adding or reordering colours later won't repaint existing birds
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("SongbirdColor", this.getSongbirdColor().id());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSongbirdColor(SongbirdColor.byId(compound.getString("SongbirdColor")));
    }

    // Natural spawns, spawn eggs and /summon get a random colour
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        this.setSongbirdColor(SongbirdColor.random(this.random));
        return data;
    }

    @Override
    protected void registerGoals() {
        // Priority 0: Emergency
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Priority 1: Commands & Taming
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 5.0F, 1.0F));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, FOOD_ITEMS, false));

        // Priority 3: Special Interactions
        this.goalSelector.addGoal(3, new PerchOnGolemGoal(this)); // Physical perching
        this.goalSelector.addGoal(3, new FollowFaunGoal(this, 1.0D, 3.0F, 7.0F));

        // Priority 4: Environmental
        this.goalSelector.addGoal(4, new SongbirdLeafPerchGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new SongbirdShelterGoal(this));

        // Priority 5: Default Flight & Looking
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    // --- BREEDING & TAMING ---

    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD_ITEMS.test(stack);
    }

    // Vanilla parrots cannot breed or be babies, so both are switched back on here
    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }

    @Override
    public boolean canMate(Animal other) {
        return other != this && other instanceof SongbirdEntity bird && this.isInLove() && bird.isInLove();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        SongbirdEntity baby = NTEntities.SONGBIRD.get().create(level);
        if (baby != null && parent instanceof SongbirdEntity parentBird) {
            // Chicks take after one of their parents
            baby.setSongbirdColor(this.random.nextBoolean() ? this.getSongbirdColor() : parentBird.getSongbirdColor());
            if (this.isTame()) {
                baby.setOwnerUUID(this.getOwnerUUID());
                baby.setTame(true, true);
            }
        }
        return baby;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean food = this.isFood(stack);

        if (!this.isTame() && food) {
            if (!player.getAbilities().instabuild) stack.shrink(1);
            if (!this.level().isClientSide) {
                if (this.random.nextInt(10) == 0) {
                    this.tame(player);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (this.isTame() && this.isOwnedBy(player)) {
            if (!this.level().isClientSide) {
                if (food && this.getHealth() < this.getMaxHealth()) {
                    this.heal(2.0F);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                } else if (food && this.getAge() == 0 && this.canFallInLove()) {
                    this.setInLove(player);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                } else {
                    this.setOrderedToSit(!this.isOrderedToSit());
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;

        // 1. Wild birds flutter away from anyone who gets close without food
        if (!this.isTame() && this.tickCount % 10 == 0 && (this.onGround() || this.isPassenger())) {
            Player near = this.level().getNearestPlayer(this, 3.0D);
            if (near != null && !near.isSpectator()
                    && !this.isFood(near.getMainHandItem()) && !this.isFood(near.getOffhandItem())) {
                this.stopRiding();
                Vec3 away = this.position().subtract(near.position()).multiply(1, 0, 1).normalize().scale(0.3);
                this.setDeltaMovement(away.x, 0.4, away.z);
            }
        }

        // 2. High-pitched Morning Chorus
        if (this.level().getDayTime() % 24000 < 1000 && this.random.nextInt(120) == 0) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.PARROT_AMBIENT,
                    this.getSoundSource(), 1.0F, 1.7F);
        }

        // 3. Dismount if Golem is in danger, or now and then
        if (this.getVehicle() instanceof IronGolem golem
                && (golem.getTarget() != null || this.random.nextInt(1000) == 0)) {
            this.stopRiding();
        }
    }

    // --- CUSTOM AI GOAL CLASSES ---

    /** Goal: Find an Iron Golem and physically sit on its head. */
    static class PerchOnGolemGoal extends Goal {
        private final SongbirdEntity bird;
        private IronGolem golem;
        private int timeLeft;

        public PerchOnGolemGoal(SongbirdEntity bird) {
            this.bird = bird;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        private boolean canPerchOn(IronGolem target) {
            return target.isAlive() && !target.isVehicle() && target.getTarget() == null;
        }

        @Override
        public boolean canUse() {
            if (this.bird.isPassenger() || this.bird.isOrderedToSit()) return false;
            if (this.bird.getRandom().nextInt(100) != 0) return false;

            List<IronGolem> list = this.bird.level().getEntitiesOfClass(IronGolem.class,
                    this.bird.getBoundingBox().inflate(10.0D), this::canPerchOn);
            if (list.isEmpty()) return false;
            this.golem = list.get(0);
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.timeLeft > 0 && !this.bird.isPassenger() && !this.bird.isOrderedToSit()
                    && canPerchOn(this.golem);
        }

        @Override
        public void start() {
            this.timeLeft = this.adjustedTickDelay(200);
        }

        @Override
        public void stop() {
            this.bird.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.timeLeft--;
            // Aim for the top of the golem, not its feet
            this.bird.getNavigation().moveTo(this.golem.getX(), this.golem.getY() + this.golem.getBbHeight(),
                    this.golem.getZ(), 1.2D);
            if (this.bird.getBoundingBox().inflate(1.0D).intersects(this.golem.getBoundingBox())) {
                this.bird.startRiding(this.golem, true);
            }
        }
    }

    /** Goal: Prefer landing on blocks tagged with #minecraft:leaves. */
    static class SongbirdLeafPerchGoal extends MoveToBlockGoal {
        private final SongbirdEntity bird;

        public SongbirdLeafPerchGoal(SongbirdEntity bird, double speed) {
            // The last number lets it look up and down for leaves, not just at its own height
            super(bird, speed, 12, 6);
            this.bird = bird;
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            return level.getBlockState(pos).is(BlockTags.LEAVES) && level.isEmptyBlock(pos.above());
        }

        @Override
        public boolean canUse() {
            return !this.bird.isOrderedToSit() && !this.bird.isPassenger() && super.canUse();
        }
    }

    /** Goal: Follow Faun entities. */
    static class FollowFaunGoal extends Goal {
        private final SongbirdEntity bird;
        private LivingEntity faun;
        private final double speed;
        private final float minDist;
        private final float maxDist;
        private int repathDelay;

        public FollowFaunGoal(SongbirdEntity bird, double speed, float min, float max) {
            this.bird = bird;
            this.speed = speed;
            this.minDist = min;
            this.maxDist = max;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.bird.isOrderedToSit() || this.bird.isPassenger()) return false;
            if (this.bird.getRandom().nextInt(10) != 0) return false;

            List<FaunEntity> list = this.bird.level().getEntitiesOfClass(FaunEntity.class,
                    this.bird.getBoundingBox().inflate(12.0D));
            if (list.isEmpty()) return false;
            this.faun = list.get(0);
            return this.bird.distanceToSqr(this.faun) > maxDist * maxDist;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.bird.isOrderedToSit() && !this.bird.isPassenger() && this.faun.isAlive()
                    && this.bird.distanceToSqr(this.faun) > minDist * minDist
                    && this.bird.distanceToSqr(this.faun) < 256.0D;
        }

        @Override
        public void start() {
            this.repathDelay = 0;
        }

        @Override
        public void stop() {
            this.bird.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (--this.repathDelay > 0) return;
            this.repathDelay = this.adjustedTickDelay(10);
            this.bird.getNavigation().moveTo(this.faun, this.speed);
        }
    }

    /** Goal: get under cover when it rains. */
    static class SongbirdShelterGoal extends MoveToBlockGoal {
        private final SongbirdEntity bird;

        public SongbirdShelterGoal(SongbirdEntity bird) {
            super(bird, 1.2D, 12, 4);
            this.bird = bird;
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            // Something to stand on, open space above it, and a roof over that space
            return !level.getBlockState(pos).isAir() && level.isEmptyBlock(pos.above())
                    && !level.canSeeSky(pos.above());
        }

        @Override
        public boolean canUse() {
            return this.bird.level().isRaining() && !this.bird.isOrderedToSit() && !this.bird.isPassenger()
                    && super.canUse();
        }
    }
}
