package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NTRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public NTRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        List<ItemLike> OPAL_SMELTABLES = List.of(NTItems.RAW_OPAL, NTBlocks.OPAL_ORE);
        List<ItemLike> LIGNITE_SMELTABLES = List.of(NTItems.LIGNITE_COAL, NTBlocks.LIGNITE_ORE, NTBlocks.DEEPSLATE_LIGNITE_ORE, NTBlocks.LIGNITE_NETHER_ORE, NTBlocks.LIGNITE_END_ORE);
        List<ItemLike> GLASS_SMELTABLES = List.of(Blocks.GLASS, NTBlocks.OLIVINE_SAND);

        // --- LIGNITE ---
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, NTBlocks.LIGNITE_BLOCK.get())
                .pattern("LLL")
                .pattern("LLL")
                .pattern("LLL")
                .define('L', NTItems.LIGNITE_COAL.get())
                .unlockedBy("has_lignite", has(NTItems.LIGNITE_COAL)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, NTItems.LIGNITE_COAL.get(), 9)
                .requires(NTBlocks.LIGNITE_BLOCK)
                .unlockedBy("has_lignite_block", has(NTBlocks.LIGNITE_BLOCK)).save(recipeOutput);

        // --- SMELTING & BLASTING ---
        oreSmelting(recipeOutput, OPAL_SMELTABLES, RecipeCategory.MISC, NTItems.OPAL.get(), 0.25f, 200, "opal");
        oreSmelting(recipeOutput, LIGNITE_SMELTABLES, RecipeCategory.MISC, NTItems.LIGNITE_COAL.get(), 0.25f, 200, "lignite");
        oreBlasting(recipeOutput, LIGNITE_SMELTABLES, RecipeCategory.MISC, NTItems.LIGNITE_COAL.get(), 0.25f, 100, "lignite");
        oreSmelting(recipeOutput, GLASS_SMELTABLES, RecipeCategory.MISC, Blocks.GLASS, 0.25f, 200, "olivine");

        // --- OLIVINE SANDSTONE ---
        stairBuilder(NTBlocks.OLIVINE_SANDSTONE_STAIRS.get(), Ingredient.of(NTBlocks.OLIVINE_SANDSTONE)).group("olivine_sandstone")
                .unlockedBy("has_olivine_sandstone", has(NTBlocks.OLIVINE_SANDSTONE)).save(recipeOutput);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, NTBlocks.OLIVINE_SANDSTONE_SLAB.get(), NTBlocks.OLIVINE_SANDSTONE.get());

        buttonBuilder(NTBlocks.OLIVINE_SANDSTONE_BUTTON.get(), Ingredient.of(NTBlocks.OLIVINE_SANDSTONE.get())).group("olivine_sandstone")
                .unlockedBy("has_olivine_sandstone", has(NTBlocks.OLIVINE_SANDSTONE.get())).save(recipeOutput);
        pressurePlate(recipeOutput, NTBlocks.OLIVINE_SANDSTONE_PRESSURE_PLATE.get(), NTBlocks.OLIVINE_SANDSTONE.get());

        fenceBuilder(NTBlocks.OLIVINE_SANDSTONE_FENCE.get(), Ingredient.of(NTBlocks.OLIVINE_SANDSTONE.get())).group("olivine_sandstone")
                .unlockedBy("has_olivine_sandstone", has(NTBlocks.OLIVINE_SANDSTONE.get())).save(recipeOutput);
        fenceGateBuilder(NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE.get(), Ingredient.of(NTBlocks.OLIVINE_SANDSTONE.get())).group("olivine_sandstone")
                .unlockedBy("has_olivine_sandstone", has(NTBlocks.OLIVINE_SANDSTONE.get())).save(recipeOutput);
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, NTBlocks.OLIVINE_SANDSTONE_WALL.get(), NTBlocks.OLIVINE_SANDSTONE.get());

        doorBuilder(NTBlocks.OLIVINE_SANDSTONE_DOOR.get(), Ingredient.of(NTBlocks.OLIVINE_SANDSTONE.get())).group("olivine_sandstone")
                .unlockedBy("has_olivine_sandstone", has(NTBlocks.OLIVINE_SANDSTONE.get())).save(recipeOutput);
        trapdoorBuilder(NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR.get(), Ingredient.of(NTBlocks.OLIVINE_SANDSTONE.get())).group("olivine_sandstone")
                .unlockedBy("has_olivine_sandstone", has(NTBlocks.OLIVINE_SANDSTONE.get())).save(recipeOutput);

        // --- BLUE SPRUCE WOOD SET ---

// Sign (3x2 Planks + 1 Stick)
        signBuilder(NTItems.BLUE_SPRUCE_SIGN_ITEM.get(), Ingredient.of(NTBlocks.BLUE_SPRUCE_PLANKS.get()))
                .unlockedBy("has_blue_spruce_planks", has(NTBlocks.BLUE_SPRUCE_PLANKS.get()))
                .save(recipeOutput);

// Hanging Sign (2 Stripped Logs + 2 Chains)
// Note: If hangingSign() also fails, use ShapedRecipeBuilder instead
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, NTItems.BLUE_SPRUCE_HANGING_SIGN_ITEM.get(), 6)
                .pattern("C C")
                .pattern("SSS")
                .pattern("SSS")
                .define('C', Items.CHAIN)
                .define('S', NTBlocks.STRIPPED_BLUE_SPRUCE_LOG.get())
                .unlockedBy("has_stripped_log", has(NTBlocks.STRIPPED_BLUE_SPRUCE_LOG.get()))
                .save(recipeOutput);

// Boat (5 Planks in a U-shape)
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, NTItems.BLUE_SPRUCE_BOAT.get())
                .pattern("P P")
                .pattern("PPP")
                .define('P', NTBlocks.BLUE_SPRUCE_PLANKS.get())
                .group("boat")
                .unlockedBy("in_water", insideOf(Blocks.WATER))
                .save(recipeOutput);

// Chest Boat (Boat + Chest)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, NTItems.BLUE_SPRUCE_CHEST_BOAT.get())
                .requires(NTItems.BLUE_SPRUCE_BOAT.get())
                .requires(Blocks.CHEST)
                .group("chest_boat")
                .unlockedBy("has_boat", has(NTItems.BLUE_SPRUCE_BOAT.get()))
                .save(recipeOutput);
    }

    // --- HELPER METHODS ---

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, NovamTerram.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}