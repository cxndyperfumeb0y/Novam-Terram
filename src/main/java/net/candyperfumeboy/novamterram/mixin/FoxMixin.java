package net.candyperfumeboy.novamterram.mixin;

import net.candyperfumeboy.novamterram.entities.custom.AbstractNTFox;
import net.candyperfumeboy.novamterram.util.INTFox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Mixin(Fox.class)
public abstract class FoxMixin implements INTFox {

    @Shadow @Final private static EntityDataAccessor<Optional<UUID>> DATA_TRUSTED_ID_0;
    @Shadow @Final private static EntityDataAccessor<Optional<UUID>> DATA_TRUSTED_ID_1;
    @Shadow public abstract boolean trusts(UUID uuid);
    @Shadow abstract void addTrustedUUID(UUID uuid);

    @Unique
    private static final EntityDataAccessor<Boolean> NT_SITTING =
            SynchedEntityData.defineId(Fox.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void nt$injectSittingData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(NT_SITTING, false);
    }

    /**
     * THE SITTING FIX: Fox's internal goals (FoxPounceGoal, StalkPreyGoal, etc.)
     * all call isOrderedToSit() — the package-private vanilla method — to decide
     * whether to run. For AbstractNTFox we redirect that call to read our
     * DATA_SITTING accessor so vanilla internal goals correctly see the sit state
     * and stop running when the fox is ordered to sit.
     * Without this, vanilla goals keep firing and override SitGoal regardless of
     * what nt$isOrderedToSit() returns.
     */
    /**
     * FIX: Redirecting isSitting instead of isOrderedToSit.
     * Vanilla goals like FoxPounceGoal check isSitting(). By injecting here,
     * we force the Fox to appear "sitting" to the AI whenever NT_SITTING is true.
     */
    @Inject(method = "isSitting", at = @At("HEAD"), cancellable = true)
    private void nt$isSittingRedirect(CallbackInfoReturnable<Boolean> cir) {
        if (this.nt$isOrderedToSit()) {
            cir.setReturnValue(true);
        }
    }


    @Inject(method = "setTargetGoals", at = @At("HEAD"), cancellable = true)
    private void nt$cancelVanillaTargetGoals(CallbackInfo ci) {
        if ((Object) this instanceof AbstractNTFox) ci.cancel();
    }

    @Override public boolean nt$isOrderedToSit() { return ((Fox)(Object)this).getEntityData().get(NT_SITTING); }
    @Override public void nt$setOrderedToSit(boolean s) { ((Fox)(Object)this).getEntityData().set(NT_SITTING, s); }
    @Override public boolean nt$trusts(UUID uuid) { return this.trusts(uuid); }
    @Override public void nt$addTrustedUUID(UUID uuid) { this.addTrustedUUID(uuid); }

    @Override
    public Collection<UUID> nt$getTrustedUUIDs() {
        Collection<UUID> uuids = new ArrayList<>();
        SynchedEntityData data = ((Fox)(Object)this).getEntityData();
        data.get(DATA_TRUSTED_ID_0).ifPresent(uuids::add);
        data.get(DATA_TRUSTED_ID_1).ifPresent(uuids::add);
        return uuids;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void nt$saveSitting(CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("NTSitting", this.nt$isOrderedToSit());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void nt$readSitting(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains("NTSitting")) this.nt$setOrderedToSit(compound.getBoolean("NTSitting"));
    }
}