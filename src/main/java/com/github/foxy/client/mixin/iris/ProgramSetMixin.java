package com.github.foxy.client.mixin.iris;

import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.client.core.util.IrisUtil;
import com.github.foxy.client.iris.IGetFoxyPatchData;
import com.github.foxy.client.iris.IrisShaderPatch;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = ProgramSet.class, remap = false)
public class ProgramSetMixin implements IGetFoxyPatchData {
    @Unique
    private IrisShaderPatch foxy$patchData;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/shaderpack/programs/ProgramSet;locateDirectives()V", shift = At.Shift.BEFORE))
    private void foxy$loadPatchData(AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, ShaderProperties shaderProperties, ShaderPack pack, CallbackInfo ci) {
        if (FoxyConfig.CONFIG.isRenderingEnabled() && IrisUtil.SHADER_SUPPORT) {
            this.foxy$patchData = IrisShaderPatch.makePatch(pack, directory, sourceProvider);
        }
    }

    @Override
    public IrisShaderPatch Foxy$getPatchData() {
        return this.foxy$patchData;
    }
}
