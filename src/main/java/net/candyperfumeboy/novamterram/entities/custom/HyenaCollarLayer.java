package net.candyperfumeboy.novamterram.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

/** Dyed collar on tamed hyenas. The texture is white where the collar is, and tinted in game. */
public class HyenaCollarLayer extends RenderLayer<HyenaEntity, HyenaModel<HyenaEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/hyena_collar.png");

    public HyenaCollarLayer(RenderLayerParent<HyenaEntity, HyenaModel<HyenaEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, HyenaEntity hyena,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (!hyena.isTame() || hyena.isInvisible()) return;
        renderColoredCutoutModel(this.getParentModel(), TEXTURE, poseStack, buffer, packedLight, hyena,
                hyena.getCollarColor().getTextureDiffuseColor());
    }
}
