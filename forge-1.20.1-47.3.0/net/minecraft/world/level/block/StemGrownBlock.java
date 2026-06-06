//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

public abstract class StemGrownBlock extends Block implements IPlantable {
    public StemGrownBlock(BlockBehaviour.Properties p_57058_) {
        super(p_57058_);
    }

    public abstract StemBlock getStem();

    public abstract AttachedStemBlock getAttachedStem();

    public BlockState getPlant(BlockGetter world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() != this ? this.defaultBlockState() : state;
    }
}
