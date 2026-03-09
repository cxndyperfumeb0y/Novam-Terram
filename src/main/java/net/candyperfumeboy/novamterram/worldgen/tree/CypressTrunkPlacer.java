package net.candyperfumeboy.novamterram.worldgen.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
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
        // 1. SET THE DIRT UNDERNEATH
        setDirtAt(level, blockSetter, random, pos.below(), config);

        // 2. THE FLARED BUTTRESS (The wide base)
        for (int y = 0; y < 3; y++) {
            float width = 1.5f - (y * 0.5f);
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (Math.sqrt(x * x + z * z) <= width + (random.nextFloat() * 0.5)) {
                        this.placeLog(level, blockSetter, random, pos.offset(x, y, z), config);
                    }
                }
            }
        }

        // 3. THE MAIN TRUNK
        for (int y = 0; y < height; y++) {
            this.placeLog(level, blockSetter, random, pos.above(y), config);
        }

        // Inside Section 4: GENERATE PROCEDURAL KNEES
        for (int i = 0; i < 4; i++) {
            int rx = random.nextInt(6) - 3;
            int rz = random.nextInt(6) - 3;
            if (Math.abs(rx) > 1 || Math.abs(rz) > 1) {
                // Look slightly up and down to find the surface for the knee
                BlockPos kneePos = pos.offset(rx, 0, rz);

                // If the current spot is water/air, try going down 1 to find mud
                if (level.isStateAtPosition(kneePos, state -> state.isAir() || state.is(Blocks.WATER))) {
                    // Logic to ensure they don't float
                }

                int kneeHeight = random.nextInt(2) + 1;
                for (int h = 0; h < kneeHeight; h++) {
                    this.placeLog(level, blockSetter, random, kneePos.above(h), config);
                }
            }
        }

        // 5. TOP BRANCHING
        return List.of(
                new FoliagePlacer.FoliageAttachment(pos.above(height), 2, false),
                new FoliagePlacer.FoliageAttachment(pos.above(height - 2).east(2), 1, false),
                new FoliagePlacer.FoliageAttachment(pos.above(height - 2).west(2), 1, false),
                new FoliagePlacer.FoliageAttachment(pos.above(height - 2).north(2), 1, false),
                new FoliagePlacer.FoliageAttachment(pos.above(height - 2).south(2), 1, false)
        );
    }
}