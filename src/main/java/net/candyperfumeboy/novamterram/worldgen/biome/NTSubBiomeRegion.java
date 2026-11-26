package net.candyperfumeboy.novamterram.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.candyperfumeboy.novamterram.Config;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

/** Rare biomes. */
public class NTSubBiomeRegion extends Region {

    public NTSubBiomeRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        NTSlotRules slots = new NTSlotRules();

        slots.give(NTBiomes.CALDERA_GROTTO, Config.ENABLE_CALDERA_GROTTO::get, Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE)
                .erosion(-1.0F, -0.375F);
        slots.give(NTBiomes.FUNGAL_REACH, Config.ENABLE_FUNGAL_REACH::get, Biomes.SPARSE_JUNGLE)
                .weirdness(0.4F, 1.0F);
        slots.give(NTBiomes.FUNGAL_REACH, Config.ENABLE_FUNGAL_REACH::get, Biomes.MUSHROOM_FIELDS)
                .temperature(-1.0F, -0.15F);

        // Start from the full vanilla layout and hand over only the parts named above.
        this.addModifiedVanillaOverworldBiomes(
                pair -> slots.apply(mapper, pair.getFirst(), pair.getSecond()),
                builder -> {});
    }
}
