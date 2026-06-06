package com.github.foxy.client.compat;

import java.nio.file.Path;

// Flashback is a Fabric-only replay mod with no Forge 1.20.1 equivalent, so this compat shim
// is inert on Forge: there is never a Flashback replay server to source LOD data from.
public class FlashbackCompat {
    public static final boolean FLASHBACK_INSTALLED = false;

    public static Path getReplayStoragePath() {
        return null;
    }
}
