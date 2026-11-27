package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Picks the coat texture and adds the flower crown. The model handles antlers. */
public class FaunRenderer extends MobRenderer<FaunEntity, FaunModel<FaunEntity>> {

    public FaunRenderer(EntityRendererProvider.Context context) {
        super(context, new FaunModel<>(context.bakeLayer(NTModelLayers.FAUN_LAYER)), 0.5f);
        this.addLayer(new FaunCrownLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(FaunEntity entity) {
        return entity.getCoat().texture(entity.isBaby());
    }
}
