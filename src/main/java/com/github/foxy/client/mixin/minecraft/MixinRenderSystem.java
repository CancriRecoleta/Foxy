package com.github.foxy.client.mixin.minecraft;


import com.mojang.blaze3d.systems.RenderSystem;
import com.github.foxy.client.VoxyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Thanks iris for making me need todo this ;-; _irritater_
@Mixin(RenderSystem.class)
public class MixinRenderSystem {
    // 1.20.1 RenderSystem.initRenderer is (int debugVerbosity, boolean synchronous) — none of the
    // 1.21.5 ShaderSource/renderDebugLabels args. Mixin 0.8.5 (shipped with Forge 1.20.1) has no
    // @Inject(order=...), so ordering relative to Oculus is left to mixin priority instead.
    @Inject(method = "initRenderer(IZ)V", at = @At("RETURN"))
    private static void voxy$injectInit(int debugVerbosity, boolean synchronous, CallbackInfo ci) {
        VoxyClient.initVoxyClient();
    }
}
