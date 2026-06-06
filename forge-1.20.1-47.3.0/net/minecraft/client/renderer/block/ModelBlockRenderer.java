//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class ModelBlockRenderer {
    private static final int FACE_CUBIC = 0;
    private static final int FACE_PARTIAL = 1;
    static final Direction[] DIRECTIONS = Direction.values();
    private final BlockColors blockColors;
    private static final int CACHE_SIZE = 100;
    static final ThreadLocal<Cache> CACHE = ThreadLocal.withInitial(Cache::new);

    public ModelBlockRenderer(BlockColors p_110999_) {
        this.blockColors = p_110999_;
    }

    /** @deprecated */
    @Deprecated
    public void tesselateBlock(BlockAndTintGetter p_234380_, BakedModel p_234381_, BlockState p_234382_, BlockPos p_234383_, PoseStack p_234384_, VertexConsumer p_234385_, boolean p_234386_, RandomSource p_234387_, long p_234388_, int p_234389_) {
        this.tesselateBlock(p_234380_, p_234381_, p_234382_, p_234383_, p_234384_, p_234385_, p_234386_, p_234387_, p_234388_, p_234389_, ModelData.EMPTY, (RenderType)null);
    }

    public void tesselateBlock(BlockAndTintGetter p_234380_, BakedModel p_234381_, BlockState p_234382_, BlockPos p_234383_, PoseStack p_234384_, VertexConsumer p_234385_, boolean p_234386_, RandomSource p_234387_, long p_234388_, int p_234389_, ModelData modelData, RenderType renderType) {
        boolean flag = Minecraft.useAmbientOcclusion() && p_234382_.getLightEmission(p_234380_, p_234383_) == 0 && p_234381_.useAmbientOcclusion(p_234382_, renderType);
        Vec3 vec3 = p_234382_.getOffset(p_234380_, p_234383_);
        p_234384_.translate(vec3.x, vec3.y, vec3.z);

        try {
            if (flag) {
                this.tesselateWithAO(p_234380_, p_234381_, p_234382_, p_234383_, p_234384_, p_234385_, p_234386_, p_234387_, p_234388_, p_234389_, modelData, renderType);
            } else {
                this.tesselateWithoutAO(p_234380_, p_234381_, p_234382_, p_234383_, p_234384_, p_234385_, p_234386_, p_234387_, p_234388_, p_234389_, modelData, renderType);
            }

        } catch (Throwable var19) {
            Throwable throwable = var19;
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating block model");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block model being tesselated");
            CrashReportCategory.populateBlockDetails(crashreportcategory, p_234380_, p_234383_, p_234382_);
            crashreportcategory.setDetail("Using AO", (Object)flag);
            throw new ReportedException(crashreport);
        }
    }

    /** @deprecated */
    @Deprecated
    public void tesselateWithAO(BlockAndTintGetter p_234391_, BakedModel p_234392_, BlockState p_234393_, BlockPos p_234394_, PoseStack p_234395_, VertexConsumer p_234396_, boolean p_234397_, RandomSource p_234398_, long p_234399_, int p_234400_) {
        this.tesselateWithAO(p_234391_, p_234392_, p_234393_, p_234394_, p_234395_, p_234396_, p_234397_, p_234398_, p_234399_, p_234400_, ModelData.EMPTY, (RenderType)null);
    }

    public void tesselateWithAO(BlockAndTintGetter p_111079_, BakedModel p_111080_, BlockState p_111081_, BlockPos p_111082_, PoseStack p_111083_, VertexConsumer p_111084_, boolean p_111085_, RandomSource p_111086_, long p_111087_, int p_111088_, ModelData modelData, RenderType renderType) {
        float[] afloat = new float[DIRECTIONS.length * 2];
        BitSet bitset = new BitSet(3);
        AmbientOcclusionFace modelblockrenderer$ambientocclusionface = new AmbientOcclusionFace();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = p_111082_.mutable();
        Direction[] var18 = DIRECTIONS;
        int var19 = var18.length;

        for(int var20 = 0; var20 < var19; ++var20) {
            Direction direction = var18[var20];
            p_111086_.setSeed(p_111087_);
            List<BakedQuad> list = p_111080_.getQuads(p_111081_, direction, p_111086_, modelData, renderType);
            if (!list.isEmpty()) {
                blockpos$mutableblockpos.setWithOffset(p_111082_, (Direction)direction);
                if (!p_111085_ || Block.shouldRenderFace(p_111081_, p_111079_, p_111082_, direction, blockpos$mutableblockpos)) {
                    this.renderModelFaceAO(p_111079_, p_111081_, p_111082_, p_111083_, p_111084_, list, afloat, bitset, modelblockrenderer$ambientocclusionface, p_111088_);
                }
            }
        }

        p_111086_.setSeed(p_111087_);
        List<BakedQuad> list1 = p_111080_.getQuads(p_111081_, (Direction)null, p_111086_, modelData, renderType);
        if (!list1.isEmpty()) {
            this.renderModelFaceAO(p_111079_, p_111081_, p_111082_, p_111083_, p_111084_, list1, afloat, bitset, modelblockrenderer$ambientocclusionface, p_111088_);
        }

    }

    /** @deprecated */
    @Deprecated
    public void tesselateWithoutAO(BlockAndTintGetter p_234402_, BakedModel p_234403_, BlockState p_234404_, BlockPos p_234405_, PoseStack p_234406_, VertexConsumer p_234407_, boolean p_234408_, RandomSource p_234409_, long p_234410_, int p_234411_) {
        this.tesselateWithoutAO(p_234402_, p_234403_, p_234404_, p_234405_, p_234406_, p_234407_, p_234408_, p_234409_, p_234410_, p_234411_, ModelData.EMPTY, (RenderType)null);
    }

    public void tesselateWithoutAO(BlockAndTintGetter p_111091_, BakedModel p_111092_, BlockState p_111093_, BlockPos p_111094_, PoseStack p_111095_, VertexConsumer p_111096_, boolean p_111097_, RandomSource p_111098_, long p_111099_, int p_111100_, ModelData modelData, RenderType renderType) {
        BitSet bitset = new BitSet(3);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = p_111094_.mutable();
        Direction[] var16 = DIRECTIONS;
        int var17 = var16.length;

        for(int var18 = 0; var18 < var17; ++var18) {
            Direction direction = var16[var18];
            p_111098_.setSeed(p_111099_);
            List<BakedQuad> list = p_111092_.getQuads(p_111093_, direction, p_111098_, modelData, renderType);
            if (!list.isEmpty()) {
                blockpos$mutableblockpos.setWithOffset(p_111094_, (Direction)direction);
                if (!p_111097_ || Block.shouldRenderFace(p_111093_, p_111091_, p_111094_, direction, blockpos$mutableblockpos)) {
                    int i = LevelRenderer.getLightColor(p_111091_, p_111093_, blockpos$mutableblockpos);
                    this.renderModelFaceFlat(p_111091_, p_111093_, p_111094_, i, p_111100_, false, p_111095_, p_111096_, list, bitset);
                }
            }
        }

        p_111098_.setSeed(p_111099_);
        List<BakedQuad> list1 = p_111092_.getQuads(p_111093_, (Direction)null, p_111098_, modelData, renderType);
        if (!list1.isEmpty()) {
            this.renderModelFaceFlat(p_111091_, p_111093_, p_111094_, -1, p_111100_, true, p_111095_, p_111096_, list1, bitset);
        }

    }

    private void renderModelFaceAO(BlockAndTintGetter p_111013_, BlockState p_111014_, BlockPos p_111015_, PoseStack p_111016_, VertexConsumer p_111017_, List<BakedQuad> p_111018_, float[] p_111019_, BitSet p_111020_, AmbientOcclusionFace p_111021_, int p_111022_) {
        BakedQuad bakedquad;
        for(Iterator var11 = p_111018_.iterator(); var11.hasNext(); this.putQuadData(p_111013_, p_111014_, p_111015_, p_111017_, p_111016_.last(), bakedquad, p_111021_.brightness[0], p_111021_.brightness[1], p_111021_.brightness[2], p_111021_.brightness[3], p_111021_.lightmap[0], p_111021_.lightmap[1], p_111021_.lightmap[2], p_111021_.lightmap[3], p_111022_)) {
            bakedquad = (BakedQuad)var11.next();
            this.calculateShape(p_111013_, p_111014_, p_111015_, bakedquad.getVertices(), bakedquad.getDirection(), p_111019_, p_111020_);
            if (!ForgeHooksClient.calculateFaceWithoutAO(p_111013_, p_111014_, p_111015_, bakedquad, p_111020_.get(0), p_111021_.brightness, p_111021_.lightmap)) {
                p_111021_.calculate(p_111013_, p_111014_, p_111015_, bakedquad.getDirection(), p_111019_, p_111020_, bakedquad.isShade());
            }
        }

    }

    private void putQuadData(BlockAndTintGetter p_111024_, BlockState p_111025_, BlockPos p_111026_, VertexConsumer p_111027_, PoseStack.Pose p_111028_, BakedQuad p_111029_, float p_111030_, float p_111031_, float p_111032_, float p_111033_, int p_111034_, int p_111035_, int p_111036_, int p_111037_, int p_111038_) {
        float f;
        float f1;
        float f2;
        if (p_111029_.isTinted()) {
            int i = this.blockColors.getColor(p_111025_, p_111024_, p_111026_, p_111029_.getTintIndex());
            f = (float)(i >> 16 & 255) / 255.0F;
            f1 = (float)(i >> 8 & 255) / 255.0F;
            f2 = (float)(i & 255) / 255.0F;
        } else {
            f = 1.0F;
            f1 = 1.0F;
            f2 = 1.0F;
        }

        p_111027_.putBulkData(p_111028_, p_111029_, new float[]{p_111030_, p_111031_, p_111032_, p_111033_}, f, f1, f2, new int[]{p_111034_, p_111035_, p_111036_, p_111037_}, p_111038_, true);
    }

    private void calculateShape(BlockAndTintGetter p_111040_, BlockState p_111041_, BlockPos p_111042_, int[] p_111043_, Direction p_111044_, @Nullable float[] p_111045_, BitSet p_111046_) {
        float f = 32.0F;
        float f1 = 32.0F;
        float f2 = 32.0F;
        float f3 = -32.0F;
        float f4 = -32.0F;
        float f5 = -32.0F;

        int j;
        float f10;
        for(j = 0; j < 4; ++j) {
            f10 = Float.intBitsToFloat(p_111043_[j * 8]);
            float f7 = Float.intBitsToFloat(p_111043_[j * 8 + 1]);
            float f8 = Float.intBitsToFloat(p_111043_[j * 8 + 2]);
            f = Math.min(f, f10);
            f1 = Math.min(f1, f7);
            f2 = Math.min(f2, f8);
            f3 = Math.max(f3, f10);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
        }

        if (p_111045_ != null) {
            p_111045_[Direction.WEST.get3DDataValue()] = f;
            p_111045_[Direction.EAST.get3DDataValue()] = f3;
            p_111045_[Direction.DOWN.get3DDataValue()] = f1;
            p_111045_[Direction.UP.get3DDataValue()] = f4;
            p_111045_[Direction.NORTH.get3DDataValue()] = f2;
            p_111045_[Direction.SOUTH.get3DDataValue()] = f5;
            j = DIRECTIONS.length;
            p_111045_[Direction.WEST.get3DDataValue() + j] = 1.0F - f;
            p_111045_[Direction.EAST.get3DDataValue() + j] = 1.0F - f3;
            p_111045_[Direction.DOWN.get3DDataValue() + j] = 1.0F - f1;
            p_111045_[Direction.UP.get3DDataValue() + j] = 1.0F - f4;
            p_111045_[Direction.NORTH.get3DDataValue() + j] = 1.0F - f2;
            p_111045_[Direction.SOUTH.get3DDataValue() + j] = 1.0F - f5;
        }

        float f9 = 1.0E-4F;
        f10 = 0.9999F;
        switch (p_111044_) {
            case DOWN:
                p_111046_.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                p_111046_.set(0, f1 == f4 && (f1 < 1.0E-4F || p_111041_.isCollisionShapeFullBlock(p_111040_, p_111042_)));
                break;
            case UP:
                p_111046_.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                p_111046_.set(0, f1 == f4 && (f4 > 0.9999F || p_111041_.isCollisionShapeFullBlock(p_111040_, p_111042_)));
                break;
            case NORTH:
                p_111046_.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                p_111046_.set(0, f2 == f5 && (f2 < 1.0E-4F || p_111041_.isCollisionShapeFullBlock(p_111040_, p_111042_)));
                break;
            case SOUTH:
                p_111046_.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                p_111046_.set(0, f2 == f5 && (f5 > 0.9999F || p_111041_.isCollisionShapeFullBlock(p_111040_, p_111042_)));
                break;
            case WEST:
                p_111046_.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                p_111046_.set(0, f == f3 && (f < 1.0E-4F || p_111041_.isCollisionShapeFullBlock(p_111040_, p_111042_)));
                break;
            case EAST:
                p_111046_.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                p_111046_.set(0, f == f3 && (f3 > 0.9999F || p_111041_.isCollisionShapeFullBlock(p_111040_, p_111042_)));
        }

    }

    private void renderModelFaceFlat(BlockAndTintGetter p_111002_, BlockState p_111003_, BlockPos p_111004_, int p_111005_, int p_111006_, boolean p_111007_, PoseStack p_111008_, VertexConsumer p_111009_, List<BakedQuad> p_111010_, BitSet p_111011_) {
        Iterator var11 = p_111010_.iterator();

        while(var11.hasNext()) {
            BakedQuad bakedquad = (BakedQuad)var11.next();
            if (p_111007_) {
                this.calculateShape(p_111002_, p_111003_, p_111004_, bakedquad.getVertices(), bakedquad.getDirection(), (float[])null, p_111011_);
                BlockPos blockpos = p_111011_.get(0) ? p_111004_.relative(bakedquad.getDirection()) : p_111004_;
                p_111005_ = LevelRenderer.getLightColor(p_111002_, p_111003_, blockpos);
            }

            float f = p_111002_.getShade(bakedquad.getDirection(), bakedquad.isShade());
            this.putQuadData(p_111002_, p_111003_, p_111004_, p_111009_, p_111008_.last(), bakedquad, f, f, f, f, p_111005_, p_111005_, p_111005_, p_111005_, p_111006_);
        }

    }

    /** @deprecated */
    @Deprecated
    public void renderModel(PoseStack.Pose p_111068_, VertexConsumer p_111069_, @Nullable BlockState p_111070_, BakedModel p_111071_, float p_111072_, float p_111073_, float p_111074_, int p_111075_, int p_111076_) {
        this.renderModel(p_111068_, p_111069_, p_111070_, p_111071_, p_111072_, p_111073_, p_111074_, p_111075_, p_111076_, ModelData.EMPTY, (RenderType)null);
    }

    public void renderModel(PoseStack.Pose p_111068_, VertexConsumer p_111069_, @Nullable BlockState p_111070_, BakedModel p_111071_, float p_111072_, float p_111073_, float p_111074_, int p_111075_, int p_111076_, ModelData modelData, RenderType renderType) {
        RandomSource randomsource = RandomSource.create();
        long i = 42L;
        Direction[] var15 = DIRECTIONS;
        int var16 = var15.length;

        for(int var17 = 0; var17 < var16; ++var17) {
            Direction direction = var15[var17];
            randomsource.setSeed(42L);
            renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, p_111071_.getQuads(p_111070_, direction, randomsource, modelData, renderType), p_111075_, p_111076_);
        }

        randomsource.setSeed(42L);
        renderQuadList(p_111068_, p_111069_, p_111072_, p_111073_, p_111074_, p_111071_.getQuads(p_111070_, (Direction)null, randomsource, modelData, renderType), p_111075_, p_111076_);
    }

    private static void renderQuadList(PoseStack.Pose p_111059_, VertexConsumer p_111060_, float p_111061_, float p_111062_, float p_111063_, List<BakedQuad> p_111064_, int p_111065_, int p_111066_) {
        BakedQuad bakedquad;
        float f;
        float f1;
        float f2;
        for(Iterator var8 = p_111064_.iterator(); var8.hasNext(); p_111060_.putBulkData(p_111059_, bakedquad, f, f1, f2, p_111065_, p_111066_)) {
            bakedquad = (BakedQuad)var8.next();
            if (bakedquad.isTinted()) {
                f = Mth.clamp(p_111061_, 0.0F, 1.0F);
                f1 = Mth.clamp(p_111062_, 0.0F, 1.0F);
                f2 = Mth.clamp(p_111063_, 0.0F, 1.0F);
            } else {
                f = 1.0F;
                f1 = 1.0F;
                f2 = 1.0F;
            }
        }

    }

    public static void enableCaching() {
        ((Cache)CACHE.get()).enable();
    }

    public static void clearCache() {
        ((Cache)CACHE.get()).disable();
    }

    @OnlyIn(Dist.CLIENT)
    static class AmbientOcclusionFace {
        final float[] brightness = new float[4];
        final int[] lightmap = new int[4];

        public AmbientOcclusionFace() {
        }

        public void calculate(BlockAndTintGetter p_111168_, BlockState p_111169_, BlockPos p_111170_, Direction p_111171_, float[] p_111172_, BitSet p_111173_, boolean p_111174_) {
            BlockPos blockpos = p_111173_.get(0) ? p_111170_.relative(p_111171_) : p_111170_;
            AdjacencyInfo modelblockrenderer$adjacencyinfo = net.minecraft.client.renderer.block.ModelBlockRenderer.AdjacencyInfo.fromFacing(p_111171_);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            Cache modelblockrenderer$cache = (Cache)ModelBlockRenderer.CACHE.get();
            blockpos$mutableblockpos.setWithOffset(blockpos, (Direction)modelblockrenderer$adjacencyinfo.corners[0]);
            BlockState blockstate = p_111168_.getBlockState(blockpos$mutableblockpos);
            int i = modelblockrenderer$cache.getLightColor(blockstate, p_111168_, blockpos$mutableblockpos);
            float f = modelblockrenderer$cache.getShadeBrightness(blockstate, p_111168_, blockpos$mutableblockpos);
            blockpos$mutableblockpos.setWithOffset(blockpos, (Direction)modelblockrenderer$adjacencyinfo.corners[1]);
            BlockState blockstate1 = p_111168_.getBlockState(blockpos$mutableblockpos);
            int j = modelblockrenderer$cache.getLightColor(blockstate1, p_111168_, blockpos$mutableblockpos);
            float f1 = modelblockrenderer$cache.getShadeBrightness(blockstate1, p_111168_, blockpos$mutableblockpos);
            blockpos$mutableblockpos.setWithOffset(blockpos, (Direction)modelblockrenderer$adjacencyinfo.corners[2]);
            BlockState blockstate2 = p_111168_.getBlockState(blockpos$mutableblockpos);
            int k = modelblockrenderer$cache.getLightColor(blockstate2, p_111168_, blockpos$mutableblockpos);
            float f2 = modelblockrenderer$cache.getShadeBrightness(blockstate2, p_111168_, blockpos$mutableblockpos);
            blockpos$mutableblockpos.setWithOffset(blockpos, (Direction)modelblockrenderer$adjacencyinfo.corners[3]);
            BlockState blockstate3 = p_111168_.getBlockState(blockpos$mutableblockpos);
            int l = modelblockrenderer$cache.getLightColor(blockstate3, p_111168_, blockpos$mutableblockpos);
            float f3 = modelblockrenderer$cache.getShadeBrightness(blockstate3, p_111168_, blockpos$mutableblockpos);
            BlockState blockstate4 = p_111168_.getBlockState(blockpos$mutableblockpos.setWithOffset(blockpos, (Direction)modelblockrenderer$adjacencyinfo.corners[0]).move(p_111171_));
            boolean flag = !blockstate4.isViewBlocking(p_111168_, blockpos$mutableblockpos) || blockstate4.getLightBlock(p_111168_, blockpos$mutableblockpos) == 0;
            BlockState blockstate5 = p_111168_.getBlockState(blockpos$mutableblockpos.setWithOffset(blockpos, (Direction)modelblockrenderer$adjacencyinfo.corners[1]).move(p_111171_));
            boolean flag1 = !blockstate5.isViewBlocking(p_111168_, blockpos$mutableblockpos) || blockstate5.getLightBlock(p_111168_, blockpos$mutableblockpos) == 0;
            BlockState blockstate6 = p_111168_.getBlockState(blockpos$mutableblockpos.setWithOffset(blockpos, (Direction)modelblockrenderer$adjacencyinfo.corners[2]).move(p_111171_));
            boolean flag2 = !blockstate6.isViewBlocking(p_111168_, blockpos$mutableblockpos) || blockstate6.getLightBlock(p_111168_, blockpos$mutableblockpos) == 0;
            BlockState blockstate7 = p_111168_.getBlockState(blockpos$mutableblockpos.setWithOffset(blockpos, (Direction)modelblockrenderer$adjacencyinfo.corners[3]).move(p_111171_));
            boolean flag3 = !blockstate7.isViewBlocking(p_111168_, blockpos$mutableblockpos) || blockstate7.getLightBlock(p_111168_, blockpos$mutableblockpos) == 0;
            float f4;
            int i1;
            if (!flag2 && !flag) {
                f4 = f;
                i1 = i;
            } else {
                blockpos$mutableblockpos.setWithOffset(blockpos, (Direction)modelblockrenderer$adjacencyinfo.corners[0]).move(modelblockrenderer$adjacencyinfo.corners[2]);
                BlockState blockstate8 = p_111168_.getBlockState(blockpos$mutableblockpos);
                f4 = modelblockrenderer$cache.getShadeBrightness(blockstate8, p_111168_, blockpos$mutableblockpos);
                i1 = modelblockrenderer$cache.getLightColor(blockstate8, p_111168_, blockpos$mutableblockpos);
            }

            int j1;
            float f5;
            if (!flag3 && !flag) {
                f5 = f;
                j1 = i;
            } else {
                blockpos$mutableblockpos.setWithOffset(blockpos, (Direction)modelblockrenderer$adjacencyinfo.corners[0]).move(modelblockrenderer$adjacencyinfo.corners[3]);
                BlockState blockstate10 = p_111168_.getBlockState(blockpos$mutableblockpos);
                f5 = modelblockrenderer$cache.getShadeBrightness(blockstate10, p_111168_, blockpos$mutableblockpos);
                j1 = modelblockrenderer$cache.getLightColor(blockstate10, p_111168_, blockpos$mutableblockpos);
            }

            int k1;
            float f6;
            if (!flag2 && !flag1) {
                f6 = f;
                k1 = i;
            } else {
                blockpos$mutableblockpos.setWithOffset(blockpos, (Direction)modelblockrenderer$adjacencyinfo.corners[1]).move(modelblockrenderer$adjacencyinfo.corners[2]);
                BlockState blockstate11 = p_111168_.getBlockState(blockpos$mutableblockpos);
                f6 = modelblockrenderer$cache.getShadeBrightness(blockstate11, p_111168_, blockpos$mutableblockpos);
                k1 = modelblockrenderer$cache.getLightColor(blockstate11, p_111168_, blockpos$mutableblockpos);
            }

            int l1;
            float f7;
            if (!flag3 && !flag1) {
                f7 = f;
                l1 = i;
            } else {
                blockpos$mutableblockpos.setWithOffset(blockpos, (Direction)modelblockrenderer$adjacencyinfo.corners[1]).move(modelblockrenderer$adjacencyinfo.corners[3]);
                BlockState blockstate12 = p_111168_.getBlockState(blockpos$mutableblockpos);
                f7 = modelblockrenderer$cache.getShadeBrightness(blockstate12, p_111168_, blockpos$mutableblockpos);
                l1 = modelblockrenderer$cache.getLightColor(blockstate12, p_111168_, blockpos$mutableblockpos);
            }

            int i3 = modelblockrenderer$cache.getLightColor(p_111169_, p_111168_, p_111170_);
            blockpos$mutableblockpos.setWithOffset(p_111170_, (Direction)p_111171_);
            BlockState blockstate9 = p_111168_.getBlockState(blockpos$mutableblockpos);
            if (p_111173_.get(0) || !blockstate9.isSolidRender(p_111168_, blockpos$mutableblockpos)) {
                i3 = modelblockrenderer$cache.getLightColor(blockstate9, p_111168_, blockpos$mutableblockpos);
            }

            float f8 = p_111173_.get(0) ? modelblockrenderer$cache.getShadeBrightness(p_111168_.getBlockState(blockpos), p_111168_, blockpos) : modelblockrenderer$cache.getShadeBrightness(p_111168_.getBlockState(p_111170_), p_111168_, p_111170_);
            AmbientVertexRemap modelblockrenderer$ambientvertexremap = net.minecraft.client.renderer.block.ModelBlockRenderer.AmbientVertexRemap.fromFacing(p_111171_);
            float f30;
            float f10;
            float f11;
            float f12;
            if (p_111173_.get(1) && modelblockrenderer$adjacencyinfo.doNonCubicWeight) {
                f30 = (f3 + f + f5 + f8) * 0.25F;
                f10 = (f2 + f + f4 + f8) * 0.25F;
                f11 = (f2 + f1 + f6 + f8) * 0.25F;
                f12 = (f3 + f1 + f7 + f8) * 0.25F;
                float f13 = p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[0].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[1].shape];
                float f14 = p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[2].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[3].shape];
                float f15 = p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[4].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[5].shape];
                float f16 = p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[6].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert0Weights[7].shape];
                float f17 = p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[0].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[1].shape];
                float f18 = p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[2].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[3].shape];
                float f19 = p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[4].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[5].shape];
                float f20 = p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[6].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert1Weights[7].shape];
                float f21 = p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[0].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[1].shape];
                float f22 = p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[2].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[3].shape];
                float f23 = p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[4].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[5].shape];
                float f24 = p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[6].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert2Weights[7].shape];
                float f25 = p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[0].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[1].shape];
                float f26 = p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[2].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[3].shape];
                float f27 = p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[4].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[5].shape];
                float f28 = p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[6].shape] * p_111172_[modelblockrenderer$adjacencyinfo.vert3Weights[7].shape];
                this.brightness[modelblockrenderer$ambientvertexremap.vert0] = f30 * f13 + f10 * f14 + f11 * f15 + f12 * f16;
                this.brightness[modelblockrenderer$ambientvertexremap.vert1] = f30 * f17 + f10 * f18 + f11 * f19 + f12 * f20;
                this.brightness[modelblockrenderer$ambientvertexremap.vert2] = f30 * f21 + f10 * f22 + f11 * f23 + f12 * f24;
                this.brightness[modelblockrenderer$ambientvertexremap.vert3] = f30 * f25 + f10 * f26 + f11 * f27 + f12 * f28;
                int i2 = this.blend(l, i, j1, i3);
                int j2 = this.blend(k, i, i1, i3);
                int k2 = this.blend(k, j, k1, i3);
                int l2 = this.blend(l, j, l1, i3);
                this.lightmap[modelblockrenderer$ambientvertexremap.vert0] = this.blend(i2, j2, k2, l2, f13, f14, f15, f16);
                this.lightmap[modelblockrenderer$ambientvertexremap.vert1] = this.blend(i2, j2, k2, l2, f17, f18, f19, f20);
                this.lightmap[modelblockrenderer$ambientvertexremap.vert2] = this.blend(i2, j2, k2, l2, f21, f22, f23, f24);
                this.lightmap[modelblockrenderer$ambientvertexremap.vert3] = this.blend(i2, j2, k2, l2, f25, f26, f27, f28);
            } else {
                f30 = (f3 + f + f5 + f8) * 0.25F;
                f10 = (f2 + f + f4 + f8) * 0.25F;
                f11 = (f2 + f1 + f6 + f8) * 0.25F;
                f12 = (f3 + f1 + f7 + f8) * 0.25F;
                this.lightmap[modelblockrenderer$ambientvertexremap.vert0] = this.blend(l, i, j1, i3);
                this.lightmap[modelblockrenderer$ambientvertexremap.vert1] = this.blend(k, i, i1, i3);
                this.lightmap[modelblockrenderer$ambientvertexremap.vert2] = this.blend(k, j, k1, i3);
                this.lightmap[modelblockrenderer$ambientvertexremap.vert3] = this.blend(l, j, l1, i3);
                this.brightness[modelblockrenderer$ambientvertexremap.vert0] = f30;
                this.brightness[modelblockrenderer$ambientvertexremap.vert1] = f10;
                this.brightness[modelblockrenderer$ambientvertexremap.vert2] = f11;
                this.brightness[modelblockrenderer$ambientvertexremap.vert3] = f12;
            }

            f30 = p_111168_.getShade(p_111171_, p_111174_);

            for(int j3 = 0; j3 < this.brightness.length; ++j3) {
                float[] var10000 = this.brightness;
                var10000[j3] *= f30;
            }

        }

        private int blend(int p_111154_, int p_111155_, int p_111156_, int p_111157_) {
            if (p_111154_ == 0) {
                p_111154_ = p_111157_;
            }

            if (p_111155_ == 0) {
                p_111155_ = p_111157_;
            }

            if (p_111156_ == 0) {
                p_111156_ = p_111157_;
            }

            return p_111154_ + p_111155_ + p_111156_ + p_111157_ >> 2 & 16711935;
        }

        private int blend(int p_111159_, int p_111160_, int p_111161_, int p_111162_, float p_111163_, float p_111164_, float p_111165_, float p_111166_) {
            int i = (int)((float)(p_111159_ >> 16 & 255) * p_111163_ + (float)(p_111160_ >> 16 & 255) * p_111164_ + (float)(p_111161_ >> 16 & 255) * p_111165_ + (float)(p_111162_ >> 16 & 255) * p_111166_) & 255;
            int j = (int)((float)(p_111159_ & 255) * p_111163_ + (float)(p_111160_ & 255) * p_111164_ + (float)(p_111161_ & 255) * p_111165_ + (float)(p_111162_ & 255) * p_111166_) & 255;
            return i << 16 | j;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class Cache {
        private boolean enabled;
        private final Long2IntLinkedOpenHashMap colorCache = (Long2IntLinkedOpenHashMap)Util.make(() -> {
            Long2IntLinkedOpenHashMap long2intlinkedopenhashmap = new Long2IntLinkedOpenHashMap(100, 0.25F) {
                protected void rehash(int p_111238_) {
                }
            };
            long2intlinkedopenhashmap.defaultReturnValue(Integer.MAX_VALUE);
            return long2intlinkedopenhashmap;
        });
        private final Long2FloatLinkedOpenHashMap brightnessCache = (Long2FloatLinkedOpenHashMap)Util.make(() -> {
            Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(100, 0.25F) {
                protected void rehash(int p_111245_) {
                }
            };
            long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
            return long2floatlinkedopenhashmap;
        });

        private Cache() {
        }

        public void enable() {
            this.enabled = true;
        }

        public void disable() {
            this.enabled = false;
            this.colorCache.clear();
            this.brightnessCache.clear();
        }

        public int getLightColor(BlockState p_111222_, BlockAndTintGetter p_111223_, BlockPos p_111224_) {
            long i = p_111224_.asLong();
            int k;
            if (this.enabled) {
                k = this.colorCache.get(i);
                if (k != Integer.MAX_VALUE) {
                    return k;
                }
            }

            k = LevelRenderer.getLightColor(p_111223_, p_111222_, p_111224_);
            if (this.enabled) {
                if (this.colorCache.size() == 100) {
                    this.colorCache.removeFirstInt();
                }

                this.colorCache.put(i, k);
            }

            return k;
        }

        public float getShadeBrightness(BlockState p_111227_, BlockAndTintGetter p_111228_, BlockPos p_111229_) {
            long i = p_111229_.asLong();
            float f1;
            if (this.enabled) {
                f1 = this.brightnessCache.get(i);
                if (!Float.isNaN(f1)) {
                    return f1;
                }
            }

            f1 = p_111227_.getShadeBrightness(p_111228_, p_111229_);
            if (this.enabled) {
                if (this.brightnessCache.size() == 100) {
                    this.brightnessCache.removeFirstFloat();
                }

                this.brightnessCache.put(i, f1);
            }

            return f1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static enum SizeInfo {
        DOWN(Direction.DOWN, false),
        UP(Direction.UP, false),
        NORTH(Direction.NORTH, false),
        SOUTH(Direction.SOUTH, false),
        WEST(Direction.WEST, false),
        EAST(Direction.EAST, false),
        FLIP_DOWN(Direction.DOWN, true),
        FLIP_UP(Direction.UP, true),
        FLIP_NORTH(Direction.NORTH, true),
        FLIP_SOUTH(Direction.SOUTH, true),
        FLIP_WEST(Direction.WEST, true),
        FLIP_EAST(Direction.EAST, true);

        final int shape;

        private SizeInfo(Direction p_111264_, boolean p_111265_) {
            this.shape = p_111264_.get3DDataValue() + (p_111265_ ? ModelBlockRenderer.DIRECTIONS.length : 0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static enum AmbientVertexRemap {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        final int vert0;
        final int vert1;
        final int vert2;
        final int vert3;
        private static final AmbientVertexRemap[] BY_FACING = (AmbientVertexRemap[])Util.make(new AmbientVertexRemap[6], (p_111204_) -> {
            p_111204_[Direction.DOWN.get3DDataValue()] = DOWN;
            p_111204_[Direction.UP.get3DDataValue()] = UP;
            p_111204_[Direction.NORTH.get3DDataValue()] = NORTH;
            p_111204_[Direction.SOUTH.get3DDataValue()] = SOUTH;
            p_111204_[Direction.WEST.get3DDataValue()] = WEST;
            p_111204_[Direction.EAST.get3DDataValue()] = EAST;
        });

        private AmbientVertexRemap(int p_111195_, int p_111196_, int p_111197_, int p_111198_) {
            this.vert0 = p_111195_;
            this.vert1 = p_111196_;
            this.vert2 = p_111197_;
            this.vert3 = p_111198_;
        }

        public static AmbientVertexRemap fromFacing(Direction p_111202_) {
            return BY_FACING[p_111202_.get3DDataValue()];
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static enum AdjacencyInfo {
        DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH}),
        UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST}),
        SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.WEST}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.EAST}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH}),
        EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.DOWN, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_NORTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.NORTH}, new SizeInfo[]{net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.FLIP_SOUTH, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.UP, net.minecraft.client.renderer.block.ModelBlockRenderer.SizeInfo.SOUTH});

        final Direction[] corners;
        final boolean doNonCubicWeight;
        final SizeInfo[] vert0Weights;
        final SizeInfo[] vert1Weights;
        final SizeInfo[] vert2Weights;
        final SizeInfo[] vert3Weights;
        private static final AdjacencyInfo[] BY_FACING = (AdjacencyInfo[])Util.make(new AdjacencyInfo[6], (p_111134_) -> {
            p_111134_[Direction.DOWN.get3DDataValue()] = DOWN;
            p_111134_[Direction.UP.get3DDataValue()] = UP;
            p_111134_[Direction.NORTH.get3DDataValue()] = NORTH;
            p_111134_[Direction.SOUTH.get3DDataValue()] = SOUTH;
            p_111134_[Direction.WEST.get3DDataValue()] = WEST;
            p_111134_[Direction.EAST.get3DDataValue()] = EAST;
        });

        private AdjacencyInfo(Direction[] p_111122_, float p_111123_, boolean p_111124_, SizeInfo[] p_111125_, SizeInfo[] p_111126_, SizeInfo[] p_111127_, SizeInfo[] p_111128_) {
            this.corners = p_111122_;
            this.doNonCubicWeight = p_111124_;
            this.vert0Weights = p_111125_;
            this.vert1Weights = p_111126_;
            this.vert2Weights = p_111127_;
            this.vert3Weights = p_111128_;
        }

        public static AdjacencyInfo fromFacing(Direction p_111132_) {
            return BY_FACING[p_111132_.get3DDataValue()];
        }
    }
}
