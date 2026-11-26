package net.candyperfumeboy.novamterram.entities.enhancements;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

@EventBusSubscriber(modid = "novamterram")
public class NTPandaEvents {

    // --- INITIALIZATION ---

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Panda panda && !event.getLevel().isClientSide) {
            // 1. Remove Panic Behavior: Pandas are now stoic tanks
            panda.goalSelector.getAvailableGoals().removeIf(goal ->
                    goal.getGoal() instanceof net.minecraft.world.entity.ai.goal.PanicGoal);

            // 2. Add Territorial Defense: Protect cubs with high priority
            panda.targetSelector.addGoal(1, new PandaProtectCubGoal(panda));

            // 3. Stat Boost: raised once. Loading a chunk no longer heals, and weak pandas stay weak.
            AttributeInstance health = panda.getAttribute(Attributes.MAX_HEALTH);
            if (health != null) {
                if (panda.isWeak()) {
                    if (health.getBaseValue() == 34.0D) { // undo the old boost
                        health.setBaseValue(10.0D);
                        panda.setHealth(Math.min(panda.getHealth(), 10.0F));
                    }
                } else if (health.getBaseValue() < 34.0D) {
                    health.setBaseValue(34.0D);
                    panda.setHealth(34.0F);
                }
            }
        }
    }

    // --- MAIN TICK LOGIC ---

    @SubscribeEvent
    public static void onPandaTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Panda panda) || panda.level().isClientSide) return;

        // I. FAMILY DYNAMICS: Cub Hitchhiking, and getting riders off again
        if (panda.tickCount % 20 == 0) {
            handleRiders(panda);
            handleNapEnd(panda);
        }

        // II. ZEN MASTER: Aura, Growth, and Parrot Interactions
        if (panda.isSitting() && panda.isEating()) {
            if (panda.tickCount % 10 == 0) applyZenAura(panda);
            handleParrotPerching(panda);

            if (panda.tickCount % 120 == 0) growBamboo(panda);

            // Bamboo Shredding: at most once per Minecraft day per panda
            if (panda.tickCount % 200 == 0 && useCooldown(panda, "NTNextShred", 24000)) {
                panda.spawnAtLocation(panda.getRandom().nextBoolean() ? Items.STICK : Items.SUGAR);
            }
        }

        // III. COMBAT & PHYSICS: Rolling (the landing thud is handled by onPandaLand)
        if (panda.isRolling()) {
            handleRollingLogic(panda);
        }

        // IV. WILDLIFE BEHAVIOR: Fishing and Napping
        handleWildlifeInteractions(panda);
    }

    @SubscribeEvent
    public static void onPandaLand(LivingFallEvent event) {
        if (event.getEntity() instanceof Panda panda && !panda.level().isClientSide && event.getDistance() > 2.5F) {
            handleImpact(panda);
        }
    }

    // True (and starts the wait) if this panda has not done this recently.
    private static boolean useCooldown(Panda panda, String key, int ticks) {
        long now = panda.level().getGameTime();
        if (now < panda.getPersistentData().getLong(key)) return false;
        panda.getPersistentData().putLong(key, now + ticks);
        return true;
    }

    // --- MODULES ---

    /**
     * Cubs hop on their mother for a while; cubs and parrots climb off again.
     */
    private static void handleRiders(Panda panda) {
        long now = panda.level().getGameTime();

        for (Entity rider : List.copyOf(panda.getPassengers())) {
            boolean leave = false;
            if (rider instanceof Panda cub) {
                leave = !cub.isBaby() || panda.getRandom().nextInt(30) == 0;
            } else if (rider instanceof Parrot) {
                leave = !panda.isEating() || panda.getRandom().nextInt(30) == 0;
            }
            if (leave) {
                rider.stopRiding();
                rider.getPersistentData().putLong("NTNextRide", now + 1200);
            }
        }

        if (!panda.isBaby() && panda.getPassengers().isEmpty()) {
            List<Panda> babies = panda.level().getEntitiesOfClass(Panda.class, panda.getBoundingBox().inflate(2.0D),
                    p -> p.isBaby() && !p.isPassenger() && p.getPersistentData().getLong("NTNextRide") <= now);
            if (!babies.isEmpty()) babies.get(0).startRiding(panda, true);
        }
    }

    /**
     * Logic for combat damage and shaking items out of trees.
     */
    private static void handleRollingLogic(Panda panda) {
        // Damage and Knockback: only adults, and only against monsters
        if (!panda.isBaby()) {
            panda.level().getEntitiesOfClass(LivingEntity.class, panda.getBoundingBox().inflate(1.2D), e -> e instanceof Enemy)
                    .forEach(e -> {
                        if (e.hurt(panda.damageSources().mobAttack(panda), 5.0F)) {
                            e.knockback(0.8F, panda.getX() - e.getX(), panda.getZ() - e.getZ());
                        }
                    });
        }

        // Tree Shaking (Shake Cocoa from Logs)
        if (panda.horizontalCollision) {
            BlockPos forward = panda.blockPosition().relative(panda.getDirection());
            if (panda.level().getBlockState(forward).is(BlockTags.LOGS) && panda.getRandom().nextFloat() < 0.15F
                    && useCooldown(panda, "NTNextCocoa", 24000)) {
                panda.spawnAtLocation(new ItemStack(Items.COCOA_BEANS, 1 + panda.getRandom().nextInt(2)));
                panda.playSound(SoundEvents.WOOD_STEP, 1.0F, 0.5F);
            }
        }

        // Scaring Ocelots
        panda.level().getEntitiesOfClass(Ocelot.class, panda.getBoundingBox().inflate(5.0D)).forEach(o -> {
            Vec3 escapeVec = o.position().subtract(panda.position()).normalize().scale(1.5D);
            o.setDeltaMovement(escapeVec.x, 0.2D, escapeVec.z);
            o.playSound(SoundEvents.CAT_HISS, 0.5F, 0.8F);
        });
    }

    /**
     * Falling logic for the "Weighty Thud" shockwave.
     */
    private static void handleImpact(Panda panda) {
        panda.level().playSound(null, panda.getX(), panda.getY(), panda.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);

        panda.level().getEntitiesOfClass(LivingEntity.class, panda.getBoundingBox().inflate(4.0D)).forEach(e -> {
            if (e != panda) {
                int strength = e.getBbWidth() < 0.8F ? 4 : 1;
                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, strength));
            }
        });
    }

    /**
     * Zen Aura provides player benefits; Parrots land on sitting Pandas.
     */
    private static void applyZenAura(Panda panda) {
        panda.level().getEntitiesOfClass(Player.class, panda.getBoundingBox().inflate(8.0D)).forEach(p -> {
            if (!p.hasEffect(MobEffects.REGENERATION)) {
                p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, false, true));
            }
            if (!p.isSprinting()) p.addEffect(new MobEffectInstance(MobEffects.SATURATION, 10, 0, false, false));
        });
    }

    private static void handleParrotPerching(Panda panda) {
        if (panda.tickCount % 100 == 0) {
            panda.level().getEntitiesOfClass(Parrot.class, panda.getBoundingBox().inflate(6.0D)).forEach(parrot -> {
                if (!parrot.isPassenger() && !parrot.isOrderedToSit() && panda.getPassengers().size() < 3
                        && parrot.getPersistentData().getLong("NTNextRide") <= panda.level().getGameTime()) {
                    parrot.startRiding(panda, true);
                }
            });
        }
    }

    /**
     * Pandas catch fish and sleep in the shade.
     */
    private static void handleWildlifeInteractions(Panda panda) {
        // Tropical Fishing
        if (panda.isInWater() && panda.tickCount % 400 == 0 && !panda.isBaby()) {
            if (panda.getRandom().nextFloat() < 0.2F && useCooldown(panda, "NTNextFish", 24000)) {
                panda.spawnAtLocation(new ItemStack(Items.TROPICAL_FISH));
                panda.playSound(SoundEvents.PANDA_EAT, 1.0F, 1.0F);
            }
        }

        // Lazy Daytime Napping (In low light/shade)
        if (panda.tickCount % 600 == 0 && !panda.isEating() && !panda.isRolling()) {
            if (panda.level().isDay() && panda.level().getMaxLocalRawBrightness(panda.blockPosition()) < 10) {
                panda.sit(true);
                panda.heal(1.0F);
                panda.getPersistentData().putLong("NTNapEnd", panda.level().getGameTime() + 200);
            }
        }
    }

    // Without this, a napping panda could stay sitting for good.
    private static void handleNapEnd(Panda panda) {
        long end = panda.getPersistentData().getLong("NTNapEnd");
        if (end != 0 && panda.level().getGameTime() >= end) {
            panda.getPersistentData().remove("NTNapEnd");
            if (!panda.isEating()) panda.sit(false);
        }
    }

    /**
     * Sometimes helps one nearby bamboo plant grow while eating. Crops and other plants are left alone.
     */
    private static void growBamboo(Panda panda) {
        if (!(panda.level() instanceof ServerLevel level) || !level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return;
        if (panda.getRandom().nextInt(5) != 0) return;
        for (BlockPos pos : BlockPos.betweenClosed(panda.blockPosition().offset(-3, -1, -3), panda.blockPosition().offset(3, 1, 3))) {
            BlockState state = level.getBlockState(pos);
            if ((state.getBlock() instanceof BambooStalkBlock || state.getBlock() instanceof BambooSaplingBlock)
                    && ((BonemealableBlock) state.getBlock()).isValidBonemealTarget(level, pos, state)) {
                ((BonemealableBlock) state.getBlock()).performBonemeal(level, level.random, pos, state);
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0.02);
                return;
            }
        }
    }

    // --- CUSTOM AI GOAL ---

    static class PandaProtectCubGoal extends NearestAttackableTargetGoal<Player> {
        private final Panda panda;
        public PandaProtectCubGoal(Panda panda) { super(panda, Player.class, 10, true, true, null); this.panda = panda; }

        @Override
        public boolean canUse() {
            // Only adults, and only if a baby is within 8 blocks
            if (panda.isBaby() || !super.canUse()) return false;
            List<Panda> cubs = panda.level().getEntitiesOfClass(Panda.class, panda.getBoundingBox().inflate(8.0D), Panda::isBaby);
            return !cubs.isEmpty();
        }

        @Override
        public void start() {
            super.start();
            panda.playSound(SoundEvents.PANDA_AGGRESSIVE_AMBIENT, 1.0F, 0.8F);
        }
    }
}