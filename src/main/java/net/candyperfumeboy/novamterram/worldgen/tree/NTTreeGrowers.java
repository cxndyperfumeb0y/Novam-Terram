package net.candyperfumeboy.novamterram.worldgen.tree;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.worldgen.NTConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class NTTreeGrowers {
    public static final TreeGrower BLUESPRUCE_TAIGA_KEY = new TreeGrower(NovamTerram.MOD_ID + ":bluespruce_taiga",
            Optional.empty(), Optional.of(NTConfiguredFeatures.BLUESPRUCE_TAIGA_KEY), Optional.empty());

}