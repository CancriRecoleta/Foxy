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
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.Effect;
import com.mojang.blaze3d.shaders.EffectProgram;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.shaders.Program.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class EffectInstance implements Effect, AutoCloseable {
    private static final String EFFECT_SHADER_PATH = "shaders/program/";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final AbstractUniform DUMMY_UNIFORM = new AbstractUniform();
    private static final boolean ALWAYS_REAPPLY = true;
    private static EffectInstance lastAppliedEffect;
    private static int lastProgramId = -1;
    private final Map<String, IntSupplier> samplerMap = Maps.newHashMap();
    private final List<String> samplerNames = Lists.newArrayList();
    private final List<Integer> samplerLocations = Lists.newArrayList();
    private final List<Uniform> uniforms = Lists.newArrayList();
    private final List<Integer> uniformLocations = Lists.newArrayList();
    private final Map<String, Uniform> uniformMap = Maps.newHashMap();
    private final int programId;
    private final String name;
    private boolean dirty;
    private final BlendMode blend;
    private final List<Integer> attributes;
    private final List<String> attributeNames;
    private final EffectProgram vertexProgram;
    private final EffectProgram fragmentProgram;

    public EffectInstance(ResourceManager p_108941_, String p_108942_) throws IOException {
        ResourceLocation rl = ResourceLocation.tryParse(p_108942_);
        ResourceLocation resourcelocation = new ResourceLocation(rl.getNamespace(), "shaders/program/" + rl.getPath() + ".json");
        this.name = p_108942_;
        Resource resource = p_108941_.getResourceOrThrow(resourcelocation);

        try {
            Reader reader = resource.openAsReader();

            try {
                JsonObject jsonobject = GsonHelper.parse((Reader)reader);
                String s = GsonHelper.getAsString(jsonobject, "vertex");
                String s1 = GsonHelper.getAsString(jsonobject, "fragment");
                JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "samplers", (JsonArray)null);
                if (jsonarray != null) {
                    int i = 0;

                    for(Iterator var12 = jsonarray.iterator(); var12.hasNext(); ++i) {
                        JsonElement jsonelement = (JsonElement)var12.next();

                        try {
                            this.parseSamplerNode(jsonelement);
                        } catch (Exception var21) {
                            Exception exception2 = var21;
                            ChainedJsonException chainedjsonexception1 = ChainedJsonException.forException(exception2);
                            chainedjsonexception1.prependJsonKey("samplers[" + i + "]");
                            throw chainedjsonexception1;
                        }
                    }
                }

                JsonArray jsonarray1 = GsonHelper.getAsJsonArray(jsonobject, "attributes", (JsonArray)null);
                Iterator var29;
                if (jsonarray1 != null) {
                    int j = 0;
                    this.attributes = Lists.newArrayListWithCapacity(jsonarray1.size());
                    this.attributeNames = Lists.newArrayListWithCapacity(jsonarray1.size());

                    for(var29 = jsonarray1.iterator(); var29.hasNext(); ++j) {
                        JsonElement jsonelement1 = (JsonElement)var29.next();

                        try {
                            this.attributeNames.add(GsonHelper.convertToString(jsonelement1, "attribute"));
                        } catch (Exception var20) {
                            ChainedJsonException chainedjsonexception2 = ChainedJsonException.forException(var20);
                            chainedjsonexception2.prependJsonKey("attributes[" + j + "]");
                            throw chainedjsonexception2;
                        }
                    }
                } else {
                    this.attributes = null;
                    this.attributeNames = null;
                }

                JsonArray jsonarray2 = GsonHelper.getAsJsonArray(jsonobject, "uniforms", (JsonArray)null);
                if (jsonarray2 != null) {
                    int k = 0;

                    for(Iterator var32 = jsonarray2.iterator(); var32.hasNext(); ++k) {
                        JsonElement jsonelement2 = (JsonElement)var32.next();

                        try {
                            this.parseUniformNode(jsonelement2);
                        } catch (Exception var19) {
                            ChainedJsonException chainedjsonexception3 = ChainedJsonException.forException(var19);
                            chainedjsonexception3.prependJsonKey("uniforms[" + k + "]");
                            throw chainedjsonexception3;
                        }
                    }
                }

                this.blend = parseBlendNode(GsonHelper.getAsJsonObject(jsonobject, "blend", (JsonObject)null));
                this.vertexProgram = getOrCreate(p_108941_, Type.VERTEX, s);
                this.fragmentProgram = getOrCreate(p_108941_, Type.FRAGMENT, s1);
                this.programId = ProgramManager.createProgram();
                ProgramManager.linkShader(this);
                this.updateLocations();
                if (this.attributeNames != null) {
                    var29 = this.attributeNames.iterator();

                    while(var29.hasNext()) {
                        String s2 = (String)var29.next();
                        int l = Uniform.glGetAttribLocation(this.programId, s2);
                        this.attributes.add(l);
                    }
                }
            } catch (Throwable var22) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Throwable var18) {
                        var22.addSuppressed(var18);
                    }
                }

                throw var22;
            }

            if (reader != null) {
                reader.close();
            }
        } catch (Exception var23) {
            Exception exception3 = var23;
            ChainedJsonException chainedjsonexception = ChainedJsonException.forException(exception3);
            String var10001 = resourcelocation.getPath();
            chainedjsonexception.setFilenameAndFlush(var10001 + " (" + resource.sourcePackId() + ")");
            throw chainedjsonexception;
        }

        this.markDirty();
    }

    public static EffectProgram getOrCreate(ResourceManager p_172567_, Program.Type p_172568_, String p_172569_) throws IOException {
        Program program = (Program)p_172568_.getPrograms().get(p_172569_);
        if (program != null && !(program instanceof EffectProgram)) {
            throw new InvalidClassException("Program is not of type EffectProgram");
        } else {
            EffectProgram effectprogram;
            if (program == null) {
                ResourceLocation rl = ResourceLocation.tryParse(p_172569_);
                String var10002 = rl.getNamespace();
                String var10003 = rl.getPath();
                ResourceLocation resourcelocation = new ResourceLocation(var10002, "shaders/program/" + var10003 + p_172568_.getExtension());
                Resource resource = p_172567_.getResourceOrThrow(resourcelocation);
                InputStream inputstream = resource.open();

                try {
                    effectprogram = EffectProgram.compileShader(p_172568_, p_172569_, inputstream, resource.sourcePackId());
                } catch (Throwable var12) {
                    if (inputstream != null) {
                        try {
                            inputstream.close();
                        } catch (Throwable var11) {
                            var12.addSuppressed(var11);
                        }
                    }

                    throw var12;
                }

                if (inputstream != null) {
                    inputstream.close();
                }
            } else {
                effectprogram = (EffectProgram)program;
            }

            return effectprogram;
        }
    }

    public static BlendMode parseBlendNode(@Nullable JsonObject p_108951_) {
        if (p_108951_ == null) {
            return new BlendMode();
        } else {
            int i = 32774;
            int j = 1;
            int k = 0;
            int l = 1;
            int i1 = 0;
            boolean flag = true;
            boolean flag1 = false;
            if (GsonHelper.isStringValue(p_108951_, "func")) {
                i = BlendMode.stringToBlendFunc(p_108951_.get("func").getAsString());
                if (i != 32774) {
                    flag = false;
                }
            }

            if (GsonHelper.isStringValue(p_108951_, "srcrgb")) {
                j = BlendMode.stringToBlendFactor(p_108951_.get("srcrgb").getAsString());
                if (j != 1) {
                    flag = false;
                }
            }

            if (GsonHelper.isStringValue(p_108951_, "dstrgb")) {
                k = BlendMode.stringToBlendFactor(p_108951_.get("dstrgb").getAsString());
                if (k != 0) {
                    flag = false;
                }
            }

            if (GsonHelper.isStringValue(p_108951_, "srcalpha")) {
                l = BlendMode.stringToBlendFactor(p_108951_.get("srcalpha").getAsString());
                if (l != 1) {
                    flag = false;
                }

                flag1 = true;
            }

            if (GsonHelper.isStringValue(p_108951_, "dstalpha")) {
                i1 = BlendMode.stringToBlendFactor(p_108951_.get("dstalpha").getAsString());
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
        lastAppliedEffect = null;

        for(int i = 0; i < this.samplerLocations.size(); ++i) {
            if (this.samplerMap.get(this.samplerNames.get(i)) != null) {
                GlStateManager._activeTexture('蓀' + i);
                GlStateManager._bindTexture(0);
            }
        }

    }

    public void apply() {
        RenderSystem.assertOnGameThread();
        this.dirty = false;
        lastAppliedEffect = this;
        this.blend.apply();
        if (this.programId != lastProgramId) {
            ProgramManager.glUseProgram(this.programId);
            lastProgramId = this.programId;
        }

        for(int i = 0; i < this.samplerLocations.size(); ++i) {
            String s = (String)this.samplerNames.get(i);
            IntSupplier intsupplier = (IntSupplier)this.samplerMap.get(s);
            if (intsupplier != null) {
                RenderSystem.activeTexture('蓀' + i);
                int j = intsupplier.getAsInt();
                if (j != -1) {
                    RenderSystem.bindTexture(j);
                    Uniform.uploadInteger((Integer)this.samplerLocations.get(i), i);
                }
            }
        }

        Iterator var5 = this.uniforms.iterator();

        while(var5.hasNext()) {
            Uniform uniform = (Uniform)var5.next();
            uniform.upload();
        }

    }

    public void markDirty() {
        this.dirty = true;
    }

    @Nullable
    public Uniform getUniform(String p_108953_) {
        RenderSystem.assertOnRenderThread();
        return (Uniform)this.uniformMap.get(p_108953_);
    }

    public AbstractUniform safeGetUniform(String p_108961_) {
        RenderSystem.assertOnGameThread();
        Uniform uniform = this.getUniform(p_108961_);
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
            this.samplerNames.remove(intlist.getInt(l));
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

    private void parseSamplerNode(JsonElement p_108949_) {
        JsonObject jsonobject = GsonHelper.convertToJsonObject(p_108949_, "sampler");
        String s = GsonHelper.getAsString(jsonobject, "name");
        if (!GsonHelper.isStringValue(jsonobject, "file")) {
            this.samplerMap.put(s, (IntSupplier)null);
            this.samplerNames.add(s);
        } else {
            this.samplerNames.add(s);
        }

    }

    public void setSampler(String p_108955_, IntSupplier p_108956_) {
        if (this.samplerMap.containsKey(p_108955_)) {
            this.samplerMap.remove(p_108955_);
        }

        this.samplerMap.put(p_108955_, p_108956_);
        this.markDirty();
    }

    private void parseUniformNode(JsonElement p_108959_) throws ChainedJsonException {
        JsonObject jsonobject = GsonHelper.convertToJsonObject(p_108959_, "uniform");
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
                uniform.set(afloat);
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
        this.fragmentProgram.attachToEffect(this);
        this.vertexProgram.attachToEffect(this);
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.programId;
    }
}
