package com.github.foxy.client.mixin.iris;

import com.github.foxy.client.core.IGetVoxyRenderSystem;
import com.github.foxy.client.core.util.FogParameters;
import com.github.foxy.client.core.util.IrisUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11C.glViewport;

// 1.20.1 LevelRenderer.renderLevel(PoseStack, float, long, boolean, Camera, GameRenderer,
// LightTexture, Matrix4f) — none of the 1.21 GraphicsResourceAllocator/DeltaTracker/GpuBufferSlice
// args. The camera modelview is poseStack.last().pose() (already rotated by GameRenderer), and the
// fog (Sodium 0.8's FogStorage is absent in Embeddium) is rebuilt from the live RenderSystem state.
@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void voxy$injectIrisCompat(
            PoseStack poseStack,
            float partialTick,
            long finishNanoTime,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightTexture,
            Matrix4f projectionMatrix,
            CallbackInfo ci) {
        if (IrisUtil.irisShaderPackEnabled()) {
            var renderer = ((IGetVoxyRenderSystem) this).voxy$getRenderSystem();
            if (renderer != null) {
                //Fix the viewport dims that the shader pipeline may have left scaled.
                glViewport(0, 0, Minecraft.getInstance().getMainRenderTarget().width, Minecraft.getInstance().getMainRenderTarget().height);

                var pos = camera.getPosition();
                float[] fc = RenderSystem.getShaderFogColor();
                FogParameters fog = new FogParameters(RenderSystem.getShaderFogStart(), RenderSystem.getShaderFogEnd(), fc[0], fc[1], fc[2], fc[3]);
                IrisUtil.CAPTURED_VIEWPORT_PARAMETERS = new IrisUtil.CapturedViewportParameters(
                        new ChunkRenderMatrices(projectionMatrix, new Matrix4f(poseStack.last().pose())),
                        fog, pos.x, pos.y, pos.z);
            }
        }
    }
}
