package net.candyperfumeboy.novamterram.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Draws the food a hyena has stolen, held in its mouth. */
public class HyenaHeldItemLayer extends RenderLayer<HyenaEntity, HyenaModel<HyenaEntity>> {
    private final ItemInHandRenderer itemRenderer;

    public HyenaHeldItemLayer(RenderLayerParent<HyenaEntity, HyenaModel<HyenaEntity>> renderer,
                              ItemInHandRenderer itemRenderer) {
        super(renderer);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, HyenaEntity hyena,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        ItemStack stack = hyena.getMainHandItem();
        if (stack.isEmpty() || hyena.isInvisible()) return;

        poseStack.pushPose();
        this.getParentModel().translateToHead(poseStack);
        poseStack.translate(0.0F, 3.2F / 16.0F, -6.0F / 16.0F); // just under the snout
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));     // lay it flat
        this.itemRenderer.renderItem(hyena, stack, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
