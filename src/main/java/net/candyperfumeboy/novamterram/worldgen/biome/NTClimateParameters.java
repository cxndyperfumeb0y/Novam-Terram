package net.candyperfumeboy.novamterram.worldgen.biome;

import net.minecraft.world.level.biome.Climate;

/**
 * NTClimateParameters
 *
 * Centralised climate parameter constants shared across all NT region classes.
 * Having one source of truth prevents drift between region files and makes
 * tuning a single parameter propagate everywhere automatically.
 *
 * All constants are package-private static finals — import this class in each
 * region file and reference via NTClimateParameters.T_WARM etc., or have each
 * region class extend/import statically for brevity.
 *
 * ========================================================================================
 * TERRABLENDER UNIQUENESS MODEL
 * ========================================================================================
 *
 * Each registered Region gets its own uniqueness slice of the world.
 * Regions do NOT compete with each other in the same parameter pass —
 * they carve out separate geographic zones where their biome list applies.
 *
 * This is the fundamental reason for using multiple region classes:
 *   - NTCoastalRegion's Aegean entries physically cannot interfere with
 *     NTOverworldRegion's inland entries — they exist in different uniqueness bands.
 *   - NTSubBiomeRegion at low weight (3) means Mirror Mire's band covers only
 *     ~11% of the world by weight (3 / ~27 total), making it rare by construction
 *     rather than relying on narrow parameters to accidentally limit it.
 *
 * ========================================================================================
 * PV FORMULA REFERENCE (weirdness → terrain height)
 * ========================================================================================
 *
 * PV = 1 - |3 * |weirdness| - 2|
 *
 * Peak terrain (PV > 0.7) only occurs where |weirdness| ≈ 0.56 to 0.78:
 *   w = -1.00 → PV =  0.00  (valleys)
 *   w = -0.78 → PV =  0.66
 *   w = -0.70 → PV =  0.90  ✓ peaks
 *   w = -0.667→ PV =  1.00  ✓ peaks (max)
 *   w = -0.56 → PV =  0.68
 *   w = -0.40 → PV =  0.20  (flat — NOT peaks)
 *   w =  0.00 → PV = -1.00  (valleys — most sunken)
 *
 * W_PEAKS_NEG = span(-0.78, -0.56) is the only range that reliably forces
 * peak terrain. Use it combined with C_DEEP_INLAND + E_JAGGED for full height.
 */
public final class NTClimateParameters {

    // Non-instantiable
    private NTClimateParameters() {}

    // ================================================================================
    // TEMPERATURE
    // ================================================================================

    public static final Climate.Parameter T_FROZEN      = Climate.Parameter.span(-1.00F, -0.45F);

    /**
     * T_FROZEN_EDGE: the coldest slice of T_COLD, overlapping T_FROZEN at the seam.
     * Used for Mirror Mire in NTSubBiomeRegion — this biome activates at temperature
     * dips toward freezing that occur naturally within Blue Grove's T_COLD territory.
     * Because it overlaps T_COLD's cold end, Mirror Mire appears as frost pockets
     * inside Blue Grove zones rather than in the open frozen tundra.
     */
    public static final Climate.Parameter T_FROZEN_EDGE = Climate.Parameter.span(-0.48F, -0.30F);

    public static final Climate.Parameter T_COLD        = Climate.Parameter.span(-0.48F, -0.18F);

    /**
     * T_BLUE_GROVE: the full temperature range of Blue Grove.
     * Spans from the core of T_COLD down into the cold edge of T_TEMPERATE,
     * allowing Blue Grove to appear in both:
     *   - True cold taiga zones (T_COLD core, snowy/icy feel)
     *   - Cool temperate transition zones (cold edge of T_TEMPERATE, lakeside feel)
     * Must be wider than T_COLD alone so Mirror Mire's T_COLD subset fits inside it.
     * Mirror Mire uses plain T_COLD — a strict subset of this range.
     */
    public static final Climate.Parameter T_BLUE_GROVE  = Climate.Parameter.span(-0.48F, -0.05F);

    public static final Climate.Parameter T_TEMPERATE   = Climate.Parameter.span(-0.20F,  0.17F);
    public static final Climate.Parameter T_WARM        = Climate.Parameter.span( 0.15F,  0.55F);
    public static final Climate.Parameter T_SCORCHING   = Climate.Parameter.span( 0.52F,  1.00F);

    // ================================================================================
    // HUMIDITY
    // ================================================================================

