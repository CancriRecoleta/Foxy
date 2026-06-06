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
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class ConditionalAdvancement {
    public ConditionalAdvancement() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static @Nullable JsonObject processConditional(JsonObject json, ICondition.IContext context) {
        JsonArray entries = GsonHelper.getAsJsonArray(json, "advancements", (JsonArray)null);
        if (entries == null) {
            return CraftingHelper.processConditions(json, "conditions", context) ? json : null;
        } else {
            int idx = 0;

            for(Iterator var4 = entries.iterator(); var4.hasNext(); ++idx) {
                JsonElement ele = (JsonElement)var4.next();
                if (!ele.isJsonObject()) {
                    throw new JsonSyntaxException("Invalid advancement entry at index " + idx + " Must be JsonObject");
                }

                if (CraftingHelper.processConditions(GsonHelper.getAsJsonArray(ele.getAsJsonObject(), "conditions"), context)) {
                    return GsonHelper.getAsJsonObject(ele.getAsJsonObject(), "advancement");
                }
            }

            return null;
        }
    }

    public static class Builder {
        private List<ICondition[]> conditions = new ArrayList();
        private List<Supplier<JsonElement>> advancements = new ArrayList();
        private List<ICondition> currentConditions = new ArrayList();
        private boolean locked = false;

        public Builder() {
        }

        public Builder addCondition(ICondition condition) {
            if (this.locked) {
                throw new IllegalStateException("Attempted to modify finished builder");
            } else {
                this.currentConditions.add(condition);
                return this;
            }
        }

        public Builder addAdvancement(Consumer<Consumer<Advancement.Builder>> callable) {
            if (this.locked) {
                throw new IllegalStateException("Attempted to modify finished builder");
            } else {
                callable.accept(this::addAdvancement);
                return this;
            }
        }

        public Builder addAdvancement(Advancement.Builder advancement) {
            Objects.requireNonNull(advancement);
            return this.addAdvancement(advancement::serializeToJson);
        }

        public Builder addAdvancement(FinishedRecipe fromRecipe) {
            Objects.requireNonNull(fromRecipe);
            return this.addAdvancement(fromRecipe::serializeAdvancement);
        }

        private Builder addAdvancement(Supplier<JsonElement> jsonSupplier) {
            if (this.locked) {
                throw new IllegalStateException("Attempted to modify finished builder");
            } else if (this.currentConditions.isEmpty()) {
                throw new IllegalStateException("Can not add a advancement with no conditions.");
            } else {
                this.conditions.add((ICondition[])this.currentConditions.toArray(new ICondition[this.currentConditions.size()]));
                this.advancements.add(jsonSupplier);
                this.currentConditions.clear();
                return this;
            }
        }

        public JsonObject write() {
            if (!this.locked) {
                if (!this.currentConditions.isEmpty()) {
                    throw new IllegalStateException("Invalid builder state: Orphaned conditions");
                }

                if (this.advancements.isEmpty()) {
                    throw new IllegalStateException("Invalid builder state: No Advancements");
                }

                this.locked = true;
            }

            JsonObject json = new JsonObject();
            JsonArray array = new JsonArray();
            json.add("advancements", array);

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
                holder.add("advancement", (JsonElement)((Supplier)this.advancements.get(x)).get());
                array.add(holder);
            }

            return json;
        }
    }
}
