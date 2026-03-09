package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.block.custom.OliveHangingBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

public class NTBlockStateProvider extends BlockStateProvider {
    public NTBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, NovamTerram.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // --- MINERALS & ORES ---
        blockWithItem(NTBlocks.LIGNITE_BLOCK);
        blockWithItem(NTBlocks.OPAL_BLOCK);
        blockWithItem(NTBlocks.OPAL_ORE);
        blockWithItem(NTBlocks.RHODOCHROSITE_BLOCK);
        blockWithItem(NTBlocks.RHODOCHROSITE_ORE);
        blockWithItem(NTBlocks.JADEITE_BLOCK);
        blockWithItem(NTBlocks.JADEITE_ORE);
        blockWithItem(NTBlocks.IOLITE_BLOCK);
        blockWithItem(NTBlocks.IOLITE_ORE);
        blockWithItem(NTBlocks.KYANITE_BLOCK);
        blockWithItem(NTBlocks.KYANITE_ORE);
        blockWithItem(NTBlocks.LIGNITE_ORE);
        blockWithItem(NTBlocks.DEEPSLATE_LIGNITE_ORE);
        blockWithItem(NTBlocks.LIGNITE_NETHER_ORE);
        blockWithItem(NTBlocks.LIGNITE_END_ORE);

