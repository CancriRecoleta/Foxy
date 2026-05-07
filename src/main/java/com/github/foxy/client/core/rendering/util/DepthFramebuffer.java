package com.github.foxy.client.core.rendering.util;

import com.github.foxy.client.core.gl.GlFramebuffer;
import com.github.foxy.client.core.gl.GlTexture;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11C.GL_DEPTH;
import static org.lwjgl.opengl.GL14C.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30C.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30C.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30C.GL_DEPTH_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.GL_STENCIL;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;
import static org.lwjgl.opengl.GL45C.nglClearNamedFramebufferfv;
import static org.lwjgl.opengl.GL45C.nglClearNamedFramebufferiv;

/**
 * Depth-only framebuffer with lazy resize and attachment-type aware clears.
 *
 * <h2>What it does</h2>
 * <p>Owns one {@link GlFramebuffer} and one depth-formatted {@link GlTexture}; the
 * texture is reallocated on size change via {@link #resize}, which returns whether a
 * fresh allocation actually happened so the caller can re-do any dependent setup
 * (e.g. linking the depth tex into a HiZ pyramid).</p>
 *
 * <h2>Format-aware attachment</h2>
 * <p>Depth-only formats attach to {@code GL_DEPTH_ATTACHMENT}; combined depth/stencil
 * formats ({@code GL_DEPTH24_STENCIL8} and {@code GL_DEPTH32F_STENCIL8}) require
 * {@code GL_DEPTH_STENCIL_ATTACHMENT}. {@link #getDepthAttachmentType()} picks the
 * right one so callers don't have to remember the rule.</p>
 *
 * <p>Cleanroom note: same shape as upstream Voxy with English javadoc, a tighter
 * static import set (everything off {@code GL45C} where available), and explicit
 * null checks before {@link GlTexture#free} on resize.</p>
 */
public final class DepthFramebuffer {
    private final int depthFormat;
    /** Holds the current depth texture; reallocated on size change. */
    private GlTexture depthBuffer;
    /** Single FBO; the texture is rebound each time {@link #resize} reallocates. */
    public final GlFramebuffer framebuffer = new GlFramebuffer();

    /** Defaults to {@code GL_DEPTH_COMPONENT24}. */
    public DepthFramebuffer() {
        this(GL_DEPTH_COMPONENT24);
    }

    /**
     * @param depthFormat sized internal depth format such as {@code GL_DEPTH_COMPONENT24}
     *                    or {@code GL_DEPTH24_STENCIL8}; the matching attachment type
     *                    is derived from this in {@link #getDepthAttachmentType()}
     */
    public DepthFramebuffer(int depthFormat) {
        this.depthFormat = depthFormat;
    }

    /**
     * Ensures the depth texture is sized {@code (width, height)}. Returns {@code true}
     * when the texture was reallocated (caller may need to rewire dependent state),
     * {@code false} when the existing texture already matched.
     */
    public boolean resize(int width, int height) {
        if (this.depthBuffer != null
                && this.depthBuffer.getWidth() == width
                && this.depthBuffer.getHeight() == height) {
            return false;
        }
        if (this.depthBuffer != null) {
            this.depthBuffer.free();
        }
        this.depthBuffer = new GlTexture().store(this.depthFormat, 1, width, height);
        this.framebuffer.attach(getDepthAttachmentType(), this.depthBuffer).verify();
        return true;
    }

    /** Right attachment type for the configured depth format. */
    public int getDepthAttachmentType() {
        return this.depthFormat == GL_DEPTH24_STENCIL8
                ? GL_DEPTH_STENCIL_ATTACHMENT
                : GL_DEPTH_ATTACHMENT;
    }

    /** Clears the depth attachment to {@code depth}. */
    public void clear(float depth) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            nglClearNamedFramebufferfv(this.framebuffer.id, GL_DEPTH, 0, stack.nfloat(depth));
        }
    }

    /** Clears the stencil attachment (must be a depth-stencil format) to {@code value}. */
    public void clearStencil(int value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            nglClearNamedFramebufferiv(this.framebuffer.id, GL_STENCIL, 0, stack.nint(value));
        }
    }

    /** Underlying depth texture; never reuse the reference past a {@link #resize}. */
    public GlTexture getDepthTex() { return this.depthBuffer; }

    /** Bind to the current GL state's draw target. */
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, this.framebuffer.id);
    }

    /** Sized internal depth format the FBO was constructed with. */
    public int getFormat() { return this.depthFormat; }

    /** Tears down both FBO and texture. Safe even if {@link #resize} was never called. */
    public void free() {
        this.framebuffer.free();
        if (this.depthBuffer != null) {
            this.depthBuffer.free();
            this.depthBuffer = null;
        }
    }
}
