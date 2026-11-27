package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * The "what" of world generation. Every key is listed here; the settings behind them are in:
 *   NTConfiguredTrees   - trees and shrubs
 *   NTConfiguredPlants  - grass, flowers, mushrooms
 *   NTConfiguredTerrain - ores, boulders, logs, water, spires, dens
 * Where and how often each one appears is decided in NTPlacedFeatures.
 */
public class NTConfiguredFeatures {

    // --- ORES ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_LIGNITE_ORE_KEY = registerKey("lignite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_LIGNITE_ORE_KEY    = registerKey("nether_lignite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_LIGNITE_ORE_KEY       = registerKey("end_lignite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OPAL_ORE_KEY              = registerKey("opal_ore");

    // --- TREES ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUESPRUCE_TAIGA_KEY      = registerKey("bluespruce_taiga");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_BLUE_SPRUCE_KEY      = registerKey("mega_blue_spruce");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUESPRUCE_SELECTOR_KEY   = registerKey("bluespruce_selector");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OLIVE_TREE_KEY            = registerKey("olive_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TEAK_TREE_KEY             = registerKey("teak_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BALSA_TREE_KEY            = registerKey("balsa_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MAHOGANY_SHRUB_KEY        = registerKey("mahogany_shrub");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MAQUIS_SHRUB_KEY          = registerKey("maquis_shrub");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANDEAN_TREE_KEY           = registerKey("andean_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_ASPEN_KEY          = registerKey("silver_aspen");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CYPRESS_TREE_CONFIGURED   = registerKey("cypress_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GHOST_CYPRESS_CONFIGURED  = registerKey("ghost_cypress");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRROR_MIRE_TREE_SELECTOR = registerKey("mirror_mire_tree_selector");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_PINE_SELECTOR    = registerKey("autumnal_pine_selector");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORANGE_AUTUMNAL_PINE      = registerKey("orange_autumnal_pine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> YELLOW_AUTUMNAL_PINE      = registerKey("yellow_autumnal_pine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_ORANGE_PINE         = registerKey("small_orange_autumnal_pine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_YELLOW_PINE         = registerKey("small_yellow_autumnal_pine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_PINE_SELECTOR       = registerKey("small_autumnal_pine_selector");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PAULOWNIA_TREE_KEY        = registerKey("paulownia_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHERRY_TREE_KEY           = registerKey("cherry_tree_rare");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WHITE_MYRTLE_TREE_KEY          = registerKey("white_myrtle_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RED_MYRTLE_TREE_KEY          = registerKey("red_myrtle_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PURPLE_MYRTLE_TREE_KEY          = registerKey("purple_myrtle_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYRTLE_TREES_SELECTOR = registerKey("myrtle_trees_selector");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ANCIENT_OAK               = registerKey("ancient_oak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FUNGAL_CANOPY_TREE        = registerKey("fungal_canopy_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROTTO_TREE_KEY           = registerKey("grotto_tree");

