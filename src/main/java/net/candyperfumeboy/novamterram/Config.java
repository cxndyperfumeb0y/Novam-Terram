package net.candyperfumeboy.novamterram;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

// --- BIOME SETTINGS ---

    // Original & Core Biomes
    public static final ModConfigSpec.BooleanValue ENABLE_BLUE_GROVE = BUILDER
            .comment("Should the Blue Grove biome generate?")
            .define("biomes.enable_blue_grove", true);

    public static final ModConfigSpec.BooleanValue ENABLE_MIRROR_MIRE = BUILDER
            .comment("Should the Mirror Mire generate as a rare heart within Blue Groves?")
            .define("biomes.enable_mirror_mire", true);

    public static final ModConfigSpec.BooleanValue ENABLE_AEGEAN = BUILDER
            .comment("Should the Aegean Coast biome generate?")
            .define("biomes.enable_aegean_coast", true);

    public static final ModConfigSpec.BooleanValue ENABLE_MAQUIS = BUILDER
            .comment("Should the Maquis Shrubland biome generate?")
            .define("biomes.enable_maquis", true);

    public static final ModConfigSpec.BooleanValue ENABLE_HEATH = BUILDER
            .comment("Should the Heath biome generate?")
            .define("biomes.enable_heath", true);

    public static final ModConfigSpec.BooleanValue ENABLE_ANDEAN_HILLS = BUILDER
            .comment("Should the Andean Hills biome generate?")
            .define("biomes.enable_andean_hills", true);

    public static final ModConfigSpec.BooleanValue ENABLE_RAINFOREST = BUILDER
            .comment("Should the Rainforest biome generate?")
            .define("biomes.enable_rainforest", true);

    public static final ModConfigSpec.BooleanValue ENABLE_CYPRESS_FEN = BUILDER
            .comment("Should the Cypress Fen biome generate?")
            .define("biomes.enable_cypress_fen", true);

    // New Highlands & Whimsical Biomes
    public static final ModConfigSpec.BooleanValue ENABLE_AUTUMNAL_TAIGA = BUILDER
            .comment("Should the Autumnal Taiga (Orange/Yellow Pines) generate?")
            .define("biomes.enable_autumnal_taiga", true);



    public static final ModConfigSpec.BooleanValue ENABLE_MYRTLE_FIELDS = BUILDER
            .comment("Should the flowering Myrtle Fields generate?")
            .define("biomes.enable_myrtle_fields", true);

    public static final ModConfigSpec.BooleanValue ENABLE_ROYAL_FOREST = BUILDER
            .comment("Should the rare, flowering Royal Forest (Paulownia trees) generate?")
            .define("biomes.enable_royal_forest", true);

    public static final ModConfigSpec.BooleanValue ENABLE_CALDERA_GROTTO = BUILDER
            .comment("Should the Caldera Grotto generate?")
            .define("biomes.enable_caldera_grotto", true);

    public static final ModConfigSpec.BooleanValue ENABLE_SAFFRON_GROVE = BUILDER
            .comment("Should the humid, orange-hazed Saffron Grove generate?")
            .define("biomes.enable_saffron_grove", true);

    public static final ModConfigSpec.BooleanValue ENABLE_AMBER_SANDS = BUILDER
            .comment("Should the rare, crystalline Amber Sands generate?")
            .define("biomes.enable_amber_sands", true);

    public static final ModConfigSpec.BooleanValue ENABLE_FUNGAL_REACH = BUILDER
            .comment("Should the vertical, misty Fungal Reach generate?")
            .define("biomes.enable_fungal_reach", true);
    // --- REGION SETTINGS ---
    public static final ModConfigSpec.IntValue REGION_WEIGHT = BUILDER
            .comment("The weight of the Novam Terram region. Higher values mean custom biomes appear more often.")
            .defineInRange("region.weight", 13, 1, 100);

    static final ModConfigSpec SPEC = BUILDER.build();
}