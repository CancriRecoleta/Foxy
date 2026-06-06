//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction.NoiseHolder;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.slf4j.Logger;

public final class DensityFunctions {
    private static final Codec<DensityFunction> CODEC;
    protected static final double MAX_REASONABLE_NOISE_VALUE = 1000000.0;
    static final Codec<Double> NOISE_VALUE_CODEC;
    public static final Codec<DensityFunction> DIRECT_CODEC;

    public static Codec<? extends DensityFunction> bootstrap(Registry<Codec<? extends DensityFunction>> p_208343_) {
        register(p_208343_, "blend_alpha", net.minecraft.world.level.levelgen.DensityFunctions.BlendAlpha.CODEC);
        register(p_208343_, "blend_offset", net.minecraft.world.level.levelgen.DensityFunctions.BlendOffset.CODEC);
        register(p_208343_, "beardifier", net.minecraft.world.level.levelgen.DensityFunctions.BeardifierMarker.CODEC);
        register(p_208343_, "old_blended_noise", BlendedNoise.CODEC);
        Marker.Type[] var1 = net.minecraft.world.level.levelgen.DensityFunctions.Marker.Type.values();
        int var2 = var1.length;

        int var3;
        for(var3 = 0; var3 < var2; ++var3) {
            Marker.Type $$1 = var1[var3];
            register(p_208343_, $$1.getSerializedName(), $$1.codec);
        }

        register(p_208343_, "noise", net.minecraft.world.level.levelgen.DensityFunctions.Noise.CODEC);
        register(p_208343_, "end_islands", net.minecraft.world.level.levelgen.DensityFunctions.EndIslandDensityFunction.CODEC);
        register(p_208343_, "weird_scaled_sampler", net.minecraft.world.level.levelgen.DensityFunctions.WeirdScaledSampler.CODEC);
        register(p_208343_, "shifted_noise", net.minecraft.world.level.levelgen.DensityFunctions.ShiftedNoise.CODEC);
        register(p_208343_, "range_choice", net.minecraft.world.level.levelgen.DensityFunctions.RangeChoice.CODEC);
        register(p_208343_, "shift_a", net.minecraft.world.level.levelgen.DensityFunctions.ShiftA.CODEC);
        register(p_208343_, "shift_b", net.minecraft.world.level.levelgen.DensityFunctions.ShiftB.CODEC);
        register(p_208343_, "shift", net.minecraft.world.level.levelgen.DensityFunctions.Shift.CODEC);
        register(p_208343_, "blend_density", net.minecraft.world.level.levelgen.DensityFunctions.BlendDensity.CODEC);
        register(p_208343_, "clamp", net.minecraft.world.level.levelgen.DensityFunctions.Clamp.CODEC);
        Mapped.Type[] var5 = net.minecraft.world.level.levelgen.DensityFunctions.Mapped.Type.values();
        var2 = var5.length;

        for(var3 = 0; var3 < var2; ++var3) {
            Mapped.Type $$2 = var5[var3];
            register(p_208343_, $$2.getSerializedName(), $$2.codec);
        }

        TwoArgumentSimpleFunction.Type[] var6 = net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.values();
        var2 = var6.length;

        for(var3 = 0; var3 < var2; ++var3) {
            TwoArgumentSimpleFunction.Type $$3 = var6[var3];
            register(p_208343_, $$3.getSerializedName(), $$3.codec);
        }

        register(p_208343_, "spline", net.minecraft.world.level.levelgen.DensityFunctions.Spline.CODEC);
        register(p_208343_, "constant", net.minecraft.world.level.levelgen.DensityFunctions.Constant.CODEC);
        return register(p_208343_, "y_clamped_gradient", net.minecraft.world.level.levelgen.DensityFunctions.YClampedGradient.CODEC);
    }

    private static Codec<? extends DensityFunction> register(Registry<Codec<? extends DensityFunction>> p_224035_, String p_224036_, KeyDispatchDataCodec<? extends DensityFunction> p_224037_) {
        return (Codec)Registry.register(p_224035_, (String)p_224036_, p_224037_.codec());
    }

    static <A, O> KeyDispatchDataCodec<O> singleArgumentCodec(Codec<A> p_224025_, Function<A, O> p_224026_, Function<O, A> p_224027_) {
        return KeyDispatchDataCodec.of(p_224025_.fieldOf("argument").xmap(p_224026_, p_224027_));
    }

    static <O> KeyDispatchDataCodec<O> singleFunctionArgumentCodec(Function<DensityFunction, O> p_224043_, Function<O, DensityFunction> p_224044_) {
        return singleArgumentCodec(DensityFunction.HOLDER_HELPER_CODEC, p_224043_, p_224044_);
    }

