//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class AttachedToLeavesDecorator extends TreeDecorator {
    public static final Codec<AttachedToLeavesDecorator> CODEC = RecordCodecBuilder.create((p_225996_) -> {
        return p_225996_.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((p_226014_) -> {
            return p_226014_.probability;
        }), Codec.intRange(0, 16).fieldOf("exclusion_radius_xz").forGetter((p_226012_) -> {
            return p_226012_.exclusionRadiusXZ;
        }), Codec.intRange(0, 16).fieldOf("exclusion_radius_y").forGetter((p_226010_) -> {
            return p_226010_.exclusionRadiusY;
        }), BlockStateProvider.CODEC.fieldOf("block_provider").forGetter((p_226008_) -> {
            return p_226008_.blockProvider;
        }), Codec.intRange(1, 16).fieldOf("required_empty_blocks").forGetter((p_226006_) -> {
            return p_226006_.requiredEmptyBlocks;
        }), ExtraCodecs.nonEmptyList(Direction.CODEC.listOf()).fieldOf("directions").forGetter((p_225998_) -> {
            return p_225998_.directions;
        })).apply(p_225996_, AttachedToLeavesDecorator::new);
    });
    protected final float probability;
    protected final int exclusionRadiusXZ;
    protected final int exclusionRadiusY;
    protected final BlockStateProvider blockProvider;
    protected final int requiredEmptyBlocks;
    protected final List<Direction> directions;

    public AttachedToLeavesDecorator(float p_225988_, int p_225989_, int p_225990_, BlockStateProvider p_225991_, int p_225992_, List<Direction> p_225993_) {
        this.probability = p_225988_;
        this.exclusionRadiusXZ = p_225989_;
        this.exclusionRadiusY = p_225990_;
        this.blockProvider = p_225991_;
        this.requiredEmptyBlocks = p_225992_;
        this.directions = p_225993_;
    }

    public void place(TreeDecorator.Context p_226000_) {
        Set<BlockPos> $$1 = new HashSet();
        RandomSource $$2 = p_226000_.random();
        Iterator var4 = Util.shuffledCopy(p_226000_.leaves(), $$2).iterator();

        while(true) {
            BlockPos $$3;
            Direction $$4;
            BlockPos $$5;
            do {
                do {
                    do {
                        if (!var4.hasNext()) {
                            return;
                        }

                        $$3 = (BlockPos)var4.next();
                        $$4 = (Direction)Util.getRandom(this.directions, $$2);
                        $$5 = $$3.relative($$4);
                    } while($$1.contains($$5));
                } while(!($$2.nextFloat() < this.probability));
            } while(!this.hasRequiredEmptyBlocks(p_226000_, $$3, $$4));

            BlockPos $$6 = $$5.offset(-this.exclusionRadiusXZ, -this.exclusionRadiusY, -this.exclusionRadiusXZ);
            BlockPos $$7 = $$5.offset(this.exclusionRadiusXZ, this.exclusionRadiusY, this.exclusionRadiusXZ);
            Iterator var10 = BlockPos.betweenClosed($$6, $$7).iterator();

            while(var10.hasNext()) {
                BlockPos $$8 = (BlockPos)var10.next();
                $$1.add($$8.immutable());
            }

            p_226000_.setBlock($$5, this.blockProvider.getState($$2, $$5));
        }
    }

    private boolean hasRequiredEmptyBlocks(TreeDecorator.Context p_226002_, BlockPos p_226003_, Direction p_226004_) {
        for(int $$3 = 1; $$3 <= this.requiredEmptyBlocks; ++$$3) {
            BlockPos $$4 = p_226003_.relative(p_226004_, $$3);
            if (!p_226002_.isAir($$4)) {
                return false;
            }
        }

        return true;
    }

    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.ATTACHED_TO_LEAVES;
    }
}
