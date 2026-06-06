//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;

public class CompassItem extends Item implements Vanishable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String TAG_LODESTONE_POS = "LodestonePos";
    public static final String TAG_LODESTONE_DIMENSION = "LodestoneDimension";
    public static final String TAG_LODESTONE_TRACKED = "LodestoneTracked";

    public CompassItem(Item.Properties p_40718_) {
        super(p_40718_);
    }

    public static boolean isLodestoneCompass(ItemStack p_40737_) {
        CompoundTag $$1 = p_40737_.getTag();
        return $$1 != null && ($$1.contains("LodestoneDimension") || $$1.contains("LodestonePos"));
    }

    private static Optional<ResourceKey<Level>> getLodestoneDimension(CompoundTag p_40728_) {
        return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, p_40728_.get("LodestoneDimension")).result();
    }

    @Nullable
    public static GlobalPos getLodestonePosition(CompoundTag p_220022_) {
        boolean $$1 = p_220022_.contains("LodestonePos");
        boolean $$2 = p_220022_.contains("LodestoneDimension");
        if ($$1 && $$2) {
            Optional<ResourceKey<Level>> $$3 = getLodestoneDimension(p_220022_);
            if ($$3.isPresent()) {
                BlockPos $$4 = NbtUtils.readBlockPos(p_220022_.getCompound("LodestonePos"));
                return GlobalPos.of((ResourceKey)$$3.get(), $$4);
            }
        }

        return null;
    }

    @Nullable
    public static GlobalPos getSpawnPosition(Level p_220020_) {
        return p_220020_.dimensionType().natural() ? GlobalPos.of(p_220020_.dimension(), p_220020_.getSharedSpawnPos()) : null;
    }

    public boolean isFoil(ItemStack p_40739_) {
        return isLodestoneCompass(p_40739_) || super.isFoil(p_40739_);
    }

    public void inventoryTick(ItemStack p_40720_, Level p_40721_, Entity p_40722_, int p_40723_, boolean p_40724_) {
        if (!p_40721_.isClientSide) {
            if (isLodestoneCompass(p_40720_)) {
                CompoundTag $$5 = p_40720_.getOrCreateTag();
                if ($$5.contains("LodestoneTracked") && !$$5.getBoolean("LodestoneTracked")) {
                    return;
                }

                Optional<ResourceKey<Level>> $$6 = getLodestoneDimension($$5);
                if ($$6.isPresent() && $$6.get() == p_40721_.dimension() && $$5.contains("LodestonePos")) {
                    BlockPos $$7 = NbtUtils.readBlockPos($$5.getCompound("LodestonePos"));
                    if (!p_40721_.isInWorldBounds($$7) || !((ServerLevel)p_40721_).getPoiManager().existsAtPosition(PoiTypes.LODESTONE, $$7)) {
                        $$5.remove("LodestonePos");
                    }
                }
            }

        }
    }

    public InteractionResult useOn(UseOnContext p_40726_) {
        BlockPos $$1 = p_40726_.getClickedPos();
        Level $$2 = p_40726_.getLevel();
        if (!$$2.getBlockState($$1).is(Blocks.LODESTONE)) {
            return super.useOn(p_40726_);
        } else {
            $$2.playSound((Player)null, (BlockPos)$$1, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
            Player $$3 = p_40726_.getPlayer();
            ItemStack $$4 = p_40726_.getItemInHand();
            boolean $$5 = !$$3.getAbilities().instabuild && $$4.getCount() == 1;
            if ($$5) {
                this.addLodestoneTags($$2.dimension(), $$1, $$4.getOrCreateTag());
            } else {
                ItemStack $$6 = new ItemStack(Items.COMPASS, 1);
                CompoundTag $$7 = $$4.hasTag() ? $$4.getTag().copy() : new CompoundTag();
                $$6.setTag($$7);
                if (!$$3.getAbilities().instabuild) {
                    $$4.shrink(1);
                }

                this.addLodestoneTags($$2.dimension(), $$1, $$7);
                if (!$$3.getInventory().add($$6)) {
                    $$3.drop($$6, false);
                }
            }

            return InteractionResult.sidedSuccess($$2.isClientSide);
        }
    }

    private void addLodestoneTags(ResourceKey<Level> p_40733_, BlockPos p_40734_, CompoundTag p_40735_) {
        p_40735_.put("LodestonePos", NbtUtils.writeBlockPos(p_40734_));
        DataResult var10000 = Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, p_40733_);
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((p_40731_) -> {
            p_40735_.put("LodestoneDimension", p_40731_);
        });
        p_40735_.putBoolean("LodestoneTracked", true);
    }

    public String getDescriptionId(ItemStack p_40741_) {
        return isLodestoneCompass(p_40741_) ? "item.minecraft.lodestone_compass" : super.getDescriptionId(p_40741_);
    }
}
