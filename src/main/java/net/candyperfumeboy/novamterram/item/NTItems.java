package net.candyperfumeboy.novamterram.item;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.item.custom.OliveItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Standalone items. Block items are registered automatically in NTBlocks,
 * and wood items (signs, boats) are in NTWoodSet.
 */
public class NTItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NovamTerram.MOD_ID);

    // --- MINERALS ---
    public static final DeferredItem<Item> OPAL = ITEMS.registerSimpleItem("opal");
    public static final DeferredItem<Item> RAW_OPAL = ITEMS.registerSimpleItem("raw_opal");
    public static final DeferredItem<Item> IOLITE = ITEMS.registerSimpleItem("iolite");
    public static final DeferredItem<Item> RAW_IOLITE = ITEMS.registerSimpleItem("raw_iolite");
    public static final DeferredItem<Item> KYANITE = ITEMS.registerSimpleItem("kyanite");
    public static final DeferredItem<Item> RAW_KYANITE = ITEMS.registerSimpleItem("raw_kyanite");
    public static final DeferredItem<Item> RHODOCHROSITE = ITEMS.registerSimpleItem("rhodochrosite");
    public static final DeferredItem<Item> RAW_RHODOCHROSITE = ITEMS.registerSimpleItem("raw_rhodochrosite");
    public static final DeferredItem<Item> JADEITE = ITEMS.registerSimpleItem("jadeite");
    public static final DeferredItem<Item> RAW_JADEITE = ITEMS.registerSimpleItem("raw_jadeite");
    public static final DeferredItem<Item> LIGNITE_COAL = ITEMS.registerSimpleItem("lignite_coal");

    // --- MOB DROPS ---
    public static final DeferredItem<Item> BEAR_CLAW = ITEMS.registerSimpleItem("bear_claw");

    // --- FOOD ---
    public static final DeferredItem<Item> OLIVE = ITEMS.register("olive",
            () -> new OliveItem(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(1)               // Half a drumstick
                            .saturationModifier(0.1f)   // Very low saturation
                            .fast()                     // Eat it quickly like dried kelp
                            .build())));

    // --- SPAWN EGGS (name, mob, base colour, spot colour) ---
    public static final DeferredItem<DeferredSpawnEggItem> SILVER_FOX_SPAWN_EGG =
            spawnEgg("silver_fox_spawn_egg", NTEntities.SILVER_FOX, 0x2E3033, 0x9BA5AF);
    public static final DeferredItem<DeferredSpawnEggItem> DESERT_FOX_SPAWN_EGG =
            spawnEgg("desert_fox_spawn_egg", NTEntities.DESERT_FOX, 0xEBD1A6, 0xB88B5B);
    public static final DeferredItem<DeferredSpawnEggItem> SPECTACLED_BEAR_SPAWN_EGG =
            spawnEgg("spectacled_bear_spawn_egg", NTEntities.SPECTACLED_BEAR, 0x111111, 0xE8D7B5);
    public static final DeferredItem<DeferredSpawnEggItem> BROWN_BEAR_SPAWN_EGG =
            spawnEgg("brown_bear_spawn_egg", NTEntities.BROWN_BEAR, 0x4B3621, 0xA68B6D);
    public static final DeferredItem<DeferredSpawnEggItem> HYENA_SPAWN_EGG =
            spawnEgg("hyena_spawn_egg", NTEntities.HYENA, 0xBA9F7B, 0x3E2E24);
    public static final DeferredItem<DeferredSpawnEggItem> FAUN_SPAWN_EGG =
            spawnEgg("faun_spawn_egg", NTEntities.FAUN, 0x5D4037, 0xD7CCC8);
    public static final DeferredItem<DeferredSpawnEggItem> SONGBIRD_SPAWN_EGG =
            spawnEgg("songbird_spawn_egg", NTEntities.SONGBIRD, 0x3498DB, 0xECF0F1);

    private static DeferredItem<DeferredSpawnEggItem> spawnEgg(
            String name, Supplier<? extends EntityType<? extends Mob>> mob, int baseColor, int spotColor) {
        return ITEMS.register(name, () -> new DeferredSpawnEggItem(mob, baseColor, spotColor, new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
