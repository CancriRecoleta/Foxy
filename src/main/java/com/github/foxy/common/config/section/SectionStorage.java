package com.github.foxy.common.config.section;

import com.github.foxy.common.config.IMappingStorage;
import com.github.foxy.common.config.IStoredSectionPositionIterator;
import com.github.foxy.common.world.WorldSection;

/**
 * Persistence facade owned by a {@link com.github.foxy.common.world.WorldEngine WorldEngine}.
 *
 * <p>Layers the section-payload load / save API on top of the lower-level
 * {@link com.github.foxy.common.config.storage.StorageBackend StorageBackend}'s opaque
 * blob store. The engine talks to a {@code SectionStorage}; the storage in turn knows how
 * to (de)serialize a {@link WorldSection} against its backend.</p>
 */
public abstract class SectionStorage implements IMappingStorage, IStoredSectionPositionIterator {
    /**
     * Loads section data into {@code into}'s backing array.
     * @return {@code 0} on success, {@code 1} when no payload was found (caller should
     *         treat the section as freshly-allocated air), or {@code -1} on failure.
     */
    public abstract int loadSection(WorldSection into);

    /** Persists {@code section}'s current state to durable storage. */
    public abstract void saveSection(WorldSection section);
}
