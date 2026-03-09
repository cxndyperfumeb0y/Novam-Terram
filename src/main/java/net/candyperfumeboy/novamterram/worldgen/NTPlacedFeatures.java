package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.candyperfumeboy.novamterram.worldgen.NTConfiguredFeatures.*;

/**
 * NTPlacedFeatures
 *
 * Defines the "where and how often" of world generation — placement modifiers that control
 * density, height, rarity, and surface conditions for every feature in Novam Terram.
 * The "what" (shape and block config) lives in NTConfiguredFeatures.
 *
 * Structure:
 *   1. Resource Key Declarations  (organized by biome)
 *   2. bootstrap()                (delegates to sub-methods)
 *   3. registerVanillaPlacements()   — vanilla feature copies only
 *   4. registerOrePlacements()
 *   5. registerTreePlacements()
 *   6. registerVegetationPlacements() — NT custom vegetation + caldera features
 *   7. registerStructurePlacements()  — boulders, logs, spires, dens
 *   8. Helper Methods
 */
public class NTPlacedFeatures {

    // ========================================================================================
    // 1. RESOURCE KEY DECLARATIONS (Organized by Biome)
    // ========================================================================================

    // --- GLOBAL / SHARED ---
    public static final ResourceKey<PlacedFeature> LIGNITE_ORE_PLACED_KEY        = registerKey("lignite_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_LIGNITE_ORE_PLACED_KEY = registerKey("nether_lignite_ore_placed");
    public static final ResourceKey<PlacedFeature> END_LIGNITE_ORE_PLACED_KEY    = registerKey("end_lignite_ore_placed");
    public static final ResourceKey<PlacedFeature> OPAL_ORE_PLACED_KEY           = registerKey("opal_ore_placed");

    // --- AUTUMNAL TAIGA ---
    public static final ResourceKey<PlacedFeature> AUTUMNAL_PINE_PLACED      = registerKey("autumnal_pine_placed");
    public static final ResourceKey<PlacedFeature> SMALL_PINE_PLACED         = registerKey("small_autumnal_pine_placed");
    public static final ResourceKey<PlacedFeature> ORANGE_PINE_CHECKED       = registerKey("orange_autumnal_pine_placed");
    public static final ResourceKey<PlacedFeature> YELLOW_PINE_CHECKED       = registerKey("yellow_autumnal_pine_placed");
    public static final ResourceKey<PlacedFeature> AUTUMNAL_VEGETATION_PLACED = registerKey("autumnal_vegetation_placed");
    public static final ResourceKey<PlacedFeature> AUTUMNAL_FLOWERS_PLACED   = registerKey("autumnal_flowers_placed");
    public static final ResourceKey<PlacedFeature> AUTUMNAL_PUMPKIN_PLACED   = registerKey("autumnal_pumpkin_placed");
    public static final ResourceKey<PlacedFeature> AUTUMNAL_LEAF_PILE_PLACED = registerKey("autumnal_leaf_pile_placed");
    public static final ResourceKey<PlacedFeature> AUTUMNAL_BOULDER_PLACED   = registerKey("autumnal_boulder_placed");
    public static final ResourceKey<PlacedFeature> PUMPKIN_CLUSTER_PLACED    = registerKey("pumpkin_cluster_placed");
    public static final ResourceKey<PlacedFeature> MUSHROOM_PATCH_PLACED     = registerKey("mushroom_patch_placed");
    public static final ResourceKey<PlacedFeature> HUGE_MUSHROOM_RARE_PLACED = registerKey("huge_mushroom_rare_placed");
    public static final ResourceKey<PlacedFeature> HUGE_MUSHROOM_COMMON_PLACED = registerKey("huge_mushroom_common_placed");
    public static final ResourceKey<PlacedFeature> AUTUMNAL_LARGE_FERN       = registerKey("autumnal_large_fern");
    public static final ResourceKey<PlacedFeature> AUTUMNAL_DEAD_BUSH        = registerKey("autumnal_dead_bush");


    // --- MYRTLE FIELDS ---
    public static final ResourceKey<PlacedFeature> MYRTLE_GRASS_PLACED    = registerKey("myrtle_grass_placed");
    public static final ResourceKey<PlacedFeature> MYRTLE_FLOWERS_PLACED  = registerKey("myrtle_flowers_placed");
    public static final ResourceKey<PlacedFeature> WHITE_MYRTLE_TREE_PLACED    = registerKey("white_myrtle_tree_placed");
    public static final ResourceKey<PlacedFeature> RED_MYRTLE_TREE_PLACED    = registerKey("red_myrtle_tree_placed");
    public static final ResourceKey<PlacedFeature> PURPLE_MYRTLE_TREE_PLACED    = registerKey("purple_myrtle_tree_placed");

    public static final ResourceKey<PlacedFeature> MYRTLE_TREES_PLACED    = registerKey("myrtle_trees_placed");

    public static final ResourceKey<PlacedFeature> CHERRY_TREE_RARE_PLACED = registerKey("cherry_tree_rare_placed");
    public static final ResourceKey<PlacedFeature> MYRTLE_TALL_GRASS      = registerKey("myrtle_tall_grass");
    public static final ResourceKey<PlacedFeature> MYRTLE_MELON           = registerKey("myrtle_melon");

    // --- ROYAL FOREST ---
    public static final ResourceKey<PlacedFeature> PAULOWNIA_TREE_PLACED     = registerKey("paulownia_tree_placed");
    public static final ResourceKey<PlacedFeature> ROYAL_FLOWER_PATCH_PLACED = registerKey("royal_flower_patch_placed");
    public static final ResourceKey<PlacedFeature> IRIS_PLACED               = registerKey("iris_placed");
    public static final ResourceKey<PlacedFeature> BLEEDING_HEART_PLACED     = registerKey("bleeding_heart_placed");
    public static final ResourceKey<PlacedFeature> SILVER_SAGE_PLACED        = registerKey("silver_sage_placed");
    public static final ResourceKey<PlacedFeature> FLORAL_FAIRY_RING_PLACED  = registerKey("floral_fairy_ring_placed");
    public static final ResourceKey<PlacedFeature> ROYAL_TALL_GRASS          = registerKey("royal_tall_grass");
    public static final ResourceKey<PlacedFeature> FOREST_ROOTED_DIRT_PLACED = registerKey("forest_rooted_dirt_placed");


    // --- BLUE GROVE ---
    public static final ResourceKey<PlacedFeature> BLUESPRUCE_TAIGA_PLACED_KEY  = registerKey("bluespruce_taiga_placed");
    public static final ResourceKey<PlacedFeature> MEGA_BLUE_SPRUCE_PLACED_KEY  = registerKey("mega_blue_spruce_placed");
    public static final ResourceKey<PlacedFeature> SILVER_ASPEN_PLACED_KEY      = registerKey("silver_aspen_placed");
    public static final ResourceKey<PlacedFeature> FALLEN_ASPEN_LOG_PLACED      = registerKey("fallen_aspen_log_placed");
    public static final ResourceKey<PlacedFeature> BLUE_GROVE_GRASS_PLACED      = registerKey("blue_grove_grass_placed");
    public static final ResourceKey<PlacedFeature> BLUE_GROVE_FLOWER_PATCH_PLACED = registerKey("blue_grove_flower_patch_placed");
    public static final ResourceKey<PlacedFeature> ASTER_PETALS_PLACED          = registerKey("aster_petals_placed");
    public static final ResourceKey<PlacedFeature> OXEYE_DAISY_PLACED           = registerKey("oxeye_daisy_placed");
    public static final ResourceKey<PlacedFeature> BLUE_GROVE_SNOW_PLACED_KEY   = registerKey("blue_grove_snow_placed");
    // Note: SILVER_FOX_DEN_PLACED is a ResourceKey — in NTBiomeFactory use features.getOrThrow(SILVER_FOX_DEN_PLACED)
    public static final ResourceKey<PlacedFeature> SILVER_FOX_DEN_PLACED        = registerKey("silver_fox_den_placed");
    public static final ResourceKey<PlacedFeature> BLUE_GROVE_LARGE_FERN        = registerKey("blue_grove_large_fern");

    // --- HEATH ---
    public static final ResourceKey<PlacedFeature> HEATH_POND_PLACED      = registerKey("heath_pond_placed");
    public static final ResourceKey<PlacedFeature> HEATHER_PATCH_PLACED   = registerKey("heather_placed");
    public static final ResourceKey<PlacedFeature> HEATHER_PETALS_PLACED  = registerKey("heather_petals_placed");
    public static final ResourceKey<PlacedFeature> HEATH_GRASS_PLACED     = registerKey("heath_grass_placed");
    public static final ResourceKey<PlacedFeature> ALLIUM_PATCH_PLACED    = registerKey("allium_patch_placed");
    public static final ResourceKey<PlacedFeature> HEATH_DEAD_BUSH        = registerKey("heath_dead_bush");
    public static final ResourceKey<PlacedFeature> HEATH_PUMPKIN          = registerKey("heath_pumpkin");
    public static final ResourceKey<PlacedFeature> HEATH_WATERLILY        = registerKey("heath_waterlily");
    public static final ResourceKey<PlacedFeature> HEATH_GRASS_PLAIN      = registerKey("heath_grass_plain");

    // --- AEGEAN ---
    public static final ResourceKey<PlacedFeature> OLIVE_TREE_PLACED_KEY      = registerKey("olive_tree_placed");
    public static final ResourceKey<PlacedFeature> LAVENDER_PATCH_PLACED      = registerKey("lavender_placed");
    public static final ResourceKey<PlacedFeature> LAVENDER_PETALS_PLACED     = registerKey("lavender_petals_placed");
    public static final ResourceKey<PlacedFeature> CALCITE_BOULDER_PLACED     = registerKey("calcite_boulder_placed");
    public static final ResourceKey<PlacedFeature> MEDITERRANEAN_GRASS_PLACED = registerKey("mediterranean_grass_placed");
    public static final ResourceKey<PlacedFeature> AEGEAN_CORAL_PLACED        = registerKey("aegean_coral_placed");
    public static final ResourceKey<PlacedFeature> AEGEAN_SUNFLOWER           = registerKey("aegean_sunflower");
    public static final ResourceKey<PlacedFeature> AEGEAN_SUGAR_CANE          = registerKey("aegean_sugar_cane");

    // --- MAQUIS ---
    public static final ResourceKey<PlacedFeature> MAQUIS_SHRUB_PLACED_KEY = registerKey("maquis_shrub_placed");
    public static final ResourceKey<PlacedFeature> MAQUIS_SUNFLOWER        = registerKey("maquis_sunflower");
    public static final ResourceKey<PlacedFeature> MAQUIS_SUGAR_CANE       = registerKey("maquis_sugar_cane");
    public static final ResourceKey<PlacedFeature> FOREST_SHRUBS_PLACED = registerKey("forest_shrubs_placed");
    // --- ANDEAN HILLS ---
    // Note: ANDEAN_VEGETATION height clamp (Y 80-180) is intentional — andesite surface rules
    // only apply at high altitude, so vegetation is restricted to match that zone.
    public static final ResourceKey<PlacedFeature> ANDEAN_TREE_PLACED_KEY  = registerKey("andean_placed");
    public static final ResourceKey<PlacedFeature> ANDEAN_SPIRE_PLACED_KEY = registerKey("andean_spire_placed");
    // Note: ANDEAN_BEAR_CAVE_PLACED is a ResourceKey — in NTBiomeFactory use features.getOrThrow(ANDEAN_BEAR_CAVE_PLACED)
    public static final ResourceKey<PlacedFeature> ANDEAN_BEAR_CAVE_PLACED = registerKey("andean_bear_den_placed");
    public static final ResourceKey<PlacedFeature> TUFF_BOULDER_PLACED     = registerKey("tuff_boulder_placed");
    public static final ResourceKey<PlacedFeature> ANDEAN_GRASS_PLACED     = registerKey("andean_grass_placed");
    public static final ResourceKey<PlacedFeature> ANDEAN_VEGETATION_PLACED = registerKey("andean_vegetation_placed");

    // --- RAINFOREST ---
    public static final ResourceKey<PlacedFeature> TEAK_PLACED_KEY          = registerKey("teak_placed");
    public static final ResourceKey<PlacedFeature> BALSA_PLACED_KEY         = registerKey("balsa_placed");
    public static final ResourceKey<PlacedFeature> MAHOGANY_SHRUB_PLACED_KEY = registerKey("mahogany_shrub_placed");
    public static final ResourceKey<PlacedFeature> RAINFOREST_GRASS_PLACED  = registerKey("rainforest_grass_placed");
    public static final ResourceKey<PlacedFeature> RAINFOREST_MELON         = registerKey("rainforest_melon");
    public static final ResourceKey<PlacedFeature> RAINFOREST_WATERLILY     = registerKey("rainforest_waterlily");

    // --- CYPRESS FEN ---
    public static final ResourceKey<PlacedFeature> CYPRESS_TREE_PLACED     = registerKey("cypress_tree_placed");
    public static final ResourceKey<PlacedFeature> CYPRESS_LILY_PLACED     = registerKey("cypress_lily_placed");
    public static final ResourceKey<PlacedFeature> FEN_MUSHROOM_PLACED     = registerKey("fen_mushroom_placed");
    public static final ResourceKey<PlacedFeature> CYPRESS_KNEE_PLACED     = registerKey("cypress_knee_placed");
    public static final ResourceKey<PlacedFeature> CYPRESS_WATERLILY       = registerKey("cypress_waterlily");
    // Slightly reduced from count 10 → 7 to be less dense than Heath while still lush
    public static final ResourceKey<PlacedFeature> CYPRESS_GRASS_PLACED    = registerKey("cypress_grass_placed");

    // --- MIRROR MIRE ---
    public static final ResourceKey<PlacedFeature> MIRROR_MIRE_TREES   = registerKey("mirror_mire_trees");
    public static final ResourceKey<PlacedFeature> SUNKEN_LOG_PLACED    = registerKey("sunken_log_placed");
    public static final ResourceKey<PlacedFeature> MIRROR_ICE_PLACED    = registerKey("mirror_ice_placed");
    public static final ResourceKey<PlacedFeature> FROSTED_REEDS_PLACED = registerKey("frosted_reeds_placed");
    public static final ResourceKey<PlacedFeature> MIRROR_WATERLILY     = registerKey("mirror_waterlily");
    public static final ResourceKey<PlacedFeature> MIRROR_DEAD_BUSH     = registerKey("mirror_dead_bush");

    // --- SAFFRON GROVE ---
    public static final ResourceKey<PlacedFeature> SAFFRON_VEGETATION_PLACED = registerKey("saffron_vegetation_placed");

    // --- AMBER SANDS ---
    public static final ResourceKey<PlacedFeature> AMBER_SANDS_VEGETATION_PLACED = registerKey("amber_sands_vegetation_placed");

    // --- CALDERA GROTTO ---
    // Note: PERIDOT_SPRING rarity of 3 is intentional — it's a common lush spring feature
    // that defines the caldera floor. Generation quality is tuned in NTFeatures.PERIDOT_SPRING.
    public static final ResourceKey<PlacedFeature> GROTTO_TREE_PLACED      = registerKey("grotto_tree_placed");
    public static final ResourceKey<PlacedFeature> PERIDOT_SPRING_PLACED   = registerKey("peridot_spring_placed");
    public static final ResourceKey<PlacedFeature> GEOTHERMAL_VENT_PLACED  = registerKey("geothermal_vent_placed");
    public static final ResourceKey<PlacedFeature> GROTTO_VEGETATION_PLACED = registerKey("grotto_vegetation_placed");
    public static final ResourceKey<PlacedFeature> CLIFF_SPIKE_PLACED      = registerKey("cliff_spike_placed");

    // --- FUNGAL REACH ---
    public static final ResourceKey<PlacedFeature> FUNGAL_REACH_VEGETATION_PLACED = registerKey("fungal_reach_vegetation_placed");
    public static final ResourceKey<PlacedFeature> FUNGAL_CANOPY_TREE_PLACED      = registerKey("fungal_canopy_tree_placed");

    // --- MISC / SHARED ACROSS BIOMES ---
    public static final ResourceKey<PlacedFeature> RARE_ANCIENT_OAK_PLACED    = registerKey("rare_ancient_oak_placed");
    public static final ResourceKey<PlacedFeature> OLIVINE_SAND_PATCH_PLACED_KEY = registerKey("olivine_sand_placed");
    public static final ResourceKey<PlacedFeature> MOSSY_BOULDER_PLACED       = registerKey("mossy_boulder_placed");
    public static final ResourceKey<PlacedFeature> DIORITE_MAGICAL_ROCK_PLACED = registerKey("diorite_magical_rock_placed");
    // Fixed: renamed from MOSSY_DIORITE_ROCK_PLACED to match NTConfiguredFeatures.DIORITE_ROCK_B
    public static final ResourceKey<PlacedFeature> DIORITE_ROCK_B_PLACED      = registerKey("diorite_rock_b_placed");
    public static final ResourceKey<PlacedFeature> REVAMPED_FOREST_TREES_PLACED = registerKey("revamped_forest_trees_placed");
    public static final ResourceKey<PlacedFeature> TALL_HIGHLAND_BAMBOO_PLACED = registerKey("tall_highland_bamboo_placed");
    // --- BIRCH FOREST ---
    public static final ResourceKey<PlacedFeature> TALL_BIRCH_TREES_PLACED      = registerKey("tall_birch_trees_placed");
    public static final ResourceKey<PlacedFeature> HIGHLAND_BIRCH_PLACED        = registerKey("highland_birch_placed");
    public static final ResourceKey<PlacedFeature> BIRCH_SHRUBS_PLACED          = registerKey("birch_shrubs_placed");
    public static final ResourceKey<PlacedFeature> SILVER_FOREST_FLOWERS_PLACED = registerKey("silver_forest_flowers_placed");
    public static final ResourceKey<PlacedFeature> DENSE_FERNS_PLACED           = registerKey("dense_ferns_placed");

    // ========================================================================================
    // 2. BOOTSTRAP
    // ========================================================================================

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);
        Map<String, Block> blocks = NTBlocks.BLOCKS.getEntries().stream()
                .collect(Collectors.toMap(e -> e.getId().getPath(), DeferredHolder::get));

