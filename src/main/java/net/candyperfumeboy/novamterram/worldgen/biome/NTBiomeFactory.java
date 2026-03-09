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
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Music;
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

        context.register(NTBiomes.BLUE_GROVE,      createBlueGrove(features, carvers));
        context.register(NTBiomes.AEGEAN,           createAegean(features, carvers));
        context.register(NTBiomes.MAQUIS,           createMaquis(features, carvers));
        context.register(NTBiomes.HEATH,            createHeath(features, carvers));
        context.register(NTBiomes.ANDEAN_HILLS,     createAndeanHills(features, carvers));
        context.register(NTBiomes.RAINFOREST,       createRainforest(features, carvers));
        context.register(NTBiomes.CYPRESS_FEN,      createCypressFen(features, carvers));
        context.register(NTBiomes.MIRROR_MIRE,      createMirrorMire(features, carvers));
        context.register(NTBiomes.AUTUMNAL_TAIGA,   createAutumnalTaiga(features, carvers));
        context.register(NTBiomes.MYRTLE_FIELDS,    createMyrtleFields(features, carvers));
        context.register(NTBiomes.ROYAL_FOREST,     createRoyalForest(features, carvers));
        context.register(NTBiomes.SAFFRON_GROVE,    createSaffronGrove(features, carvers));
        context.register(NTBiomes.AMBER_SANDS,      createAmberSands(features, carvers));
        context.register(NTBiomes.FUNGAL_REACH,     createFungalReach(features, carvers));
        context.register(NTBiomes.CALDERA_GROTTO,   createCalderaGrotto(features, carvers));
        context.register(NTBiomes.BLUEBELL_WOODS,        createBluebellWoods(features, carvers));
        context.register(NTBiomes.BLACK_FOREST,          createBlackForest(features, carvers));
        context.register(NTBiomes.SILVER_BIRCH_WETLAND,  createSilverBirchWetland(features, carvers));
        context.register(NTBiomes.COASTAL_CHAPARRAL,     createCoastalChaparral(features, carvers));
        context.register(NTBiomes.MANGROVE_LAGOON,       createMangroveLagoon(features, carvers));
        context.register(NTBiomes.LARCH_FOREST,          createLarchForest(features, carvers));
        context.register(NTBiomes.OZARK_FOREST,          createOzarkForest(features, carvers));
        context.register(NTBiomes.CLOUD_FOREST,          createCloudForest(features, carvers));
        context.register(NTBiomes.CEDAR_SLOPES,          createCedarSlopes(features, carvers));
        context.register(NTBiomes.GALLERY_FOREST,        createGalleryForest(features, carvers));
    }

    // -------------------------------------------------------------------------
    // BASE BUILDERS
    // -------------------------------------------------------------------------

    /**
     * Shared generation base: default carvers, lakes, and ores.
     * All NT biomes build on this foundation.
     */
    private static BiomeGenerationSettings.Builder baseGen(
            HolderGetter<PlacedFeature> f,
            HolderGetter<ConfiguredWorldCarver<?>> c) {
        BiomeGenerationSettings.Builder gen = new BiomeGenerationSettings.Builder(f, c);
        BiomeDefaultFeatures.addDefaultCarversAndLakes(gen);
        BiomeDefaultFeatures.addDefaultOres(gen);
        return gen;
    }

    /**
     * Shared mob spawn base: vanilla hostiles, farm animals, and cave ambient mobs.
     */
    private static MobSpawnSettings.Builder baseSpawns() {
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns(spawns);
        BiomeDefaultFeatures.farmAnimals(spawns);
        BiomeDefaultFeatures.caveSpawns(spawns);
        return spawns;
    }

    // -------------------------------------------------------------------------
    // BIOME DEFINITIONS
    // -------------------------------------------------------------------------

    public static Biome createAutumnalTaiga(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

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

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.3f)
                .downfall(0.4f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x5DB7EF)
                        .waterFogColor(0x1B262E)
                        .fogColor(0xD9E2E5)
                        .skyColor(0x9EB3C2)
                        .grassColorOverride(0xA8B05B)
                        .foliageColorOverride(0x99AA44)
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA,
                                6000, 12000, true
                        ))                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createAmberSands(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();
        BiomeDefaultFeatures.desertSpawns(spawns);

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CLIFF_SPIKE_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AMBER_SANDS_VEGETATION_PLACED));
        // TODO: Uncomment when features are implemented:
        // gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.PETRIFIED_CACTUS_PLACED));
        // gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(NTPlacedFeatures.AMBER_ORE_PLACED));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(2.0f)
                .downfall(0.0f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x43D5EE)
                        .waterFogColor(0x3E2723)
                        .fogColor(0xFFCC88)
                        .skyColor(0x77ADFF)
                        .grassColorOverride(0xC6A15B)
                        .foliageColorOverride(0xBA8B02)
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_BADLANDS,
                                6000, 12000, true
                        ))                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createSaffronGrove(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.SILVER_ASPEN_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.LAKES, features.getOrThrow(MiscOverworldPlacements.SPRING_WATER));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.SAFFRON_VEGETATION_PLACED));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.BROWN_BEAR.get(), 25, 3, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 40, 2, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 10, 1, 3));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.9f)
                .downfall(0.8f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x61A2B1)
                        .waterFogColor(0x1B262E)
                        .fogColor(0xEEDC82)
                        .skyColor(0x77ADFF)
                        .grassColorOverride(0xBDB926)
                        .foliageColorOverride(0x9AD331)
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_FOREST,
                                6000, 12000, true
                        ))                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createFungalReach(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.FUNGAL_REACH_VEGETATION_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.FUNGAL_CANOPY_TREE_PLACED));
      gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.FEN_MUSHROOM_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.HUGE_MUSHROOM_COMMON_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.RAINFOREST_MELON));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.HEATH_WATERLILY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLEEDING_HEART_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MUSHROOM_PATCH_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.RARE_ANCIENT_OAK_PLACED));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.MOOSHROOM, 8, 2, 4));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.3f)
                .downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x2F346F)
                        .waterFogColor(0x0C0514)
                        .fogColor(0x7878D9)
                        .skyColor(0x69A5BF)
                        .grassColorOverride(0x436B54)
                        .foliageColorOverride(0x14662D)
                        // Spore Blossom particles enhance the deep-forest mushroom atmosphere
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.SPORE_BLOSSOM_AIR, 0.01f))
                        // Cave ambience fits the enclosed, shadowy feel
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_SWAMP,
                                6000, 12000, true
                        ))                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createMyrtleFields(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MYRTLE_TALL_GRASS));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MYRTLE_MELON));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MYRTLE_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MYRTLE_FLOWERS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MYRTLE_TREES_PLACED));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.BROWN_BEAR.get(), 25, 3, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 15, 2, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.BEE, 5, 1, 2));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.6f)
                .downfall(0.6f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x4F7BA3)
                        .waterFogColor(0x0A233B)
                        .fogColor(0xD7E9FF)
                        .skyColor(0x78A7FF)
                        .grassColorOverride(0x65ae56)
                        .foliageColorOverride(0x7cc157)
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_MEADOW,
                                6000, 12000, true
                        ))                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createRoyalForest(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultCrystalFormations(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.ROYAL_TALL_GRASS));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.PAULOWNIA_TREE_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.ROYAL_FLOWER_PATCH_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.IRIS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLEEDING_HEART_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.SILVER_SAGE_PLACED));
        gen.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, features.getOrThrow(MiscOverworldPlacements.FOREST_ROCK));
        gen.addFeature(GenerationStep.Decoration.LAKES, features.getOrThrow(MiscOverworldPlacements.SPRING_WATER));
        gen.addFeature(GenerationStep.Decoration.LAKES, features.getOrThrow(NTPlacedFeatures.HEATH_POND_PLACED));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.BROWN_BEAR.get(), 25, 3, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.FAUN.get(), 15, 1, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.SONGBIRD.get(), 15, 1, 6));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7f)
                .downfall(0.8f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x43D5EE)
                        .waterFogColor(0x1F2A38)
                        .fogColor(0xEAD9FF)
                        .skyColor(0xAFB5FF)
                        .grassColorOverride(0x6CB37C)
                        .foliageColorOverride(0x529B63)
                        // Cherry petal particles lend the "Royal" magical quality
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.CHERRY_LEAVES, 0.005f))
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_CHERRY_GROVE,
                                6000, 12000, true
                        ))
                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createCalderaGrotto(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);

        gen.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, features.getOrThrow(NTPlacedFeatures.PERIDOT_SPRING_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CLIFF_SPIKE_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.GEOTHERMAL_VENT_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.GROTTO_VEGETATION_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.RAINFOREST_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.AEGEAN_SUNFLOWER));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.GROTTO_TREE_PLACED));
        BiomeDefaultFeatures.addLushCavesVegetationFeatures(gen);

        MobSpawnSettings.Builder spawns = baseSpawns();
        spawns.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 30, 4, 8));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 40, 1, 2));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 15, 1, 2));

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
                        // Ash particles sell the active-volcano caldera theme
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.ASH, 0.005f))
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_JUNGLE,
                                6000, 12000, true
                        ))                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createBlueGrove(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

     gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLUE_GROVE_LARGE_FERN));
        gen.addFeature(GenerationStep.Decoration.LAKES, features.getOrThrow(MiscOverworldPlacements.SPRING_WATER));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLUESPRUCE_TAIGA_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MEGA_BLUE_SPRUCE_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.SILVER_ASPEN_PLACED_KEY));
        // NOTE: SILVER_FOX_DEN_PLACED is referenced as a direct Holder field (not via getOrThrow).
        //       Verify NTPlacedFeatures declares it as: public static final Holder<PlacedFeature> SILVER_FOX_DEN_PLACED = ...
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NTPlacedFeatures.SILVER_FOX_DEN_PLACED);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLUE_GROVE_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BLUE_GROVE_FLOWER_PATCH_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.ASTER_PETALS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.OXEYE_DAISY_PLACED));
        gen.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, features.getOrThrow(NTPlacedFeatures.BLUE_GROVE_SNOW_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.FALLEN_ASPEN_LOG_PLACED));

        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.STRAY, 80, 4, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.BROWN_BEAR.get(), 25, 3, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.SILVER_FOX.get(), 15, 4, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 10, 4, 4));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.2f)
                .downfall(0.4f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x34568B)
                        .waterFogColor(0x101820)
                        .fogColor(0xA3C5D9)
                        .skyColor(0x8AB9D1)
                        .grassColorOverride(0x718E82)
                        .foliageColorOverride(0x536D6B)
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_GROVE,
                                6000, 12000, true
                        ))
                        // 2. The Texture: Set this to a looping environmental sound
                        // Use an ambient sound here, NOT a music track!
                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createHeath(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

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

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.5f)
                .downfall(0.5f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x5E7C8C)
                        .waterFogColor(0x232323)
                        .fogColor(0xC0D8FF)
                        .skyColor(0x78A7FF)
                        .grassColorOverride(0x899B5C)
                        .foliageColorOverride(0x778A4A)
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_MEADOW,
                                6000, 12000, true
                        ))
                        // 2. The Texture: Set this to a looping environmental sound
                        // Use an ambient sound here, NOT a music track!
                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createAegean(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

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

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.8f)
                .downfall(0.4f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3196C9)
                        .waterFogColor(0x052D3D)
                        .fogColor(0xC0D8FF)
                        .skyColor(0x88BBFF)
                        .grassColorOverride(0x6AB25C)
                        .foliageColorOverride(0x558C50)
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_FLOWER_FOREST,
                                6000, 12000, true
                        ))                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createMaquis(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MAQUIS_SUNFLOWER));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MAQUIS_SUGAR_CANE));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.LAVENDER_PATCH_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.LAVENDER_PETALS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CALCITE_BOULDER_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MEDITERRANEAN_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.OLIVE_TREE_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MAQUIS_SHRUB_PLACED_KEY));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 15, 2, 4));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.9f)
                .downfall(0.3f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3196C9)
                        .waterFogColor(0x050533)
                        .fogColor(0xE7D9B1)
                        .skyColor(0x78A7FF)
                        .grassColorOverride(0x91BD59)
                        .foliageColorOverride(0x77AB2F)
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_GROVE,
                                6000, 12000, true
                        ))
                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createAndeanHills(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, features.getOrThrow(NTPlacedFeatures.ANDEAN_SPIRE_PLACED_KEY));
        // NOTE: ANDEAN_BEAR_CAVE_PLACED is referenced as a direct Holder field (not via getOrThrow).
        //       Verify NTPlacedFeatures declares it as: public static final Holder<PlacedFeature> ANDEAN_BEAR_CAVE_PLACED = ...
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, NTPlacedFeatures.ANDEAN_BEAR_CAVE_PLACED);
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.TUFF_BOULDER_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.ANDEAN_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.ANDEAN_VEGETATION_PLACED));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.SPECTACLED_BEAR.get(), 25, 3, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 25, 4, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.LLAMA, 25, 4, 6));

        // FIXED: Changed hasPrecipitation to true and temperature to 0.25f for a proper Andean
        // alpine climate — cold, misty highlands with precipitation (snow at elevation).
        // Previous values (hasPrecipitation=false, temp=1.0f) produced a hot-desert climate
        // that conflicted with the intended high-altitude mountain biome theme.
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.25f)
                .downfall(0.3f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3F76E4)
                        .waterFogColor(0x050533)
                        .fogColor(0xFFE4B5)
                        .skyColor(0x70B8FF)
                        .grassColorOverride(0xadd57b)
                        .foliageColorOverride(0x6E914A)
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_JAGGED_PEAKS,
                                6000, 12000, true
                        ))
                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createRainforest(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.RAINFOREST_MELON));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.RAINFOREST_WATERLILY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.TEAK_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.BALSA_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.MAHOGANY_SHRUB_PLACED_KEY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.RAINFOREST_GRASS_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.FLOWER_MEADOW));

        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PANDA, 5, 1, 2));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.OCELOT, 25, 3, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PARROT, 10, 4, 8));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.95f)
                .downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x1B9E77)
                        .waterFogColor(0x0E4D3B)
                        .fogColor(0x2E4D2E)
                        .skyColor(0x77ADFF)
                        .grassColorOverride(0x288028)
                        .foliageColorOverride(0x1E691E)
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_BAMBOO_JUNGLE,
                                6000, 12000, true
                        ))                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createCypressFen(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CYPRESS_WATERLILY));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CYPRESS_TREE_PLACED));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CYPRESS_LILY_PLACED));
       gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.FEN_MUSHROOM_PLACED));
       gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.CYPRESS_GRASS_PLACED));

        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.BOGGED, 20, 4, 4));
        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 15, 1, 1));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 50, 3, 5));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.8f)
                .downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x614E33)
                        .waterFogColor(0x302518)
                        .fogColor(0xC9B69B)
                        .skyColor(0xD6B88D)
                        .grassColorOverride(0x6A7039)
                        .foliageColorOverride(0x565C2E)
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_SWAMP,
                                6000, 12000, true
                        ))                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    public static Biome createMirrorMire(
            HolderGetter<PlacedFeature> features,
            HolderGetter<ConfiguredWorldCarver<?>> carvers) {

        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

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

        spawns.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.STRAY, 40, 4, 4));
        spawns.addSpawn(MobCategory.UNDERGROUND_WATER_CREATURE, new MobSpawnSettings.SpawnerData(EntityType.GLOW_SQUID, 10, 4, 6));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.FROG, 20, 2, 4));
        spawns.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(NTEntities.SILVER_FOX.get(), 10, 1, 2));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(-0.05f)
                .downfall(1.0f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3D5E7A)
                        .waterFogColor(0x1A2A36)
                        .fogColor(0xDAE5EB)
                        .skyColor(0xA4C0D3)
                        .grassColorOverride(0x94B0AF)
                        .foliageColorOverride(0x829E9D)
                        .backgroundMusic(new Music(
                                SoundEvents.MUSIC_BIOME_SWAMP,
                                6000, 12000, true
                        ))                        .build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }
    public static Biome createBluebellWoods(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.TREES_BIRCH_AND_OAK));
        // You'll likely want a custom feature for a thick carpet of blue flowers here
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.PATCH_TALL_GRASS));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.6f).downfall(0.6f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x44aff5).waterFogColor(0x001d3d).fogColor(0xccd9ff).skyColor(0x78a7ff)
                        .grassColorOverride(0x5dbb70).foliageColorOverride(0x5dbb70)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FOREST)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createBlackForest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.DARK_FOREST_VEGETATION));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.PATCH_GRASS_JUNGLE));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.5f).downfall(0.8f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x1e2b21).waterFogColor(0x050505).fogColor(0x1a1a1a).skyColor(0x2d3436)
                        .grassColorOverride(0x497949).foliageColorOverride(0x2b642b)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createSilverBirchWetland(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.BIRCH_TALL));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.PATCH_WATERLILY));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.6f).downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x617b64).waterFogColor(0x1d2d30).fogColor(0xd9e5d6).skyColor(0x9eb3c2)
                        .grassColorOverride(0x7fb083).foliageColorOverride(0x699e6d)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createCoastalChaparral(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();
        BiomeDefaultFeatures.desertSpawns(spawns);

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.PATCH_DEAD_BUSH_2));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.TREES_WINDSWEPT_HILLS));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false).temperature(1.2f).downfall(0.0f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3196c9).waterFogColor(0x052d3d).fogColor(0xdbcc9f).skyColor(0x88bbff)
                        .grassColorOverride(0x9d9f5a).foliageColorOverride(0x8c8f4a)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BADLANDS)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createMangroveLagoon(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();
        spawns.addSpawn(MobCategory.WATER_AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.TROPICAL_FISH, 25, 8, 8));

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.TREES_MANGROVE));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.9f).downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x11ffcc).waterFogColor(0x053322).fogColor(0xb2f0e0).skyColor(0x75cdff)
                        .grassColorOverride(0x56ee56).foliageColorOverride(0x3eb13e)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createLarchForest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        // Use your custom Larch feature if available, else standard Spruce
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.TREES_GROVE));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.PATCH_GRASS_TAIGA));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.1f).downfall(0.4f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3d567a).waterFogColor(0x101a2b).fogColor(0xc9d1d9).skyColor(0x9fb6c2)
                        .grassColorOverride(0x8da173).foliageColorOverride(0x7a8c63)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createOzarkForest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, features.getOrThrow(MiscOverworldPlacements.FOREST_ROCK));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.TREES_WINDSWEPT_FOREST));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.7f).downfall(0.6f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x4a6a8a).waterFogColor(0x102130).fogColor(0xdde6ed).skyColor(0x77adff)
                        .grassColorOverride(0x7fb058).foliageColorOverride(0x6b9e4a)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FOREST)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createCloudForest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.VINES));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.TREES_JUNGLE));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.8f).downfall(1.0f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x2f5a5a).waterFogColor(0x0c1a1a).fogColor(0x7ea3a3).skyColor(0x94b8b8)
                        .grassColorOverride(0x4d8a5a).foliageColorOverride(0x3b7546)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.WHITE_ASH, 0.008f))
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createCedarSlopes(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.TREES_WINDSWEPT_HILLS));
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(OrePlacements.ORE_EMERALD));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.1f).downfall(0.3f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x3e567a).waterFogColor(0x101a2b).fogColor(0xc9d1d9).skyColor(0x9fb6c2)
                        .grassColorOverride(0x6e8e7c).foliageColorOverride(0x5a7a67)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }

    public static Biome createGalleryForest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.TREES_JUNGLE));
        gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(VegetationPlacements.PATCH_SUGAR_CANE));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(0.95f).downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x1bb33a).waterFogColor(0x052b10).fogColor(0xdbf0e5).skyColor(0x75cdff)
                        .grassColorOverride(0x33cc33).foliageColorOverride(0x22aa55)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SPARSE_JUNGLE)).build())
                .mobSpawnSettings(spawns.build()).generationSettings(gen.build()).build();
    }



}