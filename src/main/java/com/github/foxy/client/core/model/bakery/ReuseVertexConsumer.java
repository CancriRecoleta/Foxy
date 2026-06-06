package com.github.foxy.client.core.model.bakery;


import com.github.foxy.common.util.MemoryBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.lwjgl.system.MemoryUtil;

import com.mojang.blaze3d.vertex.VertexConsumer;

// Implements the 1.20.1 VertexConsumer interface (vertex/uv/color/.../endVertex), which is the old
// positional API rather than 1.21's fluent addVertex/setColor/setUv. Foxy only consumes geometry
// through quad(), which on 1.20.1 decodes BakedQuad.getVertices() directly (no position(i)/
// packedUV(i) accessors exist here). The interface methods delegate to the same internal writer.
public final class ReuseVertexConsumer implements VertexConsumer {
    public static final int VERTEX_FORMAT_SIZE = 24;
    private MemoryBuffer buffer = new MemoryBuffer(8192);
    private long ptr;
    private int count;
    private int defaultMeta;

    public boolean anyShaded;
    public boolean anyDarkendTex;

    public ReuseVertexConsumer() {
        this.reset();
    }

    public ReuseVertexConsumer setDefaultMeta(int meta) {
        this.defaultMeta = meta;
        return this;
    }

    // Internal writer: advances to the next vertex slot and stores the position + default meta.
    public ReuseVertexConsumer addVertex(float x, float y, float z) {
        this.ensureCanPut();
        this.ptr += VERTEX_FORMAT_SIZE; this.count++; //Goto next vertex
        this.meta(this.defaultMeta);
        MemoryUtil.memPutFloat(this.ptr, x);
        MemoryUtil.memPutFloat(this.ptr + 4, y);
        MemoryUtil.memPutFloat(this.ptr + 8, z);
        return this;
    }

    public ReuseVertexConsumer meta(int metadata) {
        MemoryUtil.memPutInt(this.ptr + 12, metadata);
        return this;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        return this.addVertex((float) x, (float) y, (float) z);
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        MemoryUtil.memPutFloat(this.ptr + 16, u);
        MemoryUtil.memPutFloat(this.ptr + 20, v);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return this;
    }

    @Override
    public void endVertex() {
        // No-op: addVertex/uv write directly, so there is no per-vertex flush step.
    }

    @Override
    public void defaultColor(int red, int green, int blue, int alpha) {
    }

    @Override
    public void unsetDefaultColor() {
    }

    public ReuseVertexConsumer quad(BakedQuad quad, int metadata) {
        this.anyShaded |= quad.isShade();
        this.anyDarkendTex |= false;
        this.ensureCanPut();
        // 1.20.1 BakedQuad packs 4 vertices into an int[]; each vertex starts at i*stride with the
        // position as float bits in [0..2] and the texture UV as float bits in [4] and [5].
        int[] vs = quad.getVertices();
        int stride = vs.length / 4;
        for (int i = 0; i < 4; i++) {
            int b = i * stride;
            this.addVertex(Float.intBitsToFloat(vs[b]), Float.intBitsToFloat(vs[b + 1]), Float.intBitsToFloat(vs[b + 2]));
            this.uv(Float.intBitsToFloat(vs[b + 4]), Float.intBitsToFloat(vs[b + 5]));
            this.meta(metadata);
        }
        return this;
    }

    private void ensureCanPut() {
        if ((long) (this.count + 5) * VERTEX_FORMAT_SIZE < this.buffer.size) {
            return;
        }
        long offset = this.ptr-this.buffer.address;
        //1.5x the size
        var newBuffer = new MemoryBuffer((((int)(this.buffer.size*2)+VERTEX_FORMAT_SIZE-1)/VERTEX_FORMAT_SIZE)*VERTEX_FORMAT_SIZE);
        this.buffer.cpyTo(newBuffer.address);
        this.buffer.free();
        this.buffer = newBuffer;
        this.ptr = offset + newBuffer.address;
    }

    public ReuseVertexConsumer reset() {
        this.anyShaded = false;
        this.anyDarkendTex = false;
        this.defaultMeta = 0;//RESET THE DEFAULT META
        this.count = 0;
        this.ptr = this.buffer.address - VERTEX_FORMAT_SIZE;//the thing is first time this gets incremented by FORMAT_STRIDE
        return this;
    }

    public void free() {
        this.ptr = 0;
        this.count = 0;
        this.buffer.free();
        this.buffer = null;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public int quadCount() {
        if (this.count%4 != 0) throw new IllegalStateException();
        return this.count/4;
    }

    public long getAddress() {
        return this.buffer.address;
    }
}
