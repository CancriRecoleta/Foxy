package com.github.foxy.client.mixin.iris;

import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.client.core.util.IrisUtil;
import com.github.foxy.client.iris.IrisShaderPatch;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.irisshaders.iris.gl.shader.StandardMacros;
import net.irisshaders.iris.helpers.StringPair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.List;

@Mixin(value = StandardMacros.class, remap = false)
public abstract class StandardMacrosMixin {

    @Shadow
    private static void define(List<StringPair> defines, String key) {}

    @Shadow
    private static void define(List<StringPair> defines, String key, String value) {}

    @WrapOperation(method = "createStandardEnvironmentDefines", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;copyOf(Ljava/util/Collection;)Lcom/google/common/collect/ImmutableList;"))
    private static ImmutableList<StringPair> foxy$injectFoxyDefine(Collection<StringPair> list, Operation<ImmutableList<StringPair>> original) {
        if (FoxyConfig.CONFIG.isRenderingEnabled() && IrisUtil.SHADER_SUPPORT) {
            define((List<StringPair>) list, "VOXY", Integer.toString(IrisShaderPatch.SHADER_DEFINE_VERSION));
        }
        return ImmutableList.copyOf(list);
    }
}
