//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class WaterloggedVegetationPatchFeature extends VegetationPatchFeature {
    public WaterloggedVegetationPatchFeature(Codec<VegetationPatchConfiguration> p_160635_) {
        super(p_160635_);
    }

    protected Set<BlockPos> placeGroundPatch(WorldGenLevel p_225339_, VegetationPatchConfiguration p_225340_, RandomSource p_225341_, BlockPos p_225342_, Predicate<BlockState> p_225343_, int p_225344_, int p_225345_) {
        Set<BlockPos> $$7 = super.placeGroundPatch(p_225339_, p_225340_, p_225341_, p_225342_, p_225343_, p_225344_, p_225345_);
        Set<BlockPos> $$8 = new HashSet();
        BlockPos.MutableBlockPos $$9 = new BlockPos.MutableBlockPos();
        Iterator var11 = $$7.iterator();

        BlockPos $$11;
        while(var11.hasNext()) {
            $$11 = (BlockPos)var11.next();
            if (!isExposed(p_225339_, $$7, $$11, $$9)) {
                $$8.add($$11);
            }
        }

        var11 = $$8.iterator();

        while(var11.hasNext()) {
            $$11 = (BlockPos)var11.next();
            p_225339_.setBlock($$11, Blocks.WATER.defaultBlockState(), 2);
        }

        return $$8;
    }

    private static boolean isExposed(WorldGenLevel p_160656_, Set<BlockPos> p_160657_, BlockPos p_160658_, BlockPos.MutableBlockPos p_160659_) {
        return isExposedDirection(p_160656_, p_160658_, p_160659_, Direction.NORTH) || isExposedDirection(p_160656_, p_160658_, p_160659_, Direction.EAST) || isExposedDirection(p_160656_, p_160658_, p_160659_, Direction.SOUTH) || isExposedDirection(p_160656_, p_160658_, p_160659_, Direction.WEST) || isExposedDirection(p_160656_, p_160658_, p_160659_, Direction.DOWN);
    }

    private static boolean isExposedDirection(WorldGenLevel p_160651_, BlockPos p_160652_, BlockPos.MutableBlockPos p_160653_, Direction p_160654_) {
        p_160653_.setWithOffset(p_160652_, (Direction)p_160654_);
        return !p_160651_.getBlockState(p_160653_).isFaceSturdy(p_160651_, p_160653_, p_160654_.getOpposite());
    }

    protected boolean placeVegetation(WorldGenLevel p_225347_, VegetationPatchConfiguration p_225348_, ChunkGenerator p_225349_, RandomSource p_225350_, BlockPos p_225351_) {
        if (super.placeVegetation(p_225347_, p_225348_, p_225349_, p_225350_, p_225351_.below())) {
            BlockState $$5 = p_225347_.getBlockState(p_225351_);
            if ($$5.hasProperty(BlockStateProperties.WATERLOGGED) && !(Boolean)$$5.getValue(BlockStateProperties.WATERLOGGED)) {
                p_225347_.setBlock(p_225351_, (BlockState)$$5.setValue(BlockStateProperties.WATERLOGGED, true), 2);
            }

            return true;
        } else {
            return false;
        }
    }
}
