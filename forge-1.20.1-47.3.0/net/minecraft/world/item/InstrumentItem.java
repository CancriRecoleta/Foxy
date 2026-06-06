//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;

public class InstrumentItem extends Item {
    private static final String TAG_INSTRUMENT = "instrument";
    private final TagKey<Instrument> instruments;

    public InstrumentItem(Item.Properties p_220099_, TagKey<Instrument> p_220100_) {
        super(p_220099_);
        this.instruments = p_220100_;
    }

    public void appendHoverText(ItemStack p_220115_, @Nullable Level p_220116_, List<Component> p_220117_, TooltipFlag p_220118_) {
        super.appendHoverText(p_220115_, p_220116_, p_220117_, p_220118_);
        Optional<ResourceKey<Instrument>> $$4 = this.getInstrument(p_220115_).flatMap(Holder::unwrapKey);
        if ($$4.isPresent()) {
            MutableComponent $$5 = Component.translatable(Util.makeDescriptionId("instrument", ((ResourceKey)$$4.get()).location()));
            p_220117_.add($$5.withStyle(ChatFormatting.GRAY));
        }

    }

    public static ItemStack create(Item p_220108_, Holder<Instrument> p_220109_) {
        ItemStack $$2 = new ItemStack(p_220108_);
        setSoundVariantId($$2, p_220109_);
        return $$2;
    }

    public static void setRandom(ItemStack p_220111_, TagKey<Instrument> p_220112_, RandomSource p_220113_) {
        Optional<Holder<Instrument>> $$3 = BuiltInRegistries.INSTRUMENT.getTag(p_220112_).flatMap((p_220103_) -> {
            return p_220103_.getRandomElement(p_220113_);
        });
        $$3.ifPresent((p_248417_) -> {
            setSoundVariantId(p_220111_, p_248417_);
        });
    }

    private static void setSoundVariantId(ItemStack p_220120_, Holder<Instrument> p_220121_) {
        CompoundTag $$2 = p_220120_.getOrCreateTag();
        $$2.putString("instrument", ((ResourceKey)p_220121_.unwrapKey().orElseThrow(() -> {
            return new IllegalStateException("Invalid instrument");
        })).location().toString());
    }

    public InteractionResultHolder<ItemStack> use(Level p_220123_, Player p_220124_, InteractionHand p_220125_) {
        ItemStack $$3 = p_220124_.getItemInHand(p_220125_);
        Optional<? extends Holder<Instrument>> $$4 = this.getInstrument($$3);
        if ($$4.isPresent()) {
            Instrument $$5 = (Instrument)((Holder)$$4.get()).value();
            p_220124_.startUsingItem(p_220125_);
            play(p_220123_, p_220124_, $$5);
            p_220124_.getCooldowns().addCooldown(this, $$5.useDuration());
            p_220124_.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.consume($$3);
        } else {
            return InteractionResultHolder.fail($$3);
        }
    }

    public int getUseDuration(ItemStack p_220131_) {
        Optional<? extends Holder<Instrument>> $$1 = this.getInstrument(p_220131_);
        return (Integer)$$1.map((p_248418_) -> {
            return ((Instrument)p_248418_.value()).useDuration();
        }).orElse(0);
    }

    private Optional<? extends Holder<Instrument>> getInstrument(ItemStack p_220135_) {
        CompoundTag $$1 = p_220135_.getTag();
        if ($$1 != null && $$1.contains("instrument", 8)) {
            ResourceLocation $$2 = ResourceLocation.tryParse($$1.getString("instrument"));
            if ($$2 != null) {
                return BuiltInRegistries.INSTRUMENT.getHolder(ResourceKey.create(Registries.INSTRUMENT, $$2));
            }
        }

        Iterator<Holder<Instrument>> $$3 = BuiltInRegistries.INSTRUMENT.getTagOrEmpty(this.instruments).iterator();
        return $$3.hasNext() ? Optional.of((Holder)$$3.next()) : Optional.empty();
    }

    public UseAnim getUseAnimation(ItemStack p_220133_) {
        return UseAnim.TOOT_HORN;
    }

    private static void play(Level p_220127_, Player p_220128_, Instrument p_220129_) {
        SoundEvent $$3 = (SoundEvent)p_220129_.soundEvent().value();
        float $$4 = p_220129_.range() / 16.0F;
        p_220127_.playSound((Player)p_220128_, (Entity)p_220128_, $$3, SoundSource.RECORDS, $$4, 1.0F);
        p_220127_.gameEvent(GameEvent.INSTRUMENT_PLAY, p_220128_.position(), Context.of((Entity)p_220128_));
    }
}
