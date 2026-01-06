package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class NTItemModelProvider extends ItemModelProvider {
    public NTItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NovamTerram.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(NTItems.LIGNITE_COAL.get());
        basicItem(NTItems.OPAL.get());
        basicItem(NTItems.RAW_OPAL.get());

        buttonItem(NTBlocks.OLIVINE_SANDSTONE_BUTTON, NTBlocks.OLIVINE_SANDSTONE);
        fenceItem(NTBlocks.OLIVINE_SANDSTONE_FENCE, NTBlocks.OLIVINE_SANDSTONE);
        wallItem(NTBlocks.OLIVINE_SANDSTONE_WALL, NTBlocks.OLIVINE_SANDSTONE);

        basicItem(NTBlocks.OLIVINE_SANDSTONE_DOOR.asItem());

        basicItem(NTItems.BEAR_CLAW.get());

        saplingItem(NTBlocks.BLUE_SPRUCE_SAPLING);

        buttonItem(NTBlocks.BLUE_SPRUCE_BUTTON, NTBlocks.BLUE_SPRUCE_PLANKS);
        fenceItem(NTBlocks.BLUE_SPRUCE_FENCE, NTBlocks.BLUE_SPRUCE_PLANKS);

        // Boats are standard flat items
        basicItem(NTItems.BLUE_SPRUCE_BOAT.get());
        basicItem(NTItems.BLUE_SPRUCE_CHEST_BOAT.get());

        basicItem(NTBlocks.BLUE_SPRUCE_DOOR.asItem());

        // Updated Sign Items to use a specific helper
        basicItem(NTItems.BLUE_SPRUCE_SIGN_ITEM.get());
        basicItem(NTItems.BLUE_SPRUCE_HANGING_SIGN_ITEM.get());

        podzolItem(NTBlocks.BLUE_PODZOL);
    }

    private ItemModelBuilder saplingItem(DeferredBlock<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID,"block/" + item.getId().getPath()));
    }

    public void buttonItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    private void podzolItem(DeferredBlock<?> block) {
        String name = block.getId().getPath(); // safe with DeferredBlock

        // Use cube_all parent so all sides of the cube are visible
        withExistingParent(name, mcLoc("block/cube_all"))
                .texture("all", modLoc("block/" + name + "_side"));
    }
}