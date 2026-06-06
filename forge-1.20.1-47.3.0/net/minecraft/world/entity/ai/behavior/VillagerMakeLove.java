//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.pathfinder.Path;

public class VillagerMakeLove extends Behavior<Villager> {
    private static final int INTERACT_DIST_SQR = 5;
    private static final float SPEED_MODIFIER = 0.5F;
    private long birthTimestamp;

    public VillagerMakeLove() {
        super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT), 350, 350);
    }

    protected boolean checkExtraStartConditions(ServerLevel p_24623_, Villager p_24624_) {
        return this.isBreedingPossible(p_24624_);
    }

    protected boolean canStillUse(ServerLevel p_24626_, Villager p_24627_, long p_24628_) {
        return p_24628_ <= this.birthTimestamp && this.isBreedingPossible(p_24627_);
    }

    protected void start(ServerLevel p_24652_, Villager p_24653_, long p_24654_) {
        AgeableMob $$3 = (AgeableMob)p_24653_.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
        BehaviorUtils.lockGazeAndWalkToEachOther(p_24653_, $$3, 0.5F);
        p_24652_.broadcastEntityEvent($$3, (byte)18);
        p_24652_.broadcastEntityEvent(p_24653_, (byte)18);
        int $$4 = 275 + p_24653_.getRandom().nextInt(50);
        this.birthTimestamp = p_24654_ + (long)$$4;
    }

    protected void tick(ServerLevel p_24667_, Villager p_24668_, long p_24669_) {
        Villager $$3 = (Villager)p_24668_.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
        if (!(p_24668_.distanceToSqr($$3) > 5.0)) {
            BehaviorUtils.lockGazeAndWalkToEachOther(p_24668_, $$3, 0.5F);
            if (p_24669_ >= this.birthTimestamp) {
                p_24668_.eatAndDigestFood();
                $$3.eatAndDigestFood();
                this.tryToGiveBirth(p_24667_, p_24668_, $$3);
            } else if (p_24668_.getRandom().nextInt(35) == 0) {
                p_24667_.broadcastEntityEvent($$3, (byte)12);
                p_24667_.broadcastEntityEvent(p_24668_, (byte)12);
            }

        }
    }

    private void tryToGiveBirth(ServerLevel p_24630_, Villager p_24631_, Villager p_24632_) {
        Optional<BlockPos> $$3 = this.takeVacantBed(p_24630_, p_24631_);
        if (!$$3.isPresent()) {
            p_24630_.broadcastEntityEvent(p_24632_, (byte)13);
            p_24630_.broadcastEntityEvent(p_24631_, (byte)13);
        } else {
            Optional<Villager> $$4 = this.breed(p_24630_, p_24631_, p_24632_);
            if ($$4.isPresent()) {
                this.giveBedToChild(p_24630_, (Villager)$$4.get(), (BlockPos)$$3.get());
            } else {
                p_24630_.getPoiManager().release((BlockPos)$$3.get());
                DebugPackets.sendPoiTicketCountPacket(p_24630_, (BlockPos)$$3.get());
            }
        }

    }

    protected void stop(ServerLevel p_24675_, Villager p_24676_, long p_24677_) {
        p_24676_.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
    }

    private boolean isBreedingPossible(Villager p_24640_) {
        Brain<Villager> $$1 = p_24640_.getBrain();
        Optional<AgeableMob> $$2 = $$1.getMemory(MemoryModuleType.BREED_TARGET).filter((p_289389_) -> {
            return p_289389_.getType() == EntityType.VILLAGER;
        });
        if (!$$2.isPresent()) {
            return false;
        } else {
            return BehaviorUtils.targetIsValid($$1, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && p_24640_.canBreed() && ((AgeableMob)$$2.get()).canBreed();
        }
    }

    private Optional<BlockPos> takeVacantBed(ServerLevel p_24649_, Villager p_24650_) {
        return p_24649_.getPoiManager().take((p_217509_) -> {
            return p_217509_.is(PoiTypes.HOME);
        }, (p_217506_, p_217507_) -> {
            return this.canReach(p_24650_, p_217507_, p_217506_);
        }, p_24650_.blockPosition(), 48);
    }

    private boolean canReach(Villager p_217501_, BlockPos p_217502_, Holder<PoiType> p_217503_) {
        Path $$3 = p_217501_.getNavigation().createPath(p_217502_, ((PoiType)p_217503_.value()).validRange());
        return $$3 != null && $$3.canReach();
    }

    private Optional<Villager> breed(ServerLevel p_24656_, Villager p_24657_, Villager p_24658_) {
        Villager $$3 = p_24657_.getBreedOffspring(p_24656_, p_24658_);
        if ($$3 == null) {
            return Optional.empty();
        } else {
            p_24657_.setAge(6000);
            p_24658_.setAge(6000);
            $$3.setAge(-24000);
            $$3.moveTo(p_24657_.getX(), p_24657_.getY(), p_24657_.getZ(), 0.0F, 0.0F);
            p_24656_.addFreshEntityWithPassengers($$3);
            p_24656_.broadcastEntityEvent($$3, (byte)12);
            return Optional.of($$3);
        }
    }

    private void giveBedToChild(ServerLevel p_24634_, Villager p_24635_, BlockPos p_24636_) {
        GlobalPos $$3 = GlobalPos.of(p_24634_.dimension(), p_24636_);
        p_24635_.getBrain().setMemory(MemoryModuleType.HOME, (Object)$$3);
    }
}
