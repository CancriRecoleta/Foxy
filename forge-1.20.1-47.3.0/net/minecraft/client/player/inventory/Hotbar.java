//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.player.inventory;

import com.google.common.collect.ForwardingList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Hotbar extends ForwardingList<ItemStack> {
    private final NonNullList<ItemStack> items;

    public Hotbar() {
        this.items = NonNullList.withSize(Inventory.getSelectionSize(), ItemStack.EMPTY);
    }

    protected List<ItemStack> delegate() {
        return this.items;
    }

    public ListTag createTag() {
        ListTag $$0 = new ListTag();
        Iterator var2 = this.delegate().iterator();

        while(var2.hasNext()) {
            ItemStack $$1 = (ItemStack)var2.next();
            $$0.add($$1.save(new CompoundTag()));
        }

        return $$0;
    }

    public void fromTag(ListTag p_108784_) {
        List<ItemStack> $$1 = this.delegate();

        for(int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            $$1.set($$2, ItemStack.of(p_108784_.getCompound($$2)));
        }

    }

    public boolean isEmpty() {
        Iterator var1 = this.delegate().iterator();

        ItemStack $$0;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            $$0 = (ItemStack)var1.next();
        } while($$0.isEmpty());

        return false;
    }
}
