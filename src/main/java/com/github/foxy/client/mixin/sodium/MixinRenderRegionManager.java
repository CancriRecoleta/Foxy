package com.github.foxy.client.mixin.sodium;

import com.github.foxy.client.core.IGetVoxyRenderSystem;
import me.jellysquid.mods.sodium.client.render.chunk.region.RenderRegionManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RenderRegionManager.class, remap = false)
public class MixinRenderRegionManager {
    @Redirect(method = "uploadResults(Lme/jellysquid/mods/sodium/client/gl/device/CommandList;Lme/jellysquid/mods/sodium/client/render/chunk/region/RenderRegion;Ljava/util/Collection;)V", at = @At(value = "INVOKE", target = "Ljava/lang/Math;toIntExact(J)I"), remap = false)
    private int voxy$cancelFade(long time) {
        var vrs = ((IGetVoxyRenderSystem)(Minecraft.getInstance().levelRenderer)).voxy$getRenderSystem();
        if (vrs!=null) {
            return -2;
        } else {
            return Math.toIntExact(time);
        }
    }
}