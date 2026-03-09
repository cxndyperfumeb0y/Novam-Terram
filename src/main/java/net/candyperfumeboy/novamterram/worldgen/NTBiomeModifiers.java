package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.worldgen.biome.NTBiomes;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

    public class NTBiomeModifiers {
        // --- ORE MODIFIER KEYS ---
    public static final ResourceKey<BiomeModifier> ADD_LIGNITE_ORE = registerKey("add_lignite_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_LIGNITE_ORE = registerKey("add_nether_lignite_ore");
    public static final ResourceKey<BiomeModifier> ADD_END_LIGNITE_ORE = registerKey("add_end_lignite_ore");
    public static final ResourceKey<BiomeModifier> ADD_OPAL_ORE = registerKey("add_opal_ore");

    // --- FLOWER FOREST REVAMP KEYS ---
    public static final ResourceKey<BiomeModifier> ADD_ANCIENT_OAK_FLOWER_FOREST = registerKey("add_ancient_oak_flower_forest");
    public static final ResourceKey<BiomeModifier> ADD_MAGICAL_ROCKS_FLOWER_FOREST = registerKey("add_magical_rocks_flower_forest");
    public static final ResourceKey<BiomeModifier> ADD_FAIRY_RINGS_FLOWER_FOREST = registerKey("add_ancient_floral_features");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        // ========================================================================
        // 1. ORE INJECTION (Global & Specific)
        // ========================================================================

        context.register(ADD_LIGNITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(NTPlacedFeatures.LIGNITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_NETHER_LIGNITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedFeatures.getOrThrow(NTPlacedFeatures.NETHER_LIGNITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_END_LIGNITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_END),
                HolderSet.direct(placedFeatures.getOrThrow(NTPlacedFeatures.END_LIGNITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_OPAL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.SWAMP), biomes.getOrThrow(Biomes.TAIGA), biomes.getOrThrow(Biomes.LUSH_CAVES)),
                HolderSet.direct(placedFeatures.getOrThrow(NTPlacedFeatures.OPAL_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        // ========================================================================
        // 2. FLOWER FOREST REVAMP (Vanilla Injection)
        // ========================================================================

        // ANCIENT OAK: Injected back into Vanilla Flower Forest
        context.register(ADD_ANCIENT_OAK_FLOWER_FOREST, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.FLOWER_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(NTPlacedFeatures.RARE_ANCIENT_OAK_PLACED)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        // FAIRY RINGS: Adding variety to the forest floor
        context.register(ADD_FAIRY_RINGS_FLOWER_FOREST, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.FLOWER_FOREST)),
                HolderSet.direct(
                        placedFeatures.getOrThrow(NTPlacedFeatures.FLORAL_FAIRY_RING_PLACED),
                        placedFeatures.getOrThrow(NTPlacedFeatures.DIORITE_MAGICAL_ROCK_PLACED)
                ),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        // MAGICAL ROCKS: Adding boulders and mossy diorite for terrain texture
        context.register(ADD_MAGICAL_ROCKS_FLOWER_FOREST, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.FLOWER_FOREST)),
                HolderSet.direct(
                        placedFeatures.getOrThrow(NTPlacedFeatures.DIORITE_MAGICAL_ROCK_PLACED),
                        placedFeatures.getOrThrow(NTPlacedFeatures.MOSSY_DIORITE_ROCK_PLACED)
                ),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, name));
    }
}