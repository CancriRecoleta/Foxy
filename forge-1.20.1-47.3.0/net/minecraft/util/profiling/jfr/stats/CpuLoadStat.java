//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record CpuLoadStat(double jvm, double userJvm, double system) {
    public CpuLoadStat(double jvm, double userJvm, double system) {
        this.jvm = jvm;
        this.userJvm = userJvm;
        this.system = system;
    }

    public static CpuLoadStat from(RecordedEvent p_185623_) {
        return new CpuLoadStat((double)p_185623_.getFloat("jvmSystem"), (double)p_185623_.getFloat("jvmUser"), (double)p_185623_.getFloat("machineTotal"));
    }

    public double jvm() {
        return this.jvm;
    }

    public double userJvm() {
        return this.userJvm;
    }

    public double system() {
        return this.system;
    }
}
