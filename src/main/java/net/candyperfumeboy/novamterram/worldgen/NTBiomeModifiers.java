package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;

/**
 * NTBiomeModifiers
 * * Handles the injection of custom features into both vanilla and modded biomes.
 * This class is the "Glue" that connects your PlacedFeatures to the world.
 */
public class NTBiomeModifiers {

    // --- ORE MODIFIERS ---
    public static final ResourceKey<BiomeModifier> ADD_LIGNITE_ORE = registerKey("add_lignite_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_LIGNITE_ORE = registerKey("add_nether_lignite_ore");
    public static final ResourceKey<BiomeModifier> ADD_END_LIGNITE_ORE = registerKey("add_end_lignite_ore");
    public static final ResourceKey<BiomeModifier> ADD_OPAL_ORE = registerKey("add_opal_ore");

    // --- FOREST & FLOWER FOREST REVAMP ---
    public static final ResourceKey<BiomeModifier> FOREST_VISION_CLEANUP = registerKey("00_forest_vision_cleanup");
    public static final ResourceKey<BiomeModifier> APPLY_FOREST_VISION = registerKey("01_apply_forest_vision");
    public static final ResourceKey<BiomeModifier> ADD_FOREST_DIRT_PATCHES = registerKey("add_forest_dirt_patches");

    //BIRCH FOREST
    public static final ResourceKey<BiomeModifier> BIRCH_VISION_CLEANUP = registerKey("birch_forest_cleanup");
    public static final ResourceKey<BiomeModifier> APPLY_BIRCH_VISION = registerKey("apply_birch_vision");
    public static final ResourceKey<BiomeModifier> ADD_BIRCH_SHRUBS = registerKey("add_birch_shrubs");
    public static final ResourceKey<BiomeModifier> BIRCH_BAMBOO_GROVES = registerKey("add_birch_bamboo");
    public static final ResourceKey<BiomeModifier> ADD_BIRCH_GEOLOGY = registerKey("add_birch_geology");
    public static final ResourceKey<BiomeModifier> ADD_SILVER_UNDERGROWTH = registerKey("add_silver_undergrowth");

    // --- FLOWER FOREST EXCLUSIVES ---
    public static final ResourceKey<BiomeModifier> ADD_ANCIENT_OAK_FLOWER_FOREST = registerKey("add_ancient_oak_flower_forest");
    public static final ResourceKey<BiomeModifier> ADD_ENCHANTED_FLORA_FEATURES = registerKey("add_enchanted_flora_features");
    public static final ResourceKey<BiomeModifier> ADD_MAGICAL_GEOLOGY = registerKey("add_magical_geology");
    public static final ResourceKey<BiomeModifier> ADD_FOREST_SHRUBS = registerKey("add_forest_shrubs");
    public static final ResourceKey<BiomeModifier> ADD_FOREST_GRASS = registerKey("add_forest_grass");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        registerOreModifiers(context, biomes, placedFeatures);
        registerForestRevampModifiers(context, biomes, placedFeatures);
        registerFlowerForestExclusives(context, biomes, placedFeatures);
        registerBirchRevampModifiers(context, biomes, placedFeatures);

    }

    private static void registerOreModifiers(BootstrapContext<BiomeModifier> context, HolderGetter<Biome> biomes, HolderGetter<PlacedFeature> features) {
        context.register(ADD_LIGNITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(features.getOrThrow(NTPlacedFeatures.LIGNITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_NETHER_LIGNITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(features.getOrThrow(NTPlacedFeatures.NETHER_LIGNITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_END_LIGNITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_END),
                HolderSet.direct(features.getOrThrow(NTPlacedFeatures.END_LIGNITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_OPAL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.SWAMP), biomes.getOrThrow(Biomes.TAIGA), biomes.getOrThrow(Biomes.LUSH_CAVES)),
                HolderSet.direct(features.getOrThrow(NTPlacedFeatures.OPAL_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
    }

    private static void registerForestRevampModifiers(BootstrapContext<BiomeModifier> context, HolderGetter<Biome> biomes, HolderGetter<PlacedFeature> features) {
        // 1. Remove Vanilla Trees (Cleanup)
        context.register(FOREST_VISION_CLEANUP, new BiomeModifiers.RemoveFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.FOREST), biomes.getOrThrow(Biomes.FLOWER_FOREST)),
                HolderSet.direct(
                        features.getOrThrow(VegetationPlacements.TREES_BIRCH_AND_OAK),
                        features.getOrThrow(VegetationPlacements.TREES_FLOWER_FOREST)
                ),
                Set.of(GenerationStep.Decoration.VEGETAL_DECORATION)
        ));

        // 2. Inject New Tall Oak Vision
        context.register(APPLY_FOREST_VISION, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.FOREST), biomes.getOrThrow(Biomes.FLOWER_FOREST)),
                HolderSet.direct(features.getOrThrow(NTPlacedFeatures.REVAMPED_FOREST_TREES_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));


        context.register(ADD_FOREST_SHRUBS, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.FOREST),
                        biomes.getOrThrow(Biomes.FLOWER_FOREST)
                ),
                // This points to the PlacedFeature we just created
                HolderSet.direct(features.getOrThrow(NTPlacedFeatures.FOREST_SHRUBS_PLACED)),

                // Decoration step for small ground plants
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_FOREST_GRASS, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.FOREST),
                        biomes.getOrThrow(Biomes.FLOWER_FOREST)
                ),
                // This points to the PlacedFeature we just created
                HolderSet.direct(features.getOrThrow(NTPlacedFeatures.BLUE_GROVE_GRASS_PLACED)),

