//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class RecipeCraftedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("recipe_crafted");

    public RecipeCraftedTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    protected TriggerInstance createInstance(JsonObject p_286541_, ContextAwarePredicate p_286267_, DeserializationContext p_286402_) {
        ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString(p_286541_, "recipe_id"));
        ItemPredicate[] $$4 = ItemPredicate.fromJsonArray(p_286541_.get("ingredients"));
        return new TriggerInstance(p_286267_, $$3, List.of($$4));
    }

    public void trigger(ServerPlayer p_281468_, ResourceLocation p_282903_, List<ItemStack> p_282070_) {
        this.trigger(p_281468_, (p_282798_) -> {
            return p_282798_.matches(p_282903_, p_282070_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ResourceLocation recipeId;
        private final List<ItemPredicate> predicates;

        public TriggerInstance(ContextAwarePredicate p_286913_, ResourceLocation p_286906_, List<ItemPredicate> p_286302_) {
            super(RecipeCraftedTrigger.ID, p_286913_);
            this.recipeId = p_286906_;
            this.predicates = p_286302_;
        }

        public static TriggerInstance craftedItem(ResourceLocation p_282794_, List<ItemPredicate> p_281369_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_282794_, p_281369_);
        }

        public static TriggerInstance craftedItem(ResourceLocation p_283538_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_283538_, List.of());
        }

        boolean matches(ResourceLocation p_283528_, List<ItemStack> p_283698_) {
            if (!p_283528_.equals(this.recipeId)) {
                return false;
            } else {
                List<ItemStack> $$2 = new ArrayList(p_283698_);
                Iterator var4 = this.predicates.iterator();

                boolean $$4;
                do {
                    if (!var4.hasNext()) {
                        return true;
                    }

                    ItemPredicate $$3 = (ItemPredicate)var4.next();
                    $$4 = false;
                    Iterator<ItemStack> $$5 = $$2.iterator();

                    while($$5.hasNext()) {
                        if ($$3.matches((ItemStack)$$5.next())) {
                            $$5.remove();
                            $$4 = true;
                            break;
                        }
                    }
                } while($$4);

                return false;
            }
        }

        public JsonObject serializeToJson(SerializationContext p_281942_) {
            JsonObject $$1 = super.serializeToJson(p_281942_);
            $$1.addProperty("recipe_id", this.recipeId.toString());
            if (this.predicates.size() > 0) {
                JsonArray $$2 = new JsonArray();
                Iterator var4 = this.predicates.iterator();

                while(var4.hasNext()) {
                    ItemPredicate $$3 = (ItemPredicate)var4.next();
                    $$2.add($$3.serializeToJson());
                }

                $$1.add("ingredients", $$2);
            }

            return $$1;
        }
    }
}
