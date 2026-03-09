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

        // 2. Define Resource Keys
        public static final ResourceKey<Biome> BLUE_GROVE = registerKey("blue_grove");
        public static final ResourceKey<Biome> AEGEAN = registerKey("aegean");
        public static final ResourceKey<Biome> MAQUIS = registerKey("maquis");
        public static final ResourceKey<Biome> HEATH = registerKey("heath");
        public static final ResourceKey<Biome> ANDEAN_HILLS = registerKey("andean_hills");
        public static final ResourceKey<Biome> RAINFOREST = registerKey("rainforest");
        public static final ResourceKey<Biome> CYPRESS_FEN = registerKey("cypress_fen");
        public static final ResourceKey<Biome> MIRROR_MIRE = registerKey("mirror_mire");
        public static final ResourceKey<Biome> RED_PEAKS = registerKey("red_peaks");
        public static final ResourceKey<Biome> AUTUMNAL_TAIGA = registerKey("autumnal_taiga");
        public static final ResourceKey<Biome> ROYAL_FOREST = registerKey("royal_forest");
        public static final ResourceKey<Biome> MYRTLE_FIELDS = registerKey("myrtle_fields");
        public static final ResourceKey<Biome> SAFFRON_GROVE = registerKey("saffron_grove");
        public static final ResourceKey<Biome> AMBER_SANDS = registerKey("amber_sands");
        public static final ResourceKey<Biome> FUNGAL_REACH = registerKey("fungal_reach");
        public static final ResourceKey<Biome> CALDERA_GROTTO = registerKey("caldera_grotto");

        // Helper method to create keys
        private static ResourceKey<Biome> registerKey(String name) {
            return ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, name));
        }

        // 3. Register method to be called in your Main Mod Class constructor
        public static void register(IEventBus eventBus) {
            BIOMES.register(eventBus);
        }
    }