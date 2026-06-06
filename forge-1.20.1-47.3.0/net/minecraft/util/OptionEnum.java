//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import net.minecraft.network.chat.Component;

public interface OptionEnum {
    int getId();

    String getKey();

    default Component getCaption() {
        return Component.translatable(this.getKey());
    }
}
