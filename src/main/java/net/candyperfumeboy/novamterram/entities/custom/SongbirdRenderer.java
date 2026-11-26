package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SongbirdRenderer extends MobRenderer<SongbirdEntity, SongbirdModel<SongbirdEntity>> {

    private static final ResourceLocation BLUE_TEXTURE = ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/songbird_blue.png");

    public SongbirdRenderer(EntityRendererProvider.Context context) {
        super(context, new SongbirdModel<>(context.bakeLayer(NTModelLayers.SONGBIRD_LAYER)), 0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(SongbirdEntity entity) {
        // One texture for now. Baby size is handled by SongbirdModel.
        return BLUE_TEXTURE;
    }
}
