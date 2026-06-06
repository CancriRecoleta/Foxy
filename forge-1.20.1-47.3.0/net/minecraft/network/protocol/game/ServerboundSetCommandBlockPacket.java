//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

public class ServerboundSetCommandBlockPacket implements Packet<ServerGamePacketListener> {
    private static final int FLAG_TRACK_OUTPUT = 1;
    private static final int FLAG_CONDITIONAL = 2;
    private static final int FLAG_AUTOMATIC = 4;
    private final BlockPos pos;
    private final String command;
    private final boolean trackOutput;
    private final boolean conditional;
    private final boolean automatic;
    private final CommandBlockEntity.Mode mode;

    public ServerboundSetCommandBlockPacket(BlockPos p_134509_, String p_134510_, CommandBlockEntity.Mode p_134511_, boolean p_134512_, boolean p_134513_, boolean p_134514_) {
        this.pos = p_134509_;
        this.command = p_134510_;
        this.trackOutput = p_134512_;
        this.conditional = p_134513_;
        this.automatic = p_134514_;
        this.mode = p_134511_;
    }

    public ServerboundSetCommandBlockPacket(FriendlyByteBuf p_179756_) {
        this.pos = p_179756_.readBlockPos();
        this.command = p_179756_.readUtf();
        this.mode = (CommandBlockEntity.Mode)p_179756_.readEnum(CommandBlockEntity.Mode.class);
        int $$1 = p_179756_.readByte();
        this.trackOutput = ($$1 & 1) != 0;
        this.conditional = ($$1 & 2) != 0;
        this.automatic = ($$1 & 4) != 0;
    }

    public void write(FriendlyByteBuf p_134523_) {
        p_134523_.writeBlockPos(this.pos);
        p_134523_.writeUtf(this.command);
        p_134523_.writeEnum(this.mode);
        int $$1 = 0;
        if (this.trackOutput) {
            $$1 |= 1;
        }

        if (this.conditional) {
            $$1 |= 2;
        }

        if (this.automatic) {
            $$1 |= 4;
        }

        p_134523_.writeByte($$1);
    }

    public void handle(ServerGamePacketListener p_134520_) {
        p_134520_.handleSetCommandBlock(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean isTrackOutput() {
        return this.trackOutput;
    }

    public boolean isConditional() {
        return this.conditional;
    }

    public boolean isAutomatic() {
        return this.automatic;
    }

    public CommandBlockEntity.Mode getMode() {
        return this.mode;
    }
}
