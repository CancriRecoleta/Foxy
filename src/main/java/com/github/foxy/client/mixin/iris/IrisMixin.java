package com.github.foxy.client.mixin.iris;

import com.github.foxy.client.iris.ShaderLoadError;
import com.github.foxy.common.Logger;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Iris.class, remap = false)
public class IrisMixin {
    @Redirect(method = "createPipeline", at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/shaderpack/ShaderPack;getProgramSet(Lnet/irisshaders/iris/shaderpack/materialmap/NamespacedId;)Lnet/irisshaders/iris/shaderpack/programs/ProgramSet;"))
    private static ProgramSet foxy$redirectProgramSet(ShaderPack shaderPack, NamespacedId dim) {
        try {
            return shaderPack.getProgramSet(dim);
        } catch (ShaderLoadError e) {
            Logger.error(e);
            return null;
        }
    }
}
