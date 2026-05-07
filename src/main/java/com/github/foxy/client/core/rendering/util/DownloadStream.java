package com.github.foxy.client.core.rendering.util;

import com.github.foxy.client.core.gl.GlBuffer;
import com.github.foxy.client.core.gl.GlFence;
import com.github.foxy.client.core.gl.GlPersistentMappedBuffer;
import com.github.foxy.common.Logger;
import com.github.foxy.common.util.AllocationArena;
import com.github.foxy.common.util.MemoryBuffer;
import it.unimi.dsi.fastutil.longs.LongArrayList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.function.Consumer;

import static com.github.foxy.common.util.AllocationArena.SIZE_LIMIT;
import static org.lwjgl.opengl.GL11C.glFinish;
import static org.lwjgl.opengl.GL30C.GL_MAP_READ_BIT;
import static org.lwjgl.opengl.GL42C.GL_BUFFER_UPDATE_BARRIER_BIT;
import static org.lwjgl.opengl.GL42C.glMemoryBarrier;
import static org.lwjgl.opengl.GL44.GL_CLIENT_MAPPED_BUFFER_BARRIER_BIT;
import static org.lwjgl.opengl.GL45C.glCopyNamedBufferSubData;

/**
 * GPU&rarr;CPU streaming reader backed by a single persistent-mapped buffer.
 *
 * <h2>Pipeline</h2>
 * <p>Each {@link #download(GlBuffer, long, long, DownloadResultConsumer) download}
 * call:</p>
 * <ol>
 *   <li>reserves {@code size} bytes of staging space from {@link AllocationArena},</li>
 *   <li>queues a {@code glCopyNamedBufferSubData} from the source GL buffer into that
 *       staging slot,</li>
 *   <li>holds the result behind a {@link GlFence} until {@link #tick()} sees the fence
 *       signal, at which point the caller's {@code resultConsumer} runs against the
 *       persistent mapping.</li>
 * </ol>
 *
 * <h2>Frame retirement</h2>
 * <p>Allocations are grouped per call to {@link #commit()}. {@link #tick()} dequeues
 * the head frame whose fence has signalled, fires its callbacks, and frees its
 * staging slots. Frames are retired in submission order, since later fences cannot
 * signal before earlier ones do.</p>
 *
 * <h2>Lifecycle</h2>
 * <p>Initialise the singleton via {@link #init(long)} after the GL context exists
 * (typically inside Forge's {@code FMLClientSetupEvent}); the legacy
 * {@code DownloadStream.INSTANCE} field is populated by {@link #init} so existing
 * upstream call sites keep working.</p>
 *
 * <p>Cleanroom note: same algorithmic shape as upstream Voxy. The cleanroom rewrite
 * lifts the singleton out of static initialisation (which would have triggered GL
 * traffic at class-load time) and adds full English javadoc; the public method
 * surface and the {@code INSTANCE} field name are preserved so renderer code that
 * pre-existed compiles unchanged.</p>
 */
public final class DownloadStream {

    /** Single global instance, populated by {@link #init(long)}. */
    public static volatile DownloadStream INSTANCE;

    /** 32 MiB default; matches upstream's compile-time choice. */
    public static final long DEFAULT_DOWNLOAD_BUFFER_SIZE = 1L << 25;

    /**
     * Allocates the singleton with a download buffer of {@code size} bytes. Must run
     * on the render thread after a GL context exists; idempotency-checked.
     */
    public static synchronized DownloadStream init(long size) {
        if (INSTANCE != null) {
            throw new IllegalStateException("DownloadStream already initialised");
        }
        INSTANCE = new DownloadStream(size);
        return INSTANCE;
    }

    /** Convenience: initialise with {@link #DEFAULT_DOWNLOAD_BUFFER_SIZE}. */
    public static DownloadStream initDefault() { return init(DEFAULT_DOWNLOAD_BUFFER_SIZE); }

