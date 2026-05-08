package com.github.foxy.client.mixin.sodium;

import com.github.foxy.client.FoxyClient;
import com.github.foxy.client.compat.FogParameters;
import com.github.foxy.client.core.IGetFoxyRenderSystem;
import com.github.foxy.client.core.rendering.Viewport;
import com.github.foxy.client.core.util.IrisUtil;
import me.jellysquid.mods.sodium.client.gl.device.CommandList;
import me.jellysquid.mods.sodium.client.gl.device.RenderDevice;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import me.jellysquid.mods.sodium.client.render.chunk.DefaultChunkRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderListIterable;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexType;
import me.jellysquid.mods.sodium.client.render.viewport.CameraTransform;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DefaultChunkRenderer.class, remap = false)
public abstract class DefaultChunkRendererMixin extends ShaderChunkRenderer {
    public DefaultChunkRendererMixin(RenderDevice device, ChunkVertexType vertexType) {
        super(device, vertexType);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void foxy$skipEmbeddiumTerrainWhenRequested(ChunkRenderMatrices matrices, CommandList commandList, ChunkRenderListIterable renderLists, TerrainRenderPass renderPass, CameraTransform camera, CallbackInfo ci) {
        if (FoxyClient.disableSodiumChunkRender()) {
            this.foxy$renderFoxyTerrain(matrices, renderPass, camera);
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/ShaderChunkRenderer;end(Lme/jellysquid/mods/sodium/client/render/chunk/terrain/TerrainRenderPass;)V", shift = At.Shift.BEFORE))
    private void foxy$renderAfterEmbeddiumCutout(ChunkRenderMatrices matrices, CommandList commandList, ChunkRenderListIterable renderLists, TerrainRenderPass renderPass, CameraTransform camera, CallbackInfo ci) {
        this.foxy$renderFoxyTerrain(matrices, renderPass, camera);
    }

    @Unique
    private void foxy$renderFoxyTerrain(ChunkRenderMatrices matrices, TerrainRenderPass renderPass, CameraTransform camera) {
        if (renderPass != DefaultTerrainRenderPasses.CUTOUT) {
            return;
        }
        var renderer = ((IGetFoxyRenderSystem) Minecraft.getInstance().levelRenderer).Foxy$getRenderSystem();
        if (renderer == null) {
            return;
        }

        Viewport<?> viewport;
        if (IrisUtil.irisShaderPackEnabled()) {
            viewport = renderer.getViewport();
        } else {
            viewport = renderer.setupViewport(matrices.projection(), matrices.modelView(), FogParameters.NONE, camera.x, camera.y, camera.z);
        }
        renderer.renderOpaque(viewport);
    }
}
