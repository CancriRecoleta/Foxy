//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.extensions;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

public interface IForgeHolderSet<T> {
    default void addInvalidationListener(Runnable runnable) {
    }

    default SerializationType serializationType() {
        SerializationType var10000;
        if (this instanceof HolderSet.ListBacked<T> listBacked) {
            var10000 = (SerializationType)listBacked.unwrap().map((tag) -> {
                return net.minecraftforge.common.extensions.IForgeHolderSet.SerializationType.STRING;
            }, (list) -> {
                return list.size() == 1 ? (SerializationType)((Holder)list.get(0)).unwrap().map((key) -> {
                    return key == null ? net.minecraftforge.common.extensions.IForgeHolderSet.SerializationType.OBJECT : net.minecraftforge.common.extensions.IForgeHolderSet.SerializationType.STRING;
                }, (value) -> {
                    return net.minecraftforge.common.extensions.IForgeHolderSet.SerializationType.OBJECT;
                }) : net.minecraftforge.common.extensions.IForgeHolderSet.SerializationType.LIST;
            });
        } else {
            var10000 = net.minecraftforge.common.extensions.IForgeHolderSet.SerializationType.UNKNOWN;
        }

        return var10000;
    }

    public static enum SerializationType {
        UNKNOWN,
        STRING,
        LIST,
        OBJECT;

        private SerializationType() {
        }
    }
}
