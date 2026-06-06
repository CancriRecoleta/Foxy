//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.damagesource;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CombatTracker {
    public static final int RESET_DAMAGE_STATUS_TIME = 100;
    public static final int RESET_COMBAT_STATUS_TIME = 300;
    private static final Style INTENTIONAL_GAME_DESIGN_STYLE;
    private final List<CombatEntry> entries = Lists.newArrayList();
    private final LivingEntity mob;
    private int lastDamageTime;
    private int combatStartTime;
    private int combatEndTime;
    private boolean inCombat;
    private boolean takingDamage;

    public CombatTracker(LivingEntity p_19285_) {
        this.mob = p_19285_;
    }

    public void recordDamage(DamageSource p_289533_, float p_289559_) {
        this.recheckStatus();
        FallLocation $$2 = FallLocation.getCurrentFallLocation(this.mob);
        CombatEntry $$3 = new CombatEntry(p_289533_, p_289559_, $$2, this.mob.fallDistance);
        this.entries.add($$3);
        this.lastDamageTime = this.mob.tickCount;
        this.takingDamage = true;
        if (!this.inCombat && this.mob.isAlive() && shouldEnterCombat(p_289533_)) {
            this.inCombat = true;
            this.combatStartTime = this.mob.tickCount;
            this.combatEndTime = this.combatStartTime;
            this.mob.onEnterCombat();
        }

    }

    private static boolean shouldEnterCombat(DamageSource p_289554_) {
        return p_289554_.getEntity() instanceof LivingEntity;
    }

    private Component getMessageForAssistedFall(Entity p_289547_, Component p_289532_, String p_289555_, String p_289548_) {
        ItemStack var10000;
        if (p_289547_ instanceof LivingEntity $$4) {
            var10000 = $$4.getMainHandItem();
        } else {
            var10000 = ItemStack.EMPTY;
        }

        ItemStack $$5 = var10000;
        return !$$5.isEmpty() && $$5.hasCustomHoverName() ? Component.translatable(p_289555_, this.mob.getDisplayName(), p_289532_, $$5.getDisplayName()) : Component.translatable(p_289548_, this.mob.getDisplayName(), p_289532_);
    }

    private Component getFallMessage(CombatEntry p_289570_, @Nullable Entity p_289561_) {
        DamageSource $$2 = p_289570_.source();
        if (!$$2.is(DamageTypeTags.IS_FALL) && !$$2.is(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL)) {
            Component $$4 = getDisplayName(p_289561_);
            Entity $$5 = $$2.getEntity();
            Component $$6 = getDisplayName($$5);
            if ($$6 != null && !$$6.equals($$4)) {
                return this.getMessageForAssistedFall($$5, $$6, "death.fell.assist.item", "death.fell.assist");
            } else {
                return (Component)($$4 != null ? this.getMessageForAssistedFall(p_289561_, $$4, "death.fell.finish.item", "death.fell.finish") : Component.translatable("death.fell.killer", this.mob.getDisplayName()));
            }
        } else {
            FallLocation $$3 = (FallLocation)Objects.requireNonNullElse(p_289570_.fallLocation(), FallLocation.GENERIC);
            return Component.translatable($$3.languageKey(), this.mob.getDisplayName());
        }
    }

    @Nullable
    private static Component getDisplayName(@Nullable Entity p_289557_) {
        return p_289557_ == null ? null : p_289557_.getDisplayName();
    }

    public Component getDeathMessage() {
        if (this.entries.isEmpty()) {
            return Component.translatable("death.attack.generic", this.mob.getDisplayName());
        } else {
            CombatEntry $$0 = (CombatEntry)this.entries.get(this.entries.size() - 1);
            DamageSource $$1 = $$0.source();
            CombatEntry $$2 = this.getMostSignificantFall();
            DeathMessageType $$3 = $$1.type().deathMessageType();
            if ($$3 == DeathMessageType.FALL_VARIANTS && $$2 != null) {
                return this.getFallMessage($$2, $$1.getEntity());
            } else if ($$3 == DeathMessageType.INTENTIONAL_GAME_DESIGN) {
                String $$4 = "death.attack." + $$1.getMsgId();
                Component $$5 = ComponentUtils.wrapInSquareBrackets(Component.translatable($$4 + ".link")).withStyle(INTENTIONAL_GAME_DESIGN_STYLE);
                return Component.translatable($$4 + ".message", this.mob.getDisplayName(), $$5);
            } else {
                return $$1.getLocalizedDeathMessage(this.mob);
            }
        }
    }

    @Nullable
    private CombatEntry getMostSignificantFall() {
        CombatEntry $$0 = null;
        CombatEntry $$1 = null;
        float $$2 = 0.0F;
        float $$3 = 0.0F;

        for(int $$4 = 0; $$4 < this.entries.size(); ++$$4) {
            CombatEntry $$5 = (CombatEntry)this.entries.get($$4);
            CombatEntry $$6 = $$4 > 0 ? (CombatEntry)this.entries.get($$4 - 1) : null;
            DamageSource $$7 = $$5.source();
            boolean $$8 = $$7.is(DamageTypeTags.ALWAYS_MOST_SIGNIFICANT_FALL);
            float $$9 = $$8 ? Float.MAX_VALUE : $$5.fallDistance();
            if (($$7.is(DamageTypeTags.IS_FALL) || $$8) && $$9 > 0.0F && ($$0 == null || $$9 > $$3)) {
                if ($$4 > 0) {
                    $$0 = $$6;
                } else {
                    $$0 = $$5;
                }

                $$3 = $$9;
            }

            if ($$5.fallLocation() != null && ($$1 == null || $$5.damage() > $$2)) {
                $$1 = $$5;
                $$2 = $$5.damage();
            }
        }

        if ($$3 > 5.0F && $$0 != null) {
            return $$0;
        } else if ($$2 > 5.0F && $$1 != null) {
            return $$1;
        } else {
            return null;
        }
    }

    public int getCombatDuration() {
        return this.inCombat ? this.mob.tickCount - this.combatStartTime : this.combatEndTime - this.combatStartTime;
    }

    public void recheckStatus() {
        int $$0 = this.inCombat ? 300 : 100;
        if (this.takingDamage && (!this.mob.isAlive() || this.mob.tickCount - this.lastDamageTime > $$0)) {
            boolean $$1 = this.inCombat;
            this.takingDamage = false;
            this.inCombat = false;
            this.combatEndTime = this.mob.tickCount;
            if ($$1) {
                this.mob.onLeaveCombat();
            }

            this.entries.clear();
        }

    }

    static {
        INTENTIONAL_GAME_DESIGN_STYLE = Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.literal("MCPE-28723")));
    }
}
