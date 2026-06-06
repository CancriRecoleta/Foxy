//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeItemModelShaper extends ItemModelShaper {
    private final Map<Holder.Reference<Item>, ModelResourceLocation> locations = Maps.newHashMap();
    private final Map<Holder.Reference<Item>, BakedModel> models = Maps.newHashMap();

    public ForgeItemModelShaper(ModelManager manager) {
        super(manager);
    }

    public @Nullable BakedModel getItemModel(Item item) {
        return (BakedModel)this.models.get(ForgeRegistries.ITEMS.getDelegateOrThrow((Object)item));
    }

    public void register(Item item, ModelResourceLocation location) {
        Holder.Reference<Item> key = ForgeRegistries.ITEMS.getDelegateOrThrow((Object)item);
        this.locations.put(key, location);
        this.models.put(key, this.getModelManager().getModel(location));
    }

    public void rebuildCache() {
        ModelManager manager = this.getModelManager();
        Iterator var2 = this.locations.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<Holder.Reference<Item>, ModelResourceLocation> e = (Map.Entry)var2.next();
            this.models.put((Holder.Reference)e.getKey(), manager.getModel((ModelResourceLocation)e.getValue()));
        }

    }

    public ModelResourceLocation getLocation(@NotNull ItemStack stack) {
        ModelResourceLocation location = (ModelResourceLocation)this.locations.get(ForgeRegistries.ITEMS.getDelegateOrThrow((Object)stack.getItem()));
        return location == null ? ModelBakery.MISSING_MODEL_LOCATION : location;
    }
}
