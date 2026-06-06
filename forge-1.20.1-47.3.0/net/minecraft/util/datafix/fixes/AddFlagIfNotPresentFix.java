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
import com.mojang.serialization.Dynamic;

public class AddFlagIfNotPresentFix extends DataFix {
    private final String name;
    private final boolean flagValue;
    private final String flagKey;
    private final DSL.TypeReference typeReference;

    public AddFlagIfNotPresentFix(Schema p_184810_, DSL.TypeReference p_184811_, String p_184812_, boolean p_184813_) {
        super(p_184810_, true);
        this.flagValue = p_184813_;
        this.flagKey = p_184812_;
        String var10001 = this.flagKey;
        this.name = "AddFlagIfNotPresentFix_" + var10001 + "=" + this.flagValue + " for " + p_184810_.getVersionKey();
        this.typeReference = p_184811_;
    }

    protected TypeRewriteRule makeRule() {
        Type<?> $$0 = this.getInputSchema().getType(this.typeReference);
        return this.fixTypeEverywhereTyped(this.name, $$0, (p_184815_) -> {
            return p_184815_.update(DSL.remainderFinder(), (p_184817_) -> {
                return p_184817_.set(this.flagKey, (Dynamic)DataFixUtils.orElseGet(p_184817_.get(this.flagKey).result(), () -> {
                    return p_184817_.createBoolean(this.flagValue);
                }));
            });
        });
    }
}
