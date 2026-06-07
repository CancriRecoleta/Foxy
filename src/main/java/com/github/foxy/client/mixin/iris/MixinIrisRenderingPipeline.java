package com.github.foxy.client.mixin.iris;

import com.github.foxy.client.core.IGetFoxyRenderSystem;
import com.github.foxy.client.core.util.IrisUtil;
import com.github.foxy.client.iris.IGetIrisFoxyPipelineData;
import com.github.foxy.client.iris.IGetFoxyPatchData;
import com.github.foxy.client.iris.IrisShaderPatch;
import com.github.foxy.client.iris.IrisFoxyRenderPipelineData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

    // Forge's Mixin 0.8.5 forbids @Inject into a constructor at any non-RETURN instruction
    // ("Cannot inject into constructors at non-return instructions"), so the upstream pair of
    // INVOKE-point injectors (capture patchData at resetPrintState, then build the pipeline at
    // createSetupComputes) cannot be ported verbatim. A naive @At("TAIL") works for the common case
    // but runs AFTER the ctor's customUniforms.optimise() (IrisRenderingPipeline.<init> line 492),
    // which prunes any custom uniform not yet registered in CustomUniforms.locationMap — including
    // ones referenced ONLY by the Foxy LOD shader patch — so they would never get a location and read
    // as 0 in the LOD shader. To preserve upstream's ordering on Forge we instead @WrapOperation the
    // optimise() call (MixinExtras wraps a method INVOKE, which IS permitted inside constructors) and
    // build the Foxy pipeline FIRST: buildPipeline -> createUniformSet -> mapholderToPass registers the
    // patch into locationMap, so optimise() then sees those uniforms as used and keeps them. customUniforms
    // (assigned line 263) and shaderStorageBufferHolder (assigned before line 489) are both set by this
    // point, and patchData is read from the programSet argument (constant for the whole ctor).
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/uniforms/custom/CustomUniforms;optimise()V"))
    private void foxy$buildPipelineBeforeOptimise(CustomUniforms instance, Operation<Void> original, ProgramSet programSet) {
        if (IrisUtil.SHADER_SUPPORT) {
            this.patchData = ((IGetFoxyPatchData) programSet).foxy$getPatchData();
        }
        if (this.patchData != null) {
            this.pipeline = IrisFoxyRenderPipelineData.buildPipeline((IrisRenderingPipeline)(Object)this, this.patchData, this.customUniforms, this.shaderStorageBufferHolder);
        }
        original.call(instance);
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
