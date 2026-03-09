package net.candyperfumeboy.novamterram.item;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.entities.variants.NTBoatEntities;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.item.custom.NTBoatItem;
import net.candyperfumeboy.novamterram.item.custom.OliveItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NTItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NovamTerram.MOD_ID);

    // --- MINERALS ---
    public static final DeferredItem<Item> OPAL = ITEMS.register("opal", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_OPAL = ITEMS.register("raw_opal", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> IOLITE = ITEMS.register("iolite", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_IOLITE = ITEMS.register("raw_iolite", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> KYANITE = ITEMS.register("kyanite", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_KYANITE = ITEMS.register("raw_kyanite", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RHODOCHROSITE = ITEMS.register("rhodochrosite", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_RHODOCHROSITE = ITEMS.register("raw_rhodochrosite", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> JADEITE = ITEMS.register("jadeite", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_JADEITE = ITEMS.register("raw_jadeite", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LIGNITE_COAL = ITEMS.register("lignite_coal", () -> new Item(new Item.Properties()));
    // --- MOB DROPS & SPAWN EGGS ---
    public static final DeferredItem<Item> BEAR_CLAW = ITEMS.register("bear_claw", () -> new Item(new Item.Properties()));

    // Silver Fox: Dark Charcoal Base with the lighter Silver-Tipped highlights
    public static final DeferredItem<DeferredSpawnEggItem> SILVER_FOX_SPAWN_EGG = ITEMS.register("silver_fox_spawn_egg",
            () -> new DeferredSpawnEggItem(NTEntities.SILVER_FOX, 0x2E3033, 0x9BA5AF, new Item.Properties()));

    // Desert Fox: Sandy base with the darker Fennec-ear/tail-tip shading
    public static final DeferredItem<DeferredSpawnEggItem> DESERT_FOX_SPAWN_EGG = ITEMS.register("desert_fox_spawn_egg",
            () -> new DeferredSpawnEggItem(NTEntities.DESERT_FOX, 0xEBD1A6, 0xB88B5B, new Item.Properties()));

    // Spectacled Bear: Deep Black base with the distinct Cream "Spectacle" markings
    public static final DeferredItem<DeferredSpawnEggItem> SPECTACLED_BEAR_SPAWN_EGG = ITEMS.register("spectacled_bear_spawn_egg",
            () -> new DeferredSpawnEggItem(NTEntities.SPECTACLED_BEAR, 0x111111, 0xE8D7B5, new Item.Properties()));

    // Brown Bear: A warm "Grizzly" Brown base with a lighter tan muzzle color
    public static final DeferredItem<DeferredSpawnEggItem> BROWN_BEAR_SPAWN_EGG = ITEMS.register("brown_bear_spawn_egg",
            () -> new DeferredSpawnEggItem(NTEntities.BROWN_BEAR, 0x4B3621, 0xA68B6D, new Item.Properties()));

    // Hyena: A dusty "Savannah" tan base with the iconic Dark Brown spots/stripes color
    public static final DeferredItem<DeferredSpawnEggItem> HYENA_SPAWN_EGG = ITEMS.register("hyena_spawn_egg",
            () -> new DeferredSpawnEggItem(NTEntities.HYENA, 0xBA9F7B, 0x3E2E24, new Item.Properties()));

    // Faun: Deep Forest Brown base with a pale "Fawn" spot/underbelly color
    public static final DeferredItem<DeferredSpawnEggItem> FAUN_SPAWN_EGG = ITEMS.register("faun_spawn_egg",
            () -> new DeferredSpawnEggItem(NTEntities.FAUN, 0x5D4037, 0xD7CCC8, new Item.Properties()));

    // Songbird: Vibrant Sky Blue base with a crisp White/Light Grey wing-tip color
    public static final DeferredItem<DeferredSpawnEggItem> SONGBIRD_SPAWN_EGG = ITEMS.register("songbird_spawn_egg",
            () -> new DeferredSpawnEggItem(NTEntities.SONGBIRD, 0x3498DB, 0xECF0F1, new Item.Properties()));

    public static final DeferredItem<Item> OLIVE = ITEMS.register("olive",
            () -> new OliveItem(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(1)               // Half a drumstick
                            .saturationModifier(0.1f)   // Very low saturation
                            .fast()                     // Eat it quickly like dried kelp
                            .build())));
    // ==========================================
    // WOOD ITEM HELPER
    // ==========================================
    public static void registerWoodItems(String name) {
        // Signs
        ITEMS.register(name + "_sign", () -> new SignItem(new Item.Properties().stacksTo(16),
                NTBlocks.BLOCKS.getEntries().stream().filter(e -> e.getId().getPath().equals(name + "_sign")).findFirst().get().get(),
                NTBlocks.BLOCKS.getEntries().stream().filter(e -> e.getId().getPath().equals(name + "_wall_sign")).findFirst().get().get()));

        ITEMS.register(name + "_hanging_sign", () -> new HangingSignItem(
                NTBlocks.BLOCKS.getEntries().stream().filter(e -> e.getId().getPath().equals(name + "_hanging_sign")).findFirst().get().get(),
                NTBlocks.BLOCKS.getEntries().stream().filter(e -> e.getId().getPath().equals(name + "_wall_hanging_sign")).findFirst().get().get(),
                new Item.Properties().stacksTo(16)));

        // Boats
        ITEMS.register(name + "_boat", () -> new NTBoatItem(false,
                NTBoatEntities.getBoatEntity(name, false), new Item.Properties().stacksTo(1)));

        ITEMS.register(name + "_chest_boat", () -> new NTBoatItem(true,
                NTBoatEntities.getBoatEntity(name, true), new Item.Properties().stacksTo(1)));
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}