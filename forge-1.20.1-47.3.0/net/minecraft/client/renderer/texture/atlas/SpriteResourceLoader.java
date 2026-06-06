//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.texture.atlas;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SpriteResourceLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter ATLAS_INFO_CONVERTER = new FileToIdConverter("atlases", ".json");
    private final List<SpriteSource> sources;

    private SpriteResourceLoader(List<SpriteSource> p_261613_) {
        this.sources = p_261613_;
    }

    public List<Supplier<SpriteContents>> list(ResourceManager p_261989_) {
        final Map<ResourceLocation, SpriteSource.SpriteSupplier> $$1 = new HashMap();
        SpriteSource.Output $$2 = new SpriteSource.Output() {
            public void add(ResourceLocation p_262067_, SpriteSource.SpriteSupplier p_261936_) {
                SpriteSource.SpriteSupplier $$2 = (SpriteSource.SpriteSupplier)$$1.put(p_262067_, p_261936_);
                if ($$2 != null) {
                    $$2.discard();
                }

            }

            public void removeAll(Predicate<ResourceLocation> p_261939_) {
                Iterator<Map.Entry<ResourceLocation, SpriteSource.SpriteSupplier>> $$1x = $$1.entrySet().iterator();

                while($$1x.hasNext()) {
                    Map.Entry<ResourceLocation, SpriteSource.SpriteSupplier> $$2 = (Map.Entry)$$1x.next();
                    if (p_261939_.test((ResourceLocation)$$2.getKey())) {
                        ((SpriteSource.SpriteSupplier)$$2.getValue()).discard();
                        $$1x.remove();
                    }
                }

            }
        };
        this.sources.forEach((p_261747_) -> {
            p_261747_.run(p_261989_, $$2);
        });
        ImmutableList.Builder<Supplier<SpriteContents>> $$3 = ImmutableList.builder();
        $$3.add(MissingTextureAtlasSprite::create);
        $$3.addAll($$1.values());
        return $$3.build();
    }

    public static SpriteResourceLoader load(ResourceManager p_261551_, ResourceLocation p_261709_) {
        ResourceLocation $$2 = ATLAS_INFO_CONVERTER.idToFile(p_261709_);
        List<SpriteSource> $$3 = new ArrayList();
        Iterator var4 = p_261551_.getResourceStack($$2).iterator();

        while(var4.hasNext()) {
            Resource $$4 = (Resource)var4.next();

            try {
                BufferedReader $$5 = $$4.openAsReader();

                try {
                    Dynamic<JsonElement> $$6 = new Dynamic(JsonOps.INSTANCE, JsonParser.parseReader($$5));
                    DataResult var10001 = SpriteSources.FILE_CODEC.parse($$6);
                    Logger var10003 = LOGGER;
                    Objects.requireNonNull(var10003);
                    $$3.addAll((Collection)var10001.getOrThrow(false, var10003::error));
                } catch (Throwable var10) {
                    if ($$5 != null) {
                        try {
                            $$5.close();
                        } catch (Throwable var9) {
                            var10.addSuppressed(var9);
                        }
                    }

                    throw var10;
                }

                if ($$5 != null) {
                    $$5.close();
                }
            } catch (Exception var11) {
                Exception $$7 = var11;
                LOGGER.warn("Failed to parse atlas definition {} in pack {}", new Object[]{$$2, $$4.sourcePackId(), $$7});
            }
        }

        return new SpriteResourceLoader($$3);
    }
}
