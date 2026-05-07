package com.github.foxy.client.compat;

import net.minecraft.client.renderer.RenderType;

public enum ChunkSectionLayer {
    SOLID,
    CUTOUT,
    CUTOUT_MIPPED,
    TRANSLUCENT,
    TRIPWIRE;

    public static ChunkSectionLayer from(RenderType type) {
        if (type == RenderType.translucent()) return TRANSLUCENT;
        if (type == RenderType.cutout()) return CUTOUT;
        if (type == RenderType.cutoutMipped()) return CUTOUT_MIPPED;
        if (type == RenderType.tripwire()) return TRIPWIRE;
        return SOLID;
    }
}
