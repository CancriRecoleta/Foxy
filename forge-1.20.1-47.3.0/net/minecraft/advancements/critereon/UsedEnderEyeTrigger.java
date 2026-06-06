//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.MinMaxBounds.Doubles;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class UsedEnderEyeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    static final ResourceLocation ID = new ResourceLocation("used_ender_eye");

    public UsedEnderEyeTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject p_286292_, ContextAwarePredicate p_286488_, DeserializationContext p_286702_) {
        MinMaxBounds.Doubles $$3 = Doubles.fromJson(p_286292_.get("distance"));
        return new TriggerInstance(p_286488_, $$3);
    }

    public void trigger(ServerPlayer p_73936_, BlockPos p_73937_) {
        double $$2 = p_73936_.getX() - (double)p_73937_.getX();
        double $$3 = p_73936_.getZ() - (double)p_73937_.getZ();
        double $$4 = $$2 * $$2 + $$3 * $$3;
        this.trigger(p_73936_, (p_73934_) -> {
            return p_73934_.matches($$4);
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Doubles level;

        public TriggerInstance(ContextAwarePredicate p_286567_, MinMaxBounds.Doubles p_286810_) {
            super(UsedEnderEyeTrigger.ID, p_286567_);
            this.level = p_286810_;
        }

        public boolean matches(double p_73952_) {
            return this.level.matchesSqr(p_73952_);
        }
    }
}
