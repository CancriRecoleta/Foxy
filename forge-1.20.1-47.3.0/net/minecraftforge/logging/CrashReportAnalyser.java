//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.logging;

import com.mojang.logging.LogUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModInfo;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

public final class CrashReportAnalyser {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, IModInfo> PACKAGE_MOD_CACHE = new HashMap();
    private static final Map<IModInfo, String[]> SUSPECTED_MODS = new HashMap();

    private CrashReportAnalyser() {
    }

    public static String appendSuspectedMods(Throwable throwable, StackTraceElement[] uncategorizedStackTrace) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            cacheModList();
            analyseCrashReport(throwable, uncategorizedStackTrace);
            buildSuspectedModsSection(stringBuilder);
        } catch (Throwable var4) {
            Throwable t = var4;
            LOGGER.error("Failed to append suspected mod(s) to crash report!", t);
        }

        return stringBuilder.toString();
    }

    private static void analyseCrashReport(Throwable throwable, StackTraceElement[] uncategorizedStackTrace) {
        scanThrowable(throwable);
        scanStacktrace(uncategorizedStackTrace);
    }

    private static void buildSuspectedModsSection(StringBuilder stringBuilder) {
        stringBuilder.append("Suspected Mod");
        stringBuilder.append(SUSPECTED_MODS.size() == 1 ? ": " : "s: ");
        if (SUSPECTED_MODS.isEmpty()) {
            stringBuilder.append("NONE\n");
        } else {
            SUSPECTED_MODS.forEach((iModInfo, position) -> {
                stringBuilder.append("\n\t").append(iModInfo.getDisplayName()).append(" (").append(iModInfo.getModId()).append("),").append(" Version: ").append(iModInfo.getVersion());
                iModInfo.getOwningFile().getConfig().getConfigElement(new String[]{"issueTrackerURL"}).ifPresent((issuesLink) -> {
                    stringBuilder.append("\n\t\t").append("Issue tracker URL: ").append(issuesLink);
                });
                stringBuilder.append("\n\t\t");
                String[] var3 = position;
                int var4 = position.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    String s = var3[var5];
                    stringBuilder.append(s);
                }

                stringBuilder.append("\n");
            });
            SUSPECTED_MODS.clear();
        }

    }

    private static void scanThrowable(Throwable throwable) {
        scanStacktrace(throwable.getStackTrace());
        if (throwable.getCause() != null && throwable.getCause() != throwable) {
            scanThrowable(throwable.getCause());
        }

    }

    private static void scanStacktrace(StackTraceElement[] stackTrace) {
        StackTraceElement[] var1 = stackTrace;
        int var2 = stackTrace.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            StackTraceElement stackTraceElement = var1[var3];
            identifyByClass(stackTraceElement);
            identifyByMixins(stackTraceElement);
        }

    }

    private static void cacheModList() {
        ModList modList = ModList.get();
        ModuleLayer gameLayer = FMLLoader.getGameLayer();
        if (modList != null) {
            modList.getMods().forEach((iModInfo) -> {
                if (!iModInfo.getModId().equals("forge") && !iModInfo.getModId().equals("minecraft")) {
                    Set<String> packages = new HashSet();
                    gameLayer.findModule(iModInfo.getModId()).ifPresent((module) -> {
                        packages.addAll(module.getPackages());
                    });
                    packages.forEach((s) -> {
                        PACKAGE_MOD_CACHE.put(s, iModInfo);
                    });
                }

            });
        }

    }

    private static void identifyByClass(StackTraceElement stackTraceElement) {
        blameIfPresent(stackTraceElement);
    }

    private static void identifyByMixins(StackTraceElement stackTraceElement) {
        IMixinInfo mixinInfo = getMixinInfo(stackTraceElement);
        if (mixinInfo != null) {
            String elementAsString = stackTraceElement.toString();
            String mixinClassName = mixinInfo.getClassName();
            List<String> targetClasses = mixinInfo.getTargetClasses();
            blameIfPresent(mixinClassName, "Mixin class: ", mixinClassName, "\n\t\tTarget", (targetClasses.size() == 1 ? ": " + (String)targetClasses.get(0) : "s: " + targetClasses).replaceAll("/", "."), "\n\t\tat ", elementAsString);
        }

    }

    private static void blameIfPresent(StackTraceElement stackTraceElement) {
        blameIfPresent(stackTraceElement.getClassName(), "at ", stackTraceElement.toString());
    }

    private static void blameIfPresent(String className, String... position) {
        String commonPackage = findMatchingPackage(className);
        if (commonPackage != null) {
            SUSPECTED_MODS.putIfAbsent((IModInfo)PACKAGE_MOD_CACHE.get(commonPackage), position);
        }

    }

    @Nullable
    private static String findMatchingPackage(String className) {
        Iterator var1 = PACKAGE_MOD_CACHE.keySet().iterator();

        String s;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            s = (String)var1.next();
        } while(!className.startsWith(s));

        return s;
    }

    @Nullable
    private static MixinMerged findMixinMerged(StackTraceElement element) {
        try {
            Class<?> clazz = Class.forName(element.getClassName());
            Method[] methods = clazz.getDeclaredMethods();
            Method[] var3 = methods;
            int var4 = methods.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Method method = var3[var5];
                if (method.getName().equals(element.getMethodName())) {
                    MixinMerged mixinMerged = (MixinMerged)method.getAnnotation(MixinMerged.class);
                    if (mixinMerged != null) {
                        return mixinMerged;
                    }
                }
            }
        } catch (NoClassDefFoundError | ClassNotFoundException var8) {
        }

        return null;
    }

    @Nullable
    private static IMixinInfo getMixinInfo(StackTraceElement element) {
        MixinMerged mixinMerged = findMixinMerged(element);
        if (mixinMerged != null) {
            ClassInfo classInfo = ClassInfo.forName(mixinMerged.mixin().replace('.', '/'));
            if (classInfo != null) {
                try {
                    Field mixinField = ClassInfo.class.getDeclaredField("mixin");
                    mixinField.setAccessible(true);
                    return (IMixinInfo)mixinField.get(classInfo);
                } catch (IllegalAccessException | NoSuchFieldException var4) {
                    ReflectiveOperationException e = var4;
                    throw new RuntimeException(e);
                }
            }
        }

        return null;
    }
}
