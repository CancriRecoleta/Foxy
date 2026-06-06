//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class SurfaceRules {
    public static final ConditionSource ON_FLOOR;
    public static final ConditionSource UNDER_FLOOR;
    public static final ConditionSource DEEP_UNDER_FLOOR;
    public static final ConditionSource VERY_DEEP_UNDER_FLOOR;
    public static final ConditionSource ON_CEILING;
    public static final ConditionSource UNDER_CEILING;

    public SurfaceRules() {
    }

    public static ConditionSource stoneDepthCheck(int p_202177_, boolean p_202178_, CaveSurface p_202179_) {
        return new StoneDepthCheck(p_202177_, p_202178_, 0, p_202179_);
    }

    public static ConditionSource stoneDepthCheck(int p_202172_, boolean p_202173_, int p_202174_, CaveSurface p_202175_) {
        return new StoneDepthCheck(p_202172_, p_202173_, p_202174_, p_202175_);
    }

    public static ConditionSource not(ConditionSource p_189393_) {
        return new NotConditionSource(p_189393_);
    }

    public static ConditionSource yBlockCheck(VerticalAnchor p_189401_, int p_189402_) {
        return new YConditionSource(p_189401_, p_189402_, false);
    }

    public static ConditionSource yStartCheck(VerticalAnchor p_189423_, int p_189424_) {
        return new YConditionSource(p_189423_, p_189424_, true);
    }

    public static ConditionSource waterBlockCheck(int p_189383_, int p_189384_) {
        return new WaterConditionSource(p_189383_, p_189384_, false);
    }

    public static ConditionSource waterStartCheck(int p_189420_, int p_189421_) {
        return new WaterConditionSource(p_189420_, p_189421_, true);
    }

    @SafeVarargs
    public static ConditionSource isBiome(ResourceKey<Biome>... p_189417_) {
        return isBiome(List.of(p_189417_));
    }

    private static BiomeConditionSource isBiome(List<ResourceKey<Biome>> p_189408_) {
        return new BiomeConditionSource(p_189408_);
    }

    public static ConditionSource noiseCondition(ResourceKey<NormalNoise.NoiseParameters> p_189410_, double p_189411_) {
        return noiseCondition(p_189410_, p_189411_, Double.MAX_VALUE);
    }

    public static ConditionSource noiseCondition(ResourceKey<NormalNoise.NoiseParameters> p_189413_, double p_189414_, double p_189415_) {
        return new NoiseThresholdConditionSource(p_189413_, p_189414_, p_189415_);
    }

    public static ConditionSource verticalGradient(String p_189404_, VerticalAnchor p_189405_, VerticalAnchor p_189406_) {
        return new VerticalGradientConditionSource(new ResourceLocation(p_189404_), p_189405_, p_189406_);
    }

    public static ConditionSource steep() {
        return net.minecraft.world.level.levelgen.SurfaceRules.Steep.INSTANCE;
    }

    public static ConditionSource hole() {
        return net.minecraft.world.level.levelgen.SurfaceRules.Hole.INSTANCE;
    }

    public static ConditionSource abovePreliminarySurface() {
        return net.minecraft.world.level.levelgen.SurfaceRules.AbovePreliminarySurface.INSTANCE;
    }

    public static ConditionSource temperature() {
        return net.minecraft.world.level.levelgen.SurfaceRules.Temperature.INSTANCE;
    }

    public static RuleSource ifTrue(ConditionSource p_189395_, RuleSource p_189396_) {
        return new TestRuleSource(p_189395_, p_189396_);
    }

    public static RuleSource sequence(RuleSource... p_198273_) {
        if (p_198273_.length == 0) {
            throw new IllegalArgumentException("Need at least 1 rule for a sequence");
        } else {
            return new SequenceRuleSource(Arrays.asList(p_198273_));
        }
    }

    public static RuleSource state(BlockState p_189391_) {
        return new BlockRuleSource(p_189391_);
    }

    public static RuleSource bandlands() {
        return net.minecraft.world.level.levelgen.SurfaceRules.Bandlands.INSTANCE;
    }

    static <A> Codec<? extends A> register(Registry<Codec<? extends A>> p_224604_, String p_224605_, KeyDispatchDataCodec<? extends A> p_224606_) {
        return (Codec)Registry.register(p_224604_, (String)p_224605_, p_224606_.codec());
    }

    static {
        ON_FLOOR = stoneDepthCheck(0, false, CaveSurface.FLOOR);
        UNDER_FLOOR = stoneDepthCheck(0, true, CaveSurface.FLOOR);
        DEEP_UNDER_FLOOR = stoneDepthCheck(0, true, 6, CaveSurface.FLOOR);
        VERY_DEEP_UNDER_FLOOR = stoneDepthCheck(0, true, 30, CaveSurface.FLOOR);
        ON_CEILING = stoneDepthCheck(0, false, CaveSurface.CEILING);
        UNDER_CEILING = stoneDepthCheck(0, true, CaveSurface.CEILING);
    }

    private static record StoneDepthCheck(int offset, boolean addSurfaceDepth, int secondaryDepthRange, CaveSurface surfaceType) implements ConditionSource {
        static final KeyDispatchDataCodec<StoneDepthCheck> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((p_189753_) -> {
            return p_189753_.group(Codec.INT.fieldOf("offset").forGetter(StoneDepthCheck::offset), Codec.BOOL.fieldOf("add_surface_depth").forGetter(StoneDepthCheck::addSurfaceDepth), Codec.INT.fieldOf("secondary_depth_range").forGetter(StoneDepthCheck::secondaryDepthRange), CaveSurface.CODEC.fieldOf("surface_type").forGetter(StoneDepthCheck::surfaceType)).apply(p_189753_, StoneDepthCheck::new);
        }));

        StoneDepthCheck(int offset, boolean addSurfaceDepth, int secondaryDepthRange, CaveSurface surfaceType) {
            this.offset = offset;
            this.addSurfaceDepth = addSurfaceDepth;
            this.secondaryDepthRange = secondaryDepthRange;
            this.surfaceType = surfaceType;
        }

        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        public Condition apply(final Context p_189755_) {
            final boolean $$1 = this.surfaceType == CaveSurface.CEILING;

            class StoneDepthCondition extends LazyYCondition {
                StoneDepthCondition() {
                    super(p_189755_);
                }

                protected boolean compute() {
                    int $$0 = $$1 ? this.context.stoneDepthBelow : this.context.stoneDepthAbove;
                    int $$1x = StoneDepthCheck.this.addSurfaceDepth ? this.context.surfaceDepth : 0;
                    int $$2 = StoneDepthCheck.this.secondaryDepthRange == 0 ? 0 : (int)Mth.map(this.context.getSurfaceSecondary(), -1.0, 1.0, 0.0, (double)StoneDepthCheck.this.secondaryDepthRange);
                    return $$0 <= 1 + StoneDepthCheck.this.offset + $$1x + $$2;
                }
            }

            return new StoneDepthCondition();
        }

        public int offset() {
            return this.offset;
        }

        public boolean addSurfaceDepth() {
            return this.addSurfaceDepth;
        }

        public int secondaryDepthRange() {
            return this.secondaryDepthRange;
        }

        public CaveSurface surfaceType() {
            return this.surfaceType;
        }
    }

    static record NotConditionSource(ConditionSource target) implements ConditionSource {
        static final KeyDispatchDataCodec<NotConditionSource> CODEC;

        NotConditionSource(ConditionSource target) {
            this.target = target;
        }

        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        public Condition apply(Context p_189674_) {
            return new NotCondition((Condition)this.target.apply(p_189674_));
        }

        public ConditionSource target() {
            return this.target;
        }

        static {
            CODEC = KeyDispatchDataCodec.of(net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource.CODEC.xmap(NotConditionSource::new, NotConditionSource::target).fieldOf("invert"));
        }
    }

    public interface ConditionSource extends Function<Context, Condition> {
        Codec<ConditionSource> CODEC = BuiltInRegistries.MATERIAL_CONDITION.byNameCodec().dispatch((p_224613_) -> {
            return p_224613_.codec().codec();
        }, Function.identity());

        static Codec<? extends ConditionSource> bootstrap(Registry<Codec<? extends ConditionSource>> p_204625_) {
            SurfaceRules.register(p_204625_, "biome", net.minecraft.world.level.levelgen.SurfaceRules.BiomeConditionSource.CODEC);
            SurfaceRules.register(p_204625_, "noise_threshold", net.minecraft.world.level.levelgen.SurfaceRules.NoiseThresholdConditionSource.CODEC);
            SurfaceRules.register(p_204625_, "vertical_gradient", net.minecraft.world.level.levelgen.SurfaceRules.VerticalGradientConditionSource.CODEC);
            SurfaceRules.register(p_204625_, "y_above", net.minecraft.world.level.levelgen.SurfaceRules.YConditionSource.CODEC);
            SurfaceRules.register(p_204625_, "water", net.minecraft.world.level.levelgen.SurfaceRules.WaterConditionSource.CODEC);
            SurfaceRules.register(p_204625_, "temperature", net.minecraft.world.level.levelgen.SurfaceRules.Temperature.CODEC);
            SurfaceRules.register(p_204625_, "steep", net.minecraft.world.level.levelgen.SurfaceRules.Steep.CODEC);
            SurfaceRules.register(p_204625_, "not", net.minecraft.world.level.levelgen.SurfaceRules.NotConditionSource.CODEC);
            SurfaceRules.register(p_204625_, "hole", net.minecraft.world.level.levelgen.SurfaceRules.Hole.CODEC);
            SurfaceRules.register(p_204625_, "above_preliminary_surface", net.minecraft.world.level.levelgen.SurfaceRules.AbovePreliminarySurface.CODEC);
            return SurfaceRules.register(p_204625_, "stone_depth", net.minecraft.world.level.levelgen.SurfaceRules.StoneDepthCheck.CODEC);
        }

        KeyDispatchDataCodec<? extends ConditionSource> codec();
    }

    private static record YConditionSource(VerticalAnchor anchor, int surfaceDepthMultiplier, boolean addStoneDepth) implements ConditionSource {
        static final KeyDispatchDataCodec<YConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((p_189455_) -> {
            return p_189455_.group(VerticalAnchor.CODEC.fieldOf("anchor").forGetter(YConditionSource::anchor), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(YConditionSource::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(YConditionSource::addStoneDepth)).apply(p_189455_, YConditionSource::new);
        }));

        YConditionSource(VerticalAnchor anchor, int surfaceDepthMultiplier, boolean addStoneDepth) {
            this.anchor = anchor;
            this.surfaceDepthMultiplier = surfaceDepthMultiplier;
            this.addStoneDepth = addStoneDepth;
        }

        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        public Condition apply(final Context p_189457_) {
            class YCondition extends LazyYCondition {
                YCondition() {
                    super(p_189457_);
                }

                protected boolean compute() {
                    return this.context.blockY + (YConditionSource.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= YConditionSource.this.anchor.resolveY(this.context.context) + this.context.surfaceDepth * YConditionSource.this.surfaceDepthMultiplier;
                }
            }

            return new YCondition();
        }

        public VerticalAnchor anchor() {
            return this.anchor;
        }

        public int surfaceDepthMultiplier() {
            return this.surfaceDepthMultiplier;
        }

        public boolean addStoneDepth() {
            return this.addStoneDepth;
        }
    }

    private static record WaterConditionSource(int offset, int surfaceDepthMultiplier, boolean addStoneDepth) implements ConditionSource {
        static final KeyDispatchDataCodec<WaterConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((p_189874_) -> {
            return p_189874_.group(Codec.INT.fieldOf("offset").forGetter(WaterConditionSource::offset), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(WaterConditionSource::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(WaterConditionSource::addStoneDepth)).apply(p_189874_, WaterConditionSource::new);
        }));

        WaterConditionSource(int offset, int surfaceDepthMultiplier, boolean addStoneDepth) {
            this.offset = offset;
            this.surfaceDepthMultiplier = surfaceDepthMultiplier;
            this.addStoneDepth = addStoneDepth;
        }

        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        public Condition apply(final Context p_189876_) {
            class WaterCondition extends LazyYCondition {
                WaterCondition() {
                    super(p_189876_);
                }

                protected boolean compute() {
                    return this.context.waterHeight == Integer.MIN_VALUE || this.context.blockY + (WaterConditionSource.this.addStoneDepth ? this.context.stoneDepthAbove : 0) >= this.context.waterHeight + WaterConditionSource.this.offset + this.context.surfaceDepth * WaterConditionSource.this.surfaceDepthMultiplier;
                }
            }

            return new WaterCondition();
        }

        public int offset() {
            return this.offset;
        }

        public int surfaceDepthMultiplier() {
            return this.surfaceDepthMultiplier;
        }

        public boolean addStoneDepth() {
            return this.addStoneDepth;
        }
    }

    static final class BiomeConditionSource implements ConditionSource {
        static final KeyDispatchDataCodec<BiomeConditionSource> CODEC;
        private final List<ResourceKey<Biome>> biomes;
        final Predicate<ResourceKey<Biome>> biomeNameTest;

        BiomeConditionSource(List<ResourceKey<Biome>> p_189493_) {
            this.biomes = p_189493_;
            Set var10001 = Set.copyOf(p_189493_);
            Objects.requireNonNull(var10001);
            this.biomeNameTest = var10001::contains;
        }

        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        public Condition apply(final Context p_189496_) {
            class BiomeCondition extends LazyYCondition {
                BiomeCondition() {
                    super(p_189496_);
                }

                protected boolean compute() {
                    return ((Holder)this.context.biome.get()).is(BiomeConditionSource.this.biomeNameTest);
                }
            }

            return new BiomeCondition();
        }

        public boolean equals(Object p_209694_) {
            if (this == p_209694_) {
                return true;
            } else if (p_209694_ instanceof BiomeConditionSource) {
                BiomeConditionSource $$1 = (BiomeConditionSource)p_209694_;
                return this.biomes.equals($$1.biomes);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.biomes.hashCode();
        }

        public String toString() {
            return "BiomeConditionSource[biomes=" + this.biomes + "]";
        }

        static {
            CODEC = KeyDispatchDataCodec.of(ResourceKey.codec(Registries.BIOME).listOf().fieldOf("biome_is").xmap(SurfaceRules::isBiome, (p_204620_) -> {
                return p_204620_.biomes;
            }));
        }
    }

    private static record NoiseThresholdConditionSource(ResourceKey<NormalNoise.NoiseParameters> noise, double minThreshold, double maxThreshold) implements ConditionSource {
        static final KeyDispatchDataCodec<NoiseThresholdConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((p_258995_) -> {
            return p_258995_.group(ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(NoiseThresholdConditionSource::noise), Codec.DOUBLE.fieldOf("min_threshold").forGetter(NoiseThresholdConditionSource::minThreshold), Codec.DOUBLE.fieldOf("max_threshold").forGetter(NoiseThresholdConditionSource::maxThreshold)).apply(p_258995_, NoiseThresholdConditionSource::new);
        }));

        NoiseThresholdConditionSource(ResourceKey<NormalNoise.NoiseParameters> noise, double minThreshold, double maxThreshold) {
            this.noise = noise;
            this.minThreshold = minThreshold;
            this.maxThreshold = maxThreshold;
        }

        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        public Condition apply(final Context p_189640_) {
            final NormalNoise $$1 = p_189640_.randomState.getOrCreateNoise(this.noise);

            class NoiseThresholdCondition extends LazyXZCondition {
                NoiseThresholdCondition() {
                    super(p_189640_);
                }

                protected boolean compute() {
                    double $$0 = $$1.getValue((double)this.context.blockX, 0.0, (double)this.context.blockZ);
                    return $$0 >= NoiseThresholdConditionSource.this.minThreshold && $$0 <= NoiseThresholdConditionSource.this.maxThreshold;
                }
            }

            return new NoiseThresholdCondition();
        }

        public ResourceKey<NormalNoise.NoiseParameters> noise() {
            return this.noise;
        }

        public double minThreshold() {
            return this.minThreshold;
        }

        public double maxThreshold() {
            return this.maxThreshold;
        }
    }

    static record VerticalGradientConditionSource(ResourceLocation randomName, VerticalAnchor trueAtAndBelow, VerticalAnchor falseAtAndAbove) implements ConditionSource {
        static final KeyDispatchDataCodec<VerticalGradientConditionSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((p_189839_) -> {
            return p_189839_.group(ResourceLocation.CODEC.fieldOf("random_name").forGetter(VerticalGradientConditionSource::randomName), VerticalAnchor.CODEC.fieldOf("true_at_and_below").forGetter(VerticalGradientConditionSource::trueAtAndBelow), VerticalAnchor.CODEC.fieldOf("false_at_and_above").forGetter(VerticalGradientConditionSource::falseAtAndAbove)).apply(p_189839_, VerticalGradientConditionSource::new);
        }));

        VerticalGradientConditionSource(ResourceLocation randomName, VerticalAnchor trueAtAndBelow, VerticalAnchor falseAtAndAbove) {
            this.randomName = randomName;
            this.trueAtAndBelow = trueAtAndBelow;
            this.falseAtAndAbove = falseAtAndAbove;
        }

        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        public Condition apply(final Context p_189841_) {
            final int $$1 = this.trueAtAndBelow().resolveY(p_189841_.context);
            final int $$2 = this.falseAtAndAbove().resolveY(p_189841_.context);
            final PositionalRandomFactory $$3 = p_189841_.randomState.getOrCreateRandomFactory(this.randomName());

            class VerticalGradientCondition extends LazyYCondition {
                VerticalGradientCondition() {
                    super(p_189841_);
                }

                protected boolean compute() {
                    int $$0 = this.context.blockY;
                    if ($$0 <= $$1) {
                        return true;
                    } else if ($$0 >= $$2) {
                        return false;
                    } else {
                        double $$1x = Mth.map((double)$$0, (double)$$1, (double)$$2, 1.0, 0.0);
                        RandomSource $$2x = $$3.at(this.context.blockX, $$0, this.context.blockZ);
                        return (double)$$2x.nextFloat() < $$1x;
                    }
                }
            }

            return new VerticalGradientCondition();
        }

        public ResourceLocation randomName() {
            return this.randomName;
        }

        public VerticalAnchor trueAtAndBelow() {
            return this.trueAtAndBelow;
        }

        public VerticalAnchor falseAtAndAbove() {
            return this.falseAtAndAbove;
        }
    }

    static enum Steep implements ConditionSource {
        INSTANCE;

        static final KeyDispatchDataCodec<Steep> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        private Steep() {
        }

        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        public Condition apply(Context p_189733_) {
            return p_189733_.steep;
        }
    }

    static enum Hole implements ConditionSource {
        INSTANCE;

        static final KeyDispatchDataCodec<Hole> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        private Hole() {
        }

        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        public Condition apply(Context p_189608_) {
            return p_189608_.hole;
        }
    }

    static enum AbovePreliminarySurface implements ConditionSource {
        INSTANCE;

        static final KeyDispatchDataCodec<AbovePreliminarySurface> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        private AbovePreliminarySurface() {
        }

        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        public Condition apply(Context p_189437_) {
            return p_189437_.abovePreliminarySurface;
        }
    }

    static enum Temperature implements ConditionSource {
        INSTANCE;

        static final KeyDispatchDataCodec<Temperature> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        private Temperature() {
        }

        public KeyDispatchDataCodec<? extends ConditionSource> codec() {
            return CODEC;
        }

        public Condition apply(Context p_189786_) {
            return p_189786_.temperature;
        }
    }

    private static record TestRuleSource(ConditionSource ifTrue, RuleSource thenRun) implements RuleSource {
        static final KeyDispatchDataCodec<TestRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((p_189817_) -> {
            return p_189817_.group(net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource.CODEC.fieldOf("if_true").forGetter(TestRuleSource::ifTrue), net.minecraft.world.level.levelgen.SurfaceRules.RuleSource.CODEC.fieldOf("then_run").forGetter(TestRuleSource::thenRun)).apply(p_189817_, TestRuleSource::new);
        }));

        TestRuleSource(ConditionSource ifTrue, RuleSource thenRun) {
            this.ifTrue = ifTrue;
            this.thenRun = thenRun;
        }

        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRule apply(Context p_189819_) {
            return new TestRule((Condition)this.ifTrue.apply(p_189819_), (SurfaceRule)this.thenRun.apply(p_189819_));
        }

        public ConditionSource ifTrue() {
            return this.ifTrue;
        }

        public RuleSource thenRun() {
            return this.thenRun;
        }
    }

    public interface RuleSource extends Function<Context, SurfaceRule> {
        Codec<RuleSource> CODEC = BuiltInRegistries.MATERIAL_RULE.byNameCodec().dispatch((p_224627_) -> {
            return p_224627_.codec().codec();
        }, Function.identity());

        static Codec<? extends RuleSource> bootstrap(Registry<Codec<? extends RuleSource>> p_204631_) {
            SurfaceRules.register(p_204631_, "bandlands", net.minecraft.world.level.levelgen.SurfaceRules.Bandlands.CODEC);
            SurfaceRules.register(p_204631_, "block", net.minecraft.world.level.levelgen.SurfaceRules.BlockRuleSource.CODEC);
            SurfaceRules.register(p_204631_, "sequence", net.minecraft.world.level.levelgen.SurfaceRules.SequenceRuleSource.CODEC);
            return SurfaceRules.register(p_204631_, "condition", net.minecraft.world.level.levelgen.SurfaceRules.TestRuleSource.CODEC);
        }

        KeyDispatchDataCodec<? extends RuleSource> codec();
    }

    private static record SequenceRuleSource(List<RuleSource> sequence) implements RuleSource {
        static final KeyDispatchDataCodec<SequenceRuleSource> CODEC;

        SequenceRuleSource(List<RuleSource> sequence) {
            this.sequence = sequence;
        }

        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRule apply(Context p_189704_) {
            if (this.sequence.size() == 1) {
                return (SurfaceRule)((RuleSource)this.sequence.get(0)).apply(p_189704_);
            } else {
                ImmutableList.Builder<SurfaceRule> $$1 = ImmutableList.builder();
                Iterator var3 = this.sequence.iterator();

                while(var3.hasNext()) {
                    RuleSource $$2 = (RuleSource)var3.next();
                    $$1.add((SurfaceRule)$$2.apply(p_189704_));
                }

                return new SequenceRule($$1.build());
            }
        }

        public List<RuleSource> sequence() {
            return this.sequence;
        }

        static {
            CODEC = KeyDispatchDataCodec.of(net.minecraft.world.level.levelgen.SurfaceRules.RuleSource.CODEC.listOf().xmap(SequenceRuleSource::new, SequenceRuleSource::sequence).fieldOf("sequence"));
        }
    }

    static record BlockRuleSource(BlockState resultState, StateRule rule) implements RuleSource {
        static final KeyDispatchDataCodec<BlockRuleSource> CODEC;

        BlockRuleSource(BlockState p_189517_) {
            this(p_189517_, new StateRule(p_189517_));
        }

        private BlockRuleSource(BlockState resultState, StateRule rule) {
            this.resultState = resultState;
            this.rule = rule;
        }

        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRule apply(Context p_189523_) {
            return this.rule;
        }

        public BlockState resultState() {
            return this.resultState;
        }

        public StateRule rule() {
            return this.rule;
        }

        static {
            CODEC = KeyDispatchDataCodec.of(BlockState.CODEC.xmap(BlockRuleSource::new, BlockRuleSource::resultState).fieldOf("result_state"));
        }
    }

    private static enum Bandlands implements RuleSource {
        INSTANCE;

        static final KeyDispatchDataCodec<Bandlands> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        private Bandlands() {
        }

        public KeyDispatchDataCodec<? extends RuleSource> codec() {
            return CODEC;
        }

        public SurfaceRule apply(Context p_189482_) {
            SurfaceSystem var10000 = p_189482_.system;
            Objects.requireNonNull(var10000);
            return var10000::getBand;
        }
    }

    private static record SequenceRule(List<SurfaceRule> rules) implements SurfaceRule {
        SequenceRule(List<SurfaceRule> rules) {
            this.rules = rules;
        }

        @Nullable
        public BlockState tryApply(int p_189694_, int p_189695_, int p_189696_) {
            Iterator var4 = this.rules.iterator();

            BlockState $$4;
            do {
                if (!var4.hasNext()) {
                    return null;
                }

                SurfaceRule $$3 = (SurfaceRule)var4.next();
                $$4 = $$3.tryApply(p_189694_, p_189695_, p_189696_);
            } while($$4 == null);

            return $$4;
        }

        public List<SurfaceRule> rules() {
            return this.rules;
        }
    }

    private static record TestRule(Condition condition, SurfaceRule followup) implements SurfaceRule {
        TestRule(Condition condition, SurfaceRule followup) {
            this.condition = condition;
            this.followup = followup;
        }

        @Nullable
        public BlockState tryApply(int p_189805_, int p_189806_, int p_189807_) {
            return !this.condition.test() ? null : this.followup.tryApply(p_189805_, p_189806_, p_189807_);
        }

        public Condition condition() {
            return this.condition;
        }

        public SurfaceRule followup() {
            return this.followup;
        }
    }

    static record StateRule(BlockState state) implements SurfaceRule {
        StateRule(BlockState state) {
            this.state = state;
        }

        public BlockState tryApply(int p_189721_, int p_189722_, int p_189723_) {
            return this.state;
        }

        public BlockState state() {
            return this.state;
        }
    }

    protected interface SurfaceRule {
        @Nullable
        BlockState tryApply(int var1, int var2, int var3);
    }

    private static record NotCondition(Condition target) implements Condition {
        NotCondition(Condition target) {
            this.target = target;
        }

        public boolean test() {
            return !this.target.test();
        }

        public Condition target() {
            return this.target;
        }
    }

    private abstract static class LazyYCondition extends LazyCondition {
        protected LazyYCondition(Context p_189625_) {
            super(p_189625_);
        }

        protected long getContextLastUpdate() {
            return this.context.lastUpdateY;
        }
    }

    private abstract static class LazyXZCondition extends LazyCondition {
        protected LazyXZCondition(Context p_189622_) {
            super(p_189622_);
        }

        protected long getContextLastUpdate() {
            return this.context.lastUpdateXZ;
        }
    }

    private abstract static class LazyCondition implements Condition {
        protected final Context context;
        private long lastUpdate;
        @Nullable
        Boolean result;

        protected LazyCondition(Context p_189619_) {
            this.context = p_189619_;
            this.lastUpdate = this.getContextLastUpdate() - 1L;
        }

        public boolean test() {
            long $$0 = this.getContextLastUpdate();
            if ($$0 == this.lastUpdate) {
                if (this.result == null) {
                    throw new IllegalStateException("Update triggered but the result is null");
                } else {
                    return this.result;
                }
            } else {
                this.lastUpdate = $$0;
                this.result = this.compute();
                return this.result;
            }
        }

        protected abstract long getContextLastUpdate();

        protected abstract boolean compute();
    }

    private interface Condition {
        boolean test();
    }

    protected static final class Context {
        private static final int HOW_FAR_BELOW_PRELIMINARY_SURFACE_LEVEL_TO_BUILD_SURFACE = 8;
        private static final int SURFACE_CELL_BITS = 4;
        private static final int SURFACE_CELL_SIZE = 16;
        private static final int SURFACE_CELL_MASK = 15;
        final SurfaceSystem system;
        final Condition temperature = new TemperatureHelperCondition(this);
        final Condition steep = new SteepMaterialCondition(this);
        final Condition hole = new HoleCondition(this);
        final Condition abovePreliminarySurface = new AbovePreliminarySurfaceCondition();
        final RandomState randomState;
        final ChunkAccess chunk;
        private final NoiseChunk noiseChunk;
        private final Function<BlockPos, Holder<Biome>> biomeGetter;
        final WorldGenerationContext context;
        private long lastPreliminarySurfaceCellOrigin = Long.MAX_VALUE;
        private final int[] preliminarySurfaceCache = new int[4];
        long lastUpdateXZ = -9223372036854775807L;
        int blockX;
        int blockZ;
        int surfaceDepth;
        private long lastSurfaceDepth2Update;
        private double surfaceSecondary;
        private long lastMinSurfaceLevelUpdate;
        private int minSurfaceLevel;
        long lastUpdateY;
        final BlockPos.MutableBlockPos pos;
        Supplier<Holder<Biome>> biome;
        int blockY;
        int waterHeight;
        int stoneDepthBelow;
        int stoneDepthAbove;

        protected Context(SurfaceSystem p_224616_, RandomState p_224617_, ChunkAccess p_224618_, NoiseChunk p_224619_, Function<BlockPos, Holder<Biome>> p_224620_, Registry<Biome> p_224621_, WorldGenerationContext p_224622_) {
            this.lastSurfaceDepth2Update = this.lastUpdateXZ - 1L;
            this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ - 1L;
            this.lastUpdateY = -9223372036854775807L;
            this.pos = new BlockPos.MutableBlockPos();
            this.system = p_224616_;
            this.randomState = p_224617_;
            this.chunk = p_224618_;
            this.noiseChunk = p_224619_;
            this.biomeGetter = p_224620_;
            this.context = p_224622_;
        }

        protected void updateXZ(int p_189570_, int p_189571_) {
            ++this.lastUpdateXZ;
            ++this.lastUpdateY;
            this.blockX = p_189570_;
            this.blockZ = p_189571_;
            this.surfaceDepth = this.system.getSurfaceDepth(p_189570_, p_189571_);
        }

        protected void updateY(int p_189577_, int p_189578_, int p_189579_, int p_189580_, int p_189581_, int p_189582_) {
            ++this.lastUpdateY;
            this.biome = Suppliers.memoize(() -> {
                return (Holder)this.biomeGetter.apply(this.pos.set(p_189580_, p_189581_, p_189582_));
            });
            this.blockY = p_189581_;
            this.waterHeight = p_189579_;
            this.stoneDepthBelow = p_189578_;
            this.stoneDepthAbove = p_189577_;
        }

        protected double getSurfaceSecondary() {
            if (this.lastSurfaceDepth2Update != this.lastUpdateXZ) {
                this.lastSurfaceDepth2Update = this.lastUpdateXZ;
                this.surfaceSecondary = this.system.getSurfaceSecondary(this.blockX, this.blockZ);
            }

            return this.surfaceSecondary;
        }

        private static int blockCoordToSurfaceCell(int p_198281_) {
            return p_198281_ >> 4;
        }

        private static int surfaceCellToBlockCoord(int p_198283_) {
            return p_198283_ << 4;
        }

        protected int getMinSurfaceLevel() {
            if (this.lastMinSurfaceLevelUpdate != this.lastUpdateXZ) {
                this.lastMinSurfaceLevelUpdate = this.lastUpdateXZ;
                int $$0 = blockCoordToSurfaceCell(this.blockX);
                int $$1 = blockCoordToSurfaceCell(this.blockZ);
                long $$2 = ChunkPos.asLong($$0, $$1);
                if (this.lastPreliminarySurfaceCellOrigin != $$2) {
                    this.lastPreliminarySurfaceCellOrigin = $$2;
                    this.preliminarySurfaceCache[0] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord($$0), surfaceCellToBlockCoord($$1));
                    this.preliminarySurfaceCache[1] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord($$0 + 1), surfaceCellToBlockCoord($$1));
                    this.preliminarySurfaceCache[2] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord($$0), surfaceCellToBlockCoord($$1 + 1));
                    this.preliminarySurfaceCache[3] = this.noiseChunk.preliminarySurfaceLevel(surfaceCellToBlockCoord($$0 + 1), surfaceCellToBlockCoord($$1 + 1));
                }

                int $$3 = Mth.floor(Mth.lerp2((double)((float)(this.blockX & 15) / 16.0F), (double)((float)(this.blockZ & 15) / 16.0F), (double)this.preliminarySurfaceCache[0], (double)this.preliminarySurfaceCache[1], (double)this.preliminarySurfaceCache[2], (double)this.preliminarySurfaceCache[3]));
                this.minSurfaceLevel = $$3 + this.surfaceDepth - 8;
            }

            return this.minSurfaceLevel;
        }

        private static class TemperatureHelperCondition extends LazyYCondition {
            TemperatureHelperCondition(Context p_189597_) {
                super(p_189597_);
            }

            protected boolean compute() {
                return ((Biome)((Holder)this.context.biome.get()).value()).coldEnoughToSnow(this.context.pos.set(this.context.blockX, this.context.blockY, this.context.blockZ));
            }
        }

        static class SteepMaterialCondition extends LazyXZCondition {
            SteepMaterialCondition(Context p_189594_) {
                super(p_189594_);
            }

            protected boolean compute() {
                int $$0 = this.context.blockX & 15;
                int $$1 = this.context.blockZ & 15;
                int $$2 = Math.max($$1 - 1, 0);
                int $$3 = Math.min($$1 + 1, 15);
                ChunkAccess $$4 = this.context.chunk;
                int $$5 = $$4.getHeight(Types.WORLD_SURFACE_WG, $$0, $$2);
                int $$6 = $$4.getHeight(Types.WORLD_SURFACE_WG, $$0, $$3);
                if ($$6 >= $$5 + 4) {
                    return true;
                } else {
                    int $$7 = Math.max($$0 - 1, 0);
                    int $$8 = Math.min($$0 + 1, 15);
                    int $$9 = $$4.getHeight(Types.WORLD_SURFACE_WG, $$7, $$1);
                    int $$10 = $$4.getHeight(Types.WORLD_SURFACE_WG, $$8, $$1);
                    return $$9 >= $$10 + 4;
                }
            }
        }

        private static final class HoleCondition extends LazyXZCondition {
            HoleCondition(Context p_189591_) {
                super(p_189591_);
            }

            protected boolean compute() {
                return this.context.surfaceDepth <= 0;
            }
        }

        final class AbovePreliminarySurfaceCondition implements Condition {
            AbovePreliminarySurfaceCondition() {
            }

            public boolean test() {
                return Context.this.blockY >= Context.this.getMinSurfaceLevel();
            }
        }
    }
}
