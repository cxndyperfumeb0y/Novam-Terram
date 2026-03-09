package net.candyperfumeboy.novamterram.worldgen.biome;


import com.mojang.datafixers.util.Pair;

import net.candyperfumeboy.novamterram.Config;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

public class NTOverworldRegion extends Region {

    public NTOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }
    // TEMPERATURE
    private static final Climate.Parameter T_FROZEN      = Climate.Parameter.span(-1.0F, -0.45F);
    private static final Climate.Parameter T_COLD        = Climate.Parameter.span(-0.48F, -0.15F); // Overlap
    private static final Climate.Parameter T_TEMPERATE   = Climate.Parameter.span(-0.18F, 0.2F);  // Overlap
    private static final Climate.Parameter T_WARM        = Climate.Parameter.span(0.17F, 0.55F);  // Overlap
    private static final Climate.Parameter T_SCORCHING   = Climate.Parameter.span(0.52F, 1.0F);   // Overlap

    // HUMIDITY
    private static final Climate.Parameter H_WASTELAND   = Climate.Parameter.span(-1.0F, -0.35F);
    private static final Climate.Parameter H_ARID        = Climate.Parameter.span(-0.38F, -0.12F); // Adjusted
    private static final Climate.Parameter H_AVERAGE     = Climate.Parameter.span(-0.13F, 0.1F);  // Overlaps slightly now
    private static final Climate.Parameter H_FERTILE     = Climate.Parameter.span(0.07F, 0.35F);
    private static final Climate.Parameter H_RAINY       = Climate.Parameter.span(0.32F, 1.0F);

    // CONTINENTALNESS (Added more "Coast" buffer)
    private static final Climate.Parameter C_MUSHROOM    = Climate.Parameter.span(-1.2F, -1.05F);
    private static final Climate.Parameter C_DEEP_OCEAN  = Climate.Parameter.span(-1.05F, -0.455F);
    private static final Climate.Parameter C_OCEAN       = Climate.Parameter.span(-0.455F, -0.15F);
    private static final Climate.Parameter C_COASTLINE   = Climate.Parameter.span(-0.19F, -0.07F);
    private static final Climate.Parameter C_NEAR_INLAND = Climate.Parameter.span(-0.11F, 0.07F);
    private static final Climate.Parameter C_INLAND      = Climate.Parameter.span(0.03F, 0.35F);
    private static final Climate.Parameter C_DEEP_INLAND = Climate.Parameter.span(0.32F, 1.0F);

    // EROSION
    private static final Climate.Parameter E_JAGGED      = Climate.Parameter.span(-1.0F, -0.55F);
    private static final Climate.Parameter E_HIGHLANDS   = Climate.Parameter.span(-0.6F, -0.15F);
    private static final Climate.Parameter E_STANDARD    = Climate.Parameter.span(-0.2F, 0.15F);
    private static final Climate.Parameter E_FLATS       = Climate.Parameter.span(0.1F, 0.6F);
    private static final Climate.Parameter E_CANYONS     = Climate.Parameter.span(0.52F, 0.87F);
    private static final Climate.Parameter E_BASINS      = Climate.Parameter.span(0.82F, 1.0F);

    // WEIRDNESS
    private static final Climate.Parameter W_EXPANSIVE   = Climate.Parameter.span(-1.0F, -0.2F);
    private static final Climate.Parameter W_NORMAL      = Climate.Parameter.span(-0.25F, 0.35F);
    private static final Climate.Parameter W_VARIED      = Climate.Parameter.span(0.3F, 0.8F);
    private static final Climate.Parameter W_SPIRES      = Climate.Parameter.span(0.72F, 1.0F);

    // ========================================================================================
    // 2. MAIN BIOME GENERATION
    // (Defines the broad areas of the world. No thin Rivers/Beaches here!)
    // ========================================================================================

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {

        // --- 1. THE AEGEAN CLUSTER (Mediterranean / Highlands-inspired) ---
        if (Config.ENABLE_AEGEAN.get()) {
            // Rugged Mountainous Islands
            addBiome(mapper, T_WARM, H_FERTILE, C_MUSHROOM, E_HIGHLANDS, W_VARIED, NTBiomes.AEGEAN);
            addBiome(mapper, T_WARM, H_FERTILE, C_OCEAN, E_HIGHLANDS, W_VARIED, NTBiomes.AEGEAN);
            addBiome(mapper, T_WARM, H_ARID,    C_OCEAN, E_HIGHLANDS, W_VARIED, NTBiomes.AEGEAN);

            // Coastal Aegean (Transitions between water and land)
            addBiome(mapper, T_WARM, H_FERTILE, C_COASTLINE, E_STANDARD, W_VARIED, NTBiomes.AEGEAN);
            addBiome(mapper, T_WARM, H_ARID,    C_COASTLINE, E_STANDARD, W_VARIED, NTBiomes.AEGEAN);
        }

        if (Config.ENABLE_MAQUIS.get()) {
            // Dry Mediterranean Shrubland
            addBiome(mapper, T_WARM, H_ARID, C_NEAR_INLAND, E_FLATS,    W_NORMAL, NTBiomes.MAQUIS);
            addBiome(mapper, T_WARM, H_ARID, C_INLAND,      E_STANDARD, W_VARIED, NTBiomes.MAQUIS);
        }

        // --- 2. THE MYSTIC GROVE CLUSTER (Cold / Damp / Magical) ---
        if (Config.ENABLE_BLUE_GROVE.get()) {
            addBiome(mapper, T_COLD, H_FERTILE, C_INLAND,      E_HIGHLANDS, W_NORMAL, NTBiomes.BLUE_GROVE);
            addBiome(mapper, T_COLD, H_FERTILE, C_DEEP_INLAND, E_HIGHLANDS, W_NORMAL, NTBiomes.BLUE_GROVE);
        }

        if (Config.ENABLE_MIRROR_MIRE.get()) {
            // Cold Sunken Basin (Misty / Frozen Swamp)
            addBiome(mapper, T_FROZEN, H_WASTELAND, C_INLAND, E_BASINS, W_VARIED, NTBiomes.MIRROR_MIRE);
            addBiome(mapper, T_FROZEN, H_RAINY, C_INLAND, E_BASINS, W_VARIED, NTBiomes.MIRROR_MIRE);
        }

        if (Config.ENABLE_AUTUMNAL_TAIGA.get()) {
            addBiome(mapper, T_COLD, H_ARID, C_INLAND,      E_STANDARD,  W_NORMAL, NTBiomes.AUTUMNAL_TAIGA);
            addBiome(mapper, T_COLD, H_ARID, C_DEEP_INLAND, E_HIGHLANDS, W_VARIED, NTBiomes.AUTUMNAL_TAIGA);
        }

        // --- 3. THE HEARTLANDS (Temperate / Neutral) ---
        if (Config.ENABLE_ROYAL_FOREST.get()) {
            // High altitude peaks
            addBiome(mapper, T_TEMPERATE, H_FERTILE, C_DEEP_INLAND, E_JAGGED, W_VARIED, NTBiomes.ROYAL_FOREST);
        }

        if (Config.ENABLE_MYRTLE_FIELDS.get()) {
            // Massive flat expanses
            addBiome(mapper, T_TEMPERATE, H_ARID, C_INLAND, E_FLATS, W_EXPANSIVE, NTBiomes.MYRTLE_FIELDS);
        }

        if (Config.ENABLE_HEATH.get()) {
            addBiome(mapper, T_TEMPERATE, H_WASTELAND, C_INLAND, E_STANDARD, W_NORMAL, NTBiomes.HEATH);
        }

        // --- 4. THE TROPICS (Hot / Scorching) ---
        if (Config.ENABLE_RAINFOREST.get()) {
            addBiome(mapper, T_SCORCHING, H_RAINY, C_INLAND, E_STANDARD,  W_NORMAL, NTBiomes.RAINFOREST);
            addBiome(mapper, T_SCORCHING, H_RAINY, C_INLAND, E_HIGHLANDS, W_VARIED, NTBiomes.RAINFOREST);
        }

        if (Config.ENABLE_SAFFRON_GROVE.get()) {
            // Tropical Orchards / Transition biome
            addBiome(mapper, T_SCORCHING, H_FERTILE, C_INLAND,      E_STANDARD, W_NORMAL, NTBiomes.SAFFRON_GROVE);
            addBiome(mapper, T_SCORCHING, H_FERTILE, C_DEEP_INLAND, E_STANDARD, W_VARIED, NTBiomes.SAFFRON_GROVE);
            addBiome(mapper, T_SCORCHING, H_FERTILE, C_INLAND, E_HIGHLANDS, W_VARIED, NTBiomes.SAFFRON_GROVE);
        }

        if (Config.ENABLE_CYPRESS_FEN.get()) {
            // Tropical Sunken Basin (Standard Swamp Shapes)
            addBiome(mapper, T_SCORCHING, H_RAINY, C_INLAND, E_BASINS, W_NORMAL, NTBiomes.CYPRESS_FEN);
        }

        if (Config.ENABLE_FUNGAL_REACH.get()) {
            // Tropical Sunken Basin (Unique Weirdness to separate from Cypress Fen)
            addBiome(mapper, T_SCORCHING, H_RAINY, C_INLAND,      E_BASINS, W_VARIED, NTBiomes.FUNGAL_REACH);
            addBiome(mapper, T_SCORCHING, H_RAINY, C_DEEP_INLAND, E_BASINS, W_VARIED, NTBiomes.FUNGAL_REACH);
        }

        if (Config.ENABLE_AMBER_SANDS.get()) {
            // ROLLING DUNES: High altitude but smooth erosion
            addBiome(mapper, T_SCORCHING, H_WASTELAND, C_INLAND, E_HIGHLANDS, W_NORMAL, NTBiomes.AMBER_SANDS);

            // CARVING CANYONS: The "Canyon" erosion value forces the deep grooves
            addBiome(mapper, T_SCORCHING, H_WASTELAND, C_DEEP_INLAND, E_CANYONS, W_NORMAL, NTBiomes.AMBER_SANDS);

        }

        if (Config.ENABLE_CALDERA_GROTTO.get()) {
            // THE RIM & PILLARS: High peaks and basalt-style pillars shooting up
            addBiome(mapper, T_SCORCHING, H_RAINY, C_INLAND, E_JAGGED, W_SPIRES, NTBiomes.CALDERA_GROTTO);
            addBiome(mapper, T_SCORCHING, H_RAINY, C_DEEP_OCEAN, E_BASINS, W_SPIRES, NTBiomes.CALDERA_GROTTO);


            addBiome(mapper, T_SCORCHING, H_RAINY, C_INLAND, E_BASINS, W_VARIED, NTBiomes.CALDERA_GROTTO);

            // THE SUNKEN BASIN: The tropical, lush floor of the volcano
            addBiome(mapper, T_SCORCHING, H_RAINY, C_INLAND, E_BASINS, W_SPIRES, NTBiomes.CALDERA_GROTTO);
        }

        if (Config.ENABLE_ANDEAN_HILLS.get()) {
            // Highlands-style high altitude plains
            addBiome(mapper, T_WARM, H_ARID, C_DEEP_INLAND, E_JAGGED, W_NORMAL, NTBiomes.ANDEAN_HILLS);
            addBiome(mapper, T_WARM, H_ARID, C_DEEP_INLAND, E_JAGGED, W_SPIRES, NTBiomes.ANDEAN_HILLS);
            addBiome(mapper, T_WARM, H_ARID, C_DEEP_INLAND, E_JAGGED, W_VARIED, NTBiomes.ANDEAN_HILLS);
        }

        // ========================================================================================
        // 5. THE "MORTAR" (Vanilla Buffers & Global Fillers)
        // ========================================================================================

        // OCEANS: Matching land temperatures
        addOceanBiome(mapper, T_FROZEN,    Climate.Parameter.span(-1.0f, 1.0f), C_DEEP_OCEAN, E_STANDARD, W_NORMAL, Biomes.DEEP_FROZEN_OCEAN);
        addOceanBiome(mapper, T_COLD,      Climate.Parameter.span(-1.0f, 1.0f), C_DEEP_OCEAN, E_STANDARD, W_NORMAL, Biomes.DEEP_COLD_OCEAN);
        addOceanBiome(mapper, T_TEMPERATE, Climate.Parameter.span(-1.0f, 1.0f), C_DEEP_OCEAN, E_STANDARD, W_NORMAL, Biomes.DEEP_OCEAN);
        addOceanBiome(mapper, T_WARM,      Climate.Parameter.span(-1.0f, 1.0f), C_DEEP_OCEAN, E_STANDARD, W_NORMAL, Biomes.LUKEWARM_OCEAN);
        addOceanBiome(mapper, T_SCORCHING, Climate.Parameter.span(-1.0f, 1.0f), C_DEEP_OCEAN, E_STANDARD, W_NORMAL, Biomes.WARM_OCEAN);

        // COASTS: Shorelines
        addBiome(mapper, T_FROZEN,    Climate.Parameter.span(-1.0f, 1.0f), C_COASTLINE, E_STANDARD, W_NORMAL, Biomes.SNOWY_BEACH);
        addBiome(mapper, T_COLD,      Climate.Parameter.span(-1.0f, 1.0f), C_COASTLINE, E_STANDARD, W_NORMAL, Biomes.STONY_SHORE);
        addBiome(mapper, T_TEMPERATE, Climate.Parameter.span(-1.0f, 1.0f), C_COASTLINE, E_STANDARD, W_NORMAL, Biomes.BEACH);

        // VANILLA TRANSITIONS (Ensuring no jarring logic jumps)
        addBiome(mapper, T_WARM,      H_RAINY,     C_INLAND, E_STANDARD, W_NORMAL, Biomes.SPARSE_JUNGLE);
        addBiome(mapper, T_TEMPERATE, H_RAINY,     C_INLAND, E_STANDARD, W_NORMAL, Biomes.DARK_FOREST);
        addBiome(mapper, T_TEMPERATE, H_ARID,      C_INLAND, E_STANDARD, W_NORMAL, Biomes.PLAINS);
        addBiome(mapper, T_TEMPERATE, H_ARID,      C_INLAND, E_HIGHLANDS,W_NORMAL, Biomes.MEADOW);

        // FILLER: Plugs holes where mod biomes aren't specified
        addBiome(mapper, T_COLD,      H_WASTELAND, C_INLAND, E_STANDARD, W_NORMAL, Biomes.TAIGA);
        addBiome(mapper, T_TEMPERATE, H_FERTILE,   C_INLAND, E_STANDARD, W_NORMAL, Biomes.BIRCH_FOREST);
        addBiome(mapper, T_SCORCHING, H_ARID,      C_INLAND, E_STANDARD, W_NORMAL, Biomes.SAVANNA);
        addBiome(mapper, T_SCORCHING, H_WASTELAND, C_INLAND, E_STANDARD, W_NORMAL, Biomes.DESERT);
        addBiome(mapper, T_COLD, H_AVERAGE, C_INLAND, E_STANDARD, W_NORMAL, Biomes.OLD_GROWTH_PINE_TAIGA);
// Adds a buffer for the Heath/Myrtle Fields
        addBiome(mapper, T_TEMPERATE, H_ARID, C_NEAR_INLAND, E_FLATS, W_NORMAL, Biomes.SUNFLOWER_PLAINS);
        addBiome(mapper, T_TEMPERATE, H_RAINY, C_MUSHROOM, E_STANDARD, W_NORMAL, Biomes.MUSHROOM_FIELDS);
    }

    private void addBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper,
                          Climate.Parameter t, Climate.Parameter h, Climate.Parameter c,
                          Climate.Parameter e, Climate.Parameter w, ResourceKey<Biome> b) {
        Climate.ParameterPoint point = Climate.parameters(t, h, c, e, Climate.Parameter.point(0.0F), w, 0.0F);
        mapper.accept(Pair.of(point, b));
    }

    private void addOceanBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper,
                               Climate.Parameter t, Climate.Parameter h, Climate.Parameter c,
                               Climate.Parameter e, Climate.Parameter w, ResourceKey<Biome> b) {
        Climate.ParameterPoint point = Climate.parameters(t, h, c, e, Climate.Parameter.span(0.1F, 1.0F), w, 0.0F);
        mapper.accept(Pair.of(point, b));
    }
}