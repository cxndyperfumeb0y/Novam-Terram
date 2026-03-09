package net.candyperfumeboy.novamterram.entities.enhancements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.OcelotRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Ocelot;

public class NTOcelotRenderer extends OcelotRenderer {
    private static final ResourceLocation DEFAULT = ResourceLocation.withDefaultNamespace("textures/entity/cat/ocelot.png");
    private static final ResourceLocation WHITE = ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/ocelot/ocelot_white.png");
    private static final ResourceLocation GREY = ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/ocelot/ocelot_grey.png");
    private static final ResourceLocation BROWN = ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/ocelot/ocelot_brown.png");

    public NTOcelotRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new OcelotCollarLayer(this, context.getModelSet()));
    }

    @Override
    public void render(Ocelot entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // The Mixin handles the sitting pose logic now, so we just render normally.
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(Ocelot entity) {
        // 1. Respect manually set variants (for spawn eggs or commands)
        if (entity.getPersistentData().contains("OcelotVariant")) {
            return getTextureFromId(entity.getPersistentData().getInt("OcelotVariant"));
        }

        // 2. Weight-based logic using the UUID hash
        // We use a larger range (0-9) to allow for 10% increments
        int hash = Math.abs(entity.getUUID().hashCode()) % 10;

        // 70% chance for Vanilla (0, 1, 2, 3, 4, 5, 6)
        // 10% chance for White (7)
        // 10% chance for Grey (8)
        // 10% chance for Brown (9)
        if (hash < 7) return DEFAULT;
        if (hash == 7) return WHITE;
        if (hash == 8) return GREY;
        return BROWN;
    }

    private ResourceLocation getTextureFromId(int id) {
        return switch (id) {
            case 1 -> WHITE;
            case 2 -> GREY;
            case 3 -> BROWN;
            default -> DEFAULT; // Any other ID defaults to Vanilla
        };
    }
}