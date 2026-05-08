package com.github.foxy.client.core.model;

import com.github.foxy.common.util.MemoryBuffer;
import it.unimi.dsi.fastutil.bytes.ByteArrayFIFOQueue;
import org.lwjgl.system.MemoryUtil;

import java.util.Arrays;

import static com.github.foxy.client.core.model.ModelFactory.LAYERS;
import static com.github.foxy.client.core.model.ModelFactory.MODEL_TEXTURE_SIZE;

/**
 * Per-block-state texture mip generator: packs the six face textures into a single
 * 3&times;2-tile atlas slot and computes its mip chain.
 *
 * <h2>Atlas tile layout</h2>
 * <p>Each block-state slot stores six face textures arranged as a 3&times;2 grid of
 * {@link ModelFactory#MODEL_TEXTURE_SIZE} squares. {@link #putTextures} writes the
 * raw mip-0 grid; the loop at the bottom then halves each axis successively, writing
 * the lower-resolution mips into the same buffer immediately after the previous
 * level (the renderer reads them via DSA texture-storage offsets).</p>
 *
 * <h2>Edge solidification</h2>
 * <p>{@link #solidify} runs an unsigned-distance flood fill across each face whose
 * mip-0 image contains any transparent pixel. The fill records the nearest opaque
 * pixel for every transparent pixel; transparent pixels are then rewritten to their
 * nearest neighbour's RGB with their alpha left at zero. This stops the
 * box-downsample at higher mips from bleeding pure-black RGB into translucent
 * pixels (a common cause of dark fringes around grass, leaves, and similar foliage
 * in distant LODs).</p>
 *
 * <h2>Cleanroom note</h2>
 * <p>Same algorithm as upstream Voxy. The cleanroom rewrite pulls the magic numbers
 * (3, 2, 4 bytes per pixel) into named constants, narrows the {@code static} field
 * visibility, drops the always-empty {@code generateMipmaps} stub, and adds full
 * English javadoc.</p>
 */
public final class MipGen {
    private MipGen() {}

    /** Bytes per RGBA8 pixel. */
    private static final int BPP = 4;
    /** Atlas tile horizontal extent, in face widths. */
    private static final int TILES_X = 3;
    /** Atlas tile vertical extent, in face heights. */
    private static final int TILES_Y = 2;
    /** Total faces per block-state model. */
    private static final int FACES = 6;

    static {
        if (MODEL_TEXTURE_SIZE > 16) {
            throw new IllegalStateException(
                    "MipGen assumes MODEL_TEXTURE_SIZE <= 16; the byte-encoded BFS positions overflow above that");
        }
    }

    /** Reusable BFS scratch buffer; encodes (linear position) | (distance &lt;&lt; 8). */
    private static final short[] FILL_DISTANCE = new short[MODEL_TEXTURE_SIZE * MODEL_TEXTURE_SIZE];

    /** Reusable BFS frontier; the byte-FIFO is enough because positions fit in a byte. */
    private static final ByteArrayFIFOQueue FILL_QUEUE = new ByteArrayFIFOQueue(MODEL_TEXTURE_SIZE * MODEL_TEXTURE_SIZE);

    /** Returns the byte offset of pixel {@code i} within the face at tile {@code (bx, by)}. */
    private static long pixelOffset(int bx, int by, int i) {
        bx += i & (MODEL_TEXTURE_SIZE - 1);
        by += i / MODEL_TEXTURE_SIZE;
        return (long) (bx + by * MODEL_TEXTURE_SIZE * TILES_X);
    }

