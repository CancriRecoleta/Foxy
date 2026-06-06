//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleUtils {
    public ParticleUtils() {
    }

    public static void spawnParticlesOnBlockFaces(Level p_216314_, BlockPos p_216315_, ParticleOptions p_216316_, IntProvider p_216317_) {
        Direction[] var4 = Direction.values();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Direction $$4 = var4[var6];
            spawnParticlesOnBlockFace(p_216314_, p_216315_, p_216316_, p_216317_, $$4, () -> {
                return getRandomSpeedRanges(p_216314_.random);
            }, 0.55);
        }

    }

    public static void spawnParticlesOnBlockFace(Level p_216319_, BlockPos p_216320_, ParticleOptions p_216321_, IntProvider p_216322_, Direction p_216323_, Supplier<Vec3> p_216324_, double p_216325_) {
        int $$7 = p_216322_.sample(p_216319_.random);

        for(int $$8 = 0; $$8 < $$7; ++$$8) {
            spawnParticleOnFace(p_216319_, p_216320_, p_216323_, p_216321_, (Vec3)p_216324_.get(), p_216325_);
        }

    }

    private static Vec3 getRandomSpeedRanges(RandomSource p_216303_) {
        return new Vec3(Mth.nextDouble(p_216303_, -0.5, 0.5), Mth.nextDouble(p_216303_, -0.5, 0.5), Mth.nextDouble(p_216303_, -0.5, 0.5));
    }

    public static void spawnParticlesAlongAxis(Direction.Axis p_144968_, Level p_144969_, BlockPos p_144970_, double p_144971_, ParticleOptions p_144972_, UniformInt p_144973_) {
        Vec3 $$6 = Vec3.atCenterOf(p_144970_);
        boolean $$7 = p_144968_ == Axis.X;
        boolean $$8 = p_144968_ == Axis.Y;
        boolean $$9 = p_144968_ == Axis.Z;
        int $$10 = p_144973_.sample(p_144969_.random);

        for(int $$11 = 0; $$11 < $$10; ++$$11) {
            double $$12 = $$6.x + Mth.nextDouble(p_144969_.random, -1.0, 1.0) * ($$7 ? 0.5 : p_144971_);
            double $$13 = $$6.y + Mth.nextDouble(p_144969_.random, -1.0, 1.0) * ($$8 ? 0.5 : p_144971_);
            double $$14 = $$6.z + Mth.nextDouble(p_144969_.random, -1.0, 1.0) * ($$9 ? 0.5 : p_144971_);
            double $$15 = $$7 ? Mth.nextDouble(p_144969_.random, -1.0, 1.0) : 0.0;
            double $$16 = $$8 ? Mth.nextDouble(p_144969_.random, -1.0, 1.0) : 0.0;
            double $$17 = $$9 ? Mth.nextDouble(p_144969_.random, -1.0, 1.0) : 0.0;
            p_144969_.addParticle(p_144972_, $$12, $$13, $$14, $$15, $$16, $$17);
        }

    }

    public static void spawnParticleOnFace(Level p_216307_, BlockPos p_216308_, Direction p_216309_, ParticleOptions p_216310_, Vec3 p_216311_, double p_216312_) {
        Vec3 $$6 = Vec3.atCenterOf(p_216308_);
        int $$7 = p_216309_.getStepX();
        int $$8 = p_216309_.getStepY();
        int $$9 = p_216309_.getStepZ();
        double $$10 = $$6.x + ($$7 == 0 ? Mth.nextDouble(p_216307_.random, -0.5, 0.5) : (double)$$7 * p_216312_);
        double $$11 = $$6.y + ($$8 == 0 ? Mth.nextDouble(p_216307_.random, -0.5, 0.5) : (double)$$8 * p_216312_);
        double $$12 = $$6.z + ($$9 == 0 ? Mth.nextDouble(p_216307_.random, -0.5, 0.5) : (double)$$9 * p_216312_);
        double $$13 = $$7 == 0 ? p_216311_.x() : 0.0;
        double $$14 = $$8 == 0 ? p_216311_.y() : 0.0;
        double $$15 = $$9 == 0 ? p_216311_.z() : 0.0;
        p_216307_.addParticle(p_216310_, $$10, $$11, $$12, $$13, $$14, $$15);
    }

    public static void spawnParticleBelow(Level p_273159_, BlockPos p_273452_, RandomSource p_273538_, ParticleOptions p_273419_) {
        double $$4 = (double)p_273452_.getX() + p_273538_.nextDouble();
        double $$5 = (double)p_273452_.getY() - 0.05;
        double $$6 = (double)p_273452_.getZ() + p_273538_.nextDouble();
        p_273159_.addParticle(p_273419_, $$4, $$5, $$6, 0.0, 0.0, 0.0);
    }
}
