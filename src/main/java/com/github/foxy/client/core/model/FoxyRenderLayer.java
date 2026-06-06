package com.github.foxy.client.core.model;

import net.minecraft.client.renderer.RenderType;

// 1.21's net.minecraft.client.renderer.chunk.ChunkSectionLayer enum does not exist in 1.20.1, where
// ItemBlockRenderTypes.getChunkRenderType / getRenderLayer return RenderType singletons instead.
// Foxy categorises those singletons into the four buckets the baker actually distinguishes; the
// mipped and non-mipped cutout layers both fold into CUTOUT (both use alpha discard).
public enum FoxyRenderLayer {
    SOLID,
    CUTOUT,
    TRANSLUCENT,
    TRIPWIRE;

    public static FoxyRenderLayer fromRenderType(RenderType type) {
        if (type == RenderType.solid()) {
            return SOLID;
        }
        if (type == RenderType.cutout() || type == RenderType.cutoutMipped()) {
            return CUTOUT;
        }
        if (type == RenderType.translucent()) {
            return TRANSLUCENT;
        }
        if (type == RenderType.tripwire()) {
            return TRIPWIRE;
        }
        // Anything else (e.g. a custom block layer) is treated as cutout so it still gets baked
        // with alpha discard rather than being silently dropped.
        return CUTOUT;
    }
}
