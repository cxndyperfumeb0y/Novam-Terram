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
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;

public class BlueSpruceVillageProcessor extends StructureProcessor {
    public static final MapCodec<BlueSpruceVillageProcessor> CODEC = MapCodec.unit(() -> BlueSpruceVillageProcessor.INSTANCE);
    public static final BlueSpruceVillageProcessor INSTANCE = new BlueSpruceVillageProcessor();

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo nextBlockInfo, StructurePlaceSettings settings) {
        BlockState state = nextBlockInfo.state();
        BlockPos blockpos = nextBlockInfo.pos();

        // 1. Clear foliage in the way
        if (state.is(Blocks.SHORT_GRASS) || state.is(Blocks.FERN)) {
            return new StructureTemplate.StructureBlockInfo(blockpos, Blocks.AIR.defaultBlockState(), nextBlockInfo.nbt());
        }

        // 2. Foundation logic
        if (originalBlockInfo.pos().getY() <= 0) {
            if (state.isAir()) {
                return new StructureTemplate.StructureBlockInfo(blockpos, Blocks.GRASS_BLOCK.defaultBlockState(), nextBlockInfo.nbt());
            }
        }

        // --- SWAP LOGS ---
        if (state.is(Blocks.SPRUCE_LOG)) {
            return new StructureTemplate.StructureBlockInfo(blockpos,
                    copyProperties(state, getBlock("blue_spruce_log").defaultBlockState()), nextBlockInfo.nbt());
        }

        // --- SWAP PLANKS ---
        if (state.is(Blocks.SPRUCE_PLANKS)) {
            return new StructureTemplate.StructureBlockInfo(blockpos,
                    getBlock("blue_spruce_planks").defaultBlockState(), nextBlockInfo.nbt());
        }

        // --- SWAP STAIRS ---
        if (state.is(Blocks.SPRUCE_STAIRS)) {
            return new StructureTemplate.StructureBlockInfo(blockpos,
                    copyProperties(state, getBlock("blue_spruce_stairs").defaultBlockState()), nextBlockInfo.nbt());
        }

        // --- SWAP SLABS ---
        if (state.is(Blocks.SPRUCE_SLAB)) {
            return new StructureTemplate.StructureBlockInfo(blockpos,
                    copyProperties(state, getBlock("blue_spruce_slab").defaultBlockState()), nextBlockInfo.nbt());
        }

        // --- SWAP FENCES ---
        if (state.is(Blocks.SPRUCE_FENCE)) {
            return new StructureTemplate.StructureBlockInfo(blockpos,
                    copyProperties(state, getBlock("blue_spruce_fence").defaultBlockState()), nextBlockInfo.nbt());
        }

        // --- SWAP DOORS ---
        if (state.is(Blocks.SPRUCE_DOOR)) {
            return new StructureTemplate.StructureBlockInfo(blockpos,
                    copyProperties(state, getBlock("blue_spruce_door").defaultBlockState()), nextBlockInfo.nbt());
        }

        // --- SWAP TRAPDOORS ---
        if (state.is(Blocks.SPRUCE_TRAPDOOR)) {
            return new StructureTemplate.StructureBlockInfo(blockpos,
                    copyProperties(state, getBlock("blue_spruce_trapdoor").defaultBlockState()), nextBlockInfo.nbt());
        }

        // --- SWAP FENCE GATES ---
        if (state.is(Blocks.SPRUCE_FENCE_GATE)) {
            return new StructureTemplate.StructureBlockInfo(blockpos,
                    copyProperties(state, getBlock("blue_spruce_fence_gate").defaultBlockState()), nextBlockInfo.nbt());
        }

        return nextBlockInfo;
    }

    // Helper to fetch blocks from the registry loop
    private Block getBlock(String name) {
        return NTBlocks.BLOCKS.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(name))
                .findFirst()
                .map(DeferredHolder::get)
                .orElseThrow(() -> new RuntimeException("Block not found for StructureProcessor: " + name));
    }

    // Clean way to copy all vanilla properties (facing, axis, waterlogged, etc) to the new block
    private BlockState copyProperties(BlockState oldState, BlockState newState) {
        BlockState result = newState;
        for (net.minecraft.world.level.block.state.properties.Property<?> property : oldState.getProperties()) {
            if (result.hasProperty(property)) {
                result = copyProperty(oldState, result, property);
            }
        }
        return result;
    }

    private <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, net.minecraft.world.level.block.state.properties.Property<T> property) {
        return to.setValue(property, from.getValue(property));
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return NTProcessors.BLUE_SPRUCE_PROCESSOR_TYPE.get();
    }
}