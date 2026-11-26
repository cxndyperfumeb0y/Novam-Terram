package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.Locale;

/**
 * Every songbird colour. To add one: add its name here and put
 * songbird_<name>.png (32x32) in assets/novamterram/textures/entity/.
 */
public enum SongbirdColor {
    BLUE, CARDINAL, GOLDFINCH, ROBIN, CHICKADEE, SPARROW,
    ORIOLE, GREEN, WHITE;

    private final String id = this.name().toLowerCase(Locale.ROOT);
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
            NovamTerram.MOD_ID, "textures/entity/songbird_" + this.id + ".png");

    public String id() {
        return this.id;
    }

    public ResourceLocation texture() {
        return this.texture;
    }

    /** Unknown or missing names (such as birds saved before colours existed) become blue. */
    public static SongbirdColor byId(String id) {
        for (SongbirdColor color : values()) {
            if (color.id.equals(id)) return color;
        }
        return BLUE;
    }

    public static SongbirdColor byIndex(int index) {
        return index >= 0 && index < values().length ? values()[index] : BLUE;
    }

    public static SongbirdColor random(RandomSource random) {
        return values()[random.nextInt(values().length)];
    }
}
