package com.github.foxy.client.core.gl;

import static org.lwjgl.opengl.GL30C.GL_MAP_READ_BIT;
import static org.lwjgl.opengl.GL30C.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL30C.GL_MAP_FLUSH_EXPLICIT_BIT;
import static org.lwjgl.opengl.GL30C.GL_MAP_UNSYNCHRONIZED_BIT;
import static org.lwjgl.opengl.GL44.GL_CLIENT_STORAGE_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;
import static org.lwjgl.opengl.GL45C.glCreateBuffers;
import static org.lwjgl.opengl.GL45C.glDeleteBuffers;
import static org.lwjgl.opengl.GL45C.glNamedBufferStorage;
import static org.lwjgl.opengl.GL45C.glUnmapNamedBuffer;
import static org.lwjgl.opengl.GL45C.nglMapNamedBufferRange;

/**
 * GL buffer allocated with {@code GL_MAP_PERSISTENT_BIT} and immediately
 * persistent-mapped, exposing a stable native pointer in {@link #addr()}.
 *
 * <p>Used for streaming write paths where the CPU keeps writing into the buffer while
 * the GPU is reading earlier ranges. Coherent mapping ({@code GL_MAP_COHERENT_BIT})
 * removes the need for explicit flushing; non-coherent mapping requires the caller to
 * issue {@code glFlushMappedNamedBufferRange} before the GPU is allowed to read.</p>
 *
 * <p>Storage flag mask: only the persistence-related bits ({@code GL_MAP_*_BIT},
 * {@code GL_CLIENT_STORAGE_BIT}) and {@code GL_MAP_PERSISTENT_BIT} are honoured; other
 * caller-supplied bits are filtered out so the storage and map flags stay consistent.</p>
 */
public final class GlPersistentMappedBuffer extends GlObject {

    /** GL buffer name. */
    public final int id;
    private final long size;
    private final long addr;

    /**
     * @param size  buffer size in bytes
     * @param flags any combination of {@code GL_MAP_READ_BIT}, {@code GL_MAP_WRITE_BIT},
     *              {@code GL_MAP_COHERENT_BIT}, {@code GL_MAP_FLUSH_EXPLICIT_BIT},
     *              {@code GL_MAP_UNSYNCHRONIZED_BIT}, {@code GL_CLIENT_STORAGE_BIT}.
     *              {@code GL_MAP_PERSISTENT_BIT} is implicit.
     */
    public GlPersistentMappedBuffer(long size, int flags) {
        this.id = glCreateBuffers();
        this.size = size;

        int storageFlags = GL_MAP_PERSISTENT_BIT
                | (flags & (GL_MAP_COHERENT_BIT | GL_MAP_WRITE_BIT | GL_MAP_READ_BIT | GL_CLIENT_STORAGE_BIT));
        glNamedBufferStorage(this.id, size, storageFlags);

        int mapFlags = GL_MAP_PERSISTENT_BIT
                | (flags & (GL_MAP_WRITE_BIT | GL_MAP_READ_BIT
                        | GL_MAP_UNSYNCHRONIZED_BIT | GL_MAP_FLUSH_EXPLICIT_BIT));
        this.addr = nglMapNamedBufferRange(this.id, 0, size, mapFlags);
    }

    @Override
    public void free() {
        free0();
        glUnmapNamedBuffer(this.id);
        glDeleteBuffers(this.id);
    }

    /** Buffer size in bytes. */
    public long size() { return this.size; }

    /** Native pointer to the start of the persistent mapping; valid until {@link #free()}. */
    public long addr() { return this.addr; }

    /** Optional debug label; routed through {@link GlDebug}. */
    public GlPersistentMappedBuffer name(String label) { return GlDebug.name(label, this); }
}
