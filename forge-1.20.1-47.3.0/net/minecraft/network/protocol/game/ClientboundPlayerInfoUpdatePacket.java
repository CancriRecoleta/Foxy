//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class ClientboundPlayerInfoUpdatePacket implements Packet<ClientGamePacketListener> {
    private final EnumSet<Action> actions;
    private final List<Entry> entries;

    public ClientboundPlayerInfoUpdatePacket(EnumSet<Action> p_251739_, Collection<ServerPlayer> p_251579_) {
        this.actions = p_251739_;
        this.entries = p_251579_.stream().map(Entry::new).toList();
    }

    public ClientboundPlayerInfoUpdatePacket(Action p_251648_, ServerPlayer p_252273_) {
        this.actions = EnumSet.of(p_251648_);
        this.entries = List.of(new Entry(p_252273_));
    }

    public static ClientboundPlayerInfoUpdatePacket createPlayerInitializing(Collection<ServerPlayer> p_252314_) {
        EnumSet<Action> $$1 = EnumSet.of(net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT, net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY, net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME);
        return new ClientboundPlayerInfoUpdatePacket($$1, p_252314_);
    }

    public ClientboundPlayerInfoUpdatePacket(FriendlyByteBuf p_251820_) {
        this.actions = p_251820_.readEnumSet(Action.class);
        this.entries = p_251820_.readList((p_249950_) -> {
            EntryBuilder $$1 = new EntryBuilder(p_249950_.readUUID());
            Iterator var3 = this.actions.iterator();

            while(var3.hasNext()) {
                Action $$2 = (Action)var3.next();
                $$2.reader.read($$1, p_249950_);
            }

            return $$1.build();
        });
    }

    public void write(FriendlyByteBuf p_249907_) {
        p_249907_.writeEnumSet(this.actions, Action.class);
        p_249907_.writeCollection(this.entries, (p_251434_, p_252303_) -> {
            p_251434_.writeUUID(p_252303_.profileId());
            Iterator var3 = this.actions.iterator();

            while(var3.hasNext()) {
                Action $$2 = (Action)var3.next();
                $$2.writer.write(p_251434_, p_252303_);
            }

        });
    }

    public void handle(ClientGamePacketListener p_249935_) {
        p_249935_.handlePlayerInfoUpdate(this);
    }

    public EnumSet<Action> actions() {
        return this.actions;
    }

    public List<Entry> entries() {
        return this.entries;
    }

    public List<Entry> newEntries() {
        return this.actions.contains(net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER) ? this.entries : List.of();
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("actions", this.actions).add("entries", this.entries).toString();
    }

    public static record Entry(UUID profileId, GameProfile profile, boolean listed, int latency, GameType gameMode, @Nullable Component displayName, @Nullable RemoteChatSession.Data chatSession) {
        Entry(ServerPlayer p_252094_) {
            this(p_252094_.getUUID(), p_252094_.getGameProfile(), true, p_252094_.latency, p_252094_.gameMode.getGameModeForPlayer(), p_252094_.getTabListDisplayName(), (RemoteChatSession.Data)Optionull.map(p_252094_.getChatSession(), RemoteChatSession::asData));
        }

        public Entry(UUID profileId, GameProfile profile, boolean listed, int latency, GameType gameMode, @Nullable Component displayName, @Nullable RemoteChatSession.Data chatSession) {
            this.profileId = profileId;
            this.profile = profile;
            this.listed = listed;
            this.latency = latency;
            this.gameMode = gameMode;
            this.displayName = displayName;
            this.chatSession = chatSession;
        }

        public UUID profileId() {
            return this.profileId;
        }

        public GameProfile profile() {
            return this.profile;
        }

        public boolean listed() {
            return this.listed;
        }

        public int latency() {
            return this.latency;
        }

        public GameType gameMode() {
            return this.gameMode;
        }

        @Nullable
        public Component displayName() {
            return this.displayName;
        }

        @Nullable
        public RemoteChatSession.Data chatSession() {
            return this.chatSession;
        }
    }

    public static enum Action {
        ADD_PLAYER((p_251116_, p_251884_) -> {
            GameProfile $$2 = new GameProfile(p_251116_.profileId, p_251884_.readUtf(16));
            $$2.getProperties().putAll(p_251884_.readGameProfileProperties());
            p_251116_.profile = $$2;
        }, (p_252022_, p_250357_) -> {
            p_252022_.writeUtf(p_250357_.profile().getName(), 16);
            p_252022_.writeGameProfileProperties(p_250357_.profile().getProperties());
        }),
        INITIALIZE_CHAT((p_253468_, p_253469_) -> {
            p_253468_.chatSession = (RemoteChatSession.Data)p_253469_.readNullable(RemoteChatSession.Data::read);
        }, (p_253470_, p_253471_) -> {
            p_253470_.writeNullable(p_253471_.chatSession, RemoteChatSession.Data::write);
        }),
        UPDATE_GAME_MODE((p_251118_, p_248955_) -> {
            p_251118_.gameMode = GameType.byId(p_248955_.readVarInt());
        }, (p_249222_, p_250996_) -> {
            p_249222_.writeVarInt(p_250996_.gameMode().getId());
        }),
        UPDATE_LISTED((p_248777_, p_248837_) -> {
            p_248777_.listed = p_248837_.readBoolean();
        }, (p_249355_, p_251658_) -> {
            p_249355_.writeBoolean(p_251658_.listed());
        }),
        UPDATE_LATENCY((p_252263_, p_248964_) -> {
            p_252263_.latency = p_248964_.readVarInt();
        }, (p_248830_, p_251312_) -> {
            p_248830_.writeVarInt(p_251312_.latency());
        }),
        UPDATE_DISPLAY_NAME((p_248840_, p_251000_) -> {
            p_248840_.displayName = (Component)p_251000_.readNullable(FriendlyByteBuf::readComponent);
        }, (p_251723_, p_251870_) -> {
            p_251723_.writeNullable(p_251870_.displayName(), FriendlyByteBuf::writeComponent);
        });

        final Reader reader;
        final Writer writer;

        private Action(Reader p_249392_, Writer p_250487_) {
            this.reader = p_249392_;
            this.writer = p_250487_;
        }

        public interface Reader {
            void read(EntryBuilder var1, FriendlyByteBuf var2);
        }

        public interface Writer {
            void write(FriendlyByteBuf var1, Entry var2);
        }
    }

    private static class EntryBuilder {
        final UUID profileId;
        GameProfile profile;
        boolean listed;
        int latency;
        GameType gameMode;
        @Nullable
        Component displayName;
        @Nullable
        RemoteChatSession.Data chatSession;

        EntryBuilder(UUID p_251670_) {
            this.gameMode = GameType.DEFAULT_MODE;
            this.profileId = p_251670_;
            this.profile = new GameProfile(p_251670_, (String)null);
        }

        Entry build() {
            return new Entry(this.profileId, this.profile, this.listed, this.latency, this.gameMode, this.displayName, this.chatSession);
        }
    }
}
