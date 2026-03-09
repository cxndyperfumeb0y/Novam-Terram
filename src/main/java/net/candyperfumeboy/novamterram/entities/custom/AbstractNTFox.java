package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.util.INTFox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathComputationType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Base class for all NT custom fox variants.
 * Trust propagation on breeding lives entirely in NTFoxEvents.onFoxBreed().
 * getBreedOffspring() must only create the entity — never touch trust.
 *
 * AT required: public net.minecraft.world.entity.animal.Fox$DefendTrustedTargetGoal
 */
public abstract class AbstractNTFox extends Fox implements INTFox {

    // Declared first so it's available when defineSynchedData is called
    private static final EntityDataAccessor<Boolean> DATA_SITTING =
            SynchedEntityData.defineId(AbstractNTFox.class, EntityDataSerializers.BOOLEAN);

    private static final String NBT_EAT_COOLDOWN = "NTEatCooldown";
    private static final String NBT_TRUSTED_LIST  = "NTTrustedUUIDs";
    private static final int    EAT_COOLDOWN_TICKS = 1200;

    private static final Predicate<Entity> TRUSTED_TARGET_SELECTOR = entity ->
            entity instanceof LivingEntity living
                    && living.getLastHurtMob() != null
                    && living.getLastHurtMobTimestamp() < living.tickCount + 600;

    private boolean isWet;
    private boolean isShaking;

    public AbstractNTFox(EntityType<? extends Fox> type, Level level) {
        super(type, level);
        this.lookControl = new Fox.FoxLookControl();
        this.moveControl = new Fox.FoxMoveControl();
        this.setCanPickUpLoot(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SITTING, false);
    }

