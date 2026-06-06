//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinManager {
    public static final String PROPERTY_TEXTURES = "textures";
    private final TextureManager textureManager;
    private final File skinsDirectory;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> insecureSkinCache;

    public SkinManager(TextureManager p_118812_, File p_118813_, final MinecraftSessionService p_118814_) {
        this.textureManager = p_118812_;
        this.skinsDirectory = p_118813_;
        this.sessionService = p_118814_;
        this.insecureSkinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>() {
            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(String p_118853_) {
                GameProfile $$1 = new GameProfile((UUID)null, "dummy_mcdummyface");
                $$1.getProperties().put("textures", new Property("textures", p_118853_, ""));

                try {
                    return p_118814_.getTextures($$1, false);
                } catch (Throwable var4) {
                    return ImmutableMap.of();
                }
            }
        });
    }

    public ResourceLocation registerTexture(MinecraftProfileTexture p_118826_, MinecraftProfileTexture.Type p_118827_) {
        return this.registerTexture(p_118826_, p_118827_, (SkinTextureCallback)null);
    }

    private ResourceLocation registerTexture(MinecraftProfileTexture p_118829_, MinecraftProfileTexture.Type p_118830_, @Nullable SkinTextureCallback p_118831_) {
        String $$3 = Hashing.sha1().hashUnencodedChars(p_118829_.getHash()).toString();
        ResourceLocation $$4 = getTextureLocation(p_118830_, $$3);
        AbstractTexture $$5 = this.textureManager.getTexture($$4, MissingTextureAtlasSprite.getTexture());
        if ($$5 == MissingTextureAtlasSprite.getTexture()) {
            File $$6 = new File(this.skinsDirectory, $$3.length() > 2 ? $$3.substring(0, 2) : "xx");
            File $$7 = new File($$6, $$3);
            HttpTexture $$8 = new HttpTexture($$7, p_118829_.getUrl(), DefaultPlayerSkin.getDefaultSkin(), p_118830_ == Type.SKIN, () -> {
                if (p_118831_ != null) {
                    p_118831_.onSkinTextureAvailable(p_118830_, $$4, p_118829_);
                }

            });
            this.textureManager.register((ResourceLocation)$$4, (AbstractTexture)$$8);
        } else if (p_118831_ != null) {
            p_118831_.onSkinTextureAvailable(p_118830_, $$4, p_118829_);
        }

        return $$4;
    }

    private static ResourceLocation getTextureLocation(MinecraftProfileTexture.Type p_242930_, String p_242947_) {
        String var10000;
        switch (p_242930_) {
            case SKIN -> var10000 = "skins";
            case CAPE -> var10000 = "capes";
            case ELYTRA -> var10000 = "elytra";
            default -> throw new IncompatibleClassChangeError();
        }

        String $$2 = var10000;
        return new ResourceLocation($$2 + "/" + p_242947_);
    }

    public void registerSkins(GameProfile p_118818_, SkinTextureCallback p_118819_, boolean p_118820_) {
        Runnable $$3 = () -> {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> $$3 = Maps.newHashMap();

            try {
                $$3.putAll(this.sessionService.getTextures(p_118818_, p_118820_));
            } catch (InsecurePublicKeyException var7) {
            }

            if ($$3.isEmpty()) {
                p_118818_.getProperties().clear();
                if (p_118818_.getId().equals(Minecraft.getInstance().getUser().getGameProfile().getId())) {
                    p_118818_.getProperties().putAll(Minecraft.getInstance().getProfileProperties());
                    $$3.putAll(this.sessionService.getTextures(p_118818_, false));
                } else {
                    this.sessionService.fillProfileProperties(p_118818_, p_118820_);

                    try {
                        $$3.putAll(this.sessionService.getTextures(p_118818_, p_118820_));
                    } catch (InsecurePublicKeyException var6) {
                    }
                }
            }

            Minecraft.getInstance().execute(() -> {
                RenderSystem.recordRenderCall(() -> {
                    ImmutableList.of(Type.SKIN, Type.CAPE).forEach((p_174848_) -> {
                        if ($$3.containsKey(p_174848_)) {
                            this.registerTexture((MinecraftProfileTexture)$$3.get(p_174848_), p_174848_, p_118819_);
                        }

                    });
                });
            });
        };
        Util.backgroundExecutor().execute($$3);
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getInsecureSkinInformation(GameProfile p_118816_) {
        Property $$1 = (Property)Iterables.getFirst(p_118816_.getProperties().get("textures"), (Object)null);
        return (Map)($$1 == null ? ImmutableMap.of() : (Map)this.insecureSkinCache.getUnchecked($$1.getValue()));
    }

    public ResourceLocation getInsecureSkinLocation(GameProfile p_240307_) {
        MinecraftProfileTexture $$1 = (MinecraftProfileTexture)this.getInsecureSkinInformation(p_240307_).get(Type.SKIN);
        return $$1 != null ? this.registerTexture($$1, Type.SKIN) : DefaultPlayerSkin.getDefaultSkin(UUIDUtil.getOrCreatePlayerUUID(p_240307_));
    }

    @OnlyIn(Dist.CLIENT)
    public interface SkinTextureCallback {
        void onSkinTextureAvailable(MinecraftProfileTexture.Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
    }
}
