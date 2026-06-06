//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChunkToProtochunkFix extends DataFix {
    private static final int NUM_SECTIONS = 16;

    public ChunkToProtochunkFix(Schema p_15285_, boolean p_15286_) {
        super(p_15285_, p_15286_);
    }

    public TypeRewriteRule makeRule() {
        return this.writeFixAndRead("ChunkToProtoChunkFix", this.getInputSchema().getType(References.CHUNK), this.getOutputSchema().getType(References.CHUNK), (p_199886_) -> {
            return p_199886_.update("Level", ChunkToProtochunkFix::fixChunkData);
        });
    }

    private static <T> Dynamic<T> fixChunkData(Dynamic<T> p_199856_) {
        boolean $$1 = p_199856_.get("TerrainPopulated").asBoolean(false);
        boolean $$2 = p_199856_.get("LightPopulated").asNumber().result().isEmpty() || p_199856_.get("LightPopulated").asBoolean(false);
        String $$5;
        if ($$1) {
            if ($$2) {
                $$5 = "mobs_spawned";
            } else {
                $$5 = "decorated";
            }
        } else {
            $$5 = "carved";
        }

        return repackTicks(repackBiomes(p_199856_)).set("Status", p_199856_.createString($$5)).set("hasLegacyStructureData", p_199856_.createBoolean(true));
    }

    private static <T> Dynamic<T> repackBiomes(Dynamic<T> p_199880_) {
        return p_199880_.update("Biomes", (p_199862_) -> {
            return (Dynamic)DataFixUtils.orElse(p_199862_.asByteBufferOpt().result().map((p_199868_) -> {
                int[] $$2 = new int[256];

                for(int $$3 = 0; $$3 < $$2.length; ++$$3) {
                    if ($$3 < p_199868_.capacity()) {
                        $$2[$$3] = p_199868_.get($$3) & 255;
                    }
                }

                return p_199880_.createIntList(Arrays.stream($$2));
            }), p_199862_);
        });
    }

    private static <T> Dynamic<T> repackTicks(Dynamic<T> p_199882_) {
        return (Dynamic)DataFixUtils.orElse(p_199882_.get("TileTicks").asStreamOpt().result().map((p_199871_) -> {
            List<ShortList> $$2 = (List)IntStream.range(0, 16).mapToObj((p_199850_) -> {
                return new ShortArrayList();
            }).collect(Collectors.toList());
            p_199871_.forEach((p_199874_) -> {
                int $$2x = p_199874_.get("x").asInt(0);
                int $$3 = p_199874_.get("y").asInt(0);
                int $$4 = p_199874_.get("z").asInt(0);
                short $$5 = packOffsetCoordinates($$2x, $$3, $$4);
                ((ShortList)$$2.get($$3 >> 4)).add($$5);
            });
            return p_199882_.remove("TileTicks").set("ToBeTicked", p_199882_.createList($$2.stream().map((p_199865_) -> {
                return p_199882_.createList(p_199865_.intStream().mapToObj((p_199859_) -> {
                    return p_199882_.createShort((short)p_199859_);
                }));
            })));
        }), p_199882_);
    }

    private static short packOffsetCoordinates(int p_15291_, int p_15292_, int p_15293_) {
        return (short)(p_15291_ & 15 | (p_15292_ & 15) << 4 | (p_15293_ & 15) << 8);
    }
}
