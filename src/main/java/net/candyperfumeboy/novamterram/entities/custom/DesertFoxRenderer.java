package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Fox;

public class DesertFoxRenderer extends FoxRenderer {
    private static final ResourceLocation AWAKE =
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/fox/warm_fox.png");
    private static final ResourceLocation SLEEPING =
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/fox/warm_fox_sleep.png");

    public DesertFoxRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Fox entity) {
        if (entity instanceof DesertFoxEntity desertFox) {
            return desertFox.isSleeping() ? SLEEPING : AWAKE;
        }
        return super.getTextureLocation(entity);
    }
}