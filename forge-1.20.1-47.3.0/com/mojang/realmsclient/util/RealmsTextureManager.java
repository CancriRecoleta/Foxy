//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsTextureManager {
    private static final Map<String, RealmsTexture> TEXTURES = Maps.newHashMap();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation TEMPLATE_ICON_LOCATION = new ResourceLocation("textures/gui/presets/isles.png");

    public RealmsTextureManager() {
    }

    public static ResourceLocation worldTemplate(String p_270945_, @Nullable String p_270612_) {
        return p_270612_ == null ? TEMPLATE_ICON_LOCATION : getTexture(p_270945_, p_270612_);
    }

    private static ResourceLocation getTexture(String p_90197_, String p_90198_) {
        RealmsTexture $$2 = (RealmsTexture)TEXTURES.get(p_90197_);
        if ($$2 != null && $$2.image().equals(p_90198_)) {
            return $$2.textureId;
        } else {
            NativeImage $$3 = loadImage(p_90198_);
            ResourceLocation $$5;
            if ($$3 == null) {
                $$5 = MissingTextureAtlasSprite.getLocation();
                TEXTURES.put(p_90197_, new RealmsTexture(p_90198_, $$5));
                return $$5;
            } else {
                $$5 = new ResourceLocation("realms", "dynamic/" + p_90197_);
                Minecraft.getInstance().getTextureManager().register((ResourceLocation)$$5, (AbstractTexture)(new DynamicTexture($$3)));
                TEXTURES.put(p_90197_, new RealmsTexture(p_90198_, $$5));
                return $$5;
            }
        }
    }

    @Nullable
    private static NativeImage loadImage(String p_270725_) {
        byte[] $$1 = Base64.getDecoder().decode(p_270725_);
        ByteBuffer $$2 = MemoryUtil.memAlloc($$1.length);

        try {
            NativeImage var9 = NativeImage.read($$2.put($$1).flip());
            return var9;
        } catch (IOException var7) {
            IOException $$3 = var7;
            LOGGER.warn("Failed to load world image: {}", p_270725_, $$3);
        } finally {
            MemoryUtil.memFree($$2);
        }

        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static record RealmsTexture(String image, ResourceLocation textureId) {
        public RealmsTexture(String image, ResourceLocation textureId) {
            this.image = image;
            this.textureId = textureId;
        }

        public String image() {
            return this.image;
        }

        public ResourceLocation textureId() {
            return this.textureId;
        }
    }
}
