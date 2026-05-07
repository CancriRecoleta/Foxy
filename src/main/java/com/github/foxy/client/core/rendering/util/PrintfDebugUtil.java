package com.github.foxy.client.core.rendering.util;

import com.github.foxy.client.core.gl.shader.IShaderProcessor;
import com.github.foxy.client.core.gl.shader.PrintfInjector;
import com.github.foxy.client.core.gl.shader.ShaderType;
import com.github.foxy.common.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Render-thread surface for the GLSL {@code printf()} injector.
 *
 * <h2>Modes</h2>
 * <ul>
 *   <li>Default: {@link #ENABLE_PRINTF_DEBUGGING} is {@code false}, the
 *       {@link #PRINTF_processor} swap-stripes any {@code printf} calls in shader
 *       source by commenting them out before they reach the GLSL compiler. Zero
 *       runtime cost; useful so shader assets can keep their {@code printf} calls
 *       in production builds.</li>
 *   <li>Enabled with {@code -DFoxy.enableShaderDebugPrintf=true}: a real
 *       {@link PrintfInjector} (50 KB ring buffer at binding 20) injects the GPU-side
 *       hooks; {@link #tick()} drains the ring once per frame and forwards each line
 *       to {@link Logger} (lines starting with {@code "LOG"}) and to the F3-overlay
 *       queue accessible via {@link #addToOut}.</li>
 * </ul>
 *
 * <p>Cleanroom note: same surface as upstream Voxy. The cleanroom rewrite drops the
 * Chinese-language inline TODO and routes the comment-stripping fallback through a
 * named {@link #stripPrintf} helper instead of an inline anonymous class. The
 * underlying {@link PrintfInjector} on Foxy is currently a stub (see its class
 * javadoc); once the full injector lands, this surface will start emitting real
 * messages without callers having to change.</p>
 */
public final class PrintfDebugUtil {
    private PrintfDebugUtil() {}

    /** {@code -DFoxy.enableShaderDebugPrintf=true} flips this on. */
    public static final boolean ENABLE_PRINTF_DEBUGGING =
            "true".equalsIgnoreCase(System.getProperty("Foxy.enableShaderDebugPrintf", "false"));

    /** Lines downloaded during the last frame; visible to F3-overlay subscribers. */
    private static final List<String> lastFrameLines = new ArrayList<>();

    /** Lines downloaded during the current frame; rotated into {@link #lastFrameLines} on tick. */
    private static final List<String> currentFrameLines = new ArrayList<>();

    /** Shader-source processor wired into shader builders that opt in to printf. */
    public static final IShaderProcessor PRINTF_processor;

    /** Real injector when debugging is enabled, otherwise {@code null}. */
    private static final PrintfInjector PRINTF_object;

    static {
        if (ENABLE_PRINTF_DEBUGGING) {
            PRINTF_object = new PrintfInjector(50_000, 20,
                    line -> {
                        if (line.startsWith("LOG")) Logger.info(line);
                        currentFrameLines.add(line);
                    },
                    currentFrameLines::clear);
            PRINTF_processor = PRINTF_object;
        } else {
            PRINTF_object = null;
            PRINTF_processor = PrintfDebugUtil::stripPrintf;
        }
    }

    /**
     * Comment-strips every {@code printf(...)} call in the source. This is a naive
     * literal replace and does not handle nested parentheses; for production-quality
     * stripping see the regex referenced in upstream's TODO. It is enough for the
     * shader assets currently in tree.
     */
    private static String stripPrintf(ShaderType type, String src) {
        return src.replace("printf", "//printf");
    }

    /** Per-frame: rotate buffers and drain the GPU ring. */
    public static void tick() {
        if (!ENABLE_PRINTF_DEBUGGING) return;
        lastFrameLines.clear();
        lastFrameLines.addAll(currentFrameLines);
        currentFrameLines.clear();
        PRINTF_object.download();
    }

    /** Append the most recent frame's printf lines to the F3 overlay's text list. */
    public static void addToOut(List<String> out) {
        if (!ENABLE_PRINTF_DEBUGGING) return;
        out.add("Printf Queue:");
        out.addAll(lastFrameLines);
    }

    /** Bind the printf SSBO so the next compute / fragment shader can write to it. */
    public static void bind() {
        if (!ENABLE_PRINTF_DEBUGGING) return;
        PRINTF_object.bind();
    }

    /** Read-only snapshot of the last frame's printf lines (test convenience). */
    public static List<String> snapshotLastFrame() {
        return Collections.unmodifiableList(new ArrayList<>(lastFrameLines));
    }
}
