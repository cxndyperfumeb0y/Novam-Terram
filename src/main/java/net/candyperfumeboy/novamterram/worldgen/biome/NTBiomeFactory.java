package net.candyperfumeboy.novamterram.worldgen.biome;

import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.worldgen.NTPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;

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
        BiomeDefaultFeatures.addDefaultCrystalFormations(gen);
        BiomeDefaultFeatures.addDefaultMonsterRoom(gen);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(gen);
        BiomeDefaultFeatures.addDefaultOres(gen);
        BiomeDefaultFeatures.addDefaultSprings(gen);
        BiomeDefaultFeatures.addSurfaceFreezing(gen);
        return gen;
    }

    /**
     * Shared mob spawn base: vanilla hostiles, farm animals, and cave ambient mobs.
     */
    private static MobSpawnSettings.Builder baseSpawns() {
        MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns(spawns);
        BiomeDefaultFeatures.farmAnimals(spawns);
        return spawns;
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    /** Adds features to one generation step, in the order written. */
    @SafeVarargs
    private static void add(BiomeGenerationSettings.Builder gen, Decoration step, ResourceKey<PlacedFeature>... placed) {
        for (ResourceKey<PlacedFeature> key : placed) {
            gen.addFeature(step, key);
        }
    }

    private static void spawn(MobSpawnSettings.Builder spawns, MobCategory category, EntityType<?> type,
                              int weight, int minGroup, int maxGroup) {
        spawns.addSpawn(category, new MobSpawnSettings.SpawnerData(type, weight, minGroup, maxGroup));
    }

    private static Biome.BiomeBuilder climate(boolean precipitation, float temperature, float downfall) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(precipitation)
                .temperature(temperature)
                .downfall(downfall);
    }

    private static BiomeSpecialEffects.Builder colors(int water, int waterFog, int fog, int sky, int grass, int foliage) {
        return new BiomeSpecialEffects.Builder()
                .waterColor(water)
                .waterFogColor(waterFog)
                .fogColor(fog)
                .skyColor(sky)
                .grassColorOverride(grass)
                .foliageColorOverride(foliage);
    }

    private static Music calmMusic(Holder<SoundEvent> sound) {
        return new Music(sound, 6000, 12000, true);
    }

    private static Biome biome(Biome.BiomeBuilder climate, BiomeSpecialEffects.Builder effects,
                               MobSpawnSettings.Builder spawns, BiomeGenerationSettings.Builder gen) {
        return climate
                .specialEffects(effects.build())
                .mobSpawnSettings(spawns.build())
                .generationSettings(gen.build())
                .build();
    }

    // -------------------------------------------------------------------------
    // BIOMES  (colors are: water, water fog, fog, sky, grass, foliage)
    // -------------------------------------------------------------------------

    private static Biome createAutumnalTaiga(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.AUTUMNAL_LARGE_FERN,
                NTPlacedFeatures.AUTUMNAL_DEAD_BUSH,
                NTPlacedFeatures.AUTUMNAL_VEGETATION_PLACED,
                NTPlacedFeatures.AUTUMNAL_PINE_PLACED,
                NTPlacedFeatures.SMALL_PINE_PLACED,
                NTPlacedFeatures.PUMPKIN_CLUSTER_PLACED,
                NTPlacedFeatures.AUTUMNAL_LEAF_PILE_PLACED,
                NTPlacedFeatures.AUTUMNAL_FLOWERS_PLACED,
                NTPlacedFeatures.AUTUMNAL_BOULDER_PLACED,
                NTPlacedFeatures.AUTUMNAL_PUMPKIN_PLACED);

        spawn(spawns, MobCategory.CREATURE, EntityType.FOX, 8, 2, 4);

        return biome(climate(true, 0.3f, 0.4f),
                colors(0x5DB7EF, 0x1B262E, 0xD9E2E5, 0x9EB3C2, 0xA8B05B, 0x99AA44)
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA)),
                spawns, gen);
    }

    private static Biome createAmberSands(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();
        BiomeDefaultFeatures.desertSpawns(spawns);

        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.CLIFF_SPIKE_PLACED,
                NTPlacedFeatures.AMBER_SANDS_VEGETATION_PLACED);
        // TODO: Uncomment when features are implemented:
        // gen.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, features.getOrThrow(NTPlacedFeatures.PETRIFIED_CACTUS_PLACED));
        // gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, features.getOrThrow(NTPlacedFeatures.AMBER_ORE_PLACED));

        return biome(climate(false, 2.0f, 0.0f),
                colors(0x43D5EE, 0x3E2723, 0xFFCC88, 0x77ADFF, 0xC6A15B, 0xBA8B02)
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_BADLANDS)),
                spawns, gen);
    }

    private static Biome createSaffronGrove(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION, NTPlacedFeatures.SILVER_ASPEN_PLACED_KEY);
        add(gen, Decoration.LAKES, MiscOverworldPlacements.SPRING_WATER);
        add(gen, Decoration.VEGETAL_DECORATION, NTPlacedFeatures.SAFFRON_VEGETATION_PLACED);

        spawn(spawns, MobCategory.CREATURE, NTEntities.BROWN_BEAR.get(), 25, 3, 6);
        spawn(spawns, MobCategory.CREATURE, EntityType.PARROT, 40, 2, 4);
        spawn(spawns, MobCategory.CREATURE, EntityType.OCELOT, 10, 1, 3);

        return biome(climate(true, 0.9f, 0.8f),
                colors(0x61A2B1, 0x1B262E, 0xEEDC82, 0x77ADFF, 0xBDB926, 0x9AD331)
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_FOREST)),
                spawns, gen);
    }

    private static Biome createFungalReach(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.FUNGAL_REACH_VEGETATION_PLACED,
                NTPlacedFeatures.FUNGAL_CANOPY_TREE_PLACED,
                NTPlacedFeatures.FEN_MUSHROOM_PLACED,
                NTPlacedFeatures.HUGE_MUSHROOM_COMMON_PLACED,
                NTPlacedFeatures.RAINFOREST_MELON,
                NTPlacedFeatures.HEATH_WATERLILY,
                NTPlacedFeatures.BLEEDING_HEART_PLACED,
                NTPlacedFeatures.MUSHROOM_PATCH_PLACED,
                NTPlacedFeatures.RARE_ANCIENT_OAK_PLACED);

        spawn(spawns, MobCategory.CREATURE, EntityType.MOOSHROOM, 8, 2, 4);

        return biome(climate(true, 0.3f, 0.9f),
                colors(0x2F346F, 0x0C0514, 0x7878D9, 0x69A5BF, 0x436B54, 0x14662D)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.SPORE_BLOSSOM_AIR, 0.01f))
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_SWAMP)),
                spawns, gen);
    }

    private static Biome createMyrtleFields(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.MYRTLE_TALL_GRASS,
                NTPlacedFeatures.MYRTLE_MELON,
                NTPlacedFeatures.MYRTLE_GRASS_PLACED,
                NTPlacedFeatures.MYRTLE_FLOWERS_PLACED,
                NTPlacedFeatures.MYRTLE_TREES_PLACED,
                NTPlacedFeatures.MYRTLE_LEAF_PILE_PLACED);

        spawn(spawns, MobCategory.CREATURE, NTEntities.BROWN_BEAR.get(), 25, 3, 6);
        spawn(spawns, MobCategory.CREATURE, EntityType.RABBIT, 15, 2, 4);
        spawn(spawns, MobCategory.CREATURE, EntityType.BEE, 5, 1, 2);

        return biome(climate(true, 0.6f, 0.6f),
                colors(0x4F7BA3, 0x0A233B, 0xD7E9FF, 0x78A7FF, 0x65ae56, 0x7cc157)
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_MEADOW)),
                spawns, gen);
    }

    private static Biome createRoyalForest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.ROYAL_TALL_GRASS,
                NTPlacedFeatures.PAULOWNIA_TREE_PLACED,
                NTPlacedFeatures.ROYAL_FLOWER_PATCH_PLACED,
                NTPlacedFeatures.IRIS_PLACED,
                NTPlacedFeatures.BLEEDING_HEART_PLACED,
                NTPlacedFeatures.SILVER_SAGE_PLACED,
                NTPlacedFeatures.PAULOWNIA_LEAF_PILE_PLACED);
        add(gen, Decoration.LOCAL_MODIFICATIONS, MiscOverworldPlacements.FOREST_ROCK);
        add(gen, Decoration.LAKES,
                MiscOverworldPlacements.SPRING_WATER,
                NTPlacedFeatures.HEATH_POND_PLACED);

        spawn(spawns, MobCategory.CREATURE, NTEntities.BROWN_BEAR.get(), 25, 3, 6);
        spawn(spawns, MobCategory.CREATURE, NTEntities.FAUN.get(), 15, 1, 4);
        spawn(spawns, MobCategory.CREATURE, NTEntities.SONGBIRD.get(), 15, 1, 6);

        return biome(climate(true, 0.7f, 0.8f),
                colors(0x43D5EE, 0x1F2A38, 0xEAD9FF, 0xAFB5FF, 0x6CB37C, 0x529B63)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.CHERRY_LEAVES, 0.005f))
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_CHERRY_GROVE)),
                spawns, gen);
    }

    private static Biome createCalderaGrotto(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);

        add(gen, Decoration.LOCAL_MODIFICATIONS, NTPlacedFeatures.PERIDOT_SPRING_PLACED);
        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.CLIFF_SPIKE_PLACED,
                NTPlacedFeatures.GEOTHERMAL_VENT_PLACED,
                NTPlacedFeatures.GROTTO_VEGETATION_PLACED,
                NTPlacedFeatures.RAINFOREST_GRASS_PLACED,
                NTPlacedFeatures.AEGEAN_SUNFLOWER,
                NTPlacedFeatures.GROTTO_TREE_PLACED);
        BiomeDefaultFeatures.addLushCavesVegetationFeatures(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        spawn(spawns, MobCategory.WATER_AMBIENT, EntityType.TROPICAL_FISH, 30, 4, 8);
        spawn(spawns, MobCategory.CREATURE, EntityType.PARROT, 40, 1, 2);
        spawn(spawns, MobCategory.CREATURE, EntityType.OCELOT, 15, 1, 2);

        return biome(climate(true, 0.95f, 0.9f),
                colors(0x11FFCC, 0x053322, 0xDBF0E5, 0x75CDFF, 0x33CC33, 0x22AA55)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.ASH, 0.005f))
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_JUNGLE)),
                spawns, gen);
    }

    private static Biome createBlueGrove(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION, NTPlacedFeatures.BLUE_GROVE_LARGE_FERN);
        add(gen, Decoration.LAKES, MiscOverworldPlacements.SPRING_WATER);
        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.BLUESPRUCE_TAIGA_PLACED_KEY,
                NTPlacedFeatures.MEGA_BLUE_SPRUCE_PLACED_KEY,
                NTPlacedFeatures.SILVER_ASPEN_PLACED_KEY,
                NTPlacedFeatures.SILVER_FOX_DEN_PLACED,
                NTPlacedFeatures.BLUE_GROVE_GRASS_PLACED,
                NTPlacedFeatures.BLUE_GROVE_FLOWER_PATCH_PLACED,
                NTPlacedFeatures.ASTER_PETALS_PLACED,
                NTPlacedFeatures.OXEYE_DAISY_PLACED);
        add(gen, Decoration.TOP_LAYER_MODIFICATION, NTPlacedFeatures.BLUE_GROVE_SNOW_PLACED_KEY);
        add(gen, Decoration.VEGETAL_DECORATION, NTPlacedFeatures.FALLEN_ASPEN_LOG_PLACED);

        spawn(spawns, MobCategory.MONSTER, EntityType.STRAY, 80, 4, 4);
        spawn(spawns, MobCategory.CREATURE, NTEntities.BROWN_BEAR.get(), 25, 3, 6);
        spawn(spawns, MobCategory.CREATURE, NTEntities.SILVER_FOX.get(), 15, 4, 6);
        spawn(spawns, MobCategory.CREATURE, EntityType.WOLF, 10, 4, 4);

        return biome(climate(true, 0.2f, 0.4f),
                colors(0x34568B, 0x101820, 0xA3C5D9, 0x8AB9D1, 0x718E82, 0x536D6B)
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_GROVE)),
                spawns, gen);
    }

    private static Biome createHeath(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.HEATH_DEAD_BUSH,
                NTPlacedFeatures.HEATH_GRASS_PLAIN,
                NTPlacedFeatures.HEATH_PUMPKIN);
        add(gen, Decoration.LAKES,
                MiscOverworldPlacements.SPRING_WATER,
                NTPlacedFeatures.HEATH_POND_PLACED);
        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.HEATHER_PATCH_PLACED,
                NTPlacedFeatures.HEATHER_PETALS_PLACED,
                NTPlacedFeatures.LAVENDER_PATCH_PLACED,
                NTPlacedFeatures.HEATH_GRASS_PLACED,
                NTPlacedFeatures.ALLIUM_PATCH_PLACED,
                NTPlacedFeatures.HEATH_WATERLILY);

        spawn(spawns, MobCategory.CREATURE, EntityType.HORSE, 100, 4, 6);
        spawn(spawns, MobCategory.CREATURE, EntityType.DONKEY, 25, 1, 2);

        return biome(climate(true, 0.5f, 0.5f),
                colors(0x5E7C8C, 0x232323, 0xC0D8FF, 0x78A7FF, 0x899B5C, 0x778A4A)
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_MEADOW)),
                spawns, gen);
    }

    private static Biome createAegean(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.AEGEAN_SUNFLOWER,
                NTPlacedFeatures.AEGEAN_SUGAR_CANE,
                NTPlacedFeatures.LAVENDER_PATCH_PLACED,
                NTPlacedFeatures.LAVENDER_PETALS_PLACED,
                NTPlacedFeatures.CALCITE_BOULDER_PLACED,
                NTPlacedFeatures.MEDITERRANEAN_GRASS_PLACED,
                VegetationPlacements.FLOWER_MEADOW,
                NTPlacedFeatures.OLIVE_TREE_PLACED_KEY);

        spawn(spawns, MobCategory.WATER_CREATURE, EntityType.DOLPHIN, 12, 1, 3);
        spawn(spawns, MobCategory.CREATURE, EntityType.TURTLE, 18, 2, 4);
        spawn(spawns, MobCategory.WATER_AMBIENT, EntityType.TROPICAL_FISH, 25, 4, 8);
        spawn(spawns, MobCategory.WATER_AMBIENT, EntityType.PUFFERFISH, 10, 1, 3);

        return biome(climate(true, 0.8f, 0.4f),
                colors(0x3196C9, 0x052D3D, 0xC0D8FF, 0x88BBFF, 0x6AB25C, 0x558C50)
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_FLOWER_FOREST)),
                spawns, gen);
    }

    private static Biome createMaquis(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.MAQUIS_SUNFLOWER,
                NTPlacedFeatures.MAQUIS_SUGAR_CANE,
                NTPlacedFeatures.LAVENDER_PATCH_PLACED,
                NTPlacedFeatures.LAVENDER_PETALS_PLACED,
                NTPlacedFeatures.CALCITE_BOULDER_PLACED,
                NTPlacedFeatures.MEDITERRANEAN_GRASS_PLACED,
                NTPlacedFeatures.OLIVE_TREE_PLACED_KEY,
                NTPlacedFeatures.MAQUIS_SHRUB_PLACED_KEY);

        spawn(spawns, MobCategory.CREATURE, EntityType.OCELOT, 15, 2, 4);

        return biome(climate(true, 0.9f, 0.3f),
                colors(0x3196C9, 0x050533, 0xE7D9B1, 0x78A7FF, 0x91BD59, 0x77AB2F)
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_GROVE)),
                spawns, gen);
    }

    private static Biome createAndeanHills(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.SURFACE_STRUCTURES, NTPlacedFeatures.ANDEAN_SPIRE_PLACED_KEY);
        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.ANDEAN_BEAR_CAVE_PLACED,
                NTPlacedFeatures.TUFF_BOULDER_PLACED,
                NTPlacedFeatures.ANDEAN_GRASS_PLACED,
                NTPlacedFeatures.ANDEAN_VEGETATION_PLACED);

        spawn(spawns, MobCategory.CREATURE, NTEntities.SPECTACLED_BEAR.get(), 25, 3, 6);
        spawn(spawns, MobCategory.CREATURE, EntityType.GOAT, 25, 4, 6);
        spawn(spawns, MobCategory.CREATURE, EntityType.LLAMA, 25, 4, 6);
        // alpine climate — cold, misty highlands with precipitation (snow at elevation).

        return biome(climate(true, 0.25f, 0.3f),
                colors(0x3F76E4, 0x050533, 0xFFE4B5, 0x70B8FF, 0xadd57b, 0x6E914A)
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_JAGGED_PEAKS)),
                spawns, gen);
    }

    private static Biome createRainforest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.RAINFOREST_MELON,
                NTPlacedFeatures.RAINFOREST_WATERLILY,
                NTPlacedFeatures.TEAK_PLACED_KEY,
                NTPlacedFeatures.BALSA_PLACED_KEY,
                NTPlacedFeatures.MAHOGANY_SHRUB_PLACED_KEY,
                NTPlacedFeatures.RAINFOREST_GRASS_PLACED,
                VegetationPlacements.FLOWER_MEADOW);

        spawn(spawns, MobCategory.CREATURE, EntityType.PANDA, 5, 1, 2);
        spawn(spawns, MobCategory.CREATURE, EntityType.OCELOT, 25, 3, 6);
        spawn(spawns, MobCategory.CREATURE, EntityType.PARROT, 10, 4, 8);

        return biome(climate(true, 0.95f, 0.9f),
                colors(0x1B9E77, 0x0E4D3B, 0x2E4D2E, 0x77ADFF, 0x288028, 0x1E691E)
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_BAMBOO_JUNGLE)),
                spawns, gen);
    }

    private static Biome createCypressFen(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.CYPRESS_WATERLILY,
                NTPlacedFeatures.CYPRESS_TREE_PLACED,
                NTPlacedFeatures.CYPRESS_LILY_PLACED,
                NTPlacedFeatures.FEN_MUSHROOM_PLACED,
                NTPlacedFeatures.CYPRESS_GRASS_PLACED);

        spawn(spawns, MobCategory.MONSTER, EntityType.BOGGED, 20, 4, 4);
        spawn(spawns, MobCategory.MONSTER, EntityType.SLIME, 15, 1, 1);
        spawn(spawns, MobCategory.CREATURE, EntityType.FROG, 50, 3, 5);

        return biome(climate(true, 0.8f, 0.9f),
                colors(0x614E33, 0x302518, 0xC9B69B, 0xD6B88D, 0x6A7039, 0x565C2E)
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_SWAMP)),
                spawns, gen);
    }

    private static Biome createMirrorMire(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.MIRROR_WATERLILY,
                NTPlacedFeatures.MIRROR_DEAD_BUSH);
        add(gen, Decoration.LAKES,
                MiscOverworldPlacements.SPRING_WATER,
                NTPlacedFeatures.HEATH_POND_PLACED);
        add(gen, Decoration.LOCAL_MODIFICATIONS, NTPlacedFeatures.SUNKEN_LOG_PLACED);
        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.BLUE_GROVE_GRASS_PLACED,
                NTPlacedFeatures.BLUE_GROVE_FLOWER_PATCH_PLACED,
                NTPlacedFeatures.MIRROR_ICE_PLACED,
                NTPlacedFeatures.FROSTED_REEDS_PLACED,
                NTPlacedFeatures.MIRROR_MIRE_TREES);

        spawn(spawns, MobCategory.MONSTER, EntityType.STRAY, 40, 4, 4);
        spawn(spawns, MobCategory.UNDERGROUND_WATER_CREATURE, EntityType.GLOW_SQUID, 10, 4, 6);
        spawn(spawns, MobCategory.CREATURE, EntityType.FROG, 20, 2, 4);
        spawn(spawns, MobCategory.CREATURE, NTEntities.SILVER_FOX.get(), 10, 1, 2);

        return biome(climate(true, -0.05f, 1.0f).temperatureAdjustment(Biome.TemperatureModifier.FROZEN),
                colors(0x3D5E7A, 0x1A2A36, 0xDAE5EB, 0xA4C0D3, 0x94B0AF, 0x829E9D)
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_SWAMP)),
                spawns, gen);
    }

    private static Biome createBluebellWoods(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        BiomeDefaultFeatures.addDefaultSoftDisks(gen);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.LOCAL_MODIFICATIONS, NTPlacedFeatures.BLUEBELL_BOULDER_PLACED);
        add(gen, Decoration.VEGETAL_DECORATION,
                NTPlacedFeatures.GREAT_OAK_RING_PLACED,   // first, so the big oak gets its space
                NTPlacedFeatures.BLUEBELL_TREES_PLACED,
                NTPlacedFeatures.BLUEBELL_SHRUBS_PLACED,
                NTPlacedFeatures.BLUEBELL_FALLEN_LOG_PLACED);
        // After everything else, so the flowers grow around the trees, rocks and logs
        add(gen, Decoration.TOP_LAYER_MODIFICATION, NTPlacedFeatures.BLUEBELL_FLOOR_PLACED);

        spawn(spawns, MobCategory.CREATURE, NTEntities.SONGBIRD.get(), 20, 2, 5);
        spawn(spawns, MobCategory.CREATURE, EntityType.RABBIT, 12, 2, 3);
        spawn(spawns, MobCategory.CREATURE, EntityType.FOX, 6, 1, 2);
        spawn(spawns, MobCategory.CREATURE, NTEntities.FAUN.get(), 3, 1, 2); // a rare, lucky sighting

        return biome(climate(true, 0.6f, 0.6f),
                colors(0x3F8FE4, 0x0A2A4A, 0xC4D4FF, 0x8DB6FF, 0x6FBF4A, 0x8FD058)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.SPORE_BLOSSOM_AIR, 0.002f))
                        .backgroundMusic(calmMusic(SoundEvents.MUSIC_BIOME_FLOWER_FOREST)),
                spawns, gen);
    }

    private static Biome createBlackForest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                VegetationPlacements.DARK_FOREST_VEGETATION,
                VegetationPlacements.PATCH_GRASS_JUNGLE);

        return biome(climate(true, 0.5f, 0.8f),
                colors(0x1e2b21, 0x050505, 0x1a1a1a, 0x2d3436, 0x497949, 0x2b642b)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA)),
                spawns, gen);
    }

    private static Biome createSilverBirchWetland(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                VegetationPlacements.BIRCH_TALL,
                VegetationPlacements.PATCH_WATERLILY);

        return biome(climate(true, 0.6f, 0.9f),
                colors(0x617b64, 0x1d2d30, 0xd9e5d6, 0x9eb3c2, 0x7fb083, 0x699e6d)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP)),
                spawns, gen);
    }

    private static Biome createCoastalChaparral(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();
        BiomeDefaultFeatures.desertSpawns(spawns);

        add(gen, Decoration.VEGETAL_DECORATION,
                VegetationPlacements.TREES_WINDSWEPT_HILLS,
                VegetationPlacements.PATCH_DEAD_BUSH_2);

        return biome(climate(false, 1.2f, 0.0f),
                colors(0x3196c9, 0x052d3d, 0xdbcc9f, 0x88bbff, 0x9d9f5a, 0x8c8f4a)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_BADLANDS)),
                spawns, gen);
    }

    private static Biome createMangroveLagoon(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        spawn(spawns, MobCategory.WATER_AMBIENT, EntityType.TROPICAL_FISH, 25, 8, 8);

        add(gen, Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_MANGROVE);

        return biome(climate(true, 0.9f, 0.9f),
                colors(0x11ffcc, 0x053322, 0xb2f0e0, 0x75cdff, 0x56ee56, 0x3eb13e)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SWAMP)),
                spawns, gen);
    }

    private static Biome createLarchForest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();
        // Use your custom Larch feature if available, else standard Spruce

        add(gen, Decoration.VEGETAL_DECORATION,
                VegetationPlacements.TREES_GROVE,
                VegetationPlacements.PATCH_GRASS_TAIGA);

        return biome(climate(true, 0.1f, 0.4f),
                colors(0x3d567a, 0x101a2b, 0xc9d1d9, 0x9fb6c2, 0x8da173, 0x7a8c63)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_OLD_GROWTH_TAIGA)),
                spawns, gen);
    }

    private static Biome createOzarkForest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.LOCAL_MODIFICATIONS, MiscOverworldPlacements.FOREST_ROCK);
        add(gen, Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_WINDSWEPT_FOREST);

        return biome(climate(true, 0.7f, 0.6f),
                colors(0x4a6a8a, 0x102130, 0xdde6ed, 0x77adff, 0x7fb058, 0x6b9e4a)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FOREST)),
                spawns, gen);
    }

    private static Biome createCloudForest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                VegetationPlacements.TREES_JUNGLE,
                VegetationPlacements.VINES);

        return biome(climate(true, 0.8f, 1.0f),
                colors(0x2f5a5a, 0x0c1a1a, 0x7ea3a3, 0x94b8b8, 0x4d8a5a, 0x3b7546)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.WHITE_ASH, 0.008f))
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_JUNGLE)),
                spawns, gen);
    }

    private static Biome createCedarSlopes(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_WINDSWEPT_HILLS);
        add(gen, Decoration.UNDERGROUND_ORES, OrePlacements.ORE_EMERALD);

        return biome(climate(true, 0.1f, 0.3f),
                colors(0x3e567a, 0x101a2b, 0xc9d1d9, 0x9fb6c2, 0x6e8e7c, 0x5a7a67)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_GROVE)),
                spawns, gen);
    }

    private static Biome createGalleryForest(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        BiomeGenerationSettings.Builder gen = baseGen(features, carvers);
        MobSpawnSettings.Builder spawns = baseSpawns();

        add(gen, Decoration.VEGETAL_DECORATION,
                VegetationPlacements.TREES_JUNGLE,
                VegetationPlacements.PATCH_SUGAR_CANE);

        return biome(climate(true, 0.95f, 0.9f),
                colors(0x1bb33a, 0x052b10, 0xdbf0e5, 0x75cdff, 0x33cc33, 0x22aa55)
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_SPARSE_JUNGLE)),
                spawns, gen);
    }
}