    /** Runs the BFS-based RGB solidification pass across the six face tiles. */
    private static void solidify(long baseAddr, byte facesNeedingSolidify) {
        for (int idx = 0; idx < FACES; idx++) {
            if (((facesNeedingSolidify >> idx) & 1) == 0) continue;
            int bx = (idx >> 1) * MODEL_TEXTURE_SIZE;
            int by = (idx & 1) * MODEL_TEXTURE_SIZE;
            long faceAddr = baseAddr + (long) (bx + by * MODEL_TEXTURE_SIZE * TILES_X) * BPP;

            // Seed BFS with every opaque pixel; transparent pixels keep distance == 0xFF.
            Arrays.fill(FILL_DISTANCE, (short) -1);
            for (int y = 0; y < MODEL_TEXTURE_SIZE; y++) {
                for (int x = 0; x < MODEL_TEXTURE_SIZE; x++) {
                    int colour = MemoryUtil.memGetInt(faceAddr + (long) (x + y * MODEL_TEXTURE_SIZE * TILES_X) * BPP);
                    if ((colour & 0xFF000000) != 0) {
                        int pos = x + y * MODEL_TEXTURE_SIZE;
                        FILL_DISTANCE[pos] = (short) pos;
                        FILL_QUEUE.enqueue((byte) pos);
                    }
                }
            }

            // BFS: at each step, distance grows by 1 (encoded in the high byte of FILL_DISTANCE).
            while (!FILL_QUEUE.isEmpty()) {
                int pos = Byte.toUnsignedInt(FILL_QUEUE.dequeueByte());
                int x = pos & (MODEL_TEXTURE_SIZE - 1);
                int y = pos / MODEL_TEXTURE_SIZE;
                short newVal = (short) (FILL_DISTANCE[pos] + (short) 0x0100);
                for (int D = 3; D >= 0; D--) {
                    int d = 2 * (D & 1) - 1;
                    int x2 = x + (((D & 2) == 2) ? d : 0);
                    int y2 = y + (((D & 2) == 0) ? d : 0);
                    if (x2 < 0 || x2 >= MODEL_TEXTURE_SIZE || y2 < 0 || y2 >= MODEL_TEXTURE_SIZE) continue;
                    int pos2 = x2 + y2 * MODEL_TEXTURE_SIZE;
                    if ((newVal & 0xFF00) < (FILL_DISTANCE[pos2] & 0xFF00)) {
                        FILL_DISTANCE[pos2] = newVal;
                        FILL_QUEUE.enqueue((byte) pos2);
                    }
                }
            }

            // Rewrite the RGB of every reached transparent pixel to its nearest opaque pixel's RGB.
            for (int i = 0; i < MODEL_TEXTURE_SIZE * MODEL_TEXTURE_SIZE; i++) {
                int d = Short.toUnsignedInt(FILL_DISTANCE[i]);
                if ((d & 0xFF00) != 0) {
                    int rgb = MemoryUtil.memGetInt(baseAddr + pixelOffset(bx, by, d & 0xFF) * BPP) & 0x00FFFFFF;
                    MemoryUtil.memPutInt(baseAddr + pixelOffset(bx, by, i) * BPP, rgb);
                }
            }
        }
    }

    /**
     * Packs the six face textures into a single block-state atlas slot and computes
     * its mip chain in place.
     *
     * @param darkened whether the source textures are pre-darkened (for shaded faces);
     *                 controls the mip filter and skips solidification when true
     *                 (RGB blending is well-defined when alpha is zero on darkened
     *                 textures so no fringe-fix is needed)
     * @param textures six face textures in face-index order
     * @param into     destination buffer; must be sized for the full mip chain
     */
    public static void putTextures(boolean darkened, ColourDepthTextureData[] textures, MemoryBuffer into) {
        final long addr = into.address;
        final int rowStride = MODEL_TEXTURE_SIZE * TILES_X;

        byte facesNeedingSolidify = 0;
        for (int faceIdx = 0; faceIdx < FACES; faceIdx++) {
            int x = (faceIdx >> 1) * MODEL_TEXTURE_SIZE;
            int y = (faceIdx & 1) * MODEL_TEXTURE_SIZE;
            int j = 0;
            boolean anyTransparent = false;
            for (int colour : textures[faceIdx].colour()) {
                int o = ((y + (j >> LAYERS)) * rowStride + ((j & (MODEL_TEXTURE_SIZE - 1)) + x)) * BPP;
                j++;
                MemoryUtil.memPutInt(addr + o, colour);
                anyTransparent |= ((colour & 0xFF000000) == 0);
            }
            facesNeedingSolidify |= (byte) ((anyTransparent ? 1 : 0) << faceIdx);
        }

        if (!darkened) {
            solidify(addr, facesNeedingSolidify);
        }

        // Mip chain: each pass halves both axes and blends 2x2 tiles into one.
        long dstAddr = addr;
        for (int i = 0; i < LAYERS - 1; i++) {
            long srcAddr = dstAddr;
            dstAddr += (MODEL_TEXTURE_SIZE * MODEL_TEXTURE_SIZE * TILES_X * TILES_Y * BPP) >> (i << 1);
            int dstWidth = (MODEL_TEXTURE_SIZE * TILES_X) >> (i + 1);
            int srcWidth = (MODEL_TEXTURE_SIZE * TILES_X) >> i;
            int dstHeight = (MODEL_TEXTURE_SIZE * TILES_Y) >> (i + 1);
            for (int px = 0; px < dstWidth; px++) {
                for (int py = 0; py < dstHeight; py++) {
                    long bp = srcAddr + (long) (px * 2 + py * 2 * srcWidth) * BPP;
                    int c00 = MemoryUtil.memGetInt(bp);
                    int c01 = MemoryUtil.memGetInt(bp + (long) srcWidth * BPP);
                    int c10 = MemoryUtil.memGetInt(bp + BPP);
                    int c11 = MemoryUtil.memGetInt(bp + (long) srcWidth * BPP + BPP);
                    MemoryUtil.memPutInt(dstAddr + (long) (px + py * dstWidth) * BPP,
                            TextureUtils.mipColours(darkened, c00, c01, c10, c11));
                }
            }
        }
    }
}