    /**
     * Returns the singleton; throws if {@link #init} hasn't run.
     * Prefer this over the {@link #INSTANCE} field for new call sites.
     */
    public static DownloadStream instance() {
        DownloadStream s = INSTANCE;
        if (s == null) throw new IllegalStateException("DownloadStream.init() not yet called");
        return s;
    }

    /** Result callback invoked once the download's fence has signalled. */
    @FunctionalInterface
    public interface DownloadResultConsumer {
        /** {@code ptr} is valid for {@code size} bytes only inside this call's body. */
        void consume(long ptr, long size);
    }

    private final AllocationArena allocationArena = new AllocationArena();
    private final GlPersistentMappedBuffer downloadBuffer;

    private final Deque<DownloadFrame> frames = new ArrayDeque<>();
    private final LongArrayList thisFrameAllocations = new LongArrayList();
    private final Deque<DownloadData> downloadList = new ArrayDeque<>();
    private final ArrayList<DownloadData> thisFrameDownloadList = new ArrayList<>();

    /** Cursor into the currently-extending allocation; {@code -1} when none is open. */
    private long currentAddr = -1L;
    /** Bytes already reserved in the currently-extending allocation. */
    private long currentOffset;

    private DownloadStream(long size) {
        this.downloadBuffer = new GlPersistentMappedBuffer(size, GL_MAP_READ_BIT)
                .name("foxy.DownloadStream");
        this.allocationArena.setLimit(size);
    }

    // ---- public download API ----------------------------------------------------------

    /** Pulls the entire buffer; result consumer sees a raw native pointer. */
    public void download(GlBuffer buffer, DownloadResultConsumer consumer) {
        download(buffer, 0L, buffer.size(), consumer);
    }

    /** Pulls the entire buffer; result consumer sees a {@link MemoryBuffer} wrapper. */
    public void download(GlBuffer buffer, Consumer<MemoryBuffer> consumer) {
        download(buffer, 0L, buffer.size(), consumer);
    }

    /** Pulls a sub-range; result consumer sees a {@link MemoryBuffer} wrapper. */
    public void download(GlBuffer buffer, long downloadOffset, long size, Consumer<MemoryBuffer> consumer) {
        download(buffer, downloadOffset, size, (ptr, sz) -> {
            consumer.accept(MemoryBuffer.createUntrackedUnfreeableRawFrom(ptr, sz));
        });
    }

    /**
     * Pulls a sub-range; result consumer is the raw {@link DownloadResultConsumer}.
     *
     * <p>Note the auto-commit at the end: every download call closes the staging slot
     * immediately so subsequent calls don't accidentally pile onto the same fence.
     * The TODO from upstream about lifting this auto-commit is preserved for now;
     * later work can opt in to grouped commits where it pays off.</p>
     */
    public void download(GlBuffer buffer, long downloadOffset, long size, DownloadResultConsumer consumer) {
        if (size <= 0L) throw new IllegalArgumentException("size must be > 0");
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("size " + size + " exceeds Integer.MAX_VALUE");
        }
        if (downloadOffset + size > buffer.size()) {
            throw new IllegalArgumentException("download range [" + downloadOffset + ", "
                    + (downloadOffset + size) + ") exceeds source buffer size " + buffer.size());
        }

