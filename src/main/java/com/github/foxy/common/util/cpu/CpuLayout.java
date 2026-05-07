package com.github.foxy.common.util.cpu;

import com.github.foxy.common.Logger;
import com.github.foxy.common.util.ThreadUtils;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinNT;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.lwjgl.system.Platform;
import oshi.SystemInfo;

import java.util.Arrays;

/**
 * Static snapshot of the host CPU's logical-to-physical layout, used for thread
 * affinity decisions in the worker pool.
 *
 * <h2>Why this exists</h2>
 * <p>Modern CPUs ship with a mix of performance (P) and efficiency (E) cores; the
 * renderer's worker pool wants to pin its dedicated threads to P cores so they don't
 * end up scheduled on a 2&times;-slower E core when the OS happens to pick one.
 * On Linux this also keeps every worker on the same NUMA / processor group, avoiding
 * cross-socket cache penalties.</p>
 *
 * <h2>How the layout is built</h2>
 * <ul>
 *   <li><b>Windows</b>: {@code Kernel32.GetLogicalProcessorInformationEx} with
 *       {@code RelationProcessorCore} returns one entry per physical core, including
 *       SMT mask and efficiency class. The cleanroom code mirrors upstream's parse.</li>
 *   <li><b>Linux</b>: OSHI's {@code SystemInfo.getHardware().getProcessor()} provides
 *       the same data with a friendlier API.</li>
 *   <li><b>Other</b>: layout is {@code null}; {@link #getCoreCount()} falls back to
 *       {@link Runtime#availableProcessors()}.</li>
 * </ul>
 *
 * <h2>Sorting</h2>
 * <p>{@link #CORES} is sorted P-first, then by processor group, then by mask. So
 * {@code CORES[0..N)} is the prefered prefix to pin to.</p>
 *
 * <p>Cleanroom note: same logic as upstream Voxy. The cleanroom rewrite drops the
 * {@code main()} demo, narrows imports, and renames the inner records / fields to be
 * self-documenting.</p>
 */
public final class CpuLayout {
    private CpuLayout() {}

    /** OS-level affinity mask for one core; {@code group} is the Windows processor group. */
    public record Affinity(long mask, short group) {}

    /** One physical core, including its efficiency-class flag and affinity. */
    public record Core(boolean isEfficiency, Affinity affinity) {}

    /** Best-effort layout snapshot; {@code null} on platforms we can't introspect. */
    public static final Core[] CORES;

    static {
        Core[] cores = null;
        try {
            if (Platform.get() == Platform.WINDOWS) {
                cores = generateLayoutWindows();
            } else if (Platform.get() == Platform.LINUX) {
                cores = generateLayoutLinux();
            }
        } catch (Throwable t) {
            Logger.error("Failed to introspect CPU layout; falling back to null", t);
        }
        CORES = cores;
    }

    // ---- thread affinity ---------------------------------------------------------------

    /** Pins the calling thread to the union of {@code cores}' affinities. */
    public static void setThreadAffinity(Core... cores) {
        Affinity[] affinities = new Affinity[cores.length];
        for (int i = 0; i < cores.length; i++) {
            affinities[i] = cores[i].affinity;
        }
        setThreadAffinity(affinities);
    }

    /**
     * Pins the calling thread to the union of {@code affinities}. Coalesces masks
     * within each Windows processor group so a single API call suffices per group.
     */
    public static void setThreadAffinity(Affinity... affinities) {
        Platform platform = Platform.get();
        if (platform == Platform.WINDOWS) {
            long[] masks = new long[affinities.length];
            short[] groups = new short[affinities.length];
            Arrays.fill(groups, (short) -1);
            int distinctGroups = 0;
            for (Affinity a : affinities) {
                int idx;
                for (idx = 0; idx < distinctGroups && groups[idx] != a.group; idx++) {
                    // body intentionally empty
                }
                if (idx == distinctGroups) {
                    groups[idx] = a.group;
                    distinctGroups++;
                }
                masks[idx] |= a.mask;
            }
            ThreadUtils.SetThreadSelectedCpuSetMasksWin32(
                    Arrays.copyOf(masks, distinctGroups),
                    Arrays.copyOf(groups, distinctGroups));
        } else if (platform == Platform.LINUX) {
            // Linux's sched_setaffinity expects masks in group order.
            Affinity[] sorted = affinities.clone();
            Arrays.sort(sorted, (a, b) -> Short.compare(a.group, b.group));
            long[] masks = new long[sorted.length];
            for (int i = 0; i < sorted.length; i++) {
                masks[i] = sorted[i].mask;
            }
            ThreadUtils.schedSetaffinityLinux(masks);
        } else {
            Logger.error("CpuLayout.setThreadAffinity: unsupported platform " + platform);
        }
    }

