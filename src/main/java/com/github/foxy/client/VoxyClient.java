package com.github.foxy.client;

import com.github.foxy.client.core.gl.Capabilities;
import com.github.foxy.client.core.rendering.util.SharedIndexBuffer;
import com.github.foxy.common.Logger;
import com.github.foxy.commonImpl.VoxyCommon;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;

public class VoxyClient {
    private static FileLock EXCLUSIVE_LOCK;

    // Wires Foxy's client-side Forge listeners. Called from FoxyMod via DistExecutor so this class
    // (and the Minecraft client classes it pulls in) is never loaded on a dedicated server.
    public static void bootstrapClient() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener((FMLClientSetupEvent event) -> event.enqueueWork(VoxyClient::onClientSetup));

        MinecraftForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event) -> {
            if (VoxyCommon.isAvailable()) {
                event.getDispatcher().register(VoxyCommands.register());
            }
        });
    }

    private static void onClientSetup() {
        DebugEntries.init();
    }

    // The heavy client init (GL capability probing, instance factory) runs from the Minecraft mixin
    // once a GL context exists, mirroring upstream voxy which also defers this past mod construction.
    public static void initVoxyClient() {
        Capabilities.init();//Ensure clinit is called

        if (Capabilities.INSTANCE.hasBrokenDepthSampler) {
            Logger.error("AMD broken depth sampler detected, foxy does not work correctly and has been disabled, this will hopefully be fixed in the future");
        }

        boolean systemSupported = Capabilities.INSTANCE.compute && Capabilities.INSTANCE.indirectParameters && !Capabilities.INSTANCE.hasBrokenDepthSampler;
        if (!systemSupported) {
             Logger.error("Foxy is unsupported on your system.");
        }

        if (systemSupported && System.getProperty("foxy.exclusiveLock", "false").equalsIgnoreCase("true")) {
            //Try acquire the lock file
            var vf = Minecraft.getInstance().gameDirectory.toPath().resolve(".foxy");
            if (!vf.toFile().isDirectory()) {
                vf.toFile().mkdir();
            }
            try {
                FileOutputStream fis = new FileOutputStream(vf.resolve("foxy.lock").toFile());
                EXCLUSIVE_LOCK = fis.getChannel().lock(0, Long.MAX_VALUE, false);
            } catch (NonWritableChannelException | IOException e) {
                //If some error write to log and unsupport
                Logger.error("Failed to acquire exclusive foxy lock file, mod will be disabled");
                systemSupported = false;
            }

        }

        if (systemSupported) {

            SharedIndexBuffer.INSTANCE.id();

            VoxyCommon.setInstanceFactory(VoxyClientInstance::new);

            if (!Capabilities.INSTANCE.subgroup) {
                Logger.warn("GPU does not support subgroup operations, expect some performance degradation");
            }

        }
    }

    public static boolean isFrexActive() {
        // FREX flawless-frames is a Fabric rendering-extension entrypoint with no Forge equivalent.
        return false;
    }

    public static int getOcclusionDebugState() {
        return 0;
    }

    public static boolean disableSodiumChunkRender() {
        return false;// getOcclusionDebugState() != 0;
    }
}
