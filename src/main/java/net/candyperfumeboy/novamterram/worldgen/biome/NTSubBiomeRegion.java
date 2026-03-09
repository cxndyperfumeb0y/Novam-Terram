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
 * NTSubBiomeRegion — rare sub-biome placement. Weight: 3.
 *
 * Contains: Mirror Mire, Caldera Grotto, Fungal Reach.
 *
 * ============================================================================
 * THE SUB-BIOME PATTERN
 * ============================================================================
 *
 * A "sub-biome" in TerraBlender is a biome that appears as a rare variant
 * inside another biome's geographic territory. The mechanism is:
 *
 *   1. Register a separate Region at LOW WEIGHT (3 here vs 10 for main regions).
 *   2. Use addModifiedVanillaOverworldBiomes — vanilla covers ALL parameter
 *      points first, then we replace specific vanilla biomes with our sub-biomes.
 *   3. The low weight means this region's uniqueness band covers only ~7% of
 *      the world. Within that band, your replacements fire. Outside it, the
 *      main regions apply normally.
 *
 * Result: the sub-biomes appear as rare pockets of their host biome's territory
 * rather than as common landscape features.
 *
 * Because addModifiedVanillaOverworldBiomes starts from the complete vanilla
 * set, there are NO submerged biomes, NO missing ocean slots, NO tundra beaches.
 * Vanilla handles all of that; we only swap what we care about.
 *
 * ============================================================================
 * MIRROR MIRE — sub-biome found ONLY inside Blue Grove
 * ============================================================================
 *
 * Mirror Mire cannot use replaceBiome() because its host is an NT biome (Blue
 * Grove), not a vanilla biome. Instead it uses explicit addBiome() calls with
 * parameter points that are a strict geometric subset of Blue Grove's points:
 *
 *   Blue Grove:  T_BLUE_GROVE × H_BLUE_GROVE × C_INLAND/DEEP × ALL erosion tiers
 *   Mirror Mire: T_COLD       × H_FERTILE    × C_INLAND/DEEP × E_BASINS only
 *
 * T_COLD is fully contained within T_BLUE_GROVE.
 * H_FERTILE is fully contained within H_BLUE_GROVE.
 * E_BASINS is the single deepest-sunken erosion tier.
 *
 * In NTOverworldRegion (weight 10, ~93% of the world): Blue Grove also
 * registers E_BASINS points, so the basin floor is forested Blue Grove.
 * In THIS region (weight 3, ~7% of the world): Mirror Mire's explicit points
 * win over the vanilla base at those exact E_BASINS slots — the basin floor
 * becomes a frozen bog while surrounding terrain remains Blue Grove-type.
 *
 * ============================================================================
 * CALDERA GROTTO — sub-biome of Rainforest (scorching rainy volcanic basin)
 * ============================================================================
 *
 * Replacement target: JUNGLE (at scorching temperature)
 *
 * Vanilla places Jungle at: hot/scorching temperature, rainy humidity, inland
 * continentalness, standard-to-flat erosion. In NTOverworldRegion, Rainforest
 * already replaces Jungle in its main band. In THIS sub-biome band (weight 3),
 * Caldera Grotto takes the Jungle slot instead — so the rare volcanic-basin
 * variant of the rainforest territory appears as geothermal grottos carved into
 * the jungle floor.
 *
 * We also replace BAMBOO_JUNGLE → Caldera Grotto. Vanilla places Bamboo Jungle
 * at scorching+rainy+inland as a rare Jungle variant. Caldera Grotto taking
 * this slot feels natural — both are dense, humid, hidden-interior biomes.
 *
 * ============================================================================
 * FUNGAL REACH — sub-biome of Mushroom Fields + Jungle
 * ============================================================================
 *
 * Replacement targets: MUSHROOM_FIELDS, JUNGLE (at fertile humidity)
 *
 * Vanilla MUSHROOM_FIELDS: isolated C_MUSHROOM (far-ocean) continentalness.
 * Fungal Reach replacing it means the rare mushroom-island territory in this
 * band becomes a denser, more alien fungal biome — still isolated at sea, just
 * stranger. Thematically: Mushroom Fields are the tame version; Fungal Reach
 * is what happens when the mycelium goes deep.
 *
 * JUNGLE at fertile (not rainy) humidity: vanilla places a standard forest-edge
 * jungle variant here. Fungal Reach taking this slot plants giant mushroom
 * canopies at the humid jungle margin — an overgrown understory spilling out
 * of the rainforest into more temperate-adjacent territory.
 */
