package net.candyperfumeboy.novamterram.entities.variants;

import net.candyperfumeboy.novamterram.NovamTerram;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class NTSignBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, NovamTerram.MOD_ID);

    // Registering the standard Standing/Wall signs
    public static final Supplier<BlockEntityType<NTSignBlockEntity>> SIGN = BLOCK_ENTITIES.register("sign",
            () -> BlockEntityType.Builder.of(NTSignBlockEntity::new,
                    NTBlocks.WOOD_SETS.stream()
                            .flatMap(wood -> Stream.of(wood.sign.get(), wood.wallSign.get()))
                            .toArray(Block[]::new)
            ).build(null));

    // Registering the Hanging/Wall Hanging signs
    public static final Supplier<BlockEntityType<NTHangingSignBlockEntity>> HANGING_SIGN = BLOCK_ENTITIES.register("hanging_sign",
            () -> BlockEntityType.Builder.of(NTHangingSignBlockEntity::new,
                    NTBlocks.WOOD_SETS.stream()
                            .flatMap(wood -> Stream.of(wood.hangingSign.get(), wood.wallHangingSign.get()))
                            .toArray(Block[]::new)
            ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
