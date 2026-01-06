package net.candyperfumeboy.novamterram.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.candyperfumeboy.novamterram.NovamTerram;
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
import net.minecraft.world.entity.vehicle.ChestBoat;
import org.joml.Quaternionf;

import java.util.Map;

public class NTBoatRenderer extends BoatRenderer {
    private final ListModel<Boat> boatModel;
    private final ResourceLocation boatTexture;
    private final ResourceLocation chestBoatTexture;

    public NTBoatRenderer(EntityRendererProvider.Context context, boolean isChestBoat, ModelLayerLocation layer, String woodName) {
        super(context, isChestBoat);

        // Bake the model part from the layer we registered in ExampleModClient
        ModelPart modelPart = context.bakeLayer(layer);

        // Wrap the part in the correct model class
        this.boatModel = isChestBoat ? new ChestBoatModel(modelPart) : new BoatModel(modelPart);

        this.boatTexture = ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/boat/blue_spruce.png");
        this.chestBoatTexture = ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/chest_boat/blue_spruce.png");
    }

    @Override
    public void render(Boat entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Align boat height with water surface
        poseStack.translate(0.0D, 0.375D, 0.0D);
        poseStack.mulPose((new Quaternionf()).setAngleAxis((float)Math.toRadians(180.0D - entityYaw), 0.0F, 1.0F, 0.0F));

        // Shake/Hurt animation logic
        float hurtTime = (float)entity.getHurtTime() - partialTicks;
        float damage = entity.getDamage() - partialTicks;
        if (damage < 0.0F) damage = 0.0F;

        if (hurtTime > 0.0F) {
            poseStack.mulPose((new Quaternionf()).setAngleAxis((float)Math.toRadians(Math.sin(hurtTime) * hurtTime * damage / 10.0F * (float)entity.getHurtDir()), 1.0F, 0.0F, 0.0F));
        }

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose((new Quaternionf()).setAngleAxis((float)Math.toRadians(90.0D), 0.0F, 1.0F, 0.0F));

        this.boatModel.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);

        // 1. Render the Visible Boat (Texture)
        VertexConsumer vertexconsumer = buffer.getBuffer(this.boatModel.renderType(this.getTextureLocation(entity)));
        this.boatModel.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);

        // 2. Render the Water Mask (Prevents boat filling with water)
        // isUnderWater() is more accurate than isSwimming() for boats
        if (!entity.isUnderWater()) {
            VertexConsumer maskConsumer = buffer.getBuffer(RenderType.waterMask());
            this.boatModel.renderToBuffer(poseStack, maskConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Boat entity) {
        return (entity instanceof ChestBoat) ? chestBoatTexture : boatTexture;
    }
}