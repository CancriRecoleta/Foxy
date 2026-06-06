//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpawnerRenderer implements BlockEntityRenderer<SpawnerBlockEntity> {
    private final EntityRenderDispatcher entityRenderer;

    public SpawnerRenderer(BlockEntityRendererProvider.Context p_173673_) {
        this.entityRenderer = p_173673_.getEntityRenderer();
    }

    public void render(SpawnerBlockEntity p_112563_, float p_112564_, PoseStack p_112565_, MultiBufferSource p_112566_, int p_112567_, int p_112568_) {
        p_112565_.pushPose();
        p_112565_.translate(0.5F, 0.0F, 0.5F);
        BaseSpawner $$6 = p_112563_.getSpawner();
        Entity $$7 = $$6.getOrCreateDisplayEntity(p_112563_.getLevel(), p_112563_.getLevel().getRandom(), p_112563_.getBlockPos());
        if ($$7 != null) {
            float $$8 = 0.53125F;
            float $$9 = Math.max($$7.getBbWidth(), $$7.getBbHeight());
            if ((double)$$9 > 1.0) {
                $$8 /= $$9;
            }

            p_112565_.translate(0.0F, 0.4F, 0.0F);
            p_112565_.mulPose(Axis.YP.rotationDegrees((float)Mth.lerp((double)p_112564_, $$6.getoSpin(), $$6.getSpin()) * 10.0F));
            p_112565_.translate(0.0F, -0.2F, 0.0F);
            p_112565_.mulPose(Axis.XP.rotationDegrees(-30.0F));
            p_112565_.scale($$8, $$8, $$8);
            this.entityRenderer.render($$7, 0.0, 0.0, 0.0, 0.0F, p_112564_, p_112565_, p_112566_, p_112567_);
        }

        p_112565_.popPose();
    }
}
