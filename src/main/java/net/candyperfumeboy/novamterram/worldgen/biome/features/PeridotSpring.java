package net.candyperfumeboy.novamterram.worldgen.biome.features;

import com.mojang.serialization.Codec;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class PeridotSpring extends Feature<NoneFeatureConfiguration> {
    public PeridotSpring(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        // 1. Optimization: Quick-fail if the area isn't suitable.
        // We check 1 block below the center for a solid floor.
        if (!level.getBlockState(pos.below()).isSolid()) {
            return false;
        }

        int baseRadius = 5 + random.nextInt(3);
        int depth = 4;

        // Don't hang the pool off a cliff: the ground must carry on out to the rim on all four sides
        for (Direction side : Direction.Plane.HORIZONTAL) {
            if (!level.getBlockState(pos.relative(side, baseRadius - 1).below(2)).isSolid()) {
                return false;
            }
        }

        // We iterate in a square, but the math inside defines the circle.
        // Radius + 2 ensures the "rim" doesn't get cut off.
        for (int x = -baseRadius - 2; x <= baseRadius + 2; x++) {
            for (int z = -baseRadius - 2; z <= baseRadius + 2; z++) {

                // --- THE "JITTER" ENGINE (Optimization: Move outside the Y loop) ---
                // Adding a slight noise offset to the distance calculation creates the organic shape.
                double noise = 0.8 + (random.nextDouble() * 0.4);
                double distSq = (x * x + z * z) * noise;

                double radiusSq = baseRadius * baseRadius;
                double innerRadiusSq = (baseRadius - 2) * (baseRadius - 2);

                // If we are outside the maximum possible rim, skip the column entirely.
                if (distSq > radiusSq + 2) continue;

                for (int y = -depth - 1; y <= 0; y++) {
                    BlockPos target = pos.offset(x, y, z);

                    // --- 1. LAVA UNDER-GLOW (Lowest layer) ---
                    if (y == -depth - 1) {
                        if (distSq < innerRadiusSq) {
                            level.setBlock(target, Blocks.LAVA.defaultBlockState(), 2);
                        }
                        continue;
                    }

                    // --- 2. THE BOILING FLOOR ---
                    if (distSq < innerRadiusSq) {
                        if (y == -depth) {
                            float chance = random.nextFloat();
                            BlockState floor = (chance < 0.55f) ? Blocks.MAGMA_BLOCK.defaultBlockState() :
                                    (chance < 0.85f) ? Blocks.SMOOTH_BASALT.defaultBlockState() :
                                            Blocks.TUFF.defaultBlockState();
                            level.setBlock(target, floor, 2);
                        } else {
                            // --- 3. THE WATER PIT ---
                            level.setBlock(target, Blocks.WATER.defaultBlockState(), 2);
                        }
                    }
                    // --- 4. THE RIM & BEACH (Flush with Y=0) ---
                    else if (distSq <= radiusSq) {
                        if (y == 0) {
                            BlockState rim = random.nextFloat() < 0.7f ?
                                    NTBlocks.OLIVINE_SAND.get().defaultBlockState() :
                                    Blocks.BASALT.defaultBlockState();
                            level.setBlock(target, rim, 2);
                        } else {
                            // Solid foundation blocks under the sand
                            level.setBlock(target, Blocks.TUFF.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}