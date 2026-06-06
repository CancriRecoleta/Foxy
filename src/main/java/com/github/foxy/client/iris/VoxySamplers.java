package com.github.foxy.client.iris;

import net.irisshaders.iris.gl.sampler.GlSampler;
import net.irisshaders.iris.gl.sampler.SamplerHolder;
import net.irisshaders.iris.gl.texture.TextureType;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;

public class VoxySamplers {
    // Oculus's SamplerHolder.addDynamicSampler takes a GlSampler instance (not a Supplier), and
    // GlSampler has no predefined MIPPED_NEAREST_NEAREST constant, so build one lazily and reuse it:
    // nearest filtering with mipmapping enabled (GlSampler(linear=false, mipmapped=true, ...)).
    private static GlSampler MIPPED_NEAREST;
    private static GlSampler mippedNearest() {
        if (MIPPED_NEAREST == null) {
            MIPPED_NEAREST = new GlSampler(false, true, false, false);
        }
        return MIPPED_NEAREST;
    }

    public static void addSamplers(IrisRenderingPipeline pipeline, SamplerHolder samplers) {
        var patchData = ((IGetVoxyPatchData)pipeline).voxy$getPatchData();
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
                var pipeData = ((IGetIrisVoxyPipelineData)pipeline).voxy$getPipelineData();
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
            }, mippedNearest(), opaqueNames);

            samplers.addDynamicSampler(TextureType.TEXTURE_2D, () -> {
                var pipeData = ((IGetIrisVoxyPipelineData)pipeline).voxy$getPipelineData();
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
            }, mippedNearest(), translucentNames);
        }
    }
}
