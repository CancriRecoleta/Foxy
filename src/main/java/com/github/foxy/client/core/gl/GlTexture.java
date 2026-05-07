package com.github.foxy.client.core.gl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_RED;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL11C.GL_RGBA8;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL14C.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL14C.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL30C.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30C.GL_DEPTH32F_STENCIL8;
import static org.lwjgl.opengl.GL30C.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30C.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL30C.GL_DEPTH_STENCIL;
import static org.lwjgl.opengl.GL30C.GL_R32UI;
import static org.lwjgl.opengl.GL30C.GL_RED_INTEGER;
import static org.lwjgl.opengl.GL30C.GL_R32F;
import static org.lwjgl.opengl.GL30C.GL_RG;
import static org.lwjgl.opengl.GL30C.GL_RG16F;
import static org.lwjgl.opengl.GL30C.GL_UNSIGNED_INT_24_8;
import static org.lwjgl.opengl.GL30C.GL_FLOAT_32_UNSIGNED_INT_24_8_REV;
import static org.lwjgl.opengl.GL45C.glCreateTextures;
import static org.lwjgl.opengl.GL45C.glDeleteTextures;
import static org.lwjgl.opengl.GL45C.glTextureStorage2D;
import static org.lwjgl.opengl.GL45C.glTextureView;
import static org.lwjgl.opengl.GL45C.nglClearTexImage;
import static org.lwjgl.opengl.GL11C.glGenTextures;

/**
 * DSA wrapper around a GL texture object ({@code glCreateTextures}).
 *
 * <p>Storage allocation is two-step on purpose: the constructor creates the name only,
 * {@link #store(int, int, int, int)} allocates immutable storage. This matches the
 * pattern used by {@code glCreateTextures} consumers that need to attach views or
 * labels before the storage call.</p>
 *
 * <p>Tracks live texture count and an estimated total byte size globally for diagnostics.</p>
 */
public final class GlTexture extends GlObject {

    private static final AtomicInteger COUNT = new AtomicInteger();
    private static final AtomicLong ESTIMATED_TOTAL_SIZE = new AtomicLong();

    /** Underlying GL texture name. */
    public final int id;
    private final int type;

    private int format;
    private int width;
    private int height;
    private int levels;
    private boolean hasAllocated;

    /** Defaults to {@code GL_TEXTURE_2D}. */
    public GlTexture() { this(GL_TEXTURE_2D); }

    /** Creates a fresh texture name of the given target type via DSA. */
    public GlTexture(int type) {
        this.id = glCreateTextures(type);
        this.type = type;
        COUNT.incrementAndGet();
    }

    /** Internal constructor for view textures, which must use the legacy {@code glGenTextures}. */
    private GlTexture(int type, boolean useGenTextures) {
        this.id = useGenTextures ? glGenTextures() : glCreateTextures(type);
        this.type = type;
        COUNT.incrementAndGet();
    }

    /**
     * Allocates immutable 2D storage of size {@code (width, height)} with {@code levels}
     * mip levels. Throws on second invocation; immutable storage is allocated once.
     */
    public GlTexture store(int format, int levels, int width, int height) {
        if (this.hasAllocated) throw new IllegalStateException("Texture already allocated");
        this.hasAllocated = true;
        this.format = format;
        this.width = width;
        this.height = height;
        this.levels = levels;
        if (this.type != GL_TEXTURE_2D) {
            // Cleanroom port covers what the renderer actually needs today; extending
            // to 3D / array / cubemap textures is an additive constructor + store overload.
            throw new IllegalStateException("Only GL_TEXTURE_2D is supported by GlTexture#store");
        }
        glTextureStorage2D(this.id, levels, format, width, height);
        ESTIMATED_TOTAL_SIZE.addAndGet(estimatedSize());
        return this;
    }

    /**
     * Creates a sibling texture sharing this one's storage but viewable as a single
     * level / layer. Caller owns the returned object.
     */
    public GlTexture createView() {
        assertAllocated();
        var view = new GlTexture(this.type, true);
        glTextureView(view.id, this.type, this.id, this.format, 0, 1, 0, 1);
        return view;
    }

