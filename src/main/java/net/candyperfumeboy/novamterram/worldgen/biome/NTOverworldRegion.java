package net.candyperfumeboy.novamterram.worldgen.biome;


import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.util.function.Consumer;

public class NTOverworldRegion extends Region {
    public NTOverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }
    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();


        // Exact Vanilla Taiga Parameters:
        // Temperature: COOL, Humidity: NEUTRAL, Continentalness: INLAND
        // --- BLUE SPRUCE TAIGA (Old Growth Style) ---
        new ParameterUtils.ParameterPointListBuilder()
                // Broaden these as much as possible to create huge "Old Growth" blobs
                .temperature(ParameterUtils.Temperature.span(ParameterUtils.Temperature.COOL, ParameterUtils.Temperature.NEUTRAL))
                .humidity(ParameterUtils.Humidity.span(ParameterUtils.Humidity.NEUTRAL, ParameterUtils.Humidity.WET))
                .continentalness(ParameterUtils.Continentalness.INLAND, ParameterUtils.Continentalness.FAR_INLAND)
                .erosion(ParameterUtils.Erosion.span(ParameterUtils.Erosion.EROSION_1, ParameterUtils.Erosion.EROSION_3))
                .depth(ParameterUtils.Depth.SURFACE)
// CRITICAL: Wide weirdness span stops the "sliver" effect
                .weirdness(Climate.Parameter.span(-0.5f, 0.5f))
                .build().forEach(point -> builder.add(point, NTBiomes.BLUE_SPRUCE_TAIGA));

        // Inside your addBiomes method, replace the Olivine Beach entry:
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.NEUTRAL)
                .continentalness(ParameterUtils.Continentalness.COAST)
                .depth(ParameterUtils.Depth.SURFACE) // <--- ENSURE THIS IS SURFACE
                .build().forEach(point -> builder.add(point, NTBiomes.OLIVINE_BEACH));


        new ParameterUtils.ParameterPointListBuilder()
                .temperature(Climate.Parameter.span(0.4f, 1.0f))  // Warm to Hot
                .humidity(Climate.Parameter.span(0.3f, 0.8f))     // Humid
                // Keep it in the Mushroom range for that "isolated sea" feel
               // .continentalness(Climate.Parameter.span(-1.2f, -1.05f))
                .continentalness(ParameterUtils.Continentalness.OCEAN, ParameterUtils.Continentalness.COAST)
                // Use Erosion 2 for a thick, solid island
                .erosion(ParameterUtils.Erosion.EROSION_2)
                .depth(ParameterUtils.Depth.SURFACE)
                // Weirdness LOW (0.0) creates a singular mound rather than peaks
                .weirdness(Climate.Parameter.span(0.8f, 1.0f))
                .build().forEach(point -> builder.add(point, NTBiomes.AEGEAN_ARCHIPELAGO));

        new ParameterUtils.ParameterPointListBuilder()
                .temperature(Climate.Parameter.span(-0.2f, 0.5f)) // Cool to Warm
                .humidity(Climate.Parameter.span(-0.5f, 0.2f))    // Dry to Normal
                .continentalness(Climate.Parameter.span(0.3f, 0.9f)) // Inland areas
                // 3. Shape: Keep Erosion 3 for your rolling hills
                .erosion(ParameterUtils.Erosion.EROSION_3)
                .depth(ParameterUtils.Depth.SURFACE)

                // 4. Weirdness: Set to 'Mid' to 'High' to avoid competing with flat Plains
                .weirdness(Climate.Parameter.span(0.0f, 0.4f))
                .build().forEach(point -> builder.add(point, NTBiomes.HEATH));
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM)
                .humidity(ParameterUtils.Humidity.DRY)
                .continentalness(ParameterUtils.Continentalness.FAR_INLAND)
                // Shift erosion slightly so it doesn't perfectly overlap Badlands
                .erosion(ParameterUtils.Erosion.EROSION_0)
                .depth(ParameterUtils.Depth.SURFACE)
                // High weirdness (0.5 to 1.0) targets the actual mountain peaks
                .weirdness(Climate.Parameter.span(0.5f, 1.5f))

                .build().forEach(point -> builder.add(point, NTBiomes.ANDES));
        builder.build().forEach(mapper);
    }
}