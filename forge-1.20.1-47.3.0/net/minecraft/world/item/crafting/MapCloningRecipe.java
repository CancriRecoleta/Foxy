//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MapCloningRecipe extends CustomRecipe {
    public MapCloningRecipe(ResourceLocation p_250551_, CraftingBookCategory p_251985_) {
        super(p_250551_, p_251985_);
    }

    public boolean matches(CraftingContainer p_43980_, Level p_43981_) {
        int $$2 = 0;
        ItemStack $$3 = ItemStack.EMPTY;

        for(int $$4 = 0; $$4 < p_43980_.getContainerSize(); ++$$4) {
            ItemStack $$5 = p_43980_.getItem($$4);
            if (!$$5.isEmpty()) {
                if ($$5.is(Items.FILLED_MAP)) {
                    if (!$$3.isEmpty()) {
                        return false;
                    }

                    $$3 = $$5;
                } else {
                    if (!$$5.is(Items.MAP)) {
                        return false;
                    }

                    ++$$2;
                }
            }
        }

        return !$$3.isEmpty() && $$2 > 0;
    }

    public ItemStack assemble(CraftingContainer p_43978_, RegistryAccess p_267299_) {
        int $$2 = 0;
        ItemStack $$3 = ItemStack.EMPTY;

        for(int $$4 = 0; $$4 < p_43978_.getContainerSize(); ++$$4) {
            ItemStack $$5 = p_43978_.getItem($$4);
            if (!$$5.isEmpty()) {
                if ($$5.is(Items.FILLED_MAP)) {
                    if (!$$3.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    $$3 = $$5;
                } else {
                    if (!$$5.is(Items.MAP)) {
                        return ItemStack.EMPTY;
                    }

                    ++$$2;
                }
            }
        }

        if (!$$3.isEmpty() && $$2 >= 1) {
            return $$3.copyWithCount($$2 + 1);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public boolean canCraftInDimensions(int p_43970_, int p_43971_) {
        return p_43970_ >= 3 && p_43971_ >= 3;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_CLONING;
    }
}
