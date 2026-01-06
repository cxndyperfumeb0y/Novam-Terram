package net.candyperfumeboy.novamterram.item.custom;

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

public class NTBoatItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
    private final Supplier<? extends EntityType<? extends Boat>> boatType;

    public NTBoatItem(boolean hasChest, Supplier<? extends EntityType<? extends Boat>> boatType, Item.Properties properties) {
        super(properties);
        this.boatType = boatType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        HitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);

        if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        }

        Vec3 viewVec = player.getViewVector(1.0F);
        List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(viewVec.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);

        if (!list.isEmpty()) {
            Vec3 eyePos = player.getEyePosition();
            for (Entity entity : list) {
                AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
                if (aabb.contains(eyePos)) {
                    return InteractionResultHolder.pass(itemstack);
                }
            }
        }

        if (hitresult.getType() == HitResult.Type.BLOCK) {
            // This is where we use the Supplier to avoid the Registry Crash
            Boat boat = this.boatType.get().create(level);
            if (boat == null) return InteractionResultHolder.fail(itemstack);

            boat.setPos(hitresult.getLocation().x, hitresult.getLocation().y, hitresult.getLocation().z);
            boat.setYRot(player.getYRot());

            if (!level.noCollision(boat, boat.getBoundingBox())) {
                return InteractionResultHolder.fail(itemstack);
            } else {
                if (!level.isClientSide) {
                    level.addFreshEntity(boat);
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, hitresult.getLocation());
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                }
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
            }
        }

        return InteractionResultHolder.pass(itemstack);
    }
}