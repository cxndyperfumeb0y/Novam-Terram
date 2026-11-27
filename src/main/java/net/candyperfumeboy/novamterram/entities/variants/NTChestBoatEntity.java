package net.candyperfumeboy.novamterram.entities.variants;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class NTChestBoatEntity extends ChestBoat {
    public NTChestBoatEntity(EntityType<? extends ChestBoat> type, Level level) {
        super(type, level);
    }

    // Each wood has its own entity type with the same ID as its item, e.g. "novamterram:teak_chest_boat"
    @Override
    public Item getDropItem() {
        return BuiltInRegistries.ITEM.get(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()));
    }

    public ResourceLocation getBoatTexture() {
        String wood = BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).getPath().replace("_chest_boat", "");
        return ResourceLocation.fromNamespaceAndPath(NovamTerram.MOD_ID, "textures/entity/boat/" + wood + ".png");
    }
}
