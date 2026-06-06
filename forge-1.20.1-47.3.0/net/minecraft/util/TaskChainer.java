//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;

@FunctionalInterface
public interface TaskChainer {
    Logger LOGGER = LogUtils.getLogger();

    static TaskChainer immediate(Executor p_251122_) {
        return (p_248285_) -> {
            p_248285_.submit(p_251122_).exceptionally((p_242314_) -> {
                LOGGER.error("Task failed", p_242314_);
                return null;
            });
        };
    }

    void append(DelayedTask var1);

    public interface DelayedTask {
        CompletableFuture<?> submit(Executor var1);
    }
}
