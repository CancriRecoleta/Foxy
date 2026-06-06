//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.model.geom;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ModelLayerLocation {
    private final ResourceLocation model;
    private final String layer;

    public ModelLayerLocation(ResourceLocation p_171121_, String p_171122_) {
        this.model = p_171121_;
        this.layer = p_171122_;
    }

    public ResourceLocation getModel() {
        return this.model;
    }

    public String getLayer() {
        return this.layer;
    }

    public boolean equals(Object p_171126_) {
        if (this == p_171126_) {
            return true;
        } else if (!(p_171126_ instanceof ModelLayerLocation)) {
            return false;
        } else {
            ModelLayerLocation $$1 = (ModelLayerLocation)p_171126_;
            return this.model.equals($$1.model) && this.layer.equals($$1.layer);
        }
    }

    public int hashCode() {
        int $$0 = this.model.hashCode();
        $$0 = 31 * $$0 + this.layer.hashCode();
        return $$0;
    }

    public String toString() {
        return this.model + "#" + this.layer;
    }
}