public class NTSubBiomeRegion extends Region {

    public NTSubBiomeRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {

            // ── CALDERA GROTTO ── volcanic basin, found only inside Rainforest territory ──
            // Jungle is vanilla's hot+rainy+inland biome — the same slot Rainforest
            // occupies in NTOverworldRegion. In this sub-biome band, Caldera Grotto
            // takes that slot instead, creating volcanic-basin grottos as a rare
            // interior variant of the rainforest zone.
            if (Config.ENABLE_CALDERA_GROTTO.get()) {
                builder.replaceBiome(Biomes.JUNGLE, NTBiomes.CALDERA_GROTTO);
                builder.replaceBiome(Biomes.BAMBOO_JUNGLE, NTBiomes.CALDERA_GROTTO);
            }

            // ── FUNGAL REACH ── deep mycelium biome, found in mushroom islands + jungle ──
            // MUSHROOM_FIELDS: vanilla's far-ocean isolated mycelium island.
            // SPARSE_JUNGLE: vanilla's warm+fertile jungle fringe.
            if (Config.ENABLE_FUNGAL_REACH.get()) {
                builder.replaceBiome(Biomes.MUSHROOM_FIELDS, NTBiomes.FUNGAL_REACH);
                builder.replaceBiome(Biomes.SPARSE_JUNGLE, NTBiomes.FUNGAL_REACH);
            }
        });

        // ── MIRROR MIRE — explicit parameter points (strict subset of Blue Grove) ──
        //
        // Mirror Mire must only spawn inside Blue Grove territory. replaceBiome()
        // cannot achieve this because it matches vanilla biomes, not NT biomes.
        // Instead, Mirror Mire uses explicit parameter points that are a geometric
        // subset of Blue Grove's parameter points in NTOverworldRegion:
        //
        //   Blue Grove:   T_BLUE_GROVE(-0.48→-0.05) × H_BLUE_GROVE(0.07→0.50) × C_INLAND/DEEP × ALL erosion
        //   Mirror Mire:  T_COLD(-0.48→-0.18)       × H_FERTILE(0.07→0.35)    × C_INLAND/DEEP × E_BASINS only
        //
        // T_COLD is fully inside T_BLUE_GROVE. H_FERTILE is fully inside H_BLUE_GROVE.
        // E_BASINS is the deepest-sunken erosion tier — the hollow floor of Blue Grove.
        //
        // In NTOverworldRegion (weight 10): Blue Grove wins E_BASINS → forest floor.
        // In THIS region (weight 3, ~7% of the world): Mirror Mire wins → frozen bog.
        // Everywhere outside E_BASINS: Blue Grove is unaffected in both regions.
        if (Config.ENABLE_MIRROR_MIRE.get()) {
            addBiome(mapper, NTClimateParameters.T_COLD, NTClimateParameters.H_FERTILE,
                    NTClimateParameters.C_INLAND,      NTClimateParameters.E_BASINS,
                    NTClimateParameters.W_NORMAL,  NTBiomes.MIRROR_MIRE);
            addBiome(mapper, NTClimateParameters.T_COLD, NTClimateParameters.H_FERTILE,
                    NTClimateParameters.C_INLAND,      NTClimateParameters.E_BASINS,
                    NTClimateParameters.W_VARIED,  NTBiomes.MIRROR_MIRE);
            addBiome(mapper, NTClimateParameters.T_COLD, NTClimateParameters.H_FERTILE,
                    NTClimateParameters.C_DEEP_INLAND, NTClimateParameters.E_BASINS,
                    NTClimateParameters.W_NORMAL,  NTBiomes.MIRROR_MIRE);
            addBiome(mapper, NTClimateParameters.T_COLD, NTClimateParameters.H_FERTILE,
                    NTClimateParameters.C_DEEP_INLAND, NTClimateParameters.E_BASINS,
                    NTClimateParameters.W_VARIED,  NTBiomes.MIRROR_MIRE);
        }
    }

    private void addBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper,
                          Climate.Parameter t, Climate.Parameter h, Climate.Parameter c,
                          Climate.Parameter e, Climate.Parameter w, ResourceKey<Biome> biome) {
        mapper.accept(Pair.of(Climate.parameters(t, h, c, e, Climate.Parameter.point(0.0F), w, 0.0F), biome));
    }
}