//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.fml.core;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraftforge.fml.IModStateTransition;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ThreadSelector;
import net.minecraftforge.fml.IModStateTransition.EventGenerator;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

record ParallelTransition(ModLoadingStage stage, Class<? extends ParallelDispatchEvent> event) implements IModStateTransition {
    ParallelTransition(ModLoadingStage stage, Class<? extends ParallelDispatchEvent> event) {
        this.stage = stage;
        this.event = event;
    }

    public Supplier<Stream<IModStateTransition.EventGenerator<?>>> eventFunctionStream() {
        return () -> {
            return Stream.of(EventGenerator.fromFunction(LamdbaExceptionUtils.rethrowFunction((mc) -> {
                return (ParallelDispatchEvent)this.event.getConstructor(ModContainer.class, ModLoadingStage.class).newInstance(mc, this.stage);
            })));
        };
    }

    public ThreadSelector threadSelector() {
        return ThreadSelector.PARALLEL;
    }

    public BiFunction<Executor, CompletableFuture<Void>, CompletableFuture<Void>> finalActivityGenerator() {
        return (e, prev) -> {
            return prev.thenApplyAsync((t) -> {
                this.stage.getDeferredWorkQueue().runTasks();
                return t;
            }, e);
        };
    }

    public BiFunction<Executor, ? extends IModStateTransition.EventGenerator<?>, CompletableFuture<Void>> preDispatchHook() {
        return (t, f) -> {
            return CompletableFuture.completedFuture((Object)null);
        };
    }

    public BiFunction<Executor, ? extends IModStateTransition.EventGenerator<?>, CompletableFuture<Void>> postDispatchHook() {
        return (t, f) -> {
            return CompletableFuture.completedFuture((Object)null);
        };
    }

    public ModLoadingStage stage() {
        return this.stage;
    }

    public Class<? extends ParallelDispatchEvent> event() {
        return this.event;
    }
}
