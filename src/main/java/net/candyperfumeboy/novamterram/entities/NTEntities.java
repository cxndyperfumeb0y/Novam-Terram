package net.candyperfumeboy.novamterram.entities;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.entities.variants.SilverFoxEntity;
import net.candyperfumeboy.novamterram.entities.variants.SpectacledBearEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NTEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, NovamTerram.MOD_ID);

    // FIX: Changed DeferredBlock to DeferredHolder
    public static final DeferredHolder<EntityType<?>, EntityType<SilverFoxEntity>> SILVER_FOX =
            ENTITY_TYPES.register("silver_fox", () -> EntityType.Builder.of(SilverFoxEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.7F)
                    .build("silver_fox"));
    // Add this to your ENTITY_TYPES registry in NTEntities.java
    public static final DeferredHolder<EntityType<?>, EntityType<SpectacledBearEntity>> SPECTACLED_BEAR =
            ENTITY_TYPES.register("spectacled_bear", () -> EntityType.Builder.of(SpectacledBearEntity::new, MobCategory.CREATURE)
                    .sized(1.3F, 1.4F) // Slightly smaller/leaner than a Polar Bear
                    .build("spectacled_bear"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}