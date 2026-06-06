//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RetryCallException;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SwitchSlotTask extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final long worldId;
    private final int slot;
    private final Runnable callback;

    public SwitchSlotTask(long p_90459_, int p_90460_, Runnable p_90461_) {
        this.worldId = p_90459_;
        this.slot = p_90460_;
        this.callback = p_90461_;
    }

    public void run() {
        RealmsClient $$0 = RealmsClient.create();
        this.setTitle(Component.translatable("mco.minigame.world.slot.screen.title"));

        for(int $$1 = 0; $$1 < 25; ++$$1) {
            try {
                if (this.aborted()) {
                    return;
                }

                if ($$0.switchSlot(this.worldId, this.slot)) {
                    this.callback.run();
                    break;
                }
            } catch (RetryCallException var4) {
                if (this.aborted()) {
                    return;
                }

                pause((long)var4.delaySeconds);
            } catch (Exception var5) {
                Exception $$3 = var5;
                if (this.aborted()) {
                    return;
                }

                LOGGER.error("Couldn't switch world!");
                this.error($$3.toString());
            }
        }

    }
}
