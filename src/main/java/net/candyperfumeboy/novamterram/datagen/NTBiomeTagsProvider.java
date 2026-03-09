package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.worldgen.biome.NTBiomes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

/**
 * Biome tag datagen for Novam Terram.
 *
 * Organisation (top to bottom):
 *   1. IS_OVERWORLD master tag + HAS_STRONGHOLD
 *   2. Vanilla BiomeTags  — type/climate/terrain
 *   3. NeoForge Tags.Biomes — temperature, humidity, vegetation, terrain flavour
 *   4. Structure tags        — vanilla structures that should spawn in NT biomes
 *
 * Temperatures used in NTBiomeFactory (for reference when tagging):
 *   BLUE_GROVE       0.25   cold
 *   AEGEAN           0.8    upper boundary of temperate / not yet hot
 *   MAQUIS           0.85   hot
 *   HEATH            0.6    temperate
 *   ANDEAN_HILLS     0.25   cold
 *   RAINFOREST       0.95   hot
 *   CYPRESS_FEN      0.75   temperate (high)
 *   MIRROR_MIRE      0.3    cold
 *   AUTUMNAL_TAIGA   0.4    cold
 *   MYRTLE_FIELDS    0.7    temperate
 *   ROYAL_FOREST     0.7    temperate
 *   SAFFRON_GROVE    0.9    hot
 *   AMBER_SANDS      1.5    hot (extreme)
 *   FUNGAL_REACH     0.3    cold (dark, shaded)
 *   CALDERA_GROTTO   0.95   hot
 *
 * IS_HOT  = temp > 0.8   | IS_TEMPERATE = 0.5–0.8 incl. | IS_COLD = temp < 0.5
 */
public class NTBiomeTagsProvider extends BiomeTagsProvider {

    public NTBiomeTagsProvider(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> lookupProvider,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NovamTerram.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        // ============================================================
        // 1. MASTER OVERWORLD TAG
        //    Every NT biome must be here — compasses, maps, and many
        //    vanilla systems gate on BiomeTags.IS_OVERWORLD.
        //    We also add HAS_STRONGHOLD so strongholds are not lost
        //    in worlds that generate mostly NT biomes.
        // ============================================================

        this.tag(BiomeTags.IS_OVERWORLD)
                .addOptional(NTBiomes.BLUE_GROVE.location())
                .addOptional(NTBiomes.AEGEAN.location())
                .addOptional(NTBiomes.MAQUIS.location())
                .addOptional(NTBiomes.HEATH.location())
                .addOptional(NTBiomes.ANDEAN_HILLS.location())
                .addOptional(NTBiomes.RAINFOREST.location())
                .addOptional(NTBiomes.CYPRESS_FEN.location())
                .addOptional(NTBiomes.MIRROR_MIRE.location())
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location())
                .addOptional(NTBiomes.MYRTLE_FIELDS.location())
                .addOptional(NTBiomes.ROYAL_FOREST.location())
                .addOptional(NTBiomes.SAFFRON_GROVE.location())
                .addOptional(NTBiomes.AMBER_SANDS.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location())
                .addOptional(NTBiomes.CALDERA_GROTTO.location());

        // Required when adding to BiomeTags.IS_OVERWORLD — prevents strongholds
        // from under-generating in worlds heavy with NT biomes.
        this.tag(BiomeTags.HAS_STRONGHOLD)
                .addTag(BiomeTags.IS_OVERWORLD);


        // ============================================================
        // 2. VANILLA BIOMETAGS — BIOME TYPE / TERRAIN CLASS
        //    These drive vanilla behaviour: raid-map icons, mob caps,
        //    structure eligibility, and inter-mod compat.
        //    Be conservative — only tag where the fit is genuine.
        // ============================================================

