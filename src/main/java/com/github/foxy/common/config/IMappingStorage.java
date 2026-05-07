package com.github.foxy.common.config;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.nio.ByteBuffer;

/**
 * Persistent key-value sink for {@link com.github.foxy.common.world.other.Mapper}'s
 * id-to-payload tables.
 *
 * <p>Keys are 32-bit ints whose top two bits encode the entry kind (block-state vs. biome,
 * see Mapper); values are arbitrary serialized blobs. Implementations may assume that
 * (id, payload) pairs are written at most once and looked up in bulk on startup.</p>
 */
public interface IMappingStorage {
    /** Writes (or overwrites) the mapping entry under {@code id}. */
    void putIdMapping(int id, ByteBuffer data);

    /** Returns a snapshot of all stored id mappings as id&rarr;byte[]. */
    Int2ObjectOpenHashMap<byte[]> getIdMappingsData();

    /** Forces any buffered writes to durable storage. */
    void flush();

    /** Releases any backend resources. */
    void close();
}
