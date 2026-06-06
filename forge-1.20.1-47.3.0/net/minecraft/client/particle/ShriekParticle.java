//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ShriekParticle extends TextureSheetParticle {
    private static final Vector3f ROTATION_VECTOR = (new Vector3f(0.5F, 0.5F, 0.5F)).normalize();
    private static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0F, -1.0F, 0.0F);
    private static final float MAGICAL_X_ROT = 1.0472F;
    private int delay;

    ShriekParticle(ClientLevel p_233976_, double p_233977_, double p_233978_, double p_233979_, int p_233980_) {
        super(p_233976_, p_233977_, p_233978_, p_233979_, 0.0, 0.0, 0.0);
        this.quadSize = 0.85F;
        this.delay = p_233980_;
        this.lifetime = 30;
        this.gravity = 0.0F;
        this.xd = 0.0;
        this.yd = 0.1;
        this.zd = 0.0;
    }

    public float getQuadSize(float p_234003_) {
        return this.quadSize * Mth.clamp(((float)this.age + p_234003_) / (float)this.lifetime * 0.75F, 0.0F, 1.0F);
    }

    public void render(VertexConsumer p_233985_, Camera p_233986_, float p_233987_) {
        if (this.delay <= 0) {
            this.alpha = 1.0F - Mth.clamp(((float)this.age + p_233987_) / (float)this.lifetime, 0.0F, 1.0F);
            this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_253347_) -> {
                p_253347_.mul((new Quaternionf()).rotationX(-1.0472F));
            });
            this.renderRotatedParticle(p_233985_, p_233986_, p_233987_, (p_253346_) -> {
                p_253346_.mul((new Quaternionf()).rotationYXZ(-3.1415927F, 1.0472F, 0.0F));
            });
        }
    }

    private void renderRotatedParticle(VertexConsumer p_233989_, Camera p_233990_, float p_233991_, Consumer<Quaternionf> p_233992_) {
        Vec3 $$4 = p_233990_.getPosition();
        float $$5 = (float)(Mth.lerp((double)p_233991_, this.xo, this.x) - $$4.x());
        float $$6 = (float)(Mth.lerp((double)p_233991_, this.yo, this.y) - $$4.y());
        float $$7 = (float)(Mth.lerp((double)p_233991_, this.zo, this.z) - $$4.z());
        Quaternionf $$8 = (new Quaternionf()).setAngleAxis(0.0F, ROTATION_VECTOR.x(), ROTATION_VECTOR.y(), ROTATION_VECTOR.z());
        p_233992_.accept($$8);
        $$8.transform(TRANSFORM_VECTOR);
        Vector3f[] $$9 = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float $$10 = this.getQuadSize(p_233991_);

        int $$13;
        for($$13 = 0; $$13 < 4; ++$$13) {
            Vector3f $$12 = $$9[$$13];
            $$12.rotate($$8);
            $$12.mul($$10);
            $$12.add($$5, $$6, $$7);
        }

        $$13 = this.getLightColor(p_233991_);
        this.makeCornerVertex(p_233989_, $$9[0], this.getU1(), this.getV1(), $$13);
        this.makeCornerVertex(p_233989_, $$9[1], this.getU1(), this.getV0(), $$13);
        this.makeCornerVertex(p_233989_, $$9[2], this.getU0(), this.getV0(), $$13);
        this.makeCornerVertex(p_233989_, $$9[3], this.getU0(), this.getV1(), $$13);
    }

    private void makeCornerVertex(VertexConsumer p_254493_, Vector3f p_253752_, float p_254250_, float p_254047_, int p_253814_) {
        p_254493_.vertex((double)p_253752_.x(), (double)p_253752_.y(), (double)p_253752_.z()).uv(p_254250_, p_254047_).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p_253814_).endVertex();
    }

    public int getLightColor(float p_233983_) {
        return 240;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        if (this.delay > 0) {
            --this.delay;
        } else {
            super.tick();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<ShriekParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_234008_) {
            this.sprite = p_234008_;
        }

        public Particle createParticle(ShriekParticleOption p_234019_, ClientLevel p_234020_, double p_234021_, double p_234022_, double p_234023_, double p_234024_, double p_234025_, double p_234026_) {
            ShriekParticle $$8 = new ShriekParticle(p_234020_, p_234021_, p_234022_, p_234023_, p_234019_.getDelay());
            $$8.pickSprite(this.sprite);
            $$8.setAlpha(1.0F);
            return $$8;
        }
    }
}
