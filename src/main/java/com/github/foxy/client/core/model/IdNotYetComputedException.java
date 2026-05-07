package com.github.foxy.client.core.model;

/**
 * Sentinel exception thrown when the renderer asks for a derived id (block or biome)
 * whose backing model / colour bake hasn't completed yet.
 *
 * <h2>Why this is an exception, not a return value</h2>
 * <p>The id-derivation paths are deep inside hot rendering code and don't have a
 * natural sentinel value to return ({@code -1} / {@code 0} are both valid block /
 * biome ids). Throwing surfaces the missing entry directly to the bakery scheduler,
 * which catches it, queues a bake, and retries the section on the next frame.</p>
 *
 * <h2>Why suppression is on</h2>
 * <p>Constructed with {@code (msg, cause, enableSuppression=false, writableStackTrace=false)}
 * so it's effectively a flyweight: no stack walk, no suppression list, allocated on
 * the hot path with negligible cost.</p>
 *
 * <p>Cleanroom note: same shape as upstream Voxy with English javadoc.</p>
 */
public class IdNotYetComputedException extends RuntimeException {

    /** The id that still needs baking. */
    public final int id;

    /** {@code true} when {@link #id} is a block-state id, {@code false} for a biome id. */
    public final boolean isIdBlockId;

    /** Auxiliary bitmask written by some catch sites; not used by the throw path. */
    public int auxBitMsk;

    /** Auxiliary data written by some catch sites; not used by the throw path. */
    public long[] auxData;

    public IdNotYetComputedException(int id, boolean isIdBlockId) {
        super(null, null, /* enableSuppression */ false, /* writableStackTrace */ false);
        this.id = id;
        this.isIdBlockId = isIdBlockId;
    }
}
