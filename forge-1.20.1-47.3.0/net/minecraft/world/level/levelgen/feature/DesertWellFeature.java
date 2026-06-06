//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class DesertWellFeature extends Feature<NoneFeatureConfiguration> {
    private static final BlockStatePredicate IS_SAND;
    private final BlockState sand;
    private final BlockState sandSlab;
    private final BlockState sandstone;
    private final BlockState water;

    public DesertWellFeature(Codec<NoneFeatureConfiguration> p_65599_) {
        super(p_65599_);
        this.sand = Blocks.SAND.defaultBlockState();
        this.sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
        this.sandstone = Blocks.SANDSTONE.defaultBlockState();
        this.water = Blocks.WATER.defaultBlockState();
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_159571_) {
        WorldGenLevel $$1 = p_159571_.level();
        BlockPos $$2 = p_159571_.origin();

        for($$2 = $$2.above(); $$1.isEmptyBlock($$2) && $$2.getY() > $$1.getMinBuildHeight() + 2; $$2 = $$2.below()) {
        }

        if (!IS_SAND.test($$1.getBlockState($$2))) {
            return false;
        } else {
            int $$5;
            int $$13;
            for($$5 = -2; $$5 <= 2; ++$$5) {
                for($$13 = -2; $$13 <= 2; ++$$13) {
                    if ($$1.isEmptyBlock($$2.offset($$5, -1, $$13)) && $$1.isEmptyBlock($$2.offset($$5, -2, $$13))) {
                        return false;
                    }
                }
            }

            int $$14;
            for($$5 = -2; $$5 <= 0; ++$$5) {
                for($$13 = -2; $$13 <= 2; ++$$13) {
                    for($$14 = -2; $$14 <= 2; ++$$14) {
                        $$1.setBlock($$2.offset($$13, $$5, $$14), this.sandstone, 2);
                    }
                }
            }

            $$1.setBlock($$2, this.water, 2);
            Iterator var8 = Plane.HORIZONTAL.iterator();

            while(var8.hasNext()) {
                Direction $$8 = (Direction)var8.next();
                $$1.setBlock($$2.relative($$8), this.water, 2);
            }

            BlockPos $$9 = $$2.below();
            $$1.setBlock($$9, this.sand, 2);
            Iterator var11 = Plane.HORIZONTAL.iterator();

            while(var11.hasNext()) {
                Direction $$10 = (Direction)var11.next();
                $$1.setBlock($$9.relative($$10), this.sand, 2);
            }

            for($$13 = -2; $$13 <= 2; ++$$13) {
                for($$14 = -2; $$14 <= 2; ++$$14) {
                    if ($$13 == -2 || $$13 == 2 || $$14 == -2 || $$14 == 2) {
                        $$1.setBlock($$2.offset($$13, 1, $$14), this.sandstone, 2);
                    }
                }
            }

            $$1.setBlock($$2.offset(2, 1, 0), this.sandSlab, 2);
            $$1.setBlock($$2.offset(-2, 1, 0), this.sandSlab, 2);
            $$1.setBlock($$2.offset(0, 1, 2), this.sandSlab, 2);
            $$1.setBlock($$2.offset(0, 1, -2), this.sandSlab, 2);

            for($$13 = -1; $$13 <= 1; ++$$13) {
                for($$14 = -1; $$14 <= 1; ++$$14) {
                    if ($$13 == 0 && $$14 == 0) {
                        $$1.setBlock($$2.offset($$13, 4, $$14), this.sandstone, 2);
                    } else {
                        $$1.setBlock($$2.offset($$13, 4, $$14), this.sandSlab, 2);
                    }
                }
            }

            for($$13 = 1; $$13 <= 3; ++$$13) {
                $$1.setBlock($$2.offset(-1, $$13, -1), this.sandstone, 2);
                $$1.setBlock($$2.offset(-1, $$13, 1), this.sandstone, 2);
                $$1.setBlock($$2.offset(1, $$13, -1), this.sandstone, 2);
                $$1.setBlock($$2.offset(1, $$13, 1), this.sandstone, 2);
            }

            List<BlockPos> $$17 = List.of($$2, $$2.east(), $$2.south(), $$2.west(), $$2.north());
            RandomSource $$18 = p_159571_.random();
            placeSusSand($$1, ((BlockPos)Util.getRandom($$17, $$18)).below(1));
            placeSusSand($$1, ((BlockPos)Util.getRandom($$17, $$18)).below(2));
            return true;
        }
    }

    private static void placeSusSand(WorldGenLevel p_278029_, BlockPos p_278082_) {
        p_278029_.setBlock(p_278082_, Blocks.SUSPICIOUS_SAND.defaultBlockState(), 3);
        p_278029_.getBlockEntity(p_278082_, BlockEntityType.BRUSHABLE_BLOCK).ifPresent((p_277322_) -> {
            p_277322_.setLootTable(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY, p_278082_.asLong());
        });
    }

    static {
        IS_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
    }
}
