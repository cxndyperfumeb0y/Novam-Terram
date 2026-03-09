package net.candyperfumeboy.novamterram.block.custom;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class NTWoodTypes {
    public static final BlockSetType BLUE_SPRUCE_BLOCK_SET = BlockSetType.register(new BlockSetType("blue_spruce"));
    public static final WoodType BLUE_SPRUCE = WoodType.register(new WoodType("novamterram:blue_spruce", BLUE_SPRUCE_BLOCK_SET));

    // RAINFOREST TYPES
    public static final BlockSetType TEAK_SET = BlockSetType.register(new BlockSetType("teak"));
    public static final WoodType TEAK = WoodType.register(new WoodType("novamterram:teak", TEAK_SET));

    public static final BlockSetType BALSA_SET = BlockSetType.register(new BlockSetType("balsa"));
    public static final WoodType BALSA = WoodType.register(new WoodType("novamterram:balsa", BALSA_SET));

    public static final BlockSetType MAHOGANY_SET = BlockSetType.register(new BlockSetType("mahogany"));
    public static final WoodType MAHOGANY = WoodType.register(new WoodType("novamterram:mahogany", MAHOGANY_SET));

    public static final BlockSetType ASPEN_SET = BlockSetType.register(new BlockSetType("aspen"));
    public static final WoodType ASPEN = WoodType.register(new WoodType("novamterram:aspen", ASPEN_SET));

}
