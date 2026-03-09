package net.candyperfumeboy.novamterram.worldgen.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MyrtleTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<MyrtleTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, MyrtleTrunkPlacer::new));

    public MyrtleTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        // Ensure this points to the correct registry entry for Myrtle
        return NTTreePlacers.MYRTLE_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int freeTreeHeight, BlockPos pos, TreeConfiguration config) {
        // 1. Prepare the ground
        setDirtAt(level, blockSetter, random, pos.below(), config);
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();

        // 2. Central short stem
        for(int i = 0; i < freeTreeHeight; i++) {
            this.placeLog(level, blockSetter, random, pos.above(i), config);
        }
        attachments.add(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, false));

        // 3. Diagonal side branches (The "Bushy" look)
        for (int i = 0; i < 3 + random.nextInt(2); i++) {
            BlockPos branchPos = pos;
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int branchLen = 1 + random.nextInt(2);

            for (int j = 0; j < branchLen; j++) {
                branchPos = branchPos.relative(dir).above();
                this.placeLog(level, blockSetter, random, branchPos, config);
            }
            // Attach leaf clump to the end of each branch
            attachments.add(new FoliagePlacer.FoliageAttachment(branchPos.above(), 1, false));
        }

        return attachments;
    }
}