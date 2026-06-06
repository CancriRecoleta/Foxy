//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CocoaDecorator extends TreeDecorator {
    public static final Codec<CocoaDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(CocoaDecorator::new, (p_69989_) -> {
        return p_69989_.probability;
    }).codec();
    private final float probability;

    public CocoaDecorator(float p_69976_) {
        this.probability = p_69976_;
    }

    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.COCOA;
    }

    public void place(TreeDecorator.Context p_226028_) {
        RandomSource $$1 = p_226028_.random();
        if (!($$1.nextFloat() >= this.probability)) {
            List<BlockPos> $$2 = p_226028_.logs();
            int $$3 = ((BlockPos)$$2.get(0)).getY();
            $$2.stream().filter((p_69980_) -> {
                return p_69980_.getY() - $$3 <= 2;
            }).forEach((p_226026_) -> {
                Iterator var3 = Plane.HORIZONTAL.iterator();

                while(var3.hasNext()) {
                    Direction $$3 = (Direction)var3.next();
                    if ($$1.nextFloat() <= 0.25F) {
                        Direction $$4 = $$3.getOpposite();
                        BlockPos $$5 = p_226026_.offset($$4.getStepX(), 0, $$4.getStepZ());
                        if (p_226028_.isAir($$5)) {
                            p_226028_.setBlock($$5, (BlockState)((BlockState)Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, $$1.nextInt(3))).setValue(CocoaBlock.FACING, $$3));
                        }
                    }
                }

            });
        }
    }
}
