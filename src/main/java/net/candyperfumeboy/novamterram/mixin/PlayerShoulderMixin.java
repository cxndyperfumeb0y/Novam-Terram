package net.candyperfumeboy.novamterram.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Vanilla knocks shoulder parrots off after falling half a block, so an ordinary jump
 * does it. This keeps them on unless the fall is big enough to hurt.
 */
@Mixin(Player.class)
public abstract class PlayerShoulderMixin {

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;removeEntitiesOnShoulder()V"))
    private void novamterram$keepParrotThroughJumps(Player player, Operation<Void> original) {
        if (player.fallDistance > 3.0F || player.isInWater() || player.getAbilities().flying
                || player.isSleeping() || player.isInPowderSnow) {
            original.call(player);
        }
    }
}
