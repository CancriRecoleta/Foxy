//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServer.WorldType;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen.Type;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.net.URL;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GetServerDetailsTask extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final RealmsServer server;
    private final Screen lastScreen;
    private final RealmsMainScreen mainScreen;
    private final ReentrantLock connectLock;

    public GetServerDetailsTask(RealmsMainScreen p_90332_, Screen p_90333_, RealmsServer p_90334_, ReentrantLock p_90335_) {
        this.lastScreen = p_90333_;
        this.mainScreen = p_90332_;
        this.server = p_90334_;
        this.connectLock = p_90335_;
    }

    public void run() {
        this.setTitle(Component.translatable("mco.connect.connecting"));

        RealmsServerAddress $$6;
        try {
            $$6 = this.fetchServerAddress();
        } catch (CancellationException var4) {
            LOGGER.info("User aborted connecting to realms");
            return;
        } catch (RealmsServiceException var5) {
            RealmsServiceException $$2 = var5;
            switch ($$2.realmsErrorCodeOrDefault(-1)) {
                case 6002:
                    setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
                    return;
                case 6006:
                    boolean $$3 = this.server.ownerUUID.equals(Minecraft.getInstance().getUser().getUuid());
                    setScreen((Screen)($$3 ? new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id, this.server.worldType == WorldType.MINIGAME) : new RealmsGenericErrorScreen(Component.translatable("mco.brokenworld.nonowner.title"), Component.translatable("mco.brokenworld.nonowner.error"), this.lastScreen)));
                    return;
                default:
                    this.error($$2.toString());
                    LOGGER.error("Couldn't connect to world", $$2);
                    return;
            }
        } catch (TimeoutException var6) {
            this.error(Component.translatable("mco.errorMessage.connectionFailure"));
            return;
        } catch (Exception var7) {
            Exception $$5 = var7;
            LOGGER.error("Couldn't connect to world", $$5);
            this.error($$5.getLocalizedMessage());
            return;
        }

        boolean $$7 = $$6.resourcePackUrl != null && $$6.resourcePackHash != null;
        Screen $$8 = $$7 ? this.resourcePackDownloadConfirmationScreen($$6, this::connectScreen) : this.connectScreen($$6);
        setScreen((Screen)$$8);
    }

    private RealmsServerAddress fetchServerAddress() throws RealmsServiceException, TimeoutException, CancellationException {
        RealmsClient $$0 = RealmsClient.create();
        int $$1 = 0;

        while($$1 < 40) {
            if (this.aborted()) {
                throw new CancellationException();
            }

            try {
                return $$0.join(this.server.id);
            } catch (RetryCallException var4) {
                RetryCallException $$2 = var4;
                pause((long)$$2.delaySeconds);
                ++$$1;
            }
        }

        throw new TimeoutException();
    }

    public RealmsLongRunningMcoTaskScreen connectScreen(RealmsServerAddress p_167638_) {
        return new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ConnectTask(this.lastScreen, this.server, p_167638_));
    }

    private RealmsLongConfirmationScreen resourcePackDownloadConfirmationScreen(RealmsServerAddress p_167640_, Function<RealmsServerAddress, Screen> p_167641_) {
        BooleanConsumer $$2 = (p_167645_) -> {
            try {
                if (p_167645_) {
                    this.scheduleResourcePackDownload(p_167640_).thenRun(() -> {
                        setScreen((Screen)p_167641_.apply(p_167640_));
                    }).exceptionally((p_287306_) -> {
                        Minecraft.getInstance().getDownloadedPackSource().clearServerPack();
                        LOGGER.error("Failed to download resource pack from {}", p_167640_, p_287306_);
                        setScreen(new RealmsGenericErrorScreen(Component.translatable("mco.download.resourcePack.fail"), this.lastScreen));
                        return null;
                    });
                    return;
                }

                setScreen(this.lastScreen);
            } finally {
                if (this.connectLock.isHeldByCurrentThread()) {
                    this.connectLock.unlock();
                }

            }

        };
        return new RealmsLongConfirmationScreen($$2, Type.INFO, Component.translatable("mco.configure.world.resourcepack.question.line1"), Component.translatable("mco.configure.world.resourcepack.question.line2"), true);
    }

    private CompletableFuture<?> scheduleResourcePackDownload(RealmsServerAddress p_167652_) {
        try {
            return Minecraft.getInstance().getDownloadedPackSource().downloadAndSelectResourcePack(new URL(p_167652_.resourcePackUrl), p_167652_.resourcePackHash, false);
        } catch (Exception var4) {
            Exception $$1 = var4;
            CompletableFuture<Void> $$2 = new CompletableFuture();
            $$2.completeExceptionally($$1);
            return $$2;
        }
    }
}
