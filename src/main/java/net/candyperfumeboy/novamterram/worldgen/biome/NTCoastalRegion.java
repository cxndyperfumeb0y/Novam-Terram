package net.candyperfumeboy.novamterram.worldgen.biome;

import com.mojang.datafixers.util.Pair;

import net.candyperfumeboy.novamterram.Config;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils.*;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

import static terrablender.api.ParameterUtils.*;
import static net.candyperfumeboy.novamterram.worldgen.biome.NTClimateParameters.*;

/**
 * NTCoastalRegion — Aegean + Maquis placement. Weight: 10.
 *
 * ============================================================================
 * WHY addModifiedVanillaOverworldBiomes (THE NS/RU PATTERN)
 * ============================================================================
 *
 * The root cause of every submerged-biome and ocean-as-tundra bug in the
 * previous iterations was using raw addBiome() calls with manual fillers.
 * No matter how many ocean/coast/near-inland filler entries you add by hand,
 * the nearest-neighbour search always has gaps and bleeds inland biomes into
 * ocean depth at some parameter combination.
 *
 * addModifiedVanillaOverworldBiomes() solves this completely:
 *   1. It calls vanilla's OverworldBiomeBuilder.addBiomes() internally,
 *      registering the FULL vanilla set: deep oceans, oceans, beaches, stony
 *      shores, rivers, inland, peaks — every parameter point covered.
 *   2. It then applies your replaceBiome() swaps on top of that complete set.
 *
 * Result: this region's uniqueness band has correct vanilla biomes at EVERY
 * parameter combination that we don't explicitly replace. No gaps. No bleed.
 * No submerged forests. No tundra coastlines. This is exactly what NS and RU
 * do in their region classes.
 *
 * ============================================================================
 * AEGEAN PLACEMENT STRATEGY
 * ============================================================================
 *
 * Aegean replaces BEACH at warm temperature. Vanilla places Beach at:
 *   T: WARM (0.2 → 0.55 in vanilla terms), C: COAST, various E and W.
 * By replacing Beach → Aegean we get the clifftop coastal biome at exactly
 * the correct continentalness (C_COASTLINE = always above sea level) with
 * no risk of ever appearing submerged.
 *
 * We also replace STONY_SHORE at warm temperature. Vanilla places Stony Shore
 * at warm C_COAST with high-erosion terrain (E_JAGGED). This gives Aegean
 * the rocky cliff variant that suits a Mediterranean coastline.
 *
 * ============================================================================
 * MAQUIS PLACEMENT STRATEGY
 * ============================================================================
 *
 * Maquis is a dry Mediterranean scrubland that sits just inland from the
 * Aegean coast. It replaces SAVANNA at warm+arid inland parameter points.
 * Vanilla Savanna occupies T_WARM × H_ARID × C_NEAR_INLAND/C_INLAND, which
 * is the exact geographic niche Maquis should occupy — dry warm land just
 * behind the clifftop coast.
 *
 * We also replace WINDSWEPT_SAVANNA (vanilla's rocky warm-arid elevated
 * terrain) to extend Maquis into the hillier inland zones.
 */
public class NTCoastalRegion extends Region {

    public NTCoastalRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {

            // ── AEGEAN ────────────────────────────────────────────────────────────────
            // Replace warm beach and warm stony shore → Aegean.
            //
            // Vanilla Beach occupies: T_WARM × C_COAST × all humidities × low erosion.
            // Vanilla Stony Shore occupies: T_WARM × C_COAST × E_JAGGED (cliffy coast).
            //
            // Both replacements keep Aegean strictly at C_COASTLINE — always above sea
            // level. Nothing here can place Aegean in ocean depth.
            if (Config.ENABLE_AEGEAN.get()) {
                builder.replaceBiome(Biomes.BEACH, NTBiomes.AEGEAN);
                builder.replaceBiome(Biomes.STONY_SHORE, NTBiomes.AEGEAN);
            }

            // ── MAQUIS ────────────────────────────────────────────────────────────────
            // Replace warm-arid inland vanilla biomes → Maquis.
            //
            // Vanilla Savanna: T_WARM × H_ARID × C_NEAR_INLAND/C_INLAND × flat erosion.
            // Vanilla Windswept Savanna: T_WARM × H_ARID × elevated/rugged erosion.
            //
            // This places Maquis as the dry scrubland immediately inland from Aegean,
            // exactly the Mediterranean gradient: sea → cliff (Aegean) → scrub (Maquis).
            if (Config.ENABLE_MAQUIS.get()) {
                builder.replaceBiome(Biomes.SAVANNA, NTBiomes.MAQUIS);
                builder.replaceBiome(Biomes.WINDSWEPT_SAVANNA, NTBiomes.MAQUIS);
            }
            if (Config.ENABLE_MANGROVE_LAGOON.get()) {
                builder.replaceBiome(Biomes.MANGROVE_SWAMP, NTBiomes.MANGROVE_LAGOON);
                builder.replaceBiome(Biomes.BEACH, NTBiomes.MANGROVE_LAGOON);
            }
        });

    }
}