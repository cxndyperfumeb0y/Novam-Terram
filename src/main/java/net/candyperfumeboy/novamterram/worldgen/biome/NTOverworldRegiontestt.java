package net.candyperfumeboy.novamterram.worldgen.biome;


import com.mojang.datafixers.util.Pair;
import net.candyperfumeboy.novamterram.Config;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class NTOverworldRegiontestt extends Region {

    public NTOverworldRegiontestt(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }
    // TEMPERATURE
    private static final Climate.Parameter T_ICY      = Climate.Parameter.span(-1.0F, -0.45F);
    private static final Climate.Parameter T_COOL     = Climate.Parameter.span(-0.45F, -0.15F);
    private static final Climate.Parameter T_NEUTRAL  = Climate.Parameter.span(-0.15F, 0.2F);
    private static final Climate.Parameter T_WARM     = Climate.Parameter.span(0.2F, 0.55F);
    private static final Climate.Parameter T_HOT      = Climate.Parameter.span(0.55F, 1.0F);

    // HUMIDITY
    private static final Climate.Parameter H_ARID     = Climate.Parameter.span(-1.0F, -0.35F);
    private static final Climate.Parameter H_DRY      = Climate.Parameter.span(-0.35F, -0.1F);
    private static final Climate.Parameter H_NEUTRAL  = Climate.Parameter.span(-0.1F, 0.1F);
    private static final Climate.Parameter H_MOIST    = Climate.Parameter.span(0.1F, 0.35F);
    private static final Climate.Parameter H_WET      = Climate.Parameter.span(0.35F, 1.0F);

    // CONTINENTALNESS (The most important for Coast/Ocean logic)
    private static final Climate.Parameter C_MUSHROOM = Climate.Parameter.span(-1.2F, -1.05F);
    private static final Climate.Parameter C_DEEP_OCEAN = Climate.Parameter.span(-1.05F, -0.455F);
    private static final Climate.Parameter C_OCEAN       = Climate.Parameter.span(-0.455F, -0.17F); // Overlaps Coast
    private static final Climate.Parameter C_COAST       = Climate.Parameter.span(-0.19F, -0.09F);  // Overlaps Near Inland
    private static final Climate.Parameter C_NEAR_INLAND = Climate.Parameter.span(-0.11F, 0.05F);
    private static final Climate.Parameter C_INLAND     = Climate.Parameter.span(0.03F, 0.3F);
    private static final Climate.Parameter C_FAR_INLAND = Climate.Parameter.span(0.3F, 1.0F);

    // EROSION (Height/Flatness)
    private static final Climate.Parameter E_PEAKS    = Climate.Parameter.span(-1.0F, -0.6F);   // MOUNTAINS (Andean/Cyclades)
    private static final Climate.Parameter E_HIGHLAND = Climate.Parameter.span(-0.6F, -0.2F);   // HILLS
    private static final Climate.Parameter E_MID      = Climate.Parameter.span(-0.2F, 0.2F);    // STANDARD
    private static final Climate.Parameter E_FLAT     = Climate.Parameter.span(0.2F, 0.6F);     // FLAT PLAINS (Myrtle)
    private static final Climate.Parameter E_VALLEYS  = Climate.Parameter.span(0.6F, 1.0F);     // LOWLANDS/SWAMPS

    // WEIRDNESS (Variance)
    private static final Climate.Parameter W_MID      = Climate.Parameter.span(-1.0F, 0.9F);
    private static final Climate.Parameter W_RARE     = Climate.Parameter.span(0.9F, 1.0F);


    // ========================================================================================
    // 2. MAIN BIOME GENERATION
    // (Defines the broad areas of the world. No thin Rivers/Beaches here!)
    // ========================================================================================

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {

        // --- 1. THE AEGEAN CLUSTER (Mediterranean) ---
        if (Config.ENABLE_AEGEAN.get()) {
            addBiome(mapper, T_WARM, H_MOIST, C_OCEAN, E_HIGHLAND, W_RARE, NTBiomes.AEGEAN);
            addBiome(mapper, T_WARM, H_DRY,   C_OCEAN, E_HIGHLAND, W_RARE, NTBiomes.AEGEAN);

            // The "Coastal" Aegean (Flatter beaches connecting to the mainland)
            addBiome(mapper, T_WARM, H_MOIST, C_COAST, E_MID, W_RARE, NTBiomes.AEGEAN);
            addBiome(mapper, T_WARM, H_DRY,   C_COAST, E_MID, W_RARE, NTBiomes.AEGEAN);
        }

        if (Config.ENABLE_MAQUIS.get()) {
            // Borders the Aegean coast. Flat near water, normal inland.
            addBiome(mapper, T_WARM, H_DRY, C_NEAR_INLAND, E_FLAT, W_MID, NTBiomes.MAQUIS);
            addBiome(mapper, T_WARM, H_DRY, C_INLAND,      E_MID,  W_RARE, NTBiomes.MAQUIS);
        }

        // --- 2. THE MYSTIC GROVE CLUSTER (Cool) ---
        if (Config.ENABLE_BLUE_GROVE.get()) {
            addBiome(mapper, T_COOL, H_MOIST, C_INLAND,      E_HIGHLAND, W_MID, NTBiomes.BLUE_GROVE);
            addBiome(mapper, T_COOL, H_MOIST, C_FAR_INLAND,      E_HIGHLAND, W_MID, NTBiomes.BLUE_GROVE);

        }

        if (Config.ENABLE_MIRROR_MIRE.get()) {
            // SWAMP LOGIC: Mapped to E_VALLEYS.
            // This fills the lowlands with Mire, but does NOT create the river itself (Overlay does that).
            addBiome(mapper, T_COOL, H_MOIST, C_INLAND, E_VALLEYS, W_RARE, NTBiomes.MIRROR_MIRE);
        }

        if (Config.ENABLE_AUTUMNAL_TAIGA.get()) {
            addBiome(mapper, T_COOL, H_DRY, C_INLAND, E_MID, W_MID, NTBiomes.AUTUMNAL_TAIGA);
            addBiome(mapper, T_COOL, H_DRY, C_FAR_INLAND, E_HIGHLAND, W_RARE, NTBiomes.AUTUMNAL_TAIGA);

        }

        // --- 3. THE HEARTLANDS (Neutral) ---
        if (Config.ENABLE_ROYAL_FOREST.get()) {
            addBiome(mapper, T_NEUTRAL, H_MOIST, C_FAR_INLAND, E_PEAKS, W_RARE, NTBiomes.ROYAL_FOREST);
        }

        if (Config.ENABLE_MYRTLE_FIELDS.get()) {
            // FLAT erosion for fields
            addBiome(mapper, T_NEUTRAL, H_DRY, C_INLAND, E_FLAT, W_RARE, NTBiomes.MYRTLE_FIELDS);
        }

        if (Config.ENABLE_HEATH.get()) {
            addBiome(mapper, T_NEUTRAL, H_ARID, C_INLAND, E_MID, W_MID, NTBiomes.HEATH);
        }

        // --- 4. THE TROPICS (Hot) ---
        if (Config.ENABLE_RAINFOREST.get()) {
            addBiome(mapper, T_HOT, H_WET, C_INLAND, E_MID,      W_MID, NTBiomes.RAINFOREST);
            addBiome(mapper, T_HOT, H_WET, C_INLAND, E_HIGHLAND, W_RARE, NTBiomes.RAINFOREST);
        }

        if (Config.ENABLE_CYPRESS_FEN.get()) {
            // SWAMP LOGIC: Mapped to E_VALLEYS.
            // Creates a wide wetland basin. The overlay will cut a river through it later.
            addBiome(mapper, T_HOT, H_WET, C_INLAND, E_VALLEYS, W_MID, NTBiomes.CYPRESS_FEN);
        }




        if (Config.ENABLE_ANDEAN_HILLS.get()) {
            // Standard High Altitude
            addBiome(mapper, T_WARM, H_DRY, C_FAR_INLAND, E_PEAKS, W_MID, NTBiomes.ANDEAN_HILLS);
            // Extreme Altitude (Rare Weirdness) - Forces it to compete with Stony Peaks
            addBiome(mapper, T_WARM, H_DRY, C_FAR_INLAND, E_PEAKS, W_RARE, NTBiomes.ANDEAN_HILLS);
        }

        // --- 4. THE TROPICS (Hot) --- (Added Saffron and Amber)
        if (Config.ENABLE_SAFFRON_GROVE.get()) {
            // Hot/Moist: Sits between the Mediterranean and the deep Rainforest
            addBiome(mapper, T_HOT, H_MOIST, C_INLAND, E_MID, W_MID, NTBiomes.SAFFRON_GROVE);
            addBiome(mapper, T_HOT, H_MOIST, C_FAR_INLAND, E_MID, W_RARE, NTBiomes.SAFFRON_GROVE);
        }

        if (Config.ENABLE_AMBER_SANDS.get()) {
            // Hot/Arid: The "Deep" Desert. Far inland and rare.
            addBiome(mapper, T_HOT, H_ARID, C_FAR_INLAND, E_VALLEYS, W_RARE, NTBiomes.AMBER_SANDS);        }

        // --- 2. THE MYSTIC GROVE CLUSTER (Cool) --- (Added Fungal Reach)
        if (Config.ENABLE_FUNGAL_REACH.get()) {
            // Cool/Wet: Using E_VALLEYS to get that "Sunken" feel under Tectonic cliffs.
            addBiome(mapper, T_HOT, H_WET, C_INLAND, E_VALLEYS, W_RARE, NTBiomes.FUNGAL_REACH);

            // We can also add a 'Near Inland' version to allow it to seep toward the coast as a low-lying swamp
            addBiome(mapper, T_HOT, H_WET, C_NEAR_INLAND, E_VALLEYS, W_RARE, NTBiomes.FUNGAL_REACH);
        }

        // ========================================================================================
        // 5. THE "MORTAR" (Vanilla Buffers to prevent jarring transitions)
        // ========================================================================================

        // OCEANS: Ensures the water matches the temperature of the land next to it.
        addOceanBiome(mapper, T_ICY,     Climate.Parameter.span(-1.0f, 1.0f), C_DEEP_OCEAN, E_MID, W_MID, Biomes.DEEP_FROZEN_OCEAN);
        addOceanBiome(mapper, T_COOL,    Climate.Parameter.span(-1.0f, 1.0f), C_DEEP_OCEAN, E_MID, W_MID, Biomes.DEEP_COLD_OCEAN);
        addOceanBiome(mapper, T_NEUTRAL, Climate.Parameter.span(-1.0f, 1.0f), C_DEEP_OCEAN, E_MID, W_MID, Biomes.DEEP_OCEAN);
        addOceanBiome(mapper, T_WARM,    Climate.Parameter.span(-1.0f, 1.0f), C_DEEP_OCEAN, E_MID, W_MID, Biomes.LUKEWARM_OCEAN);
        addOceanBiome(mapper, T_HOT,     Climate.Parameter.span(-1.0f, 1.0f), C_DEEP_OCEAN, E_MID, W_MID, Biomes.WARM_OCEAN);

        // COASTS: Prevents sudden drops into the ocean from mainland.
        addBiome(mapper, T_ICY,     Climate.Parameter.span(-1.0f, 1.0f), C_COAST, E_MID, W_MID, Biomes.SNOWY_BEACH);
        addBiome(mapper, T_COOL,    Climate.Parameter.span(-1.0f, 1.0f), C_COAST, E_MID, W_MID, Biomes.STONY_SHORE);
        addBiome(mapper, T_NEUTRAL, Climate.Parameter.span(-1.0f, 1.0f), C_COAST, E_MID, W_MID, Biomes.BEACH);

        // TRANSITION LANDS: Plugs the gaps so Hot biomes don't touch Cool biomes.
        // Hot -> Warm -> Neutral -> Cool
        addBiome(mapper, T_WARM, H_WET, C_INLAND, E_MID, W_MID, Biomes.SPARSE_JUNGLE); // Buffer below Rainforest
        addBiome(mapper, T_NEUTRAL, H_WET, C_INLAND, E_MID, W_MID, Biomes.DARK_FOREST); // Buffer above Mirror Mire
        addBiome(mapper, T_NEUTRAL, H_DRY, C_INLAND, E_MID, W_MID, Biomes.PLAINS);      // Buffer between deserts and taigas
        addBiome(mapper, T_NEUTRAL, H_DRY, C_INLAND, E_HIGHLAND, W_MID, Biomes.MEADOW); // Elevated buffer

        // Additional Vanilla Fillers to prevent generation gaps
        addBiome(mapper, T_COOL, H_ARID, C_INLAND, E_MID, W_MID, Biomes.TAIGA);
        addBiome(mapper, T_NEUTRAL, H_MOIST, C_INLAND, E_MID, W_MID, Biomes.BIRCH_FOREST);
        addBiome(mapper, T_HOT, H_DRY, C_INLAND, E_MID, W_MID, Biomes.SAVANNA);
        addBiome(mapper, T_HOT, H_ARID, C_INLAND, E_MID, W_MID, Biomes.DESERT);
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