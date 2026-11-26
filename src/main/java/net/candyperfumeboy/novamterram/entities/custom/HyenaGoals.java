package net.candyperfumeboy.novamterram.entities.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.function.Predicate;

/** Custom AI for {@link HyenaEntity}. Timers use tickCount, since goals don't run every tick. */
final class HyenaGoals {
    private HyenaGoals() {}

    @Nullable
    private static ItemEntity nearestItem(HyenaEntity hyena, Predicate<ItemEntity> filter) {
        return hyena.level()
                .getEntitiesOfClass(ItemEntity.class, hyena.getBoundingBox().inflate(8.0D, 4.0D, 8.0D), filter)
                .stream()
                .min(Comparator.comparingDouble(hyena::distanceToSqr))
                .orElse(null);
    }

    /** Takes one item off a stack lying on the ground. */
    private static ItemStack takeOne(ItemEntity item) {
        ItemStack rest = item.getItem().copy();
        ItemStack one = rest.split(1);
        if (rest.isEmpty()) item.discard();
        else item.setItem(rest);
        return one;
    }

    /** Wild adults creep up on a target, then pounce. */
    static class Stalk extends Goal {
        private final HyenaEntity hyena;
        private LivingEntity target;
        private LivingEntity lastStalked;
        private boolean pounced;
        private int giveUpTime;

        Stalk(HyenaEntity hyena) {
            this.hyena = hyena;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            LivingEntity candidate = this.hyena.getTarget();
            if (this.hyena.isTame() || this.hyena.isBaby() || candidate == null || !candidate.isAlive()) return false;
            // No sneaking up on someone already fighting you
            if (candidate == this.lastStalked || candidate == this.hyena.getLastHurtByMob()) return false;
            double distance = this.hyena.distanceToSqr(candidate);
            if (distance < 20.0D || distance > 144.0D) return false;
            if (!this.hyena.onGround() || !this.hyena.hasLineOfSight(candidate)) return false;
            this.target = candidate;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.pounced && this.target.isAlive() && this.target == this.hyena.getTarget()
                    && this.target != this.hyena.getLastHurtByMob()
                    && this.hyena.tickCount < this.giveUpTime && this.hyena.distanceToSqr(this.target) < 256.0D;
        }

        @Override
        public void start() {
            this.lastStalked = this.target;
            this.pounced = false;
            this.giveUpTime = this.hyena.tickCount + 100;
            this.hyena.setStalking(true);
            this.hyena.getNavigation().moveTo(this.target, 0.65D);
        }

        @Override
        public void stop() {
            this.hyena.setStalking(false);
            this.hyena.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.hyena.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
            if (this.hyena.distanceToSqr(this.target) <= 16.0D && this.hyena.onGround()) {
                Vec3 leap = new Vec3(this.target.getX() - this.hyena.getX(), 0.0D, this.target.getZ() - this.hyena.getZ())
                        .normalize().scale(0.6D);
                this.hyena.setDeltaMovement(leap.x, 0.42D, leap.z);
                this.pounced = true;
            } else if (this.hyena.getNavigation().isDone()) {
                this.hyena.getNavigation().moveTo(this.target, 0.65D);
            }
        }
    }

    /** A wild hyena sometimes snatches meat a player dropped and runs off with it. */
    static class StealFood extends Goal {
        private final HyenaEntity hyena;
        private ItemEntity food;
        private Player victim;
        private boolean grabbed;
        private int nextTryTime;
        private int endTime;

        StealFood(HyenaEntity hyena) {
            this.hyena = hyena;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        // Only items a player threw, not death piles or mob drops
        private boolean isTempting(ItemEntity item) {
            return item.isAlive() && !item.hasPickUpDelay() && this.hyena.isFood(item.getItem())
                    && item.getOwner() instanceof Player owner && this.hyena.distanceToSqr(owner) < 144.0D;
        }

        @Override
        public boolean canUse() {
            if (this.hyena.tickCount < this.nextTryTime) return false;
            this.nextTryTime = this.hyena.tickCount + 40;
            if (this.hyena.isTame() || this.hyena.isBaby() || this.hyena.getTarget() != null
                    || !this.hyena.getMainHandItem().isEmpty()) return false;
            if (this.hyena.getRandom().nextInt(3) != 0) return false;
            if (!EventHooks.canEntityGrief(this.hyena.level(), this.hyena)) return false;

            this.food = nearestItem(this.hyena, this::isTempting);
            return this.food != null;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.hyena.isTame() || this.hyena.getTarget() != null || this.hyena.tickCount >= this.endTime) return false;
            return this.grabbed ? !this.hyena.getMainHandItem().isEmpty() : this.isTempting(this.food);
        }

        @Override
        public void start() {
            this.grabbed = false;
            this.victim = (Player) this.food.getOwner();
            this.endTime = this.hyena.tickCount + 160;
            this.hyena.getNavigation().moveTo(this.food, 1.3D);
        }

        @Override
        public void stop() {
            if (this.grabbed) this.nextTryTime = this.hyena.tickCount + 1200;
            this.hyena.getNavigation().stop();
            this.food = null;
            this.victim = null;
        }

