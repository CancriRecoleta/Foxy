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
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public class LighthingBoltPredicate implements EntitySubPredicate {
    private static final String BLOCKS_SET_ON_FIRE_KEY = "blocks_set_on_fire";
    private static final String ENTITY_STRUCK_KEY = "entity_struck";
    private final MinMaxBounds.Ints blocksSetOnFire;
    private final EntityPredicate entityStruck;

    private LighthingBoltPredicate(MinMaxBounds.Ints p_153239_, EntityPredicate p_153240_) {
        this.blocksSetOnFire = p_153239_;
        this.entityStruck = p_153240_;
    }

    public static LighthingBoltPredicate blockSetOnFire(MinMaxBounds.Ints p_153251_) {
        return new LighthingBoltPredicate(p_153251_, EntityPredicate.ANY);
    }

    public static LighthingBoltPredicate fromJson(JsonObject p_220333_) {
        return new LighthingBoltPredicate(Ints.fromJson(p_220333_.get("blocks_set_on_fire")), EntityPredicate.fromJson(p_220333_.get("entity_struck")));
    }

    public JsonObject serializeCustomData() {
        JsonObject $$0 = new JsonObject();
        $$0.add("blocks_set_on_fire", this.blocksSetOnFire.serializeToJson());
        $$0.add("entity_struck", this.entityStruck.serializeToJson());
        return $$0;
    }

    public EntitySubPredicate.Type type() {
        return net.minecraft.advancements.critereon.EntitySubPredicate.Types.LIGHTNING;
    }

    public boolean matches(Entity p_153247_, ServerLevel p_153248_, @Nullable Vec3 p_153249_) {
        if (!(p_153247_ instanceof LightningBolt $$3)) {
            return false;
        } else {
            return this.blocksSetOnFire.matches($$3.getBlocksSetOnFire()) && (this.entityStruck == EntityPredicate.ANY || $$3.getHitEntities().anyMatch((p_153245_) -> {
                return this.entityStruck.matches(p_153248_, p_153249_, p_153245_);
            }));
        }
    }
}
