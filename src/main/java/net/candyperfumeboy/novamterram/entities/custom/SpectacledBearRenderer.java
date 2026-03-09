package net.candyperfumeboy.novamterram.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SpectacledBearRenderer extends MobRenderer<SpectacledBearEntity, NTBearModel<SpectacledBearEntity>> {

    private static final ResourceLocation VARIANT_0 = ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/bear/spectacled_bear_0.png");
    private static final ResourceLocation VARIANT_1 = ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/bear/spectacled_bear_1.png");
    private static final ResourceLocation VARIANT_2 = ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/bear/spectacled_bear_2.png");
    private static final ResourceLocation VARIANT_3 = ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/bear/spectacled_bear_3.png");

    public SpectacledBearRenderer(EntityRendererProvider.Context context) {
        // We pass the baked model layer here
        super(context, new NTBearModel<>(context.bakeLayer(NTModelLayers.SPECTACLED_BEAR_LAYER)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(SpectacledBearEntity bear) {
        return switch (bear.getVariant()) {
            case 1 -> VARIANT_1;
            case 2 -> VARIANT_2;
            case 3 -> VARIANT_3;
            default -> VARIANT_0;
        };
    }

    @Override
    protected void setupRotations(SpectacledBearEntity bear, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks, float scale) {
        // We only perform the base rotation (Yaw/Turning).
        // By removing custom PoseStack rotations here, we prevent the "Double Rotation"
        // that was likely causing the limbs to appear detached.
        super.setupRotations(bear, poseStack, ageInTicks, rotationYaw, partialTicks, scale);
    }

    @Override
    protected void scale(SpectacledBearEntity bear, PoseStack poseStack, float partialTickTime) {
        if (bear.isBaby()) {
            poseStack.scale(0.45F, 0.45F, 0.45F);
        } else {
            poseStack.scale(0.82F, 0.82F, 0.82F);
        }
        super.scale(bear, poseStack, partialTickTime);
    }
}