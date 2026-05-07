package com.github.foxy.client.iris;

import net.irisshaders.iris.gl.sampler.GlSampler;
import net.irisshaders.iris.gl.sampler.SamplerHolder;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;

public class FoxySamplers {
    private static final GlSampler FOXY_DEPTH_SAMPLER = new GlSampler(false, true, false, false);

    public static void addSamplers(IrisRenderingPipeline pipeline, SamplerHolder samplers) {
        var patchData = ((IGetFoxyPatchData)pipeline).Foxy$getPatchData();
        if (patchData != null) {
            String[] opaqueNames = new String[]{"vxDepthTexOpaque"};
            String[] translucentNames = new String[]{"vxDepthTexTrans"};
            /*
            if (IrisShaderPatch.IMPERSONATE_DISTANT_HORIZONS) {
                opaqueNames = new String[]{"vxDepthTexOpaque", "dhDepthTex1"};
                translucentNames = new String[]{"vxDepthTexTrans", "dhDepthTex", "dhDepthTex0"};
            }*/

            //TODO replace ()->0 with the actual depth texture id
            samplers.addDynamicSampler(TextureType.TEXTURE_2D, () -> {
                var pipeData = ((IGetIrisFoxyPipelineData)pipeline).Foxy$getPipelineData();
                if (pipeData == null) {
                    return 0;
                }
                if (pipeData.thePipeline == null) {
                    return 0;
                }

                //In theory the first frame could be null
                var dt = pipeData.thePipeline.fb.getDepthTex();
                if (dt == null) {
                    return 0;
                }
                return dt.id;
            }, FOXY_DEPTH_SAMPLER, opaqueNames);

            samplers.addDynamicSampler(TextureType.TEXTURE_2D, () -> {
                var pipeData = ((IGetIrisFoxyPipelineData)pipeline).Foxy$getPipelineData();
                if (pipeData == null) {
                    return 0;
                }
                if (pipeData.thePipeline == null) {
                    return 0;
                }
                //In theory the first frame could be null
                var dt = pipeData.thePipeline.fbTranslucent.getDepthTex();
                if (dt == null) {
                    return 0;
                }
                return dt.id;
            }, FOXY_DEPTH_SAMPLER, translucentNames);
        }
    }
}
