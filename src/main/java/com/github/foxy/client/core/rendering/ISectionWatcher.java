package com.github.foxy.client.core.rendering;

import com.github.foxy.common.world.WorldEngine;

/**
 * Subscriber contract for renderer-side interest in section-level world changes.
 *
 * <h2>Watch types</h2>
 * <p>{@code types} is a bitmask of {@code WorldEngine.UPDATE_TYPE_*} flags
 * (block content, child-existence, save-suppression). Subscribers register a
 * specific subset and only receive callbacks for events that intersect their
 * mask. The implementer ({@link SectionUpdateRouter}) tracks the mask per
 * section position so multiple subscribers can co-exist.</p>
 *
 * <h2>Convenience overloads</h2>
 * <p>Each method has a {@code (lvl, x, y, z)} variant that packs into a 64-bit
 * section id via {@link WorldEngine#getWorldSectionId}; the {@code (long position)}
 * variant takes a pre-packed id. Implementers only need to override the long-key
 * version.</p>
 *
 * <p>Cleanroom note: same contract as upstream Voxy with English javadoc.</p>
 */
public interface ISectionWatcher {

    /** Convenience overload of {@link #watch(long, int)} that packs the coordinates. */
    default boolean watch(int lvl, int x, int y, int z, int types) {
        return watch(WorldEngine.getWorldSectionId(lvl, x, y, z), types);
    }

    /**
     * Adds the supplied {@code types} bitmask to the watch set for {@code position}.
     * Returns {@code true} when the call actually changed the stored set.
     */
    boolean watch(long position, int types);

    /** Convenience overload of {@link #unwatch(long, int)} that packs the coordinates. */
    default boolean unwatch(int lvl, int x, int y, int z, int types) {
        return unwatch(WorldEngine.getWorldSectionId(lvl, x, y, z), types);
    }

    /**
     * Removes {@code types} bits from the watch set for {@code position}. Returns
     * {@code true} when the entry was completely removed (no remaining bits set).
     */
    boolean unwatch(long position, int types);

    /** Convenience overload of {@link #get(long)} that packs the coordinates. */
    default int get(int lvl, int x, int y, int z) {
        return get(WorldEngine.getWorldSectionId(lvl, x, y, z));
    }

    /** Returns the current watch bitmask for {@code position}, or 0 when unwatched. */
    int get(long position);
}
