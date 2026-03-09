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
    
    private static final SurfaceRules.RuleSource GRASS = SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState());
    private static final SurfaceRules.RuleSource DIRT = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
    private static final SurfaceRules.RuleSource COARSE_DIRT = SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState());
    private static final SurfaceRules.RuleSource MUD = SurfaceRules.state(Blocks.MUD.defaultBlockState());
    private static final SurfaceRules.RuleSource ROOTED_DIRT = SurfaceRules.state(Blocks.ROOTED_DIRT.defaultBlockState());
    private static final SurfaceRules.RuleSource CLAY = SurfaceRules.state(Blocks.CLAY.defaultBlockState());
    private static final SurfaceRules.RuleSource GRAVEL = SurfaceRules.state(Blocks.GRAVEL.defaultBlockState());
    private static final SurfaceRules.RuleSource SAND = SurfaceRules.state(Blocks.SAND.defaultBlockState());
    private static final SurfaceRules.RuleSource RED_SAND = SurfaceRules.state(Blocks.RED_SAND.defaultBlockState());
    private static final SurfaceRules.RuleSource PODZOL = SurfaceRules.state(Blocks.PODZOL.defaultBlockState());
    private static final SurfaceRules.RuleSource ANDESITE = SurfaceRules.state(Blocks.ANDESITE.defaultBlockState());
    private static final SurfaceRules.RuleSource GRANITE = SurfaceRules.state(Blocks.GRANITE.defaultBlockState());
    private static final SurfaceRules.RuleSource CALCITE = SurfaceRules.state(Blocks.CALCITE.defaultBlockState());
    private static final SurfaceRules.RuleSource TUFF = SurfaceRules.state(Blocks.TUFF.defaultBlockState());
    private static final SurfaceRules.RuleSource MOSSY_COBBLE = SurfaceRules.state(Blocks.MOSSY_COBBLESTONE.defaultBlockState());
    private static final SurfaceRules.RuleSource SNOW_BLOCK = SurfaceRules.state(Blocks.SNOW_BLOCK.defaultBlockState());
    private static final SurfaceRules.RuleSource PACKED_ICE = SurfaceRules.state(Blocks.PACKED_ICE.defaultBlockState());
    private static final SurfaceRules.RuleSource STONE = SurfaceRules.state(Blocks.STONE.defaultBlockState());
    private static final SurfaceRules.RuleSource OLIVINE_SAND = SurfaceRules.state(NTBlocks.OLIVINE_SAND.get().defaultBlockState());
    private static final SurfaceRules.RuleSource OLIVINE_SANDSTONE = SurfaceRules.state(NTBlocks.OLIVINE_SANDSTONE.get().defaultBlockState());
    private static final SurfaceRules.RuleSource SAFFRON_MOSS = SurfaceRules.state(NTBlocks.SAFFRON_MOSS.get().defaultBlockState());
    private static final SurfaceRules.RuleSource AMBER_SAND = SurfaceRules.state(NTBlocks.AMBER_SAND.get().defaultBlockState());
    private static final SurfaceRules.RuleSource AMBER_SANDSTONE = SurfaceRules.state(NTBlocks.AMBER_SANDSTONE.get().defaultBlockState());
    private static final SurfaceRules.RuleSource MYCELIUM = SurfaceRules.state(Blocks.MYCELIUM.defaultBlockState());

    // ========================================================================================
    // 2. MAIN RULE GENERATION
    // ========================================================================================

    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.RuleSource bluePodzol = SurfaceRules.state(NTBlocks.BLUE_PODZOL.get().defaultBlockState());

        return SurfaceRules.sequence(
                // --- A. GLOBAL OVERLAYS (Applies to all NT biomes) ---
                // If very steep and not underwater, show stone/cliff blocks
                SurfaceRules.ifTrue(SurfaceRules.steep(),
                        SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0), // 0 means AIR (not water)
                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, true, CaveSurface.FLOOR),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.RED_PEAKS), GRANITE),
                                                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.AEGEAN), CALCITE),
                                                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.MAQUIS, NTBiomes.ANDEAN_HILLS), ANDESITE),
                                                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.AMBER_SANDS), AMBER_SANDSTONE),

                                                STONE // This acts as the fallback for all other biomes
                                        )
                                )
                        )
                ),

                // --- B. BIOME SPECIFIC SEQUENCES ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.AUTUMNAL_TAIGA), autumnalTaigaRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.ROYAL_FOREST), royalForestRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.MYRTLE_FIELDS), myrtleFieldsRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.CYPRESS_FEN), cypressFenRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.MIRROR_MIRE), mirrorMireRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.RAINFOREST), rainforestRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.AEGEAN), aegeanRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.MAQUIS), maquisRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.HEATH), heathRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.ANDEAN_HILLS), andeanHillsRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.BLUE_GROVE), blueGroveRules(bluePodzol)),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.CALDERA_GROTTO), calderaGrottoRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.SAFFRON_GROVE), saffronGroveRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.AMBER_SANDS), amberSandsRules()),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.FUNGAL_REACH), fungalReachRules())
        );
    }

    // ========================================================================================
    // 3. UTILITY METHODS
    // ========================================================================================


    private static SurfaceRules.RuleSource wrapUnderwater(SurfaceRules.RuleSource aboveWater) {
        return SurfaceRules.sequence(
                // If NOT underwater (waterBlockCheck(0,0) means fluid level is 0/Air), apply land rules
                SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0), aboveWater),
                
                // Otherwise (Underwater), apply seabed rules:
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.4D), CLAY),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.0D), GRAVEL),
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.0D), SAND),
                DIRT
        );
    }

    // ========================================================================================
    // 4. BIOME RULES
    // ========================================================================================

    private static SurfaceRules.RuleSource saffronGroveRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        // Saffron Moss patches (Noise based)
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.2D), SAFFRON_MOSS),
                        // Podzol patches (Noise based)
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.3D), PODZOL),
                        // Default to Grass
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
        );
    }

    private static SurfaceRules.RuleSource amberSandsRules() {
        // 1. Noise for regular Sand pockets (using PILLAR noise at a low threshold creates nice "drifts")
        SurfaceRules.ConditionSource isSandPocket = SurfaceRules.noiseCondition(Noises.SURFACE, 0.4D, 1.0D);

        // 2. Your Banded Sandstone logic for walls/pillars
        SurfaceRules.RuleSource bandedSandstone = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(5), 5), AMBER_SANDSTONE),
                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(2), 5), RED_SAND),
                AMBER_SANDSTONE
        );

        return SurfaceRules.sequence(
                // A. PILLARS & CLIFFS: Build the 3D structures first
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.BADLANDS_PILLAR, -0.2D, 1.5D), bandedSandstone),
                SurfaceRules.ifTrue(SurfaceRules.steep(), bandedSandstone),

                // B. SURFACE FLOOR
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                        // Canyon floor / Low areas
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(65), -64),
                                SurfaceRules.sequence(
                                        // Mix in Sand pockets even in the canyon floor
                                        SurfaceRules.ifTrue(isSandPocket, SAND),
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.7D), AMBER_SANDSTONE),
                                        AMBER_SAND
                                )
                        ),

                        // Default Dune surface: Alternate between Amber and Regular Sand
                        SurfaceRules.ifTrue(isSandPocket, SAND),
                        AMBER_SAND
                )),

                // C. UNDERWATER & UNDERGROUND
                SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), AMBER_SAND),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, AMBER_SANDSTONE)
        );
    }

    private static SurfaceRules.RuleSource fungalReachRules() {
        // 1. Define the Noise Conditions
        // Higher threshold for Mycelium so it stays in clumps
        SurfaceRules.ConditionSource isMyceliumSpot = SurfaceRules.noiseCondition(Noises.SURFACE, 0.55D, 1.0D);
        // Mud patches in the basins
        SurfaceRules.ConditionSource isMudPatch = SurfaceRules.noiseCondition(Noises.SWAMP, 0.1D, 1.0D);
        // Rooted Dirt scattered across the jungle floor
        SurfaceRules.ConditionSource isRootedDirt = SurfaceRules.noiseCondition(Noises.PATCH, 0.3D, 1.0D);

        return SurfaceRules.sequence(
                // --- SURFACE LOGIC (ON FLOOR) ---
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(

                        // A. The Water's Edge / Lowlands
                        // Near water level, we want mostly Mud with a little Mycelium creep
                        SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(isMyceliumSpot, MYCELIUM),
                                        MUD // Everything else near water is Mud
                                )),

                        // B. The Lush Forest Floor
                        // Priority 1: Fungal "Hotspots"
                        SurfaceRules.ifTrue(isMyceliumSpot, MYCELIUM),

                        // Priority 2: "Exposed Roots" (Rooted Dirt)
                        SurfaceRules.ifTrue(isRootedDirt, SurfaceRules.state(Blocks.ROOTED_DIRT.defaultBlockState())),

                        // Priority 3: Mud patches even on high ground (it's a swampy jungle!)
                        SurfaceRules.ifTrue(isMudPatch, MUD),

                        // C. Fallback: The dominant lush green Grass
                        GRASS
                )),

                // --- UNDER-SURFACE LOGIC ---
                // Ensures you don't see stone immediately if you break a mud block
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(
                        SurfaceRules.ifTrue(isMudPatch, MUD),
                        DIRT
                ))
        );
    }

    private static SurfaceRules.RuleSource autumnalTaigaRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.2D), PODZOL),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.4D), COARSE_DIRT),
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
        );
    }

    private static SurfaceRules.RuleSource royalForestRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        // Mossy patches near water levels

                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.5D), DIRT),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.3D), ROOTED_DIRT),
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
        );
    }


    private static SurfaceRules.RuleSource myrtleFieldsRules() {
        return SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.7D), COARSE_DIRT),
                GRASS
        )));
    }

    private static SurfaceRules.RuleSource cypressFenRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                        // Swamp Logic: Check for water proximity
                        SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.2D), MUD),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.1D), SAND)
                        )),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.4D), ROOTED_DIRT),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.NETHERRACK, 0.45D), COARSE_DIRT),
                        GRASS
                )),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, MUD)
        );
    }

    private static SurfaceRules.RuleSource mirrorMireRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                        // Swamp Logic: Check for water proximity
                        SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.4D), CLAY),
                                MUD
                        )),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.5D), GRAVEL),
                        GRASS
                )),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, MUD)
        );
    }

    private static SurfaceRules.RuleSource rainforestRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.15D, 0.5D), MUD),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.NETHERRACK, 0.4D, 0.5D), ROOTED_DIRT),
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, 
                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, 3, CaveSurface.FLOOR), TUFF))
        );
    }

    private static SurfaceRules.RuleSource aegeanRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        // Aridity Check: dryer areas have Calcite/Sand instead of grass
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.TEMPERATURE, 0.4D),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.3D), SAND),
                                        CALCITE
                                )),
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, CALCITE)
        );
    }

    private static SurfaceRules.RuleSource calderaGrottoRules() {
        // Your Basalt Wall logic with vertical striping
        SurfaceRules.RuleSource basaltWall = SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(4), 4),
                        SurfaceRules.state(Blocks.SMOOTH_BASALT.defaultBlockState())),
                SurfaceRules.state(Blocks.BASALT.defaultBlockState())
        );

        return SurfaceRules.sequence(
                // NUCLEAR PILLAR INJECTION: Force basalt columns based on noise
                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.BADLANDS_PILLAR, 0.0D, 1.5D),
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 320), basaltWall)
                ),

                // STEEP WALLS: Natural canyon/caldera sides
                SurfaceRules.ifTrue(SurfaceRules.steep(), basaltWall),

                // THE FLOOR (Dune tops and Basin floor)
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                        // Flooded/Wet Areas (The Sugi effect)
                        SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.4D), OLIVINE_SAND)
                        ),
                        // Mud and Accents
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.65D),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.3D), MUD),
                                        ROOTED_DIRT
                                )
                        ),
                        GRASS
                )),

                // UNDERGROUND
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT),
                SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, OLIVINE_SANDSTONE)
        );
    }
    private static SurfaceRules.RuleSource maquisRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.3D), COARSE_DIRT),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.4D), ROOTED_DIRT),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.TEMPERATURE, 0.5D), ANDESITE),
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
        );
    }

    private static SurfaceRules.RuleSource heathRules() {
        // Use SWAMP noise for "tracks" or "paths" as it varies more locally than CONTINENTALNESS
        SurfaceRules.ConditionSource mudTrackNoise = SurfaceRules.noiseCondition(Noises.SWAMP, -0.1D, 0.1D);

        return SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(
                SurfaceRules.sequence(
                        // 1. THE MUD TRACKS (Highest Priority - carves through everything)
                        SurfaceRules.ifTrue(mudTrackNoise, MUD),

                        // 2. THE COARSE PATCHES (Intermediate Priority)
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.0D), COARSE_DIRT),

                        // Gravelly patches (smaller, more scattered)
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, 0.4D), GRAVEL),

                        // 3. THE BASE (Lowest Priority - fills everything else)
                        GRASS
                )
        ));
    }

    private static SurfaceRules.RuleSource andeanHillsRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        // High Andean Peaks
                        SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(170), 0), ANDESITE),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.45D), ANDESITE),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.0D, 0.4D), COARSE_DIRT),
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR,
                        SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, 3, CaveSurface.FLOOR), ANDESITE))
        );
    }

    private static SurfaceRules.RuleSource blueGroveRules(SurfaceRules.RuleSource bluePodzol) {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.0D), bluePodzol),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.3D), COARSE_DIRT),
                        GRASS
                )))
        );
    }

    private static SurfaceRules.RuleSource cycladesRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, wrapUnderwater(SurfaceRules.sequence(
                        // Rocky Greek coastlines
                        SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.0D), GRAVEL),
                                        SAND
                                )),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.TEMPERATURE, 0.2D), CALCITE),
                        GRASS
                ))),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, CALCITE)
        );
    }
}