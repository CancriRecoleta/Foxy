//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.logging;

import cpw.mods.modlauncher.log.TransformingThrowablePatternConverter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import joptsimple.internal.Strings;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.SystemReport;
import net.minecraftforge.fml.CrashReportCallables;
import net.minecraftforge.fml.ISystemReportExtender;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.forge.snapshots.ForgeSnapshotsMod;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.Logger;

public class CrashReportExtender {
    public CrashReportExtender() {
    }

    public static void extendSystemReport(SystemReport systemReport) {
        Iterator var1 = CrashReportCallables.allCrashCallables().iterator();

        while(var1.hasNext()) {
            ISystemReportExtender call = (ISystemReportExtender)var1.next();
            if (call.isActive()) {
                systemReport.setDetail(call.getLabel(), (Supplier)call);
            }
        }

    }

    public static void addCrashReportHeader(StringBuilder stringbuilder, CrashReport crashReport) {
        ForgeSnapshotsMod.addCrashReportHeader(stringbuilder, crashReport);
    }

    public static String generateEnhancedStackTrace(Throwable throwable) {
        return generateEnhancedStackTrace(throwable, true);
    }

    public static String generateEnhancedStackTrace(StackTraceElement[] stacktrace) {
        Throwable t = new Throwable();
        t.setStackTrace(stacktrace);
        return generateEnhancedStackTrace(t, false);
    }

    public static String generateEnhancedStackTrace(Throwable throwable, boolean header) {
        String s = TransformingThrowablePatternConverter.generateEnhancedStackTrace(throwable);
        return header ? s : s.substring(s.indexOf(Strings.LINE_SEPARATOR));
    }

    public static File dumpModLoadingCrashReport(Logger logger, LoadingFailedException error, File topLevelDir) {
        CrashReport crashReport = CrashReport.forThrowable(new Exception("Mod Loading has failed"), "Mod loading error has occurred");
        error.getErrors().forEach((mle) -> {
            Optional<IModInfo> modInfo = Optional.ofNullable(mle.getModInfo());
            CrashReportCategory category = crashReport.addCategory((String)modInfo.map((iModInfo) -> {
                return "MOD " + iModInfo.getModId();
            }).orElse("NO MOD INFO AVAILABLE"));
            Throwable cause = mle.getCause();

            for(int depth = 0; cause != null && cause.getCause() != null && cause.getCause() != cause; cause = cause.getCause()) {
                int var10001 = depth++;
                category.setDetail("Caused by " + var10001, (Object)("" + cause + generateEnhancedStackTrace(cause.getStackTrace()).replaceAll(Strings.LINE_SEPARATOR + "\t", "\n\t\t")));
            }

            if (cause != null) {
                category.applyStackTrace(cause);
            }

            category.setDetail("Mod File", () -> {
                return (String)modInfo.map(IModInfo::getOwningFile).map((t) -> {
                    return t.getFile().getFilePath().toUri().getPath();
                }).orElse("NO FILE INFO");
            });
            category.setDetail("Failure message", () -> {
                return mle.getCleanMessage().replace("\n", "\n\t\t");
            });
            category.setDetail("Mod Version", () -> {
                return (String)modInfo.map(IModInfo::getVersion).map(Object::toString).orElse("NO MOD INFO AVAILABLE");
            });
            category.setDetail("Mod Issue URL", () -> {
                Optional var10000 = modInfo.map(IModInfo::getOwningFile);
                Objects.requireNonNull(IModFileInfo.class);
                return (String)var10000.map(IModFileInfo.class::cast).flatMap((mfi) -> {
                    return mfi.getConfig().getConfigElement(new String[]{"issueTrackerURL"});
                }).orElse("NOT PROVIDED");
            });
            category.setDetail("Exception message", (Object)Objects.toString(cause, "MISSING EXCEPTION MESSAGE"));
        });
        File file1 = new File(topLevelDir, "crash-reports");
        SimpleDateFormat var10003 = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
        Date var10004 = new Date();
        File file2 = new File(file1, "crash-" + var10003.format(var10004) + "-fml.txt");
        if (crashReport.saveToFile(file2)) {
            logger.fatal("Crash report saved to {}", file2);
        } else {
            logger.fatal("Failed to save crash report");
        }

        System.out.print(crashReport.getFriendlyReport());
        return file2;
    }
}
