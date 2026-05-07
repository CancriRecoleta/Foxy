package com.github.foxy.client.core.gl;

import com.github.foxy.commonImpl.FoxyCommon;

/**
 * Base class for every wrapper around a GL handle.
 *
 * <h2>Lifetime invariant</h2>
 * Each instance owns exactly one handle (or one logical group of handles). The handle
 * is allocated by the subclass constructor and released by {@link #free()}. Calling
 * {@code free()} more than once is a programming error and throws.
 *
 * <h2>Diagnostics</h2>
 * Setting {@code -Dfoxy.trackGlObjects=true} causes the constructor to record the
 * call-site stack trace; if the GC ever finalizes a non-freed instance, the saved
 * trace is logged. This is opt-in because capturing a stack trace per allocation has
 * a measurable cost on the renderer's hot path.
 *
 * <p>Cleanroom note: upstream Voxy's {@code TrackedObject} performs the same job but
 * is intertwined with a global cleanup queue and per-class registries. The Foxy port
 * keeps the contract minimal &mdash; an idempotent {@code free()} guard plus an
 * opt-in leak trace.</p>
 */
public abstract class GlObject {
    /** When {@code true}, constructors capture an allocation stack trace for leak diagnosis. */
    public static final boolean TRACK_ALLOCATIONS = FoxyCommon.isVerificationFlagOn("trackGlObjects");

    private boolean freed;
    private final Throwable allocationSite;

    protected GlObject() {
        this.allocationSite = TRACK_ALLOCATIONS ? new Throwable("GlObject allocated here") : null;
    }

    /**
     * Releases the GL handle. Subclasses override to add the actual {@code glDelete*}
     * call but must invoke {@code super.free()} (or {@link #free0()}) first so the
     * idempotency check fires before the handle is destroyed.
     */
    public void free() {
        free0();
    }

    /**
     * Idempotency guard for subclasses that need to delete handles in their own
     * {@code free()} after the guard but before any superclass cleanup.
     */
    protected final void free0() {
        if (this.freed) {
            throw new IllegalStateException("GlObject double-free on " + getClass().getSimpleName());
        }
        this.freed = true;
    }

    /**
     * Asserts the wrapper is still live; cheap on the hot path.
     *
     * <p>Public so callers that just hold a reference (e.g. shader binders) can sanity
     * check the wrapper before issuing a GL call against the underlying {@code id} field.
     * Calling on a freed wrapper throws {@link IllegalStateException}.</p>
     */
    public final void assertNotFreed() {
        if (this.freed) {
            throw new IllegalStateException("Use-after-free on " + getClass().getSimpleName());
        }
    }

    /** Whether {@link #free()} has already been called. */
    public final boolean isFreed() { return this.freed; }

    @Override
    @SuppressWarnings({"removal", "deprecation"}) // finalize() is the only viable leak hook
    protected void finalize() throws Throwable {
        try {
            if (!this.freed && TRACK_ALLOCATIONS) {
                com.github.foxy.common.Logger.error(
                        "Leaked " + getClass().getSimpleName() + " was never freed", this.allocationSite);
            }
        } finally {
            super.finalize();
        }
    }
}
