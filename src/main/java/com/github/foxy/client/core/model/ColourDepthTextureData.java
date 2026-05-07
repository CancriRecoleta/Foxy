package com.github.foxy.client.core.model;

import java.util.Arrays;

/**
 * Immutable per-face texture sample taken from the software rasteriser.
 *
 * <p>Each face of a baked block model is rendered into a {@code colour} array (RGBA8
 * packed ints) plus a {@code depth} array (depth-buffer values, also packed ints) of
 * size {@code width * height}. Foxy uses these for two things:</p>
 * <ul>
 *   <li>Building the per-face texture atlas the renderer samples at LOD distances.</li>
 *   <li>De-duplicating identical block faces across the model registry &mdash;
 *       {@link #equals} compares by hash + raw arrays so two faces that produce the
 *       same texels share atlas slots.</li>
 * </ul>
 *
 * <h2>Hashing</h2>
 * <p>The pre-computed {@link #hash} field collapses the two {@code int[]}s into one
 * int. Equality re-checks the arrays; the hash is just a fast pre-filter.</p>
 *
 * <p>Cleanroom note: same record shape as upstream Voxy with English javadoc.</p>
 */
public record ColourDepthTextureData(int[] colour, int[] depth, int width, int height, int hash) {

    /** Convenience constructor that derives {@link #hash} from the supplied arrays. */
    public ColourDepthTextureData(int[] colour, int[] depth, int width, int height) {
        this(colour, depth, width, height, computeHash(colour, depth, width, height));
    }

    private static int computeHash(int[] colour, int[] depth, int width, int height) {
        // Mixes the dimensions with the array hashes so two textures of the same
        // content but different dimensions can't collide.
        return width * 312_337_173 * (Arrays.hashCode(colour) ^ Arrays.hashCode(depth)) ^ height;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ColourDepthTextureData other)) return false;
        return this.hash == other.hash
                && Arrays.equals(this.colour, other.colour)
                && Arrays.equals(this.depth, other.depth);
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    /** Deep-copy clone; the two array fields are duplicated so the result is independent. */
    @Override
    public ColourDepthTextureData clone() {
        return new ColourDepthTextureData(
                Arrays.copyOf(this.colour, this.colour.length),
                Arrays.copyOf(this.depth, this.depth.length),
                this.width, this.height, this.hash);
    }
}
