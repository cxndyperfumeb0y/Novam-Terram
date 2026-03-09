package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.candyperfumeboy.novamterram.entities.custom.FaunEntity;
import net.candyperfumeboy.novamterram.entities.custom.FaunModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FaunRenderer extends MobRenderer<FaunEntity, FaunModel<FaunEntity>> {

    // This points to where you saved your faun texture
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/faun.png");

    public FaunRenderer(EntityRendererProvider.Context context) {
        // 0.5f is a good shadow size for a deer-sized mob
        super(context, new FaunModel<>(context.bakeLayer(NTModelLayers.FAUN_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(FaunEntity entity) {
        return TEXTURE;
    }
}