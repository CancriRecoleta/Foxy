//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.inventory;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class MerchantContainer implements Container {
    private final Merchant merchant;
    private final NonNullList<ItemStack> itemStacks;
    @Nullable
    private MerchantOffer activeOffer;
    private int selectionHint;
    private int futureXp;

    public MerchantContainer(Merchant p_40003_) {
        this.itemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
        this.merchant = p_40003_;
    }

    public int getContainerSize() {
        return this.itemStacks.size();
    }

    public boolean isEmpty() {
        Iterator var1 = this.itemStacks.iterator();

        ItemStack $$0;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            $$0 = (ItemStack)var1.next();
        } while($$0.isEmpty());

        return false;
    }

    public ItemStack getItem(int p_40008_) {
        return (ItemStack)this.itemStacks.get(p_40008_);
    }

    public ItemStack removeItem(int p_40010_, int p_40011_) {
        ItemStack $$2 = (ItemStack)this.itemStacks.get(p_40010_);
        if (p_40010_ == 2 && !$$2.isEmpty()) {
            return ContainerHelper.removeItem(this.itemStacks, p_40010_, $$2.getCount());
        } else {
            ItemStack $$3 = ContainerHelper.removeItem(this.itemStacks, p_40010_, p_40011_);
            if (!$$3.isEmpty() && this.isPaymentSlot(p_40010_)) {
                this.updateSellItem();
            }

            return $$3;
        }
    }

    private boolean isPaymentSlot(int p_40023_) {
        return p_40023_ == 0 || p_40023_ == 1;
    }

    public ItemStack removeItemNoUpdate(int p_40018_) {
        return ContainerHelper.takeItem(this.itemStacks, p_40018_);
    }

    public void setItem(int p_40013_, ItemStack p_40014_) {
        this.itemStacks.set(p_40013_, p_40014_);
        if (!p_40014_.isEmpty() && p_40014_.getCount() > this.getMaxStackSize()) {
            p_40014_.setCount(this.getMaxStackSize());
        }

        if (this.isPaymentSlot(p_40013_)) {
            this.updateSellItem();
        }

    }

    public boolean stillValid(Player p_40016_) {
        return this.merchant.getTradingPlayer() == p_40016_;
    }

    public void setChanged() {
        this.updateSellItem();
    }

    public void updateSellItem() {
        this.activeOffer = null;
        ItemStack $$2;
        ItemStack $$3;
        if (((ItemStack)this.itemStacks.get(0)).isEmpty()) {
            $$2 = (ItemStack)this.itemStacks.get(1);
            $$3 = ItemStack.EMPTY;
        } else {
            $$2 = (ItemStack)this.itemStacks.get(0);
            $$3 = (ItemStack)this.itemStacks.get(1);
        }

        if ($$2.isEmpty()) {
            this.setItem(2, ItemStack.EMPTY);
            this.futureXp = 0;
        } else {
            MerchantOffers $$4 = this.merchant.getOffers();
            if (!$$4.isEmpty()) {
                MerchantOffer $$5 = $$4.getRecipeFor($$2, $$3, this.selectionHint);
                if ($$5 == null || $$5.isOutOfStock()) {
                    this.activeOffer = $$5;
                    $$5 = $$4.getRecipeFor($$3, $$2, this.selectionHint);
                }

                if ($$5 != null && !$$5.isOutOfStock()) {
                    this.activeOffer = $$5;
                    this.setItem(2, $$5.assemble());
                    this.futureXp = $$5.getXp();
                } else {
                    this.setItem(2, ItemStack.EMPTY);
                    this.futureXp = 0;
                }
            }

            this.merchant.notifyTradeUpdated(this.getItem(2));
        }
    }

    @Nullable
    public MerchantOffer getActiveOffer() {
        return this.activeOffer;
    }

    public void setSelectionHint(int p_40021_) {
        this.selectionHint = p_40021_;
        this.updateSellItem();
    }

    public void clearContent() {
        this.itemStacks.clear();
    }

    public int getFutureXp() {
        return this.futureXp;
    }
}
