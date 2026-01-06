package net.candyperfumeboy.novamterram.worldgen.biome;

import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.worldgen.NTPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class NTBiomeFactory {
    public static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);

        // Registry
        context.register(NTBiomes.BLUE_SPRUCE_TAIGA, createBlueSpruceTaiga(features, carvers));
        context.register(NTBiomes.OLIVINE_BEACH, createOlivineBeach(features, carvers));
        context.register(NTBiomes.AEGEAN_ARCHIPELAGO, createAegeanArchipelago(features, carvers));
        context.register(NTBiomes.HEATH, createHeath(features, carvers));
        context.register(NTBiomes.ANDES, createAndes(features, carvers));

    }

    // --- BIOME CREATION METHODS ---

    public static Biome createBlueSpruceTaiga(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder subGen = new BiomeGenerationSettings.Builder(features, carvers);
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();

        addGlobalGeneration(subGen, features, carvers);
        addGlobalSpawns(spawns);

        // --- CUSTOM MOB SPAWNS ---
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.SILVER_FOX.get(), 20, 3, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 8, 4, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 15, 4, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 8, 2, 3));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CAT, 5, 1, 1));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 15, 2, 4));
        // --- OLD GROWTH FEATURES ---
        // Added Mossy Boulders and Large Ferns to match Vanilla Old Growth Taiga
        subGen.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, features.getOrThrow(NTPlacedFeatures.MOSSY_BOULDER_PLACED));

        // --- VEGETATION FEATURES ---
        subGen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.PATCH_GRASS_PLAIN));
        subGen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLUESPRUCE_TAIGA_PLACED_KEY));
        subGen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.OXEYE_DAISY_PLACED));
        subGen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.25f)
                .downfall(0.8f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x34568B)      // Deep, cold blue
                        .waterFogColor(0x101820)   // Dark, murky depths
                        .fogColor(0x5A7A91)        // Darker, heavier "Steel" fog (Feels denser)
                        .skyColor(0x1B2631)        // Very dark, moody sky (Makes the trees pop)
                        .grassColorOverride(0x6A8E7F)
                        .foliageColorOverride(0x4A6D6B)

                        // --- AMBIENT PARTICLES ---
                        // Using WHITE_ASH at a low density mimics falling needles or light frost
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.WHITE_ASH, 0.012f))
                        // ADD SOUNDS: This is the missing "Content"
                        // This adds a constant low wind/forest hum
                        .ambientLoopSound(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(subGen.build())
                .build();
    }

    // ... createOlivineBeach, createAegeanArchipelago, and createHeath remain unchanged ...

    public static Biome createOlivineBeach(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder subGen = new BiomeGenerationSettings.Builder(features, carvers);
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();

        addGlobalGeneration(subGen, features, carvers);
        addGlobalSpawns(spawns);

        subGen.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, features.getOrThrow(MiscOverworldPlacements.DISK_SAND));
        subGen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.PATCH_SUGAR_CANE));
        subGen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(AquaticPlacements.SEA_PICKLE));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false).temperature(2.0f).downfall(0.0f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x41f2d3).waterFogColor(0x050533).fogColor(0xc0d8ff).skyColor(0x77adff)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.WHITE_ASH, 0.004f)).build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(subGen.build()).build();
    }

    public static Biome createAegeanArchipelago(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder subGen = new BiomeGenerationSettings.Builder(features, carvers);
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();

        addGlobalGeneration(subGen, features, carvers);
        addGlobalSpawns(spawns);

        spawns.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 5, 1, 3));
        subGen.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, features.getOrThrow(NTPlacedFeatures.CALCITE_BOULDER_PLACED));
        subGen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AEGEAN_CORAL_PLACED));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.8f).downfall(0.4f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x2957B8).waterFogColor(0x081030).fogColor(0xC0D8FF).skyColor(0x88BBFF)
                        .grassColorOverride(0x3BBD2B).foliageColorOverride(0x34A825)
                        .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE).build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(subGen.build()).build();
    }

    public static Biome createHeath(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder subGen = new BiomeGenerationSettings.Builder(features, carvers);
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();

        addGlobalGeneration(subGen, features, carvers);
        addGlobalSpawns(spawns);

        subGen.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, features.getOrThrow(MiscOverworldPlacements.DISK_GRAVEL));
        subGen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.ALLIUM_PATCH_PLACED));
        subGen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.PATCH_GRASS_PLAIN));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.5f).downfall(0.5f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x5E7C8C)
                        .waterFogColor(0x232323)
                        .fogColor(0xC0D8FF)
                        .skyColor(0x78A7FF)
                        .grassColorOverride(0x899B5C)
                        .foliageColorOverride(0x778A4A)
                        .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(subGen.build()).build();
    }

    public static Biome createAndes(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder subGen = new BiomeGenerationSettings.Builder(features, carvers);
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();


        // --- ANDES SPECIFIC SPAWNS ---
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 40, 4, 6)); // High weight for Llamas
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 20, 2, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 10, 2, 3));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.SPECTACLED_BEAR.get(), 15, 1, 3));

        // Add Gravel and Andesite patches to the surface
        subGen.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, features.getOrThrow(MiscOverworldPlacements.DISK_GRAVEL));
        subGen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(OrePlacements.ORE_ANDESITE_UPPER));

        // Vegetation: Sparse grass and cold-weather patches
        subGen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.PATCH_GRASS_PLAIN));



        return new Biome.BiomeBuilder()
                .hasPrecipitation(false) // Dry mountains don't rain often
                .temperature(1.2f)       // Hotter temperature (similar to a Savannah/Desert)
                .downfall(0.0f)          // No rain
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3F76E4)
                        .waterFogColor(0x050533)
                        .fogColor(0xFFE4B5)        // "Dusty" orange-ish fog for a dry heat feel
                        .skyColor(0x70B8FF)        // Deep, clear desert-mountain sky
                        .grassColorOverride(0xBAA451) // Burnt yellow grass
                        .foliageColorOverride(0x827732)
                        .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)

                        // Add a unique ambient sound for the Andes
                        .ambientLoopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP) // Gives a windy, desolate feel
                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(subGen.build())
                .build();
    }

    private static void addGlobalGeneration(BiomeGenerationSettings.Builder builder, HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        builder.addCarver(GenerationStep.Carving.AIR, carvers.getOrThrow(Carvers.CAVE));
        builder.addCarver(GenerationStep.Carving.AIR, carvers.getOrThrow(Carvers.CANYON));

        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(OrePlacements.ORE_DIRT));
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(OrePlacements.ORE_GRAVEL));
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(OrePlacements.ORE_GRANITE_UPPER));

        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(OrePlacements.ORE_COAL_UPPER));
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(OrePlacements.ORE_IRON_UPPER));
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(OrePlacements.ORE_GOLD_LOWER));
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(OrePlacements.ORE_DIAMOND_LARGE));

        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(NTPlacedFeatures.LIGNITE_ORE_PLACED_KEY));
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(NTPlacedFeatures.OPAL_ORE_PLACED_KEY));
    }

    private static void addGlobalSpawns(MobSpawnSettings.Builder builder) {
        BiomeDefaultFeatures.commonSpawns(builder);
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 10, 2, 4));
        builder.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 12, 4, 4));
    }
}