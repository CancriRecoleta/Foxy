//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WeightedPressurePlateBlock extends BasePressurePlateBlock {
    public static final IntegerProperty POWER;
    private final int maxWeight;

    public WeightedPressurePlateBlock(int p_273669_, BlockBehaviour.Properties p_273512_, BlockSetType p_272868_) {
        super(p_273512_, p_272868_);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWER, 0));
        this.maxWeight = p_273669_;
    }

    protected int getSignalStrength(Level p_58213_, BlockPos p_58214_) {
        int $$2 = Math.min(getEntityCount(p_58213_, TOUCH_AABB.move(p_58214_), Entity.class), this.maxWeight);
        if ($$2 > 0) {
            float $$3 = (float)Math.min(this.maxWeight, $$2) / (float)this.maxWeight;
            return Mth.ceil($$3 * 15.0F);
        } else {
            return 0;
        }
    }

    protected int getSignalForState(BlockState p_58220_) {
        return (Integer)p_58220_.getValue(POWER);
    }

    protected BlockState setSignalForState(BlockState p_58208_, int p_58209_) {
        return (BlockState)p_58208_.setValue(POWER, p_58209_);
    }

    protected int getPressedTime() {
        return 10;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58211_) {
        p_58211_.add(POWER);
    }

    static {
        POWER = BlockStateProperties.POWER;
    }
}
