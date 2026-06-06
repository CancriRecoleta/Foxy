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

public class SlideDownBlockTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("slide_down_block");

    public SlideDownBlockTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286879_, ContextAwarePredicate p_286565_, DeserializationContext p_286581_) {
        Block $$3 = deserializeBlock(p_286879_);
        StatePropertiesPredicate $$4 = StatePropertiesPredicate.fromJson(p_286879_.get("state"));
        if ($$3 != null) {
            $$4.checkState($$3.getStateDefinition(), (p_66983_) -> {
                throw new JsonSyntaxException("Block " + $$3 + " has no property " + p_66983_);
            });
        }

        return new TriggerInstance(p_286565_, $$3, $$4);
    }

    @Nullable
    private static Block deserializeBlock(JsonObject p_66988_) {
        if (p_66988_.has("block")) {
            ResourceLocation $$1 = new ResourceLocation(GsonHelper.getAsString(p_66988_, "block"));
            return (Block)BuiltInRegistries.BLOCK.getOptional($$1).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown block type '" + $$1 + "'");
            });
        } else {
            return null;
        }
    }

    public void trigger(ServerPlayer p_66979_, BlockState p_66980_) {
        this.trigger(p_66979_, (p_66986_) -> {
            return p_66986_.matches(p_66980_);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        @Nullable
        private final Block block;
        private final StatePropertiesPredicate state;

        public TriggerInstance(ContextAwarePredicate p_286920_, @Nullable Block p_286622_, StatePropertiesPredicate p_286692_) {
            super(SlideDownBlockTrigger.ID, p_286920_);
            this.block = p_286622_;
            this.state = p_286692_;
        }

        public static TriggerInstance slidesDownBlock(Block p_67007_) {
            return new TriggerInstance(ContextAwarePredicate.ANY, p_67007_, StatePropertiesPredicate.ANY);
        }

        public JsonObject serializeToJson(SerializationContext p_67011_) {
            JsonObject $$1 = super.serializeToJson(p_67011_);
            if (this.block != null) {
                $$1.addProperty("block", BuiltInRegistries.BLOCK.getKey(this.block).toString());
            }

            $$1.add("state", this.state.serializeToJson());
            return $$1;
        }

        public boolean matches(BlockState p_67009_) {
            if (this.block != null && !p_67009_.is(this.block)) {
                return false;
            } else {
                return this.state.matches(p_67009_);
            }
        }
    }
}
