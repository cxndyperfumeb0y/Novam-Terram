package net.candyperfumeboy.novamterram.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

/** The flowers in a faun's crown. The texture is white where they are, and tinted to the flower it ate. */
public class FaunCrownLayer extends RenderLayer<FaunEntity, FaunModel<FaunEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/faun_crown.png");

    public FaunCrownLayer(RenderLayerParent<FaunEntity, FaunModel<FaunEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, FaunEntity faun,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (!faun.isFloral() || faun.isInvisible()) return;
        renderColoredCutoutModel(this.getParentModel(), TEXTURE, poseStack, buffer, packedLight, faun,
                faun.getCrownColor());
    }
}
