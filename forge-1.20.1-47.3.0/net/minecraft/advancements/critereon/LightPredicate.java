//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;

public class LightPredicate {
    public static final LightPredicate ANY;
    private final MinMaxBounds.Ints composite;

    LightPredicate(MinMaxBounds.Ints p_51339_) {
        this.composite = p_51339_;
    }

    public boolean matches(ServerLevel p_51342_, BlockPos p_51343_) {
        if (this == ANY) {
            return true;
        } else if (!p_51342_.isLoaded(p_51343_)) {
            return false;
        } else {
            return this.composite.matches(p_51342_.getMaxLocalRawBrightness(p_51343_));
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject $$0 = new JsonObject();
            $$0.add("light", this.composite.serializeToJson());
            return $$0;
        }
    }

    public static LightPredicate fromJson(@Nullable JsonElement p_51345_) {
        if (p_51345_ != null && !p_51345_.isJsonNull()) {
            JsonObject $$1 = GsonHelper.convertToJsonObject(p_51345_, "light");
            MinMaxBounds.Ints $$2 = Ints.fromJson($$1.get("light"));
            return new LightPredicate($$2);
        } else {
            return ANY;
        }
    }

    static {
        ANY = new LightPredicate(Ints.ANY);
    }

    public static class Builder {
        private MinMaxBounds.Ints composite;

        public Builder() {
            this.composite = Ints.ANY;
        }

        public static Builder light() {
            return new Builder();
        }

        public Builder setComposite(MinMaxBounds.Ints p_153105_) {
            this.composite = p_153105_;
            return this;
        }

        public LightPredicate build() {
            return new LightPredicate(this.composite);
        }
    }
}
