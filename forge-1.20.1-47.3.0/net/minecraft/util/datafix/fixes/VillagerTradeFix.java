//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class VillagerTradeFix extends NamedEntityFix {
    public VillagerTradeFix(Schema p_17116_, boolean p_17117_) {
        super(p_17116_, p_17117_, "Villager trade fix", References.ENTITY, "minecraft:villager");
    }

    protected Typed<?> fix(Typed<?> p_17143_) {
        OpticFinder<?> $$1 = p_17143_.getType().findField("Offers");
        OpticFinder<?> $$2 = $$1.type().findField("Recipes");
        Type<?> $$3 = $$2.type();
        if (!($$3 instanceof List.ListType<?> $$4)) {
            throw new IllegalStateException("Recipes are expected to be a list.");
        } else {
            Type<?> $$5 = $$4.getElement();
            OpticFinder<?> $$6 = DSL.typeFinder($$5);
            OpticFinder<?> $$7 = $$5.findField("buy");
            OpticFinder<?> $$8 = $$5.findField("buyB");
            OpticFinder<?> $$9 = $$5.findField("sell");
            OpticFinder<Pair<String, String>> $$10 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
            Function<Typed<?>, Typed<?>> $$11 = (p_17150_) -> {
                return this.updateItemStack($$10, p_17150_);
            };
            return p_17143_.updateTyped($$1, (p_17125_) -> {
                return p_17125_.updateTyped($$2, (p_145782_) -> {
                    return p_145782_.updateTyped($$6, (p_145788_) -> {
                        return p_145788_.updateTyped($$7, $$11).updateTyped($$8, $$11).updateTyped($$9, $$11);
                    });
                });
            });
        }
    }

    private Typed<?> updateItemStack(OpticFinder<Pair<String, String>> p_17134_, Typed<?> p_17135_) {
        return p_17135_.update(p_17134_, (p_17145_) -> {
            return p_17145_.mapSecond((p_145790_) -> {
                return Objects.equals(p_145790_, "minecraft:carved_pumpkin") ? "minecraft:pumpkin" : p_145790_;
            });
        });
    }
}
