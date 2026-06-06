//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;

public class ItemCustomNameToComponentFix extends DataFix {
    public ItemCustomNameToComponentFix(Schema p_15927_, boolean p_15928_) {
        super(p_15927_, p_15928_);
    }

    private Dynamic<?> fixTag(Dynamic<?> p_15935_) {
        Optional<? extends Dynamic<?>> $$1 = p_15935_.get("display").result();
        if ($$1.isPresent()) {
            Dynamic<?> $$2 = (Dynamic)$$1.get();
            Optional<String> $$3 = $$2.get("Name").asString().result();
            if ($$3.isPresent()) {
                $$2 = $$2.set("Name", $$2.createString(Serializer.toJson(Component.literal((String)$$3.get()))));
            } else {
                Optional<String> $$4 = $$2.get("LocName").asString().result();
                if ($$4.isPresent()) {
                    $$2 = $$2.set("Name", $$2.createString(Serializer.toJson(Component.translatable((String)$$4.get()))));
                    $$2 = $$2.remove("LocName");
                }
            }

            return p_15935_.set("display", $$2);
        } else {
            return p_15935_;
        }
    }

    public TypeRewriteRule makeRule() {
        Type<?> $$0 = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder<?> $$1 = $$0.findField("tag");
        return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", $$0, (p_15931_) -> {
            return p_15931_.updateTyped($$1, (p_145384_) -> {
                return p_145384_.update(DSL.remainderFinder(), this::fixTag);
            });
        });
    }
}
