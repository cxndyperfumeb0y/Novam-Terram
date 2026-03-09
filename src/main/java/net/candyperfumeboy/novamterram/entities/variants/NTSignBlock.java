package net.candyperfumeboy.novamterram.entities.variants;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class NTSignBlock {
    public static class Standing extends StandingSignBlock {
        public Standing(WoodType type, Properties props) { super(type, props); }
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new NTSignBlockEntity(pos, state); // Use your custom class
        }
    }

    public static class Wall extends WallSignBlock {
        public Wall(WoodType type, Properties props) { super(type, props); }
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new NTSignBlockEntity(pos, state); // Use your custom class
        }
    }

    public static class Hanging extends CeilingHangingSignBlock {
        public Hanging(WoodType type, Properties props) { super(type, props); }
        @Override
        public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
            return new NTHangingSignBlockEntity(p, s); // You need this class!
        }
    }

    public static class WallHanging extends WallHangingSignBlock {
        public WallHanging(WoodType type, Properties props) { super(type, props); }
        @Override
        public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
            return new NTHangingSignBlockEntity(p, s); // You need this class!
        }
    }
}