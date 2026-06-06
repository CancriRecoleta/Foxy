//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import org.slf4j.Logger;

public class SingleTickProfiler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final LongSupplier realTime;
    private final long saveThreshold;
    private int tick;
    private final File location;
    private ProfileCollector profiler;

    public SingleTickProfiler(LongSupplier p_145963_, String p_145964_, long p_145965_) {
        this.profiler = InactiveProfiler.INSTANCE;
        this.realTime = p_145963_;
        this.location = new File("debug", p_145964_);
        this.saveThreshold = p_145965_;
    }

    public ProfilerFiller startTick() {
        this.profiler = new ActiveProfiler(this.realTime, () -> {
            return this.tick;
        }, false);
        ++this.tick;
        return this.profiler;
    }

    public void endTick() {
        if (this.profiler != InactiveProfiler.INSTANCE) {
            ProfileResults $$0 = this.profiler.getResults();
            this.profiler = InactiveProfiler.INSTANCE;
            if ($$0.getNanoDuration() >= this.saveThreshold) {
                File $$1 = new File(this.location, "tick-results-" + Util.getFilenameFormattedDateTime() + ".txt");
                $$0.saveResults($$1.toPath());
                LOGGER.info("Recorded long tick -- wrote info to: {}", $$1.getAbsolutePath());
            }

        }
    }

    @Nullable
    public static SingleTickProfiler createTickProfiler(String p_18633_) {
        return null;
    }

    public static ProfilerFiller decorateFiller(ProfilerFiller p_18630_, @Nullable SingleTickProfiler p_18631_) {
        return p_18631_ != null ? ProfilerFiller.tee(p_18631_.startTick(), p_18630_) : p_18630_;
    }
}
