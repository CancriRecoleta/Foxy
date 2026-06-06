//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core;

import com.google.common.collect.AbstractIterator;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.Util;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

@Immutable
public class BlockPos extends Vec3i {
    public static final Codec<BlockPos> CODEC;
    private static final Logger LOGGER;
    public static final BlockPos ZERO;
    private static final int PACKED_X_LENGTH;
    private static final int PACKED_Z_LENGTH;
    public static final int PACKED_Y_LENGTH;
    private static final long PACKED_X_MASK;
    private static final long PACKED_Y_MASK;
    private static final long PACKED_Z_MASK;
    private static final int Y_OFFSET = 0;
    private static final int Z_OFFSET;
    private static final int X_OFFSET;

    public BlockPos(int p_121869_, int p_121870_, int p_121871_) {
        super(p_121869_, p_121870_, p_121871_);
    }

    public BlockPos(Vec3i p_121877_) {
        this(p_121877_.getX(), p_121877_.getY(), p_121877_.getZ());
    }

    public static long offset(long p_121916_, Direction p_121917_) {
        return offset(p_121916_, p_121917_.getStepX(), p_121917_.getStepY(), p_121917_.getStepZ());
    }

    public static long offset(long p_121911_, int p_121912_, int p_121913_, int p_121914_) {
        return asLong(getX(p_121911_) + p_121912_, getY(p_121911_) + p_121913_, getZ(p_121911_) + p_121914_);
    }

    public static int getX(long p_121984_) {
        return (int)(p_121984_ << 64 - X_OFFSET - PACKED_X_LENGTH >> 64 - PACKED_X_LENGTH);
    }

