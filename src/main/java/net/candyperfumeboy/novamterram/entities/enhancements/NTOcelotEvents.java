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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = NovamTerram.MOD_ID)
public class NTOcelotEvents {

    private static final AttributeModifier ATTACK_BOOST = new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "ocelot_attack_boost"),
            4.0D,
            AttributeModifier.Operation.ADD_VALUE
    );

    private static final String NBT_IS_TAMED = "IsTamed";
    private static final String NBT_OWNER_UUID = "OwnerUUID";
    private static final String NBT_VARIANT = "OcelotVariant";
    private static final String NBT_COLLAR_COLOR = "CollarColor";

    // ========================================================================================
    // 1. EVENT HANDLERS
    // ========================================================================================

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Ocelot ocelot && !event.getLevel().isClientSide) {
            if (!ocelot.getPersistentData().contains(NBT_VARIANT)) {
                ocelot.getPersistentData().putInt(NBT_VARIANT, Math.abs(ocelot.getUUID().hashCode()) % 4);
            }
            ensureAttackAttribute(ocelot);
            if (isTamed(ocelot)) {
                AttributeInstance healthAttr = ocelot.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttr != null && healthAttr.getBaseValue() < 24.0D) {
                    healthAttr.setBaseValue(24.0D);
                }
            }
            rebuildAI(ocelot);
        }
    }

    @SubscribeEvent
    public static void onOcelotTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Ocelot ocelot)) return;

        // Check the Mixin's synced data
        boolean isSitting = ((IOcelotMixin) ocelot).novamterram$isSitting();

        if (isSitting) {
            // Force the pose on both Client and Server every tick
            if (ocelot.getPose() != Pose.SITTING) {
                ocelot.setPose(Pose.SITTING);
            }
            
            // Stop movement
            ocelot.getNavigation().stop();
            ocelot.setDeltaMovement(Vec3.ZERO);
            ocelot.setSprinting(false);
            ocelot.setShiftKeyDown(false);
        }

        // Server-side only logic below
        if (ocelot.level().isClientSide) return;
        if (ocelot.tickCount % 10 != 0) return;

        if (ocelot.isInWater() && !isSitting) {
            ocelot.setDeltaMovement(ocelot.getDeltaMovement().add(0, 0.05, 0));
        }

        if (isTamed(ocelot)) {
            UUID ownerUUID = getOwnerUUID(ocelot);
            if (ownerUUID != null) {
                Player owner = ocelot.level().getPlayerByUUID(ownerUUID);
                if (owner != null && ocelot.distanceToSqr(owner) < 256.0D) {
                    if (ocelot.tickCount % 20 == 0) scareCreepers(ocelot, owner);
                    if (ocelot.tickCount % 40 == 0 && !isSitting) scarePhantoms(ocelot);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onOcelotInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Ocelot ocelot)) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        boolean isTamed = isTamed(ocelot);
        boolean isFood = stack.is(ItemTags.FISHES) || stack.is(Tags.Items.FOODS_RAW_FISH);

        if (event.getLevel().isClientSide) {
            if (isTamed || isFood) event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        if (!isTamed && isFood) {
            if (!player.getAbilities().instabuild) stack.shrink(1);
            if (ocelot.getRandom().nextInt(3) == 0) {
                tameOcelot(ocelot, player);
                player.displayClientMessage(Component.translatable("entity.novamterram.ocelot.tame_success"), true);
            } else {
                ocelot.level().broadcastEntityEvent(ocelot, (byte) 6);
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
        } else if (isTamed && player.getUUID().equals(getOwnerUUID(ocelot))) {
            if (isFood) {
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
            } else if (stack.getItem() instanceof DyeItem dye) {
                ocelot.getPersistentData().putInt(NBT_COLLAR_COLOR, dye.getDyeColor().getId());
                if (!player.getAbilities().instabuild) stack.shrink(1);
                ocelot.playSound(SoundEvents.DYE_USE, 1.0F, 1.0F);
                syncOcelot(ocelot);
                event.setCancellationResult(InteractionResult.SUCCESS);
            } else if (!player.isShiftKeyDown()) {
                boolean nextSitState = !isSitting(ocelot);
                setSitting(ocelot, nextSitState);
                player.displayClientMessage(Component.translatable(nextSitState ? "entity.novamterram.ocelot.sitting" : "entity.novamterram.ocelot.standing"), true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public static void onOcelotHurt(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Ocelot ocelot && isTamed(ocelot)) {
            if (event.getSource().is(DamageTypeTags.IS_EXPLOSION) || event.getSource().is(DamageTypes.FALL)) {
                event.setCanceled(true);
                return;
            }
        }
        if (event.getSource().getEntity() instanceof Ocelot attacker) {
            if (isSitting(attacker)) setSitting(attacker, false);
            if (event.getEntity() instanceof Creeper) {
                event.setAmount(event.getAmount() * 10.0F);
                attacker.level().playSound(null, attacker.blockPosition(), SoundEvents.CAT_HISS, SoundSource.NEUTRAL, 1.0F, 1.2F);
            }
            if (!attacker.onGround() && attacker.getDeltaMovement().y < 0) {
                event.setAmount(event.getAmount() * 1.5F);
            }
        }
    }

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (event.getParentA() instanceof Ocelot pA && event.getChild() instanceof Ocelot baby) {
            if (isTamed(pA)) {
                tameOcelot(baby, pA.level().getPlayerByUUID(getOwnerUUID(pA)));
                baby.getPersistentData().putInt(NBT_VARIANT, pA.getPersistentData().getInt(NBT_VARIANT));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (event.getEntity().level().isClientSide) return;
        List<Ocelot> nearbyCats = event.getEntity().level().getEntitiesOfClass(Ocelot.class,
                new AABB(event.getEntity().blockPosition()).inflate(10.0D),
                o -> isTamed(o) && !isSitting(o));

        for (Ocelot cat : nearbyCats) {
            if (cat.getRandom().nextFloat() < 0.7F) {
                ItemStack gift = new ItemStack(cat.getRandom().nextBoolean() ? Items.STRING : Items.FEATHER);
                cat.spawnAtLocation(gift);
                cat.playSound(SoundEvents.CAT_PURR, 1.0F, 1.0F);
            }
        }
    }

    // ========================================================================================
    // 2. HELPER METHODS
    // ========================================================================================

    private static boolean isTamed(Ocelot ocelot) {
        return ocelot.getPersistentData().getBoolean(NBT_IS_TAMED);
    }

    private static boolean isSitting(Ocelot ocelot) {
        return ((IOcelotMixin) ocelot).novamterram$isSitting();
    }

    private static UUID getOwnerUUID(Ocelot ocelot) {
        if (ocelot.getPersistentData().contains(NBT_OWNER_UUID)) {
            return ocelot.getPersistentData().getUUID(NBT_OWNER_UUID);
        }
        return null;
    }

    private static void setSitting(Ocelot ocelot, boolean sitting) {
        ((IOcelotMixin) ocelot).novamterram$setSitting(sitting);
        if (sitting) {
            ocelot.getNavigation().stop();
            ocelot.setDeltaMovement(Vec3.ZERO);
            ocelot.playSound(SoundEvents.CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
        } else {
            ocelot.playSound(SoundEvents.CAT_AMBIENT, 1.0F, 1.0F);
        }
        syncOcelot(ocelot);
    }

    private static void tameOcelot(Ocelot ocelot, Player owner) {
        if (owner == null) return;
        ocelot.getPersistentData().putBoolean(NBT_IS_TAMED, true);
        ocelot.getPersistentData().putUUID(NBT_OWNER_UUID, owner.getUUID());
        ocelot.setPersistenceRequired();
        
        // SYNC: Use vanilla trusting state via Mixin
        ((IOcelotMixin) ocelot).novamterram$setTrusting(true);

        AttributeInstance healthAttr = ocelot.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) healthAttr.setBaseValue(24.0D);
        ocelot.setHealth(24.0F);
        rebuildAI(ocelot);
        syncOcelot(ocelot);
    }

    private static void ensureAttackAttribute(Ocelot ocelot) {
        AttributeInstance attackAttr = ocelot.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null && !attackAttr.hasModifier(ATTACK_BOOST.id())) {
            attackAttr.addPermanentModifier(ATTACK_BOOST);
        }
    }

    private static void syncOcelot(Ocelot ocelot) {
        if (!ocelot.level().isClientSide) {
            ocelot.level().broadcastEntityEvent(ocelot, (byte) 7);
        }
    }

    private static void scareCreepers(Ocelot ocelot, Player owner) {
        List<Creeper> creepers = ocelot.level().getEntitiesOfClass(Creeper.class, owner.getBoundingBox().inflate(10.0D));
        boolean hissed = false;
        for (Creeper c : creepers) {
            c.goalSelector.addGoal(0, new AvoidEntityGoal<>(c, Ocelot.class, 12.0F, 1.0D, 1.2D));
            if (!hissed && ocelot.getRandom().nextFloat() < 0.2F) {
                ocelot.playSound(SoundEvents.CAT_HISS, 0.8F, 1.2F);
                hissed = true;
            }
        }
    }

    private static void scarePhantoms(Ocelot ocelot) {
        List<Phantom> phantoms = ocelot.level().getEntitiesOfClass(Phantom.class, ocelot.getBoundingBox().inflate(12.0D));
        boolean hissed = false;
        for (Phantom p : phantoms) {
            Vec3 away = p.position().subtract(ocelot.position()).normalize().scale(0.5);
            p.setDeltaMovement(p.getDeltaMovement().add(away));
            if (!hissed && ocelot.getRandom().nextFloat() < 0.2F) {
                ocelot.playSound(SoundEvents.CAT_HISS, 0.8F, 1.2F);
                hissed = true;
            }
        }
    }

    private static void rebuildAI(Ocelot ocelot) {
        ocelot.goalSelector.getAvailableGoals().clear();
        ocelot.targetSelector.getAvailableGoals().clear();
        boolean isTamed = isTamed(ocelot);

        ocelot.goalSelector.addGoal(0, new FloatGoal(ocelot));
        ocelot.goalSelector.addGoal(1, new OcelotSitGoal(ocelot));

        if (isTamed) {
            ocelot.goalSelector.addGoal(2, new OcelotPounceGoal(ocelot));
            ocelot.goalSelector.addGoal(3, new OcelotFollowOwnerGoal(ocelot, 1.0D, 1.5D, 10.0F, 2.0F));
            ocelot.goalSelector.addGoal(4, new BreedGoal(ocelot, 1.0D));
            ocelot.goalSelector.addGoal(5, new OcelotAttackGoal(ocelot));
            ocelot.targetSelector.addGoal(1, new OcelotOwnerHurtByTargetGoal(ocelot));
            ocelot.targetSelector.addGoal(2, new OcelotOwnerHurtTargetGoal(ocelot));
            ocelot.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(ocelot, AbstractFish.class, true));
            ocelot.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(ocelot, Chicken.class, true));
        } else {
            ocelot.goalSelector.addGoal(2, new TemptGoal(ocelot, 0.6D, stack -> stack.is(ItemTags.FISHES), true));
            ocelot.goalSelector.addGoal(3, new OcelotPounceGoal(ocelot));
            ocelot.goalSelector.addGoal(4, new OcelotAttackGoal(ocelot));
            ocelot.goalSelector.addGoal(5, new AvoidEntityGoal<>(ocelot, Player.class, 16.0F, 0.8D, 1.33D));
            ocelot.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(ocelot, AbstractFish.class, true));
            ocelot.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(ocelot, Chicken.class, true));
        }
        ocelot.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(ocelot, Creeper.class, true));
        ocelot.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(ocelot, 1.0D));
        ocelot.goalSelector.addGoal(7, new LookAtPlayerGoal(ocelot, Player.class, 8.0F));
        ocelot.goalSelector.addGoal(8, new RandomLookAroundGoal(ocelot));
    }

    // ========================================================================================
    // 3. CUSTOM AI GOALS
    // ========================================================================================

    private static class OcelotSitGoal extends Goal {
        private final Ocelot ocelot;
        public OcelotSitGoal(Ocelot ocelot) {
            this.ocelot = ocelot;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        }
        @Override public boolean canUse() { return isSitting(ocelot); }
        @Override public void start() { ocelot.getNavigation().stop(); }
        @Override public void tick() {
            ocelot.getNavigation().stop();
            // Mixin handles pose
        }
    }

    private static class OcelotPounceGoal extends LeapAtTargetGoal {
        private final Ocelot ocelot;
        public OcelotPounceGoal(Ocelot ocelot) { super(ocelot, 0.4F); this.ocelot = ocelot; }
        @Override public boolean canUse() { return !isSitting(ocelot) && super.canUse(); }
    }

    private static class OcelotFollowOwnerGoal extends Goal {
        private final Ocelot ocelot;
        private LivingEntity owner;
        private final double walkSpeed;
        private final double sprintSpeed;
        private final float startDist;
        private int timeToRecalc;

        public OcelotFollowOwnerGoal(Ocelot ocelot, double walkSpeed, double sprintSpeed, float start, float stop) {
            this.ocelot = ocelot; this.walkSpeed = walkSpeed; this.sprintSpeed = sprintSpeed; this.startDist = start;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }
        @Override public boolean canUse() {
            if (isSitting(ocelot)) return false;
            UUID ownerUUID = getOwnerUUID(ocelot);
            if (ownerUUID == null) return false;
            this.owner = ocelot.level().getPlayerByUUID(ownerUUID);
            return this.owner != null && ocelot.distanceToSqr(this.owner) > (double)(startDist * startDist);
        }
        @Override public void tick() {
            ocelot.getLookControl().setLookAt(owner, 10.0F, (float)ocelot.getMaxHeadXRot());
            if (--timeToRecalc <= 0) {
                timeToRecalc = 10;
                double distSq = ocelot.distanceToSqr(owner);
                if (distSq >= 256.0D) teleportToOwner();
                else ocelot.getNavigation().moveTo(owner, distSq > 144.0D ? sprintSpeed : walkSpeed);
            }
        }
        private void teleportToOwner() {
            BlockPos p = owner.blockPosition();
            for (int i = 0; i < 10; i++) {
                BlockPos target = p.offset(ocelot.getRandom().nextInt(3) - 1, ocelot.getRandom().nextInt(2), ocelot.getRandom().nextInt(3) - 1);
                if (ocelot.level().getBlockState(target).isPathfindable(PathComputationType.LAND)) {
                    ocelot.moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, ocelot.getYRot(), ocelot.getXRot());
                    ocelot.getNavigation().stop();
                    return;
                }
            }
        }
    }

    private static class OcelotOwnerHurtByTargetGoal extends TargetGoal {
        private final Ocelot ocelot;
        private LivingEntity attacker;
        private int timestamp;
        public OcelotOwnerHurtByTargetGoal(Ocelot ocelot) { super(ocelot, false); this.ocelot = ocelot; }
        @Override public boolean canUse() {
            if (!isTamed(ocelot) || isSitting(ocelot)) return false;
            UUID ownerUUID = getOwnerUUID(ocelot);
            if (ownerUUID == null) return false;
            Player owner = ocelot.level().getPlayerByUUID(ownerUUID);
            if (owner == null) return false;
            this.attacker = owner.getLastHurtByMob();
            return owner.getLastHurtByMobTimestamp() != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT);
        }
        @Override public void start() {
            this.mob.setTarget(this.attacker);
            UUID ownerUUID = getOwnerUUID(ocelot);
            if (ownerUUID != null) {
                Player owner = ocelot.level().getPlayerByUUID(ownerUUID);
                if (owner != null) this.timestamp = owner.getLastHurtByMobTimestamp();
            }
            super.start();
        }
    }

    private static class OcelotOwnerHurtTargetGoal extends TargetGoal {
        private final Ocelot ocelot;
        private LivingEntity target;
        private int timestamp;
        public OcelotOwnerHurtTargetGoal(Ocelot ocelot) { super(ocelot, false); this.ocelot = ocelot; }
        @Override public boolean canUse() {
            if (!isTamed(ocelot) || isSitting(ocelot)) return false;
            UUID ownerUUID = getOwnerUUID(ocelot);
            if (ownerUUID == null) return false;
            Player owner = ocelot.level().getPlayerByUUID(ownerUUID);
            if (owner == null) return false;
            this.target = owner.getLastHurtMob();
            return owner.getLastHurtMobTimestamp() != this.timestamp && this.canAttack(this.target, TargetingConditions.DEFAULT);
        }
        @Override public void start() {
            this.mob.setTarget(this.target);
            UUID ownerUUID = getOwnerUUID(ocelot);
            if (ownerUUID != null) {
                Player owner = ocelot.level().getPlayerByUUID(ownerUUID);
                if (owner != null) this.timestamp = owner.getLastHurtMobTimestamp();
            }
            super.start();
        }
    }
}