package net.candyperfumeboy.novamterram.item.custom;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class OliveItem extends Item {
    public OliveItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Direction facing = context.getClickedFace();

        // Check if player clicked the BOTTOM of an Olive Leaf block
        if (facing == Direction.DOWN && state.is(NTBlocks.OLIVE_LEAVES.get())) {
            BlockPos placePos = pos.below();

            // If the space below is empty, plant the olive
            if (level.isEmptyBlock(placePos)) {
                level.setBlockAndUpdate(placePos, NTBlocks.OLIVE_HANGING.get().defaultBlockState());

                // Consume the item if not in creative
                if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useOn(context);
    }
}