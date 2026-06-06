//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.enchantment;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class ProtectionEnchantment extends Enchantment {
    public final Type type;

    public ProtectionEnchantment(Enchantment.Rarity p_45126_, Type p_45127_, EquipmentSlot... p_45128_) {
        super(p_45126_, p_45127_ == net.minecraft.world.item.enchantment.ProtectionEnchantment.Type.FALL ? EnchantmentCategory.ARMOR_FEET : EnchantmentCategory.ARMOR, p_45128_);
        this.type = p_45127_;
    }

    public int getMinCost(int p_45131_) {
        return this.type.getMinCost() + (p_45131_ - 1) * this.type.getLevelCost();
    }

    public int getMaxCost(int p_45144_) {
        return this.getMinCost(p_45144_) + this.type.getLevelCost();
    }

    public int getMaxLevel() {
        return 4;
    }

    public int getDamageProtection(int p_45133_, DamageSource p_45134_) {
        if (p_45134_.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return 0;
        } else if (this.type == net.minecraft.world.item.enchantment.ProtectionEnchantment.Type.ALL) {
            return p_45133_;
        } else if (this.type == net.minecraft.world.item.enchantment.ProtectionEnchantment.Type.FIRE && p_45134_.is(DamageTypeTags.IS_FIRE)) {
            return p_45133_ * 2;
        } else if (this.type == net.minecraft.world.item.enchantment.ProtectionEnchantment.Type.FALL && p_45134_.is(DamageTypeTags.IS_FALL)) {
            return p_45133_ * 3;
        } else if (this.type == net.minecraft.world.item.enchantment.ProtectionEnchantment.Type.EXPLOSION && p_45134_.is(DamageTypeTags.IS_EXPLOSION)) {
            return p_45133_ * 2;
        } else {
            return this.type == net.minecraft.world.item.enchantment.ProtectionEnchantment.Type.PROJECTILE && p_45134_.is(DamageTypeTags.IS_PROJECTILE) ? p_45133_ * 2 : 0;
        }
    }

    public boolean checkCompatibility(Enchantment p_45142_) {
        if (p_45142_ instanceof ProtectionEnchantment $$1) {
            if (this.type == $$1.type) {
                return false;
            } else {
                return this.type == net.minecraft.world.item.enchantment.ProtectionEnchantment.Type.FALL || $$1.type == net.minecraft.world.item.enchantment.ProtectionEnchantment.Type.FALL;
            }
        } else {
            return super.checkCompatibility(p_45142_);
        }
    }

    public static int getFireAfterDampener(LivingEntity p_45139_, int p_45140_) {
        int $$2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, p_45139_);
        if ($$2 > 0) {
            p_45140_ -= Mth.floor((float)p_45140_ * (float)$$2 * 0.15F);
        }

        return p_45140_;
    }

    public static double getExplosionKnockbackAfterDampener(LivingEntity p_45136_, double p_45137_) {
        int $$2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, p_45136_);
        if ($$2 > 0) {
            p_45137_ *= Mth.clamp(1.0 - (double)$$2 * 0.15, 0.0, 1.0);
        }

        return p_45137_;
    }

    public static enum Type {
        ALL(1, 11),
        FIRE(10, 8),
        FALL(5, 6),
        EXPLOSION(5, 8),
        PROJECTILE(3, 6);

        private final int minCost;
        private final int levelCost;

        private Type(int p_151299_, int p_151300_) {
            this.minCost = p_151299_;
            this.levelCost = p_151300_;
        }

        public int getMinCost() {
            return this.minCost;
        }

        public int getLevelCost() {
            return this.levelCost;
        }
    }
}
