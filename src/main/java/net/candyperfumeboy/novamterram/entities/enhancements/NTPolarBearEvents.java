package net.candyperfumeboy.novamterram.entities.enhancements;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.EnumSet;
import java.util.List;

@EventBusSubscriber(modid = "novamterram")
public class NTPolarBearEvents {

    /* --- 1. DEFENSE LOGIC (Thick Hide) --- */
    @SubscribeEvent
    public static void onEntityHurt(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof PolarBear bear && event.getSource().is(DamageTypes.ARROW)) {
            // 50% chance to deflect arrows entirely
            if (bear.getRandom().nextFloat() < 0.5F) {
                bear.level().playSound(null, bear.getX(), bear.getY(), bear.getZ(),
                        SoundEvents.SHIELD_BLOCK, SoundSource.NEUTRAL, 1.0F, 0.4F);
                event.setCanceled(true);
            }
        }
    }

    /* --- 2. INTERACTION LOGIC (Breeding/Healing) --- */
    @SubscribeEvent
    public static void onPolarBearInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof PolarBear bear)) return;
        ItemStack stack = event.getItemStack();

        if (stack.is(Items.SALMON) || stack.is(Items.COD)) {
            if (!event.getLevel().isClientSide) {
                if (bear.getAge() == 0 && bear.canFallInLove()) {
                    bear.setInLove(event.getEntity());
                    bear.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                    showParticles(bear, ParticleTypes.HEART, 7);
                    if (!event.getEntity().getAbilities().instabuild) stack.shrink(1);
                } else if (bear.getHealth() < bear.getMaxHealth()) {
                    bear.heal(4.0F);
                    bear.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                    showParticles(bear, ParticleTypes.HAPPY_VILLAGER, 5);
                    if (!event.getEntity().getAbilities().instabuild) stack.shrink(1);
                }
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    /* --- 3. GOAL INJECTION (Predatory AI) --- */
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof PolarBear bear && !event.getLevel().isClientSide) {
            // Remove vanilla aggressive goals toward players so breeding/tempting works
            bear.targetSelector.getAvailableGoals().removeIf(wrapped ->
                    wrapped.getGoal().getClass().getSimpleName().contains("PolarBearAttackPlayerGoal"));

            // Core Predator Instincts
            bear.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(bear, AbstractSkeleton.class, true)); // Hunt Undead
            bear.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(bear, AbstractFish.class, true));
            bear.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(bear, Squid.class, true));
            bear.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(bear, Fox.class, true));
            bear.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(bear, Rabbit.class, true));

            // Interaction Goals
            bear.goalSelector.addGoal(2, new BreedGoal(bear, 1.0D));
            bear.goalSelector.addGoal(3, new TemptGoal(bear, 1.1D, Ingredient.of(Items.COD, Items.SALMON), false));
            bear.targetSelector.addGoal(1, new CubProtectionGoal(bear));
            bear.goalSelector.addGoal(4, new DigDenGoal(bear));

            // Physical Attributes
            if (bear.getAttribute(Attributes.STEP_HEIGHT) != null) {
                bear.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.25D);
            }
        }
    }

    /* --- 4. TICK UPDATES (Environmental Features) --- */
    @SubscribeEvent
    public static void onPolarBearTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof PolarBear bear) || bear.level().isClientSide) return;

        handleAlphaRoar(bear);
        handleHeavySwimmer(bear);
        handleSnowPlowing(bear);
        handleWetShake(bear);
        handleDenShelterLogic(bear);
        handleBaiting(bear);
        handleIceFishing(bear);
        handleCubPosition(bear);

        if (bear.tickCount % 60 == 0) handleVillagerPanic(bear);
    }

    /* --- CORE LOGIC MODULES --- */

    private static void handleAlphaRoar(PolarBear bear) {
        if (bear.isStanding() && bear.tickCount % 120 == 0) {
            bear.level().playSound(null, bear.getX(), bear.getY(), bear.getZ(),
                    SoundEvents.POLAR_BEAR_WARNING, SoundSource.NEUTRAL, 2.0F, 0.6F);

            AABB area = bear.getBoundingBox().inflate(10.0D);
            List<Monster> monsters = bear.level().getEntitiesOfClass(Monster.class, area);

            for (Monster m : monsters) {
                m.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
                Vec3 flee = m.position().subtract(bear.position()).normalize().scale(5);
                m.getNavigation().moveTo(m.getX() + flee.x, m.getY(), m.getZ() + flee.z, 1.3D);
            }
        }
    }

    private static void handleHeavySwimmer(PolarBear bear) {
        if (bear.isInWater()) {
            bear.setDeltaMovement(bear.getDeltaMovement().multiply(1.18, 1.0, 1.18));
            if (bear.getTarget() != null) bear.setStanding(true);
        }
    }

    private static void handleIceFishing(PolarBear bear) {
        if (!bear.isBaby() && bear.tickCount % 160 == 0 && bear.getRandom().nextFloat() < 0.15F) {
            BlockPos forward = bear.blockPosition().relative(bear.getDirection());
            if (bear.level().getBlockState(forward).is(Blocks.ICE)) {
                bear.level().destroyBlock(forward, false);
                bear.playSound(SoundEvents.GLASS_BREAK, 0.4F, 1.0F);
            }
        }
    }

    private static void handleSnowPlowing(PolarBear bear) {
        if (bear.tickCount % 10 == 0) {
            BlockPos pos = bear.blockPosition();
            if (bear.level().getBlockState(pos).is(Blocks.SNOW)) {
                bear.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    private static void handleWetShake(PolarBear bear) {
        if (bear.isInWaterRainOrBubble()) {
            bear.getPersistentData().putBoolean("NTCustomWet", true);
        } else if (bear.getPersistentData().getBoolean("NTCustomWet")) {
            bear.level().playSound(null, bear.getX(), bear.getY(), bear.getZ(),
                    SoundEvents.WOLF_SHAKE, SoundSource.NEUTRAL, 1.0F, 0.6F);
            showParticles(bear, ParticleTypes.SPLASH, 12);
            bear.getPersistentData().putBoolean("NTCustomWet", false);
        }
    }

    private static void handleDenShelterLogic(PolarBear bear) {
        if (!bear.getPersistentData().contains("HomeDen")) return;
        boolean needsShelter = bear.level().isThundering() || bear.level().isNight();
        if (needsShelter) {
            BlockPos home = BlockPos.of(bear.getPersistentData().getLong("HomeDen"));
            double dist = bear.distanceToSqr(home.getX() + 0.5, home.getY(), home.getZ() + 0.5);
            if (dist < 3.0D) {
                bear.getNavigation().stop();
                if (bear.tickCount % 100 == 0) bear.heal(1.0F);
            } else if (bear.getNavigation().isDone()) {
                bear.getNavigation().moveTo(home.getX(), home.getY(), home.getZ(), 1.0D);
            }
        }
    }

    private static void handleBaiting(PolarBear bear) {
        if (bear.getTarget() != null && bear.tickCount % 20 == 0) {
            List<ItemEntity> items = bear.level().getEntitiesOfClass(ItemEntity.class, bear.getBoundingBox().inflate(6.0D),
                    i -> i.getItem().is(Items.SALMON) || i.getItem().is(Items.COD));
            if (!items.isEmpty()) {
                bear.setTarget(null);
                bear.getNavigation().moveTo(items.get(0), 1.2D);
            }
        }
    }

    private static void handleCubPosition(PolarBear bear) {
        if (!bear.isBaby() && bear.tickCount % 20 == 0 && bear.getPassengers().isEmpty()) {
            List<PolarBear> cubs = bear.level().getEntitiesOfClass(PolarBear.class, bear.getBoundingBox().inflate(2.0D),
                    c -> c.isBaby() && !c.isPassenger());
            if (!cubs.isEmpty()) cubs.get(0).startRiding(bear, true);
        }
        if (bear.hasPassenger(p -> p instanceof PolarBear)) {
            Entity baby = bear.getFirstPassenger();
            float radian = bear.yBodyRot * ((float)Math.PI / 180F);
            baby.setPos(bear.getX() + (-0.6D * Mth.sin(radian)), bear.getY() + 0.45D, bear.getZ() + (0.6D * Mth.cos(radian)));
        }
    }

    private static void handleVillagerPanic(PolarBear bear) {
        List<Villager> villagers = bear.level().getEntitiesOfClass(Villager.class, bear.getBoundingBox().inflate(12.0D));
        for (Villager v : villagers) v.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.DANGER_DETECTED_RECENTLY, true);
    }

    private static void showParticles(PolarBear bear, net.minecraft.core.particles.ParticleOptions type, int count) {
        if (bear.level() instanceof ServerLevel sl) {
            sl.sendParticles(type, bear.getX(), bear.getY() + 1.2D, bear.getZ(), count, 0.2, 0.2, 0.2, 0.05);
        }
    }

    /* --- INNER GOALS --- */

    static class DigDenGoal extends Goal {
        private final PolarBear bear;
        private int digTick = 0;
        public DigDenGoal(PolarBear b) { this.bear = b; this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK)); }
        @Override public boolean canUse() {
            return !bear.isBaby() && !bear.getPersistentData().contains("HomeDen") && bear.level().isNight() &&
                    bear.level().getBlockState(bear.blockPosition().relative(bear.getDirection())).is(Blocks.SNOW_BLOCK);
        }
        @Override public void tick() {
            if (++digTick % 10 == 0) bear.swing(InteractionHand.MAIN_HAND);
            if (digTick > 60) { bear.getPersistentData().putLong("HomeDen", bear.blockPosition().asLong()); digTick = 0; }
        }
    }

    static class CubProtectionGoal extends NearestAttackableTargetGoal<Player> {
        private final PolarBear bear;
        public CubProtectionGoal(PolarBear b) { super(b, Player.class, 10, true, true, null); this.bear = b; }
        @Override public boolean canUse() { return !bear.level().getEntitiesOfClass(PolarBear.class, bear.getBoundingBox().inflate(10.0D), PolarBear::isBaby).isEmpty() && super.canUse(); }
        @Override public void start() { super.start(); bear.setStanding(true); }
        @Override public void stop() { super.stop(); bear.setStanding(false); }
    }
}