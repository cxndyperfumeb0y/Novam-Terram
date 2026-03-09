package net.candyperfumeboy.novamterram.entities;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.entities.custom.*;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class NTEntityEvents {

    @EventBusSubscriber(modid = NovamTerram.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            // Use custom attributes for Silver Fox (Speed boost)
            event.put(NTEntities.SILVER_FOX.get(), SilverFoxEntity.createSilverFoxAttributes().build());
            event.put(NTEntities.DESERT_FOX.get(), DesertFoxEntity.createDesertFoxAttributes().build());
            event.put(NTEntities.HYENA.get(), HyenaEntity.createAttributes().build());
            event.put(NTEntities.SPECTACLED_BEAR.get(), SpectacledBearEntity.createAttributes().build());
            event.put(NTEntities.BROWN_BEAR.get(), BrownBearEntity.createAttributes().build());

            event.put(NTEntities.SONGBIRD.get(), SongbirdEntity.createAttributes().build());
            event.put(NTEntities.FAUN.get(), FaunEntity.createAttributes().build());
        }

        @SubscribeEvent
        public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {

            // HYENA: Spawns on Grass/Coarse Dirt (Savanna/Arid biomes)
            event.register(
                    NTEntities.HYENA.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnType, pos, random) -> {
                        boolean validBlock = level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) ||
                                level.getBlockState(pos.below()).is(Blocks.COARSE_DIRT);
                        return validBlock && level.getRawBrightness(pos, 0) > 8;
                    },
                    RegisterSpawnPlacementsEvent.Operation.REPLACE
            );

            // SILVER FOX: Perfectly fine!
            event.register(
                    NTEntities.SILVER_FOX.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnType, pos, random) -> {
                        boolean validBlock = level.getBlockState(pos.below()).is(NTBlocks.BLUE_PODZOL.get()) ||
                                level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON);
                        return validBlock && level.isEmptyBlock(pos);
                    },
                    RegisterSpawnPlacementsEvent.Operation.REPLACE
            );

            // DESERT FOX: Updated to spawn on SAND instead of LEAVES
            event.register(
                    NTEntities.DESERT_FOX.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnType, pos, random) -> {
                        boolean validBlock = level.getBlockState(pos.below()).is(BlockTags.SAND) ||
                                level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON);
                        return validBlock && level.isEmptyBlock(pos);
                    },
                    RegisterSpawnPlacementsEvent.Operation.REPLACE
            );
            event.register(
                    NTEntities.FAUN.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnType, pos, random) -> {
                        boolean validBlock = level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON);
                        return validBlock && level.getRawBrightness(pos, 0) > 8;
                    },
                    RegisterSpawnPlacementsEvent.Operation.REPLACE
            );

            event.register(
                    NTEntities.SONGBIRD.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING,
                    (entityType, level, spawnType, pos, random) -> {
                        boolean validBlock = level.getBlockState(pos.below()).is(BlockTags.LEAVES) ||
                                level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON);
                        return validBlock && level.getRawBrightness(pos, 0) > 8;
                    },
                    RegisterSpawnPlacementsEvent.Operation.REPLACE
            );

            // SPECTACLED BEAR: Spawns on Stone/Tuff/Andesite/Grass (Mountains)
            event.register(
                    NTEntities.SPECTACLED_BEAR.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnType, pos, random) -> {
                        boolean validBlock = level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) ||
                                level.getBlockState(pos.below()).is(Blocks.STONE) ||
                                level.getBlockState(pos.below()).is(Blocks.ANDESITE) ||
                                level.getBlockState(pos.below()).is(Blocks.TUFF);
                        return validBlock && level.isEmptyBlock(pos);
                    },
                    RegisterSpawnPlacementsEvent.Operation.REPLACE
            );

            // BROWN BEAR: Spawns on Grass/Podzol/Dirt (Forests/Taigas)
            event.register(
                    NTEntities.BROWN_BEAR.get(),
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnType, pos, random) -> {
                        boolean validBlock = level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) ||
                                level.getBlockState(pos.below()).is(Blocks.PODZOL) ||
                                level.getBlockState(pos.below()).is(Blocks.COARSE_DIRT);
                        return validBlock && level.isEmptyBlock(pos);
                    },
                    RegisterSpawnPlacementsEvent.Operation.REPLACE
            );
        }
    }
}