package net.candyperfumeboy.novamterram.entities.variants;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class NTBoatEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, NovamTerram.MOD_ID);

    // --- BLUE SPRUCE ---
    public static final DeferredHolder<EntityType<?>, EntityType<NTBoatEntity>> BLUE_SPRUCE_BOAT =
            registerBoat("blue_spruce_boat");
    public static final DeferredHolder<EntityType<?>, EntityType<NTChestBoatEntity>> BLUE_SPRUCE_CHEST_BOAT =
            registerChestBoat("blue_spruce_chest_boat");

    // --- TEAK ---
    public static final DeferredHolder<EntityType<?>, EntityType<NTBoatEntity>> TEAK_BOAT =
            registerBoat("teak_boat");
    public static final DeferredHolder<EntityType<?>, EntityType<NTChestBoatEntity>> TEAK_CHEST_BOAT =
            registerChestBoat("teak_chest_boat");

    // --- BALSA ---
    public static final DeferredHolder<EntityType<?>, EntityType<NTBoatEntity>> BALSA_BOAT =
            registerBoat("balsa_boat");
    public static final DeferredHolder<EntityType<?>, EntityType<NTChestBoatEntity>> BALSA_CHEST_BOAT =
            registerChestBoat("balsa_chest_boat");

    // --- MAHOGANY ---
    public static final DeferredHolder<EntityType<?>, EntityType<NTBoatEntity>> MAHOGANY_BOAT =
            registerBoat("mahogany_boat");
    public static final DeferredHolder<EntityType<?>, EntityType<NTChestBoatEntity>> MAHOGANY_CHEST_BOAT =
            registerChestBoat("mahogany_chest_boat");


    // --- MAHOGANY ---
    public static final DeferredHolder<EntityType<?>, EntityType<NTBoatEntity>> ASPEN_BOAT =
            registerBoat("aspen_boat");
    public static final DeferredHolder<EntityType<?>, EntityType<NTChestBoatEntity>> ASPEN_CHEST_BOAT =
            registerChestBoat("aspen_chest_boat");


    // ==========================================
    // THE HELPER FOR NTITEMS
    // ==========================================
    // Changed the return type to ? extends Boat to satisfy the compiler without casting
    public static Supplier<? extends EntityType<? extends Boat>> getBoatEntity(String name, boolean isChestBoat) {
        return switch (name) {
            case "blue_spruce" -> isChestBoat ? BLUE_SPRUCE_CHEST_BOAT : BLUE_SPRUCE_BOAT;
            case "teak" -> isChestBoat ? TEAK_CHEST_BOAT : TEAK_BOAT;
            case "balsa" -> isChestBoat ? BALSA_CHEST_BOAT : BALSA_BOAT;
            case "mahogany" -> isChestBoat ? MAHOGANY_CHEST_BOAT : MAHOGANY_BOAT;
            case "aspen" -> isChestBoat ? ASPEN_CHEST_BOAT : ASPEN_BOAT;

            default -> throw new IllegalArgumentException("No boat entity registered for wood type: " + name);
        };
    }

    private static DeferredHolder<EntityType<?>, EntityType<NTBoatEntity>> registerBoat(String name) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.<NTBoatEntity>of(NTBoatEntity::new, MobCategory.MISC)
                .sized(1.375F, 0.5625F).build(name));
    }

    private static DeferredHolder<EntityType<?>, EntityType<NTChestBoatEntity>> registerChestBoat(String name) {
        // Change ::of to ::new here
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.<NTChestBoatEntity>of(NTChestBoatEntity::new, MobCategory.MISC)
                .sized(1.375F, 0.5625F).build(name));
    }
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}