//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds.Doubles;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class DistancePredicate {
    public static final DistancePredicate ANY;
    private final MinMaxBounds.Doubles x;
    private final MinMaxBounds.Doubles y;
    private final MinMaxBounds.Doubles z;
    private final MinMaxBounds.Doubles horizontal;
    private final MinMaxBounds.Doubles absolute;

    public DistancePredicate(MinMaxBounds.Doubles p_26249_, MinMaxBounds.Doubles p_26250_, MinMaxBounds.Doubles p_26251_, MinMaxBounds.Doubles p_26252_, MinMaxBounds.Doubles p_26253_) {
        this.x = p_26249_;
        this.y = p_26250_;
        this.z = p_26251_;
        this.horizontal = p_26252_;
        this.absolute = p_26253_;
    }

    public static DistancePredicate horizontal(MinMaxBounds.Doubles p_148837_) {
        return new DistancePredicate(Doubles.ANY, Doubles.ANY, Doubles.ANY, p_148837_, Doubles.ANY);
    }

    public static DistancePredicate vertical(MinMaxBounds.Doubles p_148839_) {
        return new DistancePredicate(Doubles.ANY, p_148839_, Doubles.ANY, Doubles.ANY, Doubles.ANY);
    }

    public static DistancePredicate absolute(MinMaxBounds.Doubles p_148841_) {
        return new DistancePredicate(Doubles.ANY, Doubles.ANY, Doubles.ANY, Doubles.ANY, p_148841_);
    }

    public boolean matches(double p_26256_, double p_26257_, double p_26258_, double p_26259_, double p_26260_, double p_26261_) {
        float $$6 = (float)(p_26256_ - p_26259_);
        float $$7 = (float)(p_26257_ - p_26260_);
        float $$8 = (float)(p_26258_ - p_26261_);
        if (this.x.matches((double)Mth.abs($$6)) && this.y.matches((double)Mth.abs($$7)) && this.z.matches((double)Mth.abs($$8))) {
            if (!this.horizontal.matchesSqr((double)($$6 * $$6 + $$8 * $$8))) {
                return false;
            } else {
                return this.absolute.matchesSqr((double)($$6 * $$6 + $$7 * $$7 + $$8 * $$8));
            }
        } else {
            return false;
        }
    }

    public static DistancePredicate fromJson(@Nullable JsonElement p_26265_) {
        if (p_26265_ != null && !p_26265_.isJsonNull()) {
            JsonObject $$1 = GsonHelper.convertToJsonObject(p_26265_, "distance");
            MinMaxBounds.Doubles $$2 = Doubles.fromJson($$1.get("x"));
            MinMaxBounds.Doubles $$3 = Doubles.fromJson($$1.get("y"));
            MinMaxBounds.Doubles $$4 = Doubles.fromJson($$1.get("z"));
            MinMaxBounds.Doubles $$5 = Doubles.fromJson($$1.get("horizontal"));
            MinMaxBounds.Doubles $$6 = Doubles.fromJson($$1.get("absolute"));
            return new DistancePredicate($$2, $$3, $$4, $$5, $$6);
        } else {
            return ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject $$0 = new JsonObject();
            $$0.add("x", this.x.serializeToJson());
            $$0.add("y", this.y.serializeToJson());
            $$0.add("z", this.z.serializeToJson());
            $$0.add("horizontal", this.horizontal.serializeToJson());
            $$0.add("absolute", this.absolute.serializeToJson());
            return $$0;
        }
    }

    static {
        ANY = new DistancePredicate(Doubles.ANY, Doubles.ANY, Doubles.ANY, Doubles.ANY, Doubles.ANY);
    }
}
