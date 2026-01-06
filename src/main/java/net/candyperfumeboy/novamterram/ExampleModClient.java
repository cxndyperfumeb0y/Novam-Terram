package net.candyperfumeboy.novamterram;

import net.candyperfumeboy.novamterram.entities.*;
import net.candyperfumeboy.novamterram.entities.variants.SilverFoxRenderer;
import net.candyperfumeboy.novamterram.entities.variants.SpectacledBearRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = NovamTerram.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ExampleModClient {

    // --- Model Layers ---
    public static final ModelLayerLocation BLUE_SPRUCE_BOAT_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "boat/blue_spruce"), "main");

    public static final ModelLayerLocation BLUE_SPRUCE_CHEST_BOAT_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "chest_boat/blue_spruce"), "main");

    // --- Renderer Registration ---
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Animals
        event.registerEntityRenderer(NTEntities.SILVER_FOX.get(), SilverFoxRenderer::new);
        event.registerEntityRenderer(NTEntities.SPECTACLED_BEAR.get(), SpectacledBearRenderer::new);

        // Boats - The boolean here tells the RENDERER if it's a chest boat
        event.registerEntityRenderer(NTBoatEntities.BLUE_SPRUCE_BOAT.get(),
                context -> new NTBoatRenderer(context, false, BLUE_SPRUCE_BOAT_LAYER, "blue_spruce"));

        event.registerEntityRenderer(NTBoatEntities.BLUE_SPRUCE_CHEST_BOAT.get(),
                context -> new NTBoatRenderer(context, true, BLUE_SPRUCE_CHEST_BOAT_LAYER, "blue_spruce"));

        // Block Entities
        event.registerBlockEntityRenderer(NTSignBlockEntities.SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(NTSignBlockEntities.HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    // --- Layer Definitions ---
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // BoatModel.createBodyModel() is for regular boats (no arguments)
        event.registerLayerDefinition(BLUE_SPRUCE_BOAT_LAYER, BoatModel::createBodyModel);

        // ChestBoatModel.createBodyModel() is the specific one for chest boats
        // It also takes NO arguments, but it's inside the ChestBoatModel class
        event.registerLayerDefinition(BLUE_SPRUCE_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel);
    }

    // --- Extra Layers ---
    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        LivingEntityRenderer<Ocelot, OcelotModel<Ocelot>> renderer = event.getRenderer(EntityType.OCELOT);
        if (renderer != null) {
            renderer.addLayer(new OcelotCollarLayer(renderer));
        }
    }
}