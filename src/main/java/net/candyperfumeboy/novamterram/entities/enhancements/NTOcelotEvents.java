package net.candyperfumeboy.novamterram.entities.enhancements;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.util.IOcelotMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;
import java.util.UUID;

/**
 * Turns the vanilla ocelot into a tameable wildcat companion.
 *
 * Wild: tamed with fish (trust builds up over several feedings), flees when badly hurt,
 * hunts creepers.
 * Tamed: sit/follow, collar dyeing, healing and breeding with fish, sneak + right-click
 * to pet or take its item, play with string or a stick, perches on beds, chests and lit
 * furnaces, carries fish and feathers it finds, morning gifts, protects the
 * owner, stops creeper fuses, chases phantoms off and calms bees. Never hunts by itself.
 * The owner is told when it dies.
 *
 * AI goals live in NTOcelotGoals.
 */
@EventBusSubscriber(modid = NovamTerram.MOD_ID)
public class NTOcelotEvents {

    private static final AttributeModifier ATTACK_BOOST = new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "ocelot_attack_boost"),
            4.0D, AttributeModifier.Operation.ADD_VALUE);

    private static final AttributeModifier SPEED_AGITATED = new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "ocelot_enderman_agitation"),
            0.15D, AttributeModifier.Operation.ADD_VALUE);

    // Saved data. The first group keeps the old names so existing tamed ocelots still work.
    private static final String NBT_IS_TAMED = "IsTamed";
    private static final String NBT_OWNER = "OwnerUUID";
    private static final String NBT_COLLAR = "CollarColor";
    private static final String NBT_TRUST = "TrustLevel";
    private static final String NBT_LAST_PET = "LastPetTick";
    private static final String NBT_CARRIED_ITEM = "CarriedItemId";
    private static final String NBT_CARRIED_COUNT = "CarriedCount";
    private static final String NBT_VARIANT = "NTOcelotVariant";
    private static final String NBT_SITTING = "NTSitting";
    private static final String NBT_SLEEP_SIT = "NTSleepSit";
    private static final String NBT_OWNER_FAR = "WasOwnerFar";
    private static final String NBT_LAST_GIFT_DAY = "NTLastGiftDay";
    private static final String NBT_NO_PICKUP_UNTIL = "NTNoPickupUntil";
    static final String NBT_PLAY_UNTIL = "NTPlayUntil";

    private static final int TAME_THRESHOLD = 3;
    private static final int PET_COOLDOWN_TICKS = 6000;
    private static final int PLAY_DURATION_TICKS = 300;
    private static final int CREEPER_SCARE_RADIUS = 8;

    // Ocelots use their own event ids for hearts and smoke
    private static final byte EVENT_HEARTS = 41;
    private static final byte EVENT_SMOKE = 40;

    // ------------------------------------------------------------ state helpers

    private static IOcelotMixin data(Ocelot ocelot) {
        return (IOcelotMixin) ocelot;
    }

    static boolean isTamed(Ocelot ocelot) {
        return ocelot.getPersistentData().getBoolean(NBT_IS_TAMED);
    }

    /** Whether the owner has told it to stay. Perching on furniture does not count. */
    static boolean isSitting(Ocelot ocelot) {
        return ocelot.getPersistentData().getBoolean(NBT_SITTING);
    }

    static void showSittingPose(Ocelot ocelot, boolean sitting) {
        data(ocelot).novamterram$setSitting(sitting);
    }

    private static UUID ownerId(Ocelot ocelot) {
        CompoundTag tag = ocelot.getPersistentData();
        return tag.hasUUID(NBT_OWNER) ? tag.getUUID(NBT_OWNER) : null;
    }

    /** The owner, if they are in the same dimension. */
    static Player ownerOf(Ocelot ocelot) {
        UUID id = ownerId(ocelot);
        return id == null ? null : ocelot.level().getPlayerByUUID(id);
    }

    private static boolean isOwner(Ocelot ocelot, Player player) {
        return isTamed(ocelot) && player.getUUID().equals(ownerId(ocelot));
    }

    static void setSitting(Ocelot ocelot, boolean sitting) {
        showSittingPose(ocelot, sitting);
        ocelot.getPersistentData().putBoolean(NBT_SITTING, sitting);
        ocelot.getPersistentData().remove(NBT_SLEEP_SIT);
        if (sitting) {
            ocelot.getPersistentData().remove(NBT_PLAY_UNTIL);
            ocelot.getNavigation().stop();
            ocelot.setTarget(null);
        }
    }

    private static void meow(Ocelot ocelot, SoundEvent sound, float volume, float pitch) {
        ocelot.playSound(sound, volume, pitch + ocelot.getRandom().nextFloat() * 0.15F);
    }

    private static void consume(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) stack.shrink(1);
    }

    private static boolean isFish(ItemStack stack) {
        return stack.is(ItemTags.FISHES) || stack.is(Tags.Items.FOODS_RAW_FISH);
    }

    // ------------------------------------------------------------ lifecycle

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Ocelot ocelot)) return;

        CompoundTag tag = ocelot.getPersistentData();
        if (!tag.contains(NBT_VARIANT)) {
            // 70% vanilla, 10% each of white, grey and brown
            int roll = Math.abs(ocelot.getUUID().hashCode() % 10);
            tag.putInt(NBT_VARIANT, roll < 7 ? 0 : roll - 6);
        }
        data(ocelot).novamterram$setVariant(tag.getInt(NBT_VARIANT));
        data(ocelot).novamterram$setCollar(isTamed(ocelot) ? collarColor(ocelot) : -1);
        showSittingPose(ocelot, isSitting(ocelot));
        data(ocelot).novamterram$setHeldItem(carriedStack(ocelot));

        AttributeInstance attack = ocelot.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null && !attack.hasModifier(ATTACK_BOOST.id())) {
            attack.addPermanentModifier(ATTACK_BOOST);
        }

        NTOcelotGoals.install(ocelot);
    }

    private static int collarColor(Ocelot ocelot) {
        CompoundTag tag = ocelot.getPersistentData();
        return tag.contains(NBT_COLLAR) ? tag.getInt(NBT_COLLAR) : DyeColor.RED.getId();
    }

    private static void tame(Ocelot ocelot, UUID owner) {
        CompoundTag tag = ocelot.getPersistentData();
        tag.putBoolean(NBT_IS_TAMED, true);
        tag.putUUID(NBT_OWNER, owner);
        tag.remove(NBT_TRUST);

        ocelot.setPersistenceRequired();
        data(ocelot).novamterram$setTrusting(true);
        data(ocelot).novamterram$setCollar(collarColor(ocelot));

        AttributeInstance health = ocelot.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) health.setBaseValue(24.0D);
        ocelot.setHealth(ocelot.getMaxHealth());
        ocelot.setTarget(null);
        NTOcelotGoals.install(ocelot);
    }

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (!(event.getChild() instanceof Ocelot baby)) return;
        if (!(event.getParentA() instanceof Ocelot first) || !(event.getParentB() instanceof Ocelot second)) return;

        Ocelot looksLike = baby.getRandom().nextBoolean() ? first : second;
        baby.getPersistentData().putInt(NBT_VARIANT, looksLike.getPersistentData().getInt(NBT_VARIANT));

        Ocelot tamedParent = isTamed(first) ? first : isTamed(second) ? second : null;
        if (tamedParent != null && ownerId(tamedParent) != null) {
            tame(baby, ownerId(tamedParent));
        }
    }

    // LOWEST so nothing is announced if another handler cancels the death
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onOcelotDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Ocelot ocelot) || ocelot.level().isClientSide()) return;

        // Don't destroy whatever it was carrying
        dropCarriedItem(ocelot);

        // Same message and game rule as vanilla pets
        MinecraftServer server = ocelot.getServer();
        UUID id = ownerId(ocelot);
        if (!isTamed(ocelot) || server == null || id == null
                || !ocelot.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) return;

        ServerPlayer owner = server.getPlayerList().getPlayer(id);
        if (owner != null) {
            owner.sendSystemMessage(ocelot.getCombatTracker().getDeathMessage());
        }
    }

    // ------------------------------------------------------------ ticking

    @SubscribeEvent
    public static void onOcelotTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Ocelot ocelot) || ocelot.level().isClientSide()) return;
        int tick = ocelot.tickCount;
        if (tick % 10 != 0) return; // everything below runs on a multiple of 10

        if (!isTamed(ocelot)) {
            if (tick % 200 == 0) hissAtHostiles(ocelot);
            if (tick % 160 == 0) chirpAtBirds(ocelot);
            return;
        }

        Player owner = ownerOf(ocelot);
        if (owner == null) return;
        boolean far = ocelot.distanceToSqr(owner) > 256.0D;

        if (tick % 20 == 0) {
            if (ocelot.getPersistentData().getBoolean(NBT_OWNER_FAR) && !far) {
                meow(ocelot, SoundEvents.CAT_PURR, 0.8F, 1.1F);
            }
            ocelot.getPersistentData().putBoolean(NBT_OWNER_FAR, far);
        }
        if (far) return;

        boolean sitting = isSitting(ocelot);
        if (tick % 20 == 0 && !sitting) pickUpItem(ocelot);
        if (tick % 30 == 0) reactToEndermen(ocelot);
        if (tick % 40 == 0 && !sitting) chasePhantoms(ocelot);
        if (tick % 60 == 0 && sitting) calmBees(ocelot);
        if (tick % 200 == 0) ambientSound(ocelot, owner, sitting);
    }

    /** A tamed ocelot nearby stops creepers from finishing their fuse. */
    @SubscribeEvent
    public static void onCreeperTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Creeper creeper) || creeper.level().isClientSide()) return;
        // Flint and steel still works
        if (creeper.getSwellDir() <= 0 || creeper.isIgnited()) return;

        List<Ocelot> cats = creeper.level().getEntitiesOfClass(Ocelot.class,
                creeper.getBoundingBox().inflate(CREEPER_SCARE_RADIUS), NTOcelotEvents::isTamed);
        if (cats.isEmpty()) return;

        creeper.setSwellDir(-1);
        if (creeper.tickCount % 20 == 0) meow(cats.get(0), SoundEvents.CAT_HISS, 0.6F, 1.1F);
    }

    private static void hissAtHostiles(Ocelot ocelot) {
        if (ocelot.getRandom().nextFloat() < 0.35F && !ocelot.level().getEntitiesOfClass(
                Monster.class, ocelot.getBoundingBox().inflate(8.0D)).isEmpty()) {
            meow(ocelot, SoundEvents.CAT_HISS, 0.5F, 1.1F);
        }
    }

    private static void chirpAtBirds(Ocelot ocelot) {
        if (ocelot.getRandom().nextFloat() < 0.5F && !ocelot.level().getEntitiesOfClass(
                Parrot.class, ocelot.getBoundingBox().inflate(6.0D)).isEmpty()) {
            meow(ocelot, SoundEvents.CAT_AMBIENT, 0.4F, 1.6F);
        }
    }

    private static void ambientSound(Ocelot ocelot, Player owner, boolean sitting) {
        Level level = ocelot.level();
        BlockPos pos = ocelot.blockPosition();
        float roll = ocelot.getRandom().nextFloat();

        for (BlockPos near : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 0, 1))) {
            if (level.getFluidState(near).is(FluidTags.LAVA)) {
                meow(ocelot, SoundEvents.CAT_HISS, 0.7F, 1.1F);
                return;
            }
        }

        if (ocelot.getHealth() < ocelot.getMaxHealth() * 0.3F) {
            if (roll < 0.4F) meow(ocelot, SoundEvents.CAT_BEG_FOR_FOOD, 0.6F, 0.9F);
        } else if (level.isRainingAt(pos)) {
            if (roll < 0.25F) meow(ocelot, SoundEvents.CAT_AMBIENT, 0.5F, 0.7F);
        } else if (sitting) {
            if (roll < 0.3F) meow(ocelot, SoundEvents.CAT_PURR, 0.4F, 0.85F);
        } else if (ocelot.distanceToSqr(owner) > 25.0D && roll < 0.2F) {
            meow(ocelot, SoundEvents.CAT_AMBIENT, 0.5F, 1.0F);
        }
    }

    private static void chasePhantoms(Ocelot ocelot) {
        List<Phantom> phantoms = ocelot.level().getEntitiesOfClass(Phantom.class,
                ocelot.getBoundingBox().inflate(12.0D));
        if (phantoms.isEmpty()) return;

        for (Phantom phantom : phantoms) {
            phantom.setDeltaMovement(phantom.getDeltaMovement()
                    .add(phantom.position().subtract(ocelot.position()).normalize().scale(0.5)));
        }
        if (ocelot.getRandom().nextFloat() < 0.4F) meow(ocelot, SoundEvents.CAT_HISS, 0.8F, 1.2F);
    }

    private static void reactToEndermen(Ocelot ocelot) {
        AttributeInstance speed = ocelot.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        boolean near = !ocelot.level().getEntitiesOfClass(
                EnderMan.class, ocelot.getBoundingBox().inflate(16.0D)).isEmpty();
        boolean agitated = speed.hasModifier(SPEED_AGITATED.id());

        if (near && !agitated) {
            speed.addTransientModifier(SPEED_AGITATED);
            meow(ocelot, SoundEvents.CAT_HISS, 0.7F, 1.0F);
        } else if (!near && agitated) {
            speed.removeModifier(SPEED_AGITATED.id());
        }
    }

    private static void calmBees(Ocelot ocelot) {
        List<Bee> bees = ocelot.level().getEntitiesOfClass(
                Bee.class, ocelot.getBoundingBox().inflate(6.0D), Bee::isAngry);
        if (bees.isEmpty()) return;

        for (Bee bee : bees) {
            bee.stopBeingAngry();
        }
        meow(ocelot, SoundEvents.CAT_PURR, 0.5F, 0.9F);
    }

    // ------------------------------------------------------------ carried items

    private static void pickUpItem(Ocelot ocelot) {
        CompoundTag tag = ocelot.getPersistentData();
        if (tag.contains(NBT_CARRIED_ITEM)) return;
        if (ocelot.level().getGameTime() < tag.getLong(NBT_NO_PICKUP_UNTIL)) return;

        List<ItemEntity> found = ocelot.level().getEntitiesOfClass(ItemEntity.class,
                ocelot.getBoundingBox().inflate(2.0D),
                item -> !item.hasPickUpDelay() && (isFish(item.getItem()) || item.getItem().is(Items.FEATHER)));
        if (found.isEmpty() || !EventHooks.canEntityGrief(ocelot.level(), ocelot)) return;

        ItemEntity item = found.get(0);
        ItemStack stack = item.getItem().copy();
        tag.putString(NBT_CARRIED_ITEM, BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        tag.putInt(NBT_CARRIED_COUNT, 1);

        data(ocelot).novamterram$setHeldItem(carriedStack(ocelot));

        stack.shrink(1);
        if (stack.isEmpty()) {
            item.discard();
        } else {
            item.setItem(stack);
        }
        meow(ocelot, SoundEvents.CAT_EAT, 0.5F, 1.2F);
    }

    private static ItemStack carriedStack(Ocelot ocelot) {
        CompoundTag tag = ocelot.getPersistentData();
        ResourceLocation id = ResourceLocation.tryParse(tag.getString(NBT_CARRIED_ITEM));
        if (!tag.contains(NBT_CARRIED_ITEM) || id == null) return ItemStack.EMPTY;
        return new ItemStack(BuiltInRegistries.ITEM.get(id), tag.getInt(NBT_CARRIED_COUNT));
    }

    private static boolean dropCarriedItem(Ocelot ocelot) {
        CompoundTag tag = ocelot.getPersistentData();
        if (!tag.contains(NBT_CARRIED_ITEM)) return false;

        ItemStack stack = carriedStack(ocelot);
        if (!stack.isEmpty()) ocelot.spawnAtLocation(stack);
        data(ocelot).novamterram$setHeldItem(ItemStack.EMPTY);
        tag.remove(NBT_CARRIED_ITEM);
        tag.remove(NBT_CARRIED_COUNT);
        // Give the owner time to collect it before the cat grabs it again
        tag.putLong(NBT_NO_PICKUP_UNTIL, ocelot.level().getGameTime() + 600);
        return true;
    }

    // ------------------------------------------------------------ interaction

    @SubscribeEvent
    public static void onOcelotInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Ocelot ocelot)) return;

        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        boolean mainHand = event.getHand() == InteractionHand.MAIN_HAND;

        if (event.getLevel().isClientSide()) {
            // The collar is synced, so it tells the client whether the cat is tamed
            boolean ours = data(ocelot).novamterram$getCollar() < 0
                    ? isFish(stack)
                    : mainHand && (stack.isEmpty() || isFish(stack) || stack.getItem() instanceof DyeItem
                    || stack.is(Items.STRING) || stack.is(Items.STICK));
            if (ours) finish(event);
            return;
        }

        if (!isTamed(ocelot)) {
            if (isFish(stack)) {
                tryTame(ocelot, player, stack);
                finish(event);
            }
        } else if (mainHand && isOwner(ocelot, player) && handleOwner(ocelot, player, stack)) {
            finish(event);
        }
    }

    /** Cancelling stops vanilla from also handling the click and using a second item. */
    private static void finish(PlayerInteractEvent.EntityInteract event) {
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static void tryTame(Ocelot ocelot, Player player, ItemStack stack) {
        consume(player, stack);

        if (ocelot.getRandom().nextInt(3) != 0) {
            ocelot.level().broadcastEntityEvent(ocelot, EVENT_SMOKE);
            showWary(player);
            return;
        }

        int trust = ocelot.getPersistentData().getInt(NBT_TRUST) + 1;
        if (trust >= TAME_THRESHOLD) {
            tame(ocelot, player.getUUID());
            player.displayClientMessage(Component.translatable("entity.novamterram.ocelot.tame_success"), true);
            ocelot.level().broadcastEntityEvent(ocelot, EVENT_HEARTS);
        } else {
            ocelot.getPersistentData().putInt(NBT_TRUST, trust);
            ocelot.setPersistenceRequired(); // don't lose the progress to despawning
            meow(ocelot, SoundEvents.CAT_PURR, 0.8F, 1.0F);
            showWary(player);
        }
    }

    // Worded and shown the same way as the fox messages
    private static void showWary(Player player) {
        player.displayClientMessage(Component.translatable("entity.novamterram.ocelot.trust_gained"), true);
    }

    /** Returns false to let vanilla handle the click (growing babies, leads, name tags). */
    private static boolean handleOwner(Ocelot ocelot, Player player, ItemStack stack) {
        if (isFish(stack)) {
            if (ocelot.getHealth() < ocelot.getMaxHealth()) {
                ocelot.heal(4.0F);
                meow(ocelot, SoundEvents.CAT_EAT, 1.0F, 0.95F);
            } else if (ocelot.getAge() == 0 && ocelot.canFallInLove()) {
                ocelot.setInLove(player);
                meow(ocelot, SoundEvents.CAT_PURR, 1.0F, 1.0F);
            } else {
                return false;
            }
            consume(player, stack);
            return true;
        }

        if (stack.getItem() instanceof DyeItem dye) {
            int color = dye.getDyeColor().getId();
            if (color != collarColor(ocelot)) {
                ocelot.getPersistentData().putInt(NBT_COLLAR, color);
                data(ocelot).novamterram$setCollar(color);
                consume(player, stack);
                ocelot.playSound(SoundEvents.DYE_USE, 1.0F, 1.0F);
            }
            return true;
        }

        if (stack.is(Items.STRING) || stack.is(Items.STICK)) {
            long now = ocelot.level().getGameTime();
            if (ocelot.getPersistentData().getLong(NBT_PLAY_UNTIL) <= now) {
                setSitting(ocelot, false);
                ocelot.getPersistentData().putLong(NBT_PLAY_UNTIL, now + PLAY_DURATION_TICKS);
                meow(ocelot, SoundEvents.CAT_AMBIENT, 1.0F, 1.4F);
                player.displayClientMessage(Component.translatable("entity.novamterram.ocelot.play"), true);
            }
            return true;
        }

        if (!stack.isEmpty()) return false;

        // Sneak: take what it is carrying, otherwise pet it
        if (player.isShiftKeyDown()) {
            if (dropCarriedItem(ocelot)) {
                meow(ocelot, SoundEvents.CAT_PURR, 1.0F, 1.0F);
                return true;
            }
            CompoundTag tag = ocelot.getPersistentData();
            long now = ocelot.level().getGameTime();
            if (!tag.contains(NBT_LAST_PET) || now - tag.getLong(NBT_LAST_PET) >= PET_COOLDOWN_TICKS) {
                tag.putLong(NBT_LAST_PET, now);
                meow(ocelot, SoundEvents.CAT_PURR, 1.0F, 1.15F);
                ocelot.level().broadcastEntityEvent(ocelot, EVENT_HEARTS);
            }
            return true;
        }

        // Normal click: always sit or stand
        boolean sit = !isSitting(ocelot);
        setSitting(ocelot, sit);
        meow(ocelot, sit ? SoundEvents.CAT_BEG_FOR_FOOD : SoundEvents.CAT_AMBIENT, 0.7F, 1.0F);
        player.displayClientMessage(Component.translatable(
                sit ? "entity.novamterram.ocelot.sitting" : "entity.novamterram.ocelot.standing"), true);
        return true;
    }

    // ------------------------------------------------------------ combat

    @SubscribeEvent
    public static void onOcelotHurt(LivingIncomingDamageEvent event) {
        Entity source = event.getSource().getEntity();

        if (event.getEntity() instanceof Ocelot victim && !victim.level().isClientSide() && isTamed(victim)) {
            // Fall damage is already ignored by vanilla ocelots
            if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)
                    || source instanceof Ocelot other && isTamed(other)) {
                event.setCanceled(true);
                return;
            }
            if (isSitting(victim)) setSitting(victim, false);
        }

        if (source instanceof Ocelot attacker) {
            if (event.getEntity() instanceof Creeper) {
                event.setAmount(event.getAmount() * 10.0F);
                meow(attacker, SoundEvents.CAT_HISS, 1.0F, 1.2F);
            }
            // Pounce bonus while coming down from a leap
            if (!attacker.onGround() && attacker.getDeltaMovement().y < 0) {
                event.setAmount(event.getAmount() * 1.5F);
            }
        }
    }

    // ------------------------------------------------------------ sleeping

    private static List<Ocelot> petsNear(Player player, double radius) {
        return player.level().getEntitiesOfClass(Ocelot.class,
                player.getBoundingBox().inflate(radius), ocelot -> isOwner(ocelot, player));
    }

    /** Standing pets curl up while their owner sleeps. */
    @SubscribeEvent
    public static void onPlayerSleep(CanPlayerSleepEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || event.getProblem() != null) return;

        for (Ocelot cat : petsNear(player, 8.0D)) {
            if (isSitting(cat)) continue;
            setSitting(cat, true);
            cat.getPersistentData().putBoolean(NBT_SLEEP_SIT, true);
            meow(cat, SoundEvents.CAT_PURR, 0.5F, 0.85F);
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide()) return;

        // True only when the night was actually slept through, not when leaving the bed
        boolean sleptThrough = player.isSleepingLongEnough() && !event.updateLevel() && !event.wakeImmediately();
        long day = level.getDayTime() / 24000L;

        for (Ocelot cat : petsNear(player, 10.0D)) {
            CompoundTag tag = cat.getPersistentData();
            boolean curledUp = tag.getBoolean(NBT_SLEEP_SIT);
            // Cats the owner told to stay put are left alone
            if (isSitting(cat) && !curledUp) continue;
            if (curledUp) setSitting(cat, false);
            if (!sleptThrough) continue;

            if (dropCarriedItem(cat)) meow(cat, SoundEvents.CAT_PURR, 1.0F, 1.0F);

            if (!cat.isBaby() && (!tag.contains(NBT_LAST_GIFT_DAY) || tag.getLong(NBT_LAST_GIFT_DAY) != day)) {
                tag.putLong(NBT_LAST_GIFT_DAY, day);
                if (cat.getRandom().nextFloat() < 0.7F) {
                    cat.spawnAtLocation(rollMorningGift(cat));
                    meow(cat, SoundEvents.CAT_PURR, 1.0F, 1.0F);
                }
            }
        }
    }

    // TODO: add Novam Terram items to the gift pool
    private static ItemStack rollMorningGift(Ocelot cat) {
        float roll = cat.getRandom().nextFloat();
        if (roll < 0.05F) return new ItemStack(Items.RABBIT_FOOT);
        if (roll < 0.15F) return new ItemStack(Items.TROPICAL_FISH);
        if (roll < 0.40F) return new ItemStack(Items.SALMON);
        if (roll < 0.60F) return new ItemStack(Items.FEATHER);
        return new ItemStack(Items.COD);
    }
}
