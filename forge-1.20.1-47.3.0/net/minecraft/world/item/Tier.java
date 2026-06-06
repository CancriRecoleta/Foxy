//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public interface Tier {
    int getUses();

    float getSpeed();

    float getAttackDamageBonus();

    /** @deprecated */
    @Deprecated
    int getLevel();

    int getEnchantmentValue();

    Ingredient getRepairIngredient();

    default @Nullable TagKey<Block> getTag() {
        return null;
    }
}
