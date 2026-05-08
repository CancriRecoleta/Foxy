package com.github.foxy;

import com.github.foxy.client.FoxyClientLifecycle;
import com.github.foxy.client.FoxyCommands;
import com.github.foxy.client.FoxyEmbeddiumOptions;
import com.github.foxy.commonImpl.FoxyCommon;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge mod entry for Foxy.
 *
 * <p>Foxy is the Forge 1.20.1 cleanroom reimplementation of the upstream Voxy mod
 * (originally a Fabric mod for MC 1.21.x by Cortex). Loader-agnostic data and storage
 * code lives under {@code com.github.foxy.common}; loader glue lives under
 * {@code com.github.foxy.commonImpl}; the client-side renderer (when it lands) lives
 * under {@code com.github.foxy.client}.</p>
 *
 * <h2>Wiring</h2>
 * The {@code @Mod} constructor performs three things:
 * <ol>
 *   <li>Eagerly initialises {@link FoxyCommon} (reads ModList, sets up logging).</li>
 *   <li>On the client distribution only, attaches
 *       {@link FoxyClientLifecycle}'s {@code LoggingIn} / {@code LoggingOut} listeners
 *       so the singleton {@link com.github.foxy.commonImpl.FoxyInstance FoxyInstance}
 *       follows the player's world.</li>
 *   <li>On the client distribution only, registers
 *       {@link FoxyCommands} for {@code /foxy import current} / {@code mipall} /
 *       {@code status}.</li>
 * </ol>
 *
 * <p>The current build wires storage, live voxel ingestion, import/mipping, and the
 * client LOD render pass on top of Embeddium/Oculus-compatible hooks.</p>
 */
@Mod(Foxy.MODID)
public class Foxy {
    /** Mod identifier; matches {@code mods.toml}, the asset namespace and the mixin refmap. */
    public static final String MODID = "foxy";

    /** Wired up by Forge during mod loading. */
    public Foxy() {
        FoxyCommon.bootstrap();

        // Client-only event subscriptions. DistExecutor.unsafeRunWhenOn keeps any class
        // reference inside the Supplier off the classloading path on a dedicated server,
        // where the client packages aren't present.
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.register(FoxyClientLifecycle.class);
            MinecraftForge.EVENT_BUS.register(FoxyCommands.class);
            MinecraftForge.EVENT_BUS.register(FoxyEmbeddiumOptions.class);
        });
    }
}
