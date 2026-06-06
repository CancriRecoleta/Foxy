//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public abstract class CoralFeature extends Feature<NoneFeatureConfiguration> {
    public CoralFeature(Codec<NoneFeatureConfiguration> p_65429_) {
        super(p_65429_);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_159536_) {
        RandomSource $$1 = p_159536_.random();
        WorldGenLevel $$2 = p_159536_.level();
        BlockPos $$3 = p_159536_.origin();
        Optional<Block> $$4 = BuiltInRegistries.BLOCK.getTag(BlockTags.CORAL_BLOCKS).flatMap((p_224980_) -> {
            return p_224980_.getRandomElement($$1);
        }).map(Holder::value);
        return $$4.isEmpty() ? false : this.placeFeature($$2, $$1, $$3, ((Block)$$4.get()).defaultBlockState());
    }

    protected abstract boolean placeFeature(LevelAccessor var1, RandomSource var2, BlockPos var3, BlockState var4);

    protected boolean placeCoralBlock(LevelAccessor p_224974_, RandomSource p_224975_, BlockPos p_224976_, BlockState p_224977_) {
        BlockPos $$4 = p_224976_.above();
        BlockState $$5 = p_224974_.getBlockState(p_224976_);
        if (($$5.is(Blocks.WATER) || $$5.is(BlockTags.CORALS)) && p_224974_.getBlockState($$4).is(Blocks.WATER)) {
            p_224974_.setBlock(p_224976_, p_224977_, 3);
            if (p_224975_.nextFloat() < 0.25F) {
                BuiltInRegistries.BLOCK.getTag(BlockTags.CORALS).flatMap((p_224972_) -> {
                    return p_224972_.getRandomElement(p_224975_);
                }).map(Holder::value).ifPresent((p_204720_) -> {
                    p_224974_.setBlock($$4, p_204720_.defaultBlockState(), 2);
                });
            } else if (p_224975_.nextFloat() < 0.05F) {
                p_224974_.setBlock($$4, (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, p_224975_.nextInt(4) + 1), 2);
            }

            Iterator var7 = Plane.HORIZONTAL.iterator();

            while(var7.hasNext()) {
                Direction $$6 = (Direction)var7.next();
                if (p_224975_.nextFloat() < 0.2F) {
                    BlockPos $$7 = p_224976_.relative($$6);
                    if (p_224974_.getBlockState($$7).is(Blocks.WATER)) {
                        BuiltInRegistries.BLOCK.getTag(BlockTags.WALL_CORALS).flatMap((p_224965_) -> {
                            return p_224965_.getRandomElement(p_224975_);
                        }).map(Holder::value).ifPresent((p_204725_) -> {
                            BlockState $$4 = p_204725_.defaultBlockState();
                            if ($$4.hasProperty(BaseCoralWallFanBlock.FACING)) {
                                $$4 = (BlockState)$$4.setValue(BaseCoralWallFanBlock.FACING, $$6);
                            }

                            p_224974_.setBlock($$7, $$4, 2);
                        });
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
