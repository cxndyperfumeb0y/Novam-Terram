package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.List;

public class DesertFoxEntity extends AbstractNTFox {

    private int digCooldown = 0;

    public DesertFoxEntity(EntityType<? extends Fox> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createDesertFoxAttributes() {
        return AbstractNTFox.createAbstractFoxAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FOLLOW_RANGE, 45.0D);
    }

    @Override
    public Fox getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return NTEntities.DESERT_FOX.get().create(level);
    }

    @Override
    protected void addVariantGoals() {
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Silverfish.class, 10, true, false, null));
        this.goalSelector.addGoal(6, new BigEarListeningGoal(this));
        this.goalSelector.addGoal(7, new CamelScoutGoal(this, 0.8D));
        this.goalSelector.addGoal(12, new DesertArchaeologyGoal(this));
    }

    @Override protected boolean isNocturnal() { return true; }

    @Override
    public float getVoicePitch() { return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.7F; }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !this.isAlive()) return;
        if (digCooldown > 0) digCooldown--;

        // Cactus healing — desert fox treats cactus as medicine
        if (this.tickCount % 100 == 0 && this.getHealth() < this.getMaxHealth()
                && this.level().getBlockState(this.blockPosition()).is(Blocks.CACTUS)) {
            this.heal(1.0F);
            this.playSound(SoundEvents.FOX_EAT, 0.5F, 1.2F);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE)
                || source.is(DamageTypes.HOT_FLOOR) || source.is(DamageTypes.CACTUS))
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("NTDigCooldown", digCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        digCooldown = compound.getInt("NTDigCooldown");
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            BlockState state = this.level().getBlockState(this.blockPosition().below());
            for (int i = 0; i < 6; ++i)
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                        getX() + (random.nextDouble()-0.5D)*0.5D, getY(),
                        getZ() + (random.nextDouble()-0.5D)*0.5D, 0, 0.1D, 0);
        } else super.handleEntityEvent(id);
    }

    // Goals

    static class BigEarListeningGoal extends Goal {
        private final DesertFoxEntity fox; private int listenTimer;
        BigEarListeningGoal(DesertFoxEntity fox) { this.fox = fox; setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE)); }
        @Override public boolean canUse() {
            if (fox.isSleeping() || fox.nt$isOrderedToSit() || fox.getRandom().nextInt(200) != 0) return false;
            return !fox.level().getEntitiesOfClass(LivingEntity.class, fox.getBoundingBox().inflate(12.0D), e -> e instanceof Silverfish).isEmpty();
        }
        @Override public boolean canContinueToUse() { return listenTimer > 0 && !fox.nt$isOrderedToSit(); }
        @Override public void start() { listenTimer = 40 + fox.getRandom().nextInt(40); fox.setIsInterested(true); fox.getNavigation().stop(); }
        @Override public void stop() { fox.setIsInterested(false); }
        @Override public void tick() { if (--listenTimer % 10 == 0) fox.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.5F); }
    }

    static class CamelScoutGoal extends Goal {
        private final DesertFoxEntity fox; private final double speed; private Camel camel; private int cooldown;
        CamelScoutGoal(DesertFoxEntity fox, double speed) { this.fox = fox; this.speed = speed; setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }
        @Override public boolean canUse() {
            if (fox.isSleeping() || fox.nt$isOrderedToSit() || --cooldown > 0) return false;
            List<Camel> list = fox.level().getEntitiesOfClass(Camel.class, fox.getBoundingBox().inflate(16.0D));
            if (!list.isEmpty()) { camel = list.get(fox.getRandom().nextInt(list.size())); return true; }
            return false;
        }
        @Override public boolean canContinueToUse() { return camel != null && camel.isAlive() && !fox.nt$isOrderedToSit() && fox.distanceToSqr(camel) < 256.0D; }
        @Override public void stop() { camel = null; cooldown = 600; }
        @Override public void tick() {
            fox.getLookControl().setLookAt(camel, 10.0F, (float)fox.getMaxHeadXRot());
            if (fox.distanceToSqr(camel) > 9.0D) fox.getNavigation().moveTo(camel, speed);
            else fox.getNavigation().stop();
        }
    }

    /**
     * Archaeology dig goal. Digs in sand for loot. Handles both normal sand and
     * suspicious sand. The reflection-based loot approach has been replaced with
     * a simple and reliable item drop — reflection on BrushableBlockEntity is
     * brittle in prod builds because the field name differs between mappings.
     */
    static class DesertArchaeologyGoal extends Goal {
        private final DesertFoxEntity fox; private int digTimeout; private BlockPos targetPos;
        DesertArchaeologyGoal(DesertFoxEntity fox) { this.fox = fox; setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override public boolean canUse() {
            if (fox.digCooldown > 0 || fox.isSleeping() || fox.nt$isOrderedToSit() || fox.random.nextInt(1000) != 0) return false;
            BlockPos below = fox.blockPosition().below();
            BlockState state = fox.level().getBlockState(below);
            if (state.is(BlockTags.SAND) || state.is(Blocks.SUSPICIOUS_SAND)) { targetPos = below; return true; }
            return false;
        }
        @Override public boolean canContinueToUse() { return digTimeout > 0 && !fox.nt$isOrderedToSit(); }
        @Override public void start() { digTimeout = 80; fox.getNavigation().stop(); fox.playSound(SoundEvents.SAND_BREAK, 1.0F, 0.8F); }
        @Override public void tick() {
            digTimeout--;
            if (digTimeout % 5 == 0) fox.level().broadcastEntityEvent(fox, (byte) 4);
            if (digTimeout <= 0) {
                BlockState state = fox.level().getBlockState(targetPos);
                if (state.is(Blocks.SUSPICIOUS_SAND)) {
                    // Convert suspicious sand to normal sand and drop archaeology-themed loot
                    fox.level().setBlock(targetPos, Blocks.SAND.defaultBlockState(), 3);
                    fox.spawnAtLocation(fox.random.nextFloat() < 0.3F ? Items.GUSTER_POTTERY_SHERD : Items.BONE);
                    fox.playSound(SoundEvents.BRUSH_SAND_COMPLETED, 1.0F, 1.0F);
                } else if (state.is(NTBlocks.AMBER_SAND.get())) {
                    fox.spawnAtLocation(Items.GOLD_NUGGET);
                } else if (state.is(NTBlocks.OLIVINE_SAND.get())) {
                    fox.spawnAtLocation(Items.RAW_GOLD);
                } else {
                    fox.spawnAtLocation(fox.random.nextBoolean() ? Items.RABBIT_FOOT : Items.STICK);
                }
                fox.playSound(SoundEvents.FOX_EAT, 1.0F, 1.0F);
                fox.digCooldown = 6000;
            }
        }
    }
}