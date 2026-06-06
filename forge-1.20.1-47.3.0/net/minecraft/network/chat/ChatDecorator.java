//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface ChatDecorator {
    ChatDecorator PLAIN = (p_236950_, p_236951_) -> {
        return CompletableFuture.completedFuture(p_236951_);
    };

    CompletableFuture<Component> decorate(@Nullable ServerPlayer var1, Component var2);
}
