package net.candyperfumeboy.novamterram.worldgen.biome;

import net.candyperfumeboy.novamterram.Config;
import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;

public class NTTerrablender {
    public static void registerRegions() {
        // We replaced '15' with Config.REGION_WEIGHT.get()
        // Now you can change this value in the .toml file!
        Regions.register(new NTOverworldRegion(
                ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "overworld"),
                Config.REGION_WEIGHT.get()
        ));
    }
}