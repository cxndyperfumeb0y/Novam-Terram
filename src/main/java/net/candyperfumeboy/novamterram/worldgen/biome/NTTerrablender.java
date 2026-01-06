package net.candyperfumeboy.novamterram.worldgen.biome;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;

public class NTTerrablender {
    public static void registerRegions() {
        // The ID here should be unique. "overworld" is a good sub-path.
        // Weight (5) determines how often your biomes appear compared to vanilla.
        Regions.register(new NTOverworldRegion(
                ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "overworld"), 150));
    }
}