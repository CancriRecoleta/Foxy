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

    public static int getLightmapTextureId() {
        return Minecraft.getInstance().getTextureManager().getTexture(new ResourceLocation("dynamic/light_map_1")).getId();
    }
}
