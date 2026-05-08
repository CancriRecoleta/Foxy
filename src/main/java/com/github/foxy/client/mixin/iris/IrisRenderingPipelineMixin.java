package com.github.foxy.client.mixin.iris;

import com.github.foxy.client.core.IGetFoxyRenderSystem;
import com.github.foxy.client.core.util.IrisUtil;
import com.github.foxy.client.iris.IGetFoxyPatchData;
import com.github.foxy.client.iris.IGetIrisFoxyPipelineData;
import com.github.foxy.client.iris.IrisFoxyRenderPipelineData;
import com.github.foxy.client.iris.IrisShaderPatch;
import net.irisshaders.iris.gl.buffer.ShaderStorageBufferHolder;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.shaderpack.programs.ComputeSource;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.texture.TextureStage;
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
public class IrisRenderingPipelineMixin implements IGetFoxyPatchData, IGetIrisFoxyPipelineData {
    @Shadow
    @Final
    private CustomUniforms customUniforms;

    @Shadow
    private ShaderStorageBufferHolder shaderStorageBufferHolder;

    @Unique
    private IrisShaderPatch foxy$patchData;

    @Unique
    private IrisFoxyRenderPipelineData foxy$pipelineData;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/pipeline/transform/ShaderPrinter;resetPrintState()V", shift = At.Shift.AFTER))
    private void foxy$capturePatchData(ProgramSet programSet, CallbackInfo ci) {
        if (IrisUtil.SHADER_SUPPORT) {
            this.foxy$patchData = ((IGetFoxyPatchData) programSet).Foxy$getPatchData();
        }
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/pipeline/IrisRenderingPipeline;createSetupComputes([Lnet/irisshaders/iris/shaderpack/programs/ComputeSource;Lnet/irisshaders/iris/shaderpack/programs/ProgramSet;Lnet/irisshaders/iris/shaderpack/texture/TextureStage;)[Lnet/irisshaders/iris/gl/program/ComputeProgram;"))
    private void foxy$buildPipelineData(ProgramSet programSet, CallbackInfo ci) {
        if (this.foxy$patchData != null) {
            this.foxy$pipelineData = IrisFoxyRenderPipelineData.buildPipeline((IrisRenderingPipeline)(Object)this, this.foxy$patchData, this.customUniforms, this.shaderStorageBufferHolder);
        }
    }

    @Inject(method = "beginLevelRendering", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;activeTexture(I)V", shift = At.Shift.BEFORE), remap = true)
    private void foxy$applyCapturedViewport(CallbackInfo ci) {
        if (IrisUtil.CAPTURED_VIEWPORT_PARAMETERS == null) {
            return;
        }
        var renderer = ((IGetFoxyRenderSystem) Minecraft.getInstance().levelRenderer).Foxy$getRenderSystem();
        if (renderer != null) {
            IrisUtil.CAPTURED_VIEWPORT_PARAMETERS.apply(renderer);
        }
    }

    @Override
    public IrisShaderPatch Foxy$getPatchData() {
        return this.foxy$patchData;
    }

    @Override
    public IrisFoxyRenderPipelineData Foxy$getPipelineData() {
        return this.foxy$pipelineData;
    }
}
