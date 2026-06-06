//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public abstract class SingleQuadParticle extends Particle {
    protected float quadSize;

    protected SingleQuadParticle(ClientLevel p_107665_, double p_107666_, double p_107667_, double p_107668_) {
        super(p_107665_, p_107666_, p_107667_, p_107668_);
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
    }

    protected SingleQuadParticle(ClientLevel p_107670_, double p_107671_, double p_107672_, double p_107673_, double p_107674_, double p_107675_, double p_107676_) {
        super(p_107670_, p_107671_, p_107672_, p_107673_, p_107674_, p_107675_, p_107676_);
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
    }

    public void render(VertexConsumer p_107678_, Camera p_107679_, float p_107680_) {
        Vec3 $$3 = p_107679_.getPosition();
        float $$4 = (float)(Mth.lerp((double)p_107680_, this.xo, this.x) - $$3.x());
        float $$5 = (float)(Mth.lerp((double)p_107680_, this.yo, this.y) - $$3.y());
        float $$6 = (float)(Mth.lerp((double)p_107680_, this.zo, this.z) - $$3.z());
        Quaternionf $$8;
        if (this.roll == 0.0F) {
            $$8 = p_107679_.rotation();
        } else {
            $$8 = new Quaternionf(p_107679_.rotation());
            $$8.rotateZ(Mth.lerp(p_107680_, this.oRoll, this.roll));
        }

        Vector3f[] $$9 = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float $$10 = this.getQuadSize(p_107680_);

        for(int $$11 = 0; $$11 < 4; ++$$11) {
            Vector3f $$12 = $$9[$$11];
            $$12.rotate($$8);
            $$12.mul($$10);
            $$12.add($$4, $$5, $$6);
        }

        float $$13 = this.getU0();
        float $$14 = this.getU1();
        float $$15 = this.getV0();
        float $$16 = this.getV1();
        int $$17 = this.getLightColor(p_107680_);
        p_107678_.vertex((double)$$9[0].x(), (double)$$9[0].y(), (double)$$9[0].z()).uv($$14, $$16).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$17).endVertex();
        p_107678_.vertex((double)$$9[1].x(), (double)$$9[1].y(), (double)$$9[1].z()).uv($$14, $$15).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$17).endVertex();
        p_107678_.vertex((double)$$9[2].x(), (double)$$9[2].y(), (double)$$9[2].z()).uv($$13, $$15).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$17).endVertex();
        p_107678_.vertex((double)$$9[3].x(), (double)$$9[3].y(), (double)$$9[3].z()).uv($$13, $$16).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2($$17).endVertex();
    }

    public float getQuadSize(float p_107681_) {
        return this.quadSize;
    }

    public Particle scale(float p_107683_) {
        this.quadSize *= p_107683_;
        return super.scale(p_107683_);
    }

    protected abstract float getU0();

    protected abstract float getU1();

    protected abstract float getV0();

    protected abstract float getV1();
}