        @Override
        public void tick() {
            if (!this.grabbed) {
                this.hyena.getLookControl().setLookAt(this.food);
                if (this.hyena.distanceToSqr(this.food) < 2.0D) {
                    this.hyena.holdStolenFood(takeOne(this.food));
                    this.hyena.playSound(SoundEvents.FOX_BITE, 1.0F, 0.9F);
                    this.grabbed = true;
                    this.endTime = this.hyena.tickCount + 100; // run for 5 seconds
                } else if (this.hyena.getNavigation().isDone()) {
                    this.hyena.getNavigation().moveTo(this.food, 1.3D);
                }
            } else if (this.hyena.getNavigation().isDone()) {
                Vec3 away = DefaultRandomPos.getPosAway(this.hyena, 16, 7, this.victim.position());
                if (away != null) this.hyena.getNavigation().moveTo(away.x, away.y, away.z, 1.4D);
            }
        }
    }

    /** A hurt hyena walks to meat or bones on the ground and eats one at a time. */
    static class Scavenge extends Goal {
        private final HyenaEntity hyena;
        private ItemEntity food;
        private int nextSearchTime;
        private int giveUpTime;

        Scavenge(HyenaEntity hyena) {
            this.hyena = hyena;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        private boolean wantsFood() {
            return !this.hyena.isOrderedToSit() && this.hyena.getTarget() == null
                    && this.hyena.getHealth() < this.hyena.getMaxHealth();
        }

        private boolean isEdible(ItemEntity item) {
            return item.isAlive() && !item.hasPickUpDelay()
                    && (item.getItem().is(Items.BONE) || this.hyena.isFood(item.getItem()));
        }

        @Override
        public boolean canUse() {
            if (this.hyena.tickCount < this.nextSearchTime || !this.wantsFood()) return false;
            this.nextSearchTime = this.hyena.tickCount + 40;
            if (!EventHooks.canEntityGrief(this.hyena.level(), this.hyena)) return false;
            this.food = nearestItem(this.hyena, this::isEdible);
            return this.food != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.food != null && this.isEdible(this.food) && this.wantsFood()
                    && this.hyena.tickCount < this.giveUpTime;
        }

        @Override
        public void start() {
            this.giveUpTime = this.hyena.tickCount + 200;
            this.hyena.getNavigation().moveTo(this.food, 1.2D);
        }

        @Override
        public void stop() {
            this.food = null;
            this.hyena.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.food == null) return;
            this.hyena.getLookControl().setLookAt(this.food);
            if (this.hyena.distanceToSqr(this.food) < 2.0D) {
                takeOne(this.food);
                this.hyena.heal(3.0F);
                this.hyena.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                this.nextSearchTime = this.hyena.tickCount + 60; // chew before the next one
                this.food = null;
            } else if (this.hyena.getNavigation().isDone()) {
                this.hyena.getNavigation().moveTo(this.food, 1.2D);
            }
        }
    }

    /** By day, wild hyenas wander into shade (or just flop down) and doze. At night they roam. */
    static class RestInShade extends Goal {
        private final HyenaEntity hyena;
        private BlockPos spot;
        private int nextTryTime;
        private int endTime;

        RestInShade(HyenaEntity hyena) {
            this.hyena = hyena;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        private boolean canRelax() {
            if (this.hyena.isTame() || !this.hyena.level().isDay() || this.hyena.getTarget() != null
                    || this.hyena.getLastHurtByMob() != null || this.hyena.isInWaterOrBubble()
                    || this.hyena.isInLove()) return false;
            // A player walking close by wakes it; sneaking doesn't
            Player near = this.hyena.level().getNearestPlayer(this.hyena, 6.0D);
            return near == null || near.isSpectator() || near.isShiftKeyDown();
        }

        private boolean isShaded(BlockPos pos) {
            return !this.hyena.level().canSeeSky(pos) && this.hyena.level().isEmptyBlock(pos)
                    && this.hyena.level().getBlockState(pos.below()).isFaceSturdy(this.hyena.level(), pos.below(), Direction.UP);
        }

        @Override
        public boolean canUse() {
            if (this.hyena.tickCount < this.nextTryTime) return false;
            this.nextTryTime = this.hyena.tickCount + 100;
            if (!this.hyena.onGround() || !this.canRelax() || this.hyena.getRandom().nextInt(3) != 0) return false;

            this.spot = null;
            if (this.isShaded(this.hyena.blockPosition())) return true;
            for (int i = 0; i < 12; i++) {
                BlockPos pos = this.hyena.blockPosition().offset(this.hyena.getRandom().nextInt(21) - 10,
                        this.hyena.getRandom().nextInt(5) - 2, this.hyena.getRandom().nextInt(21) - 10);
                if (this.isShaded(pos)) {
                    this.spot = pos;
                    return true;
                }
            }
            return this.hyena.getRandom().nextInt(3) == 0; // savannas have little shade
        }

        @Override
        public boolean canContinueToUse() {
            return this.canRelax() && this.hyena.tickCount < this.endTime;
        }

        @Override
        public void start() {
            this.endTime = this.hyena.tickCount + 200; // time allowed to get there
            if (this.spot != null) {
                this.hyena.getNavigation().moveTo(this.spot.getX() + 0.5D, this.spot.getY(), this.spot.getZ() + 0.5D, 1.0D);
            }
        }

        @Override
        public void stop() {
            this.hyena.setResting(false);
            this.nextTryTime = this.hyena.tickCount + 600 + this.hyena.getRandom().nextInt(600);
        }

        @Override
        public void tick() {
            if (!this.hyena.isResting() && (this.spot == null || this.hyena.getNavigation().isDone())) {
                this.hyena.getNavigation().stop();
                this.hyena.setResting(true);
                this.endTime = this.hyena.tickCount + 300 + this.hyena.getRandom().nextInt(500);
            }
        }
    }
}
