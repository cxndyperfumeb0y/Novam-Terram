package net.candyperfumeboy.novamterram.worldgen.tree;

import com.google.common.collect.ImmutableList;
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

import java.util.List;
import java.util.function.BiConsumer;

public class AncientOakTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<AncientOakTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> trunkPlacerParts(instance).apply(instance, AncientOakTrunkPlacer::new)
    );

    public AncientOakTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        // You'll need to register this in NTTreePlacers!
        return NTTreePlacers.ANCIENT_OAK_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int freeTreeHeight, BlockPos pos, TreeConfiguration config) {
        // Grounding 4x4
        for(int x = -1; x <= 2; x++) {
            for(int z = -1; z <= 2; z++) {
                setDirtAt(level, blockSetter, random, pos.offset(x, -1, z), config);
            }
        }

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < freeTreeHeight; i++) {
            // Core 2x2
            this.placeLog(level, blockSetter, random, mutablePos, config, pos, 0, i, 0);
            this.placeLog(level, blockSetter, random, mutablePos, config, pos, 1, i, 0);
            this.placeLog(level, blockSetter, random, mutablePos, config, pos, 0, i, 1);
            this.placeLog(level, blockSetter, random, mutablePos, config, pos, 1, i, 1);

            // Tapered Base (Bottom 3 blocks)
            if (i < 3) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    this.placeLog(level, blockSetter, random, mutablePos, config, pos, dir.getStepX() > 0 ? 2 : (dir.getStepX() < 0 ? -1 : 0), i, dir.getStepZ() > 0 ? 2 : (dir.getStepZ() < 0 ? -1 : 0));
                }
            }

            // SHOULDERS: Randomly branch out at the top to support wide foliage
            if (i > freeTreeHeight - 3) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    if (random.nextFloat() < 0.4f) {
                        this.placeLog(level, blockSetter, random, mutablePos, config, pos, dir.getStepX() * 2, i, dir.getStepZ() * 2);
                    }
                }
            }
        }

        return ImmutableList.of(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, true));
    }

    private void placeLog(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> setter, RandomSource random, BlockPos.MutableBlockPos mPos, TreeConfiguration config, BlockPos origin, int x, int y, int z) {
        mPos.setWithOffset(origin, x, y, z);
        this.placeLogIfFree(level, setter, random, mPos, config);
    }
}