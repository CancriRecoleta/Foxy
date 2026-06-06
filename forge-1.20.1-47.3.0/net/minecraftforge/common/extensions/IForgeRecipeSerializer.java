//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.extensions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.conditions.ICondition;

public interface IForgeRecipeSerializer<T extends Recipe<?>> {
    private RecipeSerializer<T> self() {
        return (RecipeSerializer)this;
    }

    default T fromJson(ResourceLocation recipeLoc, JsonObject recipeJson, ICondition.IContext context) {
        return this.self().fromJson(recipeLoc, recipeJson);
    }
}
