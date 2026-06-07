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
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();

    public static FoxyConfig CONFIG = loadOrCreate();
    static {
        // Apply the persisted logging toggle as soon as the config is loaded.
        CONFIG.applyLogging();
    }

    public boolean enabled = true;
    public boolean ingestEnabled = true;
    public float sectionRenderDistance = 16;
    public int serviceThreads = (int) Math.max(CpuLayout.getCoreCount()/1.5, 1);
    public float subDivisionSize = 64;
    public boolean useEnvironmentalFog = true;
    public boolean dontUseSodiumBuilderThreads = false;
    public boolean loggingEnabled = true;
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

    // Mirror the persisted logging toggle onto the global Logger switch.
    // loggingEnabled == false silences all of Foxy's log output (info/warn/error) and the in-game
    // error popup via Logger.SHUTUP.
    public void applyLogging() {
        Logger.SHUTUP = !this.loggingEnabled;
    }


    private static FoxyConfig loadOrCreate() {
        if (FoxyCommon.isAvailable()) {
            var path = getConfigPath();
            if (Files.exists(path)) {
                try (FileReader reader = new FileReader(path.toFile())) {
                    var conf = GSON.fromJson(reader, FoxyConfig.class);
                    if (conf != null) {
                        conf.save();
                        return conf;
                    } else {
                        Logger.error("Failed to load foxy config, resetting");
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
            return config;
        }
    }

    public void save() {
        if (!FoxyCommon.isAvailable()) {
            Logger.info("Not saving config since foxy is unavalible");
            return;
        }

        try {
            Files.writeString(getConfigPath(), GSON.toJson(this));
        } catch (IOException e) {
            Logger.error("Failed to write config file", e);
        }
    }

    private static Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get()
                
                .resolve("foxy-config.json");
    }

    public boolean isRenderingEnabled() {
        return FoxyCommon.isAvailable() && this.enabled;
    }
}
