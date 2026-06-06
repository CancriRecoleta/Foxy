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
import com.mojang.serialization.Dynamic;

public class StructureReferenceCountFix extends DataFix {
    public StructureReferenceCountFix(Schema p_16961_, boolean p_16962_) {
        super(p_16961_, p_16962_);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> $$0 = this.getInputSchema().getType(References.STRUCTURE_FEATURE);
        return this.fixTypeEverywhereTyped("Structure Reference Fix", $$0, (p_16964_) -> {
            return p_16964_.update(DSL.remainderFinder(), StructureReferenceCountFix::setCountToAtLeastOne);
        });
    }

    private static <T> Dynamic<T> setCountToAtLeastOne(Dynamic<T> p_16966_) {
        return p_16966_.update("references", (p_16970_) -> {
            return p_16970_.createInt((Integer)p_16970_.asNumber().map(Number::intValue).result().filter((p_145724_) -> {
                return p_145724_ > 0;
            }).orElse(1));
        });
    }
}
