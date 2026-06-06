//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface Container extends Clearable {
    int LARGE_MAX_STACK_SIZE = 64;
    int DEFAULT_DISTANCE_LIMIT = 8;

    int getContainerSize();

    boolean isEmpty();

    ItemStack getItem(int var1);

    ItemStack removeItem(int var1, int var2);

    ItemStack removeItemNoUpdate(int var1);

    void setItem(int var1, ItemStack var2);

    default int getMaxStackSize() {
        return 64;
    }

    void setChanged();

    boolean stillValid(Player var1);

    default void startOpen(Player p_18955_) {
    }

    default void stopOpen(Player p_18954_) {
    }

    default boolean canPlaceItem(int p_18952_, ItemStack p_18953_) {
        return true;
    }

    default boolean canTakeItem(Container p_273520_, int p_272681_, ItemStack p_273702_) {
        return true;
    }

    default int countItem(Item p_18948_) {
        int $$1 = 0;

        for(int $$2 = 0; $$2 < this.getContainerSize(); ++$$2) {
            ItemStack $$3 = this.getItem($$2);
            if ($$3.getItem().equals(p_18948_)) {
                $$1 += $$3.getCount();
            }
        }

        return $$1;
    }

    default boolean hasAnyOf(Set<Item> p_18950_) {
        return this.hasAnyMatching((p_216873_) -> {
            return !p_216873_.isEmpty() && p_18950_.contains(p_216873_.getItem());
        });
    }

    default boolean hasAnyMatching(Predicate<ItemStack> p_216875_) {
        for(int $$1 = 0; $$1 < this.getContainerSize(); ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if (p_216875_.test($$2)) {
                return true;
            }
        }

        return false;
    }

    static boolean stillValidBlockEntity(BlockEntity p_273154_, Player p_273222_) {
        return stillValidBlockEntity(p_273154_, p_273222_, 8);
    }

    static boolean stillValidBlockEntity(BlockEntity p_272877_, Player p_272670_, int p_273411_) {
        Level $$3 = p_272877_.getLevel();
        BlockPos $$4 = p_272877_.getBlockPos();
        if ($$3 == null) {
            return false;
        } else if ($$3.getBlockEntity($$4) != p_272877_) {
            return false;
        } else {
            return p_272670_.distanceToSqr((double)$$4.getX() + 0.5, (double)$$4.getY() + 0.5, (double)$$4.getZ() + 0.5) <= (double)(p_273411_ * p_273411_);
        }
    }
}