        long addr = reserveStaging(size);
        this.downloadList.add(new DownloadData(buffer, addr, downloadOffset, size, consumer));
        commit();
    }

    private long reserveStaging(long size) {
        long addr;
        if (this.currentAddr == -1L || !this.allocationArena.expand(this.currentAddr, size)) {
            this.currentAddr = this.allocationArena.alloc(size);
            if (this.currentAddr == SIZE_LIMIT) {
                Logger.warn("DownloadStream full; preemptively flushing — caller may stall");
                commit();
                int attempts = 10;
                while (--attempts != 0 && this.currentAddr == SIZE_LIMIT) {
                    glFinish();
                    tick();
                    this.currentAddr = this.allocationArena.alloc(size);
                }
                if (this.currentAddr == SIZE_LIMIT) {
                    throw new IllegalStateException("DownloadStream cannot satisfy " + size
                            + " bytes after force flush");
                }
            }
            this.thisFrameAllocations.add(this.currentAddr);
            this.currentOffset = size;
            addr = this.currentAddr;
        } else {
            addr = this.currentAddr + this.currentOffset;
            this.currentOffset += size;
        }
        if (this.currentAddr + size > this.downloadBuffer.size()) {
            throw new IllegalStateException("DownloadStream allocation overflowed staging buffer");
        }
        return addr;
    }

    // ---- commit / tick ---------------------------------------------------------------

    /**
     * Issues the queued {@code glCopyBufferSubData}s into the staging buffer. Frames
     * are not yet retired here — that's {@link #tick}'s job — but the GL copy is
     * pushed so the GPU can start work.
     */
    public void commit() {
        if (this.downloadList.isEmpty()) return;

        glMemoryBarrier(GL_BUFFER_UPDATE_BARRIER_BIT);
        for (DownloadData entry : this.downloadList) {
            glCopyNamedBufferSubData(entry.target.id, this.downloadBuffer.id,
                    entry.targetOffset, entry.downloadStreamOffset, entry.size);
        }
        glMemoryBarrier(GL_CLIENT_MAPPED_BUFFER_BARRIER_BIT | GL_BUFFER_UPDATE_BARRIER_BIT);

        this.thisFrameDownloadList.addAll(this.downloadList);
        this.downloadList.clear();
        this.currentAddr = -1L;
        this.currentOffset = 0L;
    }

    /**
     * Per-frame: commits any pending downloads, snapshots them behind a fence, and
     * retires every signalled frame at the head of the queue (firing its callbacks
     * and freeing its staging slots).
     */
    public void tick() {
        commit();
        if (!this.thisFrameAllocations.isEmpty()) {
            this.frames.add(new DownloadFrame(
                    new GlFence(),
                    new LongArrayList(this.thisFrameAllocations),
                    new ArrayList<>(this.thisFrameDownloadList)));
            this.thisFrameAllocations.clear();
            this.thisFrameDownloadList.clear();
        }

        // Frames are queued in submission order; once we hit an unsignalled fence
        // every later frame is also still in flight.
        while (!this.frames.isEmpty()) {
            DownloadFrame head = this.frames.peek();
            if (!head.fence.signaled()) break;
            this.frames.pop();
            for (DownloadData data : head.data) {
                data.resultConsumer.consume(
                        this.downloadBuffer.addr() + data.downloadStreamOffset,
                        data.size);
            }
            head.allocations.forEach(this.allocationArena::free);
            head.fence.free();
        }
    }

    /**
     * Forces every queued download to drain. Spins until every fence signals. Used by
     * shutdown paths that need to ensure no GPU work is still touching the staging
     * buffer before it gets unmapped.
     */
    public void waitDiscard() {
        glFinish();
        GlFence sentinel = new GlFence();
        glFinish();
        while (!sentinel.signaled()) Thread.onSpinWait();
        sentinel.free();
        while (!this.frames.isEmpty()) {
            DownloadFrame frame = this.frames.pop();
            while (!frame.fence.signaled()) Thread.onSpinWait();
            frame.allocations.forEach(this.allocationArena::free);
            frame.fence.free();
        }
    }

    /**
     * Equivalent to {@link #waitDiscard} followed by a final {@link #tick} so any
     * still-pending callbacks fire. Used by the renderer shutdown path.
     */
    public void flushWaitClear() {
        glFinish();
        tick();
        GlFence sentinel = new GlFence();
        glFinish();
        while (!sentinel.signaled()) {
            glFinish();
            Thread.onSpinWait();
        }
        sentinel.free();
        tick();
        if (!this.frames.isEmpty()) {
            throw new IllegalStateException("DownloadStream had pending frames after flushWaitClear");
        }
    }

    private record DownloadFrame(GlFence fence, LongArrayList allocations, ArrayList<DownloadData> data) {}
    private record DownloadData(GlBuffer target,
                                long downloadStreamOffset,
                                long targetOffset,
                                long size,
                                DownloadResultConsumer resultConsumer) {}
}