    static <O> KeyDispatchDataCodec<O> doubleFunctionArgumentCodec(BiFunction<DensityFunction, DensityFunction, O> p_224039_, Function<O, DensityFunction> p_224040_, Function<O, DensityFunction> p_224041_) {
        return KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec((p_224049_) -> {
            return p_224049_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument1").forGetter(p_224040_), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument2").forGetter(p_224041_)).apply(p_224049_, p_224039_);
        }));
    }

    static <O> KeyDispatchDataCodec<O> makeCodec(MapCodec<O> p_224029_) {
        return KeyDispatchDataCodec.of(p_224029_);
    }

    private DensityFunctions() {
    }

    public static DensityFunction interpolated(DensityFunction p_208282_) {
        return new Marker(net.minecraft.world.level.levelgen.DensityFunctions.Marker.Type.Interpolated, p_208282_);
    }

    public static DensityFunction flatCache(DensityFunction p_208362_) {
        return new Marker(net.minecraft.world.level.levelgen.DensityFunctions.Marker.Type.FlatCache, p_208362_);
    }

    public static DensityFunction cache2d(DensityFunction p_208374_) {
        return new Marker(net.minecraft.world.level.levelgen.DensityFunctions.Marker.Type.Cache2D, p_208374_);
    }

    public static DensityFunction cacheOnce(DensityFunction p_208381_) {
        return new Marker(net.minecraft.world.level.levelgen.DensityFunctions.Marker.Type.CacheOnce, p_208381_);
    }

    public static DensityFunction cacheAllInCell(DensityFunction p_208388_) {
        return new Marker(net.minecraft.world.level.levelgen.DensityFunctions.Marker.Type.CacheAllInCell, p_208388_);
    }

    public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> p_208337_, @Deprecated double p_208338_, double p_208339_, double p_208340_, double p_208341_) {
        return mapFromUnitTo(new Noise(new DensityFunction.NoiseHolder(p_208337_), p_208338_, p_208339_), p_208340_, p_208341_);
    }

    public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> p_208332_, double p_208333_, double p_208334_, double p_208335_) {
        return mappedNoise(p_208332_, 1.0, p_208333_, p_208334_, p_208335_);
    }

    public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> p_208328_, double p_208329_, double p_208330_) {
        return mappedNoise(p_208328_, 1.0, 1.0, p_208329_, p_208330_);
    }

    public static DensityFunction shiftedNoise2d(DensityFunction p_208297_, DensityFunction p_208298_, double p_208299_, Holder<NormalNoise.NoiseParameters> p_208300_) {
        return new ShiftedNoise(p_208297_, zero(), p_208298_, p_208299_, 0.0, new DensityFunction.NoiseHolder(p_208300_));
    }

    public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> p_208323_) {
        return noise(p_208323_, 1.0, 1.0);
    }

    public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> p_208369_, double p_208370_, double p_208371_) {
        return new Noise(new DensityFunction.NoiseHolder(p_208369_), p_208370_, p_208371_);
    }

    public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> p_208325_, double p_208326_) {
        return noise(p_208325_, 1.0, p_208326_);
    }

    public static DensityFunction rangeChoice(DensityFunction p_208288_, double p_208289_, double p_208290_, DensityFunction p_208291_, DensityFunction p_208292_) {
        return new RangeChoice(p_208288_, p_208289_, p_208290_, p_208291_, p_208292_);
    }

    public static DensityFunction shiftA(Holder<NormalNoise.NoiseParameters> p_208367_) {
        return new ShiftA(new DensityFunction.NoiseHolder(p_208367_));
    }

    public static DensityFunction shiftB(Holder<NormalNoise.NoiseParameters> p_208379_) {
        return new ShiftB(new DensityFunction.NoiseHolder(p_208379_));
    }

    public static DensityFunction shift(Holder<NormalNoise.NoiseParameters> p_208386_) {
        return new Shift(new DensityFunction.NoiseHolder(p_208386_));
    }

    public static DensityFunction blendDensity(DensityFunction p_208390_) {
        return new BlendDensity(p_208390_);
    }

    public static DensityFunction endIslands(long p_208272_) {
        return new EndIslandDensityFunction(p_208272_);
    }

    public static DensityFunction weirdScaledSampler(DensityFunction p_208316_, Holder<NormalNoise.NoiseParameters> p_208317_, WeirdScaledSampler.RarityValueMapper p_208318_) {
        return new WeirdScaledSampler(p_208316_, new DensityFunction.NoiseHolder(p_208317_), p_208318_);
    }

    public static DensityFunction add(DensityFunction p_208294_, DensityFunction p_208295_) {
        return net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.create(net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.ADD, p_208294_, p_208295_);
    }

    public static DensityFunction mul(DensityFunction p_208364_, DensityFunction p_208365_) {
        return net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.create(net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.MUL, p_208364_, p_208365_);
    }

    public static DensityFunction min(DensityFunction p_208376_, DensityFunction p_208377_) {
        return net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.create(net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.MIN, p_208376_, p_208377_);
    }

    public static DensityFunction max(DensityFunction p_208383_, DensityFunction p_208384_) {
        return net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.create(net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.MAX, p_208383_, p_208384_);
    }

    public static DensityFunction spline(CubicSpline<Spline.Point, Spline.Coordinate> p_224021_) {
        return new Spline(p_224021_);
    }

    public static DensityFunction zero() {
        return net.minecraft.world.level.levelgen.DensityFunctions.Constant.ZERO;
    }

    public static DensityFunction constant(double p_208265_) {
        return new Constant(p_208265_);
    }

    public static DensityFunction yClampedGradient(int p_208267_, int p_208268_, double p_208269_, double p_208270_) {
        return new YClampedGradient(p_208267_, p_208268_, p_208269_, p_208270_);
    }

    public static DensityFunction map(DensityFunction p_208313_, Mapped.Type p_208314_) {
        return net.minecraft.world.level.levelgen.DensityFunctions.Mapped.create(p_208314_, p_208313_);
    }

    private static DensityFunction mapFromUnitTo(DensityFunction p_208284_, double p_208285_, double p_208286_) {
        double $$3 = (p_208285_ + p_208286_) * 0.5;
        double $$4 = (p_208286_ - p_208285_) * 0.5;
        return add(constant($$3), mul(constant($$4), p_208284_));
    }

    public static DensityFunction blendAlpha() {
        return net.minecraft.world.level.levelgen.DensityFunctions.BlendAlpha.INSTANCE;
    }

    public static DensityFunction blendOffset() {
        return net.minecraft.world.level.levelgen.DensityFunctions.BlendOffset.INSTANCE;
    }

    public static DensityFunction lerp(DensityFunction p_208302_, DensityFunction p_208303_, DensityFunction p_208304_) {
        if (p_208303_ instanceof Constant $$3) {
            return lerp(p_208302_, $$3.value, p_208304_);
        } else {
            DensityFunction $$4 = cacheOnce(p_208302_);
            DensityFunction $$5 = add(mul($$4, constant(-1.0)), constant(1.0));
            return add(mul(p_208303_, $$5), mul(p_208304_, $$4));
        }
    }

    public static DensityFunction lerp(DensityFunction p_224031_, double p_224032_, DensityFunction p_224033_) {
        return add(mul(p_224031_, add(p_224033_, constant(-p_224032_))), constant(p_224032_));
    }

    static {
        CODEC = BuiltInRegistries.DENSITY_FUNCTION_TYPE.byNameCodec().dispatch((p_224053_) -> {
            return p_224053_.codec().codec();
        }, Function.identity());
        NOISE_VALUE_CODEC = Codec.doubleRange(-1000000.0, 1000000.0);
        DIRECT_CODEC = Codec.either(NOISE_VALUE_CODEC, CODEC).xmap((p_224023_) -> {
            return (DensityFunction)p_224023_.map(DensityFunctions::constant, Function.identity());
        }, (p_224051_) -> {
            if (p_224051_ instanceof Constant $$1) {
                return Either.left($$1.value());
            } else {
                return Either.right(p_224051_);
            }
        });
    }

    protected static enum BlendAlpha implements DensityFunction.SimpleFunction {
        INSTANCE;

        public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        private BlendAlpha() {
        }

        public double compute(DensityFunction.FunctionContext p_208536_) {
            return 1.0;
        }

        public void fillArray(double[] p_208538_, DensityFunction.ContextProvider p_208539_) {
            Arrays.fill(p_208538_, 1.0);
        }

        public double minValue() {
            return 1.0;
        }

        public double maxValue() {
            return 1.0;
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    protected static enum BlendOffset implements DensityFunction.SimpleFunction {
        INSTANCE;

        public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

        private BlendOffset() {
        }

        public double compute(DensityFunction.FunctionContext p_208573_) {
            return 0.0;
        }

        public void fillArray(double[] p_208575_, DensityFunction.ContextProvider p_208576_) {
            Arrays.fill(p_208575_, 0.0);
        }

        public double minValue() {
            return 0.0;
        }

        public double maxValue() {
            return 0.0;
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    protected static enum BeardifierMarker implements BeardifierOrMarker {
        INSTANCE;

        private BeardifierMarker() {
        }

        public double compute(DensityFunction.FunctionContext p_208515_) {
            return 0.0;
        }

        public void fillArray(double[] p_208517_, DensityFunction.ContextProvider p_208518_) {
            Arrays.fill(p_208517_, 0.0);
        }

        public double minValue() {
            return 0.0;
        }

        public double maxValue() {
            return 0.0;
        }
    }

    protected static record Marker(Type type, DensityFunction wrapped) implements MarkerOrMarked {
        protected Marker(Type type, DensityFunction wrapped) {
            this.type = type;
            this.wrapped = wrapped;
        }

        public double compute(DensityFunction.FunctionContext p_208712_) {
            return this.wrapped.compute(p_208712_);
        }

        public void fillArray(double[] p_208716_, DensityFunction.ContextProvider p_208717_) {
            this.wrapped.fillArray(p_208716_, p_208717_);
        }

        public double minValue() {
            return this.wrapped.minValue();
        }

        public double maxValue() {
            return this.wrapped.maxValue();
        }

        public Type type() {
            return this.type;
        }

        public DensityFunction wrapped() {
            return this.wrapped;
        }

        static enum Type implements StringRepresentable {
            Interpolated("interpolated"),
            FlatCache("flat_cache"),
            Cache2D("cache_2d"),
            CacheOnce("cache_once"),
            CacheAllInCell("cache_all_in_cell");

            private final String name;
            final KeyDispatchDataCodec<MarkerOrMarked> codec = DensityFunctions.singleFunctionArgumentCodec((p_208740_) -> {
                return new Marker(this, p_208740_);
            }, MarkerOrMarked::wrapped);

            private Type(String p_208737_) {
                this.name = p_208737_;
            }

            public String getSerializedName() {
                return this.name;
            }
        }
    }

    protected static record Noise(DensityFunction.NoiseHolder noise, double xzScale, double yScale) implements DensityFunction {
        public static final MapCodec<Noise> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208798_) -> {
            return p_208798_.group(NoiseHolder.CODEC.fieldOf("noise").forGetter(Noise::noise), Codec.DOUBLE.fieldOf("xz_scale").forGetter(Noise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(Noise::yScale)).apply(p_208798_, Noise::new);
        });
        public static final KeyDispatchDataCodec<Noise> CODEC;

        protected Noise(DensityFunction.NoiseHolder noise, @Deprecated double xzScale, double yScale) {
            this.noise = noise;
            this.xzScale = xzScale;
            this.yScale = yScale;
        }

        public double compute(DensityFunction.FunctionContext p_208800_) {
            return this.noise.getValue((double)p_208800_.blockX() * this.xzScale, (double)p_208800_.blockY() * this.yScale, (double)p_208800_.blockZ() * this.xzScale);
        }

        public void fillArray(double[] p_224079_, DensityFunction.ContextProvider p_224080_) {
            p_224080_.fillAllDirectly(p_224079_, this);
        }

        public DensityFunction mapAll(DensityFunction.Visitor p_224077_) {
            return p_224077_.apply(new Noise(p_224077_.visitNoise(this.noise), this.xzScale, this.yScale));
        }

        public double minValue() {
            return -this.maxValue();
        }

        public double maxValue() {
            return this.noise.maxValue();
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public DensityFunction.NoiseHolder noise() {
            return this.noise;
        }

        /** @deprecated */
        @Deprecated
        public double xzScale() {
            return this.xzScale;
        }

        public double yScale() {
            return this.yScale;
        }

        static {
            CODEC = DensityFunctions.makeCodec(DATA_CODEC);
        }
    }

    protected static final class EndIslandDensityFunction implements DensityFunction.SimpleFunction {
        public static final KeyDispatchDataCodec<EndIslandDensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(new EndIslandDensityFunction(0L)));
        private static final float ISLAND_THRESHOLD = -0.9F;
        private final SimplexNoise islandNoise;

        public EndIslandDensityFunction(long p_208630_) {
            RandomSource $$1 = new LegacyRandomSource(p_208630_);
            $$1.consumeCount(17292);
            this.islandNoise = new SimplexNoise($$1);
        }

        private static float getHeightValue(SimplexNoise p_224063_, int p_224064_, int p_224065_) {
            int $$3 = p_224064_ / 2;
            int $$4 = p_224065_ / 2;
            int $$5 = p_224064_ % 2;
            int $$6 = p_224065_ % 2;
            float $$7 = 100.0F - Mth.sqrt((float)(p_224064_ * p_224064_ + p_224065_ * p_224065_)) * 8.0F;
            $$7 = Mth.clamp($$7, -100.0F, 80.0F);

            for(int $$8 = -12; $$8 <= 12; ++$$8) {
                for(int $$9 = -12; $$9 <= 12; ++$$9) {
                    long $$10 = (long)($$3 + $$8);
                    long $$11 = (long)($$4 + $$9);
                    if ($$10 * $$10 + $$11 * $$11 > 4096L && p_224063_.getValue((double)$$10, (double)$$11) < -0.8999999761581421) {
                        float $$12 = (Mth.abs((float)$$10) * 3439.0F + Mth.abs((float)$$11) * 147.0F) % 13.0F + 9.0F;
                        float $$13 = (float)($$5 - $$8 * 2);
                        float $$14 = (float)($$6 - $$9 * 2);
                        float $$15 = 100.0F - Mth.sqrt($$13 * $$13 + $$14 * $$14) * $$12;
                        $$15 = Mth.clamp($$15, -100.0F, 80.0F);
                        $$7 = Math.max($$7, $$15);
                    }
                }
            }

            return $$7;
        }

        public double compute(DensityFunction.FunctionContext p_208633_) {
            return ((double)getHeightValue(this.islandNoise, p_208633_.blockX() / 8, p_208633_.blockZ() / 8) - 8.0) / 128.0;
        }

        public double minValue() {
            return -0.84375;
        }

        public double maxValue() {
            return 0.5625;
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    protected static record WeirdScaledSampler(DensityFunction input, DensityFunction.NoiseHolder noise, RarityValueMapper rarityValueMapper) implements TransformerWithContext {
        private static final MapCodec<WeirdScaledSampler> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208438_) -> {
            return p_208438_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(WeirdScaledSampler::input), NoiseHolder.CODEC.fieldOf("noise").forGetter(WeirdScaledSampler::noise), net.minecraft.world.level.levelgen.DensityFunctions.WeirdScaledSampler.RarityValueMapper.CODEC.fieldOf("rarity_value_mapper").forGetter(WeirdScaledSampler::rarityValueMapper)).apply(p_208438_, WeirdScaledSampler::new);
        });
        public static final KeyDispatchDataCodec<WeirdScaledSampler> CODEC;

        protected WeirdScaledSampler(DensityFunction input, DensityFunction.NoiseHolder noise, RarityValueMapper rarityValueMapper) {
            this.input = input;
            this.noise = noise;
            this.rarityValueMapper = rarityValueMapper;
        }

        public double transform(DensityFunction.FunctionContext p_208440_, double p_208441_) {
            double $$2 = this.rarityValueMapper.mapper.get(p_208441_);
            return $$2 * Math.abs(this.noise.getValue((double)p_208440_.blockX() / $$2, (double)p_208440_.blockY() / $$2, (double)p_208440_.blockZ() / $$2));
        }

        public DensityFunction mapAll(DensityFunction.Visitor p_208443_) {
            return p_208443_.apply(new WeirdScaledSampler(this.input.mapAll(p_208443_), p_208443_.visitNoise(this.noise), this.rarityValueMapper));
        }

        public double minValue() {
            return 0.0;
        }

        public double maxValue() {
            return this.rarityValueMapper.maxRarity * this.noise.maxValue();
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public DensityFunction input() {
            return this.input;
        }

        public DensityFunction.NoiseHolder noise() {
            return this.noise;
        }

        public RarityValueMapper rarityValueMapper() {
            return this.rarityValueMapper;
        }

        static {
            CODEC = DensityFunctions.makeCodec(DATA_CODEC);
        }

        public static enum RarityValueMapper implements StringRepresentable {
            TYPE1("type_1", NoiseRouterData.QuantizedSpaghettiRarity::getSpaghettiRarity3D, 2.0),
            TYPE2("type_2", NoiseRouterData.QuantizedSpaghettiRarity::getSphaghettiRarity2D, 3.0);

            public static final Codec<RarityValueMapper> CODEC = StringRepresentable.fromEnum(RarityValueMapper::values);
            private final String name;
            final Double2DoubleFunction mapper;
            final double maxRarity;

            private RarityValueMapper(String p_208470_, Double2DoubleFunction p_208471_, double p_208472_) {
                this.name = p_208470_;
                this.mapper = p_208471_;
                this.maxRarity = p_208472_;
            }

            public String getSerializedName() {
                return this.name;
            }
        }
    }

    protected static record ShiftedNoise(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.NoiseHolder noise) implements DensityFunction {
        private static final MapCodec<ShiftedNoise> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208943_) -> {
            return p_208943_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_x").forGetter(ShiftedNoise::shiftX), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_y").forGetter(ShiftedNoise::shiftY), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_z").forGetter(ShiftedNoise::shiftZ), Codec.DOUBLE.fieldOf("xz_scale").forGetter(ShiftedNoise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(ShiftedNoise::yScale), NoiseHolder.CODEC.fieldOf("noise").forGetter(ShiftedNoise::noise)).apply(p_208943_, ShiftedNoise::new);
        });
        public static final KeyDispatchDataCodec<ShiftedNoise> CODEC;

        protected ShiftedNoise(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.NoiseHolder noise) {
            this.shiftX = shiftX;
            this.shiftY = shiftY;
            this.shiftZ = shiftZ;
            this.xzScale = xzScale;
            this.yScale = yScale;
            this.noise = noise;
        }

        public double compute(DensityFunction.FunctionContext p_208945_) {
            double $$1 = (double)p_208945_.blockX() * this.xzScale + this.shiftX.compute(p_208945_);
            double $$2 = (double)p_208945_.blockY() * this.yScale + this.shiftY.compute(p_208945_);
            double $$3 = (double)p_208945_.blockZ() * this.xzScale + this.shiftZ.compute(p_208945_);
            return this.noise.getValue($$1, $$2, $$3);
        }

        public void fillArray(double[] p_208956_, DensityFunction.ContextProvider p_208957_) {
            p_208957_.fillAllDirectly(p_208956_, this);
        }

        public DensityFunction mapAll(DensityFunction.Visitor p_208947_) {
            return p_208947_.apply(new ShiftedNoise(this.shiftX.mapAll(p_208947_), this.shiftY.mapAll(p_208947_), this.shiftZ.mapAll(p_208947_), this.xzScale, this.yScale, p_208947_.visitNoise(this.noise)));
        }

        public double minValue() {
            return -this.maxValue();
        }

        public double maxValue() {
            return this.noise.maxValue();
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public DensityFunction shiftX() {
            return this.shiftX;
        }

        public DensityFunction shiftY() {
            return this.shiftY;
        }

        public DensityFunction shiftZ() {
            return this.shiftZ;
        }

        public double xzScale() {
            return this.xzScale;
        }

        public double yScale() {
            return this.yScale;
        }

        public DensityFunction.NoiseHolder noise() {
            return this.noise;
        }

        static {
            CODEC = DensityFunctions.makeCodec(DATA_CODEC);
        }
    }

    static record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction {
        public static final MapCodec<RangeChoice> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208837_) -> {
            return p_208837_.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(RangeChoice::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_inclusive").forGetter(RangeChoice::minInclusive), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_exclusive").forGetter(RangeChoice::maxExclusive), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_in_range").forGetter(RangeChoice::whenInRange), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_out_of_range").forGetter(RangeChoice::whenOutOfRange)).apply(p_208837_, RangeChoice::new);
        });
        public static final KeyDispatchDataCodec<RangeChoice> CODEC;

        RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) {
            this.input = input;
            this.minInclusive = minInclusive;
            this.maxExclusive = maxExclusive;
            this.whenInRange = whenInRange;
            this.whenOutOfRange = whenOutOfRange;
        }

        public double compute(DensityFunction.FunctionContext p_208839_) {
            double $$1 = this.input.compute(p_208839_);
            return $$1 >= this.minInclusive && $$1 < this.maxExclusive ? this.whenInRange.compute(p_208839_) : this.whenOutOfRange.compute(p_208839_);
        }

        public void fillArray(double[] p_208843_, DensityFunction.ContextProvider p_208844_) {
            this.input.fillArray(p_208843_, p_208844_);

            for(int $$2 = 0; $$2 < p_208843_.length; ++$$2) {
                double $$3 = p_208843_[$$2];
                if ($$3 >= this.minInclusive && $$3 < this.maxExclusive) {
                    p_208843_[$$2] = this.whenInRange.compute(p_208844_.forIndex($$2));
                } else {
                    p_208843_[$$2] = this.whenOutOfRange.compute(p_208844_.forIndex($$2));
                }
            }

        }

        public DensityFunction mapAll(DensityFunction.Visitor p_208841_) {
            return p_208841_.apply(new RangeChoice(this.input.mapAll(p_208841_), this.minInclusive, this.maxExclusive, this.whenInRange.mapAll(p_208841_), this.whenOutOfRange.mapAll(p_208841_)));
        }

        public double minValue() {
            return Math.min(this.whenInRange.minValue(), this.whenOutOfRange.minValue());
        }

        public double maxValue() {
            return Math.max(this.whenInRange.maxValue(), this.whenOutOfRange.maxValue());
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public DensityFunction input() {
            return this.input;
        }

        public double minInclusive() {
            return this.minInclusive;
        }

        public double maxExclusive() {
            return this.maxExclusive;
        }

        public DensityFunction whenInRange() {
            return this.whenInRange;
        }

        public DensityFunction whenOutOfRange() {
            return this.whenOutOfRange;
        }

        static {
            CODEC = DensityFunctions.makeCodec(DATA_CODEC);
        }
    }

    protected static record ShiftA(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoise {
        static final KeyDispatchDataCodec<ShiftA> CODEC;

        protected ShiftA(DensityFunction.NoiseHolder offsetNoise) {
            this.offsetNoise = offsetNoise;
        }

        public double compute(DensityFunction.FunctionContext p_208884_) {
            return this.compute((double)p_208884_.blockX(), 0.0, (double)p_208884_.blockZ());
        }

        public DensityFunction mapAll(DensityFunction.Visitor p_224093_) {
            return p_224093_.apply(new ShiftA(p_224093_.visitNoise(this.offsetNoise)));
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public DensityFunction.NoiseHolder offsetNoise() {
            return this.offsetNoise;
        }

        static {
            CODEC = DensityFunctions.singleArgumentCodec(NoiseHolder.CODEC, ShiftA::new, ShiftA::offsetNoise);
        }
    }

    protected static record ShiftB(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoise {
        static final KeyDispatchDataCodec<ShiftB> CODEC;

        protected ShiftB(DensityFunction.NoiseHolder offsetNoise) {
            this.offsetNoise = offsetNoise;
        }

        public double compute(DensityFunction.FunctionContext p_208904_) {
            return this.compute((double)p_208904_.blockZ(), (double)p_208904_.blockX(), 0.0);
        }

        public DensityFunction mapAll(DensityFunction.Visitor p_224099_) {
            return p_224099_.apply(new ShiftB(p_224099_.visitNoise(this.offsetNoise)));
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public DensityFunction.NoiseHolder offsetNoise() {
            return this.offsetNoise;
        }

        static {
            CODEC = DensityFunctions.singleArgumentCodec(NoiseHolder.CODEC, ShiftB::new, ShiftB::offsetNoise);
        }
    }

    protected static record Shift(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoise {
        static final KeyDispatchDataCodec<Shift> CODEC;

        protected Shift(DensityFunction.NoiseHolder offsetNoise) {
            this.offsetNoise = offsetNoise;
        }

        public double compute(DensityFunction.FunctionContext p_208864_) {
            return this.compute((double)p_208864_.blockX(), (double)p_208864_.blockY(), (double)p_208864_.blockZ());
        }

        public DensityFunction mapAll(DensityFunction.Visitor p_224087_) {
            return p_224087_.apply(new Shift(p_224087_.visitNoise(this.offsetNoise)));
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public DensityFunction.NoiseHolder offsetNoise() {
            return this.offsetNoise;
        }

        static {
            CODEC = DensityFunctions.singleArgumentCodec(NoiseHolder.CODEC, Shift::new, Shift::offsetNoise);
        }
    }

    static record BlendDensity(DensityFunction input) implements TransformerWithContext {
        static final KeyDispatchDataCodec<BlendDensity> CODEC = DensityFunctions.singleFunctionArgumentCodec(BlendDensity::new, BlendDensity::input);

        BlendDensity(DensityFunction input) {
            this.input = input;
        }

        public double transform(DensityFunction.FunctionContext p_208553_, double p_208554_) {
            return p_208553_.getBlender().blendDensity(p_208553_, p_208554_);
        }

        public DensityFunction mapAll(DensityFunction.Visitor p_208556_) {
            return p_208556_.apply(new BlendDensity(this.input.mapAll(p_208556_)));
        }

        public double minValue() {
            return Double.NEGATIVE_INFINITY;
        }

        public double maxValue() {
            return Double.POSITIVE_INFINITY;
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public DensityFunction input() {
            return this.input;
        }
    }

    protected static record Clamp(DensityFunction input, double minValue, double maxValue) implements PureTransformer {
        private static final MapCodec<Clamp> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208597_) -> {
            return p_208597_.group(DensityFunction.DIRECT_CODEC.fieldOf("input").forGetter(Clamp::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min").forGetter(Clamp::minValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max").forGetter(Clamp::maxValue)).apply(p_208597_, Clamp::new);
        });
        public static final KeyDispatchDataCodec<Clamp> CODEC;

        protected Clamp(DensityFunction input, double minValue, double maxValue) {
            this.input = input;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public double transform(double p_208595_) {
            return Mth.clamp(p_208595_, this.minValue, this.maxValue);
        }

        public DensityFunction mapAll(DensityFunction.Visitor p_208599_) {
            return new Clamp(this.input.mapAll(p_208599_), this.minValue, this.maxValue);
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public DensityFunction input() {
            return this.input;
        }

        public double minValue() {
            return this.minValue;
        }

        public double maxValue() {
            return this.maxValue;
        }

        static {
            CODEC = DensityFunctions.makeCodec(DATA_CODEC);
        }
    }

    protected static record Mapped(Type type, DensityFunction input, double minValue, double maxValue) implements PureTransformer {
        protected Mapped(Type type, DensityFunction input, double minValue, double maxValue) {
            this.type = type;
            this.input = input;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public static Mapped create(Type p_208672_, DensityFunction p_208673_) {
            double $$2 = p_208673_.minValue();
            double $$3 = transform(p_208672_, $$2);
            double $$4 = transform(p_208672_, p_208673_.maxValue());
            return p_208672_ != net.minecraft.world.level.levelgen.DensityFunctions.Mapped.Type.ABS && p_208672_ != net.minecraft.world.level.levelgen.DensityFunctions.Mapped.Type.SQUARE ? new Mapped(p_208672_, p_208673_, $$3, $$4) : new Mapped(p_208672_, p_208673_, Math.max(0.0, $$2), Math.max($$3, $$4));
        }

        private static double transform(Type p_208669_, double p_208670_) {
            double var10000;
            switch (p_208669_) {
                case ABS:
                    var10000 = Math.abs(p_208670_);
                    break;
                case SQUARE:
                    var10000 = p_208670_ * p_208670_;
                    break;
                case CUBE:
                    var10000 = p_208670_ * p_208670_ * p_208670_;
                    break;
                case HALF_NEGATIVE:
                    var10000 = p_208670_ > 0.0 ? p_208670_ : p_208670_ * 0.5;
                    break;
                case QUARTER_NEGATIVE:
                    var10000 = p_208670_ > 0.0 ? p_208670_ : p_208670_ * 0.25;
                    break;
                case SQUEEZE:
                    double $$2 = Mth.clamp(p_208670_, -1.0, 1.0);
                    var10000 = $$2 / 2.0 - $$2 * $$2 * $$2 / 24.0;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        public double transform(double p_208665_) {
            return transform(this.type, p_208665_);
        }

        public Mapped mapAll(DensityFunction.Visitor p_208677_) {
            return create(this.type, this.input.mapAll(p_208677_));
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return this.type.codec;
        }

        public Type type() {
            return this.type;
        }

        public DensityFunction input() {
            return this.input;
        }

        public double minValue() {
            return this.minValue;
        }

        public double maxValue() {
            return this.maxValue;
        }

        static enum Type implements StringRepresentable {
            ABS("abs"),
            SQUARE("square"),
            CUBE("cube"),
            HALF_NEGATIVE("half_negative"),
            QUARTER_NEGATIVE("quarter_negative"),
            SQUEEZE("squeeze");

            private final String name;
            final KeyDispatchDataCodec<Mapped> codec = DensityFunctions.singleFunctionArgumentCodec((p_208700_) -> {
                return net.minecraft.world.level.levelgen.DensityFunctions.Mapped.create(this, p_208700_);
            }, Mapped::input);

            private Type(String p_208697_) {
                this.name = p_208697_;
            }

            public String getSerializedName() {
                return this.name;
            }
        }
    }

    interface TwoArgumentSimpleFunction extends DensityFunction {
        Logger LOGGER = LogUtils.getLogger();

        static TwoArgumentSimpleFunction create(Type p_209074_, DensityFunction p_209075_, DensityFunction p_209076_) {
            double $$3 = p_209075_.minValue();
            double $$4 = p_209076_.minValue();
            double $$5 = p_209075_.maxValue();
            double $$6 = p_209076_.maxValue();
            if (p_209074_ == net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.MIN || p_209074_ == net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.MAX) {
                boolean $$7 = $$3 >= $$6;
                boolean $$8 = $$4 >= $$5;
                if ($$7 || $$8) {
                    LOGGER.warn("Creating a " + p_209074_ + " function between two non-overlapping inputs: " + p_209075_ + " and " + p_209076_);
                }
            }

            double var10000;
            switch (p_209074_) {
                case ADD -> var10000 = $$3 + $$4;
                case MAX -> var10000 = Math.max($$3, $$4);
                case MIN -> var10000 = Math.min($$3, $$4);
                case MUL -> var10000 = $$3 > 0.0 && $$4 > 0.0 ? $$3 * $$4 : ($$5 < 0.0 && $$6 < 0.0 ? $$5 * $$6 : Math.min($$3 * $$6, $$5 * $$4));
                default -> throw new IncompatibleClassChangeError();
            }

            double $$9 = var10000;
            switch (p_209074_) {
                case ADD -> var10000 = $$5 + $$6;
                case MAX -> var10000 = Math.max($$5, $$6);
                case MIN -> var10000 = Math.min($$5, $$6);
                case MUL -> var10000 = $$3 > 0.0 && $$4 > 0.0 ? $$5 * $$6 : ($$5 < 0.0 && $$6 < 0.0 ? $$3 * $$4 : Math.max($$3 * $$4, $$5 * $$6));
                default -> throw new IncompatibleClassChangeError();
            }

            double $$10 = var10000;
            if (p_209074_ == net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.MUL || p_209074_ == net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.ADD) {
                Constant $$12;
                if (p_209075_ instanceof Constant) {
                    $$12 = (Constant)p_209075_;
                    return new MulOrAdd(p_209074_ == net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? net.minecraft.world.level.levelgen.DensityFunctions.MulOrAdd.Type.ADD : net.minecraft.world.level.levelgen.DensityFunctions.MulOrAdd.Type.MUL, p_209076_, $$9, $$10, $$12.value);
                }

                if (p_209076_ instanceof Constant) {
                    $$12 = (Constant)p_209076_;
                    return new MulOrAdd(p_209074_ == net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? net.minecraft.world.level.levelgen.DensityFunctions.MulOrAdd.Type.ADD : net.minecraft.world.level.levelgen.DensityFunctions.MulOrAdd.Type.MUL, p_209075_, $$9, $$10, $$12.value);
                }
            }

            return new Ap2(p_209074_, p_209075_, p_209076_, $$9, $$10);
        }

        Type type();

        DensityFunction argument1();

        DensityFunction argument2();

        default KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return this.type().codec;
        }

        public static enum Type implements StringRepresentable {
            ADD("add"),
            MUL("mul"),
            MIN("min"),
            MAX("max");

            final KeyDispatchDataCodec<TwoArgumentSimpleFunction> codec = DensityFunctions.doubleFunctionArgumentCodec((p_209092_, p_209093_) -> {
                return net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.create(this, p_209092_, p_209093_);
            }, TwoArgumentSimpleFunction::argument1, TwoArgumentSimpleFunction::argument2);
            private final String name;

            private Type(String p_209089_) {
                this.name = p_209089_;
            }

            public String getSerializedName() {
                return this.name;
            }
        }
    }

    public static record Spline(CubicSpline<Point, Coordinate> spline) implements DensityFunction {
        private static final Codec<CubicSpline<Point, Coordinate>> SPLINE_CODEC;
        private static final MapCodec<Spline> DATA_CODEC;
        public static final KeyDispatchDataCodec<Spline> CODEC;

        public Spline(CubicSpline<Point, Coordinate> spline) {
            this.spline = spline;
        }

        public double compute(DensityFunction.FunctionContext p_211715_) {
            return (double)this.spline.apply(new Point(p_211715_));
        }

        public double minValue() {
            return (double)this.spline.minValue();
        }

        public double maxValue() {
            return (double)this.spline.maxValue();
        }

        public void fillArray(double[] p_211722_, DensityFunction.ContextProvider p_211723_) {
            p_211723_.fillAllDirectly(p_211722_, this);
        }

        public DensityFunction mapAll(DensityFunction.Visitor p_211717_) {
            return p_211717_.apply(new Spline(this.spline.mapAll((p_224119_) -> {
                return p_224119_.mapAll(p_211717_);
            })));
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public CubicSpline<Point, Coordinate> spline() {
            return this.spline;
        }

        static {
            SPLINE_CODEC = CubicSpline.codec(net.minecraft.world.level.levelgen.DensityFunctions.Spline.Coordinate.CODEC);
            DATA_CODEC = SPLINE_CODEC.fieldOf("spline").xmap(Spline::new, Spline::spline);
            CODEC = DensityFunctions.makeCodec(DATA_CODEC);
        }

        public static record Point(DensityFunction.FunctionContext context) {
            public Point(DensityFunction.FunctionContext context) {
                this.context = context;
            }

            public DensityFunction.FunctionContext context() {
                return this.context;
            }
        }

        public static record Coordinate(Holder<DensityFunction> function) implements ToFloatFunction<Point> {
            public static final Codec<Coordinate> CODEC;

            public Coordinate(Holder<DensityFunction> function) {
                this.function = function;
            }

            public String toString() {
                Optional<ResourceKey<DensityFunction>> $$0 = this.function.unwrapKey();
                if ($$0.isPresent()) {
                    ResourceKey<DensityFunction> $$1 = (ResourceKey)$$0.get();
                    if ($$1 == NoiseRouterData.CONTINENTS) {
                        return "continents";
                    }

                    if ($$1 == NoiseRouterData.EROSION) {
                        return "erosion";
                    }

                    if ($$1 == NoiseRouterData.RIDGES) {
                        return "weirdness";
                    }

                    if ($$1 == NoiseRouterData.RIDGES_FOLDED) {
                        return "ridges";
                    }
                }

                return "Coordinate[" + this.function + "]";
            }

            public float apply(Point p_224130_) {
                return (float)((DensityFunction)this.function.value()).compute(p_224130_.context());
            }

            public float minValue() {
                return this.function.isBound() ? (float)((DensityFunction)this.function.value()).minValue() : Float.NEGATIVE_INFINITY;
            }

            public float maxValue() {
                return this.function.isBound() ? (float)((DensityFunction)this.function.value()).maxValue() : Float.POSITIVE_INFINITY;
            }

            public Coordinate mapAll(DensityFunction.Visitor p_224128_) {
                return new Coordinate(new Holder.Direct(((DensityFunction)this.function.value()).mapAll(p_224128_)));
            }

            public Holder<DensityFunction> function() {
                return this.function;
            }

            static {
                CODEC = DensityFunction.CODEC.xmap(Coordinate::new, Coordinate::function);
            }
        }
    }

    static record Constant(double value) implements DensityFunction.SimpleFunction {
        static final KeyDispatchDataCodec<Constant> CODEC;
        static final Constant ZERO;

        Constant(double value) {
            this.value = value;
        }

        public double compute(DensityFunction.FunctionContext p_208615_) {
            return this.value;
        }

        public void fillArray(double[] p_208617_, DensityFunction.ContextProvider p_208618_) {
            Arrays.fill(p_208617_, this.value);
        }

        public double minValue() {
            return this.value;
        }

        public double maxValue() {
            return this.value;
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public double value() {
            return this.value;
        }

        static {
            CODEC = DensityFunctions.singleArgumentCodec(DensityFunctions.NOISE_VALUE_CODEC, Constant::new, Constant::value);
            ZERO = new Constant(0.0);
        }
    }

    static record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.SimpleFunction {
        private static final MapCodec<YClampedGradient> DATA_CODEC = RecordCodecBuilder.mapCodec((p_208494_) -> {
            return p_208494_.group(Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("from_y").forGetter(YClampedGradient::fromY), Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("to_y").forGetter(YClampedGradient::toY), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("from_value").forGetter(YClampedGradient::fromValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("to_value").forGetter(YClampedGradient::toValue)).apply(p_208494_, YClampedGradient::new);
        });
        public static final KeyDispatchDataCodec<YClampedGradient> CODEC;

        YClampedGradient(int fromY, int toY, double fromValue, double toValue) {
            this.fromY = fromY;
            this.toY = toY;
            this.fromValue = fromValue;
            this.toValue = toValue;
        }

        public double compute(DensityFunction.FunctionContext p_208496_) {
            return Mth.clampedMap((double)p_208496_.blockY(), (double)this.fromY, (double)this.toY, this.fromValue, this.toValue);
        }

        public double minValue() {
            return Math.min(this.fromValue, this.toValue);
        }

        public double maxValue() {
            return Math.max(this.fromValue, this.toValue);
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public int fromY() {
            return this.fromY;
        }

        public int toY() {
            return this.toY;
        }

        public double fromValue() {
            return this.fromValue;
        }

        public double toValue() {
            return this.toValue;
        }

        static {
            CODEC = DensityFunctions.makeCodec(DATA_CODEC);
        }
    }

    static record Ap2(TwoArgumentSimpleFunction.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue) implements TwoArgumentSimpleFunction {
        Ap2(TwoArgumentSimpleFunction.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue) {
            this.type = type;
            this.argument1 = argument1;
            this.argument2 = argument2;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public double compute(DensityFunction.FunctionContext p_208410_) {
            double $$1 = this.argument1.compute(p_208410_);
            double var10000;
            switch (this.type) {
                case ADD -> var10000 = $$1 + this.argument2.compute(p_208410_);
                case MAX -> var10000 = $$1 > this.argument2.maxValue() ? $$1 : Math.max($$1, this.argument2.compute(p_208410_));
                case MIN -> var10000 = $$1 < this.argument2.minValue() ? $$1 : Math.min($$1, this.argument2.compute(p_208410_));
                case MUL -> var10000 = $$1 == 0.0 ? 0.0 : $$1 * this.argument2.compute(p_208410_);
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        public void fillArray(double[] p_208414_, DensityFunction.ContextProvider p_208415_) {
            this.argument1.fillArray(p_208414_, p_208415_);
            int $$7;
            double $$8;
            double $$6;
            switch (this.type) {
                case ADD:
                    double[] $$2 = new double[p_208414_.length];
                    this.argument2.fillArray($$2, p_208415_);

                    for(int $$3 = 0; $$3 < p_208414_.length; ++$$3) {
                        p_208414_[$$3] += $$2[$$3];
                    }

                    return;
                case MAX:
                    $$6 = this.argument2.maxValue();

                    for($$7 = 0; $$7 < p_208414_.length; ++$$7) {
                        $$8 = p_208414_[$$7];
                        p_208414_[$$7] = $$8 > $$6 ? $$8 : Math.max($$8, this.argument2.compute(p_208415_.forIndex($$7)));
                    }

                    return;
                case MIN:
                    $$6 = this.argument2.minValue();

                    for($$7 = 0; $$7 < p_208414_.length; ++$$7) {
                        $$8 = p_208414_[$$7];
                        p_208414_[$$7] = $$8 < $$6 ? $$8 : Math.min($$8, this.argument2.compute(p_208415_.forIndex($$7)));
                    }

                    return;
                case MUL:
                    for(int $$4 = 0; $$4 < p_208414_.length; ++$$4) {
                        double $$5 = p_208414_[$$4];
                        p_208414_[$$4] = $$5 == 0.0 ? 0.0 : $$5 * this.argument2.compute(p_208415_.forIndex($$4));
                    }
            }

        }

        public DensityFunction mapAll(DensityFunction.Visitor p_208412_) {
            return p_208412_.apply(net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.create(this.type, this.argument1.mapAll(p_208412_), this.argument2.mapAll(p_208412_)));
        }

        public double minValue() {
            return this.minValue;
        }

        public double maxValue() {
            return this.maxValue;
        }

        public TwoArgumentSimpleFunction.Type type() {
            return this.type;
        }

        public DensityFunction argument1() {
            return this.argument1;
        }

        public DensityFunction argument2() {
            return this.argument2;
        }
    }

    private static record MulOrAdd(Type specificType, DensityFunction input, double minValue, double maxValue, double argument) implements PureTransformer, TwoArgumentSimpleFunction {
        MulOrAdd(Type specificType, DensityFunction input, double minValue, double maxValue, double argument) {
            this.specificType = specificType;
            this.input = input;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.argument = argument;
        }

        public TwoArgumentSimpleFunction.Type type() {
            return this.specificType == net.minecraft.world.level.levelgen.DensityFunctions.MulOrAdd.Type.MUL ? net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.MUL : net.minecraft.world.level.levelgen.DensityFunctions.TwoArgumentSimpleFunction.Type.ADD;
        }

        public DensityFunction argument1() {
            return DensityFunctions.constant(this.argument);
        }

        public DensityFunction argument2() {
            return this.input;
        }

        public double transform(double p_208759_) {
            double var10000;
            switch (this.specificType) {
                case MUL -> var10000 = p_208759_ * this.argument;
                case ADD -> var10000 = p_208759_ + this.argument;
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        public DensityFunction mapAll(DensityFunction.Visitor p_208761_) {
            DensityFunction $$1 = this.input.mapAll(p_208761_);
            double $$2 = $$1.minValue();
            double $$3 = $$1.maxValue();
            double $$6;
            double $$9;
            if (this.specificType == net.minecraft.world.level.levelgen.DensityFunctions.MulOrAdd.Type.ADD) {
                $$6 = $$2 + this.argument;
                $$9 = $$3 + this.argument;
            } else if (this.argument >= 0.0) {
                $$6 = $$2 * this.argument;
                $$9 = $$3 * this.argument;
            } else {
                $$6 = $$3 * this.argument;
                $$9 = $$2 * this.argument;
            }

            return new MulOrAdd(this.specificType, $$1, $$6, $$9, this.argument);
        }

        public Type specificType() {
            return this.specificType;
        }

        public DensityFunction input() {
            return this.input;
        }

        public double minValue() {
            return this.minValue;
        }

        public double maxValue() {
            return this.maxValue;
        }

        public double argument() {
            return this.argument;
        }

        static enum Type {
            MUL,
            ADD;

            private Type() {
            }
        }
    }

    interface ShiftNoise extends DensityFunction {
        DensityFunction.NoiseHolder offsetNoise();

        default double minValue() {
            return -this.maxValue();
        }

        default double maxValue() {
            return this.offsetNoise().maxValue() * 4.0;
        }

        default double compute(double p_208918_, double p_208919_, double p_208920_) {
            return this.offsetNoise().getValue(p_208918_ * 0.25, p_208919_ * 0.25, p_208920_ * 0.25) * 4.0;
        }

        default void fillArray(double[] p_224103_, DensityFunction.ContextProvider p_224104_) {
            p_224104_.fillAllDirectly(p_224103_, this);
        }
    }

    public interface MarkerOrMarked extends DensityFunction {
        Marker.Type type();

        DensityFunction wrapped();

        default KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return this.type().codec;
        }

        default DensityFunction mapAll(DensityFunction.Visitor p_224070_) {
            return p_224070_.apply(new Marker(this.type(), this.wrapped().mapAll(p_224070_)));
        }
    }

    @VisibleForDebug
    public static record HolderHolder(Holder<DensityFunction> function) implements DensityFunction {
        public HolderHolder(Holder<DensityFunction> function) {
            this.function = function;
        }

        public double compute(DensityFunction.FunctionContext p_208641_) {
            return ((DensityFunction)this.function.value()).compute(p_208641_);
        }

        public void fillArray(double[] p_208645_, DensityFunction.ContextProvider p_208646_) {
            ((DensityFunction)this.function.value()).fillArray(p_208645_, p_208646_);
        }

        public DensityFunction mapAll(DensityFunction.Visitor p_208643_) {
            return p_208643_.apply(new HolderHolder(new Holder.Direct(((DensityFunction)this.function.value()).mapAll(p_208643_))));
        }

        public double minValue() {
            return this.function.isBound() ? ((DensityFunction)this.function.value()).minValue() : Double.NEGATIVE_INFINITY;
        }

        public double maxValue() {
            return this.function.isBound() ? ((DensityFunction)this.function.value()).maxValue() : Double.POSITIVE_INFINITY;
        }

        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
        }

        public Holder<DensityFunction> function() {
            return this.function;
        }
    }

    public interface BeardifierOrMarker extends DensityFunction.SimpleFunction {
        KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(net.minecraft.world.level.levelgen.DensityFunctions.BeardifierMarker.INSTANCE));

        default KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    private interface PureTransformer extends DensityFunction {
        DensityFunction input();

        default double compute(DensityFunction.FunctionContext p_208817_) {
            return this.transform(this.input().compute(p_208817_));
        }

        default void fillArray(double[] p_208819_, DensityFunction.ContextProvider p_208820_) {
            this.input().fillArray(p_208819_, p_208820_);

            for(int $$2 = 0; $$2 < p_208819_.length; ++$$2) {
                p_208819_[$$2] = this.transform(p_208819_[$$2]);
            }

        }

        double transform(double var1);
    }

    private interface TransformerWithContext extends DensityFunction {
        DensityFunction input();

        default double compute(DensityFunction.FunctionContext p_209065_) {
            return this.transform(p_209065_, this.input().compute(p_209065_));
        }

        default void fillArray(double[] p_209069_, DensityFunction.ContextProvider p_209070_) {
            this.input().fillArray(p_209069_, p_209070_);

            for(int $$2 = 0; $$2 < p_209069_.length; ++$$2) {
                p_209069_[$$2] = this.transform(p_209070_.forIndex($$2), p_209069_[$$2]);
            }

        }

        double transform(DensityFunction.FunctionContext var1, double var2);
    }
}