    // ---- platform layout introspection ------------------------------------------------

    private static Core[] generateLayoutWindows() {
        Object[] raw = Kernel32Util.getLogicalProcessorInformationEx(
                WinNT.LOGICAL_PROCESSOR_RELATIONSHIP.RelationProcessorCore);

        // If every core reports efficiencyClass == 0 the chip is homogeneous; treat
        // them all as P cores.
        boolean homogeneous = true;
        for (Object coreO : raw) {
            WinNT.PROCESSOR_RELATIONSHIP core = (WinNT.PROCESSOR_RELATIONSHIP) coreO;
            if (core.efficiencyClass != 0) {
                homogeneous = false;
                break;
            }
        }

        Core[] out = new Core[raw.length];
        int i = 0;
        for (Object coreO : raw) {
            WinNT.PROCESSOR_RELATIONSHIP core = (WinNT.PROCESSOR_RELATIONSHIP) coreO;
            boolean smt = (core.flags & 1) == 1;
            byte efficiencyClass = core.efficiencyClass;
            if (core.groupMask.length != 1) {
                throw new IllegalStateException("Core spans multiple processor groups; unsupported");
            }
            long mask = core.groupMask[0].mask.longValue();
            if ((Long.bitCount(mask) > 1) != smt) {
                throw new IllegalStateException("SMT bit and mask bit count disagree");
            }
            boolean isEfficiency = (!homogeneous) && efficiencyClass == 0;
            out[i++] = new Core(isEfficiency, new Affinity(mask, core.groupMask[0].group));
        }
        sortByPreference(out);
        return out;
    }

    private static Core[] generateLayoutLinux() {
        var processor = new SystemInfo().getHardware().getProcessor();
        // Build per-physical-core affinity by OR'ing the bits of every logical
        // processor that maps to that core.
        Int2ObjectOpenHashMap<Affinity> perCore = new Int2ObjectOpenHashMap<>();
        for (var thread : processor.getLogicalProcessors()) {
            int phys = thread.getPhysicalProcessorNumber();
            short group = (short) thread.getProcessorGroup();
            Affinity existing = perCore.getOrDefault(phys, new Affinity(0L, group));
            if (existing.group != group) {
                throw new IllegalStateException("Logical procs of one physical core disagree on group");
            }
            perCore.put(phys, new Affinity(existing.mask | (1L << thread.getProcessorNumber()), group));
        }

        var physicals = processor.getPhysicalProcessors();
        Core[] out = new Core[physicals.size()];

        // Same homogeneity check as Windows: if no core reports efficiency != 0,
        // treat them uniformly as P cores.
        boolean homogeneous = true;
        for (var p : physicals) {
            if (p.getEfficiency() != 0) {
                homogeneous = false;
                break;
            }
        }

        int i = 0;
        for (var p : physicals) {
            Affinity a = perCore.remove(p.getPhysicalProcessorNumber());
            if (a == null) {
                throw new IllegalStateException("Physical core " + p.getPhysicalProcessorNumber()
                        + " has no affinity entry");
            }
            boolean isEfficiency = (!homogeneous) && p.getEfficiency() == 0;
            out[i++] = new Core(isEfficiency, a);
        }
        sortByPreference(out);
        return out;
    }

    /** P-cores first, then by group, then by mask. */
    private static void sortByPreference(Core[] cores) {
        Arrays.sort(cores, (a, b) -> {
            if (a.isEfficiency != b.isEfficiency) {
                return a.isEfficiency ? 1 : -1;
            }
            int g = Short.compareUnsigned(a.affinity.group, b.affinity.group);
            if (g != 0) return g;
            return Long.compareUnsigned(a.affinity.mask, b.affinity.mask);
        });
    }

    /** Number of physical cores; falls back to {@link Runtime#availableProcessors()}. */
    public static int getCoreCount() {
        return CORES == null ? Runtime.getRuntime().availableProcessors() : CORES.length;
    }
}
