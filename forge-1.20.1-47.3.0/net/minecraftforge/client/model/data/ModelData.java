//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model.data;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public final class ModelData {
    public static final ModelData EMPTY = builder().build();
    private final Map<ModelProperty<?>, Object> properties;

    private ModelData(Map<ModelProperty<?>, Object> properties) {
        this.properties = properties;
    }

    public Set<ModelProperty<?>> getProperties() {
        return this.properties.keySet();
    }

    public boolean has(ModelProperty<?> property) {
        return this.properties.containsKey(property);
    }

    public <T> @Nullable T get(ModelProperty<T> property) {
        return this.properties.get(property);
    }

    public Builder derive() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder((ModelData)null);
    }

    public static final class Builder {
        private final Map<ModelProperty<?>, Object> properties = new IdentityHashMap();

        private Builder(@Nullable ModelData parent) {
            if (parent != null) {
                this.properties.putAll(parent.properties);
            }

        }

        @Contract("_, _ -> this")
        public <T> Builder with(ModelProperty<T> property, T value) {
            Preconditions.checkState(property.test(value), "The provided value is invalid for this property.");
            this.properties.put(property, value);
            return this;
        }

        @Contract("-> new")
        public ModelData build() {
            return new ModelData(Collections.unmodifiableMap(this.properties));
        }
    }
}
