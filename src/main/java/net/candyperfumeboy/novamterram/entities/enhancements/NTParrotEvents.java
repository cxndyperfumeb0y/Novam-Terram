package net.candyperfumeboy.novamterram.entities.enhancements;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Comparator;
import java.util.List;

@EventBusSubscriber(modid = "novamterram")
public class NTParrotEvents {

    @SubscribeEvent
    public static void onParrotTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Parrot parrot) || parrot.level().isClientSide) return;

        Level level = parrot.level();

        // 1. SCOUT & SPIDER-SENSE LOGIC
        if (parrot.isPassenger() && parrot.getVehicle() instanceof Player player) {
            if (parrot.tickCount % 40 == 0) {
                AABB area = player.getBoundingBox().inflate(16.0D);
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area,
                        e -> (e instanceof Monster || e instanceof Ocelot) && e.isAlive());

                if (!targets.isEmpty()) {
                    targets.stream()
                            .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)))
                            .ifPresent(closest -> {
                                parrot.getLookControl().setLookAt(closest, 30.0F, 30.0F);
                                closest.addEffect(new MobEffectInstance(MobEffects.GLOWING, 45, 0, false, false));

                                // Extra panic if it's a spider
                                if (closest instanceof Spider) {
                                    parrot.playAmbientSound(); // High pitched warning
                                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20, 0, false, false)); // Brief visual jitter
                                }
                            });
                }
            }

            // 2. RAIN-DANCER (Weather Interaction)
            if (level.isRainingAt(parrot.blockPosition()) && parrot.tickCount % 100 == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0, false, false, true));
            }

            // 3. FRUIT HARVESTER
            if (parrot.tickCount % 40 == 0) {
                BlockPos headPos = player.blockPosition().above(2);
                for (BlockPos pos : BlockPos.betweenClosed(headPos.offset(-1, -1, -1), headPos.offset(1, 1, 1))) {
                    BlockState state = level.getBlockState(pos);
                    if (state.is(Blocks.COCOA) && state.getValue(net.minecraft.world.level.block.CocoaBlock.AGE) >= 2) {
                        level.destroyBlock(pos, true, player);
                        break;
                    }
                }
            }
        }

        // GROUND LOGIC
        if (!parrot.isPassenger() && !parrot.isOrderedToSit() && parrot.onGround()) {

            // 4. FORAGER LOGIC
            if (parrot.getRandom().nextFloat() < 0.0005F) {
                BlockState stateBelow = level.getBlockState(parrot.blockPosition().below());
                if (stateBelow.is(BlockTags.DIRT) || stateBelow.is(Blocks.GRASS_BLOCK)) {
                    Holder<Biome> biome = level.getBiome(parrot.blockPosition());
                    Item foundItem = (biome.is(BiomeTags.IS_JUNGLE)) ? Items.COCOA_BEANS : Items.WHEAT_SEEDS;
                    parrot.spawnAtLocation(foundItem);
                    level.levelEvent(2001, parrot.blockPosition(), Block.getId(stateBelow));
                }
            }

            // 5. THE CHORUS
            if (parrot.tickCount % 200 == 0) {
                List<Parrot> others = level.getEntitiesOfClass(Parrot.class, parrot.getBoundingBox().inflate(8.0D));
                if (others.size() >= 2) {
                    ((ServerLevel)level).sendParticles(ParticleTypes.NOTE, parrot.getX(), parrot.getY() + 0.5, parrot.getZ(), 1, 0, 0, 0, 0);
                    if (parrot.getRandom().nextFloat() < 0.3F) {
                        BlockPos p = parrot.blockPosition().offset(parrot.getRandom().nextInt(3)-1, -1, parrot.getRandom().nextInt(3)-1);
                        if (level.getBlockState(p).getBlock() instanceof BonemealableBlock b) b.performBonemeal((ServerLevel)level, parrot.getRandom(), p, level.getBlockState(p));
                    }
                }
            }

            // 6. PLAY LOGIC
            if (parrot.tickCount % 100 == 0) {
                List<Ocelot> nearbyOcelots = level.getEntitiesOfClass(Ocelot.class, parrot.getBoundingBox().inflate(4.0D), o -> o.getPersistentData().getBoolean("IsTamed"));
                if (!nearbyOcelots.isEmpty()) {
                    level.broadcastEntityEvent(parrot, (byte) 7);
                    if (parrot.getRandom().nextFloat() < 0.15F) parrot.spawnAtLocation(Items.FEATHER);
                }
            }
        }
    }

    // 7. BERRY COURIER (Interaction Logic)
    @SubscribeEvent
    public static void onParrotInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof Parrot parrot && parrot.isPassenger()) {
            ItemStack stack = event.getItemStack();
            Player player = event.getEntity();

            if (stack.is(Items.SWEET_BERRIES) || stack.is(Items.GLOW_BERRIES)) {
                if (!event.getLevel().isClientSide) {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 10, 0));
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                    event.getLevel().broadcastEntityEvent(parrot, (byte) 7);
                }
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
                event.setCanceled(true);
            }
        }
    }
}