package net.candyperfumeboy.novamterram.entities.variants;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Fox;

public class SilverFoxRenderer extends FoxRenderer {
    // 1. Define both texture locations
    private static final ResourceLocation AWAKE =
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/silver_fox.png");
    private static final ResourceLocation SLEEPING =
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/silver_fox_sleep.png");

    public SilverFoxRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Fox entity) {
        // 2. Check if the fox is sleeping and return the correct texture
        if (entity.isSleeping()) {
            return SLEEPING;
        }
        return AWAKE;
    }
}