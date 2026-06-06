//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior.Status;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public abstract class OneShot<E extends LivingEntity> implements BehaviorControl<E>, Trigger<E> {
    private Behavior.Status status;

    public OneShot() {
        this.status = Status.STOPPED;
    }

    public final Behavior.Status getStatus() {
        return this.status;
    }

    public final boolean tryStart(ServerLevel p_260083_, E p_259643_, long p_259226_) {
        if (this.trigger(p_260083_, p_259643_, p_259226_)) {
            this.status = Status.RUNNING;
            return true;
        } else {
            return false;
        }
    }

    public final void tickOrStop(ServerLevel p_259112_, E p_259594_, long p_259046_) {
        this.doStop(p_259112_, p_259594_, p_259046_);
    }

    public final void doStop(ServerLevel p_260215_, E p_259970_, long p_260273_) {
        this.status = Status.STOPPED;
    }

    public String debugString() {
        return this.getClass().getSimpleName();
    }
}
