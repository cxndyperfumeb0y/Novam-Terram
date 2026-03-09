package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.util.INTFox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * NTFoxEvents
 *
 * Upgrades VANILLA Fox instances with NT sit/follow features.
 * Custom NT variants (SilverFox, DesertFox) use AbstractNTFox and are filtered out.
 *
 * TRUST + BREEDING FLOW:
 *   1. Player uses two sweet berries on two foxes → Fox.spawnChildFromBreeding() calls
 *      getBreedOffspring() → creates the baby entity with no trusts
 *   2. BabyEntitySpawnEvent fires → onFoxBreed() adds player UUID first (slot 0),
 *      then one parent UUID (slot 1)
 *   3. Baby now trusts the player who bred it
 *
 * The old SilverFox/DesertFox getBreedOffspring() overrides were adding parent UUIDs
 * into slots 0 and 1 before this event fired, leaving no room for the player UUID.
 * Those overrides now only create the entity — trust lives here exclusively.
 */
@EventBusSubscriber(modid = NovamTerram.MOD_ID)
public class NTFoxEvents {

    private static final String NBT_UPGRADED = "NTUpgraded";

    // =========================================================================
    // EVENT HANDLERS
    // =========================================================================

    /**
     * Injects NT sit/follow goals into vanilla Fox instances exactly once.
     * NBT_UPGRADED flag prevents re-injection on world load / dimension transfer.
     * AbstractNTFox subclasses are skipped entirely.
     */
    @SubscribeEvent
    public static void upgradeVanillaFoxes(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Fox fox)) return;
        if (fox instanceof AbstractNTFox) return;
        if (fox.getPersistentData().getBoolean(NBT_UPGRADED)) return;

        fox.getPersistentData().putBoolean(NBT_UPGRADED, true);
        fox.goalSelector.addGoal(1, new VanillaFoxSitGoal(fox));
        fox.goalSelector.addGoal(2, new VanillaFoxFollowGoal(fox, 1.25D, 8.0F, 3.0F));

        if (fox.isBaby()) {
            fox.goalSelector.getAvailableGoals().removeIf(w -> w.getGoal() instanceof FollowParentGoal);
            fox.goalSelector.addGoal(8, new FollowParentGoal(fox, 1.1D) {
                @Override
                public boolean canUse() {
                    if (fox instanceof INTFox ntFox && ntFox.nt$isOrderedToSit()) return false;
                    Player nearest = fox.level().getNearestPlayer(fox, 10.0D);
                    if (nearest != null && ((INTFox) fox).nt$trusts(nearest.getUUID())) return false;
                    return super.canUse();
                }
            });
        }

        var speedAttr = fox.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) speedAttr.setBaseValue(0.35D);
    }

    /**
     * Trust inheritance on natural breeding — the ONLY place trust is assigned.
     *
     * Priority:
     *   Slot 0: breeding player UUID  (always — this is who owns the baby)
     *   Slot 1: one parent UUID       (best-effort — fills if slot is available)
     *
     * Both vanilla Fox and AbstractNTFox babies are handled here via INTFox.
     * getBreedOffspring() on custom fox classes must NOT add trust — only create entity.
     */
    @SubscribeEvent
    public static void onFoxBreed(BabyEntitySpawnEvent event) {
        if (!(event.getChild() instanceof Fox baby)) return;
        if (!(baby instanceof INTFox ntBaby)) return;

        // Slot 0: the player who triggered breeding
        Player breedingPlayer = event.getCausedByPlayer();
        if (breedingPlayer != null) {
            ntBaby.nt$addTrustedUUID(breedingPlayer.getUUID());
        }

        // Slot 1: first trusted UUID from parent A (best-effort)
        if (event.getParentA() instanceof INTFox ntParentA) {
            for (UUID uuid : ntParentA.nt$getTrustedUUIDs()) {
                if (uuid != null) {
                    ntBaby.nt$addTrustedUUID(uuid);
                    break;
                }
            }
        }

        if (!baby.level().isClientSide() && baby.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART,
                    baby.getX(), baby.getY() + 0.5D, baby.getZ(),
                    8, 0.3D, 0.3D, 0.3D, 0.1D);
        }
    }

    /**
     * Interaction handler for vanilla Fox only.
     * AbstractNTFox handles its own interactions via mobInteract().
     */
    @SubscribeEvent
    public static void onVanillaFoxInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Fox fox) || fox instanceof AbstractNTFox || !(fox instanceof INTFox ntFox)) return;

        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        // BUG FIX: Allow Nametags and Leads to work on Vanilla Foxes!
        if (stack.is(Items.NAME_TAG) || stack.is(Items.LEAD)) return;

        UUID uuid = player.getUUID();

        // Stick Debugger
        if (stack.is(Items.STICK)) {
            if (!event.getLevel().isClientSide()) {
                if (ntFox.nt$trusts(uuid)) {
                    // Change literal to translatable
                    player.displayClientMessage(Component.translatable("message.novamterram.fox.trust_success"), true);
                    event.getLevel().broadcastEntityEvent(fox, (byte) 7);
                    fox.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);
                } else {
                    // Change literal to translatable
                    player.displayClientMessage(Component.translatable("message.novamterram.fox.trust_fail"), true);
                    fox.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.2F);
                    Vec3 away = fox.position().subtract(player.position()).normalize().scale(0.5D);
                    fox.push(away.x, 0.25D, away.z);
                }
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
            return;
        }

        // Toggle Sitting for trusted players
        if (ntFox.nt$trusts(uuid)) {
            if (stack.has(DataComponents.FOOD) && fox.getHealth() < fox.getMaxHealth()) {
                fox.heal((float) stack.get(DataComponents.FOOD).nutrition());
                event.getLevel().broadcastEntityEvent(fox, (byte) 7);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                fox.playSound(SoundEvents.FOX_EAT, 1.0F, 1.0F);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }

            if (!fox.isFood(stack)) {
                if (!event.getLevel().isClientSide()) {
                    boolean nowSitting = !ntFox.nt$isOrderedToSit();
                    ntFox.nt$setOrderedToSit(nowSitting);
                    fox.getNavigation().stop();
                    // Sync the visual state
                    fox.setSitting(nowSitting);
                    fox.setSleeping(false);
                    fox.playSound(nowSitting ? SoundEvents.FOX_SNIFF : SoundEvents.FOX_SPIT, 1.0F, 1.0F);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
            }
        }
    }

    /**
     * Atmospheric tick effects for all foxes.
     */
    @SubscribeEvent
    public static void onFoxTick(EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof Fox fox)) return;

        if (fox.isSleeping() && fox.tickCount % 40 == 0
                && fox.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.WHITE_ASH,
                    fox.getX(), fox.getY() + 0.5D, fox.getZ(), 2, 0.1, 0.1, 0.1, 0.02);
        }

        if ((fox.tickCount + fox.getId()) % 80 == 0
                && !fox.isOnFire()
                && fox.getTarget() == null
                && fox.getHealth() >= fox.getMaxHealth() * 0.5F) {
            List<Fox> nearby = fox.level().getEntitiesOfClass(Fox.class, fox.getBoundingBox().inflate(3.0D));
            if (nearby.size() > 1) {
                fox.level().playSound(null, fox.getX(), fox.getY(), fox.getZ(),
                        SoundEvents.FOX_AMBIENT, fox.getSoundSource(),
                        1.0F, 1.4F + fox.getRandom().nextFloat() * 0.4F);
                if (fox.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            fox.getX(), fox.getEyeY(), fox.getZ(), 5, 0.3, 0.3, 0.3, 0.05);
                }
            }
        }
    }

    /**
     * Variant-specific death effects. Uses registry type comparison — not string matching.
     */
    @SubscribeEvent
    public static void onFoxDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        if (entity.getType() == NTEntities.SILVER_FOX.get()) {
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    entity.getX(), entity.getY() + 0.5D, entity.getZ(), 20, 0.5, 0.5, 0.5, 0.05);
            entity.playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.5F);
        } else if (entity.getType() == NTEntities.DESERT_FOX.get()) {
            serverLevel.sendParticles(ParticleTypes.DUST_PLUME,
                    entity.getX(), entity.getY() + 0.5D, entity.getZ(), 20, 0.5, 0.5, 0.5, 0.05);
            entity.playSound(SoundEvents.SAND_STEP, 1.0F, 0.8F);
        }
    }

    // =========================================================================
    // VANILLA FOX GOAL CLASSES
    // =========================================================================

    /**
     * Sit goal for vanilla Fox. Supports day/night sleep transition while sitting.
     * FIXED: Sleep transition gated on nt$isOrderedToSit() to prevent desync when
     * the player un-orders sit while the delay timer is counting down.
     */
    public static class VanillaFoxSitGoal extends Goal {
        private final Fox fox;
        private int sleepTickDelay = 0;

        public VanillaFoxSitGoal(Fox fox) {
            this.fox = fox;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return fox instanceof INTFox ntFox && ntFox.nt$isOrderedToSit();
        }

        @Override
        public void start() {
            fox.getNavigation().stop();
            fox.setSitting(true);
            sleepTickDelay = 100;
        }

        @Override
        public void tick() {
            fox.getNavigation().stop();
            if (!(fox instanceof INTFox ntFox) || !ntFox.nt$isOrderedToSit()) return;

            if (sleepTickDelay > 0) sleepTickDelay--;

            if (sleepTickDelay <= 0 && !fox.isSleeping()) {
                long time = fox.level().getDayTime() % 24000;
                if (time > 13000 || fox.level().getMaxLocalRawBrightness(fox.blockPosition()) < 7) {
                    fox.setSitting(false);
                    fox.setSleeping(true);
                }
            }

            if (fox.isSleeping()) {
                long time = fox.level().getDayTime() % 24000;
                if (time < 13000) {
                    fox.setSleeping(false);
                    fox.setSitting(true);
                    sleepTickDelay = 200;
                }
            }

            if (!fox.isSleeping()) {
                Player nearest = fox.level().getNearestPlayer(fox, 10.0D);
                if (nearest != null && ((INTFox) fox).nt$trusts(nearest.getUUID())) {
                    fox.getLookControl().setLookAt(nearest, 10.0F, (float) fox.getMaxHeadXRot());
                }
            }
        }

        @Override
        public void stop() {
            fox.setSitting(false);
            fox.setSleeping(false);
        }
    }

    /**
     * Follow goal for vanilla Fox.
     * FIXED: Iterates ALL trusted UUIDs, uses first online player.
     * FIXED: Teleport uses isPathfindable() instead of isAir()+isSolid().
     */
    public static class VanillaFoxFollowGoal extends Goal {
        private final Fox    fox;
        private final double speed;
        private final float  startDist;
        private final float  stopDist;
        private LivingEntity owner;
        private int          recalcTimer;

        public VanillaFoxFollowGoal(Fox fox, double speed, float start, float stop) {
            this.fox = fox;
            this.speed = speed;
            this.startDist = start;
            this.stopDist = stop;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!(fox instanceof INTFox ntFox) || ntFox.nt$isOrderedToSit()) return false;
            for (UUID uuid : ntFox.nt$getTrustedUUIDs()) {
                if (uuid == null) continue;
                Player player = fox.level().getPlayerByUUID(uuid);
                if (player != null && fox.distanceToSqr(player) > (double)(startDist * startDist)) {
                    this.owner = player;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.owner != null
                    && !(fox instanceof INTFox ntFox && ntFox.nt$isOrderedToSit())
                    && fox.distanceToSqr(this.owner) > (double)(stopDist * stopDist);
        }

        @Override
        public void tick() {
            if (this.owner == null || fox.isLeashed()) return;
            fox.getLookControl().setLookAt(this.owner, 10.0F, (float) fox.getMaxHeadXRot());
            if (--recalcTimer > 0) return;
            recalcTimer = 10;
            double dsq = fox.distanceToSqr(this.owner);
            if (dsq > 144.0D) teleportToOwner();
            else fox.getNavigation().moveTo(this.owner, this.speed);
        }

        private void teleportToOwner() {
            BlockPos base = this.owner.blockPosition();
            for (int i = 0; i < 10; i++) {
                BlockPos target = base.offset(
                        fox.getRandom().nextInt(3) - 1,
                        fox.getRandom().nextInt(2),
                        fox.getRandom().nextInt(3) - 1);
                if (fox.level().getBlockState(target).isPathfindable(PathComputationType.LAND)) {
                    fox.moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
                    fox.getNavigation().stop();
                    return;
                }
            }
        }
    }
}