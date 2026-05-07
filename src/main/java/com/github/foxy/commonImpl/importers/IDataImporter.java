package com.github.foxy.commonImpl.importers;

import com.github.foxy.common.world.WorldEngine;

/**
 * Contract for one-shot data import jobs.
 *
 * <p>Implementations translate an external data source (vanilla region files, a Distant
 * Horizons sqlite database, a bobby cache, etc.) into
 * {@link com.github.foxy.common.voxelization.VoxelizedSection VoxelizedSection}s, pack
 * them into {@link com.github.foxy.common.world.WorldSection WorldSection}s, and write
 * those back through the engine's
 * {@link com.github.foxy.common.config.section.SectionStorage SectionStorage}.</p>
 *
 * <p>Lifecycle is owned by {@link com.github.foxy.commonImpl.ImportManager ImportManager}:
 * at most one import task is active per {@link WorldEngine} at a time.</p>
 */
public interface IDataImporter {
    /** Job-finished callback; {@code chunks} is the count of chunks successfully imported. */
    @FunctionalInterface
    interface ICompletionCallback {
        void onCompletion(int chunks);
    }

    /** Progress callback; the implementation decides how often to emit. */
    @FunctionalInterface
    interface IUpdateCallback {
        void onUpdate(int finished, int outOf);
    }

    /**
     * Starts the import. Implementations may run synchronously or asynchronously, but
     * must always invoke {@code completionCallback} when done. <strong>This method must
     * return promptly</strong>; async implementations should only spawn worker threads
     * here and not block.
     */
    void runImport(IUpdateCallback updateCallback, ICompletionCallback completionCallback);

    /** The engine this import is targeting. */
    WorldEngine getEngine();

    /**
     * Cancels the import and releases resources. Implementations should make
     * {@link #isRunning()} return {@code false} as soon as practical, but are not
     * required to guarantee that already-scheduled voxelization tasks stop immediately.
     */
    void shutdown();

    /** {@code true} iff a worker is still processing data. */
    boolean isRunning();
}
