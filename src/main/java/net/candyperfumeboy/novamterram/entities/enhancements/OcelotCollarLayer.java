package net.candyperfumeboy.novamterram.entities.enhancements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.item.DyeColor;

public class OcelotCollarLayer extends RenderLayer<Ocelot, OcelotModel<Ocelot>> {
    private static final ResourceLocation COLLAR_TEXTURE = ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/ocelot/ocelot_collar.png");

    public OcelotCollarLayer(RenderLayerParent<Ocelot, OcelotModel<Ocelot>> renderer, net.minecraft.client.model.geom.EntityModelSet modelSet) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Ocelot ocelot, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (ocelot.getPersistentData().getBoolean("IsTamed") && !ocelot.isInvisible()) {

            // 1. GET THE COLOR
            // We need the packed INT for this version of the method
            int colorId = ocelot.getPersistentData().contains("CollarColor") ? ocelot.getPersistentData().getInt("CollarColor") : 14;
            int colorInt = DyeColor.byId(colorId).getTextureDiffuseColor();

            // Note: In some mappings, you might need to ensure the Alpha is 255.
            // If the collar is invisible, change the line below to:
            // int finalColor = 0xFF000000 | colorInt;
            int finalColor = colorInt;

            // 2. ANIMATION SYNC
            this.getParentModel().prepareMobModel(ocelot, limbSwing, limbSwingAmount, partialTicks);
            this.getParentModel().setupAnim(ocelot, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            // 3. THE EXACT 4-ARGUMENT CALL (Or 5 depending on mapping)
            // Based on your error, it likely wants:
            // (Model, Texture, PoseStack, Buffer, Light, Entity, ColorInt)
            renderColoredCutoutModel(this.getParentModel(), COLLAR_TEXTURE, poseStack, buffer, packedLight, ocelot, finalColor);
        }
    }
}