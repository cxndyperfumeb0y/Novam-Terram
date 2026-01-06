package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
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
        // --- PICKAXE MINEABLE (Stones, Ores, Sandstone) ---
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(NTBlocks.LIGNITE_BLOCK.get(),
                        NTBlocks.OPAL_BLOCK.get(),
                        NTBlocks.OPAL_ORE.get(),
                        NTBlocks.DEEPSLATE_LIGNITE_ORE.get(),
                        NTBlocks.LIGNITE_ORE.get(),
                        NTBlocks.LIGNITE_END_ORE.get(),
                        NTBlocks.LIGNITE_NETHER_ORE.get(),
                        NTBlocks.CUT_OLIVINE_SANDSTONE.get(),
                        NTBlocks.OLIVINE_SANDSTONE.get(),
                        NTBlocks.CHISELED_OLIVINE_SANDSTONE.get(),
                        NTBlocks.OLIVINE_SANDSTONE_STAIRS.get(),
                        NTBlocks.OLIVINE_SANDSTONE_SLAB.get(),
                        NTBlocks.OLIVINE_SANDSTONE_WALL.get());

        // --- AXE MINEABLE (Wood Set) ---
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(NTBlocks.BLUE_SPRUCE_LOG.get(),
                        NTBlocks.BLUE_SPRUCE_WOOD.get(),
                        NTBlocks.STRIPPED_BLUE_SPRUCE_LOG.get(),
                        NTBlocks.STRIPPED_BLUE_SPRUCE_WOOD.get(),
                        NTBlocks.BLUE_SPRUCE_PLANKS.get(),
                        NTBlocks.BLUE_SPRUCE_STAIRS.get(),
                        NTBlocks.BLUE_SPRUCE_SLAB.get(),
                        NTBlocks.BLUE_SPRUCE_FENCE.get(),
                        NTBlocks.BLUE_SPRUCE_FENCE_GATE.get(),
                        NTBlocks.BLUE_SPRUCE_DOOR.get(),
                        NTBlocks.BLUE_SPRUCE_TRAP_DOOR.get(),
                        NTBlocks.BLUE_SPRUCE_SIGN.get(),
                        NTBlocks.BLUE_SPRUCE_WALL_SIGN.get(),
                        NTBlocks.BLUE_SPRUCE_HANGING_SIGN.get(),
                        NTBlocks.BLUE_SPRUCE_WALL_HANGING_SIGN.get());

        // --- SHOVEL MINEABLE ---
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(NTBlocks.OLIVINE_SAND.get(),
                        NTBlocks.BLUE_PODZOL.get());

        // --- TOOL LEVELS ---
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(NTBlocks.DEEPSLATE_LIGNITE_ORE.get(),
                        NTBlocks.LIGNITE_END_ORE.get(),
                        NTBlocks.LIGNITE_NETHER_ORE.get());

        // --- WOOD LOGIC ---
        tag(BlockTags.LOGS_THAT_BURN)
                .add(NTBlocks.BLUE_SPRUCE_LOG.get(),
                        NTBlocks.BLUE_SPRUCE_WOOD.get(),
                        NTBlocks.STRIPPED_BLUE_SPRUCE_LOG.get(),
                        NTBlocks.STRIPPED_BLUE_SPRUCE_WOOD.get());

        tag(BlockTags.PLANKS)
                .add(NTBlocks.BLUE_SPRUCE_PLANKS.get());

        // --- SAND & DIRT ---
        tag(BlockTags.SAND).add(NTBlocks.OLIVINE_SAND.get());
        tag(BlockTags.CAMEL_SAND_STEP_SOUND_BLOCKS).add(NTBlocks.OLIVINE_SAND.get());
        tag(BlockTags.DIRT).add(NTBlocks.BLUE_PODZOL.get());

        // --- INFRASTRUCTURE (Fences, Walls, etc.) ---
        tag(BlockTags.FENCES).add(NTBlocks.OLIVINE_SANDSTONE_FENCE.get(), NTBlocks.BLUE_SPRUCE_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE.get(), NTBlocks.BLUE_SPRUCE_FENCE_GATE.get());
        tag(BlockTags.WALLS).add(NTBlocks.OLIVINE_SANDSTONE_WALL.get());

        // --- SIGNS ---
        tag(BlockTags.STANDING_SIGNS).add(NTBlocks.BLUE_SPRUCE_SIGN.get());
        tag(BlockTags.WALL_SIGNS).add(NTBlocks.BLUE_SPRUCE_WALL_SIGN.get());
        tag(BlockTags.CEILING_HANGING_SIGNS).add(NTBlocks.BLUE_SPRUCE_HANGING_SIGN.get());
        tag(BlockTags.WALL_HANGING_SIGNS).add(NTBlocks.BLUE_SPRUCE_WALL_HANGING_SIGN.get());
    }
}