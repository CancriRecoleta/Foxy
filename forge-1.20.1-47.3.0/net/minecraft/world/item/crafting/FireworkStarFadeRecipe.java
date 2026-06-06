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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FireworkStarFadeRecipe extends CustomRecipe {
    private static final Ingredient STAR_INGREDIENT;

    public FireworkStarFadeRecipe(ResourceLocation p_249812_, CraftingBookCategory p_251846_) {
        super(p_249812_, p_251846_);
    }

    public boolean matches(CraftingContainer p_43873_, Level p_43874_) {
        boolean $$2 = false;
        boolean $$3 = false;

        for(int $$4 = 0; $$4 < p_43873_.getContainerSize(); ++$$4) {
            ItemStack $$5 = p_43873_.getItem($$4);
            if (!$$5.isEmpty()) {
                if ($$5.getItem() instanceof DyeItem) {
                    $$2 = true;
                } else {
                    if (!STAR_INGREDIENT.test($$5)) {
                        return false;
                    }

                    if ($$3) {
                        return false;
                    }

                    $$3 = true;
                }
            }
        }

        return $$3 && $$2;
    }

    public ItemStack assemble(CraftingContainer p_43871_, RegistryAccess p_266682_) {
        List<Integer> $$2 = Lists.newArrayList();
        ItemStack $$3 = null;

        for(int $$4 = 0; $$4 < p_43871_.getContainerSize(); ++$$4) {
            ItemStack $$5 = p_43871_.getItem($$4);
            Item $$6 = $$5.getItem();
            if ($$6 instanceof DyeItem) {
                $$2.add(((DyeItem)$$6).getDyeColor().getFireworkColor());
            } else if (STAR_INGREDIENT.test($$5)) {
                $$3 = $$5.copyWithCount(1);
            }
        }

        if ($$3 != null && !$$2.isEmpty()) {
            $$3.getOrCreateTagElement("Explosion").putIntArray("FadeColors", (List)$$2);
            return $$3;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public boolean canCraftInDimensions(int p_43863_, int p_43864_) {
        return p_43863_ * p_43864_ >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR_FADE;
    }

    static {
        STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);
    }
}