    // --- VEGETATION (FLOWERS, GRASS, PATCHES) ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> AEGEAN_CORAL              = registerKey("aegean_coral");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OLIVINE_SAND_PATCH_KEY    = registerKey("olivine_sand_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_GROVE_SNOW_PATCH     = registerKey("blue_grove_snow_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HEATH_POND_CONFIG         = registerKey("heath_pond");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRROR_ICE_PATCH          = registerKey("mirror_ice_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_SAGE_PATCH         = registerKey("silver_sage_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLEEDING_HEART_PATCH      = registerKey("bleeding_heart_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> IRIS_PATCH                = registerKey("iris_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ALLIUM_PATCH              = registerKey("allium_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OXEYE_DAISY_PATCH         = registerKey("oxeye_daisy_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HEATHER_PATCH             = registerKey("heather_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HEATHER_PETALS_PATCH      = registerKey("heather_petals_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LAVENDER_PATCH            = registerKey("lavender_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LAVENDER_PETALS_PATCH     = registerKey("lavender_petals_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_GROVE_FLOWER_PATCH   = registerKey("blue_grove_flower_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ASTER_PETALS_PATCH        = registerKey("aster_petals_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CYPRESS_LILY_PATCH        = registerKey("cypress_lily_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FEN_MUSHROOM_PATCH        = registerKey("fen_mushroom_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ROYAL_FLOWER_PATCH_KEY    = registerKey("royal_flower_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_VEGETATION_CONFIG = registerKey("autumnal_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_FLOWERS_CONFIG   = registerKey("autumnal_flowers_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLORAL_FAIRY_RING         = registerKey("floral_fairy_ring");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_GROVE_GRASS          = registerKey("blue_grove_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HEATH_GRASS               = registerKey("heath_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAINFOREST_GRASS          = registerKey("rainforest_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANDEAN_GRASS              = registerKey("andean_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEDITERRANEAN_GRASS       = registerKey("mediterranean_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANDEAN_VEGETATION_PATCH   = registerKey("andean_vegetation_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYRTLE_GRASS_CONFIG       = registerKey("myrtle_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYRTLE_FLOWERS_CONFIG     = registerKey("myrtle_flowers");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_PUMPKIN_PATCH_CONFIG = registerKey("autumnal_pumpkin_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_MUSHROOM_PATCH   = registerKey("autumnal_mushroom_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SAFFRON_VEGETATION        = registerKey("saffron_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AMBER_SANDS_VEGETATION    = registerKey("amber_sands_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FUNGAL_REACH_VEGETATION   = registerKey("fungal_reach_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PERIDOT_SPRING_CONFIGURED = registerKey("peridot_spring");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GEOTHERMAL_VENT_CONFIGURED = registerKey("geothermal_vent");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROTTO_VEGETATION         = registerKey("grotto_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_OAK_KEY = registerKey("tall_oak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SLENDER_DARK_OAK = registerKey("slender_dark_oak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_TREE_SELECTOR = registerKey("forest_tree_selector");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_SHRUBS = registerKey("forest_shrubs");
    // --- BOULDERS, PILES, COLUMNS, STRUCTURES ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> CALCITE_BOULDER           = registerKey("calcite_boulder");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TUFF_BOULDER              = registerKey("tuff_boulder");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSSY_COBBLESTONE_BOULDER = registerKey("mossy_cobblestone_boulder");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DIORITE_MAGICAL_ROCK      = registerKey("diorite_magical_rock");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DIORITE_ROCK_B            = registerKey("diorite_rock_b");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANDEAN_SPIRE_KEY          = registerKey("andean_spire");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FALLEN_ASPEN_LOG_KEY      = registerKey("fallen_aspen_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUNKEN_LOG_CONFIGURED     = registerKey("sunken_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CYPRESS_KNEE_CONFIGURED   = registerKey("cypress_knee");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FROSTED_REEDS_CONFIGURED  = registerKey("frosted_reeds");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RED_PEAK_SPIRE_KEY        = registerKey("red_peak_spire");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_LEAF_PILE_KEY    = registerKey("autumnal_leaf_pile");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYRTLE_LEAF_PILE_KEY = registerKey("myrtle_leaf_pile");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PAULOWNIA_LEAF_PILE_KEY = registerKey("paulownia_leaf_pile");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PUMPKIN_CLUSTER_CONFIG    = registerKey("pumpkin_cluster_config");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_BOULDER_CONFIG   = registerKey("autumnal_boulder");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_ROOTED_DIRT_PATCH   = registerKey("forest_rooted_dirt");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_BIRCH_KEY             = registerKey("tall_birch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HIGHLAND_BIRCH_KEY         = registerKey("highland_birch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_SHRUB_KEY            = registerKey("birch_shrub");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_FOREST_FLOWERS_KEY  = registerKey("silver_forest_flowers");
    // --- DENS ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_FOX_DEN_KEY        = registerKey("silver_fox_den");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANDEAN_BEAR_DEN_KEY       = registerKey("andean_bear_den");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CLIFF_SPIKE_KEY           = registerKey("cliff_spike");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_HIGHLAND_BAMBOO = registerKey("tall_highland_bamboo");

    // --- BLUEBELL WOODS ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUEBELL_TREES = registerKey("bluebell_trees");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUEBELL_SHRUB = registerKey("bluebell_shrub");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUEBELL_FALLEN_LOG = registerKey("bluebell_fallen_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUEBELL_FLOOR = registerKey("bluebell_floor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GREAT_OAK_RING = registerKey("great_oak_ring");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        NTConfiguredTrees.bootstrap(context);
        NTConfiguredPlants.bootstrap(context);
        NTConfiguredTerrain.bootstrap(context);
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, name));
    }

    static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key,
            F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
