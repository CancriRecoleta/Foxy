//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs.resources;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public abstract class SimpleJsonResourceReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Gson gson;
    private final String directory;

    public SimpleJsonResourceReloadListener(Gson p_10768_, String p_10769_) {
        this.gson = p_10768_;
        this.directory = p_10769_;
    }

    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager p_10771_, ProfilerFiller p_10772_) {
        Map<ResourceLocation, JsonElement> map = new HashMap();
        scanDirectory(p_10771_, this.directory, this.gson, map);
        return map;
    }

    public static void scanDirectory(ResourceManager p_279308_, String p_279131_, Gson p_279261_, Map<ResourceLocation, JsonElement> p_279404_) {
        FileToIdConverter filetoidconverter = FileToIdConverter.json(p_279131_);
        Iterator var5 = filetoidconverter.listMatchingResources(p_279308_).entrySet().iterator();

        while(var5.hasNext()) {
            Map.Entry<ResourceLocation, Resource> entry = (Map.Entry)var5.next();
            ResourceLocation resourcelocation = (ResourceLocation)entry.getKey();
            ResourceLocation resourcelocation1 = filetoidconverter.fileToId(resourcelocation);

            try {
                Reader reader = ((Resource)entry.getValue()).openAsReader();

                try {
                    JsonElement jsonelement = (JsonElement)GsonHelper.fromJson(p_279261_, (Reader)reader, (Class)JsonElement.class);
                    JsonElement jsonelement1 = (JsonElement)p_279404_.put(resourcelocation1, jsonelement);
                    if (jsonelement1 != null) {
                        throw new IllegalStateException("Duplicate data file ignored with ID " + resourcelocation1);
                    }
                } catch (Throwable var13) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Throwable var12) {
                            var13.addSuppressed(var12);
                        }
                    }

                    throw var13;
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (IOException | JsonParseException | IllegalArgumentException var14) {
                Exception jsonparseexception = var14;
                LOGGER.error("Couldn't parse data file {} from {}", new Object[]{resourcelocation1, resourcelocation, jsonparseexception});
            }
        }

    }

    protected ResourceLocation getPreparedPath(ResourceLocation rl) {
        String var10001 = this.directory;
        return rl.withPath(var10001 + "/" + rl.getPath() + ".json");
    }
}
