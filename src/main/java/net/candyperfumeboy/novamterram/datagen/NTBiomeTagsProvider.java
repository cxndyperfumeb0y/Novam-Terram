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

public class NTBiomeTagsProvider extends BiomeTagsProvider {
    public NTBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NovamTerram.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // ========================================================================================
        // 1. MINECRAFT BIOME CATEGORIES
        // ========================================================================================

        // --- MASTER TAG ---
        this.tag(BiomeTags.IS_OVERWORLD)
                .addOptional(NTBiomes.BLUE_GROVE.location())
                .addOptional(NTBiomes.AEGEAN.location())
                .addOptional(NTBiomes.MAQUIS.location())
                .addOptional(NTBiomes.HEATH.location())
                .addOptional(NTBiomes.RAINFOREST.location())
                .addOptional(NTBiomes.CYPRESS_FEN.location())
                .addOptional(NTBiomes.MIRROR_MIRE.location())
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location())
                .addOptional(NTBiomes.ROYAL_FOREST.location())
                .addOptional(NTBiomes.MYRTLE_FIELDS.location())
                .addOptional(NTBiomes.ANDEAN_HILLS.location())
                .addOptional(NTBiomes.CALDERA_GROTTO.location())
                .addOptional(NTBiomes.SAFFRON_GROVE.location())
                .addOptional(NTBiomes.AMBER_SANDS.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location());

        // --- CLIMATE & TYPE ---
        this.tag(BiomeTags.IS_TAIGA)
                .addOptional(NTBiomes.BLUE_GROVE.location())
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location());

        this.tag(BiomeTags.IS_JUNGLE)
                .addOptional(NTBiomes.RAINFOREST.location());

        this.tag(BiomeTags.IS_FOREST)
                .addOptional(NTBiomes.ROYAL_FOREST.location())
                .addOptional(NTBiomes.MYRTLE_FIELDS.location()) // Open forest/meadow
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location())
                .addOptional(NTBiomes.SAFFRON_GROVE.location());

        this.tag(BiomeTags.IS_SAVANNA)
                .addOptional(NTBiomes.SAFFRON_GROVE.location());

        this.tag(BiomeTags.IS_BADLANDS)
                .addOptional(NTBiomes.CALDERA_GROTTO.location())

                .addOptional(NTBiomes.AMBER_SANDS.location());

        this.tag(BiomeTags.IS_BEACH)
                .addOptional(NTBiomes.AEGEAN.location())
                .addOptional(NTBiomes.CALDERA_GROTTO.location());

        this.tag(BiomeTags.IS_OCEAN)
                .addOptional(NTBiomes.AEGEAN.location());

        this.tag(BiomeTags.IS_DEEP_OCEAN)
                .addOptional(NTBiomes.AEGEAN.location());

        this.tag(BiomeTags.IS_HILL)
                .addOptional(NTBiomes.ROYAL_FOREST.location())
                .addOptional(NTBiomes.HEATH.location())
                .addOptional(NTBiomes.MAQUIS.location());

        this.tag(BiomeTags.IS_MOUNTAIN)
                .addOptional(NTBiomes.ANDEAN_HILLS.location());

        // ========================================================================================
        // 2. NEOFORGE BIOME CATEGORIES
        // ========================================================================================

        this.tag(Tags.Biomes.IS_PLAINS)
                .addOptional(NTBiomes.MYRTLE_FIELDS.location())
                .addOptional(NTBiomes.HEATH.location());

        this.tag(Tags.Biomes.IS_DESERT)
                .addOptional(NTBiomes.AMBER_SANDS.location());

        this.tag(Tags.Biomes.IS_SWAMP)
                .addOptional(NTBiomes.CYPRESS_FEN.location())
                .addOptional(NTBiomes.MIRROR_MIRE.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location());

        this.tag(Tags.Biomes.IS_WINDSWEPT)
                .addOptional(NTBiomes.ANDEAN_HILLS.location());

        this.tag(Tags.Biomes.IS_AQUATIC)
                .addOptional(NTBiomes.AEGEAN.location());

        // --- TEMPERATURE & HUMIDITY ---
        this.tag(Tags.Biomes.IS_COLD_OVERWORLD)
                .addOptional(NTBiomes.BLUE_GROVE.location())
                .addOptional(NTBiomes.MIRROR_MIRE.location())
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location());

        this.tag(Tags.Biomes.IS_HOT_OVERWORLD)
                .addOptional(NTBiomes.RAINFOREST.location())
                .addOptional(NTBiomes.MAQUIS.location())
                .addOptional(NTBiomes.CYPRESS_FEN.location())
                .addOptional(NTBiomes.AMBER_SANDS.location());

        this.tag(Tags.Biomes.IS_WET_OVERWORLD)
                .addOptional(NTBiomes.RAINFOREST.location())
                .addOptional(NTBiomes.CYPRESS_FEN.location())
                .addOptional(NTBiomes.MIRROR_MIRE.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location());

        this.tag(Tags.Biomes.IS_DRY_OVERWORLD)
                .addOptional(NTBiomes.MAQUIS.location())
                .addOptional(NTBiomes.AMBER_SANDS.location())
                .addOptional(NTBiomes.SAFFRON_GROVE.location());

        // --- VEGETATION TYPES ---
        this.tag(Tags.Biomes.IS_CONIFEROUS_TREE)
                .addOptional(NTBiomes.BLUE_GROVE.location())
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location())
                .addOptional(NTBiomes.CYPRESS_FEN.location());

        // ========================================================================================
        // 3. STRUCTURE TAGS
        // ========================================================================================

        // Global Overworld Structures
        this.tag(BiomeTags.HAS_PILLAGER_OUTPOST).addTag(BiomeTags.IS_OVERWORLD);
        this.tag(BiomeTags.HAS_MINESHAFT).addTag(BiomeTags.IS_OVERWORLD);
        this.tag(BiomeTags.HAS_RUINED_PORTAL_STANDARD).addTag(BiomeTags.IS_OVERWORLD);

        // Specific Structures
        this.tag(BiomeTags.HAS_SWAMP_HUT)
                .addOptional(NTBiomes.CYPRESS_FEN.location())
                .addOptional(NTBiomes.MIRROR_MIRE.location())
                .addOptional(NTBiomes.FUNGAL_REACH.location());

        this.tag(BiomeTags.HAS_VILLAGE_PLAINS)
                .addOptional(NTBiomes.MYRTLE_FIELDS.location())
                .addOptional(NTBiomes.HEATH.location());

        this.tag(BiomeTags.HAS_VILLAGE_TAIGA)
                .addOptional(NTBiomes.AUTUMNAL_TAIGA.location());

        this.tag(BiomeTags.HAS_VILLAGE_DESERT)
                .addOptional(NTBiomes.AMBER_SANDS.location());

        this.tag(BiomeTags.HAS_VILLAGE_SAVANNA)
                .addOptional(NTBiomes.ANDEAN_HILLS.location())

                .addOptional(NTBiomes.SAFFRON_GROVE.location());

        this.tag(BiomeTags.HAS_DESERT_PYRAMID)
                .addOptional(NTBiomes.AMBER_SANDS.location());

        this.tag(BiomeTags.HAS_IGLOO)
                .addOptional(NTBiomes.BLUE_GROVE.location());

        this.tag(BiomeTags.HAS_JUNGLE_TEMPLE)
                .addOptional(NTBiomes.RAINFOREST.location());
    }
}