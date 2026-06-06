//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetAttributesFunction extends LootItemConditionalFunction {
    final List<Modifier> modifiers;

    SetAttributesFunction(LootItemCondition[] p_80833_, List<Modifier> p_80834_) {
        super(p_80833_);
        this.modifiers = ImmutableList.copyOf(p_80834_);
    }

    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_ATTRIBUTES;
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return (Set)this.modifiers.stream().flatMap((p_279080_) -> {
            return p_279080_.amount.getReferencedContextParams().stream();
        }).collect(ImmutableSet.toImmutableSet());
    }

    public ItemStack run(ItemStack p_80840_, LootContext p_80841_) {
        RandomSource $$2 = p_80841_.getRandom();
        Iterator var4 = this.modifiers.iterator();

        while(var4.hasNext()) {
            Modifier $$3 = (Modifier)var4.next();
            UUID $$4 = $$3.id;
            if ($$4 == null) {
                $$4 = UUID.randomUUID();
            }

            EquipmentSlot $$5 = (EquipmentSlot)Util.getRandom((Object[])$$3.slots, $$2);
            p_80840_.addAttributeModifier($$3.attribute, new AttributeModifier($$4, $$3.name, (double)$$3.amount.getFloat(p_80841_), $$3.operation), $$5);
        }

        return p_80840_;
    }

    public static ModifierBuilder modifier(String p_165236_, Attribute p_165237_, AttributeModifier.Operation p_165238_, NumberProvider p_165239_) {
        return new ModifierBuilder(p_165236_, p_165237_, p_165238_, p_165239_);
    }

    public static Builder setAttributes() {
        return new Builder();
    }

    private static class Modifier {
        final String name;
        final Attribute attribute;
        final AttributeModifier.Operation operation;
        final NumberProvider amount;
        @Nullable
        final UUID id;
        final EquipmentSlot[] slots;

        Modifier(String p_165250_, Attribute p_165251_, AttributeModifier.Operation p_165252_, NumberProvider p_165253_, EquipmentSlot[] p_165254_, @Nullable UUID p_165255_) {
            this.name = p_165250_;
            this.attribute = p_165251_;
            this.operation = p_165252_;
            this.amount = p_165253_;
            this.id = p_165255_;
            this.slots = p_165254_;
        }

        public JsonObject serialize(JsonSerializationContext p_80866_) {
            JsonObject $$1 = new JsonObject();
            $$1.addProperty("name", this.name);
            $$1.addProperty("attribute", BuiltInRegistries.ATTRIBUTE.getKey(this.attribute).toString());
            $$1.addProperty("operation", operationToString(this.operation));
            $$1.add("amount", p_80866_.serialize(this.amount));
            if (this.id != null) {
                $$1.addProperty("id", this.id.toString());
            }

            if (this.slots.length == 1) {
                $$1.addProperty("slot", this.slots[0].getName());
            } else {
                JsonArray $$2 = new JsonArray();
                EquipmentSlot[] var4 = this.slots;
                int var5 = var4.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    EquipmentSlot $$3 = var4[var6];
                    $$2.add(new JsonPrimitive($$3.getName()));
                }

                $$1.add("slot", $$2);
            }

            return $$1;
        }

        public static Modifier deserialize(JsonObject p_80863_, JsonDeserializationContext p_80864_) {
            String $$2 = GsonHelper.getAsString(p_80863_, "name");
            ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString(p_80863_, "attribute"));
            Attribute $$4 = (Attribute)BuiltInRegistries.ATTRIBUTE.get($$3);
            if ($$4 == null) {
                throw new JsonSyntaxException("Unknown attribute: " + $$3);
            } else {
                AttributeModifier.Operation $$5 = operationFromString(GsonHelper.getAsString(p_80863_, "operation"));
                NumberProvider $$6 = (NumberProvider)GsonHelper.getAsObject(p_80863_, "amount", p_80864_, NumberProvider.class);
                UUID $$7 = null;
                EquipmentSlot[] $$10;
                if (GsonHelper.isStringValue(p_80863_, "slot")) {
                    $$10 = new EquipmentSlot[]{EquipmentSlot.byName(GsonHelper.getAsString(p_80863_, "slot"))};
                } else {
                    if (!GsonHelper.isArrayNode(p_80863_, "slot")) {
                        throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
                    }

                    JsonArray $$9 = GsonHelper.getAsJsonArray(p_80863_, "slot");
                    $$10 = new EquipmentSlot[$$9.size()];
                    int $$11 = 0;

                    JsonElement $$12;
                    for(Iterator var11 = $$9.iterator(); var11.hasNext(); $$10[$$11++] = EquipmentSlot.byName(GsonHelper.convertToString($$12, "slot"))) {
                        $$12 = (JsonElement)var11.next();
                    }

                    if ($$10.length == 0) {
                        throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
                    }
                }

                if (p_80863_.has("id")) {
                    String $$14 = GsonHelper.getAsString(p_80863_, "id");

                    try {
                        $$7 = UUID.fromString($$14);
                    } catch (IllegalArgumentException var13) {
                        throw new JsonSyntaxException("Invalid attribute modifier id '" + $$14 + "' (must be UUID format, with dashes)");
                    }
                }

                return new Modifier($$2, $$4, $$5, $$6, $$10, $$7);
            }
        }

        private static String operationToString(AttributeModifier.Operation p_80861_) {
            switch (p_80861_) {
                case ADDITION -> return "addition";
                case MULTIPLY_BASE -> return "multiply_base";
                case MULTIPLY_TOTAL -> return "multiply_total";
                default -> throw new IllegalArgumentException("Unknown operation " + p_80861_);
            }
        }

        private static AttributeModifier.Operation operationFromString(String p_80870_) {
            switch (p_80870_) {
                case "addition" -> return Operation.ADDITION;
                case "multiply_base" -> return Operation.MULTIPLY_BASE;
                case "multiply_total" -> return Operation.MULTIPLY_TOTAL;
                default -> throw new JsonSyntaxException("Unknown attribute modifier operation " + p_80870_);
            }
        }
    }

    public static class ModifierBuilder {
        private final String name;
        private final Attribute attribute;
        private final AttributeModifier.Operation operation;
        private final NumberProvider amount;
        @Nullable
        private UUID id;
        private final Set<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);

        public ModifierBuilder(String p_165263_, Attribute p_165264_, AttributeModifier.Operation p_165265_, NumberProvider p_165266_) {
            this.name = p_165263_;
            this.attribute = p_165264_;
            this.operation = p_165265_;
            this.amount = p_165266_;
        }

        public ModifierBuilder forSlot(EquipmentSlot p_165269_) {
            this.slots.add(p_165269_);
            return this;
        }

        public ModifierBuilder withUuid(UUID p_165271_) {
            this.id = p_165271_;
            return this;
        }

        public Modifier build() {
            return new Modifier(this.name, this.attribute, this.operation, this.amount, (EquipmentSlot[])this.slots.toArray(new EquipmentSlot[0]), this.id);
        }
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final List<Modifier> modifiers = Lists.newArrayList();

        public Builder() {
        }

        protected Builder getThis() {
            return this;
        }

        public Builder withModifier(ModifierBuilder p_165246_) {
            this.modifiers.add(p_165246_.build());
            return this;
        }

        public LootItemFunction build() {
            return new SetAttributesFunction(this.getConditions(), this.modifiers);
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetAttributesFunction> {
        public Serializer() {
        }

        public void serialize(JsonObject p_80891_, SetAttributesFunction p_80892_, JsonSerializationContext p_80893_) {
            super.serialize(p_80891_, (LootItemConditionalFunction)p_80892_, p_80893_);
            JsonArray $$3 = new JsonArray();
            Iterator var5 = p_80892_.modifiers.iterator();

            while(var5.hasNext()) {
                Modifier $$4 = (Modifier)var5.next();
                $$3.add($$4.serialize(p_80893_));
            }

            p_80891_.add("modifiers", $$3);
        }

        public SetAttributesFunction deserialize(JsonObject p_80883_, JsonDeserializationContext p_80884_, LootItemCondition[] p_80885_) {
            JsonArray $$3 = GsonHelper.getAsJsonArray(p_80883_, "modifiers");
            List<Modifier> $$4 = Lists.newArrayListWithExpectedSize($$3.size());
            Iterator var6 = $$3.iterator();

            while(var6.hasNext()) {
                JsonElement $$5 = (JsonElement)var6.next();
                $$4.add(net.minecraft.world.level.storage.loot.functions.SetAttributesFunction.Modifier.deserialize(GsonHelper.convertToJsonObject($$5, "modifier"), p_80884_));
            }

            if ($$4.isEmpty()) {
                throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
            } else {
                return new SetAttributesFunction(p_80885_, $$4);
            }
        }
    }
}
