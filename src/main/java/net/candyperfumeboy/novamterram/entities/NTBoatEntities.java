package net.candyperfumeboy.novamterram.entities;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.entities.variants.NTBoatEntity;
import net.candyperfumeboy.novamterram.entities.variants.NTChestBoatEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NTBoatEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, NovamTerram.MOD_ID);

    // Regular Boat
    public static final DeferredHolder<EntityType<?>, EntityType<NTBoatEntity>> BLUE_SPRUCE_BOAT =
            ENTITY_TYPES.register("blue_spruce_boat", () -> EntityType.Builder.<NTBoatEntity>of(NTBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).build("blue_spruce_boat"));

    public static final DeferredHolder<EntityType<?>, EntityType<NTChestBoatEntity>> BLUE_SPRUCE_CHEST_BOAT =
            ENTITY_TYPES.register("blue_spruce_chest_boat", () -> EntityType.Builder.<NTChestBoatEntity>of(NTChestBoatEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).build("blue_spruce_chest_boat"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

