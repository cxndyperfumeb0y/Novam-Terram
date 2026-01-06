package net.candyperfumeboy.novamterram.entities;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class NTSignBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, NovamTerram.MOD_ID);

    public static final Supplier<BlockEntityType<NTSignBlockEntity>> SIGN = BLOCK_ENTITIES.register("sign",
            () -> BlockEntityType.Builder.of(NTSignBlockEntity::new,
                    NTBlocks.BLUE_SPRUCE_SIGN.get(), NTBlocks.BLUE_SPRUCE_WALL_SIGN.get()).build(null));

    public static final Supplier<BlockEntityType<NTHangingSignBlockEntity>> HANGING_SIGN = BLOCK_ENTITIES.register("hanging_sign",
            () -> BlockEntityType.Builder.of(NTHangingSignBlockEntity::new, // Point to YOUR class here
                    NTBlocks.BLUE_SPRUCE_HANGING_SIGN.get(), NTBlocks.BLUE_SPRUCE_WALL_HANGING_SIGN.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
