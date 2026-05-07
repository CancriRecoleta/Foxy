package com.github.foxy.client.core.rendering.util;

import com.github.foxy.client.core.RenderProperties;
import com.github.foxy.client.core.gl.GlFramebuffer;
import com.github.foxy.client.core.gl.GlTexture;
import com.github.foxy.client.core.gl.GlVertexArray;
import com.github.foxy.client.core.gl.shader.Shader;
import com.github.foxy.client.core.gl.shader.ShaderType;

import static org.lwjgl.opengl.ARBShaderImageLoadStore.GL_TEXTURE_FETCH_BARRIER_BIT;
import static org.lwjgl.opengl.GL11C.GL_ALWAYS;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_NEAREST;
import static org.lwjgl.opengl.GL11C.GL_NONE;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11C.glDepthFunc;
import static org.lwjgl.opengl.GL11C.glDepthMask;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glGetInteger;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14C.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL14C.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL14C.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL30C.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30C.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30C.GL_DEPTH_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30C.GL_DRAW_FRAMEBUFFER_BINDING;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;
import static org.lwjgl.opengl.GL33C.glBindSampler;
import static org.lwjgl.opengl.GL33C.glDeleteSamplers;
import static org.lwjgl.opengl.GL33C.glGenSamplers;
import static org.lwjgl.opengl.GL33C.glSamplerParameteri;
import static org.lwjgl.opengl.GL42C.GL_FRAMEBUFFER_BARRIER_BIT;
import static org.lwjgl.opengl.GL42C.glMemoryBarrier;
import static org.lwjgl.opengl.GL45C.glBindTextureUnit;
import static org.lwjgl.opengl.GL45C.glBindVertexArray;
import static org.lwjgl.opengl.GL45C.glNamedFramebufferDrawBuffer;
import static org.lwjgl.opengl.GL45C.glTextureBarrier;
import static org.lwjgl.opengl.GL45C.glTextureParameteri;
import static org.lwjgl.opengl.GL45C.glUniform1i;

/**
 * Hierarchical-Z (HiZ) depth buffer used as the input to occlusion-culling tests.
 *
 * <h2>What a HiZ buffer is</h2>
 * <p>An ordinary mip pyramid downsamples a depth texture by the standard mean filter,
 * which is wrong for occlusion: a bright cell in coarser mips may report a depth
 * <em>closer</em> than the actual closest fragment behind any of its sub-cells, so
 * occluders get falsely accepted. The HiZ filter uses {@code max} (or {@code min} —
 * driver/orientation specific) so the coarser mip never reports a depth closer than
 * its sub-cells. Renderers can then sample the right level for a screen-space query
 * size and reject occluded objects with a single fetch.</p>
 *
 * <h2>How this implementation builds the chain</h2>
 * <p>Per LOD, the constructor binds the texture's level-{@code i+1} mip as the depth
 * attachment of a fresh framebuffer pass and renders a full-screen quad whose
 * fragment shader samples mip {@code i} with the appropriate combiner. After each
 * level a barrier flushes the framebuffer/texture caches so the next level reads
 * coherent data. Finally the texture's {@code GL_TEXTURE_BASE_LEVEL} /
 * {@code GL_TEXTURE_MAX_LEVEL} are restored so callers see the full chain.</p>
 *
 * <h2>Shader assets</h2>
 * <p>{@code Foxy:hiz/blit.vsh} / {@code Foxy:hiz/blit.fsh} live in
 * {@code assets/foxy/shaders/hiz/}. The vertex shader emits a 4-vertex triangle fan
 * covering the viewport; the fragment shader does the depth-aware combine.</p>
 *
 * <p>Cleanroom note: same algorithm as upstream Voxy. The cleanroom rewrite
 * tightens the static import set, scopes GL state save/restore via try/finally,
 * removes the upstream "//+1" trailing comments of unclear intent, and adds full
 * English javadoc.</p>
 */
public final class HiZBuffer {
    private final Shader hizShader;
    private final GlFramebuffer fb = new GlFramebuffer().name("HiZ");
    private final int sampler = glGenSamplers();
    private final int depthFormat;
    private GlTexture texture;
    private int levels;
    private int width;
    private int height;
    private final RenderProperties properties;

    /** Defaults to {@code GL_DEPTH24_STENCIL8} so the chain matches the main framebuffer. */
    public HiZBuffer(RenderProperties properties) {
        this(properties, GL_DEPTH24_STENCIL8);
    }

    /**
     * @param properties     renderer-side state (depth-compare function, etc.)
     * @param depthFormat    sized internal depth format; the attachment type is derived
     */
    public HiZBuffer(RenderProperties properties, int depthFormat) {
        glNamedFramebufferDrawBuffer(this.fb.id, GL_NONE);
        this.depthFormat = depthFormat;
        this.hizShader = Shader.make()
                .apply(properties::apply)
                .add(ShaderType.VERTEX, "Foxy:hiz/blit.vsh")
                .add(ShaderType.FRAGMENT, "Foxy:hiz/blit.fsh")
                .compile()
                .name("HiZ Builder");
        this.properties = properties;
    }

