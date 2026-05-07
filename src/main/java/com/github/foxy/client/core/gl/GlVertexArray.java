package com.github.foxy.client.core.gl;

import java.util.Arrays;

import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.opengl.GL45C.glBindVertexArray;
import static org.lwjgl.opengl.GL45C.glCreateVertexArrays;
import static org.lwjgl.opengl.GL45C.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL45C.glEnableVertexArrayAttrib;
import static org.lwjgl.opengl.GL45C.glVertexArrayAttribFormat;
import static org.lwjgl.opengl.GL45C.glVertexArrayAttribIFormat;
import static org.lwjgl.opengl.GL45C.glVertexArrayElementBuffer;
import static org.lwjgl.opengl.GL45C.glVertexArrayVertexBuffer;

/**
 * DSA wrapper around a GL vertex array object.
 *
 * <p>Attribute formats are declared once via {@link #setI(int, int, int, int)} /
 * {@link #setF(int, int, int, int)} / {@link #setF(int, int, int, boolean, int)};
 * the buffer to feed them is bound separately via {@link #bindBuffer(int)}, which
 * applies the configured stride to every previously-declared attribute index. This
 * mirrors how Sodium / Embeddium feed their vertex shaders and lets the same VAO be
 * re-pointed at successive sub-buffers without redeclaring formats.</p>
 *
 * <p>{@link #STATIC_VAO} is a default-state VAO useful when the caller just needs a
 * non-zero VAO bound to satisfy core-profile validation and isn't going to feed
 * attributes through it.</p>
 */
public final class GlVertexArray extends GlObject {

    /** Default-state VAO; useful for compute or full-screen passes that don't read attributes. */
    public static final int STATIC_VAO = glGenVertexArrays();

    /** Underlying GL VAO name. */
    public final int id;

    private int[] indices = new int[0];
    private int stride;

    /** Allocates a fresh VAO via DSA. */
    public GlVertexArray() {
        this.id = glCreateVertexArrays();
    }

    @Override
    public void free() {
        free0();
        glDeleteVertexArrays(this.id);
    }

    /** Binds this VAO as the current one. */
    public void bind() {
        glBindVertexArray(this.id);
    }

    /**
     * Routes every previously-declared attribute index at this VAO to read from
     * {@code buffer} starting at offset 0 with the configured {@link #setStride stride}.
     */
    public GlVertexArray bindBuffer(int buffer) {
        for (int index : this.indices) {
            glVertexArrayVertexBuffer(this.id, index, buffer, 0L, this.stride);
        }
        return this;
    }

    /** Binds {@code buffer} as the element / index source for {@code glDrawElements*}. */
    public GlVertexArray bindElementBuffer(int buffer) {
        glVertexArrayElementBuffer(this.id, buffer);
        return this;
    }

    /** Sets the per-vertex stride passed to {@link #bindBuffer}. */
    public GlVertexArray setStride(int stride) {
        this.stride = stride;
        return this;
    }

    /** Declares an integer attribute (e.g. {@code GL_INT}, {@code GL_UNSIGNED_BYTE}). */
    public GlVertexArray setI(int index, int type, int count, int offset) {
        addIndex(index);
        glEnableVertexArrayAttrib(this.id, index);
        glVertexArrayAttribIFormat(this.id, index, count, type, offset);
        return this;
    }

    /** Declares a float attribute, no normalisation. */
    public GlVertexArray setF(int index, int type, int count, int offset) {
        return setF(index, type, count, false, offset);
    }

    /** Declares a float attribute with optional normalisation (e.g. {@code GL_UNSIGNED_BYTE} colours). */
    public GlVertexArray setF(int index, int type, int count, boolean normalize, int offset) {
        addIndex(index);
        glEnableVertexArrayAttrib(this.id, index);
        glVertexArrayAttribFormat(this.id, index, count, type, normalize, offset);
        return this;
    }

    /** Tracks attribute indices in declaration order so {@link #bindBuffer} can replay them. */
    private void addIndex(int index) {
        for (int existing : this.indices) {
            if (existing == index) return;
        }
        this.indices = Arrays.copyOf(this.indices, this.indices.length + 1);
        this.indices[this.indices.length - 1] = index;
    }
}
