package net.candyperfumeboy.novamterram.entities;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.entities.custom.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.function.Predicate;

public class NTEntityEvents {

    @EventBusSubscriber(modid = NovamTerram.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            // The foxes have their own attributes (speed boost)
            event.put(NTEntities.SILVER_FOX.get(), SilverFoxEntity.createSilverFoxAttributes().build());
            event.put(NTEntities.DESERT_FOX.get(), DesertFoxEntity.createDesertFoxAttributes().build());
            event.put(NTEntities.HYENA.get(), HyenaEntity.createAttributes().build());
            event.put(NTEntities.FAUN.get(), FaunEntity.createAttributes().build());
            event.put(NTEntities.SONGBIRD.get(), SongbirdEntity.createAttributes().build());
            event.put(NTEntities.SPECTACLED_BEAR.get(), SpectacledBearEntity.createAttributes().build());
            event.put(NTEntities.BROWN_BEAR.get(), BrownBearEntity.createAttributes().build());
        }

        // Which ground each mob can naturally spawn on. All of them also need daylight-level light,
        // and the game already checks there is enough empty space (ON_GROUND).
        @SubscribeEvent
        public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
            spawnOn(event, NTEntities.HYENA.get(), ground ->
                    ground.is(BlockTags.ANIMALS_SPAWNABLE_ON) || ground.is(Blocks.COARSE_DIRT));

            spawnOn(event, NTEntities.SILVER_FOX.get(), ground ->
                    isForestFloor(ground) || ground.is(BlockTags.FOXES_SPAWNABLE_ON));

            spawnOn(event, NTEntities.DESERT_FOX.get(), ground ->
                    ground.is(BlockTags.SAND) || ground.is(BlockTags.ANIMALS_SPAWNABLE_ON));

            spawnOn(event, NTEntities.FAUN.get(), ModBusEvents::isForestFloor);

            // Leaves only allow ocelots and parrots to spawn on them, so songbirds start on the
            // ground and fly up into the trees
            spawnOn(event, NTEntities.SONGBIRD.get(), ModBusEvents::isForestFloor);

            spawnOn(event, NTEntities.SPECTACLED_BEAR.get(), ground ->
                    ground.is(BlockTags.ANIMALS_SPAWNABLE_ON) || ground.is(Blocks.STONE)
                            || ground.is(Blocks.ANDESITE) || ground.is(Blocks.TUFF));

            spawnOn(event, NTEntities.BROWN_BEAR.get(), ModBusEvents::isForestFloor);
        }

        /** Grass and the other ground blocks found in forests, including the Novam Terram ones. */
        private static boolean isForestFloor(BlockState ground) {
            return ground.is(BlockTags.ANIMALS_SPAWNABLE_ON)
                    || ground.is(Blocks.PODZOL)
                    || ground.is(Blocks.COARSE_DIRT)
                    || ground.is(NTBlocks.BLUE_PODZOL.get())
                    || ground.is(NTBlocks.SAFFRON_MOSS.get());
        }

        private static <T extends Entity> void spawnOn(RegisterSpawnPlacementsEvent event, EntityType<T> type,
                                                       Predicate<BlockState> validGround) {
            event.register(
                    type,
                    SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (entityType, level, spawnType, pos, random) ->
                            validGround.test(level.getBlockState(pos.below())) && level.getRawBrightness(pos, 0) > 8,
                    RegisterSpawnPlacementsEvent.Operation.REPLACE
            );
        }
    }
}
