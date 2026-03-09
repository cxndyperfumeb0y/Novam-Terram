package net.candyperfumeboy.novamterram.worldgen.biome;

import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.worldgen.NTPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class NTBiomeFactory {

    public static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(NTBiomes.BLUE_GROVE, createBlueGrove(features, carvers));
        context.register(NTBiomes.AEGEAN, createAegean(features, carvers));
        context.register(NTBiomes.MAQUIS, createMaquis(features, carvers));
        context.register(NTBiomes.HEATH, createHeath(features, carvers));
        context.register(NTBiomes.ANDEAN_HILLS, createAndeanHills(features, carvers));
        context.register(NTBiomes.RAINFOREST, createRainforest(features, carvers));
        context.register(NTBiomes.CYPRESS_FEN, createCypressFen(features, carvers));
        context.register(NTBiomes.MIRROR_MIRE, createMirrorMire(features, carvers));
        context.register(NTBiomes.AUTUMNAL_TAIGA, createAutumnalTaiga(features, carvers));
        context.register(NTBiomes.MYRTLE_FIELDS, createMyrtleFields(features, carvers));
        context.register(NTBiomes.ROYAL_FOREST, createRoyalForest(features, carvers));
        context.register(NTBiomes.SAFFRON_GROVE, createSaffronGrove(features, carvers));
        context.register(NTBiomes.AMBER_SANDS, createAmberSands(features, carvers));
        context.register(NTBiomes.FUNGAL_REACH, createFungalReach(features, carvers));
        context.register(NTBiomes.CALDERA_GROTTO, createCalderaGrotto(features, carvers));

    }

    private static BiomeGenerationSettings.Builder baseGen(HolderGetter<PlacedFeature> f, HolderGetter<ConfiguredWorldCarver<?>> c) {
        BiomeGenerationSettings.Builder gen = new BiomeGenerationSettings.Builder(f, c);
        BiomeDefaultFeatures.addDefaultCarversAndLakes(gen);
        BiomeDefaultFeatures.addDefaultOres(gen);
        return gen;
    }

    private static MobSpawnSettings.Builder baseSpawns() {
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns(spawns); // Hostiles
        BiomeDefaultFeatures.farmAnimals(spawns);  // Passives
        BiomeDefaultFeatures.caveSpawns(spawns);   // Bats/Ambient
        return spawns;
    }

    // --- BIOME DEFINITIONS ---

    public static Biome createAutumnalTaiga(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        // Add Vanilla Taiga Features (Using local copies to avoid cycles)
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AUTUMNAL_LARGE_FERN));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AUTUMNAL_DEAD_BUSH));

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AUTUMNAL_VEGETATION_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AUTUMNAL_PINE_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.SMALL_PINE_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.PUMPKIN_CLUSTER_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AUTUMNAL_LEAF_PILE_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AUTUMNAL_FLOWERS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AUTUMNAL_BOULDER_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AUTUMNAL_PUMPKIN_PLACED));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FOX, 8, 2, 4));

        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.3f).downfall(0.4f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x5DB7EF).waterFogColor(0x1B262E).fogColor(0xD9E2E5).skyColor(0x9EB3C2) // Brighter Blue Water
                        .grassColorOverride(0xA8B05B).foliageColorOverride(0x99AA44)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createAmberSands(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();
        BiomeDefaultFeatures.desertSpawns(spawns);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CLIFF_SPIKE_PLACED));

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AMBER_SANDS_VEGETATION_PLACED));


        // gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.PETRIFIED_CACTUS_PLACED));
        // gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(NTPlacedFeatures.AMBER_ORE_PLACED));

        return new Biome.BiomeBuilder().hasPrecipitation(false).temperature(2.0f).downfall(0.0f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x43D5EE).waterFogColor(0x3E2723)
                        .fogColor(0xFFCC88).skyColor(0x77ADFF)
                        .grassColorOverride(0xC6A15B).foliageColorOverride(0xBA8B02)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BADLANDS)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createSaffronGrove(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.BROWN_BEAR.get(), 25, 3, 6));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.SILVER_ASPEN_PLACED_KEY));

        gen.addFeature(GenerationStep.Decoration.LAKES, features.getOrThrow(MiscOverworldPlacements.SPRING_WATER));

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.SAFFRON_VEGETATION_PLACED));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 40, 2, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 10, 1, 3));

        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.9f).downfall(0.8f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x61A2B1).waterFogColor(0x1B262E)
                        .fogColor(0xEEDC82) // Light Goldenrod Haze (Less oppressive)
                        .skyColor(0x77ADFF) // Bright Cyan/Blue Sky
                        .grassColorOverride(0xBDB926) // Saffron-ish Green (Yellow-Green)
                        .foliageColorOverride(0x9ad331) // Copper/Bronze foliage
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }


    public static Biome createFungalReach(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.MOOSHROOM, 8, 2, 4));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.FUNGAL_REACH_VEGETATION_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.FUNGAL_CANOPY_TREE_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.FEN_MUSHROOM_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.HUGE_MUSHROOM_COMMON_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.RAINFOREST_MELON));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.HEATH_WATERLILY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLEEDING_HEART_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MUSHROOM_PATCH_PLACED));

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.RARE_ANCIENT_OAK_PLACED));

        // gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.SHELF_FUNGI_PLACED));
        // gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.GIANT_FUNGAL_TREE_PLACED));

        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.3f).downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x2f346f).waterFogColor(0x0C0514)
                        .fogColor(0x7878d9).skyColor(0x69a5bf)
                        .grassColorOverride(0x436b54).foliageColorOverride(0x14662d)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.SPORE_BLOSSOM_AIR, 0.01f))
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }


    public static Biome createMyrtleFields(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.BROWN_BEAR.get(), 25, 3, 6));

        // Add Meadow Features
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MYRTLE_TALL_GRASS));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MYRTLE_MELON));

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MYRTLE_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MYRTLE_FLOWERS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MYRTLE_SHRUB_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CHERRY_TREE_RARE_PLACED));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 15, 2, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.BEE, 5, 1, 2));

        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.6f).downfall(0.6f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x4F7BA3).waterFogColor(0x0A233B).fogColor(0xD7E9FF).skyColor(0x78A7FF)
                        .grassColorOverride(0x7EB15B).foliageColorOverride(0x6CA34F)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_MEADOW)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createRoyalForest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultCrystalFormations(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.BROWN_BEAR.get(), 25, 3, 6));



        // Add Magical/Overgrown Features
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.ROYAL_TALL_GRASS));

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.PAULOWNIA_TREE_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.ROYAL_FLOWER_PATCH_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.IRIS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLEEDING_HEART_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.SILVER_SAGE_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(MiscOverworldPlacements.FOREST_ROCK));
        gen.addFeature(GenerationStep.Decoration.LAKES, features.getOrThrow(MiscOverworldPlacements.SPRING_WATER));
        gen.addFeature(GenerationStep.Decoration.LAKES, features.getOrThrow(NTPlacedFeatures.HEATH_POND_PLACED));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.FAUN.get(), 15, 1, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.SONGBIRD.get(), 15, 1, 6));

        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.7f).downfall(0.8f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x43D5EE).waterFogColor(0x1F2A38).fogColor(0xEAD9FF).skyColor(0xAFB5FF)
                        .grassColorOverride(0x6CB37C).foliageColorOverride(0x529B63)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.CHERRY_LEAVES, 0.005f))
                        .ambientLoopSound(SoundEvents.MUSIC_BIOME_CHERRY_GROVE)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_CHERRY_GROVE)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }


    public static Biome createCalderaGrotto(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);

        // --- 1. GEOLOGY (Carved into the terrain first) ---
        gen.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, features.getOrThrow(NTPlacedFeatures.PERIDOT_SPRING_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CLIFF_SPIKE_PLACED));

        // --- 2. UNDERGROWTH & VENTS (The "Floor" layer) ---
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.GEOTHERMAL_VENT_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.GROTTO_VEGETATION_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.RAINFOREST_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AEGEAN_SUNFLOWER));

        // --- 3. THE CANOPY (Trees added last to check for space) ---
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.GROTTO_TREE_PLACED));

        // Vanilla Lush Cave features (Azalea/Dripleaves) fit the caldera vibe well
        BiomeDefaultFeatures.addLushCavesVegetationFeatures(gen);

        MobSpawnSettings.Builder spawns = baseSpawns();
        spawns.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 30, 4, 8));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 40, 1, 2));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 15, 1, 2)); // Slightly higher spawn rate for your NTOcelotEvents!

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.95f)
                .downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x11FFCC)
                        .waterFogColor(0x053322)
                        .fogColor(0xDBF0E5)
                        .skyColor(0x75CDFF)
                        .grassColorOverride(0x33CC33)
                        .foliageColorOverride(0x22AA55)
                        // Selling the "Active Volcano" theme with a bit of steam/ash
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_LUSH_CAVES)).build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createBlueGrove(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();
        // Adding Strays for the cold atmosphere
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.STRAY, 80, 4, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.BROWN_BEAR.get(), 25, 3, 6));

        // Add Cold/Mystic Features
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLUE_GROVE_LARGE_FERN));

        gen.addFeature(GenerationStep.Decoration.LAKES, features.getOrThrow(MiscOverworldPlacements.SPRING_WATER));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLUESPRUCE_TAIGA_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MEGA_BLUE_SPRUCE_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.SILVER_ASPEN_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NTPlacedFeatures.SILVER_FOX_DEN_PLACED);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLUE_GROVE_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLUE_GROVE_FLOWER_PATCH_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.ASTER_PETALS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.OXEYE_DAISY_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLUE_GROVE_SNOW_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.FALLEN_ASPEN_LOG_PLACED));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.SILVER_FOX.get(), 15, 4, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 10, 4, 4));

        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.2f).downfall(0.4f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x34568B).waterFogColor(0x101820).fogColor(0xA3C5D9).skyColor(0x8AB9D1)
                        .grassColorOverride(0x718E82).foliageColorOverride(0x536D6B)
                        .ambientLoopSound(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createHeath(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        // Add Shrubland Features
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.HEATH_DEAD_BUSH));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.HEATH_GRASS_PLAIN));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.HEATH_PUMPKIN));

        gen.addFeature(GenerationStep.Decoration.LAKES, features.getOrThrow(MiscOverworldPlacements.SPRING_WATER));
        gen.addFeature(GenerationStep.Decoration.LAKES, features.getOrThrow(NTPlacedFeatures.HEATH_POND_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.HEATHER_PATCH_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.HEATHER_PETALS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.LAVENDER_PATCH_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.HEATH_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.ALLIUM_PATCH_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.HEATH_WATERLILY));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 100, 4, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 25, 1, 2));

        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.5f).downfall(0.5f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x5E7C8C).waterFogColor(0x232323).fogColor(0xC0D8FF).skyColor(0x78A7FF)
                        .grassColorOverride(0x899B5C).foliageColorOverride(0x778A4A)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_MEADOW)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createAegean(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        // Add Mediterranean Features
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AEGEAN_SUNFLOWER));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AEGEAN_SUGAR_CANE));

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.LAVENDER_PATCH_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.LAVENDER_PETALS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CALCITE_BOULDER_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MEDITERRANEAN_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.FLOWER_MEADOW));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.OLIVE_TREE_PLACED_KEY));

        spawns.addSpawn(MobCategory.WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DOLPHIN, 12, 1, 3));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.TURTLE, 18, 2, 4));
        spawns.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 4, 8));
        spawns.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.PUFFERFISH, 10, 1, 3));

        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.8f).downfall(0.4f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3196c9).waterFogColor(0x052d3d).fogColor(0xC0D8FF).skyColor(0x88BBFF)
                        .grassColorOverride(0x6ab25c).foliageColorOverride(0x558C50)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_CHERRY_GROVE)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createMaquis(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        // Add Mediterranean Features
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MAQUIS_SUNFLOWER));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MAQUIS_SUGAR_CANE));

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.LAVENDER_PATCH_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.LAVENDER_PETALS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CALCITE_BOULDER_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MEDITERRANEAN_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.OLIVE_TREE_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MAQUIS_SHRUB_PLACED_KEY));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 15, 2, 4));

        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.9f).downfall(0.3f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3196c9).waterFogColor(0x050533).fogColor(0xE7D9B1).skyColor(0x78A7FF)
                        .grassColorOverride(0x91BD59).foliageColorOverride(0x77AB2F)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_STONY_PEAKS)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createAndeanHills(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        // Add High Altitude Features
        // Removed Powder Snow/Stone as they weren't in safe list

        gen.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, features.getOrThrow(NTPlacedFeatures.ANDEAN_SPIRE_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NTPlacedFeatures.ANDEAN_BEAR_CAVE_PLACED);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.TUFF_BOULDER_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.ANDEAN_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.ANDEAN_VEGETATION_PLACED));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.SPECTACLED_BEAR.get(), 25, 3, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 25, 4, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 25, 4, 6));

        return new Biome.BiomeBuilder().hasPrecipitation(false).temperature(1f).downfall(0.0f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3F76E4).waterFogColor(0x050533).fogColor(0xFFE4B5).skyColor(0x70B8FF)
                        .grassColorOverride(0x8DB360).foliageColorOverride(0x6E914A)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createRainforest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();
        // Rare Panda spawns for extra rainforest flavor
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 5, 1, 2));

        // Add Jungle Features
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.RAINFOREST_MELON));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.RAINFOREST_WATERLILY));

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.TEAK_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BALSA_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MAHOGANY_SHRUB_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.RAINFOREST_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.FLOWER_MEADOW));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 25, 3, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 10, 4, 8));

        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.95f).downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x1B9E77).waterFogColor(0x0E4D3B).fogColor(0x2E4D2E).skyColor(0x77ADFF)
                        .grassColorOverride(0x288028).foliageColorOverride(0x1E691E)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createCypressFen(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();
        // Bogged and Slimes are perfect for a Fen
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.BOGGED, 20, 4, 4));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 15, 1, 1));

        // Add Swamp Features (Using Biome-Specific Copies)
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CYPRESS_WATERLILY));

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CYPRESS_TREE_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CYPRESS_LILY_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.FEN_MUSHROOM_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CYPRESS_GRASS_PLACED));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 50, 3, 5));

        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(0.8f).downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x614E33).waterFogColor(0x302518).fogColor(0xC9B69B).skyColor(0xD6B88D)
                        .grassColorOverride(0x6A7039).foliageColorOverride(0x565C2E)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createMirrorMire(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();
        // Mirror Mire gets Strays and Glow Squids
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.STRAY, 40, 4, 4));
        spawns.addSpawn(MobCategory.UNDERGROUND_WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GLOW_SQUID, 10, 4, 6));

        // Add Swamp Features
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MIRROR_WATERLILY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MIRROR_DEAD_BUSH));

        gen.addFeature(GenerationStep.Decoration.LAKES, features.getOrThrow(MiscOverworldPlacements.SPRING_WATER));
        gen.addFeature(GenerationStep.Decoration.LAKES, features.getOrThrow(NTPlacedFeatures.HEATH_POND_PLACED));
        gen.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, features.getOrThrow(NTPlacedFeatures.SUNKEN_LOG_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLUE_GROVE_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLUE_GROVE_FLOWER_PATCH_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MIRROR_ICE_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.FROSTED_REEDS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MIRROR_MIRE_TREES));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 20, 2, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.SILVER_FOX.get(), 10, 1, 2));

        return new Biome.BiomeBuilder().hasPrecipitation(true).temperature(-0.05f).downfall(1f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3D5E7A).waterFogColor(0x1A2A36).fogColor(0xDAE5EB).skyColor(0xA4C0D3)
                        .grassColorOverride(0x94B0AF).foliageColorOverride(0x829E9D)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }
}