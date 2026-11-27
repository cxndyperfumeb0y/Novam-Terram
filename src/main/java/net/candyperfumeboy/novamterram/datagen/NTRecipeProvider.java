package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NTRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public NTRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        // ==========================================
        // DYES & FLORA
        // ==========================================
        flowerToDye(recipeOutput, NTBlocks.HEATHER, Items.LIGHT_BLUE_DYE, "heather");
        flowerToDye(recipeOutput, NTBlocks.BLUEBELL, Items.LIGHT_BLUE_DYE, "bluebell");

        flowerToDye(recipeOutput, NTBlocks.PURPLE_BLUEBELL, Items.LIGHT_BLUE_DYE, "purple_bluebell");

        flowerToDye(recipeOutput, NTBlocks.HEATHER_PETALS, Items.LIGHT_BLUE_DYE, "heather_petals");
        flowerToDye(recipeOutput, NTBlocks.LAVENDER, Items.PURPLE_DYE, "lavender");
        flowerToDye(recipeOutput, NTBlocks.LAVENDER_PETALS, Items.PURPLE_DYE, "lavender_petals");
        flowerToDye(recipeOutput, NTBlocks.ASTER, Items.WHITE_DYE, "aster");
        flowerToDye(recipeOutput, NTBlocks.ASTER_PETALS, Items.WHITE_DYE, "aster_petals");
        flowerToDye(recipeOutput, NTBlocks.TRILLIUM, Items.LIGHT_GRAY_DYE, "trillium");
        flowerToDye(recipeOutput, NTBlocks.THISTLE, Items.PURPLE_DYE, "thistle");
        flowerToDye(recipeOutput, NTBlocks.BROMELIAD, Items.RED_DYE, "bromeliad");
        flowerToDye(recipeOutput, NTBlocks.SILVER_SAGE, Items.LIGHT_GRAY_DYE, "silver_sage");
        flowerToDye(recipeOutput, NTBlocks.BLEEDING_HEART, Items.PINK_DYE, "bleeding_heart");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GREEN_DYE)
                .requires(NTItems.OLIVE.get())
                .unlockedBy("has_olive", has(NTItems.OLIVE.get()))
                .save(recipeOutput, NovamTerram.MOD_ID + ":green_dye_from_olive");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PURPLE_DYE, 2)
                .requires(NTBlocks.IRIS.get())
                .unlockedBy("has_iris", has(NTBlocks.IRIS.get()))
                .save(recipeOutput, NovamTerram.MOD_ID + ":purple_dye_from_iris");

        flowerToDye(recipeOutput, NTBlocks.PAULOWNIA_LEAF_CARPET, Items.MAGENTA_DYE, "paulownia_carpet");
        flowerToDye(recipeOutput, NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET, Items.ORANGE_DYE, "orange_autumnal_carpet");
        flowerToDye(recipeOutput, NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET, Items.YELLOW_DYE, "yellow_autumnal_carpet");

        leafCarpetRecipe(recipeOutput, NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET, NTBlocks.ORANGE_AUTUMNAL_LEAVES);
        leafCarpetRecipe(recipeOutput, NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET, NTBlocks.YELLOW_AUTUMNAL_LEAVES);
        leafCarpetRecipe(recipeOutput, NTBlocks.PAULOWNIA_LEAF_CARPET, NTBlocks.PAULOWNIA_LEAVES);
        leafCarpetRecipe(recipeOutput, NTBlocks.MYRTLE_LEAF_CARPET, NTBlocks.WHITE_MYRTLE_LEAVES);

        // ==========================================
        // MINERALS & ORES
        // ==========================================
        List<ItemLike> OPAL_SMELTABLES = List.of(NTItems.RAW_OPAL, NTBlocks.OPAL_ORE);
        List<ItemLike> KYANITE_SMELTABLES = List.of(NTItems.RAW_KYANITE, NTBlocks.KYANITE_ORE);
        List<ItemLike> JADEITE_SMELTABLES = List.of(NTItems.RAW_JADEITE, NTBlocks.JADEITE_ORE);
        List<ItemLike> RHODOCHROSITE_SMELTABLES = List.of(NTItems.RAW_RHODOCHROSITE, NTBlocks.RHODOCHROSITE_ORE);
        List<ItemLike> IOLITE_SMELTABLES = List.of(NTItems.RAW_IOLITE, NTBlocks.IOLITE_ORE);
        List<ItemLike> LIGNITE_SMELTABLES = List.of(NTBlocks.LIGNITE_ORE, NTBlocks.DEEPSLATE_LIGNITE_ORE, NTBlocks.LIGNITE_NETHER_ORE, NTBlocks.LIGNITE_END_ORE);
        List<ItemLike> GLASS_SMELTABLES = List.of(NTBlocks.OLIVINE_SAND, NTBlocks.AMBER_SAND, NTBlocks.WHITE_SAND);

        oreSmelting(recipeOutput, OPAL_SMELTABLES, RecipeCategory.MISC, NTItems.OPAL.get(), 0.7f, 200, "opal");
        oreSmelting(recipeOutput, KYANITE_SMELTABLES, RecipeCategory.MISC, NTItems.KYANITE.get(), 0.7f, 200, "kyanite");
        oreSmelting(recipeOutput, JADEITE_SMELTABLES, RecipeCategory.MISC, NTItems.JADEITE.get(), 0.7f, 200, "jadeite");
        oreSmelting(recipeOutput, RHODOCHROSITE_SMELTABLES, RecipeCategory.MISC, NTItems.RHODOCHROSITE.get(), 0.7f, 200, "rhodochrosite");
        oreSmelting(recipeOutput, IOLITE_SMELTABLES, RecipeCategory.MISC, NTItems.IOLITE.get(), 0.7f, 200, "iolite");
        oreSmelting(recipeOutput, LIGNITE_SMELTABLES, RecipeCategory.MISC, NTItems.LIGNITE_COAL.get(), 0.15f, 200, "lignite");
        oreSmelting(recipeOutput, GLASS_SMELTABLES, RecipeCategory.MISC, Blocks.GLASS, 0.1f, 200, "olivine");

        oreBlasting(recipeOutput, OPAL_SMELTABLES, RecipeCategory.MISC, NTItems.OPAL.get(), 0.7f, 100, "opal");
        oreBlasting(recipeOutput, KYANITE_SMELTABLES, RecipeCategory.MISC, NTItems.KYANITE.get(), 0.7f, 100, "kyanite");
        oreBlasting(recipeOutput, JADEITE_SMELTABLES, RecipeCategory.MISC, NTItems.JADEITE.get(), 0.7f, 100, "jadeite");
        oreBlasting(recipeOutput, RHODOCHROSITE_SMELTABLES, RecipeCategory.MISC, NTItems.RHODOCHROSITE.get(), 0.7f, 100, "rhodochrosite");
        oreBlasting(recipeOutput, IOLITE_SMELTABLES, RecipeCategory.MISC, NTItems.IOLITE.get(), 0.7f, 100, "iolite");

        registerGemRecipes(recipeOutput, NTItems.OPAL.get(), NTBlocks.OPAL_BLOCK.get(), "opal");
        registerGemRecipes(recipeOutput, NTItems.KYANITE.get(), NTBlocks.KYANITE_BLOCK.get(), "kyanite");
        registerGemRecipes(recipeOutput, NTItems.JADEITE.get(), NTBlocks.JADEITE_BLOCK.get(), "jadeite");
        registerGemRecipes(recipeOutput, NTItems.RHODOCHROSITE.get(), NTBlocks.RHODOCHROSITE_BLOCK.get(), "rhodochrosite");
        registerGemRecipes(recipeOutput, NTItems.IOLITE.get(), NTBlocks.IOLITE_BLOCK.get(), "iolite");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, NTBlocks.LIGNITE_BLOCK.get())
                .pattern("LLL").pattern("LLL").pattern("LLL")
                .define('L', NTItems.LIGNITE_COAL.get())
                .unlockedBy("has_lignite", has(NTItems.LIGNITE_COAL)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, NTItems.LIGNITE_COAL.get(), 9)
                .requires(NTBlocks.LIGNITE_BLOCK)
                .unlockedBy("has_lignite_block", has(NTBlocks.LIGNITE_BLOCK)).save(recipeOutput);

        // ==========================================
        // SANDSTONE SETS
        // ==========================================

        registerSandstoneRecipes(recipeOutput, NTBlocks.OLIVINE_SAND, NTBlocks.OLIVINE_SANDSTONE, NTBlocks.OLIVINE_SANDSTONE_STAIRS, NTBlocks.OLIVINE_SANDSTONE_SLAB, NTBlocks.OLIVINE_SANDSTONE_BUTTON, NTBlocks.OLIVINE_SANDSTONE_PRESSURE_PLATE, NTBlocks.OLIVINE_SANDSTONE_FENCE, NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE, NTBlocks.OLIVINE_SANDSTONE_WALL, NTBlocks.OLIVINE_SANDSTONE_DOOR, NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR, NTBlocks.CUT_OLIVINE_SANDSTONE, NTBlocks.CHISELED_OLIVINE_SANDSTONE);
        registerSandstoneRecipes(recipeOutput, NTBlocks.AMBER_SAND, NTBlocks.AMBER_SANDSTONE, NTBlocks.AMBER_SANDSTONE_STAIRS, NTBlocks.AMBER_SANDSTONE_SLAB, null, null, null, null, NTBlocks.AMBER_SANDSTONE_WALL, null, null, NTBlocks.CUT_AMBER_SANDSTONE, NTBlocks.CHISELED_AMBER_SANDSTONE);
        registerSandstoneRecipes(recipeOutput, NTBlocks.WHITE_SAND, NTBlocks.WHITE_SANDSTONE, NTBlocks.WHITE_SANDSTONE_STAIRS, NTBlocks.WHITE_SANDSTONE_SLAB, null, null, null, null, NTBlocks.WHITE_SANDSTONE_WALL, null, null, NTBlocks.CUT_WHITE_SANDSTONE, NTBlocks.CHISELED_WHITE_SANDSTONE);

        // ==========================================
        // THE WOOD LOOP
        // ==========================================
        for (NTWoodSet set : NTBlocks.WOOD_SETS) {
            Block log = set.log.get();
            Block strippedLog = set.strippedLog.get();
            Block planks = set.planks.get();

            planksFromLogs(recipeOutput, planks, log, 4);
            planksFromLogs(recipeOutput, planks, set.wood.get(), 4);
            planksFromLogs(recipeOutput, planks, strippedLog, 4);
            planksFromLogs(recipeOutput, planks, set.strippedWood.get(), 4);

            woodFromLogs(recipeOutput, set.wood.get(), log);
            woodFromLogs(recipeOutput, set.strippedWood.get(), strippedLog);

            stairBuilder(set.stairs.get(), Ingredient.of(planks)).group("planks")
                    .unlockedBy("has_planks", has(planks)).save(recipeOutput);
            slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, set.slab.get(), planks);
            buttonBuilder(set.button.get(), Ingredient.of(planks)).group("planks")
                    .unlockedBy("has_planks", has(planks)).save(recipeOutput);
            pressurePlate(recipeOutput, set.pressurePlate.get(), planks);
            fenceBuilder(set.fence.get(), Ingredient.of(planks)).group("planks")
                    .unlockedBy("has_planks", has(planks)).save(recipeOutput);
            fenceGateBuilder(set.fenceGate.get(), Ingredient.of(planks)).group("planks")
                    .unlockedBy("has_planks", has(planks)).save(recipeOutput);
            doorBuilder(set.door.get(), Ingredient.of(planks)).group("planks")
                    .unlockedBy("has_planks", has(planks)).save(recipeOutput);
            trapdoorBuilder(set.trapdoor.get(), Ingredient.of(planks)).group("planks")
                    .unlockedBy("has_planks", has(planks)).save(recipeOutput);

            signBuilder(set.signItem.get(), Ingredient.of(planks))
                    .unlockedBy("has_planks", has(planks)).save(recipeOutput);

            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, set.hangingSignItem.get(), 6)
                    .pattern("C C").pattern("SSS").pattern("SSS")
                    .define('C', Items.CHAIN).define('S', strippedLog)
                    .unlockedBy("has_stripped_log", has(strippedLog)).save(recipeOutput);

            Item boat = set.boat.get();
            ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, boat)
                    .pattern("P P").pattern("PPP")
                    .define('P', planks).group("boat")
                    .unlockedBy("in_water", insideOf(Blocks.WATER)).save(recipeOutput);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, set.chestBoat.get())
                    .requires(boat).requires(Blocks.CHEST).group("chest_boat")
                    .unlockedBy("has_boat", has(boat)).save(recipeOutput);
        }
    }

    // --- CUSTOM HELPERS ---

    private void registerSandstoneRecipes(RecipeOutput out,
                                          DeferredHolder<Block, ? extends Block> sand,
                                          DeferredHolder<Block, ? extends Block> stone,
                                          DeferredHolder<Block, ? extends Block> stairs,
                                          DeferredHolder<Block, ? extends Block> slab,
                                          DeferredHolder<Block, ? extends Block> btn,
                                          DeferredHolder<Block, ? extends Block> plate,
                                          DeferredHolder<Block, ? extends Block> fence,
                                          DeferredHolder<Block, ? extends Block> gate,
                                          DeferredHolder<Block, ? extends Block> wall,
                                          DeferredHolder<Block, ? extends Block> door,
                                          DeferredHolder<Block, ? extends Block> trap,
                                          DeferredHolder<Block, ? extends Block> cut,
                                          DeferredHolder<Block, ? extends Block> chiseled) {

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, stone.get())
                .pattern("SS").pattern("SS")
                .define('S', sand.get())
                .unlockedBy("has_sand", has(sand.get())).save(out);

        stairBuilder(stairs.get(), Ingredient.of(stone.get()))
                .unlockedBy("has_stone", has(stone.get())).save(out);

        slab(out, RecipeCategory.BUILDING_BLOCKS, slab.get(), stone.get());

        if(btn != null) buttonBuilder(btn.get(), Ingredient.of(stone.get()))
                .unlockedBy("has_stone", has(stone.get())).save(out);
        if(plate != null) pressurePlate(out, plate.get(), stone.get());
        if(fence != null) fenceBuilder(fence.get(), Ingredient.of(stone.get()))
                .unlockedBy("has_stone", has(stone.get())).save(out);
        if(gate != null) fenceGateBuilder(gate.get(), Ingredient.of(stone.get()))
                .unlockedBy("has_stone", has(stone.get())).save(out);

        wall(out, RecipeCategory.BUILDING_BLOCKS, wall.get(), stone.get());

        if(door != null) doorBuilder(door.get(), Ingredient.of(stone.get()))
                .unlockedBy("has_stone", has(stone.get())).save(out);
        if(trap != null) trapdoorBuilder(trap.get(), Ingredient.of(stone.get()))
                .unlockedBy("has_stone", has(stone.get())).save(out);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, cut.get(), 4)
                .pattern("SS").pattern("SS")
                .define('S', stone.get())
                .unlockedBy("has_stone", has(stone.get())).save(out);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, chiseled.get())
                .pattern("S").pattern("S")
                .define('S', slab.get())
                .unlockedBy("has_slab", has(slab.get())).save(out);
    }

    private void flowerToDye(RecipeOutput recipeOutput, DeferredHolder<Block, Block> flower, Item dye, String name) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, dye)
                .requires(flower.get())
                .unlockedBy("has_" + name, has(flower.get()))
                .save(recipeOutput, NovamTerram.MOD_ID + ":" + getItemName(dye) + "_from_" + name);
    }

    private void leafCarpetRecipe(RecipeOutput recipeOutput, DeferredHolder<Block, Block> carpet, DeferredHolder<Block, Block> leaves) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, carpet.get(), 3)
                .pattern("LL")
                .define('L', leaves.get())
                .unlockedBy(getHasName(leaves.get()), has(leaves.get()))
                .save(recipeOutput);
    }

    private void registerGemRecipes(RecipeOutput recipeOutput, Item gem, Block block, String name) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block)
                .pattern("###").pattern("###").pattern("###")
                .define('#', gem)
                .unlockedBy("has_" + name, has(gem))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, gem, 9)
                .requires(block)
                .unlockedBy("has_" + name + "_block", has(block))
                .save(recipeOutput, NovamTerram.MOD_ID + ":" + name + "_from_block");
    }

    protected static void planksFromLogs(RecipeOutput recipeOutput, ItemLike pPlanks, Block pLog, int pAmount) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, pPlanks, pAmount)
                .requires(pLog).group("planks")
                .unlockedBy(getHasName(pLog), has(pLog)).save(recipeOutput, NovamTerram.MOD_ID + ":" + getItemName(pPlanks) + "_from_" + getItemName(pLog));
    }

    protected static void woodFromLogs(RecipeOutput recipeOutput, ItemLike pWood, ItemLike pLog) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pWood, 3)
                .pattern("##").pattern("##")
                .define('#', pLog).group("bark")
                .unlockedBy(getHasName(pLog), has(pLog)).save(recipeOutput);
    }

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, NovamTerram.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}
