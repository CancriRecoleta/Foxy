//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.enchantment;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import net.minecraft.world.item.enchantment.ProtectionEnchantment.Type;

public class Enchantments {
    private static final EquipmentSlot[] ARMOR_SLOTS;
    public static final Enchantment ALL_DAMAGE_PROTECTION;
    public static final Enchantment FIRE_PROTECTION;
    public static final Enchantment FALL_PROTECTION;
    public static final Enchantment BLAST_PROTECTION;
    public static final Enchantment PROJECTILE_PROTECTION;
    public static final Enchantment RESPIRATION;
    public static final Enchantment AQUA_AFFINITY;
    public static final Enchantment THORNS;
    public static final Enchantment DEPTH_STRIDER;
    public static final Enchantment FROST_WALKER;
    public static final Enchantment BINDING_CURSE;
    public static final Enchantment SOUL_SPEED;
    public static final Enchantment SWIFT_SNEAK;
    public static final Enchantment SHARPNESS;
    public static final Enchantment SMITE;
    public static final Enchantment BANE_OF_ARTHROPODS;
    public static final Enchantment KNOCKBACK;
    public static final Enchantment FIRE_ASPECT;
    public static final Enchantment MOB_LOOTING;
    public static final Enchantment SWEEPING_EDGE;
    public static final Enchantment BLOCK_EFFICIENCY;
    public static final Enchantment SILK_TOUCH;
    public static final Enchantment UNBREAKING;
    public static final Enchantment BLOCK_FORTUNE;
    public static final Enchantment POWER_ARROWS;
    public static final Enchantment PUNCH_ARROWS;
    public static final Enchantment FLAMING_ARROWS;
    public static final Enchantment INFINITY_ARROWS;
    public static final Enchantment FISHING_LUCK;
    public static final Enchantment FISHING_SPEED;
    public static final Enchantment LOYALTY;
    public static final Enchantment IMPALING;
    public static final Enchantment RIPTIDE;
    public static final Enchantment CHANNELING;
    public static final Enchantment MULTISHOT;
    public static final Enchantment QUICK_CHARGE;
    public static final Enchantment PIERCING;
    public static final Enchantment MENDING;
    public static final Enchantment VANISHING_CURSE;

    public Enchantments() {
    }

    private static Enchantment register(String p_44993_, Enchantment p_44994_) {
        return (Enchantment)Registry.register(BuiltInRegistries.ENCHANTMENT, (String)p_44993_, p_44994_);
    }

    static {
        ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        ALL_DAMAGE_PROTECTION = register("protection", new ProtectionEnchantment(Rarity.COMMON, Type.ALL, ARMOR_SLOTS));
        FIRE_PROTECTION = register("fire_protection", new ProtectionEnchantment(Rarity.UNCOMMON, Type.FIRE, ARMOR_SLOTS));
        FALL_PROTECTION = register("feather_falling", new ProtectionEnchantment(Rarity.UNCOMMON, Type.FALL, ARMOR_SLOTS));
        BLAST_PROTECTION = register("blast_protection", new ProtectionEnchantment(Rarity.RARE, Type.EXPLOSION, ARMOR_SLOTS));
        PROJECTILE_PROTECTION = register("projectile_protection", new ProtectionEnchantment(Rarity.UNCOMMON, Type.PROJECTILE, ARMOR_SLOTS));
        RESPIRATION = register("respiration", new OxygenEnchantment(Rarity.RARE, ARMOR_SLOTS));
        AQUA_AFFINITY = register("aqua_affinity", new WaterWorkerEnchantment(Rarity.RARE, ARMOR_SLOTS));
        THORNS = register("thorns", new ThornsEnchantment(Rarity.VERY_RARE, ARMOR_SLOTS));
        DEPTH_STRIDER = register("depth_strider", new WaterWalkerEnchantment(Rarity.RARE, ARMOR_SLOTS));
        FROST_WALKER = register("frost_walker", new FrostWalkerEnchantment(Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.FEET}));
        BINDING_CURSE = register("binding_curse", new BindingCurseEnchantment(Rarity.VERY_RARE, ARMOR_SLOTS));
        SOUL_SPEED = register("soul_speed", new SoulSpeedEnchantment(Rarity.VERY_RARE, new EquipmentSlot[]{EquipmentSlot.FEET}));
        SWIFT_SNEAK = register("swift_sneak", new SwiftSneakEnchantment(Rarity.VERY_RARE, new EquipmentSlot[]{EquipmentSlot.LEGS}));
        SHARPNESS = register("sharpness", new DamageEnchantment(Rarity.COMMON, 0, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        SMITE = register("smite", new DamageEnchantment(Rarity.UNCOMMON, 1, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        BANE_OF_ARTHROPODS = register("bane_of_arthropods", new DamageEnchantment(Rarity.UNCOMMON, 2, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        KNOCKBACK = register("knockback", new KnockbackEnchantment(Rarity.UNCOMMON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        FIRE_ASPECT = register("fire_aspect", new FireAspectEnchantment(Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        MOB_LOOTING = register("looting", new LootBonusEnchantment(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        SWEEPING_EDGE = register("sweeping", new SweepingEdgeEnchantment(Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        BLOCK_EFFICIENCY = register("efficiency", new DiggingEnchantment(Rarity.COMMON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        SILK_TOUCH = register("silk_touch", new UntouchingEnchantment(Rarity.VERY_RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        UNBREAKING = register("unbreaking", new DigDurabilityEnchantment(Rarity.UNCOMMON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        BLOCK_FORTUNE = register("fortune", new LootBonusEnchantment(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        POWER_ARROWS = register("power", new ArrowDamageEnchantment(Rarity.COMMON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        PUNCH_ARROWS = register("punch", new ArrowKnockbackEnchantment(Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        FLAMING_ARROWS = register("flame", new ArrowFireEnchantment(Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        INFINITY_ARROWS = register("infinity", new ArrowInfiniteEnchantment(Rarity.VERY_RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        FISHING_LUCK = register("luck_of_the_sea", new LootBonusEnchantment(Rarity.RARE, EnchantmentCategory.FISHING_ROD, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        FISHING_SPEED = register("lure", new FishingSpeedEnchantment(Rarity.RARE, EnchantmentCategory.FISHING_ROD, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        LOYALTY = register("loyalty", new TridentLoyaltyEnchantment(Rarity.UNCOMMON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        IMPALING = register("impaling", new TridentImpalerEnchantment(Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        RIPTIDE = register("riptide", new TridentRiptideEnchantment(Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        CHANNELING = register("channeling", new TridentChannelingEnchantment(Rarity.VERY_RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        MULTISHOT = register("multishot", new MultiShotEnchantment(Rarity.RARE, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        QUICK_CHARGE = register("quick_charge", new QuickChargeEnchantment(Rarity.UNCOMMON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        PIERCING = register("piercing", new ArrowPiercingEnchantment(Rarity.COMMON, new EquipmentSlot[]{EquipmentSlot.MAINHAND}));
        MENDING = register("mending", new MendingEnchantment(Rarity.RARE, EquipmentSlot.values()));
        VANISHING_CURSE = register("vanishing_curse", new VanishingCurseEnchantment(Rarity.VERY_RARE, EquipmentSlot.values()));
    }
}
