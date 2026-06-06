//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;

public interface CriterionTrigger<T extends CriterionTriggerInstance> {
    ResourceLocation getId();

    void addPlayerListener(PlayerAdvancements var1, Listener<T> var2);

    void removePlayerListener(PlayerAdvancements var1, Listener<T> var2);

    void removePlayerListeners(PlayerAdvancements var1);

    T createInstance(JsonObject var1, DeserializationContext var2);

    public static class Listener<T extends CriterionTriggerInstance> {
        private final T trigger;
        private final Advancement advancement;
        private final String criterion;

        public Listener(T p_13682_, Advancement p_13683_, String p_13684_) {
            this.trigger = p_13682_;
            this.advancement = p_13683_;
            this.criterion = p_13684_;
        }

        public T getTriggerInstance() {
            return this.trigger;
        }

        public void run(PlayerAdvancements p_13687_) {
            p_13687_.award(this.advancement, this.criterion);
        }

        public boolean equals(Object p_13689_) {
            if (this == p_13689_) {
                return true;
            } else if (p_13689_ != null && this.getClass() == p_13689_.getClass()) {
                Listener<?> $$1 = (Listener)p_13689_;
                if (!this.trigger.equals($$1.trigger)) {
                    return false;
                } else {
                    return !this.advancement.equals($$1.advancement) ? false : this.criterion.equals($$1.criterion);
                }
            } else {
                return false;
            }
        }

        public int hashCode() {
            int $$0 = this.trigger.hashCode();
            $$0 = 31 * $$0 + this.advancement.hashCode();
            $$0 = 31 * $$0 + this.criterion.hashCode();
            return $$0;
        }
    }
}
