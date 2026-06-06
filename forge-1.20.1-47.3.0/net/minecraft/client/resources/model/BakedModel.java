//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.resources.model;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeBakedModel;

@OnlyIn(Dist.CLIENT)
public interface BakedModel extends IForgeBakedModel {
    /** @deprecated */
    @Deprecated
    List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, RandomSource var3);

    boolean useAmbientOcclusion();

    boolean isGui3d();

    boolean usesBlockLight();

    boolean isCustomRenderer();

    /** @deprecated */
    @Deprecated
    TextureAtlasSprite getParticleIcon();

    /** @deprecated */
    @Deprecated
    default ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    ItemOverrides getOverrides();
}
