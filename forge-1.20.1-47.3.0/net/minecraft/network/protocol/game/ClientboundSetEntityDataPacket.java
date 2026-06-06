//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;

public record ClientboundSetEntityDataPacket(int id, List<SynchedEntityData.DataValue<?>> packedItems) implements Packet<ClientGamePacketListener> {
    public static final int EOF_MARKER = 255;

    public ClientboundSetEntityDataPacket(FriendlyByteBuf p_179290_) {
        this(p_179290_.readVarInt(), unpack(p_179290_));
    }

    public ClientboundSetEntityDataPacket(int id, List<SynchedEntityData.DataValue<?>> packedItems) {
        this.id = id;
        this.packedItems = packedItems;
    }

    private static void pack(List<SynchedEntityData.DataValue<?>> p_253940_, FriendlyByteBuf p_253901_) {
        Iterator var2 = p_253940_.iterator();

        while(var2.hasNext()) {
            SynchedEntityData.DataValue<?> $$2 = (SynchedEntityData.DataValue)var2.next();
            $$2.write(p_253901_);
        }

        p_253901_.writeByte(255);
    }

    private static List<SynchedEntityData.DataValue<?>> unpack(FriendlyByteBuf p_253726_) {
        List<SynchedEntityData.DataValue<?>> $$1 = new ArrayList();

        short $$2;
        while(($$2 = p_253726_.readUnsignedByte()) != 255) {
            $$1.add(DataValue.read(p_253726_, $$2));
        }

        return $$1;
    }

    public void write(FriendlyByteBuf p_133158_) {
        p_133158_.writeVarInt(this.id);
        pack(this.packedItems, p_133158_);
    }

    public void handle(ClientGamePacketListener p_133155_) {
        p_133155_.handleSetEntityData(this);
    }

    public int id() {
        return this.id;
    }

    public List<SynchedEntityData.DataValue<?>> packedItems() {
        return this.packedItems;
    }
}
