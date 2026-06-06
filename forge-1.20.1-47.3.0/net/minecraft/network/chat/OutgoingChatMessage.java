//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import net.minecraft.server.level.ServerPlayer;

public interface OutgoingChatMessage {
    Component content();

    void sendToPlayer(ServerPlayer var1, boolean var2, ChatType.Bound var3);

    static OutgoingChatMessage create(PlayerChatMessage p_249173_) {
        return (OutgoingChatMessage)(p_249173_.isSystem() ? new Disguised(p_249173_.decoratedContent()) : new Player(p_249173_));
    }

    public static record Disguised(Component content) implements OutgoingChatMessage {
        public Disguised(Component content) {
            this.content = content;
        }

        public Component content() {
            return this.content;
        }

        public void sendToPlayer(ServerPlayer p_249237_, boolean p_249574_, ChatType.Bound p_250880_) {
            p_249237_.connection.sendDisguisedChatMessage(this.content, p_250880_);
        }
    }

    public static record Player(PlayerChatMessage message) implements OutgoingChatMessage {
        public Player(PlayerChatMessage message) {
            this.message = message;
        }

        public Component content() {
            return this.message.decoratedContent();
        }

        public void sendToPlayer(ServerPlayer p_249642_, boolean p_251123_, ChatType.Bound p_251482_) {
            PlayerChatMessage $$3 = this.message.filter(p_251123_);
            if (!$$3.isFullyFiltered()) {
                p_249642_.connection.sendPlayerChatMessage($$3, p_251482_);
            }

        }

        public PlayerChatMessage message() {
            return this.message;
        }
    }
}
