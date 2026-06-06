//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public abstract class SingleItemRecipe implements Recipe<Container> {
    protected final Ingredient ingredient;
    protected final ItemStack result;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    protected final ResourceLocation id;
    protected final String group;

    public SingleItemRecipe(RecipeType<?> p_44416_, RecipeSerializer<?> p_44417_, ResourceLocation p_44418_, String p_44419_, Ingredient p_44420_, ItemStack p_44421_) {
        this.type = p_44416_;
        this.serializer = p_44417_;
        this.id = p_44418_;
        this.group = p_44419_;
        this.ingredient = p_44420_;
        this.result = p_44421_;
    }

    public RecipeType<?> getType() {
        return this.type;
    }

    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public String getGroup() {
        return this.group;
    }

    public ItemStack getResultItem(RegistryAccess p_266964_) {
        return this.result;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> $$0 = NonNullList.create();
        $$0.add(this.ingredient);
        return $$0;
    }

    public boolean canCraftInDimensions(int p_44424_, int p_44425_) {
        return true;
    }

    public ItemStack assemble(Container p_44427_, RegistryAccess p_266999_) {
        return this.result.copy();
    }

    public static class Serializer<T extends SingleItemRecipe> implements RecipeSerializer<T> {
        final SingleItemMaker<T> factory;

        protected Serializer(SingleItemMaker<T> p_44435_) {
            this.factory = p_44435_;
        }

        public T fromJson(ResourceLocation p_44449_, JsonObject p_44450_) {
            String $$2 = GsonHelper.getAsString(p_44450_, "group", "");
            Ingredient $$4;
            if (GsonHelper.isArrayNode(p_44450_, "ingredient")) {
                $$4 = Ingredient.fromJson(GsonHelper.getAsJsonArray(p_44450_, "ingredient"), false);
            } else {
                $$4 = Ingredient.fromJson(GsonHelper.getAsJsonObject(p_44450_, "ingredient"), false);
            }

            String $$5 = GsonHelper.getAsString(p_44450_, "result");
            int $$6 = GsonHelper.getAsInt(p_44450_, "count");
            ItemStack $$7 = new ItemStack((ItemLike)BuiltInRegistries.ITEM.get(new ResourceLocation($$5)), $$6);
            return this.factory.create(p_44449_, $$2, $$4, $$7);
        }

        public T fromNetwork(ResourceLocation p_44452_, FriendlyByteBuf p_44453_) {
            String $$2 = p_44453_.readUtf();
            Ingredient $$3 = Ingredient.fromNetwork(p_44453_);
            ItemStack $$4 = p_44453_.readItem();
            return this.factory.create(p_44452_, $$2, $$3, $$4);
        }

        public void toNetwork(FriendlyByteBuf p_44440_, T p_44441_) {
            p_44440_.writeUtf(p_44441_.group);
            p_44441_.ingredient.toNetwork(p_44440_);
            p_44440_.writeItem(p_44441_.result);
        }

        interface SingleItemMaker<T extends SingleItemRecipe> {
            T create(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4);
        }
    }
}
