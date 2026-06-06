//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.EntityPredicate.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PlayerTrigger extends SimpleCriterionTrigger<TriggerInstance> {
    final ResourceLocation id;

    public PlayerTrigger(ResourceLocation p_222616_) {
        this.id = p_222616_;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public TriggerInstance createInstance(JsonObject p_286310_, ContextAwarePredicate p_286629_, DeserializationContext p_286901_) {
        return new TriggerInstance(this.id, p_286629_);
    }

    public void trigger(ServerPlayer p_222619_) {
        this.trigger(p_222619_, (p_222625_) -> {
            return true;
        });
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        public TriggerInstance(ResourceLocation p_286413_, ContextAwarePredicate p_286749_) {
            super(p_286413_, p_286749_);
        }

        public static TriggerInstance located(LocationPredicate p_222636_) {
            return new TriggerInstance(CriteriaTriggers.LOCATION.id, EntityPredicate.wrap(Builder.entity().located(p_222636_).build()));
        }

        public static TriggerInstance located(EntityPredicate p_222634_) {
            return new TriggerInstance(CriteriaTriggers.LOCATION.id, EntityPredicate.wrap(p_222634_));
        }

        public static TriggerInstance sleptInBed() {
            return new TriggerInstance(CriteriaTriggers.SLEPT_IN_BED.id, ContextAwarePredicate.ANY);
        }

        public static TriggerInstance raidWon() {
            return new TriggerInstance(CriteriaTriggers.RAID_WIN.id, ContextAwarePredicate.ANY);
        }

        public static TriggerInstance avoidVibration() {
            return new TriggerInstance(CriteriaTriggers.AVOID_VIBRATION.id, ContextAwarePredicate.ANY);
        }

        public static TriggerInstance tick() {
            return new TriggerInstance(CriteriaTriggers.TICK.id, ContextAwarePredicate.ANY);
        }

        public static TriggerInstance walkOnBlockWithEquipment(Block p_222638_, Item p_222639_) {
            return located(Builder.entity().equipment(net.minecraft.advancements.critereon.EntityEquipmentPredicate.Builder.equipment().feet(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(p_222639_).build()).build()).steppingOn(net.minecraft.advancements.critereon.LocationPredicate.Builder.location().setBlock(net.minecraft.advancements.critereon.BlockPredicate.Builder.block().of(p_222638_).build()).build()).build());
        }
    }
}
