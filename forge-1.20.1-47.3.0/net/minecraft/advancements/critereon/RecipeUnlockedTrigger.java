//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeUnlockedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");

    public RecipeUnlockedTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286387_, ContextAwarePredicate p_286739_, DeserializationContext p_286649_) {
        ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString(p_286387_, "recipe"));
        return new TriggerInstance(p_286739_, $$3);
    }

    public void trigger(ServerPlayer p_63719_, Recipe<?> p_63720_) {
        this.trigger(p_63719_, (p_63723_) -> {
            return p_63723_.matches(p_63720_);
        });
    }

    public static TriggerInstance unlocked(ResourceLocation p_63729_) {
        return new TriggerInstance(ContextAwarePredicate.ANY, p_63729_);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ResourceLocation recipe;

        public TriggerInstance(ContextAwarePredicate p_286461_, ResourceLocation p_286775_) {
            super(RecipeUnlockedTrigger.ID, p_286461_);
            this.recipe = p_286775_;
        }

        public JsonObject serializeToJson(SerializationContext p_63742_) {
            JsonObject $$1 = super.serializeToJson(p_63742_);
            $$1.addProperty("recipe", this.recipe.toString());
            return $$1;
        }

        public boolean matches(Recipe<?> p_63740_) {
            return this.recipe.equals(p_63740_.getId());
        }
    }
}
