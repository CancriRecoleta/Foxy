//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldCreationTask extends LongRunningTask {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String name;
    private final String motd;
    private final long worldId;
    private final Screen lastScreen;

    public WorldCreationTask(long p_90468_, String p_90469_, String p_90470_, Screen p_90471_) {
        this.worldId = p_90468_;
        this.name = p_90469_;
        this.motd = p_90470_;
        this.lastScreen = p_90471_;
    }

    public void run() {
        this.setTitle(Component.translatable("mco.create.world.wait"));
        RealmsClient $$0 = RealmsClient.create();

        try {
            $$0.initializeWorld(this.worldId, this.name, this.motd);
            setScreen(this.lastScreen);
        } catch (RealmsServiceException var3) {
            RealmsServiceException $$1 = var3;
            LOGGER.error("Couldn't create world");
            this.error($$1.toString());
        } catch (Exception var4) {
            Exception $$2 = var4;
            LOGGER.error("Could not create world");
            this.error($$2.getLocalizedMessage());
        }

    }
}
