//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DownloadTask extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final long worldId;
    private final int slot;
    private final Screen lastScreen;
    private final String downloadName;

    public DownloadTask(long p_90320_, int p_90321_, String p_90322_, Screen p_90323_) {
        this.worldId = p_90320_;
        this.slot = p_90321_;
        this.lastScreen = p_90323_;
        this.downloadName = p_90322_;
    }

    public void run() {
        this.setTitle(Component.translatable("mco.download.preparing"));
        RealmsClient $$0 = RealmsClient.create();
        int $$1 = 0;

        while($$1 < 25) {
            try {
                if (this.aborted()) {
                    return;
                }

                WorldDownload $$2 = $$0.requestDownloadInfo(this.worldId, this.slot);
                pause(1L);
                if (this.aborted()) {
                    return;
                }

                setScreen(new RealmsDownloadLatestWorldScreen(this.lastScreen, $$2, this.downloadName, (p_90325_) -> {
                }));
                return;
            } catch (RetryCallException var4) {
                if (this.aborted()) {
                    return;
                }

                pause((long)var4.delaySeconds);
                ++$$1;
            } catch (RealmsServiceException var5) {
                if (this.aborted()) {
                    return;
                }

                LOGGER.error("Couldn't download world data");
                setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
                return;
            } catch (Exception var6) {
                Exception $$5 = var6;
                if (this.aborted()) {
                    return;
                }

                LOGGER.error("Couldn't download world data", $$5);
                this.error($$5.getLocalizedMessage());
                return;
            }
        }

    }
}
