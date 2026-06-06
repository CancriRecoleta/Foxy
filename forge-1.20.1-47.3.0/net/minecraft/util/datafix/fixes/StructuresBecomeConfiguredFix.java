//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraftforge.common.ForgeHooks;

public class StructuresBecomeConfiguredFix extends DataFix {
    private static final Map<String, Conversion> CONVERSION_MAP = ImmutableMap.builder().put("mineshaft", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands"), "minecraft:mineshaft_mesa"), "minecraft:mineshaft")).put("shipwreck", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:beach", "minecraft:snowy_beach"), "minecraft:shipwreck_beached"), "minecraft:shipwreck")).put("ocean_ruin", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:warm_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean"), "minecraft:ocean_ruin_warm"), "minecraft:ocean_ruin_cold")).put("village", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:desert"), "minecraft:village_desert", List.of("minecraft:savanna"), "minecraft:village_savanna", List.of("minecraft:snowy_plains"), "minecraft:village_snowy", List.of("minecraft:taiga"), "minecraft:village_taiga"), "minecraft:village_plains")).put("ruined_portal", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:desert"), "minecraft:ruined_portal_desert", List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands", "minecraft:windswept_hills", "minecraft:windswept_forest", "minecraft:windswept_gravelly_hills", "minecraft:savanna_plateau", "minecraft:windswept_savanna", "minecraft:stony_shore", "minecraft:meadow", "minecraft:frozen_peaks", "minecraft:jagged_peaks", "minecraft:stony_peaks", "minecraft:snowy_slopes"), "minecraft:ruined_portal_mountain", List.of("minecraft:bamboo_jungle", "minecraft:jungle", "minecraft:sparse_jungle"), "minecraft:ruined_portal_jungle", List.of("minecraft:deep_frozen_ocean", "minecraft:deep_cold_ocean", "minecraft:deep_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:frozen_ocean", "minecraft:ocean", "minecraft:cold_ocean", "minecraft:lukewarm_ocean", "minecraft:warm_ocean"), "minecraft:ruined_portal_ocean"), "minecraft:ruined_portal")).put("pillager_outpost", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:pillager_outpost")).put("mansion", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:mansion")).put("jungle_pyramid", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:jungle_pyramid")).put("desert_pyramid", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:desert_pyramid")).put("igloo", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:igloo")).put("swamp_hut", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:swamp_hut")).put("stronghold", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:stronghold")).put("monument", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:monument")).put("fortress", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:fortress")).put("endcity", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:end_city")).put("buried_treasure", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:buried_treasure")).put("nether_fossil", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:nether_fossil")).put("bastion_remnant", net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:bastion_remnant")).build();

    public StructuresBecomeConfiguredFix(Schema p_207679_) {
        super(p_207679_, false);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(References.CHUNK);
        Type<?> type1 = this.getInputSchema().getType(References.CHUNK);
        return this.writeFixAndRead("StucturesToConfiguredStructures", type, type1, this::fix);
    }

    private Dynamic<?> fix(Dynamic<?> p_207692_) {
        return p_207692_.update("structures", (p_207728_) -> {
            return p_207728_.update("starts", (p_207734_) -> {
                return this.updateStarts(p_207734_, p_207692_);
            }).update("References", (p_207731_) -> {
                return this.updateReferences(p_207731_, p_207692_);
            });
        });
    }

    private Dynamic<?> updateStarts(Dynamic<?> p_207700_, Dynamic<?> p_207701_) {
        Map<? extends Dynamic<?>, ? extends Dynamic<?>> map = (Map)p_207700_.getMapValues().result().get();
        List<Dynamic<?>> list = new ArrayList();
        map.forEach((p_207721_, p_207722_) -> {
            if (p_207722_.get("id").asString("INVALID").equals("INVALID")) {
                list.add(p_207721_);
            }

        });

        Dynamic dynamic;
        for(Iterator var5 = list.iterator(); var5.hasNext(); p_207700_ = p_207700_.remove(dynamic.asString(""))) {
            dynamic = (Dynamic)var5.next();
        }

        return p_207700_.updateMapValues((p_207715_) -> {
            return this.updateStart(p_207715_, p_207701_);
        });
    }

    private Pair<Dynamic<?>, Dynamic<?>> updateStart(Pair<Dynamic<?>, Dynamic<?>> p_207685_, Dynamic<?> p_207686_) {
        Dynamic<?> dynamic = this.findUpdatedStructureType(p_207685_, p_207686_);
        return new Pair(dynamic, ((Dynamic)p_207685_.getSecond()).set("id", dynamic));
    }

    private Dynamic<?> updateReferences(Dynamic<?> p_207717_, Dynamic<?> p_207718_) {
        Map<? extends Dynamic<?>, ? extends Dynamic<?>> map = (Map)p_207717_.getMapValues().result().get();
        List<Dynamic<?>> list = new ArrayList();
        map.forEach((p_207704_, p_207705_) -> {
            if (p_207705_.asLongStream().count() == 0L) {
                list.add(p_207704_);
            }

        });

        Dynamic dynamic;
        for(Iterator var5 = list.iterator(); var5.hasNext(); p_207717_ = p_207717_.remove(dynamic.asString(""))) {
            dynamic = (Dynamic)var5.next();
        }

        return p_207717_.updateMapValues((p_207698_) -> {
            return this.updateReference(p_207698_, p_207718_);
        });
    }

    private Pair<Dynamic<?>, Dynamic<?>> updateReference(Pair<Dynamic<?>, Dynamic<?>> p_207711_, Dynamic<?> p_207712_) {
        return p_207711_.mapFirst((p_207690_) -> {
            return this.findUpdatedStructureType(p_207711_, p_207712_);
        });
    }

    private Dynamic<?> findUpdatedStructureType(Pair<Dynamic<?>, Dynamic<?>> p_207724_, Dynamic<?> p_207725_) {
        String s = ((Dynamic)p_207724_.getFirst()).asString("UNKNOWN").toLowerCase(Locale.ROOT);
        Conversion structuresbecomeconfiguredfix$conversion = (Conversion)CONVERSION_MAP.get(s);
        if (structuresbecomeconfiguredfix$conversion == null) {
            structuresbecomeconfiguredfix$conversion = ForgeHooks.getStructureConversion(s);
        }

        if (structuresbecomeconfiguredfix$conversion == null) {
            return ForgeHooks.checkStructureNamespace(s) ? ((Dynamic)p_207724_.getSecond()).createString(s) : ((Dynamic)p_207724_.getSecond()).createString("unknown." + s);
        } else {
            Dynamic<?> dynamic = (Dynamic)p_207724_.getSecond();
            String s1 = structuresbecomeconfiguredfix$conversion.fallback;
            if (!structuresbecomeconfiguredfix$conversion.biomeMapping().isEmpty()) {
                Optional<String> optional = this.guessConfiguration(p_207725_, structuresbecomeconfiguredfix$conversion);
                if (optional.isPresent()) {
                    s1 = (String)optional.get();
                }
            }

            Dynamic<?> dynamic1 = dynamic.createString(s1);
            return dynamic1;
        }
    }

    private Optional<String> guessConfiguration(Dynamic<?> p_207694_, Conversion p_207695_) {
        Object2IntArrayMap<String> object2intarraymap = new Object2IntArrayMap();
        p_207694_.get("sections").asList(Function.identity()).forEach((p_207683_) -> {
            p_207683_.get("biomes").get("palette").asList(Function.identity()).forEach((p_207709_) -> {
                String s = (String)p_207695_.biomeMapping().get(p_207709_.asString(""));
                if (s != null) {
                    object2intarraymap.mergeInt(s, 1, Integer::sum);
                }

            });
        });
        return object2intarraymap.object2IntEntrySet().stream().max(Comparator.comparingInt(Object2IntMap.Entry::getIntValue)).map(Map.Entry::getKey);
    }

    public static record Conversion(Map<String, String> biomeMapping, String fallback) {
        public Conversion(Map<String, String> biomeMapping, String fallback) {
            this.biomeMapping = biomeMapping;
            this.fallback = fallback;
        }

        public static Conversion trivial(String p_207747_) {
            return new Conversion(Map.of(), p_207747_);
        }

        public static Conversion biomeMapped(Map<List<String>, String> p_207751_, String p_207752_) {
            return new Conversion(unpack(p_207751_), p_207752_);
        }

        private static Map<String, String> unpack(Map<List<String>, String> p_207749_) {
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            Iterator var2 = p_207749_.entrySet().iterator();

            while(var2.hasNext()) {
                Map.Entry<List<String>, String> entry = (Map.Entry)var2.next();
                ((List)entry.getKey()).forEach((p_207745_) -> {
                    builder.put(p_207745_, (String)entry.getValue());
                });
            }

            return builder.build();
        }

        public Map<String, String> biomeMapping() {
            return this.biomeMapping;
        }

        public String fallback() {
            return this.fallback;
        }
    }
}
