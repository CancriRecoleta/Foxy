//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class HoverEvent {
    static final Logger LOGGER = LogUtils.getLogger();
    private final Action<?> action;
    private final Object value;

    public <T> HoverEvent(Action<T> p_130818_, T p_130819_) {
        this.action = p_130818_;
        this.value = p_130819_;
    }

    public Action<?> getAction() {
        return this.action;
    }

    @Nullable
    public <T> T getValue(Action<T> p_130824_) {
        return this.action == p_130824_ ? p_130824_.cast(this.value) : null;
    }

    public boolean equals(Object p_130828_) {
        if (this == p_130828_) {
            return true;
        } else if (p_130828_ != null && this.getClass() == p_130828_.getClass()) {
            HoverEvent $$1 = (HoverEvent)p_130828_;
            return this.action == $$1.action && Objects.equals(this.value, $$1.value);
        } else {
            return false;
        }
    }

    public String toString() {
        return "HoverEvent{action=" + this.action + ", value='" + this.value + "'}";
    }

    public int hashCode() {
        int $$0 = this.action.hashCode();
        $$0 = 31 * $$0 + (this.value != null ? this.value.hashCode() : 0);
        return $$0;
    }

    @Nullable
    public static HoverEvent deserialize(JsonObject p_130822_) {
        String $$1 = GsonHelper.getAsString(p_130822_, "action", (String)null);
        if ($$1 == null) {
            return null;
        } else {
            Action<?> $$2 = net.minecraft.network.chat.HoverEvent.Action.getByName($$1);
            if ($$2 == null) {
                return null;
            } else {
                JsonElement $$3 = p_130822_.get("contents");
                if ($$3 != null) {
                    return $$2.deserialize($$3);
                } else {
                    Component $$4 = Serializer.fromJson(p_130822_.get("value"));
                    return $$4 != null ? $$2.deserializeFromLegacy($$4) : null;
                }
            }
        }
    }

    public JsonObject serialize() {
        JsonObject $$0 = new JsonObject();
        $$0.addProperty("action", this.action.getName());
        $$0.add("contents", this.action.serializeArg(this.value));
        return $$0;
    }

    public static class Action<T> {
        public static final Action<Component> SHOW_TEXT = new Action("show_text", true, Component.Serializer::fromJson, Component.Serializer::toJsonTree, Function.identity());
        public static final Action<ItemStackInfo> SHOW_ITEM = new Action("show_item", true, ItemStackInfo::create, ItemStackInfo::serialize, ItemStackInfo::create);
        public static final Action<EntityTooltipInfo> SHOW_ENTITY = new Action("show_entity", true, EntityTooltipInfo::create, EntityTooltipInfo::serialize, EntityTooltipInfo::create);
        private static final Map<String, Action<?>> LOOKUP;
        private final String name;
        private final boolean allowFromServer;
        private final Function<JsonElement, T> argDeserializer;
        private final Function<T, JsonElement> argSerializer;
        private final Function<Component, T> legacyArgDeserializer;

        public Action(String p_130842_, boolean p_130843_, Function<JsonElement, T> p_130844_, Function<T, JsonElement> p_130845_, Function<Component, T> p_130846_) {
            this.name = p_130842_;
            this.allowFromServer = p_130843_;
            this.argDeserializer = p_130844_;
            this.argSerializer = p_130845_;
            this.legacyArgDeserializer = p_130846_;
        }

        public boolean isAllowedFromServer() {
            return this.allowFromServer;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static Action<?> getByName(String p_130853_) {
            return (Action)LOOKUP.get(p_130853_);
        }

        T cast(Object p_130865_) {
            return p_130865_;
        }

        @Nullable
        public HoverEvent deserialize(JsonElement p_130849_) {
            T $$1 = this.argDeserializer.apply(p_130849_);
            return $$1 == null ? null : new HoverEvent(this, $$1);
        }

        @Nullable
        public HoverEvent deserializeFromLegacy(Component p_130855_) {
            T $$1 = this.legacyArgDeserializer.apply(p_130855_);
            return $$1 == null ? null : new HoverEvent(this, $$1);
        }

        public JsonElement serializeArg(Object p_130851_) {
            return (JsonElement)this.argSerializer.apply(this.cast(p_130851_));
        }

        public String toString() {
            return "<action " + this.name + ">";
        }

        static {
            LOOKUP = (Map)Stream.of(SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY).collect(ImmutableMap.toImmutableMap(Action::getName, (p_178444_) -> {
                return p_178444_;
            }));
        }
    }

    public static class ItemStackInfo {
        private final Item item;
        private final int count;
        @Nullable
        private final CompoundTag tag;
        @Nullable
        private ItemStack itemStack;

        ItemStackInfo(Item p_130893_, int p_130894_, @Nullable CompoundTag p_130895_) {
            this.item = p_130893_;
            this.count = p_130894_;
            this.tag = p_130895_;
        }

        public ItemStackInfo(ItemStack p_130897_) {
            this(p_130897_.getItem(), p_130897_.getCount(), p_130897_.getTag() != null ? p_130897_.getTag().copy() : null);
        }

        public boolean equals(Object p_130911_) {
            if (this == p_130911_) {
                return true;
            } else if (p_130911_ != null && this.getClass() == p_130911_.getClass()) {
                ItemStackInfo $$1 = (ItemStackInfo)p_130911_;
                return this.count == $$1.count && this.item.equals($$1.item) && Objects.equals(this.tag, $$1.tag);
            } else {
                return false;
            }
        }

        public int hashCode() {
            int $$0 = this.item.hashCode();
            $$0 = 31 * $$0 + this.count;
            $$0 = 31 * $$0 + (this.tag != null ? this.tag.hashCode() : 0);
            return $$0;
        }

        public ItemStack getItemStack() {
            if (this.itemStack == null) {
                this.itemStack = new ItemStack(this.item, this.count);
                if (this.tag != null) {
                    this.itemStack.setTag(this.tag);
                }
            }

            return this.itemStack;
        }

        private static ItemStackInfo create(JsonElement p_130907_) {
            if (p_130907_.isJsonPrimitive()) {
                return new ItemStackInfo((Item)BuiltInRegistries.ITEM.get(new ResourceLocation(p_130907_.getAsString())), 1, (CompoundTag)null);
            } else {
                JsonObject $$1 = GsonHelper.convertToJsonObject(p_130907_, "item");
                Item $$2 = (Item)BuiltInRegistries.ITEM.get(new ResourceLocation(GsonHelper.getAsString($$1, "id")));
                int $$3 = GsonHelper.getAsInt($$1, "count", 1);
                if ($$1.has("tag")) {
                    String $$4 = GsonHelper.getAsString($$1, "tag");

                    try {
                        CompoundTag $$5 = TagParser.parseTag($$4);
                        return new ItemStackInfo($$2, $$3, $$5);
                    } catch (CommandSyntaxException var6) {
                        CommandSyntaxException $$6 = var6;
                        HoverEvent.LOGGER.warn("Failed to parse tag: {}", $$4, $$6);
                    }
                }

                return new ItemStackInfo($$2, $$3, (CompoundTag)null);
            }
        }

        @Nullable
        private static ItemStackInfo create(Component p_130909_) {
            try {
                CompoundTag $$1 = TagParser.parseTag(p_130909_.getString());
                return new ItemStackInfo(ItemStack.of($$1));
            } catch (CommandSyntaxException var2) {
                CommandSyntaxException $$2 = var2;
                HoverEvent.LOGGER.warn("Failed to parse item tag: {}", p_130909_, $$2);
                return null;
            }
        }

        private JsonElement serialize() {
            JsonObject $$0 = new JsonObject();
            $$0.addProperty("id", BuiltInRegistries.ITEM.getKey(this.item).toString());
            if (this.count != 1) {
                $$0.addProperty("count", this.count);
            }

            if (this.tag != null) {
                $$0.addProperty("tag", this.tag.toString());
            }

            return $$0;
        }
    }

    public static class EntityTooltipInfo {
        public final EntityType<?> type;
        public final UUID id;
        @Nullable
        public final Component name;
        @Nullable
        private List<Component> linesCache;

        public EntityTooltipInfo(EntityType<?> p_130876_, UUID p_130877_, @Nullable Component p_130878_) {
            this.type = p_130876_;
            this.id = p_130877_;
            this.name = p_130878_;
        }

        @Nullable
        public static EntityTooltipInfo create(JsonElement p_130881_) {
            if (!p_130881_.isJsonObject()) {
                return null;
            } else {
                JsonObject $$1 = p_130881_.getAsJsonObject();
                EntityType<?> $$2 = (EntityType)BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(GsonHelper.getAsString($$1, "type")));
                UUID $$3 = UUID.fromString(GsonHelper.getAsString($$1, "id"));
                Component $$4 = Serializer.fromJson($$1.get("name"));
                return new EntityTooltipInfo($$2, $$3, $$4);
            }
        }

        @Nullable
        public static EntityTooltipInfo create(Component p_130883_) {
            try {
                CompoundTag $$1 = TagParser.parseTag(p_130883_.getString());
                Component $$2 = Serializer.fromJson($$1.getString("name"));
                EntityType<?> $$3 = (EntityType)BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation($$1.getString("type")));
                UUID $$4 = UUID.fromString($$1.getString("id"));
                return new EntityTooltipInfo($$3, $$4, $$2);
            } catch (Exception var5) {
                return null;
            }
        }

        public JsonElement serialize() {
            JsonObject $$0 = new JsonObject();
            $$0.addProperty("type", BuiltInRegistries.ENTITY_TYPE.getKey(this.type).toString());
            $$0.addProperty("id", this.id.toString());
            if (this.name != null) {
                $$0.add("name", Serializer.toJsonTree(this.name));
            }

            return $$0;
        }

        public List<Component> getTooltipLines() {
            if (this.linesCache == null) {
                this.linesCache = Lists.newArrayList();
                if (this.name != null) {
                    this.linesCache.add(this.name);
                }

                this.linesCache.add(Component.translatable("gui.entity_tooltip.type", this.type.getDescription()));
                this.linesCache.add(Component.literal(this.id.toString()));
            }

            return this.linesCache;
        }

        public boolean equals(Object p_130886_) {
            if (this == p_130886_) {
                return true;
            } else if (p_130886_ != null && this.getClass() == p_130886_.getClass()) {
                EntityTooltipInfo $$1 = (EntityTooltipInfo)p_130886_;
                return this.type.equals($$1.type) && this.id.equals($$1.id) && Objects.equals(this.name, $$1.name);
            } else {
                return false;
            }
        }

        public int hashCode() {
            int $$0 = this.type.hashCode();
            $$0 = 31 * $$0 + this.id.hashCode();
            $$0 = 31 * $$0 + (this.name != null ? this.name.hashCode() : 0);
            return $$0;
        }
    }
}
