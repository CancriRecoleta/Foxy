//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import net.minecraft.network.chat.Component;

public interface ProgressListener {
    void progressStartNoAbort(Component var1);

    void progressStart(Component var1);

    void progressStage(Component var1);

    void progressStagePercentage(int var1);

    void stop();
}
