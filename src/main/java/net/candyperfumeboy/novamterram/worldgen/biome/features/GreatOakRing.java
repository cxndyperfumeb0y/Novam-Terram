package net.candyperfumeboy.novamterram.worldgen.biome.features;

import com.mojang.serialization.Codec;
import net.candyperfumeboy.novamterram.worldgen.NTConfiguredFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** An ancient oak with a fairy ring of mushrooms around it. No oak, no ring. */
public class GreatOakRing extends Feature<NoneFeatureConfiguration> {
    private static final double RADIUS = 6;
    private static final int SPOTS = 30;

    public GreatOakRing(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos centre = context.origin();

        boolean grew = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getOrThrow(NTConfiguredFeatures.ANCIENT_OAK)
                .place(level, context.chunkGenerator(), random, centre);
        if (!grew) {
            return false;
        }

        for (int i = 0; i < SPOTS; i++) {
            float angle = Mth.TWO_PI * i / SPOTS;
            int x = centre.getX() + (int) Math.round(Math.cos(angle) * RADIUS);
            int z = centre.getZ() + (int) Math.round(Math.sin(angle) * RADIUS);
            BlockPos pos = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);

            // Skip cliffs, water, rocks, and a few spots so the ring looks natural
            if (Math.abs(pos.getY() - centre.getY()) > 3
                    || !level.isEmptyBlock(pos)
                    || !level.getBlockState(pos.below()).is(BlockTags.DIRT)
                    || random.nextInt(6) == 0) {
                continue;
            }

            Block mushroom = random.nextInt(5) < 3 ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM;
            level.setBlock(pos.below(), Blocks.PODZOL.defaultBlockState(), 2);
            level.setBlock(pos, mushroom.defaultBlockState(), 2);
        }
        return true;
    }
}
