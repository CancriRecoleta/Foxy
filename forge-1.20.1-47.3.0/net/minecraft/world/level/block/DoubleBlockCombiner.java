//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class DoubleBlockCombiner {
    public DoubleBlockCombiner() {
    }

    public static <S extends BlockEntity> NeighborCombineResult<S> combineWithNeigbour(BlockEntityType<S> p_52823_, Function<BlockState, BlockType> p_52824_, Function<BlockState, Direction> p_52825_, DirectionProperty p_52826_, BlockState p_52827_, LevelAccessor p_52828_, BlockPos p_52829_, BiPredicate<LevelAccessor, BlockPos> p_52830_) {
        S $$8 = p_52823_.getBlockEntity(p_52828_, p_52829_);
        if ($$8 == null) {
            return Combiner::acceptNone;
        } else if (p_52830_.test(p_52828_, p_52829_)) {
            return Combiner::acceptNone;
        } else {
            BlockType $$9 = (BlockType)p_52824_.apply(p_52827_);
            boolean $$10 = $$9 == net.minecraft.world.level.block.DoubleBlockCombiner.BlockType.SINGLE;
            boolean $$11 = $$9 == net.minecraft.world.level.block.DoubleBlockCombiner.BlockType.FIRST;
            if ($$10) {
                return new NeighborCombineResult.Single($$8);
            } else {
                BlockPos $$12 = p_52829_.relative((Direction)p_52825_.apply(p_52827_));
                BlockState $$13 = p_52828_.getBlockState($$12);
                if ($$13.is(p_52827_.getBlock())) {
                    BlockType $$14 = (BlockType)p_52824_.apply($$13);
                    if ($$14 != net.minecraft.world.level.block.DoubleBlockCombiner.BlockType.SINGLE && $$9 != $$14 && $$13.getValue(p_52826_) == p_52827_.getValue(p_52826_)) {
                        if (p_52830_.test(p_52828_, $$12)) {
                            return Combiner::acceptNone;
                        }

                        S $$15 = p_52823_.getBlockEntity(p_52828_, $$12);
                        if ($$15 != null) {
                            S $$16 = $$11 ? $$8 : $$15;
                            S $$17 = $$11 ? $$15 : $$8;
                            return new NeighborCombineResult.Double($$16, $$17);
                        }
                    }
                }

                return new NeighborCombineResult.Single($$8);
            }
        }
    }

    public interface NeighborCombineResult<S> {
        <T> T apply(Combiner<? super S, T> var1);

        public static final class Single<S> implements NeighborCombineResult<S> {
            private final S single;

            public Single(S p_52855_) {
                this.single = p_52855_;
            }

            public <T> T apply(Combiner<? super S, T> p_52857_) {
                return p_52857_.acceptSingle(this.single);
            }
        }

        public static final class Double<S> implements NeighborCombineResult<S> {
            private final S first;
            private final S second;

            public Double(S p_52849_, S p_52850_) {
                this.first = p_52849_;
                this.second = p_52850_;
            }

            public <T> T apply(Combiner<? super S, T> p_52852_) {
                return p_52852_.acceptDouble(this.first, this.second);
            }
        }
    }

    public static enum BlockType {
        SINGLE,
        FIRST,
        SECOND;

        private BlockType() {
        }
    }

    public interface Combiner<S, T> {
        T acceptDouble(S var1, S var2);

        T acceptSingle(S var1);

        T acceptNone();
    }
}
