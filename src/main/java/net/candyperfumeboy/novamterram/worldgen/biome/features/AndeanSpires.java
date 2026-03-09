package net.candyperfumeboy.novamterram.worldgen.biome.features;

import com.mojang.serialization.Codec;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class AndeanSpires extends Feature<NoneFeatureConfiguration> {
    public AndeanSpires(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        WorldGenLevel level = context.level();

        // 1. Find the ground
        while (level.isEmptyBlock(pos) && pos.getY() > level.getMinBuildHeight() + 2) {
            pos = pos.below();
        }

        if (!level.getBlockState(pos).isSolid()) {
            return false;
        }

        int height = random.nextInt(15) + 15;
        int radius = height / 5 + random.nextInt(2);
        BlockPos topPos = null;

        // 2. Build the main spire
        for (int yOffset = 0; yOffset < height; ++yOffset) {
            float taper = (1.0F - (float) yOffset / (float) height) * (float) radius;
            int horizontalRange = Mth.ceil(taper);

            for (int x = -horizontalRange; x <= horizontalRange; ++x) {
                for (int z = -horizontalRange; z <= horizontalRange; ++z) {
                    if (x * x + z * z <= (taper + 0.5F) * (taper + 0.5F)) {
                        BlockPos currentPos = pos.offset(x, yOffset, z);
                        this.placeSpireBlock(level, currentPos);

                        // Keep track of the very last block placed at the peak
                        if (yOffset == height - 1 && x == 0 && z == 0) {
                            topPos = currentPos;
                        }
                    }
                }
            }
        }

        // 3. Place the "Crown" (Bromeliad)
        if (topPos != null) {
            // Replace the very tip with dirt so the plant looks natural
            level.setBlock(topPos, Blocks.DIRT.defaultBlockState(), 2);

            // Place the Bromeliad on top of the dirt tip
            BlockPos plantPos = topPos.above();
            if (level.isEmptyBlock(plantPos)) {
                level.setBlock(plantPos, NTBlocks.BROMELIAD.get().defaultBlockState(), 2);
            }
        }

        // 4. Foundation
        for (int yOffset = -1; yOffset >= -8; --yOffset) {
            int baseRadius = radius;
            for (int x = -baseRadius; x <= baseRadius; ++x) {
                for (int z = -baseRadius; z <= baseRadius; ++z) {
                    if (x * x + z * z <= (baseRadius + 0.5F) * (baseRadius + 0.5F)) {
                        this.placeSpireBlock(level, pos.offset(x, yOffset, z));
                    }
                }
            }
        }

        return true;
    }

    private void placeSpireBlock(WorldGenLevel level, BlockPos pos) {
        RandomSource random = level.getRandom();
        float chance = random.nextFloat();
        BlockState state;

        if (chance < 0.05F) {
            state = NTBlocks.OPAL_ORE.get().defaultBlockState();
        } else if (chance < 0.40F) {
            state = Blocks.ANDESITE.defaultBlockState();
        } else if (chance < 0.55F) {
            state = Blocks.TUFF.defaultBlockState();
        } else {
            state = Blocks.STONE.defaultBlockState();
        }

        BlockState existing = level.getBlockState(pos);
        if (existing.isAir() || existing.is(Blocks.GRASS_BLOCK) || existing.is(Blocks.DIRT) || !existing.isSolid()) {
            this.setBlock(level, pos, state);
        }
    }
}