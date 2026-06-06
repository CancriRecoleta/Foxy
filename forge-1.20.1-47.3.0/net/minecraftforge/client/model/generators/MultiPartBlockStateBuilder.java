//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public final class MultiPartBlockStateBuilder implements IGeneratedBlockState {
    private final List<PartBuilder> parts = new ArrayList();
    private final Block owner;

    public MultiPartBlockStateBuilder(Block owner) {
        this.owner = owner;
    }

    public ConfiguredModel.Builder<PartBuilder> part() {
        return ConfiguredModel.builder(this);
    }

    MultiPartBlockStateBuilder addPart(PartBuilder part) {
        this.parts.add(part);
        return this;
    }

    public JsonObject toJson() {
        JsonArray variants = new JsonArray();
        Iterator var2 = this.parts.iterator();

        while(var2.hasNext()) {
            PartBuilder part = (PartBuilder)var2.next();
            variants.add(part.toJson());
        }

        JsonObject main = new JsonObject();
        main.add("multipart", variants);
        return main;
    }

    private static JsonObject toJson(List<PartBuilder.ConditionGroup> conditions, boolean useOr) {
        JsonObject groupJson = new JsonObject();
        JsonArray innerGroupJson = new JsonArray();
        groupJson.add(useOr ? "OR" : "AND", innerGroupJson);
        Iterator var4 = conditions.iterator();

        while(var4.hasNext()) {
            PartBuilder.ConditionGroup group = (PartBuilder.ConditionGroup)var4.next();
            innerGroupJson.add(group.toJson());
        }

        return groupJson;
    }

    private static JsonObject toJson(Multimap<Property<?>, Comparable<?>> conditions, boolean useOr) {
        JsonObject groupJson = new JsonObject();
        Iterator var3 = conditions.asMap().entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<Property<?>, Collection<Comparable<?>>> e = (Map.Entry)var3.next();
            StringBuilder activeString = new StringBuilder();

            Comparable val;
            for(Iterator var6 = ((Collection)e.getValue()).iterator(); var6.hasNext(); activeString.append(((Property)e.getKey()).getName(val))) {
                val = (Comparable)var6.next();
                if (activeString.length() > 0) {
                    activeString.append("|");
                }
            }

            groupJson.addProperty(((Property)e.getKey()).getName(), activeString.toString());
        }

        if (useOr) {
            JsonArray innerWhen = new JsonArray();
            Iterator var9 = groupJson.entrySet().iterator();

            while(var9.hasNext()) {
                Map.Entry<String, JsonElement> entry = (Map.Entry)var9.next();
                JsonObject obj = new JsonObject();
                obj.add((String)entry.getKey(), (JsonElement)entry.getValue());
                innerWhen.add(obj);
            }

            groupJson = new JsonObject();
            groupJson.add("OR", innerWhen);
        }

        return groupJson;
    }

    public class PartBuilder {
        public BlockStateProvider.ConfiguredModelList models;
        public boolean useOr;
        public final Multimap<Property<?>, Comparable<?>> conditions = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        public final List<ConditionGroup> nestedConditionGroups = new ArrayList();

        PartBuilder(BlockStateProvider.ConfiguredModelList models) {
            this.models = models;
        }

        public PartBuilder useOr() {
            this.useOr = true;
            return this;
        }

        @SafeVarargs
        public final <T extends Comparable<T>> PartBuilder condition(Property<T> prop, T... values) {
            Preconditions.checkNotNull(prop, "Property must not be null");
            Preconditions.checkNotNull(values, "Value list must not be null");
            Preconditions.checkArgument(values.length > 0, "Value list must not be empty");
            Preconditions.checkArgument(!this.conditions.containsKey(prop), "Cannot set condition for property \"%s\" more than once", prop.getName());
            Preconditions.checkArgument(this.canApplyTo(MultiPartBlockStateBuilder.this.owner), "IProperty %s is not valid for the block %s", prop, MultiPartBlockStateBuilder.this.owner);
            Preconditions.checkState(this.nestedConditionGroups.isEmpty(), "Can't have normal conditions if there are already nested condition groups");
            this.conditions.putAll(prop, Arrays.asList(values));
            return this;
        }

        public final ConditionGroup nestedGroup() {
            Preconditions.checkState(this.conditions.isEmpty(), "Can't have nested condition groups if there are already normal conditions");
            ConditionGroup group = new ConditionGroup();
            this.nestedConditionGroups.add(group);
            return group;
        }

        public MultiPartBlockStateBuilder end() {
            return MultiPartBlockStateBuilder.this;
        }

        JsonObject toJson() {
            JsonObject out = new JsonObject();
            if (!this.conditions.isEmpty()) {
                out.add("when", MultiPartBlockStateBuilder.toJson(this.conditions, this.useOr));
            } else if (!this.nestedConditionGroups.isEmpty()) {
                out.add("when", MultiPartBlockStateBuilder.toJson(this.nestedConditionGroups, this.useOr));
            }

            out.add("apply", this.models.toJSON());
            return out;
        }

        public boolean canApplyTo(Block b) {
            return b.getStateDefinition().getProperties().containsAll(this.conditions.keySet());
        }

        public class ConditionGroup {
            public final Multimap<Property<?>, Comparable<?>> conditions = MultimapBuilder.linkedHashKeys().arrayListValues().build();
            public final List<ConditionGroup> nestedConditionGroups = new ArrayList();
            private ConditionGroup parent = null;
            public boolean useOr;

            public ConditionGroup() {
            }

            @SafeVarargs
            public final <T extends Comparable<T>> ConditionGroup condition(Property<T> prop, T... values) {
                Preconditions.checkNotNull(prop, "Property must not be null");
                Preconditions.checkNotNull(values, "Value list must not be null");
                Preconditions.checkArgument(values.length > 0, "Value list must not be empty");
                Preconditions.checkArgument(!this.conditions.containsKey(prop), "Cannot set condition for property \"%s\" more than once", prop.getName());
                Preconditions.checkArgument(PartBuilder.this.canApplyTo(MultiPartBlockStateBuilder.this.owner), "IProperty %s is not valid for the block %s", prop, MultiPartBlockStateBuilder.this.owner);
                Preconditions.checkState(this.nestedConditionGroups.isEmpty(), "Can't have normal conditions if there are already nested condition groups");
                this.conditions.putAll(prop, Arrays.asList(values));
                return this;
            }

            public ConditionGroup nestedGroup() {
                Preconditions.checkState(this.conditions.isEmpty(), "Can't have nested condition groups if there are already normal conditions");
                ConditionGroup group = PartBuilder.this.new ConditionGroup();
                group.parent = this;
                this.nestedConditionGroups.add(group);
                return group;
            }

            public ConditionGroup endNestedGroup() {
                if (this.parent == null) {
                    throw new IllegalStateException("This condition group is not nested, use end() instead");
                } else {
                    return this.parent;
                }
            }

            public PartBuilder end() {
                if (this.parent != null) {
                    throw new IllegalStateException("This is a nested condition group, use endNestedGroup() instead");
                } else {
                    return PartBuilder.this;
                }
            }

            public ConditionGroup useOr() {
                this.useOr = true;
                return this;
            }

            JsonObject toJson() {
                if (!this.conditions.isEmpty()) {
                    return MultiPartBlockStateBuilder.toJson(this.conditions, this.useOr);
                } else {
                    return !this.nestedConditionGroups.isEmpty() ? MultiPartBlockStateBuilder.toJson(this.nestedConditionGroups, this.useOr) : new JsonObject();
                }
            }
        }
    }
}
