//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ClientboundSetEquipmentPacket implements Packet<ClientGamePacketListener> {
    private static final byte CONTINUE_MASK = -128;
    private final int entity;
    private final List<Pair<EquipmentSlot, ItemStack>> slots;

    public ClientboundSetEquipmentPacket(int p_133202_, List<Pair<EquipmentSlot, ItemStack>> p_133203_) {
        this.entity = p_133202_;
        this.slots = p_133203_;
    }

    public ClientboundSetEquipmentPacket(FriendlyByteBuf p_179297_) {
        this.entity = p_179297_.readVarInt();
        EquipmentSlot[] $$1 = EquipmentSlot.values();
        this.slots = Lists.newArrayList();

        byte $$2;
        do {
            $$2 = p_179297_.readByte();
            EquipmentSlot $$3 = $$1[$$2 & 127];
            ItemStack $$4 = p_179297_.readItem();
            this.slots.add(Pair.of($$3, $$4));
        } while(($$2 & -128) != 0);

    }

    public void write(FriendlyByteBuf p_133212_) {
        p_133212_.writeVarInt(this.entity);
        int $$1 = this.slots.size();

        for(int $$2 = 0; $$2 < $$1; ++$$2) {
            Pair<EquipmentSlot, ItemStack> $$3 = (Pair)this.slots.get($$2);
            EquipmentSlot $$4 = (EquipmentSlot)$$3.getFirst();
            boolean $$5 = $$2 != $$1 - 1;
            int $$6 = $$4.ordinal();
            p_133212_.writeByte($$5 ? $$6 | -128 : $$6);
            p_133212_.writeItem((ItemStack)$$3.getSecond());
        }

    }

    public void handle(ClientGamePacketListener p_133209_) {
        p_133209_.handleSetEquipment(this);
    }

    public int getEntity() {
        return this.entity;
    }

    public List<Pair<EquipmentSlot, ItemStack>> getSlots() {
        return this.slots;
    }
}
