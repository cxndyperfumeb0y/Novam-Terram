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

/** Main set. Weirdness below 0 is one side of a river, above 0 the other. */
public class NTOverworldRegion extends Region {

    public NTOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        NTSlotRules slots = new NTSlotRules();

        // ---- Uncommon and shape-specific first (earlier rules win) ----
        // Gallery forests line warm river valleys
        slots.give(NTBiomes.GALLERY_FOREST, Config.ENABLE_GALLERY_FOREST::get, Biomes.JUNGLE, Biomes.SAVANNA, Biomes.FOREST)
                .temperature(0.2F, 1.0F)
                .humidity(-0.1F, 1.0F)
                .weirdness(-0.2667F, -0.05F);
        slots.give(NTBiomes.GALLERY_FOREST, Config.ENABLE_GALLERY_FOREST::get, Biomes.JUNGLE, Biomes.SAVANNA, Biomes.FOREST)
                .temperature(0.2F, 1.0F)
                .humidity(-0.1F, 1.0F)
                .weirdness(0.05F, 0.2667F);
        slots.give(NTBiomes.CLOUD_FOREST, Config.ENABLE_CLOUD_FOREST::get, Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.SPARSE_JUNGLE)
                .erosion(-1.0F, -0.2225F);
        slots.give(NTBiomes.ROYAL_FOREST, Config.ENABLE_ROYAL_FOREST::get, Biomes.FOREST)
                .temperature(-0.15F, 0.2F)
                .weirdness(0.4F, 1.0F);
        slots.give(NTBiomes.SAFFRON_GROVE, Config.ENABLE_SAFFRON_GROVE::get, Biomes.SAVANNA)
                .weirdness(0.4F, 1.0F);
        slots.give(NTBiomes.COASTAL_CHAPARRAL, Config.ENABLE_COASTAL_CHAPARRAL::get, Biomes.DESERT)
                .continentalness(-0.19F, 0.03F);

        // ---- Common: roughly half of a similar vanilla biome ----
        slots.give(NTBiomes.BLUEBELL_WOODS, Config.ENABLE_BLUEBELL_WOODS::get, Biomes.BIRCH_FOREST)
                .continentalness(0.03F, 1.0F);
        slots.give(NTBiomes.BLACK_FOREST, Config.ENABLE_BLACK_FOREST::get, Biomes.DARK_FOREST)
                .weirdness(-1.0F, 0.0F);
        slots.give(NTBiomes.OZARK_FOREST, Config.ENABLE_OZARK_FOREST::get, Biomes.FOREST)
                .temperature(0.2F, 0.55F)
                .weirdness(-1.0F, 0.0F);
        slots.give(NTBiomes.LARCH_FOREST, Config.ENABLE_LARCH_FOREST::get, Biomes.SNOWY_TAIGA)
                .weirdness(-1.0F, 0.0F);
        slots.give(NTBiomes.AUTUMNAL_TAIGA, Config.ENABLE_AUTUMNAL_TAIGA::get, Biomes.OLD_GROWTH_SPRUCE_TAIGA)
                .continentalness(0.03F, 1.0F);
        slots.give(NTBiomes.AUTUMNAL_TAIGA, Config.ENABLE_AUTUMNAL_TAIGA::get, Biomes.OLD_GROWTH_PINE_TAIGA)
                .weirdness(0.4F, 1.0F);
        slots.give(NTBiomes.CEDAR_SLOPES, Config.ENABLE_CEDAR_SLOPES::get, Biomes.GROVE)
                .weirdness(-1.0F, 0.0F);
        slots.give(NTBiomes.MYRTLE_FIELDS, Config.ENABLE_MYRTLE_FIELDS::get, Biomes.PLAINS)
                .temperature(-0.15F, 0.2F)
                .weirdness(-1.0F, 0.0F);
        slots.give(NTBiomes.MYRTLE_FIELDS, Config.ENABLE_MYRTLE_FIELDS::get, Biomes.SUNFLOWER_PLAINS)
                .weirdness(0.4F, 1.0F);
        slots.give(NTBiomes.MAQUIS, Config.ENABLE_MAQUIS::get, Biomes.SAVANNA)
                .weirdness(-1.0F, 0.0F);
        slots.give(NTBiomes.RAINFOREST, Config.ENABLE_RAINFOREST::get, Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE)
                .humidity(0.3F, 1.0F)
                .weirdness(-1.0F, 0.0F);
        slots.give(NTBiomes.RAINFOREST, Config.ENABLE_RAINFOREST::get, Biomes.BAMBOO_JUNGLE)
                .weirdness(0.4F, 1.0F);
        slots.give(NTBiomes.HEATH, Config.ENABLE_HEATH::get, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS)
                .weirdness(-1.0F, 0.0F);
        slots.give(NTBiomes.AMBER_SANDS, Config.ENABLE_AMBER_SANDS::get, Biomes.BADLANDS, Biomes.WOODED_BADLANDS)
                .weirdness(-1.0F, 0.0F);
        slots.give(NTBiomes.AMBER_SANDS, Config.ENABLE_AMBER_SANDS::get, Biomes.ERODED_BADLANDS)
                .weirdness(0.4F, 1.0F);
        slots.give(NTBiomes.ANDEAN_HILLS, Config.ENABLE_ANDEAN_HILLS::get, Biomes.STONY_PEAKS, Biomes.SAVANNA_PLATEAU)
                .weirdness(-1.0F, 0.0F);
        slots.give(NTBiomes.ANDEAN_HILLS, Config.ENABLE_ANDEAN_HILLS::get, Biomes.WINDSWEPT_SAVANNA)
                .weirdness(0.4F, 1.0F);

        // ---- Wetlands ----
        slots.give(NTBiomes.SILVER_BIRCH_WETLAND, Config.ENABLE_SILVER_BIRCH_WETLAND::get, Biomes.SWAMP)
                .weirdness(-1.0F, 0.0F);
        slots.give(NTBiomes.CYPRESS_FEN, Config.ENABLE_CYPRESS_FEN::get, Biomes.SWAMP)
                .weirdness(0.4F, 1.0F);
        slots.give(NTBiomes.MANGROVE_LAGOON, Config.ENABLE_MANGROVE_LAGOON::get, Biomes.MANGROVE_SWAMP)
                .weirdness(-1.0F, 0.0F);

        // Start from the full vanilla layout and hand over only the parts named above.
        this.addModifiedVanillaOverworldBiomes(
                pair -> slots.apply(mapper, pair.getFirst(), pair.getSecond()),
                builder -> {});

        // Aegean, Blue Grove and Mirror Mire are placed by NTSpecialPlacement. These entries only
        // register them with the biome source: the offset of 1.0 means they never win here.
        Climate.Parameter any = Climate.Parameter.span(-1.0F, 1.0F);
        Climate.ParameterPoint never = Climate.parameters(any, any, any, any,
                Climate.Parameter.point(0.0F), any, 1.0F);
        if (Config.ENABLE_AEGEAN.get()) mapper.accept(Pair.of(never, NTBiomes.AEGEAN));
        if (Config.ENABLE_BLUE_GROVE.get()) mapper.accept(Pair.of(never, NTBiomes.BLUE_GROVE));
        if (Config.ENABLE_MIRROR_MIRE.get()) mapper.accept(Pair.of(never, NTBiomes.MIRROR_MIRE));
    }
}
