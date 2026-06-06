//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries.holdersets;

import net.minecraft.core.HolderSet;
import net.minecraftforge.common.extensions.IForgeHolderSet;

public interface ICustomHolderSet<T> extends HolderSet<T> {
    HolderSetType type();

    default IForgeHolderSet.SerializationType serializationType() {
        return net.minecraftforge.common.extensions.IForgeHolderSet.SerializationType.OBJECT;
    }
}
