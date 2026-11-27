package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.util.INTFox;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Ocelot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.UUID;

/** Wild foxes and ocelots sometimes go after wild songbirds. */
@EventBusSubscriber(modid = NovamTerram.MOD_ID)
public class SongbirdPredators {

    @SubscribeEvent
    public static void onPredatorJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof Mob mob)) return;
        if (!(mob instanceof Fox || mob instanceof Ocelot)) return;
        // The event runs on every load, so don't add the goal twice
        if (mob.targetSelector.getAvailableGoals().stream().anyMatch(w -> w.getGoal() instanceof HuntSongbirdGoal)) return;
        mob.targetSelector.addGoal(5, new HuntSongbirdGoal(mob));
    }

    /** True for a fox or ocelot that nobody has tamed or befriended. */
    static boolean isWildPredator(Mob mob) {
        // "IsTamed" is the tag NTOcelotEvents saves on tamed ocelots
        if (mob instanceof Ocelot ocelot) return !ocelot.getPersistentData().getBoolean("IsTamed");
        if (mob instanceof Fox fox) {
            if (fox.isSleeping()) return false;
            if (fox instanceof INTFox ntFox) {
                for (UUID trusted : ntFox.nt$getTrustedUUIDs()) {
                    if (trusted != null) return false;
                }
            }
            return true;
        }
        return false;
    }

    /** Short, occasional hunts with a long rest afterwards, so songbirds don't get wiped out. */
    private static class HuntSongbirdGoal extends NearestAttackableTargetGoal<SongbirdEntity> {
        private int nextHunt;
        private int giveUpAt;

        HuntSongbirdGoal(Mob mob) {
            super(mob, SongbirdEntity.class, 40, true, false,
                    target -> target instanceof SongbirdEntity bird && !bird.isTame());
        }

        @Override
        public boolean canUse() {
            return this.mob.tickCount >= this.nextHunt && isWildPredator(this.mob) && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.mob.tickCount < this.giveUpAt && isWildPredator(this.mob) && super.canContinueToUse();
        }

        @Override
        public void start() {
            super.start();
            this.giveUpAt = this.mob.tickCount + 200;
        }

        @Override
        public void stop() {
            super.stop();
            this.mob.setTarget(null);
            this.nextHunt = this.mob.tickCount + 3600 + this.mob.getRandom().nextInt(3600); // 3-6 minutes
        }
    }
}
