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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FaunEntity extends Animal {
    private static final EntityDataAccessor<Boolean> IS_FLORAL = SynchedEntityData.defineId(FaunEntity.class, EntityDataSerializers.BOOLEAN);

    private final Set<UUID> trustedUuids = new HashSet<>();
    private int floralTimer = 0;
    private int alertCooldown = 0;
    private int giftCooldown = 0;

    public FaunEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    // --- DATA & SYNC ---

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_FLORAL, false);
    }

    public boolean trusts(UUID uuid) {
        return this.trustedUuids.contains(uuid);
    }

    public void addTrustedUuid(UUID uuid) {
        this.trustedUuids.add(uuid);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ListTag listtag = new ListTag();
        for(UUID uuid : this.trustedUuids) {
            listtag.add(NbtUtils.createUUID(uuid));
        }
        compound.put("Trusted", listtag);
        compound.putInt("GiftCooldown", this.giftCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        ListTag listtag = compound.getList("Trusted", 11);
        for(int i = 0; i < listtag.size(); ++i) {
            this.trustedUuids.add(NbtUtils.loadUUID(listtag.get(i)));
        }
        this.giftCooldown = compound.getInt("GiftCooldown");
    }

    // --- INTERACTION ---

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(Items.APPLE) && !this.trusts(player.getUUID())) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.addTrustedUuid(player.getUUID());
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 0.5, this.getZ(), 5, 0.2, 0.2, 0.2, 0.1);
            }
            this.playSound(SoundEvents.FOX_EAT, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    // --- GOALS ---

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2D, Ingredient.of(Items.APPLE, Items.WHEAT), false));

        // Custom Goals
        this.goalSelector.addGoal(4, new EatBlockGoal(this));
        this.goalSelector.addGoal(4, new MoveToBlockGoal(this, 1.0D, 8) {
            @Override protected boolean isValidTarget(LevelReader level, BlockPos pos) {
                return level.getBlockState(pos).is(BlockTags.FLOWERS);
            }
        });

        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, FaunEntity.class, 2.5F, 1.0D, 1.2D));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    // --- AI LOGIC ---

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.isAlive()) {

            // 1. TRUSTED SKITTISHNESS
            Player nearbyPlayer = this.level().getNearestPlayer(this, 6.0D);
            if (nearbyPlayer != null && nearbyPlayer.isSprinting() && !nearbyPlayer.isCreative()) {
                if (!this.trusts(nearbyPlayer.getUUID())) {
                    this.setLastHurtByMob(nearbyPlayer);
                }
            }

            // 2. FOREST SENTINEL ALARM
            if (this.alertCooldown > 0) this.alertCooldown--;
            if (this.tickCount % 20 == 0 && this.alertCooldown == 0) {
                List<Mob> monsters = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(12.0D),
                        e -> e.getType().getCategory() == MobCategory.MONSTER);
                if (!monsters.isEmpty()) {
                    this.playSound(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(0).value(), 1.0F, 2.0F);
                    this.alertCooldown = 300;
                    this.setLastHurtByMob(monsters.get(0));
                }
            }

            // 3. FLOWER SNACKING & PARTICLES
            if (this.tickCount % 60 == 0) {
                BlockState state = this.level().getBlockState(this.blockPosition());
                if (state.is(BlockTags.FLOWERS)) {
                    this.level().destroyBlock(this.blockPosition(), false);
                    this.entityData.set(IS_FLORAL, true);
                    this.floralTimer = 1200; // 1 minute
                    this.playSound(SoundEvents.PANDA_EAT, 1.0F, 1.2F);
                }
            }
            if (this.floralTimer > 0) {
                this.floralTimer--;
                if (this.floralTimer == 0) this.entityData.set(IS_FLORAL, false);
            }

            // 4. BIRD WATCHING
            if (this.tickCount % 40 == 0) {
                List<SongbirdEntity> birds = this.level().getEntitiesOfClass(SongbirdEntity.class, this.getBoundingBox().inflate(8.0D));
                if (!birds.isEmpty()) this.getLookControl().setLookAt(birds.get(0), 10.0F, 30.0F);
            }

            // 5. GIFT GIVING LOGIC
            if (this.giftCooldown > 0) this.giftCooldown--;
            if (this.giftCooldown == 0 && nearbyPlayer != null && this.trusts(nearbyPlayer.getUUID()) && this.distanceToSqr(nearbyPlayer) < 9.0D) {
                if (this.random.nextInt(3000) == 0) { // Rare event
                    this.spawnGift();
                }
            }
        }

        // Particle Rendering
        if (this.level().isClientSide && this.entityData.get(IS_FLORAL) && this.random.nextInt(4) == 0) {
            this.level().addParticle(ParticleTypes.CHERRY_LEAVES, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, 0.05, 0);
        }
    }

    private void spawnGift() {
        ItemStack gift = this.getRandomGift();
        ItemEntity itementity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), gift);
        this.level().addFreshEntity(itementity);
        this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.0F);
        this.giftCooldown = 12000; // 10 minutes between gifts
    }

    private ItemStack getRandomGift() {
        int i = this.random.nextInt(4);
        return switch (i) {
            case 0 -> new ItemStack(Items.SWEET_BERRIES, 3);
            case 1 -> new ItemStack(Items.BROWN_MUSHROOM);
            case 2 -> new ItemStack(Items.MOSS_CARPET);
            default -> new ItemStack(Items.PUMPKIN_SEEDS);
        };
    }

    @Override
    public void ate() {
        if (this.isBaby()) this.ageUp(60);
        this.level().broadcastEntityEvent(this, (byte)10);
        this.playSound(SoundEvents.SHEEP_STEP, 1.0F, 1.0F);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.APPLE) || stack.is(Items.WHEAT);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return NTEntities.FAUN.get().create(level);
    }
}