package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class SongbirdEntity extends Parrot {

    private static final Ingredient FOOD_ITEMS = Ingredient.of(
            Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS,
            Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS
    );

    public SongbirdEntity(EntityType<? extends Parrot> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Parrot.createAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.FLYING_SPEED, 0.48F)
                .add(Attributes.MOVEMENT_SPEED, 0.22F);
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

    // --- RIDING LOGIC (IRON GOLEM PERCH) ---

    @Override
    public Vec3 getPassengerRidingPosition(Entity entity) {
        // Keeps the bird centered on the Golem's head
        return super.getPassengerRidingPosition(entity).add(0, 0.45, 0);
    }

    // --- BREEDING & TAMING ---

    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD_ITEMS.test(stack);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        SongbirdEntity baby = NTEntities.SONGBIRD.get().create(level);
        if (baby != null && parent instanceof SongbirdEntity parentBird) {
            baby.setVariant(parentBird.getVariant());
        }
        return baby;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!this.isTame() && FOOD_ITEMS.test(itemstack)) {
            if (!player.getAbilities().instabuild) itemstack.shrink(1);
            if (!this.level().isClientSide) {
                if (this.random.nextInt(10) == 0) {
                    this.tame(player);
                    this.level().broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte)6);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (this.isTame() && this.isOwnedBy(player)) {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;

        // 1. Skittishness (Unless owner/holding food)
        Player nearbyPlayer = this.level().getNearestPlayer(this, 5.0D);
        if (nearbyPlayer != null && !this.isOwnedBy(nearbyPlayer)) {
            if (!FOOD_ITEMS.test(nearbyPlayer.getMainHandItem()) && this.distanceToSqr(nearbyPlayer) < 9.0D) {
                if (this.isPassenger()) this.stopRiding();
                this.setOrderedToSit(false);
                this.setInSittingPose(false);
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.4, 0));
            }
        }

        // 2. High-pitched Morning Chorus
        if (this.level().getDayTime() % 24000 < 1000 && this.random.nextInt(120) == 0) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.PARROT_AMBIENT,
                    this.getSoundSource(), 1.0F, 1.7F);
        }

        // 3. Dismount if Golem is in danger
        if (this.isPassenger() && this.getVehicle() instanceof IronGolem golem) {
            if (golem.getTarget() != null || this.random.nextInt(1000) == 0) {
                this.stopRiding();
            }
        }
    }

    // --- CUSTOM AI GOAL CLASSES ---

    /** Goal: Find an Iron Golem and physically sit on its head. */
    static class PerchOnGolemGoal extends Goal {
        private final SongbirdEntity bird;
        private IronGolem golem;

        public PerchOnGolemGoal(SongbirdEntity bird) {
            this.bird = bird;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.bird.isPassenger() || this.bird.isOrderedToSit()) return false;
            if (this.bird.getRandom().nextInt(200) != 0) return false;

            List<IronGolem> list = this.bird.level().getEntitiesOfClass(IronGolem.class, this.bird.getBoundingBox().inflate(10.0D));
            if (!list.isEmpty()) {
                this.golem = list.get(0);
                return true;
            }
            return false;
        }

        @Override
        public void tick() {
            if (this.golem != null && this.golem.isAlive()) {
                this.bird.getNavigation().moveTo(this.golem, 1.2D);
                if (this.bird.distanceToSqr(this.golem) < 3.0D) {
                    this.bird.startRiding(this.golem, true);
                }
            }
        }
    }

    /** Goal: Prefer landing on blocks tagged with #minecraft:leaves. */
    static class SongbirdLeafPerchGoal extends MoveToBlockGoal {
        private final SongbirdEntity bird;

        public SongbirdLeafPerchGoal(SongbirdEntity bird, double speed) {
            super(bird, speed, 16);
            this.bird = bird;
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            return level.getBlockState(pos).is(BlockTags.LEAVES) && level.isEmptyBlock(pos.above());
        }

        @Override
        public boolean canUse() {
            return !this.bird.isOrderedToSit() && super.canUse();
        }
    }

    /** Goal: Follow Faun entities (Requested feature). */
    static class FollowFaunGoal extends Goal {
        private final SongbirdEntity bird;
        private LivingEntity faun;
        private final double speed;
        private final float minDist;
        private final float maxDist;

        public FollowFaunGoal(SongbirdEntity bird, double speed, float min, float max) {
            this.bird = bird;
            this.speed = speed;
            this.minDist = min;
            this.maxDist = max;
        }

        @Override
        public boolean canUse() {
            if (this.bird.isOrderedToSit() || this.bird.isPassenger()) return false;
            List<? extends LivingEntity> list = this.bird.level().getEntitiesOfClass(FaunEntity.class, this.bird.getBoundingBox().inflate(12.0D));
            if (!list.isEmpty()) {
                this.faun = list.get(0);
                return this.bird.distanceToSqr(this.faun) > (minDist * minDist);
            }
            return false;
        }

        @Override
        public void tick() {
            if (this.faun != null && this.bird.distanceToSqr(this.faun) > (maxDist * maxDist)) {
                this.bird.getNavigation().moveTo(this.faun, this.speed);
            }
        }
    }

    static class SongbirdShelterGoal extends MoveToBlockGoal {
        public SongbirdShelterGoal(SongbirdEntity bird) { super(bird, 1.2D, 16); }
        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) { return !level.canSeeSky(pos.above()); }
        @Override
        public boolean canUse() { return this.mob.level().isRaining() && super.canUse(); }
    }
}