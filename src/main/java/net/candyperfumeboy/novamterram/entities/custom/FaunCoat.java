package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Locale;

/** The faun's coat colours. */
public enum FaunCoat {
    FOREST("faun"), AUTUMN("faun_autumn"), SNOW("faun_snow"), WHITE("faun_white");

    /** Edit data/novamterram/tags/worldgen/biome/autumn_faun_biomes.json to change where autumn coats appear. */
    private static final TagKey<Biome> AUTUMN_BIOMES = TagKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "autumn_faun_biomes"));

    private final ResourceLocation texture;
    private final ResourceLocation babyTexture;

    FaunCoat(String file) {
        this.texture = path(file);
        this.babyTexture = path(file + "_baby");
    }

    private static ResourceLocation path(String file) {
        return ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/" + file + ".png");
    }

    public ResourceLocation texture(boolean baby) {
        return baby ? this.babyTexture : this.texture;
    }

    public String id() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    /** Unknown or missing names (fauns from before coats existed) are forest brown. */
    public static FaunCoat byId(String id) {
        for (FaunCoat coat : values()) {
            if (coat.id().equals(id)) return coat;
        }
        return FOREST;
    }

    public static FaunCoat byIndex(int index) {
        return index >= 0 && index < values().length ? values()[index] : FOREST;
    }

    /** White is not tied to a biome; it is rolled separately as a rare spawn. */
    public static FaunCoat forBiome(Holder<Biome> biome, BlockPos pos) {
        if (biome.is(AUTUMN_BIOMES)) return AUTUMN;
        if (biome.value().coldEnoughToSnow(pos)) return SNOW;
        return FOREST;
    }
}
