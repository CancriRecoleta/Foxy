package com.github.foxy.client.mixin.iris;

import com.github.foxy.client.core.IGetFoxyRenderSystem;
import com.github.foxy.client.core.util.IrisUtil;
import com.github.foxy.client.iris.IGetIrisFoxyPipelineData;
import com.github.foxy.client.iris.IGetFoxyPatchData;
import com.github.foxy.client.iris.IrisShaderPatch;
import com.github.foxy.client.iris.IrisFoxyRenderPipelineData;
import net.irisshaders.iris.gl.buffer.ShaderStorageBufferHolder;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IrisRenderingPipeline.class, remap = false)
public class MixinIrisRenderingPipeline implements IGetFoxyPatchData, IGetIrisFoxyPipelineData {
    @Shadow @Final private CustomUniforms customUniforms;
    @Shadow private ShaderStorageBufferHolder shaderStorageBufferHolder;
    @Unique IrisShaderPatch patchData;
    @Unique
    IrisFoxyRenderPipelineData pipeline;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/pipeline/transform/ShaderPrinter;resetPrintState()V", shift = At.Shift.AFTER))
    private void foxy$injectPatchDataStore(ProgramSet programSet, CallbackInfo ci) {
        if (IrisUtil.SHADER_SUPPORT) {
            this.patchData = ((IGetFoxyPatchData) programSet).foxy$getPatchData();
        }
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/pipeline/IrisRenderingPipeline;createSetupComputes([Lnet/irisshaders/iris/shaderpack/programs/ComputeSource;Lnet/irisshaders/iris/shaderpack/programs/ProgramSet;Lnet/irisshaders/iris/shaderpack/texture/TextureStage;)[Lnet/irisshaders/iris/gl/program/ComputeProgram;"))
    private void foxy$injectPipeline(ProgramSet programSet, CallbackInfo ci) {
        if (this.patchData != null) {
            this.pipeline = IrisFoxyRenderPipelineData.buildPipeline((IrisRenderingPipeline)(Object)this, this.patchData, this.customUniforms, this.shaderStorageBufferHolder);
        }
    }

    // Upstream Foxy (MC 1.21) injects before GlStateManager._activeTexture in beginLevelRendering;
    // that package (com.mojang.blaze3d.opengl) does not exist on 1.20.1. Oculus 1.20.1's
    // beginLevelRendering opens with RenderSystem.activeTexture(int) (its first instruction), so the
    // equivalent "inject at method entry, before the first texture activation" point targets that.
    @Inject(method = "beginLevelRendering", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;activeTexture(I)V", shift = At.Shift.BEFORE), remap = false)
    private void foxy$injectViewportSetup(CallbackInfo ci) {
        if (IrisUtil.CAPTURED_VIEWPORT_PARAMETERS != null) {
            var renderer = ((IGetFoxyRenderSystem) Minecraft.getInstance().levelRenderer).foxy$getRenderSystem();
            if (renderer != null) {
                IrisUtil.CAPTURED_VIEWPORT_PARAMETERS.apply(renderer);
            }
        }
    }

    @Override
    public IrisShaderPatch foxy$getPatchData() {
        return this.patchData;
    }

    @Override
    public IrisFoxyRenderPipelineData foxy$getPipelineData() {
        return this.pipeline;
    }
}
