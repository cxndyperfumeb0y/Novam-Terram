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

/**
 * NTPeakRegion — Andean Hills peak biome placement. Weight: 10.
 *
 * ============================================================================
 * WHY addModifiedVanillaOverworldBiomes
 * ============================================================================
 *
 * The previous raw-addBiome() approach only registered biomes at specific
 * parameter combinations, leaving ocean/coast/near-inland slots uncovered.
 * TerraBlender's nearest-neighbour search then dragged the closest inland or
 * peak entry into those gaps — producing a fully submerged Taiga ocean, or
 * Andean Hills appearing underwater at continental shelf depth.
 *
 * addModifiedVanillaOverworldBiomes() runs vanilla's complete OverworldBiomeBuilder
 * first — deep oceans, oceans, beaches, rivers, stony shores, inland, peaks,
 * everything — then applies our replacements on top. Every parameter point is
 * covered by vanilla before we touch it, so nothing can ever appear submerged.
 *
 * ============================================================================
 * ANDEAN HILLS REPLACEMENT TARGETS
 * ============================================================================
 *
 * Vanilla's warm-temperature peak slots (from OverworldBiomeBuilder):
 *
 *   STONY_PEAKS:       T_WARM × H_ARID/H_AVERAGE × C_DEEP_INLAND × E_JAGGED
 *                      → the true peak slot. Exactly what Andean Hills wants.
 *                      PV is maximal here (W_PEAKS_NEG weirdness band).
 *
 *   SAVANNA_PLATEAU:   T_WARM × H_ARID × C_DEEP_INLAND × E_HIGHLANDS
 *                      → the high plateau approach leading up to the peaks.
 *                      Replacing this extends Andean Hills down onto its own
 *                      foothills rather than cutting off abruptly at peak height.
 *
 *   WINDSWEPT_SAVANNA: T_WARM × H_ARID × C_INLAND × E_JAGGED
 *                      → rugged mid-elevation shoulder slopes.
 *                      Gives Andean Hills a lower visible approach from inland.
 *
 * Biomes we deliberately DO NOT replace:
 *   BADLANDS / WOODED_BADLANDS — H_WASTELAND slots; different climate niche,
 *     act as a natural boundary biome between Andean Hills and dry desert zones.
 *   WINDSWEPT_HILLS / WINDSWEPT_GRAVELLY_HILLS — H_FERTILE/H_AVERAGE slopes;
 *     kept as vanilla shoulders at non-arid humidity, framing Andean Hills
 *     between them without making the entire warm-highland zone one biome.
 *
 * ============================================================================
 * HEIGHT GUARANTEE (unchanged from previous analysis)
 * ============================================================================
 *
 * PV = 1 - |3 * |weirdness| - 2|
 * Peak terrain (PV > 0.66) only at weirdness in roughly (-0.78, -0.56):
 *   w = -0.667 → PV = 1.00 (max height, ~y 220-256)
 *   w = -0.78  → PV = 0.66 (still clearly peaks)
 *
 * Full height recipe: C_DEEP_INLAND + E_JAGGED + W in (-0.78, -0.56).
 * STONY_PEAKS is placed at exactly this combination, so Andean Hills
 * inherits guaranteed peak height by replacing it.
 */
public class NTPeakRegion extends Region {

    public NTPeakRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {

            if (Config.ENABLE_ANDEAN_HILLS.get()) {

                // ── TRUE PEAKS ────────────────────────────────────────────────────────
                // STONY_PEAKS is vanilla's warm-arid deep-inland jagged-erosion biome.
                // Replacing it gives Andean Hills guaranteed peak height (PV ≈ 1.0)
                // at C_DEEP_INLAND + E_JAGGED — the only combination that reaches
                // y ~220-256. This is the core height-guarantee replacement.
                builder.replaceBiome(Biomes.STONY_PEAKS, NTBiomes.ANDEAN_HILLS);

                // ── HIGH PLATEAU APPROACH ─────────────────────────────────────────────
                // SAVANNA_PLATEAU is vanilla's warm-arid elevated highland terrain.
                // Replacing it extends Andean Hills down onto its own foothills,
                // so players see a gradual rise from savanna → Andean plateau → peak
                // rather than an abrupt cliff where Stony Peaks used to begin.
                builder.replaceBiome(Biomes.SAVANNA_PLATEAU, NTBiomes.ANDEAN_HILLS);

                // ── RUGGED SHOULDER SLOPES ────────────────────────────────────────────
                // WINDSWEPT_SAVANNA is vanilla's warm-arid mid-elevation rugged terrain.
                // Replacing it gives Andean Hills a visible lower approach — the rocky
                // scrubby slopes you'd see climbing toward an Andean range from the
                // valley floor.
                builder.replaceBiome(Biomes.WINDSWEPT_SAVANNA, NTBiomes.ANDEAN_HILLS);
            }
        });
    }
}