//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.timers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

public class TimerQueue<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CALLBACK_DATA_TAG = "Callback";
    private static final String TIMER_NAME_TAG = "Name";
    private static final String TIMER_TRIGGER_TIME_TAG = "TriggerTime";
    private final TimerCallbacks<T> callbacksRegistry;
    private final Queue<Event<T>> queue;
    private UnsignedLong sequentialId;
    private final Table<String, Long, Event<T>> events;

    private static <T> Comparator<Event<T>> createComparator() {
        return Comparator.comparingLong((p_82272_) -> {
            return p_82272_.triggerTime;
        }).thenComparing((p_82269_) -> {
            return p_82269_.sequentialId;
        });
    }

    public TimerQueue(TimerCallbacks<T> p_82249_, Stream<? extends Dynamic<?>> p_82250_) {
        this(p_82249_);
        this.queue.clear();
        this.events.clear();
        this.sequentialId = UnsignedLong.ZERO;
        p_82250_.forEach((p_265027_) -> {
            Tag $$1 = (Tag)p_265027_.convert(NbtOps.INSTANCE).getValue();
            if ($$1 instanceof CompoundTag $$2) {
                this.loadEvent($$2);
            } else {
                LOGGER.warn("Invalid format of events: {}", $$1);
            }

        });
    }

    public TimerQueue(TimerCallbacks<T> p_82247_) {
        this.queue = new PriorityQueue(createComparator());
        this.sequentialId = UnsignedLong.ZERO;
        this.events = HashBasedTable.create();
        this.callbacksRegistry = p_82247_;
    }

    public void tick(T p_82257_, long p_82258_) {
        while(true) {
            Event<T> $$2 = (Event)this.queue.peek();
            if ($$2 == null || $$2.triggerTime > p_82258_) {
                return;
            }

            this.queue.remove();
            this.events.remove($$2.id, p_82258_);
            $$2.callback.handle(p_82257_, this, p_82258_);
        }
    }

    public void schedule(String p_82262_, long p_82263_, TimerCallback<T> p_82264_) {
        if (!this.events.contains(p_82262_, p_82263_)) {
            this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
            Event<T> $$3 = new Event(p_82263_, this.sequentialId, p_82262_, p_82264_);
            this.events.put(p_82262_, p_82263_, $$3);
            this.queue.add($$3);
        }
    }

    public int remove(String p_82260_) {
        Collection<Event<T>> $$1 = this.events.row(p_82260_).values();
        Queue var10001 = this.queue;
        Objects.requireNonNull(var10001);
        $$1.forEach(var10001::remove);
        int $$2 = $$1.size();
        $$1.clear();
        return $$2;
    }

    public Set<String> getEventsIds() {
        return Collections.unmodifiableSet(this.events.rowKeySet());
    }

    private void loadEvent(CompoundTag p_82266_) {
        CompoundTag $$1 = p_82266_.getCompound("Callback");
        TimerCallback<T> $$2 = this.callbacksRegistry.deserialize($$1);
        if ($$2 != null) {
            String $$3 = p_82266_.getString("Name");
            long $$4 = p_82266_.getLong("TriggerTime");
            this.schedule($$3, $$4, $$2);
        }

    }

    private CompoundTag storeEvent(Event<T> p_82255_) {
        CompoundTag $$1 = new CompoundTag();
        $$1.putString("Name", p_82255_.id);
        $$1.putLong("TriggerTime", p_82255_.triggerTime);
        $$1.put("Callback", this.callbacksRegistry.serialize(p_82255_.callback));
        return $$1;
    }

    public ListTag store() {
        ListTag $$0 = new ListTag();
        Stream var10000 = this.queue.stream().sorted(createComparator()).map(this::storeEvent);
        Objects.requireNonNull($$0);
        var10000.forEach($$0::add);
        return $$0;
    }

    public static class Event<T> {
        public final long triggerTime;
        public final UnsignedLong sequentialId;
        public final String id;
        public final TimerCallback<T> callback;

        Event(long p_82278_, UnsignedLong p_82279_, String p_82280_, TimerCallback<T> p_82281_) {
            this.triggerTime = p_82278_;
            this.sequentialId = p_82279_;
            this.id = p_82280_;
            this.callback = p_82281_;
        }
    }
}
