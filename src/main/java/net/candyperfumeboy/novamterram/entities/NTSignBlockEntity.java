package net.candyperfumeboy.novamterram.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NTSignBlockEntity extends SignBlockEntity {
    public NTSignBlockEntity(BlockPos pos, BlockState state) {
        // This 'SIGN.get()' is the magic part that connects to your registry
        super(NTSignBlockEntities.SIGN.get(), pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return NTSignBlockEntities.SIGN.get();
    }
}