//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;

public class ZombieVillagerRebuildXpFix extends NamedEntityFix {
    public ZombieVillagerRebuildXpFix(Schema p_17298_, boolean p_17299_) {
        super(p_17298_, p_17299_, "Zombie Villager XP rebuild", References.ENTITY, "minecraft:zombie_villager");
    }

    protected Typed<?> fix(Typed<?> p_17301_) {
        return p_17301_.update(DSL.remainderFinder(), (p_17303_) -> {
            Optional<Number> $$1 = p_17303_.get("Xp").asNumber().result();
            if (!$$1.isPresent()) {
                int $$2 = p_17303_.get("VillagerData").get("level").asInt(1);
                return p_17303_.set("Xp", p_17303_.createInt(VillagerRebuildLevelAndXpFix.getMinXpPerLevel($$2)));
            } else {
                return p_17303_;
            }
        });
    }
}
