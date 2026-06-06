//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.CraftingBookCategory;

public abstract class CraftingRecipeBuilder {
    public CraftingRecipeBuilder() {
    }

    protected static CraftingBookCategory determineBookCategory(RecipeCategory p_250736_) {
        CraftingBookCategory var10000;
        switch (p_250736_) {
            case BUILDING_BLOCKS:
                var10000 = CraftingBookCategory.BUILDING;
                break;
            case TOOLS:
            case COMBAT:
                var10000 = CraftingBookCategory.EQUIPMENT;
                break;
            case REDSTONE:
                var10000 = CraftingBookCategory.REDSTONE;
                break;
            default:
                var10000 = CraftingBookCategory.MISC;
        }

        return var10000;
    }

    protected abstract static class CraftingResult implements FinishedRecipe {
        private final CraftingBookCategory category;

        protected CraftingResult(CraftingBookCategory p_250313_) {
            this.category = p_250313_;
        }

        public void serializeRecipeData(JsonObject p_250456_) {
            p_250456_.addProperty("category", this.category.getSerializedName());
        }
    }
}
