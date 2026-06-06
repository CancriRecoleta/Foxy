//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.math.NumberUtils;

public class LevelFlatGeneratorInfoFix extends DataFix {
    private static final String GENERATOR_OPTIONS = "generatorOptions";
    @VisibleForTesting
    static final String DEFAULT = "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
    private static final Splitter SPLITTER = Splitter.on(';').limit(5);
    private static final Splitter LAYER_SPLITTER = Splitter.on(',');
    private static final Splitter OLD_AMOUNT_SPLITTER = Splitter.on('x').limit(2);
    private static final Splitter AMOUNT_SPLITTER = Splitter.on('*').limit(2);
    private static final Splitter BLOCK_SPLITTER = Splitter.on(':').limit(3);

    public LevelFlatGeneratorInfoFix(Schema p_16344_, boolean p_16345_) {
        super(p_16344_, p_16345_);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("LevelFlatGeneratorInfoFix", this.getInputSchema().getType(References.LEVEL), (p_16351_) -> {
            return p_16351_.update(DSL.remainderFinder(), this::fix);
        });
    }

    private Dynamic<?> fix(Dynamic<?> p_16353_) {
        return p_16353_.get("generatorName").asString("").equalsIgnoreCase("flat") ? p_16353_.update("generatorOptions", (p_16357_) -> {
            DataResult var10000 = p_16357_.asString().map(this::fixString);
            Objects.requireNonNull(p_16357_);
            return (Dynamic)DataFixUtils.orElse(var10000.map(p_16357_::createString).result(), p_16357_);
        }) : p_16353_;
    }

    @VisibleForTesting
    String fixString(String p_16355_) {
        if (p_16355_.isEmpty()) {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
        } else {
            Iterator<String> $$1 = SPLITTER.split(p_16355_).iterator();
            String $$2 = (String)$$1.next();
            int $$5;
            String $$6;
            if ($$1.hasNext()) {
                $$5 = NumberUtils.toInt($$2, 0);
                $$6 = (String)$$1.next();
            } else {
                $$5 = 0;
                $$6 = $$2;
            }

            if ($$5 >= 0 && $$5 <= 3) {
                StringBuilder $$7 = new StringBuilder();
                Splitter $$8 = $$5 < 3 ? OLD_AMOUNT_SPLITTER : AMOUNT_SPLITTER;
                $$7.append((String)StreamSupport.stream(LAYER_SPLITTER.split($$6).spliterator(), false).map((p_16349_) -> {
                    List<String> $$3 = $$8.splitToList(p_16349_);
                    int $$6;
                    String $$7;
                    if ($$3.size() == 2) {
                        $$6 = NumberUtils.toInt((String)$$3.get(0));
                        $$7 = (String)$$3.get(1);
                    } else {
                        $$6 = 1;
                        $$7 = (String)$$3.get(0);
                    }

                    List<String> $$8x = BLOCK_SPLITTER.splitToList($$7);
                    int $$9 = ((String)$$8x.get(0)).equals("minecraft") ? 1 : 0;
                    String $$10 = (String)$$8x.get($$9);
                    int $$11 = $$5 == 3 ? EntityBlockStateFix.getBlockId("minecraft:" + $$10) : NumberUtils.toInt($$10, 0);
                    int $$12 = $$9 + 1;
                    int $$13 = $$8x.size() > $$12 ? NumberUtils.toInt((String)$$8x.get($$12), 0) : 0;
                    String var10000 = $$6 == 1 ? "" : "" + $$6 + "*";
                    return var10000 + BlockStateData.getTag($$11 << 4 | $$13).get("Name").asString("");
                }).collect(Collectors.joining(",")));

                while($$1.hasNext()) {
                    $$7.append(';').append((String)$$1.next());
                }

                return $$7.toString();
            } else {
                return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
            }
        }
    }
}
