package net.candyperfumeboy.novamterram;

import net.candyperfumeboy.novamterram.block.NTWoodSet;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.block.NTParticles;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.candyperfumeboy.novamterram.entities.custom.*;
import net.candyperfumeboy.novamterram.entities.enhancements.NTOcelotRenderer;
import net.candyperfumeboy.novamterram.entities.variants.NTBoatEntities;
import net.candyperfumeboy.novamterram.entities.variants.NTBoatRenderer;
import net.candyperfumeboy.novamterram.entities.variants.NTSignBlockEntities;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.List;

@EventBusSubscriber(modid = NovamTerram.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class NovamTerramClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (NTWoodSet wood : NTBlocks.WOOD_SETS) {
                Sheets.addWoodType(wood.woodType);
            }
        });
    }


    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NTEntities.SILVER_FOX.get(), SilverFoxRenderer::new);
        event.registerEntityRenderer(NTEntities.DESERT_FOX.get(), DesertFoxRenderer::new);
        event.registerEntityRenderer(NTEntities.SPECTACLED_BEAR.get(), SpectacledBearRenderer::new);
        event.registerEntityRenderer(NTEntities.BROWN_BEAR.get(), BrownBearRenderer::new);

        // This renderer now handles its own OcelotCollarLayer internally!
        event.registerEntityRenderer(EntityType.OCELOT, NTOcelotRenderer::new);
        event.registerEntityRenderer(NTEntities.HYENA.get(), HyenaRenderer::new);

        event.registerEntityRenderer(NTEntities.SONGBIRD.get(), SongbirdRenderer::new);
        event.registerEntityRenderer(NTEntities.FAUN.get(), FaunRenderer::new);
        // --- Boats ---
        for (NTWoodSet wood : NTBlocks.WOOD_SETS) {
            String name = wood.name;
            event.registerEntityRenderer(NTBoatEntities.getBoatEntity(name, false).get(),
                    context -> new NTBoatRenderer(context, false, getBoatLayer(name, false), name));
            event.registerEntityRenderer(NTBoatEntities.getBoatEntity(name, true).get(),
                    context -> new NTBoatRenderer(context, true, getBoatLayer(name, true), name));
        }

        event.registerBlockEntityRenderer(NTSignBlockEntities.SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(NTSignBlockEntities.HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(NTModelLayers.SILVER_FOX_LAYER, FoxModel::createBodyLayer);
        event.registerLayerDefinition(NTModelLayers.DESERT_FOX_LAYER, FoxModel::createBodyLayer);
        event.registerLayerDefinition(NTModelLayers.SONGBIRD_LAYER, SongbirdModel::createBodyLayer);
        event.registerLayerDefinition(NTModelLayers.FAUN_LAYER, FaunModel::createBodyLayer);
        event.registerLayerDefinition(NTModelLayers.BROWN_BEAR_LAYER, NTBearModel::createBodyLayer);
        event.registerLayerDefinition(NTModelLayers.SPECTACLED_BEAR_LAYER, NTBearModel::createBodyLayer);
        event.registerLayerDefinition(NTModelLayers.HYENA_LAYER, HyenaModel::createBodyLayer);

        // --- Boats ---
        for (NTWoodSet wood : NTBlocks.WOOD_SETS) {
            event.registerLayerDefinition(getBoatLayer(wood.name, false), BoatModel::createBodyModel);
            event.registerLayerDefinition(getBoatLayer(wood.name, true), ChestBoatModel::createBodyModel);
        }
    }

    // --- Helpers ---

    public static ModelLayerLocation getBoatLayer(String wood, boolean chest) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, (chest ? "chest_boat/" : "boat/") + wood), "main");
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        // Every falling leaf drifts the same way as vanilla cherry petals, only the picture differs
        for (var particle : List.of(NTParticles.BLUE_SPRUCE_LEAF, NTParticles.ORANGE_LEAF, NTParticles.YELLOW_LEAF,
                NTParticles.PAULOWNIA_LEAF, NTParticles.MYRTLE_LEAF, NTParticles.RED_MYRTLE_LEAF,
                NTParticles.PURPLE_MYRTLE_LEAF)) {
            event.registerSpriteSet(particle.get(), FallingLeafProvider::new);
        }
    }

    private static class FallingLeafProvider implements net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> {
        private final net.minecraft.client.particle.SpriteSet sprites;
        public FallingLeafProvider(net.minecraft.client.particle.SpriteSet sprites) { this.sprites = sprites; }
        @Override
        public net.minecraft.client.particle.Particle createParticle(net.minecraft.core.particles.SimpleParticleType type, net.minecraft.client.multiplayer.ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new FallingLeafParticle(level, x, y, z, this.sprites);
        }
    }

    private static class FallingLeafParticle extends net.minecraft.client.particle.CherryParticle {
        protected FallingLeafParticle(net.minecraft.client.multiplayer.ClientLevel level, double x, double y, double z, net.minecraft.client.particle.SpriteSet sprites) {
            super(level, x, y, z, sprites);
        }
    }
}
