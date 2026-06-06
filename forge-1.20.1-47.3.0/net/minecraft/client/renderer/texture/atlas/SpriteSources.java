//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.texture.atlas;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.renderer.texture.atlas.sources.SourceFilter;
import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpriteSources {
    private static final BiMap<ResourceLocation, SpriteSourceType> TYPES = HashBiMap.create();
    public static final SpriteSourceType SINGLE_FILE;
    public static final SpriteSourceType DIRECTORY;
    public static final SpriteSourceType FILTER;
    public static final SpriteSourceType UNSTITCHER;
    public static final SpriteSourceType PALETTED_PERMUTATIONS;
    public static Codec<SpriteSourceType> TYPE_CODEC;
    public static Codec<SpriteSource> CODEC;
    public static Codec<List<SpriteSource>> FILE_CODEC;

    public SpriteSources() {
    }

    private static SpriteSourceType register(String p_262175_, Codec<? extends SpriteSource> p_261464_) {
        SpriteSourceType $$2 = new SpriteSourceType(p_261464_);
        ResourceLocation $$3 = new ResourceLocation(p_262175_);
        SpriteSourceType $$4 = (SpriteSourceType)TYPES.putIfAbsent($$3, $$2);
        if ($$4 != null) {
            throw new IllegalStateException("Duplicate registration " + $$3);
        } else {
            return $$2;
        }
    }

    static {
        SINGLE_FILE = register("single", SingleFile.CODEC);
        DIRECTORY = register("directory", DirectoryLister.CODEC);
        FILTER = register("filter", SourceFilter.CODEC);
        UNSTITCHER = register("unstitch", Unstitcher.CODEC);
        PALETTED_PERMUTATIONS = register("paletted_permutations", PalettedPermutations.CODEC);
        TYPE_CODEC = ResourceLocation.CODEC.flatXmap((p_274717_) -> {
            SpriteSourceType $$1 = (SpriteSourceType)TYPES.get(p_274717_);
            return $$1 != null ? DataResult.success($$1) : DataResult.error(() -> {
                return "Unknown type " + p_274717_;
            });
        }, (p_274716_) -> {
            ResourceLocation $$1 = (ResourceLocation)TYPES.inverse().get(p_274716_);
            return p_274716_ != null ? DataResult.success($$1) : DataResult.error(() -> {
                return "Unknown type " + $$1;
            });
        });
        CODEC = TYPE_CODEC.dispatch(SpriteSource::type, SpriteSourceType::codec);
        FILE_CODEC = CODEC.listOf().fieldOf("sources").codec();
    }
}
