package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import java.util.List;

import static net.candyperfumeboy.novamterram.worldgen.NTPlacedFeatures.*;

/** Where grass, flowers, mushrooms and other plants grow. The keys are listed in NTPlacedFeatures. */
final class NTPlacedPlants {
    static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, TALL_HIGHLAND_BAMBOO_PLACED, configured.getOrThrow(NTConfiguredFeatures.TALL_HIGHLAND_BAMBOO),
                List.of(
                        PlacementUtils.countExtra(1, 0.2f, 1),            // groves per chunk
                        InSquarePlacement.spread(),
                        CountPlacement.of(UniformInt.of(4, 7)),           // stalks per grove
                        RandomOffsetPlacement.horizontal(UniformInt.of(-3, 3)), // spread them out
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.wouldSurvive(Blocks.BAMBOO.defaultBlockState(), BlockPos.ZERO)),
                        BiomeFilter.biome()));

        // Silver Forest Flowers (Lily of the Valley & White Tulips)
        register(context, SILVER_FOREST_FLOWERS_PLACED, configured.getOrThrow(NTConfiguredFeatures.SILVER_FOREST_FLOWERS_KEY),
                onSurface(RarityFilter.onAverageOnceEvery(4)));

        register(context, AUTUMNAL_LARGE_FERN, configured.getOrThrow(VegetationFeatures.PATCH_LARGE_FERN),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 5, 0)));

        register(context, AUTUMNAL_DEAD_BUSH, configured.getOrThrow(VegetationFeatures.PATCH_DEAD_BUSH),
                onSurface(CountPlacement.of(4)));

        register(context, MYRTLE_TALL_GRASS, configured.getOrThrow(VegetationFeatures.PATCH_TALL_GRASS),
                onSurface(RarityFilter.onAverageOnceEvery(32)));

        register(context, FOREST_SHRUBS_PLACED, configured.getOrThrow(NTConfiguredFeatures.FOREST_SHRUBS),
                onSurface(PlacementUtils.countExtra(1, 0.05f, 1)));

        register(context, MYRTLE_MELON, configured.getOrThrow(VegetationFeatures.PATCH_MELON),
                onSurface(RarityFilter.onAverageOnceEvery(64)));

        register(context, ROYAL_TALL_GRASS, configured.getOrThrow(VegetationFeatures.PATCH_TALL_GRASS),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 5, 0)));

        register(context, BLUE_GROVE_LARGE_FERN, configured.getOrThrow(VegetationFeatures.PATCH_LARGE_FERN),
                onSurface(RarityFilter.onAverageOnceEvery(8)));

        register(context, HEATH_DEAD_BUSH, configured.getOrThrow(VegetationFeatures.PATCH_DEAD_BUSH),
                onSurface(CountPlacement.of(4)));

        register(context, HEATH_PUMPKIN, configured.getOrThrow(VegetationFeatures.PATCH_PUMPKIN),
                onSurface(RarityFilter.onAverageOnceEvery(10)));

        register(context, HEATH_WATERLILY, configured.getOrThrow(VegetationFeatures.PATCH_WATERLILY),
                onSurface(CountPlacement.of(4)));

        register(context, HEATH_GRASS_PLAIN, configured.getOrThrow(VegetationFeatures.PATCH_GRASS),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 5, 10)));

        register(context, AEGEAN_SUNFLOWER, configured.getOrThrow(VegetationFeatures.PATCH_SUNFLOWER),
                onSurface(RarityFilter.onAverageOnceEvery(2)));

        register(context, AEGEAN_SUGAR_CANE, configured.getOrThrow(VegetationFeatures.PATCH_SUGAR_CANE),
                onSurface(CountPlacement.of(10)));

        register(context, MAQUIS_SUNFLOWER, configured.getOrThrow(VegetationFeatures.PATCH_SUNFLOWER),
                onSurface(RarityFilter.onAverageOnceEvery(8)));

        register(context, MAQUIS_SUGAR_CANE, configured.getOrThrow(VegetationFeatures.PATCH_SUGAR_CANE),
                onSurface(CountPlacement.of(8)));

        register(context, RAINFOREST_MELON, configured.getOrThrow(VegetationFeatures.PATCH_MELON),
                onSurface(CountPlacement.of(2)));

        register(context, RAINFOREST_WATERLILY, configured.getOrThrow(VegetationFeatures.PATCH_WATERLILY),
                onSurface(CountPlacement.of(4)));

        register(context, CYPRESS_WATERLILY, configured.getOrThrow(VegetationFeatures.PATCH_WATERLILY),
                onSurface(CountPlacement.of(10)));

        // Borrows HEATH_GRASS configured feature — slightly reduced density (10→7) vs Heath
        register(context, CYPRESS_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.HEATH_GRASS),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 5, 7)));

        // Added SurfaceWaterDepthFilter so lily pads place near water edges, not in open water
        register(context, MIRROR_WATERLILY, configured.getOrThrow(VegetationFeatures.PATCH_WATERLILY),
                List.of(CountPlacement.of(2), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(1),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, MIRROR_DEAD_BUSH, configured.getOrThrow(VegetationFeatures.PATCH_DEAD_BUSH),
                onSurface(CountPlacement.of(1)));

        register(context, HUGE_MUSHROOM_RARE_PLACED, configured.getOrThrow(TreeFeatures.HUGE_BROWN_MUSHROOM),
                onSurface(RarityFilter.onAverageOnceEvery(20)));

        register(context, HUGE_MUSHROOM_COMMON_PLACED, configured.getOrThrow(TreeFeatures.HUGE_RED_MUSHROOM),
                onSurface(RarityFilter.onAverageOnceEvery(2)));

        register(context, MYRTLE_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.MYRTLE_GRASS_CONFIG),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 15, 4)));

        register(context, MYRTLE_FLOWERS_PLACED, configured.getOrThrow(NTConfiguredFeatures.MYRTLE_FLOWERS_CONFIG),
                onSurface(RarityFilter.onAverageOnceEvery(2)));

        register(context, MUSHROOM_PATCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.AUTUMNAL_MUSHROOM_PATCH),
                onSurface(RarityFilter.onAverageOnceEvery(4)));

        register(context, AUTUMNAL_PUMPKIN_PLACED, configured.getOrThrow(NTConfiguredFeatures.AUTUMNAL_PUMPKIN_PATCH_CONFIG),
                onSurface(RarityFilter.onAverageOnceEvery(10)));

        register(context, AUTUMNAL_VEGETATION_PLACED, configured.getOrThrow(NTConfiguredFeatures.AUTUMNAL_VEGETATION_CONFIG),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 15, 4)));

        register(context, AUTUMNAL_FLOWERS_PLACED, configured.getOrThrow(NTConfiguredFeatures.AUTUMNAL_FLOWERS_CONFIG),
                onSurface(RarityFilter.onAverageOnceEvery(6)));

        register(context, FROSTED_REEDS_PLACED, configured.getOrThrow(NTConfiguredFeatures.FROSTED_REEDS_CONFIGURED),
                onSurface(CountPlacement.of(5)));

        register(context, AEGEAN_CORAL_PLACED, configured.getOrThrow(NTConfiguredFeatures.AEGEAN_CORAL),
                List.of(CountPlacement.of(20), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome()));

        register(context, ROYAL_FLOWER_PATCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.ROYAL_FLOWER_PATCH_KEY),
                onGround(RarityFilter.onAverageOnceEvery(2)));

        register(context, ALLIUM_PATCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.ALLIUM_PATCH),
                onGround(RarityFilter.onAverageOnceEvery(8)));

        register(context, HEATHER_PATCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.HEATHER_PATCH),
                onGround(RarityFilter.onAverageOnceEvery(4)));

        register(context, HEATHER_PETALS_PLACED, configured.getOrThrow(NTConfiguredFeatures.HEATHER_PETALS_PATCH),
                onGround(RarityFilter.onAverageOnceEvery(2)));

        register(context, OXEYE_DAISY_PLACED, configured.getOrThrow(NTConfiguredFeatures.OXEYE_DAISY_PATCH),
                onGround(RarityFilter.onAverageOnceEvery(14)));

        register(context, LAVENDER_PATCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.LAVENDER_PATCH),
                onGround(RarityFilter.onAverageOnceEvery(4)));

        register(context, LAVENDER_PETALS_PLACED, configured.getOrThrow(NTConfiguredFeatures.LAVENDER_PETALS_PATCH),
                onGround(RarityFilter.onAverageOnceEvery(2)));

        register(context, BLUE_GROVE_FLOWER_PATCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.BLUE_GROVE_FLOWER_PATCH),
                onGround(RarityFilter.onAverageOnceEvery(4)));

        register(context, ASTER_PETALS_PLACED, configured.getOrThrow(NTConfiguredFeatures.ASTER_PETALS_PATCH),
                onGround(RarityFilter.onAverageOnceEvery(2)));

        register(context, BLUE_GROVE_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.BLUE_GROVE_GRASS),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 2, 5)));

        register(context, CYPRESS_LILY_PLACED, configured.getOrThrow(NTConfiguredFeatures.CYPRESS_LILY_PATCH),
                List.of(CountPlacement.of(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.WATER)),
                        BiomeFilter.biome()));

        register(context, FEN_MUSHROOM_PLACED, configured.getOrThrow(NTConfiguredFeatures.FEN_MUSHROOM_PATCH),
                onSurface(CountPlacement.of(2)));

        register(context, FLORAL_FAIRY_RING_PLACED, configured.getOrThrow(NTConfiguredFeatures.FLORAL_FAIRY_RING),
                List.of(
                        // 10% chance per chunk to try and place a ring.
                        // This is much more reliable than once every 22 chunks.
                        PlacementUtils.countExtra(0, 0.1f, 1),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        // Ensure it only lands on dirt/grass (prevents rings on trees)
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesTag(Direction.DOWN.getNormal(), BlockTags.DIRT)),
                        BiomeFilter.biome()
                ));

        register(context, SILVER_SAGE_PLACED, configured.getOrThrow(NTConfiguredFeatures.SILVER_SAGE_PATCH),
                onSurface(RarityFilter.onAverageOnceEvery(2)));

        register(context, BLEEDING_HEART_PLACED, configured.getOrThrow(NTConfiguredFeatures.BLEEDING_HEART_PATCH),
                onSurface(RarityFilter.onAverageOnceEvery(4)));

        register(context, IRIS_PLACED, configured.getOrThrow(NTConfiguredFeatures.IRIS_PATCH),
                onSurface(RarityFilter.onAverageOnceEvery(8)));

        register(context, HEATH_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.HEATH_GRASS),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 5, 10)));

        register(context, RAINFOREST_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.RAINFOREST_GRASS),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 12, 18)));

        register(context, ANDEAN_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.ANDEAN_GRASS),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 2, 4)));

        register(context, ANDEAN_VEGETATION_PLACED, configured.getOrThrow(NTConfiguredFeatures.ANDEAN_VEGETATION_PATCH),
                List.of(CountPlacement.of(8), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, MEDITERRANEAN_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.MEDITERRANEAN_GRASS),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 4, 8)));

        register(context, SAFFRON_VEGETATION_PLACED, configured.getOrThrow(NTConfiguredFeatures.SAFFRON_VEGETATION),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 20, 10)));

        register(context, AMBER_SANDS_VEGETATION_PLACED, configured.getOrThrow(NTConfiguredFeatures.AMBER_SANDS_VEGETATION),
                onSurface(CountPlacement.of(4)));

        register(context, FUNGAL_REACH_VEGETATION_PLACED, configured.getOrThrow(NTConfiguredFeatures.FUNGAL_REACH_VEGETATION),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 12, 5)));

        register(context, GROTTO_VEGETATION_PLACED, configured.getOrThrow(NTConfiguredFeatures.GROTTO_VEGETATION),
                onSurface(NoiseThresholdCountPlacement.of(-0.8D, 15, 10)));

        // Leaf carpets. The number is how many random spots are tried per chunk;
        // only spots that are underneath the right leaves get a patch. Raise it for more carpet.
        register(context, AUTUMNAL_LEAF_PILE_PLACED, configured.getOrThrow(NTConfiguredFeatures.AUTUMNAL_LEAF_PILE_KEY),
                underLeaves(8, NTBlocks.ORANGE_AUTUMNAL_LEAVES.get(), NTBlocks.YELLOW_AUTUMNAL_LEAVES.get()));

        register(context, MYRTLE_LEAF_PILE_PLACED, configured.getOrThrow(NTConfiguredFeatures.MYRTLE_LEAF_PILE_KEY),
                underLeaves(48, NTBlocks.WHITE_MYRTLE_LEAVES.get(), NTBlocks.RED_MYRTLE_LEAVES.get(),
                        NTBlocks.PURPLE_MYRTLE_LEAVES.get()));

        register(context, PAULOWNIA_LEAF_PILE_PLACED, configured.getOrThrow(NTConfiguredFeatures.PAULOWNIA_LEAF_PILE_KEY),
                underLeaves(6, NTBlocks.PAULOWNIA_LEAVES.get()));
    }

    /** Picks spots on the ground that have one of these leaf blocks somewhere overhead. */
    private static List<PlacementModifier> underLeaves(int tries, Block... leaves) {
        PlacementModifier toGround = HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
        return List.of(
                CountPlacement.of(tries),
                InSquarePlacement.spread(),
                toGround,
                // Look straight up for the leaves (this only passes if it finds them)...
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.matchesBlocks(leaves),
                        BlockPredicate.replaceable(), 20),
                // ...then drop back down to the ground.
                toGround,
                BiomeFilter.biome());
    }
}
