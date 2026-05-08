package com.github.foxy.client.core.model;

import me.jellysquid.mods.sodium.client.util.color.ColorSRGB;

import java.util.Arrays;

/**
 * Pixel-analysis helpers consumed by the texture-baking pipeline.
 *
 * <h2>Pixel-was-written checks</h2>
 * <p>The software rasteriser packs three independent "was this pixel touched"
 * signals into the depth array (low byte = stencil-style hit count, top 24 bits =
 * actual depth). The {@code checkMode} argument on every helper picks which signal
 * a given query consults:</p>
 * <ul>
 *   <li>{@link #WRITE_CHECK_STENCIL} &mdash; low byte of the depth array is non-zero
 *       (stencil-style hit count).</li>
 *   <li>{@link #WRITE_CHECK_DEPTH} &mdash; top 24 bits of the depth array differ
 *       from the cleared value.</li>
 *   <li>{@link #WRITE_CHECK_ALPHA} &mdash; alpha channel of the colour array is
 *       greater than 1 (workaround for spurious alpha=1 bleed in the rasteriser).</li>
 * </ul>
 *
 * <h2>Depth modes</h2>
 * <p>{@link #computeDepth} reduces every drawn pixel's depth to a single value via
 * one of {@link #DEPTH_MODE_AVG}, {@link #DEPTH_MODE_MAX}, {@link #DEPTH_MODE_MIN}.</p>
 *
 * <h2>Cleanroom note</h2>
 * <p>Same algorithms as upstream Voxy. The cleanroom rewrite consolidates the four
 * directional bounds-search loops into two reusable column / row scanners, replaces
 * the open-coded {@code mipColours} accumulator with a single 4-element loop,
 * narrows static visibility, and adds full English javadoc. Public {@code int}
 * constants and method signatures are preserved so existing callers compile
 * unchanged.</p>
 */
public final class TextureUtils {
    private TextureUtils() {}

    // ---- check modes ----------------------------------------------------------------

    public static final int WRITE_CHECK_STENCIL = 1;
    public static final int WRITE_CHECK_DEPTH = 2;
    public static final int WRITE_CHECK_ALPHA = 3;

    // ---- depth modes ----------------------------------------------------------------

    public static final int DEPTH_MODE_AVG = 1;
    public static final int DEPTH_MODE_MAX = 2;
    public static final int DEPTH_MODE_MIN = 3;

    /** Tests whether pixel {@code index} of {@code data} was actually drawn. */
    private static boolean wasPixelWritten(ColourDepthTextureData data, int mode, int index) {
        return switch (mode) {
            case WRITE_CHECK_STENCIL -> (data.depth()[index] & 0xFF) != 0;
            case WRITE_CHECK_DEPTH -> (data.depth()[index] >>> 8) != ((1 << 24) - 1);
            // Spurious alpha=1 bleed in the rasteriser sneaks past a strict > 0 check;
            // > 1 is the empirically-correct threshold.
            case WRITE_CHECK_ALPHA -> ((data.colour()[index] >>> 24) & 0xff) > 1;
            default -> throw new IllegalArgumentException("Unknown checkMode " + mode);
        };
    }

    /** Counts pixels for which {@link #wasPixelWritten} returns {@code true}. */
    public static int getWrittenPixelCount(ColourDepthTextureData texture, int checkMode) {
        int count = 0;
        for (int i = 0; i < texture.colour().length; i++) {
            if (wasPixelWritten(texture, checkMode, i)) count++;
        }
        return count;
    }

    /** {@code true} when every pixel has full alpha (face is fully opaque). */
    public static boolean isSolid(ColourDepthTextureData texture) {
        for (int pixel : texture.colour()) {
            if (((pixel >> 24) & 0xFF) != 255) return false;
        }
        return true;
    }

