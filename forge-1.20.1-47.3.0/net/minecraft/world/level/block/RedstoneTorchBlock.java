//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RedstoneTorchBlock extends TorchBlock {
    public static final BooleanProperty LIT;
    private static final Map<BlockGetter, List<Toggle>> RECENT_TOGGLES;
    public static final int RECENT_TOGGLE_TIMER = 60;
    public static final int MAX_RECENT_TOGGLES = 8;
    public static final int RESTART_DELAY = 160;
    private static final int TOGGLE_DELAY = 2;

    public RedstoneTorchBlock(BlockBehaviour.Properties p_55678_) {
        super(p_55678_, DustParticleOptions.REDSTONE);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true));
    }

    public void onPlace(BlockState p_55724_, Level p_55725_, BlockPos p_55726_, BlockState p_55727_, boolean p_55728_) {
        Direction[] var6 = Direction.values();
        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Direction $$5 = var6[var8];
            p_55725_.updateNeighborsAt(p_55726_.relative($$5), this);
        }

    }

    public void onRemove(BlockState p_55706_, Level p_55707_, BlockPos p_55708_, BlockState p_55709_, boolean p_55710_) {
        if (!p_55710_) {
            Direction[] var6 = Direction.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Direction $$5 = var6[var8];
                p_55707_.updateNeighborsAt(p_55708_.relative($$5), this);
            }

        }
    }

    public int getSignal(BlockState p_55694_, BlockGetter p_55695_, BlockPos p_55696_, Direction p_55697_) {
        return (Boolean)p_55694_.getValue(LIT) && Direction.UP != p_55697_ ? 15 : 0;
    }

    protected boolean hasNeighborSignal(Level p_55681_, BlockPos p_55682_, BlockState p_55683_) {
        return p_55681_.hasSignal(p_55682_.below(), Direction.DOWN);
    }

    public void tick(BlockState p_221949_, ServerLevel p_221950_, BlockPos p_221951_, RandomSource p_221952_) {
        boolean $$4 = this.hasNeighborSignal(p_221950_, p_221951_, p_221949_);
        List<Toggle> $$5 = (List)RECENT_TOGGLES.get(p_221950_);

        while($$5 != null && !$$5.isEmpty() && p_221950_.getGameTime() - ((Toggle)$$5.get(0)).when > 60L) {
            $$5.remove(0);
        }

        if ((Boolean)p_221949_.getValue(LIT)) {
            if ($$4) {
                p_221950_.setBlock(p_221951_, (BlockState)p_221949_.setValue(LIT, false), 3);
                if (isToggledTooFrequently(p_221950_, p_221951_, true)) {
                    p_221950_.levelEvent(1502, p_221951_, 0);
                    p_221950_.scheduleTick(p_221951_, p_221950_.getBlockState(p_221951_).getBlock(), 160);
                }
            }
        } else if (!$$4 && !isToggledTooFrequently(p_221950_, p_221951_, false)) {
            p_221950_.setBlock(p_221951_, (BlockState)p_221949_.setValue(LIT, true), 3);
        }

    }

    public void neighborChanged(BlockState p_55699_, Level p_55700_, BlockPos p_55701_, Block p_55702_, BlockPos p_55703_, boolean p_55704_) {
        if ((Boolean)p_55699_.getValue(LIT) == this.hasNeighborSignal(p_55700_, p_55701_, p_55699_) && !p_55700_.getBlockTicks().willTickThisTick(p_55701_, this)) {
            p_55700_.scheduleTick(p_55701_, this, 2);
        }

    }

    public int getDirectSignal(BlockState p_55719_, BlockGetter p_55720_, BlockPos p_55721_, Direction p_55722_) {
        return p_55722_ == Direction.DOWN ? p_55719_.getSignal(p_55720_, p_55721_, p_55722_) : 0;
    }

    public boolean isSignalSource(BlockState p_55730_) {
        return true;
    }

    public void animateTick(BlockState p_221954_, Level p_221955_, BlockPos p_221956_, RandomSource p_221957_) {
        if ((Boolean)p_221954_.getValue(LIT)) {
            double $$4 = (double)p_221956_.getX() + 0.5 + (p_221957_.nextDouble() - 0.5) * 0.2;
            double $$5 = (double)p_221956_.getY() + 0.7 + (p_221957_.nextDouble() - 0.5) * 0.2;
            double $$6 = (double)p_221956_.getZ() + 0.5 + (p_221957_.nextDouble() - 0.5) * 0.2;
            p_221955_.addParticle(this.flameParticle, $$4, $$5, $$6, 0.0, 0.0, 0.0);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55717_) {
        p_55717_.add(LIT);
    }

    private static boolean isToggledTooFrequently(Level p_55685_, BlockPos p_55686_, boolean p_55687_) {
        List<Toggle> $$3 = (List)RECENT_TOGGLES.computeIfAbsent(p_55685_, (p_55680_) -> {
            return Lists.newArrayList();
        });
        if (p_55687_) {
            $$3.add(new Toggle(p_55686_.immutable(), p_55685_.getGameTime()));
        }

        int $$4 = 0;

        for(int $$5 = 0; $$5 < $$3.size(); ++$$5) {
            Toggle $$6 = (Toggle)$$3.get($$5);
            if ($$6.pos.equals(p_55686_)) {
                ++$$4;
                if ($$4 >= 8) {
                    return true;
                }
            }
        }

        return false;
    }

    static {
        LIT = BlockStateProperties.LIT;
        RECENT_TOGGLES = new WeakHashMap();
    }

    public static class Toggle {
        final BlockPos pos;
        final long when;

        public Toggle(BlockPos p_55734_, long p_55735_) {
            this.pos = p_55734_;
            this.when = p_55735_;
        }
    }
}
