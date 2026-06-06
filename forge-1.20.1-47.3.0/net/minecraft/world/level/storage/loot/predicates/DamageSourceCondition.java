//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class DamageSourceCondition implements LootItemCondition {
    final DamageSourcePredicate predicate;

    DamageSourceCondition(DamageSourcePredicate p_81584_) {
        this.predicate = p_81584_;
    }

    public LootItemConditionType getType() {
        return LootItemConditions.DAMAGE_SOURCE_PROPERTIES;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.ORIGIN, LootContextParams.DAMAGE_SOURCE);
    }

    public boolean test(LootContext p_81592_) {
        DamageSource $$1 = (DamageSource)p_81592_.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        Vec3 $$2 = (Vec3)p_81592_.getParamOrNull(LootContextParams.ORIGIN);
        return $$2 != null && $$1 != null && this.predicate.matches(p_81592_.getLevel(), $$2, $$1);
    }

    public static LootItemCondition.Builder hasDamageSource(DamageSourcePredicate.Builder p_81590_) {
        return () -> {
            return new DamageSourceCondition(p_81590_.build());
        };
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<DamageSourceCondition> {
        public Serializer() {
        }

        public void serialize(JsonObject p_81605_, DamageSourceCondition p_81606_, JsonSerializationContext p_81607_) {
            p_81605_.add("predicate", p_81606_.predicate.serializeToJson());
        }

        public DamageSourceCondition deserialize(JsonObject p_81613_, JsonDeserializationContext p_81614_) {
            DamageSourcePredicate $$2 = DamageSourcePredicate.fromJson(p_81613_.get("predicate"));
            return new DamageSourceCondition($$2);
        }
    }
}