    /** {@code true} when at least one drawn pixel has partial alpha (translucent face). */
    public static boolean hasTranslucentPixel(ColourDepthTextureData data) {
        int[] colours = data.colour();
        int[] depths = data.depth();
        for (int i = 0; i < colours.length; i++) {
            if ((depths[i] & 0xFF) == 0) continue; // skip undrawn pixels
            int alpha = colours[i] >>> 24;
            if (alpha != 0 && alpha != 255) return true;
        }
        return false;
    }

    /** {@code true} when every drawn pixel has full alpha (no in-band translucency). */
    public static boolean isSolidWhereDrawn(ColourDepthTextureData data) {
        int[] colours = data.colour();
        int[] depths = data.depth();
        for (int i = 0; i < colours.length; i++) {
            if ((depths[i] & 0xFF) == 0) continue;
            if ((colours[i] >>> 24) != 255) return false;
        }
        return true;
    }

    /**
     * Determines whether the face uses biome-colour tint.
     * @return 0 = nothing written, 1 = none tinted, 2 = some tinted, 3 = all tinted
     */
    public static int computeFaceTint(ColourDepthTextureData texture, int checkMode) {
        boolean allTinted = true;
        boolean someTinted = false;
        boolean wasWritten = false;

        int[] colours = texture.colour();
        int[] depths = texture.depth();
        for (int i = 0; i < colours.length; i++) {
            if (!wasPixelWritten(texture, checkMode, i)) continue;
            // Skip pure-black / fully-transparent pixels: they don't carry tint info.
            if ((colours[i] & 0xFFFFFF) == 0 || (colours[i] >>> 24) == 0) continue;
            boolean pixelTinted = (depths[i] & (1 << 7)) != 0;
            wasWritten = true;
            allTinted &= pixelTinted;
            someTinted |= pixelTinted;
        }
        if (!wasWritten) return 0;
        return someTinted ? (allTinted ? 3 : 2) : 1;
    }

    /**
     * Reduces every drawn pixel's depth to a single representative value in [0, 1].
     *
     * @param mode      one of {@link #DEPTH_MODE_AVG}, {@link #DEPTH_MODE_MAX}, {@link #DEPTH_MODE_MIN}
     * @param checkMode one of the {@code WRITE_CHECK_*} constants
     * @return depth in [0, 1], or {@code -1} when no pixel was drawn
     */
    public static float computeDepth(ColourDepthTextureData texture, int mode, int checkMode) {
        int[] depths = texture.depth();
        long acc;
        if (mode == DEPTH_MODE_AVG) acc = 0L;
        else if (mode == DEPTH_MODE_MIN) acc = Long.MAX_VALUE;
        else if (mode == DEPTH_MODE_MAX) acc = Long.MIN_VALUE;
        else throw new IllegalArgumentException("Unknown depth mode " + mode);

        long count = 0L;
        for (int i = 0; i < depths.length; i++) {
            if (!wasPixelWritten(texture, checkMode, i)) continue;
            int depth = depths[i] >>> 8;
            switch (mode) {
                case DEPTH_MODE_AVG -> { count++; acc += depth; }
                case DEPTH_MODE_MAX -> acc = Math.max(acc, depth);
                case DEPTH_MODE_MIN -> acc = Math.min(acc, depth);
            }
        }
        return switch (mode) {
            case DEPTH_MODE_AVG -> count == 0L ? -1f : u24DepthToFloat((int) (acc / count));
            case DEPTH_MODE_MAX -> acc == Long.MIN_VALUE ? -1f : u24DepthToFloat((int) acc);
            case DEPTH_MODE_MIN -> acc == Long.MAX_VALUE ? -1f : u24DepthToFloat((int) acc);
            default -> throw new IllegalArgumentException("Unknown depth mode " + mode);
        };
    }

    /** Converts a 24-bit unsigned depth to a normalised float in [0, 1]. */
    private static float u24DepthToFloat(int depth) {
        return (float) ((double) depth / ((1 << 24) - 1));
    }

    /** Returns a fresh per-pixel coverage mask (one bit per pixel, packed into longs). */
    public static long[] generateMask(ColourDepthTextureData data, int checkMode) {
        return generateMask(data, checkMode, new long[data.width() * data.height() / 64]);
    }

