package net.candyperfumeboy.novamterram.worldgen.biome;

import net.candyperfumeboy.novamterram.Config;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Biomes that must always generate the same way, whichever TerraBlender patch they are in.
 * Called by NTBiomePlacementMixin after the normal biome has been chosen.
 */
public final class NTSpecialPlacement {

    private NTSpecialPlacement() {}

    // ---- Ocean islands (raised by data/minecraft/worldgen/density_function/overworld/offset.json) ----
    private static final float VANILLA_DEEP_OCEAN_OFFSET = -0.72595F; // flat vanilla sea floor, about y=35
    private static final float ISLAND_C_MIN = -1.0F, ISLAND_C_MAX = -0.48F;
    private static final float ISLAND_MIN_TEMP = -0.15F;
    private static final float ISLAND_SHORE_LIFT = 0.15F;  // Aegean starts in the shallows around the island
    private static final float ISLAND_CENTRE_LIFT = 0.40F; // high ground, about y=86 and up
    private static final float MAQUIS_MIN_HUMIDITY = -0.05F; // lower this to get Maquis centres more often

    // ---- Blue Grove (a narrow cold, slightly humid pocket) ----
    private static final float GROVE_T_MIN = -0.42F, GROVE_T_MAX = -0.18F;
    private static final float GROVE_H_MIN =  0.11F, GROVE_H_MAX =  0.29F;
    private static final float GROVE_C_MIN = -0.11F;

    // ---- Mirror Mire: strictly inside the Blue Grove box, leaving a ring of grove around it ----
    private static final float MIRE_T_MIN = -0.37F, MIRE_T_MAX = -0.23F;
    private static final float MIRE_H_MIN =  0.15F, MIRE_H_MAX =  0.25F;
    private static final float MIRE_C_MIN =  0.03F;
    private static final float MIRE_E_MIN =  0.05F;   // flat ground only
    private static final float MIRE_W_MIN =  0.27F;   // keeps it away from rivers

    private static final Map<BiomeSource, Map<ResourceKey<Biome>, Holder<Biome>>> CACHE = new ConcurrentHashMap<>();

    public static Holder<Biome> apply(BiomeSource source, Holder<Biome> chosen,
                                      Climate.TargetPoint target, int blockY) {
        // Keep rivers and cave biomes as they are.
        if (chosen.is(Biomes.RIVER) || chosen.is(Biomes.FROZEN_RIVER) || chosen.is(Biomes.LUSH_CAVES)
                || chosen.is(Biomes.DRIPSTONE_CAVES) || chosen.is(Biomes.DEEP_DARK)) {
            return chosen;
        }

        // depth = height gradient + terrain offset, so the offset tells us how far the floor was lifted.
        float offset = target.depth() / 10000.0F - (1.5F - (blockY + 64) * 3.0F / 384.0F);

        ResourceKey<Biome> wanted = pick(offset - VANILLA_DEEP_OCEAN_OFFSET,
                target.temperature() / 10000.0F, target.humidity() / 10000.0F,
                target.continentalness() / 10000.0F, target.erosion() / 10000.0F,
                target.weirdness() / 10000.0F);
        if (wanted == null) return chosen;

        // possibleBiomes only holds our biomes in the Overworld, so other dimensions are untouched.
        Map<ResourceKey<Biome>, Holder<Biome>> known = CACHE.computeIfAbsent(source, s -> new ConcurrentHashMap<>());
        Holder<Biome> holder = known.get(wanted);
        if (holder == null) {
            for (Holder<Biome> candidate : source.possibleBiomes()) {
                if (candidate.is(wanted)) {
                    known.put(wanted, candidate);
                    return candidate;
                }
            }
            return chosen;
        }
        return holder;
    }

    /** Returns the special biome for this climate, or null to leave the normal choice. */
    static ResourceKey<Biome> pick(float lift, float t, float h, float c, float e, float w) {
        if (c >= ISLAND_C_MIN && c <= ISLAND_C_MAX) {
            if (t < ISLAND_MIN_TEMP || lift < ISLAND_SHORE_LIFT || !Config.ENABLE_AEGEAN.get()) return null;
            boolean maquis = lift >= ISLAND_CENTRE_LIFT && h >= MAQUIS_MIN_HUMIDITY && Config.ENABLE_MAQUIS.get();
            return maquis ? NTBiomes.MAQUIS : NTBiomes.AEGEAN;
        }

        if (!Config.ENABLE_BLUE_GROVE.get()) return null; // no grove means no mire either
        if (c < GROVE_C_MIN || t < GROVE_T_MIN || t > GROVE_T_MAX || h < GROVE_H_MIN || h > GROVE_H_MAX) return null;

        boolean mire = Config.ENABLE_MIRROR_MIRE.get()
                && t >= MIRE_T_MIN && t <= MIRE_T_MAX && h >= MIRE_H_MIN && h <= MIRE_H_MAX
                && c >= MIRE_C_MIN && e >= MIRE_E_MIN && Math.abs(w) >= MIRE_W_MIN;
        return mire ? NTBiomes.MIRROR_MIRE : NTBiomes.BLUE_GROVE;
    }
}
