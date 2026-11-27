package net.candyperfumeboy.novamterram.worldgen.biome.features;

import com.mojang.serialization.Codec;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.worldgen.biome.NTBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

/**
 * Paints the whole floor of a chunk in one pass: big drifts of bluebells, winding grassy paths
 * and ferny gaps between them. Each ground block is looked at exactly once.
 * <p>
 * The shapes come from noise based on the world seed, so they flow smoothly across chunk borders.
 */
public class BluebellFloor extends Feature<NoneFeatureConfiguration> {
    // ---- Numbers you can play with ----
    private static final double PATH_SIZE = 48;     // bigger = longer, lazier paths
    private static final double PATH_WIDTH = 0.07;  // bigger = wider paths
    private static final double DRIFT_SIZE = 28;    // bigger = bigger bluebell drifts
    private static final double DRIFT_CUTOFF = -0.2; // lower = more of the woods covered in bluebells
    private static final double PURPLE_SIZE = 18;
    private static final double PURPLE_CUTOFF = 0.6; // lower = more purple pockets

    public BluebellFloor(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos corner = context.origin();

        SimplexNoise paths = new SimplexNoise(RandomSource.create(level.getSeed() + 101));
        SimplexNoise drifts = new SimplexNoise(RandomSource.create(level.getSeed() + 202));
        SimplexNoise purples = new SimplexNoise(RandomSource.create(level.getSeed() + 303));

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = corner.getX() + dx;
                int z = corner.getZ() + dz;
                pos.set(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);

                // Only empty spots on soil, and only inside Bluebell Woods
                if (!level.isEmptyBlock(pos)
                        || !level.getBlockState(pos.below()).is(BlockTags.DIRT)
                        || !level.getBiome(pos).is(NTBiomes.BLUEBELL_WOODS)) {
                    continue;
                }

                if (mushroomBesideLog(level, random, pos)) {
                    continue;
                }

                boolean onPath = Math.abs(paths.getValue(x / PATH_SIZE, z / PATH_SIZE)) < PATH_WIDTH;
                // The second, small noise gives the drifts ragged edges
                double drift = drifts.getValue(x / DRIFT_SIZE, z / DRIFT_SIZE)
                        + 0.25 * drifts.getValue(x / 6.0, z / 6.0);

                if (onPath) {
                    pathPlant(level, random, pos);
                } else if (drift > DRIFT_CUTOFF) {
                    boolean purple = purples.getValue(x / PURPLE_SIZE, z / PURPLE_SIZE) > PURPLE_CUTOFF;
                    bluebells(level, random, pos, drift - DRIFT_CUTOFF, purple);
                } else {
                    gapPlant(level, random, pos);
                }
            }
        }
        return true;
    }

    /** @param depth how far inside the drift we are; edges are thinner than the middle */
    private void bluebells(WorldGenLevel level, RandomSource random, BlockPos pos, double depth, boolean purple) {
        float roll = random.nextFloat();
        float clusterChance = depth < 0.15 ? 0.35F : 0.7F;

        if (roll < clusterChance) {
            Block cluster = purple ? NTBlocks.PURPLE_BLUEBELL_CLUSTER.get() : NTBlocks.BLUEBELL_CLUSTER.get();
            int amount = depth < 0.15 ? 1 + random.nextInt(2) : 2 + random.nextInt(3);
            level.setBlock(pos, cluster.defaultBlockState()
                    .setValue(PinkPetalsBlock.AMOUNT, amount)
                    .setValue(PinkPetalsBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random)), 2);
        } else if (roll < clusterChance + 0.12F) {
            Block flower = purple ? NTBlocks.PURPLE_BLUEBELL.get() : NTBlocks.BLUEBELL.get();
            level.setBlock(pos, flower.defaultBlockState(), 2);
        } else if (roll < clusterChance + 0.2F) {
            level.setBlock(pos, Blocks.SHORT_GRASS.defaultBlockState(), 2);
        }
    }

    private void pathPlant(WorldGenLevel level, RandomSource random, BlockPos pos) {
        float roll = random.nextFloat();
        if (roll < 0.12F) {
            level.setBlock(pos, Blocks.SHORT_GRASS.defaultBlockState(), 2);
        } else if (roll < 0.14F) {
            level.setBlock(pos, Blocks.FERN.defaultBlockState(), 2);
        }
    }

    private void gapPlant(WorldGenLevel level, RandomSource random, BlockPos pos) {
        float roll = random.nextFloat();
        BlockState plant = null;
        if (roll < 0.25F) plant = Blocks.SHORT_GRASS.defaultBlockState();
        else if (roll < 0.37F) plant = Blocks.FERN.defaultBlockState();
        else if (roll < 0.395F) plant = Blocks.LILY_OF_THE_VALLEY.defaultBlockState();
        else if (roll < 0.41F) plant = NTBlocks.TRILLIUM.get().defaultBlockState();
        else if (roll < 0.425F && level.isEmptyBlock(pos.above())) {
            DoublePlantBlock.placeAt(level, Blocks.LARGE_FERN.defaultBlockState(), pos, 2);
        }

        if (plant != null) {
            level.setBlock(pos, plant, 2);
        }
    }

    /** A few mushrooms along fallen birch logs. They get podzol so they don't pop off in daylight. */
    private boolean mushroomBesideLog(WorldGenLevel level, RandomSource random, BlockPos pos) {
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockState next = level.getBlockState(pos.relative(side));
            if (next.is(Blocks.BIRCH_LOG) && next.getValue(RotatedPillarBlock.AXIS).isHorizontal()) {
                if (random.nextFloat() >= 0.2F) {
                    return false;
                }
                level.setBlock(pos.below(), Blocks.PODZOL.defaultBlockState(), 2);
                level.setBlock(pos, (random.nextInt(3) == 0 ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM).defaultBlockState(), 2);
                return true;
            }
        }
        return false;
    }
}
