package com.github.foxy.client.core.rendering.util;

import static org.lwjgl.opengl.GL33.glBindSampler;
import static org.lwjgl.opengl.GL45.glBindTextureUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class LightMapHelper {
    public static void bind(int lightingIndex) {
        glBindSampler(lightingIndex, 0);
        glBindTextureUnit(lightingIndex, getLightmapTextureId());
    }

    /**
     * Resolves the dynamic light-map texture id used by the renderer's light sampler.
     * The location string is a static literal and always parses, but we still go through
     * {@link ResourceLocation#tryParse} to avoid the deprecated raw-string constructor.
     */
    public static int getLightmapTextureId() {
        ResourceLocation lightmap = ResourceLocation.tryParse("dynamic/light_map_1");
        return Minecraft.getInstance().getTextureManager().getTexture(lightmap).getId();
    }
}
