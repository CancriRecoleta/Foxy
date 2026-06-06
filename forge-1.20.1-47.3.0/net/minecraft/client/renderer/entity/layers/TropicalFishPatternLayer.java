//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TropicalFishPatternLayer extends RenderLayer<TropicalFish, ColorableHierarchicalModel<TropicalFish>> {
    private static final ResourceLocation KOB_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a_pattern_1.png");
    private static final ResourceLocation SUNSTREAK_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a_pattern_2.png");
    private static final ResourceLocation SNOOPER_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a_pattern_3.png");
    private static final ResourceLocation DASHER_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a_pattern_4.png");
    private static final ResourceLocation BRINELY_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a_pattern_5.png");
    private static final ResourceLocation SPOTTY_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_a_pattern_6.png");
    private static final ResourceLocation FLOPPER_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b_pattern_1.png");
    private static final ResourceLocation STRIPEY_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b_pattern_2.png");
    private static final ResourceLocation GLITTER_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b_pattern_3.png");
    private static final ResourceLocation BLOCKFISH_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b_pattern_4.png");
    private static final ResourceLocation BETTY_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b_pattern_5.png");
    private static final ResourceLocation CLAYFISH_TEXTURE = new ResourceLocation("textures/entity/fish/tropical_b_pattern_6.png");
    private final TropicalFishModelA<TropicalFish> modelA;
    private final TropicalFishModelB<TropicalFish> modelB;

    public TropicalFishPatternLayer(RenderLayerParent<TropicalFish, ColorableHierarchicalModel<TropicalFish>> p_174547_, EntityModelSet p_174548_) {
        super(p_174547_);
        this.modelA = new TropicalFishModelA(p_174548_.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL_PATTERN));
        this.modelB = new TropicalFishModelB(p_174548_.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE_PATTERN));
    }

    public void render(PoseStack p_117612_, MultiBufferSource p_117613_, int p_117614_, TropicalFish p_117615_, float p_117616_, float p_117617_, float p_117618_, float p_117619_, float p_117620_, float p_117621_) {
        TropicalFish.Pattern $$10 = p_117615_.getVariant();
        Object var10000;
        switch ($$10.base()) {
            case SMALL -> var10000 = this.modelA;
            case LARGE -> var10000 = this.modelB;
            default -> throw new IncompatibleClassChangeError();
        }

        EntityModel<TropicalFish> $$11 = var10000;
        ResourceLocation var15;
        switch ($$10) {
            case KOB -> var15 = KOB_TEXTURE;
            case SUNSTREAK -> var15 = SUNSTREAK_TEXTURE;
            case SNOOPER -> var15 = SNOOPER_TEXTURE;
            case DASHER -> var15 = DASHER_TEXTURE;
            case BRINELY -> var15 = BRINELY_TEXTURE;
            case SPOTTY -> var15 = SPOTTY_TEXTURE;
            case FLOPPER -> var15 = FLOPPER_TEXTURE;
            case STRIPEY -> var15 = STRIPEY_TEXTURE;
            case GLITTER -> var15 = GLITTER_TEXTURE;
            case BLOCKFISH -> var15 = BLOCKFISH_TEXTURE;
            case BETTY -> var15 = BETTY_TEXTURE;
            case CLAYFISH -> var15 = CLAYFISH_TEXTURE;
            default -> throw new IncompatibleClassChangeError();
        }

        ResourceLocation $$12 = var15;
        float[] $$13 = p_117615_.getPatternColor().getTextureDiffuseColors();
        coloredCutoutModelCopyLayerRender(this.getParentModel(), (EntityModel)$$11, $$12, p_117612_, p_117613_, p_117614_, p_117615_, p_117616_, p_117617_, p_117619_, p_117620_, p_117621_, p_117618_, $$13[0], $$13[1], $$13[2]);
    }
}
