package com.github.foxy.client.mixin.iris;

import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.client.core.util.IrisUtil;
import com.github.foxy.client.iris.IGetFoxyPatchData;
import com.github.foxy.client.iris.IrisShaderPatch;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = ProgramSet.class, remap = false)
public class MixinProgramSet implements IGetFoxyPatchData {
    @Shadow @Final private PackDirectives packDirectives;
    @Unique IrisShaderPatch patchData;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/shaderpack/programs/ProgramSet;locateDirectives()V", shift = At.Shift.BEFORE))
    private void foxy$injectPatchMaker(AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, ShaderProperties shaderProperties, ShaderPack pack, CallbackInfo ci) {
        if (FoxyConfig.CONFIG.isRenderingEnabled() && IrisUtil.SHADER_SUPPORT) {
            this.patchData = IrisShaderPatch.makePatch(pack, directory, sourceProvider);
        }
        /*
        if (this.patchData != null) {
            //Inject directives from foxy
            DispatchingDirectiveHolder ddh = new DispatchingDirectiveHolder();
            this.packDirectives.acceptDirectivesFrom(ddh);
            CommentDirectiveParser.findDirective(this.patchData.getPatchSource(), CommentDirective.Type.RENDERTARGETS)
                    .map(dir->Arrays.stream(dir.getDirective().split(","))
                            .mapToInt(Integer::parseInt).toArray())
                    .ifPresent(ddh::processDirective);

        }
         */
    }


    @Override
    public IrisShaderPatch foxy$getPatchData() {
        return this.patchData;
    }
}
