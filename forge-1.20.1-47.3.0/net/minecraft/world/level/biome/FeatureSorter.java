//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Graph;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public class FeatureSorter {
    public FeatureSorter() {
    }

    public static <T> List<StepFeatureData> buildFeaturesPerStep(List<T> p_220604_, Function<T, List<HolderSet<PlacedFeature>>> p_220605_, boolean p_220606_) {
        Object2IntMap<PlacedFeature> $$3 = new Object2IntOpenHashMap();
        MutableInt $$4 = new MutableInt(0);
        Comparator<FeatureData> $$5 = Comparator.comparingInt(FeatureData::step).thenComparingInt(FeatureData::featureIndex);
        Map<FeatureData, Set<FeatureData>> $$6 = new TreeMap($$5);
        int $$7 = 0;
        Iterator var8 = p_220604_.iterator();

        ArrayList $$18;
        int $$14;

        record FeatureData(int featureIndex, int step, PlacedFeature feature) {
            FeatureData(int featureIndex, int step, PlacedFeature feature) {
                this.featureIndex = featureIndex;
                this.step = step;
                this.feature = feature;
            }

            public int featureIndex() {
                return this.featureIndex;
            }

            public int step() {
                return this.step;
            }

            public PlacedFeature feature() {
                return this.feature;
            }
        }

        while(var8.hasNext()) {
            T $$8 = var8.next();
            $$18 = Lists.newArrayList();
            List<HolderSet<PlacedFeature>> $$10 = (List)p_220605_.apply($$8);
            $$7 = Math.max($$7, $$10.size());

            for($$14 = 0; $$14 < $$10.size(); ++$$14) {
                Iterator var13 = ((HolderSet)$$10.get($$14)).iterator();

                while(var13.hasNext()) {
                    Holder<PlacedFeature> $$12 = (Holder)var13.next();
                    PlacedFeature $$13 = (PlacedFeature)$$12.value();
                    $$18.add(new FeatureData($$3.computeIfAbsent($$13, (p_220609_) -> {
                        return $$4.getAndIncrement();
                    }), $$14, $$13));
                }
            }

            for($$14 = 0; $$14 < $$18.size(); ++$$14) {
                Set<FeatureData> $$15 = (Set)$$6.computeIfAbsent((FeatureData)$$18.get($$14), (p_220602_) -> {
                    return new TreeSet($$5);
                });
                if ($$14 < $$18.size() - 1) {
                    $$15.add((FeatureData)$$18.get($$14 + 1));
                }
            }
        }

        Set<FeatureData> $$16 = new TreeSet($$5);
        Set<FeatureData> $$17 = new TreeSet($$5);
        $$18 = Lists.newArrayList();
        Iterator var21 = $$6.keySet().iterator();

        while(var21.hasNext()) {
            FeatureData $$19 = (FeatureData)var21.next();
            if (!$$17.isEmpty()) {
                throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
            }

            if (!$$16.contains($$19)) {
                Objects.requireNonNull($$18);
                if (Graph.depthFirstSearch($$6, $$16, $$17, $$18::add, $$19)) {
                    if (!p_220606_) {
                        throw new IllegalStateException("Feature order cycle found");
                    }

                    List<T> $$20 = new ArrayList(p_220604_);

                    int $$21;
                    do {
                        $$21 = $$20.size();
                        ListIterator<T> $$22 = $$20.listIterator();

                        while($$22.hasNext()) {
                            T $$23 = $$22.next();
                            $$22.remove();

                            try {
                                buildFeaturesPerStep($$20, p_220605_, false);
                            } catch (IllegalStateException var18) {
                                continue;
                            }

                            $$22.add($$23);
                        }
                    } while($$21 != $$20.size());

                    throw new IllegalStateException("Feature order cycle found, involved sources: " + $$20);
                }
            }
        }

        Collections.reverse($$18);
        ImmutableList.Builder<StepFeatureData> $$25 = ImmutableList.builder();

        for($$14 = 0; $$14 < $$7; ++$$14) {
            List<PlacedFeature> $$28 = (List)$$18.stream().filter((p_220599_) -> {
                return p_220599_.step() == $$14;
            }).map(FeatureData::feature).collect(Collectors.toList());
            $$25.add(new StepFeatureData($$28));
        }

        return $$25.build();
    }

    public static record StepFeatureData(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
        StepFeatureData(List<PlacedFeature> p_220627_) {
            this(p_220627_, Util.createIndexLookup(p_220627_, (p_220633_) -> {
                return new Object2IntOpenCustomHashMap(p_220633_, Util.identityStrategy());
            }));
        }

        public StepFeatureData(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
            this.features = features;
            this.indexMapping = indexMapping;
        }

        public List<PlacedFeature> features() {
            return this.features;
        }

        public ToIntFunction<PlacedFeature> indexMapping() {
            return this.indexMapping;
        }
    }
}
