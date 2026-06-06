//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

public class ChunkHeightAndBiomeFix extends DataFix {
    public static final String DATAFIXER_CONTEXT_TAG = "__context";
    private static final String NAME = "ChunkHeightAndBiomeFix";
    private static final int OLD_SECTION_COUNT = 16;
    private static final int NEW_SECTION_COUNT = 24;
    private static final int NEW_MIN_SECTION_Y = -4;
    public static final int BLOCKS_PER_SECTION = 4096;
    private static final int LONGS_PER_SECTION = 64;
    private static final int HEIGHTMAP_BITS = 9;
    private static final long HEIGHTMAP_MASK = 511L;
    private static final int HEIGHTMAP_OFFSET = 64;
    private static final String[] HEIGHTMAP_TYPES = new String[]{"WORLD_SURFACE_WG", "WORLD_SURFACE", "WORLD_SURFACE_IGNORE_SNOW", "OCEAN_FLOOR_WG", "OCEAN_FLOOR", "MOTION_BLOCKING", "MOTION_BLOCKING_NO_LEAVES"};
    private static final Set<String> STATUS_IS_OR_AFTER_SURFACE = Set.of("surface", "carvers", "liquid_carvers", "features", "light", "spawn", "heightmaps", "full");
    private static final Set<String> STATUS_IS_OR_AFTER_NOISE = Set.of("noise", "surface", "carvers", "liquid_carvers", "features", "light", "spawn", "heightmaps", "full");
    private static final Set<String> BLOCKS_BEFORE_FEATURE_STATUS = Set.of("minecraft:air", "minecraft:basalt", "minecraft:bedrock", "minecraft:blackstone", "minecraft:calcite", "minecraft:cave_air", "minecraft:coarse_dirt", "minecraft:crimson_nylium", "minecraft:dirt", "minecraft:end_stone", "minecraft:grass_block", "minecraft:gravel", "minecraft:ice", "minecraft:lava", "minecraft:mycelium", "minecraft:nether_wart_block", "minecraft:netherrack", "minecraft:orange_terracotta", "minecraft:packed_ice", "minecraft:podzol", "minecraft:powder_snow", "minecraft:red_sand", "minecraft:red_sandstone", "minecraft:sand", "minecraft:sandstone", "minecraft:snow_block", "minecraft:soul_sand", "minecraft:soul_soil", "minecraft:stone", "minecraft:terracotta", "minecraft:warped_nylium", "minecraft:warped_wart_block", "minecraft:water", "minecraft:white_terracotta");
    private static final int BIOME_CONTAINER_LAYER_SIZE = 16;
    private static final int BIOME_CONTAINER_SIZE = 64;
    private static final int BIOME_CONTAINER_TOP_LAYER_OFFSET = 1008;
    public static final String DEFAULT_BIOME = "minecraft:plains";
    private static final Int2ObjectMap<String> BIOMES_BY_ID = new Int2ObjectOpenHashMap();

