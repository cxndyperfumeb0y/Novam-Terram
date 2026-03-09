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

import static net.candyperfumeboy.novamterram.worldgen.biome.NTClimateParameters.*;

/**
 * NTOverworldRegion — Core inland NT biomes. Weight: 10.
 *
 * Covers: Blue Grove, Autumnal Taiga, Royal Forest, Myrtle Fields, Heath,
 *         Rainforest, Saffron Grove, Cypress Fen, Amber Sands.
 *
 * Handled by dedicated region classes:
 *   Aegean, Maquis       → NTCoastalRegion
 *   Andean Hills         → NTPeakRegion
 *   Mirror Mire,
 *   Caldera Grotto,
 *   Fungal Reach         → NTSubBiomeRegion
 *
 * ============================================================================
 * MIXED STRATEGY: replaceBiome() + explicit addBiome() points
 * ============================================================================
 *
 * addModifiedVanillaOverworldBiomes() runs vanilla's complete OverworldBiomeBuilder
 * first — every ocean, beach, river, inland, and peak slot is covered before we
 * touch anything. This eliminates all submerged-biome and missing-ocean bugs.
 *
 * On top of that foundation we use two tools:
 *
 *   replaceBiome(VANILLA, NT) — swaps a vanilla biome globally within this
 *   region's band. Used when an NT biome occupies the same climate niche as
 *   a specific vanilla biome and should replace it everywhere it appears.
 *
 *   explicit addBiome() calls — used when an NT biome needs a specific climate
 *   combination that either (a) doesn't map cleanly to one vanilla biome, or
 *   (b) would conflict with another NT biome's replaceBiome() call if handled
 *   that way. These calls add precise parameter points that win over the vanilla
 *   base via nearest-neighbour priority.
 *
 * ============================================================================
 * REPLACEMENT TARGETS — rationale
 * ============================================================================
 *
 * BLUE_GROVE → OLD_GROWTH_SPRUCE_TAIGA
 *   Both occupy T_COLD × H_FERTILE × C_INLAND. Spruce taiga is the vanilla
 *   biome at exactly this cold+damp+inland niche. GROVE (deep-inland peak
 *   approach) is left untouched — it stays as the mountain shoulder.
 *
 * AUTUMNAL_TAIGA → OLD_GROWTH_PINE_TAIGA
 *   Both T_COLD × H_ARID. Dry-edge cold forest — pine taiga is the vanilla
 *   equivalent. The autumnal character replaces the pine aesthetic.
 *
 * ROYAL_FOREST → BIRCH_FOREST + OLD_GROWTH_BIRCH_FOREST
 *   Both T_TEMPERATE × H_FERTILE × elevated inland terrain. Birch forest is
 *   vanilla's fertile temperate highland forest. Royal Forest is its NT equivalent
 *   — elevated, magical, dense canopy.
 *
 * MYRTLE_FIELDS → SUNFLOWER_PLAINS
 *   Both T_TEMPERATE × H_ARID × E_FLATS. Flat, open, arid-edge temperate.
 *   Myrtle Fields inherits every sunflower plains slot — rolling meadow terrain.
 *
 * HEATH → WINDSWEPT_FOREST + WINDSWEPT_GRAVELLY_HILLS
 *   Both T_TEMPERATE × H_WASTELAND. Open, wind-exposed, low vegetation. Heath
 *   is the NT equivalent of windswept terrain at temperate latitude.
 *
 * RAINFOREST → JUNGLE + BAMBOO_JUNGLE
 *   Both T_SCORCHING × H_RAINY × C_INLAND. Rainforest takes the hot+wet+inland
 *   jungle niche entirely. Bamboo Jungle is also replaced since in this main
 *   band (weight 10, ~93% of the world) Rainforest should dominate the hot
 *   rainy inland zone. In NTSubBiomeRegion (weight 3) Caldera Grotto takes
 *   these same slots in the rare 7% band — no conflict since the regions are
 *   independent uniqueness slices.
 *
 * AMBER_SANDS → BADLANDS + WOODED_BADLANDS + ERODED_BADLANDS
 *   Both T_SCORCHING × H_WASTELAND. Badlands is vanilla's hot arid highland.
 *   Amber Sands is the NT version — golden eroded desert canyon terrain.
 *
 * SAFFRON_GROVE — explicit addBiome() calls
 *   T_SCORCHING × H_FERTILE. Vanilla puts SPARSE_JUNGLE here, but Sparse Jungle
 *   is also the Fungal Reach target in NTSubBiomeRegion. Rather than double-
 *   booking the same vanilla biome across two regions with different intents,
 *   Saffron Grove uses explicit parameter points so its placement is precise
 *   and unambiguous.
 *
 * CYPRESS_FEN — explicit addBiome() calls
 *   T_SCORCHING × H_RAINY × E_BASINS/E_CANYONS. This is the sunken hollow
 *   and ravine variant of Rainforest territory. Since Rainforest already
 *   replaces JUNGLE (which covers standard and highland erosion), Cypress Fen
 *   needs explicit E_BASINS/E_CANYONS points that sit inside the Jungle
 *   parameter space but at higher erosion values. Explicit calls win over the
 *   replaceBiome() base at these specific erosion points.
 */
