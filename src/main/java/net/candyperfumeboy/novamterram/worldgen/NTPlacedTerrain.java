package net.candyperfumeboy.novamterram.worldgen;

import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import java.util.List;

import static net.candyperfumeboy.novamterram.worldgen.NTPlacedFeatures.*;

/** Where ores, boulders, logs, water features, spires and dens appear. The keys are listed in NTPlacedFeatures. */
final class NTPlacedTerrain {
    static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, FOREST_ROOTED_DIRT_PLACED, configured.getOrThrow(NTConfiguredFeatures.FOREST_ROOTED_DIRT_PATCH),
                onSurface(CountPlacement.of(2)));

        register(context, LIGNITE_ORE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.OVERWORLD_LIGNITE_ORE_KEY),
                NTOrePlacement.commonOrePlacement(12,
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, NETHER_LIGNITE_ORE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.NETHER_LIGNITE_ORE_KEY),
                NTOrePlacement.commonOrePlacement(9,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, END_LIGNITE_ORE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.END_LIGNITE_ORE_KEY),
                NTOrePlacement.commonOrePlacement(12,
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, OPAL_ORE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.OPAL_ORE_KEY),
                NTOrePlacement.commonOrePlacement(12,
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(120))));

        // Added SurfaceWaterDepthFilter so ice patches form near water edges, not in open water
        register(context, MIRROR_ICE_PLACED, configured.getOrThrow(NTConfiguredFeatures.MIRROR_ICE_PATCH),
                List.of(CountPlacement.of(2), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(1),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, HEATH_POND_PLACED, configured.getOrThrow(NTConfiguredFeatures.HEATH_POND_CONFIG),
                onSurface(RarityFilter.onAverageOnceEvery(8)));

        register(context, OLIVINE_SAND_PATCH_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.OLIVINE_SAND_PATCH_KEY),
                onGround(CountPlacement.of(2)));

        register(context, BLUE_GROVE_SNOW_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.BLUE_GROVE_SNOW_PATCH),
                onGround(RarityFilter.onAverageOnceEvery(4)));

        // Rarity 3 is intentional — Peridot Spring is a defining lush feature of the caldera floor,
        // not a rare landmark. Generation quality is controlled in NTFeatures.PERIDOT_SPRING.
        register(context, PERIDOT_SPRING_PLACED, configured.getOrThrow(NTConfiguredFeatures.PERIDOT_SPRING_CONFIGURED),
                onSurface(RarityFilter.onAverageOnceEvery(3)));

        register(context, GEOTHERMAL_VENT_PLACED, configured.getOrThrow(NTConfiguredFeatures.GEOTHERMAL_VENT_CONFIGURED),
                onSurface(RarityFilter.onAverageOnceEvery(10)));

        register(context, FALLEN_ASPEN_LOG_PLACED, configured.getOrThrow(NTConfiguredFeatures.FALLEN_ASPEN_LOG_KEY),
                List.of(RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES),
                        // all three blocks of the log need ground under them and room to lie in
                        BlockPredicateFilter.forPredicate(BlockPredicate.allOf(
                                BlockPredicate.matchesTag(new Vec3i(0, -1, 0), BlockTags.DIRT),
                                BlockPredicate.matchesTag(new Vec3i(1, -1, 0), BlockTags.DIRT),
                                BlockPredicate.matchesTag(new Vec3i(2, -1, 0), BlockTags.DIRT),
                                BlockPredicate.replaceable(new Vec3i(0, 0, 0)),
                                BlockPredicate.replaceable(new Vec3i(1, 0, 0)),
                                BlockPredicate.replaceable(new Vec3i(2, 0, 0)))),
                        BiomeFilter.biome()));

        register(context, SUNKEN_LOG_PLACED, configured.getOrThrow(NTConfiguredFeatures.SUNKEN_LOG_CONFIGURED),
                onSurface(RarityFilter.onAverageOnceEvery(5)));

        register(context, CYPRESS_KNEE_PLACED, configured.getOrThrow(NTConfiguredFeatures.CYPRESS_KNEE_CONFIGURED),
                onSurface(CountPlacement.of(8)));

        register(context, CALCITE_BOULDER_PLACED, configured.getOrThrow(NTConfiguredFeatures.CALCITE_BOULDER),
                onGround(CountPlacement.of(2)));

        register(context, TUFF_BOULDER_PLACED, configured.getOrThrow(NTConfiguredFeatures.TUFF_BOULDER),
                onGround(RarityFilter.onAverageOnceEvery(6)));

        register(context, MOSSY_BOULDER_PLACED, configured.getOrThrow(NTConfiguredFeatures.MOSSY_COBBLESTONE_BOULDER),
                onGround(RarityFilter.onAverageOnceEvery(4)));

        register(context, AUTUMNAL_BOULDER_PLACED, configured.getOrThrow(NTConfiguredFeatures.AUTUMNAL_BOULDER_CONFIG),
                onSurface(RarityFilter.onAverageOnceEvery(12)));

        register(context, PUMPKIN_CLUSTER_PLACED, configured.getOrThrow(NTConfiguredFeatures.PUMPKIN_CLUSTER_CONFIG),
                onSurface(RarityFilter.onAverageOnceEvery(8)));

        register(context, DIORITE_MAGICAL_ROCK_PLACED, configured.getOrThrow(NTConfiguredFeatures.DIORITE_MAGICAL_ROCK),
                onSurface(RarityFilter.onAverageOnceEvery(12)));

        register(context, DIORITE_ROCK_B_PLACED, configured.getOrThrow(NTConfiguredFeatures.DIORITE_ROCK_B),
                onSurface(RarityFilter.onAverageOnceEvery(16)));

        register(context, ANDEAN_SPIRE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.ANDEAN_SPIRE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(15), InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome()));

        register(context, CLIFF_SPIKE_PLACED, configured.getOrThrow(NTConfiguredFeatures.CLIFF_SPIKE_KEY),
                onSurface(RarityFilter.onAverageOnceEvery(10)));

        // Note: In NTBiomeFactory, use features.getOrThrow(SILVER_FOX_DEN_PLACED) not the raw field
        register(context, SILVER_FOX_DEN_PLACED, configured.getOrThrow(NTConfiguredFeatures.SILVER_FOX_DEN_KEY),
                onGround(RarityFilter.onAverageOnceEvery(14)));

        // Note: In NTBiomeFactory, use features.getOrThrow(ANDEAN_BEAR_CAVE_PLACED) not the raw field
        register(context, ANDEAN_BEAR_CAVE_PLACED, configured.getOrThrow(NTConfiguredFeatures.ANDEAN_BEAR_DEN_KEY),
                onGround(RarityFilter.onAverageOnceEvery(10)));

        // --- Bluebell Woods ---
        register(context, BLUEBELL_BOULDER_PLACED, configured.getOrThrow(NTConfiguredFeatures.MOSSY_COBBLESTONE_BOULDER),
                onGround(RarityFilter.onAverageOnceEvery(6)));

        register(context, BLUEBELL_FALLEN_LOG_PLACED, configured.getOrThrow(NTConfiguredFeatures.BLUEBELL_FALLEN_LOG),
                List.of(RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES),
                        BiomeFilter.biome()));

        // No placement rules on purpose: the feature itself walks over every block of the chunk
        register(context, BLUEBELL_FLOOR_PLACED, configured.getOrThrow(NTConfiguredFeatures.BLUEBELL_FLOOR),
                List.of());
    }
}
