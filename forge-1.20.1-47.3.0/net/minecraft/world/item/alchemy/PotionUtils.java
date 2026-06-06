//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;

public class PotionUtils {
    public static final String TAG_CUSTOM_POTION_EFFECTS = "CustomPotionEffects";
    public static final String TAG_CUSTOM_POTION_COLOR = "CustomPotionColor";
    public static final String TAG_POTION = "Potion";
    private static final int EMPTY_COLOR = 16253176;
    private static final Component NO_EFFECT;

    public PotionUtils() {
    }

    public static List<MobEffectInstance> getMobEffects(ItemStack p_43548_) {
        return getAllEffects(p_43548_.getTag());
    }

    public static List<MobEffectInstance> getAllEffects(Potion p_43562_, Collection<MobEffectInstance> p_43563_) {
        List<MobEffectInstance> $$2 = Lists.newArrayList();
        $$2.addAll(p_43562_.getEffects());
        $$2.addAll(p_43563_);
        return $$2;
    }

    public static List<MobEffectInstance> getAllEffects(@Nullable CompoundTag p_43567_) {
        List<MobEffectInstance> $$1 = Lists.newArrayList();
        $$1.addAll(getPotion(p_43567_).getEffects());
        getCustomEffects(p_43567_, $$1);
        return $$1;
    }

    public static List<MobEffectInstance> getCustomEffects(ItemStack p_43572_) {
        return getCustomEffects(p_43572_.getTag());
    }

    public static List<MobEffectInstance> getCustomEffects(@Nullable CompoundTag p_43574_) {
        List<MobEffectInstance> $$1 = Lists.newArrayList();
        getCustomEffects(p_43574_, $$1);
        return $$1;
    }

    public static void getCustomEffects(@Nullable CompoundTag p_43569_, List<MobEffectInstance> p_43570_) {
        if (p_43569_ != null && p_43569_.contains("CustomPotionEffects", 9)) {
            ListTag $$2 = p_43569_.getList("CustomPotionEffects", 10);

            for(int $$3 = 0; $$3 < $$2.size(); ++$$3) {
                CompoundTag $$4 = $$2.getCompound($$3);
                MobEffectInstance $$5 = MobEffectInstance.load($$4);
                if ($$5 != null) {
                    p_43570_.add($$5);
                }
            }
        }

    }

    public static int getColor(ItemStack p_43576_) {
        CompoundTag $$1 = p_43576_.getTag();
        if ($$1 != null && $$1.contains("CustomPotionColor", 99)) {
            return $$1.getInt("CustomPotionColor");
        } else {
            return getPotion(p_43576_) == Potions.EMPTY ? 16253176 : getColor((Collection)getMobEffects(p_43576_));
        }
    }

    public static int getColor(Potion p_43560_) {
        return p_43560_ == Potions.EMPTY ? 16253176 : getColor((Collection)p_43560_.getEffects());
    }

    public static int getColor(Collection<MobEffectInstance> p_43565_) {
        int $$1 = 3694022;
        if (p_43565_.isEmpty()) {
            return 3694022;
        } else {
            float $$2 = 0.0F;
            float $$3 = 0.0F;
            float $$4 = 0.0F;
            int $$5 = 0;
            Iterator var6 = p_43565_.iterator();

            while(var6.hasNext()) {
                MobEffectInstance $$6 = (MobEffectInstance)var6.next();
                if ($$6.isVisible()) {
                    int $$7 = $$6.getEffect().getColor();
                    int $$8 = $$6.getAmplifier() + 1;
                    $$2 += (float)($$8 * ($$7 >> 16 & 255)) / 255.0F;
                    $$3 += (float)($$8 * ($$7 >> 8 & 255)) / 255.0F;
                    $$4 += (float)($$8 * ($$7 >> 0 & 255)) / 255.0F;
                    $$5 += $$8;
                }
            }

            if ($$5 == 0) {
                return 0;
            } else {
                $$2 = $$2 / (float)$$5 * 255.0F;
                $$3 = $$3 / (float)$$5 * 255.0F;
                $$4 = $$4 / (float)$$5 * 255.0F;
                return (int)$$2 << 16 | (int)$$3 << 8 | (int)$$4;
            }
        }
    }

    public static Potion getPotion(ItemStack p_43580_) {
        return getPotion(p_43580_.getTag());
    }

    public static Potion getPotion(@Nullable CompoundTag p_43578_) {
        return p_43578_ == null ? Potions.EMPTY : Potion.byName(p_43578_.getString("Potion"));
    }

