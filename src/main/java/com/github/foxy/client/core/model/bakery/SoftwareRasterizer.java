package com.github.foxy.client.core.model.bakery;

// Embeddium for 1.20.1 keeps ColorMixer under the newer api package even though
// ColorSRGB stayed under the legacy me.jellysquid.* tree.
import net.caffeinemc.mods.sodium.api.util.ColorMixer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Arrays;

import static com.github.foxy.common.util.BitOps.clamp;

/**
 * Pure-CPU software rasteriser used by the model bakery to render block faces into
 * a colour + depth + stencil framebuffer without touching the GL pipeline.
 *
 * <h2>Why not GL</h2>
 * <p>The model bakery wants per-block-state textures available off-thread and
 * deterministic across machines (so two clients building the same atlas hash to
 * identical bytes). GL would require either the render thread or a shared context
 * and would introduce driver variability. A 16&times;16 software rasteriser is
 * fast enough since faces are tiny and the pipeline is only run once per block
 * state at world-load time.</p>
 *
 * <h2>Framebuffer layout</h2>
 * <p>One {@code long[]} of length {@code targetSize * targetSize}. Per-pixel bits:</p>
 * <pre>
 *   bits  0..31  ABGR8 colour (low 32 = result of blending / texture sample)
 *   bit   32     reserved
 *   bit   33     reserved
 *   ...
 *   bit   39     metadata bit ("biome-tinted" flag from {@link ReuseVertexConsumer}'s
 *                  per-vertex meta; carried alongside colour for downstream face
 *                  classification)
 *   bits 40..63  unsigned 24-bit depth (bigger = farther; cleared to all-1s)
 * </pre>
 *
 * <h2>Triangle rasterisation</h2>
 * <p>Standard half-plane test using barycentric coordinates derived from the signed
 * edge function. Backface culling is gated by {@link #setFaceCull}; the second
 * triangle of a quad uses {@code orZero=true} to include exact-edge pixels (avoids
 * gaps along the shared diagonal).</p>
 *
 * <h2>Cleanroom note</h2>
 * <p>Same algorithm and bit layout as upstream Voxy. The cleanroom rewrite renames
 * the scratch vectors with intent-bearing names, uses
 * {@link com.github.foxy.common.util.BitOps#clamp} via static import, drops the
 * unused {@code addRGB} helper and unused {@code ColorABGR}/{@code ColorARGB}
 * imports, drops the dead {@code Random} import, and adds full English javadoc.</p>
 */
public final class SoftwareRasterizer {

    /** Target framebuffer side length, in pixels (square). */
    private final int targetSize;

    /** Packed colour + meta + depth, one entry per pixel; see class javadoc. */
    private final long[] framebuffer;

    /** Bit mask for the depth field within a framebuffer entry. */
    private static final long DEPTH_MASK = ((1L << 24) - 1) << (64 - 24);

    /** Cleared depth + zeroed colour / meta. */
    private static final long CLEAR_VALUE = DEPTH_MASK;

    /** Stencil counter increment per pixel hit; bits 32..36 hold the count. */
    private static final long STENCIL_INCREMENT = 1L << 32;

    /** Bit position of the meta-tinted flag in the framebuffer word. */
    private static final long META_BIT_POSITION = 1L << 39;

    /** Triangles below this absolute area are dropped (degenerate). */
    private static final float DEGENERATE_AREA_EPSILON = 0.001f;

    private boolean cullBackFace;
    private boolean doBlending;

    private int samplerWidth;
    private int samplerHeight;
    private int[] samplerTexture;

    // ---- per-quad scratch state (re-used to avoid GC churn) ---------------------------

    /** Scratch for {@code transformProject}; holds (x, y, z, w). */
    private final Vector4f homogeneousScratch = new Vector4f();

    /** Per-vertex screen-space positions for the current quad. */
    private final Vector3f q0pos = new Vector3f();
    private final Vector3f q1pos = new Vector3f();
    private final Vector3f q2pos = new Vector3f();
    private final Vector3f q3pos = new Vector3f();

    /** Per-vertex (meta, u, v) attributes for the current quad. */
    private final Vector3f q0attr = new Vector3f();
    private final Vector3f q1attr = new Vector3f();
    private final Vector3f q2attr = new Vector3f();
    private final Vector3f q3attr = new Vector3f();

    /** Active triangle's positions during rasterisation. */
    private final Vector3f triA = new Vector3f();
    private final Vector3f triB = new Vector3f();
    private final Vector3f triC = new Vector3f();

    /** Active triangle's attributes during rasterisation. */
    private final Vector3f attrA = new Vector3f();
    private final Vector3f attrB = new Vector3f();
    private final Vector3f attrC = new Vector3f();

