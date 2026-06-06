//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class SimpleCraftingRecipeSerializer<T extends CraftingRecipe> implements RecipeSerializer<T> {
    private final Factory<T> constructor;

    public SimpleCraftingRecipeSerializer(Factory<T> p_250090_) {
        this.constructor = p_250090_;
    }

    public T fromJson(ResourceLocation p_249786_, JsonObject p_252161_) {
        CraftingBookCategory $$2 = (CraftingBookCategory)CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(p_252161_, "category", (String)null), CraftingBookCategory.MISC);
        return this.constructor.create(p_249786_, $$2);
    }

    public T fromNetwork(ResourceLocation p_251508_, FriendlyByteBuf p_251882_) {
        CraftingBookCategory $$2 = (CraftingBookCategory)p_251882_.readEnum(CraftingBookCategory.class);
        return this.constructor.create(p_251508_, $$2);
    }

    public void toNetwork(FriendlyByteBuf p_248968_, T p_250179_) {
        p_248968_.writeEnum(p_250179_.category());
    }

    @FunctionalInterface
    public interface Factory<T extends CraftingRecipe> {
        T create(ResourceLocation var1, CraftingBookCategory var2);
    }
}