                // Decoration step for small ground plants
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));
        // 3. Add Noisy Rooted Dirt Patches
        context.register(ADD_FOREST_DIRT_PATCHES, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.FOREST), biomes.getOrThrow(Biomes.FLOWER_FOREST)),
                HolderSet.direct(features.getOrThrow(NTPlacedFeatures.FOREST_ROOTED_DIRT_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));
    }
    private static void registerBirchRevampModifiers(BootstrapContext<BiomeModifier> context, HolderGetter<Biome> biomes, HolderGetter<PlacedFeature> features) {
        // 1. Remove Vanilla Birch Trees
        context.register(BIRCH_VISION_CLEANUP, new BiomeModifiers.RemoveFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.BIRCH_FOREST),
                        biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST)
                ),
                HolderSet.direct(
                        features.getOrThrow(VegetationPlacements.TREES_BIRCH),
                        // This targets the taller "Old Growth" variants specifically
                        features.getOrThrow(VegetationPlacements.BIRCH_TALL)

                ),
                Set.of(GenerationStep.Decoration.VEGETAL_DECORATION)
        ));

        // 2. Apply Custom Tall Birches
        context.register(APPLY_BIRCH_VISION, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.BIRCH_FOREST), biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST)),
                HolderSet.direct(
                        features.getOrThrow(NTPlacedFeatures.TALL_BIRCH_TREES_PLACED),
                      features.getOrThrow(NTPlacedFeatures.HIGHLAND_BIRCH_PLACED)
                ),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));
        // 3. Add Tiny Birch Shrubs
        context.register(ADD_BIRCH_SHRUBS, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.BIRCH_FOREST), biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST)),
                HolderSet.direct(features.getOrThrow(NTPlacedFeatures.BIRCH_SHRUBS_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(BIRCH_BAMBOO_GROVES, new BiomeModifiers.AddFeaturesBiomeModifier(
                // Targeting the Old Growth variant specifically for that "Ancient" feel
                HolderSet.direct(biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST)),
                HolderSet.direct(
                        // Using the vanilla Bamboo placement but wrapped in our rarity
                        features.getOrThrow(NTPlacedFeatures.TALL_HIGHLAND_BAMBOO_PLACED)
                ),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));
        // 4. Silver Undergrowth (Ferns, Flowers, and Dirt Patches)
        context.register(ADD_SILVER_UNDERGROWTH, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.BIRCH_FOREST), biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST)),
                HolderSet.direct(
                        features.getOrThrow(NTPlacedFeatures.SILVER_FOREST_FLOWERS_PLACED),
                        features.getOrThrow(NTPlacedFeatures.BLUE_GROVE_GRASS_PLACED),

                        features.getOrThrow(NTPlacedFeatures.SILVER_SAGE_PLACED)
                ),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));
        // Magical Geology (Boulders & Mossy Diorite)
        context.register(ADD_BIRCH_GEOLOGY, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST)),
                HolderSet.direct(
                        features.getOrThrow(NTPlacedFeatures.MOSSY_BOULDER_PLACED)
                ),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS));
    }
    private static void registerFlowerForestExclusives(BootstrapContext<BiomeModifier> context, HolderGetter<Biome> biomes, HolderGetter<PlacedFeature> features) {
        // Double-Density Ancient Oaks for Flower Forests
        context.register(ADD_ANCIENT_OAK_FLOWER_FOREST, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.FLOWER_FOREST)),
                HolderSet.direct(features.getOrThrow(NTPlacedFeatures.RARE_ANCIENT_OAK_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        // Enchanted Flora (Fairy Rings)
        context.register(ADD_ENCHANTED_FLORA_FEATURES, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.FLOWER_FOREST)),
                HolderSet.direct(features.getOrThrow(NTPlacedFeatures.FLORAL_FAIRY_RING_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION));


        // Magical Geology (Boulders & Mossy Diorite)
        context.register(ADD_MAGICAL_GEOLOGY, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.FLOWER_FOREST)),
                HolderSet.direct(
                        features.getOrThrow(NTPlacedFeatures.DIORITE_MAGICAL_ROCK_PLACED),
                        features.getOrThrow(NTPlacedFeatures.MOSSY_BOULDER_PLACED)
                ),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, name));
    }
}