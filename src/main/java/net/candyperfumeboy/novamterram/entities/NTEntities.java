package net.candyperfumeboy.novamterram.entities;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.entities.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class NTEntities {
    // 1. Entity Type Registry
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, NovamTerram.MOD_ID);

    // 2. Cat Variant Registry
    public static final DeferredRegister<CatVariant> CAT_VARIANTS =
            DeferredRegister.create(Registries.CAT_VARIANT, NovamTerram.MOD_ID);

    // --- Entities ---
    // sized = hitbox (width, height), eyeHeight = where the mob sees and suffocates from,
    // clientTrackingRange = how far away players can see it, in chunks (vanilla animals use 8-10)

    public static final DeferredHolder<EntityType<?>, EntityType<SilverFoxEntity>> SILVER_FOX =
            creature("silver_fox", SilverFoxEntity::new, builder -> builder
                    .sized(0.6F, 0.7F).eyeHeight(0.4F).clientTrackingRange(8)
                    .immuneTo(Blocks.SWEET_BERRY_BUSH));

    public static final DeferredHolder<EntityType<?>, EntityType<DesertFoxEntity>> DESERT_FOX =
            creature("desert_fox", DesertFoxEntity::new, builder -> builder
                    .sized(0.6F, 0.7F).eyeHeight(0.4F).clientTrackingRange(8)
                    .immuneTo(Blocks.SWEET_BERRY_BUSH));

    public static final DeferredHolder<EntityType<?>, EntityType<HyenaEntity>> HYENA =
            creature("hyena", HyenaEntity::new, builder -> builder
                    .sized(0.75F, 0.95F).eyeHeight(0.8F).clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<FaunEntity>> FAUN =
            creature("faun", FaunEntity::new, builder -> builder
                    .sized(0.8F, 1.9F).eyeHeight(1.8F).clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<SongbirdEntity>> SONGBIRD =
            creature("songbird", SongbirdEntity::new, builder -> builder
                    .sized(0.4F, 0.5F).eyeHeight(0.35F).clientTrackingRange(8));

    public static final DeferredHolder<EntityType<?>, EntityType<SpectacledBearEntity>> SPECTACLED_BEAR =
            creature("spectacled_bear", SpectacledBearEntity::new, builder -> builder
                    .sized(1.4F, 1.4F).clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<BrownBearEntity>> BROWN_BEAR =
            creature("brown_bear", BrownBearEntity::new, builder -> builder
                    .sized(1.5F, 1.5F).clientTrackingRange(10));

    // --- Cat Variants ---
    public static final DeferredHolder<CatVariant, CatVariant> SALEEM =
            CAT_VARIANTS.register("saleem", () -> new CatVariant(ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/cat/saleem.png")));
    public static final DeferredHolder<CatVariant, CatVariant> GOJI =
            CAT_VARIANTS.register("goji", () -> new CatVariant(ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/cat/goji.png")));
    public static final DeferredHolder<CatVariant, CatVariant> PICKLES =
            CAT_VARIANTS.register("pickles", () -> new CatVariant(ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/cat/pickles.png")));
    public static final DeferredHolder<CatVariant, CatVariant> BULLEIT =
            CAT_VARIANTS.register("bulleit", () -> new CatVariant(ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/cat/bulleit.png")));

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> creature(
            String name, EntityType.EntityFactory<T> factory, UnaryOperator<EntityType.Builder<T>> settings) {
        return ENTITY_TYPES.register(name,
                () -> settings.apply(EntityType.Builder.of(factory, MobCategory.CREATURE)).build(name));
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        CAT_VARIANTS.register(eventBus);
    }
}
