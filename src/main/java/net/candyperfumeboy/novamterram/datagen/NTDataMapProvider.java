package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
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
        // --- Furnace Fuels ---
        this.builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(NTItems.LIGNITE_COAL.getId(), new FurnaceFuel(1200), false);


        // --- Compostables (Composter Interaction) ---
        this.builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(NTBlocks.BLUE_SPRUCE_SAPLING.getId(), new Compostable(0.3F), false)
                .add(NTBlocks.BLUE_SPRUCE_LEAVES.getId(), new Compostable(0.3F), false);
    }
}