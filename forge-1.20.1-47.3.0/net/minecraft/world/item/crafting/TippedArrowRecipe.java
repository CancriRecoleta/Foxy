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
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class TippedArrowRecipe extends CustomRecipe {
    public TippedArrowRecipe(ResourceLocation p_250995_, CraftingBookCategory p_252163_) {
        super(p_250995_, p_252163_);
    }

    public boolean matches(CraftingContainer p_44515_, Level p_44516_) {
        if (p_44515_.getWidth() == 3 && p_44515_.getHeight() == 3) {
            for(int $$2 = 0; $$2 < p_44515_.getWidth(); ++$$2) {
                for(int $$3 = 0; $$3 < p_44515_.getHeight(); ++$$3) {
                    ItemStack $$4 = p_44515_.getItem($$2 + $$3 * p_44515_.getWidth());
                    if ($$4.isEmpty()) {
                        return false;
                    }

                    if ($$2 == 1 && $$3 == 1) {
                        if (!$$4.is(Items.LINGERING_POTION)) {
                            return false;
                        }
                    } else if (!$$4.is(Items.ARROW)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public ItemStack assemble(CraftingContainer p_44513_, RegistryAccess p_267186_) {
        ItemStack $$2 = p_44513_.getItem(1 + p_44513_.getWidth());
        if (!$$2.is(Items.LINGERING_POTION)) {
            return ItemStack.EMPTY;
        } else {
            ItemStack $$3 = new ItemStack(Items.TIPPED_ARROW, 8);
            PotionUtils.setPotion($$3, PotionUtils.getPotion($$2));
            PotionUtils.setCustomEffects($$3, PotionUtils.getCustomEffects($$2));
            return $$3;
        }
    }

    public boolean canCraftInDimensions(int p_44505_, int p_44506_) {
        return p_44505_ >= 2 && p_44506_ >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.TIPPED_ARROW;
    }
}
