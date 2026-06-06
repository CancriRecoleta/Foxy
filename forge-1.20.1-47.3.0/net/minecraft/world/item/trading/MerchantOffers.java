//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.trading;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class MerchantOffers extends ArrayList<MerchantOffer> {
    public MerchantOffers() {
    }

    private MerchantOffers(int p_220323_) {
        super(p_220323_);
    }

    public MerchantOffers(CompoundTag p_45387_) {
        ListTag $$1 = p_45387_.getList("Recipes", 10);

        for(int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            this.add(new MerchantOffer($$1.getCompound($$2)));
        }

    }

    @Nullable
    public MerchantOffer getRecipeFor(ItemStack p_45390_, ItemStack p_45391_, int p_45392_) {
        if (p_45392_ > 0 && p_45392_ < this.size()) {
            MerchantOffer $$3 = (MerchantOffer)this.get(p_45392_);
            return $$3.satisfiedBy(p_45390_, p_45391_) ? $$3 : null;
        } else {
            for(int $$4 = 0; $$4 < this.size(); ++$$4) {
                MerchantOffer $$5 = (MerchantOffer)this.get($$4);
                if ($$5.satisfiedBy(p_45390_, p_45391_)) {
                    return $$5;
                }
            }

            return null;
        }
    }

    public void writeToStream(FriendlyByteBuf p_45394_) {
        p_45394_.writeCollection(this, (p_220325_, p_220326_) -> {
            p_220325_.writeItem(p_220326_.getBaseCostA());
            p_220325_.writeItem(p_220326_.getResult());
            p_220325_.writeItem(p_220326_.getCostB());
            p_220325_.writeBoolean(p_220326_.isOutOfStock());
            p_220325_.writeInt(p_220326_.getUses());
            p_220325_.writeInt(p_220326_.getMaxUses());
            p_220325_.writeInt(p_220326_.getXp());
            p_220325_.writeInt(p_220326_.getSpecialPriceDiff());
            p_220325_.writeFloat(p_220326_.getPriceMultiplier());
            p_220325_.writeInt(p_220326_.getDemand());
        });
    }

    public static MerchantOffers createFromStream(FriendlyByteBuf p_45396_) {
        return (MerchantOffers)p_45396_.readCollection(MerchantOffers::new, (p_220328_) -> {
            ItemStack $$1 = p_220328_.readItem();
            ItemStack $$2 = p_220328_.readItem();
            ItemStack $$3 = p_220328_.readItem();
            boolean $$4 = p_220328_.readBoolean();
            int $$5 = p_220328_.readInt();
            int $$6 = p_220328_.readInt();
            int $$7 = p_220328_.readInt();
            int $$8 = p_220328_.readInt();
            float $$9 = p_220328_.readFloat();
            int $$10 = p_220328_.readInt();
            MerchantOffer $$11 = new MerchantOffer($$1, $$3, $$2, $$5, $$6, $$7, $$9, $$10);
            if ($$4) {
                $$11.setToOutOfStock();
            }

            $$11.setSpecialPriceDiff($$8);
            return $$11;
        });
    }

    public CompoundTag createTag() {
        CompoundTag $$0 = new CompoundTag();
        ListTag $$1 = new ListTag();

        for(int $$2 = 0; $$2 < this.size(); ++$$2) {
            MerchantOffer $$3 = (MerchantOffer)this.get($$2);
            $$1.add($$3.createTag());
        }

        $$0.put("Recipes", $$1);
        return $$0;
    }
}
