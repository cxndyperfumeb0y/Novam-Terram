package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class NTConfiguredFeatures {
    // --- ORE KEYS ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_LIGNITE_ORE_KEY = registerKey("lignite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_LIGNITE_ORE_KEY = registerKey("nether_lignite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_LIGNITE_ORE_KEY = registerKey("end_lignite_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OPAL_ORE_KEY = registerKey("opal_ore");

    // --- TREE KEYS ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUESPRUCE_TAIGA_KEY = registerKey("bluespruce_taiga");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OLIVE_TREE_KEY = registerKey("olive_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_BLUE_SPRUCE_KEY = registerKey("mega_blue_spruce");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUESPRUCE_SELECTOR_KEY = registerKey("bluespruce_selector");

    // --- VEGETATION & MISC KEYS ---
    public static final ResourceKey<ConfiguredFeature<?, ?>> CALCITE_BOULDER = registerKey("calcite_boulder");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AEGEAN_CORAL = registerKey("aegean_coral");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ALLIUM_PATCH = registerKey("allium_patch");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OXEYE_DAISY_PATCH = registerKey("oxeye_daisy_patch");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        registerOreFeatures(context);
        registerTreeFeatures(context);
        registerVegetationFeatures(context);
    }

    private static void registerTreeFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        // THE FIX: We need this lookup so the Selector can find the other trees
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);

        // Olive Tree
        register(context, OLIVE_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.JUNGLE_LOG),
                new FancyTrunkPlacer(3, 2, 2),
                BlockStateProvider.simple(Blocks.JUNGLE_LEAVES),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(1, 0, 1)
        ).ignoreVines().forceDirt().build());

        // Normal Blue Spruce
        register(context, BLUESPRUCE_TAIGA_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(NTBlocks.BLUE_SPRUCE_LOG.get()),
                new StraightTrunkPlacer(5, 2, 1),
                BlockStateProvider.simple(NTBlocks.BLUE_SPRUCE_LEAVES.get()),
                new SpruceFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), ConstantInt.of(3)),
                new TwoLayersFeatureSize(1, 0, 1)
        ).ignoreVines().build());

        // Mega Blue Spruce
        register(context, MEGA_BLUE_SPRUCE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(NTBlocks.BLUE_SPRUCE_LOG.get()),
                new GiantTrunkPlacer(13, 2, 14),
                BlockStateProvider.simple(NTBlocks.BLUE_SPRUCE_LEAVES.get()),
                new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17)),
                new TwoLayersFeatureSize(1, 1, 2)
        ).ignoreVines().build());

        // THE SELECTOR: This uses 'configured' to pick between the two trees above
        register(context, BLUESPRUCE_SELECTOR_KEY, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(MEGA_BLUE_SPRUCE_KEY)), 0.1F)
                ),
                PlacementUtils.inlinePlaced(configured.getOrThrow(BLUESPRUCE_TAIGA_KEY))
        ));
    }

    private static void registerOreFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherrackReplaceables = new BlockMatchTest(Blocks.NETHERRACK);
        RuleTest endReplaceables = new BlockMatchTest(Blocks.END_STONE);

        List<OreConfiguration.TargetBlockState> overworldLigniteOres = List.of(
                OreConfiguration.target(stoneReplaceables, NTBlocks.LIGNITE_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, NTBlocks.DEEPSLATE_LIGNITE_ORE.get().defaultBlockState()));

        register(context, OVERWORLD_LIGNITE_ORE_KEY, Feature.ORE, new OreConfiguration(overworldLigniteOres, 9));
        register(context, NETHER_LIGNITE_ORE_KEY, Feature.ORE, new OreConfiguration(netherrackReplaceables,
                NTBlocks.LIGNITE_NETHER_ORE.get().defaultBlockState(), 9));
        register(context, END_LIGNITE_ORE_KEY, Feature.ORE, new OreConfiguration(endReplaceables,
                NTBlocks.LIGNITE_END_ORE.get().defaultBlockState(), 9));

        List<OreConfiguration.TargetBlockState> overworldGemOres = List.of(
                OreConfiguration.target(stoneReplaceables, NTBlocks.OPAL_ORE.get().defaultBlockState()));
        register(context, OPAL_ORE_KEY, Feature.ORE, new OreConfiguration(overworldGemOres, 12));
    }

    private static void registerVegetationFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, CALCITE_BOULDER, Feature.FOREST_ROCK,
                new BlockStateConfiguration(Blocks.CALCITE.defaultBlockState()));

        register(context, AEGEAN_CORAL, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(20, 5, 3,
                        PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.TUBE_CORAL_FAN)))));

        register(context, ALLIUM_PATCH, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(64, 5, 3,
                        PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.ALLIUM)))));

        register(context, OXEYE_DAISY_PATCH, Feature.FLOWER, new RandomPatchConfiguration(
                32, // Number of attempts to plant a flower per patch
                7,  // Horizontal spread
                3,  // Vertical spread
                PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.OXEYE_DAISY)))));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}