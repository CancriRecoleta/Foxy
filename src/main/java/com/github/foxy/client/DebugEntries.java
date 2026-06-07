package com.github.foxy.client;

import com.github.foxy.client.core.IGetFoxyRenderSystem;
import com.github.foxy.client.core.FoxyRenderSystem;
import com.github.foxy.commonImpl.FoxyCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

import java.util.List;

// 1.20.1 has no pluggable DebugScreenEntries API (that is a 1.21 feature). Foxy's F3 lines are
// produced here and injected into the vanilla F3 overlay by a DebugScreenOverlay mixin instead.
public class DebugEntries {
    public static void init() {
        // No-op on Forge 1.20.1: F3 lines are added via the debug-overlay mixin, not a registry.
    }

    // Appends Foxy's diagnostic lines to the F3 overlay. Called from the DebugScreenOverlay mixin.
    public static void appendDebugLines(List<String> lines) {
        if (!FoxyCommon.isAvailable()) {
            lines.add(ChatFormatting.RED + "foxy-" + FoxyCommon.MOD_VERSION);//Foxy installed, not available
            return;
        }
        var instance = FoxyCommon.getInstance();
        if (instance == null) {
            lines.add(ChatFormatting.YELLOW + "foxy-" + FoxyCommon.MOD_VERSION);//Foxy available, no instance active
            return;
        }

        FoxyRenderSystem vrs = null;
        var wr = Minecraft.getInstance().levelRenderer;
        if (wr != null) vrs = ((IGetFoxyRenderSystem) wr).foxy$getRenderSystem();

        lines.add((vrs == null ? ChatFormatting.DARK_GREEN : ChatFormatting.GREEN) + "foxy-" + FoxyCommon.MOD_VERSION);
        instance.addDebug(lines);
        if (vrs != null) {
            vrs.addDebugInfo(lines);
        }
    }
}