        // --- Taiga-class (coniferous, cold forest) ---
        this.tag(BiomeTags.IS_TAIGA)
                .addOptional(NTBiomes.BLUE_GROVE.location())      // cold spruce-like grove
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location()); // classic cool forest

        // --- Jungle-class ---
        this.tag(BiomeTags.IS_JUNGLE)
                .addOptional(NTBiomes.RAINFOREST.location());     // hot, dense, tropical

        // --- Forest-class (deciduous / mixed woodland) ---
        this.tag(BiomeTags.IS_FOREST)
                .addOptional(NTBiomes.ROYAL_FOREST.location())    // dense temperate woodland
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location())  // forested, mixed
                .addOptional(NTBiomes.SAFFRON_GROVE.location());  // open warm grove

        // --- Savanna-class ---
        // Saffron Grove is warm and open with sparse trees — savanna is the
        // closest vanilla archetype. Myrtle Fields is meadow-like, not savanna.
        this.tag(BiomeTags.IS_SAVANNA)
                .addOptional(NTBiomes.SAFFRON_GROVE.location());

        // --- Hills / Extreme Hills ---
        this.tag(BiomeTags.IS_HILL)
                .addOptional(NTBiomes.ROYAL_FOREST.location())    // rolling forest terrain
                .addOptional(NTBiomes.HEATH.location())           // open rolling upland
                .addOptional(NTBiomes.MAQUIS.location());         // scrubby Mediterranean hills

        // --- Mountain-class ---
        this.tag(BiomeTags.IS_MOUNTAIN)
                .addOptional(NTBiomes.ANDEAN_HILLS.location());   // high-altitude alpine biome

        // --- Badlands-class ---
        // AMBER_SANDS only — layered sedimentary sands fit the badlands archetype.
        // CALDERA_GROTTO removed: it is a volcanic jungle grotto, not badlands.
        this.tag(BiomeTags.IS_BADLANDS)
                .addOptional(NTBiomes.AMBER_SANDS.location());

        // --- Beach-class ---
        // AEGEAN is a coastal land biome, not a beach biome in the vanilla sense
        // (beaches are narrow strips between ocean and land). Removing both.
        // If you add a dedicated NT beach biome later, tag it here.

        // --- Ocean-class ---
        // AEGEAN is a coastal/island LAND biome, not an ocean biome.
        // Tagging it IS_OCEAN / IS_DEEP_OCEAN causes ocean monument and
        // guardian/elder-guardian spawner logic to fire incorrectly.
        // Removed. Tag a future dedicated ocean biome here instead.

        // --- Swamp-class ---
        // All three wetland biomes qualify structurally as swamps.
        // Note: this grants swamp hut eligibility — see structure section below.
        this.tag(BiomeTags.SPAWNS_WARM_VARIANT_FROGS)
                .addOptional(NTBiomes.CYPRESS_FEN.location())
                .addOptional(NTBiomes.MIRROR_MIRE.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location());
        this.tag(Tags.Biomes.IS_SWAMP)
                .addOptional(NTBiomes.CYPRESS_FEN.location())
                .addOptional(NTBiomes.MIRROR_MIRE.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location());

        // ============================================================
        // 3. NEOFORGE TAGS.BIOMES — TEMPERATURE, HUMIDITY, VEGETATION
        //    These are the convention tags used by other mods (Farmer's
        //    Delight, Oh The Biomes We've Gone, etc.) for crop growth,
        //    animal spawning overrides, and biome-sensitive tools.
        //    Follow the temperature thresholds: hot >0.8, cold <0.5.
        // ============================================================

        // --- TEMPERATURE ---

        // Hot (temp > 0.8): MAQUIS 0.85, RAINFOREST 0.95, SAFFRON_GROVE 0.9,
        //                   AMBER_SANDS 1.5, CALDERA_GROTTO 0.95
        this.tag(Tags.Biomes.IS_HOT_OVERWORLD)
                .addOptional(NTBiomes.MAQUIS.location())
                .addOptional(NTBiomes.RAINFOREST.location())
                .addOptional(NTBiomes.SAFFRON_GROVE.location())
                .addOptional(NTBiomes.AMBER_SANDS.location())
                .addOptional(NTBiomes.CALDERA_GROTTO.location());

        // Temperate (0.5–0.8 incl.): AEGEAN 0.8, HEATH 0.6, CYPRESS_FEN 0.75,
        //                            MYRTLE_FIELDS 0.7, ROYAL_FOREST 0.7
        this.tag(Tags.Biomes.IS_TEMPERATE_OVERWORLD)
                .addOptional(NTBiomes.AEGEAN.location())
                .addOptional(NTBiomes.HEATH.location())
                .addOptional(NTBiomes.CYPRESS_FEN.location())
                .addOptional(NTBiomes.MYRTLE_FIELDS.location())
                .addOptional(NTBiomes.ROYAL_FOREST.location());

        // Cold (temp < 0.5): BLUE_GROVE 0.25, ANDEAN_HILLS 0.25,
        //                    MIRROR_MIRE 0.3, AUTUMNAL_TAIGA 0.4, FUNGAL_REACH 0.3
        this.tag(Tags.Biomes.IS_COLD_OVERWORLD)
                .addOptional(NTBiomes.BLUE_GROVE.location())
                .addOptional(NTBiomes.ANDEAN_HILLS.location())
                .addOptional(NTBiomes.MIRROR_MIRE.location())
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location());

        // --- HUMIDITY ---

        // Wet (high downfall, frequent water/mud features)
        this.tag(Tags.Biomes.IS_WET_OVERWORLD)
                .addOptional(NTBiomes.RAINFOREST.location())
                .addOptional(NTBiomes.CYPRESS_FEN.location())
                .addOptional(NTBiomes.MIRROR_MIRE.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location())
                .addOptional(NTBiomes.CALDERA_GROTTO.location()); // geothermal moisture

        // Dry (low downfall, arid or mediterranean)
        // SAFFRON_GROVE removed — its downfall(0.8f) makes it very wet despite
        // the warm name. MAQUIS and AMBER_SANDS are genuinely dry.
        this.tag(Tags.Biomes.IS_DRY_OVERWORLD)
                .addOptional(NTBiomes.MAQUIS.location())
                .addOptional(NTBiomes.AMBER_SANDS.location());

        // Sandy (sand-dominant surface — amber sand counts)
        this.tag(Tags.Biomes.IS_SANDY)
                .addOptional(NTBiomes.AMBER_SANDS.location())
                .addOptional(NTBiomes.AEGEAN.location()); // calcite/sand coastal surface

        // --- VEGETATION DENSITY ---

        // Dense vegetation (more than a standard forest)
        this.tag(Tags.Biomes.IS_DENSE_VEGETATION_OVERWORLD)
                .addOptional(NTBiomes.RAINFOREST.location())
                .addOptional(NTBiomes.ROYAL_FOREST.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location())
                .addOptional(NTBiomes.CALDERA_GROTTO.location());

        // Sparse vegetation (trees present but infrequent)
        this.tag(Tags.Biomes.IS_SPARSE_VEGETATION_OVERWORLD)
                .addOptional(NTBiomes.HEATH.location())
                .addOptional(NTBiomes.MAQUIS.location())
                .addOptional(NTBiomes.ANDEAN_HILLS.location())
                .addOptional(NTBiomes.SAFFRON_GROVE.location())
                .addOptional(NTBiomes.AEGEAN.location());

        // --- TREE TYPE ---

        // Coniferous (spruce/pine-dominant)
        this.tag(Tags.Biomes.IS_CONIFEROUS_TREE)
                .addOptional(NTBiomes.BLUE_GROVE.location())
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location())
                .addOptional(NTBiomes.CYPRESS_FEN.location());

        // Deciduous (oak/birch/broad-leaf dominant)
        this.tag(Tags.Biomes.IS_DECIDUOUS_TREE)
                .addOptional(NTBiomes.ROYAL_FOREST.location())
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location()) // mixed
                .addOptional(NTBiomes.MYRTLE_FIELDS.location());

        // Jungle trees (large, tropical)
        this.tag(Tags.Biomes.IS_JUNGLE_TREE)
                .addOptional(NTBiomes.RAINFOREST.location())
                .addOptional(NTBiomes.CALDERA_GROTTO.location());

        // Savanna trees (acacia-like, sparse)
        this.tag(Tags.Biomes.IS_SAVANNA_TREE)
                .addOptional(NTBiomes.SAFFRON_GROVE.location());

        // --- TERRAIN FLAVOUR ---

        // Plains-like (open, flat-ish, grassy)
        this.tag(Tags.Biomes.IS_PLAINS)
                .addOptional(NTBiomes.MYRTLE_FIELDS.location())
                .addOptional(NTBiomes.HEATH.location());

        // Desert-like (arid, sandy, no passive mob spawns by default)
        this.tag(Tags.Biomes.IS_DESERT)
                .addOptional(NTBiomes.AMBER_SANDS.location());

        // Swamp-like (waterlogged, dark, muddy)
        this.tag(Tags.Biomes.IS_SWAMP)
                .addOptional(NTBiomes.CYPRESS_FEN.location())
                .addOptional(NTBiomes.MIRROR_MIRE.location());
        // FUNGAL_REACH removed from IS_SWAMP — it is a dense mushroom forest,
        // not a swamp. Swamp tag brings undesired witch/hut logic.

        // Windswept (exposed, rocky, high altitude)
        this.tag(Tags.Biomes.IS_WINDSWEPT)
                .addOptional(NTBiomes.ANDEAN_HILLS.location())
                .addOptional(NTBiomes.HEATH.location());          // exposed upland moor

        // Aquatic (water-primary biome)
        this.tag(Tags.Biomes.IS_AQUATIC)
                .addOptional(NTBiomes.AEGEAN.location());         // coastal, water-adjacent

        // Mountainous terrain
        this.tag(Tags.Biomes.IS_MOUNTAIN)
                .addOptional(NTBiomes.ANDEAN_HILLS.location());

        this.tag(Tags.Biomes.IS_MOUNTAIN_SLOPE)
                .addOptional(NTBiomes.ANDEAN_HILLS.location());

        // Lush (vibrant, wet, pool-heavy — think lush caves above ground)
        this.tag(Tags.Biomes.IS_LUSH)
                .addOptional(NTBiomes.RAINFOREST.location())
                .addOptional(NTBiomes.CALDERA_GROTTO.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location());

        // Floral (high flower density)
        this.tag(Tags.Biomes.IS_FLORAL)
                .addOptional(NTBiomes.MYRTLE_FIELDS.location())
                .addOptional(NTBiomes.HEATH.location())           // heather/wildflowers
                .addOptional(NTBiomes.SAFFRON_GROVE.location());  // saffron flowers

        // Magical (otherworldly theme)
        this.tag(Tags.Biomes.IS_MAGICAL)
                .addOptional(NTBiomes.BLUE_GROVE.location())      // ethereal blue-lit grove
                .addOptional(NTBiomes.FUNGAL_REACH.location());   // giant mushroom forest

        // Mushroom biomes
        this.tag(Tags.Biomes.IS_MUSHROOM)
                .addOptional(NTBiomes.FUNGAL_REACH.location());

        // Rare biomes (hard to find, unusual)
        this.tag(Tags.Biomes.IS_RARE)
                .addOptional(NTBiomes.CALDERA_GROTTO.location())
                .addOptional(NTBiomes.BLUE_GROVE.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location());

        // Spooky (dark, unsettling atmosphere)
        this.tag(Tags.Biomes.IS_SPOOKY)
                .addOptional(NTBiomes.MIRROR_MIRE.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location());

        // Snowy (prominent natural snow)
        this.tag(Tags.Biomes.IS_SNOWY)
                .addOptional(NTBiomes.BLUE_GROVE.location())
                .addOptional(NTBiomes.ANDEAN_HILLS.location());   // alpine snowpack


        // ============================================================
        // 4. STRUCTURE TAGS
        //    Controls which vanilla structures generate in NT biomes.
        //    Structures are ordered by: global → biome-type → specific.
        // ============================================================

        // --- GLOBAL OVERWORLD STRUCTURES ---
        // Every overworld biome should be eligible for these.
        this.tag(BiomeTags.HAS_MINESHAFT)
                .addTag(BiomeTags.IS_OVERWORLD);

        this.tag(BiomeTags.HAS_RUINED_PORTAL_STANDARD)
                .addTag(BiomeTags.IS_OVERWORLD);

        // Pillager outposts — sensible in open, non-waterlogged biomes.
        // Excluded: Aegean (coastal water), Mirror Mire, Cypress Fen, Fungal Reach
        // (all too waterlogged/swampy for a tower structure to feel right).
        this.tag(BiomeTags.HAS_PILLAGER_OUTPOST)
                .addOptional(NTBiomes.BLUE_GROVE.location())
                .addOptional(NTBiomes.MAQUIS.location())
                .addOptional(NTBiomes.HEATH.location())
                .addOptional(NTBiomes.ANDEAN_HILLS.location())
                .addOptional(NTBiomes.RAINFOREST.location())
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location())
                .addOptional(NTBiomes.MYRTLE_FIELDS.location())
                .addOptional(NTBiomes.ROYAL_FOREST.location())
                .addOptional(NTBiomes.SAFFRON_GROVE.location())
                .addOptional(NTBiomes.AMBER_SANDS.location())
                .addOptional(NTBiomes.CALDERA_GROTTO.location());

        // --- VILLAGE TYPES ---
        // Match village style to biome aesthetic.

        // Plains village (thatched, pastoral)
        this.tag(BiomeTags.HAS_VILLAGE_PLAINS)
                .addOptional(NTBiomes.MYRTLE_FIELDS.location())
                .addOptional(NTBiomes.HEATH.location());

        // Taiga village (log cabins)
        this.tag(BiomeTags.HAS_VILLAGE_TAIGA)
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location())
                .addOptional(NTBiomes.BLUE_GROVE.location());     // cold forest = taiga aesthetic

        // Desert village (sandstone)
        this.tag(BiomeTags.HAS_VILLAGE_DESERT)
                .addOptional(NTBiomes.AMBER_SANDS.location());

        // Savanna village (acacia wood, open layout)
        this.tag(BiomeTags.HAS_VILLAGE_SAVANNA)
                .addOptional(NTBiomes.SAFFRON_GROVE.location())
                .addOptional(NTBiomes.ANDEAN_HILLS.location());   // open highland

        // Snowy village — none of our biomes are fully snow-covered enough to warrant this.

        // --- BIOME-SPECIFIC STRUCTURES ---

        // Swamp hut — only genuine wetland biomes.
        // FUNGAL_REACH excluded: it is a mushroom forest, not a swamp.
        this.tag(BiomeTags.HAS_SWAMP_HUT)
                .addOptional(NTBiomes.CYPRESS_FEN.location())
                .addOptional(NTBiomes.MIRROR_MIRE.location());

        // Desert pyramid
        this.tag(BiomeTags.HAS_DESERT_PYRAMID)
                .addOptional(NTBiomes.AMBER_SANDS.location());

        // Jungle temple
        this.tag(BiomeTags.HAS_JUNGLE_TEMPLE)
                .addOptional(NTBiomes.RAINFOREST.location());

        // Igloo (cold forested areas)
        this.tag(BiomeTags.HAS_IGLOO)
                .addOptional(NTBiomes.BLUE_GROVE.location())
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location());

        // Trail ruins (overgrown, archaeological — suits old, lush biomes)
        this.tag(BiomeTags.HAS_TRAIL_RUINS)
                .addOptional(NTBiomes.ROYAL_FOREST.location())
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location())
                .addOptional(NTBiomes.RAINFOREST.location())
                .addOptional(NTBiomes.CALDERA_GROTTO.location());

        // Ancient city — only in deep, dark, spooky underground.
        // None of our biomes are underground so we leave this unset.

        // Buried treasure
        this.tag(BiomeTags.HAS_BURIED_TREASURE)
                .addOptional(NTBiomes.AEGEAN.location()); // coastal = shipwreck treasure territory
    }
}