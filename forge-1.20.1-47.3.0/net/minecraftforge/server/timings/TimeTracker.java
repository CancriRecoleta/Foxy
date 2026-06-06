//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.server.timings;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MapMaker;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TimeTracker<T> {
    public static final TimeTracker<BlockEntity> BLOCK_ENTITY_UPDATE = new TimeTracker();
    public static final TimeTracker<Entity> ENTITY_UPDATE = new TimeTracker();
    private boolean enabled;
    private int trackingDuration;
    private Map<T, int[]> timings = (new MapMaker()).weakKeys().makeMap();
    private WeakReference<T> currentlyTracking;
    private long trackTime;
    private long timing;

    public TimeTracker() {
    }

    public ImmutableList<ForgeTimings<T>> getTimingData() {
        ImmutableList.Builder<ForgeTimings<T>> builder = ImmutableList.builder();
        Iterator var2 = this.timings.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<T, int[]> entry = (Map.Entry)var2.next();
            builder.add(new ForgeTimings(entry.getKey(), Arrays.copyOfRange((int[])entry.getValue(), 0, 99)));
        }

        return builder.build();
    }

    public void reset() {
        this.enabled = false;
        this.trackTime = 0L;
        this.timings.clear();
    }

    public void trackEnd(T tracking) {
        if (this.enabled) {
            this.trackEnd(tracking, System.nanoTime());
        }
    }

    public void enable(int duration) {
        this.trackingDuration = duration;
        this.enabled = true;
    }

    public void trackStart(T toTrack) {
        if (this.enabled) {
            this.trackStart(toTrack, System.nanoTime());
        }
    }

    private void trackEnd(T object, long nanoTime) {
        if (this.currentlyTracking != null && this.currentlyTracking.get() == object) {
            int[] timings = (int[])this.timings.computeIfAbsent(object, (k) -> {
                return new int[101];
            });
            int idx = timings[100] = (timings[100] + 1) % 100;
            timings[idx] = (int)(nanoTime - this.timing);
        } else {
            this.currentlyTracking = null;
        }
    }

    private void trackStart(T toTrack, long nanoTime) {
        if (this.trackTime == 0L) {
            this.trackTime = nanoTime;
        } else if (this.trackTime + TimeUnit.NANOSECONDS.convert((long)this.trackingDuration, TimeUnit.SECONDS) < nanoTime) {
            this.enabled = false;
            this.trackTime = 0L;
        }

        this.currentlyTracking = new WeakReference(toTrack);
        this.timing = nanoTime;
    }
}
