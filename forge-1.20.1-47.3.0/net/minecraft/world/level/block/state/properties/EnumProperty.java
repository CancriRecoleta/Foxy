//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.StringRepresentable;

public class EnumProperty<T extends Enum<T> & StringRepresentable> extends Property<T> {
    private final ImmutableSet<T> values;
    private final Map<String, T> names = Maps.newHashMap();

    protected EnumProperty(String p_61579_, Class<T> p_61580_, Collection<T> p_61581_) {
        super(p_61579_, p_61580_);
        this.values = ImmutableSet.copyOf(p_61581_);
        Iterator var4 = p_61581_.iterator();

        while(var4.hasNext()) {
            T $$3 = (Enum)var4.next();
            String $$4 = ((StringRepresentable)$$3).getSerializedName();
            if (this.names.containsKey($$4)) {
                throw new IllegalArgumentException("Multiple values have the same name '" + $$4 + "'");
            }

            this.names.put($$4, $$3);
        }

    }

    public Collection<T> getPossibleValues() {
        return this.values;
    }

    public Optional<T> getValue(String p_61604_) {
        return Optional.ofNullable((Enum)this.names.get(p_61604_));
    }

    public String getName(T p_61586_) {
        return ((StringRepresentable)p_61586_).getSerializedName();
    }

    public boolean equals(Object p_61606_) {
        if (this == p_61606_) {
            return true;
        } else if (p_61606_ instanceof EnumProperty && super.equals(p_61606_)) {
            EnumProperty<?> $$1 = (EnumProperty)p_61606_;
            return this.values.equals($$1.values) && this.names.equals($$1.names);
        } else {
            return false;
        }
    }

    public int generateHashCode() {
        int $$0 = super.generateHashCode();
        $$0 = 31 * $$0 + this.values.hashCode();
        $$0 = 31 * $$0 + this.names.hashCode();
        return $$0;
    }

    public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String p_61588_, Class<T> p_61589_) {
        return create(p_61588_, p_61589_, (p_187560_) -> {
            return true;
        });
    }

    public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String p_61595_, Class<T> p_61596_, Predicate<T> p_61597_) {
        return create(p_61595_, p_61596_, (Collection)Arrays.stream((Enum[])p_61596_.getEnumConstants()).filter(p_61597_).collect(Collectors.toList()));
    }

    public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String p_61599_, Class<T> p_61600_, T... p_61601_) {
        return create(p_61599_, p_61600_, (Collection)Lists.newArrayList(p_61601_));
    }

    public static <T extends Enum<T> & StringRepresentable> EnumProperty<T> create(String p_61591_, Class<T> p_61592_, Collection<T> p_61593_) {
        return new EnumProperty(p_61591_, p_61592_, p_61593_);
    }
}
