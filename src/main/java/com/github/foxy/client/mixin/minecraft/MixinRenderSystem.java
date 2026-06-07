package com.github.foxy.client.mixin.minecraft;


import com.mojang.blaze3d.systems.RenderSystem;
import com.github.foxy.client.FoxyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Thanks iris for making me need todo this ;-; _irritater_
// priority < the default 1000 so this mixin is applied first and its @At("RETURN") callback runs before
// Oculus's MixinRenderSystem (also injects initRenderer @RETURN at default priority): we must initialise
// Foxy's GL systems before Iris loads its shaderpack. This is the Mixin-0.8.5 analog of upstream's
// @Inject(order = 900), which Forge's Mixin 0.8.5 does not support.
@Mixin(value = RenderSystem.class, priority = 900)
public class MixinRenderSystem {
    // 1.20.1 RenderSystem.initRenderer is (int debugVerbosity, boolean synchronous) — none of the
    // 1.21.5 ShaderSource/renderDebugLabels args.
    @Inject(method = "initRenderer(IZ)V", at = @At("RETURN"))
    private static void foxy$injectInit(int debugVerbosity, boolean synchronous, CallbackInfo ci) {
        FoxyClient.initFoxyClient();
    }
}
