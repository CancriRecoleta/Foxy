package com.github.foxy.client.mixin.minecraft;

import com.github.foxy.client.FoxyClientInstance;
import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.client.core.IGetFoxyRenderSystem;
import com.github.foxy.client.core.FoxyRenderSystem;
import com.github.foxy.client.core.util.IrisUtil;
import com.github.foxy.common.Logger;
import com.github.foxy.common.world.WorldEngine;
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

// priority < the default 1000 so this mixin applies first and its allChanged @At("RETURN") callback runs
// before Embeddium's WorldRendererMixin (which also injects allChanged @RETURN at default priority to
// reload its terrain): we must recreate the Foxy renderer ahead of the Sodium/Embeddium reload. This is
// the Mixin-0.8.5 analog of upstream's @Inject(order = 900), which Forge's Mixin 0.8.5 does not support.
@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class MixinLevelRenderer implements IGetFoxyRenderSystem {
    @Shadow private @Nullable ClientLevel level;
    @Unique private FoxyRenderSystem renderer;

    @Override
    public FoxyRenderSystem foxy$getRenderSystem() {
        return this.renderer;
    }

    @Inject(method = "allChanged()V", at = @At("RETURN"))//Inject before sodium/Embeddium — ordering enforced by the @Mixin(priority = 900) above
    private void foxy$reloadFoxyRenderer(CallbackInfo ci) {
        this.foxy$shutdownRenderer();
        if (this.level != null) {
            this.foxy$createRenderer();
        }
    }

    @Inject(method = "setLevel", at = @At("HEAD"))
    private void foxy$captureSetWorld(ClientLevel world, CallbackInfo ci) {
        if (this.level != world) {
            this.foxy$shutdownRenderer();
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void foxy$injectClose(CallbackInfo ci) {
        this.foxy$shutdownRenderer();
    }

    @Override
    public void foxy$shutdownRenderer() {
        if (this.renderer != null) {
            this.renderer.shutdown();
            this.renderer = null;
        }
    }

    @Override
    public void foxy$createRenderer() {
        if (this.renderer != null) throw new IllegalStateException("Cannot have multiple renderers");
        if (!FoxyConfig.CONFIG.enabled) {
            Logger.info("Not creating renderer due to disabled");
            return;
        }
        if (!FoxyConfig.CONFIG.isRenderingEnabled()) {
            Logger.info("Not creating renderer due to disabled rendering");
            return;
        }
        if (this.level == null) {
            Logger.error("Not creating renderer due to null world");
            return;
        }
        var instance = (FoxyClientInstance)FoxyCommon.getInstance();
        if (instance == null) {
            Logger.error("Not creating renderer due to null instance");
            return;
        }
        WorldEngine world = WorldIdentifier.ofEngine(this.level);
        if (world == null) {
            Logger.error("Null world selected");
            return;
        }
        try {
            this.renderer = new FoxyRenderSystem(world, instance.getServiceManager());
        } catch (RuntimeException e) {
            if (IrisUtil.irisShaderPackEnabled()) {
                IrisUtil.disableIrisShaders();
            } else {
                throw e;
            }
        }
        instance.updateDedicatedThreads();
    }
}