    public static ItemStack setPotion(ItemStack p_43550_, Potion p_43551_) {
        ResourceLocation $$2 = BuiltInRegistries.POTION.getKey(p_43551_);
        if (p_43551_ == Potions.EMPTY) {
            p_43550_.removeTagKey("Potion");
        } else {
            p_43550_.getOrCreateTag().putString("Potion", $$2.toString());
        }

        return p_43550_;
    }

    public static ItemStack setCustomEffects(ItemStack p_43553_, Collection<MobEffectInstance> p_43554_) {
        if (p_43554_.isEmpty()) {
            return p_43553_;
        } else {
            CompoundTag $$2 = p_43553_.getOrCreateTag();
            ListTag $$3 = $$2.getList("CustomPotionEffects", 9);
            Iterator var4 = p_43554_.iterator();

            while(var4.hasNext()) {
                MobEffectInstance $$4 = (MobEffectInstance)var4.next();
                $$3.add($$4.save(new CompoundTag()));
            }

            $$2.put("CustomPotionEffects", $$3);
            return p_43553_;
        }
    }

    public static void addPotionTooltip(ItemStack p_43556_, List<Component> p_43557_, float p_43558_) {
        addPotionTooltip(getMobEffects(p_43556_), p_43557_, p_43558_);
    }

    public static void addPotionTooltip(List<MobEffectInstance> p_259687_, List<Component> p_259660_, float p_259949_) {
        List<Pair<Attribute, AttributeModifier>> $$3 = Lists.newArrayList();
        Iterator var4;
        MutableComponent $$5;
        MobEffect $$6;
        if (p_259687_.isEmpty()) {
            p_259660_.add(NO_EFFECT);
        } else {
            for(var4 = p_259687_.iterator(); var4.hasNext(); p_259660_.add($$5.withStyle($$6.getCategory().getTooltipFormatting()))) {
                MobEffectInstance $$4 = (MobEffectInstance)var4.next();
                $$5 = Component.translatable($$4.getDescriptionId());
                $$6 = $$4.getEffect();
                Map<Attribute, AttributeModifier> $$7 = $$6.getAttributeModifiers();
                if (!$$7.isEmpty()) {
                    Iterator var9 = $$7.entrySet().iterator();

                    while(var9.hasNext()) {
                        Map.Entry<Attribute, AttributeModifier> $$8 = (Map.Entry)var9.next();
                        AttributeModifier $$9 = (AttributeModifier)$$8.getValue();
                        AttributeModifier $$10 = new AttributeModifier($$9.getName(), $$6.getAttributeModifierValue($$4.getAmplifier(), $$9), $$9.getOperation());
                        $$3.add(new Pair((Attribute)$$8.getKey(), $$10));
                    }
                }

                if ($$4.getAmplifier() > 0) {
                    $$5 = Component.translatable("potion.withAmplifier", $$5, Component.translatable("potion.potency." + $$4.getAmplifier()));
                }

                if (!$$4.endsWithin(20)) {
                    $$5 = Component.translatable("potion.withDuration", $$5, MobEffectUtil.formatDuration($$4, p_259949_));
                }
            }
        }

        if (!$$3.isEmpty()) {
            p_259660_.add(CommonComponents.EMPTY);
            p_259660_.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
            var4 = $$3.iterator();

            while(var4.hasNext()) {
                Pair<Attribute, AttributeModifier> $$11 = (Pair)var4.next();
                AttributeModifier $$12 = (AttributeModifier)$$11.getSecond();
                double $$13 = $$12.getAmount();
                double $$15;
                if ($$12.getOperation() != Operation.MULTIPLY_BASE && $$12.getOperation() != Operation.MULTIPLY_TOTAL) {
                    $$15 = $$12.getAmount();
                } else {
                    $$15 = $$12.getAmount() * 100.0;
                }

                if ($$13 > 0.0) {
                    p_259660_.add(Component.translatable("attribute.modifier.plus." + $$12.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format($$15), Component.translatable(((Attribute)$$11.getFirst()).getDescriptionId())).withStyle(ChatFormatting.BLUE));
                } else if ($$13 < 0.0) {
                    $$15 *= -1.0;
                    p_259660_.add(Component.translatable("attribute.modifier.take." + $$12.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format($$15), Component.translatable(((Attribute)$$11.getFirst()).getDescriptionId())).withStyle(ChatFormatting.RED));
                }
            }
        }

    }

    static {
        NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);
    }
}
