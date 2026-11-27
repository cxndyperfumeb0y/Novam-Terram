package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import java.util.List;

import static net.candyperfumeboy.novamterram.worldgen.NTPlacedFeatures.*;

/** Where trees and shrubs grow. The keys are listed in NTPlacedFeatures. */
final class NTPlacedTrees {
    static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        // Tall Birches (Main Canopy) - Denser placement
        // This is your main canopy.
        register(context, HIGHLAND_BIRCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.HIGHLAND_BIRCH_KEY),
                onDryGround(CountPlacement.of(7)));

        register(context, TALL_BIRCH_TREES_PLACED, configured.getOrThrow(NTConfiguredFeatures.TALL_BIRCH_KEY),
                onDryGround(CountPlacement.of(1)));

        // Birch Shrubs (Understory) - No longer aquatic!
        register(context, BIRCH_SHRUBS_PLACED, configured.getOrThrow(NTConfiguredFeatures.BIRCH_SHRUB_KEY),
                onDryGround(PlacementUtils.countExtra(1, 0.05f, 1)));

        register(context, REVAMPED_FOREST_TREES_PLACED, configured.getOrThrow(NTConfiguredFeatures.FOREST_TREE_SELECTOR),
                List.of(
                        PlacementUtils.countExtra(8, 0.1f, 1), // Bumped to 10 for a denser "Revamped" look
                        InSquarePlacement.spread(),
                        // 1. First, find the top solid block
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        // 2. Then, ensure it's not underwater
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        // 3. Finally, check if that specific surface block is Dirt
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesTag(Direction.DOWN.getNormal(), BlockTags.DIRT)),
                        BiomeFilter.biome()
                ));

        register(context, MEGA_BLUE_SPRUCE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.MEGA_BLUE_SPRUCE_KEY),
                onDryGround(PlacementUtils.countExtra(6, 0.1f, 1)));

        register(context, BLUESPRUCE_TAIGA_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.BLUESPRUCE_TAIGA_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(4, 0.1f, 1), NTBlocks.BLUE_SPRUCE.sapling.get()));

        register(context, SILVER_ASPEN_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.SILVER_ASPEN_KEY),
                onDryGround(RarityFilter.onAverageOnceEvery(2)));

        register(context, MIRROR_MIRE_TREES, configured.getOrThrow(NTConfiguredFeatures.MIRROR_MIRE_TREE_SELECTOR),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(2), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, CYPRESS_TREE_PLACED, configured.getOrThrow(NTConfiguredFeatures.CYPRESS_TREE_CONFIGURED),
                List.of(PlacementUtils.countExtra(2, 0.1f, 1), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(2), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // Sapling survival checks for bonemeal compatibility
        register(context, ORANGE_PINE_CHECKED, configured.getOrThrow(NTConfiguredFeatures.ORANGE_AUTUMNAL_PINE),
                List.of(PlacementUtils.filteredByBlockSurvival(NTBlocks.ORANGE_AUTUMNAL_PINE_SAPLING.get())));

        register(context, YELLOW_PINE_CHECKED, configured.getOrThrow(NTConfiguredFeatures.YELLOW_AUTUMNAL_PINE),
                List.of(PlacementUtils.filteredByBlockSurvival(NTBlocks.YELLOW_AUTUMNAL_PINE_SAPLING.get())));

        register(context, AUTUMNAL_PINE_PLACED, configured.getOrThrow(NTConfiguredFeatures.AUTUMNAL_PINE_SELECTOR),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 2, 3), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesTag(Direction.DOWN.getNormal(), BlockTags.DIRT)),
                        BiomeFilter.biome()));

        register(context, SMALL_PINE_PLACED, configured.getOrThrow(NTConfiguredFeatures.SMALL_PINE_SELECTOR),
                List.of(
                        // Use a fixed count or Noise to prevent "clumping"
                        // 8 trees per chunk is usually the sweet spot for a dense 'Nature Spirit' feel
                        CountPlacement.of(5),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        // Ensure there is at least a 2-block gap around the trunk
                        // This prevents the "merging" of different colored trees
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesTag(Direction.DOWN.getNormal(), BlockTags.DIRT)),
                        BiomeFilter.biome()
                ));

        register(context, RARE_ANCIENT_OAK_PLACED, configured.getOrThrow(NTConfiguredFeatures.ANCIENT_OAK),
                List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        BlockPredicateFilter.forPredicate(BlockPredicate.allOf(
                                BlockPredicate.matchesTag(new BlockPos(0, -1, 0), BlockTags.DIRT),
                                BlockPredicate.not(BlockPredicate.hasSturdyFace(Direction.UP)),
                                BlockPredicate.not(BlockPredicate.matchesTag(new BlockPos(0, 5, 0), BlockTags.LEAVES)),
                                BlockPredicate.not(BlockPredicate.matchesTag(new BlockPos(0, 5, 0), BlockTags.LOGS)))),
                        BiomeFilter.biome()));

        // We use a simple 1-count for sapling growth logic
        // Use CountPlacement.of(1) to avoid the "Chance data" exception
        register(context, WHITE_MYRTLE_TREE_PLACED, configured.getOrThrow(NTConfiguredFeatures.WHITE_MYRTLE_TREE_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(1)));

        register(context, RED_MYRTLE_TREE_PLACED, configured.getOrThrow(NTConfiguredFeatures.RED_MYRTLE_TREE_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(1)));

        register(context, PURPLE_MYRTLE_TREE_PLACED, configured.getOrThrow(NTConfiguredFeatures.PURPLE_MYRTLE_TREE_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(1)));

        register(context, MYRTLE_TREES_PLACED, configured.getOrThrow(NTConfiguredFeatures.MYRTLE_TREES_SELECTOR),
                List.of(
                        // 1. RARITY: Only attempt to place a tree in 1 out of every 4 chunks.
                        // Increase '4' to '8' or '10' if you want them even rarer.
                        RarityFilter.onAverageOnceEvery(4),
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        // 2. THE ANTI-CLUMPING FILTER (Horizontal Radius check):
                        // Instead of just checking head height, we check for logs
                        // in the immediate area to prevent canopy overlap.
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.not(
                                        BlockPredicate.anyOf(
                                                BlockPredicate.matchesTag(new Vec3i(2, 2, 2), BlockTags.LOGS),
                                                BlockPredicate.matchesTag(new Vec3i(-2, 2, -2), BlockTags.LOGS),
                                                BlockPredicate.matchesTag(new Vec3i(0, 2, 0), BlockTags.LOGS)
                                        )
                                )
                        ),
                        // 3. Survival Check
                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.wouldSurvive(
                                        NTBlocks.WHITE_MYRTLE_SAPLING.get().defaultBlockState(),
                                        BlockPos.ZERO
                                )
                        ),
                        BiomeFilter.biome()
                ));

        register(context, PAULOWNIA_TREE_PLACED, configured.getOrThrow(NTConfiguredFeatures.PAULOWNIA_TREE_KEY),
                List.of(PlacementUtils.countExtra(5, 0.1f, 1), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        BlockPredicateFilter.forPredicate(BlockPredicate.allOf(
                                BlockPredicate.matchesTag(Direction.DOWN.getNormal(), BlockTags.DIRT),
                                BlockPredicate.matchesBlocks(Blocks.AIR))),
                        BiomeFilter.biome()));

        register(context, CHERRY_TREE_RARE_PLACED, configured.getOrThrow(NTConfiguredFeatures.CHERRY_TREE_KEY),
                List.of(PlacementUtils.countExtra(0, 0.05f, 1), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        BlockPredicateFilter.forPredicate(BlockPredicate.allOf(
                                BlockPredicate.matchesTag(Direction.DOWN.getNormal(), BlockTags.DIRT),
                                BlockPredicate.matchesBlocks(Blocks.AIR))),
                        BiomeFilter.biome()));

        register(context, OLIVE_TREE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.OLIVE_TREE_KEY),
                onDryGround(RarityFilter.onAverageOnceEvery(6)));

        register(context, MAQUIS_SHRUB_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.MAQUIS_SHRUB_KEY),
                onDryGround(PlacementUtils.countExtra(6, 0.1f, 1)));

        register(context, TEAK_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.TEAK_TREE_KEY),
                onDryGround(RarityFilter.onAverageOnceEvery(2)));

        register(context, BALSA_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.BALSA_TREE_KEY),
                onDryGround(PlacementUtils.countExtra(4, 0.1f, 1)));

        register(context, MAHOGANY_SHRUB_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.MAHOGANY_SHRUB_KEY),
                onDryGround(PlacementUtils.countExtra(10, 0.1f, 2)));

        register(context, ANDEAN_TREE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.ANDEAN_TREE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(9), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(1), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, GROTTO_TREE_PLACED, configured.getOrThrow(NTConfiguredFeatures.GROTTO_TREE_KEY),
                List.of(
                        PlacementUtils.countExtra(8, 0.1f, 2),
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BlockPredicateFilter.forPredicate(BlockPredicate.allOf(
                                BlockPredicate.matchesTag(new BlockPos(0, -1, 0), BlockTags.DIRT),
                                BlockPredicate.hasSturdyFace(new BlockPos(0, -1, 0), Direction.UP),
                                BlockPredicate.matchesBlocks(new BlockPos(0, 4, 0), Blocks.AIR),
                                BlockPredicate.matchesBlocks(new BlockPos(0, 6, 0), Blocks.AIR))),
                        BiomeFilter.biome()));

        register(context, FUNGAL_CANOPY_TREE_PLACED, configured.getOrThrow(NTConfiguredFeatures.FUNGAL_CANOPY_TREE),
                List.of(
                        CountPlacement.of(8),
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG),
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesTag(
                                new BlockPos(0, -1, 0), BlockTags.DIRT)),
                        BlockPredicateFilter.forPredicate(BlockPredicate.allOf(
                                BlockPredicate.matchesBlocks(new BlockPos(0, 5, 0), Blocks.AIR),
                                BlockPredicate.matchesBlocks(new BlockPos(0, 8, 0), Blocks.AIR))),
                        BiomeFilter.biome()));

        // --- Bluebell Woods ---
        register(context, GREAT_OAK_RING_PLACED, configured.getOrThrow(NTConfiguredFeatures.GREAT_OAK_RING),
                List.of(RarityFilter.onAverageOnceEvery(24), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING),
                        BiomeFilter.biome()));

        register(context, BLUEBELL_TREES_PLACED, configured.getOrThrow(NTConfiguredFeatures.BLUEBELL_TREES),
                List.of(PlacementUtils.countExtra(6, 0.1f, 1), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING),
                        BiomeFilter.biome()));

        register(context, BLUEBELL_SHRUBS_PLACED, configured.getOrThrow(NTConfiguredFeatures.BLUEBELL_SHRUB),
                onDryGround(RarityFilter.onAverageOnceEvery(2)));
    }
}
