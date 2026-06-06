//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class ChanneledLightningTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

    public ChanneledLightningTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286858_, ContextAwarePredicate p_286240_, DeserializationContext p_286562_) {
        ContextAwarePredicate[] $$3 = EntityPredicate.fromJsonArray(p_286858_, "victims", p_286562_);
        return new TriggerInstance(p_286240_, $$3);
    }

    public void trigger(ServerPlayer p_21722_, Collection<? extends Entity> p_21723_) {
        List<LootContext> $$2 = (List)p_21723_.stream().map((p_21720_) -> {
            return EntityPredicate.createContext(p_21722_, p_21720_);
        }).collect(Collectors.toList());
        this.trigger(p_21722_, (p_21730_) -> {
            return p_21730_.matches($$2);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ContextAwarePredicate[] victims;

        public TriggerInstance(ContextAwarePredicate p_286697_, ContextAwarePredicate[] p_286366_) {
            super(ChanneledLightningTrigger.ID, p_286697_);
            this.victims = p_286366_;
        }

        public static TriggerInstance channeledLightning(EntityPredicate... p_21747_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, (ContextAwarePredicate[])Stream.of(p_21747_).map(EntityPredicate::wrap).toArray((p_286116_) -> {
                return new ContextAwarePredicate[p_286116_];
            }));
        }

        public boolean matches(Collection<? extends LootContext> p_21745_) {
            ContextAwarePredicate[] var2 = this.victims;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                ContextAwarePredicate $$1 = var2[var4];
                boolean $$2 = false;
                Iterator var7 = p_21745_.iterator();

                while(var7.hasNext()) {
                    LootContext $$3 = (LootContext)var7.next();
                    if ($$1.matches($$3)) {
                        $$2 = true;
                        break;
                    }
                }

                if (!$$2) {
                    return false;
                }
            }

            return true;
        }

        public JsonObject serializeToJson(SerializationContext p_21743_) {
            JsonObject $$1 = super.serializeToJson(p_21743_);
            $$1.add("victims", ContextAwarePredicate.toJson(this.victims, p_21743_));
            return $$1;
        }
    }
}
