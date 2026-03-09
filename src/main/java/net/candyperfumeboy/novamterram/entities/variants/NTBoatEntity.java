package net.candyperfumeboy.novamterram.entities.variants;

import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class NTBoatEntity extends Boat {
    private static final EntityDataAccessor<String> DATA_WOOD_TYPE = SynchedEntityData.defineId(NTBoatEntity.class, EntityDataSerializers.STRING);

    public NTBoatEntity(EntityType<? extends Boat> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_WOOD_TYPE, "blue_spruce");
    }

    public void setWoodType(String woodType) {
        this.entityData.set(DATA_WOOD_TYPE, woodType);
    }

    public String getWoodType() {
        return this.entityData.get(DATA_WOOD_TYPE);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("WoodType", this.getWoodType());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("WoodType")) {
            this.setWoodType(tag.getString("WoodType"));
        }
    }

    @Override
    public Item getDropItem() {
        return NTItems.ITEMS.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(this.getWoodType() + "_boat"))
                .findFirst()
                .map(holder -> (Item) holder.get())
                .orElse(Items.OAK_BOAT);
    }

    public ResourceLocation getBoatTexture() {
        String type = getWoodType();
        if (type.contains(":")) {
            type = type.split(":")[1];
        }
        return ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/boat/" + type + ".png");
    }
}