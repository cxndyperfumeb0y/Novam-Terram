package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SongbirdRenderer extends MobRenderer<SongbirdEntity, SongbirdModel<SongbirdEntity>> {

    // This points to where you saved your texture in your resources folder
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/songbird.png");

    public SongbirdRenderer(EntityRendererProvider.Context context) {
        // 0.3f is the size of the shadow on the ground
        super(context, new SongbirdModel<>(context.bakeLayer(NTModelLayers.SONGBIRD_LAYER)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(SongbirdEntity entity) {
        return TEXTURE;
    }
}