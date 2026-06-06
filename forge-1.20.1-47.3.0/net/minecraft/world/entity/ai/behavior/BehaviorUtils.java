//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class BehaviorUtils {
    private BehaviorUtils() {
    }

    public static void lockGazeAndWalkToEachOther(LivingEntity p_22603_, LivingEntity p_22604_, float p_22605_) {
        lookAtEachOther(p_22603_, p_22604_);
        setWalkAndLookTargetMemoriesToEachOther(p_22603_, p_22604_, p_22605_);
    }

    public static boolean entityIsVisible(Brain<?> p_22637_, LivingEntity p_22638_) {
        Optional<NearestVisibleLivingEntities> $$2 = p_22637_.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
        return $$2.isPresent() && ((NearestVisibleLivingEntities)$$2.get()).contains(p_22638_);
    }

    public static boolean targetIsValid(Brain<?> p_22640_, MemoryModuleType<? extends LivingEntity> p_22641_, EntityType<?> p_22642_) {
        return targetIsValid(p_22640_, p_22641_, (p_289317_) -> {
            return p_289317_.getType() == p_22642_;
        });
    }

    private static boolean targetIsValid(Brain<?> p_22644_, MemoryModuleType<? extends LivingEntity> p_22645_, Predicate<LivingEntity> p_22646_) {
        return p_22644_.getMemory(p_22645_).filter(p_22646_).filter(LivingEntity::isAlive).filter((p_186037_) -> {
            return entityIsVisible(p_22644_, p_186037_);
        }).isPresent();
    }

    private static void lookAtEachOther(LivingEntity p_22671_, LivingEntity p_22672_) {
        lookAtEntity(p_22671_, p_22672_);
        lookAtEntity(p_22672_, p_22671_);
    }

    public static void lookAtEntity(LivingEntity p_22596_, LivingEntity p_22597_) {
        p_22596_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityTracker(p_22597_, true)));
    }

    private static void setWalkAndLookTargetMemoriesToEachOther(LivingEntity p_22661_, LivingEntity p_22662_, float p_22663_) {
        int $$3 = true;
        setWalkAndLookTargetMemories(p_22661_, (Entity)p_22662_, p_22663_, 2);
        setWalkAndLookTargetMemories(p_22662_, (Entity)p_22661_, p_22663_, 2);
    }

    public static void setWalkAndLookTargetMemories(LivingEntity p_22591_, Entity p_22592_, float p_22593_, int p_22594_) {
        setWalkAndLookTargetMemories(p_22591_, (PositionTracker)(new EntityTracker(p_22592_, true)), p_22593_, p_22594_);
    }

    public static void setWalkAndLookTargetMemories(LivingEntity p_22618_, BlockPos p_22619_, float p_22620_, int p_22621_) {
        setWalkAndLookTargetMemories(p_22618_, (PositionTracker)(new BlockPosTracker(p_22619_)), p_22620_, p_22621_);
    }

    public static void setWalkAndLookTargetMemories(LivingEntity p_217129_, PositionTracker p_217130_, float p_217131_, int p_217132_) {
        WalkTarget $$4 = new WalkTarget(p_217130_, p_217131_, p_217132_);
        p_217129_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)p_217130_);
        p_217129_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)$$4);
    }

    public static void throwItem(LivingEntity p_22614_, ItemStack p_22615_, Vec3 p_22616_) {
        Vec3 $$3 = new Vec3(0.30000001192092896, 0.30000001192092896, 0.30000001192092896);
        throwItem(p_22614_, p_22615_, p_22616_, $$3, 0.3F);
    }

    public static void throwItem(LivingEntity p_217134_, ItemStack p_217135_, Vec3 p_217136_, Vec3 p_217137_, float p_217138_) {
        double $$5 = p_217134_.getEyeY() - (double)p_217138_;
        ItemEntity $$6 = new ItemEntity(p_217134_.level(), p_217134_.getX(), $$5, p_217134_.getZ(), p_217135_);
        $$6.setThrower(p_217134_.getUUID());
        Vec3 $$7 = p_217136_.subtract(p_217134_.position());
        $$7 = $$7.normalize().multiply(p_217137_.x, p_217137_.y, p_217137_.z);
        $$6.setDeltaMovement($$7);
        $$6.setDefaultPickUpDelay();
        p_217134_.level().addFreshEntity($$6);
    }

    public static SectionPos findSectionClosestToVillage(ServerLevel p_22582_, SectionPos p_22583_, int p_22584_) {
        int $$3 = p_22582_.sectionsToVillage(p_22583_);
        Stream var10000 = SectionPos.cube(p_22583_, p_22584_).filter((p_186017_) -> {
            return p_22582_.sectionsToVillage(p_186017_) < $$3;
        });
        Objects.requireNonNull(p_22582_);
        return (SectionPos)var10000.min(Comparator.comparingInt(p_22582_::sectionsToVillage)).orElse(p_22583_);
    }

    public static boolean isWithinAttackRange(Mob p_22633_, LivingEntity p_22634_, int p_22635_) {
        Item var4 = p_22633_.getMainHandItem().getItem();
        if (var4 instanceof ProjectileWeaponItem $$3) {
            if (p_22633_.canFireProjectileWeapon($$3)) {
                int $$4 = $$3.getDefaultProjectileRange() - p_22635_;
                return p_22633_.closerThan(p_22634_, (double)$$4);
            }
        }

        return p_22633_.isWithinMeleeAttackRange(p_22634_);
    }

    public static boolean isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(LivingEntity p_22599_, LivingEntity p_22600_, double p_22601_) {
        Optional<LivingEntity> $$3 = p_22599_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if ($$3.isEmpty()) {
            return false;
        } else {
            double $$4 = p_22599_.distanceToSqr(((LivingEntity)$$3.get()).position());
            double $$5 = p_22599_.distanceToSqr(p_22600_.position());
            return $$5 > $$4 + p_22601_ * p_22601_;
        }
    }

    public static boolean canSee(LivingEntity p_22668_, LivingEntity p_22669_) {
        Brain<?> $$2 = p_22668_.getBrain();
        return !$$2.hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES) ? false : ((NearestVisibleLivingEntities)$$2.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).contains(p_22669_);
    }

    public static LivingEntity getNearestTarget(LivingEntity p_22626_, Optional<LivingEntity> p_22627_, LivingEntity p_22628_) {
        return p_22627_.isEmpty() ? p_22628_ : getTargetNearestMe(p_22626_, (LivingEntity)p_22627_.get(), p_22628_);
    }

    public static LivingEntity getTargetNearestMe(LivingEntity p_22607_, LivingEntity p_22608_, LivingEntity p_22609_) {
        Vec3 $$3 = p_22608_.position();
        Vec3 $$4 = p_22609_.position();
        return p_22607_.distanceToSqr($$3) < p_22607_.distanceToSqr($$4) ? p_22608_ : p_22609_;
    }

    public static Optional<LivingEntity> getLivingEntityFromUUIDMemory(LivingEntity p_22611_, MemoryModuleType<UUID> p_22612_) {
        Optional<UUID> $$2 = p_22611_.getBrain().getMemory(p_22612_);
        return $$2.map((p_289315_) -> {
            return ((ServerLevel)p_22611_.level()).getEntity(p_289315_);
        }).map((p_186019_) -> {
            LivingEntity var10000;
            if (p_186019_ instanceof LivingEntity $$1) {
                var10000 = $$1;
            } else {
                var10000 = null;
            }

            return var10000;
        });
    }

    @Nullable
    public static Vec3 getRandomSwimmablePos(PathfinderMob p_147445_, int p_147446_, int p_147447_) {
        Vec3 $$3 = DefaultRandomPos.getPos(p_147445_, p_147446_, p_147447_);

        for(int $$4 = 0; $$3 != null && !p_147445_.level().getBlockState(BlockPos.containing($$3)).isPathfindable(p_147445_.level(), BlockPos.containing($$3), PathComputationType.WATER) && $$4++ < 10; $$3 = DefaultRandomPos.getPos(p_147445_, p_147446_, p_147447_)) {
        }

        return $$3;
    }

    public static boolean isBreeding(LivingEntity p_217127_) {
        return p_217127_.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET);
    }
}
