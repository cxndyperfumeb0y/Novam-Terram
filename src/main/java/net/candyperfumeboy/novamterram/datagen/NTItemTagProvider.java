package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.block.NTWoodSet;
import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
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
        // ==========================================
        // COPY FROM BLOCK TAGS
        // ==========================================
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.FENCES, ItemTags.FENCES);
        copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        copy(BlockTags.WALLS, ItemTags.WALLS);
        copy(BlockTags.DOORS, ItemTags.DOORS);
        copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
        copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
        copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);

        // ==========================================
        // AEGEAN ITEMS (Olive)
        // ==========================================
        this.tag(Tags.Items.FOODS).add(NTItems.OLIVE.get());
        this.tag(Tags.Items.SEEDS).add(NTItems.OLIVE.get());
        this.tag(Tags.Items.DYES_GREEN).add(NTItems.OLIVE.get()); // Olives make green dye!
        this.tag(ItemTags.TALL_FLOWERS).add(NTBlocks.IRIS.get().asItem());


        // --- Royal Forest Dye Mappings ---
        this.tag(Tags.Items.DYES_PURPLE).add(NTBlocks.IRIS.get().asItem());
        this.tag(Tags.Items.DYES_PINK).add(NTBlocks.BLEEDING_HEART.get().asItem());
        this.tag(Tags.Items.DYES_LIGHT_GRAY).add(NTBlocks.SILVER_SAGE.get().asItem());
        // ==========================================
        // MINERALS & GEMS
        // ==========================================
        this.tag(ItemTags.COALS).add(NTItems.LIGNITE_COAL.get());

        this.tag(Tags.Items.GEMS).add(
                NTItems.OPAL.get(),
                NTItems.KYANITE.get(),
                NTItems.JADEITE.get(),
                NTItems.RHODOCHROSITE.get(),
                NTItems.IOLITE.get()
        );

        this.tag(Tags.Items.RAW_MATERIALS).add(
                NTItems.RAW_OPAL.get(),
                NTItems.RAW_KYANITE.get(),
                NTItems.RAW_JADEITE.get(),
                NTItems.RAW_RHODOCHROSITE.get(),
                NTItems.RAW_IOLITE.get()
        );

        // ==========================================
        // THE WOOD LOOP
        // ==========================================
        for (NTWoodSet set : NTBlocks.WOOD_SETS) {
            this.tag(ItemTags.LOGS_THAT_BURN).add(
                    set.log.asItem(), set.wood.asItem(), set.strippedLog.asItem(), set.strippedWood.asItem());

            this.tag(ItemTags.PLANKS).add(set.planks.asItem());
            this.tag(ItemTags.SIGNS).add(set.signItem.get());
            this.tag(ItemTags.HANGING_SIGNS).add(set.hangingSignItem.get());
            this.tag(ItemTags.BOATS).add(set.boat.get());
            this.tag(ItemTags.CHEST_BOATS).add(set.chestBoat.get());
        }

        // ==========================================
        // STANDALONE TREES & CARPETS
        // ==========================================
        this.tag(ItemTags.DECORATED_POT_INGREDIENTS).add(
                NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET.get().asItem(),
                NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET.get().asItem(),
                NTBlocks.PAULOWNIA_LEAF_CARPET.get().asItem(),
                NTBlocks.MYRTLE_LEAF_CARPET.get().asItem()
        );

        // ==========================================
        // DYES & FLORA
        // ==========================================
        this.tag(Tags.Items.DYES_PURPLE).add(
                NTBlocks.LAVENDER.get().asItem(),
                NTBlocks.LAVENDER_PETALS.get().asItem(),
                NTBlocks.THISTLE.get().asItem()
        );

        this.tag(Tags.Items.DYES_LIGHT_BLUE).add(
                NTBlocks.HEATHER.get().asItem(),
                NTBlocks.BLUEBELL.get().asItem(),
                NTBlocks.PURPLE_BLUEBELL.get().asItem(),

                NTBlocks.HEATHER_PETALS.get().asItem()
        );

        this.tag(Tags.Items.DYES_WHITE).add(
                NTBlocks.ASTER.get().asItem(),
                NTBlocks.ASTER_PETALS.get().asItem()
        );

        this.tag(Tags.Items.DYES_MAGENTA).add(
                NTBlocks.PAULOWNIA_LEAF_CARPET.get().asItem()
        );

        this.tag(Tags.Items.DYES_ORANGE).add(NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET.get().asItem());
        this.tag(Tags.Items.DYES_YELLOW).add(NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET.get().asItem());

        this.tag(Tags.Items.DYES_LIGHT_GRAY).add(NTBlocks.TRILLIUM.get().asItem());
        this.tag(Tags.Items.DYES_RED).add(NTBlocks.BROMELIAD.get().asItem());

        // Interaction example
        this.tag(ItemTags.PIGLIN_FOOD).add(NTBlocks.LAVENDER.get().asItem());

        // --- NEW BLOCKS ---
        this.tag(ItemTags.DIRT).add(NTBlocks.SAFFRON_MOSS.get().asItem());
        this.tag(ItemTags.SAND).add(NTBlocks.AMBER_SAND.get().asItem());
        this.tag(ItemTags.SAND).add(NTBlocks.WHITE_SAND.get().asItem());
        this.tag(ItemTags.SAND).add(NTBlocks.OLIVINE_SAND.get().asItem());
    }
}
