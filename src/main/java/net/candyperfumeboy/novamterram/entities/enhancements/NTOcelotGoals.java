package net.candyperfumeboy.novamterram.entities.enhancements;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * The extra AI added on top of the vanilla ocelot. Vanilla goals (floating,
 * pouncing, attacking, breeding, wandering, looking, fleeing players while
 * untrusting) are left alone.
 *
 * Wild ocelots hunt chickens, baby turtles, fish and creepers. Tamed ocelots
 * never pick a target themselves; they only join their owner's fights.
 */
final class NTOcelotGoals {

    private NTOcelotGoals() {}

    /** Marks our own goals so they can be swapped out when an ocelot is tamed. */
    private interface NTGoal {}

    static void install(Ocelot ocelot) {
        ocelot.goalSelector.removeAllGoals(goal -> goal instanceof NTGoal);
        ocelot.targetSelector.removeAllGoals(goal -> goal instanceof NTGoal);

        if (NTOcelotEvents.isTamed(ocelot)) {
            // Drops vanilla's chicken and baby turtle hunting
            ocelot.targetSelector.removeAllGoals(goal -> goal instanceof NearestAttackableTargetGoal<?>);
            ocelot.goalSelector.addGoal(2, new SitGoal(ocelot));
            ocelot.goalSelector.addGoal(3, new PlayGoal(ocelot));
            ocelot.goalSelector.addGoal(5, new FollowOwnerGoal(ocelot));
            ocelot.goalSelector.addGoal(6, new PerchGoal(ocelot));
            ocelot.targetSelector.addGoal(0, new OwnerCombatGoal(ocelot, true));
            ocelot.targetSelector.addGoal(0, new OwnerCombatGoal(ocelot, false));
        } else {
            ocelot.goalSelector.addGoal(2, new WoundedFleeGoal(ocelot));
            ocelot.goalSelector.addGoal(3, new FishTemptGoal(ocelot));
            ocelot.targetSelector.addGoal(2, new HuntFishGoal(ocelot));
            ocelot.targetSelector.addGoal(3, new HuntCreeperGoal(ocelot));
        }
    }

    // ---------------------------------------------------------------- untamed

    private static final class FishTemptGoal extends TemptGoal implements NTGoal {
        FishTemptGoal(Ocelot ocelot) {
            super(ocelot, 0.6D, stack -> stack.is(ItemTags.FISHES), true);
        }
    }

    private static final class WoundedFleeGoal extends PanicGoal implements NTGoal {
        WoundedFleeGoal(Ocelot ocelot) {
            super(ocelot, 1.33D);
        }

        @Override
        public boolean canUse() {
            return this.mob.getHealth() < this.mob.getMaxHealth() * 0.3F && super.canUse();
        }
    }

    // ---------------------------------------------------------------- hunting

    private static final class HuntFishGoal extends NearestAttackableTargetGoal<AbstractFish> implements NTGoal {
        HuntFishGoal(Ocelot ocelot) {
            super(ocelot, AbstractFish.class, true);
        }
    }

    private static final class HuntCreeperGoal extends NearestAttackableTargetGoal<Creeper> implements NTGoal {
        HuntCreeperGoal(Ocelot ocelot) {
            super(ocelot, Creeper.class, true);
        }
    }

    // ---------------------------------------------------------------- tamed

    private static final class SitGoal extends Goal implements NTGoal {
        private final Ocelot ocelot;

        SitGoal(Ocelot ocelot) {
            this.ocelot = ocelot;
            setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return NTOcelotEvents.isSitting(ocelot);
        }

        @Override
        public void start() {
            ocelot.getNavigation().stop();
        }
    }

    /** Sprints around the owner until the play timer runs out, then sits. */
    private static final class PlayGoal extends Goal implements NTGoal {
        private final Ocelot ocelot;
        private Player owner;
        private int cooldown;

        PlayGoal(Ocelot ocelot) {
            this.ocelot = ocelot;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        private boolean timeLeft() {
            return ocelot.getPersistentData().getLong(NTOcelotEvents.NBT_PLAY_UNTIL) > ocelot.level().getGameTime();
        }

        @Override
        public boolean canUse() {
            if (NTOcelotEvents.isSitting(ocelot) || ocelot.getTarget() != null || !timeLeft()) return false;
            owner = NTOcelotEvents.ownerOf(ocelot);
            return owner != null && ocelot.distanceToSqr(owner) <= 256.0D;
        }

        @Override
        public void start() {
            cooldown = 0;
        }

        @Override
        public void tick() {
            if (--cooldown > 0) return;
            cooldown = adjustedTickDelay(15);

            double angle = ocelot.getRandom().nextDouble() * Math.PI * 2;
            double radius = 1 + ocelot.getRandom().nextDouble() * 5;
            // 1.33 is the speed vanilla ocelots treat as sprinting
            ocelot.getNavigation().moveTo(owner.getX() + Math.cos(angle) * radius, owner.getY(),
                    owner.getZ() + Math.sin(angle) * radius, 1.33D);
            if (ocelot.getRandom().nextFloat() < 0.15F) {
                ocelot.playSound(SoundEvents.CAT_AMBIENT, 0.5F, 1.3F + ocelot.getRandom().nextFloat() * 0.3F);
            }
        }

        @Override
        public void stop() {
            ocelot.getNavigation().stop();
            // Only tire out when the session actually finished, not when interrupted
            if (!timeLeft() && !NTOcelotEvents.isSitting(ocelot)) {
                NTOcelotEvents.setSitting(ocelot, true);
                ocelot.playSound(SoundEvents.CAT_PURR, 0.6F, 0.9F);
            }
        }
    }

