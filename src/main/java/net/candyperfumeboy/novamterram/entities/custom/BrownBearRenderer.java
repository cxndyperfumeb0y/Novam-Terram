package net.candyperfumeboy.novamterram.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.entities.NTModelLayers; // Make sure this import is correct
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BrownBearRenderer extends MobRenderer<BrownBearEntity, NTBearModel<BrownBearEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/bear/brown_bear.png");

    public BrownBearRenderer(EntityRendererProvider.Context context) {
        // Now using your custom layer from NTModelLayers
        super(context, new NTBearModel<>(context.bakeLayer(NTModelLayers.BROWN_BEAR_LAYER)), 0.7F);
    }

    @Override
    protected void setupRotations(BrownBearEntity bear, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks, float scale) {
        super.setupRotations(bear, poseStack, ageInTicks, rotationYaw, partialTicks, scale);

        // 1. Standing Animation
        float standingAmount = bear.getStandingAnimationScale(partialTicks);
        if (standingAmount > 0.0F) {
            float f = Mth.lerp(standingAmount, 0.0F, -45.0F);
            poseStack.mulPose(Axis.XP.rotationDegrees(f));
        }

        // 2. Sleeping is posed in NTBearModel

        // 3. Diving Animation
        if (bear.isDiving()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(30.0F));
            poseStack.translate(0.0D, -0.1D, 0.2D);
        }
    }

    @Override
    protected void scale(BrownBearEntity bear, PoseStack poseStack, float partialTickTime) {
        if (bear.isBaby()) poseStack.scale(0.5F, 0.5F, 0.5F); // this model does not shrink babies by itself
        super.scale(bear, poseStack, partialTickTime);
    }

    @Override
    public ResourceLocation getTextureLocation(BrownBearEntity bear) {
        return TEXTURE;
    }
}