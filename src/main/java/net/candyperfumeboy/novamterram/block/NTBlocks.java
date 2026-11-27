package net.candyperfumeboy.novamterram.block;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.custom.AuraBlock;
import net.candyperfumeboy.novamterram.block.custom.OliveHangingBlock;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.candyperfumeboy.novamterram.worldgen.tree.NTTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class NTBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NovamTerram.MOD_ID);

    // ==========================================
    // GEMS & ORES
    // ==========================================
    public static final DeferredBlock<Block> OPAL_BLOCK = gemBlock("opal_block");
    public static final DeferredBlock<Block> OPAL_ORE = ore("opal_ore", SoundType.STONE);
    public static final DeferredBlock<Block> IOLITE_BLOCK = gemBlock("iolite_block");
    public static final DeferredBlock<Block> IOLITE_ORE = ore("iolite_ore", SoundType.STONE);
    public static final DeferredBlock<Block> KYANITE_BLOCK = gemBlock("kyanite_block");
    public static final DeferredBlock<Block> KYANITE_ORE = ore("kyanite_ore", SoundType.STONE);
    public static final DeferredBlock<Block> JADEITE_BLOCK = gemBlock("jadeite_block");
    public static final DeferredBlock<Block> JADEITE_ORE = ore("jadeite_ore", SoundType.STONE);
    public static final DeferredBlock<Block> RHODOCHROSITE_BLOCK = gemBlock("rhodochrosite_block");
    public static final DeferredBlock<Block> RHODOCHROSITE_ORE = ore("rhodochrosite_ore", SoundType.STONE);
    public static final DeferredBlock<Block> LIGNITE_BLOCK = registerBlock("lignite_block", () -> new Block(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    public static final DeferredBlock<Block> LIGNITE_ORE = ore("lignite_ore", SoundType.STONE);
    public static final DeferredBlock<Block> LIGNITE_NETHER_ORE = ore("lignite_nether_ore", SoundType.NETHER_ORE);
    public static final DeferredBlock<Block> LIGNITE_END_ORE = ore("lignite_end_ore", SoundType.STONE);
    public static final DeferredBlock<Block> DEEPSLATE_LIGNITE_ORE = ore("deepslate_lignite_ore", SoundType.DEEPSLATE);

    // ==========================================
    // HIGHLANDS & TAIGA FLORA
    // ==========================================
    // Autumnal Pine
    public static final DeferredBlock<Block> ORANGE_AUTUMNAL_LEAVES = leaves("orange_autumnal_leaves", Blocks.SPRUCE_LEAVES, NTParticles.ORANGE_LEAF);
    public static final DeferredBlock<Block> YELLOW_AUTUMNAL_LEAVES = leaves("yellow_autumnal_leaves", Blocks.SPRUCE_LEAVES, NTParticles.YELLOW_LEAF);
    public static final DeferredBlock<Block> ORANGE_AUTUMNAL_PINE_SAPLING = sapling("orange_autumnal_pine_sapling", NTTreeGrowers.ORANGE_AUTUMNAL_PINE, Blocks.SPRUCE_SAPLING);
    public static final DeferredBlock<Block> YELLOW_AUTUMNAL_PINE_SAPLING = sapling("yellow_autumnal_pine_sapling", NTTreeGrowers.YELLOW_AUTUMNAL_PINE, Blocks.SPRUCE_SAPLING);
    public static final DeferredBlock<Block> ORANGE_AUTUMNAL_LEAF_CARPET = petals("orange_autumnal_leaf_carpet", MapColor.COLOR_ORANGE);
    public static final DeferredBlock<Block> YELLOW_AUTUMNAL_LEAF_CARPET = petals("yellow_autumnal_leaf_carpet", MapColor.COLOR_YELLOW);

    // Paulownia
    public static final DeferredBlock<Block> PAULOWNIA_LEAVES = leaves("paulownia_leaves", Blocks.OAK_LEAVES, NTParticles.PAULOWNIA_LEAF);
    public static final DeferredBlock<Block> PAULOWNIA_SAPLING = sapling("paulownia_sapling", NTTreeGrowers.PAULOWNIA, Blocks.OAK_SAPLING);
    public static final DeferredBlock<Block> PAULOWNIA_LEAF_CARPET = petals("paulownia_leaf_carpet", MapColor.COLOR_PURPLE);

    // Myrtle
    public static final DeferredBlock<Block> WHITE_MYRTLE_LEAVES = leaves("white_myrtle_leaves", Blocks.CHERRY_LEAVES, NTParticles.MYRTLE_LEAF);
    public static final DeferredBlock<Block> WHITE_MYRTLE_SAPLING = sapling("white_myrtle_sapling", NTTreeGrowers.WHITE_MYRTLE, Blocks.OAK_SAPLING);
    public static final DeferredBlock<Block> RED_MYRTLE_LEAVES = leaves("red_myrtle_leaves", Blocks.CHERRY_LEAVES, NTParticles.MYRTLE_LEAF);
    public static final DeferredBlock<Block> RED_MYRTLE_SAPLING = sapling("red_myrtle_sapling", NTTreeGrowers.RED_MYRTLE, Blocks.OAK_SAPLING);
    public static final DeferredBlock<Block> PURPLE_MYRTLE_LEAVES = leaves("purple_myrtle_leaves", Blocks.CHERRY_LEAVES, NTParticles.MYRTLE_LEAF);
    public static final DeferredBlock<Block> PURPLE_MYRTLE_SAPLING = sapling("purple_myrtle_sapling", NTTreeGrowers.PURPLE_MYRTLE, Blocks.OAK_SAPLING);
    public static final DeferredBlock<Block> MYRTLE_LEAF_CARPET = petals("myrtle_leaf_carpet", MapColor.PLANT);

    // ==========================================
    // FLOWERS & PETALS
    // ==========================================
    public static final DeferredBlock<Block> HEATHER = flower("heather", MobEffects.REGENERATION, Blocks.DANDELION);
    public static final DeferredBlock<Block> HEATHER_PETALS = registerBlock("heather_petals", () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS)));
    public static final DeferredBlock<Block> LAVENDER = flower("lavender", MobEffects.REGENERATION, Blocks.DANDELION);
    public static final DeferredBlock<Block> LAVENDER_PETALS = registerBlock("lavender_petals", () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS)));
    public static final DeferredBlock<Block> ASTER = flower("aster", MobEffects.REGENERATION, Blocks.DANDELION);
    public static final DeferredBlock<Block> ASTER_PETALS = registerBlock("aster_petals", () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS)));
    public static final DeferredBlock<Block> TRILLIUM = flower("trillium", MobEffects.REGENERATION, Blocks.DANDELION);
    public static final DeferredBlock<Block> THISTLE = flower("thistle", MobEffects.REGENERATION, Blocks.DANDELION);
    public static final DeferredBlock<Block> BROMELIAD = flower("bromeliad", MobEffects.REGENERATION, Blocks.DANDELION);
    // No block item: the olive item is what you get from it
    public static final DeferredBlock<Block> OLIVE_HANGING = BLOCKS.register("olive_hanging",
            () -> new OliveHangingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COCOA).noCollission().instabreak().randomTicks()));
    public static final DeferredBlock<Block> OLIVE_LEAVES = registerBlock("olive_leaves",
            () -> new OliveLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).randomTicks()));
    public static final DeferredBlock<Block> SILVER_SAGE = flower("silver_sage", MobEffects.ABSORPTION, Blocks.DANDELION);
    public static final DeferredBlock<Block> BLEEDING_HEART = flower("bleeding_heart", MobEffects.REGENERATION, Blocks.DANDELION);
    public static final DeferredBlock<Block> IRIS = registerBlock("iris",
            () -> new TallFlowerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PEONY).noCollission().instabreak()));
    public static final DeferredBlock<Block> BLUEBELL = flower("bluebell", MobEffects.REGENERATION, Blocks.CORNFLOWER);
    public static final DeferredBlock<Block> PURPLE_BLUEBELL = flower("purple_bluebell", MobEffects.REGENERATION, Blocks.CORNFLOWER);


    // ==========================================
    // SANDS, SOILS & STONE
    // ==========================================
    public static final DeferredBlock<Block> OLIVINE_SAND = sand("olivine_sand", -8356741);
    public static final DeferredBlock<Block> OLIVINE_SANDSTONE = stoneBlock("olivine_sandstone", Blocks.SANDSTONE);
    public static final DeferredBlock<Block> CUT_OLIVINE_SANDSTONE = stoneBlock("cut_olivine_sandstone", Blocks.CUT_SANDSTONE);
    public static final DeferredBlock<Block> CHISELED_OLIVINE_SANDSTONE = stoneBlock("chiseled_olivine_sandstone", Blocks.CHISELED_SANDSTONE);
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
    public static final DeferredBlock<Block> WHITE_SAND = sand("white_sand", 0xFFFFFF);

    public static final DeferredBlock<Block> WHITE_SANDSTONE = stoneBlock("white_sandstone", Blocks.SANDSTONE);
    public static final DeferredBlock<Block> CUT_WHITE_SANDSTONE = stoneBlock("cut_white_sandstone", Blocks.CUT_SANDSTONE);
    public static final DeferredBlock<Block> CHISELED_WHITE_SANDSTONE = stoneBlock("chiseled_white_sandstone", Blocks.CHISELED_SANDSTONE);
    public static final DeferredBlock<StairBlock> WHITE_SANDSTONE_STAIRS = registerBlock("white_sandstone_stairs", () -> new StairBlock(WHITE_SANDSTONE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_STAIRS)));
    public static final DeferredBlock<SlabBlock> WHITE_SANDSTONE_SLAB = registerBlock("white_sandstone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_SLAB)));
    public static final DeferredBlock<WallBlock> WHITE_SANDSTONE_WALL = registerBlock("white_sandstone_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_WALL)));
    public static final DeferredBlock<Block> SAFFRON_MOSS = registerBlock("saffron_moss", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)));

    // Amber set
    public static final DeferredBlock<Block> AMBER_SAND = sand("amber_sand", 0xFFCC00);

    public static final DeferredBlock<Block> AMBER_SANDSTONE = stoneBlock("amber_sandstone", Blocks.SANDSTONE);
    public static final DeferredBlock<Block> CUT_AMBER_SANDSTONE = stoneBlock("cut_amber_sandstone", Blocks.CUT_SANDSTONE);
    public static final DeferredBlock<Block> CHISELED_AMBER_SANDSTONE = stoneBlock("chiseled_amber_sandstone", Blocks.CHISELED_SANDSTONE);
    public static final DeferredBlock<StairBlock> AMBER_SANDSTONE_STAIRS = registerBlock("amber_sandstone_stairs", () -> new StairBlock(AMBER_SANDSTONE.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_STAIRS)));
    public static final DeferredBlock<SlabBlock> AMBER_SANDSTONE_SLAB = registerBlock("amber_sandstone_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_SLAB)));
    public static final DeferredBlock<WallBlock> AMBER_SANDSTONE_WALL = registerBlock("amber_sandstone_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE_WALL)));

    public static final DeferredBlock<Block> PETRIFIED_CACTUS = registerBlock("petrified_cactus", () -> new CactusBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CACTUS).sound(SoundType.STONE)));

    // ==========================================
    // WOOD SETS (all blocks and items for each wood are in NTWoodSet)
    // ==========================================
    public static final NTWoodSet BLUE_SPRUCE = new NTWoodSet("blue_spruce", NTWoodTypes.BLUE_SPRUCE_BLOCK_SET, NTWoodTypes.BLUE_SPRUCE,
            NTTreeGrowers.BLUE_SPRUCE, Blocks.SPRUCE_LOG, Blocks.SPRUCE_PLANKS, NTParticles.BLUE_SPRUCE_LEAF);
    public static final NTWoodSet TEAK = new NTWoodSet("teak", NTWoodTypes.TEAK_SET, NTWoodTypes.TEAK,
            NTTreeGrowers.TEAK, Blocks.JUNGLE_LOG, Blocks.JUNGLE_PLANKS, null);
    public static final NTWoodSet BALSA = new NTWoodSet("balsa", NTWoodTypes.BALSA_SET, NTWoodTypes.BALSA,
            NTTreeGrowers.BALSA, Blocks.OAK_LOG, Blocks.OAK_PLANKS, null);
    public static final NTWoodSet MAHOGANY = new NTWoodSet("mahogany", NTWoodTypes.MAHOGANY_SET, NTWoodTypes.MAHOGANY,
            NTTreeGrowers.MAHOGANY, Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, null);
    public static final NTWoodSet ASPEN = new NTWoodSet("aspen", NTWoodTypes.ASPEN_SET, NTWoodTypes.ASPEN,
            NTTreeGrowers.ASPEN, Blocks.BIRCH_LOG, Blocks.BIRCH_PLANKS, null);

    /** Loop over this to do something for every wood (models, tags, recipes, creative tab...). */
    public static final List<NTWoodSet> WOOD_SETS = List.of(BLUE_SPRUCE, TEAK, BALSA, MAHOGANY, ASPEN);

    // ==========================================
    // FIRE
    // ==========================================
    /** Called from common setup. Same numbers as the vanilla wood blocks. */
    public static void registerWoodLogic() {
        FireBlock fire = (FireBlock) Blocks.FIRE;

        for (NTWoodSet set : WOOD_SETS) {
            for (Supplier<? extends Block> log : List.<Supplier<? extends Block>>of(set.log, set.wood, set.strippedLog, set.strippedWood)) {
                fire.setFlammable(log.get(), 5, 5);
            }
            for (Supplier<? extends Block> part : List.<Supplier<? extends Block>>of(set.planks, set.stairs, set.slab, set.fence, set.fenceGate)) {
                fire.setFlammable(part.get(), 5, 20);
            }
            fire.setFlammable(set.leaves.get(), 30, 60);
        }

        for (DeferredBlock<Block> leaves : List.of(ORANGE_AUTUMNAL_LEAVES, YELLOW_AUTUMNAL_LEAVES, PAULOWNIA_LEAVES,
                WHITE_MYRTLE_LEAVES, RED_MYRTLE_LEAVES, PURPLE_MYRTLE_LEAVES, OLIVE_LEAVES)) {
            fire.setFlammable(leaves.get(), 30, 60);
        }
    }

    // ==========================================
    // HELPERS
    // ==========================================
    /** Registers a block together with its normal block item. */
    static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> registered = BLOCKS.register(name, block);
        NTItems.ITEMS.register(name, () -> new BlockItem(registered.get(), new Item.Properties()));
        return registered;
    }

    private static DeferredBlock<Block> gemBlock(String name) {
        return registerBlock(name, () -> new AuraBlock(BlockBehaviour.Properties.of()
                .strength(4f).lightLevel(state -> 5).requiresCorrectToolForDrops().randomTicks().sound(SoundType.AMETHYST)));
    }

    private static DeferredBlock<Block> ore(String name, SoundType sound) {
        return registerBlock(name, () -> new DropExperienceBlock(UniformInt.of(2, 4),
                BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(sound)));
    }

    private static DeferredBlock<Block> leaves(String name, Block copyFrom, Supplier<? extends ParticleOptions> particle) {
        return registerBlock(name, () -> new NTParticleLeavesBlock(BlockBehaviour.Properties.ofFullCopy(copyFrom), particle));
    }

    private static DeferredBlock<Block> sapling(String name, TreeGrower grower, Block copyFrom) {
        return registerBlock(name, () -> new SaplingBlock(grower, BlockBehaviour.Properties.ofFullCopy(copyFrom)));
    }

    private static DeferredBlock<Block> petals(String name, MapColor color) {
        return registerBlock(name, () -> new PinkPetalsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_PETALS).mapColor(color)));
    }

    private static DeferredBlock<Block> flower(String name, Holder<MobEffect> stewEffect, Block copyFrom) {
        return registerBlock(name, () -> new FlowerBlock(stewEffect, 5,
                BlockBehaviour.Properties.ofFullCopy(copyFrom).noCollission().instabreak()));
    }

    private static DeferredBlock<Block> sand(String name, int dustColor) {
        return registerBlock(name, () -> new ColoredFallingBlock(new ColorRGBA(dustColor),
                BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).sound(SoundType.SAND)));
    }

    private static DeferredBlock<Block> stoneBlock(String name, Block copyFrom) {
        return registerBlock(name, () -> new Block(BlockBehaviour.Properties.ofFullCopy(copyFrom).sound(SoundType.STONE)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    // ==========================================
    // LEAVES
    // ==========================================
    /** Leaves that can drop a falling-leaf particle. Pass null for no particle. */
    static class NTParticleLeavesBlock extends LeavesBlock {
        @Nullable
        private final Supplier<? extends ParticleOptions> particle;

        NTParticleLeavesBlock(Properties properties, @Nullable Supplier<? extends ParticleOptions> particle) {
            super(properties);
            this.particle = particle;
        }

        @Override
        public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
            super.animateTick(state, level, pos, random);

            if (this.particle != null && random.nextInt(15) == 0 && level.isEmptyBlock(pos.below())) {
                level.addParticle(this.particle.get(),
                        pos.getX() + random.nextDouble(), pos.getY() - 0.05D, pos.getZ() + random.nextDouble(),
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    /** Olive leaves also grow olives underneath. */
    private static class OliveLeavesBlock extends NTParticleLeavesBlock {
        OliveLeavesBlock(Properties properties) {
            super(properties, null);
        }

        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return true; // Normal leaves only tick to decay, these also need to grow olives
        }

        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            super.randomTick(state, level, pos, random); // Keeps vanilla leaf decay

            if (random.nextFloat() < 0.05f && level.isEmptyBlock(pos.below())) {
                level.setBlock(pos.below(), OLIVE_HANGING.get().defaultBlockState().setValue(OliveHangingBlock.AGE, 0), 3);
            }
        }
    }
}