        registerVanillaPlacements(context, configured);
        registerOrePlacements(context, configured);
        registerTreePlacements(context, configured, blocks);
        registerVegetationPlacements(context, configured);
        registerStructurePlacements(context, configured);
    }

    // ========================================================================================
    // 3. VANILLA PLACEMENTS
    // Pure vanilla configured feature references with NT-specific placement modifiers.
    // Custom NT features belong in registerVegetationPlacements or registerTreePlacements.
    // ========================================================================================

    private static void registerVanillaPlacements(
            BootstrapContext<PlacedFeature> context,
            HolderGetter<ConfiguredFeature<?, ?>> configured) {


        // --- BIRCH FOREST REVAMP ---
// In your bootstrap/registration method for PlacedFeatures:
        register(context, TALL_HIGHLAND_BAMBOO_PLACED, configured.getOrThrow(NTConfiguredFeatures.TALL_HIGHLAND_BAMBOO),
                List.of(
                        // 1. Frequency: Try to place 1 cluster in EVERY chunk.
                        // 20% chance for a second cluster if you want it slightly denser.
                        PlacementUtils.countExtra(1, 0.2f, 1),

                        InSquarePlacement.spread(),

                        // 2. The "Dry Land" Fix: Ensure it doesn't spawn on water
                        SurfaceWaterDepthFilter.forMaxDepth(0),

                        // 3. Aim for the ground
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,

                        // 4. GROVE SIZE: How many stalks in each cluster
                        // Keeping this at 4-7 makes them look like natural "clumps"
                        CountPlacement.of(UniformInt.of(4, 7)),

                        BiomeFilter.biome()
                )
        );
        // Tall Birches (Main Canopy) - Denser placement
// 1. Highland Birches: The "Common" Tree (2-3 per chunk)
// This is your main canopy.
// 1. Highland Birch: The Main Canopy (Fixed at 6 per chunk)
        register(context, HIGHLAND_BIRCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.HIGHLAND_BIRCH_KEY),
                List.of(
                        CountPlacement.of(7), // Strictly 6 attempts per chunk
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BiomeFilter.biome()
                ));

