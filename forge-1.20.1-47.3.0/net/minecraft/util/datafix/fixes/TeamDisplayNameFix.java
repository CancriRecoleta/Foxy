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
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;

public class TeamDisplayNameFix extends DataFix {
    public TeamDisplayNameFix(Schema p_17001_, boolean p_17002_) {
        super(p_17001_, p_17002_);
    }

    protected TypeRewriteRule makeRule() {
        Type<Pair<String, Dynamic<?>>> $$0 = DSL.named(References.TEAM.typeName(), DSL.remainderType());
        if (!Objects.equals($$0, this.getInputSchema().getType(References.TEAM))) {
            throw new IllegalStateException("Team type is not what was expected.");
        } else {
            return this.fixTypeEverywhere("TeamDisplayNameFix", $$0, (p_17011_) -> {
                return (p_145726_) -> {
                    return p_145726_.mapSecond((p_145728_) -> {
                        return p_145728_.update("DisplayName", (p_145731_) -> {
                            DataResult var10000 = p_145731_.asString().map((p_145733_) -> {
                                return Serializer.toJson(Component.literal(p_145733_));
                            });
                            Objects.requireNonNull(p_145728_);
                            return (Dynamic)DataFixUtils.orElse(var10000.map(p_145728_::createString).result(), p_145731_);
                        });
                    });
                };
            });
        }
    }
}
