package net.candyperfumeboy.novamterram.entities;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.entities.variants.SpectacledBearEntity;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = NovamTerram.MOD_ID)
public class NTEntityEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(NTEntities.SILVER_FOX.get(), Fox.createAttributes().build());
        event.put(NTEntities.SPECTACLED_BEAR.get(), SpectacledBearEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent event) {
        event.register(
                NTEntities.SILVER_FOX.get(),
                net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND,
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                net.minecraft.world.entity.animal.Animal::checkAnimalSpawnRules,
                net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }

    // NEW: Arctic Hunter Logic
    @SubscribeEvent
    public static void onSilverFoxTick(EntityTickEvent.Post event) {
        // We only care about YOUR Silver Fox entity type
        if (event.getEntity().getType() == NTEntities.SILVER_FOX.get() && event.getEntity() instanceof Fox fox) {

// Inside onSilverFoxTick
            if (fox.isPouncing() && fox.onGround()) {
                BlockPos pos = fox.blockPosition();

                // 2. Updated Check: Now looks for Snow OR your unique Blue Podzol
                boolean isOnArcticGround = fox.level().getBlockState(pos).is(Blocks.SNOW) ||
                        fox.level().getBlockState(pos.below()).is(Blocks.SNOW_BLOCK) ||
                        fox.level().getBlockState(pos.below()).is(NTBlocks.BLUE_PODZOL.get()); // The new check!

                if (isOnArcticGround && !fox.level().isClientSide) {
                    if (fox.getRandom().nextFloat() < 0.15F) {
                        spawnArcticTreasure(fox);
                    }
                }
            }
        }
    }

    private static void spawnArcticTreasure(Fox fox) {
        RandomSource random = fox.getRandom();
        ItemStack gift;

        int roll = random.nextInt(100);
        if (roll < 40) gift = new ItemStack(Items.SWEET_BERRIES, random.nextInt(2) + 1);
        else if (roll < 70) gift = new ItemStack(Items.GLOW_BERRIES, 1);
        else if (roll < 90) gift = new ItemStack(Items.IRON_NUGGET, random.nextInt(3) + 1);
        else if (roll < 98) gift = new ItemStack(Items.RABBIT_FOOT);
        else gift = new ItemStack(NTItems.OPAL.get()); // The ultra-rare arctic find!

        fox.spawnAtLocation(gift);
        fox.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);
    }



}