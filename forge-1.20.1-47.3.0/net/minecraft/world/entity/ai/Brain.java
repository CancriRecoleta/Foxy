//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraftforge.common.util.BrainBuilder;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class Brain<E extends LivingEntity> {
    static final Logger LOGGER = LogUtils.getLogger();
    private final Supplier<Codec<Brain<E>>> codec;
    private static final int SCHEDULE_UPDATE_DELAY = 20;
    private final Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> memories = Maps.newHashMap();
    private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
    private final Map<Integer, Map<Activity, Set<BehaviorControl<? super E>>>> availableBehaviorsByPriority = Maps.newTreeMap();
    private Schedule schedule;
    private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> activityRequirements;
    private final Map<Activity, Set<MemoryModuleType<?>>> activityMemoriesToEraseWhenStopped;
    private Set<Activity> coreActivities;
    private final Set<Activity> activeActivities;
    private Activity defaultActivity;
    private long lastScheduleUpdate;

    public static <E extends LivingEntity> Provider<E> provider(Collection<? extends MemoryModuleType<?>> p_21924_, Collection<? extends SensorType<? extends Sensor<? super E>>> p_21925_) {
        return new Provider(p_21924_, p_21925_);
    }

    public static <E extends LivingEntity> Codec<Brain<E>> codec(final Collection<? extends MemoryModuleType<?>> p_21947_, final Collection<? extends SensorType<? extends Sensor<? super E>>> p_21948_) {
        final MutableObject<Codec<Brain<E>>> mutableobject = new MutableObject();
        mutableobject.setValue((new MapCodec<Brain<E>>() {
            public <T> Stream<T> keys(DynamicOps<T> p_22029_) {
                return p_21947_.stream().flatMap((p_22020_) -> {
                    return p_22020_.getCodec().map((p_258254_) -> {
                        return BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(p_22020_);
                    }).stream();
                }).map((p_22018_) -> {
                    return p_22029_.createString(p_22018_.toString());
                });
            }

            public <T> DataResult<Brain<E>> decode(DynamicOps<T> p_22022_, MapLike<T> p_22023_) {
                MutableObject<DataResult<ImmutableList.Builder<MemoryValue<?>>>> mutableobject1 = new MutableObject(DataResult.success(ImmutableList.builder()));
                p_22023_.entries().forEach((p_258252_) -> {
                    DataResult<MemoryModuleType<?>> dataresult = BuiltInRegistries.MEMORY_MODULE_TYPE.byNameCodec().parse(p_22022_, p_258252_.getFirst());
                    DataResult<? extends MemoryValue<?>> dataresult1 = dataresult.flatMap((p_147350_) -> {
                        return this.captureRead(p_147350_, p_22022_, p_258252_.getSecond());
                    });
                    mutableobject1.setValue(((DataResult)mutableobject1.getValue()).apply2(ImmutableList.Builder::add, dataresult1));
                });
                DataResult var10000 = (DataResult)mutableobject1.getValue();
                Logger var10001 = Brain.LOGGER;
                Objects.requireNonNull(var10001);
                ImmutableList<MemoryValue<?>> immutablelist = (ImmutableList)var10000.resultOrPartial(var10001::error).map(ImmutableList.Builder::build).orElseGet(ImmutableList::of);
                Collection var10002 = p_21947_;
                Collection var10003 = p_21948_;
                MutableObject var10005 = mutableobject;
                Objects.requireNonNull(var10005);
                return DataResult.success(new Brain(var10002, var10003, immutablelist, var10005::getValue));
            }

            private <T, U> DataResult<MemoryValue<U>> captureRead(MemoryModuleType<U> p_21997_, DynamicOps<T> p_21998_, T p_21999_) {
                return ((DataResult)p_21997_.getCodec().map(DataResult::success).orElseGet(() -> {
                    return DataResult.error(() -> {
                        return "No codec for memory: " + p_21997_;
                    });
                })).flatMap((p_22011_) -> {
                    return p_22011_.parse(p_21998_, p_21999_);
                }).map((p_21992_) -> {
                    return new MemoryValue(p_21997_, Optional.of(p_21992_));
                });
            }

            public <T> RecordBuilder<T> encode(Brain<E> p_21985_, DynamicOps<T> p_21986_, RecordBuilder<T> p_21987_) {
                p_21985_.memories().forEach((p_22007_) -> {
                    p_22007_.serialize(p_21986_, p_21987_);
                });
                return p_21987_;
            }
        }).fieldOf("memories").codec());
        return (Codec)mutableobject.getValue();
    }

    public Brain(Collection<? extends MemoryModuleType<?>> p_21855_, Collection<? extends SensorType<? extends Sensor<? super E>>> p_21856_, ImmutableList<MemoryValue<?>> p_21857_, Supplier<Codec<Brain<E>>> p_21858_) {
        this.schedule = Schedule.EMPTY;
        this.activityRequirements = Maps.newHashMap();
        this.activityMemoriesToEraseWhenStopped = Maps.newHashMap();
        this.coreActivities = Sets.newHashSet();
        this.activeActivities = Sets.newHashSet();
        this.defaultActivity = Activity.IDLE;
        this.lastScheduleUpdate = -9999L;
        this.codec = p_21858_;
        Iterator var5 = p_21855_.iterator();

        while(var5.hasNext()) {
            MemoryModuleType<?> memorymoduletype = (MemoryModuleType)var5.next();
            this.memories.put(memorymoduletype, Optional.empty());
        }

        var5 = p_21856_.iterator();

        while(var5.hasNext()) {
            SensorType<? extends Sensor<? super E>> sensortype = (SensorType)var5.next();
            this.sensors.put(sensortype, sensortype.create());
        }

        var5 = this.sensors.values().iterator();

        while(var5.hasNext()) {
            Sensor<? super E> sensor = (Sensor)var5.next();
            Iterator var7 = sensor.requires().iterator();

            while(var7.hasNext()) {
                MemoryModuleType<?> memorymoduletype1 = (MemoryModuleType)var7.next();
                this.memories.put(memorymoduletype1, Optional.empty());
            }
        }

        UnmodifiableIterator var9 = p_21857_.iterator();

        while(var9.hasNext()) {
            MemoryValue<?> memoryvalue = (MemoryValue)var9.next();
            memoryvalue.setMemoryInternal(this);
        }

    }

    public <T> DataResult<T> serializeStart(DynamicOps<T> p_21915_) {
        return ((Codec)this.codec.get()).encodeStart(p_21915_, this);
    }

    Stream<MemoryValue<?>> memories() {
        return this.memories.entrySet().stream().map((p_21929_) -> {
            return net.minecraft.world.entity.ai.Brain.MemoryValue.createUnchecked((MemoryModuleType)p_21929_.getKey(), (Optional)p_21929_.getValue());
        });
    }

    public boolean hasMemoryValue(MemoryModuleType<?> p_21875_) {
        return this.checkMemory(p_21875_, MemoryStatus.VALUE_PRESENT);
    }

    public void clearMemories() {
        this.memories.keySet().forEach((p_276103_) -> {
            this.memories.put(p_276103_, Optional.empty());
        });
    }

    public <U> void eraseMemory(MemoryModuleType<U> p_21937_) {
        this.setMemory(p_21937_, Optional.empty());
    }

    public <U> void setMemory(MemoryModuleType<U> p_21880_, @Nullable U p_21881_) {
        this.setMemory(p_21880_, Optional.ofNullable(p_21881_));
    }

    public <U> void setMemoryWithExpiry(MemoryModuleType<U> p_21883_, U p_21884_, long p_21885_) {
        this.setMemoryInternal(p_21883_, Optional.of(ExpirableValue.of(p_21884_, p_21885_)));
    }

    public <U> void setMemory(MemoryModuleType<U> p_21887_, Optional<? extends U> p_21888_) {
        this.setMemoryInternal(p_21887_, p_21888_.map(ExpirableValue::of));
    }

    <U> void setMemoryInternal(MemoryModuleType<U> p_21942_, Optional<? extends ExpirableValue<?>> p_21943_) {
        if (this.memories.containsKey(p_21942_)) {
            if (p_21943_.isPresent() && this.isEmptyCollection(((ExpirableValue)p_21943_.get()).getValue())) {
                this.eraseMemory(p_21942_);
            } else {
                this.memories.put(p_21942_, p_21943_);
            }
        }

    }

    public <U> Optional<U> getMemory(MemoryModuleType<U> p_21953_) {
        Optional<? extends ExpirableValue<?>> optional = (Optional)this.memories.get(p_21953_);
        if (optional == null) {
            throw new IllegalStateException("Unregistered memory fetched: " + p_21953_);
        } else {
            return optional.map(ExpirableValue::getValue);
        }
    }

    @Nullable
    public <U> Optional<U> getMemoryInternal(MemoryModuleType<U> p_259344_) {
        Optional<? extends ExpirableValue<?>> optional = (Optional)this.memories.get(p_259344_);
        return optional == null ? null : optional.map(ExpirableValue::getValue);
    }

    public <U> long getTimeUntilExpiry(MemoryModuleType<U> p_147342_) {
        Optional<? extends ExpirableValue<?>> optional = (Optional)this.memories.get(p_147342_);
        return (Long)optional.map(ExpirableValue::getTimeToLive).orElse(0L);
    }

    /** @deprecated */
    @Deprecated
    @VisibleForDebug
    public Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> getMemories() {
        return this.memories;
    }

    public <U> boolean isMemoryValue(MemoryModuleType<U> p_21939_, U p_21940_) {
        return !this.hasMemoryValue(p_21939_) ? false : this.getMemory(p_21939_).filter((p_21922_) -> {
            return p_21922_.equals(p_21940_);
        }).isPresent();
    }

    public boolean checkMemory(MemoryModuleType<?> p_21877_, MemoryStatus p_21878_) {
        Optional<? extends ExpirableValue<?>> optional = (Optional)this.memories.get(p_21877_);
        if (optional == null) {
            return false;
        } else {
            return p_21878_ == MemoryStatus.REGISTERED || p_21878_ == MemoryStatus.VALUE_PRESENT && optional.isPresent() || p_21878_ == MemoryStatus.VALUE_ABSENT && !optional.isPresent();
        }
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Schedule p_21913_) {
        this.schedule = p_21913_;
    }

    public void setCoreActivities(Set<Activity> p_21931_) {
        this.coreActivities = p_21931_;
    }

    /** @deprecated */
    @Deprecated
    @VisibleForDebug
    public Set<Activity> getActiveActivities() {
        return this.activeActivities;
    }

    /** @deprecated */
    @Deprecated
    @VisibleForDebug
    public List<BehaviorControl<? super E>> getRunningBehaviors() {
        List<BehaviorControl<? super E>> list = new ObjectArrayList();
        Iterator var2 = this.availableBehaviorsByPriority.values().iterator();

        while(var2.hasNext()) {
            Map<Activity, Set<BehaviorControl<? super E>>> map = (Map)var2.next();
            Iterator var4 = map.values().iterator();

            while(var4.hasNext()) {
                Set<BehaviorControl<? super E>> set = (Set)var4.next();
                Iterator var6 = set.iterator();

                while(var6.hasNext()) {
                    BehaviorControl<? super E> behaviorcontrol = (BehaviorControl)var6.next();
                    if (behaviorcontrol.getStatus() == Status.RUNNING) {
                        list.add(behaviorcontrol);
                    }
                }
            }
        }

        return list;
    }

    public void useDefaultActivity() {
        this.setActiveActivity(this.defaultActivity);
    }

    public Optional<Activity> getActiveNonCoreActivity() {
        Iterator var1 = this.activeActivities.iterator();

        Activity activity;
        do {
            if (!var1.hasNext()) {
                return Optional.empty();
            }

            activity = (Activity)var1.next();
        } while(this.coreActivities.contains(activity));

        return Optional.of(activity);
    }

    public void setActiveActivityIfPossible(Activity p_21890_) {
        if (this.activityRequirementsAreMet(p_21890_)) {
            this.setActiveActivity(p_21890_);
        } else {
            this.useDefaultActivity();
        }

    }

    private void setActiveActivity(Activity p_21961_) {
        if (!this.isActive(p_21961_)) {
            this.eraseMemoriesForOtherActivitesThan(p_21961_);
            this.activeActivities.clear();
            this.activeActivities.addAll(this.coreActivities);
            this.activeActivities.add(p_21961_);
        }

    }

    private void eraseMemoriesForOtherActivitesThan(Activity p_21967_) {
        Iterator var2 = this.activeActivities.iterator();

        while(true) {
            Set set;
            do {
                Activity activity;
                do {
                    if (!var2.hasNext()) {
                        return;
                    }

                    activity = (Activity)var2.next();
                } while(activity == p_21967_);

                set = (Set)this.activityMemoriesToEraseWhenStopped.get(activity);
            } while(set == null);

            Iterator var5 = set.iterator();

            while(var5.hasNext()) {
                MemoryModuleType<?> memorymoduletype = (MemoryModuleType)var5.next();
                this.eraseMemory(memorymoduletype);
            }
        }
    }

    public void updateActivityFromSchedule(long p_21863_, long p_21864_) {
        if (p_21864_ - this.lastScheduleUpdate > 20L) {
            this.lastScheduleUpdate = p_21864_;
            Activity activity = this.getSchedule().getActivityAt((int)(p_21863_ % 24000L));
            if (!this.activeActivities.contains(activity)) {
                this.setActiveActivityIfPossible(activity);
            }
        }

    }

    public void setActiveActivityToFirstValid(List<Activity> p_21927_) {
        Iterator var2 = p_21927_.iterator();

        while(var2.hasNext()) {
            Activity activity = (Activity)var2.next();
            if (this.activityRequirementsAreMet(activity)) {
                this.setActiveActivity(activity);
                break;
            }
        }

    }

    public void setDefaultActivity(Activity p_21945_) {
        this.defaultActivity = p_21945_;
    }

    public void addActivity(Activity p_21892_, int p_21893_, ImmutableList<? extends BehaviorControl<? super E>> p_21894_) {
        this.addActivity(p_21892_, this.createPriorityPairs(p_21893_, p_21894_));
    }

    public void addActivityAndRemoveMemoryWhenStopped(Activity p_21896_, int p_21897_, ImmutableList<? extends BehaviorControl<? super E>> p_21898_, MemoryModuleType<?> p_21899_) {
        Set<Pair<MemoryModuleType<?>, MemoryStatus>> set = ImmutableSet.of(Pair.of(p_21899_, MemoryStatus.VALUE_PRESENT));
        Set<MemoryModuleType<?>> set1 = ImmutableSet.of(p_21899_);
        this.addActivityAndRemoveMemoriesWhenStopped(p_21896_, this.createPriorityPairs(p_21897_, p_21898_), set, set1);
    }

    public void addActivity(Activity p_21901_, ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> p_21902_) {
        this.addActivityAndRemoveMemoriesWhenStopped(p_21901_, p_21902_, ImmutableSet.of(), Sets.newHashSet());
    }

    public void addActivityWithConditions(Activity p_21904_, ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> p_21905_, Set<Pair<MemoryModuleType<?>, MemoryStatus>> p_21906_) {
        this.addActivityAndRemoveMemoriesWhenStopped(p_21904_, p_21905_, p_21906_, Sets.newHashSet());
    }

    public void addActivityAndRemoveMemoriesWhenStopped(Activity p_21908_, ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> p_21909_, Set<Pair<MemoryModuleType<?>, MemoryStatus>> p_21910_, Set<MemoryModuleType<?>> p_21911_) {
        this.activityRequirements.put(p_21908_, p_21910_);
        if (!p_21911_.isEmpty()) {
            this.activityMemoriesToEraseWhenStopped.put(p_21908_, p_21911_);
        }

        UnmodifiableIterator var5 = p_21909_.iterator();

        while(var5.hasNext()) {
            Pair<Integer, ? extends BehaviorControl<? super E>> pair = (Pair)var5.next();
            ((Set)((Map)this.availableBehaviorsByPriority.computeIfAbsent((Integer)pair.getFirst(), (p_21917_) -> {
                return Maps.newHashMap();
            })).computeIfAbsent(p_21908_, (p_21972_) -> {
                return Sets.newLinkedHashSet();
            })).add((BehaviorControl)pair.getSecond());
        }

    }

    @VisibleForTesting
    public void removeAllBehaviors() {
        this.availableBehaviorsByPriority.clear();
    }

    public boolean isActive(Activity p_21955_) {
        return this.activeActivities.contains(p_21955_);
    }

    public Brain<E> copyWithoutBehaviors() {
        Brain<E> brain = new Brain(this.memories.keySet(), this.sensors.keySet(), ImmutableList.of(), this.codec);
        Iterator var2 = this.memories.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> entry = (Map.Entry)var2.next();
            MemoryModuleType<?> memorymoduletype = (MemoryModuleType)entry.getKey();
            if (((Optional)entry.getValue()).isPresent()) {
                brain.memories.put(memorymoduletype, (Optional)entry.getValue());
            }
        }

        return brain;
    }

    public void tick(ServerLevel p_21866_, E p_21867_) {
        this.forgetOutdatedMemories();
        this.tickSensors(p_21866_, p_21867_);
        this.startEachNonRunningBehavior(p_21866_, p_21867_);
        this.tickEachRunningBehavior(p_21866_, p_21867_);
    }

    private void tickSensors(ServerLevel p_21950_, E p_21951_) {
        Iterator var3 = this.sensors.values().iterator();

        while(var3.hasNext()) {
            Sensor<? super E> sensor = (Sensor)var3.next();
            sensor.tick(p_21950_, p_21951_);
        }

    }

    private void forgetOutdatedMemories() {
        Iterator var1 = this.memories.entrySet().iterator();

        while(var1.hasNext()) {
            Map.Entry<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> entry = (Map.Entry)var1.next();
            if (((Optional)entry.getValue()).isPresent()) {
                ExpirableValue<?> expirablevalue = (ExpirableValue)((Optional)entry.getValue()).get();
                if (expirablevalue.hasExpired()) {
                    this.eraseMemory((MemoryModuleType)entry.getKey());
                }

                expirablevalue.tick();
            }
        }

    }

    public void stopAll(ServerLevel p_21934_, E p_21935_) {
        long i = p_21935_.level().getGameTime();
        Iterator var5 = this.getRunningBehaviors().iterator();

        while(var5.hasNext()) {
            BehaviorControl<? super E> behaviorcontrol = (BehaviorControl)var5.next();
            behaviorcontrol.doStop(p_21934_, p_21935_, i);
        }

    }

    private void startEachNonRunningBehavior(ServerLevel p_21958_, E p_21959_) {
        long i = p_21958_.getGameTime();
        Iterator var5 = this.availableBehaviorsByPriority.values().iterator();

        label34:
        while(var5.hasNext()) {
            Map<Activity, Set<BehaviorControl<? super E>>> map = (Map)var5.next();
            Iterator var7 = map.entrySet().iterator();

            while(true) {
                Map.Entry entry;
                Activity activity;
                do {
                    if (!var7.hasNext()) {
                        continue label34;
                    }

                    entry = (Map.Entry)var7.next();
                    activity = (Activity)entry.getKey();
                } while(!this.activeActivities.contains(activity));

                Iterator var10 = ((Set)entry.getValue()).iterator();

                while(var10.hasNext()) {
                    BehaviorControl<? super E> behaviorcontrol = (BehaviorControl)var10.next();
                    if (behaviorcontrol.getStatus() == Status.STOPPED) {
                        behaviorcontrol.tryStart(p_21958_, p_21959_, i);
                    }
                }
            }
        }

    }

    private void tickEachRunningBehavior(ServerLevel p_21964_, E p_21965_) {
        long i = p_21964_.getGameTime();
        Iterator var5 = this.getRunningBehaviors().iterator();

        while(var5.hasNext()) {
            BehaviorControl<? super E> behaviorcontrol = (BehaviorControl)var5.next();
            behaviorcontrol.tickOrStop(p_21964_, p_21965_, i);
        }

    }

    private boolean activityRequirementsAreMet(Activity p_21970_) {
        if (!this.activityRequirements.containsKey(p_21970_)) {
            return false;
        } else {
            Iterator var2 = ((Set)this.activityRequirements.get(p_21970_)).iterator();

            MemoryModuleType memorymoduletype;
            MemoryStatus memorystatus;
            do {
                if (!var2.hasNext()) {
                    return true;
                }

                Pair<MemoryModuleType<?>, MemoryStatus> pair = (Pair)var2.next();
                memorymoduletype = (MemoryModuleType)pair.getFirst();
                memorystatus = (MemoryStatus)pair.getSecond();
            } while(this.checkMemory(memorymoduletype, memorystatus));

            return false;
        }
    }

    private boolean isEmptyCollection(Object p_21919_) {
        return p_21919_ instanceof Collection && ((Collection)p_21919_).isEmpty();
    }

    ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> createPriorityPairs(int p_21860_, ImmutableList<? extends BehaviorControl<? super E>> p_21861_) {
        int i = p_21860_;
        ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super E>>> builder = ImmutableList.builder();
        UnmodifiableIterator var5 = p_21861_.iterator();

        while(var5.hasNext()) {
            BehaviorControl<? super E> behaviorcontrol = (BehaviorControl)var5.next();
            builder.add(Pair.of(i++, behaviorcontrol));
        }

        return builder.build();
    }

    public BrainBuilder<E> createBuilder() {
        BrainBuilder<E> builder = new BrainBuilder(this);
        builder.getMemoryTypes().addAll(this.memories.keySet());
        builder.getSensorTypes().addAll(this.sensors.keySet());
        builder.addAvailableBehaviorsByPriorityFrom(this.availableBehaviorsByPriority);
        builder.setSchedule(this.getSchedule());
        builder.addActivityRequirementsFrom(this.activityRequirements);
        builder.addActivityMemoriesToEraseWhenStoppedFrom(this.activityMemoriesToEraseWhenStopped);
        builder.getCoreActivities().addAll(this.coreActivities);
        builder.setDefaultActivity(this.defaultActivity);
        builder.setActiveActivites(this.activeActivities);
        return builder;
    }

    public void copyFromBuilder(BrainBuilder<E> builder) {
        builder.addAvailableBehaviorsByPriorityTo(this.availableBehaviorsByPriority);
        this.setSchedule(builder.getSchedule());
        builder.addActivityRequirementsTo(this.activityRequirements);
        builder.addActivityMemoriesToEraseWhenStoppedTo(this.activityMemoriesToEraseWhenStopped);
        this.setCoreActivities(builder.getCoreActivities());
        this.setDefaultActivity(builder.getDefaultActivity());
        this.activeActivities.addAll(builder.getActiveActivites());
    }

    public static final class Provider<E extends LivingEntity> {
        private final Collection<? extends MemoryModuleType<?>> memoryTypes;
        private final Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes;
        private final Codec<Brain<E>> codec;

        Provider(Collection<? extends MemoryModuleType<?>> p_22066_, Collection<? extends SensorType<? extends Sensor<? super E>>> p_22067_) {
            this.memoryTypes = p_22066_;
            this.sensorTypes = p_22067_;
            this.codec = Brain.codec(p_22066_, p_22067_);
        }

        public Brain<E> makeBrain(Dynamic<?> p_22074_) {
            DataResult var10000 = this.codec.parse(p_22074_);
            Logger var10001 = Brain.LOGGER;
            Objects.requireNonNull(var10001);
            return (Brain)var10000.resultOrPartial(var10001::error).orElseGet(() -> {
                return new Brain(this.memoryTypes, this.sensorTypes, ImmutableList.of(), () -> {
                    return this.codec;
                });
            });
        }
    }

    static final class MemoryValue<U> {
        private final MemoryModuleType<U> type;
        private final Optional<? extends ExpirableValue<U>> value;

        static <U> MemoryValue<U> createUnchecked(MemoryModuleType<U> p_22060_, Optional<? extends ExpirableValue<?>> p_22061_) {
            return new MemoryValue(p_22060_, p_22061_);
        }

        MemoryValue(MemoryModuleType<U> p_22033_, Optional<? extends ExpirableValue<U>> p_22034_) {
            this.type = p_22033_;
            this.value = p_22034_;
        }

        void setMemoryInternal(Brain<?> p_22043_) {
            p_22043_.setMemoryInternal(this.type, this.value);
        }

        public <T> void serialize(DynamicOps<T> p_22048_, RecordBuilder<T> p_22049_) {
            this.type.getCodec().ifPresent((p_22053_) -> {
                this.value.ifPresent((p_258258_) -> {
                    p_22049_.add(BuiltInRegistries.MEMORY_MODULE_TYPE.byNameCodec().encodeStart(p_22048_, this.type), p_22053_.encodeStart(p_22048_, p_258258_));
                });
            });
        }
    }
}
