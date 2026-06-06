//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public enum Direction implements StringRepresentable {
    DOWN(0, 1, -1, "down", net.minecraft.core.Direction.AxisDirection.NEGATIVE, net.minecraft.core.Direction.Axis.Y, new Vec3i(0, -1, 0)),
    UP(1, 0, -1, "up", net.minecraft.core.Direction.AxisDirection.POSITIVE, net.minecraft.core.Direction.Axis.Y, new Vec3i(0, 1, 0)),
    NORTH(2, 3, 2, "north", net.minecraft.core.Direction.AxisDirection.NEGATIVE, net.minecraft.core.Direction.Axis.Z, new Vec3i(0, 0, -1)),
    SOUTH(3, 2, 0, "south", net.minecraft.core.Direction.AxisDirection.POSITIVE, net.minecraft.core.Direction.Axis.Z, new Vec3i(0, 0, 1)),
    WEST(4, 5, 1, "west", net.minecraft.core.Direction.AxisDirection.NEGATIVE, net.minecraft.core.Direction.Axis.X, new Vec3i(-1, 0, 0)),
    EAST(5, 4, 3, "east", net.minecraft.core.Direction.AxisDirection.POSITIVE, net.minecraft.core.Direction.Axis.X, new Vec3i(1, 0, 0));

    public static final StringRepresentable.EnumCodec<Direction> CODEC = StringRepresentable.fromEnum(Direction::values);
    public static final Codec<Direction> VERTICAL_CODEC = ExtraCodecs.validate((Codec)CODEC, Direction::verifyVertical);
    private final int data3d;
    private final int oppositeIndex;
    private final int data2d;
    private final String name;
    private final Axis axis;
    private final AxisDirection axisDirection;
    private final Vec3i normal;
    private static final Direction[] VALUES = values();
    private static final Direction[] BY_3D_DATA = (Direction[])Arrays.stream(VALUES).sorted(Comparator.comparingInt((p_235687_) -> {
        return p_235687_.data3d;
    })).toArray((p_235681_) -> {
        return new Direction[p_235681_];
    });
    private static final Direction[] BY_2D_DATA = (Direction[])Arrays.stream(VALUES).filter((p_235685_) -> {
        return p_235685_.getAxis().isHorizontal();
    }).sorted(Comparator.comparingInt((p_235683_) -> {
        return p_235683_.data2d;
    })).toArray((p_235677_) -> {
        return new Direction[p_235677_];
    });

    private Direction(int p_122356_, int p_122357_, int p_122358_, String p_122359_, AxisDirection p_122360_, Axis p_122361_, Vec3i p_122362_) {
        this.data3d = p_122356_;
        this.data2d = p_122358_;
        this.oppositeIndex = p_122357_;
        this.name = p_122359_;
        this.axis = p_122361_;
        this.axisDirection = p_122360_;
        this.normal = p_122362_;
    }

    public static Direction[] orderedByNearest(Entity p_122383_) {
        float $$1 = p_122383_.getViewXRot(1.0F) * 0.017453292F;
        float $$2 = -p_122383_.getViewYRot(1.0F) * 0.017453292F;
        float $$3 = Mth.sin($$1);
        float $$4 = Mth.cos($$1);
        float $$5 = Mth.sin($$2);
        float $$6 = Mth.cos($$2);
        boolean $$7 = $$5 > 0.0F;
        boolean $$8 = $$3 < 0.0F;
        boolean $$9 = $$6 > 0.0F;
        float $$10 = $$7 ? $$5 : -$$5;
        float $$11 = $$8 ? -$$3 : $$3;
        float $$12 = $$9 ? $$6 : -$$6;
        float $$13 = $$10 * $$4;
        float $$14 = $$12 * $$4;
        Direction $$15 = $$7 ? EAST : WEST;
        Direction $$16 = $$8 ? UP : DOWN;
        Direction $$17 = $$9 ? SOUTH : NORTH;
        if ($$10 > $$12) {
            if ($$11 > $$13) {
                return makeDirectionArray($$16, $$15, $$17);
            } else {
                return $$14 > $$11 ? makeDirectionArray($$15, $$17, $$16) : makeDirectionArray($$15, $$16, $$17);
            }
        } else if ($$11 > $$14) {
            return makeDirectionArray($$16, $$17, $$15);
        } else {
            return $$13 > $$11 ? makeDirectionArray($$17, $$15, $$16) : makeDirectionArray($$17, $$16, $$15);
        }
    }

    private static Direction[] makeDirectionArray(Direction p_122399_, Direction p_122400_, Direction p_122401_) {
        return new Direction[]{p_122399_, p_122400_, p_122401_, p_122401_.getOpposite(), p_122400_.getOpposite(), p_122399_.getOpposite()};
    }

    public static Direction rotate(Matrix4f p_254393_, Direction p_254252_) {
        Vec3i $$2 = p_254252_.getNormal();
        Vector4f $$3 = p_254393_.transform(new Vector4f((float)$$2.getX(), (float)$$2.getY(), (float)$$2.getZ(), 0.0F));
        return getNearest($$3.x(), $$3.y(), $$3.z());
    }

    public static Collection<Direction> allShuffled(RandomSource p_235668_) {
        return Util.shuffledCopy((Object[])values(), p_235668_);
    }

    public static Stream<Direction> stream() {
        return Stream.of(VALUES);
    }

    public Quaternionf getRotation() {
        Quaternionf var10000;
        switch (this) {
            case DOWN -> var10000 = (new Quaternionf()).rotationX(3.1415927F);
            case UP -> var10000 = new Quaternionf();
            case NORTH -> var10000 = (new Quaternionf()).rotationXYZ(1.5707964F, 0.0F, 3.1415927F);
            case SOUTH -> var10000 = (new Quaternionf()).rotationX(1.5707964F);
            case WEST -> var10000 = (new Quaternionf()).rotationXYZ(1.5707964F, 0.0F, 1.5707964F);
            case EAST -> var10000 = (new Quaternionf()).rotationXYZ(1.5707964F, 0.0F, -1.5707964F);
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public int get3DDataValue() {
        return this.data3d;
    }

    public int get2DDataValue() {
        return this.data2d;
    }

    public AxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    public static Direction getFacingAxis(Entity p_175358_, Axis p_175359_) {
        Direction var10000;
        switch (p_175359_) {
            case X -> var10000 = EAST.isFacingAngle(p_175358_.getViewYRot(1.0F)) ? EAST : WEST;
            case Z -> var10000 = SOUTH.isFacingAngle(p_175358_.getViewYRot(1.0F)) ? SOUTH : NORTH;
            case Y -> var10000 = p_175358_.getViewXRot(1.0F) < 0.0F ? UP : DOWN;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public Direction getOpposite() {
        return from3DDataValue(this.oppositeIndex);
    }

    public Direction getClockWise(Axis p_175363_) {
        Direction var10000;
        switch (p_175363_) {
            case X -> var10000 = this != WEST && this != EAST ? this.getClockWiseX() : this;
            case Z -> var10000 = this != NORTH && this != SOUTH ? this.getClockWiseZ() : this;
            case Y -> var10000 = this != UP && this != DOWN ? this.getClockWise() : this;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public Direction getCounterClockWise(Axis p_175365_) {
        Direction var10000;
        switch (p_175365_) {
            case X -> var10000 = this != WEST && this != EAST ? this.getCounterClockWiseX() : this;
            case Z -> var10000 = this != NORTH && this != SOUTH ? this.getCounterClockWiseZ() : this;
            case Y -> var10000 = this != UP && this != DOWN ? this.getCounterClockWise() : this;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public Direction getClockWise() {
        Direction var10000;
        switch (this) {
            case NORTH -> var10000 = EAST;
            case SOUTH -> var10000 = WEST;
            case WEST -> var10000 = NORTH;
            case EAST -> var10000 = SOUTH;
            default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        }

        return var10000;
    }

    private Direction getClockWiseX() {
        Direction var10000;
        switch (this) {
            case DOWN -> var10000 = SOUTH;
            case UP -> var10000 = NORTH;
            case NORTH -> var10000 = DOWN;
            case SOUTH -> var10000 = UP;
            default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        }

        return var10000;
    }

    private Direction getCounterClockWiseX() {
        Direction var10000;
        switch (this) {
            case DOWN -> var10000 = NORTH;
            case UP -> var10000 = SOUTH;
            case NORTH -> var10000 = UP;
            case SOUTH -> var10000 = DOWN;
            default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
        }

        return var10000;
    }

    private Direction getClockWiseZ() {
        Direction var10000;
        switch (this) {
            case DOWN:
                var10000 = WEST;
                break;
            case UP:
                var10000 = EAST;
                break;
            case NORTH:
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            case WEST:
                var10000 = UP;
                break;
            case EAST:
                var10000 = DOWN;
        }

        return var10000;
    }

    private Direction getCounterClockWiseZ() {
        Direction var10000;
        switch (this) {
            case DOWN:
                var10000 = EAST;
                break;
            case UP:
                var10000 = WEST;
                break;
            case NORTH:
            case SOUTH:
            default:
                throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
            case WEST:
                var10000 = DOWN;
                break;
            case EAST:
                var10000 = UP;
        }

        return var10000;
    }

    public Direction getCounterClockWise() {
        Direction var10000;
        switch (this) {
            case NORTH -> var10000 = WEST;
            case SOUTH -> var10000 = EAST;
            case WEST -> var10000 = SOUTH;
            case EAST -> var10000 = NORTH;
            default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
        }

        return var10000;
    }

    public int getStepX() {
        return this.normal.getX();
    }

    public int getStepY() {
        return this.normal.getY();
    }

    public int getStepZ() {
        return this.normal.getZ();
    }

    public Vector3f step() {
        return new Vector3f((float)this.getStepX(), (float)this.getStepY(), (float)this.getStepZ());
    }

    public String getName() {
        return this.name;
    }

    public Axis getAxis() {
        return this.axis;
    }

    @Nullable
    public static Direction byName(@Nullable String p_122403_) {
        return (Direction)CODEC.byName(p_122403_);
    }

    public static Direction from3DDataValue(int p_122377_) {
        return BY_3D_DATA[Mth.abs(p_122377_ % BY_3D_DATA.length)];
    }

    public static Direction from2DDataValue(int p_122408_) {
        return BY_2D_DATA[Mth.abs(p_122408_ % BY_2D_DATA.length)];
    }

    @Nullable
    public static Direction fromDelta(int p_278323_, int p_278296_, int p_278347_) {
        if (p_278323_ == 0) {
            if (p_278296_ == 0) {
                if (p_278347_ > 0) {
                    return SOUTH;
                }

                if (p_278347_ < 0) {
                    return NORTH;
                }
            } else if (p_278347_ == 0) {
                if (p_278296_ > 0) {
                    return UP;
                }

                return DOWN;
            }
        } else if (p_278296_ == 0 && p_278347_ == 0) {
            if (p_278323_ > 0) {
                return EAST;
            }

            return WEST;
        }

        return null;
    }

    public static Direction fromYRot(double p_122365_) {
        return from2DDataValue(Mth.floor(p_122365_ / 90.0 + 0.5) & 3);
    }

    public static Direction fromAxisAndDirection(Axis p_122388_, AxisDirection p_122389_) {
        Direction var10000;
        switch (p_122388_) {
            case X -> var10000 = p_122389_ == net.minecraft.core.Direction.AxisDirection.POSITIVE ? EAST : WEST;
            case Z -> var10000 = p_122389_ == net.minecraft.core.Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
            case Y -> var10000 = p_122389_ == net.minecraft.core.Direction.AxisDirection.POSITIVE ? UP : DOWN;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public float toYRot() {
        return (float)((this.data2d & 3) * 90);
    }

    public static Direction getRandom(RandomSource p_235673_) {
        return (Direction)Util.getRandom((Object[])VALUES, p_235673_);
    }

    public static Direction getNearest(double p_122367_, double p_122368_, double p_122369_) {
        return getNearest((float)p_122367_, (float)p_122368_, (float)p_122369_);
    }

    public static Direction getNearest(float p_122373_, float p_122374_, float p_122375_) {
        Direction $$3 = NORTH;
        float $$4 = Float.MIN_VALUE;
        Direction[] var5 = VALUES;
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Direction $$5 = var5[var7];
            float $$6 = p_122373_ * (float)$$5.normal.getX() + p_122374_ * (float)$$5.normal.getY() + p_122375_ * (float)$$5.normal.getZ();
            if ($$6 > $$4) {
                $$4 = $$6;
                $$3 = $$5;
            }
        }

        return $$3;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }

    private static DataResult<Direction> verifyVertical(Direction p_194529_) {
        return p_194529_.getAxis().isVertical() ? DataResult.success(p_194529_) : DataResult.error(() -> {
            return "Expected a vertical direction";
        });
    }

    public static Direction get(AxisDirection p_122391_, Axis p_122392_) {
        Direction[] var2 = VALUES;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Direction $$2 = var2[var4];
            if ($$2.getAxisDirection() == p_122391_ && $$2.getAxis() == p_122392_) {
                return $$2;
            }
        }

        throw new IllegalArgumentException("No such direction: " + p_122391_ + " " + p_122392_);
    }

    public Vec3i getNormal() {
        return this.normal;
    }

    public boolean isFacingAngle(float p_122371_) {
        float $$1 = p_122371_ * 0.017453292F;
        float $$2 = -Mth.sin($$1);
        float $$3 = Mth.cos($$1);
        return (float)this.normal.getX() * $$2 + (float)this.normal.getZ() * $$3 > 0.0F;
    }

    public static enum Axis implements StringRepresentable, Predicate<Direction> {
        X("x") {
            public int choose(int p_122496_, int p_122497_, int p_122498_) {
                return p_122496_;
            }

            public double choose(double p_122492_, double p_122493_, double p_122494_) {
                return p_122492_;
            }
        },
        Y("y") {
            public int choose(int p_122510_, int p_122511_, int p_122512_) {
                return p_122511_;
            }

            public double choose(double p_122506_, double p_122507_, double p_122508_) {
                return p_122507_;
            }
        },
        Z("z") {
            public int choose(int p_122524_, int p_122525_, int p_122526_) {
                return p_122526_;
            }

            public double choose(double p_122520_, double p_122521_, double p_122522_) {
                return p_122522_;
            }
        };

        public static final Axis[] VALUES = values();
        public static final StringRepresentable.EnumCodec<Axis> CODEC = StringRepresentable.fromEnum(Axis::values);
        private final String name;

        Axis(String p_122456_) {
            this.name = p_122456_;
        }

        @Nullable
        public static Axis byName(String p_122474_) {
            return (Axis)CODEC.byName(p_122474_);
        }

        public String getName() {
            return this.name;
        }

        public boolean isVertical() {
            return this == Y;
        }

        public boolean isHorizontal() {
            return this == X || this == Z;
        }

        public String toString() {
            return this.name;
        }

        public static Axis getRandom(RandomSource p_235689_) {
            return (Axis)Util.getRandom((Object[])VALUES, p_235689_);
        }

        public boolean test(@Nullable Direction p_122472_) {
            return p_122472_ != null && p_122472_.getAxis() == this;
        }

        public Plane getPlane() {
            Plane var10000;
            switch (this) {
                case X:
                case Z:
                    var10000 = net.minecraft.core.Direction.Plane.HORIZONTAL;
                    break;
                case Y:
                    var10000 = net.minecraft.core.Direction.Plane.VERTICAL;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        public String getSerializedName() {
            return this.name;
        }

        public abstract int choose(int var1, int var2, int var3);

        public abstract double choose(double var1, double var3, double var5);
    }

    public static enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int step;
        private final String name;

        private AxisDirection(int p_122538_, String p_122539_) {
            this.step = p_122538_;
            this.name = p_122539_;
        }

        public int getStep() {
            return this.step;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        public AxisDirection opposite() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }

    public static enum Plane implements Iterable<Direction>, Predicate<Direction> {
        HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Axis[]{net.minecraft.core.Direction.Axis.X, net.minecraft.core.Direction.Axis.Z}),
        VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Axis[]{net.minecraft.core.Direction.Axis.Y});

        private final Direction[] faces;
        private final Axis[] axis;

        private Plane(Direction[] p_122555_, Axis[] p_122556_) {
            this.faces = p_122555_;
            this.axis = p_122556_;
        }

        public Direction getRandomDirection(RandomSource p_235691_) {
            return (Direction)Util.getRandom((Object[])this.faces, p_235691_);
        }

        public Axis getRandomAxis(RandomSource p_235693_) {
            return (Axis)Util.getRandom((Object[])this.axis, p_235693_);
        }

        public boolean test(@Nullable Direction p_122559_) {
            return p_122559_ != null && p_122559_.getAxis().getPlane() == this;
        }

        public Iterator<Direction> iterator() {
            return Iterators.forArray(this.faces);
        }

        public Stream<Direction> stream() {
            return Arrays.stream(this.faces);
        }

        public List<Direction> shuffledCopy(RandomSource p_235695_) {
            return Util.shuffledCopy((Object[])this.faces, p_235695_);
        }
    }
}
