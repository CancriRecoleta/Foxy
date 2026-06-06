//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;

public class MapIdFix extends DataFix {
    public MapIdFix(Schema p_16396_, boolean p_16397_) {
        super(p_16396_, p_16397_);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> $$0 = this.getInputSchema().getType(References.SAVED_DATA);
        OpticFinder<?> $$1 = $$0.findField("data");
        return this.fixTypeEverywhereTyped("Map id fix", $$0, (p_16400_) -> {
            Optional<? extends Typed<?>> $$2 = p_16400_.getOptionalTyped($$1);
            return $$2.isPresent() ? p_16400_ : p_16400_.update(DSL.remainderFinder(), (p_145512_) -> {
                return p_145512_.createMap(ImmutableMap.of(p_145512_.createString("data"), p_145512_));
            });
        });
    }
}
