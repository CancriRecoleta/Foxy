//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class DamageSource {
    private final Holder<DamageType> type;
    @Nullable
    private final Entity causingEntity;
    @Nullable
    private final Entity directEntity;
    @Nullable
    private final Vec3 damageSourcePosition;

    public String toString() {
        return "DamageSource (" + this.type().msgId() + ")";
    }

    public float getFoodExhaustion() {
        return this.type().exhaustion();
    }

    public boolean isIndirect() {
        return this.causingEntity != this.directEntity;
    }

    public DamageSource(Holder<DamageType> p_270906_, @Nullable Entity p_270796_, @Nullable Entity p_270459_, @Nullable Vec3 p_270623_) {
        this.type = p_270906_;
        this.causingEntity = p_270459_;
        this.directEntity = p_270796_;
        this.damageSourcePosition = p_270623_;
    }

    public DamageSource(Holder<DamageType> p_270818_, @Nullable Entity p_270162_, @Nullable Entity p_270115_) {
        this(p_270818_, p_270162_, p_270115_, (Vec3)null);
    }

    public DamageSource(Holder<DamageType> p_270690_, Vec3 p_270579_) {
        this(p_270690_, (Entity)null, (Entity)null, p_270579_);
    }

    public DamageSource(Holder<DamageType> p_270811_, @Nullable Entity p_270660_) {
        this(p_270811_, p_270660_, p_270660_);
    }

    public DamageSource(Holder<DamageType> p_270475_) {
        this(p_270475_, (Entity)null, (Entity)null, (Vec3)null);
    }

    @Nullable
    public Entity getDirectEntity() {
        return this.directEntity;
    }

    @Nullable
    public Entity getEntity() {
        return this.causingEntity;
    }

    public Component getLocalizedDeathMessage(LivingEntity p_19343_) {
        String $$1 = "death.attack." + this.type().msgId();
        if (this.causingEntity == null && this.directEntity == null) {
            LivingEntity $$5 = p_19343_.getKillCredit();
            String $$6 = $$1 + ".player";
            return $$5 != null ? Component.translatable($$6, p_19343_.getDisplayName(), $$5.getDisplayName()) : Component.translatable($$1, p_19343_.getDisplayName());
        } else {
            Component $$2 = this.causingEntity == null ? this.directEntity.getDisplayName() : this.causingEntity.getDisplayName();
            Entity var6 = this.causingEntity;
            ItemStack var10000;
            if (var6 instanceof LivingEntity) {
                LivingEntity $$3 = (LivingEntity)var6;
                var10000 = $$3.getMainHandItem();
            } else {
                var10000 = ItemStack.EMPTY;
            }

            ItemStack $$4 = var10000;
            return !$$4.isEmpty() && $$4.hasCustomHoverName() ? Component.translatable($$1 + ".item", p_19343_.getDisplayName(), $$2, $$4.getDisplayName()) : Component.translatable($$1, p_19343_.getDisplayName(), $$2);
        }
    }

    public String getMsgId() {
        return this.type().msgId();
    }

    public boolean scalesWithDifficulty() {
        boolean var10000;
        switch (this.type().scaling()) {
            case NEVER -> var10000 = false;
            case WHEN_CAUSED_BY_LIVING_NON_PLAYER -> var10000 = this.causingEntity instanceof LivingEntity && !(this.causingEntity instanceof Player);
            case ALWAYS -> var10000 = true;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public boolean isCreativePlayer() {
        Entity var2 = this.getEntity();
        boolean var10000;
        if (var2 instanceof Player $$0) {
            if ($$0.getAbilities().instabuild) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }

    @Nullable
    public Vec3 getSourcePosition() {
        if (this.damageSourcePosition != null) {
            return this.damageSourcePosition;
        } else {
            return this.directEntity != null ? this.directEntity.position() : null;
        }
    }

    @Nullable
    public Vec3 sourcePositionRaw() {
        return this.damageSourcePosition;
    }

    public boolean is(TagKey<DamageType> p_270890_) {
        return this.type.is(p_270890_);
    }

    public boolean is(ResourceKey<DamageType> p_276108_) {
        return this.type.is(p_276108_);
    }

    public DamageType type() {
        return (DamageType)this.type.value();
    }

    public Holder<DamageType> typeHolder() {
        return this.type;
    }
}
