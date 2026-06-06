//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.slf4j.Logger;

public class SculkSensorBlockEntity extends BlockEntity implements GameEventListener.Holder<VibrationSystem.Listener>, VibrationSystem {
    private static final Logger LOGGER = LogUtils.getLogger();
    private VibrationSystem.Data vibrationData;
    private final VibrationSystem.Listener vibrationListener;
    private final VibrationSystem.User vibrationUser;
    private int lastVibrationFrequency;

    protected SculkSensorBlockEntity(BlockEntityType<?> p_277405_, BlockPos p_277502_, BlockState p_277699_) {
        super(p_277405_, p_277502_, p_277699_);
        this.vibrationUser = this.createVibrationUser();
        this.vibrationData = new VibrationSystem.Data();
        this.vibrationListener = new VibrationSystem.Listener(this);
    }

    public SculkSensorBlockEntity(BlockPos p_155635_, BlockState p_155636_) {
        this(BlockEntityType.SCULK_SENSOR, p_155635_, p_155636_);
    }

    public VibrationSystem.User createVibrationUser() {
        return new VibrationUser(this.getBlockPos());
    }

    public void load(CompoundTag p_155649_) {
        super.load(p_155649_);
        this.lastVibrationFrequency = p_155649_.getInt("last_vibration_frequency");
        if (p_155649_.contains("listener", 10)) {
            DataResult var10000 = net.minecraft.world.level.gameevent.vibrations.VibrationSystem.Data.CODEC.parse(new Dynamic(NbtOps.INSTANCE, p_155649_.getCompound("listener")));
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            var10000.resultOrPartial(var10001::error).ifPresent((p_281146_) -> {
                this.vibrationData = p_281146_;
            });
        }

    }

    protected void saveAdditional(CompoundTag p_187511_) {
        super.saveAdditional(p_187511_);
        p_187511_.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        DataResult var10000 = net.minecraft.world.level.gameevent.vibrations.VibrationSystem.Data.CODEC.encodeStart(NbtOps.INSTANCE, this.vibrationData);
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((p_222820_) -> {
            p_187511_.put("listener", p_222820_);
        });
    }

    public VibrationSystem.Data getVibrationData() {
        return this.vibrationData;
    }

    public VibrationSystem.User getVibrationUser() {
        return this.vibrationUser;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    public void setLastVibrationFrequency(int p_222801_) {
        this.lastVibrationFrequency = p_222801_;
    }

    public VibrationSystem.Listener getListener() {
        return this.vibrationListener;
    }

    protected class VibrationUser implements VibrationSystem.User {
        public static final int LISTENER_RANGE = 8;
        protected final BlockPos blockPos;
        private final PositionSource positionSource;

        public VibrationUser(BlockPos p_283482_) {
            this.blockPos = p_283482_;
            this.positionSource = new BlockPositionSource(p_283482_);
        }

        public int getListenerRadius() {
            return 8;
        }

        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        public boolean canTriggerAvoidVibration() {
            return true;
        }

        public boolean canReceiveVibration(ServerLevel p_282127_, BlockPos p_283268_, GameEvent p_282187_, @Nullable GameEvent.Context p_282856_) {
            return !p_283268_.equals(this.blockPos) || p_282187_ != GameEvent.BLOCK_DESTROY && p_282187_ != GameEvent.BLOCK_PLACE ? SculkSensorBlock.canActivate(SculkSensorBlockEntity.this.getBlockState()) : false;
        }

        public void onReceiveVibration(ServerLevel p_282851_, BlockPos p_281608_, GameEvent p_282979_, @Nullable Entity p_282123_, @Nullable Entity p_283090_, float p_283130_) {
            BlockState $$6 = SculkSensorBlockEntity.this.getBlockState();
            if (SculkSensorBlock.canActivate($$6)) {
                SculkSensorBlockEntity.this.setLastVibrationFrequency(VibrationSystem.getGameEventFrequency(p_282979_));
                int $$7 = VibrationSystem.getRedstoneStrengthForDistance(p_283130_, this.getListenerRadius());
                Block var10 = $$6.getBlock();
                if (var10 instanceof SculkSensorBlock) {
                    SculkSensorBlock $$8 = (SculkSensorBlock)var10;
                    $$8.activate(p_282123_, p_282851_, this.blockPos, $$6, $$7, SculkSensorBlockEntity.this.getLastVibrationFrequency());
                }
            }

        }

        public void onDataChanged() {
            SculkSensorBlockEntity.this.setChanged();
        }

        public boolean requiresAdjacentChunksToBeTicking() {
            return true;
        }
    }
}
