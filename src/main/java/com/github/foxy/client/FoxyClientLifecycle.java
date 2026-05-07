package com.github.foxy.client;

import com.github.foxy.common.Logger;
import com.github.foxy.commonImpl.FoxyInstance;
import com.github.foxy.commonImpl.WorldIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Client-side world lifecycle bridge: wires Minecraft's join / leave events into
 * {@link FoxyInstance#enter} / {@link FoxyInstance#leave}.
 *
 * <p>Handlers are registered on the Forge event bus from
 * {@link com.github.foxy.Foxy Foxy}'s {@code @Mod} constructor (client dist only).</p>
 *
 * <h2>Identifier resolution</h2>
 * The identifier {@link WorldIdentifier} that gets passed to
 * {@link FoxyInstance#enter} carries:
 * <ul>
 *   <li>{@code namespace} &mdash; the SP save folder name ({@code .minecraft/saves/&lt;name&gt;})
 *       or {@code "mp:&lt;ip&gt;"} for a multiplayer server. Used so two saves with the
 *       same dimension don't cross-contaminate.</li>
 *   <li>{@code levelKey} &mdash; the current dimension (e.g. {@code minecraft:overworld}).</li>
 *   <li>{@code biomeSeed} &mdash; left at 0 for now; cleanroom port doesn't yet probe
 *       Minecraft's BiomeManager seed (handled by a future AT bump).</li>
 * </ul>
 */
public final class FoxyClientLifecycle {
    private FoxyClientLifecycle() {}

    /** Fired when the client finishes establishing a connection (SP or MP). */
    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if (level == null) {
            // Some MP servers fire LoggingIn before the world is fully synchronised.
            // Wait for the first level tick and re-resolve in onWorldLoad fallback;
            // for now just log and bail — dropping a single join is non-fatal.
            Logger.warn("FoxyClientLifecycle: LoggingIn fired with null level; skipping enter");
            return;
        }
        try {
            String namespace = resolveNamespace(mc);
            var id = new WorldIdentifier(namespace, level.dimension(), 0L);
            FoxyInstance.enter(id);
        } catch (Throwable t) {
            Logger.error("FoxyClientLifecycle: enter failed", t);
        }
    }

    /** Fired on disconnect. */
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        try {
            FoxyInstance.leave();
        } catch (Throwable t) {
            Logger.error("FoxyClientLifecycle: leave failed", t);
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
                if (name != null) return "sp:" + name.toString();
            }
        }
        var current = mc.getCurrentServer();
        if (current != null && current.ip != null) {
            return "mp:" + current.ip;
        }
        return "unknown";
    }
}
