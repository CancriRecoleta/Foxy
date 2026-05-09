package com.github.foxy.client.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.foxy.client.core.SSAO;
import com.github.foxy.common.Logger;
import com.github.foxy.common.util.cpu.CpuLayout;
import com.github.foxy.commonImpl.FoxyCommon;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class FoxyConfig {
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE, Modifier.STATIC)
            .create();

    public static FoxyConfig CONFIG = loadOrCreate();

    public boolean enabled = true;
    public boolean enableRendering = true;
    public boolean ingestEnabled = true;
    public boolean autoBackfillSingleplayer = true;
    public float sectionRenderDistance = 16;
    /**
     * Diagnostic toggle: when {@code true}, suppresses Embeddium's vanilla chunk
     * rendering so only Foxy LOD remains on screen. Used to confirm whether the
     * far-distance LOD output exists at all (visible past vanilla VD => render
     * pipeline OK, the issue is depth/stencil masking; still nothing => data
     * or traversal bug). Not intended for normal play &mdash; the chunk
     * renderer also drives entity/particle culling and lighting context.
     */
    public boolean debugDisableVanillaTerrain = false;
    public int serviceThreads = (int) Math.max(CpuLayout.getCoreCount()/1.5, 1);
    public float subDivisionSize = 64;
    public boolean useEnvironmentalFog = true;
    public boolean dontUseSodiumBuilderThreads = false;
    public String ssaoMode;

    public SSAO.SSAOMode getSSAOMode() {
        if (this.ssaoMode == null) return SSAO.SSAOMode.AUTO;
        try {
            return SSAO.SSAOMode.valueOf(this.ssaoMode.toUpperCase(Locale.ROOT));
        } catch (Exception e) { return SSAO.SSAOMode.AUTO; }
    }

    public void setSSAOMode(SSAO.SSAOMode mode) {
        this.ssaoMode = mode.name().toLowerCase(Locale.ROOT);
    }


    /** Hard upper bound for sectionRenderDistance, matching the slider's max
     *  and the capacity that {@link com.github.foxy.client.core.rendering.hierachical.HierarchicalOcclusionTraverser#MAX_QUEUE_SIZE}
     *  was sized for. Loading a stale config that exceeded this value would
     *  immediately crash the render thread on world join when the TLN buffer
     *  overflowed. */
    public static final float SECTION_RENDER_DISTANCE_MAX = 64f;

    private void clampLoaded() {
        // Defensive clamp: legacy configs (or hand-edited ones) that store a
        // sectionRenderDistance above the renderer's hard capacity are silently
        // brought back into range. Without this an out-of-range value crashes
        // HierarchicalOcclusionTraverser.addTLN as soon as the player joins.
        if (this.sectionRenderDistance > SECTION_RENDER_DISTANCE_MAX) {
            Logger.warn("Foxy: sectionRenderDistance " + this.sectionRenderDistance
                    + " exceeds max " + SECTION_RENDER_DISTANCE_MAX + "; clamping");
            this.sectionRenderDistance = SECTION_RENDER_DISTANCE_MAX;
        }
        if (this.sectionRenderDistance < 2f) {
            this.sectionRenderDistance = 2f;
        }
    }

    private static FoxyConfig loadOrCreate() {
        if (FoxyCommon.isAvailable()) {
            var path = getConfigPath();
            if (Files.exists(path)) {
                try (FileReader reader = new FileReader(path.toFile())) {
                    var conf = GSON.fromJson(reader, FoxyConfig.class);
                    if (conf != null) {
                        conf.clampLoaded();
                        conf.save();
                        return conf;
                    } else {
                        Logger.error("Failed to load Foxy config, resetting");
                    }
                } catch (IOException e) {
                    Logger.error("Could not parse config", e);
                }
            }
            Logger.info("Config doesnt exist, creating new");
            var config = new FoxyConfig();
            config.save();
            return config;
        } else {
            var config = new FoxyConfig();
            config.enabled = false;
            config.enableRendering = false;
            return config;
        }
    }

    public void save() {
        if (!FoxyCommon.isAvailable()) {
            Logger.info("Not saving config since Foxy is unavalible");
            return;
        }

        try {
            Files.writeString(getConfigPath(), GSON.toJson(this));
        } catch (IOException e) {
            Logger.error("Failed to write config file", e);
        }
    }

    private static Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get().resolve("Foxy-config.json");
    }

    public boolean isRenderingEnabled() {
        return FoxyCommon.isAvailable() && this.enabled && this.enableRendering;
    }
}
