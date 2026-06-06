//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

public class EnchantedBookItem extends Item {
    public static final String TAG_STORED_ENCHANTMENTS = "StoredEnchantments";

    public EnchantedBookItem(Item.Properties p_41149_) {
        super(p_41149_);
    }

    public boolean isFoil(ItemStack p_41166_) {
        return true;
    }

    public boolean isEnchantable(ItemStack p_41168_) {
        return false;
    }

    public static ListTag getEnchantments(ItemStack p_41164_) {
        CompoundTag $$1 = p_41164_.getTag();
        return $$1 != null ? $$1.getList("StoredEnchantments", 10) : new ListTag();
    }

    public void appendHoverText(ItemStack p_41157_, @Nullable Level p_41158_, List<Component> p_41159_, TooltipFlag p_41160_) {
        super.appendHoverText(p_41157_, p_41158_, p_41159_, p_41160_);
        ItemStack.appendEnchantmentNames(p_41159_, getEnchantments(p_41157_));
    }

    public static void addEnchantment(ItemStack p_41154_, EnchantmentInstance p_41155_) {
        ListTag $$2 = getEnchantments(p_41154_);
        boolean $$3 = true;
        ResourceLocation $$4 = EnchantmentHelper.getEnchantmentId(p_41155_.enchantment);

        for(int $$5 = 0; $$5 < $$2.size(); ++$$5) {
            CompoundTag $$6 = $$2.getCompound($$5);
            ResourceLocation $$7 = EnchantmentHelper.getEnchantmentId($$6);
            if ($$7 != null && $$7.equals($$4)) {
                if (EnchantmentHelper.getEnchantmentLevel($$6) < p_41155_.level) {
                    EnchantmentHelper.setEnchantmentLevel($$6, p_41155_.level);
                }

                $$3 = false;
                break;
            }
        }

        if ($$3) {
            $$2.add(EnchantmentHelper.storeEnchantment($$4, p_41155_.level));
        }

        p_41154_.getOrCreateTag().put("StoredEnchantments", $$2);
    }

    public static ItemStack createForEnchantment(EnchantmentInstance p_41162_) {
        ItemStack $$1 = new ItemStack(Items.ENCHANTED_BOOK);
        addEnchantment($$1, p_41162_);
        return $$1;
    }
}
