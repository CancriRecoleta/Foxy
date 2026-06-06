//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.recipes;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface RecipeBuilder {
    ResourceLocation ROOT_RECIPE_ADVANCEMENT = new ResourceLocation("recipes/root");

    RecipeBuilder unlockedBy(String var1, CriterionTriggerInstance var2);

    RecipeBuilder group(@Nullable String var1);

    Item getResult();

    void save(Consumer<FinishedRecipe> var1, ResourceLocation var2);

    default void save(Consumer<FinishedRecipe> p_176499_) {
        this.save(p_176499_, getDefaultRecipeId(this.getResult()));
    }

    default void save(Consumer<FinishedRecipe> p_176501_, String p_176502_) {
        ResourceLocation $$2 = getDefaultRecipeId(this.getResult());
        ResourceLocation $$3 = new ResourceLocation(p_176502_);
        if ($$3.equals($$2)) {
            throw new IllegalStateException("Recipe " + p_176502_ + " should remove its 'save' argument as it is equal to default one");
        } else {
            this.save(p_176501_, $$3);
        }
    }

    static ResourceLocation getDefaultRecipeId(ItemLike p_176494_) {
        return BuiltInRegistries.ITEM.getKey(p_176494_.asItem());
    }
}
