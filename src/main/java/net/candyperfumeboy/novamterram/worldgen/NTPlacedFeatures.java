package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

import static net.candyperfumeboy.novamterram.worldgen.NTConfiguredFeatures.ALLIUM_PATCH;

public class NTPlacedFeatures {

    // --- ORE PLACEMENT KEYS ---
    public static final ResourceKey<PlacedFeature> LIGNITE_ORE_PLACED_KEY = registerKey("lignite_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_LIGNITE_ORE_PLACED_KEY = registerKey("nether_lignite_ore_placed");
    public static final ResourceKey<PlacedFeature> END_LIGNITE_ORE_PLACED_KEY = registerKey("end_lignite_ore_placed");
    public static final ResourceKey<PlacedFeature> OPAL_ORE_PLACED_KEY = registerKey("opal_ore_placed");

    // --- TREE PLACEMENT KEYS ---
    public static final ResourceKey<PlacedFeature> BLUESPRUCE_TAIGA_PLACED_KEY = registerKey("bluespruce_taiga_placed");
    public static final ResourceKey<PlacedFeature> OLIVE_TREE_PLACED_KEY = registerKey("olive_tree_placed");

    // --- VEGETATION & MISC KEYS ---
    public static final ResourceKey<PlacedFeature> CALCITE_BOULDER_PLACED = registerKey("calcite_boulder_placed");
    public static final ResourceKey<PlacedFeature> MOSSY_BOULDER_PLACED = registerKey("mossy_boulder_placed");
    public static final ResourceKey<PlacedFeature> AEGEAN_CORAL_PLACED = registerKey("aegean_coral_placed");
    public static final ResourceKey<PlacedFeature> ALLIUM_PATCH_PLACED = registerKey("allium_patch_placed");
    public static final ResourceKey<PlacedFeature> OXEYE_DAISY_PLACED = registerKey("oxeye_daisy_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        registerOrePlacements(context, configured);
        registerTreePlacements(context, configured);
        registerVegetationPlacements(context, configured);
    }

    private static void registerTreePlacements(BootstrapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configured) {
        // BLUE SPRUCE: Selector (Mega + Normal) at high density
        register(context, BLUESPRUCE_TAIGA_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.BLUESPRUCE_SELECTOR_KEY),
                VegetationPlacements.treePlacement(
                        PlacementUtils.countExtra(12, 0.1f, 1),
                        NTBlocks.BLUE_SPRUCE_SAPLING.get()
                ));

        // OLIVE TREE
        register(context, OLIVE_TREE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.OLIVE_TREE_KEY),
                List.of(
                        PlacementUtils.countExtra(1, 0.1f, 1),
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BiomeFilter.biome()
                ));
    }

    private static void registerVegetationPlacements(BootstrapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configured) {
        // AEGEAN CORAL
        register(context, AEGEAN_CORAL_PLACED, configured.getOrThrow(NTConfiguredFeatures.AEGEAN_CORAL),
                List.of(
                        CountPlacement.of(20),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Direction.UP.getNormal(), Blocks.WATER)),
                        BiomeFilter.biome()
                ));

        // BOULDERS (Calcite and Mossy Cobblestone)
        register(context, CALCITE_BOULDER_PLACED, configured.getOrThrow(NTConfiguredFeatures.CALCITE_BOULDER),
                List.of(CountPlacement.of(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, MOSSY_BOULDER_PLACED, configured.getOrThrow(NTConfiguredFeatures.CALCITE_BOULDER),
                List.of(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        // ALLIUM PATCHES
        register(context, ALLIUM_PATCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.ALLIUM_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, OXEYE_DAISY_PLACED,
                context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(NTConfiguredFeatures.OXEYE_DAISY_PATCH),
                List.of(
                        RarityFilter.onAverageOnceEvery(14), // 1 patch every 14 chunks
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BiomeFilter.biome()));

    }

    private static void registerOrePlacements(BootstrapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> configured) {
        register(context, LIGNITE_ORE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.OVERWORLD_LIGNITE_ORE_KEY),
                NTOrePlacement.commonOrePlacement(12, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, NETHER_LIGNITE_ORE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.NETHER_LIGNITE_ORE_KEY),
                NTOrePlacement.commonOrePlacement(9, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, END_LIGNITE_ORE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.END_LIGNITE_ORE_KEY),
                NTOrePlacement.commonOrePlacement(12, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(80))));

        register(context, OPAL_ORE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.OPAL_ORE_KEY),
                NTOrePlacement.commonOrePlacement(12, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(120))));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}