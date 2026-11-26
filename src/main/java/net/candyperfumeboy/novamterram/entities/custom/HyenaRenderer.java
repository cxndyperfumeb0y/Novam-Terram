package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HyenaRenderer extends MobRenderer<HyenaEntity, HyenaModel<HyenaEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/hyena.png");

    public HyenaRenderer(EntityRendererProvider.Context context) {
        super(context, new HyenaModel<>(context.bakeLayer(NTModelLayers.HYENA_LAYER)), 0.55f);
        this.addLayer(new HyenaCollarLayer(this));
        this.addLayer(new HyenaHeldItemLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(HyenaEntity entity) {
        return TEXTURE;
    }
}
