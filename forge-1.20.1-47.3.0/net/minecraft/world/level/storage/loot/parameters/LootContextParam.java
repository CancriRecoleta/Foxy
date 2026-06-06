//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.parameters;

import net.minecraft.resources.ResourceLocation;

public class LootContextParam<T> {
    private final ResourceLocation name;

    public LootContextParam(ResourceLocation p_81283_) {
        this.name = p_81283_;
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public String toString() {
        return "<parameter " + this.name + ">";
    }
}