    public ChunkHeightAndBiomeFix(Schema p_184863_) {
        super(p_184863_, true);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> $$0 = this.getInputSchema().getType(References.CHUNK);
        OpticFinder<?> $$1 = $$0.findField("Level");
        OpticFinder<?> $$2 = $$1.type().findField("Sections");
        Schema $$3 = this.getOutputSchema();
        Type<?> $$4 = $$3.getType(References.CHUNK);
        Type<?> $$5 = $$4.findField("Level").type();
        Type<?> $$6 = $$5.findField("Sections").type();
        return this.fixTypeEverywhereTyped("ChunkHeightAndBiomeFix", $$0, $$4, (p_184879_) -> {
            return p_184879_.updateTyped($$1, $$5, (p_184884_) -> {
                Dynamic<?> $$4 = (Dynamic)p_184884_.get(DSL.remainderFinder());
                OptionalDynamic<?> $$5 = ((Dynamic)p_184879_.get(DSL.remainderFinder())).get("__context");
                String $$6x = (String)$$5.get("dimension").asString().result().orElse("");
                String $$7 = (String)$$5.get("generator").asString().result().orElse("");
                boolean $$8 = "minecraft:overworld".equals($$6x);
                MutableBoolean $$9 = new MutableBoolean();
                int $$10 = $$8 ? -4 : 0;
                Dynamic<?>[] $$11 = getBiomeContainers($$4, $$8, $$10, $$9);
                Dynamic<?> $$12 = makePalettedContainer($$4.createList(Stream.of($$4.createMap(ImmutableMap.of($$4.createString("Name"), $$4.createString("minecraft:air"))))));
                Set<String> $$13 = Sets.newHashSet();
                MutableObject<Supplier<ChunkProtoTickListFix.PoorMansPalettedContainer>> $$14 = new MutableObject(() -> {
                    return null;
                });
                p_184884_ = p_184884_.updateTyped($$2, $$6, (p_184936_) -> {
                    IntSet $$8 = new IntOpenHashSet();
                    Dynamic<?> $$9 = (Dynamic)p_184936_.write().result().orElseThrow(() -> {
                        return new IllegalStateException("Malformed Chunk.Level.Sections");
                    });
                    List<Dynamic<?>> $$10x = (List)$$9.asStream().map((p_184927_) -> {
                        int $$7 = p_184927_.get("Y").asInt(0);
                        Dynamic<?> $$8x = (Dynamic)DataFixUtils.orElse(p_184927_.get("Palette").result().flatMap((p_184940_) -> {
                            Stream var10000 = p_184940_.asStream().map((p_184982_) -> {
                                return p_184982_.get("Name").asString("minecraft:air");
                            });
                            Objects.requireNonNull($$13);
                            var10000.forEach($$13::add);
                            return p_184927_.get("BlockStates").result().map((p_184973_) -> {
                                return makeOptimizedPalettedContainer(p_184940_, p_184973_);
                            });
                        }), $$12);
                        Dynamic<?> $$9 = p_184927_;
                        int $$10x = $$7 - $$10;
                        if ($$10x >= 0 && $$10x < $$11.length) {
                            $$9 = $$9.set("biomes", $$11[$$10x]);
                        }

                        $$8.add($$7);
                        if (p_184927_.get("Y").asInt(Integer.MAX_VALUE) == 0) {
                            $$14.setValue(() -> {
                                List<? extends Dynamic<?>> $$1 = $$8x.get("palette").asList(Function.identity());
                                long[] $$2 = $$8x.get("data").asLongStream().toArray();
                                return new ChunkProtoTickListFix.PoorMansPalettedContainer($$1, $$2);
                            });
                        }

                        return $$9.set("block_states", $$8x).remove("Palette").remove("BlockStates");
                    }).collect(Collectors.toCollection(ArrayList::new));

                    for(int $$11x = 0; $$11x < $$11.length; ++$$11x) {
                        int $$12x = $$11x + $$10;
                        if ($$8.add($$12x)) {
                            Dynamic<?> $$13x = $$4.createMap(Map.of($$4.createString("Y"), $$4.createInt($$12x)));
                            $$13x = $$13x.set("block_states", $$12);
                            $$13x = $$13x.set("biomes", $$11[$$11x]);
                            $$10x.add($$13x);
                        }
                    }

                    return (Typed)((Pair)$$6.readTyped($$4.createList($$10x.stream())).result().orElseThrow(() -> {
                        return new IllegalStateException("ChunkHeightAndBiomeFix failed.");
                    })).getFirst();
                });
                return p_184884_.update(DSL.remainderFinder(), (p_184947_) -> {
                    if ($$8) {
                        p_184947_ = this.predictChunkStatusBeforeSurface(p_184947_, $$13);
                    }

                    return updateChunkTag(p_184947_, $$8, $$9.booleanValue(), "minecraft:noise".equals($$7), (Supplier)$$14.getValue());
                });
            });
        });
    }

