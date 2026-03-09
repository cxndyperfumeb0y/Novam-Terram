package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.worldgen.tree.*;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.*;
import net.minecraft.world.level.levelgen.feature.trunkplacers.*;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NTConfiguredFeatures {

    // ========================================================================================
    // 1. RESOURCE KEYS
    // ========================================================================================

    // --- ORES ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_LIGNITE_ORE_KEY = registerKey("lignite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_LIGNITE_ORE_KEY = registerKey("nether_lignite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_LIGNITE_ORE_KEY = registerKey("end_lignite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OPAL_ORE_KEY = registerKey("opal_ore");

    // --- TREES ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUESPRUCE_TAIGA_KEY = registerKey("bluespruce_taiga");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_BLUE_SPRUCE_KEY = registerKey("mega_blue_spruce");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUESPRUCE_SELECTOR_KEY = registerKey("bluespruce_selector");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OLIVE_TREE_KEY = registerKey("olive_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TEAK_TREE_KEY = registerKey("teak_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BALSA_TREE_KEY = registerKey("balsa_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MAHOGANY_SHRUB_KEY = registerKey("mahogany_shrub");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MAQUIS_SHRUB_KEY = registerKey("maquis_shrub");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANDEAN_TREE_KEY = registerKey("andean_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_ASPEN_KEY = registerKey("silver_aspen");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CYPRESS_TREE_CONFIGURED = registerKey("cypress_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GHOST_CYPRESS_CONFIGURED = registerKey("ghost_cypress");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRROR_MIRE_TREE_SELECTOR = registerKey("mirror_mire_tree_selector");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_PINE_SELECTOR = registerKey("autumnal_pine_selector");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORANGE_AUTUMNAL_PINE = registerKey("orange_autumnal_pine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> YELLOW_AUTUMNAL_PINE = registerKey("yellow_autumnal_pine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_ORANGE_PINE = registerKey("small_orange_autumnal_pine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_YELLOW_PINE = registerKey("small_yellow_autumnal_pine");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_PINE_SELECTOR = registerKey("small_autumnal_pine_selector");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PAULOWNIA_TREE_KEY = registerKey("paulownia_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHERRY_TREE_KEY = registerKey("cherry_tree_rare");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYRTLE_SHRUB_KEY = registerKey("myrtle_shrub");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANCIENT_OAK = registerKey("ancient_oak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FUNGAL_CANOPY_TREE = registerKey("fungal_canopy_tree");

    // --- VEGETATION (FLOWERS, GRASS, PATCHES) ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> AEGEAN_CORAL = registerKey("aegean_coral");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OLIVINE_SAND_PATCH_KEY = registerKey("olivine_sand_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_GROVE_SNOW_PATCH = registerKey("blue_grove_snow_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HEATH_POND_CONFIG = registerKey("heath_pond");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRROR_ICE_PATCH = registerKey("mirror_ice_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_SAGE_PATCH = registerKey("silver_sage_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLEEDING_HEART_PATCH = registerKey("bleeding_heart_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> IRIS_PATCH = registerKey("iris_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ALLIUM_PATCH = registerKey("allium_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OXEYE_DAISY_PATCH = registerKey("oxeye_daisy_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HEATHER_PATCH = registerKey("heather_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HEATHER_PETALS_PATCH = registerKey("heather_petals_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LAVENDER_PATCH = registerKey("lavender_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LAVENDER_PETALS_PATCH = registerKey("lavender_petals_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_GROVE_FLOWER_PATCH = registerKey("blue_grove_flower_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ASTER_PETALS_PATCH = registerKey("aster_petals_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CYPRESS_LILY_PATCH = registerKey("cypress_lily_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FEN_MUSHROOM_PATCH = registerKey("fen_mushroom_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ROYAL_FLOWER_PATCH_KEY = registerKey("royal_flower_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_VEGETATION_CONFIG = registerKey("autumnal_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_FLOWERS_CONFIG = registerKey("autumnal_flowers_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLORAL_FAIRY_RING = registerKey("floral_fairy_ring");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUE_GROVE_GRASS = registerKey("blue_grove_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HEATH_GRASS = registerKey("heath_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAINFOREST_GRASS = registerKey("rainforest_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANDEAN_GRASS = registerKey("andean_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEDITERRANEAN_GRASS = registerKey("mediterranean_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANDEAN_VEGETATION_PATCH = registerKey("andean_vegetation_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYRTLE_GRASS_CONFIG = registerKey("myrtle_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MYRTLE_FLOWERS_CONFIG = registerKey("myrtle_flowers");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_PUMPKIN_PATCH_CONFIG = registerKey("autumnal_pumpkin_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_MUSHROOM_PATCH = registerKey("autumnal_mushroom_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SAFFRON_VEGETATION = registerKey("saffron_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AMBER_SANDS_VEGETATION = registerKey("amber_sands_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FUNGAL_REACH_VEGETATION = registerKey("fungal_reach_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PERIDOT_SPRING_CONFIGURED = registerKey("peridot_spring");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GEOTHERMAL_VENT_CONFIGURED = registerKey("geothermal_vent");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROTTO_TREE_KEY = registerKey("grotto_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GROTTO_VEGETATION = registerKey("grotto_vegetation");
    // --- BOULDERS, PILES, COLUMNS ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> CALCITE_BOULDER = registerKey("calcite_boulder");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TUFF_BOULDER = registerKey("tuff_boulder");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANDEAN_SPIRE_KEY = registerKey("andean_spire");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FALLEN_ASPEN_LOG_KEY = registerKey("fallen_aspen_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUNKEN_LOG_CONFIGURED = registerKey("sunken_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CYPRESS_KNEE_CONFIGURED = registerKey("cypress_knee");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FROSTED_REEDS_CONFIGURED = registerKey("frosted_reeds");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RED_PEAK_SPIRE_KEY = registerKey("red_peak_spire");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_LEAF_PILE_KEY = registerKey("autumnal_leaf_pile");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DIORITE_MAGICAL_ROCK = registerKey("diorite_magical_rock");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MOSSY_DIORITE_ROCK = registerKey("mossy_diorite_rock");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PUMPKIN_CLUSTER_CONFIG = registerKey("pumpkin_cluster_config");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AUTUMNAL_BOULDER_CONFIG = registerKey("autumnal_boulder");

    // --- DENS & STRUCTURES ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> SILVER_FOX_DEN_KEY = registerKey("silver_fox_den");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ANDEAN_BEAR_DEN_KEY = registerKey("andean_bear_den");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CLIFF_SPIKE_KEY = registerKey("cliff_spike");

    // ========================================================================================
    // 2. BOOTSTRAP
    // ========================================================================================

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        Map<String, Block> blocks = NTBlocks.BLOCKS.getEntries().stream()
                .collect(Collectors.toMap(e -> e.getId().getPath(), DeferredHolder::get));

        // ------------------------------------------------------------------------------------
        // TREES
        // ------------------------------------------------------------------------------------

        register(context, GHOST_CYPRESS_CONFIGURED, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new CypressTrunkPlacer(8, 4, 2),
                BlockStateProvider.simple(Blocks.AIR),
                new BlobFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), 0),
                new TwoLayersFeatureSize(1, 0, 1)).build());

        register(context, CYPRESS_TREE_CONFIGURED, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new CypressTrunkPlacer(12, 4, 3),
                BlockStateProvider.simple(Blocks.OAK_LEAVES),
                new RandomSpreadFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(2), 80),
                new TwoLayersFeatureSize(1, 1, 2))
                .decorators(List.of(new LeaveVineDecorator(0.4F), new TrunkVineDecorator())).ignoreVines().build());

        register(context, MIRROR_MIRE_TREE_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(CYPRESS_TREE_CONFIGURED)), 0.8F),
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(GHOST_CYPRESS_CONFIGURED)), 0.2F)),
                PlacementUtils.inlinePlaced(configured.getOrThrow(GHOST_CYPRESS_CONFIGURED))));

        register(context, SILVER_ASPEN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(blocks.get("aspen_log")), new StraightTrunkPlacer(10, 3, 0),
                BlockStateProvider.simple(blocks.get("aspen_leaves")), new SpruceFoliagePlacer(ConstantInt.of(2), ConstantInt.of(2), ConstantInt.of(3)),
                new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build());

        register(context, BLUESPRUCE_TAIGA_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(blocks.get("blue_spruce_log")), new StraightTrunkPlacer(5, 2, 1),
                BlockStateProvider.simple(blocks.get("blue_spruce_leaves")), new SpruceFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), ConstantInt.of(3)),
                new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build());

        register(context, MEGA_BLUE_SPRUCE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(blocks.get("blue_spruce_log")), new GiantTrunkPlacer(20, 8, 15),
                BlockStateProvider.simple(blocks.get("blue_spruce_leaves")), new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17)),
                new TwoLayersFeatureSize(1, 1, 2)).ignoreVines().build());

        register(context, BLUESPRUCE_SELECTOR_KEY, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(new WeightedPlacedFeature(placedFeatures.getOrThrow(NTPlacedFeatures.MEGA_BLUE_SPRUCE_PLACED_KEY), 0.2F)),
                placedFeatures.getOrThrow(NTPlacedFeatures.BLUESPRUCE_TAIGA_PLACED_KEY)));

        register(context, ANCIENT_OAK, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new AncientOakTrunkPlacer(8, 4, 3),
                BlockStateProvider.simple(Blocks.OAK_LEAVES),
                new AncientOakFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(6)),
                new TwoLayersFeatureSize(1, 0, 2))
                .decorators(List.of(
                        new BeehiveDecorator(1.0F),
                        new AttachedToLeavesDecorator(0.15F, 1, 0, BlockStateProvider.simple(Blocks.SPORE_BLOSSOM.defaultBlockState()), 1, List.of(Direction.DOWN))
                )).build());

        register(context, ORANGE_AUTUMNAL_PINE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.SPRUCE_LOG),
                new KinkedPineTrunkPlacer(20, 8, 4),
                BlockStateProvider.simple(NTBlocks.ORANGE_AUTUMNAL_LEAVES.get()),
                new KinkedFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), UniformInt.of(12, 14)),
                new TwoLayersFeatureSize(1, 0, 2))
                .decorators(List.of(
                        new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL.defaultBlockState())),
                        new BeehiveDecorator(0.05F)
                )).build());

        register(context, YELLOW_AUTUMNAL_PINE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.SPRUCE_LOG),
                new KinkedPineTrunkPlacer(18, 6, 4),
                BlockStateProvider.simple(NTBlocks.YELLOW_AUTUMNAL_LEAVES.get()),
                new KinkedFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), UniformInt.of(10, 12)),
                new TwoLayersFeatureSize(1, 0, 2))
                .decorators(List.of(
                        new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL.defaultBlockState())),
                        new BeehiveDecorator(0.05F)
                )).build());

        register(context, AUTUMNAL_PINE_SELECTOR, Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(
                PlacementUtils.inlinePlaced(configured.getOrThrow(NTConfiguredFeatures.ORANGE_AUTUMNAL_PINE)),
                PlacementUtils.inlinePlaced(configured.getOrThrow(NTConfiguredFeatures.YELLOW_AUTUMNAL_PINE)),
                PlacementUtils.inlinePlaced(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(TreeFeatures.SPRUCE))
        )));

        register(context, SMALL_ORANGE_PINE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.SPRUCE_LOG),
                new StraightTrunkPlacer(5, 2, 1),
                BlockStateProvider.simple(NTBlocks.ORANGE_AUTUMNAL_LEAVES.get()),
                new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)),
                new TwoLayersFeatureSize(1, 0, 1))
                .ignoreVines().build());

        register(context, SMALL_YELLOW_PINE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.SPRUCE_LOG),
                new StraightTrunkPlacer(5, 2, 1),
                BlockStateProvider.simple(NTBlocks.YELLOW_AUTUMNAL_LEAVES.get()),
                new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)),
                new TwoLayersFeatureSize(1, 0, 1))
                .ignoreVines().build());

        register(context, SMALL_PINE_SELECTOR, Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(
                PlacementUtils.inlinePlaced(configured.getOrThrow(NTConfiguredFeatures.SMALL_ORANGE_PINE)),
                PlacementUtils.inlinePlaced(configured.getOrThrow(NTConfiguredFeatures.SMALL_YELLOW_PINE)),
                PlacementUtils.inlinePlaced(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(TreeFeatures.SPRUCE))
        )));

        register(context, PAULOWNIA_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new CherryTrunkPlacer(7, 1, 0, ConstantInt.of(3), ConstantInt.of(2), UniformInt.of(-2, 0), ConstantInt.of(1)),
                BlockStateProvider.simple(blocks.get("paulownia_leaves")),
                new CherryFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(5), 0.25f, 0.5f, 0.16666667f, 0.33333334f),
                new TwoLayersFeatureSize(1, 0, 2))
                .ignoreVines()
                .decorators(List.of(new BeehiveDecorator(0.08F)))
                .build());

        register(context, CHERRY_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.CHERRY_LOG),
                new CherryTrunkPlacer(7, 1, 0, ConstantInt.of(3), ConstantInt.of(2), UniformInt.of(-2, 0), ConstantInt.of(1)),
                BlockStateProvider.simple(Blocks.CHERRY_LEAVES),
                new CherryFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(5), 0.25f, 0.5f, 0.16666667f, 0.33333334f),
                new TwoLayersFeatureSize(1, 0, 2))
                .ignoreVines()
                .decorators(List.of(new BeehiveDecorator(0.1F)))
                .build());

        register(context, OLIVE_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new MyrtleTrunkPlacer(3, 1, 1),
                BlockStateProvider.simple(NTBlocks.OLIVE_LEAVES.get()),
                new MyrtleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                new TwoLayersFeatureSize(1, 0, 1))
                .ignoreVines()
                .decorators(List.of(
                        new AttachedToLeavesDecorator(0.25F, 1, 0, BlockStateProvider.simple(NTBlocks.OLIVE_HANGING.get().defaultBlockState()), 1, List.of(Direction.DOWN))
                )).build());

        register(context, TEAK_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(blocks.get("teak_log")), new FancyTrunkPlacer(10, 4, 4),
                BlockStateProvider.simple(blocks.get("teak_leaves")), new FancyFoliagePlacer(ConstantInt.of(3), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(TrunkVineDecorator.INSTANCE, new LeaveVineDecorator(0.25F), new CocoaDecorator(0.2F))).build());

        register(context, BALSA_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(blocks.get("balsa_log")), new ForkingTrunkPlacer(8, 3, 3),
                BlockStateProvider.simple(blocks.get("balsa_leaves")), new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(TrunkVineDecorator.INSTANCE, new LeaveVineDecorator(0.15F))).build());

        register(context, MYRTLE_SHRUB_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new MyrtleTrunkPlacer(2, 1, 1),
                BlockStateProvider.simple(NTBlocks.MYRTLE_LEAVES.get()),
                new MyrtleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                new TwoLayersFeatureSize(1, 0, 1))
                .ignoreVines().build());

        register(context, MAQUIS_SHRUB_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG), new StraightTrunkPlacer(1, 0, 0),
                BlockStateProvider.simple(Blocks.OAK_LEAVES), new BushFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 2),
                new TwoLayersFeatureSize(0, 0, 0)).build());

        register(context, MAHOGANY_SHRUB_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(blocks.get("mahogany_log")), new StraightTrunkPlacer(1, 0, 0),
                BlockStateProvider.simple(blocks.get("mahogany_leaves")), new BushFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 2),
                new TwoLayersFeatureSize(0, 0, 0)).build());

        register(context, ANDEAN_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.DARK_OAK_LOG), new BendingTrunkPlacer(2, 1, 1, 3, ConstantInt.of(1)),
                BlockStateProvider.simple(Blocks.AZALEA_LEAVES), new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 50),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(new BeehiveDecorator(0.20F), new TrunkVineDecorator(), new LeaveVineDecorator(0.25F))).ignoreVines().build());

        register(context, GROTTO_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.JUNGLE_LOG),
                new ForkingTrunkPlacer(7, 3, 3), // Splits the trunk for a "thicker" base
                BlockStateProvider.simple(Blocks.JUNGLE_LEAVES),
                new MegaJungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2), // Standard thick jungle foliage
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(
                        new TrunkVineDecorator(),
                        new LeaveVineDecorator(0.25F),
                        new CocoaDecorator(0.2F),
                        // Adds those nice hanging particles throughout the grotto
                        new AttachedToLeavesDecorator(0.1F, 1, 0,
                                BlockStateProvider.simple(Blocks.SPORE_BLOSSOM.defaultBlockState()),
                                1, List.of(Direction.DOWN))
                )).ignoreVines().build());

        register(context, GROTTO_VEGETATION, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(96, 10, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(Blocks.SHORT_GRASS.defaultBlockState(), 50)
                                .add(Blocks.FERN.defaultBlockState(), 30)
                                .add(Blocks.LARGE_FERN.defaultBlockState(), 10)
                                .add(Blocks.PINK_PETALS.defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 3), 10)
                                .add(Blocks.BLUE_ORCHID.defaultBlockState(), 5)
                                .add(Blocks.SPORE_BLOSSOM.defaultBlockState(), 2)
                        )))));

       register(context, PERIDOT_SPRING_CONFIGURED, NTFeatures.PERIDOT_SPRING.get(), NoneFeatureConfiguration.INSTANCE);
        register(context, GEOTHERMAL_VENT_CONFIGURED, NTFeatures.GEOTHERMAL_VENT.get(), NoneFeatureConfiguration.INSTANCE);
        // ------------------------------------------------------------------------------------
        // VEGETATION (FLOWERS, GRASS, PATCHES)
        // ------------------------------------------------------------------------------------

        register(context, AUTUMNAL_MUSHROOM_PATCH, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(32, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(Blocks.BROWN_MUSHROOM.defaultBlockState(), 2)
                                .add(Blocks.RED_MUSHROOM.defaultBlockState(), 1)
                        )))));

        register(context, AEGEAN_CORAL, Feature.RANDOM_PATCH, new RandomPatchConfiguration(20, 5, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.TUBE_CORAL_FAN)))));
        register(context, OLIVINE_SAND_PATCH_KEY, Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(NTBlocks.OLIVINE_SAND.get()))));
        register(context, BLUE_GROVE_SNOW_PATCH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(32, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SNOW.defaultBlockState())))));
        register(context, MIRROR_ICE_PATCH, Feature.DISK, new DiskConfiguration(new RuleBasedBlockStateProvider(BlockStateProvider.simple(Blocks.BLUE_ICE.defaultBlockState()), List.of()), BlockPredicate.matchesBlocks(List.of(Blocks.WATER, Blocks.DIRT, Blocks.MUD)), UniformInt.of(3, 6), 2));
        register(context, HEATH_POND_CONFIG, Feature.DELTA_FEATURE, new DeltaFeatureConfiguration(Blocks.WATER.defaultBlockState(), Blocks.MUD.defaultBlockState(), UniformInt.of(3, 7), UniformInt.of(0, 2)));

        register(context, MYRTLE_GRASS_CONFIG, Feature.RANDOM_PATCH, new RandomPatchConfiguration(32, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SHORT_GRASS)))));
        register(context, MYRTLE_FLOWERS_CONFIG, Feature.FLOWER, new RandomPatchConfiguration(64, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.BLUE_ORCHID.defaultBlockState(), 2).add(Blocks.ALLIUM.defaultBlockState(), 1).add(Blocks.OXEYE_DAISY.defaultBlockState(), 1))))));

        register(context, AUTUMNAL_PUMPKIN_PATCH_CONFIG, Feature.RANDOM_PATCH, new RandomPatchConfiguration(16, 4, 2, PlacementUtils.filtered(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.PUMPKIN.defaultBlockState(), 10).add(Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, Direction.NORTH), 1))), BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE, BlockPredicate.matchesTag(new Vec3i(0, -1, 0), BlockTags.DIRT)))));
        register(context, SILVER_SAGE_PATCH, Feature.RANDOM_PATCH, FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(NTBlocks.SILVER_SAGE.get()))));
        register(context, BLEEDING_HEART_PATCH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(32, 5, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(NTBlocks.BLEEDING_HEART.get())))));
        register(context, IRIS_PATCH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(64, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(NTBlocks.IRIS.get().defaultBlockState())))));
        register(context, FLORAL_FAIRY_RING, Feature.RANDOM_PATCH, new RandomPatchConfiguration(32, 6, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.PINK_PETALS.defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 4))))));

        register(context, ALLIUM_PATCH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(64, 5, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.ALLIUM)))));
        register(context, OXEYE_DAISY_PATCH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(32, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.OXEYE_DAISY)))));
        register(context, CYPRESS_LILY_PATCH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(20, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.LILY_PAD)))));
        register(context, FEN_MUSHROOM_PATCH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(16, 4, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.BROWN_MUSHROOM.defaultBlockState(), 2).add(Blocks.RED_MUSHROOM.defaultBlockState(), 1))))));

        register(context, HEATHER_PATCH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(64, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(NTBlocks.HEATHER.get())))));
        register(context, HEATHER_PETALS_PATCH, Feature.FLOWER, new RandomPatchConfiguration(64, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(NTBlocks.HEATHER_PETALS.get().defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 1), 4).add(NTBlocks.HEATHER_PETALS.get().defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 2), 3).add(NTBlocks.HEATHER_PETALS.get().defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 3), 2).add(NTBlocks.HEATHER_PETALS.get().defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 4), 1).build())))));

        register(context, LAVENDER_PATCH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(64, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(NTBlocks.LAVENDER.get())))));
        register(context, LAVENDER_PETALS_PATCH, Feature.FLOWER, new RandomPatchConfiguration(64, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(NTBlocks.LAVENDER_PETALS.get().defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 1), 4).add(NTBlocks.LAVENDER_PETALS.get().defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 2), 3).add(NTBlocks.LAVENDER_PETALS.get().defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 3), 2).add(NTBlocks.LAVENDER_PETALS.get().defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 4), 1).build())))));

        register(context, BLUE_GROVE_FLOWER_PATCH, Feature.FLOWER, new RandomPatchConfiguration(48, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(blocks.get("aster").defaultBlockState(), 4).add(blocks.get("trillium").defaultBlockState(), 3).add(blocks.get("thistle").defaultBlockState(), 1).build())))));
        register(context, ASTER_PETALS_PATCH, Feature.FLOWER, new RandomPatchConfiguration(64, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(blocks.get("aster_petals").defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 1), 4).add(blocks.get("aster_petals").defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 2), 3).add(blocks.get("aster_petals").defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 3), 2).add(blocks.get("aster_petals").defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 4), 1).build())))));

        register(context, ROYAL_FLOWER_PATCH_KEY, Feature.FLOWER, new RandomPatchConfiguration(48, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.POPPY.defaultBlockState(), 2).add(Blocks.DANDELION.defaultBlockState(), 1).build())))));

        register(context, HEATH_GRASS, Feature.RANDOM_PATCH, new RandomPatchConfiguration(96, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SHORT_GRASS)))));
        register(context, BLUE_GROVE_GRASS, Feature.RANDOM_PATCH, new RandomPatchConfiguration(48, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.SHORT_GRASS.defaultBlockState(), 80).add(Blocks.FERN.defaultBlockState(), 20))))));
        register(context, RAINFOREST_GRASS, Feature.RANDOM_PATCH, new RandomPatchConfiguration(128, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.SHORT_GRASS.defaultBlockState(), 70).add(Blocks.FERN.defaultBlockState(), 30))))));
        register(context, ANDEAN_GRASS, Feature.RANDOM_PATCH, new RandomPatchConfiguration(24, 5, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SHORT_GRASS)))));
        register(context, MEDITERRANEAN_GRASS, Feature.RANDOM_PATCH, new RandomPatchConfiguration(48, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SHORT_GRASS)))));
        register(context, ANDEAN_VEGETATION_PATCH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(64, 8, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.SHORT_GRASS.defaultBlockState(), 40).add(Blocks.DEAD_BUSH.defaultBlockState(), 30).add(NTBlocks.BROMELIAD.get().defaultBlockState(), 7).add(Blocks.CORNFLOWER.defaultBlockState(), 5).add(Blocks.MOSS_CARPET.defaultBlockState(), 15))))));

        register(context, AUTUMNAL_VEGETATION_CONFIG, Feature.RANDOM_PATCH, new RandomPatchConfiguration(32, 7, 3, PlacementUtils.filtered(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.SHORT_GRASS.defaultBlockState(), 6).add(Blocks.FERN.defaultBlockState(), 4).add(Blocks.LARGE_FERN.defaultBlockState(), 1).add(Blocks.TALL_GRASS.defaultBlockState(), 2).add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 1))), BlockPredicate.ONLY_IN_AIR_PREDICATE)));
        register(context, AUTUMNAL_FLOWERS_CONFIG, Feature.FLOWER, new RandomPatchConfiguration(64, 6, 2, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.ORANGE_TULIP.defaultBlockState(), 2).add(Blocks.WHITE_TULIP.defaultBlockState(), 1).add(Blocks.CORNFLOWER.defaultBlockState(), 1).add(Blocks.ALLIUM.defaultBlockState(), 1))))));

        register(context, SAFFRON_VEGETATION, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(96, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(Blocks.SHORT_GRASS.defaultBlockState(), 85)
                                .add(Blocks.FERN.defaultBlockState(), 10)
                                .add(Blocks.ORANGE_TULIP.defaultBlockState(), 5) // Occasional splash of color
                                .build())))));

        register(context, AMBER_SANDS_VEGETATION, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(32, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(Blocks.DEAD_BUSH.defaultBlockState(), 50)
                                .add(Blocks.FERN.defaultBlockState(), 20) // The "Hardy" survivor fern
                                .build())))));

        register(context, FUNGAL_REACH_VEGETATION, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(80, 7, 3, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(Blocks.FERN.defaultBlockState(), 40)
                                .add(Blocks.RED_MUSHROOM.defaultBlockState(), 20)
                                .add(Blocks.BROWN_MUSHROOM.defaultBlockState(), 20)
                                .add(Blocks.SHORT_GRASS.defaultBlockState(), 20)
                                .build())))));

        register(context, FUNGAL_CANOPY_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.JUNGLE_LOG),
                new FungalReachTrunkPlacer(10, 2, 2),
                BlockStateProvider.simple(Blocks.JUNGLE_LEAVES),
                new BlobFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(
                        // 1. Messy Vines
                        new LeaveVineDecorator(0.25F),

                        // 2. Cocoa Pods (Requires Jungle Logs)
                        new CocoaDecorator(0.2F),

                        // 3. Spore Blossoms hanging under leaves
                        // Params: probability, exclusionRadius, exclusionVerticalRange, stateProvider, requiredDirection, allowedDirections
                        new AttachedToLeavesDecorator(
                                0.15F,                  // probability (float)
                                1,                      // exclusion_radius (int)
                                0,                      // exclusion_vertical_range (int)
                                BlockStateProvider.simple(Blocks.SPORE_BLOSSOM), // block provider
                                1,                      // required_empty_blocks (int) <--- This was likely the "Direction" error
                                List.of(Direction.DOWN) // directions (List<Direction>)
                        )
                )).ignoreVines().build());

        // ------------------------------------------------------------------------------------
        // BOULDERS, PILES, COLUMNS
        // ------------------------------------------------------------------------------------

        register(context, RED_PEAK_SPIRE_KEY, NTFeatures.ANDEAN_SPIRE.get(), NoneFeatureConfiguration.INSTANCE);
        register(context, ANDEAN_SPIRE_KEY, NTFeatures.ANDEAN_SPIRE.get(), NoneFeatureConfiguration.INSTANCE);

        register(context, AUTUMNAL_LEAF_PILE_KEY, Feature.BLOCK_COLUMN, new BlockColumnConfiguration(List.of(BlockColumnConfiguration.layer(ConstantInt.of(1), new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(blocks.get("orange_autumnal_leaf_carpet").defaultBlockState(), 1).add(blocks.get("yellow_autumnal_leaf_carpet").defaultBlockState(), 1)))), Direction.DOWN, BlockPredicate.ONLY_IN_AIR_PREDICATE, true));
        register(context, CYPRESS_KNEE_CONFIGURED, Feature.BLOCK_COLUMN, new BlockColumnConfiguration(List.of(BlockColumnConfiguration.layer(UniformInt.of(1, 2), SimpleStateProvider.simple(Blocks.MUDDY_MANGROVE_ROOTS.defaultBlockState()))), Direction.UP, BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.GRASS_BLOCK, Blocks.MUD, Blocks.DIRT, Blocks.ROOTED_DIRT), true));
        register(context, FROSTED_REEDS_CONFIGURED, Feature.BLOCK_COLUMN, new BlockColumnConfiguration(List.of(BlockColumnConfiguration.layer(UniformInt.of(2, 3), SimpleStateProvider.simple(Blocks.SUGAR_CANE.defaultBlockState()))), Direction.UP, BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.CLAY, Blocks.MUD, Blocks.DIRT, Blocks.GRASS_BLOCK), true));
        register(context, SUNKEN_LOG_CONFIGURED, Feature.BLOCK_COLUMN, new BlockColumnConfiguration(List.of(BlockColumnConfiguration.layer(UniformInt.of(3, 5), new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(Blocks.STRIPPED_OAK_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X), 1)))), Direction.EAST, BlockPredicate.matchesBlocks(List.of(Blocks.AIR, Blocks.WATER, Blocks.SNOW)), true));
        register(context, FALLEN_ASPEN_LOG_KEY, Feature.BLOCK_COLUMN, new BlockColumnConfiguration(List.of(BlockColumnConfiguration.layer(new WeightedListInt(SimpleWeightedRandomList.<IntProvider>builder().add(ConstantInt.of(3), 1).build()), BlockStateProvider.simple(blocks.get("aspen_log").defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X)))), Direction.DOWN, BlockPredicate.ONLY_IN_AIR_PREDICATE, true));

        register(context, CALCITE_BOULDER, Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.CALCITE.defaultBlockState()));
        register(context, TUFF_BOULDER, Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.TUFF.defaultBlockState()));
        register(context, PUMPKIN_CLUSTER_CONFIG, Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.PUMPKIN.defaultBlockState()));
        register(context, AUTUMNAL_BOULDER_CONFIG, Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.MOSSY_COBBLESTONE.defaultBlockState()));
        register(context, DIORITE_MAGICAL_ROCK, Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.DIORITE.defaultBlockState()));
        register(context, MOSSY_DIORITE_ROCK, Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.DIORITE.defaultBlockState()));

        // ------------------------------------------------------------------------------------
        // ORES
        // ------------------------------------------------------------------------------------

        RuleTest stone = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslate = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        register(context, OVERWORLD_LIGNITE_ORE_KEY, Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(stone, blocks.get("lignite_ore").defaultBlockState()), OreConfiguration.target(deepslate, blocks.get("deepslate_lignite_ore").defaultBlockState())), 9));
        register(context, NETHER_LIGNITE_ORE_KEY, Feature.ORE, new OreConfiguration(new BlockMatchTest(Blocks.NETHERRACK), blocks.get("lignite_nether_ore").defaultBlockState(), 9));
        register(context, END_LIGNITE_ORE_KEY, Feature.ORE, new OreConfiguration(new BlockMatchTest(Blocks.END_STONE), blocks.get("lignite_end_ore").defaultBlockState(), 9));
        register(context, OPAL_ORE_KEY, Feature.ORE, new OreConfiguration(stone, blocks.get("opal_ore").defaultBlockState(), 12));

        // ------------------------------------------------------------------------------------
        // DENS & STRUCTURES
        // ------------------------------------------------------------------------------------

        register(context, SILVER_FOX_DEN_KEY, NTFeatures.SILVER_FOX_DEN.get(), NoneFeatureConfiguration.INSTANCE);
        register(context, ANDEAN_BEAR_DEN_KEY, NTFeatures.BEAR_DEN.get(), NoneFeatureConfiguration.INSTANCE);
        register(context, CLIFF_SPIKE_KEY, NTFeatures.SPIKE_FEATURE.get(), NoneFeatureConfiguration.INSTANCE);
    }

    // ========================================================================================
    // 3. HELPER METHODS
    // ========================================================================================

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}