//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

public abstract class EntityTypePredicate {
    public static final EntityTypePredicate ANY = new EntityTypePredicate() {
        public boolean matches(EntityType<?> p_37652_) {
            return true;
        }

        public JsonElement serializeToJson() {
            return JsonNull.INSTANCE;
        }
    };
    private static final Joiner COMMA_JOINER = Joiner.on(", ");

    public EntityTypePredicate() {
    }

    public abstract boolean matches(EntityType<?> var1);

    public abstract JsonElement serializeToJson();

    public static EntityTypePredicate fromJson(@Nullable JsonElement p_37644_) {
        if (p_37644_ != null && !p_37644_.isJsonNull()) {
            String $$1 = GsonHelper.convertToString(p_37644_, "type");
            ResourceLocation $$3;
            if ($$1.startsWith("#")) {
                $$3 = new ResourceLocation($$1.substring(1));
                return new TagPredicate(TagKey.create(Registries.ENTITY_TYPE, $$3));
            } else {
                $$3 = new ResourceLocation($$1);
                EntityType<?> $$4 = (EntityType)BuiltInRegistries.ENTITY_TYPE.getOptional($$3).orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown entity type '" + $$3 + "', valid types are: " + COMMA_JOINER.join(BuiltInRegistries.ENTITY_TYPE.keySet()));
                });
                return new TypePredicate($$4);
            }
        } else {
            return ANY;
        }
    }

    public static EntityTypePredicate of(EntityType<?> p_37648_) {
        return new TypePredicate(p_37648_);
    }

    public static EntityTypePredicate of(TagKey<EntityType<?>> p_204082_) {
        return new TagPredicate(p_204082_);
    }

    private static class TagPredicate extends EntityTypePredicate {
        private final TagKey<EntityType<?>> tag;

        public TagPredicate(TagKey<EntityType<?>> p_204084_) {
            this.tag = p_204084_;
        }

        public boolean matches(EntityType<?> p_37658_) {
            return p_37658_.is(this.tag);
        }

        public JsonElement serializeToJson() {
            return new JsonPrimitive("#" + this.tag.location());
        }
    }

    private static class TypePredicate extends EntityTypePredicate {
        private final EntityType<?> type;

        public TypePredicate(EntityType<?> p_37661_) {
            this.type = p_37661_;
        }

        public boolean matches(EntityType<?> p_37664_) {
            return this.type == p_37664_;
        }

        public JsonElement serializeToJson() {
            return new JsonPrimitive(BuiltInRegistries.ENTITY_TYPE.getKey(this.type).toString());
        }
    }
}
