//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.chat.PlayerChatMessage;

public interface CommandSigningContext {
    CommandSigningContext ANONYMOUS = new CommandSigningContext() {
        @Nullable
        public PlayerChatMessage getArgument(String p_242898_) {
            return null;
        }
    };

    @Nullable
    PlayerChatMessage getArgument(String var1);

    public static record SignedArguments(Map<String, PlayerChatMessage> arguments) implements CommandSigningContext {
        public SignedArguments(Map<String, PlayerChatMessage> arguments) {
            this.arguments = arguments;
        }

        @Nullable
        public PlayerChatMessage getArgument(String p_242852_) {
            return (PlayerChatMessage)this.arguments.get(p_242852_);
        }

        public Map<String, PlayerChatMessage> arguments() {
            return this.arguments;
        }
    }
}
