//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.biome;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public class Climate {
    private static final boolean DEBUG_SLOW_BIOME_SEARCH = false;
    private static final float QUANTIZATION_FACTOR = 10000.0F;
    @VisibleForTesting
    protected static final int PARAMETER_COUNT = 7;

    public Climate() {
    }

    public static TargetPoint target(float p_186782_, float p_186783_, float p_186784_, float p_186785_, float p_186786_, float p_186787_) {
        return new TargetPoint(quantizeCoord(p_186782_), quantizeCoord(p_186783_), quantizeCoord(p_186784_), quantizeCoord(p_186785_), quantizeCoord(p_186786_), quantizeCoord(p_186787_));
    }

    public static ParameterPoint parameters(float p_186789_, float p_186790_, float p_186791_, float p_186792_, float p_186793_, float p_186794_, float p_186795_) {
        return new ParameterPoint(net.minecraft.world.level.biome.Climate.Parameter.point(p_186789_), net.minecraft.world.level.biome.Climate.Parameter.point(p_186790_), net.minecraft.world.level.biome.Climate.Parameter.point(p_186791_), net.minecraft.world.level.biome.Climate.Parameter.point(p_186792_), net.minecraft.world.level.biome.Climate.Parameter.point(p_186793_), net.minecraft.world.level.biome.Climate.Parameter.point(p_186794_), quantizeCoord(p_186795_));
    }

    public static ParameterPoint parameters(Parameter p_186799_, Parameter p_186800_, Parameter p_186801_, Parameter p_186802_, Parameter p_186803_, Parameter p_186804_, float p_186805_) {
        return new ParameterPoint(p_186799_, p_186800_, p_186801_, p_186802_, p_186803_, p_186804_, quantizeCoord(p_186805_));
    }

    public static long quantizeCoord(float p_186780_) {
        return (long)(p_186780_ * 10000.0F);
    }

    public static float unquantizeCoord(long p_186797_) {
        return (float)p_186797_ / 10000.0F;
    }

    public static Sampler empty() {
        DensityFunction $$0 = DensityFunctions.zero();
        return new Sampler($$0, $$0, $$0, $$0, $$0, $$0, List.of());
    }

    public static BlockPos findSpawnPosition(List<ParameterPoint> p_207843_, Sampler p_207844_) {
        return (new SpawnFinder(p_207843_, p_207844_)).result.location();
    }

    public static record TargetPoint(long temperature, long humidity, long continentalness, long erosion, long depth, long weirdness) {
        public TargetPoint(long temperature, long humidity, long continentalness, long erosion, long depth, long weirdness) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.continentalness = continentalness;
            this.erosion = erosion;
            this.depth = depth;
            this.weirdness = weirdness;
        }

        @VisibleForTesting
        protected long[] toParameterArray() {
            return new long[]{this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, 0L};
        }

        public long temperature() {
            return this.temperature;
        }

        public long humidity() {
            return this.humidity;
        }

        public long continentalness() {
            return this.continentalness;
        }

        public long erosion() {
            return this.erosion;
        }

        public long depth() {
            return this.depth;
        }

        public long weirdness() {
            return this.weirdness;
        }
    }

    public static record ParameterPoint(Parameter temperature, Parameter humidity, Parameter continentalness, Parameter erosion, Parameter depth, Parameter weirdness, long offset) {
        public static final Codec<ParameterPoint> CODEC = RecordCodecBuilder.create((p_186885_) -> {
            return p_186885_.group(net.minecraft.world.level.biome.Climate.Parameter.CODEC.fieldOf("temperature").forGetter((p_186905_) -> {
                return p_186905_.temperature;
            }), net.minecraft.world.level.biome.Climate.Parameter.CODEC.fieldOf("humidity").forGetter((p_186902_) -> {
                return p_186902_.humidity;
            }), net.minecraft.world.level.biome.Climate.Parameter.CODEC.fieldOf("continentalness").forGetter((p_186897_) -> {
                return p_186897_.continentalness;
            }), net.minecraft.world.level.biome.Climate.Parameter.CODEC.fieldOf("erosion").forGetter((p_186894_) -> {
                return p_186894_.erosion;
            }), net.minecraft.world.level.biome.Climate.Parameter.CODEC.fieldOf("depth").forGetter((p_186891_) -> {
                return p_186891_.depth;
            }), net.minecraft.world.level.biome.Climate.Parameter.CODEC.fieldOf("weirdness").forGetter((p_186888_) -> {
                return p_186888_.weirdness;
            }), Codec.floatRange(0.0F, 1.0F).fieldOf("offset").xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter((p_186881_) -> {
                return p_186881_.offset;
            })).apply(p_186885_, ParameterPoint::new);
        });

        public ParameterPoint(Parameter temperature, Parameter humidity, Parameter continentalness, Parameter erosion, Parameter depth, Parameter weirdness, long offset) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.continentalness = continentalness;
            this.erosion = erosion;
            this.depth = depth;
            this.weirdness = weirdness;
            this.offset = offset;
        }

        long fitness(TargetPoint p_186883_) {
            return Mth.square(this.temperature.distance(p_186883_.temperature)) + Mth.square(this.humidity.distance(p_186883_.humidity)) + Mth.square(this.continentalness.distance(p_186883_.continentalness)) + Mth.square(this.erosion.distance(p_186883_.erosion)) + Mth.square(this.depth.distance(p_186883_.depth)) + Mth.square(this.weirdness.distance(p_186883_.weirdness)) + Mth.square(this.offset);
        }

        protected List<Parameter> parameterSpace() {
            return ImmutableList.of(this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, new Parameter(this.offset, this.offset));
        }

        public Parameter temperature() {
            return this.temperature;
        }

        public Parameter humidity() {
            return this.humidity;
        }

        public Parameter continentalness() {
            return this.continentalness;
        }

        public Parameter erosion() {
            return this.erosion;
        }

        public Parameter depth() {
            return this.depth;
        }

        public Parameter weirdness() {
            return this.weirdness;
        }

        public long offset() {
            return this.offset;
        }
    }

    public static record Parameter(long min, long max) {
        public static final Codec<Parameter> CODEC = ExtraCodecs.intervalCodec(Codec.floatRange(-2.0F, 2.0F), "min", "max", (p_275164_, p_275165_) -> {
            return p_275164_.compareTo(p_275165_) > 0 ? DataResult.error(() -> {
                return "Cannon construct interval, min > max (" + p_275164_ + " > " + p_275165_ + ")";
            }) : DataResult.success(new Parameter(Climate.quantizeCoord(p_275164_), Climate.quantizeCoord(p_275165_)));
        }, (p_186841_) -> {
            return Climate.unquantizeCoord(p_186841_.min());
        }, (p_186839_) -> {
            return Climate.unquantizeCoord(p_186839_.max());
        });

        public Parameter(long min, long max) {
            this.min = min;
            this.max = max;
        }

        public static Parameter point(float p_186821_) {
            return span(p_186821_, p_186821_);
        }

        public static Parameter span(float p_186823_, float p_186824_) {
            if (p_186823_ > p_186824_) {
                throw new IllegalArgumentException("min > max: " + p_186823_ + " " + p_186824_);
            } else {
                return new Parameter(Climate.quantizeCoord(p_186823_), Climate.quantizeCoord(p_186824_));
            }
        }

        public static Parameter span(Parameter p_186830_, Parameter p_186831_) {
            if (p_186830_.min() > p_186831_.max()) {
                throw new IllegalArgumentException("min > max: " + p_186830_ + " " + p_186831_);
            } else {
                return new Parameter(p_186830_.min(), p_186831_.max());
            }
        }

        public String toString() {
            return this.min == this.max ? String.format(Locale.ROOT, "%d", this.min) : String.format(Locale.ROOT, "[%d-%d]", this.min, this.max);
        }

        public long distance(long p_186826_) {
            long $$1 = p_186826_ - this.max;
            long $$2 = this.min - p_186826_;
            return $$1 > 0L ? $$1 : Math.max($$2, 0L);
        }

        public long distance(Parameter p_186828_) {
            long $$1 = p_186828_.min() - this.max;
            long $$2 = this.min - p_186828_.max();
            return $$1 > 0L ? $$1 : Math.max($$2, 0L);
        }

        public Parameter span(@Nullable Parameter p_186837_) {
            return p_186837_ == null ? this : new Parameter(Math.min(this.min, p_186837_.min()), Math.max(this.max, p_186837_.max()));
        }

        public long min() {
            return this.min;
        }

        public long max() {
            return this.max;
        }
    }

    public static record Sampler(DensityFunction temperature, DensityFunction humidity, DensityFunction continentalness, DensityFunction erosion, DensityFunction depth, DensityFunction weirdness, List<ParameterPoint> spawnTarget) {
        public Sampler(DensityFunction temperature, DensityFunction humidity, DensityFunction continentalness, DensityFunction erosion, DensityFunction depth, DensityFunction weirdness, List<ParameterPoint> spawnTarget) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.continentalness = continentalness;
            this.erosion = erosion;
            this.depth = depth;
            this.weirdness = weirdness;
            this.spawnTarget = spawnTarget;
        }

        public TargetPoint sample(int p_186975_, int p_186976_, int p_186977_) {
            int $$3 = QuartPos.toBlock(p_186975_);
            int $$4 = QuartPos.toBlock(p_186976_);
            int $$5 = QuartPos.toBlock(p_186977_);
            DensityFunction.SinglePointContext $$6 = new DensityFunction.SinglePointContext($$3, $$4, $$5);
            return Climate.target((float)this.temperature.compute($$6), (float)this.humidity.compute($$6), (float)this.continentalness.compute($$6), (float)this.erosion.compute($$6), (float)this.depth.compute($$6), (float)this.weirdness.compute($$6));
        }

        public BlockPos findSpawnPosition() {
            return this.spawnTarget.isEmpty() ? BlockPos.ZERO : Climate.findSpawnPosition(this.spawnTarget, this);
        }

        public DensityFunction temperature() {
            return this.temperature;
        }

        public DensityFunction humidity() {
            return this.humidity;
        }

        public DensityFunction continentalness() {
            return this.continentalness;
        }

        public DensityFunction erosion() {
            return this.erosion;
        }

        public DensityFunction depth() {
            return this.depth;
        }

        public DensityFunction weirdness() {
            return this.weirdness;
        }

        public List<ParameterPoint> spawnTarget() {
            return this.spawnTarget;
        }
    }

    private static class SpawnFinder {
        Result result;

        SpawnFinder(List<ParameterPoint> p_207872_, Sampler p_207873_) {
            this.result = getSpawnPositionAndFitness(p_207872_, p_207873_, 0, 0);
            this.radialSearch(p_207872_, p_207873_, 2048.0F, 512.0F);
            this.radialSearch(p_207872_, p_207873_, 512.0F, 32.0F);
        }

        private void radialSearch(List<ParameterPoint> p_207875_, Sampler p_207876_, float p_207877_, float p_207878_) {
            float $$4 = 0.0F;
            float $$5 = p_207878_;
            BlockPos $$6 = this.result.location();

            while($$5 <= p_207877_) {
                int $$7 = $$6.getX() + (int)(Math.sin((double)$$4) * (double)$$5);
                int $$8 = $$6.getZ() + (int)(Math.cos((double)$$4) * (double)$$5);
                Result $$9 = getSpawnPositionAndFitness(p_207875_, p_207876_, $$7, $$8);
                if ($$9.fitness() < this.result.fitness()) {
                    this.result = $$9;
                }

                $$4 += p_207878_ / $$5;
                if ((double)$$4 > 6.283185307179586) {
                    $$4 = 0.0F;
                    $$5 += p_207878_;
                }
            }

        }

        private static Result getSpawnPositionAndFitness(List<ParameterPoint> p_207880_, Sampler p_207881_, int p_207882_, int p_207883_) {
            double $$4 = Mth.square(2500.0);
            int $$5 = true;
            long $$6 = (long)((double)Mth.square(10000.0F) * Math.pow((double)(Mth.square((long)p_207882_) + Mth.square((long)p_207883_)) / $$4, 2.0));
            TargetPoint $$7 = p_207881_.sample(QuartPos.fromBlock(p_207882_), 0, QuartPos.fromBlock(p_207883_));
            TargetPoint $$8 = new TargetPoint($$7.temperature(), $$7.humidity(), $$7.continentalness(), $$7.erosion(), 0L, $$7.weirdness());
            long $$9 = Long.MAX_VALUE;

            ParameterPoint $$10;
            for(Iterator var13 = p_207880_.iterator(); var13.hasNext(); $$9 = Math.min($$9, $$10.fitness($$8))) {
                $$10 = (ParameterPoint)var13.next();
            }

            return new Result(new BlockPos(p_207882_, 0, p_207883_), $$6 + $$9);
        }

        private static record Result(BlockPos location, long fitness) {
            Result(BlockPos location, long fitness) {
                this.location = location;
                this.fitness = fitness;
            }

            public BlockPos location() {
                return this.location;
            }

            public long fitness() {
                return this.fitness;
            }
        }
    }

    public static class ParameterList<T> {
        private final List<Pair<ParameterPoint, T>> values;
        private final RTree<T> index;

        public static <T> Codec<ParameterList<T>> codec(MapCodec<T> p_275523_) {
            return ExtraCodecs.nonEmptyList(RecordCodecBuilder.create((p_275233_) -> {
                return p_275233_.group(net.minecraft.world.level.biome.Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), p_275523_.forGetter(Pair::getSecond)).apply(p_275233_, Pair::of);
            }).listOf()).xmap(ParameterList::new, ParameterList::values);
        }

        public ParameterList(List<Pair<ParameterPoint, T>> p_186849_) {
            this.values = p_186849_;
            this.index = net.minecraft.world.level.biome.Climate.RTree.create(p_186849_);
        }

        public List<Pair<ParameterPoint, T>> values() {
            return this.values;
        }

        public T findValue(TargetPoint p_204253_) {
            return this.findValueIndex(p_204253_);
        }

        @VisibleForTesting
        public T findValueBruteForce(TargetPoint p_204255_) {
            Iterator<Pair<ParameterPoint, T>> $$1 = this.values().iterator();
            Pair<ParameterPoint, T> $$2 = (Pair)$$1.next();
            long $$3 = ((ParameterPoint)$$2.getFirst()).fitness(p_204255_);
            T $$4 = $$2.getSecond();

            while($$1.hasNext()) {
                Pair<ParameterPoint, T> $$5 = (Pair)$$1.next();
                long $$6 = ((ParameterPoint)$$5.getFirst()).fitness(p_204255_);
                if ($$6 < $$3) {
                    $$3 = $$6;
                    $$4 = $$5.getSecond();
                }
            }

            return $$4;
        }

        public T findValueIndex(TargetPoint p_186852_) {
            return this.findValueIndex(p_186852_, RTree.Node::distance);
        }

        protected T findValueIndex(TargetPoint p_186854_, DistanceMetric<T> p_186855_) {
            return this.index.search(p_186854_, p_186855_);
        }
    }

    protected static final class RTree<T> {
        private static final int CHILDREN_PER_NODE = 6;
        private final Node<T> root;
        private final ThreadLocal<Leaf<T>> lastResult = new ThreadLocal();

        private RTree(Node<T> p_186913_) {
            this.root = p_186913_;
        }

        public static <T> RTree<T> create(List<Pair<ParameterPoint, T>> p_186936_) {
            if (p_186936_.isEmpty()) {
                throw new IllegalArgumentException("Need at least one value to build the search tree.");
            } else {
                int $$1 = ((ParameterPoint)((Pair)p_186936_.get(0)).getFirst()).parameterSpace().size();
                if ($$1 != 7) {
                    throw new IllegalStateException("Expecting parameter space to be 7, got " + $$1);
                } else {
                    List<Leaf<T>> $$2 = (List)p_186936_.stream().map((p_186934_) -> {
                        return new Leaf((ParameterPoint)p_186934_.getFirst(), p_186934_.getSecond());
                    }).collect(Collectors.toCollection(ArrayList::new));
                    return new RTree(build($$1, $$2));
                }
            }
        }

        private static <T> Node<T> build(int p_186921_, List<? extends Node<T>> p_186922_) {
            if (p_186922_.isEmpty()) {
                throw new IllegalStateException("Need at least one child to build a node");
            } else if (p_186922_.size() == 1) {
                return (Node)p_186922_.get(0);
            } else if (p_186922_.size() <= 6) {
                p_186922_.sort(Comparator.comparingLong((p_186916_) -> {
                    long $$2 = 0L;

                    for(int $$3 = 0; $$3 < p_186921_; ++$$3) {
                        Parameter $$4 = p_186916_.parameterSpace[$$3];
                        $$2 += Math.abs(($$4.min() + $$4.max()) / 2L);
                    }

                    return $$2;
                }));
                return new SubTree(p_186922_);
            } else {
                long $$2 = Long.MAX_VALUE;
                int $$3 = -1;
                List<SubTree<T>> $$4 = null;

                for(int $$5 = 0; $$5 < p_186921_; ++$$5) {
                    sort(p_186922_, p_186921_, $$5, false);
                    List<SubTree<T>> $$6 = bucketize(p_186922_);
                    long $$7 = 0L;

                    SubTree $$8;
                    for(Iterator var10 = $$6.iterator(); var10.hasNext(); $$7 += cost($$8.parameterSpace)) {
                        $$8 = (SubTree)var10.next();
                    }

                    if ($$2 > $$7) {
                        $$2 = $$7;
                        $$3 = $$5;
                        $$4 = $$6;
                    }
                }

                sort($$4, p_186921_, $$3, true);
                return new SubTree((List)$$4.stream().map((p_186919_) -> {
                    return build(p_186921_, Arrays.asList(p_186919_.children));
                }).collect(Collectors.toList()));
            }
        }

        private static <T> void sort(List<? extends Node<T>> p_186938_, int p_186939_, int p_186940_, boolean p_186941_) {
            Comparator<Node<T>> $$4 = comparator(p_186940_, p_186941_);

            for(int $$5 = 1; $$5 < p_186939_; ++$$5) {
                $$4 = $$4.thenComparing(comparator((p_186940_ + $$5) % p_186939_, p_186941_));
            }

            p_186938_.sort($$4);
        }

        private static <T> Comparator<Node<T>> comparator(int p_186924_, boolean p_186925_) {
            return Comparator.comparingLong((p_186929_) -> {
                Parameter $$3 = p_186929_.parameterSpace[p_186924_];
                long $$4 = ($$3.min() + $$3.max()) / 2L;
                return p_186925_ ? Math.abs($$4) : $$4;
            });
        }

        private static <T> List<SubTree<T>> bucketize(List<? extends Node<T>> p_186945_) {
            List<SubTree<T>> $$1 = Lists.newArrayList();
            List<Node<T>> $$2 = Lists.newArrayList();
            int $$3 = (int)Math.pow(6.0, Math.floor(Math.log((double)p_186945_.size() - 0.01) / Math.log(6.0)));
            Iterator var4 = p_186945_.iterator();

            while(var4.hasNext()) {
                Node<T> $$4 = (Node)var4.next();
                $$2.add($$4);
                if ($$2.size() >= $$3) {
                    $$1.add(new SubTree($$2));
                    $$2 = Lists.newArrayList();
                }
            }

            if (!$$2.isEmpty()) {
                $$1.add(new SubTree($$2));
            }

            return $$1;
        }

        private static long cost(Parameter[] p_186943_) {
            long $$1 = 0L;
            Parameter[] var3 = p_186943_;
            int var4 = p_186943_.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Parameter $$2 = var3[var5];
                $$1 += Math.abs($$2.max() - $$2.min());
            }

            return $$1;
        }

        static <T> List<Parameter> buildParameterSpace(List<? extends Node<T>> p_186947_) {
            if (p_186947_.isEmpty()) {
                throw new IllegalArgumentException("SubTree needs at least one child");
            } else {
                int $$1 = true;
                List<Parameter> $$2 = Lists.newArrayList();

                for(int $$3 = 0; $$3 < 7; ++$$3) {
                    $$2.add((Object)null);
                }

                Iterator var6 = p_186947_.iterator();

                while(var6.hasNext()) {
                    Node<T> $$4 = (Node)var6.next();

                    for(int $$5 = 0; $$5 < 7; ++$$5) {
                        $$2.set($$5, $$4.parameterSpace[$$5].span((Parameter)$$2.get($$5)));
                    }
                }

                return $$2;
            }
        }

        public T search(TargetPoint p_186931_, DistanceMetric<T> p_186932_) {
            long[] $$2 = p_186931_.toParameterArray();
            Leaf<T> $$3 = this.root.search($$2, (Leaf)this.lastResult.get(), p_186932_);
            this.lastResult.set($$3);
            return $$3.value;
        }

        abstract static class Node<T> {
            protected final Parameter[] parameterSpace;

            protected Node(List<Parameter> p_186958_) {
                this.parameterSpace = (Parameter[])p_186958_.toArray(new Parameter[0]);
            }

            protected abstract Leaf<T> search(long[] var1, @Nullable Leaf<T> var2, DistanceMetric<T> var3);

            protected long distance(long[] p_186960_) {
                long $$1 = 0L;

                for(int $$2 = 0; $$2 < 7; ++$$2) {
                    $$1 += Mth.square(this.parameterSpace[$$2].distance(p_186960_[$$2]));
                }

                return $$1;
            }

            public String toString() {
                return Arrays.toString(this.parameterSpace);
            }
        }

        private static final class SubTree<T> extends Node<T> {
            final Node<T>[] children;

            protected SubTree(List<? extends Node<T>> p_186967_) {
                this(net.minecraft.world.level.biome.Climate.RTree.buildParameterSpace(p_186967_), p_186967_);
            }

            protected SubTree(List<Parameter> p_186969_, List<? extends Node<T>> p_186970_) {
                super(p_186969_);
                this.children = (Node[])p_186970_.toArray(new Node[0]);
            }

            protected Leaf<T> search(long[] p_186972_, @Nullable Leaf<T> p_186973_, DistanceMetric<T> p_186974_) {
                long $$3 = p_186973_ == null ? Long.MAX_VALUE : p_186974_.distance(p_186973_, p_186972_);
                Leaf<T> $$4 = p_186973_;
                Node[] var7 = this.children;
                int var8 = var7.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    Node<T> $$5 = var7[var9];
                    long $$6 = p_186974_.distance($$5, p_186972_);
                    if ($$3 > $$6) {
                        Leaf<T> $$7 = $$5.search(p_186972_, $$4, p_186974_);
                        long $$8 = $$5 == $$7 ? $$6 : p_186974_.distance($$7, p_186972_);
                        if ($$3 > $$8) {
                            $$3 = $$8;
                            $$4 = $$7;
                        }
                    }
                }

                return $$4;
            }
        }

        private static final class Leaf<T> extends Node<T> {
            final T value;

            Leaf(ParameterPoint p_186950_, T p_186951_) {
                super(p_186950_.parameterSpace());
                this.value = p_186951_;
            }

            protected Leaf<T> search(long[] p_186953_, @Nullable Leaf<T> p_186954_, DistanceMetric<T> p_186955_) {
                return this;
            }
        }
    }

    interface DistanceMetric<T> {
        long distance(RTree.Node<T> var1, long[] var2);
    }
}
