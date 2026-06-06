//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArmorDyeRecipe extends CustomRecipe {
    public ArmorDyeRecipe(ResourceLocation p_250281_, CraftingBookCategory p_251949_) {
        super(p_250281_, p_251949_);
    }

    public boolean matches(CraftingContainer p_43769_, Level p_43770_) {
        ItemStack $$2 = ItemStack.EMPTY;
        List<ItemStack> $$3 = Lists.newArrayList();

        for(int $$4 = 0; $$4 < p_43769_.getContainerSize(); ++$$4) {
            ItemStack $$5 = p_43769_.getItem($$4);
            if (!$$5.isEmpty()) {
                if ($$5.getItem() instanceof DyeableLeatherItem) {
                    if (!$$2.isEmpty()) {
                        return false;
                    }

                    $$2 = $$5;
                } else {
                    if (!($$5.getItem() instanceof DyeItem)) {
                        return false;
                    }

                    $$3.add($$5);
                }
            }
        }

        return !$$2.isEmpty() && !$$3.isEmpty();
    }

    public ItemStack assemble(CraftingContainer p_43767_, RegistryAccess p_267017_) {
        List<DyeItem> $$2 = Lists.newArrayList();
        ItemStack $$3 = ItemStack.EMPTY;

        for(int $$4 = 0; $$4 < p_43767_.getContainerSize(); ++$$4) {
            ItemStack $$5 = p_43767_.getItem($$4);
            if (!$$5.isEmpty()) {
                Item $$6 = $$5.getItem();
                if ($$6 instanceof DyeableLeatherItem) {
                    if (!$$3.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    $$3 = $$5.copy();
                } else {
                    if (!($$6 instanceof DyeItem)) {
                        return ItemStack.EMPTY;
                    }

                    $$2.add((DyeItem)$$6);
                }
            }
        }

        if (!$$3.isEmpty() && !$$2.isEmpty()) {
            return DyeableLeatherItem.dyeArmor($$3, $$2);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public boolean canCraftInDimensions(int p_43759_, int p_43760_) {
        return p_43759_ * p_43760_ >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.ARMOR_DYE;
    }
}
