//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EnterBlockTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("enter_block");

    public EnterBlockTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286490_, ContextAwarePredicate p_286595_, DeserializationContext p_286764_) {
        Block $$3 = deserializeBlock(p_286490_);
        StatePropertiesPredicate $$4 = StatePropertiesPredicate.fromJson(p_286490_.get("state"));
        if ($$3 != null) {
            $$4.checkState($$3.getStateDefinition(), (p_31274_) -> {
                throw new JsonSyntaxException("Block " + $$3 + " has no property " + p_31274_);
            });
        }

        return new TriggerInstance(p_286595_, $$3, $$4);
    }

    @Nullable
    private static Block deserializeBlock(JsonObject p_31279_) {
        if (p_31279_.has("block")) {
            ResourceLocation $$1 = new ResourceLocation(GsonHelper.getAsString(p_31279_, "block"));
            return (Block)BuiltInRegistries.BLOCK.getOptional($$1).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown block type '" + $$1 + "'");
            });
        } else {
            return null;
        }
    }

    public void trigger(ServerPlayer p_31270_, BlockState p_31271_) {
        this.trigger(p_31270_, (p_31277_) -> {
            return p_31277_.matches(p_31271_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        @Nullable
        private final Block block;
        private final StatePropertiesPredicate state;

        public TriggerInstance(ContextAwarePredicate p_286269_, @Nullable Block p_286517_, StatePropertiesPredicate p_286864_) {
            super(EnterBlockTrigger.ID, p_286269_);
            this.block = p_286517_;
            this.state = p_286864_;
        }

        public static TriggerInstance entersBlock(Block p_31298_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_31298_, StatePropertiesPredicate.ANY);
        }

        public JsonObject serializeToJson(SerializationContext p_31302_) {
            JsonObject $$1 = super.serializeToJson(p_31302_);
            if (this.block != null) {
                $$1.addProperty("block", BuiltInRegistries.BLOCK.getKey(this.block).toString());
            }

            $$1.add("state", this.state.serializeToJson());
            return $$1;
        }

        public boolean matches(BlockState p_31300_) {
            if (this.block != null && !p_31300_.is(this.block)) {
                return false;
            } else {
                return this.state.matches(p_31300_);
            }
        }
    }
}
