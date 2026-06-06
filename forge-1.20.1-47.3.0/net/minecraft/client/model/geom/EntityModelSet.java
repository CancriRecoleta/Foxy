//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.model.geom;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityModelSet implements ResourceManagerReloadListener {
    private Map<ModelLayerLocation, LayerDefinition> roots = ImmutableMap.of();

    public EntityModelSet() {
    }

    public ModelPart bakeLayer(ModelLayerLocation p_171104_) {
        LayerDefinition $$1 = (LayerDefinition)this.roots.get(p_171104_);
        if ($$1 == null) {
            throw new IllegalArgumentException("No model for layer " + p_171104_);
        } else {
            return $$1.bakeRoot();
        }
    }

    public void onResourceManagerReload(ResourceManager p_171102_) {
        this.roots = ImmutableMap.copyOf(LayerDefinitions.createRoots());
    }
}
