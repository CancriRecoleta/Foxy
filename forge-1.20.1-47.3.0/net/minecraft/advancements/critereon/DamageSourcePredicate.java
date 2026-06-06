//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.phys.Vec3;

public class DamageSourcePredicate {
    public static final DamageSourcePredicate ANY = net.minecraft.advancements.critereon.DamageSourcePredicate.Builder.damageType().build();
    private final List<TagPredicate<DamageType>> tags;
    private final EntityPredicate directEntity;
    private final EntityPredicate sourceEntity;

    public DamageSourcePredicate(List<TagPredicate<DamageType>> p_270233_, EntityPredicate p_270167_, EntityPredicate p_270429_) {
        this.tags = p_270233_;
        this.directEntity = p_270167_;
        this.sourceEntity = p_270429_;
    }

    public boolean matches(ServerPlayer p_25449_, DamageSource p_25450_) {
        return this.matches(p_25449_.serverLevel(), p_25449_.position(), p_25450_);
    }

    public boolean matches(ServerLevel p_25445_, Vec3 p_25446_, DamageSource p_25447_) {
        if (this == ANY) {
            return true;
        } else {
            Iterator var4 = this.tags.iterator();

            TagPredicate $$3;
            do {
                if (!var4.hasNext()) {
                    if (!this.directEntity.matches(p_25445_, p_25446_, p_25447_.getDirectEntity())) {
                        return false;
                    }

                    if (!this.sourceEntity.matches(p_25445_, p_25446_, p_25447_.getEntity())) {
                        return false;
                    }

                    return true;
                }

                $$3 = (TagPredicate)var4.next();
            } while($$3.matches(p_25447_.typeHolder()));

            return false;
        }
    }

    public static DamageSourcePredicate fromJson(@Nullable JsonElement p_25452_) {
        if (p_25452_ != null && !p_25452_.isJsonNull()) {
            JsonObject $$1 = GsonHelper.convertToJsonObject(p_25452_, "damage type");
            JsonArray $$2 = GsonHelper.getAsJsonArray($$1, "tags", (JsonArray)null);
            Object $$3;
            if ($$2 != null) {
                $$3 = new ArrayList($$2.size());
                Iterator var4 = $$2.iterator();

                while(var4.hasNext()) {
                    JsonElement $$4 = (JsonElement)var4.next();
                    ((List)$$3).add(TagPredicate.fromJson($$4, Registries.DAMAGE_TYPE));
                }
            } else {
                $$3 = List.of();
            }

            EntityPredicate $$6 = EntityPredicate.fromJson($$1.get("direct_entity"));
            EntityPredicate $$7 = EntityPredicate.fromJson($$1.get("source_entity"));
            return new DamageSourcePredicate((List)$$3, $$6, $$7);
        } else {
            return ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject $$0 = new JsonObject();
            if (!this.tags.isEmpty()) {
                JsonArray $$1 = new JsonArray(this.tags.size());

                for(int $$2 = 0; $$2 < this.tags.size(); ++$$2) {
                    $$1.add(((TagPredicate)this.tags.get($$2)).serializeToJson());
                }

                $$0.add("tags", $$1);
            }

            $$0.add("direct_entity", this.directEntity.serializeToJson());
            $$0.add("source_entity", this.sourceEntity.serializeToJson());
            return $$0;
        }
    }

    public static class Builder {
        private final ImmutableList.Builder<TagPredicate<DamageType>> tags = ImmutableList.builder();
        private EntityPredicate directEntity;
        private EntityPredicate sourceEntity;

        public Builder() {
            this.directEntity = EntityPredicate.ANY;
            this.sourceEntity = EntityPredicate.ANY;
        }

        public static Builder damageType() {
            return new Builder();
        }

        public Builder tag(TagPredicate<DamageType> p_270455_) {
            this.tags.add(p_270455_);
            return this;
        }

        public Builder direct(EntityPredicate p_148230_) {
            this.directEntity = p_148230_;
            return this;
        }

        public Builder direct(EntityPredicate.Builder p_25473_) {
            this.directEntity = p_25473_.build();
            return this;
        }

        public Builder source(EntityPredicate p_148234_) {
            this.sourceEntity = p_148234_;
            return this;
        }

        public Builder source(EntityPredicate.Builder p_148232_) {
            this.sourceEntity = p_148232_.build();
            return this;
        }

        public DamageSourcePredicate build() {
            return new DamageSourcePredicate(this.tags.build(), this.directEntity, this.sourceEntity);
        }
    }
}
