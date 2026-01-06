package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class NTItemTagProvider extends ItemTagsProvider {
    public NTItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                             CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, NovamTerram.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // --- WOOD TAGS ---
        this.tag(ItemTags.LOGS_THAT_BURN)
                .add(NTBlocks.BLUE_SPRUCE_LOG.get().asItem())
                .add(NTBlocks.BLUE_SPRUCE_WOOD.get().asItem())
                .add(NTBlocks.STRIPPED_BLUE_SPRUCE_LOG.get().asItem())
                .add(NTBlocks.STRIPPED_BLUE_SPRUCE_WOOD.get().asItem());

        this.tag(ItemTags.PLANKS)
                .add(NTBlocks.BLUE_SPRUCE_PLANKS.asItem());

        // --- SIGN TAGS ---
        this.tag(ItemTags.SIGNS)
                .add(NTItems.BLUE_SPRUCE_SIGN_ITEM.get());

        this.tag(ItemTags.HANGING_SIGNS)
                .add(NTItems.BLUE_SPRUCE_HANGING_SIGN_ITEM.get());

        // --- BOAT TAGS ---
        this.tag(ItemTags.BOATS)
                .add(NTItems.BLUE_SPRUCE_BOAT.get());

        this.tag(ItemTags.CHEST_BOATS)
                .add(NTItems.BLUE_SPRUCE_CHEST_BOAT.get());

        // --- MISC TAGS ---
        this.tag(ItemTags.LEAVES)
                .add(NTBlocks.BLUE_SPRUCE_LEAVES.get().asItem());

        this.tag(ItemTags.SAPLINGS)
                .add(NTBlocks.BLUE_SPRUCE_SAPLING.get().asItem());
    }
}