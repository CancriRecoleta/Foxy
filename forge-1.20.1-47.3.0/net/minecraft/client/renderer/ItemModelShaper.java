//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemModelShaper {
    public final Int2ObjectMap<ModelResourceLocation> shapes = new Int2ObjectOpenHashMap(256);
    private final Int2ObjectMap<BakedModel> shapesCache = new Int2ObjectOpenHashMap(256);
    private final ModelManager modelManager;

    public ItemModelShaper(ModelManager p_109392_) {
        this.modelManager = p_109392_;
    }

    public BakedModel getItemModel(ItemStack p_109407_) {
        BakedModel bakedmodel = this.getItemModel(p_109407_.getItem());
        return bakedmodel == null ? this.modelManager.getMissingModel() : bakedmodel;
    }

    @Nullable
    public BakedModel getItemModel(Item p_109395_) {
        return (BakedModel)this.shapesCache.get(getIndex(p_109395_));
    }

    private static int getIndex(Item p_109405_) {
        return Item.getId(p_109405_);
    }

    public void register(Item p_109397_, ModelResourceLocation p_109398_) {
        this.shapes.put(getIndex(p_109397_), p_109398_);
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    public void rebuildCache() {
        this.shapesCache.clear();
        ObjectIterator var1 = this.shapes.entrySet().iterator();

        while(var1.hasNext()) {
            Map.Entry<Integer, ModelResourceLocation> entry = (Map.Entry)var1.next();
            this.shapesCache.put((Integer)entry.getKey(), this.modelManager.getModel((ModelResourceLocation)entry.getValue()));
        }

    }
}