public class NTOverworldRegion extends Region {

    public NTOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {

            // ── BLUE GROVE — handled below as explicit parameter points ──────────────
            // Blue Grove is a large magical cold taiga with lakes. It needs a wide
            // parameter footprint so Mirror Mire can spawn as a basin sub-biome inside
            // it. Uses T_BLUE_GROVE (cold-to-cool) and H_BLUE_GROVE (fertile-to-rainy).
            // Mirror Mire uses T_COLD × H_FERTILE × E_BASINS — a strict subset.
            // See explicit addBiome() calls after this builder block.

            // ── AUTUMNAL TAIGA ───────────────────────────────────────────────────────
            if (Config.ENABLE_AUTUMNAL_TAIGA.get()) {
                builder.replaceBiome(Biomes.OLD_GROWTH_PINE_TAIGA, NTBiomes.AUTUMNAL_TAIGA);
            }

            // ── ROYAL FOREST — handled below as explicit parameter points ─────────────
            // Royal Forest is a rare grotto/valley biome found only at high elevation.
            // replaceBiome() would place it everywhere Birch Forest appears, making it
            // too common and stripping the sense of discovery. See explicit addBiome()
            // calls after this builder block.

            // ── MYRTLE FIELDS ────────────────────────────────────────────────────────
            if (Config.ENABLE_MYRTLE_FIELDS.get()) {
                builder.replaceBiome(Biomes.SUNFLOWER_PLAINS, NTBiomes.MYRTLE_FIELDS);
            }

            // ── HEATH ────────────────────────────────────────────────────────────────
            if (Config.ENABLE_HEATH.get()) {
                builder.replaceBiome(Biomes.WINDSWEPT_FOREST, NTBiomes.HEATH);
                builder.replaceBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS, NTBiomes.HEATH);
            }

            // ── RAINFOREST ───────────────────────────────────────────────────────────
            if (Config.ENABLE_RAINFOREST.get()) {
                builder.replaceBiome(Biomes.JUNGLE, NTBiomes.RAINFOREST);
                builder.replaceBiome(Biomes.BAMBOO_JUNGLE, NTBiomes.RAINFOREST);
            }

            // ── AMBER SANDS ──────────────────────────────────────────────────────────
            if (Config.ENABLE_AMBER_SANDS.get()) {
                builder.replaceBiome(Biomes.BADLANDS, NTBiomes.AMBER_SANDS);
                builder.replaceBiome(Biomes.WOODED_BADLANDS, NTBiomes.AMBER_SANDS);
                builder.replaceBiome(Biomes.ERODED_BADLANDS, NTBiomes.AMBER_SANDS);
            }

            // ── BLUEBELL WOODS ───────────────────────────────────────────────────────
            // Replaces vanilla Birch Forest variants at T_TEMPERATE × H_FERTILE.
            // Uses birch/oak trees with a carpet of blue flowers. Both birch variants
            // are replaced so the biome has the same footprint as vanilla birch.
            if (Config.ENABLE_BLUEBELL_WOODS.get()) {
                builder.replaceBiome(Biomes.BIRCH_FOREST, NTBiomes.BLUEBELL_WOODS);
                builder.replaceBiome(Biomes.OLD_GROWTH_BIRCH_FOREST, NTBiomes.BLUEBELL_WOODS);
            }