    /** Allocates a {@code targetSize}&times;{@code targetSize} framebuffer. */
    public SoftwareRasterizer(int targetSize) {
        this.targetSize = targetSize;
        this.framebuffer = new long[targetSize * targetSize];
    }

    public void setFaceCull(boolean isBackFaceCulling) { this.cullBackFace = isBackFaceCulling; }
    public void setBlending(boolean blending) { this.doBlending = blending; }

    /** Sets the active sampler texture; must match {@code width * height}. */
    public void setSamplerTexture(int[] texture, int width, int height) {
        if (texture.length != width * height) {
            throw new IllegalArgumentException("texture length " + texture.length
                    + " != width * height " + (width * height));
        }
        this.samplerTexture = texture;
        this.samplerWidth = width;
        this.samplerHeight = height;
    }

    /** Nearest-neighbour texture lookup at normalised UV. */
    private int sampleTexture(float u, float v) {
        int pu = clamp(Math.round(u * this.samplerWidth - 0.5f), 0, this.samplerWidth - 1);
        int pv = clamp(Math.round(v * this.samplerHeight - 0.5f), 0, this.samplerHeight - 1);
        return this.samplerTexture[this.samplerWidth * pv + pu];
    }

    /** Clears every pixel to {@link #CLEAR_VALUE}. */
    public void clear() {
        Arrays.fill(this.framebuffer, CLEAR_VALUE);
    }

    /** Convenience: rasterises every quad in {@code vertices}. */
    public void raster(Matrix4f mvp, ReuseVertexConsumer vertices) {
        raster(mvp, vertices.getAddress(), vertices.quadCount());
    }

    /** Rasterises {@code quadCount} quads packed at {@code verticesAddr}. */
    public void raster(Matrix4f mvp, long verticesAddr, int quadCount) {
        if (quadCount == 0) return;
        for (int i = 0; i < quadCount; i++) {
            rasterQuad(mvp, verticesAddr + (long) ReuseVertexConsumer.VERTEX_FORMAT_SIZE * 4L * i);
        }
    }

    private void rasterQuad(Matrix4f transform, long addr) {
        loadTransformPos(transform, addr, 0, this.q0pos, this.q0attr);
        loadTransformPos(transform, addr, 1, this.q1pos, this.q1attr);
        loadTransformPos(transform, addr, 2, this.q2pos, this.q2attr);
        loadTransformPos(transform, addr, 3, this.q3pos, this.q3attr);

        // Fan triangulation 0-1-2 / 2-3-0; second triangle uses orZero so the shared
        // diagonal isn't a fence at exactly-on-edge pixels.
        this.triA.set(this.q0pos); this.triB.set(this.q1pos); this.triC.set(this.q2pos);
        this.attrA.set(this.q0attr); this.attrB.set(this.q1attr); this.attrC.set(this.q2attr);
        rasterTriangle(false);

        this.triA.set(this.q2pos); this.triB.set(this.q3pos); this.triC.set(this.q0pos);
        this.attrA.set(this.q2attr); this.attrB.set(this.q3attr); this.attrC.set(this.q0attr);
        rasterTriangle(true);
    }

    private void rasterTriangle(boolean orZero) {
        Vector3f a = this.triA, b = this.triB, c = this.triC;
        float area = edge(a, b, c);

        // Negative area means counter-clockwise winding; gate culling on that.
        if ((area < 0f) == this.cullBackFace) return;
        if (Math.abs(area) < DEGENERATE_AREA_EPSILON) return;

        int minX = Math.max((int) Math.floor(Math.min(Math.min(a.x, b.x), c.x)), 0);
        int maxX = Math.min((int) Math.ceil(Math.max(Math.max(a.x, b.x), c.x)), this.targetSize - 1);
        int minY = Math.max((int) Math.floor(Math.min(Math.min(a.y, b.y), c.y)), 0);
        int maxY = Math.min((int) Math.ceil(Math.max(Math.max(a.y, b.y), c.y)), this.targetSize - 1);

        float invArea = 1.0f / area;
        for (int py = minY; py <= maxY; py++) {
            for (int px = minX; px <= maxX; px++) {
                float cx = px + 0.5f;
                float cy = py + 0.5f;
                float w1 = edge(b, c, cx, cy) * invArea;
                float w2 = edge(c, a, cx, cy) * invArea;
                float w3 = 1.0f - w1 - w2;
                boolean inside = orZero
                        ? (w1 >= 0f && w2 >= 0f && w3 >= 0f)
                        : (w1 > 0f && w2 > 0f && w3 > 0f);
                if (inside) {
                    rasterPixel(px + py * this.targetSize, w1, w2, w3);
                }
            }
        }
    }

