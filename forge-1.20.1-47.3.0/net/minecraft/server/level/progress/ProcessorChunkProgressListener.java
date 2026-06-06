//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.level.progress;

import java.util.Objects;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public class ProcessorChunkProgressListener implements ChunkProgressListener {
    private final ChunkProgressListener delegate;
    private final ProcessorMailbox<Runnable> mailbox;

    private ProcessorChunkProgressListener(ChunkProgressListener p_9640_, Executor p_9641_) {
        this.delegate = p_9640_;
        this.mailbox = ProcessorMailbox.create(p_9641_, "progressListener");
    }

    public static ProcessorChunkProgressListener createStarted(ChunkProgressListener p_143584_, Executor p_143585_) {
        ProcessorChunkProgressListener $$2 = new ProcessorChunkProgressListener(p_143584_, p_143585_);
        $$2.start();
        return $$2;
    }

    public void updateSpawnPos(ChunkPos p_9643_) {
        this.mailbox.tell(() -> {
            this.delegate.updateSpawnPos(p_9643_);
        });
    }

    public void onStatusChange(ChunkPos p_9645_, @Nullable ChunkStatus p_9646_) {
        this.mailbox.tell(() -> {
            this.delegate.onStatusChange(p_9645_, p_9646_);
        });
    }

    public void start() {
        ProcessorMailbox var10000 = this.mailbox;
        ChunkProgressListener var10001 = this.delegate;
        Objects.requireNonNull(var10001);
        var10000.tell(var10001::start);
    }

    public void stop() {
        ProcessorMailbox var10000 = this.mailbox;
        ChunkProgressListener var10001 = this.delegate;
        Objects.requireNonNull(var10001);
        var10000.tell(var10001::stop);
    }
}
