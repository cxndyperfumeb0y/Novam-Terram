package net.candyperfumeboy.novamterram.block;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class NTWoodTypes {

    // BlockSetType handles buttons, pressure plates, and door sounds
    public static final BlockSetType BLUE_SPRUCE_BLOCK_SET = BlockSetType.register(new BlockSetType("blue_spruce"));

    // WoodType handles fence gates, signs, and boats
    public static final WoodType BLUE_SPRUCE = WoodType.register(new WoodType("novamterram:blue_spruce", NTWoodTypes.BLUE_SPRUCE_BLOCK_SET));}