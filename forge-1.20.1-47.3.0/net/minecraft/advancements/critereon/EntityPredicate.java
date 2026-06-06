//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

public class EntityPredicate {
    public static final EntityPredicate ANY;
    private final EntityTypePredicate entityType;
    private final DistancePredicate distanceToPlayer;
    private final LocationPredicate location;
    private final LocationPredicate steppingOnLocation;
    private final MobEffectsPredicate effects;
    private final NbtPredicate nbt;
    private final EntityFlagsPredicate flags;
    private final EntityEquipmentPredicate equipment;
    private final EntitySubPredicate subPredicate;
    private final EntityPredicate vehicle;
    private final EntityPredicate passenger;
    private final EntityPredicate targetedEntity;
    @Nullable
    private final String team;

    private EntityPredicate(EntityTypePredicate p_218789_, DistancePredicate p_218790_, LocationPredicate p_218791_, LocationPredicate p_218792_, MobEffectsPredicate p_218793_, NbtPredicate p_218794_, EntityFlagsPredicate p_218795_, EntityEquipmentPredicate p_218796_, EntitySubPredicate p_218797_, @Nullable String p_218798_) {
        this.entityType = p_218789_;
        this.distanceToPlayer = p_218790_;
        this.location = p_218791_;
        this.steppingOnLocation = p_218792_;
        this.effects = p_218793_;
        this.nbt = p_218794_;
        this.flags = p_218795_;
        this.equipment = p_218796_;
        this.subPredicate = p_218797_;
        this.passenger = this;
        this.vehicle = this;
        this.targetedEntity = this;
        this.team = p_218798_;
    }

    EntityPredicate(EntityTypePredicate p_218775_, DistancePredicate p_218776_, LocationPredicate p_218777_, LocationPredicate p_218778_, MobEffectsPredicate p_218779_, NbtPredicate p_218780_, EntityFlagsPredicate p_218781_, EntityEquipmentPredicate p_218782_, EntitySubPredicate p_218783_, EntityPredicate p_218784_, EntityPredicate p_218785_, EntityPredicate p_218786_, @Nullable String p_218787_) {
        this.entityType = p_218775_;
        this.distanceToPlayer = p_218776_;
        this.location = p_218777_;
        this.steppingOnLocation = p_218778_;
        this.effects = p_218779_;
        this.nbt = p_218780_;
        this.flags = p_218781_;
        this.equipment = p_218782_;
        this.subPredicate = p_218783_;
        this.vehicle = p_218784_;
        this.passenger = p_218785_;
        this.targetedEntity = p_218786_;
        this.team = p_218787_;
    }

    public static ContextAwarePredicate fromJson(JsonObject p_286877_, String p_286245_, DeserializationContext p_286427_) {
        JsonElement $$3 = p_286877_.get(p_286245_);
        return fromElement(p_286245_, p_286427_, $$3);
    }

    public static ContextAwarePredicate[] fromJsonArray(JsonObject p_286850_, String p_286682_, DeserializationContext p_286876_) {
        JsonElement $$3 = p_286850_.get(p_286682_);
        if ($$3 != null && !$$3.isJsonNull()) {
            JsonArray $$4 = GsonHelper.convertToJsonArray($$3, p_286682_);
            ContextAwarePredicate[] $$5 = new ContextAwarePredicate[$$4.size()];

            for(int $$6 = 0; $$6 < $$4.size(); ++$$6) {
                $$5[$$6] = fromElement(p_286682_ + "[" + $$6 + "]", p_286876_, $$4.get($$6));
            }

            return $$5;
        } else {
            return new ContextAwarePredicate[0];
        }
    }

    private static ContextAwarePredicate fromElement(String p_286569_, DeserializationContext p_286821_, @Nullable JsonElement p_286582_) {
        ContextAwarePredicate $$3 = ContextAwarePredicate.fromElement(p_286569_, p_286821_, p_286582_, LootContextParamSets.ADVANCEMENT_ENTITY);
        if ($$3 != null) {
            return $$3;
        } else {
            EntityPredicate $$4 = fromJson(p_286582_);
            return wrap($$4);
        }
    }

    public static ContextAwarePredicate wrap(EntityPredicate p_286570_) {
        if (p_286570_ == ANY) {
            return ContextAwarePredicate.ANY;
        } else {
            LootItemCondition $$1 = LootItemEntityPropertyCondition.hasProperties(EntityTarget.THIS, p_286570_).build();
            return new ContextAwarePredicate(new LootItemCondition[]{$$1});
        }
    }

