package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.worldgen.tree.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.*;
import net.minecraft.world.level.levelgen.feature.trunkplacers.*;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import java.util.List;

import static net.candyperfumeboy.novamterram.worldgen.NTConfiguredFeatures.*;

/** Shapes of the trees and shrubs. The keys are listed in NTConfiguredFeatures. */
final class NTConfiguredTrees {
    static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        register(context, HIGHLAND_BIRCH_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.BIRCH_LOG),
                // 1. TRUNK: Base 8, plus 0-4 random.
                // This lands between 8 and 12 blocks (Vanilla is usually 5-7).
                new StraightTrunkPlacer(9, 4, 0),
                BlockStateProvider.simple(Blocks.BIRCH_LEAVES),
                // 2. FOLIAGE: Classic Birch Blob
                // Radius 2, Offset 0, Height 3 is the "Golden Ratio" for Birch.
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                // 3. SIZE: 1, 0, 2 is the standard Minecraft logic for Birch.
                new TwoLayersFeatureSize(1, 0, 2)
        )
                .ignoreVines()
                .build());

        // Tiny Birch Shrub
        register(context, BIRCH_SHRUB_KEY, Feature.TREE,
                shrub(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES));

        register(context, TALL_BIRCH_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.BIRCH_LOG),
                // 1. TRUNK: Base 8, plus 0-4 random.
                // This lands between 8 and 12 blocks (Vanilla is usually 5-7).
                new StraightTrunkPlacer(10, 4, 0),
                BlockStateProvider.simple(Blocks.BIRCH_LEAVES),
                // 2. FOLIAGE: Classic Birch Blob
                // Radius 2, Offset 0, Height 3 is the "Golden Ratio" for Birch.
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                // 3. SIZE: 1, 0, 2 is the standard Minecraft logic for Birch.
                new TwoLayersFeatureSize(1, 0, 2)
        )
                .decorators(List.of(
                        new LeaveVineDecorator(0.20F),
                        new TrunkVineDecorator(),
                        new BeehiveDecorator(0.05F)
                ))
                .build());

        register(context, FOREST_TREE_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(TreeFeatures.FANCY_OAK)), 0.40F),
                // Short Oak (Vanilla Oak)
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(TreeFeatures.OAK)), 0.15F)
        ),
                // Fallback: Your Custom Tall Oak
                PlacementUtils.inlinePlaced(configured.getOrThrow(TALL_OAK_KEY))
        ));

        register(context, GHOST_CYPRESS_CONFIGURED, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new CypressTrunkPlacer(8, 4, 2),
                BlockStateProvider.simple(Blocks.AIR),
                new BlobFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), 0),
                new TwoLayersFeatureSize(1, 0, 1)).build());

        register(context, CYPRESS_TREE_CONFIGURED, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG.defaultBlockState()),
                new CypressTrunkPlacer(12, 4, 3),
                // 1. ADD PERSISTENT FLAG: This prevents the decay logic entirely
                BlockStateProvider.simple(Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true)),
                new RandomSpreadFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(2), 80),
                new TwoLayersFeatureSize(1, 1, 2))
                .decorators(List.of(
                        new LeaveVineDecorator(0.4F),
                        new TrunkVineDecorator()
                ))
                // 2. REMOVE .ignoreVines(): This was likely forcing vines to detach
                .build());

        // Weighted selector: 80% full cypress, 20% ghost (bare) cypress for atmosphere
        register(context, MIRROR_MIRE_TREE_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(CYPRESS_TREE_CONFIGURED)), 0.8F),
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(GHOST_CYPRESS_CONFIGURED)), 0.2F)),
                PlacementUtils.inlinePlaced(configured.getOrThrow(GHOST_CYPRESS_CONFIGURED))));

        register(context, SILVER_ASPEN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(NTBlocks.ASPEN.log.get()),
                new StraightTrunkPlacer(10, 3, 0),
                BlockStateProvider.simple(NTBlocks.ASPEN.leaves.get()),
                new SpruceFoliagePlacer(ConstantInt.of(2), ConstantInt.of(2), ConstantInt.of(3)),
                new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build());

        register(context, BLUESPRUCE_TAIGA_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(NTBlocks.BLUE_SPRUCE.log.get()),
                new StraightTrunkPlacer(5, 2, 1),
                BlockStateProvider.simple(NTBlocks.BLUE_SPRUCE.leaves.get()),
                new SpruceFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), ConstantInt.of(3)),
                new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build());

        register(context, MEGA_BLUE_SPRUCE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(NTBlocks.BLUE_SPRUCE.log.get()),
                new GiantTrunkPlacer(20, 8, 15),
                BlockStateProvider.simple(NTBlocks.BLUE_SPRUCE.leaves.get()),
                new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17)),
                new TwoLayersFeatureSize(1, 1, 2)).ignoreVines().build());

        register(context, BLUESPRUCE_SELECTOR_KEY, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(new WeightedPlacedFeature(placedFeatures.getOrThrow(NTPlacedFeatures.MEGA_BLUE_SPRUCE_PLACED_KEY), 0.2F)),
                placedFeatures.getOrThrow(NTPlacedFeatures.BLUESPRUCE_TAIGA_PLACED_KEY)));

        register(context, ORANGE_AUTUMNAL_PINE, Feature.TREE,
                autumnPine(NTBlocks.ORANGE_AUTUMNAL_LEAVES.get(), 20, 8, 12, 14));

        register(context, YELLOW_AUTUMNAL_PINE, Feature.TREE,
                autumnPine(NTBlocks.YELLOW_AUTUMNAL_LEAVES.get(), 18, 6, 10, 12));

        // Selector: orange, yellow, or a standard vanilla spruce for variety
        register(context, AUTUMNAL_PINE_SELECTOR, Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(HolderSet.direct(
                PlacementUtils.inlinePlaced(configured.getOrThrow(NTConfiguredFeatures.ORANGE_AUTUMNAL_PINE)),
                PlacementUtils.inlinePlaced(configured.getOrThrow(NTConfiguredFeatures.YELLOW_AUTUMNAL_PINE)),
                PlacementUtils.inlinePlaced(configured.getOrThrow(TreeFeatures.SPRUCE))
        )));

        register(context, SMALL_ORANGE_PINE, Feature.TREE,
                smallPine(NTBlocks.ORANGE_AUTUMNAL_LEAVES.get()));

        register(context, SMALL_YELLOW_PINE, Feature.TREE,
                smallPine(NTBlocks.YELLOW_AUTUMNAL_LEAVES.get()));

        register(context, SMALL_PINE_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(
                // 40% chance for Orange
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(NTConfiguredFeatures.SMALL_ORANGE_PINE)), 0.4F),
                // 40% chance for Yellow
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(NTConfiguredFeatures.SMALL_YELLOW_PINE)), 0.4F)
        ),
                // 20% fallback to Vanilla Spruce
                PlacementUtils.inlinePlaced(configured.getOrThrow(TreeFeatures.SPRUCE))
        ));

        // Royal Forest: Large magical oak with beehives and spore blossoms
        register(context, ANCIENT_OAK, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new AncientOakTrunkPlacer(8, 4, 3),
                BlockStateProvider.simple(Blocks.OAK_LEAVES),
                new AncientOakFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(6)),
                // Reduced the limit to 1 to allow more "air" space detection
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(
                        new BeehiveDecorator(0.05F), // Reduced from 1.0F (100%) to 5% so not every tree has a hive
                        new AttachedToLeavesDecorator(
                                0.35F, // Increased probability to 35% to account for failed attempts
                                1,     // Exclusion radius
                                0,     // Exclusion radius vertical
                                BlockStateProvider.simple(Blocks.SPORE_BLOSSOM.defaultBlockState()),
                                3,     // Number of attempts per foliage cluster
                                List.of(Direction.DOWN)
                        )
                ))
                .ignoreVines() // Helps the decorator not get confused by vine growth
                .build());

        register(context, WHITE_MYRTLE_TREE_KEY, Feature.TREE,
                blossomTree(Blocks.OAK_LOG, NTBlocks.WHITE_MYRTLE_LEAVES.get(), 0.1F));

        register(context, RED_MYRTLE_TREE_KEY, Feature.TREE,
                blossomTree(Blocks.OAK_LOG, NTBlocks.RED_MYRTLE_LEAVES.get(), 0.1F));

        register(context, PURPLE_MYRTLE_TREE_KEY, Feature.TREE,
                blossomTree(Blocks.OAK_LOG, NTBlocks.PURPLE_MYRTLE_LEAVES.get(), 0.1F));

        register(context, MYRTLE_TREES_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(WHITE_MYRTLE_TREE_KEY)), 0.4F),
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(RED_MYRTLE_TREE_KEY)), 0.3F)),
                PlacementUtils.inlinePlaced(configured.getOrThrow(PURPLE_MYRTLE_TREE_KEY))));

        register(context, CHERRY_TREE_KEY, Feature.TREE,
                blossomTree(Blocks.CHERRY_LOG, Blocks.CHERRY_LEAVES, 0.1F));

        // Royal Forest: Paulownia with cherry-style trunk and ornamental foliage
        register(context, PAULOWNIA_TREE_KEY, Feature.TREE,
                blossomTree(Blocks.OAK_LOG, NTBlocks.PAULOWNIA_LEAVES.get(), 0.08F));

        // Olive tree with hanging olive fruit decorator
        register(context, OLIVE_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new MyrtleTrunkPlacer(3, 1, 1),
                BlockStateProvider.simple(NTBlocks.OLIVE_LEAVES.get()),
                new MyrtleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                new TwoLayersFeatureSize(1, 0, 1))
                .ignoreVines()
                .decorators(List.of(
                        new AttachedToLeavesDecorator(0.25F, 1, 0,
                                BlockStateProvider.simple(NTBlocks.OLIVE_HANGING.get().defaultBlockState()),
                                1, List.of(Direction.DOWN))
                )).build());

        // Dry scrubby shrubs for Maquis understorey
        register(context, MAQUIS_SHRUB_KEY, Feature.TREE,
                shrub(Blocks.OAK_LOG, Blocks.OAK_LEAVES));

        register(context, TEAK_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(NTBlocks.TEAK.log.get()),
                new FancyTrunkPlacer(10, 4, 4),
                BlockStateProvider.simple(NTBlocks.TEAK.leaves.get()),
                new FancyFoliagePlacer(ConstantInt.of(3), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(TrunkVineDecorator.INSTANCE, new LeaveVineDecorator(0.25F), new CocoaDecorator(0.2F))).build());

        register(context, BALSA_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(NTBlocks.BALSA.log.get()),
                new ForkingTrunkPlacer(8, 3, 3),
                BlockStateProvider.simple(NTBlocks.BALSA.leaves.get()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(TrunkVineDecorator.INSTANCE, new LeaveVineDecorator(0.15F))).build());

        register(context, MAHOGANY_SHRUB_KEY, Feature.TREE,
                shrub(NTBlocks.MAHOGANY.log.get(), NTBlocks.MAHOGANY.leaves.get()));

        // Andean Hills: Twisted azalea tree with vine decorators
        register(context, ANDEAN_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.DARK_OAK_LOG),
                new BendingTrunkPlacer(2, 1, 1, 3, ConstantInt.of(1)),
                BlockStateProvider.simple(Blocks.AZALEA_LEAVES),
                new RandomSpreadFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), ConstantInt.of(2), 50),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(new BeehiveDecorator(0.20F), new TrunkVineDecorator(), new LeaveVineDecorator(0.25F)))
                .ignoreVines().build());

        // Fungal Reach: Canopy tree with spore blossoms and cocoa
        register(context, FUNGAL_CANOPY_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.JUNGLE_LOG),
                new FungalReachTrunkPlacer(10, 2, 2),
                BlockStateProvider.simple(Blocks.JUNGLE_LEAVES),
                new BlobFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(
                        // Lowered vine chance slightly to reduce "popping"
                        new LeaveVineDecorator(0.20F),
                        new CocoaDecorator(0.1F), // Fungal biomes don't usually need many cocoa beans
                        new AttachedToLeavesDecorator(
                                0.25F, // Increased to make the "spore clouds" more prominent
                                1, 0,
                                BlockStateProvider.simple(Blocks.SPORE_BLOSSOM),
                                2, // Increased attempts to ensure they find a spot
                                List.of(Direction.DOWN))
                ))
                // This is important: ignoreVines() tells the tree
                // not to let vines block the growth of branches.
                .ignoreVines()
                .build());

        // Caldera Grotto: Forked jungle tree with lush cave feel
        register(context, GROTTO_TREE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.JUNGLE_LOG),
                new ForkingTrunkPlacer(7, 3, 3),
                BlockStateProvider.simple(Blocks.JUNGLE_LEAVES),
                new MegaJungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(List.of(
                        new TrunkVineDecorator(),
                        new LeaveVineDecorator(0.25F),
                        new CocoaDecorator(0.2F),
                        new AttachedToLeavesDecorator(0.1F, 1, 0,
                                BlockStateProvider.simple(Blocks.SPORE_BLOSSOM.defaultBlockState()),
                                1, List.of(Direction.DOWN))
                )).ignoreVines().build());

        register(context, TALL_OAK_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new StraightTrunkPlacer(6, 3, 2),
                BlockStateProvider.simple(Blocks.OAK_LEAVES),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                // Limit 1 is safe, but 0 is even safer for dense forests
                new TwoLayersFeatureSize(1, 1, 1))
                .forceDirt()
                .build());

        register(context, SLENDER_DARK_OAK, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.DARK_OAK_LOG),
                new StraightTrunkPlacer(8, 4, 2),
                BlockStateProvider.simple(Blocks.DARK_OAK_LEAVES),
                new PineFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), ConstantInt.of(4)),
                // FIX: Changed (1, 1, 2) to (1, 0, 1) to allow closer neighbors
                new TwoLayersFeatureSize(1, 1, 1))
                .forceDirt()
                .build());

        // Bluebell Woods: mostly tall slim birches, with some oaks (several can hold bee nests)
        register(context, BLUEBELL_TREES, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(List.of(
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(TreeFeatures.OAK_BEES_002)), 0.18F),
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(TreeFeatures.FANCY_OAK_BEES_002)), 0.12F),
                new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configured.getOrThrow(TreeFeatures.BIRCH_BEES_002)), 0.2F)),
                PlacementUtils.inlinePlaced(configured.getOrThrow(HIGHLAND_BIRCH_KEY))));

        // Young hazel-like bushes
        register(context, BLUEBELL_SHRUB, Feature.TREE, shrub(Blocks.OAK_LOG, Blocks.OAK_LEAVES));
    }

    // ----------------------------------------------------------------------------------------
    // Shapes used by more than one tree
    // ----------------------------------------------------------------------------------------

    /** One log high with a low, bushy top. */
    private static TreeConfiguration shrub(Block log, Block leaves) {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(log),
                new StraightTrunkPlacer(1, 0, 0),
                BlockStateProvider.simple(leaves),
                new BushFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 2),
                new TwoLayersFeatureSize(0, 0, 0))
                .build();
    }

    /** Cherry-shaped tree (myrtle, paulownia, cherry). */
    private static TreeConfiguration blossomTree(Block log, Block leaves, float beehiveChance) {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(log),
                new CherryTrunkPlacer(7, 1, 0, ConstantInt.of(3), ConstantInt.of(2), UniformInt.of(-2, 0), ConstantInt.of(1)),
                BlockStateProvider.simple(leaves),
                new CherryFoliagePlacer(ConstantInt.of(4), ConstantInt.of(0), ConstantInt.of(5), 0.25f, 0.5f, 0.16666667f, 0.33333334f),
                new TwoLayersFeatureSize(1, 0, 2))
                .ignoreVines()
                .decorators(List.of(new BeehiveDecorator(beehiveChance)))
                .build();
    }

    /** Tall, kinked autumn pine that turns the ground to podzol. */
    private static TreeConfiguration autumnPine(Block leaves, int height, int extraHeight, int minFoliage, int maxFoliage) {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.SPRUCE_LOG),
                new KinkedPineTrunkPlacer(height, extraHeight, 4),
                BlockStateProvider.simple(leaves),
                new KinkedFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), UniformInt.of(minFoliage, maxFoliage)),
                new TwoLayersFeatureSize(1, 0, 2))
                .decorators(List.of(
                        new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL.defaultBlockState())),
                        new BeehiveDecorator(0.05F)))
                .build();
    }

    private static TreeConfiguration smallPine(Block leaves) {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.SPRUCE_LOG),
                new StraightTrunkPlacer(7, 2, 1),
                BlockStateProvider.simple(leaves),
                new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)),
                new TwoLayersFeatureSize(1, 0, 1))
                .ignoreVines()
                .build();
    }
}
