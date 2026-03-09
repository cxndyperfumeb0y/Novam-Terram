package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HyenaRenderer extends MobRenderer<HyenaEntity, HyenaModel<HyenaEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/hyena.png");

    public HyenaRenderer(EntityRendererProvider.Context context) {
        // Reduced shadow from 0.7f to 0.55f to match the 0.85x model scale
        super(context, new HyenaModel<>(context.bakeLayer(NTModelLayers.HYENA_LAYER)), 0.55f);
    }

    @Override
    public ResourceLocation getTextureLocation(HyenaEntity entity) {
        return TEXTURE;
    }
}