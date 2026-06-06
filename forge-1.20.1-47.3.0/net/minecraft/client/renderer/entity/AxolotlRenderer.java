//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.AxolotlModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.axolotl.Axolotl.Variant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AxolotlRenderer extends MobRenderer<Axolotl, AxolotlModel<Axolotl>> {
    private static final Map<Axolotl.Variant, ResourceLocation> TEXTURE_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (p_242076_) -> {
        Axolotl.Variant[] var1 = Variant.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Axolotl.Variant $$1 = var1[var3];
            p_242076_.put($$1, new ResourceLocation(String.format(Locale.ROOT, "textures/entity/axolotl/axolotl_%s.png", $$1.getName())));
        }

    });

    public AxolotlRenderer(EntityRendererProvider.Context p_173921_) {
        super(p_173921_, new AxolotlModel(p_173921_.bakeLayer(ModelLayers.AXOLOTL)), 0.5F);
    }

    public ResourceLocation getTextureLocation(Axolotl p_173925_) {
        return (ResourceLocation)TEXTURE_BY_TYPE.get(p_173925_.getVariant());
    }
}
