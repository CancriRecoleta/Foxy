//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;

public class ObjectiveDisplayNameFix extends DataFix {
    public ObjectiveDisplayNameFix(Schema p_16521_, boolean p_16522_) {
        super(p_16521_, p_16522_);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> $$0 = this.getInputSchema().getType(References.OBJECTIVE);
        return this.fixTypeEverywhereTyped("ObjectiveDisplayNameFix", $$0, (p_181039_) -> {
            return p_181039_.update(DSL.remainderFinder(), (p_145556_) -> {
                return p_145556_.update("DisplayName", (p_145559_) -> {
                    DataResult var10000 = p_145559_.asString().map((p_145561_) -> {
                        return Serializer.toJson(Component.literal(p_145561_));
                    });
                    Objects.requireNonNull(p_145556_);
                    return (Dynamic)DataFixUtils.orElse(var10000.map(p_145556_::createString).result(), p_145559_);
                });
            });
        });
    }
}
