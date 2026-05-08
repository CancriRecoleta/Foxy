package com.github.foxy.client.core.rendering;

import com.github.foxy.client.core.AbstractRenderPipeline;
import com.github.foxy.client.core.RenderProperties;
import com.github.foxy.client.core.gl.GlBuffer;
import com.github.foxy.client.core.gl.GlVertexArray;
import com.github.foxy.client.core.gl.shader.AutoBindingShader;
import com.github.foxy.client.core.gl.shader.Shader;
import com.github.foxy.client.core.gl.shader.ShaderLoader;
import com.github.foxy.client.core.gl.shader.ShaderType;
import com.github.foxy.client.core.rendering.util.SharedIndexBuffer;
import com.github.foxy.client.core.rendering.util.UploadStream;
import com.github.foxy.common.Logger;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11C.GL_CCW;
import static org.lwjgl.opengl.GL11C.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11C.GL_CW;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glDepthFunc;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL11C.glFrontFace;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL42C.glDrawElementsInstancedBaseInstance;
import static org.lwjgl.opengl.GL45C.glCopyNamedBufferSubData;

/**
 * Debug-render subsystem that draws an axis-aligned box around every loaded
 * chunk-section.
 *
 * <h2>What it draws</h2>
 * <p>One unit-cube per (x, z) chunk-position; the box is positioned and sized in the
 * vertex shader from the position-buffer SSBO. The rasteriser uses the depth-bound
 * FBO as the colour-less render target so the result is just a depth-buffer
 * mask of "where chunks are loaded" &mdash; useful for post-FX overlays and as an
 * input to occlusion tests.</p>
 *
 * <h2>Position buffer + handle table</h2>
 * <p>Two parallel structures keep the on-GPU and on-CPU views in sync:</p>
 * <ul>
 *   <li>{@link #chunk2idx} &mdash; chunk position (long) &rarr; slot index in the GPU buffer.</li>
 *   <li>{@link #idx2chunk} &mdash; slot index &rarr; chunk position; lets {@link #_remPos}
 *       compact the heap by moving the last entry into the freed slot.</li>
 * </ul>
 * The GPU buffer is grown geometrically when full; an SSBO rebinding keeps the
 * shader's view current.
 *
 * <h2>Add / remove queues</h2>
 * <p>External code calls {@link #addSection(long)} and {@link #removeSection(long)};
 * both routes through symmetric queues so a same-frame add-then-remove (or vice
 * versa) cancels out without touching GL state. The queues drain inside
 * {@link #render(Viewport)}.</p>
 *
 * <h2>Cleanroom note</h2>
 * <p>Same algorithm and shader as upstream Voxy. The cleanroom rewrite tightens
 * the static-import set, replaces the {@code ARBDirectStateAccess} import with
 * {@link org.lwjgl.opengl.GL45C}, names the magic numbers used in the batched draw
 * ({@code 32} per instance, {@code 6 * 2 * 3} indices per cube), and adds full
 * English javadoc.</p>
 */
public class ChunkBoundRenderer {

    /** Initial slot count in the chunk position buffer; grows geometrically when full. */
    private static final int INIT_MAX_CHUNK_COUNT = 1 << 12;

    /** Cubes per instance batch in {@code glDrawElementsInstanced}. */
    private static final int CUBES_PER_BATCH = 32;

    /** Indices per cube (6 faces × 2 triangles × 3 indices). */
    private static final int INDICES_PER_CUBE = 6 * 2 * 3;

    private GlBuffer chunkPosBuffer = new GlBuffer((long) INIT_MAX_CHUNK_COUNT * 8); // ivec2 per slot
    private final GlBuffer uniformBuffer = new GlBuffer(128);
    private final Long2IntOpenHashMap chunk2idx = new Long2IntOpenHashMap(INIT_MAX_CHUNK_COUNT);
    private long[] idx2chunk = new long[INIT_MAX_CHUNK_COUNT];

    private final Shader rasterShader;
    private final RenderProperties properties;
    private final AbstractRenderPipeline pipeline;

    private final LongOpenHashSet addQueue = new LongOpenHashSet();
    private final LongOpenHashSet remQueue = new LongOpenHashSet();

