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

public class BrownBearRenderer extends MobRenderer<BrownBearEntity, NTBearModel<BrownBearEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/bear/brown_bear.png");

    public BrownBearRenderer(EntityRendererProvider.Context context) {
        // Keeping it simple: No extra layers, 0.7F shadow radius
        super(context, new NTBearModel<>(context.bakeLayer(ModelLayers.POLAR_BEAR)), 0.7F);
    }

    @Override
    protected void setupRotations(BrownBearEntity bear, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks, float scale) {
        super.setupRotations(bear, poseStack, ageInTicks, rotationYaw, partialTicks, scale);

        // 1. Standing Animation (Standard Vanilla logic)
        float standingAmount = bear.getStandingAnimationScale(partialTicks);
        if (standingAmount > 0.0F) {
            float f = Mth.lerp(standingAmount, 0.0F, -45.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(f));
        }

        // 2. Climbing Animation (Custom)
        // Uses the getClimbRotation logic from the Entity class
        float climbRot = bear.getClimbRotation(partialTicks);
        if (climbRot > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-climbRot));
        }

        // 3. Sleeping Animation (Custom)
        if (bear.isSleepyTime()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            // Adjusted translation to prevent clipping through the floor
            poseStack.translate(0.0D, -0.2D, 0.5D);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(BrownBearEntity bear) {
        return TEXTURE;
    }
}