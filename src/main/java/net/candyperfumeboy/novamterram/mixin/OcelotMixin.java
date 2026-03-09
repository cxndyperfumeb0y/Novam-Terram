package net.candyperfumeboy.novamterram.mixin;

import net.candyperfumeboy.novamterram.util.IOcelotMixin;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Ocelot.class)
public abstract class OcelotMixin extends Mob implements IOcelotMixin {

    @Shadow protected abstract void setTrusting(boolean pTrusting);

    // FIX: Must be PRIVATE. Mixin prohibits non-private static fields.
    @Unique
    private static final EntityDataAccessor<Boolean> DATA_SITTING =
            SynchedEntityData.defineId(Ocelot.class, EntityDataSerializers.BOOLEAN);

    protected OcelotMixin(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void onDefineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DATA_SITTING, false);
    }

    @Override
    public boolean novamterram$isSitting() {
        return this.entityData.get(DATA_SITTING);
    }

    @Override
    public void novamterram$setSitting(boolean sitting) {
        this.entityData.set(DATA_SITTING, sitting);
        // Using the accessor inside the class is totally fine!
        if (sitting) {
            this.setPose(Pose.SITTING);
            this.getNavigation().stop();
        } else {
            this.setPose(Pose.STANDING);
        }
    }

    @Override
    public void novamterram$setTrusting(boolean trusting) {
        this.setTrusting(trusting);
    }

    @Override
    public boolean isCrouching() {
        return novamterram$isSitting() || super.isCrouching();
    }
}