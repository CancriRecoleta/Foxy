//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseArmorLayer extends RenderLayer<Horse, HorseModel<Horse>> {
    private final HorseModel<Horse> model;

    public HorseArmorLayer(RenderLayerParent<Horse, HorseModel<Horse>> p_174496_, EntityModelSet p_174497_) {
        super(p_174496_);
        this.model = new HorseModel(p_174497_.bakeLayer(ModelLayers.HORSE_ARMOR));
    }

    public void render(PoseStack p_117032_, MultiBufferSource p_117033_, int p_117034_, Horse p_117035_, float p_117036_, float p_117037_, float p_117038_, float p_117039_, float p_117040_, float p_117041_) {
        ItemStack $$10 = p_117035_.getArmor();
        if ($$10.getItem() instanceof HorseArmorItem) {
            HorseArmorItem $$11 = (HorseArmorItem)$$10.getItem();
            ((HorseModel)this.getParentModel()).copyPropertiesTo(this.model);
            this.model.prepareMobModel((AbstractHorse)p_117035_, p_117036_, p_117037_, p_117038_);
            this.model.setupAnim((AbstractHorse)p_117035_, p_117036_, p_117037_, p_117039_, p_117040_, p_117041_);
            float $$16;
            float $$17;
            float $$18;
            if ($$11 instanceof DyeableHorseArmorItem) {
                int $$12 = ((DyeableHorseArmorItem)$$11).getColor($$10);
                $$16 = (float)($$12 >> 16 & 255) / 255.0F;
                $$17 = (float)($$12 >> 8 & 255) / 255.0F;
                $$18 = (float)($$12 & 255) / 255.0F;
            } else {
                $$16 = 1.0F;
                $$17 = 1.0F;
                $$18 = 1.0F;
            }

            VertexConsumer $$19 = p_117033_.getBuffer(RenderType.entityCutoutNoCull($$11.getTexture()));
            this.model.renderToBuffer(p_117032_, $$19, p_117034_, OverlayTexture.NO_OVERLAY, $$16, $$17, $$18, 1.0F);
        }
    }
}
