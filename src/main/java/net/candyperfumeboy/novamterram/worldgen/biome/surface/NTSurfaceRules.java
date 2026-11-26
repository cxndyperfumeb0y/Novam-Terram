package net.candyperfumeboy.novamterram.worldgen.biome.surface;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.worldgen.biome.NTBiomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class NTSurfaceRules {

    // ========================================================================================
    // 1. BLOCK STATES
    // ========================================================================================

    // --- Vanilla blocks ---
    private static final SurfaceRules.RuleSource GRASS        = SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState());
    private static final SurfaceRules.RuleSource DIRT         = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
    private static final SurfaceRules.RuleSource COARSE_DIRT  = SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState());
    private static final SurfaceRules.RuleSource MUD          = SurfaceRules.state(Blocks.MUD.defaultBlockState());
    private static final SurfaceRules.RuleSource ROOTED_DIRT  = SurfaceRules.state(Blocks.ROOTED_DIRT.defaultBlockState());
    private static final SurfaceRules.RuleSource CLAY         = SurfaceRules.state(Blocks.CLAY.defaultBlockState());
    private static final SurfaceRules.RuleSource GRAVEL       = SurfaceRules.state(Blocks.GRAVEL.defaultBlockState());
    private static final SurfaceRules.RuleSource SAND         = SurfaceRules.state(Blocks.SAND.defaultBlockState());
    private static final SurfaceRules.RuleSource RED_SAND     = SurfaceRules.state(Blocks.RED_SAND.defaultBlockState());
    private static final SurfaceRules.RuleSource PODZOL       = SurfaceRules.state(Blocks.PODZOL.defaultBlockState());
    private static final SurfaceRules.RuleSource ANDESITE     = SurfaceRules.state(Blocks.ANDESITE.defaultBlockState());
    private static final SurfaceRules.RuleSource GRANITE      = SurfaceRules.state(Blocks.GRANITE.defaultBlockState());
    private static final SurfaceRules.RuleSource CALCITE      = SurfaceRules.state(Blocks.CALCITE.defaultBlockState());
    private static final SurfaceRules.RuleSource TUFF         = SurfaceRules.state(Blocks.TUFF.defaultBlockState());
    private static final SurfaceRules.RuleSource MOSSY_COBBLE = SurfaceRules.state(Blocks.MOSSY_COBBLESTONE.defaultBlockState());
    private static final SurfaceRules.RuleSource SNOW_BLOCK   = SurfaceRules.state(Blocks.SNOW_BLOCK.defaultBlockState());
    private static final SurfaceRules.RuleSource PACKED_ICE   = SurfaceRules.state(Blocks.PACKED_ICE.defaultBlockState());
    private static final SurfaceRules.RuleSource ICE          = SurfaceRules.state(Blocks.ICE.defaultBlockState());
    private static final SurfaceRules.RuleSource STONE        = SurfaceRules.state(Blocks.STONE.defaultBlockState());
    private static final SurfaceRules.RuleSource BASALT       = SurfaceRules.state(Blocks.BASALT.defaultBlockState());
    private static final SurfaceRules.RuleSource SMOOTH_BASALT = SurfaceRules.state(Blocks.SMOOTH_BASALT.defaultBlockState());
    private static final SurfaceRules.RuleSource MYCELIUM     = SurfaceRules.state(Blocks.MYCELIUM.defaultBlockState());

    // --- NT custom blocks ---
    private static final SurfaceRules.RuleSource OLIVINE_SAND      = SurfaceRules.state(NTBlocks.OLIVINE_SAND.get().defaultBlockState());
    private static final SurfaceRules.RuleSource OLIVINE_SANDSTONE = SurfaceRules.state(NTBlocks.OLIVINE_SANDSTONE.get().defaultBlockState());
    private static final SurfaceRules.RuleSource SAFFRON_MOSS      = SurfaceRules.state(NTBlocks.SAFFRON_MOSS.get().defaultBlockState());
    private static final SurfaceRules.RuleSource AMBER_SAND        = SurfaceRules.state(NTBlocks.AMBER_SAND.get().defaultBlockState());
    private static final SurfaceRules.RuleSource AMBER_SANDSTONE   = SurfaceRules.state(NTBlocks.AMBER_SANDSTONE.get().defaultBlockState());
    private static final SurfaceRules.RuleSource BLUE_PODZOL       = SurfaceRules.state(NTBlocks.BLUE_PODZOL.get().defaultBlockState());
    private static final SurfaceRules.RuleSource WHITE_SAND       = SurfaceRules.state(NTBlocks.WHITE_SAND.get().defaultBlockState());
    private static final SurfaceRules.RuleSource WHITE_SANDSTONE       = SurfaceRules.state(NTBlocks.WHITE_SANDSTONE.get().defaultBlockState());


    // ========================================================================================
    // 2. MAIN RULE GENERATION
    // ========================================================================================

    public static SurfaceRules.RuleSource makeRules() {
        // Only paint the real surface, so these blocks don't show up in caves
        // or replace the deep layers and bedrock.
        return SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), biomeRules());
    }

    private static SurfaceRules.RuleSource biomeRules() {
        return SurfaceRules.sequence(

                // -----------------------------------------------------------------------
                // A. GLOBAL CLIFF / STEEP FACE OVERRIDES
                //    Applied to all NT biomes before any biome-specific rule.
                //    When a face is steep and exposed (not underwater), show the
                //    geologically appropriate rock for that biome.
                // -----------------------------------------------------------------------
                SurfaceRules.ifTrue(SurfaceRules.steep(),
                        SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, true, CaveSurface.FLOOR),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.AEGEAN, NTBiomes.MAQUIS),   CALCITE),
                                                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.ANDEAN_HILLS),              ANDESITE),
                                                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.AMBER_SANDS),               AMBER_SANDSTONE),
                                                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.CALDERA_GROTTO),            BASALT),
                                                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.ROYAL_FOREST),              MOSSY_COBBLE),
                                                STONE // fallback for all other NT biomes
                                        )
                                )
                        )
                ),


                // -----------------------------------------------------------------------
                // B. BIOME-SPECIFIC SURFACE SEQUENCES
                //    Order: cold/snowy → wetlands → forests → grasslands →
                //           mediterranean → arid/volcanic → fungal
                // -----------------------------------------------------------------------
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.BLUE_GROVE),       blueGroveRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.ANDEAN_HILLS),     andeanHillsRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.AUTUMNAL_TAIGA),   autumnalTaigaRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.MIRROR_MIRE),      mirrorMireRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.CYPRESS_FEN),      cypressFenRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.RAINFOREST),       rainforestRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.ROYAL_FOREST),     royalForestRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.MYRTLE_FIELDS),    myrtleFieldsRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.HEATH),            heathRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.AEGEAN),           aegeanRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.MAQUIS),           maquisRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.SAFFRON_GROVE),    saffronGroveRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.AMBER_SANDS),      amberSandsRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.CALDERA_GROTTO),   calderaGrottoRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.FUNGAL_REACH),     fungalReachRules())
        );
    }


    // ========================================================================================
    // 3. UTILITY METHODS
    // ========================================================================================

    /**
     * Wraps an above-water surface rule set so that:
     *  - Above water: the provided rule applies
     *  - At/below water level: a varied seabed applies (clay patches, gravel, sand, dirt)
     *
     * Used by biomes that have rivers/lakes so their underwater floors feel natural.
     */
    private static SurfaceRules.RuleSource wrapUnderwater(SurfaceRules.RuleSource aboveWater) {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0), aboveWater),
                // Underwater seabed: clay pockets > gravel > sand > dirt fallback
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP,   0.4D), CLAY),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.0D), GRAVEL),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH,   0.0D), SAND),
                DIRT
        );
    }

    /**
     * A calcite-flavoured underwater wrapper for Aegean and similar Mediterranean
     * coastal biomes. Calcite/sand seabed instead of the generic dirt fallback.
     */
    private static SurfaceRules.RuleSource wrapUnderwaterCalcite(SurfaceRules.RuleSource aboveWater) {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0), aboveWater),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH,   0.3D), GRAVEL),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.0D), SAND),
                CALCITE
        );
    }


    // ========================================================================================
    // 4. BIOME SURFACE RULES
    //    Each rule must define:  ON_FLOOR  |  UNDER_FLOOR  |  (DEEP_UNDER_FLOOR if notable)
    //    Skipping UNDER_FLOOR will expose raw STONE when the player digs one block down.
    // ========================================================================================

    // ---- BLUE GROVE -----------------------------------------------------------------------
    // Cold, snowy, mystical spruce grove.
    // Surface: blue podzol in ~50 % of the biome, coarse dirt patches, grass otherwise.
    // Elevation snow: above y=130 the floor gets a snow cap — sells the "cold highland" feel.
    // Under: dirt subsurface so digging feels organic, not hollow.
    private static SurfaceRules.RuleSource blueGroveRules() {
        // Snow cap for high-elevation sections of the biome (Andean Hills can push it up).
        // yBlockCheck(absolute(130), 0) = "at or above y=130"
        SurfaceRules.ConditionSource isHighAltitude = SurfaceRules.yBlockCheck(
                VerticalAnchor.absolute(130), 0);

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        // High-altitude snow cap
                        SurfaceRules.ifTrue(isHighAltitude,
                                SurfaceRules.sequence(
                                        // At very high peaks, solid snow block
                                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(160), 0), SNOW_BLOCK),
                                        // Mid-high: noisy mix of snow and blue podzol
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.35D), SNOW_BLOCK),
                                        BLUE_PODZOL
                                )
                        ),
                        // Normal elevation: blue podzol and coarse dirt patches
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.0D),  BLUE_PODZOL),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH,   0.3D),  COARSE_DIRT),
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
        );
    }

    // ---- ANDEAN HILLS ---------------------------------------------------------------------
    // Cold, high-altitude alpine plateau. Temperature is 0.25f so snow appears below ~y=95
    // in vanilla logic, but with our explicit snowcap rule it starts at y=150 visually.
    // Andesite appears heavily at high elevations and under the surface to sell rocky peaks.
    // Under: andesite layer (2-3 blocks deep) gives a satisfying mountain core feel.
    private static SurfaceRules.RuleSource andeanHillsRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                        // 1. High Altitude Check: Above 170, it becomes Andesite
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(170), 0), ANDESITE),

                        // 2. Mid-Mountain: Patchy Rooted Dirt for texture
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.45D), ROOTED_DIRT),

                        // 3. Fallback: Grass for the valley and slopes
                        GRASS
                )),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
        );
    }

    // ---- AUTUMNAL TAIGA -------------------------------------------------------------------
    // Cool mixed forest with autumn tones. Podzol and coarse dirt break up the grass.
    // Under: simple dirt subsurface.
    private static SurfaceRules.RuleSource autumnalTaigaRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.2D), PODZOL),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH,   0.4D), COARSE_DIRT),
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
        );
    }

    // ---- MIRROR MIRE ----------------------------------------------------------------------
    // Cold, still, glassy bog. Temp = -0.05f so it's just below freezing.
    // Surface: clay near water edges, gravel patches on higher ground, grass base.
    // NEW: Packed ice patches appear via noise — mirrors the biome's "frozen mirror" name.
    // NEW: Mud subsurface ensures digging feels boggy, not rocky.
    private static SurfaceRules.RuleSource mirrorMireRules() {
        // Packed ice patches: high threshold = tight clusters in ~15% of the biome
        SurfaceRules.ConditionSource isIcePatch = SurfaceRules.noiseCondition(Noises.SURFACE, 0.7D, 1.0D);

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                        // Water edges: clay and mud
                        SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH,   0.4D), CLAY),
                                MUD
                        )),
                        // Frozen patches on land — the "mirror" effect
                        SurfaceRules.ifTrue(isIcePatch, PACKED_ICE),
                        // Scattered gravel
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH,   0.5D), GRAVEL),
                        GRASS
                )),
                // Mud subsurface — boggy all the way down
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, MUD)
        );
    }

    // ---- CYPRESS FEN ----------------------------------------------------------------------
    // Warm, waterlogged bayou. Temperature 0.8f. Mud and sand near water, rooted dirt
    // and coarse dirt on land. Coniferous-swamp feel.
    // Under: mud subsurface continues the waterlogged-ground theme.
    private static SurfaceRules.RuleSource cypressFenRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                        // Near/at water: mud pockets and sandy channels
                        SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH,   0.2D), MUD),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.1D), SAND)
                        )),
                        // Upland: rooted dirt and coarse dirt give a mossy bayou floor
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH,       0.4D), ROOTED_DIRT),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.NETHERRACK, 0.45D), COARSE_DIRT),
                        GRASS
                )),
                // Mud subsurface — swampy all the way down
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, MUD)
        );
    }

    // ---- RAINFOREST -----------------------------------------------------------------------
    // Hot, dense tropical forest. High mud/rooted dirt content at the surface.
    // NEW: the UNDER_FLOOR fix — stoneDepthCheck needs addSurfaceDepth=true to actually
    //      give a 3-block tuff layer. The original used false (only 1 block).
    // Under: 3-block tuff layer (volcanic red soil aesthetic), then stone.
    private static SurfaceRules.RuleSource rainforestRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH,       0.15D, 0.5D), MUD),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.NETHERRACK, 0.40D, 0.5D), ROOTED_DIRT),
                        GRASS
                ))),
                // FIXED: was stoneDepthCheck(0, false, 3, ...) — false means only 1 block depth.
                //        true = include the surface in the depth count, giving a proper 3-block layer.
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                        SurfaceRules.ifTrue(
                                SurfaceRules.stoneDepthCheck(0, true, 3, CaveSurface.FLOOR),
                                TUFF))
        );
    }

    // ---- ROYAL FOREST ---------------------------------------------------------------------
    // Temperate, stately woodland. Rolling terrain with a carpet of roots and bare soil.
    // NEW: mossy cobblestone near-water edge (the stale comment from the original now
    //      has an actual implementation: MOSSY_COBBLE where waterBlockCheck(-1,0)).
    // NEW: rooted dirt for the forest floor feel.
    // Under: dirt subsurface.
    private static SurfaceRules.RuleSource royalForestRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        // Mossy banks where water meets land
                        SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.3D), ROOTED_DIRT)
                        ),
                        // Bare soil patches and exposed roots on the forest floor
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.5D), DIRT),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH,   0.3D), ROOTED_DIRT),
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
        );
    }

    // ---- MYRTLE FIELDS --------------------------------------------------------------------
    // Open temperate meadow. Mostly grass with coarse dirt variation.
    // NEW: UNDER_FLOOR added — the original was missing this entirely.
    private static SurfaceRules.RuleSource myrtleFieldsRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.7D), COARSE_DIRT),
                        GRASS
                ))),
                // FIXED: was missing UNDER_FLOOR entirely — exposed stone when digging.
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
        );
    }

    // ---- HEATH ----------------------------------------------------------------------------
    // Exposed upland moor. Mud tracks carve through heather-covered ground.
    // Gravel patches add to the moorland texture.
    // NEW: UNDER_FLOOR added — the original was missing this entirely.
    private static SurfaceRules.RuleSource heathRules() {
        // Mud tracks: noise band between -0.1 and 0.1 creates narrow sinuous lines
        SurfaceRules.ConditionSource isMudTrack = SurfaceRules.noiseCondition(Noises.SWAMP, -0.1D, 0.1D);

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        // Mud tracks are highest priority — they cut through everything
                        SurfaceRules.ifTrue(isMudTrack, MUD),
                        // Coarse patches: exposed bare earth on the upland
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.0D),  COARSE_DIRT),
                        // Scattered gravel for moorland texture
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL,  0.4D),  GRAVEL),
                        GRASS
                ))),
                // FIXED: was missing UNDER_FLOOR entirely — exposed stone when digging.
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
        );
    }

    // ---- AEGEAN ---------------------------------------------------------------------------
    // Warm Mediterranean coast. Calcite-dominant with sandy/gravelly edges.
    // FIXED: underwater floor now uses calcite fallback via wrapUnderwaterCalcite
    //        instead of the generic dirt fallback — Aegean seafloor should feel stony/sandy.
    // Under: calcite subsurface gives the limestone Mediterranean bedrock feel.
    private static SurfaceRules.RuleSource aegeanRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwaterCalcite(SurfaceRules.sequence(

                        // 1. Beach: only the low ground near sea level
                        SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(66), 0)),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.6D), GRAVEL),
                                        WHITE_SAND
                                )
                        ),

                        // 2. Rocky heights
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(84), 0),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.3D), STONE),
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, -0.3D), CALCITE),
                                        GRASS
                                )
                        ),

                        // 3. Slopes: grass with calcite outcrops
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.5D), CALCITE),
                        GRASS
                ))),

                // Subsurface: Calcite bedrock
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, CALCITE)
        );

    }

    // ---- MAQUIS ---------------------------------------------------------------------------
    // Hot, scrubby Mediterranean hillside. Andesite rock outcrops on dry upland zones.
    // Under: dirt subsurface.
    private static SurfaceRules.RuleSource maquisRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE,     0.3D), COARSE_DIRT),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH,       0.4D), ROOTED_DIRT),
                        // Rock outcrops on the driest zones
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.TEMPERATURE, 0.5D), ANDESITE),
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
        );
    }

    // ---- SAFFRON GROVE --------------------------------------------------------------------
    // Warm, open grove with saffron flowers. Mossy and lush.
    // FIXED: saffron moss threshold raised from 0.2 to 0.4 — at 0.2 it covered ~50% of
    //        the biome making it the dominant surface rather than a patch.
    // Under: dirt subsurface.
    private static SurfaceRules.RuleSource saffronGroveRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        // Saffron moss: special patches, not wall-to-wall coverage
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH,   0.4D), SAFFRON_MOSS),
                        // Podzol: shaded spots under the grove canopy
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.3D), PODZOL),
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
        );
    }

    // ---- AMBER SANDS ----------------------------------------------------------------------
    // Hot, amber-coloured desert with canyon pillars and dune variation.
    // FIXED: underwater check in section C uses waterBlockCheck(0, 0) instead of (-1, 0)
    //        for the deep-underwater amber sand rule — (-1,0) is a near-water edge check.
    // Retained: banded sandstone pillars, canyon floor logic, dune mix.
    private static SurfaceRules.RuleSource amberSandsRules() {
        // Sand pockets: "drifts" of vanilla sand across the amber surface
        SurfaceRules.ConditionSource isSandPocket = SurfaceRules.noiseCondition(Noises.SURFACE, 0.4D, 1.0D);

        // Banded sandstone: used on pillar walls and steep cliffs
        SurfaceRules.RuleSource bandedSandstone = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(5), 5), AMBER_SANDSTONE),
                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(2), 5), RED_SAND),
                AMBER_SANDSTONE
        );

        return SurfaceRules.sequence(
                // A. PILLARS & CLIFFS — 3D structure built first
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.BADLANDS_PILLAR, -0.2D, 1.5D), bandedSandstone),
                SurfaceRules.ifTrue(SurfaceRules.steep(), bandedSandstone),

                // B. FLOOR SURFACE
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                        // Canyon/low areas below y=65
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(65), -64),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(isSandPocket, SAND),
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.7D), AMBER_SANDSTONE),
                                        AMBER_SAND
                                )
                        ),
                        // Dune tops: amber and vanilla sand mix
                        SurfaceRules.ifTrue(isSandPocket, SAND),
                        AMBER_SAND
                )),

                // C. UNDERWATER & UNDERGROUND
                // FIXED: was waterBlockCheck(-1, 0) — near-edge check, not deep-underwater.
                //        (0, 0) correctly fires for blocks fully submerged.
                
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, AMBER_SANDSTONE)
        );
    }

    // ---- CALDERA GROTTO -------------------------------------------------------------------
    // Hot volcanic jungle crater. Basalt column injection, olivine sand in geothermal pools.
    // FIXED: pillar injection yBlockCheck was VerticalAnchor.absolute(62) with range 320,
    //        meaning it fired from y=62 all the way to y=382 — columns intruded into all
    //        surface terrain. Changed to absolute(50) with range 80 so columns only form
    //        in the mid-altitude caldera walls, not on every hill in the biome.
    // Under: dirt then olivine sandstone deep layer.
    private static SurfaceRules.RuleSource calderaGrottoRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                        // These act as "Overlays" – they only trigger if the noise is HIGH
                        // Try 0.55 or higher to make the pockets smaller and rarer
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.55D), MUD),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.6D), OLIVINE_SAND),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.7D), WHITE_SAND),


                        // If neither of the above noise conditions are met, it falls through to this:
                        GRASS
                )),

                // Sub-surface logic
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, ROOTED_DIRT)
        );
    }
    // ---- FUNGAL REACH ---------------------------------------------------------------------
    // Cold, dark mushroom forest. Layered surface: mycelium hotspots, rooted dirt,
    // mud near water. One of the best-written original rules — mostly preserved,
    // with UNDER_FLOOR enhanced to include a rooted dirt layer before stone.
    private static SurfaceRules.RuleSource fungalReachRules() {
        SurfaceRules.ConditionSource isMyceliumSpot = SurfaceRules.noiseCondition(Noises.SURFACE, 0.55D, 1.0D);
        SurfaceRules.ConditionSource isMudPatch     = SurfaceRules.noiseCondition(Noises.SWAMP,   0.1D,  1.0D);
        SurfaceRules.ConditionSource isRootedDirt   = SurfaceRules.noiseCondition(Noises.PATCH,   0.3D,  1.0D);

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                        // Water's edge: mycelium creeping into the mud
                        SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(
                                SurfaceRules.ifTrue(isMyceliumSpot, MYCELIUM),
                                MUD
                        )),
                        // Land: mycelium hotspots → exposed roots → mud patches → grass
                        SurfaceRules.ifTrue(isMyceliumSpot, MYCELIUM),
                        SurfaceRules.ifTrue(isRootedDirt,   ROOTED_DIRT),
                        SurfaceRules.ifTrue(isMudPatch,     MUD),
                        GRASS
                )),
                // UNDER_FLOOR: rooted dirt layer before falling through to stone
                // Enhanced from original: was just mud+dirt, now rooted dirt layer added
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(
                        SurfaceRules.ifTrue(isMudPatch,   MUD),
                        SurfaceRules.ifTrue(isRootedDirt, ROOTED_DIRT),
                        DIRT
                ))
        );
    }
}