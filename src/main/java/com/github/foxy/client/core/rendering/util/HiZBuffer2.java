package com.github.foxy.client.core.rendering.util;

import com.github.foxy.client.core.gl.GlFramebuffer;
import com.github.foxy.client.core.gl.GlTexture;
import com.github.foxy.client.core.gl.GlVertexArray;
import com.github.foxy.client.core.gl.shader.Shader;
import com.github.foxy.client.core.gl.shader.ShaderType;

import static org.lwjgl.opengl.ARBShaderImageLoadStore.GL_TEXTURE_FETCH_BARRIER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.GL_NEAREST;
import static org.lwjgl.opengl.GL11C.GL_NONE;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL11C.glGetInteger;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14C.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL14C.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL30C.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30C.GL_DRAW_FRAMEBUFFER_BINDING;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.GL_R32F;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;
import static org.lwjgl.opengl.GL33C.glBindSampler;
import static org.lwjgl.opengl.GL33C.glDeleteSamplers;
import static org.lwjgl.opengl.GL33C.glGenSamplers;
import static org.lwjgl.opengl.GL33C.glSamplerParameteri;
import static org.lwjgl.opengl.GL42C.GL_FRAMEBUFFER_BARRIER_BIT;
import static org.lwjgl.opengl.GL42C.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL42C.glBindImageTexture;
import static org.lwjgl.opengl.GL42C.glMemoryBarrier;
import static org.lwjgl.opengl.GL43C.glDispatchCompute;
import static org.lwjgl.opengl.GL45C.glBindTextureUnit;
import static org.lwjgl.opengl.GL45C.glBindVertexArray;
import static org.lwjgl.opengl.GL45C.glNamedFramebufferDrawBuffer;
import static org.lwjgl.opengl.GL45C.glTextureBarrier;
import static org.lwjgl.opengl.GL45C.glTextureParameteri;
import static org.lwjgl.opengl.GL45C.glUniform1i;
import static org.lwjgl.opengl.GL45C.glUniform2f;

/**
 * Compute-shader-based HiZ pyramid builder &mdash; the experimental sibling of
 * {@link HiZBuffer} that trades a draw-per-level fragment pass for a single compute
 * dispatch.
 *
 * <h2>Why this variant exists</h2>
 * <p>{@link HiZBuffer} renders a full-screen quad once per mip level and relies on
 * fragment / texture barriers between draws to stay correct. That's six to nine draw
 * calls plus barriers for a 1080p depth chain. This implementation does:</p>
 * <ol>
 *   <li>One full-screen draw to seed level 0 from the source depth texture (so the
 *       compute path doesn't have to handle the format conversion).</li>
 *   <li>One compute dispatch that writes mip levels 1..6 in a single launch by
 *       binding each level as a writable image and writing them through atomic-free
 *       {@code imageStore} calls in shared memory.</li>
 * </ol>
 * <p>The cost is a hard cap of seven mip levels (matches the compute shader's
 * tuning); HiZBuffer's pure-fragment path scales to whatever
 * {@code log2(max(width, height))} demands. In exchange the compute path eliminates
 * almost all mip-to-mip barriers and tends to win on modern GPUs.</p>
 *
 * <h2>Format</h2>
 * <p>Defaults to {@code GL_R32F} so the compute kernel can write through
 * {@code imageStore} with no format reinterpretation. The seed pass converts the
 * source depth ({@code GL_DEPTH_COMPONENT*}) to single-channel float in the fragment
 * shader.</p>
 *
 * <h2>Shader assets</h2>
 * <ul>
 *   <li>{@code Foxy:hiz/blit.vsh} / {@code Foxy:hiz/blit.fsh} (with
 *       {@code OUTPUT_COLOUR} define) &mdash; level-0 seed pass.</li>
 *   <li>{@code Foxy:hiz/hiz.comp} &mdash; compute mip builder.</li>
 * </ul>
 *
 * <p>Cleanroom note: same algorithm as upstream Voxy. The cleanroom rewrite
 * tightens the static-import set, hoists the magic numbers ({@code 7} mip levels,
 * {@code 64x64} workgroup), notes the fragment + compute split in javadoc, and
 * scopes GL state save/restore via try/finally where applicable.</p>
 */
public final class HiZBuffer2 {

    /** Maximum number of mip levels the compute shader writes; matches its layout. */
    public static final int MAX_LEVELS = 7;

    /** Compute shader workgroup tile size in pixels per dispatch axis. */
    private static final int COMPUTE_TILE_SIZE = 64;

    private final Shader hizMip;
    private final Shader hizSeed;
    private final GlFramebuffer fb = new GlFramebuffer().name("HiZ");
    private final int sampler = glGenSamplers();
    private final int colourFormat;
    private GlTexture texture;
    private int levels;
    private int width;
    private int height;

