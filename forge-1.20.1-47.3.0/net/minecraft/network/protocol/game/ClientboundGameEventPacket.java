//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundGameEventPacket implements Packet<ClientGamePacketListener> {
    public static final Type NO_RESPAWN_BLOCK_AVAILABLE = new Type(0);
    public static final Type START_RAINING = new Type(1);
    public static final Type STOP_RAINING = new Type(2);
    public static final Type CHANGE_GAME_MODE = new Type(3);
    public static final Type WIN_GAME = new Type(4);
    public static final Type DEMO_EVENT = new Type(5);
    public static final Type ARROW_HIT_PLAYER = new Type(6);
    public static final Type RAIN_LEVEL_CHANGE = new Type(7);
    public static final Type THUNDER_LEVEL_CHANGE = new Type(8);
    public static final Type PUFFER_FISH_STING = new Type(9);
    public static final Type GUARDIAN_ELDER_EFFECT = new Type(10);
    public static final Type IMMEDIATE_RESPAWN = new Type(11);
    public static final int DEMO_PARAM_INTRO = 0;
    public static final int DEMO_PARAM_HINT_1 = 101;
    public static final int DEMO_PARAM_HINT_2 = 102;
    public static final int DEMO_PARAM_HINT_3 = 103;
    public static final int DEMO_PARAM_HINT_4 = 104;
    private final Type event;
    private final float param;

    public ClientboundGameEventPacket(Type p_132170_, float p_132171_) {
        this.event = p_132170_;
        this.param = p_132171_;
    }

    public ClientboundGameEventPacket(FriendlyByteBuf p_178865_) {
        this.event = (Type)net.minecraft.network.protocol.game.ClientboundGameEventPacket.Type.TYPES.get(p_178865_.readUnsignedByte());
        this.param = p_178865_.readFloat();
    }

    public void write(FriendlyByteBuf p_132180_) {
        p_132180_.writeByte(this.event.id);
        p_132180_.writeFloat(this.param);
    }

    public void handle(ClientGamePacketListener p_132177_) {
        p_132177_.handleGameEvent(this);
    }

    public Type getEvent() {
        return this.event;
    }

    public float getParam() {
        return this.param;
    }

    public static class Type {
        static final Int2ObjectMap<Type> TYPES = new Int2ObjectOpenHashMap();
        final int id;

        public Type(int p_132186_) {
            this.id = p_132186_;
            TYPES.put(p_132186_, this);
        }
    }
}
