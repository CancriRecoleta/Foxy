//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Criterion {
    @Nullable
    private final CriterionTriggerInstance trigger;

    public Criterion(CriterionTriggerInstance p_11415_) {
        this.trigger = p_11415_;
    }

    public Criterion() {
        this.trigger = null;
    }

    public void serializeToNetwork(FriendlyByteBuf p_11424_) {
    }

    public static Criterion criterionFromJson(JsonObject p_11418_, DeserializationContext p_11419_) {
        ResourceLocation $$2 = new ResourceLocation(GsonHelper.getAsString(p_11418_, "trigger"));
        CriterionTrigger<?> $$3 = CriteriaTriggers.getCriterion($$2);
        if ($$3 == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + $$2);
        } else {
            CriterionTriggerInstance $$4 = $$3.createInstance(GsonHelper.getAsJsonObject(p_11418_, "conditions", new JsonObject()), p_11419_);
            return new Criterion($$4);
        }
    }

    public static Criterion criterionFromNetwork(FriendlyByteBuf p_11430_) {
        return new Criterion();
    }

    public static Map<String, Criterion> criteriaFromJson(JsonObject p_11427_, DeserializationContext p_11428_) {
        Map<String, Criterion> $$2 = Maps.newHashMap();
        Iterator var3 = p_11427_.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, JsonElement> $$3 = (Map.Entry)var3.next();
            $$2.put((String)$$3.getKey(), criterionFromJson(GsonHelper.convertToJsonObject((JsonElement)$$3.getValue(), "criterion"), p_11428_));
        }

        return $$2;
    }

    public static Map<String, Criterion> criteriaFromNetwork(FriendlyByteBuf p_11432_) {
        return p_11432_.readMap(FriendlyByteBuf::readUtf, Criterion::criterionFromNetwork);
    }

    public static void serializeToNetwork(Map<String, Criterion> p_11421_, FriendlyByteBuf p_11422_) {
        p_11422_.writeMap(p_11421_, FriendlyByteBuf::writeUtf, (p_145258_, p_145259_) -> {
            p_145259_.serializeToNetwork(p_145258_);
        });
    }

    @Nullable
    public CriterionTriggerInstance getTrigger() {
        return this.trigger;
    }

    public JsonElement serializeToJson() {
        if (this.trigger == null) {
            throw new JsonSyntaxException("Missing trigger");
        } else {
            JsonObject $$0 = new JsonObject();
            $$0.addProperty("trigger", this.trigger.getCriterion().toString());
            JsonObject $$1 = this.trigger.serializeToJson(SerializationContext.INSTANCE);
            if ($$1.size() != 0) {
                $$0.add("conditions", $$1);
            }

            return $$0;
        }
    }
}
