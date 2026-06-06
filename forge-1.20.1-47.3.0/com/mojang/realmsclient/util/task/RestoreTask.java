//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RestoreTask extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Backup backup;
    private final long worldId;
    private final RealmsConfigureWorldScreen lastScreen;

    public RestoreTask(Backup p_90443_, long p_90444_, RealmsConfigureWorldScreen p_90445_) {
        this.backup = p_90443_;
        this.worldId = p_90444_;
        this.lastScreen = p_90445_;
    }

    public void run() {
        this.setTitle(Component.translatable("mco.backup.restoring"));
        RealmsClient $$0 = RealmsClient.create();
        int $$1 = 0;

        while($$1 < 25) {
            try {
                if (this.aborted()) {
                    return;
                }

                $$0.restoreWorld(this.worldId, this.backup.backupId);
                pause(1L);
                if (this.aborted()) {
                    return;
                }

                setScreen(this.lastScreen.getNewScreen());
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

                LOGGER.error("Couldn't restore backup", var5);
                setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
                return;
            } catch (Exception var6) {
                Exception $$4 = var6;
                if (this.aborted()) {
                    return;
                }

                LOGGER.error("Couldn't restore backup", $$4);
                this.error($$4.getLocalizedMessage());
                return;
            }
        }

    }
}
