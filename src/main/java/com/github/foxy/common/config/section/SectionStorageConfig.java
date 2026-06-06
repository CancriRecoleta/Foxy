package com.github.foxy.common.config.section;

import com.github.foxy.common.config.ConfigBuildCtx;
import com.github.foxy.common.config.Serialization;

public abstract class SectionStorageConfig {
    static {
        Serialization.CONFIG_TYPES.add(SectionStorageConfig.class);
    }

    public abstract SectionStorage build(ConfigBuildCtx ctx);
}
