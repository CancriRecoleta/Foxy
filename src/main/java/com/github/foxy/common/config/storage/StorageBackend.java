package com.github.foxy.common.config.storage;

import com.github.foxy.common.config.IMappingStorage;
import com.github.foxy.common.config.IStoredSectionPositionIterator;
import com.github.foxy.common.util.MemoryBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract persistent store for voxelized world data.
 *
 * <p>A backend owns two parallel namespaces:</p>
 * <ul>
 *   <li><b>Section data</b> &mdash; opaque byte blobs keyed by a packed (level, x, y, z)
 *       long. Used for serialized {@link com.github.foxy.common.voxelization.VoxelizedSection
 *       VoxelizedSection} payloads.</li>
 *   <li><b>Id mappings</b> (inherited from {@link IMappingStorage}) &mdash; serialized
 *       block-state and biome registry entries.</li>
 * </ul>
 *
 * <p>Implementations may delegate to one or more child backends (e.g. an L1 in-memory
 * cache in front of a disk-backed store); {@link #getChildBackends()} exposes that tree
 * for diagnostics and lifecycle plumbing.</p>
 */
public abstract class StorageBackend implements IMappingStorage, IStoredSectionPositionIterator {

    /**
     * Reads the section blob stored under {@code key} into the supplied scratch buffer.
     *
     * <p>Implementations may return {@code scratch} (truncated to the actual payload size)
     * or {@code null} if no entry exists for {@code key}. The caller never frees the
     * scratch buffer through this method.</p>
     */
    public abstract MemoryBuffer getSectionData(long key, MemoryBuffer scratch);

    /** Writes (or overwrites) the section blob for {@code key}. The buffer is copied. */
    public abstract void setSectionData(long key, MemoryBuffer data);

    /** Removes the section blob for {@code key}, if any. */
    public abstract void deleteSectionData(long key);

    /** Forces buffered writes to durable storage. */
    @Override public abstract void flush();

    /** Releases backend-owned native resources. */
    @Override public abstract void close();

    /** Direct child backends (e.g. underlying disk store behind an in-memory cache). */
    public List<StorageBackend> getChildBackends() { return List.of(); }

    /** Recursive flatten of {@code this} plus every transitive child backend. */
    public final List<StorageBackend> collectAllBackends() {
        var out = new ArrayList<StorageBackend>();
        out.add(this);
        for (var child : getChildBackends()) {
            out.addAll(child.collectAllBackends());
        }
        return out;
    }
}
