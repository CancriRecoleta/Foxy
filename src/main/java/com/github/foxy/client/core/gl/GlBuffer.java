package com.github.foxy.client.core.gl;

import org.lwjgl.system.MemoryUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.lwjgl.opengl.ARBSparseBuffer.GL_SPARSE_STORAGE_BIT_ARB;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.GL_UNPACK_SKIP_PIXELS;
import static org.lwjgl.opengl.GL11C.GL_UNPACK_SKIP_ROWS;
import static org.lwjgl.opengl.GL11C.glPixelStorei;
import static org.lwjgl.opengl.GL30C.GL_R8UI;
import static org.lwjgl.opengl.GL30C.GL_R32UI;
import static org.lwjgl.opengl.GL30C.GL_RED_INTEGER;
import static org.lwjgl.opengl.GL45C.glCreateBuffers;
import static org.lwjgl.opengl.GL45C.glDeleteBuffers;
import static org.lwjgl.opengl.GL45C.glNamedBufferStorage;
import static org.lwjgl.opengl.GL45C.nglClearNamedBufferData;
import static org.lwjgl.opengl.GL45C.nglClearNamedBufferSubData;

/**
 * DSA wrapper around an immutable storage GL buffer ({@code glCreateBuffers} +
 * {@code glNamedBufferStorage}).
 *
 * <p>Storage flags are passed through to {@code glNamedBufferStorage} unchanged; common
 * combinations include {@code GL_DYNAMIC_STORAGE_BIT} for CPU-writable buffers and
 * {@code GL_SPARSE_STORAGE_BIT_ARB} for sparse-bound terrain buffers.</p>
 *
 * <p>Tracks live-buffer count and total bytes globally so the renderer can show GL
 * memory usage in F3 / debug overlays.</p>
 */
public final class GlBuffer extends GlObject {

    private static final AtomicInteger COUNT = new AtomicInteger();
    private static final AtomicLong TOTAL_SIZE = new AtomicLong();

    /** Native scratch slot for {@link #fill(int)}; allocated once at class init. */
    private static final long FILL_SCRATCH = MemoryUtil.nmemAlloc(4);

    /** Underlying GL buffer name. Public so renderer code can call raw GL on it. */
    public final int id;
    private final long size;
    private final int flags;

    /** Allocates an uninitialised buffer with no storage flags. */
    public GlBuffer(long size) {
        this(size, 0, true);
    }

    /** Allocates a buffer and zeroes it (unless sparse). */
    public GlBuffer(long size, int flags) {
        this(size, flags, true);
    }

    public GlBuffer(long size, boolean zero) {
        this(size, 0, zero);
    }

    /**
     * Allocates a buffer with the given storage {@code flags}; if {@code zero} is
     * {@code true} and the buffer is non-sparse, also memsets it to zero.
     */
    public GlBuffer(long size, int flags, boolean zero) {
        this.id = glCreateBuffers();
        this.size = size;
        this.flags = flags;
        glNamedBufferStorage(this.id, size, flags);
        if (zero && (flags & GL_SPARSE_STORAGE_BIT_ARB) == 0) {
            zero();
        }
        COUNT.incrementAndGet();
        TOTAL_SIZE.addAndGet(size);
    }

    @Override
    public void free() {
        free0();
        glDeleteBuffers(this.id);
        COUNT.decrementAndGet();
        TOTAL_SIZE.addAndGet(-this.size);
    }

    /** Buffer size in bytes. */
    public long size() { return this.size; }

    public static int getCount() { return COUNT.get(); }

    public static long getTotalSize() { return TOTAL_SIZE.get(); }

    /** Whether this buffer was allocated with {@code GL_SPARSE_STORAGE_BIT_ARB}. */
    public boolean isSparse() { return (this.flags & GL_SPARSE_STORAGE_BIT_ARB) != 0; }

    /** Zero-fills the entire buffer. */
    public GlBuffer zero() {
        nglClearNamedBufferData(this.id, GL_R8UI, GL_RED_INTEGER, GL_UNSIGNED_BYTE, 0L);
        return this;
    }

    /** Zero-fills bytes {@code [offset, offset + size)}. */
    public GlBuffer zeroRange(long offset, long size) {
        nglClearNamedBufferSubData(this.id, GL_R8UI, offset, size, GL_RED_INTEGER, GL_UNSIGNED_BYTE, 0L);
        return this;
    }

    /**
     * Fills the buffer with the 32-bit pattern {@code value}. Defensively resets a
     * Mesa pixel-store quirk that historically affected {@code glClearBufferData} on
     * Intel/IRIS Mesa drivers (Mesa commit a5c3c452).
     */
    public GlBuffer fill(int value) {
        glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
        glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
        MemoryUtil.memPutInt(FILL_SCRATCH, value);
        nglClearNamedBufferData(this.id, GL_R32UI, GL_RED_INTEGER, GL_UNSIGNED_INT, FILL_SCRATCH);
        return this;
    }

    /** Optional debug label; routed through {@link GlDebug}. */
    public GlBuffer name(String label) { return GlDebug.name(label, this); }

    /** Number of live {@link GlBuffer} instances. */
    public static int liveCount() { return COUNT.get(); }
    /** Total bytes held by live {@link GlBuffer} instances. */
    public static long liveBytes() { return TOTAL_SIZE.get(); }
}
