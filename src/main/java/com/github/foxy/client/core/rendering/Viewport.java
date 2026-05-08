package com.github.foxy.client.core.rendering;

import com.github.foxy.client.compat.FogParameters;
import com.github.foxy.client.core.RenderProperties;
import com.github.foxy.client.core.gl.GlBuffer;
import com.github.foxy.client.core.rendering.util.DepthFramebuffer;
import com.github.foxy.client.core.rendering.util.HiZBuffer;
import net.minecraft.util.Mth;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import java.lang.reflect.Field;

/**
 * Per-pass camera state container plus its owned HiZ pyramid and depth-bound buffer.
 *
 * <h2>What it owns</h2>
 * <ul>
 *   <li>{@link #hiZBuffer} &mdash; rebuilt per frame from the current depth target.</li>
 *   <li>{@link #depthBoundingBuffer} &mdash; depth-only FBO whose attachment is
 *       resized lazily by {@link #update}.</li>
 *   <li>The MVP and frustum derived from {@link #projection} &times; {@link #modelView},
 *       plus the camera-relative {@link #section} / {@link #innerTranslation}
 *       split used to keep float precision usable far from the world origin.</li>
 * </ul>
 *
 * <h2>Far-from-origin precision</h2>
 * <p>32&times;32-block sections (LOD-0 sized) anchor a section-grid coordinate; the
 * camera position is split into ({@code section} integer, {@code innerTranslation}
 * float) so render code can subtract the section from world coords on the CPU and
 * then translate by the float remainder on the GPU without losing precision once
 * the player travels millions of blocks from spawn.</p>
 *
 * <h2>Frustum-planes reflective hack</h2>
 * <p>JOML's {@link FrustumIntersection} keeps its plane equations in a private
 * field; the renderer needs direct access for HiZ bounds calculations, so we grab
 * the field reflectively at construction. Same trick as upstream Voxy.</p>
 *
 * <h2>Cleanroom note</h2>
 * <p>Same fields and contract as upstream Voxy. The cleanroom rewrite makes the
 * abstract class final-on-{@code delete}, drops the commented-out HiZBuffer2
 * alternative, narrows imports, and adds full English javadoc.</p>
 */
public abstract class Viewport<A extends Viewport<A>> {

    /** Hierarchical-Z occlusion pyramid, rebuilt per frame from the depth attachment. */
    public final HiZBuffer hiZBuffer;

    /** Depth-only FBO for occlusion testing chunk bounds. */
    public final DepthFramebuffer depthBoundingBuffer = new DepthFramebuffer();

    /** Reflective handle for {@link FrustumIntersection}'s {@code planes} field. */
    private static final Field PLANES_FIELD;
    static {
        try {
            PLANES_FIELD = FrustumIntersection.class.getDeclaredField("planes");
            PLANES_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("JOML changed FrustumIntersection.planes; Viewport's reflective hack needs an update", e);
        }
    }

    public int width;
    public int height;
    public int frameId;

    /** Vanilla projection matrix; supplied by the bootstrap injector for shader compat. */
    public Matrix4f vanillaProjection = new Matrix4f();

    /** Foxy's projection matrix used to drive the actual draw. */
    public Matrix4f projection = new Matrix4f();

    public Matrix4f modelView = new Matrix4f();

    public final FrustumIntersection frustum = new FrustumIntersection();

    /** Planes of {@link #frustum} as a Vector4 array; populated reflectively. */
    public final Vector4f[] frustumPlanes;

    public double cameraX;
    public double cameraY;
    public double cameraZ;

    public FogParameters fogParameters;

    /** Cached {@link #projection} &times; {@link #modelView}. */
    public final Matrix4f MVP = new Matrix4f();

    /** Section-grid coordinates of the camera; see class javadoc on far-from-origin precision. */
    public final Vector3i section = new Vector3i();

    /** Camera position minus its section-anchored origin, in float-precision world space. */
    public final Vector3f innerTranslation = new Vector3f();

    private final RenderProperties properties;

    protected Viewport(RenderProperties properties) {
        Vector4f[] planes;
        try {
            planes = (Vector4f[]) PLANES_FIELD.get(this.frustum);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not read FrustumIntersection.planes reflectively", e);
        }
        this.frustumPlanes = planes;
        this.properties = properties;
        this.hiZBuffer = new HiZBuffer(properties);
    }

    /** Called by the renderer when this viewport is no longer needed. */
    public final void delete() {
        delete0();
    }

    /** Subclass hook for additional teardown. Always invokes the base class teardown first. */
    protected void delete0() {
        this.hiZBuffer.free();
        this.depthBoundingBuffer.free();
    }

    @SuppressWarnings("unchecked")
    public A setVanillaProjection(Matrix4fc projection) {
        this.vanillaProjection.set(projection);
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    public A setProjection(Matrix4f projection) {
        this.projection = projection;
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    public A setModelView(Matrix4fc modelView) {
        this.modelView.set(modelView);
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    public A setCamera(double x, double y, double z) {
        this.cameraX = x;
        this.cameraY = y;
        this.cameraZ = z;
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    public A setScreenSize(int width, int height) {
        this.width = width;
        this.height = height;
        return (A) this;
    }

    @SuppressWarnings("unchecked")
    public A setFogParameters(FogParameters fogParameters) {
        this.fogParameters = fogParameters;
        return (A) this;
    }

    /**
     * Recomputes derived state (MVP, frustum, section-relative camera split) and
     * resizes / clears the depth-bound FBO when the screen dimensions changed.
     */
    @SuppressWarnings("unchecked")
    public A update() {
        this.projection.mul(this.modelView, this.MVP);
        this.frustum.set(this.MVP, false);

        int sx = Mth.floor(this.cameraX) >> 5;
        int sy = Mth.floor(this.cameraY) >> 5;
        int sz = Mth.floor(this.cameraZ) >> 5;
        this.section.set(sx, sy, sz);

        this.innerTranslation.set(
                (float) (this.cameraX - (sx << 5)),
                (float) (this.cameraY - (sy << 5)),
                (float) (this.cameraZ - (sz << 5)));

        if (this.depthBoundingBuffer.resize(this.width, this.height)) {
            this.depthBoundingBuffer.clear(this.properties.inverseClearDepth());
        }
        return (A) this;
    }

    /** GPU-resident draw command list for this viewport's pass; subclass-defined. */
    public abstract GlBuffer getRenderList();
}
