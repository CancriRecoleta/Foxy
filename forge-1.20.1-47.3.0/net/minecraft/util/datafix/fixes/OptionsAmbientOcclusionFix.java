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
import com.mojang.serialization.Dynamic;

public class OptionsAmbientOcclusionFix extends DataFix {
    public OptionsAmbientOcclusionFix(Schema p_263497_) {
        super(p_263497_, false);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("OptionsAmbientOcclusionFix", this.getInputSchema().getType(References.OPTIONS), (p_263493_) -> {
            return p_263493_.update(DSL.remainderFinder(), (p_263531_) -> {
                return (Dynamic)DataFixUtils.orElse(p_263531_.get("ao").asString().map((p_263546_) -> {
                    return p_263531_.set("ao", p_263531_.createString(updateValue(p_263546_)));
                }).result(), p_263531_);
            });
        });
    }

    private static String updateValue(String p_263541_) {
        String var10000;
        switch (p_263541_) {
            case "0":
                var10000 = "false";
                break;
            case "1":
            case "2":
                var10000 = "true";
                break;
            default:
                var10000 = p_263541_;
        }

        return var10000;
    }
}
