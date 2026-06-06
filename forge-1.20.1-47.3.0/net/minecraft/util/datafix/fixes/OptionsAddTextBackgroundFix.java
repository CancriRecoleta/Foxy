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

public class OptionsAddTextBackgroundFix extends DataFix {
    public OptionsAddTextBackgroundFix(Schema p_16607_, boolean p_16608_) {
        super(p_16607_, p_16608_);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("OptionsAddTextBackgroundFix", this.getInputSchema().getType(References.OPTIONS), (p_16610_) -> {
            return p_16610_.update(DSL.remainderFinder(), (p_145567_) -> {
                return (Dynamic)DataFixUtils.orElse(p_145567_.get("chatOpacity").asString().map((p_145570_) -> {
                    return p_145567_.set("textBackgroundOpacity", p_145567_.createDouble(this.calculateBackground(p_145570_)));
                }).result(), p_145567_);
            });
        });
    }

    private double calculateBackground(String p_16617_) {
        try {
            double $$1 = 0.9 * Double.parseDouble(p_16617_) + 0.1;
            return $$1 / 2.0;
        } catch (NumberFormatException var4) {
            return 0.5;
        }
    }
}
