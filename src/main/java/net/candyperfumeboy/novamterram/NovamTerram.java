package net.candyperfumeboy.novamterram;

import com.mojang.logging.LogUtils;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.block.NTParticles;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.entities.variants.NTBoatEntities;
import net.candyperfumeboy.novamterram.entities.variants.NTSignBlockEntities;
import net.candyperfumeboy.novamterram.item.NTCreativeModTabs;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.candyperfumeboy.novamterram.item.NTLootModifiers;
import net.candyperfumeboy.novamterram.worldgen.NTFeatures;
import net.candyperfumeboy.novamterram.worldgen.NTProcessors;
import net.candyperfumeboy.novamterram.worldgen.biome.NTBiomes;
import net.candyperfumeboy.novamterram.worldgen.biome.NTTerrablender;
import net.candyperfumeboy.novamterram.worldgen.biome.surface.NTSurfaceRules;
import net.candyperfumeboy.novamterram.worldgen.tree.NTTreePlacers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import terrablender.api.SurfaceRuleManager;

@Mod(NovamTerram.MOD_ID)
public class NovamTerram {
    public static final String MOD_ID = "novamterram";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NovamTerram(IEventBus modEventBus, ModContainer modContainer) {
        // Registry Systems
        NTBlocks.register(modEventBus);
        NTItems.register(modEventBus);
        NTEntities.register(modEventBus);
        NTBoatEntities.ENTITY_TYPES.register(modEventBus);
        NTSignBlockEntities.register(modEventBus);
        NTParticles.register(modEventBus);

        // Worldgen & Biomes
        NTBiomes.register(modEventBus); // Updated to use static helper
        NTFeatures.register(modEventBus);
        NTProcessors.PROCESSORS.register(modEventBus);
        NTTreePlacers.TRUNK_PLACERS.register(modEventBus);
        NTTreePlacers.FOLIAGE_PLACERS.register(modEventBus);

        // Systems
        NTCreativeModTabs.register(modEventBus);
        NTLootModifiers.register(modEventBus);

        // Lifecycle Events
        modEventBus.addListener(this::commonSetup);

        // Config
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 1. TerraBlender Setup
            NTTerrablender.registerRegions();
            SurfaceRuleManager.addSurfaceRules(
                    SurfaceRuleManager.RuleCategory.OVERWORLD,
                    MOD_ID,
                    NTSurfaceRules.makeRules()
            );

            // 2. Wood Logic Registration (Axe Stripping & Flammability)
            NTBlocks.registerWoodLogic();

            LOGGER.info("Novam Terram common setup complete: Wood logic and Biomes initialized.");
        });
    }
}