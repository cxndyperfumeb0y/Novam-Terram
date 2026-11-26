package net.candyperfumeboy.novamterram.entities.enhancements;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.util.IOcelotMixin;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.OcelotRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Ocelot;

public class NTOcelotRenderer extends OcelotRenderer {
    private static final ResourceLocation DEFAULT = ResourceLocation.withDefaultNamespace("textures/entity/cat/ocelot.png");
    private static final ResourceLocation WHITE = texture("ocelot_white");
    private static final ResourceLocation GREY = texture("ocelot_grey");
    private static final ResourceLocation BROWN = texture("ocelot_brown");

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/ocelot/" + name + ".png");
    }

    public NTOcelotRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new NTOcelotModel(context.bakeLayer(ModelLayers.OCELOT));
        this.addLayer(new OcelotCollarLayer(this));
        this.addLayer(new OcelotHeldItemLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Ocelot ocelot) {
        return switch (((IOcelotMixin) ocelot).novamterram$getVariant()) {
            case 1 -> WHITE;
            case 2 -> GREY;
            case 3 -> BROWN;
            default -> DEFAULT;
        };
    }
}
