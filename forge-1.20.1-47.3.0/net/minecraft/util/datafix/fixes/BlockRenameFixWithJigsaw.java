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
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class BlockRenameFixWithJigsaw extends BlockRenameFix {
    private final String name;

    public BlockRenameFixWithJigsaw(Schema p_145150_, String p_145151_) {
        super(p_145150_, p_145151_);
        this.name = p_145151_;
    }

    public TypeRewriteRule makeRule() {
        DSL.TypeReference $$0 = References.BLOCK_ENTITY;
        String $$1 = "minecraft:jigsaw";
        OpticFinder<?> $$2 = DSL.namedChoice("minecraft:jigsaw", this.getInputSchema().getChoiceType($$0, "minecraft:jigsaw"));
        TypeRewriteRule $$3 = this.fixTypeEverywhereTyped(this.name + " for jigsaw state", this.getInputSchema().getType($$0), this.getOutputSchema().getType($$0), (p_145155_) -> {
            return p_145155_.updateTyped($$2, this.getOutputSchema().getChoiceType($$0, "minecraft:jigsaw"), (p_145157_) -> {
                return p_145157_.update(DSL.remainderFinder(), (p_145159_) -> {
                    return p_145159_.update("final_state", (p_145162_) -> {
                        Optional var10000 = p_145162_.asString().result().map((p_145168_) -> {
                            int $$1 = p_145168_.indexOf(91);
                            int $$2 = p_145168_.indexOf(123);
                            int $$3 = p_145168_.length();
                            if ($$1 > 0) {
                                $$3 = Math.min($$3, $$1);
                            }

                            if ($$2 > 0) {
                                $$3 = Math.min($$3, $$2);
                            }

                            String $$4 = p_145168_.substring(0, $$3);
                            String $$5 = this.fixBlock($$4);
                            return $$5 + p_145168_.substring($$3);
                        });
                        Objects.requireNonNull(p_145159_);
                        return (Dynamic)DataFixUtils.orElse(var10000.map(p_145159_::createString), p_145162_);
                    });
                });
            });
        });
        return TypeRewriteRule.seq(super.makeRule(), $$3);
    }

    public static DataFix create(Schema p_145164_, String p_145165_, final Function<String, String> p_145166_) {
        return new BlockRenameFixWithJigsaw(p_145164_, p_145165_) {
            protected String fixBlock(String p_145176_) {
                return (String)p_145166_.apply(p_145176_);
            }
        };
    }
}
