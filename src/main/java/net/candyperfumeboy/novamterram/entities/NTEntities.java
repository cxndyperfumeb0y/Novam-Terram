package net.candyperfumeboy.novamterram.entities;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.entities.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.CatVariant;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NTEntities {
    // 1. Entity Type Registry
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, NovamTerram.MOD_ID);

    // 3. Cat Variant Registry
    public static final DeferredRegister<CatVariant> CAT_VARIANTS =
            DeferredRegister.create(Registries.CAT_VARIANT, NovamTerram.MOD_ID);

    // --- Entities ---
    public static final DeferredHolder<EntityType<?>, EntityType<SilverFoxEntity>> SILVER_FOX =
            ENTITY_TYPES.register("silver_fox", () -> EntityType.Builder.of(SilverFoxEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.7F).build("silver_fox"));
    // --- Entities ---
// Locate the HYENA entry in your NTEntities class and update it to this:
    public static final DeferredHolder<EntityType<?>, EntityType<HyenaEntity>> HYENA =
            ENTITY_TYPES.register("hyena", () -> EntityType.Builder.of(HyenaEntity::new, MobCategory.CREATURE)
                    .sized(0.75F, 0.8F) // Hitbox tightened to match the 0.85x scale
                    .build("hyena"));
    public static final DeferredHolder<EntityType<?>, EntityType<DesertFoxEntity>> DESERT_FOX =
            ENTITY_TYPES.register("desert_fox", () -> EntityType.Builder.of(DesertFoxEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.7F).build("desert_fox"));

    public static final DeferredHolder<EntityType<?>, EntityType<FaunEntity>> FAUN =
            ENTITY_TYPES.register("faun", () -> EntityType.Builder.of(FaunEntity::new, MobCategory.CREATURE)
                    .sized(0.8F, 1.6F).build("faun"));

    public static final DeferredHolder<EntityType<?>, EntityType<SongbirdEntity>> SONGBIRD =
            ENTITY_TYPES.register("songbird", () -> EntityType.Builder.of(SongbirdEntity::new, MobCategory.CREATURE)
                    .sized(0.4F, 0.4F).build("songbird"));

    public static final DeferredHolder<EntityType<?>, EntityType<SpectacledBearEntity>> SPECTACLED_BEAR =
            ENTITY_TYPES.register("spectacled_bear", () -> EntityType.Builder.of(SpectacledBearEntity::new, MobCategory.CREATURE)
                    .sized(1.4F, 1.4F).build("spectacled_bear"));

    public static final DeferredHolder<EntityType<?>, EntityType<BrownBearEntity>> BROWN_BEAR =
            ENTITY_TYPES.register("brown_bear", () -> EntityType.Builder.of(BrownBearEntity::new, MobCategory.CREATURE)
                    .sized(1.5F, 1.5F).build("brown_bear"));

    // --- Cat Variants ---
    public static final DeferredHolder<CatVariant, CatVariant> SALEEM =
            CAT_VARIANTS.register("saleem", () -> new CatVariant(ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/cat/saleem.png")));
    public static final DeferredHolder<CatVariant, CatVariant> GOJI =
            CAT_VARIANTS.register("goji", () -> new CatVariant(ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/cat/goji.png")));
    public static final DeferredHolder<CatVariant, CatVariant> PICKLES =
            CAT_VARIANTS.register("pickles", () -> new CatVariant(ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/cat/pickles.png")));
    public static final DeferredHolder<CatVariant, CatVariant> BULLEIT =
            CAT_VARIANTS.register("bulleit", () -> new CatVariant(ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/cat/bulleit.png")));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        CAT_VARIANTS.register(eventBus);
    }
}