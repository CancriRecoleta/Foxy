package com.github.foxy.client.core.rendering;

import com.github.foxy.client.core.util.IrisUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ViewportSelector <T extends Viewport<?>> {
    // Vivecraft's VR render-pass API is Fabric-only and not a declared Forge 1.20.1 dependency,
    // so VR viewport selection is disabled here; only the default and iris-shadow viewports remain.
    public static final boolean VIVECRAFT_INSTALLED = false;

    private final Supplier<T> creator;
    private final T defaultViewport;
    private final Map<Object, T> extraViewports = new HashMap<>();//TODO should maybe be a weak hashmap with value cleanup queue thing?

    public ViewportSelector(Supplier<T> viewportCreator) {
        this.creator = viewportCreator;
        this.defaultViewport = viewportCreator.get();
    }

    private T getOrCreate(Object holder) {
        return this.extraViewports.computeIfAbsent(holder, a->this.creator.get());
    }

    private static final Object IRIS_SHADOW_OBJECT = new Object();
    public T getViewport() {
        T viewport = null;

        if (viewport == null && IrisUtil.irisShadowActive()) {
            viewport = this.getOrCreate(IRIS_SHADOW_OBJECT);
        }

        if (viewport == null) {
            viewport = this.defaultViewport;
        }
        return viewport;
    }

    public void free() {
        this.defaultViewport.delete();
        this.extraViewports.values().forEach(Viewport::delete);
        this.extraViewports.clear();
    }
}
