//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.quickplay;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class QuickPlay {
    public static final Component ERROR_TITLE = Component.translatable("quickplay.error.title");
    private static final Component INVALID_IDENTIFIER = Component.translatable("quickplay.error.invalid_identifier");
    private static final Component REALM_CONNECT = Component.translatable("quickplay.error.realm_connect");
    private static final Component REALM_PERMISSION = Component.translatable("quickplay.error.realm_permission");
    private static final Component TO_TITLE = Component.translatable("gui.toTitle");
    private static final Component TO_WORLD_LIST = Component.translatable("gui.toWorld");
    private static final Component TO_REALMS_LIST = Component.translatable("gui.toRealms");

    public QuickPlay() {
    }

    public static void connect(Minecraft p_279319_, GameConfig.QuickPlayData p_279291_, ReloadInstance p_279328_, RealmsClient p_279322_) {
        String $$4 = p_279291_.singleplayer();
        String $$5 = p_279291_.multiplayer();
        String $$6 = p_279291_.realms();
        p_279328_.done().thenRunAsync(() -> {
            if (!Util.isBlank($$4)) {
                joinSingleplayerWorld(p_279319_, $$4);
            } else if (!Util.isBlank($$5)) {
                joinMultiplayerWorld(p_279319_, $$5);
            } else if (!Util.isBlank($$6)) {
                joinRealmsWorld(p_279319_, p_279322_, $$6);
            }

        }, p_279319_);
    }

    private static void joinSingleplayerWorld(Minecraft p_279420_, String p_279459_) {
        if (!p_279420_.getLevelSource().levelExists(p_279459_)) {
            Screen $$2 = new SelectWorldScreen(new TitleScreen());
            p_279420_.setScreen(new DisconnectedScreen($$2, ERROR_TITLE, INVALID_IDENTIFIER, TO_WORLD_LIST));
        } else {
            p_279420_.forceSetScreen(new GenericDirtMessageScreen(Component.translatable("selectWorld.data_read")));
            p_279420_.createWorldOpenFlows().loadLevel(new TitleScreen(), p_279459_);
        }
    }

    private static void joinMultiplayerWorld(Minecraft p_279276_, String p_279128_) {
        ServerList $$2 = new ServerList(p_279276_);
        $$2.load();
        ServerData $$3 = $$2.get(p_279128_);
        if ($$3 == null) {
            $$3 = new ServerData(I18n.get("selectServer.defaultName"), p_279128_, false);
            $$2.add($$3, true);
            $$2.save();
        }

        ServerAddress $$4 = ServerAddress.parseString(p_279128_);
        ConnectScreen.startConnecting(new JoinMultiplayerScreen(new TitleScreen()), p_279276_, $$4, $$3, true);
    }

    private static void joinRealmsWorld(Minecraft p_279320_, RealmsClient p_279468_, String p_279371_) {
        long $$9;
        RealmsServerList $$10;
        TitleScreen $$13;
        RealmsMainScreen $$12;
        try {
            $$9 = Long.parseLong(p_279371_);
            $$10 = p_279468_.listWorlds();
        } catch (NumberFormatException var9) {
            $$12 = new RealmsMainScreen(new TitleScreen());
            p_279320_.setScreen(new DisconnectedScreen($$12, ERROR_TITLE, INVALID_IDENTIFIER, TO_REALMS_LIST));
            return;
        } catch (RealmsServiceException var10) {
            $$13 = new TitleScreen();
            p_279320_.setScreen(new DisconnectedScreen($$13, ERROR_TITLE, REALM_CONNECT, TO_TITLE));
            return;
        }

        RealmsServer $$11 = (RealmsServer)$$10.servers.stream().filter((p_279424_) -> {
            return p_279424_.id == $$9;
        }).findFirst().orElse((Object)null);
        if ($$11 == null) {
            $$12 = new RealmsMainScreen(new TitleScreen());
            p_279320_.setScreen(new DisconnectedScreen($$12, ERROR_TITLE, REALM_PERMISSION, TO_REALMS_LIST));
        } else {
            $$13 = new TitleScreen();
            GetServerDetailsTask $$14 = new GetServerDetailsTask(new RealmsMainScreen($$13), $$13, $$11, new ReentrantLock());
            p_279320_.setScreen(new RealmsLongRunningMcoTaskScreen($$13, $$14));
        }
    }
}
