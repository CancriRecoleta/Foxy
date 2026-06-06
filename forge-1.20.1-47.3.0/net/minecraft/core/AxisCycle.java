//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core;

import net.minecraft.core.Direction.Axis;

public enum AxisCycle {
    NONE {
        public int cycle(int p_121810_, int p_121811_, int p_121812_, Direction.Axis p_121813_) {
            return p_121813_.choose(p_121810_, p_121811_, p_121812_);
        }

        public double cycle(double p_175242_, double p_175243_, double p_175244_, Direction.Axis p_175245_) {
            return p_175245_.choose(p_175242_, p_175243_, p_175244_);
        }

        public Direction.Axis cycle(Direction.Axis p_121815_) {
            return p_121815_;
        }

        public AxisCycle inverse() {
            return this;
        }
    },
    FORWARD {
        public int cycle(int p_121821_, int p_121822_, int p_121823_, Direction.Axis p_121824_) {
            return p_121824_.choose(p_121823_, p_121821_, p_121822_);
        }

        public double cycle(double p_175247_, double p_175248_, double p_175249_, Direction.Axis p_175250_) {
            return p_175250_.choose(p_175249_, p_175247_, p_175248_);
        }

        public Direction.Axis cycle(Direction.Axis p_121826_) {
            return AXIS_VALUES[Math.floorMod(p_121826_.ordinal() + 1, 3)];
        }

        public AxisCycle inverse() {
            return BACKWARD;
        }
    },
    BACKWARD {
        public int cycle(int p_121832_, int p_121833_, int p_121834_, Direction.Axis p_121835_) {
            return p_121835_.choose(p_121833_, p_121834_, p_121832_);
        }

        public double cycle(double p_175252_, double p_175253_, double p_175254_, Direction.Axis p_175255_) {
            return p_175255_.choose(p_175253_, p_175254_, p_175252_);
        }

        public Direction.Axis cycle(Direction.Axis p_121837_) {
            return AXIS_VALUES[Math.floorMod(p_121837_.ordinal() - 1, 3)];
        }

        public AxisCycle inverse() {
            return FORWARD;
        }
    };

    public static final Direction.Axis[] AXIS_VALUES = Axis.values();
    public static final AxisCycle[] VALUES = values();

    AxisCycle() {
    }

    public abstract int cycle(int var1, int var2, int var3, Direction.Axis var4);

    public abstract double cycle(double var1, double var3, double var5, Direction.Axis var7);

    public abstract Direction.Axis cycle(Direction.Axis var1);

    public abstract AxisCycle inverse();

    public static AxisCycle between(Direction.Axis p_121800_, Direction.Axis p_121801_) {
        return VALUES[Math.floorMod(p_121801_.ordinal() - p_121800_.ordinal(), 3)];
    }
}
