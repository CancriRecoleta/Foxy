//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapExtendingRecipe extends ShapedRecipe {
    public MapExtendingRecipe(ResourceLocation p_250265_, CraftingBookCategory p_250154_) {
        super(p_250265_, "", p_250154_, 3, 3, NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.FILLED_MAP), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER)), new ItemStack(Items.MAP));
    }

    public boolean matches(CraftingContainer p_43993_, Level p_43994_) {
        if (!super.matches(p_43993_, p_43994_)) {
            return false;
        } else {
            ItemStack $$2 = findFilledMap(p_43993_);
            if ($$2.isEmpty()) {
                return false;
            } else {
                MapItemSavedData $$3 = MapItem.getSavedData($$2, p_43994_);
                if ($$3 == null) {
                    return false;
                } else if ($$3.isExplorationMap()) {
                    return false;
                } else {
                    return $$3.scale < 4;
                }
            }
        }
    }

    public ItemStack assemble(CraftingContainer p_43991_, RegistryAccess p_266892_) {
        ItemStack $$2 = findFilledMap(p_43991_).copyWithCount(1);
        $$2.getOrCreateTag().putInt("map_scale_direction", 1);
        return $$2;
    }

    private static ItemStack findFilledMap(CraftingContainer p_279436_) {
        for(int $$1 = 0; $$1 < p_279436_.getContainerSize(); ++$$1) {
            ItemStack $$2 = p_279436_.getItem($$1);
            if ($$2.is(Items.FILLED_MAP)) {
                return $$2;
            }
        }

        return ItemStack.EMPTY;
    }

    public boolean isSpecial() {
        return true;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_EXTENDING;
    }
}
