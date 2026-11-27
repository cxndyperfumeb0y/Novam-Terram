package net.candyperfumeboy.novamterram.block;

import net.candyperfumeboy.novamterram.entities.variants.NTBoatEntities;
import net.candyperfumeboy.novamterram.entities.variants.NTSignBlock;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.candyperfumeboy.novamterram.item.custom.NTBoatItem;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Every block and item that belongs to one wood type.
 * To add a new wood: add it to NTWoodTypes and NTBoatEntities, then add one line in NTBlocks.
 */
public class NTWoodSet {
    public final String name;
    public final WoodType woodType;

    public final DeferredBlock<RotatedPillarBlock> log;
    public final DeferredBlock<RotatedPillarBlock> wood;
    public final DeferredBlock<RotatedPillarBlock> strippedLog;
    public final DeferredBlock<RotatedPillarBlock> strippedWood;
    public final DeferredBlock<Block> planks;
    public final DeferredBlock<SaplingBlock> sapling;
    public final DeferredBlock<LeavesBlock> leaves;
    public final DeferredBlock<StairBlock> stairs;
    public final DeferredBlock<SlabBlock> slab;
    public final DeferredBlock<FenceBlock> fence;
    public final DeferredBlock<FenceGateBlock> fenceGate;
    public final DeferredBlock<DoorBlock> door;
    public final DeferredBlock<TrapDoorBlock> trapdoor;
    public final DeferredBlock<ButtonBlock> button;
    public final DeferredBlock<PressurePlateBlock> pressurePlate;
    public final DeferredBlock<NTSignBlock.Standing> sign;
    public final DeferredBlock<NTSignBlock.Wall> wallSign;
    public final DeferredBlock<NTSignBlock.Hanging> hangingSign;
    public final DeferredBlock<NTSignBlock.WallHanging> wallHangingSign;

    public final DeferredItem<SignItem> signItem;
    public final DeferredItem<HangingSignItem> hangingSignItem;
    public final DeferredItem<NTBoatItem> boat;
    public final DeferredItem<NTBoatItem> chestBoat;

    NTWoodSet(String name, BlockSetType setType, WoodType woodType, TreeGrower grower,
              Block vanillaLog, Block vanillaPlanks, @Nullable Supplier<? extends ParticleOptions> leafParticle) {
        this.name = name;
        this.woodType = woodType;

        // The stripped versions come first so the normal ones can point at them
        DeferredBlock<RotatedPillarBlock> strippedLog = NTBlocks.registerBlock("stripped_" + name + "_log",
                () -> new NTFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(vanillaLog)));
        DeferredBlock<RotatedPillarBlock> strippedWood = NTBlocks.registerBlock("stripped_" + name + "_wood",
                () -> new NTFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(vanillaLog)));
        this.strippedLog = strippedLog;
        this.strippedWood = strippedWood;
        this.log = NTBlocks.registerBlock(name + "_log",
                () -> new NTFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(vanillaLog), strippedLog));
        this.wood = NTBlocks.registerBlock(name + "_wood",
                () -> new NTFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(vanillaLog), strippedWood));

        DeferredBlock<Block> planks = NTBlocks.registerBlock(name + "_planks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(vanillaPlanks)));
        this.planks = planks;
        this.sapling = NTBlocks.registerBlock(name + "_sapling",
                () -> new SaplingBlock(grower, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));
        this.leaves = NTBlocks.registerBlock(name + "_leaves",
                () -> new NTBlocks.NTParticleLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                        .noOcclusion().randomTicks().mapColor(MapColor.PLANT).sound(SoundType.GRASS)
                        .isSuffocating((state, level, pos) -> false)
                        .isViewBlocking((state, level, pos) -> false), leafParticle));

        this.stairs = NTBlocks.registerBlock(name + "_stairs",
                () -> new StairBlock(planks.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(planks.get())));
        this.slab = NTBlocks.registerBlock(name + "_slab",
                () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(planks.get())));
        this.fence = NTBlocks.registerBlock(name + "_fence",
                () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(planks.get())));
        this.fenceGate = NTBlocks.registerBlock(name + "_fence_gate",
                () -> new FenceGateBlock(woodType, BlockBehaviour.Properties.ofFullCopy(planks.get())));
        this.door = NTBlocks.registerBlock(name + "_door",
                () -> new DoorBlock(setType, BlockBehaviour.Properties.ofFullCopy(planks.get()).noOcclusion()));
        this.trapdoor = NTBlocks.registerBlock(name + "_trapdoor",
                () -> new TrapDoorBlock(setType, BlockBehaviour.Properties.ofFullCopy(planks.get()).noOcclusion()));
        this.button = NTBlocks.registerBlock(name + "_button",
                () -> new ButtonBlock(setType, 30, BlockBehaviour.Properties.ofFullCopy(planks.get()).noCollission()));
        this.pressurePlate = NTBlocks.registerBlock(name + "_pressure_plate",
                () -> new PressurePlateBlock(setType, BlockBehaviour.Properties.ofFullCopy(planks.get())));

        // Signs: the blocks have no automatic block item, their items are registered below
        DeferredBlock<NTSignBlock.Standing> sign = NTBlocks.BLOCKS.register(name + "_sign",
                () -> new NTSignBlock.Standing(woodType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)));
        DeferredBlock<NTSignBlock.Wall> wallSign = NTBlocks.BLOCKS.register(name + "_wall_sign",
                () -> new NTSignBlock.Wall(woodType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN)));
        DeferredBlock<NTSignBlock.Hanging> hangingSign = NTBlocks.BLOCKS.register(name + "_hanging_sign",
                () -> new NTSignBlock.Hanging(woodType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN)));
        DeferredBlock<NTSignBlock.WallHanging> wallHangingSign = NTBlocks.BLOCKS.register(name + "_wall_hanging_sign",
                () -> new NTSignBlock.WallHanging(woodType, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN)));
        this.sign = sign;
        this.wallSign = wallSign;
        this.hangingSign = hangingSign;
        this.wallHangingSign = wallHangingSign;

        this.signItem = NTItems.ITEMS.register(name + "_sign",
                () -> new SignItem(new Item.Properties().stacksTo(16), sign.get(), wallSign.get()));
        this.hangingSignItem = NTItems.ITEMS.register(name + "_hanging_sign",
                () -> new HangingSignItem(hangingSign.get(), wallHangingSign.get(), new Item.Properties().stacksTo(16)));
        this.boat = NTItems.ITEMS.register(name + "_boat",
                () -> new NTBoatItem(false, NTBoatEntities.getBoatEntity(name, false), new Item.Properties().stacksTo(1)));
        this.chestBoat = NTItems.ITEMS.register(name + "_chest_boat",
                () -> new NTBoatItem(true, NTBoatEntities.getBoatEntity(name, true), new Item.Properties().stacksTo(1)));
    }
}
