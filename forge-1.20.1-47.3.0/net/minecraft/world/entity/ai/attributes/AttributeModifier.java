//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.attributes;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

public class AttributeModifier {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final double amount;
    private final Operation operation;
    private final Supplier<String> nameGetter;
    private final UUID id;

    public AttributeModifier(String p_22196_, double p_22197_, Operation p_22198_) {
        this(Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()), () -> {
            return p_22196_;
        }, p_22197_, p_22198_);
    }

    public AttributeModifier(UUID p_22200_, String p_22201_, double p_22202_, Operation p_22203_) {
        this(p_22200_, () -> {
            return p_22201_;
        }, p_22202_, p_22203_);
    }

    public AttributeModifier(UUID p_22205_, Supplier<String> p_22206_, double p_22207_, Operation p_22208_) {
        this.id = p_22205_;
        this.nameGetter = p_22206_;
        this.amount = p_22207_;
        this.operation = p_22208_;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return (String)this.nameGetter.get();
    }

    public Operation getOperation() {
        return this.operation;
    }

    public double getAmount() {
        return this.amount;
    }

    public boolean equals(Object p_22221_) {
        if (this == p_22221_) {
            return true;
        } else if (p_22221_ != null && this.getClass() == p_22221_.getClass()) {
            AttributeModifier $$1 = (AttributeModifier)p_22221_;
            return Objects.equals(this.id, $$1.id);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        double var10000 = this.amount;
        return "AttributeModifier{amount=" + var10000 + ", operation=" + this.operation + ", name='" + (String)this.nameGetter.get() + "', id=" + this.id + "}";
    }

    public CompoundTag save() {
        CompoundTag $$0 = new CompoundTag();
        $$0.putString("Name", this.getName());
        $$0.putDouble("Amount", this.amount);
        $$0.putInt("Operation", this.operation.toValue());
        $$0.putUUID("UUID", this.id);
        return $$0;
    }

    @Nullable
    public static AttributeModifier load(CompoundTag p_22213_) {
        try {
            UUID $$1 = p_22213_.getUUID("UUID");
            Operation $$2 = net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.fromValue(p_22213_.getInt("Operation"));
            return new AttributeModifier($$1, p_22213_.getString("Name"), p_22213_.getDouble("Amount"), $$2);
        } catch (Exception var3) {
            Exception $$3 = var3;
            LOGGER.warn("Unable to create attribute: {}", $$3.getMessage());
            return null;
        }
    }

    public static enum Operation {
        ADDITION(0),
        MULTIPLY_BASE(1),
        MULTIPLY_TOTAL(2);

        private static final Operation[] OPERATIONS = new Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
        private final int value;

        private Operation(int p_22234_) {
            this.value = p_22234_;
        }

        public int toValue() {
            return this.value;
        }

        public static Operation fromValue(int p_22237_) {
            if (p_22237_ >= 0 && p_22237_ < OPERATIONS.length) {
                return OPERATIONS[p_22237_];
            } else {
                throw new IllegalArgumentException("No operation with value " + p_22237_);
            }
        }
    }
}
