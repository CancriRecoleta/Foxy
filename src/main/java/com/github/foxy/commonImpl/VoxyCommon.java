package com.github.foxy.commonImpl;

import com.github.foxy.common.Logger;
import com.github.foxy.common.config.Serialization;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class VoxyCommon {
    public static final String MOD_VERSION;
    public static final boolean IS_DEDICATED_SERVER;
    public static final boolean IS_IN_MINECRAFT;

    static {
        // This class may be touched very early (during mixin/AT transform) before the "foxy" mod
        // container is queryable, so the static initializer must not depend on it. We are always
        // loaded inside Forge here; the version is filled in best-effort. The config serialization
        // scan (which walks the mod file) is deferred to bootstrap(), run from the @Mod constructor
        // once ModList is fully populated.
        ModList modList = ModList.get();
        ModContainer mod = modList == null ? null : modList.getModContainerById("foxy").orElse(null);
        IS_IN_MINECRAFT = true;
        MOD_VERSION = mod != null ? mod.getModInfo().getVersion().toString() : "<UNKNOWN>";
        IS_DEDICATED_SERVER = FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }

    private static boolean bootstrapped = false;
    // Called from the @Mod entrypoint during construction (ModList ready), runs config serialization
    // setup. Idempotent so an early class-load followed by the explicit call only initialises once.
    public static synchronized void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        Serialization.init();
    }

    //This is hardcoded like this because people do not understand what they are doing
    public static boolean isVerificationFlagOn(String name) {
        return isVerificationFlagOn(name, false);
    }

    public static boolean isVerificationFlagOn(String name, boolean defaultOn) {
        return System.getProperty("foxy."+name, defaultOn?"true":"false").equals("true");
    }

    public static void breakpoint() {
        int breakpoint = 0;
    }

    public interface IInstanceFactory {VoxyInstance create();}
    private static VoxyInstance INSTANCE;
    private static IInstanceFactory FACTORY = null;

    public static void setInstanceFactory(IInstanceFactory factory) {
        if (FACTORY != null) {
            throw new IllegalStateException("Cannot set instance factory more than once");
        }
        FACTORY = factory;
    }

    public static VoxyInstance getInstance() {
        return INSTANCE;
    }

    public static void shutdownInstance() {
        if (INSTANCE != null) {
            var instance = INSTANCE;
            INSTANCE = null;//Make it null before shutdown
            instance.shutdown();
        }
    }

    public static void createInstance() {
        if (FACTORY == null) {
            //Logger.info("Voxy factory");
            return;
        }
        if (INSTANCE != null) {
            throw new IllegalStateException("Cannot create multiple instances");
        }
        INSTANCE = FACTORY.create();
    }

    //Is voxy available in any capacity
    public static boolean isAvailable() {
        return FACTORY != null;
    }

    public static final boolean IS_MINE_IN_ABYSS = false;
}