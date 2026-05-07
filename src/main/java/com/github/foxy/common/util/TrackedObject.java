package com.github.foxy.common.util;

import com.github.foxy.common.Logger;
import com.github.foxy.commonImpl.FoxyCommon;

import java.lang.ref.Cleaner;

import static com.github.foxy.common.util.GlobalCleaner.CLEANER;

public abstract class TrackedObject {
    //TODO: maybe make this false? for performance overhead?
    public static final boolean TRACK_OBJECT_ALLOCATIONS = FoxyCommon.isVerificationFlagOn("ensureTrackedObjectsAreFreed", true);
    public static final boolean TRACK_OBJECT_ALLOCATION_STACKS = FoxyCommon.isVerificationFlagOn("trackObjectAllocationStacks");

    private final Ref ref;
    protected TrackedObject() {
        this(true);
    }

    protected TrackedObject(boolean shouldTrack) {
        this.ref = register(shouldTrack, this);
    }

    protected TrackedObject(Object forObj, boolean shouldTrack) {
        this.ref = register(shouldTrack, forObj);
    }

    protected void free0() {
        if (this.isFreed()) {
            throw new IllegalStateException("Object " + this + " was double freed.");
        }
        this.ref.freedRef[0] = true;
        if (TRACK_OBJECT_ALLOCATIONS) {
            if (this.ref.cleanable != null) {
                this.ref.cleanable.clean();
            }
        }
    }

    public abstract void free();

    public void assertNotFreed() {
        if (isFreed()) {
            throw new IllegalStateException("Object " + this + " should not be free, but is");
        }
    }

    public boolean isFreed() {
        return this.ref.freedRef[0];
    }

    public record Ref(Cleaner.Cleanable cleanable, boolean[] freedRef) {}

    public static Ref register(boolean track, Object obj) {
        boolean[] freed = new boolean[1];
        Cleaner.Cleanable cleanable = null;
        if (TRACK_OBJECT_ALLOCATIONS && track) {
            String clazz = obj.getClass().getName();
            Throwable trace;
            if (TRACK_OBJECT_ALLOCATION_STACKS) {
                trace = new Throwable();
                trace.fillInStackTrace();
            } else {
                trace = null;
            }
            cleanable = CLEANER.register(obj, () -> {
                if (!freed[0]) {
                    if (trace == null) {
                        Logger.error("Object named: " + clazz + " was not freed, location at:\nEnable allocation stack tracing");
                    } else {
                        Logger.error("Object named: " + clazz + " was not freed, location at:", trace);
                    }
                }
            });
        }
        return new Ref(cleanable, freed);
    }

    public static final class TrackedObjectObject extends TrackedObject {
        private TrackedObjectObject(Object forObj) {
            super(forObj, true);
        }

        @Override
        public void free() {
            this.free0();
        }
    }

    public static TrackedObject createTrackedObject(Object forObj) {
        return new TrackedObjectObject(forObj);
    }

}

