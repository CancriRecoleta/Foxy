//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling.metrics.profiling;

import net.minecraft.util.profiling.ProfilerFiller;

public interface MetricsRecorder {
    void end();

    void cancel();

    void startTick();

    boolean isRecording();

    ProfilerFiller getProfiler();

    void endTick();
}
