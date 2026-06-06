//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BufferUploader {
    @Nullable
    private static VertexBuffer lastImmediateBuffer;

    public BufferUploader() {
    }

    public static void reset() {
        if (lastImmediateBuffer != null) {
            invalidate();
            VertexBuffer.unbind();
        }

    }

    public static void invalidate() {
        lastImmediateBuffer = null;
    }

    public static void drawWithShader(BufferBuilder.RenderedBuffer p_231203_) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> {
                _drawWithShader(p_231203_);
            });
        } else {
            _drawWithShader(p_231203_);
        }

    }

    private static void _drawWithShader(BufferBuilder.RenderedBuffer p_231212_) {
        VertexBuffer $$1 = upload(p_231212_);
        if ($$1 != null) {
            $$1.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        }

    }

    public static void draw(BufferBuilder.RenderedBuffer p_231210_) {
        VertexBuffer $$1 = upload(p_231210_);
        if ($$1 != null) {
            $$1.draw();
        }

    }

    @Nullable
    private static VertexBuffer upload(BufferBuilder.RenderedBuffer p_231214_) {
        RenderSystem.assertOnRenderThread();
        if (p_231214_.isEmpty()) {
            p_231214_.release();
            return null;
        } else {
            VertexBuffer $$1 = bindImmediateBuffer(p_231214_.drawState().format());
            $$1.upload(p_231214_);
            return $$1;
        }
    }

    private static VertexBuffer bindImmediateBuffer(VertexFormat p_231207_) {
        VertexBuffer $$1 = p_231207_.getImmediateDrawVertexBuffer();
        bindImmediateBuffer($$1);
        return $$1;
    }

    private static void bindImmediateBuffer(VertexBuffer p_231205_) {
        if (p_231205_ != lastImmediateBuffer) {
            p_231205_.bind();
            lastImmediateBuffer = p_231205_;
        }

    }
}
