//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.fml.event.lifecycle;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;

public class ParallelDispatchEvent extends ModLifecycleEvent {
    private final ModLoadingStage modLoadingStage;

    public ParallelDispatchEvent(ModContainer container, ModLoadingStage stage) {
        super(container);
        this.modLoadingStage = stage;
    }

    private Optional<DeferredWorkQueue> getQueue() {
        return DeferredWorkQueue.lookup(Optional.of(this.modLoadingStage));
    }

    public CompletableFuture<Void> enqueueWork(Runnable work) {
        return (CompletableFuture)this.getQueue().map((q) -> {
            return q.enqueueWork(this.getContainer(), work);
        }).orElseThrow(() -> {
            return new RuntimeException("No work queue found!");
        });
    }

    public <T> CompletableFuture<T> enqueueWork(Supplier<T> work) {
        return (CompletableFuture)this.getQueue().map((q) -> {
            return q.enqueueWork(this.getContainer(), work);
        }).orElseThrow(() -> {
            return new RuntimeException("No work queue found!");
        });
    }
}
