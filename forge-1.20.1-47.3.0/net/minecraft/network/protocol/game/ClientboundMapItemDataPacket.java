//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class ClientboundMapItemDataPacket implements Packet<ClientGamePacketListener> {
    private final int mapId;
    private final byte scale;
    private final boolean locked;
    @Nullable
    private final List<MapDecoration> decorations;
    @Nullable
    private final MapItemSavedData.MapPatch colorPatch;

    public ClientboundMapItemDataPacket(int p_178970_, byte p_178971_, boolean p_178972_, @Nullable Collection<MapDecoration> p_178973_, @Nullable MapItemSavedData.MapPatch p_178974_) {
        this.mapId = p_178970_;
        this.scale = p_178971_;
        this.locked = p_178972_;
        this.decorations = p_178973_ != null ? Lists.newArrayList(p_178973_) : null;
        this.colorPatch = p_178974_;
    }

    public ClientboundMapItemDataPacket(FriendlyByteBuf p_178976_) {
        this.mapId = p_178976_.readVarInt();
        this.scale = p_178976_.readByte();
        this.locked = p_178976_.readBoolean();
        this.decorations = (List)p_178976_.readNullable((p_237731_) -> {
            return p_237731_.readList((p_178981_) -> {
                MapDecoration.Type $$1 = (MapDecoration.Type)p_178981_.readEnum(MapDecoration.Type.class);
                byte $$2 = p_178981_.readByte();
                byte $$3 = p_178981_.readByte();
                byte $$4 = (byte)(p_178981_.readByte() & 15);
                Component $$5 = (Component)p_178981_.readNullable(FriendlyByteBuf::readComponent);
                return new MapDecoration($$1, $$2, $$3, $$4, $$5);
            });
        });
        int $$1 = p_178976_.readUnsignedByte();
        if ($$1 > 0) {
            int $$2 = p_178976_.readUnsignedByte();
            int $$3 = p_178976_.readUnsignedByte();
            int $$4 = p_178976_.readUnsignedByte();
            byte[] $$5 = p_178976_.readByteArray();
            this.colorPatch = new MapItemSavedData.MapPatch($$3, $$4, $$1, $$2, $$5);
        } else {
            this.colorPatch = null;
        }

    }

    public void write(FriendlyByteBuf p_132447_) {
        p_132447_.writeVarInt(this.mapId);
        p_132447_.writeByte(this.scale);
        p_132447_.writeBoolean(this.locked);
        p_132447_.writeNullable(this.decorations, (p_237728_, p_237729_) -> {
            p_237728_.writeCollection(p_237729_, (p_237725_, p_237726_) -> {
                p_237725_.writeEnum(p_237726_.getType());
                p_237725_.writeByte(p_237726_.getX());
                p_237725_.writeByte(p_237726_.getY());
                p_237725_.writeByte(p_237726_.getRot() & 15);
                p_237725_.writeNullable(p_237726_.getName(), FriendlyByteBuf::writeComponent);
            });
        });
        if (this.colorPatch != null) {
            p_132447_.writeByte(this.colorPatch.width);
            p_132447_.writeByte(this.colorPatch.height);
            p_132447_.writeByte(this.colorPatch.startX);
            p_132447_.writeByte(this.colorPatch.startY);
            p_132447_.writeByteArray(this.colorPatch.mapColors);
        } else {
            p_132447_.writeByte(0);
        }

    }

    public void handle(ClientGamePacketListener p_132444_) {
        p_132444_.handleMapItemData(this);
    }

    public int getMapId() {
        return this.mapId;
    }

    public void applyToMap(MapItemSavedData p_132438_) {
        if (this.decorations != null) {
            p_132438_.addClientSideDecorations(this.decorations);
        }

        if (this.colorPatch != null) {
            this.colorPatch.applyToMap(p_132438_);
        }

    }

    public byte getScale() {
        return this.scale;
    }

    public boolean isLocked() {
        return this.locked;
    }
}
