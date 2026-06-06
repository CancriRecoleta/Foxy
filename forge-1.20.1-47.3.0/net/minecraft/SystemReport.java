//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;

public class SystemReport {
    public static final long BYTES_PER_MEBIBYTE = 1048576L;
    private static final long ONE_GIGA = 1000000000L;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String OPERATING_SYSTEM;
    private static final String JAVA_VERSION;
    private static final String JAVA_VM_VERSION;
    private final Map<String, String> entries = Maps.newLinkedHashMap();

    public SystemReport() {
        this.setDetail("Minecraft Version", SharedConstants.getCurrentVersion().getName());
        this.setDetail("Minecraft Version ID", SharedConstants.getCurrentVersion().getId());
        this.setDetail("Operating System", OPERATING_SYSTEM);
        this.setDetail("Java Version", JAVA_VERSION);
        this.setDetail("Java VM Version", JAVA_VM_VERSION);
        this.setDetail("Memory", () -> {
            Runtime $$0 = Runtime.getRuntime();
            long $$1 = $$0.maxMemory();
            long $$2 = $$0.totalMemory();
            long $$3 = $$0.freeMemory();
            long $$4 = $$1 / 1048576L;
            long $$5 = $$2 / 1048576L;
            long $$6 = $$3 / 1048576L;
            return "" + $$3 + " bytes (" + $$6 + " MiB) / " + $$2 + " bytes (" + $$5 + " MiB) up to " + $$1 + " bytes (" + $$4 + " MiB)";
        });
        this.setDetail("CPUs", () -> {
            return String.valueOf(Runtime.getRuntime().availableProcessors());
        });
        this.ignoreErrors("hardware", () -> {
            this.putHardware(new SystemInfo());
        });
        this.setDetail("JVM Flags", () -> {
            List<String> $$0 = (List)Util.getVmArguments().collect(Collectors.toList());
            return String.format(Locale.ROOT, "%d total; %s", $$0.size(), String.join(" ", $$0));
        });
    }

    public void setDetail(String p_143520_, String p_143521_) {
        this.entries.put(p_143520_, p_143521_);
    }

    public void setDetail(String p_143523_, Supplier<String> p_143524_) {
        try {
            this.setDetail(p_143523_, (String)p_143524_.get());
        } catch (Exception var4) {
            Exception $$2 = var4;
            LOGGER.warn("Failed to get system info for {}", p_143523_, $$2);
            this.setDetail(p_143523_, "ERR");
        }

    }

    private void putHardware(SystemInfo p_143536_) {
        HardwareAbstractionLayer $$1 = p_143536_.getHardware();
        this.ignoreErrors("processor", () -> {
            this.putProcessor($$1.getProcessor());
        });
        this.ignoreErrors("graphics", () -> {
            this.putGraphics($$1.getGraphicsCards());
        });
        this.ignoreErrors("memory", () -> {
            this.putMemory($$1.getMemory());
        });
    }

    private void ignoreErrors(String p_143517_, Runnable p_143518_) {
        try {
            p_143518_.run();
        } catch (Throwable var4) {
            Throwable $$2 = var4;
            LOGGER.warn("Failed retrieving info for group {}", p_143517_, $$2);
        }

    }

    private void putPhysicalMemory(List<PhysicalMemory> p_143532_) {
        int $$1 = 0;
        Iterator var3 = p_143532_.iterator();

        while(var3.hasNext()) {
            PhysicalMemory $$2 = (PhysicalMemory)var3.next();
            String $$3 = String.format(Locale.ROOT, "Memory slot #%d ", $$1++);
            this.setDetail($$3 + "capacity (MB)", () -> {
                return String.format(Locale.ROOT, "%.2f", (float)$$2.getCapacity() / 1048576.0F);
            });
            this.setDetail($$3 + "clockSpeed (GHz)", () -> {
                return String.format(Locale.ROOT, "%.2f", (float)$$2.getClockSpeed() / 1.0E9F);
            });
            String var10001 = $$3 + "type";
            Objects.requireNonNull($$2);
            this.setDetail(var10001, $$2::getMemoryType);
        }

    }

