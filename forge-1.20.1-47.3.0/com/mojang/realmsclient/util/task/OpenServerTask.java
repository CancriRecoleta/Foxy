//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServer.State;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class OpenServerTask extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final RealmsServer serverData;
    private final Screen returnScreen;
    private final boolean join;
    private final RealmsMainScreen mainScreen;
    private final Minecraft minecraft;

    public OpenServerTask(RealmsServer p_181344_, Screen p_181345_, RealmsMainScreen p_181346_, boolean p_181347_, Minecraft p_181348_) {
        this.serverData = p_181344_;
        this.returnScreen = p_181345_;
        this.join = p_181347_;
        this.mainScreen = p_181346_;
        this.minecraft = p_181348_;
    }

    public void run() {
        this.setTitle(Component.translatable("mco.configure.world.opening"));
        RealmsClient $$0 = RealmsClient.create();

        for(int $$1 = 0; $$1 < 25; ++$$1) {
            if (this.aborted()) {
                return;
            }

            try {
                boolean $$2 = $$0.open(this.serverData.id);
                if ($$2) {
                    this.minecraft.execute(() -> {
                        if (this.returnScreen instanceof RealmsConfigureWorldScreen) {
                            ((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
                        }

                        this.serverData.state = State.OPEN;
                        if (this.join) {
                            this.mainScreen.play(this.serverData, this.returnScreen);
                        } else {
                            this.minecraft.setScreen(this.returnScreen);
                        }

                    });
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

                LOGGER.error("Failed to open server", $$4);
                this.error("Failed to open the server");
            }
        }

    }
}
