//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.util.RandomSource;

public class ShufflingList<U> implements Iterable<U> {
    protected final List<WeightedEntry<U>> entries;
    private final RandomSource random = RandomSource.create();

    public ShufflingList() {
        this.entries = Lists.newArrayList();
    }

    private ShufflingList(List<WeightedEntry<U>> p_147921_) {
        this.entries = Lists.newArrayList(p_147921_);
    }

    public static <U> Codec<ShufflingList<U>> codec(Codec<U> p_147928_) {
        return net.minecraft.world.entity.ai.behavior.ShufflingList.WeightedEntry.codec(p_147928_).listOf().xmap(ShufflingList::new, (p_147926_) -> {
            return p_147926_.entries;
        });
    }

    public ShufflingList<U> add(U p_147930_, int p_147931_) {
        this.entries.add(new WeightedEntry(p_147930_, p_147931_));
        return this;
    }

    public ShufflingList<U> shuffle() {
        this.entries.forEach((p_147924_) -> {
            p_147924_.setRandom(this.random.nextFloat());
        });
        this.entries.sort(Comparator.comparingDouble(WeightedEntry::getRandWeight));
        return this;
    }

    public Stream<U> stream() {
        return this.entries.stream().map(WeightedEntry::getData);
    }

    public Iterator<U> iterator() {
        return Iterators.transform(this.entries.iterator(), WeightedEntry::getData);
    }

    public String toString() {
        return "ShufflingList[" + this.entries + "]";
    }

    public static class WeightedEntry<T> {
        final T data;
        final int weight;
        private double randWeight;

        WeightedEntry(T p_147938_, int p_147939_) {
            this.weight = p_147939_;
            this.data = p_147938_;
        }

        private double getRandWeight() {
            return this.randWeight;
        }

        void setRandom(float p_147942_) {
            this.randWeight = -Math.pow((double)p_147942_, (double)(1.0F / (float)this.weight));
        }

        public T getData() {
            return this.data;
        }

        public int getWeight() {
            return this.weight;
        }

        public String toString() {
            return this.weight + ":" + this.data;
        }

        public static <E> Codec<WeightedEntry<E>> codec(final Codec<E> p_147944_) {
            return new Codec<WeightedEntry<E>>() {
                public <T> DataResult<Pair<WeightedEntry<E>, T>> decode(DynamicOps<T> p_147962_, T p_147963_) {
                    Dynamic<T> $$2 = new Dynamic(p_147962_, p_147963_);
                    OptionalDynamic var10000 = $$2.get("data");
                    Codec var10001 = p_147944_;
                    Objects.requireNonNull(var10001);
                    return var10000.flatMap(var10001::parse).map((p_147957_) -> {
                        return new WeightedEntry(p_147957_, $$2.get("weight").asInt(1));
                    }).map((p_147960_) -> {
                        return Pair.of(p_147960_, p_147962_.empty());
                    });
                }

                public <T> DataResult<T> encode(WeightedEntry<E> p_147952_, DynamicOps<T> p_147953_, T p_147954_) {
                    return p_147953_.mapBuilder().add("weight", p_147953_.createInt(p_147952_.weight)).add("data", p_147944_.encodeStart(p_147953_, p_147952_.data)).build(p_147954_);
                }
            };
        }
    }
}
