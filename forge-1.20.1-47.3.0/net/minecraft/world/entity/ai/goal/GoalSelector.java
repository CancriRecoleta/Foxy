//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.goal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class GoalSelector {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final WrappedGoal NO_GOAL = new WrappedGoal(Integer.MAX_VALUE, new Goal() {
        public boolean canUse() {
            return false;
        }
    }) {
        public boolean isRunning() {
            return false;
        }
    };
    private final Map<Goal.Flag, WrappedGoal> lockedFlags = new EnumMap(Goal.Flag.class);
    private final Set<WrappedGoal> availableGoals = Sets.newLinkedHashSet();
    private final Supplier<ProfilerFiller> profiler;
    private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);
    private int tickCount;
    private int newGoalRate = 3;

    public GoalSelector(Supplier<ProfilerFiller> p_25351_) {
        this.profiler = p_25351_;
    }

    public void addGoal(int p_25353_, Goal p_25354_) {
        this.availableGoals.add(new WrappedGoal(p_25353_, p_25354_));
    }

    @VisibleForTesting
    public void removeAllGoals(Predicate<Goal> p_262575_) {
        this.availableGoals.removeIf((p_262564_) -> {
            return p_262575_.test(p_262564_.getGoal());
        });
    }

    public void removeGoal(Goal p_25364_) {
        this.availableGoals.stream().filter((p_25378_) -> {
            return p_25378_.getGoal() == p_25364_;
        }).filter(WrappedGoal::isRunning).forEach(WrappedGoal::stop);
        this.availableGoals.removeIf((p_25367_) -> {
            return p_25367_.getGoal() == p_25364_;
        });
    }

    private static boolean goalContainsAnyFlags(WrappedGoal p_186076_, EnumSet<Goal.Flag> p_186077_) {
        Iterator var2 = p_186076_.getFlags().iterator();

        Goal.Flag $$2;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            $$2 = (Goal.Flag)var2.next();
        } while(!p_186077_.contains($$2));

        return true;
    }

    private static boolean goalCanBeReplacedForAllFlags(WrappedGoal p_186079_, Map<Goal.Flag, WrappedGoal> p_186080_) {
        Iterator var2 = p_186079_.getFlags().iterator();

        Goal.Flag $$2;
        do {
            if (!var2.hasNext()) {
                return true;
            }

            $$2 = (Goal.Flag)var2.next();
        } while(((WrappedGoal)p_186080_.getOrDefault($$2, NO_GOAL)).canBeReplacedBy(p_186079_));

        return false;
    }

    public void tick() {
        ProfilerFiller $$0 = (ProfilerFiller)this.profiler.get();
        $$0.push("goalCleanup");
        Iterator $$2 = this.availableGoals.iterator();

        while(true) {
            WrappedGoal $$4;
            do {
                do {
                    if (!$$2.hasNext()) {
                        $$2 = this.lockedFlags.entrySet().iterator();

                        while($$2.hasNext()) {
                            Map.Entry<Goal.Flag, WrappedGoal> $$3 = (Map.Entry)$$2.next();
                            if (!((WrappedGoal)$$3.getValue()).isRunning()) {
                                $$2.remove();
                            }
                        }

                        $$0.pop();
                        $$0.push("goalUpdate");
                        $$2 = this.availableGoals.iterator();

                        while(true) {
                            do {
                                do {
                                    do {
                                        do {
                                            if (!$$2.hasNext()) {
                                                $$0.pop();
                                                this.tickRunningGoals(true);
                                                return;
                                            }

                                            $$4 = (WrappedGoal)$$2.next();
                                        } while($$4.isRunning());
                                    } while(goalContainsAnyFlags($$4, this.disabledFlags));
                                } while(!goalCanBeReplacedForAllFlags($$4, this.lockedFlags));
                            } while(!$$4.canUse());

                            Iterator var4 = $$4.getFlags().iterator();

                            while(var4.hasNext()) {
                                Goal.Flag $$5 = (Goal.Flag)var4.next();
                                WrappedGoal $$6 = (WrappedGoal)this.lockedFlags.getOrDefault($$5, NO_GOAL);
                                $$6.stop();
                                this.lockedFlags.put($$5, $$4);
                            }

                            $$4.start();
                        }
                    }

                    $$4 = (WrappedGoal)$$2.next();
                } while(!$$4.isRunning());
            } while(!goalContainsAnyFlags($$4, this.disabledFlags) && $$4.canContinueToUse());

            $$4.stop();
        }
    }

    public void tickRunningGoals(boolean p_186082_) {
        ProfilerFiller $$1 = (ProfilerFiller)this.profiler.get();
        $$1.push("goalTick");
        Iterator var3 = this.availableGoals.iterator();

        while(true) {
            WrappedGoal $$2;
            do {
                do {
                    if (!var3.hasNext()) {
                        $$1.pop();
                        return;
                    }

                    $$2 = (WrappedGoal)var3.next();
                } while(!$$2.isRunning());
            } while(!p_186082_ && !$$2.requiresUpdateEveryTick());

            $$2.tick();
        }
    }

    public Set<WrappedGoal> getAvailableGoals() {
        return this.availableGoals;
    }

    public Stream<WrappedGoal> getRunningGoals() {
        return this.availableGoals.stream().filter(WrappedGoal::isRunning);
    }

    public void setNewGoalRate(int p_148098_) {
        this.newGoalRate = p_148098_;
    }

    public void disableControlFlag(Goal.Flag p_25356_) {
        this.disabledFlags.add(p_25356_);
    }

    public void enableControlFlag(Goal.Flag p_25375_) {
        this.disabledFlags.remove(p_25375_);
    }

    public void setControlFlag(Goal.Flag p_25361_, boolean p_25362_) {
        if (p_25362_) {
            this.enableControlFlag(p_25361_);
        } else {
            this.disableControlFlag(p_25361_);
        }

    }
}
