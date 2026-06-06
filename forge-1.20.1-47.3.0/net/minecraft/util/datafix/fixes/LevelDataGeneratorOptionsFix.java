//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;

public class LevelDataGeneratorOptionsFix extends DataFix {
    static final Map<String, String> MAP = (Map)Util.make(Maps.newHashMap(), (p_16330_) -> {
        p_16330_.put("0", "minecraft:ocean");
        p_16330_.put("1", "minecraft:plains");
        p_16330_.put("2", "minecraft:desert");
        p_16330_.put("3", "minecraft:mountains");
        p_16330_.put("4", "minecraft:forest");
        p_16330_.put("5", "minecraft:taiga");
        p_16330_.put("6", "minecraft:swamp");
        p_16330_.put("7", "minecraft:river");
        p_16330_.put("8", "minecraft:nether");
        p_16330_.put("9", "minecraft:the_end");
        p_16330_.put("10", "minecraft:frozen_ocean");
        p_16330_.put("11", "minecraft:frozen_river");
        p_16330_.put("12", "minecraft:snowy_tundra");
        p_16330_.put("13", "minecraft:snowy_mountains");
        p_16330_.put("14", "minecraft:mushroom_fields");
        p_16330_.put("15", "minecraft:mushroom_field_shore");
        p_16330_.put("16", "minecraft:beach");
        p_16330_.put("17", "minecraft:desert_hills");
        p_16330_.put("18", "minecraft:wooded_hills");
        p_16330_.put("19", "minecraft:taiga_hills");
        p_16330_.put("20", "minecraft:mountain_edge");
        p_16330_.put("21", "minecraft:jungle");
        p_16330_.put("22", "minecraft:jungle_hills");
        p_16330_.put("23", "minecraft:jungle_edge");
        p_16330_.put("24", "minecraft:deep_ocean");
        p_16330_.put("25", "minecraft:stone_shore");
        p_16330_.put("26", "minecraft:snowy_beach");
        p_16330_.put("27", "minecraft:birch_forest");
        p_16330_.put("28", "minecraft:birch_forest_hills");
        p_16330_.put("29", "minecraft:dark_forest");
        p_16330_.put("30", "minecraft:snowy_taiga");
        p_16330_.put("31", "minecraft:snowy_taiga_hills");
        p_16330_.put("32", "minecraft:giant_tree_taiga");
        p_16330_.put("33", "minecraft:giant_tree_taiga_hills");
        p_16330_.put("34", "minecraft:wooded_mountains");
        p_16330_.put("35", "minecraft:savanna");
        p_16330_.put("36", "minecraft:savanna_plateau");
        p_16330_.put("37", "minecraft:badlands");
        p_16330_.put("38", "minecraft:wooded_badlands_plateau");
        p_16330_.put("39", "minecraft:badlands_plateau");
        p_16330_.put("40", "minecraft:small_end_islands");
        p_16330_.put("41", "minecraft:end_midlands");
        p_16330_.put("42", "minecraft:end_highlands");
        p_16330_.put("43", "minecraft:end_barrens");
        p_16330_.put("44", "minecraft:warm_ocean");
        p_16330_.put("45", "minecraft:lukewarm_ocean");
        p_16330_.put("46", "minecraft:cold_ocean");
        p_16330_.put("47", "minecraft:deep_warm_ocean");
        p_16330_.put("48", "minecraft:deep_lukewarm_ocean");
        p_16330_.put("49", "minecraft:deep_cold_ocean");
        p_16330_.put("50", "minecraft:deep_frozen_ocean");
        p_16330_.put("127", "minecraft:the_void");
        p_16330_.put("129", "minecraft:sunflower_plains");
        p_16330_.put("130", "minecraft:desert_lakes");
        p_16330_.put("131", "minecraft:gravelly_mountains");
        p_16330_.put("132", "minecraft:flower_forest");
        p_16330_.put("133", "minecraft:taiga_mountains");
        p_16330_.put("134", "minecraft:swamp_hills");
        p_16330_.put("140", "minecraft:ice_spikes");
        p_16330_.put("149", "minecraft:modified_jungle");
        p_16330_.put("151", "minecraft:modified_jungle_edge");
        p_16330_.put("155", "minecraft:tall_birch_forest");
        p_16330_.put("156", "minecraft:tall_birch_hills");
        p_16330_.put("157", "minecraft:dark_forest_hills");
        p_16330_.put("158", "minecraft:snowy_taiga_mountains");
        p_16330_.put("160", "minecraft:giant_spruce_taiga");
        p_16330_.put("161", "minecraft:giant_spruce_taiga_hills");
        p_16330_.put("162", "minecraft:modified_gravelly_mountains");
        p_16330_.put("163", "minecraft:shattered_savanna");
        p_16330_.put("164", "minecraft:shattered_savanna_plateau");
        p_16330_.put("165", "minecraft:eroded_badlands");
        p_16330_.put("166", "minecraft:modified_wooded_badlands_plateau");
        p_16330_.put("167", "minecraft:modified_badlands_plateau");
    });
    public static final String GENERATOR_OPTIONS = "generatorOptions";

