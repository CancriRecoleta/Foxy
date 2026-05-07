package com.github.foxy.client.core.model.bakery;

import com.github.foxy.common.util.MemoryBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.lwjgl.system.MemoryUtil;

/**
 * Off-heap vertex accumulator that captures positions / UVs / metadata into a packed
 * 24-byte format.
 *
 * <h2>Vertex format</h2>
 * <pre>
 *   bytes 0..11   position xyz   (3 &times; float)
 *   bytes 12..15  per-vertex metadata (int; defaults to {@link #setDefaultMeta})
 *   bytes 16..23  uv coordinate (2 &times; float)
 * </pre>
 * The native memory backing {@link #buffer} is the renderer's source of truth for the
 * pre-baked vertex stream that gets uploaded to the GPU.
 *
 * <h2>Why this implements {@link VertexConsumer}</h2>
 * <p>Vanilla's {@code BakedQuad}-walking pipeline emits its vertices through the
 * {@link VertexConsumer} interface. Implementing it lets us reuse Minecraft's own
 * walker without having to reimplement quad subdivision / metadata propagation.
 * Most {@link VertexConsumer} hooks (colour, overlay, normal) are intentionally
 * no-ops: the caller-supplied {@link #setDefaultMeta} carries everything we need.</p>
 *
 * <h2>Growth strategy</h2>
 * <p>{@link #ensureCanPut} doubles the buffer when the next 5 vertices wouldn't fit.
 * The 5-vertex slack matches the {@code vertex(...)} call sequence: each vertex
 * advances {@code ptr} by one slot before writing, so we need at least one extra
 * slot beyond the actual write position to leave room for the format-stride math.</p>
 *
 * <p>Cleanroom note: same algorithm and 24-byte format as upstream Voxy with
 * English javadoc and consistent {@code copyTo} naming on the cleanroom MemoryBuffer.</p>
 */
public final class ReuseVertexConsumer implements VertexConsumer {

    /** Bytes per vertex; see class javadoc for layout. */
    public static final int VERTEX_FORMAT_SIZE = 24;

    /** Initial buffer capacity in bytes; will grow on first overflow. */
    private static final int INITIAL_CAPACITY = 8192;

    private MemoryBuffer buffer = new MemoryBuffer(INITIAL_CAPACITY);
    private long ptr;
    private int vertexCount;
    private int defaultMeta;

    /** Set by the {@code quad()} walker when it sees a shaded {@link BakedQuad}. */
    public boolean anyShaded;

    /** Reserved for future texture-darkening detection; currently always {@code false}. */
    public boolean anyDarkendTex;

    public ReuseVertexConsumer() {
        reset();
    }

    /** Sets the metadata bits written into bytes 12..15 of every vertex by default. */
    public ReuseVertexConsumer setDefaultMeta(int meta) {
        this.defaultMeta = meta;
        return this;
    }

    @Override
    public ReuseVertexConsumer vertex(double x, double y, double z) {
        ensureCanPut();
        // Advance to the next 24-byte slot first, then write. The initial reset puts
        // ptr at -VERTEX_FORMAT_SIZE so the first advance lands at offset 0.
        this.ptr += VERTEX_FORMAT_SIZE;
        this.vertexCount++;
        meta(this.defaultMeta);
        MemoryUtil.memPutFloat(this.ptr, (float) x);
        MemoryUtil.memPutFloat(this.ptr + 4, (float) y);
        MemoryUtil.memPutFloat(this.ptr + 8, (float) z);
        return this;
    }

    /** Overwrites the metadata bytes (12..15) of the most recently written vertex. */
    public ReuseVertexConsumer meta(int metadata) {
        MemoryUtil.memPutInt(this.ptr + 12, metadata);
        return this;
    }

    @Override public ReuseVertexConsumer color(int red, int green, int blue, int alpha) { return this; }
    @Override public VertexConsumer color(int packed) { return this; }

    @Override
    public ReuseVertexConsumer uv(float u, float v) {
        MemoryUtil.memPutFloat(this.ptr + 16, u);
        MemoryUtil.memPutFloat(this.ptr + 20, v);
        return this;
    }

    @Override public ReuseVertexConsumer overlayCoords(int u, int v) { return this; }
    @Override public ReuseVertexConsumer uv2(int u, int v) { return this; }
    @Override public ReuseVertexConsumer normal(float x, float y, float z) { return this; }
    @Override public void endVertex() {}
    @Override public void defaultColor(int red, int green, int blue, int alpha) {}
    @Override public void unsetDefaultColor() {}

    /**
     * Walks {@code quad}'s 4 vertices and emits them to this consumer. The vanilla
     * vertex array packs positions, packed colour, UV and lightmap into successive
     * floats; we read positions and UVs only.
     */
    public ReuseVertexConsumer quad(BakedQuad quad, int metadata) {
        this.anyShaded |= quad.isShade();
        this.anyDarkendTex = false;
        ensureCanPut();
        int[] vertices = quad.getVertices();
        for (int i = 0; i < 4; i++) {
            int base = i * 8;
            vertex(
                    Float.intBitsToFloat(vertices[base]),
                    Float.intBitsToFloat(vertices[base + 1]),
                    Float.intBitsToFloat(vertices[base + 2])
            );
            uv(
                    Float.intBitsToFloat(vertices[base + 4]),
                    Float.intBitsToFloat(vertices[base + 5])
            );
            meta(metadata);
        }
        return this;
    }

    /** Doubles the backing buffer when it cannot fit 5 more vertices. */
    private void ensureCanPut() {
        if ((long) (this.vertexCount + 5) * VERTEX_FORMAT_SIZE < this.buffer.size) {
            return;
        }
        long offset = this.ptr - this.buffer.address;
        long newSize = (((long) this.buffer.size * 2 + VERTEX_FORMAT_SIZE - 1)
                / VERTEX_FORMAT_SIZE) * VERTEX_FORMAT_SIZE;
        MemoryBuffer next = new MemoryBuffer(newSize);
        this.buffer.copyTo(next.address);
        this.buffer.free();
        this.buffer = next;
        this.ptr = offset + next.address;
    }

    /** Resets the accumulator to empty without freeing the backing buffer. */
    public ReuseVertexConsumer reset() {
        this.anyShaded = false;
        this.anyDarkendTex = false;
        this.defaultMeta = 0;
        this.vertexCount = 0;
        // Park ptr one slot before the buffer start so the first vertex() call lands
        // at offset 0 after the +VERTEX_FORMAT_SIZE advance.
        this.ptr = this.buffer.address - VERTEX_FORMAT_SIZE;
        return this;
    }

    /** Releases the off-heap buffer; the consumer is unusable afterwards. */
    public void free() {
        this.ptr = 0L;
        this.vertexCount = 0;
        this.buffer.free();
        this.buffer = null;
    }

    /** {@code true} when no vertices have been written since the last {@link #reset}. */
    public boolean isEmpty() { return this.vertexCount == 0; }

    /** Number of complete quads (vertex count must be a multiple of 4). */
    public int quadCount() {
        if (this.vertexCount % 4 != 0) {
            throw new IllegalStateException("Vertex count " + this.vertexCount + " is not a multiple of 4");
        }
        return this.vertexCount / 4;
    }

    /** Native start address of the backing buffer; valid until {@link #free}. */
    public long getAddress() { return this.buffer.address; }
}
