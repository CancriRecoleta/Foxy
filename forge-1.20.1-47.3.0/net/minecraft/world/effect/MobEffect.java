//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.effect;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.common.extensions.IForgeMobEffect;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;

public class MobEffect implements IForgeMobEffect {
    private final Map<Attribute, AttributeModifier> attributeModifiers = Maps.newHashMap();
    private final MobEffectCategory category;
    private final int color;
    @Nullable
    private String descriptionId;
    private Supplier<MobEffectInstance.FactorData> factorDataFactory = () -> {
        return null;
    };
    private Object effectRenderer;

    @Nullable
    public static MobEffect byId(int p_19454_) {
        return (MobEffect)BuiltInRegistries.MOB_EFFECT.byId(p_19454_);
    }

    public static int getId(MobEffect p_19460_) {
        return BuiltInRegistries.MOB_EFFECT.getId(p_19460_);
    }

    public static int getIdFromNullable(@Nullable MobEffect p_216883_) {
        return BuiltInRegistries.MOB_EFFECT.getId(p_216883_);
    }

    protected MobEffect(MobEffectCategory p_19451_, int p_19452_) {
        this.category = p_19451_;
        this.color = p_19452_;
        this.initClient();
    }

    public Optional<MobEffectInstance.FactorData> createFactorData() {
        return Optional.ofNullable((MobEffectInstance.FactorData)this.factorDataFactory.get());
    }

    public void applyEffectTick(LivingEntity p_19467_, int p_19468_) {
        if (this == MobEffects.REGENERATION) {
            if (p_19467_.getHealth() < p_19467_.getMaxHealth()) {
                p_19467_.heal(1.0F);
            }
        } else if (this == MobEffects.POISON) {
            if (p_19467_.getHealth() > 1.0F) {
                p_19467_.hurt(p_19467_.damageSources().magic(), 1.0F);
            }
        } else if (this == MobEffects.WITHER) {
            p_19467_.hurt(p_19467_.damageSources().wither(), 1.0F);
        } else if (this == MobEffects.HUNGER && p_19467_ instanceof Player) {
            ((Player)p_19467_).causeFoodExhaustion(0.005F * (float)(p_19468_ + 1));
        } else if (this == MobEffects.SATURATION && p_19467_ instanceof Player) {
            if (!p_19467_.level().isClientSide) {
                ((Player)p_19467_).getFoodData().eat(p_19468_ + 1, 1.0F);
            }
        } else if (this == MobEffects.HEAL && !p_19467_.isInvertedHealAndHarm() || this == MobEffects.HARM && p_19467_.isInvertedHealAndHarm()) {
            p_19467_.heal((float)Math.max(4 << p_19468_, 0));
        } else if (this == MobEffects.HARM && !p_19467_.isInvertedHealAndHarm() || this == MobEffects.HEAL && p_19467_.isInvertedHealAndHarm()) {
            p_19467_.hurt(p_19467_.damageSources().magic(), (float)(6 << p_19468_));
        }

    }

    public void applyInstantenousEffect(@Nullable Entity p_19462_, @Nullable Entity p_19463_, LivingEntity p_19464_, int p_19465_, double p_19466_) {
        int j;
        if (this == MobEffects.HEAL && !p_19464_.isInvertedHealAndHarm() || this == MobEffects.HARM && p_19464_.isInvertedHealAndHarm()) {
            j = (int)(p_19466_ * (double)(4 << p_19465_) + 0.5);
            p_19464_.heal((float)j);
        } else if ((this != MobEffects.HARM || p_19464_.isInvertedHealAndHarm()) && (this != MobEffects.HEAL || !p_19464_.isInvertedHealAndHarm())) {
            this.applyEffectTick(p_19464_, p_19465_);
        } else {
            j = (int)(p_19466_ * (double)(6 << p_19465_) + 0.5);
            if (p_19462_ == null) {
                p_19464_.hurt(p_19464_.damageSources().magic(), (float)j);
            } else {
                p_19464_.hurt(p_19464_.damageSources().indirectMagic(p_19462_, p_19463_), (float)j);
            }
        }

    }

    public boolean isDurationEffectTick(int p_19455_, int p_19456_) {
        int i;
        if (this == MobEffects.REGENERATION) {
            i = 50 >> p_19456_;
            if (i > 0) {
                return p_19455_ % i == 0;
            } else {
                return true;
            }
        } else if (this == MobEffects.POISON) {
            i = 25 >> p_19456_;
            if (i > 0) {
                return p_19455_ % i == 0;
            } else {
                return true;
            }
        } else if (this == MobEffects.WITHER) {
            i = 40 >> p_19456_;
            if (i > 0) {
                return p_19455_ % i == 0;
            } else {
                return true;
            }
        } else {
            return this == MobEffects.HUNGER;
        }
    }

    public boolean isInstantenous() {
        return false;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("effect", BuiltInRegistries.MOB_EFFECT.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Component getDisplayName() {
        return Component.translatable(this.getDescriptionId());
    }

    public MobEffectCategory getCategory() {
        return this.category;
    }

    public int getColor() {
        return this.color;
    }

    public MobEffect addAttributeModifier(Attribute p_19473_, String p_19474_, double p_19475_, AttributeModifier.Operation p_19476_) {
        AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(p_19474_), this::getDescriptionId, p_19475_, p_19476_);
        this.attributeModifiers.put(p_19473_, attributemodifier);
        return this;
    }

    public MobEffect setFactorDataFactory(Supplier<MobEffectInstance.FactorData> p_216880_) {
        this.factorDataFactory = p_216880_;
        return this;
    }

    public Map<Attribute, AttributeModifier> getAttributeModifiers() {
        return this.attributeModifiers;
    }

    public void removeAttributeModifiers(LivingEntity p_19469_, AttributeMap p_19470_, int p_19471_) {
        Iterator var4 = this.attributeModifiers.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<Attribute, AttributeModifier> entry = (Map.Entry)var4.next();
            AttributeInstance attributeinstance = p_19470_.getInstance((Attribute)entry.getKey());
            if (attributeinstance != null) {
                attributeinstance.removeModifier((AttributeModifier)entry.getValue());
            }
        }

    }

    public void addAttributeModifiers(LivingEntity p_19478_, AttributeMap p_19479_, int p_19480_) {
        Iterator var4 = this.attributeModifiers.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<Attribute, AttributeModifier> entry = (Map.Entry)var4.next();
            AttributeInstance attributeinstance = p_19479_.getInstance((Attribute)entry.getKey());
            if (attributeinstance != null) {
                AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
                attributeinstance.removeModifier(attributemodifier);
                attributeinstance.addPermanentModifier(new AttributeModifier(attributemodifier.getId(), this.getDescriptionId() + " " + p_19480_, this.getAttributeModifierValue(p_19480_, attributemodifier), attributemodifier.getOperation()));
            }
        }

    }

    public double getAttributeModifierValue(int p_19457_, AttributeModifier p_19458_) {
        return p_19458_.getAmount() * (double)(p_19457_ + 1);
    }

    public boolean isBeneficial() {
        return this.category == MobEffectCategory.BENEFICIAL;
    }

    public Object getEffectRendererInternal() {
        return this.effectRenderer;
    }

    private void initClient() {
        if (FMLEnvironment.dist == Dist.CLIENT && !FMLLoader.getLaunchHandler().isData()) {
            this.initializeClient((properties) -> {
                this.effectRenderer = properties;
            });
        }

    }

    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
    }
}
