package net.candyperfumeboy.novamterram.entities.enhancements;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Comparator;
import java.util.List;

/**
 * Extra parrot behaviour.
 *
 * A parrot on your shoulder is not an entity in the world (the game stores it inside the
 * player), so shoulder abilities run from the player's side.
 */
@EventBusSubscriber(modid = NovamTerram.MOD_ID)
public class NTParrotEvents {

    private static final int ONE_DAY = 24000;
    private static final int BERRY_COOLDOWN = 6000; // 5 minutes

    // ------------------------------------------------------------ helpers

    private static boolean isReady(Entity entity, String key) {
        return entity.level().getGameTime() >= entity.getPersistentData().getLong(key);
    }

    private static void startCooldown(Entity entity, String key, int ticks) {
        entity.getPersistentData().putLong(key, entity.level().getGameTime() + ticks);
    }

    private static boolean isParrot(CompoundTag shoulder) {
        return shoulder.getString("id").equals("minecraft:parrot");
    }

    private static boolean hasShoulderParrot(Player player) {
        return isParrot(player.getShoulderEntityLeft()) || isParrot(player.getShoulderEntityRight());
    }

    // ------------------------------------------------------------ on your shoulder

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 20 != 0) return;
        if (player.isSpectator() || !hasShoulderParrot(player)) return;

        if (player.tickCount % 40 == 0) {
            scout(player);
            harvestCocoa(player);
        }
        // Rain dancer
        if (player.tickCount % 100 == 0 && player.level().isRainingAt(player.blockPosition())) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0, false, false, true));
        }
    }

    /** Highlights the nearest threat, with an extra warning for spiders. */
    private static void scout(ServerPlayer player) {
        List<LivingEntity> threats = player.level().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(16.0D),
                e -> e.isAlive() && (e instanceof Monster
                        || e instanceof Ocelot ocelot && !NTOcelotEvents.isTamed(ocelot)));

        threats.stream().min(Comparator.comparingDouble(e -> e.distanceToSqr(player))).ifPresent(closest -> {
            closest.addEffect(new MobEffectInstance(MobEffects.GLOWING, 45, 0, false, false));

            if (closest instanceof Spider && isReady(player, "NTNextSpiderWarning")) {
                startCooldown(player, "NTNextSpiderWarning", 200);
                player.level().playSound(null, player.blockPosition(), SoundEvents.PARROT_IMITATE_SPIDER,
                        SoundSource.NEUTRAL, 0.8F, 1.2F);
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20, 0, false, false));
            }
        });
    }

    /** Picks one ripe cocoa pod within reach of the parrot. */
    private static void harvestCocoa(ServerPlayer player) {
        BlockPos shoulder = player.blockPosition().above(2);
        for (BlockPos pos : BlockPos.betweenClosed(shoulder.offset(-1, -1, -1), shoulder.offset(1, 1, 1))) {
            BlockState state = player.level().getBlockState(pos);
            if (state.is(Blocks.COCOA) && state.getValue(CocoaBlock.AGE) >= 2) {
                if (player.mayBuild()) player.level().destroyBlock(pos, true, player);
                return;
            }
        }
    }

    /** Sneak + use berries in the air to hand one to your shoulder parrot. */
    @SubscribeEvent
    public static void onFeedShoulderParrot(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (!player.isShiftKeyDown() || !hasShoulderParrot(player)) return;
        if (!stack.is(Items.SWEET_BERRIES) && !stack.is(Items.GLOW_BERRIES)) return;

        if (player.level() instanceof ServerLevel level && isReady(player, "NTNextParrotBerry")) {
            startCooldown(player, "NTNextParrotBerry", BERRY_COOLDOWN);
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
            player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 10, 0));
            if (!player.getAbilities().instabuild) stack.shrink(1);
            level.sendParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.8, player.getZ(),
                    4, 0.4, 0.2, 0.4, 0);
        }
        event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
        event.setCanceled(true);
    }

    // ------------------------------------------------------------ on the ground

    @SubscribeEvent
    public static void onParrotTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Parrot parrot) || !(parrot.level() instanceof ServerLevel level)) return;
        if (parrot.tickCount % 20 != 0) return;
        if (parrot.isPassenger() || parrot.isOrderedToSit() || !parrot.onGround()) return;

        forage(parrot, level);
        if (parrot.tickCount % 200 == 0) chorus(parrot, level);
        if (parrot.tickCount % 100 == 0) playWithOcelots(parrot, level);
    }

    /** At most once a day per parrot. */
    private static void forage(Parrot parrot, ServerLevel level) {
        if (!isReady(parrot, "NTNextForage") || parrot.getRandom().nextFloat() >= 0.01F) return;

        BlockState ground = level.getBlockState(parrot.blockPosition().below());
        if (!ground.is(BlockTags.DIRT)) return;

        Item found = level.getBiome(parrot.blockPosition()).is(BiomeTags.IS_JUNGLE)
                ? Items.COCOA_BEANS : Items.WHEAT_SEEDS;
        parrot.spawnAtLocation(found);
        level.levelEvent(2001, parrot.blockPosition(), Block.getId(ground));
        startCooldown(parrot, "NTNextForage", ONE_DAY);
    }

    /** Parrots in a group sing, and sometimes help a plant grow (once a day each). */
    private static void chorus(Parrot parrot, ServerLevel level) {
        if (level.getEntitiesOfClass(Parrot.class, parrot.getBoundingBox().inflate(8.0D)).size() < 2) return;

        level.sendParticles(ParticleTypes.NOTE, parrot.getX(), parrot.getY() + 0.5, parrot.getZ(), 1, 0, 0, 0, 0);
        if (parrot.getRandom().nextFloat() >= 0.3F || !isReady(parrot, "NTNextBonemeal")
                || !EventHooks.canEntityGrief(level, parrot)) return;

        BlockPos pos = parrot.blockPosition().offset(
                parrot.getRandom().nextInt(3) - 1, -1, parrot.getRandom().nextInt(3) - 1);
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof BonemealableBlock plant
                && plant.isValidBonemealTarget(level, pos, state)
                && plant.isBonemealSuccess(level, parrot.getRandom(), pos, state)) {
            plant.performBonemeal(level, parrot.getRandom(), pos, state);
            startCooldown(parrot, "NTNextBonemeal", ONE_DAY);
        }
    }

    private static void playWithOcelots(Parrot parrot, ServerLevel level) {
        if (level.getEntitiesOfClass(Ocelot.class, parrot.getBoundingBox().inflate(4.0D),
                NTOcelotEvents::isTamed).isEmpty()) return;

        level.broadcastEntityEvent(parrot, (byte) 7);
        // At most one feather per parrot per day
        if (isReady(parrot, "NTNextPlayFeather") && parrot.getRandom().nextFloat() < 0.15F) {
            parrot.spawnAtLocation(Items.FEATHER);
            startCooldown(parrot, "NTNextPlayFeather", ONE_DAY);
        }
    }
}
