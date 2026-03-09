package net.candyperfumeboy.novamterram.worldgen.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FungalReachTrunkPlacer extends TrunkPlacer {
    // FIXED: Codec must point to FungalReachTrunkPlacer, not Cypress
    public static final MapCodec<FungalReachTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, FungalReachTrunkPlacer::new));

    public FungalReachTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        // Ensure this matches your registration in NTTreePlacers
        return NTTreePlacers.FUNGAL_REACH_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int freeTreeHeight, BlockPos pos, TreeConfiguration config) {
        // Set the dirt underneath
        setDirtAt(level, blockSetter, random, pos.below(), config);
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();

        // 1. Place the main trunk
        for (int i = 0; i < freeTreeHeight; ++i) {
            this.placeLog(level, blockSetter, random, pos.above(i), config);
        }

        // 2. Create "Canopy Arms"
        // We want the branches to start a bit lower than the very top for a 'nestled' look
        int branchHeight = freeTreeHeight - 1;

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            // Only branch out on some sides to keep it "messy" and organic
            if (random.nextFloat() < 0.75f) {
                BlockPos branchPos = pos.above(branchHeight).relative(dir);
                this.placeLog(level, blockSetter, random, branchPos, config);

                // If the tree is tall enough, extend the branch out one more block
                if (freeTreeHeight > 5 && random.nextBoolean()) {
                    branchPos = branchPos.relative(dir);
                    this.placeLog(level, blockSetter, random, branchPos, config);
                }

                // Attach foliage to the end of each branch
                attachments.add(new FoliagePlacer.FoliageAttachment(branchPos.above(), 0, false));
            }
        }

        // Always add a center attachment for the top canopy
        attachments.add(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, false));

        return attachments;
    }
}