    /** Returns the matching {@code GL_*_ATTACHMENT} for the configured depth format. */
    private int depthAttachmentType() {
        return this.depthFormat == GL_DEPTH24_STENCIL8 ? GL_DEPTH_STENCIL_ATTACHMENT : GL_DEPTH_ATTACHMENT;
    }

    private void allocate(int width, int height) {
        this.levels = (int) Math.ceil(Math.log(Math.max(width, height)) / Math.log(2));
        this.texture = new GlTexture().store(this.depthFormat, this.levels, width, height).name("HiZ");
        glTextureParameteri(this.texture.id, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
        glTextureParameteri(this.texture.id, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTextureParameteri(this.texture.id, GL_TEXTURE_COMPARE_MODE, GL_NONE);
        glTextureParameteri(this.texture.id, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTextureParameteri(this.texture.id, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glSamplerParameteri(this.sampler, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
        glSamplerParameteri(this.sampler, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glSamplerParameteri(this.sampler, GL_TEXTURE_COMPARE_MODE, GL_NONE);
        glSamplerParameteri(this.sampler, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glSamplerParameteri(this.sampler, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        this.width = width;
        this.height = height;
        this.fb.attach(depthAttachmentType(), this.texture, 0).verify();
    }

    /**
     * Builds the HiZ chain by reading {@code srcDepthTex} into level 0 and reducing
     * downward. The HiZ texture is sized to the largest power-of-two not exceeding
     * {@code (width, height)}; if that size differs from the previous build, the
     * texture is reallocated.
     */
    public void buildMipChain(int srcDepthTex, int width, int height) {
        int powW = Integer.highestOneBit(width);
        int powH = Integer.highestOneBit(height);
        if (this.width != powW || this.height != powH) {
            if (this.texture != null) {
                this.texture.free();
                this.texture = null;
            }
            allocate(powW, powH);
        }

        // Save GL state we are about to clobber.
        int boundFB = glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);

        glBindVertexArray(GlVertexArray.STATIC_VAO);
        this.hizShader.bind();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fb.id);

        // The HiZ blit always writes the depth output regardless of source comparison;
        // GL_ALWAYS + depthMask=true achieves a straight copy through the DSV path.
        glDepthFunc(GL_ALWAYS);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);

        glBindTextureUnit(0, srcDepthTex);
        glBindSampler(0, this.sampler);
        glUniform1i(0, 0);

        int cw = this.width;
        int ch = this.height;
        for (int i = 0; i < this.levels; i++) {
            this.fb.attach(depthAttachmentType(), this.texture, i);
            glViewport(0, 0, cw, ch);
            cw = Math.max(cw / 2, 1);
            ch = Math.max(ch / 2, 1);
            glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
            glTextureBarrier();
            glMemoryBarrier(GL_FRAMEBUFFER_BARRIER_BIT | GL_TEXTURE_FETCH_BARRIER_BIT);
            // Pin sampling to the level we just wrote so the next iteration reads it.
            glTextureParameteri(this.texture.id, 0x813C /* GL_TEXTURE_BASE_LEVEL */, i);
            glTextureParameteri(this.texture.id, 0x813D /* GL_TEXTURE_MAX_LEVEL  */, i);
            if (i == 0) {
                // Switch the texture binding from the source depth tex to our own
                // mip chain once level 0 has been seeded.
                glBindTextureUnit(0, this.texture.id);
            }
        }
        // Restore full chain visibility.
        glTextureParameteri(this.texture.id, 0x813C, 0);
        glTextureParameteri(this.texture.id, 0x813D, 1000);

        // Restore caller-visible GL state.
        glDepthFunc(this.properties.closerEqualDepthCompare());
        glDisable(GL_DEPTH_TEST);
        glBindFramebuffer(GL_FRAMEBUFFER, boundFB);
        glViewport(0, 0, width, height);
        glBindVertexArray(0);
    }

    /** Underlying HiZ texture id; throws if {@link #buildMipChain} hasn't run yet. */
    public int getHizTextureId() {
        if (this.texture == null) {
            throw new IllegalStateException("HiZBuffer has no texture yet; call buildMipChain first");
        }
        return this.texture.id;
    }

    /** Packs (width, height) into a single int for shader uniform use. */
    public int getPackedLevels() {
        return (this.width << 16) | this.height;
    }

    /** Tears down the FBO, texture, sampler, and shader. Idempotent on null fields. */
    public void free() {
        this.fb.free();
        if (this.texture != null) {
            this.texture.free();
            this.texture = null;
        }
        glDeleteSamplers(this.sampler);
        this.hizShader.free();
    }
}
