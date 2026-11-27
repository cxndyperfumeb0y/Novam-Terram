package net.candyperfumeboy.novamterram.entities.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** What fauns know about flowers: crown colours, and what is worth showing a friend. */
final class FaunFlowers {

    static final int DEFAULT_COLOR = 0xFFF08CB4; // pink

    private FaunFlowers() {
    }

    static boolean isTasty(BlockState state) {
        return state.is(BlockTags.SMALL_FLOWERS) && !state.is(Blocks.WITHER_ROSE);
    }

    static int crownColor(BlockState flower) {
        if (flower.is(Blocks.POPPY) || flower.is(Blocks.RED_TULIP)) return 0xFFDE3630;
        if (flower.is(Blocks.DANDELION)) return 0xFFF6D63C;
        if (flower.is(Blocks.ORANGE_TULIP) || flower.is(Blocks.TORCHFLOWER)) return 0xFFF08C28;
        if (flower.is(Blocks.CORNFLOWER)) return 0xFF506EE6;
        if (flower.is(Blocks.BLUE_ORCHID)) return 0xFF46BEF0;
        if (flower.is(Blocks.ALLIUM)) return 0xFFBE64E6;
        if (flower.is(Blocks.AZURE_BLUET) || flower.is(Blocks.OXEYE_DAISY)
                || flower.is(Blocks.WHITE_TULIP) || flower.is(Blocks.LILY_OF_THE_VALLEY)) return 0xFFF4F4EC;
        return DEFAULT_COLOR; // pink tulips and any other flower
    }

    /** One hive, mushroom or flower spot between 10 and 32 blocks away, or null if there is none. */
    @Nullable
    static BlockPos findSomethingToShow(Level level, BlockPos origin, net.minecraft.util.RandomSource random) {
        BlockPos[] nearest = new BlockPos[3]; // hive, mushroom, flower
        double[] best = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};

        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-32, -8, -32), origin.offset(32, 8, 32))) {
            double dx = pos.getX() - origin.getX();
            double dz = pos.getZ() - origin.getZ();
            double distance = dx * dx + dz * dz;
            if (distance < 100.0D || distance > 1024.0D) continue;

            BlockState state = level.getBlockState(pos);
            int kind;
            if (state.is(BlockTags.BEEHIVES)) kind = 0;
            else if (state.is(Blocks.BROWN_MUSHROOM) || state.is(Blocks.RED_MUSHROOM)) kind = 1;
            else if (isTasty(state) || state.is(BlockTags.TALL_FLOWERS)) kind = 2;
            else continue;

            if (distance < best[kind]) {
                best[kind] = distance;
                nearest[kind] = pos.immutable();
            }
        }

        List<BlockPos> found = new ArrayList<>();
        for (BlockPos pos : nearest) {
            if (pos != null) found.add(pos);
        }
        return found.isEmpty() ? null : found.get(random.nextInt(found.size()));
    }
}
