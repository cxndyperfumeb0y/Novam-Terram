package net.candyperfumeboy.novamterram.worldgen.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class CypressTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<CypressTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, CypressTrunkPlacer::new));

    public CypressTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return NTTreePlacers.CYPRESS_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int height, BlockPos pos, TreeConfiguration config) {

        // 1. FIND THE GROUND (Correcting for uneven terrain)
        BlockPos basePos = pos;
        int depthSearch = 0;
        // Search downwards to find a solid block, up to 3 blocks deep
        while (depthSearch < 3 && level.isStateAtPosition(basePos.below(), state -> state.isAir() || state.is(Blocks.WATER))) {
            basePos = basePos.below();
            depthSearch++;
        }

        // Set dirt at the adjusted base
        setDirtAt(level, blockSetter, random, basePos.below(), config);

        // 2. THE FLARED BUTTRESS (Anchored to the adjusted basePos)
        for (int y = 0; y < 3; y++) {
            float width = 1.5f - (y * 0.5f);
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (Math.sqrt(x * x + z * z) <= width + (random.nextFloat() * 0.5)) {
                        this.placeLog(level, blockSetter, random, basePos.offset(x, y, z), config);
                    }
                }
            }
        }

        // 3. THE MAIN TRUNK (Growing up from the adjusted basePos)
        for (int y = 0; y < height; y++) {
            this.placeLog(level, blockSetter, random, basePos.above(y), config);
        }

        // 4. KNEES (Use the adjusted basePos for consistency)
        for (int i = 0; i < 4; i++) {
            int rx = random.nextInt(6) - 3;
            int rz = random.nextInt(6) - 3;
            if (Math.abs(rx) > 1 || Math.abs(rz) > 1) {
                BlockPos kneePos = basePos.offset(rx, 0, rz);
                // Ensure knees are connected to ground if air/water found
                if (level.isStateAtPosition(kneePos, state -> state.isAir() || state.is(Blocks.WATER))) {
                    kneePos = kneePos.below();
                }
                int kneeHeight = random.nextInt(2) + 1;
                for (int h = 0; h < kneeHeight; h++) {
                    this.placeLog(level, blockSetter, random, kneePos.above(h), config);
                }
            }
        }

        return List.of(
                new FoliagePlacer.FoliageAttachment(basePos.above(height), 0, false),
                new FoliagePlacer.FoliageAttachment(basePos.above(height - 2), 1, false)
        );
    }
}