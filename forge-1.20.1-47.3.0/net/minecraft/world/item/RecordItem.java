//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;

public class RecordItem extends Item {
    /** @deprecated */
    @Deprecated
    private static final Map<SoundEvent, RecordItem> BY_NAME = Maps.newHashMap();
    private final int analogOutput;
    /** @deprecated */
    @Deprecated
    private final SoundEvent sound;
    private final int lengthInTicks;
    private final Supplier<SoundEvent> soundSupplier;

    /** @deprecated */
    @Deprecated
    public RecordItem(int p_239614_, SoundEvent p_239615_, Item.Properties p_239616_, int p_239617_) {
        super(p_239616_);
        this.analogOutput = p_239614_;
        this.sound = p_239615_;
        this.lengthInTicks = p_239617_ * 20;
        BY_NAME.put(this.sound, this);
        this.soundSupplier = ForgeRegistries.SOUND_EVENTS.getDelegateOrThrow((Object)this.sound);
    }

    public RecordItem(int comparatorValue, Supplier<SoundEvent> soundSupplier, Item.Properties builder, int lengthInTicks) {
        super(builder);
        this.analogOutput = comparatorValue;
        this.sound = null;
        this.soundSupplier = soundSupplier;
        this.lengthInTicks = lengthInTicks;
    }

    public InteractionResult useOn(UseOnContext p_43048_) {
        Level level = p_43048_.getLevel();
        BlockPos blockpos = p_43048_.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (blockstate.is(Blocks.JUKEBOX) && !(Boolean)blockstate.getValue(JukeboxBlock.HAS_RECORD)) {
            ItemStack itemstack = p_43048_.getItemInHand();
            if (!level.isClientSide) {
                Player player = p_43048_.getPlayer();
                BlockEntity blockentity = level.getBlockEntity(blockpos);
                if (blockentity instanceof JukeboxBlockEntity) {
                    JukeboxBlockEntity jukeboxblockentity = (JukeboxBlockEntity)blockentity;
                    jukeboxblockentity.setFirstItem(itemstack.copy());
                    level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, Context.of(player, blockstate));
                }

                itemstack.shrink(1);
                if (player != null) {
                    player.awardStat(Stats.PLAY_RECORD);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public int getAnalogOutput() {
        return this.analogOutput;
    }

    public void appendHoverText(ItemStack p_43043_, @Nullable Level p_43044_, List<Component> p_43045_, TooltipFlag p_43046_) {
        p_43045_.add(this.getDisplayName().withStyle(ChatFormatting.GRAY));
    }

    public MutableComponent getDisplayName() {
        return Component.translatable(this.getDescriptionId() + ".desc");
    }

    @Nullable
    public static RecordItem getBySound(SoundEvent p_43041_) {
        return (RecordItem)BY_NAME.get(p_43041_);
    }

    public SoundEvent getSound() {
        return (SoundEvent)this.soundSupplier.get();
    }

    public int getLengthInTicks() {
        return this.lengthInTicks;
    }
}
