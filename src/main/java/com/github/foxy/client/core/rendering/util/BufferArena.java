package com.github.foxy.client.core.rendering.util;

import com.github.foxy.client.core.gl.Capabilities;
import com.github.foxy.client.core.gl.GlBuffer;
import com.github.foxy.common.util.AllocationArena;
import com.github.foxy.common.util.MemoryBuffer;
import com.github.foxy.common.util.UnsafeUtil;
import com.github.foxy.commonImpl.FoxyCommon;

import java.util.function.Consumer;

/**
 * Element-aligned bump arena over a single {@link GlBuffer}.
 *
 * <h2>What it solves</h2>
 * <p>The renderer's geometry / draw-indirect data is uploaded as fixed-size records
 * (one section's worth of vertex data, one MDIC command, etc.). A {@code BufferArena}
 * carves a single big GPU buffer into element-sized slots and hands callers an integer
 * offset; downstream draws then index into the buffer by that offset. The slot
 * allocator is {@link AllocationArena} sized in <em>elements</em>, not bytes, so an
 * external SSBO binding with {@code .stride * elementOffset} works without further
 * arithmetic on the GPU side.</p>
 *
 * <h2>Capacity</h2>
 * <p>{@code capacity} must be a multiple of {@code elementSize}; the underlying
 * {@link GlBuffer} is allocated once with that capacity and never resized. A debug
 * flag ({@code -Dfoxy.checkSSBOMaxSize=true}) verifies the requested capacity fits in
 * the driver's reported maximum SSBO binding range &mdash; useful when tuning buffer
 * sizes for a new GPU.</p>
 *
 * <h2>Usage</h2>
 * <ul>
 *   <li>{@link #upload(MemoryBuffer)} writes one or more elements through
 *       {@link UploadStream} and returns the element offset, or {@code -1} if no slot
 *       fits.</li>
 *   <li>{@link #free(long)} releases a previously-allocated slot.</li>
 *   <li>{@link #downloadRemove(long, Consumer)} reads a slot back via
 *       {@link DownloadStream} and frees it once the download has been queued.</li>
 *   <li>{@link #usage()} reports the fraction of slots currently in use.</li>
 * </ul>
 *
 * <p>Cleanroom note: same algorithmic shape as upstream Voxy; the cleanroom rewrite
 * uses {@link Math#toIntExact} for the {@code long → int} narrowing on
 * {@link AllocationArena#free} (which returns {@code long} in the cleanroom port) and
 * adds full English javadoc.</p>
 */
public final class BufferArena {

    /** Verifies requested capacity against the GL driver's max SSBO binding size. */
    private static final boolean CHECK_SSBO_MAX_SIZE_CHECK =
            FoxyCommon.isVerificationFlagOn("checkSSBOMaxSize");

    private final int elementSize;
    private final GlBuffer buffer;
    private final AllocationArena allocationMap = new AllocationArena();
    private long used;

    /**
     * @param capacity    bytes of GPU storage; must be a positive multiple of
     *                    {@code elementSize}
     * @param elementSize size of one record in bytes; the arena hands out slots
     *                    counted in these units
     */
    public BufferArena(long capacity, int elementSize) {
        if (elementSize <= 0) {
            throw new IllegalArgumentException("elementSize must be > 0");
        }
        if (capacity % elementSize != 0L) {
            throw new IllegalArgumentException("capacity (" + capacity
                    + ") must be a multiple of elementSize (" + elementSize + ")");
        }
        if (CHECK_SSBO_MAX_SIZE_CHECK && capacity > Capabilities.INSTANCE.ssboMaxSize) {
            throw new IllegalArgumentException("BufferArena capacity " + capacity
                    + " exceeds driver SSBO max " + Capabilities.INSTANCE.ssboMaxSize);
        }
        this.elementSize = elementSize;
        this.buffer = new GlBuffer(capacity);
        this.allocationMap.setLimit(capacity / elementSize);
    }

    /**
     * Reserves a slot of {@code source.size / elementSize} elements, copies the caller's
     * bytes through {@link UploadStream}, and returns the element offset. Returns
     * {@code -1} when no contiguous run of free slots is large enough.
     */
    public long upload(MemoryBuffer source) {
        if (source.size % this.elementSize != 0L) {
            throw new IllegalArgumentException("source size " + source.size
                    + " not a multiple of elementSize " + this.elementSize);
        }
        long elementCount = source.size / this.elementSize;
        long elementOffset = this.allocationMap.alloc(elementCount);
        if (elementOffset == AllocationArena.SIZE_LIMIT) return -1L;

        long uploadPtr = UploadStream.INSTANCE.upload(this.buffer,
                elementOffset * this.elementSize, source.size);
        UnsafeUtil.memcpy(source.address, uploadPtr, source.size);
        this.used += elementCount;
        return elementOffset;
    }

    /** Releases the slot at {@code allocation} previously returned by {@link #upload}. */
    public void free(long allocation) {
        this.used -= this.allocationMap.free(allocation);
    }

    /** Tears down the underlying {@link GlBuffer}; the arena is unusable afterwards. */
    public void free() {
        this.buffer.free();
    }

    /** Underlying GL buffer name; throws if the buffer has already been freed. */
    public int id() {
        this.buffer.assertNotFreed();
        return this.buffer.id;
    }

    /** Slot occupancy as a fraction of total capacity, in {@code [0, 1]}. */
    public float usage() {
        long totalSlots = this.buffer.size() / this.elementSize;
        if (totalSlots == 0L) return 0f;
        return (float) ((double) this.used / (double) totalSlots);
    }

    /** Bytes currently allocated to live slots. */
    public long getUsedBytes() {
        return this.used * this.elementSize;
    }

    /**
     * Queues a download of the slot's contents into {@link DownloadStream}'s mapped
     * buffer and frees the slot immediately after the queue accepts the request.
     * The {@code consumer} runs on the render thread once the download fence signals.
     *
     * <p>{@code AllocationArena.free} returns a {@code long}; element counts here are
     * bounded well below {@link Integer#MAX_VALUE}, so {@link Math#toIntExact} surfaces
     * any future invariant violation immediately rather than truncating silently.</p>
     */
    public void downloadRemove(long allocation, Consumer<MemoryBuffer> consumer) {
        int elementCount = Math.toIntExact(this.allocationMap.free(allocation));
        this.used -= elementCount;
        DownloadStream.INSTANCE.download(this.buffer,
                allocation * this.elementSize,
                (long) elementCount * this.elementSize,
                consumer);
    }
}
