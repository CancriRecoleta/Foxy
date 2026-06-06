//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.realms;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServer.WorldType;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.quickplay.QuickPlayLog.Type;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsConnect {
    static final Logger LOGGER = LogUtils.getLogger();
    final Screen onlineScreen;
    volatile boolean aborted;
    @Nullable
    Connection connection;

    public RealmsConnect(Screen p_120693_) {
        this.onlineScreen = p_120693_;
    }

    public void connect(final RealmsServer p_175032_, ServerAddress p_175033_) {
        final Minecraft $$2 = Minecraft.getInstance();
        $$2.setConnectedToRealms(true);
        $$2.prepareForMultiplayer();
        $$2.getNarrator().sayNow((Component)Component.translatable("mco.connect.success"));
        final String $$3 = p_175033_.getHost();
        final int $$4 = p_175033_.getPort();
        (new Thread("Realms-connect-task") {
            public void run() {
                InetSocketAddress $$0 = null;

                String $$2x;
                try {
                    $$0 = new InetSocketAddress($$3, $$4);
                    if (RealmsConnect.this.aborted) {
                        return;
                    }

                    RealmsConnect.this.connection = Connection.connectToServer($$0, $$2.options.useNativeTransport());
                    if (RealmsConnect.this.aborted) {
                        return;
                    }

                    ClientHandshakePacketListenerImpl $$1 = new ClientHandshakePacketListenerImpl(RealmsConnect.this.connection, $$2, p_175032_.toServerData($$3), RealmsConnect.this.onlineScreen, false, (Duration)null, (p_120726_) -> {
                    });
                    if (p_175032_.worldType == WorldType.MINIGAME) {
                        $$1.setMinigameName(p_175032_.minigameName);
                    }

                    RealmsConnect.this.connection.setListener($$1);
                    if (RealmsConnect.this.aborted) {
                        return;
                    }

                    RealmsConnect.this.connection.send(new ClientIntentionPacket($$3, $$4, ConnectionProtocol.LOGIN));
                    if (RealmsConnect.this.aborted) {
                        return;
                    }

                    $$2x = $$2.getUser().getName();
                    UUID $$3x = $$2.getUser().getProfileId();
                    RealmsConnect.this.connection.send(new ServerboundHelloPacket($$2x, Optional.ofNullable($$3x)));
                    $$2.updateReportEnvironment(ReportEnvironment.realm(p_175032_));
                    $$2.quickPlayLog().setWorldData(Type.REALMS, String.valueOf(p_175032_.id), p_175032_.name);
                } catch (Exception var5) {
                    Exception $$4x = var5;
                    $$2.getDownloadedPackSource().clearServerPack();
                    if (RealmsConnect.this.aborted) {
                        return;
                    }

                    RealmsConnect.LOGGER.error("Couldn't connect to world", $$4x);
                    $$2x = $$4x.toString();
                    if ($$0 != null) {
                        String $$6 = "" + $$0 + ":" + $$4;
                        $$2x = $$2x.replaceAll($$6, "");
                    }

                    DisconnectedRealmsScreen $$7 = new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, CommonComponents.CONNECT_FAILED, Component.translatable("disconnect.genericReason", $$2x));
                    $$2.execute(() -> {
                        $$2.setScreen($$7);
                    });
                }

            }
        }).start();
    }

    public void abort() {
        this.aborted = true;
        if (this.connection != null && this.connection.isConnected()) {
            this.connection.disconnect(Component.translatable("disconnect.genericReason"));
            this.connection.handleDisconnection();
        }

    }

    public void tick() {
        if (this.connection != null) {
            if (this.connection.isConnected()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }

    }
}
