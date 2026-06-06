//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix extends DataFix {
    public ItemWrittenBookPagesStrictJsonFix(Schema p_16164_, boolean p_16165_) {
        super(p_16164_, p_16165_);
    }

    public Dynamic<?> fixTag(Dynamic<?> p_16172_) {
        return p_16172_.update("pages", (p_16175_) -> {
            DataResult var10000 = p_16175_.asStreamOpt().map((p_145441_) -> {
                return p_145441_.map((p_145443_) -> {
                    if (!p_145443_.asString().result().isPresent()) {
                        return p_145443_;
                    } else {
                        String $$1 = p_145443_.asString("");
                        Component $$2 = null;
                        if (!"null".equals($$1) && !StringUtils.isEmpty($$1)) {
                            if ($$1.charAt(0) == '"' && $$1.charAt($$1.length() - 1) == '"' || $$1.charAt(0) == '{' && $$1.charAt($$1.length() - 1) == '}') {
                                try {
                                    $$2 = (Component)GsonHelper.fromNullableJson(BlockEntitySignTextStrictJsonFix.GSON, $$1, Component.class, true);
                                    if ($$2 == null) {
                                        $$2 = CommonComponents.EMPTY;
                                    }
                                } catch (Exception var6) {
                                }

                                if ($$2 == null) {
                                    try {
                                        $$2 = Serializer.fromJson($$1);
                                    } catch (Exception var5) {
                                    }
                                }

                                if ($$2 == null) {
                                    try {
                                        $$2 = Serializer.fromJsonLenient($$1);
                                    } catch (Exception var4) {
                                    }
                                }

                                if ($$2 == null) {
                                    $$2 = Component.literal($$1);
                                }
                            } else {
                                $$2 = Component.literal($$1);
                            }
                        } else {
                            $$2 = CommonComponents.EMPTY;
                        }

                        return p_145443_.createString(Serializer.toJson((Component)$$2));
                    }
                });
            });
            Objects.requireNonNull(p_16172_);
            return (Dynamic)DataFixUtils.orElse(var10000.map(p_16172_::createList).result(), p_16172_.emptyList());
        });
    }

    public TypeRewriteRule makeRule() {
        Type<?> $$0 = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder<?> $$1 = $$0.findField("tag");
        return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", $$0, (p_16168_) -> {
            return p_16168_.updateTyped($$1, (p_145439_) -> {
                return p_145439_.update(DSL.remainderFinder(), this::fixTag);
            });
        });
    }
}
