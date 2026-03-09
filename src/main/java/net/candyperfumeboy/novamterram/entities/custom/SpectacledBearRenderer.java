package net.candyperfumeboy.novamterram.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.client.model.PolarBearModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SpectacledBearRenderer extends MobRenderer<SpectacledBearEntity, NTBearModel<SpectacledBearEntity>> {

    // Define the 4 texture variants
    private static final ResourceLocation VARIANT_0 = ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/bear/spectacled_bear_0.png");
    private static final ResourceLocation VARIANT_1 = ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/bear/spectacled_bear_1.png");
    private static final ResourceLocation VARIANT_2 = ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/bear/spectacled_bear_2.png");
    private static final ResourceLocation VARIANT_3 = ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/bear/spectacled_bear_3.png");

    public SpectacledBearRenderer(EntityRendererProvider.Context context) {
        super(context, new NTBearModel<>(context.bakeLayer(ModelLayers.POLAR_BEAR)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(SpectacledBearEntity bear) {
        // Return texture based on the variant saved in the entity
        return switch (bear.getVariant()) {
            case 1 -> VARIANT_1;
            case 2 -> VARIANT_2;
            case 3 -> VARIANT_3;
            default -> VARIANT_0;
        };
    }

    @Override
    protected void setupRotations(SpectacledBearEntity bear, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks, float scale) {
        super.setupRotations(bear, poseStack, ageInTicks, rotationYaw, partialTicks, scale);

        // 1. Standing Animation
        float standingAmount = bear.getStandingAnimationScale(partialTicks);
        if (standingAmount > 0.0F) {
            float f = Mth.lerp(standingAmount, 0.0F, -45.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(f));
        }

        // 2. Music/Dancing Animation (Wobble)
        if (bear.isDancing()) {
            float wobble = Mth.sin(ageInTicks * 0.3F) * 8.0F;
            poseStack.mulPose(Axis.ZP.rotationDegrees(wobble));
        }

        // 3. Climbing Animation
        float climbRot = bear.getClimbRotation(partialTicks);
        if (climbRot > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-climbRot));
        }

        // 4. Sleeping Animation
        if (bear.isSleepyTime()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.translate(0.0D, -0.2D, 0.5D);
        }
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