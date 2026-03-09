package net.candyperfumeboy.novamterram.worldgen.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

/**
 * Advanced Foliage Placer for "Mega" Autumnal Pines.
 * Features:
 * 1. Bell-Curve Shape: Widest in the middle, tucking in at the bottom for walkability.
 * 2. Star-Shaped Branches: Skips corners to create a "cross" or "star" look from above.
 * 3. Jagged Tiers: Creates natural "steps" in the leaves for a Christmas tree look.
 */
public class KinkedFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<KinkedFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> foliagePlacerParts(instance)
                    .and(IntProvider.codec(0, 24).fieldOf("crown_height").forGetter(fp -> fp.crownHeight))
                    .apply(instance, KinkedFoliagePlacer::new)
    );

    private final IntProvider crownHeight;

    public KinkedFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider crownHeight) {
        super(radius, offset);
        this.crownHeight = crownHeight;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return NTTreePlacers.KINKED_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter blockSetter, RandomSource random, TreeConfiguration config, int maxFreeTreeHeight, FoliageAttachment attachment, int foliageHeight, int foliageRadius, int offset) {
        BlockPos topPos = attachment.pos();

        for (int yOffset = offset; yOffset >= offset - foliageHeight; --yOffset) {
            int currentY = topPos.getY() + yOffset;
            int distanceDown = offset - yOffset;

            // 1. DETERMINE THE TIER (Every 3 or 4 blocks)
            int tierHeight = 4;
            int tierIndex = distanceDown / tierHeight;
            int positionInTier = distanceDown % tierHeight; // 0 is the top of a tier, 3 is the bottom

            // 2. THE RADIUS CALCULATION
            // Base width grows with the tier index
            int baseRadius = tierIndex + attachment.radiusOffset();

            // 3. THE "WAIST" LOGIC
            // If we are at the very top of a new tier (position 0),
            // we shrink the radius by 1 to create the "gap" look.
            int finalRadius = baseRadius;
            if (positionInTier == 0 && tierIndex > 0) {
                finalRadius -= 1;
            } else {
                // Otherwise, let it flare out slightly as it reaches the bottom of the tier
                finalRadius += (positionInTier > 2 ? 1 : 0);
            }

            // 4. PREVENT "BLOCKINESS" AT THE TOP
            if (distanceDown <= 1) finalRadius = 0;
            if (distanceDown == 2) finalRadius = 1;

            // 5. HARD CAP
            finalRadius = Math.min(finalRadius, 4);

            this.placeLeavesRow(level, blockSetter, random, config, new BlockPos(topPos.getX(), currentY, topPos.getZ()), finalRadius, 0, attachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
        // Ensure there is always trunk visible at the bottom (Clearance)
        int sampled = this.crownHeight.sample(random);
        return Math.min(sampled, height - 6);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean doubleTrunk) {
        // --- STAR SHAPE LOGIC ---
        // This creates a pointy, conifer-like branch structure instead of a box or circle.

        int x = Math.abs(localX);
        int z = Math.abs(localZ);
        
        // 1. Basic Distance Check (Circular)
        int distSq = x * x + z * z;
        int maxDistSq = range * range;
        if (distSq > maxDistSq + 1) return true;

        // 2. CORNER CUTTING (The "Star" Effect)
        // If we are far from the center (range > 1), skip the diagonal corners.
        // This leaves the cardinal directions (N, S, E, W) longer, creating branches.
        if (range > 1) {
            // If both X and Z are significant, we are in a "corner". Skip it.
            // Example: At range 3, position (2,2) is skipped, but (3,0) is kept.
            if (x > range / 2 && z > range / 2) {
                return true;
            }
        }

        return false;
    }
}