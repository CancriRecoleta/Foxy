//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.saveddata.maps;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

public class MapFrame {
    private final BlockPos pos;
    private final int rotation;
    private final int entityId;

    public MapFrame(BlockPos p_77866_, int p_77867_, int p_77868_) {
        this.pos = p_77866_;
        this.rotation = p_77867_;
        this.entityId = p_77868_;
    }

    public static MapFrame load(CompoundTag p_77873_) {
        BlockPos $$1 = NbtUtils.readBlockPos(p_77873_.getCompound("Pos"));
        int $$2 = p_77873_.getInt("Rotation");
        int $$3 = p_77873_.getInt("EntityId");
        return new MapFrame($$1, $$2, $$3);
    }

    public CompoundTag save() {
        CompoundTag $$0 = new CompoundTag();
        $$0.put("Pos", NbtUtils.writeBlockPos(this.pos));
        $$0.putInt("Rotation", this.rotation);
        $$0.putInt("EntityId", this.entityId);
        return $$0;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getRotation() {
        return this.rotation;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public String getId() {
        return frameId(this.pos);
    }

    public static String frameId(BlockPos p_77871_) {
        int var10000 = p_77871_.getX();
        return "frame-" + var10000 + "," + p_77871_.getY() + "," + p_77871_.getZ();
    }
}
