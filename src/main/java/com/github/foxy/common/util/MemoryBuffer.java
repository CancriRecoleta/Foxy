package com.github.foxy.common.util;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Owned slice of off-heap memory addressed by a raw pointer.
 *
 * <p>Foxy's storage and voxelization layers shuttle large variable-length blobs between
 * threads without bouncing through {@link ByteBuffer}'s 2&nbsp;GiB / position semantics, so
 * each blob is wrapped in one of these. The address is allocated via
 * {@link MemoryUtil#nmemAlloc(long)} on construction and freed by {@link #free()}; the
 * caller is responsible for calling {@code free()} exactly once.</p>
 *
 * <p>Two static counters expose live-buffer count and total bytes for diagnostics; they
 * are updated for any buffer constructed with the public size constructor.</p>
 */
public final class MemoryBuffer implements AutoCloseable {

    private static final AtomicInteger COUNT = new AtomicInteger();
    private static final AtomicLong TOTAL_SIZE = new AtomicLong();

    /** Off-heap pointer; valid until {@link #free()} is called. */
    public final long address;

    /** Length in bytes of the region rooted at {@link #address}. */
    public final long size;

    private final boolean owns;
    private boolean freed;

    /** Allocates a fresh {@code size}-byte off-heap region. */
    public MemoryBuffer(long size) {
        this.address = MemoryUtil.nmemAlloc(size);
        this.size = size;
        this.owns = true;
        COUNT.incrementAndGet();
        TOTAL_SIZE.addAndGet(size);
    }

    private MemoryBuffer(long address, long size, boolean owns) {
        this.address = address;
        this.size = size;
        this.owns = owns;
    }

    /** Wraps an externally-owned region; {@link #free()} on the result is a no-op. */
    public static MemoryBuffer wrap(long address, long size) {
        return new MemoryBuffer(address, size, false);
    }

    /** Memcpy this buffer's contents to the destination address. */
    public void copyTo(long dst) {
        assertLive();
        MemoryUtil.memCopy(this.address, dst, this.size);
    }

    /** Memcpy {@code size} bytes from a source address into this buffer. */
    public MemoryBuffer copyFrom(long src) {
        assertLive();
        MemoryUtil.memCopy(src, this.address, this.size);
        return this;
    }

    /** Allocates a fresh buffer with the same contents and size. */
    public MemoryBuffer copy() {
        var dup = new MemoryBuffer(this.size);
        copyTo(dup.address);
        return dup;
    }

    /** Zero-fills the entire region. */
    public MemoryBuffer zero() {
        assertLive();
        MemoryUtil.memSet(this.address, 0, this.size);
        return this;
    }

    /** Read-only ByteBuffer view into the region. */
    public ByteBuffer asByteBuffer() {
        assertLive();
        return MemoryUtil.memByteBuffer(this.address, (int) Math.min(this.size, Integer.MAX_VALUE));
    }

    /** Releases the off-heap region; safe to call once on owning buffers. */
    @Override
    public void close() { free(); }

    /** Releases the off-heap region; safe to call once on owning buffers. */
    public void free() {
        if (this.freed) return;
        this.freed = true;
        if (this.owns) {
            MemoryUtil.nmemFree(this.address);
            COUNT.decrementAndGet();
            TOTAL_SIZE.addAndGet(-this.size);
        }
    }

    private void assertLive() {
        if (this.freed) throw new IllegalStateException("MemoryBuffer was already freed");
    }

    /** Live owning-buffer count, for diagnostics. */
    public static int liveCount() { return COUNT.get(); }

    /** Total bytes held by live owning buffers, for diagnostics. */
    public static long liveBytes() { return TOTAL_SIZE.get(); }
}
