package net.candyperfumeboy.novamterram.entities;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = NovamTerram.MOD_ID)
public class NTLlamaEvents {

    @SubscribeEvent
    public static void onLlamaJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Llama llama) {
            // 1. MOUNTAIN AGILITY: Step up full blocks (1.0f)
            var stepHeight = llama.getAttribute(Attributes.STEP_HEIGHT);
            if (stepHeight != null) {
                stepHeight.setBaseValue(1.0f);
            }

            // 2. GUARDIAN AI: Defend against Monsters
            llama.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(llama, Monster.class, true));
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Llama llama) {

            // 1. POWDERED SNOW PROTECTION
            if (llama.getControllingPassenger() instanceof Player player) {
                // Keep the player from freezing
                if (player.getTicksFrozen() > 0) {
                    player.setTicksFrozen(0);
                }

                // 2. STEERING LOGIC (The "Wheat" Controller)
                // If the player is holding Wheat, they can steer the Llama
                if (player.isHolding(Items.WHEAT)) {
                    // Match Llama rotation to Player rotation
                    llama.setYRot(player.getYRot());
                    llama.setYHeadRot(player.getYRot());
                    llama.yRotO = llama.getYRot();

                    // Move the Llama forward if the player is "driving"
                    // We check if the player is pressing movement keys (approximate)
                    if (player.zza > 0) {
                        float speed = 0.2F;
                        // Apply High Altitude Speed Bonus
                        if (llama.getY() > 100) speed += 0.1F;

                        llama.setSpeed(speed);
                        llama.travel(new net.minecraft.world.phys.Vec3(0, 0, 1));
                    }
                }
            }

            // 3. AMBIENT ALTITUDE BUFF (For wild/unridden llamas)
            if (!llama.level().isClientSide && llama.getY() > 100 && llama.tickCount % 20 == 0) {
                if (llama.getControllingPassenger() == null) {
                    llama.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0, false, false));
                }
            }
        }
    }
    @SubscribeEvent
    public static void onLlamaSpitDamage(LivingIncomingDamageEvent event) {
        // 5. BUFFED SPIT
        if (event.getSource().getDirectEntity() instanceof LlamaSpit) {
            event.setAmount(6.0F); // Increased damage for your mountain Llama

            if (event.getEntity() instanceof Monster monster) {
                monster.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            }
        }
    }

@SubscribeEvent
public static void onLlamaTick(EntityTickEvent.Post event) {
    if (event.getEntity() instanceof Llama llama && llama.isVehicle()) {
        if (llama.getControllingPassenger() instanceof Player player) {

            // Only steer if the player is holding Wheat
            if (player.isHolding(Items.WHEAT)) {

                // 1. Set the Llama's rotation to match the player
                llama.setYRot(player.getYRot());
                llama.yRotO = llama.getYRot();
                llama.setXRot(player.getXRot() * 0.5F);
                llama.setYBodyRot(llama.getYRot());
                llama.setYHeadRot(llama.getYRot());

                // 2. High Altitude Speed Logic
                float speed = 0.2F; // Base speed
                if (llama.getY() > 120) {
                    speed = 0.45F; // "Andean Sprint" speed
                }

                // 3. Force Movement Forward
                // This makes the Llama move in the direction the player is looking
                Vec3 movement = player.getLookAngle().multiply(1.0, 0.0, 1.0).normalize().scale(speed);
                llama.setDeltaMovement(movement.x, llama.getDeltaMovement().y, movement.z);
            }
        }
    }
}}