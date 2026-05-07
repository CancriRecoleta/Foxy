package com.github.foxy.client.core.gl.shader;

/**
 * Stub of upstream Voxy's GLSL {@code printf()} injector.
 *
 * <h2>What the upstream version does</h2>
 * <p>Upstream rewrites every {@code printf("fmt", arg, arg)} call site in shader source
 * into a sequence of writes into a shared SSBO ring-buffer; a CPU-side download stream
 * then drains the buffer each frame and forwards each string to a callback. Useful for
 * debugging compute shaders that have no other observable side effects.</p>
 *
 * <h2>Why this is a stub</h2>
 * <p>The implementation depends on a CPU-side ring-buffer download helper
 * ({@code DownloadStream}, not yet ported to Foxy) and on the
 * {@link com.github.foxy.client.core.gl.GlBuffer GlBuffer} infrastructure being driven
 * from a thread that owns the GL context. Both prerequisites land in later renderer
 * milestones; until then the injector is a no-op {@link IShaderProcessor} so callers
 * can wire it into their {@link Shader#make(IShaderProcessor...) shader builders}
 * without conditional code.</p>
 *
 * <h2>Behaviour</h2>
 * <p>{@link #process(ShaderType, String)} returns its input unchanged. {@link #flush()}
 * is a no-op. Constructing a {@code PrintfInjector} is cheap; nothing is allocated on
 * the GL side.</p>
 */
public final class PrintfInjector implements IShaderProcessor {
    @SuppressWarnings("unused")
    private final int bufferSize;
    @SuppressWarnings("unused")
    private final int bufferBindingIndex;

    /**
     * Stub constructor; arguments are accepted for source-compat with the eventual
     * full implementation but are ignored.
     */
    public PrintfInjector(int bufferSize, int bufferBindingIndex) {
        this.bufferSize = bufferSize;
        this.bufferBindingIndex = bufferBindingIndex;
    }

    @Override
    public String process(ShaderType type, String source) {
        return source;
    }

    /** No-op until the download-stream side lands. */
    public void flush() {
        // intentionally empty
    }
}
