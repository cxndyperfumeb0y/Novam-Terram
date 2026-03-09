package net.candyperfumeboy.novamterram.entities.variants;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class NTSignBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, NovamTerram.MOD_ID);

    // Registering the standard Standing/Wall signs
    public static final Supplier<BlockEntityType<NTSignBlockEntity>> SIGN = BLOCK_ENTITIES.register("sign",
            () -> BlockEntityType.Builder.of(NTSignBlockEntity::new,
                    getAllBlocksOfType("sign") // This grabs ALL registered signs
            ).build(null));

    // Registering the Hanging/Wall Hanging signs
    public static final Supplier<BlockEntityType<NTHangingSignBlockEntity>> HANGING_SIGN = BLOCK_ENTITIES.register("hanging_sign",
            () -> BlockEntityType.Builder.of(NTHangingSignBlockEntity::new,
                    getAllBlocksOfType("hanging_sign") // This grabs ALL registered hanging signs
            ).build(null));

    // Helper method to collect all blocks from the NTBlocks registry that match the suffix
    private static Block[] getAllBlocksOfType(String suffix) {
        return NTBlocks.BLOCKS.getEntries().stream()
                .filter(holder -> {
                    String path = holder.getId().getPath();
                    // Filters for "sign" (but not hanging) OR "hanging_sign"
                    if (suffix.equals("sign")) {
                        return path.endsWith("_sign") && !path.contains("hanging");
                    }
                    return path.endsWith(suffix);
                })
                .map(DeferredHolder::get)
                .toArray(Block[]::new);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}