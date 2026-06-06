package com.github.foxy.client.core.rendering.util;

import static org.lwjgl.opengl.GL33.glBindSampler;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;

import net.minecraft.client.Minecraft;

public class LightMapHelper {
    public static void bind(int lightingIndex) {
        glBindSampler(lightingIndex, 0);
        glBindTextureUnit(lightingIndex, getLightmapTextureId());
    }

    public static int getLightmapTextureId() {
        // 1.20.1: LightTexture holds a private DynamicTexture (exposed via AT); its getId() is the
        // GL texture name (no 1.21 GpuTexture/getTextureView indirection).
        return Minecraft.getInstance().gameRenderer.lightTexture().lightTexture.getId();
    }
}