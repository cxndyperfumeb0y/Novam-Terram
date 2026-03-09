package net.candyperfumeboy.novamterram.worldgen.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class MyrtleFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<MyrtleFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).apply(instance, MyrtleFoliagePlacer::new));

    public MyrtleFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        // Ensure this points to your actual registry entry
        return NTTreePlacers.MYRTLE_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter setter, RandomSource random, TreeConfiguration config, int maxFreeTreeHeight, FoliageAttachment attachment, int foliageHeight, int foliageRadius, int offset) {
        BlockPos center = attachment.pos();

        // Create a small, messy cluster of leaves around each branch end
        // foliageRadius comes from the IntProvider in your ConfiguredFeature
        for (int i = 0; i < 3; i++) {
            this.placeLeavesRow(level, setter, random, config, center, foliageRadius - i, i, attachment.doubleTrunk());
        }

        // Add a few "stray" leaves for a wild look
        if (random.nextFloat() < 0.5f) {
            BlockPos strayPos = center.above(2);
            setter.set(strayPos, config.foliageProvider.getState(random, strayPos));
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
        // This is the height of the leaf clump itself
        return 3;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int radius, boolean doubleTrunk) {
        // This makes the bush look rounded and organic instead of a square
        if (localX == radius && localZ == radius && radius > 0) {
            return random.nextFloat() < 0.7f;
        }
        return false;
    }
}