package net.candyperfumeboy.novamterram.util;

import java.util.Collection;
import java.util.UUID;

/**
 * INTFox
 *
 * Interface applied to ALL Fox instances (vanilla and custom) via FoxMixin,
 * and implemented directly by AbstractNTFox for custom fox variants.
 *
 * Exposes vanilla Fox's package-private trust system and adds the NT sit flag.
 *
 * NOTE: Fox.DefendTrustedTargetGoal is package-private. If AbstractNTFox uses it
 * directly, you need an access transformer entry:
 *   public net.minecraft.world.entity.animal.Fox$DefendTrustedTargetGoal
 * Check your accesstransformers.cfg / at.cfg if you get an illegal access error.
 */
public interface INTFox {

    /** Returns true if this fox has been ordered to sit by a trusted player. */
    boolean nt$isOrderedToSit();

    /** Sets the sit state. Implementations must sync to clients via SynchedEntityData. */
    void nt$setOrderedToSit(boolean sitting);

    /** Returns true if the given UUID is in this fox's trust list. */
    boolean nt$trusts(UUID uuid);

    /**
     * Adds a UUID to this fox's trust list.
     * Vanilla Fox has only two trust slots (DATA_TRUSTED_ID_0, DATA_TRUSTED_ID_1).
     * If both are occupied the call is silently ignored by vanilla logic.
     * Always add the breeding player UUID FIRST to ensure they claim a slot.
     */
    void nt$addTrustedUUID(UUID uuid);

    /** Returns all currently trusted UUIDs (up to 2 for vanilla foxes). */
    Collection<UUID> nt$getTrustedUUIDs();
}