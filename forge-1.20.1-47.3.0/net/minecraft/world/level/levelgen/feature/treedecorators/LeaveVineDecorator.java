//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class LeaveVineDecorator extends TreeDecorator {
    public static final Codec<LeaveVineDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(LeaveVineDecorator::new, (p_226037_) -> {
        return p_226037_.probability;
    }).codec();
    private final float probability;

    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.LEAVE_VINE;
    }

    public LeaveVineDecorator(float p_226031_) {
        this.probability = p_226031_;
    }

    public void place(TreeDecorator.Context p_226039_) {
        RandomSource $$1 = p_226039_.random();
        p_226039_.leaves().forEach((p_226035_) -> {
            BlockPos $$6;
            if ($$1.nextFloat() < this.probability) {
                $$6 = p_226035_.west();
                if (p_226039_.isAir($$6)) {
                    addHangingVine($$6, VineBlock.EAST, p_226039_);
                }
            }

            if ($$1.nextFloat() < this.probability) {
                $$6 = p_226035_.east();
                if (p_226039_.isAir($$6)) {
                    addHangingVine($$6, VineBlock.WEST, p_226039_);
                }
            }

            if ($$1.nextFloat() < this.probability) {
                $$6 = p_226035_.north();
                if (p_226039_.isAir($$6)) {
                    addHangingVine($$6, VineBlock.SOUTH, p_226039_);
                }
            }

            if ($$1.nextFloat() < this.probability) {
                $$6 = p_226035_.south();
                if (p_226039_.isAir($$6)) {
                    addHangingVine($$6, VineBlock.NORTH, p_226039_);
                }
            }

        });
    }

    private static void addHangingVine(BlockPos p_226041_, BooleanProperty p_226042_, TreeDecorator.Context p_226043_) {
        p_226043_.placeVine(p_226041_, p_226042_);
        int $$3 = 4;

        for(p_226041_ = p_226041_.below(); p_226043_.isAir(p_226041_) && $$3 > 0; --$$3) {
            p_226043_.placeVine(p_226041_, p_226042_);
            p_226041_ = p_226041_.below();
        }

    }
}