    public boolean matches(ServerPlayer p_36612_, @Nullable Entity p_36613_) {
        return this.matches(p_36612_.serverLevel(), p_36612_.position(), p_36613_);
    }

    public boolean matches(ServerLevel p_36608_, @Nullable Vec3 p_36609_, @Nullable Entity p_36610_) {
        if (this == ANY) {
            return true;
        } else if (p_36610_ == null) {
            return false;
        } else if (!this.entityType.matches(p_36610_.getType())) {
            return false;
        } else {
            if (p_36609_ == null) {
                if (this.distanceToPlayer != DistancePredicate.ANY) {
                    return false;
                }
            } else if (!this.distanceToPlayer.matches(p_36609_.x, p_36609_.y, p_36609_.z, p_36610_.getX(), p_36610_.getY(), p_36610_.getZ())) {
                return false;
            }

            if (!this.location.matches(p_36608_, p_36610_.getX(), p_36610_.getY(), p_36610_.getZ())) {
                return false;
            } else {
                if (this.steppingOnLocation != LocationPredicate.ANY) {
                    Vec3 $$3 = Vec3.atCenterOf(p_36610_.getOnPos());
                    if (!this.steppingOnLocation.matches(p_36608_, $$3.x(), $$3.y(), $$3.z())) {
                        return false;
                    }
                }

                if (!this.effects.matches(p_36610_)) {
                    return false;
                } else if (!this.nbt.matches(p_36610_)) {
                    return false;
                } else if (!this.flags.matches(p_36610_)) {
                    return false;
                } else if (!this.equipment.matches(p_36610_)) {
                    return false;
                } else if (!this.subPredicate.matches(p_36610_, p_36608_, p_36609_)) {
                    return false;
                } else if (!this.vehicle.matches(p_36608_, p_36609_, p_36610_.getVehicle())) {
                    return false;
                } else if (this.passenger != ANY && p_36610_.getPassengers().stream().noneMatch((p_150322_) -> {
                    return this.passenger.matches(p_36608_, p_36609_, p_150322_);
                })) {
                    return false;
                } else if (!this.targetedEntity.matches(p_36608_, p_36609_, p_36610_ instanceof Mob ? ((Mob)p_36610_).getTarget() : null)) {
                    return false;
                } else {
                    if (this.team != null) {
                        Team $$4 = p_36610_.getTeam();
                        if ($$4 == null || !this.team.equals($$4.getName())) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }
    }

    public static EntityPredicate fromJson(@Nullable JsonElement p_36615_) {
        if (p_36615_ != null && !p_36615_.isJsonNull()) {
            JsonObject $$1 = GsonHelper.convertToJsonObject(p_36615_, "entity");
            EntityTypePredicate $$2 = EntityTypePredicate.fromJson($$1.get("type"));
            DistancePredicate $$3 = DistancePredicate.fromJson($$1.get("distance"));
            LocationPredicate $$4 = LocationPredicate.fromJson($$1.get("location"));
            LocationPredicate $$5 = LocationPredicate.fromJson($$1.get("stepping_on"));
            MobEffectsPredicate $$6 = MobEffectsPredicate.fromJson($$1.get("effects"));
            NbtPredicate $$7 = NbtPredicate.fromJson($$1.get("nbt"));
            EntityFlagsPredicate $$8 = EntityFlagsPredicate.fromJson($$1.get("flags"));
            EntityEquipmentPredicate $$9 = EntityEquipmentPredicate.fromJson($$1.get("equipment"));
            EntitySubPredicate $$10 = EntitySubPredicate.fromJson($$1.get("type_specific"));
            EntityPredicate $$11 = fromJson($$1.get("vehicle"));
            EntityPredicate $$12 = fromJson($$1.get("passenger"));
            EntityPredicate $$13 = fromJson($$1.get("targeted_entity"));
            String $$14 = GsonHelper.getAsString($$1, "team", (String)null);
            return (new Builder()).entityType($$2).distance($$3).located($$4).steppingOn($$5).effects($$6).nbt($$7).flags($$8).equipment($$9).subPredicate($$10).team($$14).vehicle($$11).passenger($$12).targetedEntity($$13).build();
        } else {
            return ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject $$0 = new JsonObject();
            $$0.add("type", this.entityType.serializeToJson());
            $$0.add("distance", this.distanceToPlayer.serializeToJson());
            $$0.add("location", this.location.serializeToJson());
            $$0.add("stepping_on", this.steppingOnLocation.serializeToJson());
            $$0.add("effects", this.effects.serializeToJson());
            $$0.add("nbt", this.nbt.serializeToJson());
            $$0.add("flags", this.flags.serializeToJson());
            $$0.add("equipment", this.equipment.serializeToJson());
            $$0.add("type_specific", this.subPredicate.serialize());
            $$0.add("vehicle", this.vehicle.serializeToJson());
            $$0.add("passenger", this.passenger.serializeToJson());
            $$0.add("targeted_entity", this.targetedEntity.serializeToJson());
            $$0.addProperty("team", this.team);
            return $$0;
        }
    }

    public static LootContext createContext(ServerPlayer p_36617_, Entity p_36618_) {
        LootParams $$2 = (new LootParams.Builder(p_36617_.serverLevel())).withParameter(LootContextParams.THIS_ENTITY, p_36618_).withParameter(LootContextParams.ORIGIN, p_36617_.position()).create(LootContextParamSets.ADVANCEMENT_ENTITY);
        return (new LootContext.Builder($$2)).create((ResourceLocation)null);
    }

    static {
        ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, EntitySubPredicate.ANY, (String)null);
    }

    public static class Builder {
        private EntityTypePredicate entityType;
        private DistancePredicate distanceToPlayer;
        private LocationPredicate location;
        private LocationPredicate steppingOnLocation;
        private MobEffectsPredicate effects;
        private NbtPredicate nbt;
        private EntityFlagsPredicate flags;
        private EntityEquipmentPredicate equipment;
        private EntitySubPredicate subPredicate;
        private EntityPredicate vehicle;
        private EntityPredicate passenger;
        private EntityPredicate targetedEntity;
        @Nullable
        private String team;

        public Builder() {
            this.entityType = EntityTypePredicate.ANY;
            this.distanceToPlayer = DistancePredicate.ANY;
            this.location = LocationPredicate.ANY;
            this.steppingOnLocation = LocationPredicate.ANY;
            this.effects = MobEffectsPredicate.ANY;
            this.nbt = NbtPredicate.ANY;
            this.flags = EntityFlagsPredicate.ANY;
            this.equipment = EntityEquipmentPredicate.ANY;
            this.subPredicate = EntitySubPredicate.ANY;
            this.vehicle = EntityPredicate.ANY;
            this.passenger = EntityPredicate.ANY;
            this.targetedEntity = EntityPredicate.ANY;
        }

        public static Builder entity() {
            return new Builder();
        }

        public Builder of(EntityType<?> p_36637_) {
            this.entityType = EntityTypePredicate.of(p_36637_);
            return this;
        }

        public Builder of(TagKey<EntityType<?>> p_204078_) {
            this.entityType = EntityTypePredicate.of(p_204078_);
            return this;
        }

        public Builder entityType(EntityTypePredicate p_36647_) {
            this.entityType = p_36647_;
            return this;
        }

        public Builder distance(DistancePredicate p_36639_) {
            this.distanceToPlayer = p_36639_;
            return this;
        }

        public Builder located(LocationPredicate p_36651_) {
            this.location = p_36651_;
            return this;
        }

        public Builder steppingOn(LocationPredicate p_150331_) {
            this.steppingOnLocation = p_150331_;
            return this;
        }

        public Builder effects(MobEffectsPredicate p_36653_) {
            this.effects = p_36653_;
            return this;
        }

        public Builder nbt(NbtPredicate p_36655_) {
            this.nbt = p_36655_;
            return this;
        }

        public Builder flags(EntityFlagsPredicate p_36643_) {
            this.flags = p_36643_;
            return this;
        }

        public Builder equipment(EntityEquipmentPredicate p_36641_) {
            this.equipment = p_36641_;
            return this;
        }

        public Builder subPredicate(EntitySubPredicate p_218801_) {
            this.subPredicate = p_218801_;
            return this;
        }

        public Builder vehicle(EntityPredicate p_36645_) {
            this.vehicle = p_36645_;
            return this;
        }

        public Builder passenger(EntityPredicate p_150329_) {
            this.passenger = p_150329_;
            return this;
        }

        public Builder targetedEntity(EntityPredicate p_36664_) {
            this.targetedEntity = p_36664_;
            return this;
        }

        public Builder team(@Nullable String p_36659_) {
            this.team = p_36659_;
            return this;
        }

        public EntityPredicate build() {
            return new EntityPredicate(this.entityType, this.distanceToPlayer, this.location, this.steppingOnLocation, this.effects, this.nbt, this.flags, this.equipment, this.subPredicate, this.vehicle, this.passenger, this.targetedEntity, this.team);
        }
    }
}
