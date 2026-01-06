package net.candyperfumeboy.novamterram;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.block.NTWoodTypes;
import net.candyperfumeboy.novamterram.entities.NTBoatEntities;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.entities.NTSignBlockEntities;
import net.candyperfumeboy.novamterram.entities.OcelotCollarLayer;
import net.candyperfumeboy.novamterram.item.NTCreativeModTabs;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.candyperfumeboy.novamterram.item.NTLootModifiers;
import net.candyperfumeboy.novamterram.worldgen.NTProcessors;
import net.candyperfumeboy.novamterram.worldgen.biome.NTBiomes;
import net.candyperfumeboy.novamterram.worldgen.biome.NTOverworldRegion;
import net.candyperfumeboy.novamterram.worldgen.biome.NTTerrablender;
import net.candyperfumeboy.novamterram.worldgen.biome.surface.NTSurfaceRules;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(NovamTerram.MOD_ID)
public class NovamTerram {
    public static final String MOD_ID = "novamterram";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NovamTerram(IEventBus modEventBus, ModContainer modContainer) {
        // Registering Mod Lifecycle
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::onAddLayers);

        // Registering Systems
        NTProcessors.PROCESSORS.register(modEventBus);
        NTBoatEntities.ENTITY_TYPES.register(modEventBus);
        NTCreativeModTabs.register(modEventBus);
        NTEntities.register(modEventBus);
        NTItems.register(modEventBus);
        NTBlocks.register(modEventBus);
        NTLootModifiers.register(modEventBus);
        NTBiomes.BIOMES.register(modEventBus);
        NTSignBlockEntities.register(modEventBus);
        WoodType.register(NTWoodTypes.BLUE_SPRUCE);

        // Global Event Bus
        NeoForge.EVENT_BUS.register(this);

        // Config
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // TerraBlender Setup
            NTTerrablender.registerRegions();
            SurfaceRuleManager.addSurfaceRules(
                    SurfaceRuleManager.RuleCategory.OVERWORLD,
                    MOD_ID,
                    NTSurfaceRules.makeRules()
            );

            // Wood & Sign Setup
            Sheets.addWoodType(NTWoodTypes.BLUE_SPRUCE);
        });
    }

    private void onAddLayers(EntityRenderersEvent.AddLayers event) {
        var renderer = event.getRenderer(EntityType.OCELOT);
        if (renderer instanceof LivingEntityRenderer living) {
            living.addLayer(new OcelotCollarLayer(living));
            LOGGER.info("NOVAM TERRAM: Ocelot Collar Layer Registered");
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(NTItems.OPAL);
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(NTBlocks.OPAL_BLOCK);
            event.accept(NTBlocks.OPAL_ORE);
            event.accept(NTBlocks.BLUE_SPRUCE_LOG);
            event.accept(NTBlocks.BLUE_SPRUCE_PLANKS);
            event.accept(NTBlocks.BLUE_SPRUCE_SIGN); // Added to tab for testing
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}