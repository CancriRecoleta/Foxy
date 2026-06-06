//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;

public class AttributeSupplier {
    private final Map<Attribute, AttributeInstance> instances;

    public AttributeSupplier(Map<Attribute, AttributeInstance> p_22243_) {
        this.instances = ImmutableMap.copyOf(p_22243_);
    }

    private AttributeInstance getAttributeInstance(Attribute p_22261_) {
        AttributeInstance attributeinstance = (AttributeInstance)this.instances.get(p_22261_);
        if (attributeinstance == null) {
            throw new IllegalArgumentException("Can't find attribute " + BuiltInRegistries.ATTRIBUTE.getKey(p_22261_));
        } else {
            return attributeinstance;
        }
    }

    public double getValue(Attribute p_22246_) {
        return this.getAttributeInstance(p_22246_).getValue();
    }

    public double getBaseValue(Attribute p_22254_) {
        return this.getAttributeInstance(p_22254_).getBaseValue();
    }

    public double getModifierValue(Attribute p_22248_, UUID p_22249_) {
        AttributeModifier attributemodifier = this.getAttributeInstance(p_22248_).getModifier(p_22249_);
        if (attributemodifier == null) {
            throw new IllegalArgumentException("Can't find modifier " + p_22249_ + " on attribute " + BuiltInRegistries.ATTRIBUTE.getKey(p_22248_));
        } else {
            return attributemodifier.getAmount();
        }
    }

    @Nullable
    public AttributeInstance createInstance(Consumer<AttributeInstance> p_22251_, Attribute p_22252_) {
        AttributeInstance attributeinstance = (AttributeInstance)this.instances.get(p_22252_);
        if (attributeinstance == null) {
            return null;
        } else {
            AttributeInstance attributeinstance1 = new AttributeInstance(p_22252_, p_22251_);
            attributeinstance1.replaceFrom(attributeinstance);
            return attributeinstance1;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean hasAttribute(Attribute p_22259_) {
        return this.instances.containsKey(p_22259_);
    }

    public boolean hasModifier(Attribute p_22256_, UUID p_22257_) {
        AttributeInstance attributeinstance = (AttributeInstance)this.instances.get(p_22256_);
        return attributeinstance != null && attributeinstance.getModifier(p_22257_) != null;
    }

    public static class Builder {
        private final Map<Attribute, AttributeInstance> builder = Maps.newHashMap();
        private boolean instanceFrozen;
        private final List<Builder> others = new ArrayList();

        public Builder() {
        }

        public Builder(AttributeSupplier attributeMap) {
            this.builder.putAll(attributeMap.instances);
        }

        public void combine(Builder other) {
            this.builder.putAll(other.builder);
            this.others.add(other);
        }

        public boolean hasAttribute(Attribute attribute) {
            return this.builder.containsKey(attribute);
        }

        private AttributeInstance create(Attribute p_22275_) {
            AttributeInstance attributeinstance = new AttributeInstance(p_22275_, (p_258260_) -> {
                if (this.instanceFrozen) {
                    throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + BuiltInRegistries.ATTRIBUTE.getKey(p_22275_));
                }
            });
            this.builder.put(p_22275_, attributeinstance);
            return attributeinstance;
        }

        public Builder add(Attribute p_22267_) {
            this.create(p_22267_);
            return this;
        }

        public Builder add(Attribute p_22269_, double p_22270_) {
            AttributeInstance attributeinstance = this.create(p_22269_);
            attributeinstance.setBaseValue(p_22270_);
            return this;
        }

        public AttributeSupplier build() {
            this.instanceFrozen = true;
            this.others.forEach((p_70141_) -> {
                p_70141_.instanceFrozen = true;
            });
            return new AttributeSupplier(this.builder);
        }
    }
}
