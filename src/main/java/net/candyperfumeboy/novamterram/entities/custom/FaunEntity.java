package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * FaunEntity - The definitive Forest Sentinel for Novam Terram.
 * * FEATURES:
 * - Gender Dimorphism (Bucks vs Does)
 * - Antler Shedding Cycle (Drops Bone Meal every 7 days, regrows in 3)
 * - Predator Avoidance (Bears, Foxes, Wild Wolves)
 * - Weather Huddling (Seeks cover/shade during rain)
 * - Trust & Gift System (Apples build trust; Fauns drop forest items)
 * - "Hiding" Camouflage (Freezes movement when watched by strangers)
 * - Optimization: Tick Staggering (Prevents lag spikes with large herds)
 */
public class FaunEntity extends Animal {
    // --- DATA ACCESSORS ---
    private static final EntityDataAccessor<Boolean> IS_BUCK = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_FLORAL = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_ANTLERS = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_HIDING = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.BOOLEAN);

    // --- PERSISTENT LOGIC ---
    private final Set<UUID> trustedUuids = new HashSet<>();
    private int floralTimer = 0;
    private int alertCooldown = 0;
    private int giftCooldown = 0;
    private int shedCooldown = 168000; // 7 Minecraft Days
    private int regrowTimer = 0;
    private int hideTicks = 0;

    public FaunEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    // --- AI GOALS ---
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));

        // 1. PREDATOR AVOIDANCE (Excludes Tamed Wolves)
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Wolf.class, 12.0F, 1.5D, 2.2D, (e) -> e instanceof Wolf wolf && !wolf.isTame()));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, BrownBearEntity.class, 16.0F, 1.6D, 2.4D));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, SpectacledBearEntity.class, 14.0F, 1.5D, 2.2D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, SilverFoxEntity.class, 8.0F, 1.2D, 1.6D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, DesertFoxEntity.class, 8.0F, 1.2D, 1.6D));

        // 2. REPRODUCTION & TEMPTATION
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.25D, Ingredient.of(Items.APPLE, Items.WHEAT), false));

        // 3. ENVIRONMENTAL ATTRACTION (Moss & Salt Licks)
        this.goalSelector.addGoal(6, new MoveToBlockGoal(this, 1.0D, 12) {
            @Override protected boolean isValidTarget(LevelReader level, BlockPos pos) {
                BlockState state = level.getBlockState(pos);
                return state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.RAW_IRON_BLOCK);
            }
        });

        // 4. FEEDING BEHAVIOR
        this.goalSelector.addGoal(7, new EatBlockGoal(this)); // Grass eating
        this.goalSelector.addGoal(8, new MoveToBlockGoal(this, 1.2D, 16) {
            @Override protected boolean isValidTarget(LevelReader level, BlockPos pos) {
                return level.getBlockState(pos).is(BlockTags.FLOWERS);
            }
        });

        // 5. WEATHER BEHAVIOR (Huddle under cover when raining)
        this.goalSelector.addGoal(9, new FleeSunGoal(this, 1.0D) {
            @Override public boolean canUse() { return level().isRaining() && super.canUse(); }
        });

        // 6. GENERAL MOBILITY
        this.goalSelector.addGoal(10, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(13, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    // --- TICK LOGIC ---
    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.isAlive()) {

            // A. CAMOUFLAGE / HIDING MECHANIC (Stops movement if watched)
            Player watcher = this.level().getNearestPlayer(this, 15.0D);
            if (watcher != null && !this.trusts(watcher.getUUID()) && watcher.getLookAngle().dot(this.position().subtract(watcher.position()).normalize()) > 0.95D) {
                this.hideTicks++;
                if (this.hideTicks > 20) {
                    this.setHiding(true);
                    this.getNavigation().stop();
                }
            } else {
                this.hideTicks = 0;
                this.setHiding(false);
            }

            // B. SKITTISHNESS (Triggers Panic if sprinted at)
            if (watcher != null && watcher.isSprinting() && !this.trusts(watcher.getUUID()) && !watcher.isCreative()) {
                this.setLastHurtByMob(watcher);
            }

            // C. STAGGERED FOREST ALARM (Optimized)
            if (this.alertCooldown > 0) this.alertCooldown--;
            if ((this.tickCount + this.getId()) % 40 == 0 && this.alertCooldown == 0) {
                if (!this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(15.0D),
                        e -> e.getType().getCategory() == MobCategory.MONSTER).isEmpty()) {
                    this.playSound(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(0).value(), 1.2F, 1.5F);
                    this.alertCooldown = 800;
                }
            }

            // D. ANTLER SHEDDING CYCLE
            handleAntlerCycle();

            // E. GIFT GIVING (Trusted players only)
            if (this.giftCooldown > 0) this.giftCooldown--;
            if (this.giftCooldown == 0 && watcher != null && this.trusts(watcher.getUUID()) && this.distanceToSqr(watcher) < 16.0D) {
                if (this.random.nextInt(3500) == 0) spawnGift();
            }

            // F. STAGGERED FLOWER CONSUMPTION (Optimized)
            if ((this.tickCount + this.getId()) % 100 == 0 && !this.isFloral()) {
                if (this.level().getBlockState(this.blockPosition()).is(BlockTags.FLOWERS)) {
                    this.level().destroyBlock(this.blockPosition(), false);
                    this.setFloral(true);
                    this.floralTimer = 1200;
                    this.playSound(SoundEvents.PANDA_EAT, 1.0F, 1.0F);
                }
            }
            if (this.floralTimer > 0 && --this.floralTimer == 0) this.setFloral(false);
        }

        // CLIENT-SIDE PARTICLES (Floral Buff)
        if (this.level().isClientSide && this.isFloral() && this.random.nextInt(8) == 0) {
            this.level().addParticle(ParticleTypes.CHERRY_LEAVES, this.getRandomX(0.5D), this.getRandomY() + 0.5D, this.getRandomZ(0.5D), 0, 0.02, 0);
        }
    }

    private void handleAntlerCycle() {
        if (this.isBuck() && !this.isBaby()) {
            if (this.hasAntlers()) {
                if (--this.shedCooldown <= 0) {
                    this.setHasAntlers(false);
                    this.spawnAtLocation(Items.BONE_MEAL, 1);
                    this.playSound(SoundEvents.ITEM_BREAK, 1.0F, 0.9F);
                    this.regrowTimer = 72000; // 3 Days
                }
            } else if (--this.regrowTimer <= 0) {
                this.setHasAntlers(true);
                this.shedCooldown = 168000;
                this.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0F, 1.1F);
            }
        }
    }

    // --- DATA HANDLING ---
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_BUCK, false);
        builder.define(IS_FLORAL, false);
        builder.define(HAS_ANTLERS, true);
        builder.define(IS_HIDING, false);
    }

    public boolean isBuck() { return this.entityData.get(IS_BUCK); }
    public void setBuck(boolean b) { this.entityData.set(IS_BUCK, b); }
    public boolean isFloral() { return this.entityData.get(IS_FLORAL); }
    public void setFloral(boolean b) { this.entityData.set(IS_FLORAL, b); }
    public boolean hasAntlers() { return this.entityData.get(HAS_ANTLERS); }
    public void setHasAntlers(boolean b) { this.entityData.set(HAS_ANTLERS, b); }
    public boolean isHiding() { return this.entityData.get(IS_HIDING); }
    public void setHiding(boolean b) { this.entityData.set(IS_HIDING, b); }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance diff, MobSpawnType type, @Nullable SpawnGroupData data) {
        this.setBuck(this.random.nextBoolean());
        this.setHasAntlers(this.isBuck());
        return super.finalizeSpawn(level, diff, type, data);
    }

    // --- TRUST & INTERACTION ---
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.APPLE) && !this.trusts(player.getUUID())) {
            if (!player.getAbilities().instabuild) stack.shrink(1);
            this.addTrustedUuid(player.getUUID());
            this.playSound(SoundEvents.HORSE_EAT, 1.0F, 1.0F);
            if (this.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 1.0, this.getZ(), 5, 0.3, 0.3, 0.3, 0.05);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    private void spawnGift() {
        ItemStack gift = switch (this.random.nextInt(4)) {
            case 0 -> new ItemStack(Items.SWEET_BERRIES, 4);
            case 1 -> new ItemStack(Blocks.MOSS_BLOCK);
            case 2 -> new ItemStack(Blocks.FLOWERING_AZALEA);
            default -> new ItemStack(Items.APPLE);
        };
        this.spawnAtLocation(gift);
        this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.2F);
        this.giftCooldown = 12000;
    }

    public void addTrustedUuid(UUID uuid) { this.trustedUuids.add(uuid); }
    public boolean trusts(UUID uuid) { return this.trustedUuids.contains(uuid); }

    // --- NBT PERSISTENCE ---
    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("IsBuck", this.isBuck());
        nbt.putBoolean("HasAntlers", this.hasAntlers());
        nbt.putInt("ShedCooldown", this.shedCooldown);
        nbt.putInt("RegrowTimer", this.regrowTimer);
        nbt.putInt("GiftCooldown", this.giftCooldown);
        ListTag list = new ListTag();
        for(UUID uuid : this.trustedUuids) list.add(NbtUtils.createUUID(uuid));
        nbt.put("Trusted", list);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setBuck(nbt.getBoolean("IsBuck"));
        this.setHasAntlers(nbt.getBoolean("HasAntlers"));
        this.shedCooldown = nbt.getInt("ShedCooldown");
        this.regrowTimer = nbt.getInt("RegrowTimer");
        this.giftCooldown = nbt.getInt("GiftCooldown");
        ListTag list = nbt.getList("Trusted", 11);
        for(int i = 0; i < list.size(); i++) this.trustedUuids.add(NbtUtils.loadUUID(list.get(i)));
    }

    // --- SOUNDS & MISC ---
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.GOAT_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource ds) { return SoundEvents.GOAT_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.GOAT_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { this.playSound(SoundEvents.HORSE_STEP, 0.15F, 1.3F); }

    @Override public boolean isFood(ItemStack stack) { return stack.is(Items.APPLE) || stack.is(Items.WHEAT); }

    @Nullable @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        return NTEntities.FAUN.get().create(level);
    }
}