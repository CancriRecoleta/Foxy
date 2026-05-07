package com.github.foxy.client.core;

import net.minecraft.client.Minecraft;

public interface IGetFoxyRenderSystem {
    FoxyRenderSystem Foxy$getRenderSystem();
    void Foxy$shutdownRenderer();
    void Foxy$createRenderer();

    static FoxyRenderSystem getNullable() {
        var lr = (IGetFoxyRenderSystem)Minecraft.getInstance().levelRenderer;
        if (lr == null) return null;
        return lr.Foxy$getRenderSystem();
    }
}
