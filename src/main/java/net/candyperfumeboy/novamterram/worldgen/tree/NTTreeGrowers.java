package net.candyperfumeboy.novamterram.worldgen.tree;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.worldgen.NTConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class NTTreeGrowers {

    // BLUE SPRUCE: Regular Taiga tree, with a Mega variant for 2x2
    public static final TreeGrower BLUE_SPRUCE = new TreeGrower(
            NovamTerram.MOD_ID + ":blue_spruce",
            Optional.of(NTConfiguredFeatures.MEGA_BLUE_SPRUCE_KEY),
            Optional.of(NTConfiguredFeatures.BLUESPRUCE_TAIGA_KEY),
            Optional.empty()
    );

    // TEAK
    public static final TreeGrower TEAK = new TreeGrower(
            NovamTerram.MOD_ID + ":teak",
            Optional.empty(),
            Optional.of(NTConfiguredFeatures.TEAK_TREE_KEY),
            Optional.empty()
    );

    // BALSA
    public static final TreeGrower BALSA = new TreeGrower(
            NovamTerram.MOD_ID + ":balsa",
            Optional.empty(),
            Optional.of(NTConfiguredFeatures.BALSA_TREE_KEY),
            Optional.empty()
    );

    // MAHOGANY
    public static final TreeGrower MAHOGANY = new TreeGrower(
            NovamTerram.MOD_ID + ":mahogany",
            Optional.empty(),
            Optional.of(NTConfiguredFeatures.MAHOGANY_SHRUB_KEY),
            Optional.empty()
    );

    // ASPEN
    public static final TreeGrower ASPEN = new TreeGrower(
            NovamTerram.MOD_ID + ":aspen",
            Optional.empty(),
            Optional.of(NTConfiguredFeatures.SILVER_ASPEN_KEY),
            Optional.empty()
    );

    // OLIVE
    public static final TreeGrower OLIVE = new TreeGrower(
            NovamTerram.MOD_ID + ":olive",
            Optional.empty(),
            Optional.of(NTConfiguredFeatures.OLIVE_TREE_KEY),
            Optional.empty()
    );

    // ORANGE AUTUMNAL PINE
    public static final TreeGrower ORANGE_AUTUMNAL_PINE = new TreeGrower(
            NovamTerram.MOD_ID + ":orange_autumnal_pine",
            Optional.of(NTConfiguredFeatures.ORANGE_AUTUMNAL_PINE),
            Optional.of(NTConfiguredFeatures.SMALL_ORANGE_PINE),
            Optional.empty()
    );

    // YELLOW AUTUMNAL PINE
    public static final TreeGrower YELLOW_AUTUMNAL_PINE = new TreeGrower(
            NovamTerram.MOD_ID + ":yellow_autumnal_pine",
            Optional.of(NTConfiguredFeatures.YELLOW_AUTUMNAL_PINE),
            Optional.of(NTConfiguredFeatures.SMALL_YELLOW_PINE),
            Optional.empty()
    );

    // PAULOWNIA
    public static final TreeGrower PAULOWNIA = new TreeGrower(
            NovamTerram.MOD_ID + ":paulownia",
            Optional.empty(),
            Optional.of(NTConfiguredFeatures.PAULOWNIA_TREE_KEY),
            Optional.empty()
    );

    // --- NEW: FLOWERING MYRTLE TRIO ---

    // WHITE MYRTLE
    public static final TreeGrower WHITE_MYRTLE = new TreeGrower(
            NovamTerram.MOD_ID + ":white_myrtle",
            Optional.empty(),
            Optional.of(NTConfiguredFeatures.WHITE_MYRTLE_TREE_KEY),
            Optional.empty()
    );

    // RED MYRTLE
    public static final TreeGrower RED_MYRTLE = new TreeGrower(
            NovamTerram.MOD_ID + ":red_myrtle",
            Optional.empty(),
            Optional.of(NTConfiguredFeatures.RED_MYRTLE_TREE_KEY),
            Optional.empty()
    );

    // PURPLE MYRTLE
    public static final TreeGrower PURPLE_MYRTLE = new TreeGrower(
            NovamTerram.MOD_ID + ":purple_myrtle",
            Optional.empty(),
            Optional.of(NTConfiguredFeatures.PURPLE_MYRTLE_TREE_KEY),
            Optional.empty()
    );
}