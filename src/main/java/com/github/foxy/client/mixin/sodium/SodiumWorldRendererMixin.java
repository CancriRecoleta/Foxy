package com.github.foxy.client.mixin.sodium;

import com.github.foxy.commonImpl.FoxyCommon;
import me.jellysquid.mods.sodium.client.gl.device.CommandList;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public final class SodiumWorldRendererMixin {
    @Inject(method = "initRenderer", at = @At("TAIL"))
    private void foxy$updateDedicatedThreads(CommandList commandList, CallbackInfo ci) {
        var instance = FoxyCommon.getInstance();
        if (instance != null) {
            instance.updateDedicatedThreads();
        }
    }
}
