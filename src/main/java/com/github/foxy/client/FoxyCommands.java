package com.github.foxy.client;

import com.github.foxy.common.Logger;
import com.github.foxy.commonImpl.FoxyInstance;
import com.github.foxy.commonImpl.importers.WorldImporter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Brigadier command tree exposed at {@code /foxy}.
 *
 * <h2>Branches</h2>
 * <ul>
 *   <li>{@code /foxy import current} &mdash; voxelize and persist the singleplayer
 *       world's current-dimension {@code region/} directory.</li>
 *   <li>{@code /foxy mipall} &mdash; rebuild the LOD pyramid from every stored LOD-0
 *       section; finishes asynchronously via the engine's {@link
 *       com.github.foxy.common.world.service.MipService MipService}.</li>
 *   <li>{@code /foxy status} &mdash; one-line summary of engine state plus the mip
 *       service's queue and throughput counters.</li>
 * </ul>
 *
 * <p>Registered on Forge's {@link RegisterClientCommandsEvent}. Every subcommand emits
 * feedback via the source's {@code sendSuccess} / {@code sendFailure}; nothing happens
 * silently. Commands return {@code 1} on success and {@code 0} on user error.</p>
 */
public final class FoxyCommands {
    private FoxyCommands() {}

    /** Hook for Forge's command-registration event. */
    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("foxy")
                .then(Commands.literal("import")
                        .then(Commands.literal("current").executes(FoxyCommands::importCurrent)))
                .then(Commands.literal("mipall").executes(FoxyCommands::mipAll))
                .then(Commands.literal("status").executes(FoxyCommands::status)));
    }

    // ---- /foxy import current ---------------------------------------------------------

    private static int importCurrent(CommandContext<CommandSourceStack> ctx) {
        var src = ctx.getSource();
        var mc = Minecraft.getInstance();

        if (!mc.hasSingleplayerServer()) {
            src.sendFailure(Component.literal("/foxy import current is only supported in single-player"));
            return 0;
        }
        var server = mc.getSingleplayerServer();
        if (server == null) {
            src.sendFailure(Component.literal("Singleplayer server is not running"));
            return 0;
        }
        var level = mc.level;
        if (level == null) {
            src.sendFailure(Component.literal("Not currently in a world"));
            return 0;
        }

        Path worldRoot = server.getWorldPath(LevelResource.ROOT);
        Path regionDir = DimensionType.getStorageFolder(level.dimension(), worldRoot).resolve("region");
        if (!Files.isDirectory(regionDir)) {
            src.sendFailure(Component.literal("No region directory at " + regionDir));
            return 0;
        }

        var instance = FoxyInstance.current();
        if (instance == null) {
            src.sendFailure(Component.literal("FoxyInstance not active; rejoin the world and retry"));
            return 0;
        }

        try {
            var engine = instance.getOrCreateEngine();
            var biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
            var importer = new WorldImporter(engine, regionDir, biomeRegistry);
            if (!instance.importManager().tryRunImport(importer)) {
                src.sendFailure(Component.literal("An import is already running on this engine"));
                return 0;
            }
        } catch (Throwable t) {
            Logger.error("/foxy import current failed", t);
            src.sendFailure(Component.literal("Import failed: " + t.getMessage()));
            return 0;
        }

        src.sendSuccess(() -> Component.literal("Foxy: import started for " + regionDir), false);
        return Command.SINGLE_SUCCESS;
    }

    // ---- /foxy mipall ------------------------------------------------------------------

    private static int mipAll(CommandContext<CommandSourceStack> ctx) {
        var src = ctx.getSource();
        var instance = FoxyInstance.current();
        if (instance == null) {
            src.sendFailure(Component.literal("FoxyInstance not active"));
            return 0;
        }
        var engine = instance.getOrCreateEngine();
        var mipService = instance.getOrCreateMipService(engine);
        int enqueued = mipService.mipAll();
        src.sendSuccess(() -> Component.literal("Foxy: enqueued " + enqueued + " LOD-1 parents for mipping"),
                false);
        return Command.SINGLE_SUCCESS;
    }

    // ---- /foxy status ------------------------------------------------------------------

    private static int status(CommandContext<CommandSourceStack> ctx) {
        var src = ctx.getSource();
        var instance = FoxyInstance.current();
        if (instance == null) {
            src.sendSuccess(() -> Component.literal("Foxy: no instance active"), false);
            return Command.SINGLE_SUCCESS;
        }
        var engine = instance.getEngine(instance.identifier());
        if (engine == null) {
            src.sendSuccess(() -> Component.literal("Foxy: instance " + instance.identifier()
                    + " bound, engine not yet built"), false);
            return Command.SINGLE_SUCCESS;
        }
        // Snapshot first so the rendered string is consistent across line breaks.
        int sections = engine.getActiveSectionCount();
        var mipService = instance.getOrCreateMipService(engine);
        int pending = mipService.pendingCount();
        int inFlight = mipService.inFlightCount();
        long mipped = mipService.totalMipped();
        boolean importing = instance.importManager().isImporting(engine);

        src.sendSuccess(() -> Component.literal(
                "Foxy [" + instance.identifier().getWorldId() + "] sections=" + sections
                        + " mip(pending=" + pending + ",inFlight=" + inFlight + ",done=" + mipped + ")"
                        + (importing ? " IMPORTING" : "")
        ), false);
        return Command.SINGLE_SUCCESS;
    }
}
