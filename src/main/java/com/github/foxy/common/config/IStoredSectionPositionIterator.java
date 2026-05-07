package com.github.foxy.common.config;

import java.util.function.LongConsumer;

/**
 * Iterator contract for enumerating section keys held by a storage backend.
 *
 * <p>Section keys are produced by
 * {@code com.github.foxy.common.world.WorldEngine#getWorldSectionId(int, int, int, int)}
 * and pack a (level, x, y, z) tuple into a single long. The {@code level} parameter on
 * {@link #iteratePositions(int, LongConsumer)} filters by LOD level; pass {@code -1} to
 * receive every key regardless of level.</p>
 */
public interface IStoredSectionPositionIterator {
    /** Calls {@code callback} once per stored section key matching {@code level}. */
    void iteratePositions(int level, LongConsumer callback);
}
