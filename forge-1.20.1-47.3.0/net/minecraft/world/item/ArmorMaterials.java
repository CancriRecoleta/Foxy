//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.crafting.Ingredient;

public enum ArmorMaterials implements StringRepresentable, ArmorMaterial {
    LEATHER("leather", 5, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (p_266652_) -> {
        p_266652_.put(Type.BOOTS, 1);
        p_266652_.put(Type.LEGGINGS, 2);
        p_266652_.put(Type.CHESTPLATE, 3);
        p_266652_.put(Type.HELMET, 1);
    }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.LEATHER);
    }),
    CHAIN("chainmail", 15, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (p_266651_) -> {
        p_266651_.put(Type.BOOTS, 1);
        p_266651_.put(Type.LEGGINGS, 4);
        p_266651_.put(Type.CHESTPLATE, 5);
        p_266651_.put(Type.HELMET, 2);
    }), 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.IRON_INGOT);
    }),
    IRON("iron", 15, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (p_266654_) -> {
        p_266654_.put(Type.BOOTS, 2);
        p_266654_.put(Type.LEGGINGS, 5);
        p_266654_.put(Type.CHESTPLATE, 6);
        p_266654_.put(Type.HELMET, 2);
    }), 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.IRON_INGOT);
    }),
    GOLD("gold", 7, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (p_266650_) -> {
        p_266650_.put(Type.BOOTS, 1);
        p_266650_.put(Type.LEGGINGS, 3);
        p_266650_.put(Type.CHESTPLATE, 5);
        p_266650_.put(Type.HELMET, 2);
    }), 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.GOLD_INGOT);
    }),
    DIAMOND("diamond", 33, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (p_266649_) -> {
        p_266649_.put(Type.BOOTS, 3);
        p_266649_.put(Type.LEGGINGS, 6);
        p_266649_.put(Type.CHESTPLATE, 8);
        p_266649_.put(Type.HELMET, 3);
    }), 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> {
        return Ingredient.of(Items.DIAMOND);
    }),
    TURTLE("turtle", 25, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (p_266656_) -> {
        p_266656_.put(Type.BOOTS, 2);
        p_266656_.put(Type.LEGGINGS, 5);
        p_266656_.put(Type.CHESTPLATE, 6);
        p_266656_.put(Type.HELMET, 2);
    }), 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0F, 0.0F, () -> {
        return Ingredient.of(Items.SCUTE);
    }),
    NETHERITE("netherite", 37, (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (p_266655_) -> {
        p_266655_.put(Type.BOOTS, 3);
        p_266655_.put(Type.LEGGINGS, 6);
        p_266655_.put(Type.CHESTPLATE, 8);
        p_266655_.put(Type.HELMET, 3);
    }), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, () -> {
        return Ingredient.of(Items.NETHERITE_INGOT);
    });

    public static final StringRepresentable.EnumCodec<ArmorMaterials> CODEC = StringRepresentable.fromEnum(ArmorMaterials::values);
    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = (EnumMap)Util.make(new EnumMap(ArmorItem.Type.class), (p_266653_) -> {
        p_266653_.put(Type.BOOTS, 13);
        p_266653_.put(Type.LEGGINGS, 15);
        p_266653_.put(Type.CHESTPLATE, 16);
        p_266653_.put(Type.HELMET, 11);
    });
    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    private ArmorMaterials(String p_268171_, int p_268303_, EnumMap p_267941_, int p_268086_, SoundEvent p_268145_, float p_268058_, float p_268180_, Supplier p_268256_) {
        this.name = p_268171_;
        this.durabilityMultiplier = p_268303_;
        this.protectionFunctionForType = p_267941_;
        this.enchantmentValue = p_268086_;
        this.sound = p_268145_;
        this.toughness = p_268058_;
        this.knockbackResistance = p_268180_;
        this.repairIngredient = new LazyLoadedValue(p_268256_);
    }

    public int getDurabilityForType(ArmorItem.Type p_266745_) {
        return (Integer)HEALTH_FUNCTION_FOR_TYPE.get(p_266745_) * this.durabilityMultiplier;
    }

    public int getDefenseForType(ArmorItem.Type p_266752_) {
        return (Integer)this.protectionFunctionForType.get(p_266752_);
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public SoundEvent getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return (Ingredient)this.repairIngredient.get();
    }

    public String getName() {
        return this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public String getSerializedName() {
        return this.name;
    }
}
