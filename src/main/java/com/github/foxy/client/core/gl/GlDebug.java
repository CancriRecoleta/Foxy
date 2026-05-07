package com.github.foxy.client.core.gl;

import static org.lwjgl.opengl.GL43C.GL_BUFFER;
import static org.lwjgl.opengl.GL43C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL43C.GL_TEXTURE;
import static org.lwjgl.opengl.GL43C.GL_PROGRAM;
import static org.lwjgl.opengl.GL43C.glObjectLabel;

/**
 * Optional KHR-debug labelling helper used to make RenderDoc / NSight captures readable.
 *
 * <p>Disabled by default (no-op). Enable with {@code -Dfoxy.glDebug=true}; when on, every
 * {@code name(label, obj)} call attaches the label to the underlying GL handle. The
 * cost when disabled is one boolean field read per call.</p>
 */
public final class GlDebug {
    /** Toggle for KHR-debug labelling; default {@code false} so production runs are unaffected. */
    public static final boolean GL_DEBUG = "true".equalsIgnoreCase(System.getProperty("foxy.glDebug", "false"));

    private GlDebug() {}

    /** Labels a buffer object. */
    public static GlBuffer name(String label, GlBuffer buffer) {
        if (GL_DEBUG) glObjectLabel(GL_BUFFER, buffer.id, label);
        return buffer;
    }

    /** Labels a persistent-mapped buffer. */
    public static GlPersistentMappedBuffer name(String label, GlPersistentMappedBuffer buffer) {
        if (GL_DEBUG) glObjectLabel(GL_BUFFER, buffer.id, label);
        return buffer;
    }

    /** Labels a framebuffer object. */
    public static GlFramebuffer name(String label, GlFramebuffer framebuffer) {
        if (GL_DEBUG) glObjectLabel(GL_FRAMEBUFFER, framebuffer.id, label);
        return framebuffer;
    }

    /** Labels a texture. */
    public static GlTexture name(String label, GlTexture texture) {
        if (GL_DEBUG) glObjectLabel(GL_TEXTURE, texture.id, label);
        return texture;
    }

    /** Labels a program / shader by raw program id. */
    public static int nameProgram(String label, int programId) {
        if (GL_DEBUG) glObjectLabel(GL_PROGRAM, programId, label);
        return programId;
    }
}
