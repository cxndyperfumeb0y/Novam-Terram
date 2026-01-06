package net.candyperfumeboy.novamterram.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Ocelot;

public class OcelotCollarLayer extends RenderLayer<Ocelot, OcelotModel<Ocelot>> {
    private static final ResourceLocation COLLAR_TEXTURE = ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/ocelot/ocelot_collar.png");

    public OcelotCollarLayer(RenderLayerParent<Ocelot, OcelotModel<Ocelot>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Ocelot ocelot, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (ocelot.getPersistentData().getBoolean("IsTamed") && !ocelot.isInvisible()) {

            // 1. Get the color ID from NBT (Purple is 10)
            int colorId = ocelot.getPersistentData().contains("CollarColor") ? ocelot.getPersistentData().getInt("CollarColor") : 10;
            net.minecraft.world.item.DyeColor dye = net.minecraft.world.item.DyeColor.byId(colorId);

            // 2. Get the Hex color and add the Alpha bits (FF at the start for 100% opacity)
            int colorHex = dye.getTextureDiffuseColor();
            int finalColor = 0xFF000000 | colorHex;

            // 3. Get the vertex consumer for the texture
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(COLLAR_TEXTURE));

            // 4. Render directly to the model buffer
            // This uses 5 arguments and is very stable across 1.21 versions
            this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, finalColor);
        }
    }}