//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.schedule;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ScheduleBuilder {
    private final Schedule schedule;
    private final List<ActivityTransition> transitions = Lists.newArrayList();

    public ScheduleBuilder(Schedule p_38038_) {
        this.schedule = p_38038_;
    }

    public ScheduleBuilder changeActivityAt(int p_38041_, Activity p_38042_) {
        this.transitions.add(new ActivityTransition(p_38041_, p_38042_));
        return this;
    }

    public Schedule build() {
        Set var10000 = (Set)this.transitions.stream().map(ActivityTransition::getActivity).collect(Collectors.toSet());
        Schedule var10001 = this.schedule;
        Objects.requireNonNull(var10001);
        var10000.forEach(var10001::ensureTimelineExistsFor);
        this.transitions.forEach((p_38044_) -> {
            Activity $$1 = p_38044_.getActivity();
            this.schedule.getAllTimelinesExceptFor($$1).forEach((p_150245_) -> {
                p_150245_.addKeyframe(p_38044_.getTime(), 0.0F);
            });
            this.schedule.getTimelineFor($$1).addKeyframe(p_38044_.getTime(), 1.0F);
        });
        return this.schedule;
    }

    private static class ActivityTransition {
        private final int time;
        private final Activity activity;

        public ActivityTransition(int p_38051_, Activity p_38052_) {
            this.time = p_38051_;
            this.activity = p_38052_;
        }

        public int getTime() {
            return this.time;
        }

        public Activity getActivity() {
            return this.activity;
        }
    }
}
