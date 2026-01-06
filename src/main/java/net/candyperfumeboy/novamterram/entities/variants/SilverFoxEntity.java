package net.candyperfumeboy.novamterram.entities.variants;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.level.Level;

public class SilverFoxEntity extends Fox {
    public SilverFoxEntity(EntityType<? extends Fox> entityType, Level level) {
        super(entityType, level);
    }

    // You can add unique behaviors here later, like making them
    // immune to freezing in your cold taiga!
}