    private void rasterPixel(int index, float w1, float w2, float w3) {
        float z = Math.fma(w1, this.triA.z, Math.fma(w2, this.triB.z, w3 * this.triC.z));
        z = Math.fma(z, 0.5f, 0.5f);
        // Clamp tiny negative values back to 0 to absorb FMA rounding error.
        if (z < 0.0f && -0.000001f <= z) z = 0f;
        if (z < 0.0f || z > 1.0f) return;

        int meta = Float.floatToRawIntBits(this.attrA.x);
        float u = Math.fma(w1, this.attrA.y, Math.fma(w2, this.attrB.y, w3 * this.attrC.y));
        float v = Math.fma(w1, this.attrA.z, Math.fma(w2, this.attrB.z, w3 * this.attrC.z));

        int colour = sampleTexture(u, v);

        // meta bit 0 = "discard on transparent"; per upstream the renderer discards
        // any sample whose alpha is below the cutoff so transparent fragments don't
        // bleed through.
        final int ALPHA_CUTOFF = 0;
        if ((meta & 1) != 0 && (colour >>> 24) <= ALPHA_CUTOFF) return;

        // Stencil increment is unconditional once the discard test passes.
        this.framebuffer[index] += STENCIL_INCREMENT;

        long depthVal = ((long) (((double) z) * ((1 << 24) - 1))) << (64 - 24);
        // Decrement-by-one so a pixel at the absolute clear depth still registers as
        // "drawn" (same as upstream's "We want to render _something_ at least").
        if (depthVal == DEPTH_MASK) depthVal--;

        // Strict less-than depth test (using unsigned compare, since DEPTH_MASK is 0xFFFF...).
        if (Long.compareUnsigned(this.framebuffer[index], depthVal) <= 0) return;

        // Replace depth.
        this.framebuffer[index] &= ~DEPTH_MASK;
        this.framebuffer[index] |= depthVal;

        // Replace meta (carrying bit 2 of the source meta, shifted to bit 39).
        this.framebuffer[index] &= ~META_BIT_POSITION;
        this.framebuffer[index] |= ((long) (meta & 4)) << 37;

        int previousColour = (int) this.framebuffer[index];
        this.framebuffer[index] &= ~Integer.toUnsignedLong(-1);

        if (this.doBlending) {
            colour = doBlending(previousColour, colour);
        }
        this.framebuffer[index] |= Integer.toUnsignedLong(colour);
    }

    /**
     * Approximate matching of the GL state
     * {@code glBlendFuncSeparatei(0, GL_ONE_MINUS_DST_ALPHA, GL_DST_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA)}.
     */
    private static int doBlending(int src, int dst) {
        int srcAlpha = (src >>> 24) & 0xFF;
        if (srcAlpha == 0) return dst;
        int dstAlpha = (dst >>> 24) & 0xFF;
        src &= ~(0xFF << 24);
        dst &= ~(0xFF << 24);
        int blendAlpha = Math.min(0xFF, srcAlpha + ((dstAlpha * (255 - srcAlpha)) >> 8));
        int blendRgb = ColorMixer.mix(dst, src, dstAlpha);
        return blendRgb | (blendAlpha << 24);
    }

    /** Signed 2D edge function (cross product) used for triangle area / inside tests. */
    private static float edge(Vector3f a, Vector3f b, Vector3f c) {
        return (c.x - a.x) * (b.y - a.y) - (c.y - a.y) * (b.x - a.x);
    }

    private static float edge(Vector3f a, Vector3f b, float cx, float cy) {
        return (cx - a.x) * (b.y - a.y) - (cy - a.y) * (b.x - a.x);
    }

    /**
     * Reads the {@code vert}-th vertex of the quad at {@code addr}, applies the MVP,
     * converts to screen space, and writes positions / attributes into the supplied
     * scratch vectors. Throws if the projected w drifts away from 1 (sanity check).
     */
    private void loadTransformPos(Matrix4f transform, long addr, int vert,
                                   Vector3f outPos, Vector3f outAttr) {
        long vertAddr = addr + (long) vert * ReuseVertexConsumer.VERTEX_FORMAT_SIZE;
        this.homogeneousScratch.setFromAddress(vertAddr);
        outAttr.setFromAddress(vertAddr + 3 * 4);
        this.homogeneousScratch.w = 1.0f;
        var projected = transform.transformProject(this.homogeneousScratch);
        if (Math.abs(this.homogeneousScratch.w - 1.0f) > 0.000001f) {
            throw new IllegalStateException("Projected w drifted: " + this.homogeneousScratch.w);
        }
        outPos.set(
                Math.fma(projected.x, 0.5f, 0.5f) * this.targetSize,
                Math.fma(projected.y, 0.5f, 0.5f) * this.targetSize,
                projected.z);
    }

    /** Direct read access to the framebuffer for the bakery's post-processing. */
    public long[] getRawFramebuffer() {
        return this.framebuffer;
    }
}
