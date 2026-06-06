//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.UUID;

public class EntityStringUuidFix extends DataFix {
    public EntityStringUuidFix(Schema p_15694_, boolean p_15695_) {
        super(p_15694_, p_15695_);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("EntityStringUuidFix", this.getInputSchema().getType(References.ENTITY), (p_15697_) -> {
            return p_15697_.update(DSL.remainderFinder(), (p_145331_) -> {
                Optional<String> $$1 = p_145331_.get("UUID").asString().result();
                if ($$1.isPresent()) {
                    UUID $$2 = UUID.fromString((String)$$1.get());
                    return p_145331_.remove("UUID").set("UUIDMost", p_145331_.createLong($$2.getMostSignificantBits())).set("UUIDLeast", p_145331_.createLong($$2.getLeastSignificantBits()));
                } else {
                    return p_145331_;
                }
            });
        });
    }
}
