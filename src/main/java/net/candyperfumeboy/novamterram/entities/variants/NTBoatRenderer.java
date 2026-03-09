package net.candyperfumeboy.novamterram.entities.variants;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

public class NTBoatRenderer extends BoatRenderer {
    private final ListModel<Boat> boatModel;

    public NTBoatRenderer(EntityRendererProvider.Context context, boolean isChestBoat, ModelLayerLocation layer, String woodType) {
        super(context, isChestBoat);
        ModelPart modelPart = context.bakeLayer(layer);
        this.boatModel = isChestBoat ? new ChestBoatModel(modelPart) : new BoatModel(modelPart);
    }

    @Override
    public void render(Boat entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.375D, 0.0D);
        poseStack.mulPose((new Quaternionf()).setAngleAxis((float)Math.toRadians(180.0D - entityYaw), 0.0F, 1.0F, 0.0F));

        float hurtTime = (float)entity.getHurtTime() - partialTicks;
        float damage = entity.getDamage() - partialTicks;
        if (damage < 0.0F) damage = 0.0F;

        if (hurtTime > 0.0F) {
            poseStack.mulPose((new Quaternionf()).setAngleAxis((float)Math.toRadians(Math.sin(hurtTime) * hurtTime * damage / 10.0F * (float)entity.getHurtDir()), 1.0F, 0.0F, 0.0F));
        }

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose((new Quaternionf()).setAngleAxis((float)Math.toRadians(90.0D), 0.0F, 1.0F, 0.0F));

        this.boatModel.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);

        VertexConsumer vertexconsumer = buffer.getBuffer(this.boatModel.renderType(this.getTextureLocation(entity)));
        this.boatModel.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);

        if (!entity.isUnderWater()) {
            VertexConsumer maskConsumer = buffer.getBuffer(RenderType.waterMask());
            this.boatModel.renderToBuffer(poseStack, maskConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Boat entity) {
        // We check if the entity is one of our custom ones and ask it for its specific texture
        if (entity instanceof NTBoatEntity ntBoat) {
            return ntBoat.getBoatTexture();
        } else if (entity instanceof NTChestBoatEntity ntChestBoat) {
            return ntChestBoat.getBoatTexture();
        }

        // Fallback for safety
        return ResourceLocation.withDefaultNamespace("textures/entity/boat/oak.png");
    }
}