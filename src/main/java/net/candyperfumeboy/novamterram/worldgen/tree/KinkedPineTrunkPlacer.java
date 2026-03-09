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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;


public class KinkedPineTrunkPlacer extends TrunkPlacer {

    public static final MapCodec<KinkedPineTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> trunkPlacerParts(instance).apply(instance, KinkedPineTrunkPlacer::new)
    );

    public KinkedPineTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return NTTreePlacers.KINKED_PINE_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            RandomSource random,
            int freeTreeHeight,
            BlockPos pos,
            TreeConfiguration config
    ) {
        // --- STEP 1: DEEP GROUNDING ---
        // Sets dirt blocks in a 4x4 area underneath to prevent floating.
        for (int x = -1; x <= 2; x++) {
            for (int z = -1; z <= 2; z++) {
                setDirtAt(level, blockSetter, random, pos.offset(x, -1, z), config);
            }
        }

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        // --- STEP 2: MAIN TRUNK LOOP ---
        for (int i = 0; i < freeTreeHeight; i++) {

            // A: THE CORE (2x2 Pillar)
            // Places the main sturdy trunk.
            this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 0, i, 0);

            // TAPER LOGIC: The trunk becomes a 1x1 needle for the last 3 blocks.
            // This creates the "Pointy Top" seen in your Pagoda image.
            if (i < freeTreeHeight - 3) {
                this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 1, i, 0);
                this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 0, i, 1);
                this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 1, i, 1);
            }

            // B: THE ROOT FOUNDATION (Bottom 2 Blocks)
            // Creates the heavy 4x4 footprint.
            if (i < 2) {
                // North/South/East/West Extensions
                this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, -1, i, 0);
                this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, -1, i, 1);
                this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 2, i, 0);
                this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 2, i, 1);
                this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 0, i, -1);
                this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 1, i, -1);
                this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 0, i, 2);
                this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 1, i, 2);

                // Round Corners (Only at the very bottom, i=0)
                if (i == 0) {
                    this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, -1, i, -1);
                    this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 2, i, -1);
                    this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, -1, i, 2);
                    this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 2, i, 2);
                }
            }

            // C: SYMMETRICAL BUTTRESSES (Blocks 2 through 4)
            // *CHANGED*: Now checks pairs to create strong vertical ribs,
            // matching the structured "Pagoda" look.
            else if (i < 5) {
                float chance = 0.60f; // 60% chance for a root to extend up
                // West Side Buttress
                if (random.nextFloat() < chance) {
                    this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, -1, i, 0);
                    this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, -1, i, 1);
                }
                // East Side Buttress
                if (random.nextFloat() < chance) {
                    this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 2, i, 0);
                    this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 2, i, 1);
                }
                // North Side Buttress
                if (random.nextFloat() < chance) {
                    this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 0, i, -1);
                    this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 1, i, -1);
                }
                // South Side Buttress
                if (random.nextFloat() < chance) {
                    this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 0, i, 2);
                    this.placeLogIfFreeWithOffset(level, blockSetter, random, mutablePos, config, pos, 1, i, 2);
                }
            }
        }

        // --- STEP 3: ATTACHMENT ---
        return ImmutableList.of(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, true));
    }

    private void placeLogIfFreeWithOffset(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos.MutableBlockPos mutablePos, TreeConfiguration config, BlockPos origin, int offsetX, int offsetY, int offsetZ) {
        mutablePos.setWithOffset(origin, offsetX, offsetY, offsetZ);
        this.placeLogIfFree(level, blockSetter, random, mutablePos, config);
    }
}