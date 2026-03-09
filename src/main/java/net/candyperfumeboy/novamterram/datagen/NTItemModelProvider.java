package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

public class NTItemModelProvider extends ItemModelProvider {
    public NTItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NovamTerram.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // --- MINERALS & ORES ---
        basicItem(NTItems.LIGNITE_COAL.get());
        basicItem(NTItems.OPAL.get()); basicItem(NTItems.RAW_OPAL.get());
        basicItem(NTItems.KYANITE.get()); basicItem(NTItems.RAW_KYANITE.get());
        basicItem(NTItems.JADEITE.get()); basicItem(NTItems.RAW_JADEITE.get());
        basicItem(NTItems.RHODOCHROSITE.get()); basicItem(NTItems.RAW_RHODOCHROSITE.get());
        basicItem(NTItems.RAW_IOLITE.get()); basicItem(NTItems.IOLITE.get());
        basicItem(NTItems.BEAR_CLAW.get());
        basicItem(NTItems.OLIVE.get());

        // --- SANDSTONE SETS ---
        // Olivine
        buttonItem(NTBlocks.OLIVINE_SANDSTONE_BUTTON, NTBlocks.OLIVINE_SANDSTONE);
        fenceItem(NTBlocks.OLIVINE_SANDSTONE_FENCE, NTBlocks.OLIVINE_SANDSTONE);
        wallItem(NTBlocks.OLIVINE_SANDSTONE_WALL, NTBlocks.OLIVINE_SANDSTONE);
        basicItem(NTBlocks.OLIVINE_SANDSTONE_DOOR.asItem());
        // Amber
        wallItem(NTBlocks.AMBER_SANDSTONE_WALL, NTBlocks.AMBER_SANDSTONE);
        // White
        wallItem(NTBlocks.WHITE_SANDSTONE_WALL, NTBlocks.WHITE_SANDSTONE);


        // --- WOOD LOOP ---
        String[] woodNames = {"blue_spruce", "teak", "balsa", "mahogany", "aspen"};
        for (String wood : woodNames) {
            buttonItem(wood + "_button", wood + "_planks");
            fenceItem(wood + "_fence", wood + "_planks");
            basicItem(getItem(wood + "_boat"));
            basicItem(getItem(wood + "_chest_boat"));
            basicItem(getItem(wood + "_sign"));
            basicItem(getItem(wood + "_hanging_sign"));
            basicItem(getBlock(wood + "_door").asItem());
            saplingItemModel(wood + "_sapling");
        }

        // --- FLORA ---
        saplingItem(NTBlocks.ORANGE_AUTUMNAL_PINE_SAPLING); saplingItem(NTBlocks.YELLOW_AUTUMNAL_PINE_SAPLING);
        saplingItem(NTBlocks.PAULOWNIA_SAPLING); saplingItem(NTBlocks.PURPLE_MYRTLE_SAPLING); saplingItem(NTBlocks.WHITE_MYRTLE_SAPLING); saplingItem(NTBlocks.RED_MYRTLE_SAPLING);
        simpleBlockItemModel(NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET); simpleBlockItemModel(NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET);
        simpleBlockItemModel(NTBlocks.PAULOWNIA_LEAF_CARPET); simpleBlockItemModel(NTBlocks.MYRTLE_LEAF_CARPET);
        simpleBlockItemModel(NTBlocks.OLIVE_LEAVES);
        podzolItem(NTBlocks.BLUE_PODZOL);
        basicBlockItem(NTBlocks.HEATHER); basicBlockItem(NTBlocks.LAVENDER); basicBlockItem(NTBlocks.ASTER); basicBlockItem(NTBlocks.BLUEBELL); basicBlockItem(NTBlocks.PURPLE_BLUEBELL);
        basicBlockItem(NTBlocks.THISTLE); basicBlockItem(NTBlocks.TRILLIUM); basicBlockItem(NTBlocks.BROMELIAD);
        basicBlockItem(NTBlocks.HEATHER_PETALS); basicBlockItem(NTBlocks.LAVENDER_PETALS); basicBlockItem(NTBlocks.ASTER_PETALS);
        basicBlockItem(NTBlocks.SILVER_SAGE); basicBlockItem(NTBlocks.BLEEDING_HEART);
        irisItem(NTBlocks.IRIS);
    }

    // --- HELPERS ---
    public void irisItem(DeferredBlock<Block> block) { String name = block.getId().getPath(); withExistingParent(name, mcLoc("item/generated")).texture("layer0", modLoc("item/" + name)); }
    public void basicBlockItem(DeferredBlock<Block> block) { String name = block.getId().getPath(); withExistingParent(name, mcLoc("item/generated")).texture("layer0", modLoc("block/" + name)); }
    private Item getItem(String name) { return NTItems.ITEMS.getEntries().stream().filter(e -> e.getId().getPath().equals(name)).findFirst().map(DeferredHolder::get).orElseThrow(() -> new RuntimeException("Item not found: " + name)); }
    private Block getBlock(String name) { return NTBlocks.BLOCKS.getEntries().stream().filter(e -> e.getId().getPath().equals(name)).findFirst().map(DeferredHolder::get).orElseThrow(() -> new RuntimeException("Block not found: " + name)); }
    private ItemModelBuilder saplingItemModel(String name) { return withExistingParent(name, mcLoc("item/generated")).texture("layer0", modLoc("block/" + name)); }
    private ItemModelBuilder saplingItem(DeferredBlock<Block> item) { return saplingItemModel(item.getId().getPath()); }
    public void buttonItem(String name, String baseBlockName) { this.withExistingParent(name, mcLoc("block/button_inventory")).texture("texture", modLoc("block/" + baseBlockName)); }
    public void buttonItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) { buttonItem(block.getId().getPath(), baseBlock.getId().getPath()); }
    public void fenceItem(String name, String baseBlockName) { this.withExistingParent(name, mcLoc("block/fence_inventory")).texture("texture", modLoc("block/" + baseBlockName)); }
    public void simpleBlockItemModel(DeferredBlock<Block> block) { String name = block.getId().getPath(); getBuilder(name).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + name))); }
    public void fenceItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) { fenceItem(block.getId().getPath(), baseBlock.getId().getPath()); }
    public void wallItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) { this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory")).texture("wall", modLoc("block/" + baseBlock.getId().getPath())); }
    private void podzolItem(DeferredBlock<?> block) { String name = block.getId().getPath(); withExistingParent(name, mcLoc("block/cube_all")).texture("all", modLoc("block/" + name + "_side")); }
}