//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.storage.loot.LootContext;

public class LightningStrikeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("lightning_strike");

    public LightningStrikeTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286889_, ContextAwarePredicate p_286650_, DeserializationContext p_286384_) {
        ContextAwarePredicate $$3 = EntityPredicate.fromJson(p_286889_, "lightning", p_286384_);
        ContextAwarePredicate $$4 = EntityPredicate.fromJson(p_286889_, "bystander", p_286384_);
        return new TriggerInstance(p_286650_, $$3, $$4);
    }

    public void trigger(ServerPlayer p_153392_, LightningBolt p_153393_, List<Entity> p_153394_) {
        List<LootContext> $$3 = (List)p_153394_.stream().map((p_153390_) -> {
            return EntityPredicate.createContext(p_153392_, p_153390_);
        }).collect(Collectors.toList());
        LootContext $$4 = EntityPredicate.createContext(p_153392_, p_153393_);
        this.trigger(p_153392_, (p_153402_) -> {
            return p_153402_.matches($$4, $$3);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ContextAwarePredicate lightning;
        private final ContextAwarePredicate bystander;

        public TriggerInstance(ContextAwarePredicate p_286747_, ContextAwarePredicate p_286287_, ContextAwarePredicate p_286566_) {
            super(LightningStrikeTrigger.ID, p_286747_);
            this.lightning = p_286287_;
            this.bystander = p_286566_;
        }

        public static TriggerInstance lighthingStrike(EntityPredicate p_153414_, EntityPredicate p_153415_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, EntityPredicate.wrap(p_153414_), EntityPredicate.wrap(p_153415_));
        }

        public boolean matches(LootContext p_153419_, List<LootContext> p_153420_) {
            if (!this.lightning.matches(p_153419_)) {
                return false;
            } else {
                if (this.bystander != ContextAwarePredicate.ANY) {
                    Stream var10000 = p_153420_.stream();
                    ContextAwarePredicate var10001 = this.bystander;
                    Objects.requireNonNull(var10001);
                    if (var10000.noneMatch(var10001::matches)) {
                        return false;
                    }
                }

                return true;
            }
        }

        public JsonObject serializeToJson(SerializationContext p_153417_) {
            JsonObject $$1 = super.serializeToJson(p_153417_);
            $$1.add("lightning", this.lightning.toJson(p_153417_));
            $$1.add("bystander", this.bystander.toJson(p_153417_));
            return $$1;
        }
    }
}
