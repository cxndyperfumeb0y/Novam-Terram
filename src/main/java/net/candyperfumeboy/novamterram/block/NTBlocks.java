package net.candyperfumeboy.novamterram.block;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.custom.AuraBlock;
import net.candyperfumeboy.novamterram.block.custom.NTFlammableRotatedPillarBlock;
import net.candyperfumeboy.novamterram.block.custom.NTWoodTypes;
import net.candyperfumeboy.novamterram.block.custom.OliveHangingBlock;
import net.candyperfumeboy.novamterram.entities.variants.NTSignBlock;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.candyperfumeboy.novamterram.worldgen.tree.NTTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class NTBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NovamTerram.MOD_ID);
    private static final List<WoodGroup> WOOD_GROUPS = new ArrayList<>();

    // ==========================================
    // GEMS & ORES
    // ==========================================
    public static final DeferredBlock<Block> OPAL_BLOCK = registerBlock("opal_block", () -> new AuraBlock(BlockBehaviour.Properties.of().strength(4f).lightLevel(state -> 5).requiresCorrectToolForDrops().randomTicks().sound(SoundType.AMETHYST)));
    public static final DeferredBlock<Block> OPAL_ORE = registerBlock("opal_ore", () -> new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> IOLITE_BLOCK = registerBlock("iolite_block", () -> new AuraBlock(BlockBehaviour.Properties.of().strength(4f).lightLevel(state -> 5).requiresCorrectToolForDrops().randomTicks().sound(SoundType.AMETHYST)));
    public static final DeferredBlock<Block> IOLITE_ORE = registerBlock("iolite_ore", () -> new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> KYANITE_BLOCK = registerBlock("kyanite_block", () -> new AuraBlock(BlockBehaviour.Properties.of().strength(4f).lightLevel(state -> 5).requiresCorrectToolForDrops().randomTicks().sound(SoundType.AMETHYST)));
    public static final DeferredBlock<Block> KYANITE_ORE = registerBlock("kyanite_ore", () -> new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> JADEITE_BLOCK = registerBlock("jadeite_block", () -> new AuraBlock(BlockBehaviour.Properties.of().strength(4f).lightLevel(state -> 5).requiresCorrectToolForDrops().randomTicks().sound(SoundType.AMETHYST)));
    public static final DeferredBlock<Block> JADEITE_ORE = registerBlock("jadeite_ore", () -> new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> RHODOCHROSITE_BLOCK = registerBlock("rhodochrosite_block", () -> new AuraBlock(BlockBehaviour.Properties.of().strength(4f).lightLevel(state -> 5).requiresCorrectToolForDrops().randomTicks().sound(SoundType.AMETHYST)));
    public static final DeferredBlock<Block> RHODOCHROSITE_ORE = registerBlock("rhodochrosite_ore", () -> new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> LIGNITE_BLOCK = registerBlock("lignite_block", () -> new Block(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> LIGNITE_ORE = registerBlock("lignite_ore", () -> new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> LIGNITE_NETHER_ORE = registerBlock("lignite_nether_ore", () -> new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.NETHER_ORE)));
    public static final DeferredBlock<Block> LIGNITE_END_ORE = registerBlock("lignite_end_ore", () -> new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> DEEPSLATE_LIGNITE_ORE = registerBlock("deepslate_lignite_ore", () -> new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));

    // ==========================================
    // HIGHLANDS & TAIGA FLORA
    // ==========================================
    // Autumnal Pine
    public static final DeferredBlock<Block> ORANGE_AUTUMNAL_LEAVES = registerBlock("orange_autumnal_leaves", () -> new NTParticleLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LEAVES)));
    public static final DeferredBlock<Block> YELLOW_AUTUMNAL_LEAVES = registerBlock("yellow_autumnal_leaves", () -> new NTParticleLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LEAVES)));
    public static final DeferredBlock<Block> ORANGE_AUTUMNAL_PINE_SAPLING = registerBlock("orange_autumnal_pine_sapling", () -> new SaplingBlock(NTTreeGrowers.ORANGE_AUTUMNAL_PINE, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SAPLING)));
    public static final DeferredBlock<Block> YELLOW_AUTUMNAL_PINE_SAPLING = registerBlock("yellow_autumnal_pine_sapling", () -> new SaplingBlock(NTTreeGrowers.YELLOW_AUTUMNAL_PINE, BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SAPLING)));
    public static final DeferredBlock<Block> ORANGE_AUTUMNAL_LEAF_CARPET = registerBlock("orange_autumnal_leaf_carpet", () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS).mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<Block> YELLOW_AUTUMNAL_LEAF_CARPET = registerBlock("yellow_autumnal_leaf_carpet", () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS).mapColor(MapColor.COLOR_YELLOW)));

    // Paulownia
    public static final DeferredBlock<Block> PAULOWNIA_LEAVES = registerBlock("paulownia_leaves", () -> new NTParticleLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)));
    public static final DeferredBlock<Block> PAULOWNIA_SAPLING = registerBlock("paulownia_sapling", () -> new SaplingBlock(NTTreeGrowers.PAULOWNIA, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final DeferredBlock<Block> PAULOWNIA_LEAF_CARPET = registerBlock("paulownia_leaf_carpet", () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS).mapColor(MapColor.COLOR_PURPLE)));

    // Myrtle
    // 1. WHITE MYRTLE
    public static final DeferredBlock<Block> WHITE_MYRTLE_LEAVES = registerBlock("white_myrtle_leaves",
            () -> new NTParticleLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LEAVES)));
    public static final DeferredBlock<Block> WHITE_MYRTLE_SAPLING = registerBlock("white_myrtle_sapling",
            () -> new SaplingBlock(NTTreeGrowers.WHITE_MYRTLE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));

    // 2. RED MYRTLE
    public static final DeferredBlock<Block> RED_MYRTLE_LEAVES = registerBlock("red_myrtle_leaves",
            () -> new NTParticleLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LEAVES)));
    public static final DeferredBlock<Block> RED_MYRTLE_SAPLING = registerBlock("red_myrtle_sapling",
            () -> new SaplingBlock(NTTreeGrowers.RED_MYRTLE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));

    // 3. PURPLE MYRTLE
    public static final DeferredBlock<Block> PURPLE_MYRTLE_LEAVES = registerBlock("purple_myrtle_leaves",
            () -> new NTParticleLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LEAVES)));
    public static final DeferredBlock<Block> PURPLE_MYRTLE_SAPLING = registerBlock("purple_myrtle_sapling",
            () -> new SaplingBlock(NTTreeGrowers.PURPLE_MYRTLE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));public static final DeferredBlock<Block> MYRTLE_LEAF_CARPET = registerBlock("myrtle_leaf_carpet", () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS).mapColor(MapColor.PLANT)));

    // ==========================================
    // FLOWERS & PETALS
    // ==========================================
    public static final DeferredBlock<Block> HEATHER = registerBlock("heather", () -> new FlowerBlock(MobEffects.REGENERATION, 5, BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).noCollission().instabreak()));
    public static final DeferredBlock<Block> HEATHER_PETALS = registerBlock("heather_petals", () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS)));
    public static final DeferredBlock<Block> LAVENDER = registerBlock("lavender", () -> new FlowerBlock(MobEffects.REGENERATION, 5, BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).noCollission().instabreak()));
    public static final DeferredBlock<Block> LAVENDER_PETALS = registerBlock("lavender_petals", () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS)));
    public static final DeferredBlock<Block> ASTER = registerBlock("aster", () -> new FlowerBlock(MobEffects.REGENERATION, 5, BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).noCollission().instabreak()));
    public static final DeferredBlock<Block> ASTER_PETALS = registerBlock("aster_petals", () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS)));
    public static final DeferredBlock<Block> TRILLIUM = registerBlock("trillium", () -> new FlowerBlock(MobEffects.REGENERATION, 5, BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).noCollission().instabreak()));
    public static final DeferredBlock<Block> THISTLE = registerBlock("thistle", () -> new FlowerBlock(MobEffects.REGENERATION, 5, BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).noCollission().instabreak()));
    public static final DeferredBlock<Block> BROMELIAD = registerBlock("bromeliad", () -> new FlowerBlock(MobEffects.REGENERATION, 5, BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).noCollission().instabreak()));
    public static final DeferredBlock<Block> OLIVE_HANGING = BLOCKS.register("olive_hanging",
            () -> new OliveHangingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COCOA)
                    .noCollission()
                    .instabreak()
                    .randomTicks()));

    public static final DeferredBlock<Block> OLIVE_LEAVES = registerBlock("olive_leaves",
            () -> new OliveLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                    .randomTicks())); // Ensure randomTicks is enabled in properties



    public static final DeferredBlock<Block> SILVER_SAGE = registerBlock("silver_sage",
            () -> new FlowerBlock(MobEffects.ABSORPTION, 5, BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).noCollission().instabreak()));

    public static final DeferredBlock<Block> BLEEDING_HEART = registerBlock("bleeding_heart",
            () -> new FlowerBlock(MobEffects.REGENERATION, 5, BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION).noCollission().instabreak()));

    public static final DeferredBlock<Block> IRIS = registerBlock("iris",
            () -> new TallFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PEONY).noCollission().instabreak()));

    public static final DeferredBlock<Block> BLUEBELL = registerBlock("bluebell", () -> new FlowerBlock(MobEffects.REGENERATION, 5, BlockBehaviour.Properties.ofFullCopy(Blocks.CORNFLOWER).noCollission().instabreak()));
    public static final DeferredBlock<Block> PURPLE_BLUEBELL = registerBlock("purple_bluebell", () -> new FlowerBlock(MobEffects.REGENERATION, 5, BlockBehaviour.Properties.ofFullCopy(Blocks.CORNFLOWER).noCollission().instabreak()));


    // ==========================================
    // SANDS, SOILS & STONE
    // ==========================================
    public static final DeferredBlock<Block> OLIVINE_SAND = registerBlock("olivine_sand", () -> new ColoredFallingBlock(new ColorRGBA(-8356741), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).sound(SoundType.SAND)));
    public static final DeferredBlock<Block> OLIVINE_SANDSTONE = registerBlock("olivine_sandstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> CUT_OLIVINE_SANDSTONE = registerBlock("cut_olivine_sandstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CUT_SANDSTONE).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> CHISELED_OLIVINE_SANDSTONE = registerBlock("chiseled_olivine_sandstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_SANDSTONE).sound(SoundType.STONE)));
    public static final DeferredBlock<StairBlock> OLIVINE_SANDSTONE_STAIRS = registerBlock("olivine_sandstone_stairs", () -> new StairBlock(OLIVINE_SANDSTONE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_STAIRS)));
    public static final DeferredBlock<SlabBlock> OLIVINE_SANDSTONE_SLAB = registerBlock("olivine_sandstone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_SLAB)));
    public static final DeferredBlock<PressurePlateBlock> OLIVINE_SANDSTONE_PRESSURE_PLATE = registerBlock("olivine_sandstone_pressure_plate", () -> new PressurePlateBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE)));
    public static final DeferredBlock<ButtonBlock> OLIVINE_SANDSTONE_BUTTON = registerBlock("olivine_sandstone_button", () -> new ButtonBlock(BlockSetType.STONE, 20, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BUTTON)));
    public static final DeferredBlock<WallBlock> OLIVINE_SANDSTONE_WALL = registerBlock("olivine_sandstone_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_WALL)));
    public static final DeferredBlock<FenceBlock> OLIVINE_SANDSTONE_FENCE = registerBlock("olivine_sandstone_fence", () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_WALL)));
    public static final DeferredBlock<FenceGateBlock> OLIVINE_SANDSTONE_FENCE_GATE = registerBlock("olivine_sandstone_gate", () -> new FenceGateBlock(WoodType.ACACIA, BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_FENCE_GATE).sound(SoundType.STONE)));
    public static final DeferredBlock<DoorBlock> OLIVINE_SANDSTONE_DOOR = registerBlock("olivine_sandstone_door", () -> new DoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).noOcclusion()));
    public static final DeferredBlock<TrapDoorBlock> OLIVINE_SANDSTONE_TRAP_DOOR = registerBlock("olivine_sandstone_trap_door", () -> new TrapDoorBlock(BlockSetType.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).noOcclusion()));
    public static final DeferredBlock<Block> BLUE_PODZOL = registerBlock("blue_podzol", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.PODZOL).sound(SoundType.ROOTED_DIRT)));
    public static final DeferredBlock<Block> WHITE_SAND = registerBlock("white_sand", () -> new ColoredFallingBlock(new ColorRGBA(0xFFFFFF), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).sound(SoundType.SAND)));

    public static final DeferredBlock<Block> WHITE_SANDSTONE = registerBlock("white_sandstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> CUT_WHITE_SANDSTONE = registerBlock("cut_white_sandstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CUT_SANDSTONE).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> CHISELED_WHITE_SANDSTONE = registerBlock("chiseled_white_sandstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_SANDSTONE).sound(SoundType.STONE)));
    public static final DeferredBlock<StairBlock> WHITE_SANDSTONE_STAIRS = registerBlock("white_sandstone_stairs", () -> new StairBlock(WHITE_SANDSTONE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_STAIRS)));
    public static final DeferredBlock<SlabBlock> WHITE_SANDSTONE_SLAB = registerBlock("white_sandstone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_SLAB)));
    public static final DeferredBlock<WallBlock> WHITE_SANDSTONE_WALL = registerBlock("white_sandstone_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_WALL)));
    // --- NEW BLOCKS ---
    public static final DeferredBlock<Block> SAFFRON_MOSS = registerBlock("saffron_moss", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)));
    
    // Amber Sand (Using ColoredFallingBlock to match Olivine)
    public static final DeferredBlock<Block> AMBER_SAND = registerBlock("amber_sand", () -> new ColoredFallingBlock(new ColorRGBA(0xFFCC00), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).sound(SoundType.SAND)));
    
    public static final DeferredBlock<Block> AMBER_SANDSTONE = registerBlock("amber_sandstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> CUT_AMBER_SANDSTONE = registerBlock("cut_amber_sandstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CUT_SANDSTONE).sound(SoundType.STONE)));
    public static final DeferredBlock<Block> CHISELED_AMBER_SANDSTONE = registerBlock("chiseled_amber_sandstone", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_SANDSTONE).sound(SoundType.STONE)));
    public static final DeferredBlock<StairBlock> AMBER_SANDSTONE_STAIRS = registerBlock("amber_sandstone_stairs", () -> new StairBlock(AMBER_SANDSTONE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_STAIRS)));
    public static final DeferredBlock<SlabBlock> AMBER_SANDSTONE_SLAB = registerBlock("amber_sandstone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_SLAB)));
    public static final DeferredBlock<WallBlock> AMBER_SANDSTONE_WALL = registerBlock("amber_sandstone_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_WALL)));

    public static final DeferredBlock<Block> PETRIFIED_CACTUS = registerBlock("petrified_cactus", () -> new CactusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS).sound(SoundType.STONE)));

    // ==========================================
    // WOOD SET REGISTRATION
    // ==========================================
    static {
        WOOD_GROUPS.add(registerFullWoodSet("blue_spruce", NTWoodTypes.BLUE_SPRUCE_BLOCK_SET, NTWoodTypes.BLUE_SPRUCE, NTTreeGrowers.BLUE_SPRUCE, Blocks.SPRUCE_LOG, Blocks.SPRUCE_PLANKS));
        WOOD_GROUPS.add(registerFullWoodSet("teak", NTWoodTypes.TEAK_SET, NTWoodTypes.TEAK, NTTreeGrowers.TEAK, Blocks.JUNGLE_LOG, Blocks.JUNGLE_PLANKS));
        WOOD_GROUPS.add(registerFullWoodSet("balsa", NTWoodTypes.BALSA_SET, NTWoodTypes.BALSA, NTTreeGrowers.BALSA, Blocks.OAK_LOG, Blocks.OAK_PLANKS));
        WOOD_GROUPS.add(registerFullWoodSet("mahogany", NTWoodTypes.MAHOGANY_SET, NTWoodTypes.MAHOGANY, NTTreeGrowers.MAHOGANY, Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS));
        WOOD_GROUPS.add(registerFullWoodSet("aspen", NTWoodTypes.ASPEN_SET, NTWoodTypes.ASPEN, NTTreeGrowers.ASPEN, Blocks.BIRCH_LOG, Blocks.BIRCH_PLANKS));
    }

    private static WoodGroup registerFullWoodSet(String name, BlockSetType setType, WoodType woodType, TreeGrower grower, Block logVanilla, Block planksVanilla) {
        WoodGroup group = new WoodGroup();
        group.log = registerBlock(name + "_log", () -> new NTFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(logVanilla)));
        group.wood = registerBlock(name + "_wood", () -> new NTFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(logVanilla)));
        group.strippedLog = registerBlock("stripped_" + name + "_log", () -> new NTFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(logVanilla)));
        group.strippedWood = registerBlock("stripped_" + name + "_wood", () -> new NTFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(logVanilla)));
        DeferredBlock<Block> planks = registerBlock(name + "_planks", () -> new PlanksBlock(BlockBehaviour.Properties.ofFullCopy(planksVanilla)));
        registerBlock(name + "_sapling", () -> new SaplingBlock(grower, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));
        group.leaves = registerBlock(name + "_leaves", () -> new NTParticleLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).noOcclusion().randomTicks().mapColor(MapColor.PLANT).sound(SoundType.GRASS).isSuffocating((state, level, pos) -> false).isViewBlocking((state, level, pos) -> false)));
        registerBlock(name + "_stairs", () -> new StairBlock(planks.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(planks.get())));
        registerBlock(name + "_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(planks.get())));
        registerBlock(name + "_fence", () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(planks.get())));
        registerBlock(name + "_fence_gate", () -> new FenceGateBlock(woodType, BlockBehaviour.Properties.ofFullCopy(planks.get())));
        registerBlock(name + "_door", () -> new DoorBlock(setType, BlockBehaviour.Properties.ofFullCopy(planks.get()).noOcclusion()));
        registerBlock(name + "_trapdoor", () -> new TrapDoorBlock(setType, BlockBehaviour.Properties.ofFullCopy(planks.get()).noOcclusion()));
        registerBlock(name + "_button", () -> new ButtonBlock(setType, 30, BlockBehaviour.Properties.ofFullCopy(planks.get()).noCollission()));
        registerBlock(name + "_pressure_plate", () -> new PressurePlateBlock(setType, BlockBehaviour.Properties.ofFullCopy(planks.get())));
        BLOCKS.register(name + "_sign", () -> new NTSignBlock.Standing(woodType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)));
        BLOCKS.register(name + "_wall_sign", () -> new NTSignBlock.Wall(woodType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN)));
        BLOCKS.register(name + "_hanging_sign", () -> new NTSignBlock.Hanging(woodType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN)));
        BLOCKS.register(name + "_wall_hanging_sign", () -> new NTSignBlock.WallHanging(woodType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN)));
        NTItems.registerWoodItems(name);
        return group;
    }

    public static void registerWoodLogic() {
        try {
            java.lang.reflect.Field field = AxeItem.class.getDeclaredField("f_40590_");
            field.setAccessible(true);
            Map<Block, Block> currentMap = new HashMap<>((Map<Block, Block>) field.get(null));
            for (WoodGroup group : WOOD_GROUPS) {
                currentMap.put(group.log.get(), group.strippedLog.get());
                currentMap.put(group.wood.get(), group.strippedWood.get());
            }
            field.set(null, currentMap);
        } catch (Exception e) {
            NovamTerram.LOGGER.error("Failed to register Novam Terram strippables: " + e.getMessage());
        }

        FireBlock fireblock = (FireBlock) Blocks.FIRE;
        for (WoodGroup group : WOOD_GROUPS) {
            fireblock.setFlammable(group.log.get(), 5, 5);
            fireblock.setFlammable(group.wood.get(), 5, 5);
            fireblock.setFlammable(group.strippedLog.get(), 5, 5);
            fireblock.setFlammable(group.strippedWood.get(), 5, 5);
            fireblock.setFlammable(group.leaves.get(), 30, 60);
        }

        fireblock.setFlammable(ORANGE_AUTUMNAL_LEAVES.get(), 30, 60);
        fireblock.setFlammable(YELLOW_AUTUMNAL_LEAVES.get(), 30, 60);
        fireblock.setFlammable(PAULOWNIA_LEAVES.get(), 30, 60);
        fireblock.setFlammable(WHITE_MYRTLE_LEAVES.get(), 30, 60);
        fireblock.setFlammable(RED_MYRTLE_LEAVES.get(), 30, 60);

        fireblock.setFlammable(PURPLE_MYRTLE_LEAVES.get(), 30, 60);

    }

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

    private static class WoodGroup {
        public DeferredBlock<Block> log;
        public DeferredBlock<Block> wood;
        public DeferredBlock<Block> strippedLog;
        public DeferredBlock<Block> strippedWood;
        public DeferredBlock<Block> leaves;
    }

    private static class PlanksBlock extends Block {
        public PlanksBlock(Properties p) { super(p); }
        @Override public boolean isFlammable(BlockState s, BlockGetter l, BlockPos p, Direction d) { return true; }
        @Override public int getFlammability(BlockState s, BlockGetter l, BlockPos p, Direction d) { return 20; }
        @Override public int getFireSpreadSpeed(BlockState s, BlockGetter l, BlockPos p, Direction d) { return 5; }
    }

    private static class NTParticleLeavesBlock extends LeavesBlock {
        public NTParticleLeavesBlock(Properties p) { super(p); }
        @Override
        public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
            super.animateTick(state, level, pos, random);

            if (random.nextInt(15) == 0) {
                BlockPos below = pos.below();
                if (level.isEmptyBlock(below)) {
                    double d0 = (double) pos.getX() + random.nextDouble();
                    double d1 = (double) pos.getY() - 0.05D;
                    double d2 = (double) pos.getZ() + random.nextDouble();

                    // Map specific leaves to their particles
                    if (state.is(ORANGE_AUTUMNAL_LEAVES.get())) {
                        level.addParticle(NTParticles.ORANGE_LEAF.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else if (state.is(YELLOW_AUTUMNAL_LEAVES.get())) {
                        level.addParticle(NTParticles.YELLOW_LEAF.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else if (state.is(PAULOWNIA_LEAVES.get())) {
                        level.addParticle(NTParticles.PAULOWNIA_LEAF.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else if (state.is(WHITE_MYRTLE_LEAVES.get())) {
                        level.addParticle(NTParticles.MYRTLE_LEAF.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else if (state.is(RED_MYRTLE_LEAVES.get())) {
                        level.addParticle(NTParticles.MYRTLE_LEAF.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else if (state.is(PURPLE_MYRTLE_LEAVES.get())) {
                        level.addParticle(NTParticles.MYRTLE_LEAF.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    } else if (state.getBlock().toString().contains("blue_spruce_leaves")) {
                        level.addParticle(NTParticles.BLUE_SPRUCE_LEAF.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }

    private static class OliveLeavesBlock extends NTParticleLeavesBlock {
        public OliveLeavesBlock(Properties p) {
            super(p);
        }

        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return true; // Leaves usually tick for decay, but we need it for growth too
        }

        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            super.randomTick(state, level, pos, random); // Keep vanilla leaf decay logic

            // 5% chance per random tick to spawn an olive if the space below is air
            if (random.nextFloat() < 0.05f) {
                BlockPos spaceBelow = pos.below();
                if (level.isEmptyBlock(spaceBelow)) {
                    level.setBlock(spaceBelow, NTBlocks.OLIVE_HANGING.get().defaultBlockState().setValue(OliveHangingBlock.AGE, 0), 3);
                }
            }
        }
    }
}