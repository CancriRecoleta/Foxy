//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.lang.reflect.Type;
import java.util.Iterator;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

public class BlockEntitySignTextStrictJsonFix extends NamedEntityFix {
    public static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(Component.class, new JsonDeserializer<Component>() {
        public MutableComponent deserialize(JsonElement p_14875_, Type p_14876_, JsonDeserializationContext p_14877_) throws JsonParseException {
            if (p_14875_.isJsonPrimitive()) {
                return Component.literal(p_14875_.getAsString());
            } else if (p_14875_.isJsonArray()) {
                JsonArray $$3 = p_14875_.getAsJsonArray();
                MutableComponent $$4 = null;
                Iterator var6 = $$3.iterator();

                while(var6.hasNext()) {
                    JsonElement $$5 = (JsonElement)var6.next();
                    MutableComponent $$6 = this.deserialize($$5, $$5.getClass(), p_14877_);
                    if ($$4 == null) {
                        $$4 = $$6;
                    } else {
                        $$4.append((Component)$$6);
                    }
                }

                return $$4;
            } else {
                throw new JsonParseException("Don't know how to turn " + p_14875_ + " into a Component");
            }
        }
    }).create();

    public BlockEntitySignTextStrictJsonFix(Schema p_14864_, boolean p_14865_) {
        super(p_14864_, p_14865_, "BlockEntitySignTextStrictJsonFix", References.BLOCK_ENTITY, "Sign");
    }

    private Dynamic<?> updateLine(Dynamic<?> p_14871_, String p_14872_) {
        String $$2 = p_14871_.get(p_14872_).asString("");
        Component $$3 = null;
        if (!"null".equals($$2) && !StringUtils.isEmpty($$2)) {
            if ($$2.charAt(0) == '"' && $$2.charAt($$2.length() - 1) == '"' || $$2.charAt(0) == '{' && $$2.charAt($$2.length() - 1) == '}') {
                try {
                    $$3 = (Component)GsonHelper.fromNullableJson(GSON, $$2, Component.class, true);
                    if ($$3 == null) {
                        $$3 = CommonComponents.EMPTY;
                    }
                } catch (Exception var8) {
                }

                if ($$3 == null) {
                    try {
                        $$3 = Serializer.fromJson($$2);
                    } catch (Exception var7) {
                    }
                }

                if ($$3 == null) {
                    try {
                        $$3 = Serializer.fromJsonLenient($$2);
                    } catch (Exception var6) {
                    }
                }

                if ($$3 == null) {
                    $$3 = Component.literal($$2);
                }
            } else {
                $$3 = Component.literal($$2);
            }
        } else {
            $$3 = CommonComponents.EMPTY;
        }

        return p_14871_.set(p_14872_, p_14871_.createString(Serializer.toJson((Component)$$3)));
    }

    protected Typed<?> fix(Typed<?> p_14867_) {
        return p_14867_.update(DSL.remainderFinder(), (p_14869_) -> {
            p_14869_ = this.updateLine(p_14869_, "Text1");
            p_14869_ = this.updateLine(p_14869_, "Text2");
            p_14869_ = this.updateLine(p_14869_, "Text3");
            p_14869_ = this.updateLine(p_14869_, "Text4");
            return p_14869_;
        });
    }
}
