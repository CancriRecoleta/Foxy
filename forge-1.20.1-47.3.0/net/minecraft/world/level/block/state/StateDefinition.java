//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.properties.Property;

public class StateDefinition<O, S extends StateHolder<O, S>> {
    static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
    private final O owner;
    private final ImmutableSortedMap<String, Property<?>> propertiesByName;
    private final ImmutableList<S> states;

    protected StateDefinition(Function<O, S> p_61052_, O p_61053_, Factory<O, S> p_61054_, Map<String, Property<?>> p_61055_) {
        this.owner = p_61053_;
        this.propertiesByName = ImmutableSortedMap.copyOf(p_61055_);
        Supplier<S> $$4 = () -> {
            return (StateHolder)p_61052_.apply(p_61053_);
        };
        MapCodec<S> $$5 = MapCodec.of(Encoder.empty(), Decoder.unit($$4));

        Map.Entry $$6;
        for(UnmodifiableIterator var7 = this.propertiesByName.entrySet().iterator(); var7.hasNext(); $$5 = appendPropertyCodec($$5, $$4, (String)$$6.getKey(), (Property)$$6.getValue())) {
            $$6 = (Map.Entry)var7.next();
        }

        MapCodec<S> $$7 = $$5;
        Map<Map<Property<?>, Comparable<?>>, S> $$8 = Maps.newLinkedHashMap();
        List<S> $$9 = Lists.newArrayList();
        Stream<List<Pair<Property<?>, Comparable<?>>>> $$10 = Stream.of(Collections.emptyList());

        Property $$11;
        for(UnmodifiableIterator var11 = this.propertiesByName.values().iterator(); var11.hasNext(); $$10 = $$10.flatMap((p_61072_) -> {
            return $$11.getPossibleValues().stream().map((p_155961_) -> {
                List<Pair<Property<?>, Comparable<?>>> $$3 = Lists.newArrayList(p_61072_);
                $$3.add(Pair.of($$11, p_155961_));
                return $$3;
            });
        })) {
            $$11 = (Property)var11.next();
        }

        $$10.forEach((p_61063_) -> {
            ImmutableMap<Property<?>, Comparable<?>> $$6 = (ImmutableMap)p_61063_.stream().collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));
            S $$7x = (StateHolder)p_61054_.create(p_61053_, $$6, $$7);
            $$8.put($$6, $$7x);
            $$9.add($$7x);
        });
        Iterator var13 = $$9.iterator();

        while(var13.hasNext()) {
            S $$12 = (StateHolder)var13.next();
            $$12.populateNeighbours($$8);
        }

        this.states = ImmutableList.copyOf($$9);
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> MapCodec<S> appendPropertyCodec(MapCodec<S> p_61077_, Supplier<S> p_61078_, String p_61079_, Property<T> p_61080_) {
        return Codec.mapPair(p_61077_, p_61080_.valueCodec().fieldOf(p_61079_).orElseGet((p_187541_) -> {
        }, () -> {
            return p_61080_.value((StateHolder)p_61078_.get());
        })).xmap((p_187536_) -> {
            return (StateHolder)((StateHolder)p_187536_.getFirst()).setValue(p_61080_, ((Property.Value)p_187536_.getSecond()).value());
        }, (p_187533_) -> {
            return Pair.of(p_187533_, p_61080_.value(p_187533_));
        });
    }

    public ImmutableList<S> getPossibleStates() {
        return this.states;
    }

    public S any() {
        return (StateHolder)this.states.get(0);
    }

    public O getOwner() {
        return this.owner;
    }

    public Collection<Property<?>> getProperties() {
        return this.propertiesByName.values();
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.propertiesByName.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
    }

    @Nullable
    public Property<?> getProperty(String p_61082_) {
        return (Property)this.propertiesByName.get(p_61082_);
    }

    public interface Factory<O, S> {
        S create(O var1, ImmutableMap<Property<?>, Comparable<?>> var2, MapCodec<S> var3);
    }

    public static class Builder<O, S extends StateHolder<O, S>> {
        private final O owner;
        private final Map<String, Property<?>> properties = Maps.newHashMap();

        public Builder(O p_61098_) {
            this.owner = p_61098_;
        }

        public Builder<O, S> add(Property<?>... p_61105_) {
            Property[] var2 = p_61105_;
            int var3 = p_61105_.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Property<?> $$1 = var2[var4];
                this.validateProperty($$1);
                this.properties.put($$1.getName(), $$1);
            }

            return this;
        }

        private <T extends Comparable<T>> void validateProperty(Property<T> p_61100_) {
            String $$1 = p_61100_.getName();
            if (!StateDefinition.NAME_PATTERN.matcher($$1).matches()) {
                throw new IllegalArgumentException(this.owner + " has invalidly named property: " + $$1);
            } else {
                Collection<T> $$2 = p_61100_.getPossibleValues();
                if ($$2.size() <= 1) {
                    throw new IllegalArgumentException(this.owner + " attempted use property " + $$1 + " with <= 1 possible values");
                } else {
                    Iterator var4 = $$2.iterator();

                    String $$4;
                    do {
                        if (!var4.hasNext()) {
                            if (this.properties.containsKey($$1)) {
                                throw new IllegalArgumentException(this.owner + " has duplicate property: " + $$1);
                            }

                            return;
                        }

                        T $$3 = (Comparable)var4.next();
                        $$4 = p_61100_.getName($$3);
                    } while(StateDefinition.NAME_PATTERN.matcher($$4).matches());

                    throw new IllegalArgumentException(this.owner + " has property: " + $$1 + " with invalidly named value: " + $$4);
                }
            }
        }

        public StateDefinition<O, S> create(Function<O, S> p_61102_, Factory<O, S> p_61103_) {
            return new StateDefinition(p_61102_, this.owner, p_61103_, this.properties);
        }
    }
}
