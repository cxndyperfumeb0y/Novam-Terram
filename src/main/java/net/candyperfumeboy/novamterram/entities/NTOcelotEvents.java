package net.candyperfumeboy.novamterram.entities;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.entities.ai.OcelotFollowOwnerGoal;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

@EventBusSubscriber(modid = NovamTerram.MOD_ID)
public class NTOcelotEvents {

    @SubscribeEvent
    public static void onOcelotInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof Ocelot ocelot) {
            ItemStack stack = event.getItemStack();
            boolean isTamed = ocelot.getPersistentData().getBoolean("IsTamed");

            if (event.getHand() != InteractionHand.MAIN_HAND) return;

            // 1. TAMING
            if (!isTamed && (stack.is(Items.COD) || stack.is(Items.SALMON))) {
                if (!event.getLevel().isClientSide) {
                    if (!event.getEntity().getAbilities().instabuild) stack.shrink(1);
                    ocelot.getPersistentData().putBoolean("IsTamed", true);
                    ocelot.getPersistentData().putUUID("OwnerUUID", event.getEntity().getUUID());

                    // ANTI-DESPAWN: Set persistence when tamed
                    ocelot.setPersistenceRequired();

                    event.getLevel().broadcastEntityEvent(ocelot, (byte) 7); // Hearts
                    event.getEntity().displayClientMessage(Component.literal("This Ocelot now follows you!"), true);
                    ocelot.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
                }
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
                event.setCanceled(true);
            }

            // 2. HEALING
            else if (isTamed && (stack.is(Items.COD) || stack.is(Items.SALMON)) && ocelot.getHealth() < ocelot.getMaxHealth()) {
                if (!event.getLevel().isClientSide) {
                    if (!event.getEntity().getAbilities().instabuild) stack.shrink(1);
                    ocelot.heal(2.0F);
                    ocelot.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
                    if (ocelot.getRandom().nextFloat() < 0.3F) {
                        ocelot.playSound(SoundEvents.CAT_AMBIENT, 1.0F, 1.0F);
                    }
                }
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
                event.setCanceled(true);
            }

            // 3. DYEING
            else if (isTamed && stack.getItem() instanceof DyeItem dye) {
                if (!event.getLevel().isClientSide) {
                    ocelot.getPersistentData().putInt("CollarColor", dye.getDyeColor().getId());
                    event.getLevel().playSound(null, ocelot.blockPosition(), SoundEvents.DYE_USE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    if (!event.getEntity().getAbilities().instabuild) stack.shrink(1);
                }
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
                event.setCanceled(true);
            }
            if (isTamed && (stack.is(Items.COD) || stack.is(Items.SALMON)) && ocelot.getAge() == 0 && !ocelot.isInLove()) {
                if (!event.getLevel().isClientSide) {
                    ocelot.setInLove(event.getEntity()); // This puts the Ocelot in "Love Mode"
                    if (!event.getEntity().getAbilities().instabuild) stack.shrink(1);
                }
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
                event.setCanceled(true);
            }

            // 4. SITTING & TOGGLE SOUNDS
            else if (isTamed && stack.isEmpty()) {
                if (!event.getLevel().isClientSide) {
                    boolean isSitting = !ocelot.getPersistentData().getBoolean("IsSitting");
                    ocelot.getPersistentData().putBoolean("IsSitting", isSitting);

                    if (isSitting) {
                        ocelot.setPose(Pose.CROUCHING);
                        ocelot.getNavigation().stop();
                        ocelot.playSound(SoundEvents.CAT_STRAY_AMBIENT, 1.0F, 0.8F + ocelot.getRandom().nextFloat() * 0.4F);
                    } else {
                        ocelot.setPose(Pose.STANDING);
                        ocelot.playSound(SoundEvents.CAT_AMBIENT, 1.0F, 1.0F);
                    }

                    event.getEntity().displayClientMessage(
                            Component.literal(isSitting ? "Ocelot is now Sitting" : "Ocelot is now Following"), true);
                }
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onOcelotHurt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Ocelot ocelot)) return;

        boolean isTamed = ocelot.getPersistentData().getBoolean("IsTamed");

        if (event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)) {
            event.setCanceled(true);
            return;
        }

        if (event.getSource().is(net.minecraft.world.damagesource.DamageTypes.FALL)) {
            event.setCanceled(true);
            return;
        }

        if (isTamed && event.getSource().getEntity() instanceof Player player) {
            if (player.getUUID().equals(ocelot.getPersistentData().getUUID("OwnerUUID"))) {
                event.setCanceled(true);
                return;
            }
        }

        if (isTamed && event.getAmount() > 0.0F) {
            if (ocelot.getRandom().nextFloat() < 0.5F) {
                ocelot.playSound(SoundEvents.CAT_HISS, 1.0F, 1.0F);
            } else {
                ocelot.playSound(SoundEvents.CAT_HURT, 1.0F, 1.0F);
            }
        }

        if (event.getSource().getEntity() instanceof Ocelot) {
            if (event.getEntity() instanceof Creeper) {
                event.setAmount(event.getAmount() * 10.0F);
                event.getEntity().level().playSound(null, event.getEntity().blockPosition(), SoundEvents.CAT_HISS, SoundSource.NEUTRAL, 1.0F, 1.5F);
            }
        }
    }

    @SubscribeEvent
    public static void onOcelotTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Ocelot ocelot) {
            boolean isSitting = ocelot.getPersistentData().getBoolean("IsSitting");

            if (isSitting) {
                ocelot.setPose(Pose.CROUCHING);
                ocelot.getNavigation().stop();
                ocelot.setDeltaMovement(0, ocelot.getDeltaMovement().y, 0);

                if (!ocelot.level().isClientSide && ocelot.getRandom().nextInt(400) == 0) {
                    ocelot.level().playSound(null, ocelot.blockPosition(), SoundEvents.CAT_PURR, SoundSource.NEUTRAL, 0.6F, 1.0F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        if (event.getParentA() instanceof Ocelot parentA && event.getChild() instanceof Ocelot baby) {
            if (parentA.getPersistentData().getBoolean("IsTamed")) {
                baby.getPersistentData().putBoolean("IsTamed", true);
                baby.getPersistentData().putUUID("OwnerUUID", parentA.getPersistentData().getUUID("OwnerUUID"));
                baby.getPersistentData().putInt("CollarColor", parentA.getPersistentData().getInt("CollarColor"));
                baby.setPersistenceRequired(); // Babies should be persistent too
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Ocelot ocelot) {
            // ANTI-DESPAWN: Ensure existing tamed ones are set to persistent when they load
            if (ocelot.getPersistentData().getBoolean("IsTamed")) {
                ocelot.setPersistenceRequired();
            }

            // Remove FEAR
            ocelot.goalSelector.getAvailableGoals().removeIf(goal ->
                    goal.getPriority() == 4 || goal.getPriority() == 1
            );

            // Add CUSTOM AI
            ocelot.goalSelector.addGoal(2, new OcelotFollowOwnerGoal(ocelot, 1.2D));
            ocelot.goalSelector.addGoal(3, new LookAtPlayerGoal(ocelot, Player.class, 10.0F));
            ocelot.goalSelector.addGoal(2, new LeapAtTargetGoal(ocelot, 0.4F));

            // Add CREEPER HUNTING (Priority 1)
            ocelot.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(ocelot, Creeper.class, true));

            // Add PREDATOR HUNTING (Only if NOT sitting)
            ocelot.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(ocelot, Chicken.class, true) {
                @Override public boolean canUse() { return super.canUse() && !ocelot.getPersistentData().getBoolean("IsSitting"); }
            });
            ocelot.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(ocelot, Rabbit.class, true) {
                @Override public boolean canUse() { return super.canUse() && !ocelot.getPersistentData().getBoolean("IsSitting"); }
            });
            ocelot.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(ocelot, Cod.class, true) {
                @Override public boolean canUse() { return super.canUse() && !ocelot.getPersistentData().getBoolean("IsSitting"); }
            });
            ocelot.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(ocelot, Salmon.class, true) {
                @Override public boolean canUse() { return super.canUse() && !ocelot.getPersistentData().getBoolean("IsSitting"); }
            });
        }

    }

    @SubscribeEvent
    public static void onPhantomTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Phantom phantom) {
            AABB searchArea = phantom.getBoundingBox().inflate(16.0D);
            List<Ocelot> nearbyOcelots = phantom.level().getEntitiesOfClass(Ocelot.class, searchArea);

            if (!nearbyOcelots.isEmpty()) {
                Ocelot ocelot = nearbyOcelots.get(0);
                double xDir = phantom.getX() - ocelot.getX();
                double zDir = phantom.getZ() - ocelot.getZ();
                phantom.setDeltaMovement(phantom.getDeltaMovement().add(Math.signum(xDir) * 0.2, 0.1, Math.signum(zDir) * 0.2));

                if (phantom.getRandom().nextInt(60) == 0) {
                    phantom.level().playSound(null, phantom.blockPosition(), SoundEvents.PHANTOM_HURT, SoundSource.HOSTILE, 1.0F, 1.2F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (event.getEntity().level().isClientSide) return;
        Player player = event.getEntity();
        AABB area = new AABB(player.blockPosition()).inflate(10.0D);
        List<Ocelot> ocelots = player.level().getEntitiesOfClass(Ocelot.class, area,
                ocelot -> ocelot.getPersistentData().getBoolean("IsTamed") &&
                        !ocelot.getPersistentData().getBoolean("IsSitting"));

        for (Ocelot ocelot : ocelots) {
            if (ocelot.getRandom().nextFloat() < 0.7F) {
                ocelot.spawnAtLocation(getRandomOcelotGift(ocelot.getRandom()));
                ocelot.playSound(SoundEvents.CAT_AMBIENT, 1.0F, 1.0F);
            }
        }
    }

    private static ItemStack getRandomOcelotGift(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 30) return new ItemStack(Items.FEATHER);
        if (roll < 60) return new ItemStack(Items.CHICKEN);
        if (roll < 80) return new ItemStack(Items.STRING);
        if (roll < 90) return new ItemStack(Items.RABBIT_FOOT);
        return new ItemStack(Items.COCOA_BEANS);
    }


    @SubscribeEvent
    public static void onCrossbreed(BabyEntitySpawnEvent event) {
        if ((event.getParentA() instanceof Ocelot && event.getParentB() instanceof net.minecraft.world.entity.animal.Cat) ||
                (event.getParentA() instanceof net.minecraft.world.entity.animal.Cat && event.getParentB() instanceof Ocelot)) {

            net.minecraft.world.level.Level level = event.getParentA().level();

            if (!level.isClientSide) {
                boolean isOcelotBaby = level.random.nextBoolean();
                net.minecraft.world.entity.AgeableMob baby;

                if (isOcelotBaby) {
                    baby = net.minecraft.world.entity.EntityType.OCELOT.create(level);
                } else {
                    baby = net.minecraft.world.entity.EntityType.CAT.create(level);
                }

                if (baby != null) {
                    baby.setAge(-24000);
                    baby.moveTo(event.getParentA().getX(), event.getParentA().getY(), event.getParentA().getZ(), 0, 0);

                    if (baby instanceof Ocelot ocelotBaby) {
                        ocelotBaby.getPersistentData().putBoolean("IsTamed", true);
                        ocelotBaby.getPersistentData().putUUID("OwnerUUID", event.getParentA().getUUID());
                        ocelotBaby.setPersistenceRequired();
                    }

                    level.addFreshEntity(baby);
                    level.broadcastEntityEvent(baby, (byte) 7);
                }
            }
            event.setCanceled(true);
        }
    }}