    private Dynamic<?> predictChunkStatusBeforeSurface(Dynamic<?> p_184904_, Set<String> p_184905_) {
        return p_184904_.update("Status", (p_184919_) -> {
            String $$2 = p_184919_.asString("empty");
            if (STATUS_IS_OR_AFTER_SURFACE.contains($$2)) {
                return p_184919_;
            } else {
                p_184905_.remove("minecraft:air");
                boolean $$3 = !p_184905_.isEmpty();
                p_184905_.removeAll(BLOCKS_BEFORE_FEATURE_STATUS);
                boolean $$4 = !p_184905_.isEmpty();
                if ($$4) {
                    return p_184919_.createString("liquid_carvers");
                } else if (!"noise".equals($$2) && !$$3) {
                    return "biomes".equals($$2) ? p_184919_.createString("structure_references") : p_184919_;
                } else {
                    return p_184919_.createString("noise");
                }
            }
        });
    }

    private static Dynamic<?>[] getBiomeContainers(Dynamic<?> p_184907_, boolean p_184908_, int p_184909_, MutableBoolean p_184910_) {
        Dynamic<?>[] $$4 = new Dynamic[p_184908_ ? 24 : 16];
        int[] $$5 = (int[])p_184907_.get("Biomes").asIntStreamOpt().result().map(IntStream::toArray).orElse((Object)null);
        int $$8;
        int $$9;
        if ($$5 != null && $$5.length == 1536) {
            p_184910_.setValue(true);

            for($$8 = 0; $$8 < 24; ++$$8) {
                $$9 = $$8;
                $$4[$$8] = makeBiomeContainer(p_184907_, (p_184967_) -> {
                    return getOldBiome($$5, $$9 * 64 + p_184967_);
                });
            }
        } else if ($$5 != null && $$5.length == 1024) {
            int $$14;
            for($$8 = 0; $$8 < 16; ++$$8) {
                $$9 = $$8 - p_184909_;
                $$14 = $$8;
                $$4[$$9] = makeBiomeContainer(p_184907_, (p_184954_) -> {
                    return getOldBiome($$5, $$14 * 64 + p_184954_);
                });
            }

            if (p_184908_) {
                Dynamic<?> $$11 = makeBiomeContainer(p_184907_, (p_184976_) -> {
                    return getOldBiome($$5, p_184976_ % 16);
                });
                Dynamic<?> $$12 = makeBiomeContainer(p_184907_, (p_184963_) -> {
                    return getOldBiome($$5, p_184963_ % 16 + 1008);
                });

                for($$14 = 0; $$14 < 4; ++$$14) {
                    $$4[$$14] = $$11;
                }

                for($$14 = 20; $$14 < 24; ++$$14) {
                    $$4[$$14] = $$12;
                }
            }
        } else {
            Arrays.fill($$4, makePalettedContainer(p_184907_.createList(Stream.of(p_184907_.createString("minecraft:plains")))));
        }

        return $$4;
    }

    private static int getOldBiome(int[] p_184949_, int p_184950_) {
        return p_184949_[p_184950_] & 255;
    }

