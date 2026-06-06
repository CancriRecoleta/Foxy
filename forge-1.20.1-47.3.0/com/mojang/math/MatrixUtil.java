//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.math;

import org.apache.commons.lang3.tuple.Triple;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MatrixUtil {
    private static final float G = 3.0F + 2.0F * Math.sqrt(2.0F);
    private static final GivensParameters PI_4 = GivensParameters.fromPositiveAngle(0.7853982F);

    private MatrixUtil() {
    }

    public static Matrix4f mulComponentWise(Matrix4f p_254173_, float p_253864_) {
        return p_254173_.set(p_254173_.m00() * p_253864_, p_254173_.m01() * p_253864_, p_254173_.m02() * p_253864_, p_254173_.m03() * p_253864_, p_254173_.m10() * p_253864_, p_254173_.m11() * p_253864_, p_254173_.m12() * p_253864_, p_254173_.m13() * p_253864_, p_254173_.m20() * p_253864_, p_254173_.m21() * p_253864_, p_254173_.m22() * p_253864_, p_254173_.m23() * p_253864_, p_254173_.m30() * p_253864_, p_254173_.m31() * p_253864_, p_254173_.m32() * p_253864_, p_254173_.m33() * p_253864_);
    }

    private static GivensParameters approxGivensQuat(float p_276275_, float p_276276_, float p_276282_) {
        float $$3 = 2.0F * (p_276275_ - p_276282_);
        float $$4 = p_276276_;
        return G * $$4 * $$4 < $$3 * $$3 ? GivensParameters.fromUnnormalized($$4, $$3) : PI_4;
    }

    private static GivensParameters qrGivensQuat(float p_253897_, float p_254413_) {
        float $$2 = (float)java.lang.Math.hypot((double)p_253897_, (double)p_254413_);
        float $$3 = $$2 > 1.0E-6F ? p_254413_ : 0.0F;
        float $$4 = Math.abs(p_253897_) + Math.max($$2, 1.0E-6F);
        if (p_253897_ < 0.0F) {
            float $$5 = $$3;
            $$3 = $$4;
            $$4 = $$5;
        }

        return GivensParameters.fromUnnormalized($$3, $$4);
    }

    private static void similarityTransform(Matrix3f p_276319_, Matrix3f p_276263_) {
        p_276319_.mul(p_276263_);
        p_276263_.transpose();
        p_276263_.mul(p_276319_);
        p_276319_.set(p_276263_);
    }

    private static void stepJacobi(Matrix3f p_276262_, Matrix3f p_276279_, Quaternionf p_276314_, Quaternionf p_276299_) {
        GivensParameters $$8;
        Quaternionf $$9;
        if (p_276262_.m01 * p_276262_.m01 + p_276262_.m10 * p_276262_.m10 > 1.0E-6F) {
            $$8 = approxGivensQuat(p_276262_.m00, 0.5F * (p_276262_.m01 + p_276262_.m10), p_276262_.m11);
            $$9 = $$8.aroundZ(p_276314_);
            p_276299_.mul($$9);
            $$8.aroundZ(p_276279_);
            similarityTransform(p_276262_, p_276279_);
        }

        if (p_276262_.m02 * p_276262_.m02 + p_276262_.m20 * p_276262_.m20 > 1.0E-6F) {
            $$8 = approxGivensQuat(p_276262_.m00, 0.5F * (p_276262_.m02 + p_276262_.m20), p_276262_.m22).inverse();
            $$9 = $$8.aroundY(p_276314_);
            p_276299_.mul($$9);
            $$8.aroundY(p_276279_);
            similarityTransform(p_276262_, p_276279_);
        }

        if (p_276262_.m12 * p_276262_.m12 + p_276262_.m21 * p_276262_.m21 > 1.0E-6F) {
            $$8 = approxGivensQuat(p_276262_.m11, 0.5F * (p_276262_.m12 + p_276262_.m21), p_276262_.m22);
            $$9 = $$8.aroundX(p_276314_);
            p_276299_.mul($$9);
            $$8.aroundX(p_276279_);
            similarityTransform(p_276262_, p_276279_);
        }

    }

    public static Quaternionf eigenvalueJacobi(Matrix3f p_276278_, int p_276269_) {
        Quaternionf $$2 = new Quaternionf();
        Matrix3f $$3 = new Matrix3f();
        Quaternionf $$4 = new Quaternionf();

        for(int $$5 = 0; $$5 < p_276269_; ++$$5) {
            stepJacobi(p_276278_, $$3, $$4, $$2);
        }

        $$2.normalize();
        return $$2;
    }

    public static Triple<Quaternionf, Vector3f, Quaternionf> svdDecompose(Matrix3f p_253947_) {
        Matrix3f $$1 = new Matrix3f(p_253947_);
        $$1.transpose();
        $$1.mul(p_253947_);
        Quaternionf $$2 = eigenvalueJacobi($$1, 5);
        float $$3 = $$1.m00;
        float $$4 = $$1.m11;
        boolean $$5 = (double)$$3 < 1.0E-6;
        boolean $$6 = (double)$$4 < 1.0E-6;
        Matrix3f $$7 = $$1;
        Matrix3f $$8 = p_253947_.rotate($$2);
        Quaternionf $$9 = new Quaternionf();
        Quaternionf $$10 = new Quaternionf();
        GivensParameters $$12;
        if ($$5) {
            $$12 = qrGivensQuat($$8.m11, -$$8.m10);
        } else {
            $$12 = qrGivensQuat($$8.m00, $$8.m01);
        }

        Quaternionf $$13 = $$12.aroundZ($$10);
        Matrix3f $$14 = $$12.aroundZ($$7);
        $$9.mul($$13);
        $$14.transpose().mul($$8);
        $$7 = $$8;
        if ($$5) {
            $$12 = qrGivensQuat($$14.m22, -$$14.m20);
        } else {
            $$12 = qrGivensQuat($$14.m00, $$14.m02);
        }

        $$12 = $$12.inverse();
        Quaternionf $$15 = $$12.aroundY($$10);
        Matrix3f $$16 = $$12.aroundY($$7);
        $$9.mul($$15);
        $$16.transpose().mul($$14);
        $$7 = $$14;
        if ($$6) {
            $$12 = qrGivensQuat($$16.m22, -$$16.m21);
        } else {
            $$12 = qrGivensQuat($$16.m11, $$16.m12);
        }

        Quaternionf $$17 = $$12.aroundX($$10);
        Matrix3f $$18 = $$12.aroundX($$7);
        $$9.mul($$17);
        $$18.transpose().mul($$16);
        Vector3f $$19 = new Vector3f($$18.m00, $$18.m11, $$18.m22);
        return Triple.of($$9, $$19, $$2.conjugate());
    }
}
