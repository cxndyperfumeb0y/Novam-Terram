package net.candyperfumeboy.novamterram.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.candyperfumeboy.novamterram.worldgen.biome.NTSpecialPlacement;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;

// WrapMethod runs around the whole method, so it works after TerraBlender's own hook.
@Mixin(MultiNoiseBiomeSource.class)
public abstract class NTBiomePlacementMixin {

    @WrapMethod(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;")
    private Holder<Biome> novamterram$placeSpecialBiomes(int x, int y, int z, Climate.Sampler sampler,
                                                         Operation<Holder<Biome>> original) {
        Holder<Biome> chosen = original.call(x, y, z, sampler);
        return NTSpecialPlacement.apply((BiomeSource) (Object) this, chosen, sampler.sample(x, y, z), y * 4);
    }
}
