//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/** @deprecated */
@Deprecated
public class LakeFeature extends Feature<Configuration> {
    private static final BlockState AIR;

    public LakeFeature(Codec<Configuration> p_66259_) {
        super(p_66259_);
    }

    public boolean place(FeaturePlaceContext<Configuration> p_159958_) {
        BlockPos $$1 = p_159958_.origin();
        WorldGenLevel $$2 = p_159958_.level();
        RandomSource $$3 = p_159958_.random();
        Configuration $$4 = (Configuration)p_159958_.config();
        if ($$1.getY() <= $$2.getMinBuildHeight() + 4) {
            return false;
        } else {
            $$1 = $$1.below(4);
            boolean[] $$5 = new boolean[2048];
            int $$6 = $$3.nextInt(4) + 4;

            for(int $$7 = 0; $$7 < $$6; ++$$7) {
                double $$8 = $$3.nextDouble() * 6.0 + 3.0;
                double $$9 = $$3.nextDouble() * 4.0 + 2.0;
                double $$10 = $$3.nextDouble() * 6.0 + 3.0;
                double $$11 = $$3.nextDouble() * (16.0 - $$8 - 2.0) + 1.0 + $$8 / 2.0;
                double $$12 = $$3.nextDouble() * (8.0 - $$9 - 4.0) + 2.0 + $$9 / 2.0;
                double $$13 = $$3.nextDouble() * (16.0 - $$10 - 2.0) + 1.0 + $$10 / 2.0;

                for(int $$14 = 1; $$14 < 15; ++$$14) {
                    for(int $$15 = 1; $$15 < 15; ++$$15) {
                        for(int $$16 = 1; $$16 < 7; ++$$16) {
                            double $$17 = ((double)$$14 - $$11) / ($$8 / 2.0);
                            double $$18 = ((double)$$16 - $$12) / ($$9 / 2.0);
                            double $$19 = ((double)$$15 - $$13) / ($$10 / 2.0);
                            double $$20 = $$17 * $$17 + $$18 * $$18 + $$19 * $$19;
                            if ($$20 < 1.0) {
                                $$5[($$14 * 16 + $$15) * 8 + $$16] = true;
                            }
                        }
                    }
                }
            }

            BlockState $$21 = $$4.fluid().getState($$3, $$1);

            int $$33;
            boolean $$41;
            int $$22;
            int $$34;
            for($$22 = 0; $$22 < 16; ++$$22) {
                for($$33 = 0; $$33 < 16; ++$$33) {
                    for($$34 = 0; $$34 < 8; ++$$34) {
                        $$41 = !$$5[($$22 * 16 + $$33) * 8 + $$34] && ($$22 < 15 && $$5[(($$22 + 1) * 16 + $$33) * 8 + $$34] || $$22 > 0 && $$5[(($$22 - 1) * 16 + $$33) * 8 + $$34] || $$33 < 15 && $$5[($$22 * 16 + $$33 + 1) * 8 + $$34] || $$33 > 0 && $$5[($$22 * 16 + ($$33 - 1)) * 8 + $$34] || $$34 < 7 && $$5[($$22 * 16 + $$33) * 8 + $$34 + 1] || $$34 > 0 && $$5[($$22 * 16 + $$33) * 8 + ($$34 - 1)]);
                        if ($$41) {
                            BlockState $$26 = $$2.getBlockState($$1.offset($$22, $$34, $$33));
                            if ($$34 >= 4 && $$26.liquid()) {
                                return false;
                            }

                            if ($$34 < 4 && !$$26.isSolid() && $$2.getBlockState($$1.offset($$22, $$34, $$33)) != $$21) {
                                return false;
                            }
                        }
                    }
                }
            }

            boolean $$36;
            for($$22 = 0; $$22 < 16; ++$$22) {
                for($$33 = 0; $$33 < 16; ++$$33) {
                    for($$34 = 0; $$34 < 8; ++$$34) {
                        if ($$5[($$22 * 16 + $$33) * 8 + $$34]) {
                            BlockPos $$30 = $$1.offset($$22, $$34, $$33);
                            if (this.canReplaceBlock($$2.getBlockState($$30))) {
                                $$36 = $$34 >= 4;
                                $$2.setBlock($$30, $$36 ? AIR : $$21, 2);
                                if ($$36) {
                                    $$2.scheduleTick($$30, AIR.getBlock(), 0);
                                    this.markAboveForPostProcessing($$2, $$30);
                                }
                            }
                        }
                    }
                }
            }

            BlockState $$32 = $$4.barrier().getState($$3, $$1);
            if (!$$32.isAir()) {
                for($$33 = 0; $$33 < 16; ++$$33) {
                    for($$34 = 0; $$34 < 16; ++$$34) {
                        for(int $$35 = 0; $$35 < 8; ++$$35) {
                            $$36 = !$$5[($$33 * 16 + $$34) * 8 + $$35] && ($$33 < 15 && $$5[(($$33 + 1) * 16 + $$34) * 8 + $$35] || $$33 > 0 && $$5[(($$33 - 1) * 16 + $$34) * 8 + $$35] || $$34 < 15 && $$5[($$33 * 16 + $$34 + 1) * 8 + $$35] || $$34 > 0 && $$5[($$33 * 16 + ($$34 - 1)) * 8 + $$35] || $$35 < 7 && $$5[($$33 * 16 + $$34) * 8 + $$35 + 1] || $$35 > 0 && $$5[($$33 * 16 + $$34) * 8 + ($$35 - 1)]);
                            if ($$36 && ($$35 < 4 || $$3.nextInt(2) != 0)) {
                                BlockState $$37 = $$2.getBlockState($$1.offset($$33, $$35, $$34));
                                if ($$37.isSolid() && !$$37.is(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE)) {
                                    BlockPos $$38 = $$1.offset($$33, $$35, $$34);
                                    $$2.setBlock($$38, $$32, 2);
                                    this.markAboveForPostProcessing($$2, $$38);
                                }
                            }
                        }
                    }
                }
            }

            if ($$21.getFluidState().is(FluidTags.WATER)) {
                for($$33 = 0; $$33 < 16; ++$$33) {
                    for($$34 = 0; $$34 < 16; ++$$34) {
                        $$41 = true;
                        BlockPos $$42 = $$1.offset($$33, 4, $$34);
                        if (((Biome)$$2.getBiome($$42).value()).shouldFreeze($$2, $$42, false) && this.canReplaceBlock($$2.getBlockState($$42))) {
                            $$2.setBlock($$42, Blocks.ICE.defaultBlockState(), 2);
                        }
                    }
                }
            }

            return true;
        }
    }

    private boolean canReplaceBlock(BlockState p_190952_) {
        return !p_190952_.is(BlockTags.FEATURES_CANNOT_REPLACE);
    }

    static {
        AIR = Blocks.CAVE_AIR.defaultBlockState();
    }

    public static record Configuration(BlockStateProvider fluid, BlockStateProvider barrier) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create((p_190962_) -> {
            return p_190962_.group(BlockStateProvider.CODEC.fieldOf("fluid").forGetter(Configuration::fluid), BlockStateProvider.CODEC.fieldOf("barrier").forGetter(Configuration::barrier)).apply(p_190962_, Configuration::new);
        });

        public Configuration(BlockStateProvider fluid, BlockStateProvider barrier) {
            this.fluid = fluid;
            this.barrier = barrier;
        }

        public BlockStateProvider fluid() {
            return this.fluid;
        }

        public BlockStateProvider barrier() {
            return this.barrier;
        }
    }
}
