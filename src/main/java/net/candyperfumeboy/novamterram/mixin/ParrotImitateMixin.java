package net.candyperfumeboy.novamterram.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.candyperfumeboy.novamterram.entities.custom.SongbirdEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Songbirds inherit from Parrot, which copies monster noises. This turns that off for them only. */
@Mixin(Parrot.class)
public class ParrotImitateMixin {

    // require = 0: if the game code doesn't match, this is skipped instead of crashing
    @WrapOperation(method = "aiStep", require = 0,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/Parrot;imitateNearbyMobs(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean novamterram$songbirdsDoNotImitate(Level level, Entity parrot, Operation<Boolean> original) {
        return !(parrot instanceof SongbirdEntity) && original.call(level, parrot);
    }
}
