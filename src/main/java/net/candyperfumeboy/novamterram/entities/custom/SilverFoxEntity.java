package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class SilverFoxEntity extends Fox {

    public SilverFoxEntity(EntityType<? extends Fox> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new VillagePorchGoal(this, 1.0D));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Fox.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D) // Faster than vanilla fox (0.3D)
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    public boolean isInvisible() {
        // Camouflage logic: Invisible when crouching in snow/ice
        if (this.isCrouching()) {
            BlockState below = this.level().getBlockState(this.blockPosition().below());
            return below.is(Blocks.SNOW_BLOCK) || below.is(Blocks.SNOW) || below.is(Blocks.ICE) || below.is(Blocks.PACKED_ICE) || below.is(Blocks.BLUE_ICE);
        }
        return super.isInvisible();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        int timer = this.tickCount + this.getId();

        // 1. GUARDIAN, VILLAGE & SOCIAL LOGIC
        if (timer % 40 == 0) {
            performCombinedAuraLogic();
        }

        // 2. TREASURE DIGGING
        if (this.isPouncing() && this.getXRot() > 60.0F) {
            checkForDigging();
        } else {
            this.getPersistentData().putBoolean("HasDugThisPounce", false);
        }

        // 3. FLOWER TRAILS (Bonemeal Effect)
        if (timer % 20 == 0 && this.getDeltaMovement().horizontalDistanceSqr() > 0.01D) {
            if (this.random.nextFloat() < 0.05F) { // 5% chance when moving
                applyBonemealEffect();
            }
        }
    }

    @Nullable
    @Override
    public Fox getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return NTEntities.SILVER_FOX.get().create(level);
    }

    private void performCombinedAuraLogic() {
        AABB searchArea = this.getBoundingBox().inflate(16.0D);
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, searchArea);

        Monster targetMonster = null;
        IronGolem golem = null;

        for (LivingEntity entity : entities) {
            // A. Spider Fleeing
            if (entity instanceof Spider spider) {
                Vec3 escapePos = DefaultRandomPos.getPosAway(spider, 16, 7, this.position());
                if (escapePos != null) {
                    spider.getNavigation().moveTo(escapePos.x, escapePos.y, escapePos.z, 1.5D);
                }
            }
            // B. Tracking for Golem alerts
            else if (entity instanceof Monster m) {
                targetMonster = m;
            }
            else if (entity instanceof IronGolem g) {
                golem = g;
                trySocialize(g); // RARE INTERACTION
            }
            // C. Villager Interactions
            else if (entity instanceof Villager v) {
                trySocialize(v); // RARE INTERACTION

                if (this.isSleeping() || this.isSitting()) {
                    v.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 300, 0, false, false));

                    if (this.random.nextFloat() < 0.02F) {
                        v.swing(InteractionHand.MAIN_HAND);
                        this.spawnAtLocation(this.random.nextFloat() < 0.1F ? Items.EMERALD : Items.BREAD);
                        this.playSound(SoundEvents.VILLAGER_CELEBRATE, 1.0F, 1.0F);
                    }
                }
            }
        }

        // Alert Golems
        if (targetMonster != null && golem != null && !this.isSleeping()) {
            this.playSound(SoundEvents.FOX_SCREECH, 1.5F, 1.2F);
            golem.setTarget(targetMonster);
        }
    }

    /**
     * RARE SOCIAL INTERACTION:
     * The fox looks at the villager/golem and produces heart particles.
     */
    private void trySocialize(LivingEntity target) {
        if (this.random.nextFloat() < 0.01F) { // 1% chance every 2 seconds
            this.getLookControl().setLookAt(target, 10.0F, (float)this.getMaxHeadXRot());
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART,
                        this.getX(), this.getY() + 0.5D, this.getZ(),
                        1, 0.2D, 0.2D, 0.2D, 0.05D);
            }
        }
    }

    private void checkForDigging() {
        if (this.getPersistentData().getBoolean("HasDugThisPounce")) return;

        BlockPos below = this.blockPosition().below();
        BlockState floor = this.level().getBlockState(below);

        // Allow digging in Snow OR Blue Podzol
        if (floor.is(Blocks.SNOW_BLOCK) || floor.is(Blocks.SNOW) || floor.is(NTBlocks.BLUE_PODZOL.get())) {
            this.getPersistentData().putBoolean("HasDugThisPounce", true);
            if (this.random.nextFloat() < 0.1F) {
                ItemStack loot = this.random.nextFloat() < 0.15F ?
                        new ItemStack(NTItems.OPAL.get()) :
                        new ItemStack(Items.IRON_NUGGET);

                this.spawnAtLocation(loot);
                this.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);

                if (floor.is(Blocks.SNOW)) {
                    this.level().removeBlock(below, false);
                }
            }
        }
    }

    private void applyBonemealEffect() {
        BlockPos pos = this.blockPosition();
        if (this.level() instanceof ServerLevel serverLevel) {
            // Simulate bonemeal use
            BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), serverLevel, pos);
            // Add some magical particles
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY(), this.getZ(), 3, 0.5, 0.5, 0.5, 0.0);
        }
    }

    static class VillagePorchGoal extends MoveToBlockGoal {
        private final SilverFoxEntity fox;

        public VillagePorchGoal(SilverFoxEntity fox, double speed) {
            super(fox, speed, 32);
            this.fox = fox;
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            BlockState state = level.getBlockState(pos);
            // Use direct block references instead of string IDs
            return (state.is(NTBlocks.BLOCKS.getEntries().stream().filter(e -> e.getId().getPath().equals("blue_spruce_stairs")).findFirst().get().get()) ||
                    state.is(NTBlocks.BLOCKS.getEntries().stream().filter(e -> e.getId().getPath().equals("blue_spruce_slab")).findFirst().get().get())) &&
                    level.isEmptyBlock(pos.above());
        }
    }
}