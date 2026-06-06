//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServer.State;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class CloseServerTask extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;

    public CloseServerTask(RealmsServer p_90302_, RealmsConfigureWorldScreen p_90303_) {
        this.serverData = p_90302_;
        this.configureScreen = p_90303_;
    }

    public void run() {
        this.setTitle(Component.translatable("mco.configure.world.closing"));
        RealmsClient $$0 = RealmsClient.create();

        for(int $$1 = 0; $$1 < 25; ++$$1) {
            if (this.aborted()) {
                return;
            }

            try {
                boolean $$2 = $$0.close(this.serverData.id);
                if ($$2) {
                    this.configureScreen.stateChanged();
                    this.serverData.state = State.CLOSED;
                    setScreen(this.configureScreen);
                    break;
                }
            } catch (RetryCallException var4) {
                if (this.aborted()) {
                    return;
                }

                pause((long)var4.delaySeconds);
            } catch (Exception var5) {
                Exception $$4 = var5;
                if (this.aborted()) {
                    return;
                }

                LOGGER.error("Failed to close server", $$4);
                this.error("Failed to close the server");
            }
        }

    }
}
