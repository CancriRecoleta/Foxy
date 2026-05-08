package com.github.foxy.client.core.rendering;

import com.github.foxy.client.core.util.IrisUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Per-pass viewport multiplexer: hands callers the right {@link Viewport} for the
 * active draw pass (main, Iris shadow, Vivecraft eye, ...).
 *
 * <h2>Selection precedence</h2>
 * The {@link #getViewport()} method picks the first non-null match from:
 * <ol>
 *   <li>Vivecraft's per-eye viewport (only when {@link #VIVECRAFT_INSTALLED} is true,
 *       which is hard-coded {@code false} on the cleanroom Forge port — see
 *       {@link #getVivecraftViewport()}).</li>
 *   <li>Iris shadow-pass viewport (when {@link IrisUtil#irisShadowActive()} is true).</li>
 *   <li>The default viewport.</li>
 * </ol>
 *
 * <h2>Caching</h2>
 * <p>The non-default viewports are kept in {@link #extraViewports} keyed by an opaque
 * holder object (the Iris shadow path uses a sentinel; Vivecraft would key by
 * render-pass enum). Lookups go through {@link Map#computeIfAbsent}, which calls
 * {@link #creator} on the first hit and caches the result.</p>
 *
 * <h2>Cleanroom note</h2>
 * <p>Same shape as upstream Voxy. The cleanroom rewrite removes the upstream TODO
 * about a weak-key cleanup queue (it never landed), drops the Vivecraft API import
 * (Vivecraft has no Forge port at 1.20.1 with the same {@code VRRenderingAPI}
 * surface), simplifies {@link #getViewport()} to a single-pass selection chain, and
 * adds full English javadoc.</p>
 */
public class ViewportSelector<T extends Viewport<?>> {

    /** Hard-coded false on Forge 1.20.1 cleanroom port; Vivecraft API is unavailable. */
    public static final boolean VIVECRAFT_INSTALLED = false;

    /** Sentinel key for the Iris shadow-pass viewport. */
    private static final Object IRIS_SHADOW_KEY = new Object();

    private final Supplier<T> creator;
    private final T defaultViewport;
    private final Map<Object, T> extraViewports = new HashMap<>();

    public ViewportSelector(Supplier<T> viewportCreator) {
        this.creator = viewportCreator;
        this.defaultViewport = viewportCreator.get();
    }

    /**
     * Resolves the viewport for the current draw context. Always returns non-null.
     */
    public T getViewport() {
        if (VIVECRAFT_INSTALLED) {
            T vrView = getVivecraftViewport();
            if (vrView != null) return vrView;
        }
        if (IrisUtil.irisShadowActive()) {
            return getOrCreate(IRIS_SHADOW_KEY);
        }
        return this.defaultViewport;
    }

    /** Frees the default viewport and every extra viewport. */
    public void free() {
        this.defaultViewport.delete();
        this.extraViewports.values().forEach(Viewport::delete);
        this.extraViewports.clear();
    }

    private T getOrCreate(Object holder) {
        return this.extraViewports.computeIfAbsent(holder, k -> this.creator.get());
    }

    /**
     * Stub for the Vivecraft viewport selector. The cleanroom Forge port returns
     * {@code null} unconditionally; if a Forge-side Vivecraft API ever lands the
     * implementation should branch on the active render pass and return one of the
     * cached eye-viewports.
     */
    private T getVivecraftViewport() {
        return null;
    }
}