    public ChunkBoundRenderer(AbstractRenderPipeline pipeline) {
        this.chunk2idx.defaultReturnValue(-1);
        this.properties = pipeline.properties;

        // The vertex shader reads TAA jitter via the pipeline if available; the
        // boolean is plumbed through a #define so the same source compiles in both modes.
        String vertSource = ShaderLoader.parse("Foxy:chunkoutline/outline.vsh");
        String taa = pipeline.taaFunction("getTAA");
        boolean useTaa = taa != null;
        if (useTaa) {
            this.pipeline = pipeline;
            vertSource = vertSource + "\n\n\n" + taa;
        } else {
            this.pipeline = null;
        }

        this.rasterShader = Shader.makeAuto()
                .addSource(ShaderType.VERTEX, vertSource)
                .defineIf("TAA", useTaa)
                .add(ShaderType.FRAGMENT, "Foxy:chunkoutline/outline.fsh")
                .apply(this.properties::apply)
                .compile()
                .ubo(0, this.uniformBuffer)
                .ssbo(1, this.chunkPosBuffer);
    }

    public void addSection(long pos) {
        if (!this.remQueue.remove(pos)) {
            this.addQueue.add(pos);
        }
    }

    public void removeSection(long pos) {
        if (!this.addQueue.remove(pos)) {
            this.remQueue.add(pos);
        }
    }

    /**
     * Drains pending add / remove queues, uploads the per-frame uniform block and
     * issues the batched indirect draws.
     *
     * <p>Touches and then restores GL state for: front-face winding, cull face,
     * depth test, depth function, vertex array binding, framebuffer binding.</p>
     */
    public void render(Viewport<?> viewport) {
        // Drain removes first so the addQueue isn't immediately compared against
        // stale slot indices.
        if (!this.remQueue.isEmpty()) {
            boolean wasEmpty = this.chunk2idx.isEmpty();
            this.remQueue.forEach(this::_remPos);
            this.remQueue.clear();
            if (this.chunk2idx.isEmpty() && !wasEmpty) {
                viewport.depthBoundingBuffer.clear(this.properties.inverseClearDepth());
            }
        }

        if (this.chunk2idx.isEmpty() && this.addQueue.isEmpty()) return;
        viewport.depthBoundingBuffer.clear(this.properties.inverseClearDepth());

        // ---- per-frame uniform upload --------------------------------------------------
        long ptr = UploadStream.INSTANCE.upload(this.uniformBuffer, 0, 128);
        long matPtr = ptr;
        ptr += 4 * 4 * 4;

        final float renderDistanceBlocks = Minecraft.getInstance().options.getEffectiveRenderDistance() * 16f;
        int bx = (int) viewport.cameraX;
        int by = (int) viewport.cameraY;
        int bz = (int) viewport.cameraZ;
        new Vector3i(bx, by, bz).getToAddress(ptr);
        ptr += 4 * 4;

        var negInnerBlock = new Vector3f(
                (float) (viewport.cameraX - bx),
                (float) (viewport.cameraY - by),
                (float) (viewport.cameraZ - bz));
        negInnerBlock.getToAddress(ptr);
        ptr += 4 * 3;

        viewport.MVP.translate(negInnerBlock.negate(), new Matrix4f()).getToAddress(matPtr);
        MemoryUtil.memPutFloat(ptr, renderDistanceBlocks);

        UploadStream.INSTANCE.commit();

        // ---- pre-draw GL state ---------------------------------------------------------
        // Reverse winding so we render the AABB's back faces (frontface gets occluded
        // by anything inside the box, which is what the depth pass wants).
        glFrontFace(GL_CW);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(this.properties.furtherDepthCompare());

        glBindVertexArray(GlVertexArray.STATIC_VAO);
        viewport.depthBoundingBuffer.bind();
        this.rasterShader.bind();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, SharedIndexBuffer.INSTANCE_BB_BYTE.id());
        if (this.pipeline != null) this.pipeline.bindUniforms();

