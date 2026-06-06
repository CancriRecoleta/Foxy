//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.shaders.Program.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ShaderInstance implements Shader, AutoCloseable {
    public static final String SHADER_PATH = "shaders";
    private static final String SHADER_CORE_PATH = "shaders/core/";
    private static final String SHADER_INCLUDE_PATH = "shaders/include/";
    static final Logger LOGGER = LogUtils.getLogger();
    private static final AbstractUniform DUMMY_UNIFORM = new AbstractUniform();
    private static final boolean ALWAYS_REAPPLY = true;
    private static ShaderInstance lastAppliedShader;
    private static int lastProgramId = -1;
    private final Map<String, Object> samplerMap;
    private final List<String> samplerNames;
    private final List<Integer> samplerLocations;
    private final List<Uniform> uniforms;
    private final List<Integer> uniformLocations;
    private final Map<String, Uniform> uniformMap;
    private final int programId;
    private final String name;
    private boolean dirty;
    private final BlendMode blend;
    private final List<Integer> attributes;
    private final List<String> attributeNames;
    private final Program vertexProgram;
    private final Program fragmentProgram;
    private final VertexFormat vertexFormat;
    @Nullable
    public final Uniform MODEL_VIEW_MATRIX;
    @Nullable
    public final Uniform PROJECTION_MATRIX;
    @Nullable
    public final Uniform INVERSE_VIEW_ROTATION_MATRIX;
    @Nullable
    public final Uniform TEXTURE_MATRIX;
    @Nullable
    public final Uniform SCREEN_SIZE;
    @Nullable
    public final Uniform COLOR_MODULATOR;
    @Nullable
    public final Uniform LIGHT0_DIRECTION;
    @Nullable
    public final Uniform LIGHT1_DIRECTION;
    @Nullable
    public final Uniform GLINT_ALPHA;
    @Nullable
    public final Uniform FOG_START;
    @Nullable
    public final Uniform FOG_END;
    @Nullable
    public final Uniform FOG_COLOR;
    @Nullable
    public final Uniform FOG_SHAPE;
    @Nullable
    public final Uniform LINE_WIDTH;
    @Nullable
    public final Uniform GAME_TIME;
    @Nullable
    public final Uniform CHUNK_OFFSET;

    /** @deprecated */
    @Deprecated
    public ShaderInstance(ResourceProvider p_173336_, String p_173337_, VertexFormat p_173338_) throws IOException {
        this(p_173336_, new ResourceLocation(p_173337_), p_173338_);
    }

    public ShaderInstance(ResourceProvider p_173336_, ResourceLocation shaderLocation, VertexFormat p_173338_) throws IOException {
        this.samplerMap = Maps.newHashMap();
        this.samplerNames = Lists.newArrayList();
        this.samplerLocations = Lists.newArrayList();
        this.uniforms = Lists.newArrayList();
        this.uniformLocations = Lists.newArrayList();
        this.uniformMap = Maps.newHashMap();
        this.name = shaderLocation.getNamespace().equals("minecraft") ? shaderLocation.getPath() : shaderLocation.toString();
        this.vertexFormat = p_173338_;
        ResourceLocation resourcelocation = new ResourceLocation(shaderLocation.getNamespace(), "shaders/core/" + shaderLocation.getPath() + ".json");

        try {
            Reader reader = p_173336_.openAsReader(resourcelocation);

            try {
                JsonObject jsonobject = GsonHelper.parse((Reader)reader);
                String s1 = GsonHelper.getAsString(jsonobject, "vertex");
                String s = GsonHelper.getAsString(jsonobject, "fragment");
                JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "samplers", (JsonArray)null);
                if (jsonarray != null) {
                    int i = 0;

                    for(Iterator var11 = jsonarray.iterator(); var11.hasNext(); ++i) {
                        JsonElement jsonelement = (JsonElement)var11.next();

                        try {
                            this.parseSamplerNode(jsonelement);
                        } catch (Exception var20) {
                            Exception exception2 = var20;
                            ChainedJsonException chainedjsonexception1 = ChainedJsonException.forException(exception2);
                            chainedjsonexception1.prependJsonKey("samplers[" + i + "]");
                            throw chainedjsonexception1;
                        }
                    }
                }

                JsonArray jsonarray1 = GsonHelper.getAsJsonArray(jsonobject, "attributes", (JsonArray)null);
                if (jsonarray1 != null) {
                    int j = 0;
                    this.attributes = Lists.newArrayListWithCapacity(jsonarray1.size());
                    this.attributeNames = Lists.newArrayListWithCapacity(jsonarray1.size());

                    for(Iterator var28 = jsonarray1.iterator(); var28.hasNext(); ++j) {
                        JsonElement jsonelement1 = (JsonElement)var28.next();

                        try {
                            this.attributeNames.add(GsonHelper.convertToString(jsonelement1, "attribute"));
                        } catch (Exception var19) {
                            ChainedJsonException chainedjsonexception2 = ChainedJsonException.forException(var19);
                            chainedjsonexception2.prependJsonKey("attributes[" + j + "]");
                            throw chainedjsonexception2;
                        }
                    }
                } else {
                    this.attributes = null;
                    this.attributeNames = null;
                }

                JsonArray jsonarray2 = GsonHelper.getAsJsonArray(jsonobject, "uniforms", (JsonArray)null);
                int l;
                if (jsonarray2 != null) {
                    l = 0;

                    for(Iterator var31 = jsonarray2.iterator(); var31.hasNext(); ++l) {
                        JsonElement jsonelement2 = (JsonElement)var31.next();

                        try {
                            this.parseUniformNode(jsonelement2);
                        } catch (Exception var18) {
                            ChainedJsonException chainedjsonexception3 = ChainedJsonException.forException(var18);
                            chainedjsonexception3.prependJsonKey("uniforms[" + l + "]");
                            throw chainedjsonexception3;
                        }
                    }
                }

                this.blend = parseBlendNode(GsonHelper.getAsJsonObject(jsonobject, "blend", (JsonObject)null));
                this.vertexProgram = getOrCreate(p_173336_, Type.VERTEX, s1);
                this.fragmentProgram = getOrCreate(p_173336_, Type.FRAGMENT, s);
                this.programId = ProgramManager.createProgram();
                if (this.attributeNames != null) {
                    l = 0;

                    for(UnmodifiableIterator var32 = p_173338_.getElementAttributeNames().iterator(); var32.hasNext(); ++l) {
                        String s2 = (String)var32.next();
                        Uniform.glBindAttribLocation(this.programId, l, s2);
                        this.attributes.add(l);
                    }
                }

                ProgramManager.linkShader(this);
                this.updateLocations();
            } catch (Throwable var21) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Throwable var17) {
                        var21.addSuppressed(var17);
                    }
                }

                throw var21;
            }

            if (reader != null) {
                reader.close();
            }
        } catch (Exception var22) {
            Exception exception3 = var22;
            ChainedJsonException chainedjsonexception = ChainedJsonException.forException(exception3);
            chainedjsonexception.setFilenameAndFlush(resourcelocation.getPath());
            throw chainedjsonexception;
        }

        this.markDirty();
        this.MODEL_VIEW_MATRIX = this.getUniform("ModelViewMat");
        this.PROJECTION_MATRIX = this.getUniform("ProjMat");
        this.INVERSE_VIEW_ROTATION_MATRIX = this.getUniform("IViewRotMat");
        this.TEXTURE_MATRIX = this.getUniform("TextureMat");
        this.SCREEN_SIZE = this.getUniform("ScreenSize");
        this.COLOR_MODULATOR = this.getUniform("ColorModulator");
        this.LIGHT0_DIRECTION = this.getUniform("Light0_Direction");
        this.LIGHT1_DIRECTION = this.getUniform("Light1_Direction");
        this.GLINT_ALPHA = this.getUniform("GlintAlpha");
        this.FOG_START = this.getUniform("FogStart");
        this.FOG_END = this.getUniform("FogEnd");
        this.FOG_COLOR = this.getUniform("FogColor");
        this.FOG_SHAPE = this.getUniform("FogShape");
        this.LINE_WIDTH = this.getUniform("LineWidth");
        this.GAME_TIME = this.getUniform("GameTime");
        this.CHUNK_OFFSET = this.getUniform("ChunkOffset");
    }

    private static Program getOrCreate(final ResourceProvider p_173341_, Program.Type p_173342_, String p_173343_) throws IOException {
        Program program1 = (Program)p_173342_.getPrograms().get(p_173343_);
        Program program;
        if (program1 == null) {
            ResourceLocation loc = new ResourceLocation(p_173343_);
            String var10000 = loc.getPath();
            String s = "shaders/core/" + var10000 + p_173342_.getExtension();
            ResourceLocation resourcelocation = new ResourceLocation(loc.getNamespace(), s);
            Resource resource = p_173341_.getResourceOrThrow(resourcelocation);
            InputStream inputstream = resource.open();

            try {
                final String s1 = FileUtil.getFullResourcePath(s);
                program = Program.compileShader(p_173342_, p_173343_, inputstream, resource.sourcePackId(), new GlslPreprocessor() {
                    private final Set<String> importedPaths = Sets.newHashSet();

                    public String applyImport(boolean p_173374_, String p_173375_) {
                        ResourceLocation resourcelocation = ForgeHooksClient.getShaderImportLocation(s1, p_173374_, p_173375_);
                        if (!this.importedPaths.add(resourcelocation.toString())) {
                            return null;
                        } else {
                            try {
                                Reader reader = p_173341_.openAsReader(resourcelocation);

                                String var5;
                                try {
                                    var5 = IOUtils.toString(reader);
                                } catch (Throwable var8) {
                                    if (reader != null) {
                                        try {
                                            reader.close();
                                        } catch (Throwable var7) {
                                            var8.addSuppressed(var7);
                                        }
                                    }

                                    throw var8;
                                }

                                if (reader != null) {
                                    reader.close();
                                }

                                return var5;
                            } catch (IOException var9) {
                                IOException ioexception = var9;
                                ShaderInstance.LOGGER.error("Could not open GLSL import {}: {}", resourcelocation, ioexception.getMessage());
                                return "#error " + ioexception.getMessage();
                            }
                        }
                    }
                });
            } catch (Throwable var13) {
                if (inputstream != null) {
                    try {
                        inputstream.close();
                    } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                    }
                }

                throw var13;
            }

            if (inputstream != null) {
                inputstream.close();
            }
        } else {
            program = program1;
        }

        return program;
    }

    public static BlendMode parseBlendNode(JsonObject p_173347_) {
        if (p_173347_ == null) {
            return new BlendMode();
        } else {
            int i = 32774;
            int j = 1;
            int k = 0;
            int l = 1;
            int i1 = 0;
            boolean flag = true;
            boolean flag1 = false;
            if (GsonHelper.isStringValue(p_173347_, "func")) {
                i = BlendMode.stringToBlendFunc(p_173347_.get("func").getAsString());
                if (i != 32774) {
                    flag = false;
                }
            }

            if (GsonHelper.isStringValue(p_173347_, "srcrgb")) {
                j = BlendMode.stringToBlendFactor(p_173347_.get("srcrgb").getAsString());
                if (j != 1) {
                    flag = false;
                }
            }

            if (GsonHelper.isStringValue(p_173347_, "dstrgb")) {
                k = BlendMode.stringToBlendFactor(p_173347_.get("dstrgb").getAsString());
                if (k != 0) {
                    flag = false;
                }
            }

            if (GsonHelper.isStringValue(p_173347_, "srcalpha")) {
                l = BlendMode.stringToBlendFactor(p_173347_.get("srcalpha").getAsString());
                if (l != 1) {
                    flag = false;
                }

                flag1 = true;
            }

            if (GsonHelper.isStringValue(p_173347_, "dstalpha")) {
                i1 = BlendMode.stringToBlendFactor(p_173347_.get("dstalpha").getAsString());
                if (i1 != 0) {
                    flag = false;
                }

                flag1 = true;
            }

            if (flag) {
                return new BlendMode();
            } else {
                return flag1 ? new BlendMode(j, k, l, i1, i) : new BlendMode(j, k, i);
            }
        }
    }

    public void close() {
        Iterator var1 = this.uniforms.iterator();

        while(var1.hasNext()) {
            Uniform uniform = (Uniform)var1.next();
            uniform.close();
        }

        ProgramManager.releaseProgram(this);
    }

    public void clear() {
        RenderSystem.assertOnRenderThread();
        ProgramManager.glUseProgram(0);
        lastProgramId = -1;
        lastAppliedShader = null;
        int i = GlStateManager._getActiveTexture();

        for(int j = 0; j < this.samplerLocations.size(); ++j) {
            if (this.samplerMap.get(this.samplerNames.get(j)) != null) {
                GlStateManager._activeTexture('蓀' + j);
                GlStateManager._bindTexture(0);
            }
        }

        GlStateManager._activeTexture(i);
    }

    public void apply() {
        RenderSystem.assertOnRenderThread();
        this.dirty = false;
        lastAppliedShader = this;
        this.blend.apply();
        if (this.programId != lastProgramId) {
            ProgramManager.glUseProgram(this.programId);
            lastProgramId = this.programId;
        }

        int i = GlStateManager._getActiveTexture();

        for(int j = 0; j < this.samplerLocations.size(); ++j) {
            String s = (String)this.samplerNames.get(j);
            if (this.samplerMap.get(s) != null) {
                int k = Uniform.glGetUniformLocation(this.programId, s);
                Uniform.uploadInteger(k, j);
                RenderSystem.activeTexture('蓀' + j);
                Object object = this.samplerMap.get(s);
                int l = -1;
                if (object instanceof RenderTarget) {
                    l = ((RenderTarget)object).getColorTextureId();
                } else if (object instanceof AbstractTexture) {
                    l = ((AbstractTexture)object).getId();
                } else if (object instanceof Integer) {
                    l = (Integer)object;
                }

                if (l != -1) {
                    RenderSystem.bindTexture(l);
                }
            }
        }

        GlStateManager._activeTexture(i);
        Iterator var7 = this.uniforms.iterator();

        while(var7.hasNext()) {
            Uniform uniform = (Uniform)var7.next();
            uniform.upload();
        }

    }

    public void markDirty() {
        this.dirty = true;
    }

    @Nullable
    public Uniform getUniform(String p_173349_) {
        RenderSystem.assertOnRenderThread();
        return (Uniform)this.uniformMap.get(p_173349_);
    }

    public AbstractUniform safeGetUniform(String p_173357_) {
        RenderSystem.assertOnGameThread();
        Uniform uniform = this.getUniform(p_173357_);
        return (AbstractUniform)(uniform == null ? DUMMY_UNIFORM : uniform);
    }

    private void updateLocations() {
        RenderSystem.assertOnRenderThread();
        IntList intlist = new IntArrayList();

        int l;
        for(l = 0; l < this.samplerNames.size(); ++l) {
            String s = (String)this.samplerNames.get(l);
            int j = Uniform.glGetUniformLocation(this.programId, s);
            if (j == -1) {
                LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", this.name, s);
                this.samplerMap.remove(s);
                intlist.add(l);
            } else {
                this.samplerLocations.add(j);
            }
        }

        for(l = intlist.size() - 1; l >= 0; --l) {
            int i1 = intlist.getInt(l);
            this.samplerNames.remove(i1);
        }

        Iterator var6 = this.uniforms.iterator();

        while(var6.hasNext()) {
            Uniform uniform = (Uniform)var6.next();
            String s1 = uniform.getName();
            int k = Uniform.glGetUniformLocation(this.programId, s1);
            if (k == -1) {
                LOGGER.warn("Shader {} could not find uniform named {} in the specified shader program.", this.name, s1);
            } else {
                this.uniformLocations.add(k);
                uniform.setLocation(k);
                this.uniformMap.put(s1, uniform);
            }
        }

    }

    private void parseSamplerNode(JsonElement p_173345_) {
        JsonObject jsonobject = GsonHelper.convertToJsonObject(p_173345_, "sampler");
        String s = GsonHelper.getAsString(jsonobject, "name");
        if (!GsonHelper.isStringValue(jsonobject, "file")) {
            this.samplerMap.put(s, (Object)null);
            this.samplerNames.add(s);
        } else {
            this.samplerNames.add(s);
        }

    }

    public void setSampler(String p_173351_, Object p_173352_) {
        this.samplerMap.put(p_173351_, p_173352_);
        this.markDirty();
    }

    private void parseUniformNode(JsonElement p_173355_) throws ChainedJsonException {
        JsonObject jsonobject = GsonHelper.convertToJsonObject(p_173355_, "uniform");
        String s = GsonHelper.getAsString(jsonobject, "name");
        int i = Uniform.getTypeFromString(GsonHelper.getAsString(jsonobject, "type"));
        int j = GsonHelper.getAsInt(jsonobject, "count");
        float[] afloat = new float[Math.max(j, 16)];
        JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "values");
        if (jsonarray.size() != j && jsonarray.size() > 1) {
            throw new ChainedJsonException("Invalid amount of values specified (expected " + j + ", found " + jsonarray.size() + ")");
        } else {
            int k = 0;

            for(Iterator var9 = jsonarray.iterator(); var9.hasNext(); ++k) {
                JsonElement jsonelement = (JsonElement)var9.next();

                try {
                    afloat[k] = GsonHelper.convertToFloat(jsonelement, "value");
                } catch (Exception var13) {
                    Exception exception = var13;
                    ChainedJsonException chainedjsonexception = ChainedJsonException.forException(exception);
                    chainedjsonexception.prependJsonKey("values[" + k + "]");
                    throw chainedjsonexception;
                }
            }

            if (j > 1 && jsonarray.size() == 1) {
                while(k < j) {
                    afloat[k] = afloat[0];
                    ++k;
                }
            }

            int l = j > 1 && j <= 4 && i < 8 ? j - 1 : 0;
            Uniform uniform = new Uniform(s, i + l, j, this);
            if (i <= 3) {
                uniform.setSafe((int)afloat[0], (int)afloat[1], (int)afloat[2], (int)afloat[3]);
            } else if (i <= 7) {
                uniform.setSafe(afloat[0], afloat[1], afloat[2], afloat[3]);
            } else {
                uniform.set(Arrays.copyOfRange(afloat, 0, j));
            }

            this.uniforms.add(uniform);
        }
    }

    public Program getVertexProgram() {
        return this.vertexProgram;
    }

    public Program getFragmentProgram() {
        return this.fragmentProgram;
    }

    public void attachToProgram() {
        this.fragmentProgram.attachToShader(this);
        this.vertexProgram.attachToShader(this);
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.programId;
    }
}
