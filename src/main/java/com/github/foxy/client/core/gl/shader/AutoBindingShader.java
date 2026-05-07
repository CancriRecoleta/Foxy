package com.github.foxy.client.core.gl.shader;

import com.github.foxy.client.core.gl.GlBuffer;
import com.github.foxy.client.core.gl.GlDebug;
import com.github.foxy.client.core.gl.GlTexture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30C.glBindBufferBase;
import static org.lwjgl.opengl.GL30C.glBindBufferRange;
import static org.lwjgl.opengl.GL31C.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL33C.glBindSampler;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL45C.glBindTextureUnit;

/**
 * {@link Shader} extension that remembers SSBO / UBO / texture bindings and applies
 * them on every {@link #bind()}.
 *
 * <p>The point is to keep render-pass code declarative: declare {@code shader.ssbo(0,
 * sectionDataBuffer).texture(0, sampler, depthTex)} once during setup, then call
 * {@code shader.bind()} per draw &mdash; the wrapper re-binds the resources each time
 * so the GL state is always consistent with what the shader expects, regardless of
 * what other render passes did between draws.</p>
 *
 * <h2>Define-keyed helpers</h2>
 * The {@code ssbo(String define, ...)} / {@code texture(String define, ...)} overloads
 * read the binding index from the builder's {@code #define} table. Pattern of use:
 * <pre>
 *   builder.define("BINDING_SECTION_DATA", 0);
 *   ...
 *   shader.ssbo("BINDING_SECTION_DATA", buffer);
 * </pre>
 * which keeps the binding index in one place (the GLSL source) instead of scattered
 * literals on the Java side.
 *
 * <h2>Cleanroom note</h2>
 * <p>Upstream had a {@code rebuild} flag with a TODO to switch to {@code glBindBuffersBase}
 * / {@code glBindTextures} batched APIs. The Foxy port keeps the per-binding loop because
 * driver coverage of the batched calls is uneven on Intel; the upgrade is one method to
 * swap in if profiling later shows it matters.</p>
 */
public final class AutoBindingShader extends Shader {

    private record BufferBinding(int target, int index, GlBuffer buffer, long offset, long size) {}
    private record TextureBinding(int unit, int sampler, GlTexture texture) {}

    private final Map<String, String> defines;
    private final List<BufferBinding> bufferBindings = new ArrayList<>();
    private final List<TextureBinding> textureBindings = new ArrayList<>();

    AutoBindingShader(Shader.Builder<AutoBindingShader> builder, int program) {
        super(program);
        // Builder is otherwise self-contained; we only keep its define table so the
        // ssbo(String, ...) / texture(String, ...) lookups continue to work.
        this.defines = builder.definesView();
    }

    /** Optional KHR-debug program label; chainable. */
    @Override
    public AutoBindingShader name(String label) {
        GlDebug.nameProgram(label, id());
        return this;
    }

    // ---- SSBO ---------------------------------------------------------------------

    /** Conditional bind: only attaches when {@code define} appears in the builder's defines. */
    public AutoBindingShader ssboIf(String define, GlBuffer buffer) {
        if (this.defines.containsKey(define)) ssbo(define, buffer);
        return this;
    }

    /** Binds {@code buffer} as SSBO at index {@code index}. */
    public AutoBindingShader ssbo(int index, GlBuffer buffer) {
        return ssbo(index, buffer, 0L);
    }

    /** Binds {@code buffer} as SSBO at the index encoded by {@code #define define}. */
    public AutoBindingShader ssbo(String define, GlBuffer buffer) {
        return ssbo(parseIndex(define), buffer, 0L);
    }

    /** Binds {@code buffer} as SSBO at {@code index}, starting at {@code offset}. */
    public AutoBindingShader ssbo(int index, GlBuffer buffer, long offset) {
        replaceBinding(new BufferBinding(GL_SHADER_STORAGE_BUFFER, index, buffer, offset, -1L));
        return this;
    }

    // ---- UBO ----------------------------------------------------------------------

    /** Binds {@code buffer} as UBO at the index encoded by {@code #define define}. */
    public AutoBindingShader ubo(String define, GlBuffer buffer) {
        return ubo(parseIndex(define), buffer);
    }

    /** Binds {@code buffer} as UBO at {@code index}. */
    public AutoBindingShader ubo(int index, GlBuffer buffer) {
        return ubo(index, buffer, 0L);
    }

    /** Binds {@code buffer} as UBO at {@code index}, starting at {@code offset}. */
    public AutoBindingShader ubo(int index, GlBuffer buffer, long offset) {
        replaceBinding(new BufferBinding(GL_UNIFORM_BUFFER, index, buffer, offset, -1L));
        return this;
    }

    // ---- texture ------------------------------------------------------------------

    /** Binds {@code texture} to the unit encoded by {@code #define define}, no sampler. */
    public AutoBindingShader texture(String define, GlTexture texture) {
        return texture(define, -1, texture);
    }

    /** Binds {@code texture} to the unit encoded by {@code #define define}, with sampler {@code sampler}. */
    public AutoBindingShader texture(String define, int sampler, GlTexture texture) {
        return texture(parseIndex(define), sampler, texture);
    }

    /**
     * Binds {@code texture} to {@code unit} with {@code sampler} (or {@code -1} for
     * the default). Replaces any previous binding at the same unit.
     */
    public AutoBindingShader texture(int unit, int sampler, GlTexture texture) {
        for (int i = 0; i < this.textureBindings.size(); i++) {
            if (this.textureBindings.get(i).unit == unit) {
                this.textureBindings.set(i, new TextureBinding(unit, sampler, texture));
                return this;
            }
        }
        this.textureBindings.add(new TextureBinding(unit, sampler, texture));
        return this;
    }

    // ---- bind ----------------------------------------------------------------------

    @Override
    public void bind() {
        super.bind();
        for (BufferBinding b : this.bufferBindings) {
            b.buffer.assertNotFreed();
            if (b.offset == 0L && b.size == -1L) {
                glBindBufferBase(b.target, b.index, b.buffer.id);
            } else {
                glBindBufferRange(b.target, b.index, b.buffer.id, b.offset, b.size);
            }
        }
        for (TextureBinding t : this.textureBindings) {
            if (t.texture != null) {
                t.texture.assertNotFreed();
                glBindTextureUnit(t.unit, t.texture.id);
            }
            if (t.sampler != -1) {
                glBindSampler(t.unit, t.sampler);
            }
        }
    }

    // ---- internals -----------------------------------------------------------------

    private void replaceBinding(BufferBinding incoming) {
        for (int i = 0; i < this.bufferBindings.size(); i++) {
            BufferBinding existing = this.bufferBindings.get(i);
            if (existing.target == incoming.target && existing.index == incoming.index) {
                this.bufferBindings.set(i, incoming);
                return;
            }
        }
        this.bufferBindings.add(incoming);
    }

    /** Reads the {@code #define} value supplied by the builder as an int binding index. */
    private int parseIndex(String define) {
        String value = this.defines.get(define);
        if (value == null) {
            throw new IllegalArgumentException("Define '" + define + "' was not set on the shader builder");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Define '" + define + "' has non-integer value '" + value + "'");
        }
    }
}
