package net.candyperfumeboy.novamterram.item.custom;

import net.candyperfumeboy.novamterram.entities.variants.NTBoatEntity;
import net.candyperfumeboy.novamterram.entities.variants.NTChestBoatEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NTBoatItem extends BoatItem {
    private final boolean hasChest;
    private final Supplier<? extends EntityType<? extends Boat>> entityTypeFactory;

    public NTBoatItem(boolean hasChest, Supplier<? extends EntityType<? extends Boat>> type, Properties props) {
        super(hasChest, Boat.Type.OAK, props);
        this.hasChest = hasChest;
        this.entityTypeFactory = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        HitResult hitresult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.ANY);

        if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        }

        Vec3 vec3 = player.getViewVector(1.0F);
        List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vec3.scale(5.0D)).inflate(1.0D), EntitySelector.NO_SPECTATORS.and(Entity::isPickable));

        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (entity.getBoundingBox().inflate(entity.getPickRadius()).contains(hitresult.getLocation())) {
                    return InteractionResultHolder.pass(itemstack);
                }
            }
        }

        if (hitresult.getType() == HitResult.Type.BLOCK) {
            Boat boat = this.entityTypeFactory.get().create(level);
            if (boat == null) return InteractionResultHolder.fail(itemstack);

            boat.setPos(hitresult.getLocation().x, hitresult.getLocation().y, hitresult.getLocation().z);

            // FIX: We check hasChest first, then cast to the specific custom class
            String woodName = itemstack.getItem().toString().replace("_chest_boat", "").replace("_boat", "");
            if (this.hasChest) {
                if (boat instanceof NTChestBoatEntity chestBoat) chestBoat.setWoodType(woodName);
            } else {
                if (boat instanceof NTBoatEntity regularBoat) regularBoat.setWoodType(woodName);
            }

            boat.setYRot(player.getYRot());
            if (!level.noCollision(boat, boat.getBoundingBox())) {
                return InteractionResultHolder.fail(itemstack);
            } else {
                if (!level.isClientSide) {
                    level.addFreshEntity(boat);
                    level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.ENTITY_PLACE, hitresult.getLocation());
                    itemstack.consume(1, player);
                }
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
            }
        }
        return InteractionResultHolder.pass(itemstack);
    }
}