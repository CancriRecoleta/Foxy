//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomHeadLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
    private final float scaleX;
    private final float scaleY;
    private final float scaleZ;
    private final Map<SkullBlock.Type, SkullModelBase> skullModels;
    private final ItemInHandRenderer itemInHandRenderer;

    public CustomHeadLayer(RenderLayerParent<T, M> p_234829_, EntityModelSet p_234830_, ItemInHandRenderer p_234831_) {
        this(p_234829_, p_234830_, 1.0F, 1.0F, 1.0F, p_234831_);
    }

    public CustomHeadLayer(RenderLayerParent<T, M> p_234822_, EntityModelSet p_234823_, float p_234824_, float p_234825_, float p_234826_, ItemInHandRenderer p_234827_) {
        super(p_234822_);
        this.scaleX = p_234824_;
        this.scaleY = p_234825_;
        this.scaleZ = p_234826_;
        this.skullModels = SkullBlockRenderer.createSkullRenderers(p_234823_);
        this.itemInHandRenderer = p_234827_;
    }

    public void render(PoseStack p_116731_, MultiBufferSource p_116732_, int p_116733_, T p_116734_, float p_116735_, float p_116736_, float p_116737_, float p_116738_, float p_116739_, float p_116740_) {
        ItemStack $$10 = p_116734_.getItemBySlot(EquipmentSlot.HEAD);
        if (!$$10.isEmpty()) {
            Item $$11 = $$10.getItem();
            p_116731_.pushPose();
            p_116731_.scale(this.scaleX, this.scaleY, this.scaleZ);
            boolean $$12 = p_116734_ instanceof Villager || p_116734_ instanceof ZombieVillager;
            float $$15;
            if (p_116734_.isBaby() && !(p_116734_ instanceof Villager)) {
                float $$13 = 2.0F;
                $$15 = 1.4F;
                p_116731_.translate(0.0F, 0.03125F, 0.0F);
                p_116731_.scale(0.7F, 0.7F, 0.7F);
                p_116731_.translate(0.0F, 1.0F, 0.0F);
            }

            ((HeadedModel)this.getParentModel()).getHead().translateAndRotate(p_116731_);
            if ($$11 instanceof BlockItem && ((BlockItem)$$11).getBlock() instanceof AbstractSkullBlock) {
                $$15 = 1.1875F;
                p_116731_.scale(1.1875F, -1.1875F, -1.1875F);
                if ($$12) {
                    p_116731_.translate(0.0F, 0.0625F, 0.0F);
                }

                GameProfile $$16 = null;
                if ($$10.hasTag()) {
                    CompoundTag $$17 = $$10.getTag();
                    if ($$17.contains("SkullOwner", 10)) {
                        $$16 = NbtUtils.readGameProfile($$17.getCompound("SkullOwner"));
                    }
                }

                p_116731_.translate(-0.5, 0.0, -0.5);
                SkullBlock.Type $$18 = ((AbstractSkullBlock)((BlockItem)$$11).getBlock()).getType();
                SkullModelBase $$19 = (SkullModelBase)this.skullModels.get($$18);
                RenderType $$20 = SkullBlockRenderer.getRenderType($$18, $$16);
                Entity var22 = p_116734_.getVehicle();
                WalkAnimationState $$23;
                if (var22 instanceof LivingEntity) {
                    LivingEntity $$21 = (LivingEntity)var22;
                    $$23 = $$21.walkAnimation;
                } else {
                    $$23 = p_116734_.walkAnimation;
                }

                float $$24 = $$23.position(p_116737_);
                SkullBlockRenderer.renderSkull((Direction)null, 180.0F, $$24, p_116731_, p_116732_, p_116733_, $$19, $$20);
            } else {
                label60: {
                    if ($$11 instanceof ArmorItem) {
                        ArmorItem $$25 = (ArmorItem)$$11;
                        if ($$25.getEquipmentSlot() == EquipmentSlot.HEAD) {
                            break label60;
                        }
                    }

                    translateToHead(p_116731_, $$12);
                    this.itemInHandRenderer.renderItem(p_116734_, $$10, ItemDisplayContext.HEAD, false, p_116731_, p_116732_, p_116733_);
                }
            }

            p_116731_.popPose();
        }
    }

    public static void translateToHead(PoseStack p_174484_, boolean p_174485_) {
        float $$2 = 0.625F;
        p_174484_.translate(0.0F, -0.25F, 0.0F);
        p_174484_.mulPose(Axis.YP.rotationDegrees(180.0F));
        p_174484_.scale(0.625F, -0.625F, -0.625F);
        if (p_174485_) {
            p_174484_.translate(0.0F, 0.1875F, 0.0F);
        }

    }
}
