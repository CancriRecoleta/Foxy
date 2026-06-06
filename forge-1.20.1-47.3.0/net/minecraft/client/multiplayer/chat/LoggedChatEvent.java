//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.multiplayer.chat;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage.Player;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage.System;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface LoggedChatEvent {
    Codec<LoggedChatEvent> CODEC = StringRepresentable.fromEnum(Type::values).dispatch(LoggedChatEvent::type, Type::codec);

    Type type();

    @OnlyIn(Dist.CLIENT)
    public static enum Type implements StringRepresentable {
        PLAYER("player", () -> {
            return Player.CODEC;
        }),
        SYSTEM("system", () -> {
            return System.CODEC;
        });

        private final String serializedName;
        private final Supplier<Codec<? extends LoggedChatEvent>> codec;

        private Type(String p_254335_, Supplier p_254115_) {
            this.serializedName = p_254335_;
            this.codec = p_254115_;
        }

        private Codec<? extends LoggedChatEvent> codec() {
            return (Codec)this.codec.get();
        }

        public String getSerializedName() {
            return this.serializedName;
        }
    }
}
