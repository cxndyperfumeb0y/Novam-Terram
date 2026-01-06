package net.candyperfumeboy.novamterram.worldgen.structure;

import com.mojang.serialization.MapCodec;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.worldgen.NTProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

public class BlueSpruceVillageProcessor extends StructureProcessor {
    public static final MapCodec<BlueSpruceVillageProcessor> CODEC = MapCodec.unit(() -> BlueSpruceVillageProcessor.INSTANCE);
    public static final BlueSpruceVillageProcessor INSTANCE = new BlueSpruceVillageProcessor();

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo nextBlockInfo, StructurePlaceSettings settings) {
        BlockState state = nextBlockInfo.state();
        BlockPos blockpos = nextBlockInfo.pos();

        // --- FIX FLOATING & DIRT PATCHES ---
        // 1. Remove grass/ferns that are in the way of the structure snapping to the ground
        if (state.is(Blocks.SHORT_GRASS) || state.is(Blocks.FERN)) {
            return new StructureTemplate.StructureBlockInfo(blockpos, Blocks.AIR.defaultBlockState(), nextBlockInfo.nbt());
        }
// 2. THE FOUNDATION COLUMN:
// If the block in the NBT is part of the floor (y <= 0)
        if (originalBlockInfo.pos().getY() <= 0) {
            // If the world currently has AIR here, we must fill it
            if (state.isAir()) {
                // We return DIRT to create a solid base
                return new StructureTemplate.StructureBlockInfo(blockpos, Blocks.GRASS_BLOCK.defaultBlockState(), nextBlockInfo.nbt());
            }
        }

        // --- SWAP LOGS ---
        if (state.is(Blocks.SPRUCE_LOG)) {
            BlockState newState = NTBlocks.BLUE_SPRUCE_LOG.get().defaultBlockState();
            if (state.hasProperty(RotatedPillarBlock.AXIS)) {
                newState = newState.setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
            }
            return new StructureTemplate.StructureBlockInfo(blockpos, newState, nextBlockInfo.nbt());
        }

        // --- SWAP PLANKS ---
        if (state.is(Blocks.SPRUCE_PLANKS)) {
            return new StructureTemplate.StructureBlockInfo(blockpos, NTBlocks.BLUE_SPRUCE_PLANKS.get().defaultBlockState(), nextBlockInfo.nbt());
        }

        // --- SWAP STAIRS ---
        if (state.is(Blocks.SPRUCE_STAIRS)) {
            BlockState newState = NTBlocks.BLUE_SPRUCE_STAIRS.get().defaultBlockState();
            if (state.hasProperty(StairBlock.FACING)) newState = newState.setValue(StairBlock.FACING, state.getValue(StairBlock.FACING));
            if (state.hasProperty(StairBlock.HALF)) newState = newState.setValue(StairBlock.HALF, state.getValue(StairBlock.HALF));
            if (state.hasProperty(StairBlock.SHAPE)) newState = newState.setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE));
            if (state.hasProperty(StairBlock.WATERLOGGED)) newState = newState.setValue(StairBlock.WATERLOGGED, state.getValue(StairBlock.WATERLOGGED));
            return new StructureTemplate.StructureBlockInfo(blockpos, newState, nextBlockInfo.nbt());
        }

        // --- SWAP SLABS ---
        if (state.is(Blocks.SPRUCE_SLAB)) {
            BlockState newState = NTBlocks.BLUE_SPRUCE_SLAB.get().defaultBlockState();
            if (state.hasProperty(SlabBlock.TYPE)) newState = newState.setValue(SlabBlock.TYPE, state.getValue(SlabBlock.TYPE));
            if (state.hasProperty(SlabBlock.WATERLOGGED)) newState = newState.setValue(SlabBlock.WATERLOGGED, state.getValue(SlabBlock.WATERLOGGED));
            return new StructureTemplate.StructureBlockInfo(blockpos, newState, nextBlockInfo.nbt());
        }

        // --- SWAP FENCES ---
        if (state.is(Blocks.SPRUCE_FENCE)) {
            BlockState newState = NTBlocks.BLUE_SPRUCE_FENCE.get().defaultBlockState();
            if (state.hasProperty(FenceBlock.WATERLOGGED)) newState = newState.setValue(FenceBlock.WATERLOGGED, state.getValue(FenceBlock.WATERLOGGED));
            if (state.hasProperty(FenceBlock.NORTH)) newState = newState.setValue(FenceBlock.NORTH, state.getValue(FenceBlock.NORTH));
            if (state.hasProperty(FenceBlock.SOUTH)) newState = newState.setValue(FenceBlock.SOUTH, state.getValue(FenceBlock.SOUTH));
            if (state.hasProperty(FenceBlock.EAST)) newState = newState.setValue(FenceBlock.EAST, state.getValue(FenceBlock.EAST));
            if (state.hasProperty(FenceBlock.WEST)) newState = newState.setValue(FenceBlock.WEST, state.getValue(FenceBlock.WEST));
            return new StructureTemplate.StructureBlockInfo(blockpos, newState, nextBlockInfo.nbt());
        }

        // --- SWAP DOORS ---
        if (state.is(Blocks.SPRUCE_DOOR)) {
            BlockState newState = NTBlocks.BLUE_SPRUCE_DOOR.get().defaultBlockState();
            if (state.hasProperty(DoorBlock.FACING)) newState = newState.setValue(DoorBlock.FACING, state.getValue(DoorBlock.FACING));
            if (state.hasProperty(DoorBlock.HALF)) newState = newState.setValue(DoorBlock.HALF, state.getValue(DoorBlock.HALF));
            if (state.hasProperty(DoorBlock.HINGE)) newState = newState.setValue(DoorBlock.HINGE, state.getValue(DoorBlock.HINGE));
            if (state.hasProperty(DoorBlock.OPEN)) newState = newState.setValue(DoorBlock.OPEN, state.getValue(DoorBlock.OPEN));
            return new StructureTemplate.StructureBlockInfo(blockpos, newState, nextBlockInfo.nbt());
        }

        // --- SWAP TRAPDOORS ---
        if (state.is(Blocks.SPRUCE_TRAPDOOR)) {
            BlockState newState = NTBlocks.BLUE_SPRUCE_TRAP_DOOR.get().defaultBlockState();
            if (state.hasProperty(TrapDoorBlock.FACING)) newState = newState.setValue(TrapDoorBlock.FACING, state.getValue(TrapDoorBlock.FACING));
            if (state.hasProperty(TrapDoorBlock.HALF)) newState = newState.setValue(TrapDoorBlock.HALF, state.getValue(TrapDoorBlock.HALF));
            if (state.hasProperty(TrapDoorBlock.OPEN)) newState = newState.setValue(TrapDoorBlock.OPEN, state.getValue(TrapDoorBlock.OPEN));
            if (state.hasProperty(TrapDoorBlock.WATERLOGGED)) newState = newState.setValue(TrapDoorBlock.WATERLOGGED, state.getValue(TrapDoorBlock.WATERLOGGED));
            return new StructureTemplate.StructureBlockInfo(blockpos, newState, nextBlockInfo.nbt());
        }

        // --- SWAP FENCE GATES ---
        if (state.is(Blocks.SPRUCE_FENCE_GATE)) {
            BlockState newState = NTBlocks.BLUE_SPRUCE_FENCE_GATE.get().defaultBlockState();
            if (state.hasProperty(FenceGateBlock.FACING)) newState = newState.setValue(FenceGateBlock.FACING, state.getValue(FenceGateBlock.FACING));
            if (state.hasProperty(FenceGateBlock.OPEN)) newState = newState.setValue(FenceGateBlock.OPEN, state.getValue(FenceGateBlock.OPEN));
            if (state.hasProperty(FenceGateBlock.IN_WALL)) newState = newState.setValue(FenceGateBlock.IN_WALL, state.getValue(FenceGateBlock.IN_WALL));
            return new StructureTemplate.StructureBlockInfo(blockpos, newState, nextBlockInfo.nbt());
        }

        return nextBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return NTProcessors.BLUE_SPRUCE_PROCESSOR_TYPE.get();
    }
}