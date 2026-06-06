//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class BlockEntityUUIDFix extends AbstractUUIDFix {
    public BlockEntityUUIDFix(Schema p_14883_) {
        super(p_14883_, References.BLOCK_ENTITY);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("BlockEntityUUIDFix", this.getInputSchema().getType(this.typeReference), (p_14885_) -> {
            p_14885_ = this.updateNamedChoice(p_14885_, "minecraft:conduit", this::updateConduit);
            p_14885_ = this.updateNamedChoice(p_14885_, "minecraft:skull", this::updateSkull);
            return p_14885_;
        });
    }

    private Dynamic<?> updateSkull(Dynamic<?> p_14890_) {
        return (Dynamic)p_14890_.get("Owner").get().map((p_14894_) -> {
            return (Dynamic)replaceUUIDString(p_14894_, "Id", "Id").orElse(p_14894_);
        }).map((p_14888_) -> {
            return p_14890_.remove("Owner").set("SkullOwner", p_14888_);
        }).result().orElse(p_14890_);
    }

    private Dynamic<?> updateConduit(Dynamic<?> p_14892_) {
        return (Dynamic)replaceUUIDMLTag(p_14892_, "target_uuid", "Target").orElse(p_14892_);
    }
}
