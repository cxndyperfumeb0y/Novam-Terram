package net.candyperfumeboy.novamterram.entities.enhancements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.candyperfumeboy.novamterram.util.IOcelotMixin;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.item.DyeColor;

public class OcelotCollarLayer extends RenderLayer<Ocelot, OcelotModel<Ocelot>> {
    // Cats and ocelots share a model layout, so the vanilla cat collar fits
    private static final ResourceLocation TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/cat/cat_collar.png");

    public OcelotCollarLayer(RenderLayerParent<Ocelot, OcelotModel<Ocelot>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Ocelot ocelot,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        int color = ((IOcelotMixin) ocelot).novamterram$getCollar();
        if (color < 0 || ocelot.isInvisible()) return;

        // The model is already posed by the main renderer
        renderColoredCutoutModel(this.getParentModel(), TEXTURE, poseStack, buffer, packedLight, ocelot,
                DyeColor.byId(color).getTextureDiffuseColor());
    }
}
