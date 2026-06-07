package com.github.foxy.client.mixin.sodium;

import com.github.foxy.commonImpl.FoxyCommon;
import com.github.foxy.commonImpl.FoxyInstance;
import me.jellysquid.mods.sodium.client.gl.device.CommandList;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public class MixinSodiumWorldRenderer {
    @Inject(method = "initRenderer", at = @At("TAIL"), remap = false)
    private void foxy$injectThreadUpdate(CommandList cl, CallbackInfo ci) {
        var vi = FoxyCommon.getInstance();
        if (vi != null) vi.updateDedicatedThreads();
    }
}
