package net.candyperfumeboy.novamterram.mixin;

import net.candyperfumeboy.novamterram.util.IOcelotMixin;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Only holds synced data. The saved copy lives in persistentData and is pushed
 * into these fields by NTOcelotEvents when the ocelot loads.
 */
@Mixin(Ocelot.class)
public abstract class OcelotMixin extends Mob implements IOcelotMixin {

    @Shadow protected abstract void setTrusting(boolean trusting);

    @Unique
    private static final EntityDataAccessor<Boolean> NOVAMTERRAM$SITTING =
            SynchedEntityData.defineId(Ocelot.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Integer> NOVAMTERRAM$VARIANT =
            SynchedEntityData.defineId(Ocelot.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Integer> NOVAMTERRAM$COLLAR =
            SynchedEntityData.defineId(Ocelot.class, EntityDataSerializers.INT);

    @Unique
    private static final EntityDataAccessor<ItemStack> NOVAMTERRAM$HELD =
            SynchedEntityData.defineId(Ocelot.class, EntityDataSerializers.ITEM_STACK);

    protected OcelotMixin(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void novamterram$defineData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(NOVAMTERRAM$SITTING, false);
        builder.define(NOVAMTERRAM$VARIANT, 0);
        builder.define(NOVAMTERRAM$COLLAR, -1);
        builder.define(NOVAMTERRAM$HELD, ItemStack.EMPTY);
    }

    @Override
    public boolean novamterram$isSitting() {
        return this.entityData.get(NOVAMTERRAM$SITTING);
    }

    @Override
    public void novamterram$setSitting(boolean sitting) {
        this.entityData.set(NOVAMTERRAM$SITTING, sitting);
    }

    @Override
    public int novamterram$getVariant() {
        return this.entityData.get(NOVAMTERRAM$VARIANT);
    }

    @Override
    public void novamterram$setVariant(int variant) {
        this.entityData.set(NOVAMTERRAM$VARIANT, variant);
    }

    @Override
    public int novamterram$getCollar() {
        return this.entityData.get(NOVAMTERRAM$COLLAR);
    }

    @Override
    public void novamterram$setCollar(int colorId) {
        this.entityData.set(NOVAMTERRAM$COLLAR, colorId);
    }

    @Override
    public ItemStack novamterram$getHeldItem() {
        return this.entityData.get(NOVAMTERRAM$HELD);
    }

    @Override
    public void novamterram$setHeldItem(ItemStack stack) {
        this.entityData.set(NOVAMTERRAM$HELD, stack);
    }

    @Override
    public void novamterram$setTrusting(boolean trusting) {
        this.setTrusting(trusting);
    }
}
