package net.candyperfumeboy.novamterram.entities.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import java.util.EnumSet;
import java.util.List;

/** Everything a faun decides to do. */
public final class FaunGoals {

    private FaunGoals() {
    }

    /** Runs away from whatever startled it or its herd. */
    static class SpookedGoal extends Goal {
        private final FaunEntity faun;

        SpookedGoal(FaunEntity faun) {
            this.faun = faun;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        }

        private boolean runAway() {
            Vec3 to = DefaultRandomPos.getPosAway(this.faun, 16, 7, this.faun.spookedFrom());
            return to != null && this.faun.getNavigation().moveTo(to.x, to.y, to.z, 1.8D);
        }

        @Override
        public boolean canUse() {
            return this.faun.isSpooked() && runAway();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.faun.getNavigation().isDone() || (this.faun.isSpooked() && runAway());
        }

        @Override
        public void stop() {
            this.faun.getNavigation().stop();
        }
    }

    /** Stands stock still while a stranger watches, then bolts if they come close or keep staring. */
    static class FreezeGoal extends Goal {
        private final FaunEntity faun;
        private Player watcher;
        private int watchedFor;
        private int giveUpAt;
        private int nextFreeze;

        FreezeGoal(FaunEntity faun) {
            this.faun = faun;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        private boolean isStaring(Player player) {
            if (player == null || !player.isAlive() || player.isSpectator() || player.isCreative()) return false;
            if (this.faun.trusts(player.getUUID())) return false;
            if (this.faun.isFood(player.getMainHandItem()) || this.faun.isFood(player.getOffhandItem())) return false;
            Vec3 toFaun = this.faun.getEyePosition().subtract(player.getEyePosition()).normalize();
            return player.getViewVector(1.0F).dot(toFaun) > 0.95D && player.hasLineOfSight(this.faun);
        }

        @Override
        public boolean canUse() {
            if (this.faun.tickCount < this.nextFreeze || this.faun.isResting() || !this.faun.onGround()) return false;
            Player near = this.faun.level().getNearestPlayer(this.faun, 16.0D);
            if (!isStaring(near) || this.faun.distanceToSqr(near) < 16.0D) {
                this.watchedFor = 0;
                return false;
            }
            this.watcher = near;
            return ++this.watchedFor >= 10; // about a second
        }

        @Override
        public boolean canContinueToUse() {
            return this.faun.tickCount < this.giveUpAt && !this.faun.isSpooked()
                    && this.faun.distanceToSqr(this.watcher) >= 16.0D && isStaring(this.watcher);
        }

        @Override
        public void start() {
            this.giveUpAt = this.faun.tickCount + 120;
            this.faun.getNavigation().stop();
            this.faun.setHiding(true);
        }

        @Override
        public void stop() {
            this.faun.setHiding(false);
            this.watchedFor = 0;
            this.nextFreeze = this.faun.tickCount + 100;
            // Still being stared at: nerves give out
            if (isStaring(this.watcher)) this.faun.spook(this.watcher.position(), 60);
            this.watcher = null;
        }
    }

    /** Brings a trusted player a present. One a day per player, however many fauns they have. */
    static class GiftGoal extends Goal {
        private static final String LAST_GIFT = "NTFaunGiftDay";

        private final FaunEntity faun;
        private ServerPlayer friend;
        private int nextCheck;
        private int timeLeft;

        GiftGoal(FaunEntity faun) {
            this.faun = faun;
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
            if (this.faun.tickCount < this.nextCheck) return false;
            this.nextCheck = this.faun.tickCount + 600 + this.faun.getRandom().nextInt(1200);
            if (this.faun.isBaby() || this.faun.isResting()) return false;
            if (!(this.faun.level().getNearestPlayer(this.faun, 12.0D) instanceof ServerPlayer player)) return false;
            if (player.isSpectator() || !this.faun.trusts(player.getUUID()) || hasHadGift(player)) return false;
            this.friend = player;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.timeLeft > 0 && this.friend.isAlive() && this.faun.distanceToSqr(this.friend) < 256.0D
                    && !hasHadGift(this.friend);
        }

        @Override
        public void start() {
            this.timeLeft = this.adjustedTickDelay(200);
        }

