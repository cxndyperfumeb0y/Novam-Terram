package net.candyperfumeboy.novamterram.block.custom;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;

public class NTFlammableRotatedPillarBlock extends RotatedPillarBlock {
    public NTFlammableRotatedPillarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context,
                                                     ItemAbility itemAbility, boolean simulate) {
        // Only trigger if an Axe is used for "Stripping"
        if (itemAbility == net.neoforged.neoforge.common.ItemAbilities.AXE_STRIP) {
            String currentPath = state.getBlock().asItem().toString().replace(NovamTerram.MOD_ID + ":", "");

            // Logic: if current block is "blue_spruce_log", look for "stripped_blue_spruce_log"
            String strippedPath = "stripped_" + currentPath;

            return NTBlocks.BLOCKS.getEntries().stream()
                    .filter(e -> e.getId().getPath().equals(strippedPath))
                    .findFirst()
                    .map(holder -> holder.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS)))
                    .orElse(super.getToolModifiedState(state, context, itemAbility, simulate));
        }

        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}