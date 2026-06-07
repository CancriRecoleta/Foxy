package com.github.foxy.client.mixin.sodium;

import com.github.foxy.client.compat.SemaphoreBlockImpersonator;
import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.common.thread.MultiThreadPrioritySemaphore;
import com.github.foxy.commonImpl.FoxyCommon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Semaphore;

@Mixin(targets={"me.jellysquid.mods.sodium.client.render.chunk.compile.executor.ChunkJobQueue"},remap = false)
public class MixinChunkJobQueue {
    @Unique private MultiThreadPrioritySemaphore.Block foxy$semaphoreBlock;

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(I)Ljava/util/concurrent/Semaphore;"))
    private Semaphore foxy$injectUnifiedPool(int permits) {
        var instance = FoxyCommon.getInstance();
        if (instance != null && !FoxyConfig.CONFIG.dontUseSodiumBuilderThreads) {
            this.foxy$semaphoreBlock = instance.getThreadPool().groupSemaphore.createBlock();
            return new SemaphoreBlockImpersonator(this.foxy$semaphoreBlock);
        }
        return new Semaphore(permits);
    }

    @Inject(method = "shutdown", at = @At("RETURN"))
    private void foxy$injectAtShutdown(CallbackInfoReturnable ci) {
        if (this.foxy$semaphoreBlock != null) {
            this.foxy$semaphoreBlock.free();
        }
    }
}
