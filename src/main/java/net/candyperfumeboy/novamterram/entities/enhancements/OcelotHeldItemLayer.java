package net.candyperfumeboy.novamterram.entities.enhancements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.candyperfumeboy.novamterram.util.IOcelotMixin;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Draws the item an ocelot is carrying in its mouth. */
public class OcelotHeldItemLayer extends RenderLayer<Ocelot, OcelotModel<Ocelot>> {
    private final ItemInHandRenderer itemRenderer;

    public OcelotHeldItemLayer(RenderLayerParent<Ocelot, OcelotModel<Ocelot>> renderer, ItemInHandRenderer itemRenderer) {
        super(renderer);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Ocelot ocelot,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        ItemStack stack = ((IOcelotMixin) ocelot).novamterram$getHeldItem();
        if (stack.isEmpty() || ocelot.isInvisible()
                || !(this.getParentModel() instanceof NTOcelotModel model)) return;

        poseStack.pushPose();
        if (ocelot.isBaby()) {
            // Same adjustment the model applies to a baby's head
            poseStack.scale(0.75F, 0.75F, 0.75F);
            poseStack.translate(0.0F, 10.0F / 16.0F, 4.0F / 16.0F);
        }
        model.headPart().translateAndRotate(poseStack);
        poseStack.translate(0.0F, 2.1F / 16.0F, -6.0F / 16.0F); // just under the nose
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));     // lay it flat
        this.itemRenderer.renderItem(ocelot, stack, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
