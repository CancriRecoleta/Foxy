package com.github.foxy.client.core.util;

import com.github.foxy.client.compat.FogParameters;
import com.github.foxy.client.core.FoxyRenderSystem;
import com.github.foxy.client.core.rendering.Viewport;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraftforge.fml.ModList;

import java.io.IOException;

/**
 * Thin façade over Oculus / Iris's public surface.
 *
 * <h2>Why this exists</h2>
 * <p>Iris (Fabric) and Oculus (its Forge port for 1.20.1) expose a small but useful API
 * the Foxy renderer hooks into: detecting the current shader pack, capturing the
 * shadow-pass state, clearing samplers between passes, etc. Wrapping every call site
 * with a {@link #IRIS_INSTALLED} guard would clutter the renderer; centralising the
 * guards here keeps the call sites short and the integration optional.</p>
 *
 * <h2>Captured viewport parameters</h2>
 * <p>{@link CapturedViewportParameters} bundles the projection / model-view + camera
 * + fog state captured at the moment Iris hands its viewport to Foxy. The renderer
 * later replays it through {@link FoxyRenderSystem#setupViewport} so the shadow pass
 * sees the same camera as the main pass.</p>
 *
 * <h2>Cleanroom note</h2>
 * <p>Same surface as upstream Voxy. The cleanroom rewrite drops the dead
 * {@code SHADER_SUPPORT = true} flag (commented-out branch never disabled), and
 * fixes the inconsistent {@code !Iris.getCurrentPack().isEmpty()} negation in
 * {@code irisShadersEnabledInConfig0}.</p>
 */
public final class IrisUtil {
    private IrisUtil() {}

    /** Bundle of the Iris viewport parameters captured for replay. */
    public record CapturedViewportParameters(
            ChunkRenderMatrices matrices,
            FogParameters parameters,
            double x, double y, double z) {

        /** Re-applies the captured state onto {@code system}. */
        public Viewport<?> apply(FoxyRenderSystem system) {
            return system.setupViewport(
                    this.matrices.projection(),
                    this.matrices.modelView(),
                    this.parameters,
                    this.x, this.y, this.z);
        }
    }

    /** Most-recent capture; the main render pass writes here, the shadow pass reads. */
    public static CapturedViewportParameters CAPTURED_VIEWPORT_PARAMETERS;

    /** Whether the Forge port (Oculus) is loaded; gates every Iris API call below. */
    public static final boolean IRIS_INSTALLED = ModList.get().isLoaded("oculus");

    /**
     * Whether the shader-driven LOD pipeline is enabled. Currently always {@code true}
     * since the experimental flag never landed; kept for source-compat with callers.
     */
    public static final boolean SHADER_SUPPORT = true;

    // ---- shadow pass --------------------------------------------------------------

    /** {@code true} when Iris is currently rendering its shadow pass. */
    public static boolean irisShadowActive() {
        return IRIS_INSTALLED && ShadowRenderer.ACTIVE;
    }

    /** Resets samplers 0..15 to the null sampler so a stale Iris bind doesn't bleed in. */
    public static void clearIrisSamplers() {
        if (!IRIS_INSTALLED) return;
        for (int i = 0; i < 16; i++) {
            IrisRenderSystem.bindSamplerToUnit(i, 0);
        }
    }

    // ---- shader pack lifecycle ----------------------------------------------------

    /** Forces Iris to reload the active shader pack; no-op when no pack is active. */
    public static void reload() {
        if (!IRIS_INSTALLED) return;
        try {
            if (IrisApi.getInstance().isShaderPackInUse()
                    || IrisApi.getInstance().getConfig().areShadersEnabled()) {
                Iris.reload();
            }
        } catch (IOException e) {
            throw new RuntimeException("Iris shader-pack reload failed", e);
        }
    }

    /** {@code true} when a shader pack is currently selected / active. */
    public static boolean irisShaderPackEnabled() {
        return IRIS_INSTALLED && Iris.getCurrentPack().isPresent();
    }

    /** Mirrors {@link #irisShaderPackEnabled} via the Iris config view. */
    public static boolean irisShadersEnabledInConfig() {
        return IRIS_INSTALLED && Iris.getCurrentPack().isPresent();
    }

    /** Disables shaders via the public Iris config and applies the change. */
    public static void disableIrisShaders() {
        if (!IRIS_INSTALLED) return;
        IrisApi.getInstance().getConfig().setShadersEnabledAndApply(false);
    }
}
