//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model.lighting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.ForgeConfig;

public class ForgeModelBlockRenderer extends ModelBlockRenderer {
    private static final Direction[] SIDES = Direction.values();
    private final ThreadLocal<QuadLighter> flatLighter;
    private final ThreadLocal<QuadLighter> smoothLighter;

    public ForgeModelBlockRenderer(BlockColors colors) {
        super(colors);
        this.flatLighter = ThreadLocal.withInitial(() -> {
            return new FlatQuadLighter(colors);
        });
        this.smoothLighter = ThreadLocal.withInitial(() -> {
            return new SmoothQuadLighter(colors);
        });
    }

    public void tesselateWithoutAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean checkSides, RandomSource rand, long seed, int packedOverlay, ModelData modelData, RenderType renderType) {
        if ((Boolean)ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get()) {
            render(vertexConsumer, (QuadLighter)this.flatLighter.get(), level, model, state, pos, poseStack, checkSides, rand, seed, packedOverlay, modelData, renderType);
        } else {
            super.tesselateWithoutAO(level, model, state, pos, poseStack, vertexConsumer, checkSides, rand, seed, packedOverlay, modelData, renderType);
        }

    }

    public void tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean checkSides, RandomSource rand, long seed, int packedOverlay, ModelData modelData, RenderType renderType) {
        if ((Boolean)ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get()) {
            render(vertexConsumer, (QuadLighter)this.smoothLighter.get(), level, model, state, pos, poseStack, checkSides, rand, seed, packedOverlay, modelData, renderType);
        } else {
            super.tesselateWithAO(level, model, state, pos, poseStack, vertexConsumer, checkSides, rand, seed, packedOverlay, modelData, renderType);
        }

    }

    public static boolean render(VertexConsumer vertexConsumer, QuadLighter lighter, BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, boolean checkSides, RandomSource rand, long seed, int packedOverlay, ModelData modelData, RenderType renderType) {
        ForgeModelBlockRenderer renderer = (ForgeModelBlockRenderer)Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        PoseStack.Pose pose = poseStack.last();
        boolean empty = true;
        boolean smoothLighter = lighter instanceof SmoothQuadLighter;
        QuadLighter flatLighter = null;
        rand.setSeed(seed);
        List<BakedQuad> quads = model.getQuads(state, (Direction)null, rand, modelData, renderType);
        if (!quads.isEmpty()) {
            empty = false;
            lighter.setup(level, pos, state);
            Iterator var20 = quads.iterator();

            label78:
            while(true) {
                while(true) {
                    if (!var20.hasNext()) {
                        break label78;
                    }

                    BakedQuad quad = (BakedQuad)var20.next();
                    if (smoothLighter && !quad.hasAmbientOcclusion()) {
                        if (flatLighter == null) {
                            flatLighter = (QuadLighter)renderer.flatLighter.get();
                            flatLighter.setup(level, pos, state);
                        }

                        flatLighter.process(vertexConsumer, pose, quad, packedOverlay);
                    } else {
                        lighter.process(vertexConsumer, pose, quad, packedOverlay);
                    }
                }
            }
        }

        Direction[] var27 = SIDES;
        int var26 = var27.length;

        label67:
        for(int var22 = 0; var22 < var26; ++var22) {
            Direction side = var27[var22];
            if (!checkSides || Block.shouldRenderFace(state, level, pos, side, pos.relative(side))) {
                rand.setSeed(seed);
                quads = model.getQuads(state, side, rand, modelData, renderType);
                if (!quads.isEmpty()) {
                    if (empty) {
                        empty = false;
                        lighter.setup(level, pos, state);
                    }

                    Iterator var24 = quads.iterator();

                    while(true) {
                        while(true) {
                            if (!var24.hasNext()) {
                                continue label67;
                            }

                            BakedQuad quad = (BakedQuad)var24.next();
                            if (smoothLighter && !quad.hasAmbientOcclusion()) {
                                if (flatLighter == null) {
                                    flatLighter = (QuadLighter)renderer.flatLighter.get();
                                    flatLighter.setup(level, pos, state);
                                }

                                flatLighter.process(vertexConsumer, pose, quad, packedOverlay);
                            } else {
                                lighter.process(vertexConsumer, pose, quad, packedOverlay);
                            }
                        }
                    }
                }
            }
        }

        lighter.reset();
        if (flatLighter != null) {
            flatLighter.reset();
        }

        return !empty;
    }
}
