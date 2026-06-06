//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.loot;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootModifierManager extends SimpleJsonResourceReloadListener {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON_INSTANCE = Deserializers.createFunctionSerializer().create();
    private Map<ResourceLocation, IGlobalLootModifier> registeredLootModifiers = ImmutableMap.of();
    private static final String folder = "loot_modifiers";

    public LootModifierManager() {
        super(GSON_INSTANCE, "loot_modifiers");
    }

    protected void apply(Map<ResourceLocation, JsonElement> resourceList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        ImmutableMap.Builder<ResourceLocation, IGlobalLootModifier> builder = ImmutableMap.builder();
        List<ResourceLocation> finalLocations = new ArrayList();
        ResourceLocation resourcelocation = new ResourceLocation("forge", "loot_modifiers/global_loot_modifiers.json");
        Iterator var7 = resourceManagerIn.getResourceStack(resourcelocation).iterator();

        while(var7.hasNext()) {
            Resource iresource = (Resource)var7.next();

            try {
                InputStream inputstream = iresource.open();

                try {
                    Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));

                    try {
                        JsonObject jsonobject = (JsonObject)GsonHelper.fromJson(GSON_INSTANCE, (Reader)reader, (Class)JsonObject.class);
                        boolean replace = jsonobject.get("replace").getAsBoolean();
                        if (replace) {
                            finalLocations.clear();
                        }

                        JsonArray entryList = jsonobject.get("entries").getAsJsonArray();
                        Iterator var14 = entryList.iterator();

                        while(var14.hasNext()) {
                            JsonElement entry = (JsonElement)var14.next();
                            ResourceLocation loc = new ResourceLocation(entry.getAsString());
                            finalLocations.remove(loc);
                            finalLocations.add(loc);
                        }
                    } catch (Throwable var19) {
                        try {
                            reader.close();
                        } catch (Throwable var18) {
                            var19.addSuppressed(var18);
                        }

                        throw var19;
                    }

                    reader.close();
                } catch (Throwable var20) {
                    if (inputstream != null) {
                        try {
                            inputstream.close();
                        } catch (Throwable var17) {
                            var20.addSuppressed(var17);
                        }
                    }

                    throw var20;
                }

                if (inputstream != null) {
                    inputstream.close();
                }
            } catch (IOException | RuntimeException var21) {
                Exception ioexception = var21;
                LOGGER.error("Couldn't read global loot modifier list {} in data pack {}", resourcelocation, iresource.sourcePackId(), ioexception);
            }
        }

        var7 = finalLocations.iterator();

        while(var7.hasNext()) {
            ResourceLocation location = (ResourceLocation)var7.next();
            JsonElement json = (JsonElement)resourceList.get(location);
            IGlobalLootModifier.DIRECT_CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial((errorMsg) -> {
                LOGGER.warn("Could not decode GlobalLootModifier with json id {} - error: {}", location, errorMsg);
            }).ifPresent((modifier) -> {
                builder.put(location, modifier);
            });
        }

        this.registeredLootModifiers = builder.build();
    }

    public Collection<IGlobalLootModifier> getAllLootMods() {
        return this.registeredLootModifiers.values();
    }
}
