//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;

public class ObjectiveRenderTypeFix extends DataFix {
    public ObjectiveRenderTypeFix(Schema p_16536_, boolean p_16537_) {
        super(p_16536_, p_16537_);
    }

    private static String getRenderType(String p_262957_) {
        return p_262957_.equals("health") ? "hearts" : "integer";
    }

    protected TypeRewriteRule makeRule() {
        Type<?> $$0 = this.getInputSchema().getType(References.OBJECTIVE);
        return this.fixTypeEverywhereTyped("ObjectiveRenderTypeFix", $$0, (p_181041_) -> {
            return p_181041_.update(DSL.remainderFinder(), (p_145565_) -> {
                Optional<String> $$1 = p_145565_.get("RenderType").asString().result();
                if ($$1.isEmpty()) {
                    String $$2 = p_145565_.get("CriteriaName").asString("");
                    String $$3 = getRenderType($$2);
                    return p_145565_.set("RenderType", p_145565_.createString($$3));
                } else {
                    return p_145565_;
                }
            });
        });
    }
}
