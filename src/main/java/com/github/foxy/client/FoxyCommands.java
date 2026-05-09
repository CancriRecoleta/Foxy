package com.github.foxy.client;

import com.github.foxy.client.core.IGetFoxyRenderSystem;
import com.github.foxy.client.core.rendering.building.RenderGenerationService;
import com.github.foxy.commonImpl.FoxyInstance;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Brigadier command tree exposed at {@code /foxy}.
 *
 * <h2>Branches</h2>
 * <ul>
 *   <li>{@code /foxy status} &mdash; one-line summary of engine state plus the mip
 *       service's queue and throughput counters. The remaining commands
 *       (import, mipall) were removed once their work became fully automatic:
 *       {@link FoxyAutoBackfill} starts a region import on join and on dimension
 *       change, and {@link com.github.foxy.commonImpl.ImportManager.Task#onCompleted}
 *       enqueues the LOD pyramid rebuild as soon as the import finishes.</li>
 * </ul>
 *
 * <p>Registered on Forge's {@link RegisterClientCommandsEvent}.</p>
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
                .then(Commands.literal("status").executes(FoxyCommands::status)));
    }

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

        // Renderer-side diagnostics: these reveal whether the bottleneck is data
        // (storage) or rendering (mesh generation / GPU traversal). When data is
        // healthy but render is empty, expect non-zero TLNs but stalled mesh
        // queue or growing fail counter.
        int tlnCount = -1;
        int meshQueue = -1;
        int bakeInflight = -1;
        int bakedCount = -1;
        int meshFails = RenderGenerationService.MESH_FAILED_COUNTER.get();
        var lr = Minecraft.getInstance().levelRenderer;
        if (lr != null) {
            var renderer = ((IGetFoxyRenderSystem) lr).Foxy$getRenderSystem();
            if (renderer != null) {
                tlnCount = renderer.getTraversal().getTopNodeCount();
                meshQueue = renderer.getRenderGenerationService().getTaskCount();
                bakeInflight = renderer.getModelBakery().getProcessingCount();
                bakedCount = renderer.getModelBakery().factory.getBakedCount();
            }
        }

        final int tlnFinal = tlnCount;
        final int meshQueueFinal = meshQueue;
        final int bakeInflightFinal = bakeInflight;
        final int bakedFinal = bakedCount;
        src.sendSuccess(() -> Component.literal(
                "Foxy [" + instance.identifier().getWorldId() + "] sections=" + sections
                        + " mip(pending=" + pending + ",inFlight=" + inFlight + ",done=" + mipped + ")"
                        + " render(tln=" + tlnFinal + ",meshQ=" + meshQueueFinal + ",fails=" + meshFails + ")"
                        + " bake(inflight=" + bakeInflightFinal + ",done=" + bakedFinal + ")"
                        + (importing ? " IMPORTING" : "")
        ), false);
        return Command.SINGLE_SUCCESS;
    }
}