    private void putVirtualMemory(VirtualMemory p_143550_) {
        this.setDetail("Virtual memory max (MB)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float)p_143550_.getVirtualMax() / 1048576.0F);
        });
        this.setDetail("Virtual memory used (MB)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float)p_143550_.getVirtualInUse() / 1048576.0F);
        });
        this.setDetail("Swap memory total (MB)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float)p_143550_.getSwapTotal() / 1048576.0F);
        });
        this.setDetail("Swap memory used (MB)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float)p_143550_.getSwapUsed() / 1048576.0F);
        });
    }

    private void putMemory(GlobalMemory p_143542_) {
        this.ignoreErrors("physical memory", () -> {
            this.putPhysicalMemory(p_143542_.getPhysicalMemory());
        });
        this.ignoreErrors("virtual memory", () -> {
            this.putVirtualMemory(p_143542_.getVirtualMemory());
        });
    }

    private void putGraphics(List<GraphicsCard> p_143553_) {
        int $$1 = 0;
        Iterator var3 = p_143553_.iterator();

        while(var3.hasNext()) {
            GraphicsCard $$2 = (GraphicsCard)var3.next();
            String $$3 = String.format(Locale.ROOT, "Graphics card #%d ", $$1++);
            String var10001 = $$3 + "name";
            Objects.requireNonNull($$2);
            this.setDetail(var10001, $$2::getName);
            var10001 = $$3 + "vendor";
            Objects.requireNonNull($$2);
            this.setDetail(var10001, $$2::getVendor);
            this.setDetail($$3 + "VRAM (MB)", () -> {
                return String.format(Locale.ROOT, "%.2f", (float)$$2.getVRam() / 1048576.0F);
            });
            var10001 = $$3 + "deviceId";
            Objects.requireNonNull($$2);
            this.setDetail(var10001, $$2::getDeviceId);
            var10001 = $$3 + "versionInfo";
            Objects.requireNonNull($$2);
            this.setDetail(var10001, $$2::getVersionInfo);
        }

    }

    private void putProcessor(CentralProcessor p_143540_) {
        CentralProcessor.ProcessorIdentifier $$1 = p_143540_.getProcessorIdentifier();
        Objects.requireNonNull($$1);
        this.setDetail("Processor Vendor", $$1::getVendor);
        Objects.requireNonNull($$1);
        this.setDetail("Processor Name", $$1::getName);
        Objects.requireNonNull($$1);
        this.setDetail("Identifier", $$1::getIdentifier);
        Objects.requireNonNull($$1);
        this.setDetail("Microarchitecture", $$1::getMicroarchitecture);
        this.setDetail("Frequency (GHz)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float)$$1.getVendorFreq() / 1.0E9F);
        });
        this.setDetail("Number of physical packages", () -> {
            return String.valueOf(p_143540_.getPhysicalPackageCount());
        });
        this.setDetail("Number of physical CPUs", () -> {
            return String.valueOf(p_143540_.getPhysicalProcessorCount());
        });
        this.setDetail("Number of logical CPUs", () -> {
            return String.valueOf(p_143540_.getLogicalProcessorCount());
        });
    }

    public void appendToCrashReportString(StringBuilder p_143526_) {
        p_143526_.append("-- ").append("System Details").append(" --\n");
        p_143526_.append("Details:");
        this.entries.forEach((p_143529_, p_143530_) -> {
            p_143526_.append("\n\t");
            p_143526_.append(p_143529_);
            p_143526_.append(": ");
            p_143526_.append(p_143530_);
        });
    }

    public String toLineSeparatedString() {
        return (String)this.entries.entrySet().stream().map((p_143534_) -> {
            String var10000 = (String)p_143534_.getKey();
            return var10000 + ": " + (String)p_143534_.getValue();
        }).collect(Collectors.joining(System.lineSeparator()));
    }

    static {
        String var10000 = System.getProperty("os.name");
        OPERATING_SYSTEM = var10000 + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
        var10000 = System.getProperty("java.version");
        JAVA_VERSION = var10000 + ", " + System.getProperty("java.vendor");
        var10000 = System.getProperty("java.vm.name");
        JAVA_VM_VERSION = var10000 + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
    }
}
