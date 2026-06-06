//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot;

import net.minecraft.resources.ResourceLocation;

public record LootDataId<T>(LootDataType<T> type, ResourceLocation location) {
    public LootDataId(LootDataType<T> type, ResourceLocation location) {
        this.type = type;
        this.location = location;
    }

    public LootDataType<T> type() {
        return this.type;
    }

    public ResourceLocation location() {
        return this.location;
    }
}
