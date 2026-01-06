package net.candyperfumeboy.novamterram.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NTHangingSignBlockEntity extends HangingSignBlockEntity {
    public NTHangingSignBlockEntity(BlockPos pos, BlockState state) {
        // We use NTSignBlockEntities.HANGING_SIGN.get() to link to your custom registry ID
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return NTSignBlockEntities.HANGING_SIGN.get();
    }
}