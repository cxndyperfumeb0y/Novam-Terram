package net.candyperfumeboy.novamterram.util;

import net.minecraft.world.item.ItemStack;

/** Extra ocelot state added by OcelotMixin. All values are synced to the client. */
public interface IOcelotMixin {
    /** Whether to show the sitting pose (told to sit, or perched on furniture). */
    boolean novamterram$isSitting();
    void novamterram$setSitting(boolean sitting);

    /** 0 = vanilla, 1 = white, 2 = grey, 3 = brown. */
    int novamterram$getVariant();
    void novamterram$setVariant(int variant);

    /** Dye colour id of the collar, or -1 when the ocelot is not tamed. */
    int novamterram$getCollar();
    void novamterram$setCollar(int colorId);

    /** The item shown in the mouth. */
    ItemStack novamterram$getHeldItem();
    void novamterram$setHeldItem(ItemStack stack);

    void novamterram$setTrusting(boolean trusting);
}
