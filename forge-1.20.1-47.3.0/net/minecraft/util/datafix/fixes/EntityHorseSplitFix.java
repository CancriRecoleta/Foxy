//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class EntityHorseSplitFix extends EntityRenameFix {
    public EntityHorseSplitFix(Schema p_15447_, boolean p_15448_) {
        super("EntityHorseSplitFix", p_15447_, p_15448_);
    }

    protected Pair<String, Typed<?>> fix(String p_15451_, Typed<?> p_15452_) {
        Dynamic<?> $$2 = (Dynamic)p_15452_.get(DSL.remainderFinder());
        if (Objects.equals("EntityHorse", p_15451_)) {
            int $$3 = $$2.get("Type").asInt(0);
            String $$8;
            switch ($$3) {
                case 0:
                default:
                    $$8 = "Horse";
                    break;
                case 1:
                    $$8 = "Donkey";
                    break;
                case 2:
                    $$8 = "Mule";
                    break;
                case 3:
                    $$8 = "ZombieHorse";
                    break;
                case 4:
                    $$8 = "SkeletonHorse";
            }

            $$2.remove("Type");
            Type<?> $$9 = (Type)this.getOutputSchema().findChoiceType(References.ENTITY).types().get($$8);
            DataResult var10001 = p_15452_.write();
            Objects.requireNonNull($$9);
            return Pair.of($$8, (Typed)((Pair)var10001.flatMap($$9::readTyped).result().orElseThrow(() -> {
                return new IllegalStateException("Could not parse the new horse");
            })).getFirst());
        } else {
            return Pair.of(p_15451_, p_15452_);
        }
    }
}
