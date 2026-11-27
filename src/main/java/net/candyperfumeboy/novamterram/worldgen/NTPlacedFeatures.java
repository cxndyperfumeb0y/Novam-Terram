package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import java.util.List;

/**
 * The "where and how often" of world generation. Every key is listed here; the rules are in:
 *   NTPlacedTrees   - trees and shrubs
 *   NTPlacedPlants  - grass, flowers, mushrooms
 *   NTPlacedTerrain - ores, boulders, logs, water, spires, dens
 */
public class NTPlacedFeatures {

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
    public static final ResourceKey<PlacedFeature> MYRTLE_LEAF_PILE_PLACED = registerKey("myrtle_leaf_pile_placed");
    public static final ResourceKey<PlacedFeature> PAULOWNIA_LEAF_PILE_PLACED = registerKey("paulownia_leaf_pile_placed");
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
    public static final ResourceKey<PlacedFeature> DIORITE_ROCK_B_PLACED      = registerKey("diorite_rock_b_placed");
    public static final ResourceKey<PlacedFeature> REVAMPED_FOREST_TREES_PLACED = registerKey("revamped_forest_trees_placed");
    public static final ResourceKey<PlacedFeature> TALL_HIGHLAND_BAMBOO_PLACED = registerKey("tall_highland_bamboo_placed");
    // --- BIRCH FOREST ---
    public static final ResourceKey<PlacedFeature> TALL_BIRCH_TREES_PLACED      = registerKey("tall_birch_trees_placed");
    public static final ResourceKey<PlacedFeature> HIGHLAND_BIRCH_PLACED        = registerKey("highland_birch_placed");
    public static final ResourceKey<PlacedFeature> BIRCH_SHRUBS_PLACED          = registerKey("birch_shrubs_placed");
    public static final ResourceKey<PlacedFeature> SILVER_FOREST_FLOWERS_PLACED = registerKey("silver_forest_flowers_placed");
    public static final ResourceKey<PlacedFeature> DENSE_FERNS_PLACED           = registerKey("dense_ferns_placed");

    // --- BLUEBELL WOODS ---
    public static final ResourceKey<PlacedFeature> BLUEBELL_TREES_PLACED = registerKey("bluebell_trees_placed");
    public static final ResourceKey<PlacedFeature> BLUEBELL_SHRUBS_PLACED = registerKey("bluebell_shrubs_placed");
    public static final ResourceKey<PlacedFeature> BLUEBELL_FALLEN_LOG_PLACED = registerKey("bluebell_fallen_log_placed");
    public static final ResourceKey<PlacedFeature> BLUEBELL_BOULDER_PLACED = registerKey("bluebell_boulder_placed");
    public static final ResourceKey<PlacedFeature> BLUEBELL_FLOOR_PLACED = registerKey("bluebell_floor_placed");
    public static final ResourceKey<PlacedFeature> GREAT_OAK_RING_PLACED = registerKey("great_oak_ring_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        NTPlacedTrees.bootstrap(context);
        NTPlacedPlants.bootstrap(context);
        NTPlacedTerrain.bootstrap(context);
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, name));
    }

    static void register(
            BootstrapContext<PlacedFeature> context,
            ResourceKey<PlacedFeature> key,
            Holder<ConfiguredFeature<?, ?>> configuration,
            List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, modifiers));
    }

    // ----------------------------------------------------------------------------------------
    // Helpers. "how often" is a rarity, count or noise modifier.
    // ----------------------------------------------------------------------------------------

    /** On whatever the surface is, including water and leaves. */
    static List<PlacementModifier> onSurface(PlacementModifier howOften) {
        return List.of(howOften, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
    }

    /** On the top solid block. */
    static List<PlacementModifier> onGround(PlacementModifier howOften) {
        return List.of(howOften, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
    }

    /** On the top solid block, never under water. */
    static List<PlacementModifier> onDryGround(PlacementModifier howOften) {
        return List.of(howOften, InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
    }
}
