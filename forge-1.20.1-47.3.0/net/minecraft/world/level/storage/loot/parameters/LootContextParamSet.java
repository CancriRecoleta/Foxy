//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class LootContextParamSet {
    private final Set<LootContextParam<?>> required;
    private final Set<LootContextParam<?>> all;

    LootContextParamSet(Set<LootContextParam<?>> p_81388_, Set<LootContextParam<?>> p_81389_) {
        this.required = ImmutableSet.copyOf(p_81388_);
        this.all = ImmutableSet.copyOf(Sets.union(p_81388_, p_81389_));
    }

    public boolean isAllowed(LootContextParam<?> p_165476_) {
        return this.all.contains(p_165476_);
    }

    public Set<LootContextParam<?>> getRequired() {
        return this.required;
    }

    public Set<LootContextParam<?>> getAllowed() {
        return this.all;
    }

    public String toString() {
        Joiner var10000 = Joiner.on(", ");
        Iterator var10001 = this.all.stream().map((p_81400_) -> {
            String var10000 = this.required.contains(p_81400_) ? "!" : "";
            return var10000 + p_81400_.getName();
        }).iterator();
        return "[" + var10000.join(var10001) + "]";
    }

    public void validateUser(ValidationContext p_81396_, LootContextUser p_81397_) {
        Set<LootContextParam<?>> $$2 = p_81397_.getReferencedContextParams();
        Set<LootContextParam<?>> $$3 = Sets.difference($$2, this.all);
        if (!$$3.isEmpty()) {
            p_81396_.reportProblem("Parameters " + $$3 + " are not provided in this context");
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Set<LootContextParam<?>> required = Sets.newIdentityHashSet();
        private final Set<LootContextParam<?>> optional = Sets.newIdentityHashSet();

        public Builder() {
        }

        public Builder required(LootContextParam<?> p_81407_) {
            if (this.optional.contains(p_81407_)) {
                throw new IllegalArgumentException("Parameter " + p_81407_.getName() + " is already optional");
            } else {
                this.required.add(p_81407_);
                return this;
            }
        }

        public Builder optional(LootContextParam<?> p_81409_) {
            if (this.required.contains(p_81409_)) {
                throw new IllegalArgumentException("Parameter " + p_81409_.getName() + " is already required");
            } else {
                this.optional.add(p_81409_);
                return this;
            }
        }

        public LootContextParamSet build() {
            return new LootContextParamSet(this.required, this.optional);
        }
    }
}
