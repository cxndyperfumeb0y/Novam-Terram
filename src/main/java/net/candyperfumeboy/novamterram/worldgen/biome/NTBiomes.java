package net.candyperfumeboy.novamterram.worldgen.biome;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.worldgen.NTPlacedFeatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.intellij.lang.annotations.Identifier;


import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;

public class NTBiomes{
    // 1. Create the Deferred Register for Biomes
    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(Registries.BIOME, NovamTerram.MOD_ID);

    // 2. Define the ResourceKey for Blue Spruce Taiga
    public static final ResourceKey<Biome> BLUE_SPRUCE_TAIGA = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "blue_spruce_taiga")
    );

    // 3. Define the ResourceKey for Green Desert
    public static final ResourceKey<Biome> OLIVINE_BEACH = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "olivine_beach")
    );
    // 3. Define the ResourceKey for Green Desert
    public static final ResourceKey<Biome> AEGEAN_ARCHIPELAGO = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "aegean_archipelago")
    );

    // 3. Define the ResourceKey for Green Desert
    public static final ResourceKey<Biome> HEATH = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "heath")
    );


    // 3. Define the ResourceKey for Green Desert
    public static final ResourceKey<Biome> ANDES = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "andes")
    );

    // 4. Register method to be called in your Main Mod Class constructor
    public static void register(IEventBus eventBus) {
        BIOMES.register(eventBus);
    }
}