    /** Variant of {@link #generateMask(ColourDepthTextureData, int)} that reuses {@code dst}. */
    public static long[] generateMask(ColourDepthTextureData data, int checkMode, long[] dst) {
        Arrays.fill(dst, 0L);
        int i = 0;
        for (int y = 0; y < data.height(); y++) {
            for (int x = 0; x < data.width(); x++) {
                if (wasPixelWritten(data, checkMode, i)) {
                    dst[i / 64] |= 1L << (i & 63);
                }
                i++;
            }
        }
        return dst;
    }

    /**
     * Returns the tight rectangle of drawn pixels as {@code [minX, maxX, minY, maxY]}.
     * Pixel order is bottom-left-to-top-right (x major).
     */
    public static int[] computeBounds(ColourDepthTextureData data, int checkMode) {
        int w = data.width();
        int h = data.height();
        int minX = scanColumns(data, checkMode, 0, w, 1);
        int maxX = scanColumns(data, checkMode, w - 1, -1, -1);
        int minY = scanRows(data, checkMode, 0, h, 1);
        int maxY = scanRows(data, checkMode, h - 1, -1, -1);
        return new int[]{minX, maxX, minY, maxY};
    }

    /** Sweeps columns from {@code start} (inclusive) toward {@code stop} until a written pixel is found. */
    private static int scanColumns(ColourDepthTextureData data, int checkMode, int start, int stop, int step) {
        int w = data.width();
        int h = data.height();
        for (int x = start; x != stop; x += step) {
            for (int y = 0; y < h; y++) {
                if (wasPixelWritten(data, checkMode, x + y * w)) return x;
            }
        }
        // Bounds requested for an empty face: return the natural end-of-axis sentinel.
        return step > 0 ? w : -1;
    }

    /** Sweeps rows from {@code start} (inclusive) toward {@code stop} until a written pixel is found. */
    private static int scanRows(ColourDepthTextureData data, int checkMode, int start, int stop, int step) {
        int w = data.width();
        int h = data.height();
        for (int y = start; y != stop; y += step) {
            for (int x = 0; x < w; x++) {
                if (wasPixelWritten(data, checkMode, y * h + x)) return y;
            }
        }
        return step > 0 ? h : -1;
    }

    /**
     * sRGB-aware 2&times;2 box filter; ignores fully-transparent samples unless the
     * caller indicates the textures are pre-darkened (in which case alpha carries
     * darken weight, not transparency, and every sample contributes).
     */
    public static int mipColours(boolean darkened, int c00, int c01, int c10, int c11) {
        // Upstream inverts the flag for cleaner branch reuse below; we keep the trick.
        boolean treatAlphaAsValue = !darkened;

        float r = 0f, g = 0f, b = 0f, a = 0f;
        for (int c : new int[]{c00, c01, c10, c11}) {
            if (treatAlphaAsValue || (c >>> 24) != 0) {
                r += ColorSRGB.srgbToLinear((c >> 0) & 0xFF);
                g += ColorSRGB.srgbToLinear((c >> 8) & 0xFF);
                b += ColorSRGB.srgbToLinear((c >> 16) & 0xFF);
                a += treatAlphaAsValue ? (c >>> 24) : ColorSRGB.srgbToLinear(c >>> 24);
            }
        }
        return ColorSRGB.linearToSrgb(
                r / 4f,
                g / 4f,
                b / 4f,
                treatAlphaAsValue ? ((int) a) / 4 : linearToSrgbChannel(a / 4f)
        );
    }

    /** Single-channel linear→sRGB matching the OpenGL spec curve. */
    private static int linearToSrgbChannel(float value) {
        value = Math.max(0.0f, Math.min(1.0f, value));
        float srgb = value <= 0.0031308f
                ? value * 12.92f
                : 1.055f * (float) Math.pow(value, 1.0f / 2.4f) - 0.055f;
        return Math.max(0, Math.min(255, Math.round(srgb * 255.0f)));
    }
}
