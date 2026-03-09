package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

import static org.apache.commons.lang3.ArrayUtils.add;

public class NTDataMapProvider extends DataMapProvider {
    protected NTDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        // ==========================================
        // FURNACE FUELS
        // ==========================================
        this.builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(NTItems.LIGNITE_COAL.getId(), new FurnaceFuel(1200), false);

        // ==========================================
        // COMPOSTABLES
        // ==========================================
        var composters = this.builder(NeoForgeDataMaps.COMPOSTABLES);

        // --- Standard Wood Sets Loop ---
        String[] woodNames = {"blue_spruce", "teak", "balsa", "mahogany", "aspen"};
        for (String wood : woodNames) {
            composters.add(getId(wood + "_sapling"), new Compostable(0.3F), false);
            composters.add(getId(wood + "_leaves"), new Compostable(0.3F), false);
        }

        // --- Aegean Flora (Olive) ---
        composters.add(NTItems.OLIVE.getId(), new Compostable(0.65F), false);
        composters.add(NTBlocks.OLIVE_LEAVES.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.OLIVE_HANGING.getId(), new Compostable(0.5F), false);

        // --- Autumnal Pine (Orange & Yellow) ---
        composters.add(NTBlocks.ORANGE_AUTUMNAL_PINE_SAPLING.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.YELLOW_AUTUMNAL_PINE_SAPLING.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.ORANGE_AUTUMNAL_LEAVES.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.YELLOW_AUTUMNAL_LEAVES.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET.getId(), new Compostable(0.3F), false);

        // --- Paulownia ---
        composters.add(NTBlocks.PAULOWNIA_SAPLING.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.PAULOWNIA_LEAVES.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.PAULOWNIA_LEAF_CARPET.getId(), new Compostable(0.3F), false);

        // --- White Myrtle ---
        composters.add(NTBlocks.WHITE_MYRTLE_SAPLING.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.WHITE_MYRTLE_LEAVES.getId(), new Compostable(0.3F), false);

// --- Red Myrtle ---
        composters.add(NTBlocks.RED_MYRTLE_SAPLING.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.RED_MYRTLE_LEAVES.getId(), new Compostable(0.3F), false);

// --- Purple Myrtle ---
        composters.add(NTBlocks.PURPLE_MYRTLE_SAPLING.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.PURPLE_MYRTLE_LEAVES.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.MYRTLE_LEAF_CARPET.getId(), new Compostable(0.3F), false);

        // --- Flowers (0.65F) & Petals (0.3F) ---
        composters.add(NTBlocks.HEATHER.getId(), new Compostable(0.65F), false);
        composters.add(NTBlocks.HEATHER_PETALS.getId(), new Compostable(0.3F), false);

        composters.add(NTBlocks.LAVENDER.getId(), new Compostable(0.65F), false);
        composters.add(NTBlocks.LAVENDER_PETALS.getId(), new Compostable(0.3F), false);
        composters.add(NTBlocks.BLUEBELL.getId(), new Compostable(0.65F), false);
        composters.add(NTBlocks.PURPLE_BLUEBELL.getId(), new Compostable(0.65F), false);

        composters.add(NTBlocks.ASTER.getId(), new Compostable(0.65F), false);
        composters.add(NTBlocks.ASTER_PETALS.getId(), new Compostable(0.3F), false);

        composters.add(NTBlocks.TRILLIUM.getId(), new Compostable(0.65F), false);
        composters.add(NTBlocks.THISTLE.getId(), new Compostable(0.65F), false);
        composters.add(NTBlocks.BROMELIAD.getId(), new Compostable(0.65F), false);

        composters.add(NTBlocks.SILVER_SAGE.getId(), new Compostable(0.65F), false);
        composters.add(NTBlocks.BLEEDING_HEART.getId(), new Compostable(0.65F), false);
        composters.add(NTBlocks.IRIS.getId(), new Compostable(0.65F), false);
    }

    /**
     * Helper to get the ResourceLocation ID of a block from your registry by name.
     */
    private ResourceLocation getId(String name) {
        return NTBlocks.BLOCKS.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(name))
                .findFirst()
                .map(holder -> holder.getId())
                .orElseThrow(() -> new RuntimeException("Could not find block ID for DataMap: " + name));
    }
}