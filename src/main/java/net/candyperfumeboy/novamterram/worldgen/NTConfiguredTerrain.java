package net.candyperfumeboy.novamterram.worldgen;

import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
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
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.foliageplacers.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.*;
import net.minecraft.world.level.levelgen.feature.trunkplacers.*;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import java.util.List;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import java.util.ArrayList;

import static net.candyperfumeboy.novamterram.worldgen.NTConfiguredFeatures.*;

/** Ores, boulders, logs, water features, spires and dens. The keys are listed in NTConfiguredFeatures. */
final class NTConfiguredTerrain {
    static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stone = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslate = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        register(context, OLIVINE_SAND_PATCH_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(NTBlocks.OLIVINE_SAND.get()))));

        register(context, BLUE_GROVE_SNOW_PATCH, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(32, 7, 3,
                        PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.SNOW.defaultBlockState())))));

        register(context, FOREST_ROOTED_DIRT_PATCH, Feature.VEGETATION_PATCH,
                new VegetationPatchConfiguration(
                        BlockTags.DIRT,
                        BlockStateProvider.simple(Blocks.ROOTED_DIRT),
                        // Fix 1: Use PlacementUtils.inlinePlaced to wrap NO_OP
                        PlacementUtils.inlinePlaced(Feature.NO_OP, FeatureConfiguration.NONE),
                        CaveSurface.FLOOR,
                        // Fix 2: Use actual IntProviders, not their codecs
                        UniformInt.of(3, 6), // Radius
                        0.5f,
                        1,
                        0.0f,
                        ConstantInt.of(1),    // Extra thickness (switched to ConstantInt for simplicity)
                        0.8f
                ));

        // Mirror Mire: Blue ice disk patches forming frozen pools
        register(context, MIRROR_ICE_PATCH, Feature.DISK,
                new DiskConfiguration(
                        new RuleBasedBlockStateProvider(BlockStateProvider.simple(Blocks.BLUE_ICE.defaultBlockState()), List.of()),
                        BlockPredicate.matchesBlocks(List.of(Blocks.WATER, Blocks.DIRT, Blocks.MUD)),
                        UniformInt.of(3, 6), 2));

        // Heath / Royal Forest: Small delta-style mud ponds
        register(context, HEATH_POND_CONFIG, Feature.DELTA_FEATURE,
                new DeltaFeatureConfiguration(Blocks.WATER.defaultBlockState(), Blocks.MUD.defaultBlockState(),
                        UniformInt.of(3, 7), UniformInt.of(0, 2)));

        // Caldera Grotto custom features
        register(context, PERIDOT_SPRING_CONFIGURED, NTFeatures.PERIDOT_SPRING.get(), NoneFeatureConfiguration.INSTANCE);

        register(context, GEOTHERMAL_VENT_CONFIGURED, NTFeatures.GEOTHERMAL_VENT.get(), NoneFeatureConfiguration.INSTANCE);

        register(context, CALCITE_BOULDER,          Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.CALCITE.defaultBlockState()));

        register(context, TUFF_BOULDER,             Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.TUFF.defaultBlockState()));

        register(context, AUTUMNAL_BOULDER_CONFIG,  Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.MOSSY_COBBLESTONE.defaultBlockState()));

        register(context, PUMPKIN_CLUSTER_CONFIG,   Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.PUMPKIN.defaultBlockState()));

        register(context, MOSSY_COBBLESTONE_BOULDER, Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.MOSSY_COBBLESTONE.defaultBlockState()));

        // Both diorite variants use Blocks.DIORITE — DIORITE_ROCK_B is a second scatter variant
        // so placement can be tuned independently (e.g. different rarity/heightmap in PlacedFeatures)
        register(context, DIORITE_MAGICAL_ROCK,     Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.DIORITE.defaultBlockState()));

        register(context, DIORITE_ROCK_B,           Feature.FOREST_ROCK, new BlockStateConfiguration(Blocks.DIORITE.defaultBlockState()));

        // Cypress Fen: Muddy mangrove root knees growing from wet ground
        register(context, CYPRESS_KNEE_CONFIGURED, Feature.BLOCK_COLUMN,
                new BlockColumnConfiguration(List.of(
                        BlockColumnConfiguration.layer(UniformInt.of(1, 2),
                                SimpleStateProvider.simple(Blocks.MUDDY_MANGROVE_ROOTS.defaultBlockState()))),
                        Direction.UP,
                        BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER),
                        true));

        // Mirror Mire: Frosted sugar cane reeds along frozen banks
        register(context, FROSTED_REEDS_CONFIGURED, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(20, 4, 0,
                        PlacementUtils.inlinePlaced(Feature.BLOCK_COLUMN,
                                BlockColumnConfiguration.simple(UniformInt.of(2, 3),
                                        BlockStateProvider.simple(Blocks.SUGAR_CANE)),
                                // only where sugar cane could really grow (soil beside water)
                                BlockPredicateFilter.forPredicate(BlockPredicate.allOf(
                                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                                        BlockPredicate.wouldSurvive(Blocks.SUGAR_CANE.defaultBlockState(), BlockPos.ZERO))))));

        // Mirror Mire: Half-submerged rotting log
        register(context, SUNKEN_LOG_CONFIGURED, Feature.BLOCK_COLUMN,
                new BlockColumnConfiguration(List.of(
                        BlockColumnConfiguration.layer(UniformInt.of(3, 5),
                                new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                        .add(Blocks.STRIPPED_OAK_LOG.defaultBlockState()
                                                .setValue(RotatedPillarBlock.AXIS, Direction.Axis.X), 1)
                                        .build()))),
                        Direction.EAST,
                        BlockPredicate.matchesBlocks(List.of(Blocks.AIR, Blocks.WATER, Blocks.SNOW)),
                        true));

        // Blue Grove: Fallen aspen log lying on the forest floor
        register(context, FALLEN_ASPEN_LOG_KEY, Feature.BLOCK_COLUMN,
                new BlockColumnConfiguration(List.of(
                        BlockColumnConfiguration.layer(ConstantInt.of(3),
                                BlockStateProvider.simple(NTBlocks.ASPEN.log.get().defaultBlockState()
                                        .setValue(RotatedPillarBlock.AXIS, Direction.Axis.X)))),
                        Direction.EAST, BlockPredicate.replaceable(), true));

        register(context, RED_PEAK_SPIRE_KEY,  NTFeatures.ANDEAN_SPIRE.get(), NoneFeatureConfiguration.INSTANCE);

        register(context, ANDEAN_SPIRE_KEY,    NTFeatures.ANDEAN_SPIRE.get(), NoneFeatureConfiguration.INSTANCE);

        register(context, SILVER_FOX_DEN_KEY,  NTFeatures.SILVER_FOX_DEN.get(), NoneFeatureConfiguration.INSTANCE);

        register(context, ANDEAN_BEAR_DEN_KEY, NTFeatures.BEAR_DEN.get(), NoneFeatureConfiguration.INSTANCE);

        register(context, CLIFF_SPIKE_KEY,     NTFeatures.SPIKE_FEATURE.get(), NoneFeatureConfiguration.INSTANCE);

        register(context, OVERWORLD_LIGNITE_ORE_KEY, Feature.ORE,
                new OreConfiguration(List.of(
                        OreConfiguration.target(stone,     NTBlocks.LIGNITE_ORE.get().defaultBlockState()),
                        OreConfiguration.target(deepslate, NTBlocks.DEEPSLATE_LIGNITE_ORE.get().defaultBlockState())), 9));

        register(context, NETHER_LIGNITE_ORE_KEY, Feature.ORE,
                new OreConfiguration(new BlockMatchTest(Blocks.NETHERRACK),
                        NTBlocks.LIGNITE_NETHER_ORE.get().defaultBlockState(), 9));

        register(context, END_LIGNITE_ORE_KEY, Feature.ORE,
                new OreConfiguration(new BlockMatchTest(Blocks.END_STONE),
                        NTBlocks.LIGNITE_END_ORE.get().defaultBlockState(), 9));

        register(context, OPAL_ORE_KEY, Feature.ORE,
                new OreConfiguration(stone, NTBlocks.OPAL_ORE.get().defaultBlockState(), 12));

        // Bluebell Woods: fallen birch logs, lying either way
        register(context, BLUEBELL_FALLEN_LOG, Feature.SIMPLE_RANDOM_SELECTOR, new SimpleRandomFeatureConfiguration(
                HolderSet.direct(fallenBirch(Direction.EAST), fallenBirch(Direction.SOUTH))));

        // Bluebell Woods: the flower floor and the ancient oak with its fairy ring (see biome/features)
        register(context, BLUEBELL_FLOOR, NTFeatures.BLUEBELL_FLOOR.get(), NoneFeatureConfiguration.INSTANCE);
        register(context, GREAT_OAK_RING, NTFeatures.GREAT_OAK_RING.get(), NoneFeatureConfiguration.INSTANCE);
    }

    /** A 3 block birch log lying in this direction, only where all of it has soil underneath. */
    private static Holder<PlacedFeature> fallenBirch(Direction way) {
        List<BlockPredicate> checks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Vec3i along = new Vec3i(way.getStepX() * i, 0, way.getStepZ() * i);
            checks.add(BlockPredicate.replaceable(along));
            checks.add(BlockPredicate.matchesTag(along.below(), BlockTags.DIRT));
        }
        return PlacementUtils.inlinePlaced(Feature.BLOCK_COLUMN,
                new BlockColumnConfiguration(List.of(
                        BlockColumnConfiguration.layer(ConstantInt.of(3),
                                BlockStateProvider.simple(Blocks.BIRCH_LOG.defaultBlockState()
                                        .setValue(RotatedPillarBlock.AXIS, way.getAxis())))),
                        way, BlockPredicate.replaceable(), true),
                BlockPredicateFilter.forPredicate(BlockPredicate.allOf(checks)));
    }
}
