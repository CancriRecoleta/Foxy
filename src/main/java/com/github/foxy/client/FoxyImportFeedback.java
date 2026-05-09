package com.github.foxy.client;

import com.github.foxy.common.world.WorldEngine;
import com.github.foxy.commonImpl.ImportManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Bridges {@link ImportManager.IUpdateBroadcaster} events to the in-game UI so
 * the user sees auto-imports running without having to read the log.
 *
 * <ul>
 *   <li>Progress is rendered via {@code Gui.setOverlayMessage} (the action-bar
 *       slot used by {@code /title actionbar}). The action-bar fades after a
 *       few seconds, so a coarser per-broadcaster throttle here on top of the
 *       50&nbsp;ms one in {@link ImportManager.Task#onUpdate} keeps the message
 *       visibly stable rather than flickering on every chunk.</li>
 *   <li>Completion goes to chat (system message) where it persists in the
 *       chat log for users who tab away mid-import.</li>
 * </ul>
 */
public final class FoxyImportFeedback {

    /** Milliseconds between consecutive action-bar updates. */
    private static final long UI_THROTTLE_MILLIS = 500L;

    private FoxyImportFeedback() {}

    /** Builds and returns the broadcaster instance to register with {@link ImportManager}. */
    public static ImportManager.IUpdateBroadcaster createBroadcaster() {
        return new Impl();
    }

    private static final class Impl implements ImportManager.IUpdateBroadcaster {
        private volatile long lastUiPaintMillis = 0L;

        @Override
        public void onProgress(WorldEngine engine, int finished, int outOf) {
            long now = System.currentTimeMillis();
            // Worker thread races are fine here: the action-bar only needs an
            // approximately-current value, and a missed update will be picked
            // up by the next progress event 50 ms later.
            if (now - this.lastUiPaintMillis < UI_THROTTLE_MILLIS) return;
            this.lastUiPaintMillis = now;

            int pct = outOf > 0 ? (int) ((finished * 100L) / outOf) : 0;
            MutableComponent msg = Component
                    .literal("Foxy import: ")
                    .withStyle(ChatFormatting.AQUA)
                    .append(Component.literal(finished + " / " + outOf + " (" + pct + "%)")
                            .withStyle(ChatFormatting.WHITE));

            // Gui.setOverlayMessage marshals onto the render thread internally;
            // calling it from the worker is safe.
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.gui != null) {
                mc.gui.setOverlayMessage(msg, false);
            }
        }

        @Override
        public void onCompleted(WorldEngine engine, int chunksImported, int mipParentsQueued) {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null) return;
            mc.execute(() -> {
                if (mc.player == null) return;
                MutableComponent msg = Component
                        .literal("Foxy: ")
                        .withStyle(ChatFormatting.AQUA)
                        .append(Component.literal("imported " + chunksImported + " chunks, mipping "
                                        + mipParentsQueued + " LOD parents")
                                .withStyle(ChatFormatting.GRAY));
                mc.player.displayClientMessage(msg, false);
            });
        }
    }
}
