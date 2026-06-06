//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;

public class ImpossibleTrigger implements CriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("impossible");

    public ImpossibleTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public void addPlayerListener(PlayerAdvancements p_41565_, CriterionTrigger.Listener<TriggerInstance> p_41566_) {
    }

    public void removePlayerListener(PlayerAdvancements p_41572_, CriterionTrigger.Listener<TriggerInstance> p_41573_) {
    }

    public void removePlayerListeners(PlayerAdvancements p_41563_) {
    }

    public TriggerInstance createInstance(JsonObject p_41569_, DeserializationContext p_41570_) {
        return new TriggerInstance();
    }

    public static class TriggerInstance implements CriterionTriggerInstance {
        public TriggerInstance() {
        }

        public ResourceLocation getCriterion() {
            return ImpossibleTrigger.ID;
        }

        public JsonObject serializeToJson(SerializationContext p_41577_) {
            return new JsonObject();
        }
    }
}
