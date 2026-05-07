package com.github.foxy.client.core.gl;

import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30C.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL45C.glCheckNamedFramebufferStatus;
import static org.lwjgl.opengl.GL45C.glCreateFramebuffers;
import static org.lwjgl.opengl.GL45C.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL45C.glNamedFramebufferDrawBuffers;
import static org.lwjgl.opengl.GL45C.glNamedFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL45C.glNamedFramebufferTexture;

/**
 * DSA wrapper around a GL framebuffer object.
 *
 * <p>Attachment helpers ({@link #attach(int, GlTexture)},
 * {@link #attach(int, GlRenderBuffer)}) are chainable so common pipeline setup reads
 * left-to-right:</p>
 * <pre>
 *   var fbo = new GlFramebuffer()
 *       .attach(GL_COLOR_ATTACHMENT0, colorTex)
 *       .attach(GL_DEPTH_ATTACHMENT, depthRb)
 *       .setDrawBuffers(GL_COLOR_ATTACHMENT0)
 *       .verify();
 * </pre>
 */
public final class GlFramebuffer extends GlObject {
    /** Underlying GL framebuffer name. */
    public final int id;

    /** Allocates a fresh framebuffer name via DSA. */
    public GlFramebuffer() {
        this.id = glCreateFramebuffers();
    }

    /** Attaches mip level 0 of {@code texture} to {@code attachment}. */
    public GlFramebuffer attach(int attachment, GlTexture texture) {
        return attach(attachment, texture, 0);
    }

    /** Attaches the given {@code level} of {@code texture} to {@code attachment}. */
    public GlFramebuffer attach(int attachment, GlTexture texture, int level) {
        glNamedFramebufferTexture(this.id, attachment, texture.id, level);
        return this;
    }

    /** Attaches a renderbuffer to {@code attachment}. */
    public GlFramebuffer attach(int attachment, GlRenderBuffer buffer) {
        glNamedFramebufferRenderbuffer(this.id, attachment, GL_RENDERBUFFER, buffer.id);
        return this;
    }

    /** Sets the FBO's draw-buffer list, e.g. {@code GL_COLOR_ATTACHMENT0}. */
    public GlFramebuffer setDrawBuffers(int... buffers) {
        glNamedFramebufferDrawBuffers(this.id, buffers);
        return this;
    }

    /** Verifies completeness; throws with the GL status code if anything is wrong. */
    public GlFramebuffer verify() {
        int code = glCheckNamedFramebufferStatus(this.id, GL_FRAMEBUFFER);
        if (code != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Framebuffer incomplete: 0x" + Integer.toHexString(code));
        }
        return this;
    }

    @Override
    public void free() {
        free0();
        glDeleteFramebuffers(this.id);
    }

    /** Optional debug label. */
    public GlFramebuffer name(String label) { return GlDebug.name(label, this); }
}
