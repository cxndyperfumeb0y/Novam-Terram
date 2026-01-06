package net.candyperfumeboy.novamterram.worldgen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.worldgen.structure.BlueSpruceVillageProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class NTProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, NovamTerram.MOD_ID);

    public static final Supplier<StructureProcessorType<BlueSpruceVillageProcessor>> BLUE_SPRUCE_PROCESSOR_TYPE =
            PROCESSORS.register("blue_spruce_village", () -> () -> BlueSpruceVillageProcessor.CODEC);}