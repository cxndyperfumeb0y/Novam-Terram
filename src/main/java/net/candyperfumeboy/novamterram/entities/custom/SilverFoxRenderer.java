package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Fox;

public class SilverFoxRenderer extends FoxRenderer {
    private static final ResourceLocation AWAKE =
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/fox/silver_fox.png");
    private static final ResourceLocation SLEEPING =
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/fox/silver_fox_sleep.png");

    public SilverFoxRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Fox entity) {
        if (entity instanceof SilverFoxEntity silverFox) {
            return silverFox.isSleeping() ? SLEEPING : AWAKE;
        }
        return super.getTextureLocation(entity);
    }
}