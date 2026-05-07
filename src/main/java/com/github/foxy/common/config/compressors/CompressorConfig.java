package com.github.foxy.common.config.compressors;

import com.github.foxy.common.config.ConfigBuildCtx;
import com.github.foxy.common.config.Serialization;

public abstract class CompressorConfig {
    static {
        Serialization.CONFIG_TYPES.add(CompressorConfig.class);
    }

    public abstract StorageCompressor build(ConfigBuildCtx ctx);
}

