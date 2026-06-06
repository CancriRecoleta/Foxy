//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class DistanceTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    final ResourceLocation id;

    public DistanceTrigger(ResourceLocation p_186163_) {
        this.id = p_186163_;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public TriggerInstance createInstance(JsonObject p_286540_, ContextAwarePredicate p_286753_, DeserializationContext p_286709_) {
        LocationPredicate $$3 = LocationPredicate.fromJson(p_286540_.get("start_position"));
        DistancePredicate $$4 = DistancePredicate.fromJson(p_286540_.get("distance"));
        return new TriggerInstance(this.id, p_286753_, $$3, $$4);
    }

    public void trigger(ServerPlayer p_186166_, Vec3 p_186167_) {
        Vec3 $$2 = p_186166_.position();
        this.trigger(p_186166_, (p_284572_) -> {
            return p_284572_.matches(p_186166_.serverLevel(), p_186167_, $$2);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final LocationPredicate startPosition;
        private final DistancePredicate distance;

        public TriggerInstance(ResourceLocation p_286369_, ContextAwarePredicate p_286587_, LocationPredicate p_286563_, DistancePredicate p_286818_) {
            super(p_286369_, p_286587_);
            this.startPosition = p_286563_;
            this.distance = p_286818_;
        }

        public static TriggerInstance fallFromHeight(EntityPredicate.Builder p_186198_, DistancePredicate p_186199_, LocationPredicate p_186200_) {
            return new TriggerInstance(CriteriaTriggers.FALL_FROM_HEIGHT.id, EntityPredicate.wrap(p_186198_.build()), p_186200_, p_186199_);
        }

        public static TriggerInstance rideEntityInLava(EntityPredicate.Builder p_186195_, DistancePredicate p_186196_) {
            return new TriggerInstance(CriteriaTriggers.RIDE_ENTITY_IN_LAVA_TRIGGER.id, EntityPredicate.wrap(p_186195_.build()), LocationPredicate.ANY, p_186196_);
        }

        public static TriggerInstance travelledThroughNether(DistancePredicate p_186193_) {
            return new TriggerInstance(CriteriaTriggers.NETHER_TRAVEL.id, ContextAwarePredicate.ANY, LocationPredicate.ANY, p_186193_);
        }

        public JsonObject serializeToJson(SerializationContext p_186202_) {
            JsonObject $$1 = super.serializeToJson(p_186202_);
            $$1.add("start_position", this.startPosition.serializeToJson());
            $$1.add("distance", this.distance.serializeToJson());
            return $$1;
        }

        public boolean matches(ServerLevel p_186189_, Vec3 p_186190_, Vec3 p_186191_) {
            if (!this.startPosition.matches(p_186189_, p_186190_.x, p_186190_.y, p_186190_.z)) {
                return false;
            } else {
                return this.distance.matches(p_186190_.x, p_186190_.y, p_186190_.z, p_186191_.x, p_186191_.y, p_186191_.z);
            }
        }
    }
}
