//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;

public class StatePropertiesPredicate {
    public static final StatePropertiesPredicate ANY = new StatePropertiesPredicate(ImmutableList.of());
    private final List<PropertyMatcher> properties;

    private static PropertyMatcher fromJson(String p_67687_, JsonElement p_67688_) {
        if (p_67688_.isJsonPrimitive()) {
            String $$2 = p_67688_.getAsString();
            return new ExactPropertyMatcher(p_67687_, $$2);
        } else {
            JsonObject $$3 = GsonHelper.convertToJsonObject(p_67688_, "value");
            String $$4 = $$3.has("min") ? getStringOrNull($$3.get("min")) : null;
            String $$5 = $$3.has("max") ? getStringOrNull($$3.get("max")) : null;
            return (PropertyMatcher)($$4 != null && $$4.equals($$5) ? new ExactPropertyMatcher(p_67687_, $$4) : new RangedPropertyMatcher(p_67687_, $$4, $$5));
        }
    }

    @Nullable
    private static String getStringOrNull(JsonElement p_67690_) {
        return p_67690_.isJsonNull() ? null : p_67690_.getAsString();
    }

    StatePropertiesPredicate(List<PropertyMatcher> p_67662_) {
        this.properties = ImmutableList.copyOf(p_67662_);
    }

    public <S extends StateHolder<?, S>> boolean matches(StateDefinition<?, S> p_67670_, S p_67671_) {
        Iterator var3 = this.properties.iterator();

        PropertyMatcher $$2;
        do {
            if (!var3.hasNext()) {
                return true;
            }

            $$2 = (PropertyMatcher)var3.next();
        } while($$2.match(p_67670_, p_67671_));

        return false;
    }

    public boolean matches(BlockState p_67668_) {
        return this.matches(p_67668_.getBlock().getStateDefinition(), p_67668_);
    }

    public boolean matches(FluidState p_67685_) {
        return this.matches(p_67685_.getType().getStateDefinition(), p_67685_);
    }

    public void checkState(StateDefinition<?, ?> p_67673_, Consumer<String> p_67674_) {
        this.properties.forEach((p_67678_) -> {
            p_67678_.checkState(p_67673_, p_67674_);
        });
    }

    public static StatePropertiesPredicate fromJson(@Nullable JsonElement p_67680_) {
        if (p_67680_ != null && !p_67680_.isJsonNull()) {
            JsonObject $$1 = GsonHelper.convertToJsonObject(p_67680_, "properties");
            List<PropertyMatcher> $$2 = Lists.newArrayList();
            Iterator var3 = $$1.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<String, JsonElement> $$3 = (Map.Entry)var3.next();
                $$2.add(fromJson((String)$$3.getKey(), (JsonElement)$$3.getValue()));
            }

            return new StatePropertiesPredicate($$2);
        } else {
            return ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject $$0 = new JsonObject();
            if (!this.properties.isEmpty()) {
                this.properties.forEach((p_67683_) -> {
                    $$0.add(p_67683_.getName(), p_67683_.toJson());
                });
            }

            return $$0;
        }
    }

    static class ExactPropertyMatcher extends PropertyMatcher {
        private final String value;

        public ExactPropertyMatcher(String p_67709_, String p_67710_) {
            super(p_67709_);
            this.value = p_67710_;
        }

        protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> p_67713_, Property<T> p_67714_) {
            T $$2 = p_67713_.getValue(p_67714_);
            Optional<T> $$3 = p_67714_.getValue(this.value);
            return $$3.isPresent() && $$2.compareTo((Comparable)$$3.get()) == 0;
        }

        public JsonElement toJson() {
            return new JsonPrimitive(this.value);
        }
    }

    static class RangedPropertyMatcher extends PropertyMatcher {
        @Nullable
        private final String minValue;
        @Nullable
        private final String maxValue;

        public RangedPropertyMatcher(String p_67730_, @Nullable String p_67731_, @Nullable String p_67732_) {
            super(p_67730_);
            this.minValue = p_67731_;
            this.maxValue = p_67732_;
        }

        protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> p_67735_, Property<T> p_67736_) {
            T $$2 = p_67735_.getValue(p_67736_);
            Optional $$4;
            if (this.minValue != null) {
                $$4 = p_67736_.getValue(this.minValue);
                if (!$$4.isPresent() || $$2.compareTo((Comparable)$$4.get()) < 0) {
                    return false;
                }
            }

            if (this.maxValue != null) {
                $$4 = p_67736_.getValue(this.maxValue);
                if (!$$4.isPresent() || $$2.compareTo((Comparable)$$4.get()) > 0) {
                    return false;
                }
            }

            return true;
        }

        public JsonElement toJson() {
            JsonObject $$0 = new JsonObject();
            if (this.minValue != null) {
                $$0.addProperty("min", this.minValue);
            }

            if (this.maxValue != null) {
                $$0.addProperty("max", this.maxValue);
            }

            return $$0;
        }
    }

    private abstract static class PropertyMatcher {
        private final String name;

        public PropertyMatcher(String p_67717_) {
            this.name = p_67717_;
        }

        public <S extends StateHolder<?, S>> boolean match(StateDefinition<?, S> p_67719_, S p_67720_) {
            Property<?> $$2 = p_67719_.getProperty(this.name);
            return $$2 == null ? false : this.match(p_67720_, $$2);
        }

        protected abstract <T extends Comparable<T>> boolean match(StateHolder<?, ?> var1, Property<T> var2);

        public abstract JsonElement toJson();

        public String getName() {
            return this.name;
        }

        public void checkState(StateDefinition<?, ?> p_67722_, Consumer<String> p_67723_) {
            Property<?> $$2 = p_67722_.getProperty(this.name);
            if ($$2 == null) {
                p_67723_.accept(this.name);
            }

        }
    }

    public static class Builder {
        private final List<PropertyMatcher> matchers = Lists.newArrayList();

        private Builder() {
        }

        public static Builder properties() {
            return new Builder();
        }

        public Builder hasProperty(Property<?> p_67701_, String p_67702_) {
            this.matchers.add(new ExactPropertyMatcher(p_67701_.getName(), p_67702_));
            return this;
        }

        public Builder hasProperty(Property<Integer> p_67695_, int p_67696_) {
            return this.hasProperty(p_67695_, Integer.toString(p_67696_));
        }

        public Builder hasProperty(Property<Boolean> p_67704_, boolean p_67705_) {
            return this.hasProperty(p_67704_, Boolean.toString(p_67705_));
        }

        public <T extends Comparable<T> & StringRepresentable> Builder hasProperty(Property<T> p_67698_, T p_67699_) {
            return this.hasProperty(p_67698_, ((StringRepresentable)p_67699_).getSerializedName());
        }

        public StatePropertiesPredicate build() {
            return new StatePropertiesPredicate(this.matchers);
        }
    }
}