    public LevelDataGeneratorOptionsFix(Schema p_16309_, boolean p_16310_) {
        super(p_16309_, p_16310_);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> $$0 = this.getOutputSchema().getType(References.LEVEL);
        return this.fixTypeEverywhereTyped("LevelDataGeneratorOptionsFix", this.getInputSchema().getType(References.LEVEL), $$0, (p_16314_) -> {
            return (Typed)p_16314_.write().flatMap((p_145484_) -> {
                Optional<String> $$2 = p_145484_.get("generatorOptions").asString().result();
                Dynamic $$7;
                if ("flat".equalsIgnoreCase(p_145484_.get("generatorName").asString(""))) {
                    String $$3 = (String)$$2.orElse("");
                    $$7 = p_145484_.set("generatorOptions", convert($$3, p_145484_.getOps()));
                } else if ("buffet".equalsIgnoreCase(p_145484_.get("generatorName").asString("")) && $$2.isPresent()) {
                    Dynamic<JsonElement> $$5 = new Dynamic(JsonOps.INSTANCE, GsonHelper.parse((String)$$2.get(), true));
                    $$7 = p_145484_.set("generatorOptions", $$5.convert(p_145484_.getOps()));
                } else {
                    $$7 = p_145484_;
                }

                return $$0.readTyped($$7);
            }).map(Pair::getFirst).result().orElseThrow(() -> {
                return new IllegalStateException("Could not read new level type.");
            });
        });
    }

    private static <T> Dynamic<T> convert(String p_16327_, DynamicOps<T> p_16328_) {
        Iterator<String> $$2 = Splitter.on(';').split(p_16327_).iterator();
        String $$3 = "minecraft:plains";
        Map<String, Map<String, String>> $$4 = Maps.newHashMap();
        Object $$12;
        if (!p_16327_.isEmpty() && $$2.hasNext()) {
            $$12 = getLayersInfoFromString((String)$$2.next());
            if (!((List)$$12).isEmpty()) {
                if ($$2.hasNext()) {
                    $$3 = (String)MAP.getOrDefault($$2.next(), "minecraft:plains");
                }

                if ($$2.hasNext()) {
                    String[] $$6 = ((String)$$2.next()).toLowerCase(Locale.ROOT).split(",");
                    String[] var7 = $$6;
                    int var8 = $$6.length;

                    for(int var9 = 0; var9 < var8; ++var9) {
                        String $$7 = var7[var9];
                        String[] $$8 = $$7.split("\\(", 2);
                        if (!$$8[0].isEmpty()) {
                            $$4.put($$8[0], Maps.newHashMap());
                            if ($$8.length > 1 && $$8[1].endsWith(")") && $$8[1].length() > 1) {
                                String[] $$9 = $$8[1].substring(0, $$8[1].length() - 1).split(" ");
                                String[] var13 = $$9;
                                int var14 = $$9.length;

                                for(int var15 = 0; var15 < var14; ++var15) {
                                    String $$10 = var13[var15];
                                    String[] $$11 = $$10.split("=", 2);
                                    if ($$11.length == 2) {
                                        ((Map)$$4.get($$8[0])).put($$11[0], $$11[1]);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    $$4.put("village", Maps.newHashMap());
                }
            }
        } else {
            $$12 = Lists.newArrayList();
            ((List)$$12).add(Pair.of(1, "minecraft:bedrock"));
            ((List)$$12).add(Pair.of(2, "minecraft:dirt"));
            ((List)$$12).add(Pair.of(1, "minecraft:grass_block"));
            $$4.put("village", Maps.newHashMap());
        }

        T $$13 = p_16328_.createList(((List)$$12).stream().map((p_16320_) -> {
            return p_16328_.createMap(ImmutableMap.of(p_16328_.createString("height"), p_16328_.createInt((Integer)p_16320_.getFirst()), p_16328_.createString("block"), p_16328_.createString((String)p_16320_.getSecond())));
        }));
        T $$14 = p_16328_.createMap((Map)$$4.entrySet().stream().map((p_16323_) -> {
            return Pair.of(p_16328_.createString(((String)p_16323_.getKey()).toLowerCase(Locale.ROOT)), p_16328_.createMap((Map)((Map)p_16323_.getValue()).entrySet().stream().map((p_145487_) -> {
                return Pair.of(p_16328_.createString((String)p_145487_.getKey()), p_16328_.createString((String)p_145487_.getValue()));
            }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        return new Dynamic(p_16328_, p_16328_.createMap(ImmutableMap.of(p_16328_.createString("layers"), $$13, p_16328_.createString("biome"), p_16328_.createString($$3), p_16328_.createString("structures"), $$14)));
    }

    @Nullable
    private static Pair<Integer, String> getLayerInfoFromString(String p_16325_) {
        String[] $$1 = p_16325_.split("\\*", 2);
        int $$4;
        if ($$1.length == 2) {
            try {
                $$4 = Integer.parseInt($$1[0]);
            } catch (NumberFormatException var4) {
                return null;
            }
        } else {
            $$4 = 1;
        }

        String $$5 = $$1[$$1.length - 1];
        return Pair.of($$4, $$5);
    }

    private static List<Pair<Integer, String>> getLayersInfoFromString(String p_16335_) {
        List<Pair<Integer, String>> $$1 = Lists.newArrayList();
        String[] $$2 = p_16335_.split(",");
        String[] var3 = $$2;
        int var4 = $$2.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String $$3 = var3[var5];
            Pair<Integer, String> $$4 = getLayerInfoFromString($$3);
            if ($$4 == null) {
                return Collections.emptyList();
            }

            $$1.add($$4);
        }

        return $$1;
    }
}
