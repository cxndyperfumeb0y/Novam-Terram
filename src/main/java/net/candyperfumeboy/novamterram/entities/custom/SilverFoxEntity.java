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
import net.minecraft.world.entity.item.ItemEntity;
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

import java.util.List;

public class SilverFoxEntity extends AbstractNTFox {

    private boolean hasDugThisPounce = false;

    public SilverFoxEntity(EntityType<? extends Fox> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createSilverFoxAttributes() {
        return AbstractNTFox.createAbstractFoxAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    public Fox getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return NTEntities.SILVER_FOX.get().create(level);
    }

    @Override protected void addVariantGoals() { this.goalSelector.addGoal(5, new VillagePorchGoal(this, 1.0D)); }
    @Override protected boolean isNocturnal() { return false; }

    @Override
    public boolean isInvisible() {
        if (this.isCrouching()) {
            BlockState below = this.level().getBlockState(this.blockPosition().below());
            return below.is(Blocks.SNOW_BLOCK) || below.is(Blocks.SNOW)
                    || below.is(Blocks.ICE) || below.is(Blocks.PACKED_ICE) || below.is(Blocks.BLUE_ICE);
        }
        return super.isInvisible();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) return;
        int timer = this.tickCount + this.getId();
        if (timer % 40 == 0) performAuraLogic();
        // isPouncing() is package-private — approximate with movement state
        boolean pouncing = !this.onGround() && this.getDeltaMovement().y < -0.1D && this.getXRot() > 50.0F;
        if (pouncing) checkForDigging(); else hasDugThisPounce = false;
        if (timer % 20 == 0 && this.getDeltaMovement().horizontalDistanceSqr() > 0.01D && this.random.nextFloat() < 0.05F)
            applyBonemealEffect();
    }

    private void performAuraLogic() {
        AABB area = this.getBoundingBox().inflate(16.0D);
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, area);
        Monster targetMonster = null; IronGolem golem = null;

        for (LivingEntity entity : entities) {
            if (entity instanceof Spider spider) {
                Vec3 pos = DefaultRandomPos.getPosAway(spider, 16, 7, this.position());
                if (pos != null) spider.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.5D);
            } else if (entity instanceof Monster m) {
                targetMonster = m;
            } else if (entity instanceof IronGolem g) {
                golem = g;
                if (!this.nt$isOrderedToSit()) trySocialize(g);
            } else if (entity instanceof Villager v) {
                if (!this.nt$isOrderedToSit()) trySocialize(v);
                if (this.isSleeping() || this.nt$isOrderedToSit()) {
                    v.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 300, 0, false, false));
                    if (this.random.nextFloat() < 0.02F) {
                        v.swing(InteractionHand.MAIN_HAND);
                        this.spawnAtLocation(this.random.nextFloat() < 0.1F ? Items.EMERALD : Items.BREAD);
                        this.playSound(SoundEvents.VILLAGER_CELEBRATE, 1.0F, 1.0F);
                    }
                }
            }
        }
        if (targetMonster != null && golem != null && !this.isSleeping() && !this.nt$isOrderedToSit()) {
            this.playSound(SoundEvents.FOX_SCREECH, 1.5F, 1.2F);
            golem.setTarget(targetMonster);
        }
    }

    private void trySocialize(LivingEntity target) {
        if (this.random.nextFloat() < 0.01F) {
            this.getLookControl().setLookAt(target, 10.0F, (float)this.getMaxHeadXRot());
            if (this.level() instanceof ServerLevel sl)
                sl.sendParticles(ParticleTypes.HEART, getX(), getEyeY(), getZ(), 1, 0.3D, 0.3D, 0.3D, 0.05D);
        }
    }

    private void checkForDigging() {
        if (hasDugThisPounce) return;
        BlockPos below = this.blockPosition().below();
        BlockState floor = this.level().getBlockState(below);
        if (floor.is(Blocks.SNOW_BLOCK) || floor.is(Blocks.SNOW) || floor.is(NTBlocks.BLUE_PODZOL.get())) {
            hasDugThisPounce = true;
            if (this.random.nextFloat() < 0.1F) {
                ItemStack loot = this.random.nextFloat() < 0.15F ? new ItemStack(NTItems.OPAL.get()) : new ItemStack(Items.IRON_NUGGET);
                ItemEntity item = new ItemEntity(this.level(), getX(), getY(), getZ(), loot);
                item.setDeltaMovement(0, 0.2, 0);
                this.level().addFreshEntity(item);
                this.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);
                if (floor.is(Blocks.SNOW)) this.level().removeBlock(below, false);
            }
        }
    }

    private void applyBonemealEffect() {
        if (this.level() instanceof ServerLevel sl) {
            boolean grew = BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), sl, this.blockPosition());
            if (grew) {
                sl.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY(), getZ(), 5, 0.5, 0.5, 0.5, 0.05);
                this.playSound(SoundEvents.BONE_MEAL_USE, 1.0F, 1.0F);
            }
        }
    }

    // =========================================================================
    // GOALS
    // =========================================================================

    /**
     * VillagePorchGoal — Silver Fox seeks out Blue Spruce furniture blocks to
     * rest on, like a cat on a porch.
     *
     * FIXED: Replaced string path matching with direct NTBlocks references.
     * String matching on getPath() was fragile (silent break on block rename)
     * and slower. Direct block reference is both safer and faster.
     *
     * If BLUE_SPRUCE_STAIRS/SLAB are not yet in NTBlocks, add them or use a
     * custom BlockTag (e.g. NTBlockTags.SILVER_FOX_REST_BLOCKS).
     */
    static class VillagePorchGoal extends MoveToBlockGoal {
        private final SilverFoxEntity fox;

        VillagePorchGoal(SilverFoxEntity fox, double speed) {
            super(fox, speed, 32);
            this.fox = fox;
        }

        @Override
        public boolean canUse() {
            return !fox.nt$isOrderedToSit() && super.canUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            BlockState state = level.getBlockState(pos);

            // Get the registry name of the block being checked
            String blockId = net.minecraft.core.registries.BuiltInRegistries.BLOCK
                    .getKey(state.getBlock()).getPath();

            // Direct string comparison is MUCH faster than stream-filtering the whole registry
            return (blockId.equals("blue_spruce_stairs") || blockId.equals("blue_spruce_slab"))
                    && level.isEmptyBlock(pos.above());
        }
    }
}