        // --- SANDSTONE SET (OLIVINE) ---
        blockWithItem(NTBlocks.OLIVINE_SAND);
        blockWithItem(NTBlocks.OLIVINE_SANDSTONE);
        blockWithItem(NTBlocks.CHISELED_OLIVINE_SANDSTONE);
        blockWithItem(NTBlocks.CUT_OLIVINE_SANDSTONE);
        stairsBlock(NTBlocks.OLIVINE_SANDSTONE_STAIRS.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));
        slabBlock(NTBlocks.OLIVINE_SANDSTONE_SLAB.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));
        buttonBlock(NTBlocks.OLIVINE_SANDSTONE_BUTTON.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));
        pressurePlateBlock(NTBlocks.OLIVINE_SANDSTONE_PRESSURE_PLATE.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));
        fenceBlock(NTBlocks.OLIVINE_SANDSTONE_FENCE.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));
        fenceGateBlock(NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));
        wallBlock(NTBlocks.OLIVINE_SANDSTONE_WALL.get(), blockTexture(NTBlocks.OLIVINE_SANDSTONE.get()));
        doorBlockWithRenderType(NTBlocks.OLIVINE_SANDSTONE_DOOR.get(), modLoc("block/olivine_sandstone_door_bottom"), modLoc("block/olivine_sandstone_door_top"), "cutout");
        trapdoorBlockWithRenderType(NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR.get(), modLoc("block/olivine_sandstone_trapdoor"), true, "cutout");
        blockWithItem(NTBlocks.WHITE_SAND);
        blockWithItem(NTBlocks.WHITE_SANDSTONE);
        blockWithItem(NTBlocks.CHISELED_WHITE_SANDSTONE);
        blockWithItem(NTBlocks.CUT_WHITE_SANDSTONE);
        stairsBlock(NTBlocks.WHITE_SANDSTONE_STAIRS.get(), blockTexture(NTBlocks.WHITE_SANDSTONE.get()));
        slabBlock(NTBlocks.WHITE_SANDSTONE_SLAB.get(), blockTexture(NTBlocks.WHITE_SANDSTONE.get()), blockTexture(NTBlocks.WHITE_SANDSTONE.get()));
        wallBlock(NTBlocks.WHITE_SANDSTONE_WALL.get(), blockTexture(NTBlocks.WHITE_SANDSTONE.get()));

        blockItem(NTBlocks.WHITE_SANDSTONE_STAIRS);
        blockItem(NTBlocks.WHITE_SANDSTONE_SLAB);
        blockItem(NTBlocks.OLIVINE_SANDSTONE_STAIRS);
        blockItem(NTBlocks.OLIVINE_SANDSTONE_SLAB);
        blockItem(NTBlocks.OLIVINE_SANDSTONE_PRESSURE_PLATE);
        blockItem(NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE);
        blockItem(NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR, "_bottom");

        // --- SANDSTONE SET (AMBER) ---
        blockWithItem(NTBlocks.AMBER_SAND);
        blockWithItem(NTBlocks.AMBER_SANDSTONE);
        blockWithItem(NTBlocks.CHISELED_AMBER_SANDSTONE);
        blockWithItem(NTBlocks.CUT_AMBER_SANDSTONE);
        stairsBlock(NTBlocks.AMBER_SANDSTONE_STAIRS.get(), blockTexture(NTBlocks.AMBER_SANDSTONE.get()));
        slabBlock(NTBlocks.AMBER_SANDSTONE_SLAB.get(), blockTexture(NTBlocks.AMBER_SANDSTONE.get()), blockTexture(NTBlocks.AMBER_SANDSTONE.get()));
        wallBlock(NTBlocks.AMBER_SANDSTONE_WALL.get(), blockTexture(NTBlocks.AMBER_SANDSTONE.get()));

        blockItem(NTBlocks.AMBER_SANDSTONE_STAIRS);
        blockItem(NTBlocks.AMBER_SANDSTONE_SLAB);

        // --- NEW BLOCKS ---
        podzolVariant(NTBlocks.SAFFRON_MOSS.get(), "saffron");
        petrifiedCactusBlock(NTBlocks.PETRIFIED_CACTUS.get());

        // --- WOOD LOOP (Full Sets) ---
        String[] woodNames = {"blue_spruce", "teak", "balsa", "mahogany", "aspen"};
        for (String wood : woodNames) {
            Block log = getBlock(wood + "_log");
            Block woodBlock = getBlock(wood + "_wood");
            Block strippedLog = getBlock("stripped_" + wood + "_log");
            Block strippedWood = getBlock("stripped_" + wood + "_wood");
            Block planks = getBlock(wood + "_planks");

            logBlock((RotatedPillarBlock) log);
            axisBlock((RotatedPillarBlock) woodBlock, blockTexture(log), blockTexture(log));
            logBlock((RotatedPillarBlock) strippedLog);
            axisBlock((RotatedPillarBlock) strippedWood, blockTexture(strippedLog), blockTexture(strippedLog));

            simpleBlockItem(log, models().getExistingFile(modLoc("block/" + wood + "_log")));
            simpleBlockItem(woodBlock, models().getExistingFile(modLoc("block/" + wood + "_wood")));
            simpleBlockItem(strippedLog, models().getExistingFile(modLoc("block/stripped_" + wood + "_log")));
            simpleBlockItem(strippedWood, models().getExistingFile(modLoc("block/stripped_" + wood + "_wood")));

            simpleBlockWithItem(planks, cubeAll(planks));
            leavesBlock(wood + "_leaves");
            saplingBlock(wood + "_sapling");

            stairsBlock((StairBlock) getBlock(wood + "_stairs"), blockTexture(planks));
            slabBlock((SlabBlock) getBlock(wood + "_slab"), blockTexture(planks), blockTexture(planks));
            buttonBlock((ButtonBlock) getBlock(wood + "_button"), blockTexture(planks));
            pressurePlateBlock((PressurePlateBlock) getBlock(wood + "_pressure_plate"), blockTexture(planks));
            fenceBlock((FenceBlock) getBlock(wood + "_fence"), blockTexture(planks));
            fenceGateBlock((FenceGateBlock) getBlock(wood + "_fence_gate"), blockTexture(planks));

            doorBlockWithRenderType((DoorBlock) getBlock(wood + "_door"), modLoc("block/" + wood + "_door_bottom"), modLoc("block/" + wood + "_door_top"), "cutout");
            trapdoorBlockWithRenderType((TrapDoorBlock) getBlock(wood + "_trapdoor"), modLoc("block/" + wood + "_trapdoor"), true, "cutout");

            simpleBlockItem(getBlock(wood + "_stairs"), models().getExistingFile(modLoc("block/" + wood + "_stairs")));
            simpleBlockItem(getBlock(wood + "_slab"), models().getExistingFile(modLoc("block/" + wood + "_slab")));
            simpleBlockItem(getBlock(wood + "_pressure_plate"), models().getExistingFile(modLoc("block/" + wood + "_pressure_plate")));
            simpleBlockItem(getBlock(wood + "_fence_gate"), models().getExistingFile(modLoc("block/" + wood + "_fence_gate")));
            simpleBlockItem(getBlock(wood + "_trapdoor"), models().getExistingFile(modLoc("block/" + wood + "_trapdoor_bottom")));

            signBlock((StandingSignBlock) getBlock(wood + "_sign"), (WallSignBlock) getBlock(wood + "_wall_sign"), blockTexture(planks));
            hangingSignBlock((CeilingHangingSignBlock) getBlock(wood + "_hanging_sign"), (WallHangingSignBlock) getBlock(wood + "_wall_hanging_sign"), blockTexture(planks));
        }

        // --- AEGEAN FLORA (Olive & Shrubbery) ---
        leavesBlock("olive_leaves");
        hangingFruitBlock(NTBlocks.OLIVE_HANGING.get(), "olive_stage");

        // --- HIGHLANDS & STANDALONE FLORA ---
        leavesBlock("orange_autumnal_leaves");
        saplingBlock("orange_autumnal_pine_sapling");
        leafCarpetBlock(NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET.get(), getBlock("orange_autumnal_leaves"));

        leavesBlock("yellow_autumnal_leaves");
        saplingBlock("yellow_autumnal_pine_sapling");
        leafCarpetBlock(NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET.get(), getBlock("yellow_autumnal_leaves"));

        leavesBlock("paulownia_leaves");
        saplingBlock("paulownia_sapling");
        leafCarpetBlock(NTBlocks.PAULOWNIA_LEAF_CARPET.get(), getBlock("paulownia_leaves"));

        leavesBlock("white_myrtle_leaves");
        saplingBlock("white_myrtle_sapling");
        leavesBlock("red_myrtle_leaves");
        saplingBlock("red_myrtle_sapling");
        leavesBlock("purple_myrtle_leaves");
        saplingBlock("purple_myrtle_sapling");
        leafCarpetBlock(NTBlocks.MYRTLE_LEAF_CARPET.get(), getBlock("white_myrtle_leaves"));

        // --- SPECIALS & FLOWERS ---
        podzolVariant(NTBlocks.BLUE_PODZOL.get(), "blue_podzol");

        crossBlock(NTBlocks.HEATHER.get());
        crossBlock(NTBlocks.LAVENDER.get());
        crossBlock(NTBlocks.ASTER.get());
        crossBlock(NTBlocks.TRILLIUM.get());
        crossBlock(NTBlocks.THISTLE.get());
        crossBlock(NTBlocks.BROMELIAD.get());
        crossBlock(NTBlocks.BLUEBELL.get());

        crossBlock(NTBlocks.PURPLE_BLUEBELL.get());


        petalBlock(NTBlocks.HEATHER_PETALS.get());
        petalBlock(NTBlocks.LAVENDER_PETALS.get());
        petalBlock(NTBlocks.ASTER_PETALS.get());
        // --- ROYAL FOREST FLORA ---
        crossBlock(NTBlocks.SILVER_SAGE.get());
        crossBlock(NTBlocks.BLEEDING_HEART.get());

        // Double Tall Iris
        tallCrossBlock(NTBlocks.IRIS.get());
    }

    // --- NEW HELPERS ---
    private void petrifiedCactusBlock(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

        // 1. Define the block model with 3 distinct textures
        ModelFile model = models().cubeBottomTop(name,
                modLoc("block/" + name + "_side"),
                modLoc("block/" + name + "_bottom"),
                modLoc("block/" + name + "_top")
        ).renderType("cutout");

        // 2. Define a separate item model that also uses the cube shape
        // This ensures your item in the inventory looks like a 3D block
        ModelFile itemModel = models().cubeBottomTop(name + "_item",
                modLoc("block/" + name + "_side"),
                modLoc("block/" + name + "_bottom"),
                modLoc("block/" + name + "_top")
        );

        // 3. Register the block and the custom item model
        simpleBlock(block, model);
        itemModels().getBuilder(name).parent(itemModel);
    }

    private void tallCrossBlock(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

        // Generate the two models (top and bottom)
        ModelFile bottom = models().cross(name + "_bottom", modLoc("block/" + name + "_bottom")).renderType("cutout");
        ModelFile top = models().cross(name + "_top", modLoc("block/" + name + "_top")).renderType("cutout");

        // Map the models to the block states
        getVariantBuilder(block).forAllStates(state -> {
            boolean isLower = state.getValue(TallFlowerBlock.HALF) == DoubleBlockHalf.LOWER;
            return ConfiguredModel.builder()
                    .modelFile(isLower ? bottom : top)
                    .build();
        });

        // Item model (usually uses the top texture or a composite, but standard is the top/full flower)
        itemModels().withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", modLoc("block/" + name + "_top"));
    }
    private void hangingFruitBlock(Block block, String textureBaseName) {
        getVariantBuilder(block).forAllStates(state -> {
            int age = state.getValue(OliveHangingBlock.AGE);
            String modelName = BuiltInRegistries.BLOCK.getKey(block).getPath() + "_age" + age;

            ModelFile model = models().withExistingParent(modelName, mcLoc("block/cross"))
                    .texture("cross", modLoc("block/" + textureBaseName + age))
                    .renderType("cutout");

            return ConfiguredModel.builder().modelFile(model).build();
        });
    }

    // --- EXISTING HELPERS ---
    private void leafCarpetBlock(Block carpetBlock, Block leafSource) {
        String name = BuiltInRegistries.BLOCK.getKey(carpetBlock).getPath();
        ResourceLocation leafTex = blockTexture(leafSource);
        ModelFile model = models().withExistingParent(name, mcLoc("block/carpet"))
                .texture("wool", leafTex)
                .texture("particle", leafTex);
        simpleBlock(carpetBlock, model);
        simpleBlockItem(carpetBlock, model);
    }

    private Block getBlock(String name) {
        return NTBlocks.BLOCKS.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(name))
                .findFirst()
                .map(DeferredHolder::get)
                .orElseThrow(() -> new RuntimeException("Block not found: " + name));
    }

    private void saplingBlock(String name) {
        Block block = getBlock(name);
        simpleBlock(block, models().cross(name, blockTexture(block)).renderType("cutout"));
    }

    private void leavesBlock(String name) {
        Block block = getBlock(name);
        simpleBlockWithItem(block, models().singleTexture(name, mcLoc("block/leaves"), "all", blockTexture(block)).renderType("cutout"));
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
        ModelFile model = models().cubeBottomTop(name, modLoc("block/" + name + "_side"), mcLoc("block/dirt"), modLoc("block/" + name + "_top"));
        simpleBlock(block, model);
        simpleBlockItem(block, model);
    }

    private void petalBlock(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        BlockModelBuilder[] models = new BlockModelBuilder[4];
        for (int i = 1; i <= 4; i++) {
            models[i - 1] = models().withExistingParent(name + "_" + i, mcLoc("block/block"))
                    .texture("particle", modLoc("block/" + name))
                    .texture("texture", modLoc("block/" + name))
                    .renderType("cutout");
            models[i - 1].element().from(1, 0.1f, 2).to(9, 0.1f, 10).face(Direction.UP).texture("#texture").ao(false).end().face(Direction.DOWN).texture("#texture").ao(false).end().end();
            if (i >= 2) models[i - 1].element().from(8, 0.15f, 1).to(16, 0.15f, 9).face(Direction.UP).texture("#texture").ao(false).end().face(Direction.DOWN).texture("#texture").ao(false).end().end();
            if (i >= 3) models[i - 1].element().from(2, 0.2f, 8).to(10, 0.2f, 16).face(Direction.UP).texture("#texture").ao(false).end().face(Direction.DOWN).texture("#texture").ao(false).end().end();
            if (i >= 4) models[i - 1].element().from(7, 0.25f, 7).to(15, 0.25f, 15).face(Direction.UP).texture("#texture").ao(false).end().face(Direction.DOWN).texture("#texture").ao(false).end().end();
        }
        getVariantBuilder(block).forAllStates(state -> {
            int amount = state.getValue(PinkPetalsBlock.AMOUNT);
            int rotation = (int) state.getValue(PinkPetalsBlock.FACING).toYRot();
            return ConfiguredModel.builder().modelFile(models[amount - 1]).rotationY((rotation + 180) % 360).build();
        });
    }

    private void crossBlock(Block block) {
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        simpleBlock(block, models().cross(name, blockTexture(block)).renderType("cutout"));
    }
}