package com.github.foxy.client.core.rendering.post;

import com.github.foxy.client.core.RenderProperties;
import com.github.foxy.client.core.gl.shader.Shader;
import com.github.foxy.client.core.gl.shader.ShaderType;
import com.github.foxy.client.core.rendering.util.SharedIndexBuffer;

import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL45C.glCreateVertexArrays;

/**
 * Reusable full-screen pass: binds a fragment shader and issues one indexed draw of
 * a 2-triangle screen-covering quad.
 *
 * <h2>Why it's not just {@code glDrawArrays(0, 3)}</h2>
 * <p>The simplest full-screen trick uses one oversized triangle and reads
 * {@code gl_VertexID} in the vertex shader to compute its corner. That works but
 * costs a fragment-shader sample of a triangle that extends past the viewport
 * (clipped, not free). This implementation uses a 4-vertex quad indexed as two
 * triangles (6 indices) sourced from {@link SharedIndexBuffer#INSTANCE_BYTE} so the
 * GPU's primitive pipeline sees only on-screen pixels. The vertex shader
 * ({@code Foxy:post/fullscreen.vert} by default) emits the 4 corners directly.</p>
 *
 * <h2>State touched</h2>
 * <p>{@link #blit()} binds {@link #EMPTY_VAO} (no attribute streams, just the index
 * buffer), the configured shader, and {@link SharedIndexBuffer#INSTANCE_BYTE} for
 * indices, then unbinds the VAO at the end. Caller-set blending / depth state is
 * left untouched so callers can compose blits.</p>
 *
 * <h2>Construction overloads</h2>
 * Pass either:
 * <ul>
 *   <li>{@code (properties, fragId)} for the default vertex shader; or</li>
 *   <li>{@code (properties, vertId, fragId)} to override both.</li>
 * </ul>
 * Each accepts an optional {@code Consumer<Shader.Builder>} for last-mile builder
 * tweaks (extra defines, replacements, etc.).
 *
 * <p>Cleanroom note: same surface as upstream Voxy. The cleanroom rewrite drops the
 * unused {@code java.util.function.Function} import, removes the unchecked
 * {@code (Shader.Builder<T>)} cast — the bound generic was never useful since
 * callers always operate on the base {@link Shader} type — and adds full English
 * javadoc.</p>
 */
public final class FullscreenBlit {

    /** Empty VAO that satisfies core-profile validation while the index buffer drives the draw. */
    private static final int EMPTY_VAO = glCreateVertexArrays();

    /** Default vertex shader id; the fullscreen.vert ships with the mod. */
    private static final String DEFAULT_VERT_ID = "Foxy:post/fullscreen.vert";

    private final Shader shader;

    /** Default vertex shader, no extra builder customization. */
    public FullscreenBlit(RenderProperties properties, String fragId) {
        this(properties, DEFAULT_VERT_ID, fragId, b -> {});
    }

    /** Custom vertex shader, no extra builder customization. */
    public FullscreenBlit(RenderProperties properties, String vertId, String fragId) {
        this(properties, vertId, fragId, b -> {});
    }

    /** Default vertex shader, extra builder customization (e.g. defines / replacements). */
    public FullscreenBlit(RenderProperties properties, String fragId, Consumer<Shader.Builder<Shader>> applyer) {
        this(properties, DEFAULT_VERT_ID, fragId, applyer);
    }

    /** Custom vertex + fragment shader with extra builder customization. */
    public FullscreenBlit(RenderProperties properties, String vertId, String fragId,
                          Consumer<Shader.Builder<Shader>> applyer) {
        this.shader = Shader.make()
                .apply(properties::apply)
                .add(ShaderType.VERTEX, vertId)
                .add(ShaderType.FRAGMENT, fragId)
                .apply(applyer)
                .compile();
    }

    /** Binds just the shader; useful when the caller will call {@link #blit()} later. */
    public void bind() {
        this.shader.bind();
    }

    /**
     * Issues one full-screen draw using the byte-indexed 6-index quad in
     * {@link SharedIndexBuffer#INSTANCE_BYTE}. Re-binds the shader so callers can
     * {@code blit()} repeatedly without thinking about state.
     */
    public void blit() {
        glBindVertexArray(EMPTY_VAO);
        this.shader.bind();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, SharedIndexBuffer.INSTANCE_BYTE.id());
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_BYTE, 0L);
        glBindVertexArray(0);
    }

    /** Tears down the underlying shader. The shared VAO/index buffer outlive the blit. */
    public void delete() {
        this.shader.free();
    }
}
