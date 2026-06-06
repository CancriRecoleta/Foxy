//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs.resources;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class SimplePreparableReloadListener<T> implements PreparableReloadListener {
    public SimplePreparableReloadListener() {
    }

    public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_10780_, ResourceManager p_10781_, ProfilerFiller p_10782_, ProfilerFiller p_10783_, Executor p_10784_, Executor p_10785_) {
        CompletableFuture var10000 = CompletableFuture.supplyAsync(() -> {
            return this.prepare(p_10781_, p_10782_);
        }, p_10784_);
        Objects.requireNonNull(p_10780_);
        return var10000.thenCompose(p_10780_::wait).thenAcceptAsync((p_10792_) -> {
            this.apply(p_10792_, p_10781_, p_10783_);
        }, p_10785_);
    }

    protected abstract T prepare(ResourceManager var1, ProfilerFiller var2);

    protected abstract void apply(T var1, ResourceManager var2, ProfilerFiller var3);
}
