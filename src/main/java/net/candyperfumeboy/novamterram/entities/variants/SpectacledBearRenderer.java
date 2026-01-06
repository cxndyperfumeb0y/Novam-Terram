package net.candyperfumeboy.novamterram.entities.variants;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PolarBearRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.PolarBear;

public class SpectacledBearRenderer extends PolarBearRenderer {
    // Path: src/main/resources/assets/novamterram/textures/entity/bear/spectacled_bear.png
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/bear/spectacled_bear.png");

    public SpectacledBearRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(PolarBear bear) {
        return TEXTURE;
    }
}