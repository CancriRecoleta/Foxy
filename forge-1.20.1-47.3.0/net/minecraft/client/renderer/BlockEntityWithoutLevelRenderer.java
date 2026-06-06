//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockEntityWithoutLevelRenderer implements ResourceManagerReloadListener {
    private static final ShulkerBoxBlockEntity[] SHULKER_BOXES = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((p_172557_) -> {
        return new ShulkerBoxBlockEntity(p_172557_, BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState());
    }).toArray((p_172553_) -> {
        return new ShulkerBoxBlockEntity[p_172553_];
    });
    private static final ShulkerBoxBlockEntity DEFAULT_SHULKER_BOX;
    private final ChestBlockEntity chest;
    private final ChestBlockEntity trappedChest;
    private final EnderChestBlockEntity enderChest;
    private final BannerBlockEntity banner;
    private final BedBlockEntity bed;
    private final ConduitBlockEntity conduit;
    private final DecoratedPotBlockEntity decoratedPot;
    private ShieldModel shieldModel;
    private TridentModel tridentModel;
    private Map<SkullBlock.Type, SkullModelBase> skullModels;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final EntityModelSet entityModelSet;

    public BlockEntityWithoutLevelRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        this.chest = new ChestBlockEntity(BlockPos.ZERO, Blocks.CHEST.defaultBlockState());
        this.trappedChest = new TrappedChestBlockEntity(BlockPos.ZERO, Blocks.TRAPPED_CHEST.defaultBlockState());
        this.enderChest = new EnderChestBlockEntity(BlockPos.ZERO, Blocks.ENDER_CHEST.defaultBlockState());
        this.banner = new BannerBlockEntity(BlockPos.ZERO, Blocks.WHITE_BANNER.defaultBlockState());
        this.bed = new BedBlockEntity(BlockPos.ZERO, Blocks.RED_BED.defaultBlockState());
        this.conduit = new ConduitBlockEntity(BlockPos.ZERO, Blocks.CONDUIT.defaultBlockState());
        this.decoratedPot = new DecoratedPotBlockEntity(BlockPos.ZERO, Blocks.DECORATED_POT.defaultBlockState());
        this.blockEntityRenderDispatcher = p_172550_;
        this.entityModelSet = p_172551_;
    }

    public void onResourceManagerReload(ResourceManager p_172555_) {
        this.shieldModel = new ShieldModel(this.entityModelSet.bakeLayer(ModelLayers.SHIELD));
        this.tridentModel = new TridentModel(this.entityModelSet.bakeLayer(ModelLayers.TRIDENT));
        this.skullModels = SkullBlockRenderer.createSkullRenderers(this.entityModelSet);
    }

    public void renderByItem(ItemStack p_108830_, ItemDisplayContext p_270899_, PoseStack p_108832_, MultiBufferSource p_108833_, int p_108834_, int p_108835_) {
        Item $$6 = p_108830_.getItem();
        if ($$6 instanceof BlockItem) {
            Block $$7 = ((BlockItem)$$6).getBlock();
            if ($$7 instanceof AbstractSkullBlock) {
                GameProfile $$8 = null;
                if (p_108830_.hasTag()) {
                    CompoundTag $$9 = p_108830_.getTag();
                    if ($$9.contains("SkullOwner", 10)) {
                        $$8 = NbtUtils.readGameProfile($$9.getCompound("SkullOwner"));
                    } else if ($$9.contains("SkullOwner", 8) && !Util.isBlank($$9.getString("SkullOwner"))) {
                        $$8 = new GameProfile((UUID)null, $$9.getString("SkullOwner"));
                        $$9.remove("SkullOwner");
                        SkullBlockEntity.updateGameprofile($$8, (p_172560_) -> {
                            $$9.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), p_172560_));
                        });
                    }
                }

                SkullBlock.Type $$10 = ((AbstractSkullBlock)$$7).getType();
                SkullModelBase $$11 = (SkullModelBase)this.skullModels.get($$10);
                RenderType $$12 = SkullBlockRenderer.getRenderType($$10, $$8);
                SkullBlockRenderer.renderSkull((Direction)null, 180.0F, 0.0F, p_108832_, p_108833_, p_108834_, $$11, $$12);
            } else {
                BlockState $$13 = $$7.defaultBlockState();
                Object $$24;
                if ($$7 instanceof AbstractBannerBlock) {
                    this.banner.fromItem(p_108830_, ((AbstractBannerBlock)$$7).getColor());
                    $$24 = this.banner;
                } else if ($$7 instanceof BedBlock) {
                    this.bed.setColor(((BedBlock)$$7).getColor());
                    $$24 = this.bed;
                } else if ($$13.is(Blocks.CONDUIT)) {
                    $$24 = this.conduit;
                } else if ($$13.is(Blocks.CHEST)) {
                    $$24 = this.chest;
                } else if ($$13.is(Blocks.ENDER_CHEST)) {
                    $$24 = this.enderChest;
                } else if ($$13.is(Blocks.TRAPPED_CHEST)) {
                    $$24 = this.trappedChest;
                } else if ($$13.is(Blocks.DECORATED_POT)) {
                    this.decoratedPot.setFromItem(p_108830_);
                    $$24 = this.decoratedPot;
                } else {
                    if (!($$7 instanceof ShulkerBoxBlock)) {
                        return;
                    }

                    DyeColor $$21 = ShulkerBoxBlock.getColorFromItem($$6);
                    if ($$21 == null) {
                        $$24 = DEFAULT_SHULKER_BOX;
                    } else {
                        $$24 = SHULKER_BOXES[$$21.getId()];
                    }
                }

                this.blockEntityRenderDispatcher.renderItem((BlockEntity)$$24, p_108832_, p_108833_, p_108834_, p_108835_);
            }
        } else {
            if (p_108830_.is(Items.SHIELD)) {
                boolean $$25 = BlockItem.getBlockEntityData(p_108830_) != null;
                p_108832_.pushPose();
                p_108832_.scale(1.0F, -1.0F, -1.0F);
                Material $$26 = $$25 ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
                VertexConsumer $$27 = $$26.sprite().wrap(ItemRenderer.getFoilBufferDirect(p_108833_, this.shieldModel.renderType($$26.atlasLocation()), true, p_108830_.hasFoil()));
                this.shieldModel.handle().render(p_108832_, $$27, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
                if ($$25) {
                    List<Pair<Holder<BannerPattern>, DyeColor>> $$28 = BannerBlockEntity.createPatterns(ShieldItem.getColor(p_108830_), BannerBlockEntity.getItemPatterns(p_108830_));
                    BannerRenderer.renderPatterns(p_108832_, p_108833_, p_108834_, p_108835_, this.shieldModel.plate(), $$26, false, $$28, p_108830_.hasFoil());
                } else {
                    this.shieldModel.plate().render(p_108832_, $$27, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
                }

                p_108832_.popPose();
            } else if (p_108830_.is(Items.TRIDENT)) {
                p_108832_.pushPose();
                p_108832_.scale(1.0F, -1.0F, -1.0F);
                VertexConsumer $$29 = ItemRenderer.getFoilBufferDirect(p_108833_, this.tridentModel.renderType(TridentModel.TEXTURE), false, p_108830_.hasFoil());
                this.tridentModel.renderToBuffer(p_108832_, $$29, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
                p_108832_.popPose();
            }

        }
    }

    static {
        DEFAULT_SHULKER_BOX = new ShulkerBoxBlockEntity(BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState());
    }
}