        int count = this.chunk2idx.size();
        if (count >= CUBES_PER_BATCH) {
            glDrawElementsInstanced(GL_TRIANGLES, INDICES_PER_CUBE * CUBES_PER_BATCH,
                    GL_UNSIGNED_BYTE, 0L, count / CUBES_PER_BATCH);
        }
        int leftover = count % CUBES_PER_BATCH;
        if (leftover != 0) {
            glDrawElementsInstancedBaseInstance(GL_TRIANGLES, INDICES_PER_CUBE * leftover,
                    GL_UNSIGNED_BYTE, 0L, 1, (count / CUBES_PER_BATCH) * CUBES_PER_BATCH);
        }

        // ---- restore GL state ----------------------------------------------------------
        glFrontFace(GL_CCW);
        glDepthFunc(this.properties.closerEqualDepthCompare());
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        // Drain adds last so newly-added slots are uploaded after the main draw used the
        // previous frame's state.
        if (!this.addQueue.isEmpty()) {
            this.addQueue.forEach(this::_addPos);
            this.addQueue.clear();
            UploadStream.INSTANCE.commit();
        }
    }

    /**
     * Removes {@code pos} from the heap, moving the last entry into its slot to keep
     * {@link #idx2chunk} dense (so {@link #chunk2idx#size} is the heap watermark).
     */
    private void _remPos(long pos) {
        int idx = this.chunk2idx.remove(pos);
        if (idx == -1) {
            Logger.warn("ChunkBoundRenderer: tried to remove unknown chunk " + pos);
            return;
        }
        if (idx == this.chunk2idx.size()) {
            // Already at the end; the heap stays compact for free.
            return;
        }
        if (this.idx2chunk[idx] != pos) {
            throw new IllegalStateException("ChunkBoundRenderer: idx2chunk inconsistency at slot " + idx);
        }
        // Move the last entry into the freed slot.
        long endPos = this.idx2chunk[this.chunk2idx.size()];
        if (this.chunk2idx.put(endPos, idx) == -1) {
            throw new IllegalStateException("ChunkBoundRenderer: couldn't relocate end pos " + endPos);
        }
        this.idx2chunk[idx] = endPos;
        put(idx, endPos);
    }

    /** Appends {@code pos} to the heap, growing the position buffer when needed. */
    private void _addPos(long pos) {
        if (this.chunk2idx.containsKey(pos)) {
            Logger.warn("ChunkBoundRenderer: tried to add already-tracked chunk " + pos);
            return;
        }
        ensureCapacity();
        int idx = this.chunk2idx.size();
        this.chunk2idx.put(pos, idx);
        this.idx2chunk[idx] = pos;
        put(idx, pos);
    }

    /** Grows the GPU buffer + idx2chunk array by 1.5x when full; rebinds the shader's SSBO. */
    private void ensureCapacity() {
        if (this.chunk2idx.size() < this.idx2chunk.length) return;
        UploadStream.INSTANCE.commit();

        int newSize = (int) (this.idx2chunk.length * 1.5);
        Logger.info("ChunkBoundRenderer: resizing position buffer to " + newSize + " slots");
        var oldBuf = this.chunkPosBuffer;
        this.chunkPosBuffer = new GlBuffer((long) newSize * 8L);
        glCopyNamedBufferSubData(oldBuf.id, this.chunkPosBuffer.id, 0L, 0L, oldBuf.size());
        oldBuf.free();

        long[] oldArr = this.idx2chunk;
        this.idx2chunk = new long[newSize];
        System.arraycopy(oldArr, 0, this.idx2chunk, 0, oldArr.length);

        ((AutoBindingShader) this.rasterShader).ssbo(1, this.chunkPosBuffer);
    }

    /** Writes one (x, z) ivec2 entry into slot {@code idx} of the GPU buffer. */
    private void put(int idx, long pos) {
        long ptr = UploadStream.INSTANCE.upload(this.chunkPosBuffer, 8L * idx, 8L);
        MemoryUtil.memPutInt(ptr, (int) (pos & 0xFFFFFFFFL));
        MemoryUtil.memPutInt(ptr + 4, (int) ((pos >>> 32) & 0xFFFFFFFFL));
    }

    /** Drops every tracked chunk; the GPU buffer keeps its allocation. */
    public void reset() {
        this.chunk2idx.clear();
    }

    /** Tears down the shader and both buffers. */
    public void free() {
        this.rasterShader.free();
        this.uniformBuffer.free();
        this.chunkPosBuffer.free();
    }
}
