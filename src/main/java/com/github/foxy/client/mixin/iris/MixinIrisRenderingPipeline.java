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

    // Forge's Mixin 0.8.5 forbids @Inject into a constructor at any non-RETURN instruction
    // ("Cannot inject into constructors at non-return instructions"), so the upstream pair of
    // INVOKE-point injectors (capture patchData at resetPrintState, then build the pipeline at
    // createSetupComputes) is merged into a single @At("TAIL") callback that runs once the pipeline is
    // fully constructed. Order is therefore guaranteed (patchData is set before it is read), and the
    // @Shadow ctor fields customUniforms/shaderStorageBufferHolder are fully assigned by TAIL.
    // (Upstream's Fabric Mixin permits the mid-ctor injects; Forge's does not.)
    @Inject(method = "<init>", at = @At("TAIL"))
    private void foxy$injectPipeline(ProgramSet programSet, CallbackInfo ci) {
        if (IrisUtil.SHADER_SUPPORT) {
            this.patchData = ((IGetFoxyPatchData) programSet).foxy$getPatchData();
        }
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
