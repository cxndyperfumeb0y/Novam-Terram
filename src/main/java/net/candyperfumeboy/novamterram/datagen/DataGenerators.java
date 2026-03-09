package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = NovamTerram.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // --- SERVER DATA ---
        if (event.includeServer()) {
            // 1. Worldgen Registry (Datapack) - Run this early so biomes exist for tags
            generator.addProvider(true, new NTDatapackProvider(packOutput, lookupProvider));

            // 2. Loot Tables
            generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                    List.of(new LootTableProvider.SubProviderEntry(NTBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));

            // 3. Recipes & Data Maps
            generator.addProvider(true, new NTRecipeProvider(packOutput, lookupProvider));
            generator.addProvider(true, new NTDataMapProvider(packOutput, lookupProvider));

            // 4. Tags (Items depend on Blocks)
            NTBlockTagProvider blockTags = new NTBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
            generator.addProvider(true, blockTags);
            generator.addProvider(true, new NTItemTagProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));

            // Biome Tags
            generator.addProvider(true, new NTBiomeTagsProvider(packOutput, lookupProvider, existingFileHelper));
        }

        // --- CLIENT ASSETS ---
        if (event.includeClient()) {
            generator.addProvider(true, new NTItemModelProvider(packOutput, existingFileHelper));
            generator.addProvider(true, new NTBlockStateProvider(packOutput, existingFileHelper));

            // Helpful Tip: Add a LanguageProvider here if you haven't yet!
            // generator.addProvider(true, new NTLanguageProvider(packOutput, "en_us"));
        }
    }
}