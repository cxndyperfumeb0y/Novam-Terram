package net.candyperfumeboy.novamterram.entities.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/** The songbird's daily gift, flocking, roosting, hunting silverfish and escaping predators. */
public final class SongbirdGoals {

    private SongbirdGoals() {
    }

    /** A pet bird brings its owner a seed once a day. The limit is per player, so more birds don't mean more seeds. */
    static class SeedGiftGoal extends Goal {
        private static final String LAST_GIFT = "NTSongbirdSeedDay";
        private static final Item[] SEEDS = {
                Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS
        };

        private final SongbirdEntity bird;
        private ServerPlayer owner;
        private int nextCheck;
        private int timeLeft;

        SeedGiftGoal(SongbirdEntity bird) {
            this.bird = bird;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        private static long today(ServerPlayer player) {
            return player.level().getGameTime() / 24000L;
        }

        private static boolean hasHadGift(ServerPlayer player) {
            var saved = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
            return saved.contains(LAST_GIFT) && saved.getLong(LAST_GIFT) == today(player);
        }

        @Override
        public boolean canUse() {
            if (this.bird.tickCount < this.nextCheck) return false;
            // A random wait, so the gift turns up at some point rather than straight away
            this.nextCheck = this.bird.tickCount + 600 + this.bird.getRandom().nextInt(1200);

            if (!this.bird.isTame() || this.bird.isOrderedToSit() || this.bird.isPassenger()) return false;
            if (!(this.bird.getOwner() instanceof ServerPlayer player) || player.isSpectator()) return false;
            if (this.bird.distanceToSqr(player) > 144.0D || hasHadGift(player)) return false;
            this.owner = player;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.timeLeft > 0 && this.owner.isAlive() && !this.bird.isOrderedToSit()
                    && this.bird.distanceToSqr(this.owner) < 256.0D && !hasHadGift(this.owner);
        }

        @Override
        public void start() {
            this.timeLeft = this.adjustedTickDelay(200);
        }

        @Override
        public void stop() {
            this.owner = null;
            this.bird.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.timeLeft--;
            this.bird.getNavigation().moveTo(this.owner, 1.1D);
            if (this.bird.distanceToSqr(this.owner) > 6.25D) return;

            var data = this.owner.getPersistentData();
            var saved = data.getCompound(Player.PERSISTED_NBT_TAG);
            saved.putLong(LAST_GIFT, today(this.owner));
            data.put(Player.PERSISTED_NBT_TAG, saved);

            this.bird.spawnAtLocation(new ItemStack(SEEDS[this.bird.getRandom().nextInt(SEEDS.length)]));
            this.bird.sing();
            this.owner.displayClientMessage(Component.translatable("message.novamterram.songbird.gift"), true);
        }
    }

    /** Wild birds that have drifted apart fly back towards each other. */
    static class FlockGoal extends Goal {
        private final SongbirdEntity bird;
        private Vec3 target;
        private int nextCheck;

        FlockGoal(SongbirdEntity bird) {
            this.bird = bird;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.bird.tickCount < this.nextCheck) return false;
            this.nextCheck = this.bird.tickCount + 40 + this.bird.getRandom().nextInt(60);
            if (this.bird.isTame() || this.bird.isPassenger()) return false;

            List<SongbirdEntity> flock = this.bird.nearbyWildBirds(16.0D);
            if (flock.isEmpty()) return false;

            Vec3 centre = Vec3.ZERO;
            for (LivingEntity other : flock) {
                centre = centre.add(other.position());
            }
            centre = centre.scale(1.0D / flock.size());
            if (this.bird.position().distanceToSqr(centre) < 36.0D) return false;

            // Not all to the exact same spot
            this.target = centre.add(this.bird.getRandom().nextInt(5) - 2, 0.5D,
                    this.bird.getRandom().nextInt(5) - 2);
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.bird.isTame() && !this.bird.getNavigation().isDone();
        }

        @Override
        public void start() {
            this.bird.getNavigation().moveTo(this.target.x, this.target.y, this.target.z, 1.0D);
        }

        @Override
        public void stop() {
            this.bird.getNavigation().stop();
        }
    }

    /** At night wild birds settle on top of leaves and sleep. */
    static class RoostGoal extends MoveToBlockGoal {
        private final SongbirdEntity bird;

