package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SongbirdRenderer extends MobRenderer<SongbirdEntity, SongbirdModel<SongbirdEntity>> {

    public SongbirdRenderer(EntityRendererProvider.Context context) {
        super(context, new SongbirdModel<>(context.bakeLayer(NTModelLayers.SONGBIRD_LAYER)), 0.25f);
    }

    // On a head, draw the bird exactly where that player is drawn, so it never lags behind
    @Override
    public Vec3 getRenderOffset(SongbirdEntity bird, float partialTick) {
        Player player = bird.getHeadOwner();
        if (player == null) return super.getRenderOffset(bird, partialTick);
        return player.getPosition(partialTick)
                .add(0.0D, SongbirdEntity.headHeight(player), 0.0D)
                .subtract(bird.getPosition(partialTick));
    }

    @Override
    public ResourceLocation getTextureLocation(SongbirdEntity entity) {
        // Baby size is handled by SongbirdModel.
        return entity.getSongbirdColor().texture();
    }
}