    /** Defaults to {@code GL_R32F}. */
    public HiZBuffer2() {
        this(GL_R32F);
    }

    /**
     * @param colourFormat sized internal colour format for the HiZ texture; must be
     *                     compatible with the {@code Foxy:hiz/hiz.comp} {@code imageStore}
     *                     calls (default {@code GL_R32F})
     */
    public HiZBuffer2(int colourFormat) {
        glNamedFramebufferDrawBuffer(this.fb.id, GL_COLOR_ATTACHMENT0);
        this.colourFormat = colourFormat;
        this.hizMip = Shader.make()
                .add(ShaderType.COMPUTE, "Foxy:hiz/hiz.comp")
                .compile()
                .name("HiZ.Mip");
        this.hizSeed = Shader.make()
                .add(ShaderType.VERTEX, "Foxy:hiz/blit.vsh")
                .add(ShaderType.FRAGMENT, "Foxy:hiz/blit.fsh")
                .define("OUTPUT_COLOUR")
                .compile()
                .name("HiZ.Seed");
    }

    private void allocate(int width, int height) {
        // Cap by the compute kernel's hard limit; the chain doesn't need more for the
        // occlusion query sizes the renderer cares about anyway.
        this.levels = Math.min(MAX_LEVELS,
                (int) Math.ceil(Math.log(Math.max(width, height)) / Math.log(2)));
        this.texture = new GlTexture().store(this.colourFormat, this.levels, width, height).name("HiZ");
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
        this.fb.attach(GL_COLOR_ATTACHMENT0, this.texture, 0).verify();
    }

    /**
     * Builds the HiZ chain by seeding level 0 from {@code srcDepthTex} via a fragment
     * pass, then dispatching one compute kernel that writes levels 1..{@link #MAX_LEVELS}-1.
     * Resizes the HiZ texture to the largest power-of-two not exceeding
     * {@code (width, height)} when needed.
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

        // ---- pass 1: fragment seed of level 0 -----------------------------------------
        int boundFB = glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        glBindVertexArray(GlVertexArray.STATIC_VAO);
        this.hizSeed.bind();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fb.id);
        glDisable(GL_DEPTH_TEST);

        glBindTextureUnit(0, srcDepthTex);
        glBindSampler(0, this.sampler);
        glUniform1i(0, 0);
        glViewport(0, 0, this.width, this.height);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);

        glTextureBarrier();
        glMemoryBarrier(GL_FRAMEBUFFER_BARRIER_BIT | GL_TEXTURE_FETCH_BARRIER_BIT);
        glBindFramebuffer(GL_FRAMEBUFFER, boundFB);
        glViewport(0, 0, width, height);
        glBindVertexArray(0);

        // ---- pass 2: compute mip chain levels 1..MAX_LEVELS-1 ------------------------
        this.hizMip.bind();
        glUniform2f(0, 1f / this.width, 1f / this.height);
        glBindTextureUnit(0, this.texture.id);
        glBindSampler(0, this.sampler);
        // Bind every output level the compute shader will write through imageStore.
        for (int i = 1; i < MAX_LEVELS; i++) {
            glBindImageTexture(i, this.texture.id, i, false, 0, GL_WRITE_ONLY, this.colourFormat);
        }
        glDispatchCompute(this.width / COMPUTE_TILE_SIZE, this.height / COMPUTE_TILE_SIZE, 1);

        // Detach so subsequent passes don't see stale bindings.
        glBindSampler(0, 0);
        for (int i = 0; i < MAX_LEVELS; i++) {
            glBindTextureUnit(i, 0);
        }
    }

    /** Underlying HiZ texture id; throws if {@link #buildMipChain} hasn't run yet. */
    public int getHizTextureId() {
        if (this.texture == null) {
            throw new IllegalStateException("HiZBuffer2 has no texture yet; call buildMipChain first");
        }
        return this.texture.id;
    }

    /**
     * Packs (log2(width), log2(height)) into a single int for shader uniforms.
     *
     * <p>Both dimensions are powers of two by construction, so {@code numberOfTrailingZeros}
     * is the log2.</p>
     */
    public int getPackedLevels() {
        return (Integer.numberOfTrailingZeros(this.width) << 16)
                | Integer.numberOfTrailingZeros(this.height);
    }

    /** Tears down both shaders, the FBO, the texture, and the sampler. */
    public void free() {
        this.fb.free();
        if (this.texture != null) {
            this.texture.free();
            this.texture = null;
        }
        glDeleteSamplers(this.sampler);
        this.hizSeed.free();
        this.hizMip.free();
    }
}