    @Override
    public void free() {
        free0();
        if (this.hasAllocated) {
            ESTIMATED_TOTAL_SIZE.addAndGet(-estimatedSize());
            this.hasAllocated = false;
        }
        glDeleteTextures(this.id);
        COUNT.decrementAndGet();
    }

    /** Optional debug label. */
    public GlTexture name(String label) { assertAllocated(); return GlDebug.name(label, this); }

    public static int getCount() { return COUNT.get(); }

    public static long getEstimatedTotalSize() { return ESTIMATED_TOTAL_SIZE.get(); }

    /** Allocated width in pixels. */
    public int getWidth() { assertAllocated(); return this.width; }
    /** Allocated height in pixels. */
    public int getHeight() { assertAllocated(); return this.height; }
    /** Number of mip levels. */
    public int getLevels() { assertAllocated(); return this.levels; }
    /** Sized internal format, e.g. {@code GL_RGBA8}. */
    public int getFormat() { assertAllocated(); return this.format; }

    /**
     * Returns the matching pixel-transfer base format for {@link #getFormat()}.
     * Used by {@code glClearTexImage} / {@code glTextureSubImage2D}.
     */
    public int getPixelTransferFormat() {
        assertAllocated();
        return switch (this.format) {
            case GL_RGBA8 -> GL_RGBA;
            case GL_RG16F -> GL_RG;
            case GL_R32UI -> GL_RED_INTEGER;
            case GL_R32F -> GL_RED;
            case GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT32F, GL_DEPTH_COMPONENT32 -> GL_DEPTH_COMPONENT;
            case GL_DEPTH24_STENCIL8, GL_DEPTH32F_STENCIL8 -> GL_DEPTH_STENCIL;
            default -> throw new IllegalStateException("Unhandled internal format 0x" + Integer.toHexString(this.format));
        };
    }

    private long estimatedSize() {
        long elemSize = switch (this.format) {
            case GL_R32UI, GL_RGBA8, GL_DEPTH24_STENCIL8, GL_R32F, GL_RG16F,
                 GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT32F, GL_DEPTH_COMPONENT32 -> 4L;
            case GL_DEPTH32F_STENCIL8 -> 8L;
            default -> 4L; // Conservative best-guess so unknown formats still contribute to the stat.
        };
        long total = 0;
        for (int lvl = 0; lvl < this.levels; lvl++) {
            total += Math.max(((long) this.width) >> lvl, 1L)
                    * Math.max(((long) this.height) >> lvl, 1L)
                    * elemSize;
        }
        return total;
    }

    /** Throws {@link IllegalStateException} if {@link #store} hasn't been called yet. */
    public void assertAllocated() {
        if (!this.hasAllocated) throw new IllegalStateException("GlTexture not yet allocated");
    }

    /** Clears every mip level to zero. Internal-format-aware: chooses the right pixel type per format. */
    public GlTexture zero() {
        assertAllocated();
        int type = switch (this.format) {
            case GL_R32UI -> GL_UNSIGNED_INT;
            case GL_R32F, GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT32F, GL_DEPTH_COMPONENT32, GL_RG16F,
                 GL_RGBA8 -> GL_FLOAT;
            case GL_DEPTH24_STENCIL8 -> GL_UNSIGNED_INT_24_8;
            case GL_DEPTH32F_STENCIL8 -> GL_FLOAT_32_UNSIGNED_INT_24_8_REV;
            default -> throw new IllegalStateException("Unhandled clear format 0x" + Integer.toHexString(this.format));
        };
        for (int lvl = 0; lvl < this.levels; lvl++) {
            nglClearTexImage(this.id, lvl, getPixelTransferFormat(), type, 0L);
        }
        return this;
    }

    /** Number of live {@link GlTexture} instances. */
    public static int liveCount() { return COUNT.get(); }
    /** Estimated bytes held by live {@link GlTexture} instances. */
    public static long estimatedLiveBytes() { return ESTIMATED_TOTAL_SIZE.get(); }
}