        @Override
        public void stop() {
            this.friend = null;
            this.faun.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.timeLeft--;
            this.faun.getLookControl().setLookAt(this.friend);
            this.faun.getNavigation().moveTo(this.friend, 1.0D);
            if (this.faun.distanceToSqr(this.friend) > 6.25D) return;

            var data = this.friend.getPersistentData();
            var saved = data.getCompound(Player.PERSISTED_NBT_TAG);
            saved.putLong(LAST_GIFT, today(this.friend));
            data.put(Player.PERSISTED_NBT_TAG, saved);

            this.faun.spawnAtLocation(switch (this.faun.getRandom().nextInt(5)) {
                case 0 -> new ItemStack(Items.SWEET_BERRIES, 4);
                case 1 -> new ItemStack(Items.MOSS_BLOCK);
                case 2 -> new ItemStack(Items.FLOWERING_AZALEA);
                case 3 -> new ItemStack(Items.BROWN_MUSHROOM, 2);
                default -> new ItemStack(Items.APPLE, 2);
            });
            this.faun.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 0.8F);
            this.friend.displayClientMessage(Component.translatable("message.novamterram.faun.gift"), true);
        }
    }

    /** Two bucks lock antlers for a few seconds. Nobody gets hurt. */
    static class SparGoal extends Goal {
        private final FaunEntity faun;
        private FaunEntity rival; // set by whoever starts the match
        private int timeLeft;
        private int nextSpar;