    private static final class FollowOwnerGoal extends Goal implements NTGoal {
        private final Ocelot ocelot;
        private Player owner;
        private int cooldown;

        FollowOwnerGoal(Ocelot ocelot) {
            this.ocelot = ocelot;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        private boolean canFollow() {
            return !NTOcelotEvents.isSitting(ocelot) && !ocelot.isLeashed() && !ocelot.isPassenger();
        }

        @Override
        public boolean canUse() {
            if (!canFollow()) return false;
            owner = NTOcelotEvents.ownerOf(ocelot);
            if (owner == null || owner.isSpectator()) return false;
            double distance = ocelot.distanceToSqr(owner);
            // Let it finish a fight unless the owner has gone a long way off
            if (ocelot.getTarget() != null && distance < 576.0D) return false;
            return distance > 100.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return canFollow() && owner.isAlive() && owner.level() == ocelot.level()
                    && ocelot.distanceToSqr(owner) > 9.0D;
        }

        @Override
        public void start() {
            cooldown = 0;
            ocelot.setTarget(null);
        }

        @Override
        public void stop() {
            ocelot.getNavigation().stop();
        }

        @Override
        public void tick() {
            ocelot.getLookControl().setLookAt(owner, 10.0F, ocelot.getMaxHeadXRot());
            if (--cooldown > 0) return;
            cooldown = adjustedTickDelay(10);

            double distance = ocelot.distanceToSqr(owner);
            if (distance >= 256.0D) {
                teleportNearOwner();
            } else {
                ocelot.getNavigation().moveTo(owner, distance > 144.0D ? 1.33D : 1.0D);
            }
        }

        /** Same safety rules as vanilla pets: solid footing and room to stand. */
        private void teleportNearOwner() {
            BlockPos base = owner.blockPosition();
            for (int attempt = 0; attempt < 10; attempt++) {
                int dx = ocelot.getRandom().nextInt(7) - 3;
                int dz = ocelot.getRandom().nextInt(7) - 3;
                if (Math.abs(dx) < 2 && Math.abs(dz) < 2) continue;

                BlockPos pos = base.offset(dx, ocelot.getRandom().nextInt(3) - 1, dz);
                if (WalkNodeEvaluator.getPathTypeStatic(ocelot, pos) != PathType.WALKABLE) continue;

                Vec3 destination = Vec3.atBottomCenterOf(pos);
                AABB box = ocelot.getBoundingBox().move(destination.subtract(ocelot.position()));
                if (!ocelot.level().noCollision(ocelot, box)) continue;

                ocelot.moveTo(destination.x, destination.y, destination.z, ocelot.getYRot(), ocelot.getXRot());
                ocelot.getNavigation().stop();
                return;
            }
        }
    }

    /** Like vanilla cats: sits on beds, closed chests and lit furnaces. */
    private static final class PerchGoal extends MoveToBlockGoal implements NTGoal {
        private final Ocelot ocelot;

        PerchGoal(Ocelot ocelot) {
            super(ocelot, 0.8D, 8);
            this.ocelot = ocelot;
        }

        private boolean isFree() {
            return !NTOcelotEvents.isSitting(ocelot) && !ocelot.isLeashed() && !ocelot.isPassenger()
                    && !ocelot.isInLove() && ocelot.getTarget() == null;
        }

        @Override
        public boolean canUse() {
            return isFree() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return isFree() && super.canContinueToUse();
        }

        @Override
        public void tick() {
            super.tick();
            NTOcelotEvents.showSittingPose(ocelot, isReachedTarget());
        }

        @Override
        public void stop() {
            super.stop();
            NTOcelotEvents.showSittingPose(ocelot, NTOcelotEvents.isSitting(ocelot));
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            if (!level.isEmptyBlock(pos.above())) return false;
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.CHEST)) return ChestBlockEntity.getOpenCount(level, pos) < 1;
            if (state.is(Blocks.FURNACE)) return state.getValue(FurnaceBlock.LIT);
            return state.is(BlockTags.BEDS)
                    && state.getOptionalValue(BedBlock.PART).map(part -> part != BedPart.HEAD).orElse(true);
        }
    }

    /** Joins the owner's fights: either defending them or attacking what they attack. */
    private static final class OwnerCombatGoal extends TargetGoal implements NTGoal {
        private final Ocelot ocelot;
        private final boolean defending;
        private LivingEntity candidate;
        private int candidateTime;
        private int handledTime;

        OwnerCombatGoal(Ocelot ocelot, boolean defending) {
            super(ocelot, false);
            this.ocelot = ocelot;
            this.defending = defending;
            setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (NTOcelotEvents.isSitting(ocelot)) return false;
            Player owner = NTOcelotEvents.ownerOf(ocelot);
            if (owner == null) return false;

            candidate = defending ? owner.getLastHurtByMob() : owner.getLastHurtMob();
            candidateTime = defending ? owner.getLastHurtByMobTimestamp() : owner.getLastHurtMobTimestamp();

            if (candidate == null || candidate == owner || candidateTime == handledTime) return false;
            if (isFriend(candidate, owner)) return false;
            return canAttack(candidate, TargetingConditions.DEFAULT);
        }

        @Override
        public void start() {
            mob.setTarget(candidate);
            handledTime = candidateTime;
            super.start();
        }

        /** Never turn on the owner's other pets. */
        private static boolean isFriend(LivingEntity entity, Player owner) {
            if (entity instanceof Ocelot other && NTOcelotEvents.isTamed(other)) return true;
            return entity instanceof OwnableEntity pet && owner.getUUID().equals(pet.getOwnerUUID());
        }
    }
}
