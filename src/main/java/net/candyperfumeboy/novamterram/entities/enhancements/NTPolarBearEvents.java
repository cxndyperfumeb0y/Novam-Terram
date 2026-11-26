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
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@EventBusSubscriber(modid = "novamterram")
public class NTPolarBearEvents {

    private static final String DEN = "NTDen";         // saved position of a dug den
    private static final String CALM = "NTCalmUntil";  // set after eating bait

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
                    wrapped.getGoal().getClass().getSimpleName().contains("PolarBearAttackPlayer"));

            // Core Predator Instincts
            bear.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(bear, AbstractSkeleton.class, true)); // Hunt Undead
            bear.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(bear, Squid.class, true));
            // Wild foxes only: foxes that trust a player are left alone.
            bear.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(bear, Fox.class, 10, true, false,
                    e -> !(e instanceof net.candyperfumeboy.novamterram.util.INTFox fox) || fox.nt$getTrustedUUIDs().isEmpty()));
            bear.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(bear, Rabbit.class, true));

            // Interaction Goals
            bear.goalSelector.addGoal(2, new BreedGoal(bear, 1.0D));
            bear.goalSelector.addGoal(3, new TemptGoal(bear, 1.1D, Ingredient.of(Items.COD, Items.SALMON), false));
            bear.targetSelector.addGoal(1, new CubProtectionGoal(bear));
            bear.goalSelector.addGoal(0, new EatBaitGoal(bear));
            bear.goalSelector.addGoal(4, new DigDenGoal(bear));
            bear.goalSelector.addGoal(4, new AmbushFishingGoal(bear)); // replaces chasing fish around

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
        if (!bear.isBaby() && bear.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING) && bear.tickCount % 160 == 0 && bear.getRandom().nextFloat() < 0.15F) {
            BlockPos forward = bear.blockPosition().relative(bear.getDirection());
            if (bear.level().getBlockState(forward).is(Blocks.ICE)) {
                bear.level().destroyBlock(forward, false);
                bear.playSound(SoundEvents.GLASS_BREAK, 0.4F, 1.0F);
            }
        }
    }

    private static void handleSnowPlowing(PolarBear bear) {
        if (bear.tickCount % 10 == 0 && bear.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)) {
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
        if (!bear.getPersistentData().contains(DEN) || bear.getTarget() != null) return;
        if (!bear.level().isThundering() && !bear.level().isNight()) return;

        BlockPos home = BlockPos.of(bear.getPersistentData().getLong(DEN));
        double dist = bear.distanceToSqr(home.getX() + 0.5, home.getY(), home.getZ() + 0.5);
        if (dist < 4.0D) {
            bear.getNavigation().stop();
            if (bear.tickCount % 100 == 0) bear.heal(1.0F);
        } else if (bear.tickCount % 40 == 0 && bear.getNavigation().isDone()) {
            bear.getNavigation().moveTo(home.getX() + 0.5, home.getY(), home.getZ() + 0.5, 1.0D);
        }
    }

    private static void handleCubPosition(PolarBear bear) {
        if (bear.isBaby() || bear.tickCount % 20 != 0) return;
        long now = bear.level().getGameTime();

        // A riding cub climbs off after about half a minute (or when it grows up).
        if (bear.getFirstPassenger() instanceof PolarBear cub) {
            if (!cub.isBaby() || bear.getRandom().nextInt(30) == 0) {
                cub.stopRiding();
                cub.getPersistentData().putLong("NTNextRide", now + 1200);
            }
        } else if (bear.getPassengers().isEmpty()) {
            List<PolarBear> cubs = bear.level().getEntitiesOfClass(PolarBear.class, bear.getBoundingBox().inflate(2.0D),
                    c -> c.isBaby() && !c.isPassenger() && c.getPersistentData().getLong("NTNextRide") <= now);
            if (!cubs.isEmpty()) cubs.get(0).startRiding(bear, true);
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

    /** A bear that smells dropped salmon or cod goes to it, eats one and calms down. */
    static class EatBaitGoal extends Goal {
        private final PolarBear bear;
        private ItemEntity bait;
        private int ticks;
        public EatBaitGoal(PolarBear b) { this.bear = b; this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK)); }

        @Override public boolean canUse() {
            if (bear.isBaby() || bear.tickCount % 10 != 0) return false;
            // A full, calm bear ignores more fish, unless it has been provoked again.
            if (bear.getTarget() == null && isCalm(bear)) return false;
            List<ItemEntity> items = bear.level().getEntitiesOfClass(ItemEntity.class, bear.getBoundingBox().inflate(8.0D),
                    i -> i.getItem().is(Items.SALMON) || i.getItem().is(Items.COD));
            bait = items.isEmpty() ? null : items.get(0);
            return bait != null;
        }

        @Override public boolean canContinueToUse() { return bait != null && bait.isAlive() && ticks < 200; }
        @Override public boolean requiresUpdateEveryTick() { return true; }

        @Override public void start() {
            ticks = 0;
            dropTargets();
            bear.setStanding(false);
        }

        @Override public void tick() {
            if (ticks++ % 10 == 0) {
                dropTargets();
                bear.getNavigation().moveTo(bait, 1.2D);
            }
            bear.getLookControl().setLookAt(bait);
            if (bear.distanceToSqr(bait) < 5.0D) {
                ItemStack rest = bait.getItem().copy();
                rest.shrink(1);
                if (rest.isEmpty()) bait.discard(); else bait.setItem(rest);
                bear.heal(4.0F);
                bear.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                showParticles(bear, ParticleTypes.HAPPY_VILLAGER, 5);
                bear.getPersistentData().putLong(CALM, bear.level().getGameTime() + 600L); // 30 seconds
                bait = null;
            }
        }

        @Override public void stop() { bait = null; bear.getNavigation().stop(); }

        // Clearing the target alone is not enough: the running target goals would put it straight back.
        private void dropTargets() {
            bear.targetSelector.getAvailableGoals().stream().filter(g -> g.isRunning()).forEach(g -> g.stop());
            bear.stopBeingAngry();
        }
    }

    private static boolean isCalm(PolarBear bear) {
        return bear.level().getGameTime() < bear.getPersistentData().getLong(CALM);
    }

    /** Waits beside water, then lunges when a fish swims close. */
    static class AmbushFishingGoal extends Goal {
        private final PolarBear bear;
        private AbstractFish fish;
        private int ticks;
        private int lunge;
        private long nextTime;
        public AmbushFishingGoal(PolarBear b) { this.bear = b; this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK)); }

        @Override public boolean canUse() {
            if (bear.isBaby() || bear.getTarget() != null || bear.level().getGameTime() < nextTime
                    || bear.getRandom().nextInt(40) != 0) return false;
            List<AbstractFish> found = bear.level().getEntitiesOfClass(AbstractFish.class,
                    bear.getBoundingBox().inflate(10.0D, 4.0D, 10.0D), f -> f.isInWater());
            fish = found.isEmpty() ? null : found.get(bear.getRandom().nextInt(found.size()));
            return fish != null;
        }

        @Override public boolean canContinueToUse() {
            return fish != null && fish.isAlive() && bear.getTarget() == null && ticks < 300 && lunge < 20
                    && bear.distanceToSqr(fish) < 196.0D;
        }

        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void start() { ticks = 0; lunge = 0; }

        @Override public void tick() {
            ticks++;
            bear.getLookControl().setLookAt(fish);
            double dist = bear.distanceToSqr(fish);

            if (lunge > 0) { // mid-air
                lunge++;
                if (dist < 6.0D) {
                    fish.hurt(bear.damageSources().mobAttack(bear), 10.0F);
                    lunge = 20;
                }
                return;
            }

            if (dist > 20.0D) {
                if (ticks % 10 == 0) bear.getNavigation().moveTo(fish, 1.0D);
                return;
            }

            bear.getNavigation().stop(); // hold still and watch
            if (ticks > 40 && dist < 16.0D) {
                Vec3 jump = fish.position().subtract(bear.position()).normalize().scale(0.7D);
                bear.setDeltaMovement(jump.x, 0.4D, jump.z);
                bear.playSound(SoundEvents.POLAR_BEAR_WARNING, 0.5F, 1.2F);
                lunge = 1;
            }
        }

        @Override public void stop() {
            fish = null;
            bear.getNavigation().stop();
            nextTime = bear.level().getGameTime() + 1200L; // one ambush per minute
        }
    }

    /**
     * At night an adult without a den looks for snow blocks. It hollows out a snow bank (2 x 2 x 2)
     * if there is one, otherwise it scrapes a shallow 3 x 3 bed. Each bear digs only once.
     */
    static class DigDenGoal extends Goal {
        private final PolarBear bear;
        private final List<BlockPos> toDig = new ArrayList<>();
        private BlockPos stand;
        private BlockPos den;
        private int ticks;
        private int digTick;
        private long nextSearch;
        public DigDenGoal(PolarBear b) { this.bear = b; this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK)); }

        @Override public boolean canUse() {
            Level level = bear.level();
            if (bear.isBaby() || bear.getTarget() != null || !level.isNight() || level.getGameTime() < nextSearch
                    || bear.getPersistentData().contains(DEN)
                    || !level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)) return false;
            nextSearch = level.getGameTime() + 1200L; // searching is not free, so only once a minute
            return findBank() || findBed();
        }

        private boolean isSnow(BlockPos p) { return bear.level().getBlockState(p).is(Blocks.SNOW_BLOCK); }

        private boolean isOpen(BlockPos p) {
            BlockState s = bear.level().getBlockState(p);
            return s.isAir() || s.is(Blocks.SNOW);
        }

        // Something to rest on: not air, not a snow layer and not water.
        private boolean isFirm(BlockPos p) { return !isOpen(p) && bear.level().getFluidState(p).isEmpty(); }

        private boolean findBank() {
            BlockPos origin = bear.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-8, -2, -8), origin.offset(8, 2, 8))) {
                if (!isOpen(pos) || !isOpen(pos.above()) || !isFirm(pos.below())) continue;
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    if (!isSnow(pos.relative(dir))) continue;
                    if (collectCave(pos.immutable(), dir)) return true;
                }
            }
            return false;
        }

        private boolean collectCave(BlockPos entrance, Direction dir) {
            toDig.clear();
            Direction side = dir.getClockWise();
            for (int depth = 1; depth <= 2; depth++) {
                for (int width = 0; width <= 1; width++) {
                    BlockPos floor = entrance.relative(dir, depth).relative(side, width);
                    // solid floor, two blocks of snow to dig, and a roof that stays
                    if (!isFirm(floor.below()) || !isSnow(floor) || !isSnow(floor.above()) || !isSnow(floor.above(2))) return false;
                    toDig.add(floor);
                    toDig.add(floor.above());
                }
            }
            stand = entrance;
            den = entrance.relative(dir);
            return true;
        }

        private boolean findBed() {
            toDig.clear();
            BlockPos ground = bear.blockPosition().below();
            for (BlockPos pos : BlockPos.betweenClosed(ground.offset(-1, 0, -1), ground.offset(1, 0, 1))) {
                if (!isSnow(pos) || !isFirm(pos.below()) || !isOpen(pos.above())) return false;
                toDig.add(pos.immutable());
            }
            stand = bear.blockPosition();
            den = ground;
            return true;
        }

        @Override public boolean canContinueToUse() {
            return !toDig.isEmpty() && bear.getTarget() == null && ticks < 300;
        }

        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void start() { ticks = 0; digTick = 0; }

        @Override public void tick() {
            ticks++;
            if (bear.distanceToSqr(stand.getX() + 0.5, stand.getY(), stand.getZ() + 0.5) > 4.0D) {
                if (ticks % 10 == 1) bear.getNavigation().moveTo(stand.getX() + 0.5, stand.getY(), stand.getZ() + 0.5, 1.0D);
                return;
            }
            bear.getNavigation().stop();
            bear.getLookControl().setLookAt(den.getX() + 0.5, den.getY(), den.getZ() + 0.5);
            if (++digTick % 10 == 0) {
                bear.swing(InteractionHand.MAIN_HAND);
                bear.playSound(SoundEvents.SNOW_BREAK, 1.0F, 0.8F);
            }
            if (digTick >= 60) {
                for (BlockPos pos : toDig) {
                    if (isSnow(pos)) bear.level().destroyBlock(pos, false);
                }
                bear.getPersistentData().putLong(DEN, den.asLong());
                toDig.clear();
            }
        }

        @Override public void stop() { toDig.clear(); bear.getNavigation().stop(); }
    }

    static class CubProtectionGoal extends NearestAttackableTargetGoal<Player> {
        private final PolarBear bear;
        public CubProtectionGoal(PolarBear b) { super(b, Player.class, 10, true, true, null); this.bear = b; }
        @Override public boolean canContinueToUse() { return !isCalm(bear) && super.canContinueToUse(); }
        @Override public boolean canUse() { return !bear.isBaby() && !isCalm(bear) && super.canUse() && !bear.level().getEntitiesOfClass(PolarBear.class, bear.getBoundingBox().inflate(10.0D), PolarBear::isBaby).isEmpty(); }
        @Override public void start() { super.start(); bear.setStanding(true); }
        @Override public void stop() { super.stop(); bear.setStanding(false); }
    }
}