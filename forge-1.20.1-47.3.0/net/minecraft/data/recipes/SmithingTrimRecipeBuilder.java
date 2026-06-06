//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SmithingTrimRecipeBuilder {
    private final RecipeCategory category;
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final Advancement.Builder advancement = Builder.recipeAdvancement();
    private final RecipeSerializer<?> type;

    public SmithingTrimRecipeBuilder(RecipeSerializer<?> p_267085_, RecipeCategory p_267007_, Ingredient p_266712_, Ingredient p_267018_, Ingredient p_267264_) {
        this.category = p_267007_;
        this.type = p_267085_;
        this.template = p_266712_;
        this.base = p_267018_;
        this.addition = p_267264_;
    }

    public static SmithingTrimRecipeBuilder smithingTrim(Ingredient p_266812_, Ingredient p_266843_, Ingredient p_267309_, RecipeCategory p_267269_) {
        return new SmithingTrimRecipeBuilder(RecipeSerializer.SMITHING_TRIM, p_267269_, p_266812_, p_266843_, p_267309_);
    }

    public SmithingTrimRecipeBuilder unlocks(String p_266882_, CriterionTriggerInstance p_267233_) {
        this.advancement.addCriterion(p_266882_, p_267233_);
        return this;
    }

    public void save(Consumer<FinishedRecipe> p_267231_, ResourceLocation p_266718_) {
        this.ensureValid(p_266718_);
        this.advancement.parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", (CriterionTriggerInstance)RecipeUnlockedTrigger.unlocked(p_266718_)).rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(p_266718_)).requirements(RequirementsStrategy.OR);
        p_267231_.accept(new Result(p_266718_, this.type, this.template, this.base, this.addition, this.advancement, p_266718_.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation p_267040_) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + p_267040_);
        }
    }

    public static record Result(ResourceLocation id, RecipeSerializer<?> type, Ingredient template, Ingredient base, Ingredient addition, Advancement.Builder advancement, ResourceLocation advancementId) implements FinishedRecipe {
        public Result(ResourceLocation id, RecipeSerializer<?> type, Ingredient template, Ingredient base, Ingredient addition, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.type = type;
            this.template = template;
            this.base = base;
            this.addition = addition;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        public void serializeRecipeData(JsonObject p_267008_) {
            p_267008_.add("template", this.template.toJson());
            p_267008_.add("base", this.base.toJson());
            p_267008_.add("addition", this.addition.toJson());
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public RecipeSerializer<?> getType() {
            return this.type;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }

        public ResourceLocation id() {
            return this.id;
        }

        public RecipeSerializer<?> type() {
            return this.type;
        }

        public Ingredient template() {
            return this.template;
        }

        public Ingredient base() {
            return this.base;
        }

        public Ingredient addition() {
            return this.addition;
        }

        public Advancement.Builder advancement() {
            return this.advancement;
        }

        public ResourceLocation advancementId() {
            return this.advancementId;
        }
    }
}
