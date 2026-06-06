//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterDropParticle extends TextureSheetParticle {
    protected WaterDropParticle(ClientLevel p_108484_, double p_108485_, double p_108486_, double p_108487_) {
        super(p_108484_, p_108485_, p_108486_, p_108487_, 0.0, 0.0, 0.0);
        this.xd *= 0.30000001192092896;
        this.yd = Math.random() * 0.20000000298023224 + 0.10000000149011612;
        this.zd *= 0.30000001192092896;
        this.setSize(0.01F, 0.01F);
        this.gravity = 0.06F;
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        } else {
            this.yd -= (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.9800000190734863;
            this.yd *= 0.9800000190734863;
            this.zd *= 0.9800000190734863;
            if (this.onGround) {
                if (Math.random() < 0.5) {
                    this.remove();
                }

                this.xd *= 0.699999988079071;
                this.zd *= 0.699999988079071;
            }

            BlockPos $$0 = BlockPos.containing(this.x, this.y, this.z);
            double $$1 = Math.max(this.level.getBlockState($$0).getCollisionShape(this.level, $$0).max(Axis.Y, this.x - (double)$$0.getX(), this.z - (double)$$0.getZ()), (double)this.level.getFluidState($$0).getHeight(this.level, $$0));
            if ($$1 > 0.0 && this.y < (double)$$0.getY() + $$1) {
                this.remove();
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_108492_) {
            this.sprite = p_108492_;
        }

        public Particle createParticle(SimpleParticleType p_108503_, ClientLevel p_108504_, double p_108505_, double p_108506_, double p_108507_, double p_108508_, double p_108509_, double p_108510_) {
            WaterDropParticle $$8 = new WaterDropParticle(p_108504_, p_108505_, p_108506_, p_108507_);
            $$8.pickSprite(this.sprite);
            return $$8;
        }
    }
}
