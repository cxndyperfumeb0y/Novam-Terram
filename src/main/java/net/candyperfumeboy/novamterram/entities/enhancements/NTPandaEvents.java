package net.candyperfumeboy.novamterram.entities.enhancements;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
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

            // 3. Stat Boost: Increased health for "Apex Herbivore" status
            if (panda.getAttribute(Attributes.MAX_HEALTH) != null) {
                panda.getAttribute(Attributes.MAX_HEALTH).setBaseValue(34.0D);
                panda.setHealth(34.0F);
            }
        }
    }

    // --- MAIN TICK LOGIC ---

    @SubscribeEvent
    public static void onPandaTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Panda panda) || panda.level().isClientSide) return;

        // I. FAMILY DYNAMICS: Cub Hitchhiking
        handleCubCarrying(panda);

        // II. ZEN MASTER: Aura, Growth, and Parrot Interactions
        if (panda.isSitting() && panda.isEating()) {
            applyZenAura(panda);
            handleParrotPerching(panda);

            if (panda.tickCount % 120 == 0) growJungle(panda);

            // Bamboo Shredding (Sticks/Sugar byproduct)
            if (panda.tickCount % 200 == 0) {
                panda.spawnAtLocation(panda.getRandom().nextBoolean() ? Items.STICK : Items.SUGAR);
            }
        }

        // III. COMBAT & PHYSICS: Rolling and Weight
        if (panda.isRolling()) {
            handleRollingLogic(panda);
        }

        if (panda.fallDistance > 2.5F) {
            handleImpact(panda);
        }

        // IV. WILDLIFE BEHAVIOR: Fishing and Napping
        handleWildlifeInteractions(panda);
    }

    // --- MODULES ---

    /**
     * Seat babies on the mother's back.
     */
    private static void handleCubCarrying(Panda panda) {
        if (!panda.isBaby() && panda.tickCount % 30 == 0 && panda.getPassengers().isEmpty()) {
            List<Panda> babies = panda.level().getEntitiesOfClass(Panda.class, panda.getBoundingBox().inflate(2.0D),
                    p -> p.isBaby() && !p.isPassenger());
            if (!babies.isEmpty()) babies.get(0).startRiding(panda, true);
        }

        if (panda.hasPassenger(p -> p instanceof Panda)) {
            Entity baby = panda.getFirstPassenger();
            float radian = panda.yBodyRot * ((float)Math.PI / 180F);
            // Baby sits lower when mother sits to eat
            double yOff = panda.isSitting() ? 0.35D : 0.65D;
            double xOff = -0.55D * Mth.sin(radian);
            double zOff = 0.55D * Mth.cos(radian);
            baby.setPos(panda.getX() + xOff, panda.getY() + yOff, panda.getZ() + zOff);
        }
    }

    /**
     * Logic for combat damage and shaking items out of trees.
     */
    private static void handleRollingLogic(Panda panda) {
        // Damage and Knockback
        panda.level().getEntitiesOfClass(LivingEntity.class, panda.getBoundingBox().inflate(1.2D)).forEach(e -> {
            if (e != panda && !(e instanceof Panda && ((Panda)e).isBaby())) {
                e.hurt(panda.damageSources().mobAttack(panda), 5.0F);
                e.knockback(0.8F, panda.getX() - e.getX(), panda.getZ() - e.getZ());
            }
        });

        // Tree Shaking (Shake Cocoa from Logs)
        if (panda.horizontalCollision) {
            BlockPos forward = panda.blockPosition().relative(panda.getDirection());
            if (panda.level().getBlockState(forward).is(BlockTags.LOGS) && panda.getRandom().nextFloat() < 0.15F) {
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
        if (panda.onGround()) {
            // Corrected playSound: Using panda.level().playSound(Player, x, y, z, Sound, Source, Volume, Pitch)
            panda.level().playSound(null, panda.getX(), panda.getY(), panda.getZ(),
                    SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F, 0.5F);

            panda.level().getEntitiesOfClass(LivingEntity.class, panda.getBoundingBox().inflate(4.0D)).forEach(e -> {
                if (e != panda) {
                    int strength = e.getBbWidth() < 0.8F ? 4 : 1;
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, strength));
                }
            });
            panda.fallDistance = 0;
        }
    }

    /**
     * Zen Aura provides player benefits; Parrots land on sitting Pandas.
     */
    private static void applyZenAura(Panda panda) {
        panda.level().getEntitiesOfClass(Player.class, panda.getBoundingBox().inflate(8.0D)).forEach(p -> {
            p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, false, true));
            if (!p.isSprinting()) p.addEffect(new MobEffectInstance(MobEffects.SATURATION, 10, 0, false, false));
        });
    }

    private static void handleParrotPerching(Panda panda) {
        if (panda.tickCount % 100 == 0) {
            panda.level().getEntitiesOfClass(Parrot.class, panda.getBoundingBox().inflate(6.0D)).forEach(parrot -> {
                if (!parrot.isPassenger() && !parrot.isOrderedToSit() && panda.getPassengers().size() < 3) {
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
            if (panda.getRandom().nextFloat() < 0.2F) {
                panda.spawnAtLocation(new ItemStack(Items.TROPICAL_FISH));
                panda.playSound(SoundEvents.PANDA_EAT, 1.0F, 1.0F);
            }
        }

        // Lazy Daytime Napping (In low light/shade)
        if (panda.tickCount % 600 == 0 && !panda.isEating() && !panda.isRolling()) {
            if (panda.level().isDay() && panda.level().getMaxLocalRawBrightness(panda.blockPosition()) < 10) {
                panda.sit(true);
                panda.heal(1.0F);
            }
        }
    }

    /**
     * Chance to bonemeal nearby vegetation when eating.
     */
    private static void growJungle(Panda panda) {
        BlockPos.betweenClosedStream(panda.blockPosition().offset(-3, -1, -3), panda.blockPosition().offset(3, 1, 3))
                .filter(pos -> panda.level().getBlockState(pos).getBlock() instanceof BonemealableBlock)
                .findFirst()
                .ifPresent(pos -> {
                    BlockState state = panda.level().getBlockState(pos);
                    if (panda.level() instanceof ServerLevel sl) {
                        ((BonemealableBlock)state.getBlock()).performBonemeal(sl, sl.random, pos, state);
                        sl.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0.02);
                    }
                });
    }

    // --- CUSTOM AI GOAL ---

    static class PandaProtectCubGoal extends NearestAttackableTargetGoal<Player> {
        private final Panda panda;
        public PandaProtectCubGoal(Panda panda) { super(panda, Player.class, 10, true, true, null); this.panda = panda; }

        @Override
        public boolean canUse() {
            // Only aggressive if a baby is within 8 blocks
            List<Panda> cubs = panda.level().getEntitiesOfClass(Panda.class, panda.getBoundingBox().inflate(8.0D), Panda::isBaby);
            return !cubs.isEmpty() && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            panda.playSound(SoundEvents.PANDA_AGGRESSIVE_AMBIENT, 1.0F, 0.8F);
        }
    }
}