            // ── BLACK FOREST ─────────────────────────────────────────────────────────
            // Replaces vanilla Dark Forest at T_TEMPERATE × H_FERTILE/RAINY.
            // Inherits dark_forest_vegetation density but with a much darker aesthetic.
            if (Config.ENABLE_BLACK_FOREST.get()) {
                builder.replaceBiome(Biomes.DARK_FOREST, NTBiomes.BLACK_FOREST);
            }
        });

        // ── BLUE GROVE — explicit parameter points ────────────────────────────────
        // Large magical cold taiga. T_BLUE_GROVE spans full T_COLD into the cold
        // edge of T_TEMPERATE — appears in both icy taiga and cool misty forest zones.
        // H_BLUE_GROVE spans H_FERTILE into H_RAINY — damp moss to lakeside mist.
        // ALL erosion tiers are covered so Blue Grove has a large continuous footprint.
        // Mirror Mire claims E_BASINS as a subset — those sunken hollows become
        // frozen bogs while surrounding terrain stays Blue Grove.
        if (Config.ENABLE_BLUE_GROVE.get()) {
            // Standard forest floor
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_INLAND,      E_STANDARD,  W_EXPANSIVE, NTBiomes.BLUE_GROVE);
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_INLAND,      E_STANDARD,  W_NORMAL,    NTBiomes.BLUE_GROVE);
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_DEEP_INLAND, E_STANDARD,  W_NORMAL,    NTBiomes.BLUE_GROVE);
            // Highland ridges and slopes
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_INLAND,      E_HIGHLANDS, W_NORMAL,    NTBiomes.BLUE_GROVE);
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_INLAND,      E_HIGHLANDS, W_VARIED,    NTBiomes.BLUE_GROVE);
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_DEEP_INLAND, E_HIGHLANDS, W_NORMAL,    NTBiomes.BLUE_GROVE);
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_DEEP_INLAND, E_HIGHLANDS, W_VARIED,    NTBiomes.BLUE_GROVE);
            // Rugged jagged terrain (mountain-adjacent forest)
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_INLAND,      E_JAGGED,    W_NORMAL,    NTBiomes.BLUE_GROVE);
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_DEEP_INLAND, E_JAGGED,    W_NORMAL,    NTBiomes.BLUE_GROVE);
            // Flat lowlands and flats (where lakes form — Mirror Mire's E_BASINS
            // sits just beyond E_FLATS in the erosion axis, so these are the
            // lake-dotted flats just above the frost-hollow floor)
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_INLAND,      E_FLATS,     W_NORMAL,    NTBiomes.BLUE_GROVE);
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_DEEP_INLAND, E_FLATS,     W_NORMAL,    NTBiomes.BLUE_GROVE);
            // Deep sunken basins — NOTE: Mirror Mire claims these in NTSubBiomeRegion.
            // Registering Blue Grove here too means in the main band (weight 10,
            // ~93% of the world) basins are Blue Grove. In the sub-biome band
            // (weight 3, ~7%) Mirror Mire wins these slots instead.
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_INLAND,      E_BASINS,    W_NORMAL,    NTBiomes.BLUE_GROVE);
            addBiome(mapper, T_BLUE_GROVE, H_BLUE_GROVE, C_DEEP_INLAND, E_BASINS,    W_NORMAL,    NTBiomes.BLUE_GROVE);
        }

        // ── ROYAL FOREST — explicit parameter points ──────────────────────────────
        // Rare grotto/valley biome found only at elevated deep-inland terrain.
        // T_TEMPERATE × H_FERTILE × C_DEEP_INLAND is the key — far from coast,
        // high base elevation. E_JAGGED targets mountain-carved valley floors and
        // rugged highland terrain; E_HIGHLANDS targets elevated open plateau valleys.
        // C_INLAND is intentionally excluded — that's the common accessible forest
        // zone. Royal Forest should require genuine inland mountain travel to find.
        if (Config.ENABLE_ROYAL_FOREST.get()) {
            addBiome(mapper, T_TEMPERATE, H_FERTILE, C_DEEP_INLAND, E_JAGGED,    W_NORMAL,   NTBiomes.ROYAL_FOREST);
            addBiome(mapper, T_TEMPERATE, H_FERTILE, C_DEEP_INLAND, E_JAGGED,    W_VARIED,   NTBiomes.ROYAL_FOREST);
            addBiome(mapper, T_TEMPERATE, H_FERTILE, C_DEEP_INLAND, E_HIGHLANDS, W_VARIED,   NTBiomes.ROYAL_FOREST);
            addBiome(mapper, T_TEMPERATE, H_FERTILE, C_DEEP_INLAND, E_HIGHLANDS, W_NORMAL,   NTBiomes.ROYAL_FOREST);
        }

        // ── SAFFRON GROVE — explicit parameter points ─────────────────────────────
        // T_SCORCHING × H_FERTILE × inland. Vanilla's SPARSE_JUNGLE is the nearest
        // vanilla biome here, but it's also the Fungal Reach target in NTSubBiomeRegion.
        // Explicit points avoid ambiguity and place Saffron Grove precisely at the
        // bright, humid, fertile hot-forest terrain it needs.
        if (Config.ENABLE_SAFFRON_GROVE.get()) {
            addBiome(mapper, T_SCORCHING, H_FERTILE, C_INLAND,      E_STANDARD,  W_NORMAL,   NTBiomes.SAFFRON_GROVE);
            addBiome(mapper, T_SCORCHING, H_FERTILE, C_INLAND,      E_HIGHLANDS, W_VARIED,   NTBiomes.SAFFRON_GROVE);
            addBiome(mapper, T_SCORCHING, H_FERTILE, C_DEEP_INLAND, E_STANDARD,  W_NORMAL,   NTBiomes.SAFFRON_GROVE);
            addBiome(mapper, T_SCORCHING, H_FERTILE, C_DEEP_INLAND, E_HIGHLANDS, W_NORMAL,   NTBiomes.SAFFRON_GROVE);
        }

        // ── CYPRESS FEN — explicit parameter points ───────────────────────────────
        // T_SCORCHING × H_RAINY × high-erosion (basins + canyons). These slots sit
        // inside the Rainforest/Jungle parameter space but at erosion values that
        // produce sunken, waterlogged, low-lying terrain — exactly what a cypress fen
        // looks like. Explicit addBiome calls at E_BASINS/E_CANYONS override the
        // Rainforest replaceBiome() for these specific erosion combinations.
        if (Config.ENABLE_CYPRESS_FEN.get()) {
            addBiome(mapper, T_SCORCHING, H_RAINY, C_INLAND,      E_BASINS,  W_NORMAL,       NTBiomes.CYPRESS_FEN);
            addBiome(mapper, T_SCORCHING, H_RAINY, C_INLAND,      E_BASINS,  W_VARIED,       NTBiomes.CYPRESS_FEN);
            addBiome(mapper, T_SCORCHING, H_RAINY, C_INLAND,      E_CANYONS, W_NORMAL,       NTBiomes.CYPRESS_FEN);
            addBiome(mapper, T_SCORCHING, H_RAINY, C_NEAR_INLAND, E_BASINS,  W_NORMAL,       NTBiomes.CYPRESS_FEN);
        }

        // ── LARCH FOREST ─────────────────────────────────────────────────────────
        // Cold taiga variant. T_COLD × H_AVERAGE keeps it drier than Blue Grove
        // (H_FERTILE+). Occupies rolling cold upland terrain at STANDARD and HIGHLANDS
        // erosion — distinct from jagged Cedar Slopes and wet Blue Grove basins.
        if (Config.ENABLE_LARCH_FOREST.get()) {
            addBiome(mapper, T_COLD, H_AVERAGE, C_INLAND,      E_STANDARD,  W_NORMAL,  NTBiomes.LARCH_FOREST);
            addBiome(mapper, T_COLD, H_AVERAGE, C_INLAND,      E_STANDARD,  W_VARIED,  NTBiomes.LARCH_FOREST);
            addBiome(mapper, T_COLD, H_AVERAGE, C_DEEP_INLAND, E_STANDARD,  W_NORMAL,  NTBiomes.LARCH_FOREST);
            addBiome(mapper, T_COLD, H_AVERAGE, C_INLAND,      E_HIGHLANDS, W_NORMAL,  NTBiomes.LARCH_FOREST);
            addBiome(mapper, T_COLD, H_AVERAGE, C_DEEP_INLAND, E_HIGHLANDS, W_NORMAL,  NTBiomes.LARCH_FOREST);
        }

        // ── SILVER BIRCH WETLAND ──────────────────────────────────────────────────
        // Birch wetland with waterlilies. T_TEMPERATE × H_RAINY × E_BASINS targets
        // low-lying waterlogged temperate hollows — warmer than Blue Grove basins,
        // dominated by birch rather than the dark conifers of Mirror Mire.
        if (Config.ENABLE_SILVER_BIRCH_WETLAND.get()) {
            addBiome(mapper, T_TEMPERATE, H_RAINY, C_NEAR_INLAND, E_BASINS, W_NORMAL,  NTBiomes.SILVER_BIRCH_WETLAND);
            addBiome(mapper, T_TEMPERATE, H_RAINY, C_NEAR_INLAND, E_BASINS, W_VARIED,  NTBiomes.SILVER_BIRCH_WETLAND);
            addBiome(mapper, T_TEMPERATE, H_RAINY, C_INLAND,      E_BASINS, W_NORMAL,  NTBiomes.SILVER_BIRCH_WETLAND);
        }

        // ── OZARK FOREST ─────────────────────────────────────────────────────────
        // Rocky warm forest with boulders. T_WARM × H_FERTILE × E_HIGHLANDS gives
        // it the hilly inland terrain it needs. HEATH already takes WINDSWEPT_FOREST
        // via replaceBiome at T_TEMPERATE — Ozark Forest sits warmer and avoids conflict.
        if (Config.ENABLE_OZARK_FOREST.get()) {
            addBiome(mapper, T_WARM, H_FERTILE, C_INLAND,      E_HIGHLANDS, W_NORMAL,  NTBiomes.OZARK_FOREST);
            addBiome(mapper, T_WARM, H_FERTILE, C_INLAND,      E_HIGHLANDS, W_VARIED,  NTBiomes.OZARK_FOREST);
            addBiome(mapper, T_WARM, H_FERTILE, C_DEEP_INLAND, E_HIGHLANDS, W_NORMAL,  NTBiomes.OZARK_FOREST);
        }

        // ── CLOUD FOREST ─────────────────────────────────────────────────────────
        // Misty elevated jungle. T_WARM (cooler than Rainforest's T_SCORCHING) ×
        // H_RAINY × E_HIGHLANDS targets mist-prone elevated slopes. White ash particle
        // effect registered in NTBiomeFactory produces the cloud/fog visual.
        if (Config.ENABLE_CLOUD_FOREST.get()) {
            addBiome(mapper, T_WARM, H_RAINY, C_INLAND,      E_HIGHLANDS, W_NORMAL,  NTBiomes.CLOUD_FOREST);
            addBiome(mapper, T_WARM, H_RAINY, C_INLAND,      E_HIGHLANDS, W_VARIED,  NTBiomes.CLOUD_FOREST);
            addBiome(mapper, T_WARM, H_RAINY, C_DEEP_INLAND, E_HIGHLANDS, W_NORMAL,  NTBiomes.CLOUD_FOREST);
            addBiome(mapper, T_WARM, H_RAINY, C_INLAND,      E_STANDARD,  W_NORMAL,  NTBiomes.CLOUD_FOREST);
        }

        // ── GALLERY FOREST ────────────────────────────────────────────────────────
        // River-corridor jungle with sugar cane. T_WARM × H_RAINY × C_NEAR_INLAND ×
        // E_BASINS targets low river-valley floors near the coastal transition — where
        // gallery forests grow in real geography alongside watercourses.
        // Distinct from Cloud Forest (elevated) and Cypress Fen (T_SCORCHING).
        if (Config.ENABLE_GALLERY_FOREST.get()) {
            addBiome(mapper, T_WARM, H_RAINY, C_NEAR_INLAND, E_BASINS, W_NORMAL,  NTBiomes.GALLERY_FOREST);
            addBiome(mapper, T_WARM, H_RAINY, C_NEAR_INLAND, E_BASINS, W_VARIED,  NTBiomes.GALLERY_FOREST);
            addBiome(mapper, T_WARM, H_RAINY, C_INLAND,      E_BASINS, W_NORMAL,  NTBiomes.GALLERY_FOREST);
        }

        // ── COASTAL CHAPARRAL ─────────────────────────────────────────────────────
        // Hot dry scrubland. T_SCORCHING × H_WASTELAND is the harshest heat+drought
        // combination. C_NEAR_INLAND keeps it at the sun-baked inland edge of the
        // coastal zone. Distinct from Amber Sands (badlands geology) and Maquis
        // (T_WARM savanna scrub in NTCoastalRegion).
        if (Config.ENABLE_COASTAL_CHAPARRAL.get()) {
            addBiome(mapper, T_SCORCHING, H_WASTELAND, C_NEAR_INLAND, E_STANDARD, W_NORMAL,  NTBiomes.COASTAL_CHAPARRAL);
            addBiome(mapper, T_SCORCHING, H_WASTELAND, C_NEAR_INLAND, E_FLATS,    W_NORMAL,  NTBiomes.COASTAL_CHAPARRAL);
            addBiome(mapper, T_SCORCHING, H_WASTELAND, C_INLAND,      E_STANDARD, W_NORMAL,  NTBiomes.COASTAL_CHAPARRAL);
        }
    }

    private void addBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper,
                          Climate.Parameter t, Climate.Parameter h, Climate.Parameter c,
                          Climate.Parameter e, Climate.Parameter w, ResourceKey<Biome> biome) {
        mapper.accept(Pair.of(Climate.parameters(t, h, c, e, Climate.Parameter.point(0.0F), w, 0.0F), biome));
    }
}