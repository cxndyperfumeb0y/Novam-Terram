package net.candyperfumeboy.novamterram.worldgen.biome.surface;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.worldgen.biome.NTBiomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;


public class NTSurfaceRules {

    public static SurfaceRules.RuleSource makeRules() {
        // 1. Define our custom states
        SurfaceRules.RuleSource grass = SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState());
        SurfaceRules.RuleSource olivineSand = SurfaceRules.state(NTBlocks.OLIVINE_SAND.get().defaultBlockState());
        SurfaceRules.RuleSource olivineSandstone = SurfaceRules.state(NTBlocks.OLIVINE_SANDSTONE.get().defaultBlockState());
        SurfaceRules.RuleSource bluePodzol = SurfaceRules.state(NTBlocks.BLUE_PODZOL.get().defaultBlockState());
        SurfaceRules.RuleSource coarseDirt = SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState());
        SurfaceRules.RuleSource andesite = SurfaceRules.state(Blocks.ANDESITE.defaultBlockState());
        SurfaceRules.RuleSource dirt = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
        return SurfaceRules.sequence(
                // --- AEGEAN ARCHIPELAGO ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.AEGEAN_ARCHIPELAGO),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(110), -64),
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, grass),
                                                SurfaceRules.state(Blocks.CALCITE.defaultBlockState())
                                        )
                                )
                        )
                ),

                // --- OLIVINE BEACH ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.OLIVINE_BEACH),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(5, false, CaveSurface.FLOOR), olivineSand),
                                                olivineSandstone
                                        )
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, olivineSandstone)
                        )
                ),

                // --- HEATH ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.HEATH),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.0, 0.3),
                                                        SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState())),
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.3, 0.5),
                                                        SurfaceRules.state(Blocks.GRAVEL.defaultBlockState())),
                                                grass
                                        )
                                ),
                                SurfaceRules.state(Blocks.DIRT.defaultBlockState())
                        )
                ),

                // --- HOT/DRY PERUVIAN ANDES ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.ANDES),
                        SurfaceRules.sequence(
                                // PEAKS: Above Y=190, everything is cold Andesite stone
                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(190), 0), andesite),

                                // SURFACE: The top layer
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.sequence(
                                                // Use a noise provider to create "patches" of Coarse Dirt
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.0D, 0.4D), coarseDirt),
                                                // Otherwise, use standard Grass
                                                grass
                                        )
                                ),

                                // UNDERWATER/UNDERGROUND: Fill with regular dirt below the grass
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, dirt)
                        )

                ),

                // --- BLUE SPRUCE TAIGA ---
                SurfaceRules.ifTrue(SurfaceRules.isBiome(NTBiomes.BLUE_SPRUCE_TAIGA),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                                        SurfaceRules.sequence(
                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, 0.0D),
                                                        bluePodzol),

                                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.NETHERRACK, 0.5D),
                                                        SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState())),

                                                grass
                                        )
                                ),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.state(Blocks.DIRT.defaultBlockState()))
                        )
                )
        );
    }
}