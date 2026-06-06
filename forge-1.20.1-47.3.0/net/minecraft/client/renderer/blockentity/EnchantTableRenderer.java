//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantTableRenderer implements BlockEntityRenderer<EnchantmentTableBlockEntity> {
    public static final Material BOOK_LOCATION;
    private final BookModel bookModel;

    public EnchantTableRenderer(BlockEntityRendererProvider.Context p_173619_) {
        this.bookModel = new BookModel(p_173619_.bakeLayer(ModelLayers.BOOK));
    }

    public void render(EnchantmentTableBlockEntity p_112418_, float p_112419_, PoseStack p_112420_, MultiBufferSource p_112421_, int p_112422_, int p_112423_) {
        p_112420_.pushPose();
        p_112420_.translate(0.5F, 0.75F, 0.5F);
        float $$6 = (float)p_112418_.time + p_112419_;
        p_112420_.translate(0.0F, 0.1F + Mth.sin($$6 * 0.1F) * 0.01F, 0.0F);

        float $$7;
        for($$7 = p_112418_.rot - p_112418_.oRot; $$7 >= 3.1415927F; $$7 -= 6.2831855F) {
        }

        while($$7 < -3.1415927F) {
            $$7 += 6.2831855F;
        }

        float $$8 = p_112418_.oRot + $$7 * p_112419_;
        p_112420_.mulPose(Axis.YP.rotation(-$$8));
        p_112420_.mulPose(Axis.ZP.rotationDegrees(80.0F));
        float $$9 = Mth.lerp(p_112419_, p_112418_.oFlip, p_112418_.flip);
        float $$10 = Mth.frac($$9 + 0.25F) * 1.6F - 0.3F;
        float $$11 = Mth.frac($$9 + 0.75F) * 1.6F - 0.3F;
        float $$12 = Mth.lerp(p_112419_, p_112418_.oOpen, p_112418_.open);
        this.bookModel.setupAnim($$6, Mth.clamp($$10, 0.0F, 1.0F), Mth.clamp($$11, 0.0F, 1.0F), $$12);
        VertexConsumer $$13 = BOOK_LOCATION.buffer(p_112421_, RenderType::entitySolid);
        this.bookModel.render(p_112420_, $$13, p_112422_, p_112423_, 1.0F, 1.0F, 1.0F, 1.0F);
        p_112420_.popPose();
    }

    static {
        BOOK_LOCATION = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/enchanting_table_book"));
    }
}
