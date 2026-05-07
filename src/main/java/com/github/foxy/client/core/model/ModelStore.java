package com.github.foxy.client.core.model;

import com.github.foxy.client.core.RenderResourceReuse;
import com.github.foxy.client.core.gl.GlBuffer;
import com.github.foxy.client.core.gl.GlTexture;

import static org.lwjgl.opengl.GL11C.GL_NEAREST;
import static org.lwjgl.opengl.GL14C.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL12C.GL_TEXTURE_MAX_LOD;
import static org.lwjgl.opengl.GL12C.GL_TEXTURE_MIN_LOD;
import static org.lwjgl.opengl.GL30C.glBindBufferBase;
import static org.lwjgl.opengl.GL33C.glBindSampler;
import static org.lwjgl.opengl.GL33C.glDeleteSamplers;
import static org.lwjgl.opengl.GL33C.glGenSamplers;
import static org.lwjgl.opengl.GL33C.glSamplerParameteri;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL45C.glBindTextureUnit;

/**
 * GPU-side block-model registry: three resources keyed by block-state id.
 *
 * <h2>Buffers</h2>
 * <ul>
 *   <li>{@link #modelBuffer} &mdash; one {@code MODEL_SIZE}-byte record per block-state
 *       id, holding face metadata, UV bounds, etc.</li>
 *   <li>{@link #modelColourBuffer} &mdash; one 4-byte sRGB colour per block-state id,
 *       used for the LOD shader's "average colour" sampling at far distances.</li>
 *   <li>{@link #textures} &mdash; the model texture atlas (1 face per slot &times;
 *       6 faces per model). Pulled from {@link RenderResourceReuse} so multiple
 *       renderer instances can share the same atlas across world reloads.</li>
 * </ul>
 *
 * <h2>Sampler</h2>
 * <p>{@link #blockSampler} is a lazy {@code glGenSamplers} object configured for
 * point-mag / nearest-mip-linear filtering with a min-LOD of 0 and a max-LOD equal
 * to {@code log2(MODEL_TEXTURE_SIZE)} so the GPU never reaches into the half-empty
 * mip levels at the chain's tail.</p>
 *
 * <p>Cleanroom note: same shape as upstream Voxy with English javadoc.</p>
 */
public class ModelStore {

    /** Size of one per-block-state metadata record, in bytes. */
    public static final int MODEL_SIZE = 64;

    /** Capacity of the model registry; one entry per block-state id. */
    public static final int MODEL_CAPACITY = 1 << 16;

    final GlBuffer modelBuffer;
    final GlBuffer modelColourBuffer;
    final GlTexture textures;

    /** Sampler used when reading the model atlas; tuned for the registry's mip chain. */
    public final int blockSampler = glGenSamplers();

    public ModelStore() {
        this.modelBuffer = new GlBuffer((long) MODEL_SIZE * MODEL_CAPACITY).name("ModelData");
        this.modelColourBuffer = new GlBuffer(4L * MODEL_CAPACITY).name("ModelColour");
        this.textures = RenderResourceReuse.getOrCreateModelStoreTextureAtlas();

        int maxLod = Integer.numberOfTrailingZeros(ModelFactory.MODEL_TEXTURE_SIZE);
        glSamplerParameteri(this.blockSampler, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glSamplerParameteri(this.blockSampler, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glSamplerParameteri(this.blockSampler, GL_TEXTURE_MIN_LOD, 0);
        glSamplerParameteri(this.blockSampler, GL_TEXTURE_MAX_LOD, maxLod);
    }

    /** Tears down the GL buffers, returns the atlas to the reuse pool, deletes the sampler. */
    public void free() {
        this.modelBuffer.free();
        this.modelColourBuffer.free();
        RenderResourceReuse.giveBackModelStoreTextureAtlas(this.textures);
        glDeleteSamplers(this.blockSampler);
    }

    /**
     * Binds the three resources at the requested SSBO / texture unit indices for the
     * next shader to sample.
     *
     * @param modelBindingIndex   SSBO binding for {@link #modelBuffer}
     * @param colourBindingIndex  SSBO binding for {@link #modelColourBuffer}
     * @param textureBindingIndex texture unit for the model atlas + sampler
     */
    public void bind(int modelBindingIndex, int colourBindingIndex, int textureBindingIndex) {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, modelBindingIndex, this.modelBuffer.id);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, colourBindingIndex, this.modelColourBuffer.id);
        glBindTextureUnit(textureBindingIndex, this.textures.id);
        glBindSampler(textureBindingIndex, this.blockSampler);
    }
}
