package net.candyperfumeboy.novamterram.entities.variants;

import net.candyperfumeboy.novamterram.entities.NTBoatEntities;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class NTBoatEntity extends Boat {
    public NTBoatEntity(EntityType<? extends Boat> type, Level level) {
        super(type, level);
    }

    public NTBoatEntity(Level level, double x, double y, double z) {
        this((EntityType<? extends Boat>) NTBoatEntities.BLUE_SPRUCE_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }
    @Override
    public Boat.Type getVariant() {
        // We return OAK to satisfy vanilla logic, but the renderer will use the texture below
        return Boat.Type.OAK;
    }

    // This is the most important part for 1.21.1
    public ResourceLocation getBoatTexture() {
        return ResourceLocation.fromNamespaceAndPath("novamterram", "textures/entity/boat/blue_spruce.png");
    }
    @Override
    public Item getDropItem() {
        return NTItems.BLUE_SPRUCE_BOAT.get();
    }
}