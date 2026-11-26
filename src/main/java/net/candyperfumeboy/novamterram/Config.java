package net.candyperfumeboy.novamterram;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // --- BIOME SETTINGS ---

    // Mediterranean & Coastal
    public static final ModConfigSpec.BooleanValue ENABLE_AEGEAN = BUILDER
            .comment("Should the Aegean Coast biome generate?")
            .define("biomes.enable_aegean_coast", true);

    public static final ModConfigSpec.BooleanValue ENABLE_MAQUIS = BUILDER
            .comment("Should the Maquis Shrubland biome generate?")
            .define("biomes.enable_maquis", true);

    public static final ModConfigSpec.BooleanValue ENABLE_MANGROVE_LAGOON = BUILDER
            .comment("Should the tropical Mangrove Lagoon generate?")
            .define("biomes.enable_mangrove_lagoon", false);

    public static final ModConfigSpec.BooleanValue ENABLE_COASTAL_CHAPARRAL = BUILDER
            .comment("Should the arid Coastal Chaparral generate?")
            .define("biomes.enable_coastal_chaparral", true);

    // Forests & Woods
    public static final ModConfigSpec.BooleanValue ENABLE_BLUE_GROVE = BUILDER
            .comment("Should the Blue Grove biome generate?")
            .define("biomes.enable_blue_grove", true);

    public static final ModConfigSpec.BooleanValue ENABLE_ROYAL_FOREST = BUILDER
            .comment("Should the rare, flowering Royal Forest (Paulownia trees) generate?")
            .define("biomes.enable_royal_forest", true);

    public static final ModConfigSpec.BooleanValue ENABLE_BLUEBELL_WOODS = BUILDER
            .comment("Should the flower-carpeted Bluebell Woods generate?")
            .define("biomes.enable_bluebell_woods", false);

    public static final ModConfigSpec.BooleanValue ENABLE_BLACK_FOREST = BUILDER
            .comment("Should the dense, dark Black Forest generate?")
            .define("biomes.enable_black_forest", false);

    public static final ModConfigSpec.BooleanValue ENABLE_OZARK_FOREST = BUILDER
            .comment("Should the rocky Ozark Hardwood Forest generate?")
            .define("biomes.enable_ozark_forest", true);

    // Highlands & Mountains
    public static final ModConfigSpec.BooleanValue ENABLE_ANDEAN_HILLS = BUILDER
            .comment("Should the Andean Hills biome generate?")
            .define("biomes.enable_andean_hills", true);

    public static final ModConfigSpec.BooleanValue ENABLE_CLOUD_FOREST = BUILDER
            .comment("Should the misty Cloud Forest (Andean variant) generate?")
            .define("biomes.enable_cloud_forest", false);

    public static final ModConfigSpec.BooleanValue ENABLE_CEDAR_SLOPES = BUILDER
            .comment("Should the steep Cedar Slopes generate?")
            .define("biomes.enable_cedar_slopes", false);

    public static final ModConfigSpec.BooleanValue ENABLE_HEATH = BUILDER
            .comment("Should the Heath biome generate?")
            .define("biomes.enable_heath", true);

    // Taigas & Cold Biomes
    public static final ModConfigSpec.BooleanValue ENABLE_AUTUMNAL_TAIGA = BUILDER
            .comment("Should the Autumnal Taiga (Orange/Yellow Pines) generate?")
            .define("biomes.enable_autumnal_taiga", true);

    public static final ModConfigSpec.BooleanValue ENABLE_LARCH_FOREST = BUILDER
            .comment("Should the Siberian-inspired Larch Forest generate?")
            .define("biomes.enable_larch_forest", false);

    public static final ModConfigSpec.BooleanValue ENABLE_SNOWBLOSSOM_GROVE = BUILDER
            .comment("Should the Snowblossom Grove generate?")
            .define("biomes.enable_snowblossom_grove", false);

    // Wetlands & Rivers
    public static final ModConfigSpec.BooleanValue ENABLE_MIRROR_MIRE = BUILDER
            .comment("Should the Mirror Mire generate as a rare heart within Blue Groves?")
            .define("biomes.enable_mirror_mire", true);

    public static final ModConfigSpec.BooleanValue ENABLE_CYPRESS_FEN = BUILDER
            .comment("Should the Cypress Fen biome generate?")
            .define("biomes.enable_cypress_fen", true);

    public static final ModConfigSpec.BooleanValue ENABLE_SILVER_BIRCH_WETLAND = BUILDER
            .comment("Should the watery Silver Birch Wetland generate?")
            .define("biomes.enable_silver_birch_wetland", false);

    public static final ModConfigSpec.BooleanValue ENABLE_GALLERY_FOREST = BUILDER
            .comment("Should the narrow Gallery Forest generate along rivers?")
            .define("biomes.enable_gallery_forest", false);

    // Exotic & Tropical
    public static final ModConfigSpec.BooleanValue ENABLE_RAINFOREST = BUILDER
            .comment("Should the Rainforest biome generate?")
            .define("biomes.enable_rainforest", true);

    public static final ModConfigSpec.BooleanValue ENABLE_SAFFRON_GROVE = BUILDER
            .comment("Should the humid, orange-hazed Saffron Grove generate?")
            .define("biomes.enable_saffron_grove", true);

    public static final ModConfigSpec.BooleanValue ENABLE_MYRTLE_FIELDS = BUILDER
            .comment("Should the flowering Myrtle Fields generate?")
            .define("biomes.enable_myrtle_fields", true);

    public static final ModConfigSpec.BooleanValue ENABLE_FUNGAL_REACH = BUILDER
            .comment("Should the vertical, misty Fungal Reach generate?")
            .define("biomes.enable_fungal_reach", false);

    public static final ModConfigSpec.BooleanValue ENABLE_AMBER_SANDS = BUILDER
            .comment("Should the rare, crystalline Amber Sands generate?")
            .define("biomes.enable_amber_sands", true);

    public static final ModConfigSpec.BooleanValue ENABLE_SILVER_BIRCH_VELD = BUILDER
            .comment("Should the Silver Birch Veld generate?")
            .define("biomes.enable_silver_birch_veld", false);

    public static final ModConfigSpec.BooleanValue ENABLE_CALDERA_GROTTO = BUILDER
            .comment("Should the Caldera Grotto generate?")
            .define("biomes.enable_caldera_grotto", false);

    // --- REGION SETTINGS ---
    public static final ModConfigSpec.IntValue COASTAL_REGION_WEIGHT = BUILDER
            .comment("The weight of the Novam Terram Coastal regions.")
            .defineInRange("region.coastal_weight", 10, 0, 100);

    public static final ModConfigSpec.IntValue OVERWORLD_REGION_WEIGHT = BUILDER
            .comment("The weight of the Novam Terram Overworld regions.")
            .defineInRange("region.overworld_weight", 10, 0, 100);

    public static final ModConfigSpec.IntValue PEAK_REGION_WEIGHT = BUILDER
            .comment("The weight of the Novam Terram Peak region.")
            .defineInRange("region.peak_weight", 10, 0, 100);

    public static final ModConfigSpec.IntValue SUB_REGION_WEIGHT = BUILDER
            .comment("The weight of the Novam Terram Subbiome region.")
            .defineInRange("region.sub_weight", 3, 0, 100);

    static final ModConfigSpec SPEC = BUILDER.build();
}