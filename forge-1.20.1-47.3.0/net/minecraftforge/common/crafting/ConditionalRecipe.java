//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.Nullable;

public class ConditionalRecipe {
    @ObjectHolder(
        registryName = "recipe_serializer",
        value = "forge:conditional"
    )
    public static final RecipeSerializer<Recipe<?>> SERIALZIER = null;

    public ConditionalRecipe() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<ICondition[]> conditions = new ArrayList();
        private List<FinishedRecipe> recipes = new ArrayList();
        private ResourceLocation advId;
        private ConditionalAdvancement.Builder adv;
        private List<ICondition> currentConditions = new ArrayList();

        public Builder() {
        }

        public Builder addCondition(ICondition condition) {
            this.currentConditions.add(condition);
            return this;
        }

        public Builder addRecipe(Consumer<Consumer<FinishedRecipe>> callable) {
            callable.accept(this::addRecipe);
            return this;
        }

        public Builder addRecipe(FinishedRecipe recipe) {
            if (this.currentConditions.isEmpty()) {
                throw new IllegalStateException("Can not add a recipe with no conditions.");
            } else {
                this.conditions.add((ICondition[])this.currentConditions.toArray(new ICondition[this.currentConditions.size()]));
                this.recipes.add(recipe);
                this.currentConditions.clear();
                return this;
            }
        }

        public Builder generateAdvancement() {
            return this.generateAdvancement((ResourceLocation)null);
        }

        public Builder generateAdvancement(@Nullable ResourceLocation id) {
            ConditionalAdvancement.Builder builder = ConditionalAdvancement.builder();

            for(int i = 0; i < this.recipes.size(); ++i) {
                ICondition[] var4 = (ICondition[])this.conditions.get(i);
                int var5 = var4.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    ICondition cond = var4[var6];
                    builder = builder.addCondition(cond);
                }

                builder = builder.addAdvancement((FinishedRecipe)this.recipes.get(i));
            }

            return this.setAdvancement(id, builder);
        }

        public Builder setAdvancement(ConditionalAdvancement.Builder advancement) {
            return this.setAdvancement((ResourceLocation)null, advancement);
        }

        public Builder setAdvancement(String namespace, String path, ConditionalAdvancement.Builder advancement) {
            return this.setAdvancement(new ResourceLocation(namespace, path), advancement);
        }

        public Builder setAdvancement(@Nullable ResourceLocation id, ConditionalAdvancement.Builder advancement) {
            if (this.adv != null) {
                throw new IllegalStateException("Invalid ConditionalRecipeBuilder, Advancement already set");
            } else {
                this.advId = id;
                this.adv = advancement;
                return this;
            }
        }

        public void build(Consumer<FinishedRecipe> consumer, String namespace, String path) {
            this.build(consumer, new ResourceLocation(namespace, path));
        }

        public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
            if (!this.currentConditions.isEmpty()) {
                throw new IllegalStateException("Invalid ConditionalRecipe builder, Orphaned conditions");
            } else if (this.recipes.isEmpty()) {
                throw new IllegalStateException("Invalid ConditionalRecipe builder, No recipes");
            } else {
                if (this.advId == null && this.adv != null) {
                    this.advId = new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath());
                }

                consumer.accept(new Finished(id, this.conditions, this.recipes, this.advId, this.adv));
            }
        }
    }

    private static class Finished implements FinishedRecipe {
        private final ResourceLocation id;
        private final List<ICondition[]> conditions;
        private final List<FinishedRecipe> recipes;
        private final ResourceLocation advId;
        private final ConditionalAdvancement.Builder adv;

        private Finished(ResourceLocation id, List<ICondition[]> conditions, List<FinishedRecipe> recipes, @Nullable ResourceLocation advId, @Nullable ConditionalAdvancement.@Nullable Builder adv) {
            this.id = id;
            this.conditions = conditions;
            this.recipes = recipes;
            this.advId = advId;
            this.adv = adv;
        }

        public void serializeRecipeData(JsonObject json) {
            JsonArray array = new JsonArray();
            json.add("recipes", array);

            for(int x = 0; x < this.conditions.size(); ++x) {
                JsonObject holder = new JsonObject();
                JsonArray conds = new JsonArray();
                ICondition[] var6 = (ICondition[])this.conditions.get(x);
                int var7 = var6.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    ICondition c = var6[var8];
                    conds.add(CraftingHelper.serialize(c));
                }

                holder.add("conditions", conds);
                holder.add("recipe", ((FinishedRecipe)this.recipes.get(x)).serializeRecipe());
                array.add(holder);
            }

        }

        public ResourceLocation getId() {
            return this.id;
        }

        public RecipeSerializer<?> getType() {
            return ConditionalRecipe.SERIALZIER;
        }

        public JsonObject serializeAdvancement() {
            return this.adv == null ? null : this.adv.write();
        }

        public ResourceLocation getAdvancementId() {
            return this.advId;
        }
    }

    public static class Serializer<T extends Recipe<?>> implements RecipeSerializer<T> {
        public Serializer() {
        }

        public T fromJson(ResourceLocation recipeId, JsonObject json) {
            return this.fromJson(recipeId, json, IContext.EMPTY);
        }

        public T fromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context) {
            JsonArray items = GsonHelper.getAsJsonArray(json, "recipes");
            int idx = 0;

            for(Iterator var6 = items.iterator(); var6.hasNext(); ++idx) {
                JsonElement ele = (JsonElement)var6.next();
                if (!ele.isJsonObject()) {
                    throw new JsonSyntaxException("Invalid recipes entry at index " + idx + " Must be JsonObject");
                }

                if (CraftingHelper.processConditions(GsonHelper.getAsJsonArray(ele.getAsJsonObject(), "conditions"), context)) {
                    return RecipeManager.fromJson(recipeId, GsonHelper.getAsJsonObject(ele.getAsJsonObject(), "recipe"));
                }
            }

            return null;
        }

        public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return null;
        }

        public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        }
    }
}
