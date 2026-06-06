//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.commons.lang3.mutable.MutableLong;

public class AcquirePoi {
    public static final int SCAN_RANGE = 48;

    public AcquirePoi() {
    }

    public static BehaviorControl<PathfinderMob> create(Predicate<Holder<PoiType>> p_259994_, MemoryModuleType<GlobalPos> p_259167_, boolean p_259077_, Optional<Byte> p_259824_) {
        return create(p_259994_, p_259167_, p_259167_, p_259077_, p_259824_);
    }

    public static BehaviorControl<PathfinderMob> create(Predicate<Holder<PoiType>> p_260007_, MemoryModuleType<GlobalPos> p_259129_, MemoryModuleType<GlobalPos> p_260194_, boolean p_259108_, Optional<Byte> p_260129_) {
        int $$5 = true;
        int $$6 = true;
        MutableLong $$7 = new MutableLong(0L);
        Long2ObjectMap<JitteredLinearRetry> $$8 = new Long2ObjectOpenHashMap();
        OneShot<PathfinderMob> $$9 = BehaviorBuilder.create((p_258276_) -> {
            return p_258276_.group(p_258276_.absent(p_260194_)).apply(p_258276_, (p_258300_) -> {
                return (p_258292_, p_258293_, p_258294_) -> {
                    if (p_259108_ && p_258293_.isBaby()) {
                        return false;
                    } else if ($$7.getValue() == 0L) {
                        $$7.setValue(p_258292_.getGameTime() + (long)p_258292_.random.nextInt(20));
                        return false;
                    } else if (p_258292_.getGameTime() < $$7.getValue()) {
                        return false;
                    } else {
                        $$7.setValue(p_258294_ + 20L + (long)p_258292_.getRandom().nextInt(20));
                        PoiManager $$9 = p_258292_.getPoiManager();
                        $$8.long2ObjectEntrySet().removeIf((p_22338_) -> {
                            return !((JitteredLinearRetry)p_22338_.getValue()).isStillValid(p_258294_);
                        });
                        Predicate<BlockPos> $$10 = (p_258266_) -> {
                            JitteredLinearRetry $$3 = (JitteredLinearRetry)$$8.get(p_258266_.asLong());
                            if ($$3 == null) {
                                return true;
                            } else if (!$$3.shouldRetry(p_258294_)) {
                                return false;
                            } else {
                                $$3.markAttempt(p_258294_);
                                return true;
                            }
                        };
                        Set<Pair<Holder<PoiType>, BlockPos>> $$11 = (Set)$$9.findAllClosestFirstWithType(p_260007_, $$10, p_258293_.blockPosition(), 48, Occupancy.HAS_SPACE).limit(5L).collect(Collectors.toSet());
                        Path $$12 = findPathToPois(p_258293_, $$11);
                        if ($$12 != null && $$12.canReach()) {
                            BlockPos $$13 = $$12.getTarget();
                            $$9.getType($$13).ifPresent((p_288780_) -> {
                                $$9.take(p_260007_, (p_217108_, p_217109_) -> {
                                    return p_217109_.equals($$13);
                                }, $$13, 1);
                                p_258300_.set(GlobalPos.of(p_258292_.dimension(), $$13));
                                p_260129_.ifPresent((p_147369_) -> {
                                    p_258292_.broadcastEntityEvent(p_258293_, p_147369_);
                                });
                                $$8.clear();
                                DebugPackets.sendPoiTicketCountPacket(p_258292_, $$13);
                            });
                        } else {
                            Iterator var14 = $$11.iterator();

                            while(var14.hasNext()) {
                                Pair<Holder<PoiType>, BlockPos> $$14 = (Pair)var14.next();
                                $$8.computeIfAbsent(((BlockPos)$$14.getSecond()).asLong(), (p_264881_) -> {
                                    return new JitteredLinearRetry(p_258292_.random, p_258294_);
                                });
                            }
                        }

                        return true;
                    }
                };
            });
        });
        return p_260194_ == p_259129_ ? $$9 : BehaviorBuilder.create((p_258269_) -> {
            return p_258269_.group(p_258269_.absent(p_259129_)).apply(p_258269_, (p_258302_) -> {
                return $$9;
            });
        });
    }

    @Nullable
    public static Path findPathToPois(Mob p_217098_, Set<Pair<Holder<PoiType>, BlockPos>> p_217099_) {
        if (p_217099_.isEmpty()) {
            return null;
        } else {
            Set<BlockPos> $$2 = new HashSet();
            int $$3 = 1;
            Iterator var4 = p_217099_.iterator();

            while(var4.hasNext()) {
                Pair<Holder<PoiType>, BlockPos> $$4 = (Pair)var4.next();
                $$3 = Math.max($$3, ((PoiType)((Holder)$$4.getFirst()).value()).validRange());
                $$2.add((BlockPos)$$4.getSecond());
            }

            return p_217098_.getNavigation().createPath((Set)$$2, $$3);
        }
    }

    private static class JitteredLinearRetry {
        private static final int MIN_INTERVAL_INCREASE = 40;
        private static final int MAX_INTERVAL_INCREASE = 80;
        private static final int MAX_RETRY_PATHFINDING_INTERVAL = 400;
        private final RandomSource random;
        private long previousAttemptTimestamp;
        private long nextScheduledAttemptTimestamp;
        private int currentDelay;

        JitteredLinearRetry(RandomSource p_217111_, long p_217112_) {
            this.random = p_217111_;
            this.markAttempt(p_217112_);
        }

        public void markAttempt(long p_22381_) {
            this.previousAttemptTimestamp = p_22381_;
            int $$1 = this.currentDelay + this.random.nextInt(40) + 40;
            this.currentDelay = Math.min($$1, 400);
            this.nextScheduledAttemptTimestamp = p_22381_ + (long)this.currentDelay;
        }

        public boolean isStillValid(long p_22383_) {
            return p_22383_ - this.previousAttemptTimestamp < 400L;
        }

        public boolean shouldRetry(long p_22385_) {
            return p_22385_ >= this.nextScheduledAttemptTimestamp;
        }

        public String toString() {
            return "RetryMarker{, previousAttemptAt=" + this.previousAttemptTimestamp + ", nextScheduledAttemptAt=" + this.nextScheduledAttemptTimestamp + ", currentDelay=" + this.currentDelay + "}";
        }
    }
}
