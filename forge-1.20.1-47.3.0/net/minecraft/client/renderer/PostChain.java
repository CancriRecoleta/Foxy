//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class PostChain implements AutoCloseable {
    private static final String MAIN_RENDER_TARGET = "minecraft:main";
    private final RenderTarget screenTarget;
    private final ResourceManager resourceManager;
    private final String name;
    private final List<PostPass> passes = Lists.newArrayList();
    private final Map<String, RenderTarget> customRenderTargets = Maps.newHashMap();
    private final List<RenderTarget> fullSizedTargets = Lists.newArrayList();
    private Matrix4f shaderOrthoMatrix;
    private int screenWidth;
    private int screenHeight;
    private float time;
    private float lastStamp;

    public PostChain(TextureManager p_110018_, ResourceManager p_110019_, RenderTarget p_110020_, ResourceLocation p_110021_) throws IOException, JsonSyntaxException {
        this.resourceManager = p_110019_;
        this.screenTarget = p_110020_;
        this.time = 0.0F;
        this.lastStamp = 0.0F;
        this.screenWidth = p_110020_.viewWidth;
        this.screenHeight = p_110020_.viewHeight;
        this.name = p_110021_.toString();
        this.updateOrthoMatrix();
        this.load(p_110018_, p_110021_);
    }

    private void load(TextureManager p_110034_, ResourceLocation p_110035_) throws IOException, JsonSyntaxException {
        Resource resource = this.resourceManager.getResourceOrThrow(p_110035_);

        try {
            Reader reader = resource.openAsReader();

            try {
                JsonObject jsonobject = GsonHelper.parse((Reader)reader);
                JsonArray jsonarray1;
                int j;
                Iterator var8;
                JsonElement jsonelement1;
                Exception exception;
                ChainedJsonException chainedjsonexception2;
                if (GsonHelper.isArrayNode(jsonobject, "targets")) {
                    jsonarray1 = jsonobject.getAsJsonArray("targets");
                    j = 0;

                    for(var8 = jsonarray1.iterator(); var8.hasNext(); ++j) {
                        jsonelement1 = (JsonElement)var8.next();

                        try {
                            this.parseTargetNode(jsonelement1);
                        } catch (Exception var14) {
                            exception = var14;
                            chainedjsonexception2 = ChainedJsonException.forException(exception);
                            chainedjsonexception2.prependJsonKey("targets[" + j + "]");
                            throw chainedjsonexception2;
                        }
                    }
                }

                if (GsonHelper.isArrayNode(jsonobject, "passes")) {
                    jsonarray1 = jsonobject.getAsJsonArray("passes");
                    j = 0;

                    for(var8 = jsonarray1.iterator(); var8.hasNext(); ++j) {
                        jsonelement1 = (JsonElement)var8.next();

                        try {
                            this.parsePassNode(p_110034_, jsonelement1);
                        } catch (Exception var13) {
                            exception = var13;
                            chainedjsonexception2 = ChainedJsonException.forException(exception);
                            chainedjsonexception2.prependJsonKey("passes[" + j + "]");
                            throw chainedjsonexception2;
                        }
                    }
                }
            } catch (Throwable var15) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Throwable var12) {
                        var15.addSuppressed(var12);
                    }
                }

                throw var15;
            }

            if (reader != null) {
                reader.close();
            }

        } catch (Exception var16) {
            Exception exception2 = var16;
            ChainedJsonException chainedjsonexception = ChainedJsonException.forException(exception2);
            String var10001 = p_110035_.getPath();
            chainedjsonexception.setFilenameAndFlush(var10001 + " (" + resource.sourcePackId() + ")");
            throw chainedjsonexception;
        }
    }

    private void parseTargetNode(JsonElement p_110029_) throws ChainedJsonException {
        if (GsonHelper.isStringValue(p_110029_)) {
            this.addTempTarget(p_110029_.getAsString(), this.screenWidth, this.screenHeight);
        } else {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(p_110029_, "target");
            String s = GsonHelper.getAsString(jsonobject, "name");
            int i = GsonHelper.getAsInt(jsonobject, "width", this.screenWidth);
            int j = GsonHelper.getAsInt(jsonobject, "height", this.screenHeight);
            if (this.customRenderTargets.containsKey(s)) {
                throw new ChainedJsonException(s + " is already defined");
            }

            this.addTempTarget(s, i, j);
        }

    }

    private void parsePassNode(TextureManager p_110031_, JsonElement p_110032_) throws IOException {
        JsonObject jsonobject = GsonHelper.convertToJsonObject(p_110032_, "pass");
        String s = GsonHelper.getAsString(jsonobject, "name");
        String s1 = GsonHelper.getAsString(jsonobject, "intarget");
        String s2 = GsonHelper.getAsString(jsonobject, "outtarget");
        RenderTarget rendertarget = this.getRenderTarget(s1);
        RenderTarget rendertarget1 = this.getRenderTarget(s2);
        if (rendertarget == null) {
            throw new ChainedJsonException("Input target '" + s1 + "' does not exist");
        } else if (rendertarget1 == null) {
            throw new ChainedJsonException("Output target '" + s2 + "' does not exist");
        } else {
            PostPass postpass = this.addPass(s, rendertarget, rendertarget1);
            JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "auxtargets", (JsonArray)null);
            if (jsonarray != null) {
                int i = 0;

                for(Iterator var12 = jsonarray.iterator(); var12.hasNext(); ++i) {
                    JsonElement jsonelement = (JsonElement)var12.next();

                    try {
                        JsonObject jsonobject1 = GsonHelper.convertToJsonObject(jsonelement, "auxtarget");
                        String s5 = GsonHelper.getAsString(jsonobject1, "name");
                        String s3 = GsonHelper.getAsString(jsonobject1, "id");
                        boolean flag;
                        String s4;
                        if (s3.endsWith(":depth")) {
                            flag = true;
                            s4 = s3.substring(0, s3.lastIndexOf(58));
                        } else {
                            flag = false;
                            s4 = s3;
                        }

                        RenderTarget rendertarget2 = this.getRenderTarget(s4);
                        if (rendertarget2 == null) {
                            if (flag) {
                                throw new ChainedJsonException("Render target '" + s4 + "' can't be used as depth buffer");
                            }

                            ResourceLocation rl = ResourceLocation.tryParse(s4);
                            ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "textures/effect/" + rl.getPath() + ".png");
                            this.resourceManager.getResource(resourcelocation).orElseThrow(() -> {
                                return new ChainedJsonException("Render target or texture '" + s4 + "' does not exist");
                            });
                            RenderSystem.setShaderTexture(0, resourcelocation);
                            p_110031_.bindForSetup(resourcelocation);
                            AbstractTexture abstracttexture = p_110031_.getTexture(resourcelocation);
                            int j = GsonHelper.getAsInt(jsonobject1, "width");
                            int k = GsonHelper.getAsInt(jsonobject1, "height");
                            boolean flag1 = GsonHelper.getAsBoolean(jsonobject1, "bilinear");
                            if (flag1) {
                                RenderSystem.texParameter(3553, 10241, 9729);
                                RenderSystem.texParameter(3553, 10240, 9729);
                            } else {
                                RenderSystem.texParameter(3553, 10241, 9728);
                                RenderSystem.texParameter(3553, 10240, 9728);
                            }

                            Objects.requireNonNull(abstracttexture);
                            postpass.addAuxAsset(s5, abstracttexture::getId, j, k);
                        } else if (flag) {
                            Objects.requireNonNull(rendertarget2);
                            postpass.addAuxAsset(s5, rendertarget2::getDepthTextureId, rendertarget2.width, rendertarget2.height);
                        } else {
                            Objects.requireNonNull(rendertarget2);
                            postpass.addAuxAsset(s5, rendertarget2::getColorTextureId, rendertarget2.width, rendertarget2.height);
                        }
                    } catch (Exception var27) {
                        Exception exception1 = var27;
                        ChainedJsonException chainedjsonexception = ChainedJsonException.forException(exception1);
                        chainedjsonexception.prependJsonKey("auxtargets[" + i + "]");
                        throw chainedjsonexception;
                    }
                }
            }

            JsonArray jsonarray1 = GsonHelper.getAsJsonArray(jsonobject, "uniforms", (JsonArray)null);
            if (jsonarray1 != null) {
                int l = 0;

                for(Iterator var30 = jsonarray1.iterator(); var30.hasNext(); ++l) {
                    JsonElement jsonelement1 = (JsonElement)var30.next();

                    try {
                        this.parseUniformNode(jsonelement1);
                    } catch (Exception var26) {
                        ChainedJsonException chainedjsonexception1 = ChainedJsonException.forException(var26);
                        chainedjsonexception1.prependJsonKey("uniforms[" + l + "]");
                        throw chainedjsonexception1;
                    }
                }
            }

        }
    }

    private void parseUniformNode(JsonElement p_110048_) throws ChainedJsonException {
        JsonObject jsonobject = GsonHelper.convertToJsonObject(p_110048_, "uniform");
        String s = GsonHelper.getAsString(jsonobject, "name");
        Uniform uniform = ((PostPass)this.passes.get(this.passes.size() - 1)).getEffect().getUniform(s);
        if (uniform == null) {
            throw new ChainedJsonException("Uniform '" + s + "' does not exist");
        } else {
            float[] afloat = new float[4];
            int i = 0;

            for(Iterator var7 = GsonHelper.getAsJsonArray(jsonobject, "values").iterator(); var7.hasNext(); ++i) {
                JsonElement jsonelement = (JsonElement)var7.next();

                try {
                    afloat[i] = GsonHelper.convertToFloat(jsonelement, "value");
                } catch (Exception var11) {
                    Exception exception = var11;
                    ChainedJsonException chainedjsonexception = ChainedJsonException.forException(exception);
                    chainedjsonexception.prependJsonKey("values[" + i + "]");
                    throw chainedjsonexception;
                }
            }

            switch (i) {
                case 0:
                default:
                    break;
                case 1:
                    uniform.set(afloat[0]);
                    break;
                case 2:
                    uniform.set(afloat[0], afloat[1]);
                    break;
                case 3:
                    uniform.set(afloat[0], afloat[1], afloat[2]);
                    break;
                case 4:
                    uniform.set(afloat[0], afloat[1], afloat[2], afloat[3]);
            }

        }
    }

    public RenderTarget getTempTarget(String p_110037_) {
        return (RenderTarget)this.customRenderTargets.get(p_110037_);
    }

    public void addTempTarget(String p_110039_, int p_110040_, int p_110041_) {
        RenderTarget rendertarget = new TextureTarget(p_110040_, p_110041_, true, Minecraft.ON_OSX);
        rendertarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        if (this.screenTarget.isStencilEnabled()) {
            rendertarget.enableStencil();
        }

        this.customRenderTargets.put(p_110039_, rendertarget);
        if (p_110040_ == this.screenWidth && p_110041_ == this.screenHeight) {
            this.fullSizedTargets.add(rendertarget);
        }

    }

    public void close() {
        Iterator var1 = this.customRenderTargets.values().iterator();

        while(var1.hasNext()) {
            RenderTarget rendertarget = (RenderTarget)var1.next();
            rendertarget.destroyBuffers();
        }

        var1 = this.passes.iterator();

        while(var1.hasNext()) {
            PostPass postpass = (PostPass)var1.next();
            postpass.close();
        }

        this.passes.clear();
    }

    public PostPass addPass(String p_110043_, RenderTarget p_110044_, RenderTarget p_110045_) throws IOException {
        PostPass postpass = new PostPass(this.resourceManager, p_110043_, p_110044_, p_110045_);
        this.passes.add(this.passes.size(), postpass);
        return postpass;
    }

    private void updateOrthoMatrix() {
        this.shaderOrthoMatrix = (new Matrix4f()).setOrtho(0.0F, (float)this.screenTarget.width, 0.0F, (float)this.screenTarget.height, 0.1F, 1000.0F);
    }

    public void resize(int p_110026_, int p_110027_) {
        this.screenWidth = this.screenTarget.width;
        this.screenHeight = this.screenTarget.height;
        this.updateOrthoMatrix();
        Iterator var3 = this.passes.iterator();

        while(var3.hasNext()) {
            PostPass postpass = (PostPass)var3.next();
            postpass.setOrthoMatrix(this.shaderOrthoMatrix);
        }

        var3 = this.fullSizedTargets.iterator();

        while(var3.hasNext()) {
            RenderTarget rendertarget = (RenderTarget)var3.next();
            rendertarget.resize(p_110026_, p_110027_, Minecraft.ON_OSX);
        }

    }

    public void process(float p_110024_) {
        if (p_110024_ < this.lastStamp) {
            this.time += 1.0F - this.lastStamp;
            this.time += p_110024_;
        } else {
            this.time += p_110024_ - this.lastStamp;
        }

        for(this.lastStamp = p_110024_; this.time > 20.0F; this.time -= 20.0F) {
        }

        Iterator var2 = this.passes.iterator();

        while(var2.hasNext()) {
            PostPass postpass = (PostPass)var2.next();
            postpass.process(this.time / 20.0F);
        }

    }

    public final String getName() {
        return this.name;
    }

    @Nullable
    private RenderTarget getRenderTarget(@Nullable String p_110050_) {
        if (p_110050_ == null) {
            return null;
        } else {
            return p_110050_.equals("minecraft:main") ? this.screenTarget : (RenderTarget)this.customRenderTargets.get(p_110050_);
        }
    }
}