// 2. Tall Birches: The "Visible but Special" Tree (Fixed at 1 per chunk)
        register(context, TALL_BIRCH_TREES_PLACED, configured.getOrThrow(NTConfiguredFeatures.TALL_BIRCH_KEY),
                List.of(
                        CountPlacement.of(1), // Strictly 1 attempt per chunk
                        InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BiomeFilter.biome()
                ));
// Birch Shrubs (Understory) - No longer aquatic!
        register(context, BIRCH_SHRUBS_PLACED, configured.getOrThrow(NTConfiguredFeatures.BIRCH_SHRUB_KEY),
                List.of(
                        // 1. Keep it rare and scattered
                        PlacementUtils.countExtra(1, 0.05f, 1),
                        InSquarePlacement.spread(),

                        // 2. THE FIX: Only spawn where water depth is ZERO
                        SurfaceWaterDepthFilter.forMaxDepth(0),

                        // 3. Use MOTION_BLOCKING to ensure it hits the grass, not a leaf above it
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,

                        BiomeFilter.biome()
                ));
        // Silver Forest Flowers (Lily of the Valley & White Tulips)
        register(context, SILVER_FOREST_FLOWERS_PLACED, configured.getOrThrow(NTConfiguredFeatures.SILVER_FOREST_FLOWERS_KEY),
                List.of(RarityFilter.onAverageOnceEvery(4),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()));

        // --- AUTUMNAL TAIGA ---
        register(context, AUTUMNAL_LARGE_FERN, configured.getOrThrow(VegetationFeatures.PATCH_LARGE_FERN),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 5, 0), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, AUTUMNAL_DEAD_BUSH, configured.getOrThrow(VegetationFeatures.PATCH_DEAD_BUSH),
                List.of(CountPlacement.of(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));


        // --- MYRTLE FIELDS ---
        register(context, MYRTLE_TALL_GRASS, configured.getOrThrow(VegetationFeatures.PATCH_TALL_GRASS),
                List.of(
                        // RarityFilter.onAverageOnceEvery(x) is the "Gold Standard" for rarity.
                        // 32 means only 1 out of every 32 chunks/attempts succeeds.
                        RarityFilter.onAverageOnceEvery(32),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

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

        register(context, FOREST_SHRUBS_PLACED, configured.getOrThrow(NTConfiguredFeatures.FOREST_SHRUBS),
                List.of(
                        PlacementUtils.countExtra(1, 0.05f, 1), // 5% chance per chunk
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, MYRTLE_MELON, configured.getOrThrow(VegetationFeatures.PATCH_MELON),
                List.of(
                        // Change 8 to 64 or even 128 for a "Truly Rare" feel.
                        RarityFilter.onAverageOnceEvery(64),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome()
                ));

        register(context, FOREST_ROOTED_DIRT_PLACED, configured.getOrThrow(NTConfiguredFeatures.FOREST_ROOTED_DIRT_PATCH),
                List.of(
                        CountPlacement.of(2), // 3 attempts per chunk (makes it common but natural)
                        InSquarePlacement.spread(), // Random X/Z within the chunk
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, // THE FIX: Prevents floating by snapping to ground
                        BiomeFilter.biome() // Keeps it inside the biomes we specify in the modifier
                ));

        // --- ROYAL FOREST ---
        register(context, ROYAL_TALL_GRASS, configured.getOrThrow(VegetationFeatures.PATCH_TALL_GRASS),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 5, 0), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));



        // --- BLUE GROVE ---
        register(context, BLUE_GROVE_LARGE_FERN, configured.getOrThrow(VegetationFeatures.PATCH_LARGE_FERN),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- HEATH ---
        register(context, HEATH_DEAD_BUSH, configured.getOrThrow(VegetationFeatures.PATCH_DEAD_BUSH),
                List.of(CountPlacement.of(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, HEATH_PUMPKIN, configured.getOrThrow(VegetationFeatures.PATCH_PUMPKIN),
                List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, HEATH_WATERLILY, configured.getOrThrow(VegetationFeatures.PATCH_WATERLILY),
                List.of(CountPlacement.of(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, HEATH_GRASS_PLAIN, configured.getOrThrow(VegetationFeatures.PATCH_GRASS),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 5, 10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- AEGEAN ---
        register(context, AEGEAN_SUNFLOWER, configured.getOrThrow(VegetationFeatures.PATCH_SUNFLOWER),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, AEGEAN_SUGAR_CANE, configured.getOrThrow(VegetationFeatures.PATCH_SUGAR_CANE),
                List.of(CountPlacement.of(10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- MAQUIS ---
        register(context, MAQUIS_SUNFLOWER, configured.getOrThrow(VegetationFeatures.PATCH_SUNFLOWER),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, MAQUIS_SUGAR_CANE, configured.getOrThrow(VegetationFeatures.PATCH_SUGAR_CANE),
                List.of(CountPlacement.of(8), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- RAINFOREST ---
        register(context, RAINFOREST_MELON, configured.getOrThrow(VegetationFeatures.PATCH_MELON),
                List.of(CountPlacement.of(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, RAINFOREST_WATERLILY, configured.getOrThrow(VegetationFeatures.PATCH_WATERLILY),
                List.of(CountPlacement.of(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- CYPRESS FEN ---
        register(context, CYPRESS_WATERLILY, configured.getOrThrow(VegetationFeatures.PATCH_WATERLILY),
                List.of(CountPlacement.of(10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // Borrows HEATH_GRASS configured feature — slightly reduced density (10→7) vs Heath
        register(context, CYPRESS_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.HEATH_GRASS),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 5, 7), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- MIRROR MIRE ---
        // Added SurfaceWaterDepthFilter so lily pads place near water edges, not in open water
        register(context, MIRROR_WATERLILY, configured.getOrThrow(VegetationFeatures.PATCH_WATERLILY),
                List.of(CountPlacement.of(2), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(1),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, MIRROR_DEAD_BUSH, configured.getOrThrow(VegetationFeatures.PATCH_DEAD_BUSH),
                List.of(CountPlacement.of(1), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- FUNGAL REACH ---
        // Fixed: HUGE_MUSHROOM_RARE now uses brown mushroom to differentiate from common red
        register(context, HUGE_MUSHROOM_RARE_PLACED, configured.getOrThrow(TreeFeatures.HUGE_BROWN_MUSHROOM),
                List.of(RarityFilter.onAverageOnceEvery(20), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, HUGE_MUSHROOM_COMMON_PLACED, configured.getOrThrow(TreeFeatures.HUGE_RED_MUSHROOM),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
    }

    // ========================================================================================
    // 4. ORE PLACEMENTS
    // ========================================================================================

    private static void registerOrePlacements(
            BootstrapContext<PlacedFeature> context,
            HolderGetter<ConfiguredFeature<?, ?>> configured) {

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
    }

    // ========================================================================================
    // 5. TREE PLACEMENTS
    // ========================================================================================

    private static void registerTreePlacements(
            BootstrapContext<PlacedFeature> context,
            HolderGetter<ConfiguredFeature<?, ?>> configured,
            Map<String, Block> blocks) {

        // --- BLUE GROVE ---
        register(context, MEGA_BLUE_SPRUCE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.MEGA_BLUE_SPRUCE_KEY),
                List.of(PlacementUtils.countExtra(6, 0.1f, 1), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, BLUESPRUCE_TAIGA_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.BLUESPRUCE_TAIGA_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(4, 0.1f, 1), blocks.get("blue_spruce_sapling")));

        register(context, SILVER_ASPEN_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.SILVER_ASPEN_KEY),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        // --- MIRROR MIRE ---
        register(context, MIRROR_MIRE_TREES, configured.getOrThrow(NTConfiguredFeatures.MIRROR_MIRE_TREE_SELECTOR),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(2), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- CYPRESS FEN ---
        register(context, CYPRESS_TREE_PLACED, configured.getOrThrow(NTConfiguredFeatures.CYPRESS_TREE_CONFIGURED),
                List.of(PlacementUtils.countExtra(2, 0.1f, 1), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(2), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- AUTUMNAL TAIGA ---
        // Sapling survival checks for bonemeal compatibility
        register(context, ORANGE_PINE_CHECKED, configured.getOrThrow(NTConfiguredFeatures.ORANGE_AUTUMNAL_PINE),
                List.of(PlacementUtils.filteredByBlockSurvival(NTBlocks.ORANGE_AUTUMNAL_PINE_SAPLING.get())));

        register(context, YELLOW_PINE_CHECKED, configured.getOrThrow(NTConfiguredFeatures.YELLOW_AUTUMNAL_PINE),
                List.of(PlacementUtils.filteredByBlockSurvival(NTBlocks.YELLOW_AUTUMNAL_PINE_SAPLING.get())));

        // Fixed: added SurfaceWaterDepthFilter so pines don't attempt to place in shallow water
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

        // --- MISC / SHARED ---
        register(context, RARE_ANCIENT_OAK_PLACED, configured.getOrThrow(ANCIENT_OAK),
                List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                        BlockPredicateFilter.forPredicate(BlockPredicate.allOf(
                                BlockPredicate.matchesTag(new BlockPos(0, -1, 0), BlockTags.DIRT),
                                BlockPredicate.not(BlockPredicate.hasSturdyFace(Direction.UP)),
                                BlockPredicate.not(BlockPredicate.matchesTag(new BlockPos(0, 5, 0), BlockTags.LEAVES)),
                                BlockPredicate.not(BlockPredicate.matchesTag(new BlockPos(0, 5, 0), BlockTags.LOGS)))),
                        BiomeFilter.biome()));

        // --- MYRTLE FIELDS ---
        // --- INDIVIDUAL PLACED FEATURES (For Saplings/Growers) ---
// We use a simple 1-count for sapling growth logic
// --- INDIVIDUAL PLACED FEATURES (For Saplings/Growers) ---
// Use CountPlacement.of(1) to avoid the "Chance data" exception
        register(context, WHITE_MYRTLE_TREE_PLACED, configured.getOrThrow(WHITE_MYRTLE_TREE_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(1)));

        register(context, RED_MYRTLE_TREE_PLACED, configured.getOrThrow(RED_MYRTLE_TREE_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(1)));

        register(context, PURPLE_MYRTLE_TREE_PLACED, configured.getOrThrow(PURPLE_MYRTLE_TREE_KEY),
                VegetationPlacements.treePlacement(CountPlacement.of(1)));
        register(context, MYRTLE_TREES_PLACED, configured.getOrThrow(MYRTLE_TREES_SELECTOR),
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

        // --- ROYAL FOREST ---
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

        // --- AEGEAN / MAQUIS ---
        register(context, OLIVE_TREE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.OLIVE_TREE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, MAQUIS_SHRUB_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.MAQUIS_SHRUB_KEY),
                List.of(PlacementUtils.countExtra(6, 0.1f, 1), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        // --- RAINFOREST ---
        register(context, TEAK_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.TEAK_TREE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, BALSA_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.BALSA_TREE_KEY),
                List.of(PlacementUtils.countExtra(4, 0.1f, 1), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, MAHOGANY_SHRUB_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.MAHOGANY_SHRUB_KEY),
                List.of(PlacementUtils.countExtra(10, 0.1f, 2), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        // --- ANDEAN HILLS ---
        register(context, ANDEAN_TREE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.ANDEAN_TREE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(9), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(1), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        // --- CALDERA GROTTO ---
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

        // --- FUNGAL REACH ---
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
    }

    // ========================================================================================
    // 6. VEGETATION PLACEMENTS
    // Custom NT vegetation, water features, and Caldera Grotto special features.
    // ========================================================================================

    private static void registerVegetationPlacements(
            BootstrapContext<PlacedFeature> context,
            HolderGetter<ConfiguredFeature<?, ?>> configured) {

        // --- MYRTLE FIELDS ---
        register(context, MYRTLE_GRASS_PLACED, configured.getOrThrow(MYRTLE_GRASS_CONFIG),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 15, 4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, MYRTLE_FLOWERS_PLACED, configured.getOrThrow(MYRTLE_FLOWERS_CONFIG),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- AUTUMNAL TAIGA ---
        register(context, MUSHROOM_PATCH_PLACED, configured.getOrThrow(AUTUMNAL_MUSHROOM_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, AUTUMNAL_PUMPKIN_PLACED, configured.getOrThrow(AUTUMNAL_PUMPKIN_PATCH_CONFIG),
                List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, AUTUMNAL_VEGETATION_PLACED, configured.getOrThrow(AUTUMNAL_VEGETATION_CONFIG),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 15, 4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, AUTUMNAL_FLOWERS_PLACED, configured.getOrThrow(AUTUMNAL_FLOWERS_CONFIG),
                List.of(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- MIRROR MIRE ---
        // Added SurfaceWaterDepthFilter so ice patches form near water edges, not in open water
        register(context, MIRROR_ICE_PLACED, configured.getOrThrow(NTConfiguredFeatures.MIRROR_ICE_PATCH),
                List.of(CountPlacement.of(2), InSquarePlacement.spread(),
                        SurfaceWaterDepthFilter.forMaxDepth(1),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // Fixed: reduced from count 14 → 5 for a patchier, more atmospheric frozen reed feel
        register(context, FROSTED_REEDS_PLACED, configured.getOrThrow(NTConfiguredFeatures.FROSTED_REEDS_CONFIGURED),
                List.of(CountPlacement.of(5), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- HEATH / ROYAL FOREST (SHARED) ---
        register(context, HEATH_POND_PLACED, configured.getOrThrow(NTConfiguredFeatures.HEATH_POND_CONFIG),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- AEGEAN ---
        register(context, AEGEAN_CORAL_PLACED, configured.getOrThrow(NTConfiguredFeatures.AEGEAN_CORAL),
                List.of(CountPlacement.of(20), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome()));

        // --- MISC / SHARED ---
        register(context, OLIVINE_SAND_PATCH_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.OLIVINE_SAND_PATCH_KEY),
                List.of(CountPlacement.of(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, BLUE_GROVE_SNOW_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.BLUE_GROVE_SNOW_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        // --- ROYAL FOREST ---
        register(context, ROYAL_FLOWER_PATCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.ROYAL_FLOWER_PATCH_KEY),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        // --- HEATH ---
        register(context, ALLIUM_PATCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.ALLIUM_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, HEATHER_PATCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.HEATHER_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, HEATHER_PETALS_PLACED, configured.getOrThrow(NTConfiguredFeatures.HEATHER_PETALS_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, OXEYE_DAISY_PLACED, configured.getOrThrow(NTConfiguredFeatures.OXEYE_DAISY_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(14), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        // --- AEGEAN / MAQUIS (SHARED) ---
        register(context, LAVENDER_PATCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.LAVENDER_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, LAVENDER_PETALS_PLACED, configured.getOrThrow(NTConfiguredFeatures.LAVENDER_PETALS_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        // --- BLUE GROVE ---
        register(context, BLUE_GROVE_FLOWER_PATCH_PLACED, configured.getOrThrow(NTConfiguredFeatures.BLUE_GROVE_FLOWER_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, ASTER_PETALS_PLACED, configured.getOrThrow(NTConfiguredFeatures.ASTER_PETALS_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, BLUE_GROVE_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.BLUE_GROVE_GRASS),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 2, 5), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- CYPRESS FEN ---
        register(context, CYPRESS_LILY_PLACED, configured.getOrThrow(NTConfiguredFeatures.CYPRESS_LILY_PATCH),
                List.of(CountPlacement.of(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.WATER)),
                        BiomeFilter.biome()));

        register(context, FEN_MUSHROOM_PLACED, configured.getOrThrow(NTConfiguredFeatures.FEN_MUSHROOM_PATCH),
                List.of(CountPlacement.of(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- ROYAL FOREST ---
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
                List.of(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, BLEEDING_HEART_PLACED, configured.getOrThrow(NTConfiguredFeatures.BLEEDING_HEART_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, IRIS_PLACED, configured.getOrThrow(NTConfiguredFeatures.IRIS_PATCH),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- HEATH ---
        register(context, HEATH_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.HEATH_GRASS),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 5, 10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- RAINFOREST ---
        register(context, RAINFOREST_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.RAINFOREST_GRASS),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 15, 25), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- ANDEAN HILLS ---
        register(context, ANDEAN_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.ANDEAN_GRASS),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 2, 4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // Height clamp intentional: andesite surface rules only apply above Y 80,
        // so vegetation is restricted to match the high-altitude andesite zone.
        register(context, ANDEAN_VEGETATION_PLACED, configured.getOrThrow(NTConfiguredFeatures.ANDEAN_VEGETATION_PATCH),
                List.of(CountPlacement.of(8), InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(80), VerticalAnchor.absolute(180)),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        // --- MEDITERRANEAN (AEGEAN / MAQUIS) ---
        register(context, MEDITERRANEAN_GRASS_PLACED, configured.getOrThrow(NTConfiguredFeatures.MEDITERRANEAN_GRASS),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 4, 8), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- SAFFRON GROVE ---
        register(context, SAFFRON_VEGETATION_PLACED, configured.getOrThrow(NTConfiguredFeatures.SAFFRON_VEGETATION),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 20, 10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- AMBER SANDS ---
        register(context, AMBER_SANDS_VEGETATION_PLACED, configured.getOrThrow(NTConfiguredFeatures.AMBER_SANDS_VEGETATION),
                List.of(CountPlacement.of(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- FUNGAL REACH ---
        register(context, FUNGAL_REACH_VEGETATION_PLACED, configured.getOrThrow(NTConfiguredFeatures.FUNGAL_REACH_VEGETATION),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 12, 5), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- CALDERA GROTTO ---
        // Rarity 3 is intentional — Peridot Spring is a defining lush feature of the caldera floor,
        // not a rare landmark. Generation quality is controlled in NTFeatures.PERIDOT_SPRING.
        register(context, PERIDOT_SPRING_PLACED, configured.getOrThrow(NTConfiguredFeatures.PERIDOT_SPRING_CONFIGURED),
                List.of(RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, GEOTHERMAL_VENT_PLACED, configured.getOrThrow(NTConfiguredFeatures.GEOTHERMAL_VENT_CONFIGURED),
                List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, GROTTO_VEGETATION_PLACED, configured.getOrThrow(NTConfiguredFeatures.GROTTO_VEGETATION),
                List.of(NoiseThresholdCountPlacement.of(-0.8D, 15, 10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
    }

    // ========================================================================================
    // 7. STRUCTURE PLACEMENTS
    // Boulders, fallen logs, spires, dens — physical world objects rather than vegetation.
    // ========================================================================================

    private static void registerStructurePlacements(
            BootstrapContext<PlacedFeature> context,
            HolderGetter<ConfiguredFeature<?, ?>> configured) {

        // --- LOGS ---
        register(context, FALLEN_ASPEN_LOG_PLACED, configured.getOrThrow(NTConfiguredFeatures.FALLEN_ASPEN_LOG_KEY),
                List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, SUNKEN_LOG_PLACED, configured.getOrThrow(NTConfiguredFeatures.SUNKEN_LOG_CONFIGURED),
                List.of(RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- CYPRESS KNEES ---
        register(context, CYPRESS_KNEE_PLACED, configured.getOrThrow(NTConfiguredFeatures.CYPRESS_KNEE_CONFIGURED),
                List.of(CountPlacement.of(8), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- LEAF PILES ---
        register(context, AUTUMNAL_LEAF_PILE_PLACED, configured.getOrThrow(NTConfiguredFeatures.AUTUMNAL_LEAF_PILE_KEY),
                List.of(CountPlacement.of(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // --- BOULDERS ---
        register(context, CALCITE_BOULDER_PLACED, configured.getOrThrow(NTConfiguredFeatures.CALCITE_BOULDER),
                List.of(CountPlacement.of(2), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, TUFF_BOULDER_PLACED, configured.getOrThrow(NTConfiguredFeatures.TUFF_BOULDER),
                List.of(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        // Fixed: now correctly points to MOSSY_COBBLESTONE_BOULDER instead of CALCITE_BOULDER
        register(context, MOSSY_BOULDER_PLACED, configured.getOrThrow(NTConfiguredFeatures.MOSSY_COBBLESTONE_BOULDER),
                List.of(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        register(context, AUTUMNAL_BOULDER_PLACED, configured.getOrThrow(AUTUMNAL_BOULDER_CONFIG),
                List.of(RarityFilter.onAverageOnceEvery(12), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, PUMPKIN_CLUSTER_PLACED, configured.getOrThrow(PUMPKIN_CLUSTER_CONFIG),
                List.of(RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(context, DIORITE_MAGICAL_ROCK_PLACED, configured.getOrThrow(DIORITE_MAGICAL_ROCK),
                List.of(RarityFilter.onAverageOnceEvery(12), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        // Fixed: renamed from MOSSY_DIORITE_ROCK_PLACED, now points to DIORITE_ROCK_B
        register(context, DIORITE_ROCK_B_PLACED, configured.getOrThrow(NTConfiguredFeatures.DIORITE_ROCK_B),
                List.of(RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));


        register(context, ANDEAN_SPIRE_PLACED_KEY, configured.getOrThrow(NTConfiguredFeatures.ANDEAN_SPIRE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(15), InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG), BiomeFilter.biome()));

        register(context, CLIFF_SPIKE_PLACED, configured.getOrThrow(NTConfiguredFeatures.CLIFF_SPIKE_KEY),
                List.of(CountPlacement.of(1), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        RarityFilter.onAverageOnceEvery(10), BiomeFilter.biome()));

        // --- DENS ---
        // Note: In NTBiomeFactory, use features.getOrThrow(SILVER_FOX_DEN_PLACED) not the raw field
        register(context, SILVER_FOX_DEN_PLACED, configured.getOrThrow(NTConfiguredFeatures.SILVER_FOX_DEN_KEY),
                List.of(RarityFilter.onAverageOnceEvery(14), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));

        // Note: In NTBiomeFactory, use features.getOrThrow(ANDEAN_BEAR_CAVE_PLACED) not the raw field
        register(context, ANDEAN_BEAR_CAVE_PLACED, configured.getOrThrow(NTConfiguredFeatures.ANDEAN_BEAR_DEN_KEY),
                List.of(RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));
    }

    // ========================================================================================
    // 8. HELPER METHODS
    // ========================================================================================

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, name));
    }

    private static void register(
            BootstrapContext<PlacedFeature> context,
            ResourceKey<PlacedFeature> key,
            Holder<ConfiguredFeature<?, ?>> configuration,
            List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, modifiers));
    }
}