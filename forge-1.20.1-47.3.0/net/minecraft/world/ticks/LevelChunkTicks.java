//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.ticks;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;

public class LevelChunkTicks<T> implements SerializableTickContainer<T>, TickContainerAccess<T> {
    private final Queue<ScheduledTick<T>> tickQueue;
    @Nullable
    private List<SavedTick<T>> pendingTicks;
    private final Set<ScheduledTick<?>> ticksPerPosition;
    @Nullable
    private BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> onTickAdded;

    public LevelChunkTicks() {
        this.tickQueue = new PriorityQueue(ScheduledTick.DRAIN_ORDER);
        this.ticksPerPosition = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
    }

    public LevelChunkTicks(List<SavedTick<T>> p_193169_) {
        this.tickQueue = new PriorityQueue(ScheduledTick.DRAIN_ORDER);
        this.ticksPerPosition = new ObjectOpenCustomHashSet(ScheduledTick.UNIQUE_TICK_HASH);
        this.pendingTicks = p_193169_;
        Iterator var2 = p_193169_.iterator();

        while(var2.hasNext()) {
            SavedTick<T> $$1 = (SavedTick)var2.next();
            this.ticksPerPosition.add(ScheduledTick.probe($$1.type(), $$1.pos()));
        }

    }

    public void setOnTickAdded(@Nullable BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> p_193182_) {
        this.onTickAdded = p_193182_;
    }

    @Nullable
    public ScheduledTick<T> peek() {
        return (ScheduledTick)this.tickQueue.peek();
    }

    @Nullable
    public ScheduledTick<T> poll() {
        ScheduledTick<T> $$0 = (ScheduledTick)this.tickQueue.poll();
        if ($$0 != null) {
            this.ticksPerPosition.remove($$0);
        }

        return $$0;
    }

    public void schedule(ScheduledTick<T> p_193177_) {
        if (this.ticksPerPosition.add(p_193177_)) {
            this.scheduleUnchecked(p_193177_);
        }

    }

    private void scheduleUnchecked(ScheduledTick<T> p_193194_) {
        this.tickQueue.add(p_193194_);
        if (this.onTickAdded != null) {
            this.onTickAdded.accept(this, p_193194_);
        }

    }

    public boolean hasScheduledTick(BlockPos p_193179_, T p_193180_) {
        return this.ticksPerPosition.contains(ScheduledTick.probe(p_193180_, p_193179_));
    }

    public void removeIf(Predicate<ScheduledTick<T>> p_193184_) {
        Iterator<ScheduledTick<T>> $$1 = this.tickQueue.iterator();

        while($$1.hasNext()) {
            ScheduledTick<T> $$2 = (ScheduledTick)$$1.next();
            if (p_193184_.test($$2)) {
                $$1.remove();
                this.ticksPerPosition.remove($$2);
            }
        }

    }

    public Stream<ScheduledTick<T>> getAll() {
        return this.tickQueue.stream();
    }

    public int count() {
        return this.tickQueue.size() + (this.pendingTicks != null ? this.pendingTicks.size() : 0);
    }

    public ListTag save(long p_193174_, Function<T, String> p_193175_) {
        ListTag $$2 = new ListTag();
        Iterator var5;
        if (this.pendingTicks != null) {
            var5 = this.pendingTicks.iterator();

            while(var5.hasNext()) {
                SavedTick<T> $$3 = (SavedTick)var5.next();
                $$2.add($$3.save(p_193175_));
            }
        }

        var5 = this.tickQueue.iterator();

        while(var5.hasNext()) {
            ScheduledTick<T> $$4 = (ScheduledTick)var5.next();
            $$2.add(SavedTick.saveTick($$4, p_193175_, p_193174_));
        }

        return $$2;
    }

    public void unpack(long p_193172_) {
        if (this.pendingTicks != null) {
            int $$1 = -this.pendingTicks.size();
            Iterator var4 = this.pendingTicks.iterator();

            while(var4.hasNext()) {
                SavedTick<T> $$2 = (SavedTick)var4.next();
                this.scheduleUnchecked($$2.unpack(p_193172_, (long)($$1++)));
            }
        }

        this.pendingTicks = null;
    }

    public static <T> LevelChunkTicks<T> load(ListTag p_193186_, Function<String, Optional<T>> p_193187_, ChunkPos p_193188_) {
        ImmutableList.Builder<SavedTick<T>> $$3 = ImmutableList.builder();
        Objects.requireNonNull($$3);
        SavedTick.loadTickList(p_193186_, p_193187_, p_193188_, $$3::add);
        return new LevelChunkTicks($$3.build());
    }
}
