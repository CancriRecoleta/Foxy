package com.github.foxy.client.core.gl.shader;

/**
 * Pluggable GLSL source transformer applied during {@link Shader.Builder#addSource}.
 *
 * <p>Processors are composed in the order they were passed to
 * {@link Shader#make(IShaderProcessor...)}: the first processor sees the raw source,
 * its output is fed to the second, and so on. Use this hook to inject debug printf
 * support, replace constants, normalise whitespace, etc., without modifying the
 * shader assets on disk.</p>
 */
@FunctionalInterface
public interface IShaderProcessor {
    /** Returns the transformed source for {@code source} of the given {@code type}. */
    String process(ShaderType type, String source);
}
