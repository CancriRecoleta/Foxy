//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class EntityPaintingFieldsRenameFix extends NamedEntityFix {
    public EntityPaintingFieldsRenameFix(Schema p_216606_) {
        super(p_216606_, false, "EntityPaintingFieldsRenameFix", References.ENTITY, "minecraft:painting");
    }

    public Dynamic<?> fixTag(Dynamic<?> p_216610_) {
        return this.renameField(this.renameField(p_216610_, "Motive", "variant"), "Facing", "facing");
    }

    private Dynamic<?> renameField(Dynamic<?> p_216612_, String p_216613_, String p_216614_) {
        Optional<? extends Dynamic<?>> $$3 = p_216612_.get(p_216613_).result();
        Optional<? extends Dynamic<?>> $$4 = $$3.map((p_216619_) -> {
            return p_216612_.remove(p_216613_).set(p_216614_, p_216619_);
        });
        return (Dynamic)DataFixUtils.orElse($$4, p_216612_);
    }

    protected Typed<?> fix(Typed<?> p_216608_) {
        return p_216608_.update(DSL.remainderFinder(), this::fixTag);
    }
}
