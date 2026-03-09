package net.candyperfumeboy.novamterram.worldgen.biome.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GeothermalVent extends Feature<NoneFeatureConfiguration> {
    public GeothermalVent(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource rand = context.random();

        if (!level.getBlockState(pos.below()).is(BlockTags.DIRT) && !level.getBlockState(pos.below()).is(Blocks.MOSS_BLOCK)) return false;

        int height = 2 + rand.nextInt(2);

        // 1. Heat Stress Ring (The ground around the vent)
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos ringPos = pos.below().relative(dir);
            if (rand.nextFloat() < 0.4f) {
                // Bake the ground into Magma or Obsidian
                level.setBlock(ringPos, rand.nextBoolean() ? Blocks.MAGMA_BLOCK.defaultBlockState() : Blocks.OBSIDIAN.defaultBlockState(), 2);
            }
        }

        // 2. Build the Chimney
        for (int i = 0; i <= height; i++) {
            BlockPos center = pos.above(i);

            // The Core
            if (i == height) {
                level.setBlock(center, Blocks.LAVA.defaultBlockState(), 2);
            } else {
                level.setBlock(center, Blocks.SMOOTH_BASALT.defaultBlockState(), 2);
            }

            // The Shell
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos shellPos = center.relative(dir);

                // Mineral Build-up: Mix Calcite in with the Basalt
                BlockState shellBlock = rand.nextFloat() < 0.2f ? Blocks.CALCITE.defaultBlockState() : Blocks.POLISHED_BASALT.defaultBlockState();
                level.setBlock(shellPos, shellBlock, 2);

                // Jagged Top (Dripstone "Teeth")
                if (i == height) {
                    BlockPos spikePos = shellPos.above();
                    level.setBlock(spikePos, Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.UP), 2);
                }
            }
        }
        return true;
    }
}