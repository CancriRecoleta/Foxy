package com.github.foxy.client.mixin.minecraft;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels Minecraft's per-tick "world thumbnail" auto-screenshot.
 *
 * <h2>Why</h2>
 * <p>{@code GameRenderer.tryTakeScreenshotIfNeeded()} schedules
 * {@code GameRenderer.takeAutoScreenshot} which calls
 * {@code NativeImage.downloadTexture} → {@code GL11.glGetTexImage}. On NVIDIA
 * 596.36 + Embeddium 0.3 + Oculus 1.7 + Foxy this segfaults inside
 * {@code nvoglv64.dll+0xb97827}. The crash has been observed reproducibly
 * around the 44–60-second mark after world join (the autosave timer for the
 * thumbnail). The vanilla feature is purely cosmetic (saves
 * {@code <save>/icon.png}) so cancelling it on every Foxy install is a free fix.</p>
 *
 * <h2>Why not target the deeper {@code takeAutoScreenshot} or the GL call</h2>
 * <p>Cancelling at the highest entry point ({@code tryTakeScreenshotIfNeeded})
 * also skips the "screenshot is overdue" timer logic, so Foxy isn't paying for a
 * timer wake-up every tick. Cheaper than an inner-method cancel.</p>
 *
 * <p>If a future driver version stops crashing here, this mixin can be removed
 * without touching anything else.</p>
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "tryTakeScreenshotIfNeeded", at = @At("HEAD"), cancellable = true)
    private void foxy$skipAutoScreenshot(CallbackInfo ci) {
        ci.cancel();
    }
}
