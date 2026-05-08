package com.github.foxy.commonImpl;

import com.github.foxy.common.Logger;
import com.github.foxy.common.world.WorldEngine;
import com.github.foxy.commonImpl.importers.IDataImporter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Scheduler for {@link IDataImporter} jobs.
 *
 * <p>Maintains a {@link WorldEngine}-keyed map of currently-active {@link Task}s and
 * enforces &quot;at most one import per engine&quot;. Each {@code Task} tracks its start
 * time and the timestamp of the last forwarded progress event, throttling updates to
 * {@link #PROGRESS_THROTTLE_MILLIS} so high-frequency callbacks from the importer don't
 * saturate UI subscribers.</p>
 *
 * <h2>Cleanroom design notes</h2>
 * <ul>
 *   <li>{@link ConcurrentHashMap} replaces upstream's plain {@link java.util.HashMap}
 *       guarded by {@code synchronized(this)}; this simplifies double-checked
 *       publication when starting a new task.</li>
 *   <li>Upstream extended {@code ImportTask} via subclassing so client UIs could
 *       intercept progress. The cleanroom port instead exposes an
 *       {@link IUpdateBroadcaster} hook receiving {@code (engine, finished, outOf)}
 *       directly, avoiding the inheritance ceremony.</li>
 *   <li>Client side-effects like Windows taskbar progress are kept out of this class;
 *       a client module subscribes to the broadcaster and translates events.</li>
 * </ul>
 */
public class ImportManager {
    /** Minimum interval between forwarded progress events, in milliseconds. */
    public static final long PROGRESS_THROTTLE_MILLIS = 50L;

    private final Map<WorldEngine, Task> activeImports = new ConcurrentHashMap<>();

    /** Subscriber hook for progress events; client UIs / loggers / taskbars attach here. */
    @FunctionalInterface
    public interface IUpdateBroadcaster {
        void onProgress(WorldEngine engine, int finished, int outOf);
    }

    private volatile IUpdateBroadcaster broadcaster = (e, f, o) -> {};

    /** Registers a progress subscriber; the latest call wins. */
    public void setBroadcaster(IUpdateBroadcaster broadcaster) {
        this.broadcaster = broadcaster == null ? (e, f, o) -> {} : broadcaster;
    }

    /** One in-flight import. */
    protected class Task {
        protected final IDataImporter importer;
        protected final long startTimeMillis;
        protected volatile long lastUpdateMillis;

        protected Task(IDataImporter importer) {
            this.importer = importer;
            this.startTimeMillis = System.currentTimeMillis();
            this.lastUpdateMillis = this.startTimeMillis;
        }

        private void start() {
            if (this.importer.isRunning()) {
                throw new IllegalStateException("Importer already running");
            }
            this.importer.runImport(this::onUpdate, this::onCompleted);
        }

        /** Throttle gate for progress callbacks. */
        protected void onUpdate(int finished, int outOf) {
            long now = System.currentTimeMillis();
            if (now - this.lastUpdateMillis < PROGRESS_THROTTLE_MILLIS) return;
            this.lastUpdateMillis = now;
            try {
                ImportManager.this.broadcaster.onProgress(this.importer.getEngine(), finished, outOf);
            } catch (Throwable t) {
                Logger.error("Import broadcaster threw", t);
            }
        }

        protected void onCompleted(int chunks) {
            var engine = this.importer.getEngine();
            var instance = engine.instanceIn;
            if (instance != null && chunks > 0) {
                int enqueued = instance.getOrCreateMipService(engine).mipAll();
                Logger.info("Foxy import completed: " + chunks + " chunks imported, " + enqueued + " LOD parents queued for mipping");
            }
            ImportManager.this.jobFinished(this);
        }

        protected void shutdown() {
            this.importer.shutdown();
        }

        protected boolean isCompleted() {
            return !this.importer.isRunning();
        }
    }

    /** Subclass extension point: override to inject a custom Task type. */
    protected Task createTask(IDataImporter importer) {
        return new Task(importer);
    }

    /**
     * Tries to start {@code importer}. Returns {@code false} if another import is
     * already active for the same engine.
     *
     * <p>On success, {@code importer.runImport} has been invoked on the calling thread;
     * whether work is then performed synchronously or asynchronously is up to the
     * importer.</p>
     */
    public boolean tryRunImport(IDataImporter importer) {
        var engine = importer.getEngine();
        var newTask = createTask(importer);
        var existing = this.activeImports.putIfAbsent(engine, newTask);
        if (existing != null) {
            if (!existing.isCompleted()) {
                return false;
            }
            // The previous task says it's finished but never cleared itself from the
            // map; treat this as a missed jobFinished and replace.
            this.activeImports.replace(engine, existing, newTask);
        }
        try {
            newTask.start();
            return true;
        } catch (Throwable t) {
            // Roll back the put we just did so future calls aren't permanently locked out.
            this.activeImports.remove(engine, newTask);
            throw t;
        }
    }

    /**
     * Atomically: if no import is active for {@code engine}, builds one via
     * {@code factory} and starts it; otherwise returns {@code false} without invoking
     * {@code factory} (avoiding expensive constructor work in the common no-op case).
     *
     * <p>Wraps the body in {@code engine.acquireRef() / releaseRef()} so the engine's
     * idle-timeout machinery cannot reap it while a task is being launched.</p>
     */
    public boolean makeAndRunIfNone(WorldEngine engine, Supplier<IDataImporter> factory) {
        engine.acquireRef();
        try {
            if (this.activeImports.containsKey(engine)) return false;
            return tryRunImport(factory.get());
        } finally {
            engine.releaseRef();
        }
    }

    /** Cancels the active import on {@code engine}; returns whether any was actually cancelled. */
    public boolean cancelImport(WorldEngine engine) {
        var task = this.activeImports.get(engine);
        if (task == null) return false;
        task.shutdown();
        this.activeImports.remove(engine, task);
        return true;
    }

    /** Called from the importer's completion callback to deregister the task. */
    private void jobFinished(Task task) {
        this.activeImports.remove(task.importer.getEngine(), task);
    }

    /** Number of currently-active import tasks. */
    public int activeCount() { return this.activeImports.size(); }

    /** {@code true} when an import is in flight on {@code engine}. */
    public boolean isImporting(WorldEngine engine) {
        var t = this.activeImports.get(engine);
        return t != null && !t.isCompleted();
    }
}
