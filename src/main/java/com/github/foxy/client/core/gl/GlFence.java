package com.github.foxy.client.core.gl;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL32.GL_SIGNALED;
import static org.lwjgl.opengl.GL32.GL_SYNC_GPU_COMMANDS_COMPLETE;
import static org.lwjgl.opengl.GL32.GL_SYNC_STATUS;
import static org.lwjgl.opengl.GL32.GL_UNSIGNALED;
import static org.lwjgl.opengl.GL32.glDeleteSync;
import static org.lwjgl.opengl.GL32.glFenceSync;
import static org.lwjgl.opengl.GL32.nglGetSynciv;

/**
 * GPU fence used to coordinate CPU&harr;GPU work.
 *
 * <p>Construction inserts a {@code GL_SYNC_GPU_COMMANDS_COMPLETE} sync after the
 * commands queued at that moment; {@link #signaled()} polls without blocking and
 * caches the result so subsequent calls don't issue further GL queries.</p>
 *
 * <p>Polling uses {@code glGetSynciv} (a status query) rather than
 * {@code glClientWaitSync} with a zero timeout. The query variant is cheaper on
 * some drivers and side-effect-free, which matters when polling once per frame
 * across many fences.</p>
 */
public final class GlFence extends GlObject {

    /** Native scratch slot for the {@code glGetSynciv} status read. */
    private static final long SCRATCH = MemoryUtil.nmemCalloc(1, 4);

    private final long handle;
    private boolean signaled;

    /** Inserts a fence after the currently-queued GL commands. */
    public GlFence() {
        this.handle = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
    }

    /**
     * Non-blocking poll for fence completion. Returns {@code true} once the GPU has
     * passed the fence, and stays {@code true} forever after. Subsequent calls are
     * cheap (one boolean read) once the fence has signalled.
     */
    public boolean signaled() {
        if (this.signaled) return true;
        MemoryUtil.memPutInt(SCRATCH, -1);
        nglGetSynciv(this.handle, GL_SYNC_STATUS, 1, 0L, SCRATCH);
        int status = MemoryUtil.memGetInt(SCRATCH);
        if (status == GL_SIGNALED) {
            this.signaled = true;
        } else if (status != GL_UNSIGNALED) {
            throw new IllegalStateException("Unexpected glGetSynciv status: 0x" + Integer.toHexString(status));
        }
        return this.signaled;
    }

    @Override
    public void free() {
        free0();
        glDeleteSync(this.handle);
    }
}
