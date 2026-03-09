package net.candyperfumeboy.novamterram.worldgen.biome;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NTBiomes {

        public static final DeferredRegister<Biome> BIOMES =
                DeferredRegister.create(Registries.BIOME, NovamTerram.MOD_ID);

        // =========================================================================
        // EXISTING BIOMES
        // =========================================================================
        public static final ResourceKey<Biome> BLUE_GROVE = registerKey("blue_grove");
        public static final ResourceKey<Biome> AEGEAN = registerKey("aegean");
        public static final ResourceKey<Biome> MAQUIS = registerKey("maquis");
        public static final ResourceKey<Biome> HEATH = registerKey("heath");
        public static final ResourceKey<Biome> ANDEAN_HILLS = registerKey("andean_hills");
        public static final ResourceKey<Biome> RAINFOREST = registerKey("rainforest");
        public static final ResourceKey<Biome> CYPRESS_FEN = registerKey("cypress_fen");
        public static final ResourceKey<Biome> MIRROR_MIRE = registerKey("mirror_mire");
        public static final ResourceKey<Biome> AUTUMNAL_TAIGA = registerKey("autumnal_taiga");
        public static final ResourceKey<Biome> ROYAL_FOREST = registerKey("royal_forest");
        public static final ResourceKey<Biome> MYRTLE_FIELDS = registerKey("myrtle_fields");
        public static final ResourceKey<Biome> SAFFRON_GROVE = registerKey("saffron_grove");
        public static final ResourceKey<Biome> AMBER_SANDS = registerKey("amber_sands");
        public static final ResourceKey<Biome> FUNGAL_REACH = registerKey("fungal_reach");
        public static final ResourceKey<Biome> CALDERA_GROTTO = registerKey("caldera_grotto");
        public static final ResourceKey<Biome> SNOWBLOSSOM_GROVE = registerKey("snowblossom_grove");
        public static final ResourceKey<Biome> SILVER_BIRCH_VELD = registerKey("silver_birch_veld");
public static final ResourceKey<Biome> BLUEBELL_WOODS = registerKey("bluebell_woods");
        public static final ResourceKey<Biome> BLACK_FOREST = registerKey("black_forest");
        public static final ResourceKey<Biome> SILVER_BIRCH_WETLAND = registerKey("silver_birch_wetland");
        public static final ResourceKey<Biome> COASTAL_CHAPARRAL = registerKey("coastal_chaparral");
        public static final ResourceKey<Biome> MANGROVE_LAGOON = registerKey("mangrove_lagoon");
        public static final ResourceKey<Biome> LARCH_FOREST = registerKey("larch_forest");
        public static final ResourceKey<Biome> OZARK_FOREST = registerKey("ozark_forest");
        public static final ResourceKey<Biome> CLOUD_FOREST = registerKey("cloud_forest");
        public static final ResourceKey<Biome> CEDAR_SLOPES = registerKey("cedar_slopes");
        public static final ResourceKey<Biome> GALLERY_FOREST = registerKey("gallery_forest");

        // Helper method to create keys
        private static ResourceKey<Biome> registerKey(String name) {
                return ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, name));
        }

        // Register method to be called in your Main Mod Class constructor
        public static void register(IEventBus eventBus) {
                BIOMES.register(eventBus);
        }
}