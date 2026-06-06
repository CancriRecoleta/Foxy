//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureAtlas extends AbstractTexture implements Dumpable, Tickable {
    private static final Logger LOGGER = LogUtils.getLogger();
    /** @deprecated */
    @Deprecated
    public static final ResourceLocation LOCATION_BLOCKS;
    /** @deprecated */
    @Deprecated
    public static final ResourceLocation LOCATION_PARTICLES;
    private List<SpriteContents> sprites = List.of();
    private List<TextureAtlasSprite.Ticker> animatedTextures = List.of();
    private Map<ResourceLocation, TextureAtlasSprite> texturesByName = Map.of();
    private final ResourceLocation location;
    private final int maxSupportedTextureSize;
    private int width;
    private int height;
    private int mipLevel;

    public TextureAtlas(ResourceLocation p_118269_) {
        this.location = p_118269_;
        this.maxSupportedTextureSize = RenderSystem.maxSupportedTextureSize();
    }

    public void load(ResourceManager p_118282_) {
    }

    public void upload(SpriteLoader.Preparations p_250662_) {
        LOGGER.info("Created: {}x{}x{} {}-atlas", new Object[]{p_250662_.width(), p_250662_.height(), p_250662_.mipLevel(), this.location});
        TextureUtil.prepareImage(this.getId(), p_250662_.mipLevel(), p_250662_.width(), p_250662_.height());
        this.width = p_250662_.width();
        this.height = p_250662_.height();
        this.mipLevel = p_250662_.mipLevel();
        this.clearTextureData();
        this.texturesByName = Map.copyOf(p_250662_.regions());
        List<SpriteContents> list = new ArrayList();
        List<TextureAtlasSprite.Ticker> list1 = new ArrayList();
        Iterator var4 = p_250662_.regions().values().iterator();

        while(var4.hasNext()) {
            TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)var4.next();
            list.add(textureatlassprite.contents());

            try {
                textureatlassprite.uploadFirstFrame();
            } catch (Throwable var9) {
                Throwable throwable = var9;
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Stitching texture atlas");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Texture being stitched together");
                crashreportcategory.setDetail("Atlas path", (Object)this.location);
                crashreportcategory.setDetail("Sprite", (Object)textureatlassprite);
                throw new ReportedException(crashreport);
            }

            TextureAtlasSprite.Ticker textureatlassprite$ticker = textureatlassprite.createTicker();
            if (textureatlassprite$ticker != null) {
                list1.add(textureatlassprite$ticker);
            }
        }

        this.sprites = List.copyOf(list);
        this.animatedTextures = List.copyOf(list1);
        ForgeHooksClient.onTextureStitchedPost(this);
    }

    public void dumpContents(ResourceLocation p_276106_, Path p_276127_) throws IOException {
        String s = p_276106_.toDebugFileName();
        TextureUtil.writeAsPNG(p_276127_, s, this.getId(), this.mipLevel, this.width, this.height);
        dumpSpriteNames(p_276127_, s, this.texturesByName);
    }

    private static void dumpSpriteNames(Path p_261769_, String p_262102_, Map<ResourceLocation, TextureAtlasSprite> p_261722_) {
        Path path = p_261769_.resolve(p_262102_ + ".txt");

        try {
            Writer writer = Files.newBufferedWriter(path);

            try {
                Iterator var5 = p_261722_.entrySet().stream().sorted(Entry.comparingByKey()).toList().iterator();

                while(var5.hasNext()) {
                    Map.Entry<ResourceLocation, TextureAtlasSprite> entry = (Map.Entry)var5.next();
                    TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)entry.getValue();
                    writer.write(String.format(Locale.ROOT, "%s\tx=%d\ty=%d\tw=%d\th=%d%n", entry.getKey(), textureatlassprite.getX(), textureatlassprite.getY(), textureatlassprite.contents().width(), textureatlassprite.contents().height()));
                }
            } catch (Throwable var9) {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                    }
                }

                throw var9;
            }

            if (writer != null) {
                writer.close();
            }
        } catch (IOException var10) {
            IOException ioexception = var10;
            LOGGER.warn("Failed to write file {}", path, ioexception);
        }

    }

    public void cycleAnimationFrames() {
        this.bind();
        Iterator var1 = this.animatedTextures.iterator();

        while(var1.hasNext()) {
            TextureAtlasSprite.Ticker textureatlassprite$ticker = (TextureAtlasSprite.Ticker)var1.next();
            textureatlassprite$ticker.tickAndUpload();
        }

    }

    public void tick() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::cycleAnimationFrames);
        } else {
            this.cycleAnimationFrames();
        }

    }

    public TextureAtlasSprite getSprite(ResourceLocation p_118317_) {
        TextureAtlasSprite textureatlassprite = (TextureAtlasSprite)this.texturesByName.get(p_118317_);
        return textureatlassprite == null ? (TextureAtlasSprite)this.texturesByName.get(MissingTextureAtlasSprite.getLocation()) : textureatlassprite;
    }

    public void clearTextureData() {
        this.sprites.forEach(SpriteContents::close);
        this.animatedTextures.forEach(TextureAtlasSprite.Ticker::close);
        this.sprites = List.of();
        this.animatedTextures = List.of();
        this.texturesByName = Map.of();
    }

    public ResourceLocation location() {
        return this.location;
    }

    public int maxSupportedTextureSize() {
        return this.maxSupportedTextureSize;
    }

    int getWidth() {
        return this.width;
    }

    int getHeight() {
        return this.height;
    }

    public void updateFilter(SpriteLoader.Preparations p_251993_) {
        this.setFilter(false, p_251993_.mipLevel() > 0);
    }

    public Set<ResourceLocation> getTextureLocations() {
        return Collections.unmodifiableSet(this.texturesByName.keySet());
    }

    static {
        LOCATION_BLOCKS = InventoryMenu.BLOCK_ATLAS;
        LOCATION_PARTICLES = new ResourceLocation("textures/atlas/particles.png");
    }
}
