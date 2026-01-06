package net.candyperfumeboy.novamterram.item;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.entities.NTBoatEntities;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.item.custom.FuelItem;
import net.candyperfumeboy.novamterram.item.custom.NTBoatItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NTItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NovamTerram.MOD_ID);

    public static final DeferredItem<Item> OPAL = ITEMS.register("opal", () ->
            new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_OPAL = ITEMS.register("raw_opal", () ->
            new Item(new Item.Properties()));
    public static final DeferredItem<Item> LIGNITE_COAL = ITEMS.register("lignite_coal", () ->
            new Item(new Item.Properties()));
    public static final DeferredItem<Item> SILVER_FOX_SPAWN_EGG = ITEMS.register("silver_fox_spawn_egg",
            () -> new DeferredSpawnEggItem(NTEntities.SILVER_FOX, 0x313439, 0xA8B1BC, new Item.Properties()));
    public static final DeferredItem<DeferredSpawnEggItem> SPECTACLED_BEAR_SPAWN_EGG = ITEMS.register("spectacled_bear_spawn_egg",
            () -> new DeferredSpawnEggItem(NTEntities.SPECTACLED_BEAR, 0x1B1B1B, 0xD9C5A3, new Item.Properties()));

    public static final DeferredItem<Item> BEAR_CLAW = ITEMS.register("bear_claw",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> BLUE_SPRUCE_SIGN_ITEM = ITEMS.register("blue_spruce_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16), NTBlocks.BLUE_SPRUCE_SIGN.get(), NTBlocks.BLUE_SPRUCE_WALL_SIGN.get()));

    public static final DeferredItem<Item> BLUE_SPRUCE_HANGING_SIGN_ITEM = ITEMS.register("blue_spruce_hanging_sign",
            () -> new HangingSignItem(NTBlocks.BLUE_SPRUCE_HANGING_SIGN.get(), NTBlocks.BLUE_SPRUCE_WALL_HANGING_SIGN.get(), new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> BLUE_SPRUCE_BOAT = ITEMS.register("blue_spruce_boat",
            () -> new NTBoatItem(false, NTBoatEntities.BLUE_SPRUCE_BOAT, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> BLUE_SPRUCE_CHEST_BOAT = ITEMS.register("blue_spruce_chest_boat",
            () -> new NTBoatItem(true, NTBoatEntities.BLUE_SPRUCE_CHEST_BOAT, new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
