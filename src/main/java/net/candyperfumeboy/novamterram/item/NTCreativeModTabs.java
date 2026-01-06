package net.candyperfumeboy.novamterram.item;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class NTCreativeModTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NovamTerram.MOD_ID);

    public static final Supplier<CreativeModeTab> NOVAM_TERRAM_TAB = CREATIVE_MODE_TAB.register("novam_terram_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(NTItems.OPAL.get()))
                    .title(Component.translatable("creativetab.novamterram"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // --- MINERALS & ORES ---
                        output.accept(NTItems.OPAL.get());
                        output.accept(NTItems.RAW_OPAL.get());
                        output.accept(NTBlocks.OPAL_BLOCK.get());
                        output.accept(NTBlocks.OPAL_ORE.get());
                        output.accept(NTItems.LIGNITE_COAL.get());
                        output.accept(NTBlocks.LIGNITE_BLOCK.get());
                        output.accept(NTBlocks.LIGNITE_ORE.get());
                        output.accept(NTBlocks.DEEPSLATE_LIGNITE_ORE.get());
                        output.accept(NTBlocks.LIGNITE_NETHER_ORE.get());
                        output.accept(NTBlocks.LIGNITE_END_ORE.get());

                        // --- OLIVINE SANDSTONE SET ---
                        output.accept(NTBlocks.OLIVINE_SAND.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE.get());
                        output.accept(NTBlocks.CHISELED_OLIVINE_SANDSTONE.get());
                        output.accept(NTBlocks.CUT_OLIVINE_SANDSTONE.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_STAIRS.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_SLAB.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_WALL.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_FENCE.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_DOOR.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_PRESSURE_PLATE.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_BUTTON.get());

                        // --- BLUE SPRUCE WOOD SET ---
                        output.accept(NTBlocks.BLUE_SPRUCE_LOG.get());
                        output.accept(NTBlocks.BLUE_SPRUCE_WOOD.get());
                        output.accept(NTBlocks.STRIPPED_BLUE_SPRUCE_LOG.get());
                        output.accept(NTBlocks.STRIPPED_BLUE_SPRUCE_WOOD.get());
                        output.accept(NTBlocks.BLUE_SPRUCE_PLANKS.get());
                        output.accept(NTBlocks.BLUE_SPRUCE_STAIRS.get());
                        output.accept(NTBlocks.BLUE_SPRUCE_SLAB.get());
                        output.accept(NTBlocks.BLUE_SPRUCE_FENCE.get());
                        output.accept(NTBlocks.BLUE_SPRUCE_FENCE_GATE.get());
                        output.accept(NTBlocks.BLUE_SPRUCE_DOOR.get());
                        output.accept(NTBlocks.BLUE_SPRUCE_TRAP_DOOR.get());
                        output.accept(NTBlocks.BLUE_SPRUCE_PRESSURE_PLATE.get());
                        output.accept(NTBlocks.BLUE_SPRUCE_BUTTON.get());

                        // Signs & Hanging Signs (Items)
                        output.accept(NTItems.BLUE_SPRUCE_SIGN_ITEM.get());
                        output.accept(NTItems.BLUE_SPRUCE_HANGING_SIGN_ITEM.get());

                        // Boats
                        output.accept(NTItems.BLUE_SPRUCE_BOAT.get());
                        output.accept(NTItems.BLUE_SPRUCE_CHEST_BOAT.get());

                        // Nature
                        output.accept(NTBlocks.BLUE_SPRUCE_SAPLING.get());
                        output.accept(NTBlocks.BLUE_SPRUCE_LEAVES.get());
                        output.accept(NTBlocks.BLUE_PODZOL.get());

                        // --- MOB DROPS & SPAWN EGGS ---
                        output.accept(NTItems.BEAR_CLAW.get());
                        output.accept(NTItems.SILVER_FOX_SPAWN_EGG.get());
                        output.accept(NTItems.SPECTACLED_BEAR_SPAWN_EGG.get());

                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}