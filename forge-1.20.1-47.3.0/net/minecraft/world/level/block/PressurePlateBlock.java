//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class PressurePlateBlock extends BasePressurePlateBlock {
    public static final BooleanProperty POWERED;
    private final Sensitivity sensitivity;

    public PressurePlateBlock(Sensitivity p_273523_, BlockBehaviour.Properties p_273571_, BlockSetType p_273284_) {
        super(p_273571_, p_273284_);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false));
        this.sensitivity = p_273523_;
    }

    protected int getSignalForState(BlockState p_55270_) {
        return (Boolean)p_55270_.getValue(POWERED) ? 15 : 0;
    }

    protected BlockState setSignalForState(BlockState p_55259_, int p_55260_) {
        return (BlockState)p_55259_.setValue(POWERED, p_55260_ > 0);
    }

    protected int getSignalStrength(Level p_55264_, BlockPos p_55265_) {
        Class var10000;
        switch (this.sensitivity) {
            case EVERYTHING -> var10000 = Entity.class;
            case MOBS -> var10000 = LivingEntity.class;
            default -> throw new IncompatibleClassChangeError();
        }

        Class $$2 = var10000;
        return getEntityCount(p_55264_, TOUCH_AABB.move(p_55265_), $$2) > 0 ? 15 : 0;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55262_) {
        p_55262_.add(POWERED);
    }

    static {
        POWERED = BlockStateProperties.POWERED;
    }

    public static enum Sensitivity {
        EVERYTHING,
        MOBS;

        private Sensitivity() {
        }
    }
}
