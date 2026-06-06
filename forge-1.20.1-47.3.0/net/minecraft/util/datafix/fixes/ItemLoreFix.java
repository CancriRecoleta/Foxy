//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;

public class ItemLoreFix extends DataFix {
    public ItemLoreFix(Schema p_15958_, boolean p_15959_) {
        super(p_15958_, p_15959_);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> $$0 = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder<?> $$1 = $$0.findField("tag");
        return this.fixTypeEverywhereTyped("Item Lore componentize", $$0, (p_15962_) -> {
            return p_15962_.updateTyped($$1, (p_145392_) -> {
                return p_145392_.update(DSL.remainderFinder(), (p_145394_) -> {
                    return p_145394_.update("display", (p_145396_) -> {
                        return p_145396_.update("Lore", (p_145398_) -> {
                            DataResult var10000 = p_145398_.asStreamOpt().map(ItemLoreFix::fixLoreList);
                            Objects.requireNonNull(p_145398_);
                            return (Dynamic)DataFixUtils.orElse(var10000.map(p_145398_::createList).result(), p_145398_);
                        });
                    });
                });
            });
        });
    }

    private static <T> Stream<Dynamic<T>> fixLoreList(Stream<Dynamic<T>> p_15970_) {
        return p_15970_.map((p_15966_) -> {
            DataResult var10000 = p_15966_.asString().map(ItemLoreFix::fixLoreEntry);
            Objects.requireNonNull(p_15966_);
            return (Dynamic)DataFixUtils.orElse(var10000.map(p_15966_::createString).result(), p_15966_);
        });
    }

    private static String fixLoreEntry(String p_15968_) {
        return Serializer.toJson(Component.literal(p_15968_));
    }
}
