package com.github.foxy.client;

import com.github.foxy.client.core.IGetFoxyRenderSystem;
import com.github.foxy.common.Logger;
import com.github.foxy.commonImpl.FoxyInstance;
import com.github.foxy.commonImpl.WorldIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.nio.file.Path;

/**
 * Client-side world lifecycle bridge: wires Minecraft's join / leave events into
 * {@link FoxyInstance#enter} / {@link FoxyInstance#leave}.
 *
 * <p>Single-player can deliver the login event before {@link Minecraft#level} is
 * populated, so initialization is retried from the client tick until a level and
 * level renderer are both available.</p>
 */
public final class FoxyClientLifecycle {
    private static boolean pendingEnter;
    private static boolean rendererCreateAttempted;

    private FoxyClientLifecycle() {}

    /** Fired when the client finishes establishing a connection (SP or MP). */
    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        pendingEnter = true;
        rendererCreateAttempted = false;
        ensureEntered("login");
    }

    /** Single-player can fire login before the client level is ready; retry until it is. */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ensureEntered("tick");
        }
    }

    /** Fired on disconnect. */
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        try {
            pendingEnter = false;
            rendererCreateAttempted = false;
            ((IGetFoxyRenderSystem) Minecraft.getInstance().levelRenderer).Foxy$shutdownRenderer();
            FoxyInstance.leave();
        } catch (Throwable t) {
            Logger.error("FoxyClientLifecycle: leave failed", t);
        }
    }

    private static void ensureEntered(String source) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if (level == null || mc.levelRenderer == null) {
            return;
        }

        try {
            String namespace = resolveNamespace(mc);
            var id = new WorldIdentifier(namespace, level.dimension(), 0L);
            var instance = FoxyInstance.current();
            if (instance == null) {
                instance = FoxyInstance.enter(id, resolveStorageBasePath(mc));
                // ImportManager keeps a single broadcaster slot and starts with
                // a no-op; wire the in-game UI feedback exactly once per
                // FoxyInstance so action-bar progress and chat completion
                // messages reach the user without any explicit command.
                instance.importManager().setBroadcaster(FoxyImportFeedback.createBroadcaster());
                Logger.info("FoxyClientLifecycle: entered world from " + source + " as " + id);
            }

            var rendererHook = (IGetFoxyRenderSystem) mc.levelRenderer;
            if (rendererHook.Foxy$getRenderSystem() == null && !rendererCreateAttempted) {
                rendererCreateAttempted = true;
                rendererHook.Foxy$createRenderer();
            }
            instance = FoxyInstance.current();
            if (instance != null) {
                // Per-tick poll: trySchedule is idempotent within a session (a
                // CHECKED_THIS_SESSION key plus a marker file de-dupes both
                // between ticks and across runs). This is what gives us
                // dimension-aware backfill: when the player walks through a
                // portal, mc.level swaps to the nether ClientLevel and the
                // next tick's call resolves to the nether engine.
                FoxyAutoBackfill.trySchedule(mc, instance);
            }
            pendingEnter = false;
        } catch (Throwable t) {
            if (pendingEnter) {
                pendingEnter = false;
                Logger.error("FoxyClientLifecycle: enter failed from " + source, t);
            }
        }
    }

    /**
     * Picks a stable namespace for the current connection: the save folder name on
     * single-player, the host string on multiplayer, {@code "unknown"} otherwise.
     */
    private static String resolveNamespace(Minecraft mc) {
        if (mc.hasSingleplayerServer()) {
            var server = mc.getSingleplayerServer();
            if (server != null) {
                var path = server.getWorldPath(LevelResource.ROOT);
                var name = path.getFileName();
                if (name != null) return "sp:" + sanitizePathPart(name.toString());
            }
        }
        var current = mc.getCurrentServer();
        if (current != null && current.ip != null) {
            return "mp:" + sanitizePathPart(current.ip);
        }
        return "unknown";
    }

    private static Path resolveStorageBasePath(Minecraft mc) {
        if (mc.hasSingleplayerServer()) {
            var server = mc.getSingleplayerServer();
            if (server != null) {
                return server.getWorldPath(LevelResource.ROOT).resolve("foxy");
            }
        }

        Path basePath = mc.gameDirectory.toPath().resolve(".foxy").resolve("saves");
        var current = mc.getCurrentServer();
        if (current == null || current.ip == null) {
            return basePath.resolve("UNKNOWN").toAbsolutePath();
        }
        return basePath.resolve(sanitizePathPart(current.ip)).toAbsolutePath();
    }

    private static String sanitizePathPart(String value) {
        return value.replace(':', '_')
                .replace('/', '_')
                .replace('\\', '_');
    }
}