        RoostGoal(SongbirdEntity bird) {
            super(bird, 1.0D, 12, 8);
            this.bird = bird;
            // LOOK too, so a sleeping bird doesn't turn to watch players
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        private boolean wantsToRoost() {
            return this.bird.level().isNight() && !this.bird.isTame() && !this.bird.isPassenger()
                    && !this.bird.isLeashed() && !this.bird.wasRecentlyStartled();
        }

        // The usual wait between block searches is 10-20 seconds, too slow at dusk
        @Override
        protected int nextStartTick(net.minecraft.world.entity.PathfinderMob mob) {
            return reducedTickDelay(60 + mob.getRandom().nextInt(60));
        }

        @Override
        public boolean canUse() {
            return wantsToRoost() && super.canUse();
        }

        // Stays all night, and gives up if it can't get there
        @Override
        public boolean canContinueToUse() {
            return wantsToRoost() && this.tryTicks < 600 && isValidTarget(this.bird.level(), this.blockPos);
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            return level.getBlockState(pos).is(BlockTags.LEAVES) && level.isEmptyBlock(pos.above());
        }

        @Override
        public void tick() {
            super.tick();
            boolean asleep = this.isReachedTarget() && this.bird.onGround();
            if (asleep) {
                this.bird.getNavigation().stop();
            }
            this.bird.setRoosting(asleep);
        }

        @Override
        public void stop() {
            super.stop();
            this.bird.setRoosting(false);
        }
    }

    /** A pet bird snaps up silverfish. */
    static class SilverfishGoal extends Goal {
        private final SongbirdEntity bird;
        private Silverfish prey;
        private int nextCheck;
        private int timeLeft;

        SilverfishGoal(SongbirdEntity bird) {
            this.bird = bird;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.bird.tickCount < this.nextCheck) return false;
            this.nextCheck = this.bird.tickCount + 20;
            if (!this.bird.isTame() || this.bird.isBaby() || this.bird.isOrderedToSit() || this.bird.isPassenger()) {
                return false;
            }
            List<Silverfish> found = this.bird.level().getEntitiesOfClass(Silverfish.class,
                    this.bird.getBoundingBox().inflate(10.0D), fish -> fish.isAlive() && this.bird.hasLineOfSight(fish));
            if (found.isEmpty()) return false;
            this.prey = found.get(0);
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.timeLeft > 0 && this.prey.isAlive() && !this.bird.isOrderedToSit();
        }

        @Override
        public void start() {
            this.timeLeft = this.adjustedTickDelay(160);
        }

        @Override
        public void stop() {
            this.prey = null;
            this.bird.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.timeLeft--;
            this.bird.getNavigation().moveTo(this.prey, 1.4D);
            if (this.bird.distanceToSqr(this.prey) < 2.25D
                    && this.prey.hurt(this.bird.damageSources().mobAttack(this.bird), 8.0F)) {
                this.bird.playSound(SoundEvents.PARROT_EAT, 1.0F, 1.4F);
                this.bird.heal(1.0F);
            }
        }
    }

    /** A wild bird that has spotted a fox or ocelot heads for the treetops. */
    static class TreeEscapeGoal extends MoveToBlockGoal {
        private final SongbirdEntity bird;

        TreeEscapeGoal(SongbirdEntity bird) {
            super(bird, 1.5D, 14, 10);
            this.bird = bird;
        }

        @Override
        protected int nextStartTick(net.minecraft.world.entity.PathfinderMob mob) {
            return reducedTickDelay(20);
        }

        @Override
        public boolean canUse() {
            return this.bird.isFleeing() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.bird.isFleeing() && this.tryTicks < 200
                    && this.bird.level().getBlockState(this.blockPos).is(BlockTags.LEAVES);
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            // Only leaves higher than the bird, so it goes up rather than into a bush
            return pos.getY() > this.bird.getY() + 1.0D
                    && level.getBlockState(pos).is(BlockTags.LEAVES) && level.isEmptyBlock(pos.above());
        }

        @Override
        public void tick() {
            super.tick();
            if (this.isReachedTarget()) {
                this.bird.getNavigation().stop();
            }
        }
    }
}
