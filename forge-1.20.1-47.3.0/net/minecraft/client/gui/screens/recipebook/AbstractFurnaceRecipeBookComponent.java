//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.recipebook;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractFurnaceRecipeBookComponent extends RecipeBookComponent {
    @Nullable
    private Ingredient fuels;

    public AbstractFurnaceRecipeBookComponent() {
    }

    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(152, 182, 28, 18, RECIPE_BOOK_LOCATION);
    }

    public void slotClicked(@Nullable Slot p_100120_) {
        super.slotClicked(p_100120_);
        if (p_100120_ != null && p_100120_.index < this.menu.getSize()) {
            this.ghostRecipe.clear();
        }

    }

    public void setupGhostRecipe(Recipe<?> p_100122_, List<Slot> p_100123_) {
        ItemStack $$2 = p_100122_.getResultItem(this.minecraft.level.registryAccess());
        this.ghostRecipe.setRecipe(p_100122_);
        this.ghostRecipe.addIngredient(Ingredient.of($$2), ((Slot)p_100123_.get(2)).x, ((Slot)p_100123_.get(2)).y);
        NonNullList<Ingredient> $$3 = p_100122_.getIngredients();
        Slot $$4 = (Slot)p_100123_.get(1);
        if ($$4.getItem().isEmpty()) {
            if (this.fuels == null) {
                this.fuels = Ingredient.of(this.getFuelItems().stream().filter((p_280880_) -> {
                    return p_280880_.isEnabled(this.minecraft.level.enabledFeatures());
                }).map(ItemStack::new));
            }

            this.ghostRecipe.addIngredient(this.fuels, $$4.x, $$4.y);
        }

        Iterator<Ingredient> $$5 = $$3.iterator();

        for(int $$6 = 0; $$6 < 2; ++$$6) {
            if (!$$5.hasNext()) {
                return;
            }

            Ingredient $$7 = (Ingredient)$$5.next();
            if (!$$7.isEmpty()) {
                Slot $$8 = (Slot)p_100123_.get($$6);
                this.ghostRecipe.addIngredient($$7, $$8.x, $$8.y);
            }
        }

    }

    protected abstract Set<Item> getFuelItems();
}
