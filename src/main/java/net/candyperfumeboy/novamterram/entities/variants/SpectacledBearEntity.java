package net.candyperfumeboy.novamterram.entities.variants;

import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SpectacledBearEntity extends PolarBear {
    private int climbEnergy = 1000; // Stamina for climbing

    public SpectacledBearEntity(EntityType<? extends PolarBear> type, Level level) {
        super(type, level);
        // Clear aggressive vanilla target goals so they don't attack the player
        this.targetSelector.removeAllGoals(goal -> true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Fear of Goats: Priority 1 (Highest). Will run from goats within 10 blocks.
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Goat.class, 10.0F, 1.2D, 1.5D));
    }

    // --- BREEDING ---
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return NTEntities.SPECTACLED_BEAR.get().create(level);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.SWEET_BERRIES) || stack.is(Items.SUGAR);
    }

    // --- INTERACTION & RIDING ---
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Refueling mechanic
        if (this.isFood(itemstack) && this.climbEnergy < 1000) {
            this.climbEnergy = Math.min(1000, this.climbEnergy + 300);
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.playSound(SoundEvents.POLAR_BEAR_AMBIENT, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // Mount logic
        if (!this.isBaby() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return (LivingEntity) this.getFirstPassenger();
    }

    // --- PHYSICS & TRAVEL ---
    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive() && this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
            this.setYRot(player.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(player.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;

            float xControl = player.xxa * 0.5F;
            float zControl = player.zza;

            if (zControl <= 0.0F) {
                zControl *= 0.25F;
            }

            // Slow speed on flat ground (Balanced vs Horses)
            this.setSpeed(0.15F);
            super.travel(new Vec3(xControl, travelVector.y, zControl));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public void tick() {
        super.tick();

        // 1. Climbing Physics with Energy Drain
        if (this.horizontalCollision) {
            // Panic/Stop if energy is depleted
            if (this.climbEnergy > 0) {
                Vec3 movement = this.getDeltaMovement();
                double climbSpeed = this.isVehicle() ? 0.25D : 0.2D;
                this.setDeltaMovement(movement.x, climbSpeed, movement.z);
                this.setOnGround(true);

                if (this.tickCount % 5 == 0) {
                    this.climbEnergy--;
                }
            }
        }

        // 2. Visual Reaction to Goats (Rearing Up in Fear)
        if (this.tickCount % 40 == 0 && !this.level().getEntitiesOfClass(Goat.class, this.getBoundingBox().inflate(6.0D)).isEmpty()) {
            this.setStanding(true);
            this.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.0F, 1.2F);
        }
    }

    @Override
    public boolean onClimbable() {
        return (this.horizontalCollision && this.climbEnergy > 0) || super.onClimbable();
    }

    @Override
    public boolean isAggressive() {
        return false;
    }
}