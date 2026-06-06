//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

public class SuspiciousStewRecipe extends CustomRecipe {
    public SuspiciousStewRecipe(ResourceLocation p_248870_, CraftingBookCategory p_250392_) {
        super(p_248870_, p_250392_);
    }

    public boolean matches(CraftingContainer p_44499_, Level p_44500_) {
        boolean $$2 = false;
        boolean $$3 = false;
        boolean $$4 = false;
        boolean $$5 = false;

        for(int $$6 = 0; $$6 < p_44499_.getContainerSize(); ++$$6) {
            ItemStack $$7 = p_44499_.getItem($$6);
            if (!$$7.isEmpty()) {
                if ($$7.is(Blocks.BROWN_MUSHROOM.asItem()) && !$$4) {
                    $$4 = true;
                } else if ($$7.is(Blocks.RED_MUSHROOM.asItem()) && !$$3) {
                    $$3 = true;
                } else if ($$7.is(ItemTags.SMALL_FLOWERS) && !$$2) {
                    $$2 = true;
                } else {
                    if (!$$7.is(Items.BOWL) || $$5) {
                        return false;
                    }

                    $$5 = true;
                }
            }
        }

        return $$2 && $$4 && $$3 && $$5;
    }

    public ItemStack assemble(CraftingContainer p_44497_, RegistryAccess p_266871_) {
        ItemStack $$2 = new ItemStack(Items.SUSPICIOUS_STEW, 1);

        for(int $$3 = 0; $$3 < p_44497_.getContainerSize(); ++$$3) {
            ItemStack $$4 = p_44497_.getItem($$3);
            if (!$$4.isEmpty()) {
                SuspiciousEffectHolder $$5 = SuspiciousEffectHolder.tryGet($$4.getItem());
                if ($$5 != null) {
                    SuspiciousStewItem.saveMobEffect($$2, $$5.getSuspiciousEffect(), $$5.getEffectDuration());
                    break;
                }
            }
        }

        return $$2;
    }

    public boolean canCraftInDimensions(int p_44489_, int p_44490_) {
        return p_44489_ >= 2 && p_44490_ >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SUSPICIOUS_STEW;
    }
}
