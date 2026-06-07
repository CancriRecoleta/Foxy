package com.github.foxy.client.core;

import net.minecraft.client.Minecraft;

public interface IGetFoxyRenderSystem {
    FoxyRenderSystem foxy$getRenderSystem();
    void foxy$shutdownRenderer();
    void foxy$createRenderer();

    static FoxyRenderSystem getNullable() {
        var lr = (IGetFoxyRenderSystem)Minecraft.getInstance().levelRenderer;
        if (lr == null) return null;
        return lr.foxy$getRenderSystem();
    }
}
