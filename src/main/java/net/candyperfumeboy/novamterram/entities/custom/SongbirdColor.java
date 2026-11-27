package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.Locale;

/**
 * Every songbird colour and its song. To add one: add a line here and put
 * songbird_<name>.png (32x32) in assets/novamterram/textures/entity/.
 */
public enum SongbirdColor {
    // voice = how high its chirps are, beat = ticks between song notes,
    // song = the tune, as note pitches (0.5 is lowest, 2.0 is highest)
    BLUE(1.00F, 4, 1.19F, 1.50F, 1.19F),
    CARDINAL(0.90F, 5, 1.78F, 1.33F, 1.78F, 1.33F),
    GOLDFINCH(1.12F, 2, 1.50F, 1.68F, 1.89F, 1.68F, 2.00F),
    ROBIN(0.95F, 4, 1.19F, 1.41F, 1.33F, 1.59F),
    CHICKADEE(1.08F, 6, 1.89F, 1.41F, 1.41F),
    SPARROW(1.02F, 3, 1.59F, 1.59F, 1.59F, 1.89F),
    ORIOLE(0.88F, 5, 1.06F, 1.33F, 1.59F, 1.33F, 1.78F),
    GREEN(0.85F, 3, 1.00F, 1.50F, 1.00F, 1.50F),
    WHITE(1.15F, 7, 1.50F, 1.78F, 2.00F);

    private final float voice;
    private final int beat;
    private final float[] song;

    SongbirdColor(float voice, int beat, float... song) {
        this.voice = voice;
        this.beat = beat;
        this.song = song;
    }

    public float voice() {
        return this.voice;
    }

    public int beat() {
        return this.beat;
    }

    public int songLength() {
        return this.song.length;
    }

    public float note(int index) {
        return this.song[index];
    }

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

    /** The colours that suit a biome, for birds that spawn in the wild. */
    public static SongbirdColor forBiome(Holder<Biome> biome, BlockPos pos, RandomSource random) {
        if (biome.value().coldEnoughToSnow(pos)) return pick(random, CARDINAL, CHICKADEE, WHITE);
        if (biome.is(BiomeTags.IS_TAIGA)) return pick(random, CHICKADEE, BLUE, ROBIN);
        if (biome.is(BiomeTags.IS_JUNGLE)) return pick(random, GREEN, ORIOLE);
        if (biome.is(Biomes.FLOWER_FOREST) || biome.is(Biomes.MEADOW) || biome.is(Biomes.CHERRY_GROVE)) {
            return pick(random, GOLDFINCH, BLUE, ORIOLE, ROBIN);
        }
        if (biome.is(Biomes.PLAINS) || biome.is(Biomes.SUNFLOWER_PLAINS)) {
            return pick(random, SPARROW, GOLDFINCH, ROBIN);
        }
        if (biome.is(BiomeTags.IS_SAVANNA) || biome.is(Biomes.DESERT)) return pick(random, SPARROW, ORIOLE);
        return random(random); // everywhere else, including most Novam Terram forests
    }

    private static SongbirdColor pick(RandomSource random, SongbirdColor... options) {
        return options[random.nextInt(options.length)];
    }
}
