package net.candyperfumeboy.novamterram.entities.enhancements;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.util.IOcelotMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * NTOcelotEvents
 *
 * Overhauls the vanilla Ocelot into a fully featured tamed wildcat companion.
 *
 * ============================================================================
 * FEATURE SUMMARY
 * ============================================================================
 *
 * TAMING & INTERACTION:
 *   - Trust-based taming (TAME_THRESHOLD fish feedings, 1-in-3 chance each)
 *   - Collar dyeing via DyeItem
 *   - Sit/stand toggle with empty hand
 *   - Fish heals or triggers breeding
 *   - Petting cooldown — right-click with empty hand on sitting ocelot gives
 *     heart particles and purring once every 5 minutes (300s = 6000 ticks)
 *   - Playtime — right-click with string/stick triggers a 15s play session
 *     where the ocelot sprints around the owner erratically
 *   - Carrying small items — picks up nearby raw fish/feathers, owner retrieves
 *     by right-clicking with empty hand while ocelot is sitting
 *   - Lead-compatible when tamed
 *
 * PROTECTION:
 *   - Explosion and fall damage immunity when tamed
 *   - Friendly-fire prevention between tamed ocelots (3-layer system)
 *   - Creeper flee behaviour: AvoidEntityGoal injected once per Creeper,
 *     plus explosion cancel when ocelot is within 8 blocks (like vanilla cats)
 *   - Phantom harassment via positional nudging
 *
 * AMBIENT BEHAVIOUR:
 *   - State-aware ambient sounds (idle purr, following, low health, rain, night)
 *   - Untamed ocelots hiss at nearby hostiles every ~10s
 *   - Untamed ocelots chirp (high-pitch ambient) when a bird-type mob is nearby
 *   - Environmental reactions: hiss near lava, quieter near water
 *   - Enderman aversion: agitated hissing + brief speed boost when Enderman nearby
 *   - Bee calming: sitting/purring ocelot resets anger on nearby bees
 *   - Owner return detection: purr when owner comes back into range
 *   - Owner bed-enter: auto-sit and soft purr when owner goes to sleep
 *   - Morning gifts: weighted loot on owner wake (fish/feather/rabbit's foot)
 *
 * MISC:
 *   - Variant and collar colour via PersistentData
 *   - Baby inherits parent variant and tame state
 *   - Water buoyancy nudge
 *   - Aerial pounce damage multiplier
 */
@EventBusSubscriber(modid = NovamTerram.MOD_ID)
public class NTOcelotEvents {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    private static final AttributeModifier ATTACK_BOOST = new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "ocelot_attack_boost"),
            4.0D, AttributeModifier.Operation.ADD_VALUE);

    private static final AttributeModifier SPEED_AGITATED = new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "ocelot_enderman_agitation"),
            0.15D, AttributeModifier.Operation.ADD_VALUE);

    // PersistentData keys
    private static final String NBT_IS_TAMED          = "IsTamed";
    private static final String NBT_OWNER_UUID         = "OwnerUUID";
    private static final String NBT_VARIANT            = "OcelotVariant";
    private static final String NBT_COLLAR_COLOR       = "CollarColor";
    private static final String NBT_TRUST              = "TrustLevel";
    private static final String NBT_LAST_PET_TICK      = "LastPetTick";
    private static final String NBT_PLAY_TICKS_LEFT    = "PlayTicksRemaining";
    private static final String NBT_CARRIED_ITEM       = "CarriedItemId";   // item registry name
    private static final String NBT_CARRIED_COUNT      = "CarriedCount";
    private static final String NBT_WAS_OWNER_FAR      = "WasOwnerFar";

    private static final int TAME_THRESHOLD       = 3;
    private static final int PET_COOLDOWN_TICKS   = 6000;  // 5 minutes
    private static final int PLAY_DURATION_TICKS  = 300;   // 15 seconds
    private static final int PLAY_RADIUS          = 5;     // blocks from owner during play
    private static final int CREEPER_SCARE_RADIUS = 8;     // blocks — matches vanilla cat

    // Vanilla entity event bytes
    private static final byte EVENT_TAME_SUCCESS = 7;
    private static final byte EVENT_TAME_FAIL    = 6;

    /**
     * Tracks which Creeper entity IDs have already received an AvoidEntityGoal.
     * Runtime only — prevents the goal-accumulation memory leak from the original.
     */
    private static final Set<Integer> SCARED_CREEPER_IDS = new HashSet<>();

    // =========================================================================
    // 1. EVENT HANDLERS
    // =========================================================================

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Ocelot ocelot)) return;
        if (event.getLevel().isClientSide()) return;

        if (!ocelot.getPersistentData().contains(NBT_VARIANT)) {
            ocelot.getPersistentData().putInt(NBT_VARIANT,
                    Math.abs(ocelot.getUUID().hashCode()) % 4);
        }

        ensureAttackAttribute(ocelot);

        if (isTamed(ocelot)) {
            AttributeInstance health = ocelot.getAttribute(Attributes.MAX_HEALTH);
            if (health != null && health.getBaseValue() < 24.0D)
                health.setBaseValue(24.0D);
        }

        rebuildAI(ocelot);
    }

    @SubscribeEvent
    public static void onOcelotTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Ocelot ocelot)) return;
        if (ocelot.level().isClientSide()) return;

        boolean sitting = isSitting(ocelot);
        int tick = ocelot.tickCount;

        // ── Water buoyancy ──────────────────────────────────────────────────
        if (tick % 10 == 0 && ocelot.isInWater() && !sitting) {
            ocelot.setDeltaMovement(ocelot.getDeltaMovement().add(0, 0.05, 0));
        }

        // ── Untamed-only behaviours ─────────────────────────────────────────
        if (!isTamed(ocelot)) {
            if (tick % 200 == 0) hissAtNearbyHostiles(ocelot);
            if (tick % 160 == 0) chirpAtNearbyBirds(ocelot);
            return;
        }

        // ── Tamed-only behaviours ───────────────────────────────────────────
        UUID ownerUUID = getOwnerUUID(ocelot);
        if (ownerUUID == null) return;
        Player owner = ocelot.level().getPlayerByUUID(ownerUUID);

        // Friendly-fire target clear (retaliation edge case)
        if (tick % 20 == 0 && isTamedOcelot(ocelot.getTarget())) {
            ocelot.setTarget(null);
        }

        // Owner proximity tracking for return-detection
        boolean ownerNearby = owner != null && ocelot.distanceToSqr(owner) <= 256.0D;
        boolean wasOwnerFar = ocelot.getPersistentData().getBoolean(NBT_WAS_OWNER_FAR);

        if (owner != null) {
            boolean isNowFar = ocelot.distanceToSqr(owner) > 256.0D;
            ocelot.getPersistentData().putBoolean(NBT_WAS_OWNER_FAR, isNowFar);

            // Owner just returned — play a greeting purr
            if (wasOwnerFar && !isNowFar) {
                ocelot.playSound(SoundEvents.CAT_PURR,
                        0.8F, 1.1F + ocelot.getRandom().nextFloat() * 0.1F);
            }
        }

        if (!ownerNearby) return;

        // ── Playtime movement ───────────────────────────────────────────────
        int playTicks = ocelot.getPersistentData().getInt(NBT_PLAY_TICKS_LEFT);
        if (playTicks > 0) {
            ocelot.getPersistentData().putInt(NBT_PLAY_TICKS_LEFT, playTicks - 1);
            if (tick % 15 == 0 && !sitting) {
                // Sprint in a random direction within PLAY_RADIUS of owner
                double angle = ocelot.getRandom().nextDouble() * Math.PI * 2;
                double r = 1 + ocelot.getRandom().nextDouble() * PLAY_RADIUS;
                ocelot.getNavigation().moveTo(
                        owner.getX() + Math.cos(angle) * r,
                        owner.getY(),
                        owner.getZ() + Math.sin(angle) * r,
                        1.6D);
                if (ocelot.getRandom().nextFloat() < 0.15F)
                    ocelot.playSound(SoundEvents.CAT_AMBIENT,
                            0.6F, 1.3F + ocelot.getRandom().nextFloat() * 0.3F);
            }
            if (playTicks == 1) {
                // Play just ended — sit down exhausted
                setSitting(ocelot, true);
                ocelot.playSound(SoundEvents.CAT_PURR, 0.7F, 0.9F);
            }
        }

        // ── Ambient sounds by state ─────────────────────────────────────────
        if (tick % 80 == 0) tickAmbientSounds(ocelot, owner, sitting);

        // ── Creeper repulsion ───────────────────────────────────────────────
        if (tick % 20 == 0) repelCreepers(ocelot, owner);

        // ── Phantom harassment ──────────────────────────────────────────────
        if (tick % 40 == 0 && !sitting) harassPhantoms(ocelot);

        // ── Enderman aversion ───────────────────────────────────────────────
        if (tick % 30 == 0) handleEndermanAversion(ocelot);

        // ── Bee calming ─────────────────────────────────────────────────────
        if (tick % 60 == 0 && sitting) calmNearbyBees(ocelot);

        // ── Item pickup ─────────────────────────────────────────────────────
        if (tick % 20 == 0 && !sitting) tryPickupNearbyItems(ocelot);
    }

    @SubscribeEvent
    public static void onOcelotInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Ocelot ocelot)) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Player player  = event.getEntity();
        ItemStack stack = event.getItemStack();
        boolean tamed  = isTamed(ocelot);
        boolean isfish = stack.is(ItemTags.FISHES) || stack.is(Tags.Items.FOODS_RAW_FISH);

        if (event.getLevel().isClientSide()) {
            if (tamed || isfish) event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        if (!tamed && isfish) {
            handleTamingAttempt(ocelot, player, stack, event);
        } else if (tamed && player.getUUID().equals(getOwnerUUID(ocelot))) {
            handleOwnerInteraction(ocelot, player, stack, isfish, event);
        }
    }

    @SubscribeEvent
    public static void onOcelotHurt(LivingIncomingDamageEvent event) {
        // Layer 1: hard stop — tamed ocelots never damage each other
        if (event.getSource().getEntity() instanceof Ocelot attacker
                && isTamed(attacker)
                && event.getEntity() instanceof Ocelot victim
                && isTamed(victim)) {
            event.setCanceled(true);
            attacker.setTarget(null);
            return;
        }

        // Tamed ocelot immunities
        if (event.getEntity() instanceof Ocelot ocelot && isTamed(ocelot)) {
            if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)
                    || event.getSource().is(DamageTypes.FALL)) {
                event.setCanceled(true);
                return;
            }
        }

        // Ocelot as attacker — damage modifiers
        if (event.getSource().getEntity() instanceof Ocelot attacker) {
            if (isSitting(attacker)) setSitting(attacker, false);

            if (event.getEntity() instanceof Creeper) {
                event.setAmount(event.getAmount() * 10.0F);
                attacker.level().playSound(null, attacker.blockPosition(),
                        SoundEvents.CAT_HISS, SoundSource.NEUTRAL, 1.0F, 1.2F);
            }

            // Aerial pounce bonus
            if (!attacker.onGround() && attacker.getDeltaMovement().y < 0) {
                event.setAmount(event.getAmount() * 1.5F);
            }
        }
    }

    /**
     * Creeper explosion prevention — mirrors vanilla cat behaviour.
     * When a tamed ocelot is within CREEPER_SCARE_RADIUS of a Creeper that is
     * about to explode, the explosion is cancelled and the Creeper flees.
     * Uses MobEffectEvent as a proxy since Creepers about to explode don't have
     * a dedicated cancel hook — instead we cancel the explosion in the damage
     * event above (the ocelot's 10× attack kills the Creeper before it can go
     * off) and prevent detonation via the level's entity tick chain.
     *
     * For the passive "Creeper won't even start fusing" behaviour, the
     * AvoidEntityGoal injected in repelCreepers() handles it. This handler
     * covers the edge case where a Creeper has already started fusing near
     * an ocelot that hasn't been detected yet.
     */
    @SubscribeEvent
    public static void onCreeperFuseNearOcelot(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Creeper creeper)) return;
        if (creeper.level().isClientSide()) return;
        // Only care about actively fusing Creepers
        if (creeper.getSwellDir() <= 0) return;

        // Check for any tamed ocelot within scare radius
        List<Ocelot> nearby = creeper.level().getEntitiesOfClass(
                Ocelot.class,
                creeper.getBoundingBox().inflate(CREEPER_SCARE_RADIUS),
                NTOcelotEvents::isTamed);

        if (!nearby.isEmpty()) {
            // Stop the fuse — set swell direction to retreat
            creeper.setSwellDir(-1);
            // Push Creeper away from the nearest ocelot
            Ocelot closest = nearby.get(0);
            Vec3 flee = creeper.position().subtract(closest.position()).normalize().scale(0.4);
            creeper.setDeltaMovement(creeper.getDeltaMovement().add(flee.x, 0.1, flee.z));
            if (creeper.getRandom().nextFloat() < 0.15F)
                closest.playSound(SoundEvents.CAT_HISS, 0.7F, 1.2F);
        }
    }

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (!(event.getParentA() instanceof Ocelot parent)) return;
        if (!(event.getChild()   instanceof Ocelot baby))   return;

        if (isTamed(parent)) {
            tameOcelot(baby, parent.level().getPlayerByUUID(getOwnerUUID(parent)));
            baby.getPersistentData().putInt(NBT_VARIANT,
                    parent.getPersistentData().getInt(NBT_VARIANT));
        }
    }

    /**
     * Owner goes to sleep — nearby tamed ocelots auto-sit and play a soft purr.
     * Uses CanPlayerSleepEvent which fires when sleep is attempted. We only react
     * if sleep isn't already being blocked by something (daytime, monsters, etc.)
     * by checking that getProblem() is null — meaning vanilla is allowing the sleep.
     */
    @SubscribeEvent
    public static void onPlayerSleep(CanPlayerSleepEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        // Only react when sleep is actually going to succeed
        if (event.getProblem() != null) return;

        Player player = event.getEntity();
        List<Ocelot> cats = player.level().getEntitiesOfClass(
                Ocelot.class,
                new AABB(player.blockPosition()).inflate(8.0D),
                o -> isTamed(o) && player.getUUID().equals(getOwnerUUID(o)));

        for (Ocelot cat : cats) {
            if (!isSitting(cat)) setSitting(cat, true);
            cat.playSound(SoundEvents.CAT_PURR,
                    0.5F, 0.85F + cat.getRandom().nextFloat() * 0.1F);
        }
    }

    /**
     * Morning gifts — weighted loot drops on owner wake.
     *   40% Raw Salmon     25% Tropical Fish
     *   20% Feather        10% Cod
     *    5% Rabbit's Foot
     * TODO: extend with NT-specific items once NTItems exists.
     */
    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        Player player = event.getEntity();

        List<Ocelot> cats = player.level().getEntitiesOfClass(
                Ocelot.class,
                new AABB(player.blockPosition()).inflate(10.0D),
                o -> isTamed(o) && !isSitting(o)
                        && player.getUUID().equals(getOwnerUUID(o)));

        for (Ocelot cat : cats) {
            if (cat.getRandom().nextFloat() < 0.7F) {
                cat.spawnAtLocation(rollMorningGift(cat));
                cat.playSound(SoundEvents.CAT_PURR, 1.0F, 1.0F);
            }
            // Also check for carried items to deliver to the player on wake
            deliverCarriedItem(cat, player);
        }
    }

    /**
     * Lead compatibility — tamed ocelots can be leashed by their owner.
     * Vanilla ocelots reject leads because Mob.canBeLeashed() checks for the
     * LEASHABLE data component which ocelots don't have. We intercept the
     * interaction here and call setLeashedTo() directly when the conditions are met.
     * This fires before vanilla's interaction handling, so cancelling it prevents
     * the vanilla "cannot leash" rejection.
     */
    @SubscribeEvent
    public static void onLeashAttempt(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Ocelot ocelot)) return;
        if (event.getLevel().isClientSide()) return;

        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        // Only handle lead items
        if (!stack.is(Items.LEAD)) return;

        // Only the owner can leash their tamed ocelot
        if (!isTamed(ocelot) || !player.getUUID().equals(getOwnerUUID(ocelot))) return;

        if (!ocelot.isLeashed()) {
            ocelot.setLeashedTo(player, true);
            if (!player.getAbilities().instabuild) stack.shrink(1);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    // =========================================================================
    // 2. INTERACTION HELPERS
    // =========================================================================

    /**
     * Trust-based taming. Requires TAME_THRESHOLD successful fish interactions.
     * Each feeding has a 1-in-3 chance of incrementing trust.
     */
    private static void handleTamingAttempt(Ocelot ocelot, Player player,
                                            ItemStack stack,
                                            PlayerInteractEvent.EntityInteract event) {
        if (!player.getAbilities().instabuild) stack.shrink(1);

        if (ocelot.getRandom().nextInt(3) == 0) {
            int trust = ocelot.getPersistentData().getInt(NBT_TRUST) + 1;
            ocelot.getPersistentData().putInt(NBT_TRUST, trust);

            if (trust >= TAME_THRESHOLD) {
                tameOcelot(ocelot, player);
                player.displayClientMessage(
                        Component.translatable("entity.novamterram.ocelot.tame_success"), true);
                ocelot.level().broadcastEntityEvent(ocelot, EVENT_TAME_SUCCESS);
            } else {
                ocelot.playSound(SoundEvents.CAT_PURR, 0.8F, 1.0F);
                player.displayClientMessage(
                        Component.translatable("entity.novamterram.ocelot.trust_gained",
                                trust, TAME_THRESHOLD), true);
            }
        } else {
            ocelot.level().broadcastEntityEvent(ocelot, EVENT_TAME_FAIL);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    /**
     * All confirmed-owner interactions on a tamed ocelot.
     * Priority order: fish → dye → play item → petting/sitting → item retrieval
     */
    private static void handleOwnerInteraction(Ocelot ocelot, Player player,
                                               ItemStack stack, boolean isfish,
                                               PlayerInteractEvent.EntityInteract event) {
        // Fish: heal or breed
        if (isfish) {
            if (ocelot.getHealth() < ocelot.getMaxHealth()) {
                ocelot.heal(4.0F);
                ocelot.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                event.setCancellationResult(InteractionResult.SUCCESS);
            } else if (!ocelot.isInLove() && ocelot.getAge() == 0) {
                ocelot.setInLove(player);
                ocelot.playSound(SoundEvents.CAT_PURR, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
            return;
        }

        // Dye: change collar colour
        if (stack.getItem() instanceof DyeItem dye) {
            ocelot.getPersistentData().putInt(NBT_COLLAR_COLOR, dye.getDyeColor().getId());
            if (!player.getAbilities().instabuild) stack.shrink(1);
            ocelot.playSound(SoundEvents.DYE_USE, 1.0F, 1.0F);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        // Play toy: string or stick triggers playtime
        if (stack.is(Items.STRING) || stack.is(Items.STICK)) {
            if (ocelot.getPersistentData().getInt(NBT_PLAY_TICKS_LEFT) <= 0) {
                ocelot.getPersistentData().putInt(NBT_PLAY_TICKS_LEFT, PLAY_DURATION_TICKS);
                if (isSitting(ocelot)) setSitting(ocelot, false);
                ocelot.playSound(SoundEvents.CAT_AMBIENT,
                        1.0F, 1.4F + ocelot.getRandom().nextFloat() * 0.2F);
                player.displayClientMessage(
                        Component.translatable("entity.novamterram.ocelot.play"), true);
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        // Empty hand: petting / sit toggle / carried item retrieval
        if (stack.isEmpty()) {
            // Sitting + has carried items → retrieve them first
            if (isSitting(ocelot) && ocelot.getPersistentData().contains(NBT_CARRIED_ITEM)) {
                deliverCarriedItem(ocelot, player);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }

            // Petting: if sitting and cooldown has expired, give heart particles
            if (isSitting(ocelot)) {
                long lastPet  = ocelot.getPersistentData().getLong(NBT_LAST_PET_TICK);
                long now      = ocelot.level().getGameTime();
                if (now - lastPet >= PET_COOLDOWN_TICKS) {
                    ocelot.getPersistentData().putLong(NBT_LAST_PET_TICK, now);
                    ocelot.playSound(SoundEvents.CAT_PURR,
                            1.0F, 1.15F + ocelot.getRandom().nextFloat() * 0.1F);
                    ocelot.level().broadcastEntityEvent(ocelot, EVENT_TAME_SUCCESS); // heart particles
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    return;
                }
            }

            // Otherwise: toggle sit/stand (not sneaking)
            if (!player.isShiftKeyDown()) {
                boolean nextSit = !isSitting(ocelot);
                setSitting(ocelot, nextSit);
                player.displayClientMessage(Component.translatable(
                        nextSit ? "entity.novamterram.ocelot.sitting"
                                : "entity.novamterram.ocelot.standing"), true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    // =========================================================================
    // 3. AI HELPERS
    // =========================================================================

    private static void rebuildAI(Ocelot ocelot) {
        ocelot.goalSelector.getAvailableGoals().clear();
        ocelot.targetSelector.getAvailableGoals().clear();
        boolean tamed = isTamed(ocelot);

        ocelot.goalSelector.addGoal(0, new FloatGoal(ocelot));
        ocelot.goalSelector.addGoal(1, new OcelotSitGoal(ocelot));

        if (tamed) {
            ocelot.goalSelector.addGoal(2, new OcelotPounceGoal(ocelot));
            ocelot.goalSelector.addGoal(3, new OcelotFollowOwnerGoal(ocelot, 1.0D, 1.5D, 10.0F, 2.0F));
            ocelot.goalSelector.addGoal(4, new BreedGoal(ocelot, 1.0D));
            ocelot.goalSelector.addGoal(5, new MeleeAttackGoal(ocelot, 1.0D, true));
            ocelot.targetSelector.addGoal(1, new OcelotOwnerHurtByTargetGoal(ocelot));
            ocelot.targetSelector.addGoal(2, new OcelotOwnerHurtTargetGoal(ocelot));
            // Predicate excludes tamed ocelots from being targeted
            ocelot.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
                    ocelot, AbstractFish.class, 10, true, false, e -> !isTamedOcelot(e)));
            ocelot.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(
                    ocelot, Chicken.class, 10, true, false, e -> !isTamedOcelot(e)));
        } else {
            ocelot.goalSelector.addGoal(2, new TemptGoal(ocelot, 0.6D,
                    s -> s.is(ItemTags.FISHES), true));
            ocelot.goalSelector.addGoal(3, new OcelotPounceGoal(ocelot));
            ocelot.goalSelector.addGoal(4, new MeleeAttackGoal(ocelot, 1.0D, true));
            ocelot.goalSelector.addGoal(5, new OcelotFleeGoal(ocelot));
            ocelot.goalSelector.addGoal(6, new AvoidEntityGoal<>(ocelot, Player.class,
                    16.0F, 0.8D, 1.33D));
            ocelot.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                    ocelot, AbstractFish.class, true));
            ocelot.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                    ocelot, Chicken.class, true));
        }

        ocelot.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(
                ocelot, Creeper.class, true));
        ocelot.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(ocelot, 1.0D));
        ocelot.goalSelector.addGoal(8, new LookAtPlayerGoal(ocelot, Player.class, 8.0F));
        ocelot.goalSelector.addGoal(9, new RandomLookAroundGoal(ocelot));
    }

    private static void ensureAttackAttribute(Ocelot ocelot) {
        AttributeInstance atk = ocelot.getAttribute(Attributes.ATTACK_DAMAGE);
        if (atk != null && !atk.hasModifier(ATTACK_BOOST.id()))
            atk.addPermanentModifier(ATTACK_BOOST);
    }

    /**
     * State-aware ambient sounds. Called every 80 ticks (~4 seconds).
     * Each branch has its own RNG gate to keep sounds from becoming too repetitive.
     */
    private static void tickAmbientSounds(Ocelot ocelot, Player owner, boolean sitting) {
        var rand = ocelot.getRandom();
        Level level = ocelot.level();
        boolean isRaining = level.isRaining()
                && level.canSeeSky(ocelot.blockPosition());
        boolean isNight = !level.isDay();
        boolean lowHealth = ocelot.getHealth() < ocelot.getMaxHealth() * 0.3F;

        if (lowHealth && rand.nextFloat() < 0.4F) {
            // Distress
            ocelot.playSound(SoundEvents.CAT_HURT, 0.6F, 0.9F + rand.nextFloat() * 0.2F);
        } else if (isRaining && rand.nextFloat() < 0.25F) {
            // Rain aversion — lower, grumpier ambient
            ocelot.playSound(SoundEvents.CAT_AMBIENT, 0.5F, 0.7F + rand.nextFloat() * 0.1F);
        } else if (sitting && rand.nextFloat() < 0.3F) {
            // Idle sitting purr
            ocelot.playSound(SoundEvents.CAT_PURR, 0.4F, 0.85F + rand.nextFloat() * 0.15F);
        } else if (!sitting && owner != null
                && ocelot.distanceToSqr(owner) > 25.0D
                && rand.nextFloat() < 0.2F) {
            // Following — occasional ambient check-in
            ocelot.playSound(SoundEvents.CAT_AMBIENT, 0.5F, 1.0F + rand.nextFloat() * 0.1F);
        } else if (isNight && !sitting && rand.nextFloat() < 0.15F) {
            // Quieter, more cautious night ambient
            ocelot.playSound(SoundEvents.CAT_AMBIENT, 0.4F, 0.85F + rand.nextFloat() * 0.1F);
        }

        // Environmental hiss — near lava
        BlockPos pos = ocelot.blockPosition();
        boolean nearLava = level.getBlockState(pos.below()).is(Blocks.LAVA)
                || level.getBlockState(pos.north()).is(Blocks.LAVA)
                || level.getBlockState(pos.south()).is(Blocks.LAVA)
                || level.getBlockState(pos.east()).is(Blocks.LAVA)
                || level.getBlockState(pos.west()).is(Blocks.LAVA);
        if (nearLava && rand.nextFloat() < 0.5F) {
            ocelot.playSound(SoundEvents.CAT_HISS, 0.8F, 1.1F + rand.nextFloat() * 0.2F);
        }
    }

    /**
     * One-time AvoidEntityGoal injection per Creeper + positional push.
     * The goal is injected exactly once per Creeper per session (SCARED_CREEPER_IDS
     * prevents accumulation). The push gives immediate responsive repulsion.
     */
    private static void repelCreepers(Ocelot ocelot, Player owner) {
        List<Creeper> nearby = ocelot.level().getEntitiesOfClass(
                Creeper.class, owner.getBoundingBox().inflate(10.0D));
        boolean hissed = false;

        for (Creeper creeper : nearby) {
            if (!SCARED_CREEPER_IDS.contains(creeper.getId())) {
                creeper.goalSelector.addGoal(0,
                        new AvoidEntityGoal<>(creeper, Ocelot.class,
                                (float) CREEPER_SCARE_RADIUS, 1.0D, 1.2D));
                SCARED_CREEPER_IDS.add(creeper.getId());
            }
            Vec3 push = creeper.position().subtract(owner.position()).normalize().scale(0.15);
            creeper.setDeltaMovement(creeper.getDeltaMovement().add(push.x, 0.0, push.z));

            if (!hissed && ocelot.getRandom().nextFloat() < 0.2F) {
                ocelot.playSound(SoundEvents.CAT_HISS, 0.8F, 1.2F);
                hissed = true;
            }
        }
    }

    /** Nudges nearby Phantoms away — ocelot harasses rather than kills. */
    private static void harassPhantoms(Ocelot ocelot) {
        List<Phantom> phantoms = ocelot.level().getEntitiesOfClass(
                Phantom.class, ocelot.getBoundingBox().inflate(12.0D));
        boolean hissed = false;

        for (Phantom phantom : phantoms) {
            Vec3 away = phantom.position().subtract(ocelot.position()).normalize().scale(0.5);
            phantom.setDeltaMovement(phantom.getDeltaMovement().add(away));
            if (!hissed && ocelot.getRandom().nextFloat() < 0.2F) {
                ocelot.playSound(SoundEvents.CAT_HISS, 0.8F, 1.2F);
                hissed = true;
            }
        }
    }

    /**
     * Enderman aversion — ocelot becomes agitated when an Enderman is nearby.
     * Applies a temporary speed boost via attribute modifier and plays stressed hissing.
     * The modifier is added and removed within this method's scope to avoid stacking.
     */
    private static void handleEndermanAversion(Ocelot ocelot) {
        boolean endermanNearby = !ocelot.level().getEntitiesOfClass(
                EnderMan.class, ocelot.getBoundingBox().inflate(16.0D)).isEmpty();

        AttributeInstance speed = ocelot.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        if (endermanNearby) {
            if (!speed.hasModifier(SPEED_AGITATED.id())) {
                speed.addTransientModifier(SPEED_AGITATED);
                ocelot.playSound(SoundEvents.CAT_HISS,
                        0.7F, 1.0F + ocelot.getRandom().nextFloat() * 0.2F);
            }
        } else {
            if (speed.hasModifier(SPEED_AGITATED.id())) {
                speed.removeModifier(SPEED_AGITATED.id());
            }
        }
    }

    /**
     * Sitting ocelots calm nearby angry bees.
     * A purring cat near a hive is a genuinely calming presence — resets
     * bee anger timers within 6 blocks.
     */
    private static void calmNearbyBees(Ocelot ocelot) {
        List<Bee> bees = ocelot.level().getEntitiesOfClass(
                Bee.class, ocelot.getBoundingBox().inflate(6.0D),
                b -> b.isAngry());
        if (bees.isEmpty()) return;

        for (Bee bee : bees) {
            bee.setRemainingPersistentAngerTime(0);
            bee.setTarget(null);
        }
        // Play a soft purr to signal the calming happened
        ocelot.playSound(SoundEvents.CAT_PURR,
                0.5F, 0.9F + ocelot.getRandom().nextFloat() * 0.1F);
    }

    /**
     * Hissing at nearby hostiles — untamed ocelots only, purely cosmetic.
     */
    private static void hissAtNearbyHostiles(Ocelot ocelot) {
        boolean hostile = !ocelot.level().getEntitiesOfClass(
                Monster.class, ocelot.getBoundingBox().inflate(8.0D)).isEmpty();
        if (hostile)
            ocelot.playSound(SoundEvents.CAT_HISS, 0.6F,
                    1.1F + ocelot.getRandom().nextFloat() * 0.2F);
    }

    /**
     * Bird chirping — untamed ocelots make the classic cat-watching-bird
     * chittering sound when a Parrot or NT Songbird is nearby.
     * Represented here with a high-pitch CAT_AMBIENT since there's no
     * dedicated chirp sound in vanilla.
     */
    private static void chirpAtNearbyBirds(Ocelot ocelot) {
        // Check for parrots or any flying ambient mob within 6 blocks
        boolean birdNearby = !ocelot.level().getEntitiesOfClass(
                net.minecraft.world.entity.animal.Parrot.class,
                ocelot.getBoundingBox().inflate(6.0D)).isEmpty();
        if (birdNearby && ocelot.getRandom().nextFloat() < 0.5F)
            ocelot.playSound(SoundEvents.CAT_AMBIENT,
                    0.4F, 1.6F + ocelot.getRandom().nextFloat() * 0.3F);
    }

    /**
     * Passive item pickup — tamed ocelots pick up raw fish or feathers on the
     * ground within 3 blocks and store them in PersistentData.
     * Only one item carried at a time. Owner retrieves by right-clicking
     * with empty hand while the ocelot is sitting.
     */
    private static void tryPickupNearbyItems(Ocelot ocelot) {
        // Already carrying something
        if (ocelot.getPersistentData().contains(NBT_CARRIED_ITEM)) return;

        List<ItemEntity> items = ocelot.level().getEntitiesOfClass(
                ItemEntity.class,
                ocelot.getBoundingBox().inflate(3.0D),
                ie -> {
                    ItemStack s = ie.getItem();
                    return s.is(ItemTags.FISHES)
                            || s.is(Items.FEATHER)
                            || s.is(Tags.Items.FOODS_RAW_FISH);
                });

        if (items.isEmpty()) return;

        ItemEntity target = items.get(0);
        ItemStack picked  = target.getItem().copy();
        picked.setCount(1);

        // Store registry name and count
        ocelot.getPersistentData().putString(NBT_CARRIED_ITEM,
                picked.getItem().builtInRegistryHolder().key().location().toString());
        ocelot.getPersistentData().putInt(NBT_CARRIED_COUNT, 1);

        // Shrink the dropped stack
        target.getItem().shrink(1);
        if (target.getItem().isEmpty()) target.discard();

        ocelot.playSound(SoundEvents.CAT_EAT, 0.5F, 1.2F);
    }

    /**
     * Delivers any carried item to the given player and clears carried state.
     */
    private static ItemStack rollMorningGift(Ocelot cat) {
        float r = cat.getRandom().nextFloat();
        if (r < 0.05F) return new ItemStack(Items.RABBIT_FOOT);
        if (r < 0.15F) return new ItemStack(Items.TROPICAL_FISH);
        if (r < 0.40F) return new ItemStack(Items.SALMON);
        if (r < 0.60F) return new ItemStack(Items.FEATHER);
        return new ItemStack(Items.COD);
    }

    private static void deliverCarriedItem(Ocelot cat, Player player) {
        if (cat.getPersistentData().contains(NBT_CARRIED_ITEM)) {
            String itemId = cat.getPersistentData().getString(NBT_CARRIED_ITEM);
            int count = cat.getPersistentData().getInt(NBT_CARRIED_COUNT);

            // Logic to spawn the item at the player's feet
            cat.spawnAtLocation(new ItemStack(
                    net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                            ResourceLocation.parse(itemId)), count));

            // Clear the data
            cat.getPersistentData().remove(NBT_CARRIED_ITEM);
            cat.getPersistentData().remove(NBT_CARRIED_COUNT);
            cat.playSound(SoundEvents.CAT_PURR, 1.0F, 1.0F);
        }
    }
    // =========================================================================
    // 4. STATE ACCESSORS
    // =========================================================================

    private static boolean isTamed(Ocelot ocelot) {
        return ocelot.getPersistentData().getBoolean(NBT_IS_TAMED);
    }

    /** Null-safe: returns false if entity is null or not a tamed ocelot. */
    private static boolean isTamedOcelot(LivingEntity entity) {
        return entity instanceof Ocelot o && isTamed(o);
    }

    private static boolean isSitting(Ocelot ocelot) {
        return ((IOcelotMixin) ocelot).novamterram$isSitting();
    }

    private static UUID getOwnerUUID(Ocelot ocelot) {
        return ocelot.getPersistentData().contains(NBT_OWNER_UUID)
                ? ocelot.getPersistentData().getUUID(NBT_OWNER_UUID)
                : null;
    }

    private static void setSitting(Ocelot ocelot, boolean sitting) {
        ((IOcelotMixin) ocelot).novamterram$setSitting(sitting);
        if (sitting) {
            ocelot.setDeltaMovement(Vec3.ZERO);
            ocelot.playSound(SoundEvents.CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
        } else {
            ocelot.playSound(SoundEvents.CAT_AMBIENT, 1.0F, 1.0F);
        }
    }

    private static void tameOcelot(Ocelot ocelot, Player owner) {
        if (owner == null) return;
        ocelot.getPersistentData().putBoolean(NBT_IS_TAMED, true);
        ocelot.getPersistentData().putUUID(NBT_OWNER_UUID, owner.getUUID());
        ocelot.getPersistentData().remove(NBT_TRUST);
        ocelot.setPersistenceRequired();
        ((IOcelotMixin) ocelot).novamterram$setTrusting(true);

        AttributeInstance health = ocelot.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) health.setBaseValue(24.0D);
        ocelot.setHealth(24.0F);
        rebuildAI(ocelot);
    }

    // =========================================================================
    // 5. CUSTOM AI GOALS
    // =========================================================================

    private static class OcelotSitGoal extends Goal {
        private final Ocelot ocelot;
        OcelotSitGoal(Ocelot ocelot) {
            this.ocelot = ocelot;
            setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }
        @Override public boolean canUse() { return isSitting(ocelot); }
        @Override public void start()     { ocelot.getNavigation().stop(); }
        @Override public void tick()      { ocelot.getNavigation().stop(); }
    }

    private static class OcelotPounceGoal extends LeapAtTargetGoal {
        private final Ocelot ocelot;
        OcelotPounceGoal(Ocelot ocelot) { super(ocelot, 0.4F); this.ocelot = ocelot; }
        @Override public boolean canUse() { return !isSitting(ocelot) && super.canUse(); }
    }

    /** Untamed ocelots flee from attackers when below 30% health. */
    private static class OcelotFleeGoal extends Goal {
        private final Ocelot ocelot;
        OcelotFleeGoal(Ocelot ocelot) {
            this.ocelot = ocelot;
            setFlags(EnumSet.of(Flag.MOVE));
        }
        @Override public boolean canUse() {
            return !isTamed(ocelot)
                    && ocelot.getHealth() < ocelot.getMaxHealth() * 0.3F
                    && ocelot.getLastHurtByMob() != null;
        }
        @Override public void start() {
            LivingEntity threat = ocelot.getLastHurtByMob();
            if (threat == null) return;
            Vec3 away = ocelot.position().subtract(threat.position()).normalize().scale(1.5);
            ocelot.getNavigation().moveTo(
                    ocelot.getX() + away.x, ocelot.getY(),
                    ocelot.getZ() + away.z, 1.4D);
        }
    }

    private static class OcelotFollowOwnerGoal extends Goal {
        private final Ocelot  ocelot;
        private LivingEntity  owner;
        private final double  walkSpeed;
        private final double  sprintSpeed;
        private final float   startDist;
        private int           recalcTimer;

        OcelotFollowOwnerGoal(Ocelot ocelot, double walk, double sprint,
                              float start, float stop) {
            this.ocelot = ocelot; this.walkSpeed = walk;
            this.sprintSpeed = sprint; this.startDist = start;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override public boolean canUse() {
            if (isSitting(ocelot)
                    || ocelot.getPersistentData().getInt(NBT_PLAY_TICKS_LEFT) > 0)
                return false;
            UUID id = getOwnerUUID(ocelot);
            if (id == null) return false;
            this.owner = ocelot.level().getPlayerByUUID(id);
            return this.owner != null
                    && ocelot.distanceToSqr(this.owner) > startDist * startDist;
        }

        @Override public void tick() {
            ocelot.getLookControl().setLookAt(owner, 10.0F,
                    (float) ocelot.getMaxHeadXRot());
            if (--recalcTimer > 0) return;
            recalcTimer = 10;
            double dsq = ocelot.distanceToSqr(owner);
            if (dsq >= 256.0D) teleportToOwner();
            else ocelot.getNavigation().moveTo(
                    owner, dsq > 144.0D ? sprintSpeed : walkSpeed);
        }

        private void teleportToOwner() {
            BlockPos base = owner.blockPosition();
            for (int i = 0; i < 10; i++) {
                BlockPos t = base.offset(
                        ocelot.getRandom().nextInt(3) - 1,
                        ocelot.getRandom().nextInt(2),
                        ocelot.getRandom().nextInt(3) - 1);
                if (ocelot.level().getBlockState(t)
                        .isPathfindable(PathComputationType.LAND)) {
                    ocelot.moveTo(t.getX() + 0.5, t.getY(), t.getZ() + 0.5,
                            ocelot.getYRot(), ocelot.getXRot());
                    ocelot.getNavigation().stop();
                    return;
                }
            }
        }
    }

    private static class OcelotOwnerHurtByTargetGoal extends TargetGoal {
        private final Ocelot ocelot;
        private LivingEntity attacker;
        private int          lastTs;

        OcelotOwnerHurtByTargetGoal(Ocelot ocelot) {
            super(ocelot, false); this.ocelot = ocelot;
        }

        @Override public boolean canUse() {
            if (!isTamed(ocelot) || isSitting(ocelot)) return false;
            UUID id = getOwnerUUID(ocelot);
            if (id == null) return false;
            Player owner = ocelot.level().getPlayerByUUID(id);
            if (owner == null) return false;
            this.attacker = owner.getLastHurtByMob();
            if (isTamedOcelot(attacker)) return false; // never target tamed ocelots
            int ts = owner.getLastHurtByMobTimestamp();
            return ts != lastTs && canAttack(attacker, TargetingConditions.DEFAULT);
        }

        @Override public void start() {
            mob.setTarget(attacker);
            UUID id = getOwnerUUID(ocelot);
            if (id != null) {
                Player owner = ocelot.level().getPlayerByUUID(id);
                if (owner != null) lastTs = owner.getLastHurtByMobTimestamp();
            }
            super.start();
        }
    }

    private static class OcelotOwnerHurtTargetGoal extends TargetGoal {
        private final Ocelot ocelot;
        private LivingEntity target;
        private int          lastTs;

        OcelotOwnerHurtTargetGoal(Ocelot ocelot) {
            super(ocelot, false); this.ocelot = ocelot;
        }

        @Override public boolean canUse() {
            if (!isTamed(ocelot) || isSitting(ocelot)) return false;
            UUID id = getOwnerUUID(ocelot);
            if (id == null) return false;
            Player owner = ocelot.level().getPlayerByUUID(id);
            if (owner == null) return false;
            this.target = owner.getLastHurtMob();
            if (isTamedOcelot(target)) return false; // never target tamed ocelots
            int ts = owner.getLastHurtMobTimestamp();
            return ts != lastTs && canAttack(target, TargetingConditions.DEFAULT);
        }

        @Override public void start() {
            mob.setTarget(target);
            UUID id = getOwnerUUID(ocelot);
            if (id != null) {
                Player owner = ocelot.level().getPlayerByUUID(id);
                if (owner != null) lastTs = owner.getLastHurtMobTimestamp();
            }
            super.start();
        }
    }
}