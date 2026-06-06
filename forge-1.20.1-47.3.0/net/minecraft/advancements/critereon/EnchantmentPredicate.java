//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentPredicate {
    public static final EnchantmentPredicate ANY = new EnchantmentPredicate();
    public static final EnchantmentPredicate[] NONE = new EnchantmentPredicate[0];
    @Nullable
    private final Enchantment enchantment;
    private final MinMaxBounds.Ints level;

    public EnchantmentPredicate() {
        this.enchantment = null;
        this.level = Ints.ANY;
    }

    public EnchantmentPredicate(@Nullable Enchantment p_30471_, MinMaxBounds.Ints p_30472_) {
        this.enchantment = p_30471_;
        this.level = p_30472_;
    }

    public boolean containedIn(Map<Enchantment, Integer> p_30477_) {
        if (this.enchantment != null) {
            if (!p_30477_.containsKey(this.enchantment)) {
                return false;
            }

            int $$1 = (Integer)p_30477_.get(this.enchantment);
            if (this.level != Ints.ANY && !this.level.matches($$1)) {
                return false;
            }
        } else if (this.level != Ints.ANY) {
            Iterator var4 = p_30477_.values().iterator();

            Integer $$2;
            do {
                if (!var4.hasNext()) {
                    return false;
                }

                $$2 = (Integer)var4.next();
            } while(!this.level.matches($$2));

            return true;
        }

        return true;
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject $$0 = new JsonObject();
            if (this.enchantment != null) {
                $$0.addProperty("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this.enchantment).toString());
            }

            $$0.add("levels", this.level.serializeToJson());
            return $$0;
        }
    }

    public static EnchantmentPredicate fromJson(@Nullable JsonElement p_30475_) {
        if (p_30475_ != null && !p_30475_.isJsonNull()) {
            JsonObject $$1 = GsonHelper.convertToJsonObject(p_30475_, "enchantment");
            Enchantment $$2 = null;
            if ($$1.has("enchantment")) {
                ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString($$1, "enchantment"));
                $$2 = (Enchantment)BuiltInRegistries.ENCHANTMENT.getOptional($$3).orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown enchantment '" + $$3 + "'");
                });
            }

            MinMaxBounds.Ints $$4 = Ints.fromJson($$1.get("levels"));
            return new EnchantmentPredicate($$2, $$4);
        } else {
            return ANY;
        }
    }

    public static EnchantmentPredicate[] fromJsonArray(@Nullable JsonElement p_30481_) {
        if (p_30481_ != null && !p_30481_.isJsonNull()) {
            JsonArray $$1 = GsonHelper.convertToJsonArray(p_30481_, "enchantments");
            EnchantmentPredicate[] $$2 = new EnchantmentPredicate[$$1.size()];

            for(int $$3 = 0; $$3 < $$2.length; ++$$3) {
                $$2[$$3] = fromJson($$1.get($$3));
            }

            return $$2;
        } else {
            return NONE;
        }
    }
}