        SparGoal(FaunEntity faun) {
            this.faun = faun;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        private static boolean canSpar(FaunEntity faun) {
            return faun.isAlive() && !faun.isBaby() && faun.isBuck() && faun.hasAntlers()
                    && !faun.isResting() && !faun.isSpooked() && !faun.isHiding();
        }

        private static SparGoal sparGoalOf(FaunEntity faun) {
            return faun.goalSelector.getAvailableGoals().stream()
                    .map(wrapped -> wrapped.getGoal())
                    .filter(goal -> goal instanceof SparGoal)
                    .map(goal -> (SparGoal) goal)
                    .findFirst().orElse(null);
        }

        @Override
        public boolean canUse() {
            if (this.rival != null) return canSpar(this.faun) && canSpar(this.rival); // we were challenged
            if (this.faun.tickCount < this.nextSpar || !canSpar(this.faun)) return false;
            if (this.faun.getRandom().nextInt(600) != 0) return false;

            for (FaunEntity other : this.faun.nearbyFauns(8.0D)) {
                SparGoal theirs = sparGoalOf(other);
                if (theirs != null && theirs.rival == null && canSpar(other) && other.tickCount >= theirs.nextSpar) {
                    this.rival = other;
                    theirs.rival = this.faun;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.timeLeft > 0 && canSpar(this.faun) && canSpar(this.rival);
        }

        @Override
        public void start() {
            this.timeLeft = this.adjustedTickDelay(140);
        }

        @Override
        public void stop() {
            this.faun.setSparring(false);
            this.faun.getNavigation().stop();
            this.rival = null;
            this.nextSpar = this.faun.tickCount + 4800;
        }

        @Override
        public void tick() {
            this.timeLeft--;
            this.faun.getLookControl().setLookAt(this.rival, 30.0F, 30.0F);
            if (this.faun.distanceToSqr(this.rival) > 4.0D) {
                this.faun.setSparring(false);
                this.faun.getNavigation().moveTo(this.rival, 1.0D);
                return;
            }
            this.faun.getNavigation().stop();
            this.faun.lookAt(this.rival, 30.0F, 30.0F);
            this.faun.setSparring(true);
            // Only one of the pair makes the noise
            if (this.timeLeft % 8 == 0 && this.faun.getId() < this.rival.getId()) {
                this.faun.playSound(SoundEvents.GOAT_RAM_IMPACT, 0.6F, 1.4F);
            }
        }
    }

    /** Visits flowers, moss and salt licks (a block of raw iron). */
    static class TreatGoal extends MoveToBlockGoal {
        private final FaunEntity faun;
        private boolean done;

        TreatGoal(FaunEntity faun) {
            super(faun, 1.0D, 10, 3);
            this.faun = faun;
        }

        @Override
        protected int nextStartTick(PathfinderMob mob) {
            return reducedTickDelay(1200 + mob.getRandom().nextInt(2400)); // every 1-3 minutes
        }

        @Override
        public boolean canUse() {
            return !this.faun.level().isNight() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.done && super.canContinueToUse();
        }

        @Override
        public void start() {
            this.done = false;
            super.start();
        }

        @Override
        public double acceptedDistance() {
            return 2.0D;
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            BlockState state = level.getBlockState(pos);
            return FaunFlowers.isTasty(state) || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.RAW_IRON_BLOCK);
        }

        @Override
        public void tick() {
            super.tick();
            if (this.done || !this.isReachedTarget()) return;
            this.done = true;

            BlockState state = this.faun.level().getBlockState(this.blockPos);
            if (FaunFlowers.isTasty(state)) {
                // Leaves gardens alone when mobGriefing is off
                if (this.faun.isFloral() || !EventHooks.canEntityGrief(this.faun.level(), this.faun)) return;
                this.faun.level().destroyBlock(this.blockPos, false);
                this.faun.eatFlower(state);
                this.faun.playSound(SoundEvents.PANDA_EAT, 1.0F, 1.0F);
            } else {
                this.faun.heal(4.0F);
                this.faun.playSound(SoundEvents.HORSE_EAT, 0.8F, 1.2F);
            }
            this.faun.level().broadcastEntityEvent(this.faun, (byte) 10); // head down
        }
    }

    /** Gets under trees when it rains. */
    static class ShelterGoal extends MoveToBlockGoal {
        private final FaunEntity faun;

        ShelterGoal(FaunEntity faun) {
            super(faun, 1.1D, 12, 4);
            this.faun = faun;
        }

        @Override
        protected int nextStartTick(PathfinderMob mob) {
            return reducedTickDelay(100);
        }

        @Override
        public boolean canUse() {
            return this.faun.level().isRainingAt(this.faun.blockPosition().above()) && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.faun.level().isRaining() && !this.isReachedTarget() && super.canContinueToUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            // Solid ground with room to stand and a roof overhead
            return level.getBlockState(pos).isSolid() && level.isEmptyBlock(pos.above())
                    && level.isEmptyBlock(pos.above(2)) && !level.canSeeSky(pos.above());
        }
    }

    /** Lies down at night. One grown faun in each herd stays up to keep watch. */
    static class RestGoal extends Goal {
        private final FaunEntity faun;
        private int nextRest;

        RestGoal(FaunEntity faun) {
            this.faun = faun;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        private boolean calm() {
            return this.faun.level().isNight() && this.faun.onGround() && !this.faun.isSpooked()
                    && !this.faun.isInWater() && !this.faun.isLeashed() && !this.faun.isInLove()
                    && this.faun.guideTarget() == null;
        }

        @Override
        public boolean canUse() {
            if (this.faun.tickCount < this.nextRest || !calm()) return false;
            if (this.faun.getRandom().nextInt(100) != 0) return false;

            List<FaunEntity> herd = this.faun.nearbyFauns(12.0D);
            if (this.faun.isBaby() || herd.isEmpty()) return true;
            return herd.stream().anyMatch(other -> !other.isBaby() && !other.isResting());
        }

        @Override
        public boolean canContinueToUse() {
            return calm();
        }

        @Override
        public void start() {
            this.faun.getNavigation().stop();
            this.faun.setResting(true);
        }

        @Override
        public void stop() {
            this.faun.setResting(false);
            this.nextRest = this.faun.tickCount + 600;
        }
    }

    /** The herd follows a leading buck. Without one, wanderers just drift back together. */
    static class HerdGoal extends Goal {
        private final FaunEntity faun;
        private FaunEntity other;
        private int nextCheck;

        HerdGoal(FaunEntity faun) {
            this.faun = faun;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        // Every faun picks the same one: a white stag first, then a fixed order by id
        private static boolean outranks(FaunEntity a, FaunEntity b) {
            boolean aWhite = a.getCoat() == FaunCoat.WHITE;
            boolean bWhite = b.getCoat() == FaunCoat.WHITE;
            if (aWhite != bWhite) return aWhite;
            return a.getUUID().compareTo(b.getUUID()) < 0;
        }

        @Override
        public boolean canUse() {
            if (this.faun.tickCount < this.nextCheck) return false;
            this.nextCheck = this.faun.tickCount + 60 + this.faun.getRandom().nextInt(60);
            if (this.faun.isBaby()) return false; // fawns follow their parents

            List<FaunEntity> herd = this.faun.nearbyFauns(24.0D);
            if (herd.isEmpty()) return false;

            FaunEntity leader = this.faun.isBuck() ? this.faun : null;
            for (FaunEntity member : herd) {
                if (member.isBuck() && !member.isBaby() && (leader == null || outranks(member, leader))) {
                    leader = member;
                }
            }

            if (leader != null) {
                if (leader == this.faun || this.faun.distanceToSqr(leader) < 64.0D) return false;
                this.other = leader;
                return true;
            }
            if (herd.stream().anyMatch(f -> this.faun.distanceToSqr(f) < 64.0D)) return false;
            this.other = herd.get(this.faun.getRandom().nextInt(herd.size()));
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.other.isAlive() && !this.faun.getNavigation().isDone()
                    && this.faun.distanceToSqr(this.other) > 25.0D;
        }

        @Override
        public void start() {
            this.faun.getNavigation().moveTo(this.other, 1.0D);
        }

        @Override
        public void stop() {
            this.faun.getNavigation().stop();
        }
    }

    /** A doe puts herself between a nearby fawn and danger, and kicks what gets too close. */
    static class ProtectFawnGoal extends Goal {
        private final FaunEntity faun;
        private FaunEntity fawn;
        private Mob threat;
        private int nextCheck;
        private int nextKick;
        private int repath;

        ProtectFawnGoal(FaunEntity faun) {
            this.faun = faun;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        private boolean canFight() {
            return !this.faun.isBaby() && !this.faun.isBuck() && !this.faun.isOnFire()
                    && this.faun.getHealth() > this.faun.getMaxHealth() * 0.5F;
        }

        // Creepers would explode and anything huge is hopeless, so those she runs from
        private static boolean worthFighting(Mob mob) {
            return mob.isAlive() && !(mob instanceof Creeper) && mob.getMaxHealth() <= 60.0F
                    && (FaunEntity.isThreat(mob) || mob.getTarget() instanceof FaunEntity);
        }

        @Override
        public boolean canUse() {
            if (this.faun.tickCount < this.nextCheck) return false;
            this.nextCheck = this.faun.tickCount + 10;
            if (!canFight()) return false;

            for (FaunEntity young : this.faun.nearbyFauns(10.0D)) {
                if (!young.isBaby()) continue;
                List<Mob> threats = this.faun.level().getEntitiesOfClass(Mob.class, young.getBoundingBox().inflate(8.0D),
                        mob -> worthFighting(mob) && this.faun.hasLineOfSight(mob));
                if (!threats.isEmpty()) {
                    this.fawn = young;
                    this.threat = threats.get(0);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return canFight() && this.fawn.isAlive() && worthFighting(this.threat)
                    && this.fawn.distanceToSqr(this.threat) < 144.0D;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.repath = 0;
        }

        @Override
        public void stop() {
            this.faun.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.faun.getLookControl().setLookAt(this.threat, 30.0F, 30.0F);
            if (this.faun.distanceToSqr(this.threat) < 6.25D) {
                if (this.faun.tickCount >= this.nextKick) {
                    this.nextKick = this.faun.tickCount + 30;
                    this.faun.lookAt(this.threat, 90.0F, 30.0F);
                    this.faun.kick(this.threat);
                }
                return;
            }
            if (--this.repath > 0) return;
            this.repath = 10;
            // Two blocks from the fawn, on the side the danger is coming from
            Vec3 toThreat = this.threat.position().subtract(this.fawn.position()).normalize();
            Vec3 spot = this.fawn.position().add(toThreat.scale(2.0D));
            this.faun.getNavigation().moveTo(spot.x, spot.y, spot.z, 1.5D);
        }
    }

    /** Leads a friend to something worth seeing, waiting whenever they fall behind. */
    static class GuideGoal extends Goal {
        private final FaunEntity faun;
        private int timeLeft;

        GuideGoal(FaunEntity faun) {
            this.faun = faun;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.faun.guideTarget() != null;
        }

        @Override
        public boolean canContinueToUse() {
            Player friend = this.faun.guideFollower();
            return this.timeLeft > 0 && this.faun.guideTarget() != null && friend != null && friend.isAlive()
                    && this.faun.distanceToSqr(friend) < 1024.0D;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.timeLeft = 1800; // 90 seconds
        }

        // Also runs if something scares it off halfway
        @Override
        public void stop() {
            Player friend = this.faun.guideFollower();
            if (this.faun.guideTarget() != null && friend != null) {
                friend.displayClientMessage(Component.translatable("message.novamterram.faun.guide_lost"), true);
            }
            this.faun.stopGuiding();
            this.faun.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.timeLeft--;
            BlockPos target = this.faun.guideTarget();
            Player friend = this.faun.guideFollower();
            if (target == null || friend == null) return;

            double dx = target.getX() + 0.5D - this.faun.getX();
            double dz = target.getZ() + 0.5D - this.faun.getZ();
            if (dx * dx + dz * dz < 9.0D) { // height ignored: hives hang out of reach
                friend.displayClientMessage(Component.translatable("message.novamterram.faun.guide_done"), true);
                if (this.faun.level() instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.HAPPY_VILLAGER, target.getX() + 0.5D, target.getY() + 0.5D,
                            target.getZ() + 0.5D, 25, 0.5D, 0.5D, 0.5D, 0.0D);
                }
                this.faun.stopGuiding();
                return;
            }
            if (this.faun.distanceToSqr(friend) > 49.0D) {
                this.faun.getNavigation().stop();
                this.faun.getLookControl().setLookAt(friend, 30.0F, 30.0F);
            } else if (this.faun.getNavigation().isDone() || this.timeLeft % 20 == 0) {
                this.faun.getNavigation().moveTo(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D, 1.1D);
            }
        }
    }
}
