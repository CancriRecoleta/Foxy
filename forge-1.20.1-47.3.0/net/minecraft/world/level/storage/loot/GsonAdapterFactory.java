//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class GsonAdapterFactory {
    public GsonAdapterFactory() {
    }

    public static <E, T extends SerializerType<E>> Builder<E, T> builder(Registry<T> p_78802_, String p_78803_, String p_78804_, Function<E, T> p_78805_) {
        return new Builder(p_78802_, p_78803_, p_78804_, p_78805_);
    }

    public static class Builder<E, T extends SerializerType<E>> {
        private final Registry<T> registry;
        private final String elementName;
        private final String typeKey;
        private final Function<E, T> typeGetter;
        @Nullable
        private Pair<T, InlineSerializer<? extends E>> inlineType;
        @Nullable
        private T defaultType;

        Builder(Registry<T> p_78812_, String p_78813_, String p_78814_, Function<E, T> p_78815_) {
            this.registry = p_78812_;
            this.elementName = p_78813_;
            this.typeKey = p_78814_;
            this.typeGetter = p_78815_;
        }

        public Builder<E, T> withInlineSerializer(T p_164987_, InlineSerializer<? extends E> p_164988_) {
            this.inlineType = Pair.of(p_164987_, p_164988_);
            return this;
        }

        public Builder<E, T> withDefaultType(T p_164985_) {
            this.defaultType = p_164985_;
            return this;
        }

        public Object build() {
            return new JsonAdapter(this.registry, this.elementName, this.typeKey, this.typeGetter, this.defaultType, this.inlineType);
        }
    }

    public interface InlineSerializer<T> {
        JsonElement serialize(T var1, JsonSerializationContext var2);

        T deserialize(JsonElement var1, JsonDeserializationContext var2);
    }

    private static class JsonAdapter<E, T extends SerializerType<E>> implements JsonDeserializer<E>, JsonSerializer<E> {
        private final Registry<T> registry;
        private final String elementName;
        private final String typeKey;
        private final Function<E, T> typeGetter;
        @Nullable
        private final T defaultType;
        @Nullable
        private final Pair<T, InlineSerializer<? extends E>> inlineType;

        JsonAdapter(Registry<T> p_164995_, String p_164996_, String p_164997_, Function<E, T> p_164998_, @Nullable T p_164999_, @Nullable Pair<T, InlineSerializer<? extends E>> p_165000_) {
            this.registry = p_164995_;
            this.elementName = p_164996_;
            this.typeKey = p_164997_;
            this.typeGetter = p_164998_;
            this.defaultType = p_164999_;
            this.inlineType = p_165000_;
        }

        public E deserialize(JsonElement p_78848_, Type p_78849_, JsonDeserializationContext p_78850_) throws JsonParseException {
            if (p_78848_.isJsonObject()) {
                JsonObject $$3 = GsonHelper.convertToJsonObject(p_78848_, this.elementName);
                String $$4 = GsonHelper.getAsString($$3, this.typeKey, "");
                SerializerType $$7;
                if ($$4.isEmpty()) {
                    $$7 = this.defaultType;
                } else {
                    ResourceLocation $$6 = new ResourceLocation($$4);
                    $$7 = (SerializerType)this.registry.get($$6);
                }

                if ($$7 == null) {
                    throw new JsonSyntaxException("Unknown type '" + $$4 + "'");
                } else {
                    return $$7.getSerializer().deserialize($$3, p_78850_);
                }
            } else if (this.inlineType == null) {
                throw new UnsupportedOperationException("Object " + p_78848_ + " can't be deserialized");
            } else {
                return ((InlineSerializer)this.inlineType.getSecond()).deserialize(p_78848_, p_78850_);
            }
        }

        public JsonElement serialize(E p_78852_, Type p_78853_, JsonSerializationContext p_78854_) {
            T $$3 = (SerializerType)this.typeGetter.apply(p_78852_);
            if (this.inlineType != null && this.inlineType.getFirst() == $$3) {
                return ((InlineSerializer)this.inlineType.getSecond()).serialize(p_78852_, p_78854_);
            } else if ($$3 == null) {
                throw new JsonSyntaxException("Unknown type: " + p_78852_);
            } else {
                JsonObject $$4 = new JsonObject();
                $$4.addProperty(this.typeKey, this.registry.getKey($$3).toString());
                $$3.getSerializer().serialize($$4, p_78852_, p_78854_);
                return $$4;
            }
        }
    }
}
