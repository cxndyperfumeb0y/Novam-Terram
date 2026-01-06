package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.worldgen.NTBiomeModifiers;
import net.candyperfumeboy.novamterram.worldgen.NTConfiguredFeatures;
import net.candyperfumeboy.novamterram.worldgen.NTPlacedFeatures;
import net.candyperfumeboy.novamterram.worldgen.biome.NTBiomeFactory;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class NTDatapackProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()

            .add(Registries.CONFIGURED_FEATURE, NTConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, NTPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, NTBiomeModifiers::bootstrap)
                .add(Registries.BIOME, NTBiomeFactory::bootstrap);





    public NTDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(NovamTerram.MOD_ID));
    }
}