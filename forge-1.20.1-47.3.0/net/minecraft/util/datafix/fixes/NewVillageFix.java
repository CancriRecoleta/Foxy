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
import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class NewVillageFix extends DataFix {
    public NewVillageFix(Schema p_16476_, boolean p_16477_) {
        super(p_16476_, p_16477_);
    }

    protected TypeRewriteRule makeRule() {
        CompoundList.CompoundListType<String, ?> $$0 = DSL.compoundList(DSL.string(), this.getInputSchema().getType(References.STRUCTURE_FEATURE));
        OpticFinder<? extends List<? extends Pair<String, ?>>> $$1 = $$0.finder();
        return this.cap($$0);
    }

    private <SF> TypeRewriteRule cap(CompoundList.CompoundListType<String, SF> p_16499_) {
        Type<?> $$1 = this.getInputSchema().getType(References.CHUNK);
        Type<?> $$2 = this.getInputSchema().getType(References.STRUCTURE_FEATURE);
        OpticFinder<?> $$3 = $$1.findField("Level");
        OpticFinder<?> $$4 = $$3.type().findField("Structures");
        OpticFinder<?> $$5 = $$4.type().findField("Starts");
        OpticFinder<List<Pair<String, SF>>> $$6 = p_16499_.finder();
        return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("NewVillageFix", $$1, (p_16483_) -> {
            return p_16483_.updateTyped($$3, (p_145526_) -> {
                return p_145526_.updateTyped($$4, (p_145530_) -> {
                    return p_145530_.updateTyped($$5, (p_145533_) -> {
                        return p_145533_.update($$6, (p_145544_) -> {
                            return (List)p_145544_.stream().filter((p_145546_) -> {
                                return !Objects.equals(p_145546_.getFirst(), "Village");
                            }).map((p_145535_) -> {
                                return p_145535_.mapFirst((p_145542_) -> {
                                    return p_145542_.equals("New_Village") ? "Village" : p_145542_;
                                });
                            }).collect(Collectors.toList());
                        });
                    }).update(DSL.remainderFinder(), (p_145550_) -> {
                        return p_145550_.update("References", (p_145552_) -> {
                            Optional<? extends Dynamic<?>> $$1 = p_145552_.get("New_Village").result();
                            return ((Dynamic)DataFixUtils.orElse($$1.map((p_145540_) -> {
                                return p_145552_.remove("New_Village").set("Village", p_145540_);
                            }), p_145552_)).remove("Village");
                        });
                    });
                });
            });
        }), this.fixTypeEverywhereTyped("NewVillageStartFix", $$2, (p_16497_) -> {
            return p_16497_.update(DSL.remainderFinder(), (p_145537_) -> {
                return p_145537_.update("id", (p_145548_) -> {
                    return Objects.equals(NamespacedSchema.ensureNamespaced(p_145548_.asString("")), "minecraft:new_village") ? p_145548_.createString("minecraft:village") : p_145548_;
                });
            });
        }));
    }
}
