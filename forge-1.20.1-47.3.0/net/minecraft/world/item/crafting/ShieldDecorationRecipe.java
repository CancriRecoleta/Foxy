//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ShieldDecorationRecipe extends CustomRecipe {
    public ShieldDecorationRecipe(ResourceLocation p_251738_, CraftingBookCategory p_251065_) {
        super(p_251738_, p_251065_);
    }

    public boolean matches(CraftingContainer p_44308_, Level p_44309_) {
        ItemStack $$2 = ItemStack.EMPTY;
        ItemStack $$3 = ItemStack.EMPTY;

        for(int $$4 = 0; $$4 < p_44308_.getContainerSize(); ++$$4) {
            ItemStack $$5 = p_44308_.getItem($$4);
            if (!$$5.isEmpty()) {
                if ($$5.getItem() instanceof BannerItem) {
                    if (!$$3.isEmpty()) {
                        return false;
                    }

                    $$3 = $$5;
                } else {
                    if (!$$5.is(Items.SHIELD)) {
                        return false;
                    }

                    if (!$$2.isEmpty()) {
                        return false;
                    }

                    if (BlockItem.getBlockEntityData($$5) != null) {
                        return false;
                    }

                    $$2 = $$5;
                }
            }
        }

        if (!$$2.isEmpty() && !$$3.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public ItemStack assemble(CraftingContainer p_44306_, RegistryAccess p_267112_) {
        ItemStack $$2 = ItemStack.EMPTY;
        ItemStack $$3 = ItemStack.EMPTY;

        for(int $$4 = 0; $$4 < p_44306_.getContainerSize(); ++$$4) {
            ItemStack $$5 = p_44306_.getItem($$4);
            if (!$$5.isEmpty()) {
                if ($$5.getItem() instanceof BannerItem) {
                    $$2 = $$5;
                } else if ($$5.is(Items.SHIELD)) {
                    $$3 = $$5.copy();
                }
            }
        }

        if ($$3.isEmpty()) {
            return $$3;
        } else {
            CompoundTag $$6 = BlockItem.getBlockEntityData($$2);
            CompoundTag $$7 = $$6 == null ? new CompoundTag() : $$6.copy();
            $$7.putInt("Base", ((BannerItem)$$2.getItem()).getColor().getId());
            BlockItem.setBlockEntityData($$3, BlockEntityType.BANNER, $$7);
            return $$3;
        }
    }

    public boolean canCraftInDimensions(int p_44298_, int p_44299_) {
        return p_44298_ * p_44299_ >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHIELD_DECORATION;
    }
}
