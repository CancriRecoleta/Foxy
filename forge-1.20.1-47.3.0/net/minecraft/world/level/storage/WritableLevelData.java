//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage;

import net.minecraft.core.BlockPos;

public interface WritableLevelData extends LevelData {
    void setXSpawn(int var1);

    void setYSpawn(int var1);

    void setZSpawn(int var1);

    void setSpawnAngle(float var1);

    default void setSpawn(BlockPos p_78649_, float p_78650_) {
        this.setXSpawn(p_78649_.getX());
        this.setYSpawn(p_78649_.getY());
        this.setZSpawn(p_78649_.getZ());
        this.setSpawnAngle(p_78650_);
    }
}
