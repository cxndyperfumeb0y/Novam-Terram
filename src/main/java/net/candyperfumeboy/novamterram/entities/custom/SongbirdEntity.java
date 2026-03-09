package net.candyperfumeboy.novamterram.entities.custom;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class SongbirdEntity extends Parrot {

    public SongbirdEntity(EntityType<? extends Parrot> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Parrot.createAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.FLYING_SPEED, 0.45F)
                .add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    @Override
    protected void registerGoals() {
        // Priority 0: Survival
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Priority 2: Interaction
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));

        // Priority 3: Symbiosis (Following Fauns)
        this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0D, 3.0F, 7.0F));

        // Priority 4: Environment - Seek Shelter during rain
        this.goalSelector.addGoal(4, new MoveToBlockGoal(this, 1.0D, 16) {
            @Override
            protected boolean isValidTarget(LevelReader level, BlockPos pos) {
                return !level.canSeeSky(pos.above());
            }

            @Override
            public boolean canUse() {
                return SongbirdEntity.this.level().isRaining() && super.canUse();
            }
        });

        // Priority 5: Movement
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.isAlive()) {

            // --- 1. SKITTISH REACTION ---
            Player nearbyPlayer = this.level().getNearestPlayer(this, 5.0D);
            if (nearbyPlayer != null) {
                boolean tooClose = this.distanceToSqr(nearbyPlayer) < 9.0D;
                boolean playerSprinting = nearbyPlayer.isSprinting() && this.distanceToSqr(nearbyPlayer) < 25.0D;

                if (tooClose || playerSprinting) {
                    if (this.onGround() || this.isInSittingPose()) {
                        this.setInSittingPose(false);
                        this.setOrderedToSit(false);
                        this.setDeltaMovement(this.getDeltaMovement().add(0, 0.3, 0));
                    }
                }
            }

            // --- 2. MORNING CHORUS & WEATHER ---
            long time = this.level().getDayTime() % 24000;
            boolean isMorning = time > 0 && time < 1000;

            if (isMorning && this.random.nextInt(80) == 0) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.PARROT_AMBIENT, this.getSoundSource(), 1.0F, 1.2F);
            }

            if (this.level().isRaining() && !this.onGround() && this.random.nextInt(100) == 0) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.2, 0));
            }

            // --- 3. BERRY FORAGING ---
            if (this.random.nextInt(200) == 0) {
                BlockState state = this.level().getBlockState(this.blockPosition());
                if (state.is(Blocks.SWEET_BERRY_BUSH)) {
                    this.level().playSound(null, this.blockPosition(), SoundEvents.PARROT_EAT, this.getSoundSource(), 1.0F, 1.0F);
                }
            }

            // --- 4. MUSIC DANCING & PARTICLES ---
            if (this.isPartyParrot()) {
                if (this.random.nextInt(15) == 0) {
                    ((ServerLevel)this.level()).sendParticles(ParticleTypes.NOTE,
                            this.getX(), this.getY() + 0.6, this.getZ(), 1, 0.3, 0.3, 0.3, 0.1);
                }
                // Small chance to heal the player slightly while dancing (The Harmony Effect)
                if (nearbyPlayer != null && this.distanceToSqr(nearbyPlayer) < 16.0D && this.random.nextInt(100) == 0) {
                    nearbyPlayer.heal(0.5F);
                    ((ServerLevel)this.level()).sendParticles(ParticleTypes.HEART,
                            this.getX(), this.getY() + 0.5, this.getZ(), 1, 0, 0, 0, 0);
                }
            }

            // --- 5. SEED SCATTERER (Environment Growth) ---
            if (this.onGround() && this.random.nextInt(2000) == 0) {
                BlockPos pos = this.blockPosition();
                if (this.level().getBlockState(pos).isAir() && this.level().getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)) {
                    this.level().setBlock(pos, Blocks.SHORT_GRASS.defaultBlockState(), 3);
                }
            }
        }
    }

    // Ensure the bird dances to nearby music
    @Override
    public void setRecordPlayingNearby(BlockPos pos, boolean isPlaying) {
        super.setRecordPlayingNearby(pos, isPlaying);
    }

    // --- HELPER GOAL: FOLLOW FAUN ---
    private static class FollowMobGoal extends Goal {
        private final SongbirdEntity bird;
        private FaunEntity targetMob;
        private final double speed;
        private final float minDist;
        private final float maxDist;

        public FollowMobGoal(SongbirdEntity bird, double speed, float min, float max) {
            this.bird = bird;
            this.speed = speed;
            this.minDist = min;
            this.maxDist = max;
        }

        @Override
        public boolean canUse() {
            if (this.bird.level().getGameTime() % 40 == 0) { // Optimization: Only check every 2 seconds
                List<FaunEntity> list = this.bird.level().getEntitiesOfClass(FaunEntity.class, this.bird.getBoundingBox().inflate(12.0D));
                if (!list.isEmpty()) {
                    this.targetMob = list.get(0);
                    return this.bird.distanceToSqr(this.targetMob) > (minDist * minDist);
                }
            }
            return false;
        }

        @Override
        public void tick() {
            if (this.targetMob != null && this.bird.distanceToSqr(this.targetMob) > (maxDist * maxDist)) {
                this.bird.getNavigation().moveTo(this.targetMob, this.speed);
            }
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        return null;
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, net.minecraft.world.damagesource.DamageSource pSource) {
        return false;
    }
}