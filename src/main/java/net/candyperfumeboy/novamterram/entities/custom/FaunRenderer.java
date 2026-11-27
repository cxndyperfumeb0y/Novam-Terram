package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * FaunRenderer - Novam Terram 1.21.1
 * Adults and fawns have their own textures.
 * The Model class handles antlers and the flower crown.
 */
public class FaunRenderer extends MobRenderer<FaunEntity, FaunModel<FaunEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/faun.png");
    private static final ResourceLocation BABY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/faun_baby.png");

    public FaunRenderer(EntityRendererProvider.Context context) {
        // Shadow size 0.5f is standard for Deer/Wolves
        super(context, new FaunModel<>(context.bakeLayer(NTModelLayers.FAUN_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(FaunEntity entity) {
        return entity.isBaby() ? BABY_TEXTURE : TEXTURE;
    }
}