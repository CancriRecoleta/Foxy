//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands;

import net.minecraft.network.chat.Component;

public interface CommandSource {
    CommandSource NULL = new CommandSource() {
        public void sendSystemMessage(Component p_230799_) {
        }

        public boolean acceptsSuccess() {
            return false;
        }

        public boolean acceptsFailure() {
            return false;
        }

        public boolean shouldInformAdmins() {
            return false;
        }
    };

    void sendSystemMessage(Component var1);

    boolean acceptsSuccess();

    boolean acceptsFailure();

    boolean shouldInformAdmins();

    default boolean alwaysAccepts() {
        return false;
    }
}
