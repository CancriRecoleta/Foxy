//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class EntityHasScoreCondition implements LootItemCondition {
    final Map<String, IntRange> scores;
    final LootContext.EntityTarget entityTarget;

    EntityHasScoreCondition(Map<String, IntRange> p_81618_, LootContext.EntityTarget p_81619_) {
        this.scores = ImmutableMap.copyOf(p_81618_);
        this.entityTarget = p_81619_;
    }

    public LootItemConditionType getType() {
        return LootItemConditions.ENTITY_SCORES;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set)Stream.concat(Stream.of(this.entityTarget.getParam()), this.scores.values().stream().flatMap((p_165487_) -> {
            return p_165487_.getReferencedContextParams().stream();
        })).collect(ImmutableSet.toImmutableSet());
    }

    public boolean test(LootContext p_81631_) {
        Entity $$1 = (Entity)p_81631_.getParamOrNull(this.entityTarget.getParam());
        if ($$1 == null) {
            return false;
        } else {
            Scoreboard $$2 = $$1.level().getScoreboard();
            Iterator var4 = this.scores.entrySet().iterator();

            Map.Entry $$3;
            do {
                if (!var4.hasNext()) {
                    return true;
                }

                $$3 = (Map.Entry)var4.next();
            } while(this.hasScore(p_81631_, $$1, $$2, (String)$$3.getKey(), (IntRange)$$3.getValue()));

            return false;
        }
    }

    protected boolean hasScore(LootContext p_165491_, Entity p_165492_, Scoreboard p_165493_, String p_165494_, IntRange p_165495_) {
        Objective $$5 = p_165493_.getObjective(p_165494_);
        if ($$5 == null) {
            return false;
        } else {
            String $$6 = p_165492_.getScoreboardName();
            return !p_165493_.hasPlayerScore($$6, $$5) ? false : p_165495_.test(p_165491_, p_165493_.getOrCreatePlayerScore($$6, $$5).getScore());
        }
    }

    public static Builder hasScores(LootContext.EntityTarget p_165489_) {
        return new Builder(p_165489_);
    }

    public static class Builder implements LootItemCondition.Builder {
        private final Map<String, IntRange> scores = Maps.newHashMap();
        private final LootContext.EntityTarget entityTarget;

        public Builder(LootContext.EntityTarget p_165499_) {
            this.entityTarget = p_165499_;
        }

        public Builder withScore(String p_165501_, IntRange p_165502_) {
            this.scores.put(p_165501_, p_165502_);
            return this;
        }

        public LootItemCondition build() {
            return new EntityHasScoreCondition(this.scores, this.entityTarget);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<EntityHasScoreCondition> {
        public Serializer() {
        }

        public void serialize(JsonObject p_81644_, EntityHasScoreCondition p_81645_, JsonSerializationContext p_81646_) {
            JsonObject $$3 = new JsonObject();
            Iterator var5 = p_81645_.scores.entrySet().iterator();

            while(var5.hasNext()) {
                Map.Entry<String, IntRange> $$4 = (Map.Entry)var5.next();
                $$3.add((String)$$4.getKey(), p_81646_.serialize($$4.getValue()));
            }

            p_81644_.add("scores", $$3);
            p_81644_.add("entity", p_81646_.serialize(p_81645_.entityTarget));
        }

        public EntityHasScoreCondition deserialize(JsonObject p_81652_, JsonDeserializationContext p_81653_) {
            Set<Map.Entry<String, JsonElement>> $$2 = GsonHelper.getAsJsonObject(p_81652_, "scores").entrySet();
            Map<String, IntRange> $$3 = Maps.newLinkedHashMap();
            Iterator var5 = $$2.iterator();

            while(var5.hasNext()) {
                Map.Entry<String, JsonElement> $$4 = (Map.Entry)var5.next();
                $$3.put((String)$$4.getKey(), (IntRange)GsonHelper.convertToObject((JsonElement)$$4.getValue(), "score", p_81653_, IntRange.class));
            }

            return new EntityHasScoreCondition($$3, (LootContext.EntityTarget)GsonHelper.getAsObject(p_81652_, "entity", p_81653_, LootContext.EntityTarget.class));
        }
    }
}
