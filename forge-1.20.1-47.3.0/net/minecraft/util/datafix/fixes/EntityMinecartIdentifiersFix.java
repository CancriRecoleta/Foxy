//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;

public class EntityMinecartIdentifiersFix extends DataFix {
    private static final List<String> MINECART_BY_ID = Lists.newArrayList(new String[]{"MinecartRideable", "MinecartChest", "MinecartFurnace"});

    public EntityMinecartIdentifiersFix(Schema p_15479_, boolean p_15480_) {
        super(p_15479_, p_15480_);
    }

    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType<String> $$0 = this.getInputSchema().findChoiceType(References.ENTITY);
        TaggedChoice.TaggedChoiceType<String> $$1 = this.getOutputSchema().findChoiceType(References.ENTITY);
        return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", $$0, $$1, (p_15485_) -> {
            return (p_145290_) -> {
                if (!Objects.equals(p_145290_.getFirst(), "Minecart")) {
                    return p_145290_;
                } else {
                    Typed<? extends Pair<String, ?>> $$4 = (Typed)$$0.point(p_15485_, "Minecart", p_145290_.getSecond()).orElseThrow(IllegalStateException::new);
                    Dynamic<?> $$5 = (Dynamic)$$4.getOrCreate(DSL.remainderFinder());
                    int $$6 = $$5.get("Type").asInt(0);
                    String $$8;
                    if ($$6 > 0 && $$6 < MINECART_BY_ID.size()) {
                        $$8 = (String)MINECART_BY_ID.get($$6);
                    } else {
                        $$8 = "MinecartRideable";
                    }

                    return Pair.of($$8, (DataResult)$$4.write().map((p_145294_) -> {
                        return ((Type)$$1.types().get($$8)).read(p_145294_);
                    }).result().orElseThrow(() -> {
                        return new IllegalStateException("Could not read the new minecart.");
                    }));
                }
            };
        });
    }
}
