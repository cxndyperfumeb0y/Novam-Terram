package net.candyperfumeboy.novamterram.entities;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class NTModelLayers {
    // This creates a unique ID for your Songbird's 3D shape
    public static final ModelLayerLocation SONGBIRD_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "songbird"), "main");

    public static final ModelLayerLocation FAUN_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "faun"), "main");

    public static final ModelLayerLocation BROWN_BEAR_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "brown_bear"), "main");

    public static final ModelLayerLocation SPECTACLED_BEAR_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "spectacled_bear"), "main");

    public static final ModelLayerLocation HYENA_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "hyena"), "main");

    public static final ModelLayerLocation SILVER_FOX_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "silver_fox"), "main");

    public static final ModelLayerLocation DESERT_FOX_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "desert_fox"), "main");

    // If you add a Fox or Bear model later, you'd add their layers here too!
}