    private static Dynamic<?> updateChunkTag(Dynamic<?> p_184912_, boolean p_184913_, boolean p_184914_, boolean p_184915_, Supplier<ChunkProtoTickListFix.PoorMansPalettedContainer> p_184916_) {
        p_184912_ = p_184912_.remove("Biomes");
        if (!p_184913_) {
            return updateCarvingMasks(p_184912_, 16, 0);
        } else if (p_184914_) {
            return updateCarvingMasks(p_184912_, 24, 0);
        } else {
            p_184912_ = updateHeightmaps(p_184912_);
            p_184912_ = addPaddingEntries(p_184912_, "LiquidsToBeTicked");
            p_184912_ = addPaddingEntries(p_184912_, "PostProcessing");
            p_184912_ = addPaddingEntries(p_184912_, "ToBeTicked");
            p_184912_ = updateCarvingMasks(p_184912_, 24, 4);
            p_184912_ = p_184912_.update("UpgradeData", ChunkHeightAndBiomeFix::shiftUpgradeData);
            if (!p_184915_) {
                return p_184912_;
            } else {
                Optional<? extends Dynamic<?>> $$5 = p_184912_.get("Status").result();
                if ($$5.isPresent()) {
                    Dynamic<?> $$6 = (Dynamic)$$5.get();
                    String $$7 = $$6.asString("");
                    if (!"empty".equals($$7)) {
                        p_184912_ = p_184912_.set("blending_data", p_184912_.createMap(ImmutableMap.of(p_184912_.createString("old_noise"), p_184912_.createBoolean(STATUS_IS_OR_AFTER_NOISE.contains($$7)))));
                        ChunkProtoTickListFix.PoorMansPalettedContainer $$8 = (ChunkProtoTickListFix.PoorMansPalettedContainer)p_184916_.get();
                        if ($$8 != null) {
                            BitSet $$9 = new BitSet(256);
                            boolean $$10 = $$7.equals("noise");

                            for(int $$11 = 0; $$11 < 16; ++$$11) {
                                for(int $$12 = 0; $$12 < 16; ++$$12) {
                                    Dynamic<?> $$13 = $$8.get($$12, 0, $$11);
                                    boolean $$14 = $$13 != null && "minecraft:bedrock".equals($$13.get("Name").asString(""));
                                    boolean $$15 = $$13 != null && "minecraft:air".equals($$13.get("Name").asString(""));
                                    if ($$15) {
                                        $$9.set($$11 * 16 + $$12);
                                    }

                                    $$10 |= $$14;
                                }
                            }

                            if ($$10 && $$9.cardinality() != $$9.size()) {
                                Dynamic<?> $$16 = "full".equals($$7) ? p_184912_.createString("heightmaps") : $$6;
                                p_184912_ = p_184912_.set("below_zero_retrogen", p_184912_.createMap(ImmutableMap.of(p_184912_.createString("target_status"), $$16, p_184912_.createString("missing_bedrock"), p_184912_.createLongList(LongStream.of($$9.toLongArray())))));
                                p_184912_ = p_184912_.set("Status", p_184912_.createString("empty"));
                            }

                            p_184912_ = p_184912_.set("isLightOn", p_184912_.createBoolean(false));
                        }
                    }
                }

                return p_184912_;
            }
        }
    }

    private static <T> Dynamic<T> shiftUpgradeData(Dynamic<T> p_196591_) {
        return p_196591_.update("Indices", (p_196614_) -> {
            Map<Dynamic<?>, Dynamic<?>> $$1 = new HashMap();
            p_196614_.getMapValues().result().ifPresent((p_196610_) -> {
                p_196610_.forEach((p_196601_, p_196602_) -> {
                    try {
                        p_196601_.asString().result().map(Integer::parseInt).ifPresent((p_196607_) -> {
                            int $$4 = p_196607_ - -4;
                            $$1.put(p_196601_.createString(Integer.toString($$4)), p_196602_);
                        });
                    } catch (NumberFormatException var4) {
                    }

                });
            });
            return p_196614_.createMap($$1);
        });
    }

    private static Dynamic<?> updateCarvingMasks(Dynamic<?> p_184888_, int p_184889_, int p_184890_) {
        Dynamic<?> $$3 = p_184888_.get("CarvingMasks").orElseEmptyMap();
        $$3 = $$3.updateMapValues((p_196587_) -> {
            long[] $$4 = BitSet.valueOf(((Dynamic)p_196587_.getSecond()).asByteBuffer().array()).toLongArray();
            long[] $$5 = new long[64 * p_184889_];
            System.arraycopy($$4, 0, $$5, 64 * p_184890_, $$4.length);
            return Pair.of((Dynamic)p_196587_.getFirst(), p_184888_.createLongList(LongStream.of($$5)));
        });
        return p_184888_.set("CarvingMasks", $$3);
    }

