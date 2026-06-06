//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.phys.Vec3;

public class SlimePredicate implements EntitySubPredicate {
    private final MinMaxBounds.Ints size;

    private SlimePredicate(MinMaxBounds.Ints p_223420_) {
        this.size = p_223420_;
    }

    public static SlimePredicate sized(MinMaxBounds.Ints p_223427_) {
        return new SlimePredicate(p_223427_);
    }

    public static SlimePredicate fromJson(JsonObject p_223429_) {
        MinMaxBounds.Ints $$1 = Ints.fromJson(p_223429_.get("size"));
        return new SlimePredicate($$1);
    }

    public JsonObject serializeCustomData() {
        JsonObject $$0 = new JsonObject();
        $$0.add("size", this.size.serializeToJson());
        return $$0;
    }

    public boolean matches(Entity p_223423_, ServerLevel p_223424_, @Nullable Vec3 p_223425_) {
        if (p_223423_ instanceof Slime $$3) {
            return this.size.matches($$3.getSize());
        } else {
            return false;
        }
    }

    public EntitySubPredicate.Type type() {
        return net.minecraft.advancements.critereon.EntitySubPredicate.Types.SLIME;
    }
}
