//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.animation;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public record AnimationChannel(Target target, Keyframe... keyframes) {
    public AnimationChannel(Target target, Keyframe... keyframes) {
        this.target = target;
        this.keyframes = keyframes;
    }

    public Target target() {
        return this.target;
    }

    public Keyframe[] keyframes() {
        return this.keyframes;
    }

    @OnlyIn(Dist.CLIENT)
    public interface Target {
        void apply(ModelPart var1, Vector3f var2);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Interpolations {
        public static final Interpolation LINEAR = (p_253292_, p_253293_, p_253294_, p_253295_, p_253296_, p_253297_) -> {
            Vector3f $$6 = p_253294_[p_253295_].target();
            Vector3f $$7 = p_253294_[p_253296_].target();
            return $$6.lerp($$7, p_253293_, p_253292_).mul(p_253297_);
        };
        public static final Interpolation CATMULLROM = (p_254076_, p_232235_, p_232236_, p_232237_, p_232238_, p_232239_) -> {
            Vector3f $$6 = p_232236_[Math.max(0, p_232237_ - 1)].target();
            Vector3f $$7 = p_232236_[p_232237_].target();
            Vector3f $$8 = p_232236_[p_232238_].target();
            Vector3f $$9 = p_232236_[Math.min(p_232236_.length - 1, p_232238_ + 1)].target();
            p_254076_.set(Mth.catmullrom(p_232235_, $$6.x(), $$7.x(), $$8.x(), $$9.x()) * p_232239_, Mth.catmullrom(p_232235_, $$6.y(), $$7.y(), $$8.y(), $$9.y()) * p_232239_, Mth.catmullrom(p_232235_, $$6.z(), $$7.z(), $$8.z(), $$9.z()) * p_232239_);
            return p_254076_;
        };

        public Interpolations() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Targets {
        public static final Target POSITION = ModelPart::offsetPos;
        public static final Target ROTATION = ModelPart::offsetRotation;
        public static final Target SCALE = ModelPart::offsetScale;

        public Targets() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface Interpolation {
        Vector3f apply(Vector3f var1, float var2, Keyframe[] var3, int var4, int var5, float var6);
    }
}