    public static final Climate.Parameter H_WASTELAND   = Climate.Parameter.span(-1.00F, -0.35F);
    public static final Climate.Parameter H_ARID        = Climate.Parameter.span(-0.38F, -0.12F);
    public static final Climate.Parameter H_AVERAGE     = Climate.Parameter.span(-0.13F,  0.10F);
    public static final Climate.Parameter H_FERTILE     = Climate.Parameter.span( 0.07F,  0.35F);

    /**
     * H_BLUE_GROVE: the humidity range of Blue Grove — fertile through rainy.
     * Wider than H_FERTILE alone, allowing Blue Grove to appear in:
     *   - Damp mossy forests (H_FERTILE core)
     *   - Wetter lakeside and misty zones (into H_RAINY)
     * Mirror Mire uses plain H_FERTILE — a strict subset of this range, keeping
     * it locked to the dampest-but-not-soaking portions of Blue Grove territory.
     */
    public static final Climate.Parameter H_BLUE_GROVE  = Climate.Parameter.span( 0.07F,  0.50F);

    public static final Climate.Parameter H_RAINY       = Climate.Parameter.span( 0.32F,  1.00F);

    /**
     * H_ANY: all humidities. Use ONLY where humidity genuinely doesn't differentiate
     * biome character (Mirror Mire in freezing temp; Aegean disabled coast fallbacks).
     * NEVER use for coast or ocean entries — it causes single entries to dominate
     * entire coastline temperature bands.
     */
    public static final Climate.Parameter H_ANY         = Climate.Parameter.span(-1.00F,  1.00F);

    // ================================================================================
    // CONTINENTALNESS
    //
    // C_COASTLINE (-0.19 to -0.11): ALWAYS above sea level — the land/sea interface.
    // C_OCEAN (-0.455 to -0.19):    ALWAYS submerged. Never use for surface biomes.
    // ================================================================================

    public static final Climate.Parameter C_MUSHROOM    = Climate.Parameter.span(-1.20F, -1.05F);
    public static final Climate.Parameter C_DEEP_OCEAN  = Climate.Parameter.span(-1.05F, -0.455F);
    public static final Climate.Parameter C_OCEAN       = Climate.Parameter.span(-0.455F, -0.19F);
    public static final Climate.Parameter C_COASTLINE   = Climate.Parameter.span(-0.19F, -0.11F);
    public static final Climate.Parameter C_NEAR_INLAND = Climate.Parameter.span(-0.11F,  0.07F);
    public static final Climate.Parameter C_INLAND      = Climate.Parameter.span( 0.03F,  0.30F);

    /**
     * C_DEEP_INLAND: far inland, high base terrain height.
     * Required (along with E_JAGGED + W_PEAKS_NEG) to reach Stony Peaks heights.
     */
    public static final Climate.Parameter C_DEEP_INLAND = Climate.Parameter.span( 0.27F,  1.00F);

    // ================================================================================
    // EROSION
    //
    // E_JAGGED (-1.0 to -0.55): Required for peak heights alongside C_DEEP_INLAND.
    // E_BASINS (0.80 to 1.00):  Sunken hollow terrain — used for Mirror Mire.
    // ================================================================================

    public static final Climate.Parameter E_JAGGED      = Climate.Parameter.span(-1.00F, -0.55F);
    public static final Climate.Parameter E_HIGHLANDS   = Climate.Parameter.span(-0.60F, -0.20F);
    public static final Climate.Parameter E_STANDARD    = Climate.Parameter.span(-0.22F,  0.10F);
    public static final Climate.Parameter E_FLATS       = Climate.Parameter.span( 0.07F,  0.55F);
    public static final Climate.Parameter E_CANYONS     = Climate.Parameter.span( 0.45F,  0.85F);
    public static final Climate.Parameter E_BASINS      = Climate.Parameter.span( 0.80F,  1.00F);

    // ================================================================================
    // WEIRDNESS
    //
    // W_PEAKS_NEG: the ONLY weirdness range that guarantees peak terrain (PV > 0.66).
    // Centred on -0.667 where PV = 1.0 (maximum). See formula above.
    // ================================================================================

    /** Negative-side peak weirdness: PV 0.66–1.00. Use for all peak biomes. */
    public static final Climate.Parameter W_PEAKS_NEG   = Climate.Parameter.span(-0.78F, -0.56F);

    public static final Climate.Parameter W_EXPANSIVE   = Climate.Parameter.span(-1.00F, -0.20F);
    public static final Climate.Parameter W_NORMAL      = Climate.Parameter.span(-0.25F,  0.35F);
    public static final Climate.Parameter W_VARIED      = Climate.Parameter.span( 0.30F,  0.80F);
    public static final Climate.Parameter W_SPIRES      = Climate.Parameter.span( 0.75F,  1.00F);
}