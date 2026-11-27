package net.candyperfumeboy.novamterram.datagen;

import java.util.List;
import net.candyperfumeboy.novamterram.block.NTWoodSet;
import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class NTBlockTagProvider extends BlockTagsProvider {
    public NTBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NovamTerram.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // --- PICKAXE MINEABLE ---
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(NTBlocks.LIGNITE_BLOCK.get(), NTBlocks.OPAL_BLOCK.get(), NTBlocks.OPAL_ORE.get(),
                        NTBlocks.RHODOCHROSITE_BLOCK.get(), NTBlocks.RHODOCHROSITE_ORE.get(),
                        NTBlocks.KYANITE_BLOCK.get(), NTBlocks.KYANITE_ORE.get(),
                        NTBlocks.IOLITE_BLOCK.get(), NTBlocks.IOLITE_ORE.get(),
                        NTBlocks.JADEITE_BLOCK.get(), NTBlocks.JADEITE_ORE.get(),
                        NTBlocks.DEEPSLATE_LIGNITE_ORE.get(), NTBlocks.LIGNITE_ORE.get(),
                        NTBlocks.LIGNITE_END_ORE.get(), NTBlocks.LIGNITE_NETHER_ORE.get(),
                        NTBlocks.CUT_OLIVINE_SANDSTONE.get(), NTBlocks.OLIVINE_SANDSTONE.get(),
                        NTBlocks.CHISELED_OLIVINE_SANDSTONE.get(), NTBlocks.OLIVINE_SANDSTONE_STAIRS.get(),
                        NTBlocks.OLIVINE_SANDSTONE_SLAB.get(), NTBlocks.OLIVINE_SANDSTONE_WALL.get(),
                        NTBlocks.PETRIFIED_CACTUS.get(),
                        NTBlocks.AMBER_SANDSTONE.get(), NTBlocks.CUT_AMBER_SANDSTONE.get(),
                        NTBlocks.CHISELED_AMBER_SANDSTONE.get(), NTBlocks.AMBER_SANDSTONE_STAIRS.get(),
                        NTBlocks.AMBER_SANDSTONE_SLAB.get(), NTBlocks.AMBER_SANDSTONE_WALL.get(),
                        NTBlocks.WHITE_SANDSTONE.get(), NTBlocks.CUT_WHITE_SANDSTONE.get(),
                        NTBlocks.CHISELED_WHITE_SANDSTONE.get(), NTBlocks.WHITE_SANDSTONE_STAIRS.get(),
                        NTBlocks.WHITE_SANDSTONE_SLAB.get(), NTBlocks.WHITE_SANDSTONE_WALL.get(),
                        NTBlocks.OLIVINE_SANDSTONE_FENCE.get(), NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE.get(),
                        NTBlocks.OLIVINE_SANDSTONE_DOOR.get(), NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR.get(),
                        NTBlocks.OLIVINE_SANDSTONE_PRESSURE_PLATE.get(), NTBlocks.OLIVINE_SANDSTONE_BUTTON.get());

        // --- SHOVEL MINEABLE ---
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(NTBlocks.OLIVINE_SAND.get(), NTBlocks.BLUE_PODZOL.get(),
                        NTBlocks.SAFFRON_MOSS.get(), NTBlocks.AMBER_SAND.get(),
                        NTBlocks.WHITE_SAND.get());

        // --- HOE MINEABLE ---
        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(NTBlocks.ORANGE_AUTUMNAL_LEAVES.get(), NTBlocks.YELLOW_AUTUMNAL_LEAVES.get(),
                        NTBlocks.PAULOWNIA_LEAVES.get(), NTBlocks.WHITE_MYRTLE_LEAVES.get(),
                        NTBlocks.RED_MYRTLE_LEAVES.get(),NTBlocks.PURPLE_MYRTLE_LEAVES.get(),
                        NTBlocks.OLIVE_LEAVES.get(), NTBlocks.OLIVE_HANGING.get());

        // --- TOOL LEVELS ---
        tag(BlockTags.NEEDS_IRON_TOOL).add(NTBlocks.DEEPSLATE_LIGNITE_ORE.get(), NTBlocks.LIGNITE_END_ORE.get(),
                NTBlocks.LIGNITE_NETHER_ORE.get(), NTBlocks.OPAL_ORE.get(), NTBlocks.OPAL_BLOCK.get(),
                NTBlocks.IOLITE_ORE.get(), NTBlocks.IOLITE_BLOCK.get(), NTBlocks.KYANITE_ORE.get(), NTBlocks.KYANITE_BLOCK.get());
        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(NTBlocks.JADEITE_ORE.get(), NTBlocks.JADEITE_BLOCK.get(),
                NTBlocks.RHODOCHROSITE_ORE.get(), NTBlocks.RHODOCHROSITE_BLOCK.get());

        // --- SAND & DIRT ---
        tag(BlockTags.SAND).add(NTBlocks.OLIVINE_SAND.get(), NTBlocks.AMBER_SAND.get(), NTBlocks.WHITE_SAND.get());
        tag(BlockTags.CAMEL_SAND_STEP_SOUND_BLOCKS).add(NTBlocks.OLIVINE_SAND.get(), NTBlocks.AMBER_SAND.get(), NTBlocks.WHITE_SAND.get());
        tag(BlockTags.DIRT).add(NTBlocks.BLUE_PODZOL.get(), NTBlocks.SAFFRON_MOSS.get());

        // --- INFRASTRUCTURE ---
        tag(BlockTags.FENCES).add(NTBlocks.OLIVINE_SANDSTONE_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE.get());
        tag(BlockTags.WALLS).add(NTBlocks.OLIVINE_SANDSTONE_WALL.get(), NTBlocks.AMBER_SANDSTONE_WALL.get(), NTBlocks.WHITE_SANDSTONE_WALL.get());
        tag(BlockTags.STAIRS).add(NTBlocks.OLIVINE_SANDSTONE_STAIRS.get(), NTBlocks.AMBER_SANDSTONE_STAIRS.get(), NTBlocks.WHITE_SANDSTONE_STAIRS.get());
        tag(BlockTags.SLABS).add(NTBlocks.OLIVINE_SANDSTONE_SLAB.get(), NTBlocks.AMBER_SANDSTONE_SLAB.get(), NTBlocks.WHITE_SANDSTONE_SLAB.get());
        tag(BlockTags.DOORS).add(NTBlocks.OLIVINE_SANDSTONE_DOOR.get());
        tag(BlockTags.TRAPDOORS).add(NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR.get());
        tag(BlockTags.PRESSURE_PLATES).add(NTBlocks.OLIVINE_SANDSTONE_PRESSURE_PLATE.get());
        tag(BlockTags.BUTTONS).add(NTBlocks.OLIVINE_SANDSTONE_BUTTON.get());

        // --- BEACON & ORES ---
        tag(BlockTags.BEACON_BASE_BLOCKS).add(NTBlocks.OPAL_BLOCK.get(), NTBlocks.IOLITE_BLOCK.get(), NTBlocks.KYANITE_BLOCK.get(), NTBlocks.JADEITE_BLOCK.get(), NTBlocks.RHODOCHROSITE_BLOCK.get());
        tag(Tags.Blocks.ORES).add(NTBlocks.OPAL_ORE.get(), NTBlocks.IOLITE_ORE.get(), NTBlocks.KYANITE_ORE.get(), NTBlocks.JADEITE_ORE.get(), NTBlocks.RHODOCHROSITE_ORE.get(), NTBlocks.LIGNITE_ORE.get(), NTBlocks.DEEPSLATE_LIGNITE_ORE.get(), NTBlocks.LIGNITE_NETHER_ORE.get(), NTBlocks.LIGNITE_END_ORE.get());
        tag(Tags.Blocks.STORAGE_BLOCKS).add(NTBlocks.OPAL_BLOCK.get(), NTBlocks.IOLITE_BLOCK.get(), NTBlocks.KYANITE_BLOCK.get(), NTBlocks.JADEITE_BLOCK.get(), NTBlocks.RHODOCHROSITE_BLOCK.get(), NTBlocks.LIGNITE_BLOCK.get());

        // --- FLORA ---
        tag(BlockTags.SMALL_FLOWERS).add(NTBlocks.HEATHER.get(), NTBlocks.PURPLE_BLUEBELL.get(), NTBlocks.BLUEBELL.get(), NTBlocks.LAVENDER.get(), NTBlocks.ASTER.get(), NTBlocks.THISTLE.get(), NTBlocks.BROMELIAD.get(), NTBlocks.TRILLIUM.get(), NTBlocks.SILVER_SAGE.get(), NTBlocks.BLEEDING_HEART.get());
        tag(BlockTags.TALL_FLOWERS).add(NTBlocks.IRIS.get());
        // Same tags vanilla gives pink petals: bees like them, trees can grow through them
        for (var tag : List.of(BlockTags.FLOWERS, BlockTags.SWORD_EFFICIENT, BlockTags.INSIDE_STEP_SOUND_BLOCKS,
                BlockTags.REPLACEABLE_BY_TREES, BlockTags.MINEABLE_WITH_HOE)) {
            tag(tag).add(NTBlocks.BLUEBELL_CLUSTER.get(), NTBlocks.PURPLE_BLUEBELL_CLUSTER.get());
        }
        tag(BlockTags.SAPLINGS).add(NTBlocks.ORANGE_AUTUMNAL_PINE_SAPLING.get(), NTBlocks.YELLOW_AUTUMNAL_PINE_SAPLING.get(), NTBlocks.PAULOWNIA_SAPLING.get(), NTBlocks.WHITE_MYRTLE_SAPLING.get(), NTBlocks.RED_MYRTLE_SAPLING.get(), NTBlocks.PURPLE_MYRTLE_SAPLING.get());
        tag(BlockTags.LEAVES).add(NTBlocks.ORANGE_AUTUMNAL_LEAVES.get(), NTBlocks.YELLOW_AUTUMNAL_LEAVES.get(), NTBlocks.PAULOWNIA_LEAVES.get(), NTBlocks.WHITE_MYRTLE_LEAVES.get(), NTBlocks.RED_MYRTLE_LEAVES.get(), NTBlocks.PURPLE_MYRTLE_LEAVES.get(), NTBlocks.OLIVE_LEAVES.get());
        tag(BlockTags.REPLACEABLE).add(NTBlocks.HEATHER_PETALS.get(), NTBlocks.LAVENDER_PETALS.get(), NTBlocks.ASTER_PETALS.get(), NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET.get(), NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET.get(), NTBlocks.PAULOWNIA_LEAF_CARPET.get(), NTBlocks.MYRTLE_LEAF_CARPET.get(), NTBlocks.OLIVE_HANGING.get(), NTBlocks.SILVER_SAGE.get(), NTBlocks.BLEEDING_HEART.get(), NTBlocks.IRIS.get());

        // --- WOOD LOOP ---
        for (NTWoodSet set : NTBlocks.WOOD_SETS) {
            tag(BlockTags.MINEABLE_WITH_AXE).add(set.log.get(), set.wood.get(), set.strippedLog.get(), set.strippedWood.get(), set.planks.get(), set.stairs.get(), set.slab.get(), set.fence.get(), set.fenceGate.get(), set.door.get(), set.trapdoor.get(), set.pressurePlate.get(), set.button.get(), set.sign.get(), set.wallSign.get(), set.hangingSign.get(), set.wallHangingSign.get());
            tag(BlockTags.LOGS_THAT_BURN).add(set.log.get(), set.wood.get(), set.strippedLog.get(), set.strippedWood.get());
            tag(BlockTags.PLANKS).add(set.planks.get());
            tag(BlockTags.SAPLINGS).add(set.sapling.get());
            tag(BlockTags.LEAVES).add(set.leaves.get());
            tag(BlockTags.MINEABLE_WITH_HOE).add(set.leaves.get());
            tag(BlockTags.FENCE_GATES).add(set.fenceGate.get());

            // The "wooden" tags make fences connect to vanilla fences and let these burn as furnace fuel
            tag(BlockTags.WOODEN_FENCES).add(set.fence.get());
            tag(BlockTags.WOODEN_STAIRS).add(set.stairs.get());
            tag(BlockTags.WOODEN_SLABS).add(set.slab.get());
            tag(BlockTags.WOODEN_DOORS).add(set.door.get());
            tag(BlockTags.WOODEN_TRAPDOORS).add(set.trapdoor.get());
            tag(BlockTags.WOODEN_PRESSURE_PLATES).add(set.pressurePlate.get());
            tag(BlockTags.WOODEN_BUTTONS).add(set.button.get());

            tag(BlockTags.STANDING_SIGNS).add(set.sign.get());
            tag(BlockTags.WALL_SIGNS).add(set.wallSign.get());
            tag(BlockTags.CEILING_HANGING_SIGNS).add(set.hangingSign.get());
            tag(BlockTags.WALL_HANGING_SIGNS).add(set.wallHangingSign.get());
        }
    }
}
