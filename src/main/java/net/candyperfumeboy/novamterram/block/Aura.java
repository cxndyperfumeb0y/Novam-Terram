package net.candyperfumeboy.novamterram.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Aura {

    private static final int RADIUS = 6;        // Max distance for effect
    private static final double MAX_PULL = 0.05; // Maximum pull strength at center

    public static void apply(ServerLevel level, BlockPos centerPos) {
        Vec3 center = Vec3.atCenterOf(centerPos);

        // Cubic area around the block
        AABB box = new AABB(
                center.x - RADIUS, center.y - RADIUS, center.z - RADIUS,
                center.x + RADIUS, center.y + RADIUS, center.z + RADIUS
        );

        for (Entity entity : level.getEntities(null, box)) {
            if (entity instanceof ExperienceOrb orb) {
                double dx = center.x - orb.getX();
                double dy = center.y - orb.getY();
                double dz = center.z - orb.getZ();

                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (distance == 0) continue;

                // Linear falloff: stronger near the center
                double strength = MAX_PULL * (1.0 - distance / RADIUS);
                if (strength < 0) strength = 0;

                orb.setDeltaMovement(
                        orb.getDeltaMovement().add(
                                dx / distance * strength,
                                dy / distance * strength,
                                dz / distance * strength
                        )
                );
            }
        }
    }
}