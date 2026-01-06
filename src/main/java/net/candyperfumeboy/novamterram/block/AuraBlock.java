package net.candyperfumeboy.novamterram.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AuraBlock extends Block {

    private static final int TICKS_BETWEEN_UPDATES = 2; // Runs every 2 ticks (~10/sec)

    public AuraBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1); // schedule first tick
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Only apply XP pull every few ticks
        if (level.getGameTime() % TICKS_BETWEEN_UPDATES == 0) {
            Aura.apply(level, pos);
        }

        // Schedule next tick
        level.scheduleTick(pos, this, 1);

        // Particle effect (client-side)
        if (level.isClientSide) {
            spawnParticles(level, pos, random);
        }
    }

    private void spawnParticles(Level level, BlockPos pos, RandomSource random) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.7; // slightly above the block
        double cz = pos.getZ() + 0.5;

        // Spawn a few particles per tick
        for (int i = 0; i < 3; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 1.2;
            double offsetY = random.nextDouble() * 0.5;
            double offsetZ = (random.nextDouble() - 0.5) * 1.2;

            level.addParticle(
                    ParticleTypes.ENCHANT,
                    cx + offsetX,
                    cy + offsetY,
                    cz + offsetZ,
                    0, 0.05, 0 // small upward motion
            );
        }
    }}