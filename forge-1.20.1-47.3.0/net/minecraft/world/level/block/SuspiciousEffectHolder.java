//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface SuspiciousEffectHolder {
    MobEffect getSuspiciousEffect();

    int getEffectDuration();

    static List<SuspiciousEffectHolder> getAllEffectHolders() {
        return (List)BuiltInRegistries.ITEM.stream().map(SuspiciousEffectHolder::tryGet).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Nullable
    static SuspiciousEffectHolder tryGet(ItemLike p_259322_) {
        Item var3 = p_259322_.asItem();
        if (var3 instanceof BlockItem $$1) {
            Block var6 = $$1.getBlock();
            if (var6 instanceof SuspiciousEffectHolder $$2) {
                return $$2;
            }
        }

        Item var2 = p_259322_.asItem();
        if (var2 instanceof SuspiciousEffectHolder $$3) {
            return $$3;
        } else {
            return null;
        }
    }
}
