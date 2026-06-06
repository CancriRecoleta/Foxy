//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class FunctionReference extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    final ResourceLocation name;

    FunctionReference(LootItemCondition[] p_279226_, ResourceLocation p_279246_) {
        super(p_279226_);
        this.name = p_279246_;
    }

    public LootItemFunctionType getType() {
        return LootItemFunctions.REFERENCE;
    }

    public void validate(ValidationContext p_279281_) {
        LootDataId<LootItemFunction> $$1 = new LootDataId(LootDataType.MODIFIER, this.name);
        if (p_279281_.hasVisitedElement($$1)) {
            p_279281_.reportProblem("Function " + this.name + " is recursively called");
        } else {
            super.validate(p_279281_);
            p_279281_.resolver().getElementOptional($$1).ifPresentOrElse((p_279367_) -> {
                p_279367_.validate(p_279281_.enterElement(".{" + this.name + "}", $$1));
            }, () -> {
                p_279281_.reportProblem("Unknown function table called " + this.name);
            });
        }
    }

    protected ItemStack run(ItemStack p_279458_, LootContext p_279370_) {
        LootItemFunction $$2 = (LootItemFunction)p_279370_.getResolver().getElement(LootDataType.MODIFIER, this.name);
        if ($$2 == null) {
            LOGGER.warn("Unknown function: {}", this.name);
            return p_279458_;
        } else {
            LootContext.VisitedEntry<?> $$3 = LootContext.createVisitedEntry($$2);
            if (p_279370_.pushVisitedElement($$3)) {
                ItemStack var5;
                try {
                    var5 = (ItemStack)$$2.apply(p_279458_, p_279370_);
                } finally {
                    p_279370_.popVisitedElement($$3);
                }

                return var5;
            } else {
                LOGGER.warn("Detected infinite loop in loot tables");
                return p_279458_;
            }
        }
    }

    public static LootItemConditionalFunction.Builder<?> functionReference(ResourceLocation p_279115_) {
        return simpleBuilder((p_279452_) -> {
            return new FunctionReference(p_279452_, p_279115_);
        });
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<FunctionReference> {
        public Serializer() {
        }

        public void serialize(JsonObject p_279239_, FunctionReference p_279287_, JsonSerializationContext p_279375_) {
            p_279239_.addProperty("name", p_279287_.name.toString());
        }

        public FunctionReference deserialize(JsonObject p_279189_, JsonDeserializationContext p_279307_, LootItemCondition[] p_279314_) {
            ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString(p_279189_, "name"));
            return new FunctionReference(p_279314_, $$3);
        }
    }
}
