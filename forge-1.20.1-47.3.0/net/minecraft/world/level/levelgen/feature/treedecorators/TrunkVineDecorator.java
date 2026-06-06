//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;

public class TrunkVineDecorator extends TreeDecorator {
    public static final Codec<TrunkVineDecorator> CODEC = Codec.unit(() -> {
        return INSTANCE;
    });
    public static final TrunkVineDecorator INSTANCE = new TrunkVineDecorator();

    public TrunkVineDecorator() {
    }

    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.TRUNK_VINE;
    }

    public void place(TreeDecorator.Context p_226077_) {
        RandomSource $$1 = p_226077_.random();
        p_226077_.logs().forEach((p_226075_) -> {
            BlockPos $$6;
            if ($$1.nextInt(3) > 0) {
                $$6 = p_226075_.west();
                if (p_226077_.isAir($$6)) {
                    p_226077_.placeVine($$6, VineBlock.EAST);
                }
            }

            if ($$1.nextInt(3) > 0) {
                $$6 = p_226075_.east();
                if (p_226077_.isAir($$6)) {
                    p_226077_.placeVine($$6, VineBlock.WEST);
                }
            }

            if ($$1.nextInt(3) > 0) {
                $$6 = p_226075_.north();
                if (p_226077_.isAir($$6)) {
                    p_226077_.placeVine($$6, VineBlock.SOUTH);
                }
            }

            if ($$1.nextInt(3) > 0) {
                $$6 = p_226075_.south();
                if (p_226077_.isAir($$6)) {
                    p_226077_.placeVine($$6, VineBlock.NORTH);
                }
            }

        });
    }
}
