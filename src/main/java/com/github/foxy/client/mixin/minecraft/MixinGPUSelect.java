package com.github.foxy.client.mixin.minecraft;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.github.foxy.client.GPUSelectorWindows2;
import com.github.foxy.common.util.ThreadUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinGPUSelect {
    // Mixin forbids @At("HEAD") on a constructor (it would precede the super() call); 1.20.1's
    // Minecraft constructor also doesn't call Options.save(), so inject at TAIL. The thread-priority
    // bump applies fine here; the opt-in Windows GPU selection is only meaningful when the
    // foxy.forceGpuSelectionIndex property is set.
    @Inject(method = "<init>", at = @At("TAIL"))
    private void foxy$injectInitWindow(GameConfig gc, CallbackInfo ci) {
        //System.load("C:\\Program Files\\RenderDoc\\renderdoc.dll");
        var prop = System.getProperty("foxy.forceGpuSelectionIndex", "NO");
        if (!prop.equals("NO")) {
            GPUSelectorWindows2.doSelector(Integer.parseInt(prop));
        }

        //Force the current thread priority to be realtime
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        ThreadUtils.SetSelfThreadPriorityWin32(ThreadUtils.WIN32_THREAD_PRIORITY_TIME_CRITICAL);
    }
}
