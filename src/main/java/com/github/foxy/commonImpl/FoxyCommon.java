package com.github.foxy.commonImpl;

import com.github.foxy.Foxy;
import com.github.foxy.common.Logger;
import com.github.foxy.common.config.Serialization;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.IModInfo;

/**
 * Forge-side bootstrap and shared environment constants.
 *
 * <p>Lives under {@code commonImpl} (loader glue) rather than {@code common} (loader
 * agnostic) because it touches Forge-only types ({@code ModList}, {@code FMLEnvironment}).
 * Touch any field of this class to trigger its static initializer.</p>
 */
public final class FoxyCommon {
    private FoxyCommon() {}

    /** Friendly version string of the loaded Foxy mod, or {@code "<UNKNOWN>"} outside Forge. */
    public static final String MOD_VERSION;

    /** {@code true} when running on a dedicated server. */
    public static final boolean IS_DEDICATED_SERVER;

    /** {@code true} when running inside a real Forge environment (vs. unit tests). */
    public static final boolean IS_IN_MINECRAFT;

    public static final boolean IS_MINE_IN_ABYSS = false;

    static {
        IModInfo info = null;
        try {
            var container = ModList.get().getModContainerById(Foxy.MODID).orElse(null);
            if (container != null) info = container.getModInfo();
        } catch (Throwable ignored) {
            // ModList may not be initialised in unit tests / classpath probing.
        }

        if (info == null) {
            IS_IN_MINECRAFT = false;
            MOD_VERSION = "<UNKNOWN>";
            IS_DEDICATED_SERVER = false;
            Logger.warn("Foxy bootstrap ran outside a Forge environment");
        } else {
            IS_IN_MINECRAFT = true;
            MOD_VERSION = info.getVersion().toString();
            IS_DEDICATED_SERVER = FMLEnvironment.dist == Dist.DEDICATED_SERVER;
            Logger.info("Foxy " + MOD_VERSION + " loading on " + (IS_DEDICATED_SERVER ? "dedicated server" : "client/integrated"));
        }
    }

    /** Reads {@code -Dfoxy.<name>} as a boolean verification flag (default {@code false}). */
    public static boolean isVerificationFlagOn(String name) { return isVerificationFlagOn(name, false); }

    /** Reads {@code -Dfoxy.<name>} as a boolean verification flag with caller-supplied default. */
    public static boolean isVerificationFlagOn(String name, boolean defaultOn) {
        return System.getProperty("foxy." + name, defaultOn ? "true" : "false").equals("true");
    }

    /**
     * No-op convenience used by {@link Foxy#Foxy()} to force this class's static init to
     * run during mod construction. Also doubles as a debugger anchor.
     */
    public static void bootstrap() {
        // Touching IS_IN_MINECRAFT triggers the static block; the if just suppresses
        // 'value never used' warnings.
        if (!IS_IN_MINECRAFT) {
            // intentionally empty
        }
        if (Serialization.GSON == null) {
            Serialization.init();
        }
    }

    public static boolean isAvailable() {
        return IS_IN_MINECRAFT;
    }

    public static FoxyInstance getInstance() {
        return FoxyInstance.current();
    }
}
