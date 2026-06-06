//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.network;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.RandomSource;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.network.DualStackUtils;
import net.minecraftforge.network.NetworkHooks;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class ServerLoginPacketListenerImpl implements ServerLoginPacketListener, TickablePacketListener {
    private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_TICKS_BEFORE_LOGIN = 600;
    private static final RandomSource RANDOM = RandomSource.create();
    private final byte[] challenge;
    final MinecraftServer server;
    final Connection connection;
    State state;
    private int tick;
    @Nullable
    public GameProfile gameProfile;
    private final String serverId;
    @Nullable
    private ServerPlayer delayedAcceptPlayer;

    public ServerLoginPacketListenerImpl(MinecraftServer p_10027_, Connection p_10028_) {
        this.state = net.minecraft.server.network.ServerLoginPacketListenerImpl.State.HELLO;
        this.serverId = "";
        this.server = p_10027_;
        this.connection = p_10028_;
        this.challenge = Ints.toByteArray(RANDOM.nextInt());
    }

    public void tick() {
        if (this.state == net.minecraft.server.network.ServerLoginPacketListenerImpl.State.NEGOTIATING) {
            boolean negotiationComplete = NetworkHooks.tickNegotiation(this, this.connection, this.delayedAcceptPlayer);
            if (negotiationComplete) {
                this.state = net.minecraft.server.network.ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
            }
        } else if (this.state == net.minecraft.server.network.ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT) {
            this.handleAcceptedLogin();
        } else if (this.state == net.minecraft.server.network.ServerLoginPacketListenerImpl.State.DELAY_ACCEPT) {
            ServerPlayer serverplayer = this.server.getPlayerList().getPlayer(this.gameProfile.getId());
            if (serverplayer == null) {
                this.state = net.minecraft.server.network.ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
                this.placeNewPlayer(this.delayedAcceptPlayer);
                this.delayedAcceptPlayer = null;
            }
        }

        if (this.tick++ == 600) {
            this.disconnect(Component.translatable("multiplayer.disconnect.slow_login"));
        }

    }

    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    public void disconnect(Component p_10054_) {
        try {
            LOGGER.info("Disconnecting {}: {}", this.getUserName(), p_10054_.getString());
            this.connection.send(new ClientboundLoginDisconnectPacket(p_10054_));
            this.connection.disconnect(p_10054_);
        } catch (Exception var3) {
            Exception exception = var3;
            LOGGER.error("Error whilst disconnecting player", exception);
        }

    }

    public void handleAcceptedLogin() {
        if (!this.gameProfile.isComplete()) {
            this.gameProfile = this.createFakeProfile(this.gameProfile);
        }

        Component component = this.server.getPlayerList().canPlayerLogin(this.connection.getRemoteAddress(), this.gameProfile);
        if (component != null) {
            this.disconnect(component);
        } else {
            this.state = net.minecraft.server.network.ServerLoginPacketListenerImpl.State.ACCEPTED;
            if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
                this.connection.send(new ClientboundLoginCompressionPacket(this.server.getCompressionThreshold()), PacketSendListener.thenRun(() -> {
                    this.connection.setupCompression(this.server.getCompressionThreshold(), true);
                }));
            }

            this.connection.send(new ClientboundGameProfilePacket(this.gameProfile));
            ServerPlayer serverplayer = this.server.getPlayerList().getPlayer(this.gameProfile.getId());

            try {
                ServerPlayer serverplayer1 = this.server.getPlayerList().getPlayerForLogin(this.gameProfile);
                if (serverplayer != null) {
                    this.state = net.minecraft.server.network.ServerLoginPacketListenerImpl.State.DELAY_ACCEPT;
                    this.delayedAcceptPlayer = serverplayer1;
                } else {
                    this.placeNewPlayer(serverplayer1);
                }
            } catch (Exception var5) {
                Exception exception = var5;
                LOGGER.error("Couldn't place player in world", exception);
                Component component1 = Component.translatable("multiplayer.disconnect.invalid_player_data");
                this.connection.send(new ClientboundDisconnectPacket(component1));
                this.connection.disconnect(component1);
            }
        }

    }

    private void placeNewPlayer(ServerPlayer p_143700_) {
        this.server.getPlayerList().placeNewPlayer(this.connection, p_143700_);
    }

    public void onDisconnect(Component p_10043_) {
        LOGGER.info("{} lost connection: {}", this.getUserName(), p_10043_.getString());
    }

    public String getUserName() {
        String addressString = DualStackUtils.getAddressString(this.connection.getRemoteAddress());
        return this.gameProfile != null ? this.gameProfile + " (" + addressString + ")" : addressString;
    }

    public void handleHello(ServerboundHelloPacket p_10047_) {
        Validate.validState(this.state == net.minecraft.server.network.ServerLoginPacketListenerImpl.State.HELLO, "Unexpected hello packet", new Object[0]);
        Validate.validState(isValidUsername(p_10047_.name()), "Invalid characters in username", new Object[0]);
        GameProfile gameprofile = this.server.getSingleplayerProfile();
        if (gameprofile != null && p_10047_.name().equalsIgnoreCase(gameprofile.getName())) {
            this.gameProfile = gameprofile;
            this.state = net.minecraft.server.network.ServerLoginPacketListenerImpl.State.NEGOTIATING;
        } else {
            this.gameProfile = new GameProfile((UUID)null, p_10047_.name());
            if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
                this.state = net.minecraft.server.network.ServerLoginPacketListenerImpl.State.KEY;
                this.connection.send(new ClientboundHelloPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.challenge));
            } else {
                this.state = net.minecraft.server.network.ServerLoginPacketListenerImpl.State.NEGOTIATING;
            }
        }

    }

    public static boolean isValidUsername(String p_203793_) {
        return p_203793_.chars().filter((p_203791_) -> {
            return p_203791_ <= 32 || p_203791_ >= 127;
        }).findAny().isEmpty();
    }

    public void handleKey(ServerboundKeyPacket p_10049_) {
        Validate.validState(this.state == net.minecraft.server.network.ServerLoginPacketListenerImpl.State.KEY, "Unexpected key packet", new Object[0]);

        final String s;
        try {
            PrivateKey privatekey = this.server.getKeyPair().getPrivate();
            if (!p_10049_.isChallengeValid(this.challenge, privatekey)) {
                throw new IllegalStateException("Protocol error");
            }

            SecretKey secretkey = p_10049_.getSecretKey(privatekey);
            Cipher cipher = Crypt.getCipher(2, secretkey);
            Cipher cipher1 = Crypt.getCipher(1, secretkey);
            s = (new BigInteger(Crypt.digestData("", this.server.getKeyPair().getPublic(), secretkey))).toString(16);
            this.state = net.minecraft.server.network.ServerLoginPacketListenerImpl.State.AUTHENTICATING;
            this.connection.setEncryptionKey(cipher, cipher1);
        } catch (CryptException var7) {
            CryptException cryptexception = var7;
            throw new IllegalStateException("Protocol error", cryptexception);
        }

        Thread thread = new Thread(SidedThreadGroups.SERVER, "User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet()) {
            public void run() {
                GameProfile gameprofile = ServerLoginPacketListenerImpl.this.gameProfile;

                try {
                    ServerLoginPacketListenerImpl.this.gameProfile = ServerLoginPacketListenerImpl.this.server.getSessionService().hasJoinedServer(new GameProfile((UUID)null, gameprofile.getName()), s, this.getAddress());
                    if (ServerLoginPacketListenerImpl.this.gameProfile != null) {
                        ServerLoginPacketListenerImpl.LOGGER.info("UUID of player {} is {}", ServerLoginPacketListenerImpl.this.gameProfile.getName(), ServerLoginPacketListenerImpl.this.gameProfile.getId());
                        ServerLoginPacketListenerImpl.this.state = net.minecraft.server.network.ServerLoginPacketListenerImpl.State.NEGOTIATING;
                    } else if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                        ServerLoginPacketListenerImpl.LOGGER.warn("Failed to verify username but will let them in anyway!");
                        ServerLoginPacketListenerImpl.this.gameProfile = gameprofile;
                        ServerLoginPacketListenerImpl.this.state = net.minecraft.server.network.ServerLoginPacketListenerImpl.State.NEGOTIATING;
                    } else {
                        ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.unverified_username"));
                        ServerLoginPacketListenerImpl.LOGGER.error("Username '{}' tried to join with an invalid session", gameprofile.getName());
                    }
                } catch (AuthenticationUnavailableException var3) {
                    if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                        ServerLoginPacketListenerImpl.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                        ServerLoginPacketListenerImpl.this.gameProfile = gameprofile;
                        ServerLoginPacketListenerImpl.this.state = net.minecraft.server.network.ServerLoginPacketListenerImpl.State.NEGOTIATING;
                    } else {
                        ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.authservers_down"));
                        ServerLoginPacketListenerImpl.LOGGER.error("Couldn't verify username because servers are unavailable");
                    }
                }

            }

            @Nullable
            private InetAddress getAddress() {
                SocketAddress socketaddress = ServerLoginPacketListenerImpl.this.connection.getRemoteAddress();
                return ServerLoginPacketListenerImpl.this.server.getPreventProxyConnections() && socketaddress instanceof InetSocketAddress ? ((InetSocketAddress)socketaddress).getAddress() : null;
            }
        };
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
    }

    public void handleCustomQueryPacket(ServerboundCustomQueryPacket p_10045_) {
        if (!NetworkHooks.onCustomPayload(p_10045_, this.connection)) {
            this.disconnect(Component.translatable("multiplayer.disconnect.unexpected_query_response"));
        }

    }

    protected GameProfile createFakeProfile(GameProfile p_10039_) {
        UUID uuid = UUIDUtil.createOfflinePlayerUUID(p_10039_.getName());
        return new GameProfile(uuid, p_10039_.getName());
    }

    static enum State {
        HELLO,
        KEY,
        AUTHENTICATING,
        NEGOTIATING,
        READY_TO_ACCEPT,
        DELAY_ACCEPT,
        ACCEPTED;

        private State() {
        }
    }
}
