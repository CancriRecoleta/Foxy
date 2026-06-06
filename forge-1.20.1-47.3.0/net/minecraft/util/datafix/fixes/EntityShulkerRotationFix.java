//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class EntityShulkerRotationFix extends NamedEntityFix {
    public EntityShulkerRotationFix(Schema p_15680_) {
        super(p_15680_, false, "EntityShulkerRotationFix", References.ENTITY, "minecraft:shulker");
    }

    public Dynamic<?> fixTag(Dynamic<?> p_15684_) {
        List<Double> $$1 = p_15684_.get("Rotation").asList((p_15686_) -> {
            return p_15686_.asDouble(180.0);
        });
        if (!$$1.isEmpty()) {
            $$1.set(0, (Double)$$1.get(0) - 180.0);
            Stream var10003 = $$1.stream();
            Objects.requireNonNull(p_15684_);
            return p_15684_.set("Rotation", p_15684_.createList(var10003.map(p_15684_::createDouble)));
        } else {
            return p_15684_;
        }
    }

    protected Typed<?> fix(Typed<?> p_15682_) {
        return p_15682_.update(DSL.remainderFinder(), this::fixTag);
    }
}
