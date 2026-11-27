package net.candyperfumeboy.novamterram.entities.custom;

import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class SongbirdEntity extends Parrot {

    private static final Ingredient FOOD_ITEMS = Ingredient.of(
            Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS,
            Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD
    );
    private static final byte SING_EVENT = 80;

    private int singTicks; // client animation
    private int nextAlarmCheck;
    private int alarmReadyAt;
    private int startledUntil;
    private int fleeUntil;
    private int songNote = -1; // -1 = not singing
    private int nextNoteAt;
    private boolean wearerWasSneaking;
    private int lastSneakPress;

    // Synced so the client knows which texture to draw
    private static final EntityDataAccessor<Integer> DATA_COLOR =
            SynchedEntityData.defineId(SongbirdEntity.class, EntityDataSerializers.INT);
    // Synced so the client can draw the sleeping pose
    private static final EntityDataAccessor<Boolean> DATA_ROOSTING =
            SynchedEntityData.defineId(SongbirdEntity.class, EntityDataSerializers.BOOLEAN);
    // Entity id of the player whose head this bird is on, or -1
    private static final EntityDataAccessor<Integer> DATA_HEAD_OWNER =
            SynchedEntityData.defineId(SongbirdEntity.class, EntityDataSerializers.INT);

    public SongbirdEntity(EntityType<? extends Parrot> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Parrot.createAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.FLYING_SPEED, 0.48F)
                .add(Attributes.MOVEMENT_SPEED, 0.22F);
    }

    // --- COLOUR ---

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLOR, 0);
        builder.define(DATA_ROOSTING, false);
        builder.define(DATA_HEAD_OWNER, -1);
    }

    public SongbirdColor getSongbirdColor() {
        return SongbirdColor.byIndex(this.entityData.get(DATA_COLOR));
    }

    public void setSongbirdColor(SongbirdColor color) {
        this.entityData.set(DATA_COLOR, color.ordinal());
    }

    // Saved by name, so adding or reordering colours later won't repaint existing birds
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("SongbirdColor", this.getSongbirdColor().id());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSongbirdColor(SongbirdColor.byId(compound.getString("SongbirdColor")));
    }

    // Wild spawns match their biome. Spawn eggs and /summon can be any colour.
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        boolean wild = spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION;
        this.setSongbirdColor(wild
                ? SongbirdColor.forBiome(level.getBiome(this.blockPosition()), this.blockPosition(), this.random)
                : SongbirdColor.random(this.random));
        return data;
    }

    @Override
    protected void registerGoals() {
        // Priority 0: Emergency
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new SongbirdGoals.TreeEscapeGoal(this));

        // Priority 1: Commands & Taming
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new SongbirdGoals.SilverfishGoal(this));
        this.goalSelector.addGoal(2, new SongbirdGoals.SeedGiftGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 5.0F, 1.0F));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, FOOD_ITEMS, false));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D)); // chicks stay with adults

        // Priority 3: Special Interactions
        this.goalSelector.addGoal(3, new PerchOnGolemGoal(this)); // Physical perching
        this.goalSelector.addGoal(3, new FollowFaunGoal(this, 1.0D, 3.0F, 7.0F));

        // Priority 4: Sleeping
        this.goalSelector.addGoal(4, new SongbirdGoals.RoostGoal(this));

        // Priority 5: Environmental
        this.goalSelector.addGoal(5, new SongbirdLeafPerchGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new SongbirdShelterGoal(this));

        // Priority 6 and below: Flocking, Flight & Looking
        this.goalSelector.addGoal(6, new SongbirdGoals.FlockGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    // --- BREEDING & TAMING ---

    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD_ITEMS.test(stack);
    }

    // Vanilla parrots cannot breed or be babies, so both are switched back on here
    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }

    @Override
    public boolean canMate(Animal other) {
        return other != this && other instanceof SongbirdEntity bird && this.isInLove() && bird.isInLove();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        SongbirdEntity baby = NTEntities.SONGBIRD.get().create(level);
        if (baby != null && parent instanceof SongbirdEntity parentBird) {
            // Chicks take after one of their parents
            baby.setSongbirdColor(this.random.nextBoolean() ? this.getSongbirdColor() : parentBird.getSongbirdColor());
            if (this.isTame()) {
                baby.setOwnerUUID(this.getOwnerUUID());
                baby.setTame(true, true);
            }
        }
        return baby;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean food = this.isFood(stack);

        if (!this.isTame() && food) {
            if (!player.getAbilities().instabuild) stack.shrink(1);
            if (!this.level().isClientSide) {
                if (this.random.nextInt(10) == 0) {
                    this.tame(player);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                    player.displayClientMessage(Component.translatable("message.novamterram.songbird.trust_success"), true);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                    player.displayClientMessage(Component.translatable("message.novamterram.songbird.trust_fail"), true);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // Sneak + empty hand puts a pet on your head
        if (this.isTame() && this.isOwnedBy(player) && player.isSecondaryUseActive() && stack.isEmpty()) {
            if (!this.level().isClientSide && canStayOn(player) && !hasBirdOnHead(player)) {
                this.perchOn(player);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (this.isTame() && this.isOwnedBy(player)) {
            if (!this.level().isClientSide) {
                if (food && this.getHealth() < this.getMaxHealth()) {
                    this.heal(2.0F);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                } else if (food && this.getAge() == 0 && this.canFallInLove()) {
                    this.setInLove(player);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                } else {
                    boolean sit = !this.isOrderedToSit();
                    this.setOrderedToSit(sit);
                    this.getNavigation().stop();
                    player.displayClientMessage(Component.translatable(
                            sit ? "message.novamterram.songbird.waiting" : "message.novamterram.songbird.following"), true);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            if (this.singTicks > 0) this.singTicks--;
            return;
        }

        // 1. Wild birds flutter away from anyone who walks up without food. Sneak to get close.
        if (!this.isTame() && this.tickCount % 10 == 0 && (this.onGround() || this.isPassenger())) {
            Player near = this.level().getNearestPlayer(this, 3.0D);
            if (near != null && !near.isSpectator() && !near.isShiftKeyDown()
                    && !this.isFood(near.getMainHandItem()) && !this.isFood(near.getOffhandItem())) {
                this.scatterFlock(near.position());
            }
        }

        // Pets warn their owner about monsters, even while sitting
        if (this.isTame() && this.tickCount >= this.nextAlarmCheck) {
            this.nextAlarmCheck = this.tickCount + 40;
            this.checkForDanger();
        }

        // Wild birds head for the trees when a fox or ocelot gets close
        if (!this.isTame() && this.tickCount % 20 == 0 && !this.isFleeing()) {
            List<Mob> predators = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(6.0D),
                    mob -> SongbirdPredators.isWildPredator(mob) && this.hasLineOfSight(mob));
            if (!predators.isEmpty()) {
                this.fleeUntil = this.tickCount + 200;
                this.scatterFlock(predators.get(0).position());
            }
        }

        this.singNextNote();
        this.joinFaunChoir();

        // 2. Morning chorus
        if (this.level().getDayTime() % 24000 < 1000 && !this.isRoosting() && this.random.nextInt(240) == 0) {
            this.sing();
        }

        // 3. Dismount if Golem is in danger, or now and then
        if (this.getVehicle() instanceof IronGolem golem
                && (golem.getTarget() != null || this.random.nextInt(1000) == 0)) {
            this.stopRiding();
        }
    }

    // --- DANGER ALARM ---

    private void checkForDanger() {
        if (this.tickCount < this.alarmReadyAt) return;
        if (!(this.getOwner() instanceof Player owner) || this.distanceToSqr(owner) > 256.0D) return;

        // Line of sight stops it going off because of monsters in caves underneath
        List<Mob> threats = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(12.0D),
                mob -> mob instanceof Enemy && mob.isAlive()
                        && !(mob instanceof NeutralMob && mob.getTarget() == null) // calm endermen etc.
                        && this.hasLineOfSight(mob));
        if (threats.isEmpty()) return;

        for (Mob threat : threats) {
            threat.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false), this);
        }
        this.chirp(); // a sharp call rather than a song
        owner.displayClientMessage(Component.translatable("message.novamterram.songbird.alarm"), true);
        this.alarmReadyAt = this.tickCount + 300;
    }

    // --- FLOCKING & ROOSTING ---

    List<SongbirdEntity> nearbyWildBirds(double range) {
        return this.level().getEntitiesOfClass(SongbirdEntity.class, this.getBoundingBox().inflate(range),
                other -> other != this && other.isAlive() && !other.isTame());
    }

    /** This bird and the wild birds around it all take off. */
    private void scatterFlock(Vec3 from) {
        for (SongbirdEntity other : this.nearbyWildBirds(6.0D)) {
            other.flutterFrom(from);
        }
        this.flutterFrom(from);
    }

    private void flutterFrom(Vec3 from) {
        if (!this.isTame()) this.fleeUntil = Math.max(this.fleeUntil, this.tickCount + 60);
        this.startledUntil = this.tickCount + 200;
        this.setRoosting(false);
        this.stopRiding();
        Vec3 away = this.position().subtract(from).multiply(1, 0, 1).normalize().scale(0.3);
        this.setDeltaMovement(away.x, 0.4, away.z);
    }

    boolean wasRecentlyStartled() {
        return this.tickCount < this.startledUntil;
    }

    public boolean isRoosting() {
        return this.entityData.get(DATA_ROOSTING);
    }

    void setRoosting(boolean roosting) {
        this.entityData.set(DATA_ROOSTING, roosting);
    }

    boolean isFleeing() {
        return this.tickCount < this.fleeUntil;
    }

    // Hurting one wild bird sends the whole group up
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide) {
            this.hopOff();
            this.startledUntil = this.tickCount + 200;
            this.setRoosting(false);
            if (!this.isTame()) {
                Vec3 from = source.getSourcePosition() != null ? source.getSourcePosition() : this.position();
                for (SongbirdEntity other : this.nearbyWildBirds(8.0D)) {
                    other.flutterFrom(from);
                }
            }
        }
        return hurt;
    }

    // --- SINGING & SOUNDS ---

    /** One chirp with the head-up animation and a note particle. */
    void chirp() {
        this.playSound(SoundEvents.PARROT_AMBIENT, 1.0F, this.getVoicePitch());
        this.level().broadcastEntityEvent(this, SING_EVENT);
    }

    /** Starts this colour's whistled tune. The notes are played one by one in singNextNote. */
    void sing() {
        if (this.songNote >= 0) return;
        this.songNote = 0;
        this.nextNoteAt = this.tickCount;
        this.level().broadcastEntityEvent(this, SING_EVENT);
    }

    private void singNextNote() {
        if (this.songNote < 0 || this.tickCount < this.nextNoteAt) return;
        SongbirdColor color = this.getSongbirdColor();
        if (this.songNote >= color.songLength()) {
            this.songNote = -1;
            return;
        }
        // A vanilla note block flute, which sounds like a whistle at high pitch
        this.playSound(SoundEvents.NOTE_BLOCK_FLUTE.value(), 0.5F, color.note(this.songNote));
        this.songNote++;
        this.nextNoteAt = this.tickCount + color.beat();
    }

    // Birds near a calm Faun take turns singing
    private void joinFaunChoir() {
        // Every 10 seconds, each bird starting at one of five slightly different moments
        long turn = Math.floorMod(this.getId(), 5) * 16L;
        if (this.level().getGameTime() % 200L != turn || this.isRoosting() || this.isFleeing()) return;
        if (!this.level().getEntitiesOfClass(FaunEntity.class, this.getBoundingBox().inflate(8.0D),
                faun -> faun.isAlive() && !faun.isHiding()).isEmpty()) {
            this.sing();
        }
    }

    // --- HEAD PERCH ---

    public boolean isOnHead() {
        return this.entityData.get(DATA_HEAD_OWNER) >= 0;
    }

    /** The player wearing this bird, or null. Works on both client and server. */
    @Nullable
    public Player getHeadOwner() {
        int id = this.entityData.get(DATA_HEAD_OWNER);
        return id >= 0 && this.level().getEntity(id) instanceof Player player ? player : null;
    }

    /** How far above the player's feet the bird sits. */
    public static double headHeight(Player player) {
        return player.getBbHeight() + 0.07D;
    }

    private static boolean canStayOn(Player player) {
        return player.isAlive() && !player.isSpectator() && !player.isSleeping() && !player.isFallFlying()
                && !player.isPassenger() && !player.isSwimming() && !player.isUnderWater() && !player.isInvisible();
    }

    private boolean hasBirdOnHead(Player player) {
        return !this.level().getEntitiesOfClass(SongbirdEntity.class, player.getBoundingBox().inflate(2.0D),
                bird -> bird.getHeadOwner() == player).isEmpty();
    }

    private void perchOn(Player player) {
        this.stopRiding();
        this.setOrderedToSit(false);
        this.getNavigation().stop();
        this.wearerWasSneaking = true; // they are sneaking right now
        this.lastSneakPress = -100;
        this.entityData.set(DATA_HEAD_OWNER, player.getId());
        player.displayClientMessage(Component.translatable("message.novamterram.songbird.on_head"), true);
    }

    private void hopOff() {
        if (!this.isOnHead()) return;
        Player player = this.getHeadOwner();
        this.entityData.set(DATA_HEAD_OWNER, -1);
        this.noPhysics = false;
        if (player != null) {
            // Inside the player's own space, so it can't end up stuck in a ceiling
            this.setPos(player.getX(), player.getY() + 1.0D, player.getZ());
        }
    }

    @Override
    public void tick() {
        this.noPhysics = this.isOnHead();
        super.tick();
        if (!this.isOnHead()) return;

        Player player = this.getHeadOwner();
        if (!this.level().isClientSide) {
            if (player == null || !canStayOn(player) || !this.isOwnedBy(player)) {
                this.hopOff();
                return;
            }
            // Two quick sneaks make it hop off
            boolean sneaking = player.isShiftKeyDown();
            if (sneaking && !this.wearerWasSneaking) {
                if (this.tickCount - this.lastSneakPress <= 10) {
                    this.hopOff();
                    return;
                }
                this.lastSneakPress = this.tickCount;
            }
            this.wearerWasSneaking = sneaking;
        }
        if (player == null) return;

        this.setPos(player.getX(), player.getY() + headHeight(player), player.getZ());
        this.setDeltaMovement(Vec3.ZERO);
        this.fallDistance = 0.0F;
        this.setYRot(player.getYHeadRot());
        this.yBodyRot = player.getYHeadRot();
        this.yHeadRot = player.getYHeadRot();
    }

    // While on a head: no walking, no gravity, and clicks and arrows pass through it
    @Override
    protected boolean isImmobile() {
        return this.isOnHead() || super.isImmobile();
    }

    @Override
    public boolean isNoGravity() {
        return this.isOnHead() || super.isNoGravity();
    }

    @Override
    public boolean isPickable() {
        return !this.isOnHead() && super.isPickable();
    }

    @Override
    public boolean isPushable() {
        return !this.isOnHead() && super.isPushable();
    }

    @Override
    protected void doPush(Entity entity) {
        if (!this.isOnHead()) super.doPush(entity);
    }

    // Stops pandas and the like from grabbing a bird off your head
    @Override
    public boolean startRiding(Entity vehicle, boolean force) {
        return !this.isOnHead() && super.startRiding(vehicle, force);
    }

    public int getSingTicks() {
        return this.singTicks;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == SING_EVENT) {
            this.singTicks = 20;
            this.level().addParticle(ParticleTypes.NOTE, this.getX(), this.getY() + 0.6D, this.getZ(),
                    this.random.nextDouble(), 0.0D, 0.0D); // first speed value picks the note colour
        } else {
            super.handleEntityEvent(id);
        }
    }

    // Plain chirps: vanilla parrots sometimes copy monster noises here
    @Override
    public SoundEvent getAmbientSound() {
        return this.isRoosting() ? null : SoundEvents.PARROT_AMBIENT; // quiet while asleep
    }

    // Higher pitched than a parrot, and a little different for each colour
    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 1.45F * this.getSongbirdColor().voice();
    }

    // --- CUSTOM AI GOAL CLASSES ---

    /** Goal: Find an Iron Golem and physically sit on its head. */
    static class PerchOnGolemGoal extends Goal {
        private final SongbirdEntity bird;
        private IronGolem golem;
        private int timeLeft;

        public PerchOnGolemGoal(SongbirdEntity bird) {
            this.bird = bird;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        private boolean canPerchOn(IronGolem target) {
            return target.isAlive() && !target.isVehicle() && target.getTarget() == null;
        }

        @Override
        public boolean canUse() {
            if (this.bird.isPassenger() || this.bird.isOrderedToSit() || this.bird.isRoosting()) return false;
            if (this.bird.getRandom().nextInt(100) != 0) return false;

            List<IronGolem> list = this.bird.level().getEntitiesOfClass(IronGolem.class,
                    this.bird.getBoundingBox().inflate(10.0D), this::canPerchOn);
            if (list.isEmpty()) return false;
            this.golem = list.get(0);
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.timeLeft > 0 && !this.bird.isPassenger() && !this.bird.isOrderedToSit()
                    && canPerchOn(this.golem);
        }

        @Override
        public void start() {
            this.timeLeft = this.adjustedTickDelay(200);
        }

        @Override
        public void stop() {
            this.bird.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.timeLeft--;
            // Aim for the top of the golem, not its feet
            this.bird.getNavigation().moveTo(this.golem.getX(), this.golem.getY() + this.golem.getBbHeight(),
                    this.golem.getZ(), 1.2D);
            if (this.bird.getBoundingBox().inflate(1.0D).intersects(this.golem.getBoundingBox())) {
                this.bird.startRiding(this.golem, true);
            }
        }
    }

    /** Goal: Prefer landing on blocks tagged with #minecraft:leaves. */
    static class SongbirdLeafPerchGoal extends MoveToBlockGoal {
        private final SongbirdEntity bird;

        public SongbirdLeafPerchGoal(SongbirdEntity bird, double speed) {
            // The last number lets it look up and down for leaves, not just at its own height
            super(bird, speed, 12, 6);
            this.bird = bird;
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            return level.getBlockState(pos).is(BlockTags.LEAVES) && level.isEmptyBlock(pos.above());
        }

        @Override
        public boolean canUse() {
            return !this.bird.isOrderedToSit() && !this.bird.isPassenger() && super.canUse();
        }
    }

    /** Goal: Follow Faun entities. */
    static class FollowFaunGoal extends Goal {
        private final SongbirdEntity bird;
        private LivingEntity faun;
        private final double speed;
        private final float minDist;
        private final float maxDist;
        private int repathDelay;

        public FollowFaunGoal(SongbirdEntity bird, double speed, float min, float max) {
            this.bird = bird;
            this.speed = speed;
            this.minDist = min;
            this.maxDist = max;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.bird.isOrderedToSit() || this.bird.isPassenger() || this.bird.isRoosting()) return false;
            if (this.bird.getRandom().nextInt(10) != 0) return false;

            List<FaunEntity> list = this.bird.level().getEntitiesOfClass(FaunEntity.class,
                    this.bird.getBoundingBox().inflate(16.0D));
            if (list.isEmpty()) return false;
            this.faun = list.get(0);
            return this.bird.distanceToSqr(this.faun) > maxDist * maxDist;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.bird.isOrderedToSit() && !this.bird.isPassenger() && this.faun.isAlive()
                    && this.bird.distanceToSqr(this.faun) > minDist * minDist
                    && this.bird.distanceToSqr(this.faun) < 256.0D;
        }

        @Override
        public void start() {
            this.repathDelay = 0;
        }

        @Override
        public void stop() {
            this.bird.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (--this.repathDelay > 0) return;
            this.repathDelay = this.adjustedTickDelay(10);
            this.bird.getNavigation().moveTo(this.faun, this.speed);
        }
    }

    /** Goal: get under cover when it rains. */
    static class SongbirdShelterGoal extends MoveToBlockGoal {
        private final SongbirdEntity bird;

        public SongbirdShelterGoal(SongbirdEntity bird) {
            super(bird, 1.2D, 12, 4);
            this.bird = bird;
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            // Something to stand on, open space above it, and a roof over that space
            return !level.getBlockState(pos).isAir() && level.isEmptyBlock(pos.above())
                    && !level.canSeeSky(pos.above());
        }

        @Override
        public boolean canUse() {
            return this.bird.level().isRaining() && !this.bird.isOrderedToSit() && !this.bird.isPassenger()
                    && super.canUse();
        }
    }
}
