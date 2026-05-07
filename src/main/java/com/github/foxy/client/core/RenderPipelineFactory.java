package com.github.foxy.client.core;

import com.github.foxy.client.core.rendering.hierachical.AsyncNodeManager;
import com.github.foxy.client.core.rendering.hierachical.HierarchicalOcclusionTraverser;
import com.github.foxy.client.core.rendering.hierachical.NodeCleaner;
import com.github.foxy.client.core.util.IrisUtil;
import com.github.foxy.client.iris.IGetIrisFoxyPipelineData;
import com.github.foxy.common.Logger;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;

import java.util.function.BooleanSupplier;

public class RenderPipelineFactory {
    public static AbstractRenderPipeline createPipeline(RenderProperties properties, AsyncNodeManager nodeManager, NodeCleaner nodeCleaner, HierarchicalOcclusionTraverser traversal, BooleanSupplier frexSupplier) {
        //Note this is where will choose/create e.g. IrisRenderPipeline or normal pipeline
        AbstractRenderPipeline pipeline = null;
        if (IrisUtil.IRIS_INSTALLED && IrisUtil.SHADER_SUPPORT) {
            pipeline = createIrisPipeline(properties, nodeManager, nodeCleaner, traversal, frexSupplier);
        }
        if (pipeline == null) {
            pipeline = new NormalRenderPipeline(properties, nodeManager, nodeCleaner, traversal, frexSupplier);
        }
        return pipeline;
    }

    private static AbstractRenderPipeline createIrisPipeline(RenderProperties properties, AsyncNodeManager nodeManager, NodeCleaner nodeCleaner, HierarchicalOcclusionTraverser traversal, BooleanSupplier frexSupplier) {
        var irisPipe = Iris.getPipelineManager().getPipelineNullable();
        if (irisPipe == null) {
            return null;
        }
        if (irisPipe instanceof IGetIrisFoxyPipelineData getFoxyPipeData) {
            var pipeData = getFoxyPipeData.Foxy$getPipelineData();
            if (pipeData == null) {
                return null;
            }
            Logger.info("Creating Foxy iris render pipeline");
            try {
                return new IrisFoxyRenderPipeline(properties, pipeData, nodeManager, nodeCleaner, traversal, frexSupplier);
            } catch (Exception e) {
                Logger.error("Failed to create iris render pipeline", e);
                IrisUtil.disableIrisShaders();
                return null;
            }
        }
        return null;
    }
}
