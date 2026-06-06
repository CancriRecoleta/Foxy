//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CatRenderer extends MobRenderer<Cat, CatModel<Cat>> {
    public CatRenderer(EntityRendererProvider.Context p_173943_) {
        super(p_173943_, new CatModel(p_173943_.bakeLayer(ModelLayers.CAT)), 0.4F);
        this.addLayer(new CatCollarLayer(this, p_173943_.getModelSet()));
    }

    public ResourceLocation getTextureLocation(Cat p_113950_) {
        return p_113950_.getResourceLocation();
    }

    protected void scale(Cat p_113952_, PoseStack p_113953_, float p_113954_) {
        super.scale(p_113952_, p_113953_, p_113954_);
        p_113953_.scale(0.8F, 0.8F, 0.8F);
    }

    protected void setupRotations(Cat p_113956_, PoseStack p_113957_, float p_113958_, float p_113959_, float p_113960_) {
        super.setupRotations(p_113956_, p_113957_, p_113958_, p_113959_, p_113960_);
        float $$5 = p_113956_.getLieDownAmount(p_113960_);
        if ($$5 > 0.0F) {
            p_113957_.translate(0.4F * $$5, 0.15F * $$5, 0.1F * $$5);
            p_113957_.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp($$5, 0.0F, 90.0F)));
            BlockPos $$6 = p_113956_.blockPosition();
            List<Player> $$7 = p_113956_.level().getEntitiesOfClass(Player.class, (new AABB($$6)).inflate(2.0, 2.0, 2.0));
            Iterator var9 = $$7.iterator();

            while(var9.hasNext()) {
                Player $$8 = (Player)var9.next();
                if ($$8.isSleeping()) {
                    p_113957_.translate(0.15F * $$5, 0.0F, 0.0F);
                    break;
                }
            }
        }

    }
}
