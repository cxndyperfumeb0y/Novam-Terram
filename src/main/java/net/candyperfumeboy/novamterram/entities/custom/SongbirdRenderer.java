package net.candyperfumeboy.novamterram.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Parrot; // Ensure this is imported

public class SongbirdRenderer extends MobRenderer<SongbirdEntity, SongbirdModel<SongbirdEntity>> {

    // Renamed variables to avoid clashing with Parrot.Variant names
    private static final ResourceLocation BLUE_TEXTURE = ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/songbird_blue.png");

    public SongbirdRenderer(EntityRendererProvider.Context context) {
        super(context, new SongbirdModel<>(context.bakeLayer(NTModelLayers.SONGBIRD_LAYER)), 0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(SongbirdEntity entity) {
        // By using 'Parrot.Variant.BLUE', we tell Java exactly what we want
        return switch (entity.getVariant()) {
            default -> BLUE_TEXTURE;
        };
    }

    @Override
    protected void scale(SongbirdEntity entity, PoseStack poseStack, float partialTickTime) {
        if (entity.isBaby()) {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }
        super.scale(entity, poseStack, partialTickTime);
    }
}