package net.candyperfumeboy.novamterram.worldgen.biome.features;

import com.mojang.serialization.Codec;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.entities.custom.SilverFoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SilverFoxDen extends Feature<NoneFeatureConfiguration> {
    public SilverFoxDen(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        // Only build on dry soil (never on water, rock or tree tops)
        if (!level.getBlockState(pos.below()).is(BlockTags.DIRT) || !level.getFluidState(pos).isEmpty()) {
            return false;
        }

        // 1. Create the "Den" floor (Podzol/Coarse Dirt)
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.sqrt(x * x + z * z) <= 2.5) {
                    BlockPos floorPos = pos.offset(x, 0, z);

                    // We target the block exactly at 'pos' height and change the one below it
                    if (level.getBlockState(floorPos.below()).is(BlockTags.DIRT) || level.getBlockState(floorPos.below()).is(Blocks.GRASS_BLOCK)) {
                        BlockState state = random.nextFloat() > 0.3f ? Blocks.PODZOL.defaultBlockState() : Blocks.COARSE_DIRT.defaultBlockState();
                        level.setBlock(floorPos.below(), state, 2);
                    }
                }
            }
        }
// 1.5. Add the Sweet Berry Bush in the center
        BlockPos bushPos = pos; // The center of our den
        if (level.getBlockState(bushPos).isAir() && level.getBlockState(bushPos.below()).is(BlockTags.DIRT)) {
            // We set the age to 3 so it's a full bush with berries
            BlockState berryBush = Blocks.SWEET_BERRY_BUSH.defaultBlockState()
                    .setValue(net.minecraft.world.level.block.SweetBerryBushBlock.AGE, 3);
            level.setBlock(bushPos, berryBush, 2);
        }
        // 2. Spawn the Foxes
        // Beside the bush if there's room there, otherwise in the middle (so they aren't stuck in a block)
        BlockPos beside = pos.east();
        BlockPos foxPos = level.getBlockState(beside).getCollisionShape(level, beside).isEmpty() ? beside : pos;
        int foxCount = 1 + random.nextInt(2);
        for (int i = 0; i < foxCount; i++) {
            SilverFoxEntity fox = NTEntities.SILVER_FOX.get().create(level.getLevel());
            if (fox != null) {
                // Ensure they spawn on the SURFACE of our new den
                fox.moveTo(foxPos.getX() + 0.5, foxPos.getY(), foxPos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
                // Keep them from despawning
                fox.setPersistenceRequired();

                fox.setSleeping(true); // allowed by the access transformer

                level.addFreshEntity(fox);
            }
        }
        return true;
    }
}