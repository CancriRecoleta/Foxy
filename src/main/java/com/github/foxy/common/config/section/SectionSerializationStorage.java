package com.github.foxy.common.config.section;

import com.github.foxy.common.Logger;
import com.github.foxy.common.config.storage.StorageBackend;
import com.github.foxy.common.util.MemoryBuffer;
import com.github.foxy.common.world.SaveLoadSystemV1;
import com.github.foxy.common.world.WorldSection;
import com.github.foxy.common.world.other.Mapper;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.LongConsumer;

/**
 * Concrete {@link SectionStorage} backed by a {@link StorageBackend}.
 *
 * <p>{@link #loadSection} pulls the byte payload from the backend into a thread-local
 * scratch buffer, hands it to {@link SaveLoadSystemV1#deserialize}, and returns the
 * tri-state ({@code -1} / {@code 0} / {@code 1}) the caller expects. {@link #saveSection}
 * round-trips through {@link SaveLoadSystemV1#serialize}.</p>
 *
 * <p>Mapping ids and section position iteration delegate straight to the backend.</p>
 */
public final class SectionSerializationStorage extends SectionStorage {

    /** Scratch buffer per worker thread, sized for the largest possible payload. */
    private static final ThreadLocal<MemoryBuffer> SCRATCH = ThreadLocal.withInitial(
            () -> new MemoryBuffer(SaveLoadSystemV1.MAX_SERIALIZED_SECTION_SIZE));

    private final StorageBackend backend;

    /** Wraps {@code backend} with the LUT-based section (de)serializer. */
    public SectionSerializationStorage(StorageBackend backend) {
        this.backend = backend;
    }

    @Override
    public int loadSection(WorldSection into) {
        var data = this.backend.getSectionData(into.key, SCRATCH.get());
        if (data == null) {
            // No payload yet: caller will treat the section as fresh air.
            return 1;
        }
        if (!SaveLoadSystemV1.deserialize(into, data)) {
            this.backend.deleteSectionData(into.key);
            // Wipe the section to a known state so the renderer doesn't see stale data.
            Arrays.fill(into._unsafeGetRawDataArray(), Mapper.AIR);
            Logger.error("Section " + into.lvl + ", " + into.x + ", " + into.y + ", " + into.z
                    + " failed to deserialize; deleted from store");
            return -1;
        }
        return 0;
    }

    @Override
    public void saveSection(WorldSection section) {
        try (var blob = SaveLoadSystemV1.serialize(section)) {
            this.backend.setSectionData(section.key, blob);
        }
    }

    @Override public void putIdMapping(int id, ByteBuffer data) { this.backend.putIdMapping(id, data); }
    @Override public Int2ObjectOpenHashMap<byte[]> getIdMappingsData() { return this.backend.getIdMappingsData(); }
    @Override public void flush() { this.backend.flush(); }
    @Override public void close() { this.backend.close(); }
    @Override public void iteratePositions(int level, LongConsumer consumer) {
        this.backend.iteratePositions(level, consumer);
    }
}
