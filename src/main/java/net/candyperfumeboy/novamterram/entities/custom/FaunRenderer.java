package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * FaunRenderer - Novam Terram 1.21.1
 * Simplified to use a single texture.
 * The Model class handles the visibility of antlers for Bucks/Does.
 */
public class FaunRenderer extends MobRenderer<FaunEntity, FaunModel<FaunEntity>> {

    // Points to your single faun texture
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/faun.png");

    public FaunRenderer(EntityRendererProvider.Context context) {
        // Shadow size 0.5f is standard for Deer/Wolves
        super(context, new FaunModel<>(context.bakeLayer(NTModelLayers.FAUN_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(FaunEntity entity) {
        // Returns the same texture for every faun (Buck, Doe, and Baby)
        return TEXTURE;
    }
}