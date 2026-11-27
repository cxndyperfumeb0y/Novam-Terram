package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.worldgen.biome.features.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NTFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, NovamTerram.MOD_ID);

    // This registers the "AndeanSpireFeature" logic under the name "andean_spire"
    public static final DeferredHolder<Feature<?>, AndeanSpires> ANDEAN_SPIRE =
            FEATURES.register("andean_spire", () -> new AndeanSpires(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, SilverFoxDen> SILVER_FOX_DEN =
            FEATURES.register("silver_fox_den_feature",
                    () -> new SilverFoxDen(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, SpikeFeature> SPIKE_FEATURE =
            FEATURES.register("spike_feature",
                    () -> new SpikeFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, PeridotSpring> PERIDOT_SPRING =
            FEATURES.register("peridot_spring_feature",
                    () -> new PeridotSpring(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, GeothermalVent> GEOTHERMAL_VENT =
            FEATURES.register("geothermal_vent_feature",
                    () -> new GeothermalVent(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, BearDen> BEAR_DEN =
            FEATURES.register("bear_den_feature",
                    () -> new BearDen(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, BluebellFloor> BLUEBELL_FLOOR =
            FEATURES.register("bluebell_floor", () -> new BluebellFloor(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, GreatOakRing> GREAT_OAK_RING =
            FEATURES.register("great_oak_ring", () -> new GreatOakRing(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}