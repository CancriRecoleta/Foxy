//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;

public class ItemStackLinkedSet {
    public static final Hash.Strategy<? super ItemStack> TYPE_AND_TAG = new Hash.Strategy<ItemStack>() {
        public int hashCode(@Nullable ItemStack p_251266_) {
            return ItemStackLinkedSet.hashStackAndTag(p_251266_);
        }

        public boolean equals(@Nullable ItemStack p_250623_, @Nullable ItemStack p_251135_) {
            return p_250623_ == p_251135_ || p_250623_ != null && p_251135_ != null && p_250623_.isEmpty() == p_251135_.isEmpty() && ItemStack.isSameItemSameTags(p_250623_, p_251135_);
        }
    };

    public ItemStackLinkedSet() {
    }

    static int hashStackAndTag(@Nullable ItemStack p_262160_) {
        if (p_262160_ != null) {
            CompoundTag $$1 = p_262160_.getTag();
            int $$2 = 31 + p_262160_.getItem().hashCode();
            return 31 * $$2 + ($$1 == null ? 0 : $$1.hashCode());
        } else {
            return 0;
        }
    }

    public static Set<ItemStack> createTypeAndTagSet() {
        return new ObjectLinkedOpenCustomHashSet(TYPE_AND_TAG);
    }
}
