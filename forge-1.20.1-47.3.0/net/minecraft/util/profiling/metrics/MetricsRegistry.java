//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling.metrics;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class MetricsRegistry {
    public static final MetricsRegistry INSTANCE = new MetricsRegistry();
    private final WeakHashMap<ProfilerMeasured, Void> measuredInstances = new WeakHashMap();

    private MetricsRegistry() {
    }

    public void add(ProfilerMeasured p_146073_) {
        this.measuredInstances.put(p_146073_, (Object)null);
    }

    public List<MetricSampler> getRegisteredSamplers() {
        Map<String, List<MetricSampler>> $$0 = (Map)this.measuredInstances.keySet().stream().flatMap((p_146079_) -> {
            return p_146079_.profiledMetrics().stream();
        }).collect(Collectors.groupingBy(MetricSampler::getName));
        return aggregateDuplicates($$0);
    }

    private static List<MetricSampler> aggregateDuplicates(Map<String, List<MetricSampler>> p_146077_) {
        return (List)p_146077_.entrySet().stream().map((p_146075_) -> {
            String $$1 = (String)p_146075_.getKey();
            List<MetricSampler> $$2 = (List)p_146075_.getValue();
            return (MetricSampler)($$2.size() > 1 ? new AggregatedMetricSampler($$1, $$2) : (MetricSampler)$$2.get(0));
        }).collect(Collectors.toList());
    }

    private static class AggregatedMetricSampler extends MetricSampler {
        private final List<MetricSampler> delegates;

        AggregatedMetricSampler(String p_146082_, List<MetricSampler> p_146083_) {
            super(p_146082_, ((MetricSampler)p_146083_.get(0)).getCategory(), () -> {
                return averageValueFromDelegates(p_146083_);
            }, () -> {
                beforeTick(p_146083_);
            }, thresholdTest(p_146083_));
            this.delegates = p_146083_;
        }

        private static MetricSampler.ThresholdTest thresholdTest(List<MetricSampler> p_146088_) {
            return (p_146091_) -> {
                return p_146088_.stream().anyMatch((p_146086_) -> {
                    return p_146086_.thresholdTest != null ? p_146086_.thresholdTest.test(p_146091_) : false;
                });
            };
        }

        private static void beforeTick(List<MetricSampler> p_146093_) {
            Iterator var1 = p_146093_.iterator();

            while(var1.hasNext()) {
                MetricSampler $$1 = (MetricSampler)var1.next();
                $$1.onStartTick();
            }

        }

        private static double averageValueFromDelegates(List<MetricSampler> p_146095_) {
            double $$1 = 0.0;

            MetricSampler $$2;
            for(Iterator var3 = p_146095_.iterator(); var3.hasNext(); $$1 += $$2.getSampler().getAsDouble()) {
                $$2 = (MetricSampler)var3.next();
            }

            return $$1 / (double)p_146095_.size();
        }

        public boolean equals(@Nullable Object p_146101_) {
            if (this == p_146101_) {
                return true;
            } else if (p_146101_ != null && this.getClass() == p_146101_.getClass()) {
                if (!super.equals(p_146101_)) {
                    return false;
                } else {
                    AggregatedMetricSampler $$1 = (AggregatedMetricSampler)p_146101_;
                    return this.delegates.equals($$1.delegates);
                }
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{super.hashCode(), this.delegates});
        }
    }
}
