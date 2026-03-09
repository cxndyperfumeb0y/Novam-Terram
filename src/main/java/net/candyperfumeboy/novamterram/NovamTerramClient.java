package net.candyperfumeboy.novamterram;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.block.NTParticles;
import net.candyperfumeboy.novamterram.block.custom.NTWoodTypes;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.candyperfumeboy.novamterram.entities.custom.*;
import net.candyperfumeboy.novamterram.entities.enhancements.NTOcelotRenderer;
import net.candyperfumeboy.novamterram.entities.variants.NTBoatEntities;
import net.candyperfumeboy.novamterram.entities.variants.NTBoatEntity;
import net.candyperfumeboy.novamterram.entities.variants.NTBoatRenderer;
import net.candyperfumeboy.novamterram.entities.variants.NTSignBlockEntities;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = NovamTerram.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class NovamTerramClient {

    private static final String[] WOODS = {"blue_spruce", "teak", "balsa", "mahogany", "aspen"};

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Sheets.addWoodType(NTWoodTypes.BLUE_SPRUCE);
            Sheets.addWoodType(NTWoodTypes.TEAK);
            Sheets.addWoodType(NTWoodTypes.BALSA);
            Sheets.addWoodType(NTWoodTypes.MAHOGANY);
            Sheets.addWoodType(NTWoodTypes.ASPEN);

            for (String wood : WOODS) {
                setLeafRender(wood + "_leaves");
            }
        });
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        Block aspenLeaves = BuiltInRegistries.BLOCK.get(
                ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "aspen_leaves")
        );

        if (aspenLeaves != Blocks.AIR) {
            event.register((state, world, pos, tintIndex) -> {
                if (world != null && pos != null) {
                    return BiomeColors.getAverageFoliageColor(world, pos);
                }
                return FoliageColor.getDefaultColor();
            }, aspenLeaves, NTBlocks.MYRTLE_LEAVES.get());
        }
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        Block aspenLeaves = BuiltInRegistries.BLOCK.get(
                ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "aspen_leaves")
        );

        if (aspenLeaves != Blocks.AIR) {
            event.register((stack, tintIndex) -> FoliageColor.getDefaultColor(),
                    aspenLeaves.asItem(),
                    NTBlocks.MYRTLE_LEAVES.get().asItem()
            );
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NTEntities.SILVER_FOX.get(), SilverFoxRenderer::new);
        event.registerEntityRenderer(NTEntities.SPECTACLED_BEAR.get(), SpectacledBearRenderer::new);
        event.registerEntityRenderer(NTEntities.BROWN_BEAR.get(), BrownBearRenderer::new);

        // This renderer now handles its own OcelotCollarLayer internally!
        event.registerEntityRenderer(EntityType.OCELOT, NTOcelotRenderer::new);

        event.registerEntityRenderer(NTEntities.SONGBIRD.get(), SongbirdRenderer::new);
        event.registerEntityRenderer(NTEntities.FAUN.get(), FaunRenderer::new);
        // --- Boats ---
        for (String wood : WOODS) {
            final String woodType = wood;
            event.registerEntityRenderer(getBoatEntity(woodType + "_boat"),
                    context -> new NTBoatRenderer(context, false, getBoatLayer(woodType, false), woodType));
            event.registerEntityRenderer(getBoatEntity(woodType + "_chest_boat"),
                    context -> new NTBoatRenderer(context, true, getBoatLayer(woodType, true), woodType));
        }

        event.registerBlockEntityRenderer(NTSignBlockEntities.SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(NTSignBlockEntities.HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // --- NEW: Songbird Layer Math ---
        event.registerLayerDefinition(NTModelLayers.SONGBIRD_LAYER, SongbirdModel::createBodyLayer);
        event.registerLayerDefinition(NTModelLayers.FAUN_LAYER, FaunModel::createBodyLayer);

        // --- Boats ---
        for (String wood : WOODS) {
            event.registerLayerDefinition(getBoatLayer(wood, false), BoatModel::createBodyModel);
            event.registerLayerDefinition(getBoatLayer(wood, true), ChestBoatModel::createBodyModel);
        }
    }

    // --- Helpers ---

    private static void setLeafRender(String leafName) {
        Block leafBlock = NTBlocks.BLOCKS.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(leafName))
                .map(DeferredHolder::get)
                .findFirst()
                .orElse(null);
        if (leafBlock != null) {
            ItemBlockRenderTypes.setRenderLayer(leafBlock, RenderType.cutoutMipped());
        }
    }

    public static ModelLayerLocation getBoatLayer(String wood, boolean chest) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, (chest ? "chest_boat/" : "boat/") + wood), "main");
    }

    private static EntityType<? extends NTBoatEntity> getBoatEntity(String name) {
        return (EntityType<? extends NTBoatEntity>) NTBoatEntities.ENTITY_TYPES.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(name))
                .findFirst()
                .map(DeferredHolder::get)
                .orElseThrow(() -> new RuntimeException("Could not find Boat Entity: " + name));
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(NTParticles.BLUE_SPRUCE_LEAF.get(), BlueLeafProvider::new);
    }

    private static class BlueLeafProvider implements net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> {
        private final net.minecraft.client.particle.SpriteSet sprites;
        public BlueLeafProvider(net.minecraft.client.particle.SpriteSet sprites) { this.sprites = sprites; }
        @Override
        public net.minecraft.client.particle.Particle createParticle(net.minecraft.core.particles.SimpleParticleType type, net.minecraft.client.multiplayer.ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new BlueLeafParticle(level, x, y, z, this.sprites);
        }
    }

    private static class BlueLeafParticle extends net.minecraft.client.particle.CherryParticle {
        protected BlueLeafParticle(net.minecraft.client.multiplayer.ClientLevel level, double x, double y, double z, net.minecraft.client.particle.SpriteSet sprites) {
            super(level, x, y, z, sprites);
        }
    }
}