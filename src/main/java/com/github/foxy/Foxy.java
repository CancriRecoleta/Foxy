package com.github.foxy;

import com.github.foxy.commonImpl.FoxyCommon;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge mod entry for Foxy.
 *
 * <p>Foxy is the Forge 1.20.1 cleanroom reimplementation of the upstream Voxy mod
 * (originally a Fabric mod for MC 1.21.x by Cortex). Loader-agnostic data and storage
 * code lives under {@code com.github.foxy.common}; loader glue lives under
 * {@code com.github.foxy.commonImpl}; the client-side renderer (when present) lives
 * under {@code com.github.foxy.client}.</p>
 *
 * <p>The current build delivers the storage + voxelization layer only; client rendering
 * is not yet wired up.</p>
 */
@Mod(Foxy.MODID)
public class Foxy {
    /** Mod identifier; matches {@code mods.toml}, the asset namespace and the mixin refmap. */
    public static final String MODID = "foxy";

    /** Wired up by Forge during mod loading. */
    public Foxy() {
        FoxyCommon.bootstrap();
    }
}
