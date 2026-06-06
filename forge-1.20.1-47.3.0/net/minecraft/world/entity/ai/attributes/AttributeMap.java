//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class AttributeMap {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<Attribute, AttributeInstance> attributes = Maps.newHashMap();
    private final Set<AttributeInstance> dirtyAttributes = Sets.newHashSet();
    private final AttributeSupplier supplier;

    public AttributeMap(AttributeSupplier p_22144_) {
        this.supplier = p_22144_;
    }

    private void onAttributeModified(AttributeInstance p_22158_) {
        if (p_22158_.getAttribute().isClientSyncable()) {
            this.dirtyAttributes.add(p_22158_);
        }

    }

    public Set<AttributeInstance> getDirtyAttributes() {
        return this.dirtyAttributes;
    }

    public Collection<AttributeInstance> getSyncableAttributes() {
        return (Collection)this.attributes.values().stream().filter((p_22184_) -> {
            return p_22184_.getAttribute().isClientSyncable();
        }).collect(Collectors.toList());
    }

    @Nullable
    public AttributeInstance getInstance(Attribute p_22147_) {
        return (AttributeInstance)this.attributes.computeIfAbsent(p_22147_, (p_22188_) -> {
            return this.supplier.createInstance(this::onAttributeModified, p_22188_);
        });
    }

    @Nullable
    public AttributeInstance getInstance(Holder<Attribute> p_250010_) {
        return this.getInstance((Attribute)p_250010_.value());
    }

    public boolean hasAttribute(Attribute p_22172_) {
        return this.attributes.get(p_22172_) != null || this.supplier.hasAttribute(p_22172_);
    }

    public boolean hasAttribute(Holder<Attribute> p_248893_) {
        return this.hasAttribute((Attribute)p_248893_.value());
    }

    public boolean hasModifier(Attribute p_22155_, UUID p_22156_) {
        AttributeInstance $$2 = (AttributeInstance)this.attributes.get(p_22155_);
        return $$2 != null ? $$2.getModifier(p_22156_) != null : this.supplier.hasModifier(p_22155_, p_22156_);
    }

    public boolean hasModifier(Holder<Attribute> p_250299_, UUID p_250415_) {
        return this.hasModifier((Attribute)p_250299_.value(), p_250415_);
    }

    public double getValue(Attribute p_22182_) {
        AttributeInstance $$1 = (AttributeInstance)this.attributes.get(p_22182_);
        return $$1 != null ? $$1.getValue() : this.supplier.getValue(p_22182_);
    }

    public double getBaseValue(Attribute p_22186_) {
        AttributeInstance $$1 = (AttributeInstance)this.attributes.get(p_22186_);
        return $$1 != null ? $$1.getBaseValue() : this.supplier.getBaseValue(p_22186_);
    }

    public double getModifierValue(Attribute p_22174_, UUID p_22175_) {
        AttributeInstance $$2 = (AttributeInstance)this.attributes.get(p_22174_);
        return $$2 != null ? $$2.getModifier(p_22175_).getAmount() : this.supplier.getModifierValue(p_22174_, p_22175_);
    }

    public double getModifierValue(Holder<Attribute> p_251534_, UUID p_250438_) {
        return this.getModifierValue((Attribute)p_251534_.value(), p_250438_);
    }

    public void removeAttributeModifiers(Multimap<Attribute, AttributeModifier> p_22162_) {
        p_22162_.asMap().forEach((p_22152_, p_22153_) -> {
            AttributeInstance $$2 = (AttributeInstance)this.attributes.get(p_22152_);
            if ($$2 != null) {
                Objects.requireNonNull($$2);
                p_22153_.forEach($$2::removeModifier);
            }

        });
    }

    public void addTransientAttributeModifiers(Multimap<Attribute, AttributeModifier> p_22179_) {
        p_22179_.forEach((p_22149_, p_22150_) -> {
            AttributeInstance $$2 = this.getInstance(p_22149_);
            if ($$2 != null) {
                $$2.removeModifier(p_22150_);
                $$2.addTransientModifier(p_22150_);
            }

        });
    }

    public void assignValues(AttributeMap p_22160_) {
        p_22160_.attributes.values().forEach((p_22177_) -> {
            AttributeInstance $$1 = this.getInstance(p_22177_.getAttribute());
            if ($$1 != null) {
                $$1.replaceFrom(p_22177_);
            }

        });
    }

    public ListTag save() {
        ListTag $$0 = new ListTag();
        Iterator var2 = this.attributes.values().iterator();

        while(var2.hasNext()) {
            AttributeInstance $$1 = (AttributeInstance)var2.next();
            $$0.add($$1.save());
        }

        return $$0;
    }

    public void load(ListTag p_22169_) {
        for(int $$1 = 0; $$1 < p_22169_.size(); ++$$1) {
            CompoundTag $$2 = p_22169_.getCompound($$1);
            String $$3 = $$2.getString("Name");
            Util.ifElse(BuiltInRegistries.ATTRIBUTE.getOptional(ResourceLocation.tryParse($$3)), (p_22167_) -> {
                AttributeInstance $$2x = this.getInstance(p_22167_);
                if ($$2x != null) {
                    $$2x.load($$2);
                }

            }, () -> {
                LOGGER.warn("Ignoring unknown attribute '{}'", $$3);
            });
        }

    }
}
