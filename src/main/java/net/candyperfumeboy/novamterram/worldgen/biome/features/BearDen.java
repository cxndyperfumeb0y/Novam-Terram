package net.candyperfumeboy.novamterram.worldgen.biome.features;

import com.mojang.serialization.Codec;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.entities.custom.SpectacledBearEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BearDen extends Feature<NoneFeatureConfiguration> {
    public BearDen(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        // 1. Scan for a "Back Wall" - we want this to spawn ON a cliffside
        if (!level.getBlockState(pos.west()).isSolidRender(level, pos.west())) {
            return false; // Only spawn if there's a mountain wall behind it
        }

        // 2. Carve the cave and set the floor to Andesite
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = 0; z <= 2; z++) {
                    BlockPos relativePos = pos.offset(x, y, -z);

                    // The Floor
                    // Solid floor under the room (the bottom layer), then clear the room itself
                    if (y == 0) level.setBlock(relativePos.below(), Blocks.ANDESITE.defaultBlockState(), 2);
                    level.setBlock(relativePos, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }

        // 3. Guarantee the Spectacled Bear
        SpectacledBearEntity bear = NTEntities.SPECTACLED_BEAR.get().create(level.getLevel());
        if (bear != null) {
            bear.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() - 0.5, random.nextFloat() * 360.0F, 0.0F);
            bear.setPersistenceRequired();

            bear.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), net.minecraft.world.entity.MobSpawnType.CHUNK_GENERATION, null);
            level.addFreshEntity(bear);
        }

        return true;
    }
}