    private static Dynamic<?> addPaddingEntries(Dynamic<?> p_184901_, String p_184902_) {
        List<Dynamic<?>> $$2 = (List)p_184901_.get(p_184902_).orElseEmptyList().asStream().collect(Collectors.toCollection(ArrayList::new));
        if ($$2.size() == 24) {
            return p_184901_;
        } else {
            Dynamic<?> $$3 = p_184901_.emptyList();

            for(int $$4 = 0; $$4 < 4; ++$$4) {
                $$2.add(0, $$3);
                $$2.add($$3);
            }

            return p_184901_.set(p_184902_, p_184901_.createList($$2.stream()));
        }
    }

    private static Dynamic<?> updateHeightmaps(Dynamic<?> p_184886_) {
        return p_184886_.update("Heightmaps", (p_196612_) -> {
            String[] var1 = HEIGHTMAP_TYPES;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String $$1 = var1[var3];
                p_196612_ = p_196612_.update($$1, ChunkHeightAndBiomeFix::getFixedHeightmap);
            }

            return p_196612_;
        });
    }

    private static Dynamic<?> getFixedHeightmap(Dynamic<?> p_184957_) {
        return p_184957_.createLongList(p_184957_.asLongStream().map((p_196589_) -> {
            long $$1 = 0L;

            for(int $$2 = 0; $$2 + 9 <= 64; $$2 += 9) {
                long $$3 = p_196589_ >> $$2 & 511L;
                long $$5;
                if ($$3 == 0L) {
                    $$5 = 0L;
                } else {
                    $$5 = Math.min($$3 + 64L, 511L);
                }

                $$1 |= $$5 << $$2;
            }

            return $$1;
        }));
    }

    private static Dynamic<?> makeBiomeContainer(Dynamic<?> p_184895_, Int2IntFunction p_184896_) {
        Int2IntMap $$2 = new Int2IntLinkedOpenHashMap();

        int $$6;
        for(int $$3 = 0; $$3 < 64; ++$$3) {
            $$6 = p_184896_.applyAsInt($$3);
            if (!$$2.containsKey($$6)) {
                $$2.put($$6, $$2.size());
            }
        }

        Dynamic<?> $$5 = p_184895_.createList($$2.keySet().stream().map((p_196598_) -> {
            return p_184895_.createString((String)BIOMES_BY_ID.getOrDefault(p_196598_, "minecraft:plains"));
        }));
        $$6 = ceillog2($$2.size());
        if ($$6 == 0) {
            return makePalettedContainer($$5);
        } else {
            int $$7 = 64 / $$6;
            int $$8 = (64 + $$7 - 1) / $$7;
            long[] $$9 = new long[$$8];
            int $$10 = 0;
            int $$11 = 0;

            for(int $$12 = 0; $$12 < 64; ++$$12) {
                int $$13 = p_184896_.applyAsInt($$12);
                $$9[$$10] |= (long)$$2.get($$13) << $$11;
                $$11 += $$6;
                if ($$11 + $$6 > 64) {
                    ++$$10;
                    $$11 = 0;
                }
            }

            Dynamic<?> $$14 = p_184895_.createLongList(Arrays.stream($$9));
            return makePalettedContainer($$5, $$14);
        }
    }

    private static Dynamic<?> makePalettedContainer(Dynamic<?> p_184970_) {
        return p_184970_.createMap(ImmutableMap.of(p_184970_.createString("palette"), p_184970_));
    }

    private static Dynamic<?> makePalettedContainer(Dynamic<?> p_184892_, Dynamic<?> p_184893_) {
        return p_184892_.createMap(ImmutableMap.of(p_184892_.createString("palette"), p_184892_, p_184892_.createString("data"), p_184893_));
    }

    private static Dynamic<?> makeOptimizedPalettedContainer(Dynamic<?> p_184959_, Dynamic<?> p_184960_) {
        List<Dynamic<?>> $$2 = (List)p_184959_.asStream().collect(Collectors.toCollection(ArrayList::new));
        if ($$2.size() == 1) {
            return makePalettedContainer(p_184959_);
        } else {
            p_184959_ = padPaletteEntries(p_184959_, p_184960_, $$2);
            return makePalettedContainer(p_184959_, p_184960_);
        }
    }

    private static Dynamic<?> padPaletteEntries(Dynamic<?> p_196593_, Dynamic<?> p_196594_, List<Dynamic<?>> p_196595_) {
        long $$3 = p_196594_.asLongStream().count() * 64L;
        long $$4 = $$3 / 4096L;
        int $$5 = p_196595_.size();
        int $$6 = ceillog2($$5);
        if ($$4 <= (long)$$6) {
            return p_196593_;
        } else {
            Dynamic<?> $$7 = p_196593_.createMap(ImmutableMap.of(p_196593_.createString("Name"), p_196593_.createString("minecraft:air")));
            int $$8 = (1 << (int)($$4 - 1L)) + 1;
            int $$9 = $$8 - $$5;

            for(int $$10 = 0; $$10 < $$9; ++$$10) {
                p_196595_.add($$7);
            }

            return p_196593_.createList(p_196595_.stream());
        }
    }

    public static int ceillog2(int p_184866_) {
        return p_184866_ == 0 ? 0 : (int)Math.ceil(Math.log((double)p_184866_) / Math.log(2.0));
    }

    static {
        BIOMES_BY_ID.put(0, "minecraft:ocean");
        BIOMES_BY_ID.put(1, "minecraft:plains");
        BIOMES_BY_ID.put(2, "minecraft:desert");
        BIOMES_BY_ID.put(3, "minecraft:mountains");
        BIOMES_BY_ID.put(4, "minecraft:forest");
        BIOMES_BY_ID.put(5, "minecraft:taiga");
        BIOMES_BY_ID.put(6, "minecraft:swamp");
        BIOMES_BY_ID.put(7, "minecraft:river");
        BIOMES_BY_ID.put(8, "minecraft:nether_wastes");
        BIOMES_BY_ID.put(9, "minecraft:the_end");
        BIOMES_BY_ID.put(10, "minecraft:frozen_ocean");
        BIOMES_BY_ID.put(11, "minecraft:frozen_river");
        BIOMES_BY_ID.put(12, "minecraft:snowy_tundra");
        BIOMES_BY_ID.put(13, "minecraft:snowy_mountains");
        BIOMES_BY_ID.put(14, "minecraft:mushroom_fields");
        BIOMES_BY_ID.put(15, "minecraft:mushroom_field_shore");
        BIOMES_BY_ID.put(16, "minecraft:beach");
        BIOMES_BY_ID.put(17, "minecraft:desert_hills");
        BIOMES_BY_ID.put(18, "minecraft:wooded_hills");
        BIOMES_BY_ID.put(19, "minecraft:taiga_hills");
        BIOMES_BY_ID.put(20, "minecraft:mountain_edge");
        BIOMES_BY_ID.put(21, "minecraft:jungle");
        BIOMES_BY_ID.put(22, "minecraft:jungle_hills");
        BIOMES_BY_ID.put(23, "minecraft:jungle_edge");
        BIOMES_BY_ID.put(24, "minecraft:deep_ocean");
        BIOMES_BY_ID.put(25, "minecraft:stone_shore");
        BIOMES_BY_ID.put(26, "minecraft:snowy_beach");
        BIOMES_BY_ID.put(27, "minecraft:birch_forest");
        BIOMES_BY_ID.put(28, "minecraft:birch_forest_hills");
        BIOMES_BY_ID.put(29, "minecraft:dark_forest");
        BIOMES_BY_ID.put(30, "minecraft:snowy_taiga");
        BIOMES_BY_ID.put(31, "minecraft:snowy_taiga_hills");
        BIOMES_BY_ID.put(32, "minecraft:giant_tree_taiga");
        BIOMES_BY_ID.put(33, "minecraft:giant_tree_taiga_hills");
        BIOMES_BY_ID.put(34, "minecraft:wooded_mountains");
        BIOMES_BY_ID.put(35, "minecraft:savanna");
        BIOMES_BY_ID.put(36, "minecraft:savanna_plateau");
        BIOMES_BY_ID.put(37, "minecraft:badlands");
        BIOMES_BY_ID.put(38, "minecraft:wooded_badlands_plateau");
        BIOMES_BY_ID.put(39, "minecraft:badlands_plateau");
        BIOMES_BY_ID.put(40, "minecraft:small_end_islands");
        BIOMES_BY_ID.put(41, "minecraft:end_midlands");
        BIOMES_BY_ID.put(42, "minecraft:end_highlands");
        BIOMES_BY_ID.put(43, "minecraft:end_barrens");
        BIOMES_BY_ID.put(44, "minecraft:warm_ocean");
        BIOMES_BY_ID.put(45, "minecraft:lukewarm_ocean");
        BIOMES_BY_ID.put(46, "minecraft:cold_ocean");
        BIOMES_BY_ID.put(47, "minecraft:deep_warm_ocean");
        BIOMES_BY_ID.put(48, "minecraft:deep_lukewarm_ocean");
        BIOMES_BY_ID.put(49, "minecraft:deep_cold_ocean");
        BIOMES_BY_ID.put(50, "minecraft:deep_frozen_ocean");
        BIOMES_BY_ID.put(127, "minecraft:the_void");
        BIOMES_BY_ID.put(129, "minecraft:sunflower_plains");
        BIOMES_BY_ID.put(130, "minecraft:desert_lakes");
        BIOMES_BY_ID.put(131, "minecraft:gravelly_mountains");
        BIOMES_BY_ID.put(132, "minecraft:flower_forest");
        BIOMES_BY_ID.put(133, "minecraft:taiga_mountains");
        BIOMES_BY_ID.put(134, "minecraft:swamp_hills");
        BIOMES_BY_ID.put(140, "minecraft:ice_spikes");
        BIOMES_BY_ID.put(149, "minecraft:modified_jungle");
        BIOMES_BY_ID.put(151, "minecraft:modified_jungle_edge");
        BIOMES_BY_ID.put(155, "minecraft:tall_birch_forest");
        BIOMES_BY_ID.put(156, "minecraft:tall_birch_hills");
        BIOMES_BY_ID.put(157, "minecraft:dark_forest_hills");
        BIOMES_BY_ID.put(158, "minecraft:snowy_taiga_mountains");
        BIOMES_BY_ID.put(160, "minecraft:giant_spruce_taiga");
        BIOMES_BY_ID.put(161, "minecraft:giant_spruce_taiga_hills");
        BIOMES_BY_ID.put(162, "minecraft:modified_gravelly_mountains");
        BIOMES_BY_ID.put(163, "minecraft:shattered_savanna");
        BIOMES_BY_ID.put(164, "minecraft:shattered_savanna_plateau");
        BIOMES_BY_ID.put(165, "minecraft:eroded_badlands");
        BIOMES_BY_ID.put(166, "minecraft:modified_wooded_badlands_plateau");
        BIOMES_BY_ID.put(167, "minecraft:modified_badlands_plateau");
        BIOMES_BY_ID.put(168, "minecraft:bamboo_jungle");
        BIOMES_BY_ID.put(169, "minecraft:bamboo_jungle_hills");
        BIOMES_BY_ID.put(170, "minecraft:soul_sand_valley");
        BIOMES_BY_ID.put(171, "minecraft:crimson_forest");
        BIOMES_BY_ID.put(172, "minecraft:warped_forest");
        BIOMES_BY_ID.put(173, "minecraft:basalt_deltas");
        BIOMES_BY_ID.put(174, "minecraft:dripstone_caves");
        BIOMES_BY_ID.put(175, "minecraft:lush_caves");
        BIOMES_BY_ID.put(177, "minecraft:meadow");
        BIOMES_BY_ID.put(178, "minecraft:grove");
        BIOMES_BY_ID.put(179, "minecraft:snowy_slopes");
        BIOMES_BY_ID.put(180, "minecraft:snowcapped_peaks");
        BIOMES_BY_ID.put(181, "minecraft:lofty_peaks");
        BIOMES_BY_ID.put(182, "minecraft:stony_peaks");
    }
}
