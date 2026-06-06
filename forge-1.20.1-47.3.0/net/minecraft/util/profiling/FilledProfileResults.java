//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class FilledProfileResults implements ProfileResults {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ProfilerPathEntry EMPTY = new ProfilerPathEntry() {
        public long getDuration() {
            return 0L;
        }

        public long getMaxDuration() {
            return 0L;
        }

        public long getCount() {
            return 0L;
        }

        public Object2LongMap<String> getCounters() {
            return Object2LongMaps.emptyMap();
        }
    };
    private static final Splitter SPLITTER = Splitter.on('\u001e');
    private static final Comparator<Map.Entry<String, CounterCollector>> COUNTER_ENTRY_COMPARATOR = Entry.comparingByValue(Comparator.comparingLong((p_18489_) -> {
        return p_18489_.totalValue;
    })).reversed();
    private final Map<String, ? extends ProfilerPathEntry> entries;
    private final long startTimeNano;
    private final int startTimeTicks;
    private final long endTimeNano;
    private final int endTimeTicks;
    private final int tickDuration;

    public FilledProfileResults(Map<String, ? extends ProfilerPathEntry> p_18464_, long p_18465_, int p_18466_, long p_18467_, int p_18468_) {
        this.entries = p_18464_;
        this.startTimeNano = p_18465_;
        this.startTimeTicks = p_18466_;
        this.endTimeNano = p_18467_;
        this.endTimeTicks = p_18468_;
        this.tickDuration = p_18468_ - p_18466_;
    }

    private ProfilerPathEntry getEntry(String p_18526_) {
        ProfilerPathEntry $$1 = (ProfilerPathEntry)this.entries.get(p_18526_);
        return $$1 != null ? $$1 : EMPTY;
    }

    public List<ResultField> getTimes(String p_18493_) {
        String $$1 = p_18493_;
        ProfilerPathEntry $$2 = this.getEntry("root");
        long $$3 = $$2.getDuration();
        ProfilerPathEntry $$4 = this.getEntry(p_18493_);
        long $$5 = $$4.getDuration();
        long $$6 = $$4.getCount();
        List<ResultField> $$7 = Lists.newArrayList();
        if (!p_18493_.isEmpty()) {
            p_18493_ = p_18493_ + "\u001e";
        }

        long $$8 = 0L;
        Iterator var14 = this.entries.keySet().iterator();

        while(var14.hasNext()) {
            String $$9 = (String)var14.next();
            if (isDirectChild(p_18493_, $$9)) {
                $$8 += this.getEntry($$9).getDuration();
            }
        }

        float $$10 = (float)$$8;
        if ($$8 < $$5) {
            $$8 = $$5;
        }

        if ($$3 < $$8) {
            $$3 = $$8;
        }

        Iterator var26 = this.entries.keySet().iterator();

        while(var26.hasNext()) {
            String $$11 = (String)var26.next();
            if (isDirectChild(p_18493_, $$11)) {
                ProfilerPathEntry $$12 = this.getEntry($$11);
                long $$13 = $$12.getDuration();
                double $$14 = (double)$$13 * 100.0 / (double)$$8;
                double $$15 = (double)$$13 * 100.0 / (double)$$3;
                String $$16 = $$11.substring(p_18493_.length());
                $$7.add(new ResultField($$16, $$14, $$15, $$12.getCount()));
            }
        }

        if ((float)$$8 > $$10) {
            $$7.add(new ResultField("unspecified", (double)((float)$$8 - $$10) * 100.0 / (double)$$8, (double)((float)$$8 - $$10) * 100.0 / (double)$$3, $$6));
        }

        Collections.sort($$7);
        $$7.add(0, new ResultField($$1, 100.0, (double)$$8 * 100.0 / (double)$$3, $$6));
        return $$7;
    }

    private static boolean isDirectChild(String p_18495_, String p_18496_) {
        return p_18496_.length() > p_18495_.length() && p_18496_.startsWith(p_18495_) && p_18496_.indexOf(30, p_18495_.length() + 1) < 0;
    }

    private Map<String, CounterCollector> getCounterValues() {
        Map<String, CounterCollector> $$0 = Maps.newTreeMap();
        this.entries.forEach((p_18512_, p_18513_) -> {
            Object2LongMap<String> $$3 = p_18513_.getCounters();
            if (!$$3.isEmpty()) {
                List<String> $$4 = SPLITTER.splitToList(p_18512_);
                $$3.forEach((p_145944_, p_145945_) -> {
                    ((CounterCollector)$$0.computeIfAbsent(p_145944_, (p_145947_) -> {
                        return new CounterCollector();
                    })).addValue($$4.iterator(), p_145945_);
                });
            }

        });
        return $$0;
    }

    public long getStartTimeNano() {
        return this.startTimeNano;
    }

    public int getStartTimeTicks() {
        return this.startTimeTicks;
    }

    public long getEndTimeNano() {
        return this.endTimeNano;
    }

    public int getEndTimeTicks() {
        return this.endTimeTicks;
    }

    public boolean saveResults(Path p_145940_) {
        Writer $$1 = null;

        boolean var4;
        try {
            Files.createDirectories(p_145940_.getParent());
            $$1 = Files.newBufferedWriter(p_145940_, StandardCharsets.UTF_8);
            $$1.write(this.getProfilerResults(this.getNanoDuration(), this.getTickDuration()));
            boolean var10 = true;
            return var10;
        } catch (Throwable var8) {
            Throwable $$2 = var8;
            LOGGER.error("Could not save profiler results to {}", p_145940_, $$2);
            var4 = false;
        } finally {
            IOUtils.closeQuietly($$1);
        }

        return var4;
    }

    protected String getProfilerResults(long p_18486_, int p_18487_) {
        StringBuilder $$2 = new StringBuilder();
        $$2.append("---- Minecraft Profiler Results ----\n");
        $$2.append("// ");
        $$2.append(getComment());
        $$2.append("\n\n");
        $$2.append("Version: ").append(SharedConstants.getCurrentVersion().getId()).append('\n');
        $$2.append("Time span: ").append(p_18486_ / 1000000L).append(" ms\n");
        $$2.append("Tick span: ").append(p_18487_).append(" ticks\n");
        $$2.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", (float)p_18487_ / ((float)p_18486_ / 1.0E9F))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        $$2.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.appendProfilerResults(0, "root", $$2);
        $$2.append("--- END PROFILE DUMP ---\n\n");
        Map<String, CounterCollector> $$3 = this.getCounterValues();
        if (!$$3.isEmpty()) {
            $$2.append("--- BEGIN COUNTER DUMP ---\n\n");
            this.appendCounters($$3, $$2, p_18487_);
            $$2.append("--- END COUNTER DUMP ---\n\n");
        }

        return $$2.toString();
    }

    public String getProfilerResults() {
        StringBuilder $$0 = new StringBuilder();
        this.appendProfilerResults(0, "root", $$0);
        return $$0.toString();
    }

    private static StringBuilder indentLine(StringBuilder p_18498_, int p_18499_) {
        p_18498_.append(String.format(Locale.ROOT, "[%02d] ", p_18499_));

        for(int $$2 = 0; $$2 < p_18499_; ++$$2) {
            p_18498_.append("|   ");
        }

        return p_18498_;
    }

    private void appendProfilerResults(int p_18482_, String p_18483_, StringBuilder p_18484_) {
        List<ResultField> $$3 = this.getTimes(p_18483_);
        Object2LongMap<String> $$4 = ((ProfilerPathEntry)ObjectUtils.firstNonNull(new ProfilerPathEntry[]{(ProfilerPathEntry)this.entries.get(p_18483_), EMPTY})).getCounters();
        $$4.forEach((p_18508_, p_18509_) -> {
            indentLine(p_18484_, p_18482_).append('#').append(p_18508_).append(' ').append(p_18509_).append('/').append(p_18509_ / (long)this.tickDuration).append('\n');
        });
        if ($$3.size() >= 3) {
            for(int $$5 = 1; $$5 < $$3.size(); ++$$5) {
                ResultField $$6 = (ResultField)$$3.get($$5);
                indentLine(p_18484_, p_18482_).append($$6.name).append('(').append($$6.count).append('/').append(String.format(Locale.ROOT, "%.0f", (float)$$6.count / (float)this.tickDuration)).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", $$6.percentage)).append("%/").append(String.format(Locale.ROOT, "%.2f", $$6.globalPercentage)).append("%\n");
                if (!"unspecified".equals($$6.name)) {
                    try {
                        this.appendProfilerResults(p_18482_ + 1, p_18483_ + "\u001e" + $$6.name, p_18484_);
                    } catch (Exception var9) {
                        Exception $$7 = var9;
                        p_18484_.append("[[ EXCEPTION ").append($$7).append(" ]]");
                    }
                }
            }

        }
    }

    private void appendCounterResults(int p_18476_, String p_18477_, CounterCollector p_18478_, int p_18479_, StringBuilder p_18480_) {
        indentLine(p_18480_, p_18476_).append(p_18477_).append(" total:").append(p_18478_.selfValue).append('/').append(p_18478_.totalValue).append(" average: ").append(p_18478_.selfValue / (long)p_18479_).append('/').append(p_18478_.totalValue / (long)p_18479_).append('\n');
        p_18478_.children.entrySet().stream().sorted(COUNTER_ENTRY_COMPARATOR).forEach((p_18474_) -> {
            this.appendCounterResults(p_18476_ + 1, (String)p_18474_.getKey(), (CounterCollector)p_18474_.getValue(), p_18479_, p_18480_);
        });
    }

    private void appendCounters(Map<String, CounterCollector> p_18515_, StringBuilder p_18516_, int p_18517_) {
        p_18515_.forEach((p_18503_, p_18504_) -> {
            p_18516_.append("-- Counter: ").append(p_18503_).append(" --\n");
            this.appendCounterResults(0, "root", (CounterCollector)p_18504_.children.get("root"), p_18517_, p_18516_);
            p_18516_.append("\n\n");
        });
    }

    private static String getComment() {
        String[] $$0 = new String[]{"I'd Rather Be Surfing", "Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

        try {
            return $$0[(int)(Util.getNanos() % (long)$$0.length)];
        } catch (Throwable var2) {
            return "Witty comment unavailable :(";
        }
    }

    public int getTickDuration() {
        return this.tickDuration;
    }

    static class CounterCollector {
        long selfValue;
        long totalValue;
        final Map<String, CounterCollector> children = Maps.newHashMap();

        CounterCollector() {
        }

        public void addValue(Iterator<String> p_18548_, long p_18549_) {
            this.totalValue += p_18549_;
            if (!p_18548_.hasNext()) {
                this.selfValue += p_18549_;
            } else {
                ((CounterCollector)this.children.computeIfAbsent((String)p_18548_.next(), (p_18546_) -> {
                    return new CounterCollector();
                })).addValue(p_18548_, p_18549_);
            }

        }
    }
}
