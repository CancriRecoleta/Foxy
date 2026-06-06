//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MobAppearanceParticle extends Particle {
    private final Model model;
    private final RenderType renderType;

    MobAppearanceParticle(ClientLevel p_107114_, double p_107115_, double p_107116_, double p_107117_) {
        super(p_107114_, p_107115_, p_107116_, p_107117_);
        this.renderType = RenderType.entityTranslucent(ElderGuardianRenderer.GUARDIAN_ELDER_LOCATION);
        this.model = new GuardianModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELDER_GUARDIAN));
        this.gravity = 0.0F;
        this.lifetime = 30;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public void render(VertexConsumer p_107125_, Camera p_107126_, float p_107127_) {
        float $$3 = ((float)this.age + p_107127_) / (float)this.lifetime;
        float $$4 = 0.05F + 0.5F * Mth.sin($$3 * 3.1415927F);
        PoseStack $$5 = new PoseStack();
        $$5.mulPose(p_107126_.rotation());
        $$5.mulPose(Axis.XP.rotationDegrees(150.0F * $$3 - 60.0F));
        $$5.scale(-1.0F, -1.0F, 1.0F);
        $$5.translate(0.0F, -1.101F, 1.5F);
        MultiBufferSource.BufferSource $$6 = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer $$7 = $$6.getBuffer(this.renderType);
        this.model.renderToBuffer($$5, $$7, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, $$4);
        $$6.endBatch();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Provider() {
        }

        public Particle createParticle(SimpleParticleType p_107140_, ClientLevel p_107141_, double p_107142_, double p_107143_, double p_107144_, double p_107145_, double p_107146_, double p_107147_) {
            return new MobAppearanceParticle(p_107141_, p_107142_, p_107143_, p_107144_);
        }
    }
}
