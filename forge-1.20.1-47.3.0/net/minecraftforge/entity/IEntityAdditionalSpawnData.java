//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.entity;

import net.minecraft.network.FriendlyByteBuf;

public interface IEntityAdditionalSpawnData {
    void writeSpawnData(FriendlyByteBuf var1);

    void readSpawnData(FriendlyByteBuf var1);
}
