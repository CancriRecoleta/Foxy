//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CompositeModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
    private final Map<String, T> childModels = new LinkedHashMap();
    private final List<String> itemRenderOrder = new ArrayList();

    public static <T extends ModelBuilder<T>> CompositeModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new CompositeModelBuilder(parent, existingFileHelper);
    }

    protected CompositeModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(new ResourceLocation("forge:composite"), parent, existingFileHelper);
    }

    public CompositeModelBuilder<T> child(String name, T modelBuilder) {
        Preconditions.checkNotNull(name, "name must not be null");
        Preconditions.checkNotNull(modelBuilder, "modelBuilder must not be null");
        this.childModels.put(name, modelBuilder);
        this.itemRenderOrder.add(name);
        return this;
    }

    public CompositeModelBuilder<T> itemRenderOrder(String... names) {
        Preconditions.checkNotNull(names, "names must not be null");
        Preconditions.checkArgument(names.length > 0, "names must contain at least one element");
        String[] var2 = names;
        int var3 = names.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String name = var2[var4];
            if (!this.childModels.containsKey(name)) {
                throw new IllegalArgumentException("names contains \"" + name + "\", which is not a child of this model");
            }
        }

        this.itemRenderOrder.clear();
        this.itemRenderOrder.addAll(Arrays.asList(names));
        return this;
    }

    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        JsonObject children = new JsonObject();
        Iterator var3 = this.childModels.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, T> entry = (Map.Entry)var3.next();
            children.add((String)entry.getKey(), ((ModelBuilder)entry.getValue()).toJson());
        }

        json.add("children", children);
        JsonArray itemRenderOrder = new JsonArray();
        Iterator var7 = this.itemRenderOrder.iterator();

        while(var7.hasNext()) {
            String name = (String)var7.next();
            itemRenderOrder.add(name);
        }

        json.add("item_render_order", itemRenderOrder);
        return json;
    }
}