    public static int getY(long p_122009_) {
        return (int)(p_122009_ << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
    }

    public static int getZ(long p_122016_) {
        return (int)(p_122016_ << 64 - Z_OFFSET - PACKED_Z_LENGTH >> 64 - PACKED_Z_LENGTH);
    }

    public static BlockPos of(long p_122023_) {
        return new BlockPos(getX(p_122023_), getY(p_122023_), getZ(p_122023_));
    }

    public static BlockPos containing(double p_275310_, double p_275414_, double p_275737_) {
        return new BlockPos(Mth.floor(p_275310_), Mth.floor(p_275414_), Mth.floor(p_275737_));
    }

    public static BlockPos containing(Position p_275443_) {
        return containing(p_275443_.x(), p_275443_.y(), p_275443_.z());
    }

    public long asLong() {
        return asLong(this.getX(), this.getY(), this.getZ());
    }

    public static long asLong(int p_121883_, int p_121884_, int p_121885_) {
        long $$3 = 0L;
        $$3 |= ((long)p_121883_ & PACKED_X_MASK) << X_OFFSET;
        $$3 |= ((long)p_121884_ & PACKED_Y_MASK) << 0;
        $$3 |= ((long)p_121885_ & PACKED_Z_MASK) << Z_OFFSET;
        return $$3;
    }

    public static long getFlatIndex(long p_122028_) {
        return p_122028_ & -16L;
    }

    public BlockPos offset(int p_121973_, int p_121974_, int p_121975_) {
        return p_121973_ == 0 && p_121974_ == 0 && p_121975_ == 0 ? this : new BlockPos(this.getX() + p_121973_, this.getY() + p_121974_, this.getZ() + p_121975_);
    }

    public Vec3 getCenter() {
        return Vec3.atCenterOf(this);
    }

    public BlockPos offset(Vec3i p_121956_) {
        return this.offset(p_121956_.getX(), p_121956_.getY(), p_121956_.getZ());
    }

    public BlockPos subtract(Vec3i p_121997_) {
        return this.offset(-p_121997_.getX(), -p_121997_.getY(), -p_121997_.getZ());
    }

    public BlockPos multiply(int p_175263_) {
        if (p_175263_ == 1) {
            return this;
        } else {
            return p_175263_ == 0 ? ZERO : new BlockPos(this.getX() * p_175263_, this.getY() * p_175263_, this.getZ() * p_175263_);
        }
    }

    public BlockPos above() {
        return this.relative(Direction.UP);
    }

    public BlockPos above(int p_121972_) {
        return this.relative(Direction.UP, p_121972_);
    }

    public BlockPos below() {
        return this.relative(Direction.DOWN);
    }

    public BlockPos below(int p_122000_) {
        return this.relative(Direction.DOWN, p_122000_);
    }

    public BlockPos north() {
        return this.relative(Direction.NORTH);
    }

    public BlockPos north(int p_122014_) {
        return this.relative(Direction.NORTH, p_122014_);
    }

    public BlockPos south() {
        return this.relative(Direction.SOUTH);
    }

    public BlockPos south(int p_122021_) {
        return this.relative(Direction.SOUTH, p_122021_);
    }

    public BlockPos west() {
        return this.relative(Direction.WEST);
    }

    public BlockPos west(int p_122026_) {
        return this.relative(Direction.WEST, p_122026_);
    }

    public BlockPos east() {
        return this.relative(Direction.EAST);
    }

    public BlockPos east(int p_122031_) {
        return this.relative(Direction.EAST, p_122031_);
    }

    public BlockPos relative(Direction p_121946_) {
        return new BlockPos(this.getX() + p_121946_.getStepX(), this.getY() + p_121946_.getStepY(), this.getZ() + p_121946_.getStepZ());
    }

    public BlockPos relative(Direction p_121948_, int p_121949_) {
        return p_121949_ == 0 ? this : new BlockPos(this.getX() + p_121948_.getStepX() * p_121949_, this.getY() + p_121948_.getStepY() * p_121949_, this.getZ() + p_121948_.getStepZ() * p_121949_);
    }

    public BlockPos relative(Direction.Axis p_121943_, int p_121944_) {
        if (p_121944_ == 0) {
            return this;
        } else {
            int $$2 = p_121943_ == Axis.X ? p_121944_ : 0;
            int $$3 = p_121943_ == Axis.Y ? p_121944_ : 0;
            int $$4 = p_121943_ == Axis.Z ? p_121944_ : 0;
            return new BlockPos(this.getX() + $$2, this.getY() + $$3, this.getZ() + $$4);
        }
    }

    public BlockPos rotate(Rotation p_121918_) {
        switch (p_121918_) {
            case NONE:
            default:
                return this;
            case CLOCKWISE_90:
                return new BlockPos(-this.getZ(), this.getY(), this.getX());
            case CLOCKWISE_180:
                return new BlockPos(-this.getX(), this.getY(), -this.getZ());
            case COUNTERCLOCKWISE_90:
                return new BlockPos(this.getZ(), this.getY(), -this.getX());
        }
    }

    public BlockPos cross(Vec3i p_122011_) {
        return new BlockPos(this.getY() * p_122011_.getZ() - this.getZ() * p_122011_.getY(), this.getZ() * p_122011_.getX() - this.getX() * p_122011_.getZ(), this.getX() * p_122011_.getY() - this.getY() * p_122011_.getX());
    }

    public BlockPos atY(int p_175289_) {
        return new BlockPos(this.getX(), p_175289_, this.getZ());
    }

    public BlockPos immutable() {
        return this;
    }

    public MutableBlockPos mutable() {
        return new MutableBlockPos(this.getX(), this.getY(), this.getZ());
    }

    public static Iterable<BlockPos> randomInCube(RandomSource p_235651_, int p_235652_, BlockPos p_235653_, int p_235654_) {
        return randomBetweenClosed(p_235651_, p_235652_, p_235653_.getX() - p_235654_, p_235653_.getY() - p_235654_, p_235653_.getZ() - p_235654_, p_235653_.getX() + p_235654_, p_235653_.getY() + p_235654_, p_235653_.getZ() + p_235654_);
    }

    /** @deprecated */
    @Deprecated
    public static Stream<BlockPos> squareOutSouthEast(BlockPos p_284978_) {
        return Stream.of(p_284978_, p_284978_.south(), p_284978_.east(), p_284978_.south().east());
    }

    public static Iterable<BlockPos> randomBetweenClosed(RandomSource p_235642_, int p_235643_, int p_235644_, int p_235645_, int p_235646_, int p_235647_, int p_235648_, int p_235649_) {
        int $$8 = p_235647_ - p_235644_ + 1;
        int $$9 = p_235648_ - p_235645_ + 1;
        int $$10 = p_235649_ - p_235646_ + 1;
        return () -> {
            return new AbstractIterator<BlockPos>() {
                final MutableBlockPos nextPos = new MutableBlockPos();
                int counter = p_235633_;

                protected BlockPos computeNext() {
                    if (this.counter <= 0) {
                        return (BlockPos)this.endOfData();
                    } else {
                        BlockPos $$0 = this.nextPos.set(p_235634_ + p_235635_.nextInt(p_235636_), p_235637_ + p_235635_.nextInt(p_235638_), p_235639_ + p_235635_.nextInt(p_235640_));
                        --this.counter;
                        return $$0;
                    }
                }
            };
        };
    }

    public static Iterable<BlockPos> withinManhattan(BlockPos p_121926_, int p_121927_, int p_121928_, int p_121929_) {
        int $$4 = p_121927_ + p_121928_ + p_121929_;
        int $$5 = p_121926_.getX();
        int $$6 = p_121926_.getY();
        int $$7 = p_121926_.getZ();
        return () -> {
            return new AbstractIterator<BlockPos>() {
                private final MutableBlockPos cursor = new MutableBlockPos();
                private int currentDepth;
                private int maxX;
                private int maxY;
                private int x;
                private int y;
                private boolean zMirror;

                protected BlockPos computeNext() {
                    if (this.zMirror) {
                        this.zMirror = false;
                        this.cursor.setZ(p_121894_ - (this.cursor.getZ() - p_121894_));
                        return this.cursor;
                    } else {
                        MutableBlockPos $$0;
                        for($$0 = null; $$0 == null; ++this.y) {
                            if (this.y > this.maxY) {
                                ++this.x;
                                if (this.x > this.maxX) {
                                    ++this.currentDepth;
                                    if (this.currentDepth > p_121895_) {
                                        return (BlockPos)this.endOfData();
                                    }

                                    this.maxX = Math.min(p_121896_, this.currentDepth);
                                    this.x = -this.maxX;
                                }

                                this.maxY = Math.min(p_121897_, this.currentDepth - Math.abs(this.x));
                                this.y = -this.maxY;
                            }

                            int $$1 = this.x;
                            int $$2 = this.y;
                            int $$3 = this.currentDepth - Math.abs($$1) - Math.abs($$2);
                            if ($$3 <= p_121898_) {
                                this.zMirror = $$3 != 0;
                                $$0 = this.cursor.set(p_121899_ + $$1, p_121900_ + $$2, p_121894_ + $$3);
                            }
                        }

                        return $$0;
                    }
                }
            };
        };
    }

    public static Optional<BlockPos> findClosestMatch(BlockPos p_121931_, int p_121932_, int p_121933_, Predicate<BlockPos> p_121934_) {
        Iterator var4 = withinManhattan(p_121931_, p_121932_, p_121933_, p_121932_).iterator();

        BlockPos $$4;
        do {
            if (!var4.hasNext()) {
                return Optional.empty();
            }

            $$4 = (BlockPos)var4.next();
        } while(!p_121934_.test($$4));

        return Optional.of($$4);
    }

    public static Stream<BlockPos> withinManhattanStream(BlockPos p_121986_, int p_121987_, int p_121988_, int p_121989_) {
        return StreamSupport.stream(withinManhattan(p_121986_, p_121987_, p_121988_, p_121989_).spliterator(), false);
    }

    public static Iterable<BlockPos> betweenClosed(BlockPos p_121941_, BlockPos p_121942_) {
        return betweenClosed(Math.min(p_121941_.getX(), p_121942_.getX()), Math.min(p_121941_.getY(), p_121942_.getY()), Math.min(p_121941_.getZ(), p_121942_.getZ()), Math.max(p_121941_.getX(), p_121942_.getX()), Math.max(p_121941_.getY(), p_121942_.getY()), Math.max(p_121941_.getZ(), p_121942_.getZ()));
    }

    public static Stream<BlockPos> betweenClosedStream(BlockPos p_121991_, BlockPos p_121992_) {
        return StreamSupport.stream(betweenClosed(p_121991_, p_121992_).spliterator(), false);
    }

    public static Stream<BlockPos> betweenClosedStream(BoundingBox p_121920_) {
        return betweenClosedStream(Math.min(p_121920_.minX(), p_121920_.maxX()), Math.min(p_121920_.minY(), p_121920_.maxY()), Math.min(p_121920_.minZ(), p_121920_.maxZ()), Math.max(p_121920_.minX(), p_121920_.maxX()), Math.max(p_121920_.minY(), p_121920_.maxY()), Math.max(p_121920_.minZ(), p_121920_.maxZ()));
    }

    public static Stream<BlockPos> betweenClosedStream(AABB p_121922_) {
        return betweenClosedStream(Mth.floor(p_121922_.minX), Mth.floor(p_121922_.minY), Mth.floor(p_121922_.minZ), Mth.floor(p_121922_.maxX), Mth.floor(p_121922_.maxY), Mth.floor(p_121922_.maxZ));
    }

    public static Stream<BlockPos> betweenClosedStream(int p_121887_, int p_121888_, int p_121889_, int p_121890_, int p_121891_, int p_121892_) {
        return StreamSupport.stream(betweenClosed(p_121887_, p_121888_, p_121889_, p_121890_, p_121891_, p_121892_).spliterator(), false);
    }

    public static Iterable<BlockPos> betweenClosed(int p_121977_, int p_121978_, int p_121979_, int p_121980_, int p_121981_, int p_121982_) {
        int $$6 = p_121980_ - p_121977_ + 1;
        int $$7 = p_121981_ - p_121978_ + 1;
        int $$8 = p_121982_ - p_121979_ + 1;
        int $$9 = $$6 * $$7 * $$8;
        return () -> {
            return new AbstractIterator<BlockPos>() {
                private final MutableBlockPos cursor = new MutableBlockPos();
                private int index;

                protected BlockPos computeNext() {
                    if (this.index == p_122002_) {
                        return (BlockPos)this.endOfData();
                    } else {
                        int $$0 = this.index % p_122003_;
                        int $$1 = this.index / p_122003_;
                        int $$2 = $$1 % p_122004_;
                        int $$3 = $$1 / p_122004_;
                        ++this.index;
                        return this.cursor.set(p_122005_ + $$0, p_122006_ + $$2, p_122007_ + $$3);
                    }
                }
            };
        };
    }

    public static Iterable<MutableBlockPos> spiralAround(BlockPos p_121936_, int p_121937_, Direction p_121938_, Direction p_121939_) {
        Validate.validState(p_121938_.getAxis() != p_121939_.getAxis(), "The two directions cannot be on the same axis", new Object[0]);
        return () -> {
            return new AbstractIterator<MutableBlockPos>() {
                private final Direction[] directions = new Direction[]{p_121951_, p_121952_, p_121951_.getOpposite(), p_121952_.getOpposite()};
                private final MutableBlockPos cursor = p_121953_.mutable().move(p_121952_);
                private final int legs = 4 * p_121954_;
                private int leg = -1;
                private int legSize;
                private int legIndex;
                private int lastX;
                private int lastY;
                private int lastZ;

                {
                    this.lastX = this.cursor.getX();
                    this.lastY = this.cursor.getY();
                    this.lastZ = this.cursor.getZ();
                }

                protected MutableBlockPos computeNext() {
                    this.cursor.set(this.lastX, this.lastY, this.lastZ).move(this.directions[(this.leg + 4) % 4]);
                    this.lastX = this.cursor.getX();
                    this.lastY = this.cursor.getY();
                    this.lastZ = this.cursor.getZ();
                    if (this.legIndex >= this.legSize) {
                        if (this.leg >= this.legs) {
                            return (MutableBlockPos)this.endOfData();
                        }

                        ++this.leg;
                        this.legIndex = 0;
                        this.legSize = this.leg / 2 + 1;
                    }

                    ++this.legIndex;
                    return this.cursor;
                }
            };
        };
    }

    public static int breadthFirstTraversal(BlockPos p_278078_, int p_277385_, int p_277666_, BiConsumer<BlockPos, Consumer<BlockPos>> p_277755_, Predicate<BlockPos> p_278094_) {
        Queue<Pair<BlockPos, Integer>> $$5 = new ArrayDeque();
        LongSet $$6 = new LongOpenHashSet();
        $$5.add(Pair.of(p_278078_, 0));
        int $$7 = 0;

        while(!$$5.isEmpty()) {
            Pair<BlockPos, Integer> $$8 = (Pair)$$5.poll();
            BlockPos $$9 = (BlockPos)$$8.getLeft();
            int $$10 = (Integer)$$8.getRight();
            long $$11 = $$9.asLong();
            if ($$6.add($$11) && p_278094_.test($$9)) {
                ++$$7;
                if ($$7 >= p_277666_) {
                    return $$7;
                }

                if ($$10 < p_277385_) {
                    p_277755_.accept($$9, (p_277234_) -> {
                        $$5.add(Pair.of(p_277234_, $$10 + 1));
                    });
                }
            }
        }

        return $$7;
    }

    static {
        CODEC = Codec.INT_STREAM.comapFlatMap((p_121967_) -> {
            return Util.fixedSize((IntStream)p_121967_, 3).map((p_175270_) -> {
                return new BlockPos(p_175270_[0], p_175270_[1], p_175270_[2]);
            });
        }, (p_121924_) -> {
            return IntStream.of(new int[]{p_121924_.getX(), p_121924_.getY(), p_121924_.getZ()});
        }).stable();
        LOGGER = LogUtils.getLogger();
        ZERO = new BlockPos(0, 0, 0);
        PACKED_X_LENGTH = 1 + Mth.log2(Mth.smallestEncompassingPowerOfTwo(30000000));
        PACKED_Z_LENGTH = PACKED_X_LENGTH;
        PACKED_Y_LENGTH = 64 - PACKED_X_LENGTH - PACKED_Z_LENGTH;
        PACKED_X_MASK = (1L << PACKED_X_LENGTH) - 1L;
        PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;
        PACKED_Z_MASK = (1L << PACKED_Z_LENGTH) - 1L;
        Z_OFFSET = PACKED_Y_LENGTH;
        X_OFFSET = PACKED_Y_LENGTH + PACKED_Z_LENGTH;
    }

    public static class MutableBlockPos extends BlockPos {
        public MutableBlockPos() {
            this(0, 0, 0);
        }

        public MutableBlockPos(int p_122130_, int p_122131_, int p_122132_) {
            super(p_122130_, p_122131_, p_122132_);
        }

        public MutableBlockPos(double p_122126_, double p_122127_, double p_122128_) {
            this(Mth.floor(p_122126_), Mth.floor(p_122127_), Mth.floor(p_122128_));
        }

        public BlockPos offset(int p_122163_, int p_122164_, int p_122165_) {
            return super.offset(p_122163_, p_122164_, p_122165_).immutable();
        }

        public BlockPos multiply(int p_175305_) {
            return super.multiply(p_175305_).immutable();
        }

        public BlockPos relative(Direction p_122152_, int p_122153_) {
            return super.relative(p_122152_, p_122153_).immutable();
        }

        public BlockPos relative(Direction.Axis p_122145_, int p_122146_) {
            return super.relative(p_122145_, p_122146_).immutable();
        }

        public BlockPos rotate(Rotation p_122138_) {
            return super.rotate(p_122138_).immutable();
        }

        public MutableBlockPos set(int p_122179_, int p_122180_, int p_122181_) {
            this.setX(p_122179_);
            this.setY(p_122180_);
            this.setZ(p_122181_);
            return this;
        }

        public MutableBlockPos set(double p_122170_, double p_122171_, double p_122172_) {
            return this.set(Mth.floor(p_122170_), Mth.floor(p_122171_), Mth.floor(p_122172_));
        }

        public MutableBlockPos set(Vec3i p_122191_) {
            return this.set(p_122191_.getX(), p_122191_.getY(), p_122191_.getZ());
        }

        public MutableBlockPos set(long p_122189_) {
            return this.set(getX(p_122189_), getY(p_122189_), getZ(p_122189_));
        }

        public MutableBlockPos set(AxisCycle p_122140_, int p_122141_, int p_122142_, int p_122143_) {
            return this.set(p_122140_.cycle(p_122141_, p_122142_, p_122143_, Axis.X), p_122140_.cycle(p_122141_, p_122142_, p_122143_, Axis.Y), p_122140_.cycle(p_122141_, p_122142_, p_122143_, Axis.Z));
        }

        public MutableBlockPos setWithOffset(Vec3i p_122160_, Direction p_122161_) {
            return this.set(p_122160_.getX() + p_122161_.getStepX(), p_122160_.getY() + p_122161_.getStepY(), p_122160_.getZ() + p_122161_.getStepZ());
        }

        public MutableBlockPos setWithOffset(Vec3i p_122155_, int p_122156_, int p_122157_, int p_122158_) {
            return this.set(p_122155_.getX() + p_122156_, p_122155_.getY() + p_122157_, p_122155_.getZ() + p_122158_);
        }

        public MutableBlockPos setWithOffset(Vec3i p_175307_, Vec3i p_175308_) {
            return this.set(p_175307_.getX() + p_175308_.getX(), p_175307_.getY() + p_175308_.getY(), p_175307_.getZ() + p_175308_.getZ());
        }

        public MutableBlockPos move(Direction p_122174_) {
            return this.move(p_122174_, 1);
        }

        public MutableBlockPos move(Direction p_122176_, int p_122177_) {
            return this.set(this.getX() + p_122176_.getStepX() * p_122177_, this.getY() + p_122176_.getStepY() * p_122177_, this.getZ() + p_122176_.getStepZ() * p_122177_);
        }

        public MutableBlockPos move(int p_122185_, int p_122186_, int p_122187_) {
            return this.set(this.getX() + p_122185_, this.getY() + p_122186_, this.getZ() + p_122187_);
        }

        public MutableBlockPos move(Vec3i p_122194_) {
            return this.set(this.getX() + p_122194_.getX(), this.getY() + p_122194_.getY(), this.getZ() + p_122194_.getZ());
        }

        public MutableBlockPos clamp(Direction.Axis p_122148_, int p_122149_, int p_122150_) {
            switch (p_122148_) {
                case X -> return this.set(Mth.clamp(this.getX(), p_122149_, p_122150_), this.getY(), this.getZ());
                case Y -> return this.set(this.getX(), Mth.clamp(this.getY(), p_122149_, p_122150_), this.getZ());
                case Z -> return this.set(this.getX(), this.getY(), Mth.clamp(this.getZ(), p_122149_, p_122150_));
                default -> throw new IllegalStateException("Unable to clamp axis " + p_122148_);
            }
        }

        public MutableBlockPos setX(int p_175341_) {
            super.setX(p_175341_);
            return this;
        }

        public MutableBlockPos setY(int p_175343_) {
            super.setY(p_175343_);
            return this;
        }

        public MutableBlockPos setZ(int p_175345_) {
            super.setZ(p_175345_);
            return this;
        }

        public BlockPos immutable() {
            return new BlockPos(this);
        }
    }
}
