package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.worldgen.tree.*;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.foliageplacers.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.*;
import net.minecraft.world.level.levelgen.feature.trunkplacers.*;
import java.util.List;

import static net.candyperfumeboy.novamterram.worldgen.NTConfiguredFeatures.*;

/** Grass, flowers, mushrooms and other plant patches. The keys are listed in NTConfiguredFeatures. */
final class NTConfiguredPlants {
    static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {

        // A single stalk, 7 to 11 tall, with leaves at the top
        register(context, TALL_HIGHLAND_BAMBOO, Feature.BLOCK_COLUMN, new BlockColumnConfiguration(
                List.of(
                        BlockColumnConfiguration.layer(UniformInt.of(4, 8), bamboo(BambooLeaves.NONE)),
                        BlockColumnConfiguration.layer(ConstantInt.of(1), bamboo(BambooLeaves.SMALL)),
                        BlockColumnConfiguration.layer(ConstantInt.of(2), bamboo(BambooLeaves.LARGE))),
                Direction.UP,
                BlockPredicate.ONLY_IN_AIR_PREDICATE,
                true)); // if there isn't room, keep the leafy tip

        // Silver Undergrowth (White Flowers)
        register(context, SILVER_FOREST_FLOWERS_KEY, Feature.FLOWER,
                patch(32, 7, 3, mix()
                        .add(Blocks.LILY_OF_THE_VALLEY.defaultBlockState(), 2)
                        .add(Blocks.WHITE_TULIP.defaultBlockState(), 1)));

        register(context, FOREST_SHRUBS, Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.DEAD_BUSH)));

        register(context, AEGEAN_CORAL, Feature.RANDOM_PATCH,
                patch(20, 5, 3, BlockStateProvider.simple(Blocks.TUBE_CORAL_FAN)));

        register(context, MYRTLE_GRASS_CONFIG, Feature.RANDOM_PATCH,
                patch(32, 7, 3, BlockStateProvider.simple(Blocks.SHORT_GRASS)));

        register(context, HEATH_GRASS, Feature.RANDOM_PATCH,
                patch(96, 7, 3, BlockStateProvider.simple(Blocks.SHORT_GRASS)));

        register(context, BLUE_GROVE_GRASS, Feature.RANDOM_PATCH,
                patch(48, 7, 3, mix()
                        .add(Blocks.SHORT_GRASS.defaultBlockState(), 80)
                        .add(Blocks.FERN.defaultBlockState(), 20)));

        register(context, RAINFOREST_GRASS, Feature.RANDOM_PATCH,
                patch(96, 7, 2, mix()
                        .add(Blocks.SHORT_GRASS.defaultBlockState(), 70)
                        .add(Blocks.FERN.defaultBlockState(), 30)));

        register(context, ANDEAN_GRASS, Feature.RANDOM_PATCH,
                patch(24, 5, 2, BlockStateProvider.simple(Blocks.SHORT_GRASS)));

        register(context, MEDITERRANEAN_GRASS, Feature.RANDOM_PATCH,
                patch(48, 7, 3, BlockStateProvider.simple(Blocks.SHORT_GRASS)));

        register(context, AUTUMNAL_VEGETATION_CONFIG, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(32, 7, 3,
                        PlacementUtils.filtered(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(new WeightedStateProvider(
                                        SimpleWeightedRandomList.<BlockState>builder()
                                                .add(Blocks.SHORT_GRASS.defaultBlockState(), 6)
                                                .add(Blocks.FERN.defaultBlockState(), 4)
                                                .add(Blocks.LARGE_FERN.defaultBlockState(), 1)
                                                .add(Blocks.TALL_GRASS.defaultBlockState(), 2)
                                                .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 1))),
                                BlockPredicate.ONLY_IN_AIR_PREDICATE)));

        // Moss carpet will happily sit on top of grass and flowers (and look like it's floating),
        // so every plant in this mix has to have a solid block under it.
        register(context, ANDEAN_VEGETATION_PATCH, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(64, 8, 3,
                        PlacementUtils.filtered(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(new WeightedStateProvider(mix()
                                        .add(Blocks.SHORT_GRASS.defaultBlockState(), 40)
                                        .add(Blocks.DEAD_BUSH.defaultBlockState(), 30)
                                        .add(NTBlocks.BROMELIAD.get().defaultBlockState(), 7)
                                        .add(Blocks.CORNFLOWER.defaultBlockState(), 5)
                                        .add(Blocks.MOSS_CARPET.defaultBlockState(), 15))),
                                BlockPredicate.allOf(
                                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                                        BlockPredicate.hasSturdyFace(new Vec3i(0, -1, 0), Direction.UP)))));

        register(context, SAFFRON_VEGETATION, Feature.RANDOM_PATCH,
                patch(96, 7, 3, mix()
                        .add(Blocks.SHORT_GRASS.defaultBlockState(), 85)
                        .add(Blocks.FERN.defaultBlockState(), 10)
                        .add(Blocks.ORANGE_TULIP.defaultBlockState(), 5)));

        register(context, AMBER_SANDS_VEGETATION, Feature.RANDOM_PATCH,
                patch(32, 7, 3, mix()
                        .add(Blocks.DEAD_BUSH.defaultBlockState(), 50)
                        .add(Blocks.FERN.defaultBlockState(), 20)));

        register(context, FUNGAL_REACH_VEGETATION, Feature.RANDOM_PATCH,
                patch(80, 7, 3, mix()
                        .add(Blocks.FERN.defaultBlockState(), 40)
                        .add(Blocks.RED_MUSHROOM.defaultBlockState(), 20)
                        .add(Blocks.BROWN_MUSHROOM.defaultBlockState(), 20)
                        .add(Blocks.SHORT_GRASS.defaultBlockState(), 20)));

        register(context, GROTTO_VEGETATION, Feature.RANDOM_PATCH,
                patch(96, 10, 3, mix()
                        .add(Blocks.SHORT_GRASS.defaultBlockState(), 50)
                        .add(Blocks.FERN.defaultBlockState(), 30)
                        .add(Blocks.LARGE_FERN.defaultBlockState(), 10)
                        .add(Blocks.PINK_PETALS.defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 3), 10)
                        .add(Blocks.BLUE_ORCHID.defaultBlockState(), 5)
                        .add(Blocks.SPORE_BLOSSOM.defaultBlockState(), 2)));

        // so the two placed variants actually differ visually
        register(context, AUTUMNAL_MUSHROOM_PATCH, Feature.RANDOM_PATCH,
                patch(32, 7, 3, mix()
                        .add(Blocks.BROWN_MUSHROOM.defaultBlockState(), 2)
                        .add(Blocks.RED_MUSHROOM.defaultBlockState(), 1)));

        register(context, FEN_MUSHROOM_PATCH, Feature.RANDOM_PATCH,
                patch(16, 4, 2, mix()
                        .add(Blocks.BROWN_MUSHROOM.defaultBlockState(), 2)
                        .add(Blocks.RED_MUSHROOM.defaultBlockState(), 1)));

        register(context, MYRTLE_FLOWERS_CONFIG, Feature.FLOWER,
                patch(64, 6, 2, mix()
                        .add(Blocks.BLUE_ORCHID.defaultBlockState(), 2)
                        .add(Blocks.ALLIUM.defaultBlockState(), 1)
                        .add(Blocks.OXEYE_DAISY.defaultBlockState(), 1)));

        register(context, AUTUMNAL_FLOWERS_CONFIG, Feature.FLOWER,
                patch(64, 6, 2, mix()
                        .add(Blocks.ORANGE_TULIP.defaultBlockState(), 2)
                        .add(Blocks.WHITE_TULIP.defaultBlockState(), 1)
                        .add(Blocks.CORNFLOWER.defaultBlockState(), 1)
                        .add(Blocks.ALLIUM.defaultBlockState(), 1)));

        register(context, ROYAL_FLOWER_PATCH_KEY, Feature.FLOWER,
                patch(48, 6, 2, mix()
                        .add(Blocks.POPPY.defaultBlockState(), 2)
                        .add(Blocks.DANDELION.defaultBlockState(), 1)));

        register(context, BLUE_GROVE_FLOWER_PATCH, Feature.FLOWER,
                patch(48, 6, 2, mix()
                        .add(NTBlocks.ASTER.get().defaultBlockState(), 4)
                        .add(NTBlocks.TRILLIUM.get().defaultBlockState(), 3)
                        .add(NTBlocks.THISTLE.get().defaultBlockState(), 1)));

        register(context, SILVER_SAGE_PATCH, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(NTBlocks.SILVER_SAGE.get()))));

        register(context, BLEEDING_HEART_PATCH, Feature.RANDOM_PATCH,
                patch(32, 5, 2, BlockStateProvider.simple(NTBlocks.BLEEDING_HEART.get())));

        register(context, IRIS_PATCH, Feature.RANDOM_PATCH,
                patch(64, 6, 2, BlockStateProvider.simple(NTBlocks.IRIS.get().defaultBlockState())));

        register(context, FLORAL_FAIRY_RING, Feature.RANDOM_PATCH,
                patch(48, 8, 3, BlockStateProvider.simple(Blocks.PINK_PETALS.defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, 4))));

        register(context, ALLIUM_PATCH, Feature.RANDOM_PATCH,
                patch(64, 5, 3, BlockStateProvider.simple(Blocks.ALLIUM)));

        register(context, OXEYE_DAISY_PATCH, Feature.RANDOM_PATCH,
                patch(32, 7, 3, BlockStateProvider.simple(Blocks.OXEYE_DAISY)));

        register(context, CYPRESS_LILY_PATCH, Feature.RANDOM_PATCH,
                patch(20, 7, 3, BlockStateProvider.simple(Blocks.LILY_PAD)));

        register(context, HEATHER_PATCH, Feature.RANDOM_PATCH,
                patch(64, 6, 2, BlockStateProvider.simple(NTBlocks.HEATHER.get())));

        register(context, HEATHER_PETALS_PATCH, Feature.FLOWER,
                patch(64, 6, 2, petals(NTBlocks.HEATHER_PETALS.get())));

        register(context, LAVENDER_PATCH, Feature.RANDOM_PATCH,
                patch(64, 6, 2, BlockStateProvider.simple(NTBlocks.LAVENDER.get())));

        register(context, LAVENDER_PETALS_PATCH, Feature.FLOWER,
                patch(64, 6, 2, petals(NTBlocks.LAVENDER_PETALS.get())));

        register(context, ASTER_PETALS_PATCH, Feature.FLOWER,
                patch(64, 7, 3, petals(NTBlocks.ASTER_PETALS.get())));

        register(context, AUTUMNAL_PUMPKIN_PATCH_CONFIG, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(16, 4, 2,
                        PlacementUtils.filtered(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(new WeightedStateProvider(
                                        SimpleWeightedRandomList.<BlockState>builder()
                                                .add(Blocks.PUMPKIN.defaultBlockState(), 10)
                                                .add(Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, Direction.NORTH), 1)
                                                .build())),
                                BlockPredicate.allOf(
                                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                                        BlockPredicate.matchesTag(new Vec3i(0, -1, 0), BlockTags.DIRT)))));

        // Fallen leaf carpets. Where they appear is set in NTPlacedPlants (only under their own trees).
        register(context, AUTUMNAL_LEAF_PILE_KEY, Feature.RANDOM_PATCH,
                patch(24, 4, 2, leafCarpet(NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET.get(),
                        NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET.get())));

        register(context, MYRTLE_LEAF_PILE_KEY, Feature.RANDOM_PATCH,
                patch(32, 4, 2, leafCarpet(NTBlocks.MYRTLE_LEAF_CARPET.get())));

        register(context, PAULOWNIA_LEAF_PILE_KEY, Feature.RANDOM_PATCH,
                patch(24, 4, 2, leafCarpet(NTBlocks.PAULOWNIA_LEAF_CARPET.get())));
    }

    // ----------------------------------------------------------------------------------------
    // Helpers: patch(tries, sideways spread, up/down spread, what to place)
    // ----------------------------------------------------------------------------------------

    private static RandomPatchConfiguration patch(int tries, int xzSpread, int ySpread, BlockStateProvider blocks) {
        return new RandomPatchConfiguration(tries, xzSpread, ySpread,
                PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(blocks)));
    }

    /** Same as above for a weighted mix of blocks. */
    private static RandomPatchConfiguration patch(int tries, int xzSpread, int ySpread,
                                                  SimpleWeightedRandomList.Builder<BlockState> mix) {
        return patch(tries, xzSpread, ySpread, new WeightedStateProvider(mix));
    }

    private static BlockStateProvider bamboo(BambooLeaves leaves) {
        return BlockStateProvider.simple(Blocks.BAMBOO.defaultBlockState()
                .setValue(BambooStalkBlock.LEAVES, leaves)
                .setValue(BambooStalkBlock.AGE, 1));
    }

    private static SimpleWeightedRandomList.Builder<BlockState> mix() {
        return SimpleWeightedRandomList.builder();
    }

    /** Petal blocks with 1 to 4 flowers; fewer flowers is more common. */
    private static SimpleWeightedRandomList.Builder<BlockState> petals(Block block) {
        SimpleWeightedRandomList.Builder<BlockState> mix = mix();
        for (int amount = 1; amount <= 4; amount++) {
            mix.add(block.defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, amount), 5 - amount);
        }
        return mix;
    }

    /** Leaf carpets facing every direction with 1 to 4 clumps, so they don't look copy-pasted. */
    private static SimpleWeightedRandomList.Builder<BlockState> leafCarpet(Block... carpets) {
        SimpleWeightedRandomList.Builder<BlockState> mix = mix();
        for (Block carpet : carpets) {
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                for (int amount = 1; amount <= 4; amount++) {
                    mix.add(carpet.defaultBlockState()
                            .setValue(PinkPetalsBlock.FACING, facing)
                            .setValue(PinkPetalsBlock.AMOUNT, amount), 1);
                }
            }
        }
        return mix;
    }
}
