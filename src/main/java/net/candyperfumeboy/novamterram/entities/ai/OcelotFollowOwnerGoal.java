package net.candyperfumeboy.novamterram.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.UUID;

public class OcelotFollowOwnerGoal extends Goal {
    private final Ocelot ocelot;
    private LivingEntity owner;
    private final double speedModifier;
    private int timeToRecalcPath;

    public OcelotFollowOwnerGoal(Ocelot ocelot, double speed) {
        this.ocelot = ocelot;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Stop the goal if they are sitting
        if (ocelot.getPersistentData().getBoolean("IsSitting")) {
            this.ocelot.setPose(Pose.CROUCHING); // Visually sit
            return false;
        }

        if (!ocelot.getPersistentData().getBoolean("IsTamed")) return false;

        UUID uuid = ocelot.getPersistentData().getUUID("OwnerUUID");
        if (uuid == null) return false;

        this.owner = ocelot.level().getPlayerByUUID(uuid);
        if (this.owner == null) return false;

        return ocelot.distanceToSqr(this.owner) > 36.0D;
    }

    @Override
    public void tick() {
        this.ocelot.getLookControl().setLookAt(this.owner, 10.0F, (float)this.ocelot.getMaxHeadXRot());

        // TELEPORT LOGIC (If further than 12 blocks)
        if (this.ocelot.distanceToSqr(this.owner) >= 144.0D) {
            this.teleportToOwner();
        } else if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            this.ocelot.getNavigation().moveTo(this.owner, this.speedModifier);
        }
    }

    private void teleportToOwner() {
        BlockPos pos = this.owner.blockPosition();
        for (int i = 0; i < 10; ++i) {
            int x = this.ocelot.getRandom().nextInt(3) - 1;
            int z = this.ocelot.getRandom().nextInt(3) - 1;
            if (maybeTeleport(pos.getX() + x, pos.getY(), pos.getZ() + z)) return;
        }
    }

    private boolean maybeTeleport(int x, int y, int z) {
        BlockPos targetPos = new BlockPos(x, y, z);
        BlockState state = this.ocelot.level().getBlockState(targetPos.below());

        // Ensure they land on a solid block
        if (state.isFaceSturdy(this.ocelot.level(), targetPos.below(), net.minecraft.core.Direction.UP)) {
            this.ocelot.moveTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.ocelot.getYRot(), this.ocelot.getXRot());
            this.ocelot.getNavigation().stop();
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (ocelot.getPersistentData().getBoolean("IsSitting")) return false;
        return ocelot.distanceToSqr(this.owner) > 9.0D;
    }

    @Override
    public void stop() {
        this.owner = null;
        this.ocelot.getNavigation().stop();
    }
}