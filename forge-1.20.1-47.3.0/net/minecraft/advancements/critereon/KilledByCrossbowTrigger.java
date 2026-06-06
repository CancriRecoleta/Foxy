//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledByCrossbowTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("killed_by_crossbow");

    public KilledByCrossbowTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286238_, ContextAwarePredicate p_286227_, DeserializationContext p_286919_) {
        ContextAwarePredicate[] $$3 = EntityPredicate.fromJsonArray(p_286238_, "victims", p_286919_);
        MinMaxBounds.Ints $$4 = Ints.fromJson(p_286238_.get("unique_entity_types"));
        return new TriggerInstance(p_286227_, $$3, $$4);
    }

    public void trigger(ServerPlayer p_46872_, Collection<Entity> p_46873_) {
        List<LootContext> $$2 = Lists.newArrayList();
        Set<EntityType<?>> $$3 = Sets.newHashSet();
        Iterator var5 = p_46873_.iterator();

        while(var5.hasNext()) {
            Entity $$4 = (Entity)var5.next();
            $$3.add($$4.getType());
            $$2.add(EntityPredicate.createContext(p_46872_, $$4));
        }

        this.trigger(p_46872_, (p_46881_) -> {
            return p_46881_.matches($$2, $$3.size());
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final ContextAwarePredicate[] victims;
        private final MinMaxBounds.Ints uniqueEntityTypes;

        public TriggerInstance(ContextAwarePredicate p_286398_, ContextAwarePredicate[] p_286510_, MinMaxBounds.Ints p_286571_) {
            super(KilledByCrossbowTrigger.ID, p_286398_);
            this.victims = p_286510_;
            this.uniqueEntityTypes = p_286571_;
        }

        public static TriggerInstance crossbowKilled(EntityPredicate.Builder... p_46901_) {
            ContextAwarePredicate[] $$1 = new ContextAwarePredicate[p_46901_.length];

            for(int $$2 = 0; $$2 < p_46901_.length; ++$$2) {
                EntityPredicate.Builder $$3 = p_46901_[$$2];
                $$1[$$2] = EntityPredicate.wrap($$3.build());
            }

            return new TriggerInstance(ContextAwarePredicate.ANY, $$1, Ints.ANY);
        }

        public static TriggerInstance crossbowKilled(MinMaxBounds.Ints p_46894_) {
            ContextAwarePredicate[] $$1 = new ContextAwarePredicate[0];
            return new TriggerInstance(ContextAwarePredicate.ANY, $$1, p_46894_);
        }

        public boolean matches(Collection<LootContext> p_46898_, int p_46899_) {
            if (this.victims.length > 0) {
                List<LootContext> $$2 = Lists.newArrayList(p_46898_);
                ContextAwarePredicate[] var4 = this.victims;
                int var5 = var4.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    ContextAwarePredicate $$3 = var4[var6];
                    boolean $$4 = false;
                    Iterator<LootContext> $$5 = $$2.iterator();

                    while($$5.hasNext()) {
                        LootContext $$6 = (LootContext)$$5.next();
                        if ($$3.matches($$6)) {
                            $$5.remove();
                            $$4 = true;
                            break;
                        }
                    }

                    if (!$$4) {
                        return false;
                    }
                }
            }

            return this.uniqueEntityTypes.matches(p_46899_);
        }

        public JsonObject serializeToJson(SerializationContext p_46896_) {
            JsonObject $$1 = super.serializeToJson(p_46896_);
            $$1.add("victims", ContextAwarePredicate.toJson(this.victims, p_46896_));
            $$1.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
            return $$1;
        }
    }
}
