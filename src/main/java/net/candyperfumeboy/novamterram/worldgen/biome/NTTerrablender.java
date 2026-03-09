package net.candyperfumeboy.novamterram.worldgen.biome;

import net.candyperfumeboy.novamterram.Config;
import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;

public class NTTerrablender {

    public static void registerRegions() {
        // Core inland biomes — all standard NT terrain
        Regions.register(new NTOverworldRegion(
                ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "overworld"),
                Config.OVERWORLD_REGION_WEIGHT.get()
        ));

        // Mediterranean coastal biomes — Aegean cliff + Maquis scrubland
        // Isolated so coast parameters can't interfere with inland parameters
        Regions.register(new NTCoastalRegion(
                ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "coastal"),
                Config.COASTAL_REGION_WEIGHT.get()
        ));

        // Peak biomes — Andean Hills
        // Isolated so C_DEEP_INLAND+E_JAGGED peak parameters don't compete with
        // flat inland biome entries in NTOverworldRegion
        Regions.register(new NTPeakRegion(
                ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "peak"),
                Config.PEAK_REGION_WEIGHT.get()
        ));

        // Sub-biome — Mirror Mire (rare frozen frost pockets inside Blue Grove zones)
        // Low weight (3) makes it rare by construction — only ~7% of world bands
        Regions.register(new NTSubBiomeRegion(
                ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "subbiomes"),
                Config.SUB_REGION_WEIGHT.get()
        ));
    }
}

