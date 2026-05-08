package com.github.foxy.client.mixin.minecraft;

import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.client.FoxyClient;
import com.github.foxy.client.core.FoxyRenderSystem;
import com.github.foxy.client.core.IGetFoxyRenderSystem;
import com.github.foxy.client.core.util.IrisUtil;
import com.github.foxy.common.Logger;
import com.github.foxy.commonImpl.FoxyCommon;
import com.github.foxy.commonImpl.WorldIdentifier;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements IGetFoxyRenderSystem {
    @Shadow
    private @Nullable ClientLevel level;

    @Unique
    private FoxyRenderSystem foxy$renderer;

    @Override
    public FoxyRenderSystem Foxy$getRenderSystem() {
        return this.foxy$renderer;
    }

    @Inject(method = "allChanged()V", at = @At("RETURN"))
    private void foxy$reloadRenderer(CallbackInfo ci) {
        this.Foxy$shutdownRenderer();
        if (this.level != null) {
            this.Foxy$createRenderer();
        }
    }

    @Inject(method = "setLevel", at = @At("HEAD"))
    private void foxy$captureSetLevel(ClientLevel level, CallbackInfo ci) {
        if (this.level != level) {
            this.Foxy$shutdownRenderer();
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void foxy$close(CallbackInfo ci) {
        this.Foxy$shutdownRenderer();
    }

    @Override
    public void Foxy$shutdownRenderer() {
        if (this.foxy$renderer != null) {
            this.foxy$renderer.shutdown();
            this.foxy$renderer = null;
        }
    }

    @Override
    public void Foxy$createRenderer() {
        if (this.foxy$renderer != null) {
            throw new IllegalStateException("Cannot create multiple Foxy renderers");
        }
        if (!FoxyConfig.CONFIG.enabled || !FoxyConfig.CONFIG.isRenderingEnabled()) {
            Logger.info("Not creating Foxy renderer due to disabled config");
            return;
        }
        if (this.level == null) {
            Logger.error("Not creating Foxy renderer due to null level");
            return;
        }

        var instance = FoxyCommon.getInstance();
        if (instance == null) {
            Logger.error("Not creating Foxy renderer due to null FoxyInstance");
            return;
        }

        var activeId = instance.identifier();
        var worldId = activeId.levelKey().equals(this.level.dimension())
                ? activeId
                : new WorldIdentifier(activeId.namespace(), this.level.dimension(), activeId.biomeSeed());
        var world = instance.getOrCreateEngine(worldId);
        if (world == null) {
            Logger.error("Not creating Foxy renderer due to null WorldEngine");
            return;
        }

        try {
            FoxyClient.initFoxyClient();
            this.foxy$renderer = new FoxyRenderSystem(world, instance.getServiceManager());
        } catch (RuntimeException e) {
            if (IrisUtil.irisShaderPackEnabled()) {
                Logger.error("Foxy renderer creation failed with Oculus shaders enabled; disabling shaders", e);
                IrisUtil.disableIrisShaders();
            } else {
                throw e;
            }
        }
    }
}
