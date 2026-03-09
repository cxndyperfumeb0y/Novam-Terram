package net.candyperfumeboy.novamterram.block;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NTParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, NovamTerram.MOD_ID);

    // Blue Spruce (Original)
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLUE_SPRUCE_LEAF =
            PARTICLE_TYPES.register("blue_spruce_leaf", () -> new SimpleParticleType(false));

    // Autumnal Pine
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ORANGE_LEAF =
            PARTICLE_TYPES.register("orange_leaf", () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> YELLOW_LEAF =
            PARTICLE_TYPES.register("yellow_leaf", () -> new SimpleParticleType(false));

    // Paulownia
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PAULOWNIA_LEAF =
            PARTICLE_TYPES.register("paulownia_leaf", () -> new SimpleParticleType(false));

    // Myrtle
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MYRTLE_LEAF =
            PARTICLE_TYPES.register("myrtle_leaf", () -> new SimpleParticleType(false));


    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}