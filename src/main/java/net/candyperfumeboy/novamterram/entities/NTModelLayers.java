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

    // If you add a Fox or Bear model later, you'd add their layers here too!
}