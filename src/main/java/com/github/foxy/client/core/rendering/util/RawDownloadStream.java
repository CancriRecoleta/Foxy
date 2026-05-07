package com.github.foxy.client.core.rendering.util;

import com.github.foxy.client.core.gl.GlFence;
import com.github.foxy.client.core.gl.GlPersistentMappedBuffer;
import com.github.foxy.common.Logger;
import com.github.foxy.common.util.AllocationArena;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import static com.github.foxy.common.util.AllocationArena.SIZE_LIMIT;
import static org.lwjgl.opengl.GL11C.glFinish;
import static org.lwjgl.opengl.GL30C.GL_MAP_READ_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;

/**
 * Lower-level sibling of {@link DownloadStream} that hands out raw native pointers
 * into a coherent persistent-mapped buffer.
 *
 * <h2>Difference from {@link DownloadStream}</h2>
 * <p>{@link DownloadStream} owns the GL copy machinery: callers tell it &quot;pull
 * bytes {@code [a, b)} of GL buffer {@code X} into staging&quot;. {@link RawDownloadStream}
 * is the opposite &mdash; the caller has already arranged for whatever GL command they
 * want (e.g. {@code glReadPixels} into the staging address, or a compute shader
 * writing through a binding) and just needs:</p>
 * <ol>
 *   <li>a slot in the staging buffer to write into,</li>
 *   <li>a fence-gated callback that fires once the GPU is done writing.</li>
 * </ol>
 *
 * <p>Coherent mapping is enabled so callers don't need an explicit {@code glFlushMapped*}
 * after their write; the down-side is mildly slower per-byte throughput on some
 * NVIDIA drivers, but the simpler API is worth it for the use cases this stream
 * targets (debug printf injectors, compute shader output drains, occasional
 * occlusion-result reads).</p>
 *
 * <h2>Lifecycle</h2>
 * <p>Constructed with an explicit size; the caller owns the instance and frees it via
 * {@link #free()}. There is no shared singleton because different subsystems want
 * differently-sized buffers (the printf injector needs only a few KB, the depth-pyramid
 * download path wants megabytes).</p>
 *
 * <p>Cleanroom note: same shape as upstream, with English javadoc, AllocationArena's
 * long-return narrowed via {@link Math#toIntExact}, and a {@link Deque}-based queue of
 * pending fragments instead of upstream's {@code ArrayList} for slightly cleaner
 * pop semantics in {@link #submit()}.</p>
 */
public final class RawDownloadStream {

    /** Result callback invoked once the staging slot's data is GPU-visible to the CPU. */
    @FunctionalInterface
    public interface IDownloadCompletedCallback {
        /** {@code ptr} is valid only inside this call's body. */
        void accept(long ptr);
    }

    private record DownloadFragment(int allocation, IDownloadCompletedCallback callback) {}
    private record DownloadFrame(GlFence fence, DownloadFragment[] fragments) {}

    private final GlPersistentMappedBuffer downloadBuffer;
    private final AllocationArena allocationArena = new AllocationArena();
    private final ArrayList<DownloadFragment> currentFrameFragments = new ArrayList<>();
    private final Deque<DownloadFrame> frames = new ArrayDeque<>();

    /** Allocates a {@code size}-byte coherent mapping. */
    public RawDownloadStream(int size) {
        this.downloadBuffer = new GlPersistentMappedBuffer(size, GL_MAP_READ_BIT | GL_MAP_COHERENT_BIT)
                .name("foxy.RawDownloadStream");
        this.allocationArena.setLimit(size);
    }

    /**
     * Reserves {@code size} bytes in the staging buffer; returns the byte offset the
     * caller should pass to its GL operation. The {@code callback} runs once the
     * download fence has signalled (after the next {@link #submit()} + {@link #tick()}).
     *
     * <p>If the arena cannot satisfy the request, the stream force-flushes (glFinish
     * + tick) once and tries again; if still no fit, throws.</p>
     */
    public int download(int size, IDownloadCompletedCallback callback) {
        long allocation = this.allocationArena.alloc(size);
        if (allocation == SIZE_LIMIT) {
            Logger.warn("RawDownloadStream full; force-flushing");
            glFinish();
            tick();
            allocation = this.allocationArena.alloc(size);
            if (allocation == SIZE_LIMIT) {
                throw new IllegalStateException("RawDownloadStream cannot satisfy " + size
                        + " bytes after force flush");
            }
        }
        // Buffer size is bounded by the int constructor argument; allocations therefore
        // never exceed Integer.MAX_VALUE, but toIntExact is a cheap belt-and-braces check.
        int allocationInt = Math.toIntExact(allocation);
        this.currentFrameFragments.add(new DownloadFragment(allocationInt, callback));
        return allocationInt;
    }

    /**
     * Closes the current frame: snapshots its fragments behind a fresh {@link GlFence}
     * and starts a new accumulator. After this call, every pointer handed out for the
     * just-snapshotted fragments is logically invalid until the matching callback fires.
     */
    public void submit() {
        if (this.currentFrameFragments.isEmpty()) return;
        DownloadFragment[] fragments = this.currentFrameFragments.toArray(new DownloadFragment[0]);
        this.currentFrameFragments.clear();
        this.frames.add(new DownloadFrame(new GlFence(), fragments));
    }

    /**
     * Per-frame: emits a fence for any in-progress fragments and retires every frame
     * at the head whose fence has signalled (running its callbacks and freeing its
     * staging slots).
     */
    public void tick() {
        submit();
        while (!this.frames.isEmpty()) {
            DownloadFrame head = this.frames.peek();
            if (!head.fence.signaled()) break;
            this.frames.poll();
            for (DownloadFragment fragment : head.fragments) {
                fragment.callback.accept(this.downloadBuffer.addr() + fragment.allocation);
                this.allocationArena.free(fragment.allocation);
            }
            head.fence.free();
        }
    }

    /** Underlying GL buffer name. */
    public int getBufferId() { return this.downloadBuffer.id; }

    /**
     * Tears the stream down: drains every queued fragment, releases every fence,
     * unmaps the staging buffer. Safe to call from shutdown paths.
     */
    public void free() {
        glFinish();
        tick();
        GlFence sentinel = new GlFence();
        while (!sentinel.signaled()) {
            glFinish();
        }
        sentinel.free();
        tick();
        if (!this.frames.isEmpty()) {
            throw new IllegalStateException("RawDownloadStream had pending frames after free()");
        }
        this.downloadBuffer.free();
    }
}
