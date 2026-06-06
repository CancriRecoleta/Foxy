//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class FluidPredicate {
    public static final FluidPredicate ANY;
    @Nullable
    private final TagKey<Fluid> tag;
    @Nullable
    private final Fluid fluid;
    private final StatePropertiesPredicate properties;

    public FluidPredicate(@Nullable TagKey<Fluid> p_204102_, @Nullable Fluid p_204103_, StatePropertiesPredicate p_204104_) {
        this.tag = p_204102_;
        this.fluid = p_204103_;
        this.properties = p_204104_;
    }

    public boolean matches(ServerLevel p_41105_, BlockPos p_41106_) {
        if (this == ANY) {
            return true;
        } else if (!p_41105_.isLoaded(p_41106_)) {
            return false;
        } else {
            FluidState $$2 = p_41105_.getFluidState(p_41106_);
            if (this.tag != null && !$$2.is(this.tag)) {
                return false;
            } else if (this.fluid != null && !$$2.is(this.fluid)) {
                return false;
            } else {
                return this.properties.matches($$2);
            }
        }
    }

    public static FluidPredicate fromJson(@Nullable JsonElement p_41108_) {
        if (p_41108_ != null && !p_41108_.isJsonNull()) {
            JsonObject $$1 = GsonHelper.convertToJsonObject(p_41108_, "fluid");
            Fluid $$2 = null;
            if ($$1.has("fluid")) {
                ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString($$1, "fluid"));
                $$2 = (Fluid)BuiltInRegistries.FLUID.get($$3);
            }

            TagKey<Fluid> $$4 = null;
            if ($$1.has("tag")) {
                ResourceLocation $$5 = new ResourceLocation(GsonHelper.getAsString($$1, "tag"));
                $$4 = TagKey.create(Registries.FLUID, $$5);
            }

            StatePropertiesPredicate $$6 = StatePropertiesPredicate.fromJson($$1.get("state"));
            return new FluidPredicate($$4, $$2, $$6);
        } else {
            return ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject $$0 = new JsonObject();
            if (this.fluid != null) {
                $$0.addProperty("fluid", BuiltInRegistries.FLUID.getKey(this.fluid).toString());
            }

            if (this.tag != null) {
                $$0.addProperty("tag", this.tag.location().toString());
            }

            $$0.add("state", this.properties.serializeToJson());
            return $$0;
        }
    }

    static {
        ANY = new FluidPredicate((TagKey)null, (Fluid)null, StatePropertiesPredicate.ANY);
    }

    public static class Builder {
        @Nullable
        private Fluid fluid;
        @Nullable
        private TagKey<Fluid> fluids;
        private StatePropertiesPredicate properties;

        private Builder() {
            this.properties = StatePropertiesPredicate.ANY;
        }

        public static Builder fluid() {
            return new Builder();
        }

        public Builder of(Fluid p_151172_) {
            this.fluid = p_151172_;
            return this;
        }

        public Builder of(TagKey<Fluid> p_204106_) {
            this.fluids = p_204106_;
            return this;
        }

        public Builder setProperties(StatePropertiesPredicate p_151170_) {
            this.properties = p_151170_;
            return this;
        }

        public FluidPredicate build() {
            return new FluidPredicate(this.fluids, this.fluid, this.properties);
        }
    }
}
