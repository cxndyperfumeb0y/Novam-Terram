package net.candyperfumeboy.novamterram.worldgen.biome.features;

import com.mojang.serialization.Codec;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.worldgen.biome.NTBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SpikeFeature extends Feature<NoneFeatureConfiguration> {

    public SpikeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos pos = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();

        // 1. Safety Check: Don't spawn on top of Springs or deep water
        // If the origin is water, skip it to keep the Grotto/Springs clean.
        if (level.getFluidState(pos).isSource()) {
            return false;
        }

        // 2. Palette selection
        BlockState primary;
        BlockState secondary;
        if (level.getBiome(pos).is(NTBiomes.AMBER_SANDS)) {
            primary = NTBlocks.AMBER_SANDSTONE.get().defaultBlockState();
            secondary = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
        } else if (level.getBiome(pos).is(NTBiomes.CALDERA_GROTTO)) {
            primary = Blocks.BASALT.defaultBlockState();
            secondary = Blocks.SMOOTH_BASALT.defaultBlockState();
        } else return false;

        // 3. Dimensions (12 to 20 blocks tall)
        int height = 12 + random.nextInt(9);
        int radius = 2;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Circle check for a smooth 5x5-ish base
                if (x * x + z * z <= radius * radius) {

                    // --- FOUNDATION: BUILD DOWN ---
                    // This ensures no part of the cliff hangs over a ledge.
                    // We go down up to 10 blocks until we hit something that isn't air or water.
                    BlockPos foundationPos = pos.offset(x, -1, z);
                    for (int d = 0; d < 10; d++) {
                        BlockState checkState = level.getBlockState(foundationPos);
                        if (checkState.isAir() || level.getFluidState(foundationPos).isSource()) {
                            level.setBlock(foundationPos, primary, 2);
                            foundationPos = foundationPos.below();
                        } else {
                            break; // Hit solid ground!
                        }
                    }

                    // --- PILLAR: BUILD UP ---
                    for (int y = 0; y < height; y++) {
                        // Taper the top: If on the last layer, only fill a 3x3 area
                        if (y >= height - 1 && x * x + z * z > 1) {
                            continue;
                        }

                        BlockPos target = pos.offset(x, y, z);
                        // Only place if it's air or water (don't overwrite existing terrain)
                        if (level.getBlockState(target).isAir() || level.getFluidState(target).isSource()) {
                            // Clean 3-block horizontal bands
                            BlockState state = (y % 6 < 3) ? primary : secondary;
                            level.setBlock(target, state, 2);
                        }
                    }
                }
            }
        }
        return true;
    }
}