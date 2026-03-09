package net.candyperfumeboy.novamterram.entities.enhancements;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.entities.custom.SpectacledBearEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

@EventBusSubscriber(modid = NovamTerram.MOD_ID)
public class NTLlamaEvents {

    @SubscribeEvent
    public static void onLlamaJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Llama llama) {
            // 1. MOUNTAIN AGILITY: Step up full blocks
            var stepHeight = llama.getAttribute(Attributes.STEP_HEIGHT);
            if (stepHeight != null) {
                stepHeight.setBaseValue(1.0f);
            }
            // 2. GUARDIAN AI: Target monsters
            llama.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(llama, Monster.class, true));
        }
    }

    @SubscribeEvent
    public static void onLlamaTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Llama llama) {

            // --- A. RIDING & STEERING ---
            if (llama.isVehicle() && llama.getControllingPassenger() instanceof Player player) {
                if (player.getTicksFrozen() > 0) player.setTicksFrozen(0);

                if (player.isHolding(Items.WHEAT)) {
                    llama.setYRot(player.getYRot());
                    llama.setYHeadRot(player.getYRot());
                    llama.yRotO = llama.getYRot();
                    llama.setYBodyRot(llama.getYRot());

                    float speed = 0.22F;
                    if (llama.getY() > 120) speed = 0.45F; // Andean Sprint

                    if (player.zza > 0) {
                        Vec3 movement = player.getLookAngle().multiply(1.0, 0.0, 1.0).normalize().scale(speed);
                        llama.setDeltaMovement(movement.x, llama.getDeltaMovement().y, movement.z);
                    }
                }
            }

            // --- B. RECRUITMENT & TRADER SYNERGY ---
            if (llama.tickCount % 60 == 0 && !llama.level().isClientSide) {
                if (llama.inCaravan() || llama.getLeashHolder() instanceof Player) {
                    List<Llama> wildLlamas = llama.level().getEntitiesOfClass(Llama.class, llama.getBoundingBox().inflate(6.0D));
                    for (Llama wild : wildLlamas) {
                        if (!wild.inCaravan() && wild.getLeashHolder() == null && wild.getControllingPassenger() == null) {
                            wild.joinCaravan(llama);
                            wild.playSound(net.minecraft.sounds.SoundEvents.LLAMA_AMBIENT, 1.0F, 1.5F);
                        }
                    }
                }

                if (!llama.level().getEntitiesOfClass(WanderingTrader.class, llama.getBoundingBox().inflate(15.0D)).isEmpty()) {
                    llama.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false));
                }
            }

            // --- C. BEAR PANIC VS COURAGE ---
            if (llama.tickCount % 20 == 0 && !llama.level().isClientSide) {
                var bears = llama.level().getEntitiesOfClass(SpectacledBearEntity.class, llama.getBoundingBox().inflate(10.0D));
                if (!bears.isEmpty()) {
                    if (!llama.inCaravan()) {
                        llama.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 2, false, false));
                        llama.setYHeadRot(llama.getYHeadRot() + (llama.getRandom().nextFloat() * 40 - 20));
                    } else {
                        llama.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1, false, false));
                    }
                }
            }

            // --- D. GOAT SOCIAL SYNERGY ---
            if (llama.tickCount % 100 == 0 && !llama.level().isClientSide) {
                if (!llama.level().getEntitiesOfClass(Goat.class, llama.getBoundingBox().inflate(8.0D)).isEmpty()) {
                    llama.playSound(net.minecraft.sounds.SoundEvents.LLAMA_AMBIENT, 0.8F, 0.8F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLlamaCombat(LivingIncomingDamageEvent event) {
        // --- E. CARAVAN DEFENSIVE VOLLEY ---
        if (event.getEntity() instanceof Llama victim && victim.inCaravan() && event.getSource().getEntity() instanceof LivingEntity attacker) {
            Llama head = victim.getCaravanHead();
            if (head != null) {
                head.setTarget(attacker);
                // Alert nearby caravan members to look at the same threat
                List<Llama> caravan = victim.level().getEntitiesOfClass(Llama.class, victim.getBoundingBox().inflate(8.0D));
                for (Llama member : caravan) {
                    if (member.inCaravan()) member.setTarget(attacker);
                }
            }
        }

        // --- F. BUFFED SPIT & MOUNTAIN COMBO ---
        if (event.getSource().getDirectEntity() instanceof LlamaSpit) {
            event.setAmount(6.0F);
            if (event.getEntity() instanceof LivingEntity victim) {
                victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                Vec3 dir = victim.position().subtract(event.getSource().getDirectEntity().position()).normalize().scale(0.8);
                victim.setDeltaMovement(dir.x, 0.3, dir.z);

                List<Goat> goats = victim.level().getEntitiesOfClass(Goat.class, victim.getBoundingBox().inflate(12.0D));
                for (Goat goat : goats) {
                    goat.setTarget(victim);
                }
            }
        }
    }
}