package net.candyperfumeboy.novamterram.worldgen.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class AncientOakFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<AncientOakFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> foliagePlacerParts(instance)
                    .and(IntProvider.codec(0, 16).fieldOf("foliage_height").forGetter(fp -> fp.foliageHeight))
                    .apply(instance, AncientOakFoliagePlacer::new)
    );

    protected final IntProvider foliageHeight;

    public AncientOakFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider foliageHeight) {
        super(radius, offset);
        this.foliageHeight = foliageHeight;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return NTTreePlacers.ANCIENT_OAK_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter blockSetter, RandomSource random, TreeConfiguration config, int maxFreeTreeHeight, FoliageAttachment attachment, int foliageHeight, int foliageRadius, int offset) {
        // Ancient Oaks are "Flat-Topped" domes.
        for (int i = offset; i >= offset - foliageHeight; --i) {
            // Radius grows slightly as we go down, then shrinks at the very bottom
            float progress = (float)(offset - i) / (float)foliageHeight;
            int currentRadius = foliageRadius + (progress > 0.5f ? 1 : 2);

            // Squash the dome: make the middle layers the widest
            if (i == offset || i == offset - foliageHeight) {
                currentRadius -= 2;
            }

            this.placeLeavesRow(level, blockSetter, random, config, attachment.pos(), currentRadius, i, attachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
        return this.foliageHeight.sample(random);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean doubleTrunk) {
        // Circular math for a rounded dome
        if (localX == range && localZ == range && range > 0) return true;

        // Magical thinning: allow 20% of leaf blocks to be "air" to let light hit the flowers
        if (random.nextFloat() < 0.20f) return true;

        return (float)(localX * localX + localZ * localZ) > (float)(range * range) + 0.5f;
    }
}