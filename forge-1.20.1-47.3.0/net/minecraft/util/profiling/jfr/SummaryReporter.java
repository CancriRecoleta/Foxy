//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.profiling.jfr.parse.JfrStatsParser;
import net.minecraft.util.profiling.jfr.parse.JfrStatsResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class SummaryReporter {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Runnable onDeregistration;

    protected SummaryReporter(Runnable p_185398_) {
        this.onDeregistration = p_185398_;
    }

    public void recordingStopped(@Nullable Path p_185401_) {
        if (p_185401_ != null) {
            this.onDeregistration.run();
            infoWithFallback(() -> {
                return "Dumped flight recorder profiling to " + p_185401_;
            });

            JfrStatsResult $$3;
            Throwable $$5;
            try {
                $$3 = JfrStatsParser.parse(p_185401_);
            } catch (Throwable var5) {
                $$5 = var5;
                warnWithFallback(() -> {
                    return "Failed to parse JFR recording";
                }, $$5);
                return;
            }

            try {
                Objects.requireNonNull($$3);
                infoWithFallback($$3::asJson);
                String var10001 = p_185401_.getFileName().toString();
                Path $$4 = p_185401_.resolveSibling("jfr-report-" + StringUtils.substringBefore(var10001, ".jfr") + ".json");
                Files.writeString($$4, $$3.asJson(), StandardOpenOption.CREATE);
                infoWithFallback(() -> {
                    return "Dumped recording summary to " + $$4;
                });
            } catch (Throwable var4) {
                $$5 = var4;
                warnWithFallback(() -> {
                    return "Failed to output JFR report";
                }, $$5);
            }

        }
    }

    private static void infoWithFallback(Supplier<String> p_201933_) {
        if (LogUtils.isLoggerActive()) {
            LOGGER.info((String)p_201933_.get());
        } else {
            Bootstrap.realStdoutPrintln((String)p_201933_.get());
        }

    }

    private static void warnWithFallback(Supplier<String> p_201935_, Throwable p_201936_) {
        if (LogUtils.isLoggerActive()) {
            LOGGER.warn((String)p_201935_.get(), p_201936_);
        } else {
            Bootstrap.realStdoutPrintln((String)p_201935_.get());
            p_201936_.printStackTrace(Bootstrap.STDOUT);
        }

    }
}
