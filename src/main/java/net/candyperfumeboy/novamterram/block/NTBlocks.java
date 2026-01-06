package net.candyperfumeboy.novamterram.block;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.entities.NTSignBlock;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.candyperfumeboy.novamterram.worldgen.tree.NTTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class NTBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NovamTerram.MOD_ID);

    // ==========================================
    // GEMS & ORES
    // ==========================================
    public static final DeferredBlock<Block> OPAL_BLOCK = registerBlock("opal_block", () ->
            new AuraBlock(BlockBehaviour.Properties.of().strength(4f).lightLevel(state -> 5).requiresCorrectToolForDrops().randomTicks().sound(SoundType.AMETHYST)));

    public static final DeferredBlock<Block> OPAL_ORE = registerBlock("opal_ore", () ->
            new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> LIGNITE_BLOCK = registerBlock("lignite_block", () ->
            new Block(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> LIGNITE_ORE = registerBlock("lignite_ore", () ->
            new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> LIGNITE_NETHER_ORE = registerBlock("lignite_nether_ore", () ->
            new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.NETHER_ORE)));

    public static final DeferredBlock<Block> LIGNITE_END_ORE = registerBlock("lignite_end_ore", () ->
            new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final DeferredBlock<Block> DEEPSLATE_LIGNITE_ORE = registerBlock("deepslate_lignite_ore", () ->
            new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));

    // ==========================================
    // BLUE SPRUCE WOOD SET (Custom WoodType)
    // ==========================================
    public static final DeferredBlock<Block> BLUE_SPRUCE_LOG = registerBlock("blue_spruce_log",
            () -> new NTFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LOG)));

    public static final DeferredBlock<Block> BLUE_SPRUCE_WOOD = registerBlock("blue_spruce_wood",
            () -> new NTFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WOOD)));

    public static final DeferredBlock<Block> STRIPPED_BLUE_SPRUCE_LOG = registerBlock("stripped_blue_spruce_log",
            () -> new NTFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_SPRUCE_LOG)));

    public static final DeferredBlock<Block> STRIPPED_BLUE_SPRUCE_WOOD = registerBlock("stripped_blue_spruce_wood",
            () -> new NTFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_SPRUCE_WOOD)));

    public static final DeferredBlock<Block> BLUE_SPRUCE_PLANKS = registerBlock("blue_spruce_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS)) {
                @Override public boolean isFlammable(BlockState s, BlockGetter l, BlockPos p, Direction d) { return true; }
                @Override public int getFlammability(BlockState s, BlockGetter l, BlockPos p, Direction d) { return 20; }
                @Override public int getFireSpreadSpeed(BlockState s, BlockGetter l, BlockPos p, Direction d) { return 5; }
            });

    public static final DeferredBlock<Block> BLUE_SPRUCE_LEAVES = registerBlock("blue_spruce_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LEAVES)) {
                @Override public boolean isFlammable(BlockState s, BlockGetter l, BlockPos p, Direction d) { return true; }
                @Override public int getFlammability(BlockState s, BlockGetter l, BlockPos p, Direction d) { return 60; }
                @Override public int getFireSpreadSpeed(BlockState s, BlockGetter l, BlockPos p, Direction d) { return 30; }
            });

    public static final DeferredBlock<Block> BLUE_SPRUCE_SAPLING = registerBlock("blue_spruce_sapling",
            () -> new SaplingBlock(NTTreeGrowers.BLUESPRUCE_TAIGA_KEY, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SAPLING)));

    public static final DeferredBlock<StairBlock> BLUE_SPRUCE_STAIRS = registerBlock("blue_spruce_stairs",
            () -> new StairBlock(NTBlocks.BLUE_SPRUCE_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_STAIRS)));

    public static final DeferredBlock<SlabBlock> BLUE_SPRUCE_SLAB = registerBlock("blue_spruce_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SLAB)));

    public static final DeferredBlock<PressurePlateBlock> BLUE_SPRUCE_PRESSURE_PLATE = registerBlock("blue_spruce_pressure_plate",
            () -> new PressurePlateBlock(NTWoodTypes.BLUE_SPRUCE_BLOCK_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PRESSURE_PLATE)));

    public static final DeferredBlock<ButtonBlock> BLUE_SPRUCE_BUTTON = registerBlock("blue_spruce_button",
            () -> new ButtonBlock(NTWoodTypes.BLUE_SPRUCE_BLOCK_SET, 20, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_BUTTON)));

    public static final DeferredBlock<FenceBlock> BLUE_SPRUCE_FENCE = registerBlock("blue_spruce_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_FENCE)));

    public static final DeferredBlock<FenceGateBlock> BLUE_SPRUCE_FENCE_GATE = registerBlock("blue_spruce_gate",
            () -> new FenceGateBlock(NTWoodTypes.BLUE_SPRUCE, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_FENCE_GATE)));

    public static final DeferredBlock<DoorBlock> BLUE_SPRUCE_DOOR = registerBlock("blue_spruce_door",
            () -> new DoorBlock(NTWoodTypes.BLUE_SPRUCE_BLOCK_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_DOOR).noOcclusion()));

    public static final DeferredBlock<TrapDoorBlock> BLUE_SPRUCE_TRAP_DOOR = registerBlock("blue_spruce_trap_door",
            () -> new TrapDoorBlock(NTWoodTypes.BLUE_SPRUCE_BLOCK_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_TRAPDOOR).noOcclusion()));

    // Signs use the NTSignBlock classes to fix the BlockEntityType crash
    public static final DeferredBlock<Block> BLUE_SPRUCE_SIGN = BLOCKS.register("blue_spruce_sign",
            () -> new NTSignBlock.Standing(NTWoodTypes.BLUE_SPRUCE, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SIGN)));

    public static final DeferredBlock<Block> BLUE_SPRUCE_WALL_SIGN = BLOCKS.register("blue_spruce_wall_sign",
            () -> new NTSignBlock.Wall(NTWoodTypes.BLUE_SPRUCE, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_SIGN)));

    public static final DeferredBlock<Block> BLUE_SPRUCE_HANGING_SIGN = BLOCKS.register("blue_spruce_hanging_sign",
            () -> new NTSignBlock.Hanging(NTWoodTypes.BLUE_SPRUCE, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_HANGING_SIGN)));

    public static final DeferredBlock<Block> BLUE_SPRUCE_WALL_HANGING_SIGN = BLOCKS.register("blue_spruce_wall_hanging_sign",
            () -> new NTSignBlock.WallHanging(NTWoodTypes.BLUE_SPRUCE, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WALL_HANGING_SIGN)));

    // ==========================================
    // SANDS & OLIVINE SANDSTONE SET
    // ==========================================
    public static final DeferredBlock<Block> OLIVINE_SAND = registerBlock("olivine_sand", () ->
            new ColoredFallingBlock(new ColorRGBA(-8356741), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).sound(SoundType.SAND)));

    public static final DeferredBlock<Block> OLIVINE_SANDSTONE = registerBlock("olivine_sandstone", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).sound(SoundType.STONE)));

    public static final DeferredBlock<Block> CUT_OLIVINE_SANDSTONE = registerBlock("cut_olivine_sandstone", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CUT_SANDSTONE).sound(SoundType.STONE)));

    public static final DeferredBlock<Block> CHISELED_OLIVINE_SANDSTONE = registerBlock("chiseled_olivine_sandstone", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_SANDSTONE).sound(SoundType.STONE)));

    public static final DeferredBlock<StairBlock> OLIVINE_SANDSTONE_STAIRS = registerBlock("olivine_sandstone_stairs",
            () -> new StairBlock(NTBlocks.OLIVINE_SANDSTONE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_STAIRS)));

    public static final DeferredBlock<SlabBlock> OLIVINE_SANDSTONE_SLAB = registerBlock("olivine_sandstone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_SLAB)));

    public static final DeferredBlock<PressurePlateBlock> OLIVINE_SANDSTONE_PRESSURE_PLATE = registerBlock("olivine_sandstone_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE)));

    public static final DeferredBlock<ButtonBlock> OLIVINE_SANDSTONE_BUTTON = registerBlock("olivine_sandstone_button",
            () -> new ButtonBlock(BlockSetType.STONE, 20, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BUTTON)));

    public static final DeferredBlock<WallBlock> OLIVINE_SANDSTONE_WALL = registerBlock("olivine_sandstone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_WALL)));

    public static final DeferredBlock<FenceBlock> OLIVINE_SANDSTONE_FENCE = registerBlock("olivine_sandstone_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_WALL)));

    public static final DeferredBlock<FenceGateBlock> OLIVINE_SANDSTONE_FENCE_GATE = registerBlock("olivine_sandstone_gate",
            () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_FENCE_GATE).sound(SoundType.STONE)));

    public static final DeferredBlock<DoorBlock> OLIVINE_SANDSTONE_DOOR = registerBlock("olivine_sandstone_door",
            () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).noOcclusion()));

    public static final DeferredBlock<TrapDoorBlock> OLIVINE_SANDSTONE_TRAP_DOOR = registerBlock("olivine_sandstone_trap_door",
            () -> new TrapDoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).noOcclusion()));

    // ==========================================
    // SOILS
    // ==========================================
    public static final DeferredBlock<Block> BLUE_PODZOL = registerBlock("blue_podzol", () ->
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.PODZOL).sound(SoundType.ROOTED_DIRT)));

    // ==========================================
    // HELPERS & REGISTRATION
    // ==========================================
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        NTItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}