    public static AttributeSupplier.Builder createAbstractFoxAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.SAFE_FALL_DISTANCE, 5.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitGoal());
        this.goalSelector.addGoal(2, new FollowTrustedGoal(1.25D, 10.0F, 2.0F));
        this.targetSelector.addGoal(1, new Fox.DefendTrustedTargetGoal(
                LivingEntity.class, false, false,
                entity -> TRUSTED_TARGET_SELECTOR.test(entity) && !this.nt$trusts(entity.getUUID())));
        this.goalSelector.addGoal(3, new Fox.FaceplantGoal());
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, PolarBear.class, 8.0F, 1.6D, 1.4D));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(8, new Fox.StalkPreyGoal());
        this.goalSelector.addGoal(9, new Fox.FoxPounceGoal());
        this.goalSelector.addGoal(10, new FollowParentGoal(this, 1.1D) {
            @Override public boolean canUse() {
                if (AbstractNTFox.this.nt$isOrderedToSit()) return false;
                for (UUID uuid : AbstractNTFox.this.nt$getTrustedUUIDs())
                    if (uuid != null && AbstractNTFox.this.level().getPlayerByUUID(uuid) != null) return false;
                return super.canUse();
            }
            // Must also override canContinueToUse — vanilla's version doesn't check sit state,
            // so if this goal was already running when sit is ordered it keeps going and
            // fights SitGoal through shared Flag.MOVE, preventing the baby from sitting.
            @Override public boolean canContinueToUse() {
                return !AbstractNTFox.this.nt$isOrderedToSit() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(11, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(12, new Fox.FoxEatBerriesGoal(1.2D, 12, 1));
        this.goalSelector.addGoal(13, new Fox.FoxSearchForItemsGoal());
        this.goalSelector.addGoal(14, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(15, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Chicken.class, 10, false, false, null));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Rabbit.class, 10, false, false, null));
        this.addVariantGoals();
    }

    protected abstract void addVariantGoals();
    protected abstract boolean isNocturnal();

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.NAME_TAG) || stack.is(Items.LEAD)) return super.mobInteract(player, hand);

        UUID uuid = player.getUUID();
        if (stack.is(Items.STICK)) {
            if (!this.level().isClientSide) {
                if (this.nt$trusts(uuid)) {
                    player.displayClientMessage(Component.translatable("message.novamterram.fox.trust_success"), true);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                    this.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);
                } else {
                    player.displayClientMessage(Component.translatable("message.novamterram.fox.trust_fail"), true);
                    this.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.2F);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (this.nt$trusts(uuid)) {
            if (stack.has(DataComponents.FOOD) && this.getHealth() < this.getMaxHealth()) {
                this.heal((float) stack.get(DataComponents.FOOD).nutrition());
                this.level().broadcastEntityEvent(this, (byte) 7);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                this.playSound(SoundEvents.FOX_EAT, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
            if (!this.isFood(stack)) {
                if (!this.level().isClientSide) {
                    boolean nowSitting = !this.nt$isOrderedToSit();
                    this.nt$setOrderedToSit(nowSitting);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.setSleeping(false);
                    this.playSound(nowSitting ? SoundEvents.FOX_SNIFF : SoundEvents.FOX_SPIT, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !this.isAlive() || !this.isEffectiveAi()) return;

        if (this.isInWaterRainOrBubble()) {
            this.isWet = true; this.isShaking = false;
        } else if (this.isWet && this.random.nextFloat() < 0.05F) {
            this.isWet = false; this.isShaking = true;
            this.level().broadcastEntityEvent(this, (byte) 8);
            this.playSound(SoundEvents.WOLF_SHAKE, 1.0F, 1.0F);
        }

        if (!this.nt$isOrderedToSit() && this.getTarget() == null && !this.isSleeping())
            if (this.isNocturnal() == this.level().isDay() && this.random.nextFloat() < 0.001F)
                this.setSleeping(true);

        ItemStack mainHand = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (mainHand.has(DataComponents.FOOD)) {
            int cd = this.getPersistentData().getInt(NBT_EAT_COOLDOWN);
            if (cd > 0) this.getPersistentData().putInt(NBT_EAT_COOLDOWN, cd - 1);
            else if (this.random.nextFloat() < 0.005F) {
                this.eat(this.level(), mainHand);
                this.playSound(SoundEvents.FOX_EAT, 1.0F, 1.0F);
                this.getPersistentData().putInt(NBT_EAT_COOLDOWN, EAT_COOLDOWN_TICKS);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 8) this.isShaking = true;
        else super.handleEntityEvent(id);
    }

    // INTFox — overrides the mixin's Fox.class implementations with our DATA_SITTING

    @Override
    public boolean nt$isOrderedToSit() { return this.entityData.get(DATA_SITTING); }

    @Override
    public void nt$setOrderedToSit(boolean sitting) {
        this.entityData.set(DATA_SITTING, sitting);
        this.setSitting(sitting); // sync vanilla visual immediately
    }

    @Override
    public boolean nt$trusts(UUID uuid) { return uuid != null && this.nt$getTrustedUUIDs().contains(uuid); }

    @Override
    public Collection<UUID> nt$getTrustedUUIDs() {
        List<UUID> uuids = new ArrayList<>();
        CompoundTag pd = this.getPersistentData();
        if (pd.contains(NBT_TRUSTED_LIST)) {
            ListTag list = pd.getList(NBT_TRUSTED_LIST, Tag.TAG_INT_ARRAY);
            for (Tag tag : list) uuids.add(NbtUtils.loadUUID(tag));
        }
        return uuids;
    }

    @Override
    public void nt$addTrustedUUID(UUID uuid) {
        if (uuid == null) return;
        this.addTrustedUUID(uuid); // vanilla slots
        CompoundTag pd = this.getPersistentData();
        ListTag list = pd.contains(NBT_TRUSTED_LIST)
                ? pd.getList(NBT_TRUSTED_LIST, Tag.TAG_INT_ARRAY) : new ListTag();
        if (list.stream().noneMatch(t -> NbtUtils.loadUUID(t).equals(uuid))) {
            list.add(NbtUtils.createUUID(uuid));
            pd.put(NBT_TRUSTED_LIST, list);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("NTSitting", this.nt$isOrderedToSit());
        compound.putInt(NBT_EAT_COOLDOWN, this.getPersistentData().getInt(NBT_EAT_COOLDOWN));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.nt$setOrderedToSit(compound.getBoolean("NTSitting"));
        if (compound.contains(NBT_EAT_COOLDOWN))
            this.getPersistentData().putInt(NBT_EAT_COOLDOWN, compound.getInt(NBT_EAT_COOLDOWN));
    }

    @Override
    public void onOffspringSpawnedFromEgg(Player player, Mob child) {
        if (child instanceof AbstractNTFox baby) baby.nt$addTrustedUUID(player.getUUID());
    }

    // Goals

    class SitGoal extends Goal {
        SitGoal() { this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE)); }
        @Override public boolean canUse() { return AbstractNTFox.this.nt$isOrderedToSit(); }
        @Override public void start() { AbstractNTFox.this.getNavigation().stop(); }
        @Override public void tick() {
            AbstractNTFox.this.getNavigation().stop();
            Player nearest = AbstractNTFox.this.level().getNearestPlayer(AbstractNTFox.this, 10.0D);
            if (nearest != null && AbstractNTFox.this.nt$trusts(nearest.getUUID()))
                AbstractNTFox.this.getLookControl().setLookAt(nearest, 10.0F, (float)AbstractNTFox.this.getMaxHeadXRot());
        }
        @Override public void stop() { /* setSitting handled by nt$setOrderedToSit */ }
    }

    class FollowTrustedGoal extends Goal {
        private final double speed; private final float startDist, stopDist;
        private LivingEntity owner; private int recalcTimer;

        FollowTrustedGoal(double speed, float start, float stop) {
            this.speed = speed; this.startDist = start; this.stopDist = stop;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override public boolean canUse() {
            if (AbstractNTFox.this.nt$isOrderedToSit()) return false;
            for (UUID uuid : AbstractNTFox.this.nt$getTrustedUUIDs()) {
                if (uuid == null) continue;
                Player p = AbstractNTFox.this.level().getPlayerByUUID(uuid);
                if (p != null && AbstractNTFox.this.distanceToSqr(p) > startDist * startDist) {
                    this.owner = p; return true;
                }
            }
            return false;
        }

        @Override public boolean canContinueToUse() {
            return owner != null && !AbstractNTFox.this.nt$isOrderedToSit()
                    && AbstractNTFox.this.distanceToSqr(owner) > stopDist * stopDist;
        }

        @Override public void stop() { this.owner = null; }

        @Override public void tick() {
            AbstractNTFox.this.getLookControl().setLookAt(owner, 10.0F, (float)AbstractNTFox.this.getMaxHeadXRot());
            if (--recalcTimer > 0) return;
            recalcTimer = 10;
            double dsq = AbstractNTFox.this.distanceToSqr(owner);
            if (dsq >= 144.0D) teleport();
            else AbstractNTFox.this.getNavigation().moveTo(owner, speed);
        }

        private void teleport() {
            BlockPos base = owner.blockPosition();
            for (int i = 0; i < 10; i++) {
                BlockPos t = base.offset(AbstractNTFox.this.getRandom().nextInt(3)-1, AbstractNTFox.this.getRandom().nextInt(2), AbstractNTFox.this.getRandom().nextInt(3)-1);
                if (AbstractNTFox.this.level().getBlockState(t).isPathfindable(PathComputationType.LAND)) {
                    AbstractNTFox.this.moveTo(t.getX()+0.5, t.getY(), t.getZ()+0.5, AbstractNTFox.this.getYRot(), AbstractNTFox.this.getXRot());
                    AbstractNTFox.this.getNavigation().stop();
                    return;
                }
            }
        }
    }
}