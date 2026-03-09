package net.candyperfumeboy.novamterram.item;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NTCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NovamTerram.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NOVAM_TERRAM_TAB =
            CREATIVE_MODE_TABS.register("novam_terram_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.novam_terram_tab"))
                    .icon(() -> new ItemStack(NTItems.OPAL.get()))
                    .displayItems((parameters, output) -> {
                        // --- MINERALS & ORES ---
                        output.accept(NTItems.OPAL.get());
                        output.accept(NTItems.RAW_OPAL.get());
                        output.accept(NTBlocks.OPAL_BLOCK.get());
                        output.accept(NTBlocks.OPAL_ORE.get());

                        output.accept(NTItems.IOLITE.get());
                        output.accept(NTItems.RAW_IOLITE.get());
                        output.accept(NTBlocks.IOLITE_BLOCK.get());
                        output.accept(NTBlocks.IOLITE_ORE.get());

                        output.accept(NTItems.KYANITE.get());
                        output.accept(NTItems.RAW_KYANITE.get());
                        output.accept(NTBlocks.KYANITE_BLOCK.get());
                        output.accept(NTBlocks.KYANITE_ORE.get());

                        output.accept(NTItems.JADEITE.get());
                        output.accept(NTItems.RAW_JADEITE.get());
                        output.accept(NTBlocks.JADEITE_BLOCK.get());
                        output.accept(NTBlocks.JADEITE_ORE.get());

                        output.accept(NTItems.RHODOCHROSITE.get());
                        output.accept(NTItems.RAW_RHODOCHROSITE.get());
                        output.accept(NTBlocks.RHODOCHROSITE_BLOCK.get());
                        output.accept(NTBlocks.RHODOCHROSITE_ORE.get());

                        output.accept(NTItems.LIGNITE_COAL.get());
                        output.accept(NTBlocks.LIGNITE_BLOCK.get());
                        output.accept(NTBlocks.LIGNITE_ORE.get());
                        output.accept(NTBlocks.DEEPSLATE_LIGNITE_ORE.get());
                        output.accept(NTBlocks.LIGNITE_NETHER_ORE.get());
                        output.accept(NTBlocks.LIGNITE_END_ORE.get());

                        // --- SANDS & SOILS ---
                        output.accept(NTBlocks.BLUE_PODZOL.get());
                        output.accept(NTBlocks.SAFFRON_MOSS.get()); // NEW

                        // Olivine Set
                        output.accept(NTBlocks.OLIVINE_SAND.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE.get());
                        output.accept(NTBlocks.CUT_OLIVINE_SANDSTONE.get());
                        output.accept(NTBlocks.CHISELED_OLIVINE_SANDSTONE.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_STAIRS.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_SLAB.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_WALL.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_FENCE.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_DOOR.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_PRESSURE_PLATE.get());
                        output.accept(NTBlocks.OLIVINE_SANDSTONE_BUTTON.get());

                        // Amber Set (NEW)
                        output.accept(NTBlocks.AMBER_SAND.get());
                        output.accept(NTBlocks.AMBER_SANDSTONE.get());
                        output.accept(NTBlocks.CUT_AMBER_SANDSTONE.get());
                        output.accept(NTBlocks.CHISELED_AMBER_SANDSTONE.get());
                        output.accept(NTBlocks.AMBER_SANDSTONE_STAIRS.get());
                        output.accept(NTBlocks.AMBER_SANDSTONE_SLAB.get());
                        output.accept(NTBlocks.AMBER_SANDSTONE_WALL.get());
                        output.accept(NTBlocks.PETRIFIED_CACTUS.get());

                        // --- WOOD SETS ---
                        String[] woodNames = {"blue_spruce", "teak", "balsa", "mahogany", "aspen"};
                        for (String wood : woodNames) {
                            acceptBlock(output, wood + "_log");
                            acceptBlock(output, wood + "_wood");
                            acceptBlock(output, "stripped_" + wood + "_log");
                            acceptBlock(output, "stripped_" + wood + "_wood");
                            acceptBlock(output, wood + "_planks");
                            acceptBlock(output, wood + "_stairs");
                            acceptBlock(output, wood + "_slab");
                            acceptBlock(output, wood + "_fence");
                            acceptBlock(output, wood + "_fence_gate");
                            acceptBlock(output, wood + "_door");
                            acceptBlock(output, wood + "_trapdoor");
                            acceptBlock(output, wood + "_pressure_plate");
                            acceptBlock(output, wood + "_button");
                            acceptBlock(output, wood + "_leaves");
                            acceptBlock(output, wood + "_sapling");
                            acceptItem(output, wood + "_sign");
                            acceptItem(output, wood + "_hanging_sign");
                            acceptItem(output, wood + "_boat");
                            acceptItem(output, wood + "_chest_boat");
                        }

                        // --- SPECIAL TREES (Autumnal, Paulownia, Myrtle) ---
                        output.accept(NTBlocks.ORANGE_AUTUMNAL_LEAVES.get());
                        output.accept(NTBlocks.ORANGE_AUTUMNAL_PINE_SAPLING.get());
                        output.accept(NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET.get());

                        output.accept(NTBlocks.YELLOW_AUTUMNAL_LEAVES.get());
                        output.accept(NTBlocks.YELLOW_AUTUMNAL_PINE_SAPLING.get());
                        output.accept(NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET.get());

                        output.accept(NTBlocks.PAULOWNIA_LEAVES.get());
                        output.accept(NTBlocks.PAULOWNIA_SAPLING.get());
                        output.accept(NTBlocks.PAULOWNIA_LEAF_CARPET.get());

                        output.accept(NTBlocks.MYRTLE_LEAVES.get());
                        output.accept(NTBlocks.MYRTLE_SAPLING.get());
                        output.accept(NTBlocks.MYRTLE_LEAF_CARPET.get());

                        output.accept(NTBlocks.OLIVE_LEAVES.get());
                        output.accept(NTItems.OLIVE.get());

                        // --- FOLIAGE ---
                        output.accept(NTBlocks.LAVENDER.get());
                        output.accept(NTBlocks.LAVENDER_PETALS.get());
                        output.accept(NTBlocks.HEATHER.get());
                        output.accept(NTBlocks.HEATHER_PETALS.get());
                        output.accept(NTBlocks.ASTER.get());
                        output.accept(NTBlocks.ASTER_PETALS.get());
                        output.accept(NTBlocks.TRILLIUM.get());
                        output.accept(NTBlocks.THISTLE.get());
                        output.accept(NTBlocks.BROMELIAD.get());
                        output.accept(NTBlocks.SILVER_SAGE.get());
                        output.accept(NTBlocks.IRIS.get());
                        output.accept(NTBlocks.BLEEDING_HEART.get());

                        // --- MOB DROPS & SPAWN EGGS ---
                        output.accept(NTItems.BEAR_CLAW.get());
                        output.accept(NTItems.SILVER_FOX_SPAWN_EGG.get());
                        output.accept(NTItems.SPECTACLED_BEAR_SPAWN_EGG.get());
                        output.accept(NTItems.BROWN_BEAR_SPAWN_EGG.get());

                        output.accept(NTItems.SONGBIRD_SPAWN_EGG.get());
                        output.accept(NTItems.FAUN_SPAWN_EGG.get());

                    }).build());

    // Helper methods to look up the registry entries by name string
    private static void acceptBlock(CreativeModeTab.Output output, String name) {
        NTBlocks.BLOCKS.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(name))
                .findFirst().ifPresent(holder -> output.accept(holder.get()));
    }

    private static void acceptItem(CreativeModeTab.Output output, String name) {
        NTItems.ITEMS.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(name))
                .findFirst().ifPresent(holder -> output.accept(holder.get()));
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}