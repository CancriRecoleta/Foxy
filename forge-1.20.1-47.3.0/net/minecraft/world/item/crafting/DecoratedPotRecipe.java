//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;

public class DecoratedPotRecipe extends CustomRecipe {
    public DecoratedPotRecipe(ResourceLocation p_273671_, CraftingBookCategory p_273056_) {
        super(p_273671_, p_273056_);
    }

    public boolean matches(CraftingContainer p_272882_, Level p_272812_) {
        if (!this.canCraftInDimensions(p_272882_.getWidth(), p_272882_.getHeight())) {
            return false;
        } else {
            for(int $$2 = 0; $$2 < p_272882_.getContainerSize(); ++$$2) {
                ItemStack $$3 = p_272882_.getItem($$2);
                switch ($$2) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                        if (!$$3.is(ItemTags.DECORATED_POT_INGREDIENTS)) {
                            return false;
                        }
                        break;
                    case 2:
                    case 4:
                    case 6:
                    default:
                        if (!$$3.is(Items.AIR)) {
                            return false;
                        }
                }
            }

            return true;
        }
    }

    public ItemStack assemble(CraftingContainer p_272861_, RegistryAccess p_273288_) {
        DecoratedPotBlockEntity.Decorations $$2 = new DecoratedPotBlockEntity.Decorations(p_272861_.getItem(1).getItem(), p_272861_.getItem(3).getItem(), p_272861_.getItem(5).getItem(), p_272861_.getItem(7).getItem());
        return createDecoratedPotItem($$2);
    }

    public static ItemStack createDecoratedPotItem(DecoratedPotBlockEntity.Decorations p_285413_) {
        ItemStack $$1 = Items.DECORATED_POT.getDefaultInstance();
        CompoundTag $$2 = p_285413_.save(new CompoundTag());
        BlockItem.setBlockEntityData($$1, BlockEntityType.DECORATED_POT, $$2);
        return $$1;
    }

    public boolean canCraftInDimensions(int p_273734_, int p_273516_) {
        return p_273734_ == 3 && p_273516_ == 3;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.DECORATED_POT_RECIPE;
    }
}
