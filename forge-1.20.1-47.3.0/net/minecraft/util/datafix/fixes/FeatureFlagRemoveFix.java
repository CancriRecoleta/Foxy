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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FeatureFlagRemoveFix extends DataFix {
    private final String name;
    private final Set<String> flagsToRemove;

    public FeatureFlagRemoveFix(Schema p_277930_, String p_277628_, Set<String> p_277886_) {
        super(p_277930_, false);
        this.name = p_277628_;
        this.flagsToRemove = p_277886_;
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(References.LEVEL), (p_277407_) -> {
            return p_277407_.update(DSL.remainderFinder(), this::fixTag);
        });
    }

    private <T> Dynamic<T> fixTag(Dynamic<T> p_277583_) {
        List<Dynamic<T>> $$1 = (List)p_277583_.get("removed_features").asStream().collect(Collectors.toCollection(ArrayList::new));
        Dynamic<T> $$2 = p_277583_.update("enabled_features", (p_278098_) -> {
            Optional var10000 = p_278098_.asStreamOpt().result().map((p_277400_) -> {
                return p_277400_.filter((p_277338_) -> {
                    Optional<String> $$3 = p_277338_.asString().result();
                    if ($$3.isEmpty()) {
                        return true;
                    } else {
                        boolean $$4 = this.flagsToRemove.contains($$3.get());
                        if ($$4) {
                            $$1.add(p_277583_.createString((String)$$3.get()));
                        }

                        return !$$4;
                    }
                });
            });
            Objects.requireNonNull(p_277583_);
            return (Dynamic)DataFixUtils.orElse(var10000.map(p_277583_::createList), p_278098_);
        });
        if (!$$1.isEmpty()) {
            $$2 = $$2.set("removed_features", p_277583_.createList($$1.stream()));
        }

        return $$2;
    }
}
