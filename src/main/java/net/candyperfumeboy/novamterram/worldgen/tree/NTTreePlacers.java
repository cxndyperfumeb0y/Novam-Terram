package net.candyperfumeboy.novamterram.worldgen.tree;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NTTreePlacers {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS =
            DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, NovamTerram.MOD_ID);

    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS =
            DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, NovamTerram.MOD_ID);

    // --- TRUNKS ---
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<CypressTrunkPlacer>> CYPRESS_TRUNK_PLACER =
            TRUNK_PLACERS.register("cypress_trunk_placer", () -> new TrunkPlacerType<>(CypressTrunkPlacer.CODEC));

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<MyrtleTrunkPlacer>> MYRTLE_TRUNK_PLACER =
            TRUNK_PLACERS.register("myrtle_trunk_placer", () -> new TrunkPlacerType<>(MyrtleTrunkPlacer.CODEC));

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<KinkedPineTrunkPlacer>> KINKED_PINE_TRUNK_PLACER =
            TRUNK_PLACERS.register("kinked_pine_trunk_placer", () -> new TrunkPlacerType<>(KinkedPineTrunkPlacer.CODEC));

    // NEW: Ancient Oak Trunk
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<AncientOakTrunkPlacer>> ANCIENT_OAK_TRUNK_PLACER =
            TRUNK_PLACERS.register("ancient_oak_trunk_placer", () -> new TrunkPlacerType<>(AncientOakTrunkPlacer.CODEC));

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<FungalReachTrunkPlacer>> FUNGAL_REACH_TRUNK_PLACER =
            TRUNK_PLACERS.register("fungal_reach_trunk_placer", () -> new TrunkPlacerType<>(FungalReachTrunkPlacer.CODEC));

    // --- FOLIAGE ---
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<MyrtleFoliagePlacer>> MYRTLE_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("myrtle_foliage_placer", () -> new FoliagePlacerType<>(MyrtleFoliagePlacer.CODEC));

    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<KinkedFoliagePlacer>> KINKED_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("kinked_foliage_placer", () -> new FoliagePlacerType<>(KinkedFoliagePlacer.CODEC));

    // NEW: Ancient Oak Foliage
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<AncientOakFoliagePlacer>> ANCIENT_OAK_FOLIAGE_PLACER =
            FOLIAGE_PLACERS.register("ancient_oak_foliage_placer", () -> new FoliagePlacerType<>(AncientOakFoliagePlacer.CODEC));


    public static void register(IEventBus eventBus) {
        TRUNK_PLACERS.register(eventBus);
        FOLIAGE_PLACERS.register(eventBus);
    }
}