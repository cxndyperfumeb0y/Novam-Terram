package net.candyperfumeboy.novamterram.entities.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;

public abstract class AbstractNTBear extends Animal {
    // Synced Data
    private static final EntityDataAccessor<Boolean> IS_STANDING = SynchedEntityData.defineId(AbstractNTBear.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SLEEPING = SynchedEntityData.defineId(AbstractNTBear.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TRUST = SynchedEntityData.defineId(AbstractNTBear.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DANCING = SynchedEntityData.defineId(AbstractNTBear.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_DIVING = SynchedEntityData.defineId(AbstractNTBear.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CLIMBING = SynchedEntityData.defineId(AbstractNTBear.class, EntityDataSerializers.BOOLEAN);

    // Animation Scales
    private float clientSideStandAnimationO;
    private float clientSideStandAnimation;

    // Logic Timers
    protected int honeyCooldown = 0;
    protected int honeyDrunkTimer = 0;

    protected AbstractNTBear(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_STANDING, false);
        builder.define(DATA_SLEEPING, false);
        builder.define(DATA_TRUST, 0);
        builder.define(DATA_DANCING, false);
        builder.define(DATA_DIVING, false);
        builder.define(DATA_CLIMBING, false);
    }

    /* --- GETTERS & SETTERS --- */
    public boolean isStanding() { return this.entityData.get(IS_STANDING); }
    public void setStanding(boolean standing) { this.entityData.set(IS_STANDING, standing); }

    public boolean isSleepyTime() { return this.entityData.get(DATA_SLEEPING); }
    public void setSleepyTime(boolean s) { this.entityData.set(DATA_SLEEPING, s); }

    public int getTrustLevel() { return this.entityData.get(DATA_TRUST); }
    public void setTrustLevel(int level) { this.entityData.set(DATA_TRUST, Mth.clamp(level, 0, 100)); }

    public boolean isDancing() { return this.entityData.get(DATA_DANCING); }
    public void setDancing(boolean dancing) { this.entityData.set(DATA_DANCING, dancing); }

    public boolean isDiving() { return this.entityData.get(DATA_DIVING); }
    public void setDiving(boolean diving) { this.entityData.set(DATA_DIVING, diving); }

    public boolean isClimbing() { return this.entityData.get(DATA_CLIMBING); }
    public void setClimbing(boolean climbing) { this.entityData.set(DATA_CLIMBING, climbing); }

    /* --- CORE LOGIC --- */
    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            float oldAnimation = this.clientSideStandAnimation;
            this.clientSideStandAnimationO = oldAnimation;
            this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation + (this.isStanding() ? 1.0F : -1.0F), 0.0F, 6.0F);
            if (oldAnimation != this.clientSideStandAnimation) this.refreshDimensions();
        }

        if (!this.level().isClientSide) {
            if (this.tickCount % 20 == 0) {
                if (this.honeyCooldown > 0) this.honeyCooldown--;
                if (this.honeyDrunkTimer > 0) this.honeyDrunkTimer--;
                BlockPos pos = this.blockPosition();
                boolean isNearJukebox = this.level().getBlockState(pos.above()).is(Blocks.JUKEBOX) ||
                        this.level().getBlockState(pos.below()).is(Blocks.JUKEBOX);
                this.setDancing(isNearJukebox);
            }
        }
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        if (pose == Pose.STANDING) {
            float f = this.clientSideStandAnimation / 6.0F;
            return super.getDefaultDimensions(pose).scale(1.0F, 1.0F + f);
        }
        return super.getDefaultDimensions(pose);
    }

    public float getStandingAnimationScale(float partialTicks) {
        return Mth.lerp(partialTicks, this.clientSideStandAnimationO, this.clientSideStandAnimation) / 6.0F;
    }

    @Override
    protected void positionRider(Entity rider, Entity.MoveFunction moveFunction) {
        super.positionRider(rider, moveFunction);
        if (this.hasPassenger(rider)) {
            float vOffset = this.isStanding() ? 1.1F : 0.35F;
            float bOffset = this.isStanding() ? 0.2F : 0.5F;
            float radian = this.yBodyRot * ((float)Math.PI / 180F);
            moveFunction.accept(rider, this.getX() + (bOffset * Mth.sin(radian)), this.getY() + vOffset, this.getZ() + (-bOffset * Mth.cos(radian)));
        }
    }

    @Override
    protected float getWaterSlowDown() { return 0.98F; }

    @Override
    public void addAdditionalSaveData(CompoundTag c) {
        super.addAdditionalSaveData(c);
        c.putBoolean("IsSleepy", this.isSleepyTime());
        c.putInt("TrustLevel", this.getTrustLevel());
        c.putInt("HoneyCooldown", this.honeyCooldown);
        c.putInt("HoneyDrunkTimer", this.honeyDrunkTimer);
        c.putBoolean("IsDiving", this.isDiving());
        c.putBoolean("IsClimbing", this.isClimbing());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag c) {
        super.readAdditionalSaveData(c);
        this.setSleepyTime(c.getBoolean("IsSleepy"));
        this.setTrustLevel(c.getInt("TrustLevel"));
        this.honeyCooldown = c.getInt("HoneyCooldown");
        this.honeyDrunkTimer = c.getInt("HoneyDrunkTimer");
        this.setDiving(c.getBoolean("IsDiving"));
        this.setClimbing(c.getBoolean("IsClimbing"));
    }
}