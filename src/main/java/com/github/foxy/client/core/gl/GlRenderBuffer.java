package com.github.foxy.client.core.gl;

import static org.lwjgl.opengl.GL45C.glCreateRenderbuffers;
import static org.lwjgl.opengl.GL45C.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL45C.glNamedRenderbufferStorage;

/**
 * DSA wrapper around a GL renderbuffer.
 *
 * <p>Renderbuffers are the right choice when an FBO attachment is never sampled in a
 * shader (depth-only passes, MSAA resolves) &mdash; they skip the texturing path the
 * driver would set up for a {@link GlTexture} and free up a few hardware features.</p>
 */
public final class GlRenderBuffer extends GlObject {
    /** Underlying GL renderbuffer name. */
    public final int id;

    /** Allocates a renderbuffer with immutable storage at {@code (width, height)}. */
    public GlRenderBuffer(int internalFormat, int width, int height) {
        this.id = glCreateRenderbuffers();
        glNamedRenderbufferStorage(this.id, internalFormat, width, height);
    }

    @Override
    public void free() {
        free0();
        glDeleteRenderbuffers(this.id);
    }
}
