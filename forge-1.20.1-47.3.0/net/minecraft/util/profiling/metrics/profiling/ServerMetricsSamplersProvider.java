//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling.metrics.profiling;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class ServerMetricsSamplersProvider implements MetricsSamplerProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Set<MetricSampler> samplers = new ObjectOpenHashSet();
    private final ProfilerSamplerAdapter samplerFactory = new ProfilerSamplerAdapter();

    public ServerMetricsSamplersProvider(LongSupplier p_146180_, boolean p_146181_) {
        this.samplers.add(tickTimeSampler(p_146180_));
        if (p_146181_) {
            this.samplers.addAll(runtimeIndependentSamplers());
        }

    }

    public static Set<MetricSampler> runtimeIndependentSamplers() {
        ImmutableSet.Builder<MetricSampler> $$0 = ImmutableSet.builder();

        try {
            CpuStats $$1 = new CpuStats();
            Stream var10000 = IntStream.range(0, $$1.nrOfCpus).mapToObj((p_146185_) -> {
                return MetricSampler.create("cpu#" + p_146185_, MetricCategory.CPU, () -> {
                    return $$1.loadForCpu(p_146185_);
                });
            });
            Objects.requireNonNull($$0);
            var10000.forEach($$0::add);
        } catch (Throwable var2) {
            Throwable $$2 = var2;
            LOGGER.warn("Failed to query cpu, no cpu stats will be recorded", $$2);
        }

        $$0.add(MetricSampler.create("heap MiB", MetricCategory.JVM, () -> {
            return (double)((float)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576.0F);
        }));
        $$0.addAll(MetricsRegistry.INSTANCE.getRegisteredSamplers());
        return $$0.build();
    }

    public Set<MetricSampler> samplers(Supplier<ProfileCollector> p_146191_) {
        this.samplers.addAll(this.samplerFactory.newSamplersFoundInProfiler(p_146191_));
        return this.samplers;
    }

    public static MetricSampler tickTimeSampler(final LongSupplier p_146189_) {
        Stopwatch $$1 = Stopwatch.createUnstarted(new Ticker() {
            public long read() {
                return p_146189_.getAsLong();
            }
        });
        ToDoubleFunction<Stopwatch> $$2 = (p_146187_) -> {
            if (p_146187_.isRunning()) {
                p_146187_.stop();
            }

            long $$1 = p_146187_.elapsed(TimeUnit.NANOSECONDS);
            p_146187_.reset();
            return (double)$$1;
        };
        MetricSampler.ValueIncreasedByPercentage $$3 = new MetricSampler.ValueIncreasedByPercentage(2.0F);
        return MetricSampler.builder("ticktime", MetricCategory.TICK_LOOP, $$2, $$1).withBeforeTick(Stopwatch::start).withThresholdAlert($$3).build();
    }

    static class CpuStats {
        private final SystemInfo systemInfo = new SystemInfo();
        private final CentralProcessor processor;
        public final int nrOfCpus;
        private long[][] previousCpuLoadTick;
        private double[] currentLoad;
        private long lastPollMs;

        CpuStats() {
            this.processor = this.systemInfo.getHardware().getProcessor();
            this.nrOfCpus = this.processor.getLogicalProcessorCount();
            this.previousCpuLoadTick = this.processor.getProcessorCpuLoadTicks();
            this.currentLoad = this.processor.getProcessorCpuLoadBetweenTicks(this.previousCpuLoadTick);
        }

        public double loadForCpu(int p_146208_) {
            long $$1 = System.currentTimeMillis();
            if (this.lastPollMs == 0L || this.lastPollMs + 501L < $$1) {
                this.currentLoad = this.processor.getProcessorCpuLoadBetweenTicks(this.previousCpuLoadTick);
                this.previousCpuLoadTick = this.processor.getProcessorCpuLoadTicks();
                this.lastPollMs = $$1;
            }

            return this.currentLoad[p_146208_] * 100.0;
        }
    }
}
