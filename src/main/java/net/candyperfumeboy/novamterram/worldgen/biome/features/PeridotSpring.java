package net.candyperfumeboy.novamterram.worldgen.biome.features;

import com.mojang.serialization.Codec;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
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

        // 1. Validation: Ensure we are in a valid spot (avoid placing on existing features)
        if (!level.getFluidState(pos).isEmpty()) return false;

        // 2. Dimensions
        int radius = 5 + random.nextInt(3); // Slightly larger for "centerpiece" feel
        int radiusSq = radius * radius;
        int innerRadiusSq = (radius - 2) * (radius - 2);
        int depth = 4; // Deeper for better magma column visibility

        // 3. Carve and Place logic
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int distSq = x * x + z * z;

                if (distSq <= radiusSq) {
                    // We clear a little higher (up to y=4) to ensure no floating trees/grass
                    for (int y = -depth; y <= 4; y++) {
                        BlockPos target = pos.offset(x, y, z);

                        // --- BASIN FLOOR AND WATER ---
                        if (y < 0) {
                            if (distSq < innerRadiusSq) {
                                if (y == -depth) {
                                    // Floor Palette
                                    float chance = random.nextFloat();
                                    BlockState floor = (chance < 0.4f) ? Blocks.MAGMA_BLOCK.defaultBlockState() :
                                            (chance < 0.7f) ? Blocks.SMOOTH_BASALT.defaultBlockState() : Blocks.TUFF.defaultBlockState();
                                    level.setBlock(target, floor, 2);
                                } else {
                                    level.setBlock(target, Blocks.WATER.defaultBlockState(), 2);
                                }
                            } else {
                                // The Slope: Olivine Sand foundation
                                level.setBlock(target, NTBlocks.OLIVINE_SAND.get().defaultBlockState(), 2);
                            }
                        }
                        // --- THE RIM (y=0) ---
                        else if (y == 0) {
                            if (distSq >= innerRadiusSq) {
                                // 50/50 mix of Olivine Sand and Basalt for a volcanic beach look
                                BlockState rim = random.nextBoolean() ? NTBlocks.OLIVINE_SAND.get().defaultBlockState() : Blocks.BASALT.defaultBlockState();
                                level.setBlock(target, rim, 2);
                            } else {
                                level.setBlock(target, Blocks.WATER.defaultBlockState(), 2);
                            }
                        }
                        // --- CLEARING ABOVE ---
                        else {
                            // Clear air space but don't overwrite the spikes if they generated first
                            if (!level.getBlockState(target).is(Blocks.BASALT)) {
                                level.setBlock(target, Blocks.AIR.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}