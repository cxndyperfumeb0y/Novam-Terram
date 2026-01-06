package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class NTBlockStateProvider extends BlockStateProvider {
    public NTBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, NovamTerram.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(NTBlocks.LIGNITE_BLOCK);
        blockWithItem(NTBlocks.OPAL_BLOCK);
        blockWithItem(NTBlocks.OPAL_ORE);
        blockWithItem(NTBlocks.LIGNITE_ORE);
        blockWithItem(NTBlocks.DEEPSLATE_LIGNITE_ORE);
        blockWithItem(NTBlocks.LIGNITE_NETHER_ORE);
        blockWithItem(NTBlocks.LIGNITE_END_ORE);


        blockWithItem(NTBlocks.OLIVINE_SAND);

        stairsBlock(NTBlocks.OLIVINE_SANDSTONE_STAIRS.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));
        slabBlock(NTBlocks.OLIVINE_SANDSTONE_SLAB.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));

        buttonBlock(NTBlocks.OLIVINE_SANDSTONE_BUTTON.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));
        pressurePlateBlock(NTBlocks.OLIVINE_SANDSTONE_PRESSURE_PLATE.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));

        fenceBlock(NTBlocks.OLIVINE_SANDSTONE_FENCE.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));
        fenceGateBlock(NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));
        wallBlock(NTBlocks.OLIVINE_SANDSTONE_WALL.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));

        doorBlockWithRenderType(NTBlocks.OLIVINE_SANDSTONE_DOOR.get(), modLoc("block/olivine_sandstone_door_bottom"), modLoc("block/olivine_sandstone_door_top"), "cutout");
        trapdoorBlockWithRenderType(NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR.get(), modLoc("block/olivine_sandstone_trapdoor"), true, "cutout");


        blockItem(NTBlocks.OLIVINE_SANDSTONE_STAIRS);
        blockItem(NTBlocks.OLIVINE_SANDSTONE_SLAB);
        blockItem(NTBlocks.OLIVINE_SANDSTONE_PRESSURE_PLATE);
        blockItem(NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE);
        blockItem(NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR, "_bottom");




        //WOODS

        logBlock(((RotatedPillarBlock) NTBlocks.BLUE_SPRUCE_LOG.get()));
        axisBlock(((RotatedPillarBlock) NTBlocks.BLUE_SPRUCE_WOOD.get()), blockTexture(NTBlocks.BLUE_SPRUCE_LOG.get()), blockTexture(NTBlocks.BLUE_SPRUCE_LOG.get()));
        logBlock(((RotatedPillarBlock) NTBlocks.STRIPPED_BLUE_SPRUCE_LOG.get()));
        axisBlock(((RotatedPillarBlock) NTBlocks.STRIPPED_BLUE_SPRUCE_WOOD.get()), blockTexture(NTBlocks.STRIPPED_BLUE_SPRUCE_LOG.get()), blockTexture(NTBlocks.STRIPPED_BLUE_SPRUCE_LOG.get()));

        blockItem(NTBlocks.BLUE_SPRUCE_LOG);
        blockItem(NTBlocks.BLUE_SPRUCE_WOOD);
        blockItem(NTBlocks.STRIPPED_BLUE_SPRUCE_LOG);
        blockItem(NTBlocks.STRIPPED_BLUE_SPRUCE_WOOD);

        blockWithItem(NTBlocks.BLUE_SPRUCE_PLANKS);

        leavesBlock(NTBlocks.BLUE_SPRUCE_LEAVES);
        saplingBlock(NTBlocks.BLUE_SPRUCE_SAPLING);

        stairsBlock(NTBlocks.BLUE_SPRUCE_STAIRS.get(), blockTexture(NTBlocks.BLUE_SPRUCE_PLANKS.get()));
        slabBlock(NTBlocks.BLUE_SPRUCE_SLAB.get(), blockTexture(NTBlocks.BLUE_SPRUCE_PLANKS.get()), blockTexture(NTBlocks.BLUE_SPRUCE_PLANKS.get()));

        buttonBlock(NTBlocks.BLUE_SPRUCE_BUTTON.get(), blockTexture(NTBlocks.BLUE_SPRUCE_PLANKS.get()));
        pressurePlateBlock(NTBlocks.BLUE_SPRUCE_PRESSURE_PLATE.get(), blockTexture(NTBlocks.BLUE_SPRUCE_PLANKS.get()));

        fenceBlock(NTBlocks.BLUE_SPRUCE_FENCE.get(), blockTexture(NTBlocks.BLUE_SPRUCE_PLANKS.get()));
        fenceGateBlock(NTBlocks.BLUE_SPRUCE_FENCE_GATE.get(), blockTexture(NTBlocks.BLUE_SPRUCE_PLANKS.get()));

        doorBlockWithRenderType(NTBlocks.BLUE_SPRUCE_DOOR.get(), modLoc("block/blue_spruce_door_bottom"), modLoc("block/blue_spruce_door_top"), "cutout");
        trapdoorBlockWithRenderType(NTBlocks.BLUE_SPRUCE_TRAP_DOOR.get(), modLoc("block/blue_spruce_trapdoor"), true, "cutout");


        blockItem(NTBlocks.BLUE_SPRUCE_STAIRS);
        blockItem(NTBlocks.BLUE_SPRUCE_SLAB);
        blockItem(NTBlocks.BLUE_SPRUCE_PRESSURE_PLATE);
        blockItem(NTBlocks.BLUE_SPRUCE_FENCE_GATE);
        blockItem(NTBlocks.BLUE_SPRUCE_TRAP_DOOR, "_bottom");

        podzolVariant(NTBlocks.BLUE_PODZOL.get(), "blue_podzol");

        // Standing and Wall Signs
        signBlock((StandingSignBlock) NTBlocks.BLUE_SPRUCE_SIGN.get(),
                (WallSignBlock) NTBlocks.BLUE_SPRUCE_WALL_SIGN.get(),
                blockTexture(NTBlocks.BLUE_SPRUCE_PLANKS.get()));

// Hanging Signs
        hangingSignBlock((CeilingHangingSignBlock) NTBlocks.BLUE_SPRUCE_HANGING_SIGN.get(),
                (WallHangingSignBlock) NTBlocks.BLUE_SPRUCE_WALL_HANGING_SIGN.get(),
                blockTexture(NTBlocks.BLUE_SPRUCE_PLANKS.get()));

    }

    private void saplingBlock(DeferredBlock<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.get(),
                models().cross(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void leavesBlock(DeferredBlock<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().singleTexture(BuiltInRegistries.BLOCK.getKey(blockRegistryObject.get()).getPath(), ResourceLocation.parse("minecraft:block/leaves"),
                        "all", blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock){
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
    private void blockItem(DeferredBlock<?> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("novamterram:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("novamterram:block/" + deferredBlock.getId().getPath() + appendix));
    }

    private void podzolVariant(Block block, String name) {
        models().cubeBottomTop(
                name,
                modLoc("block/" + name + "_side"),
                mcLoc("block/dirt"),                 // vanilla dirt bottom
                modLoc("block/" + name + "_top")
        );

        simpleBlock(block, models().getExistingFile(modLoc("block/" + name)));
    }
}
