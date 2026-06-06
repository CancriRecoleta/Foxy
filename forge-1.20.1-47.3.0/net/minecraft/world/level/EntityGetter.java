//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface EntityGetter {
    List<Entity> getEntities(@Nullable Entity var1, AABB var2, Predicate<? super Entity> var3);

    <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> var1, AABB var2, Predicate<? super T> var3);

    default <T extends Entity> List<T> getEntitiesOfClass(Class<T> p_45979_, AABB p_45980_, Predicate<? super T> p_45981_) {
        return this.getEntities(EntityTypeTest.forClass(p_45979_), p_45980_, p_45981_);
    }

    List<? extends Player> players();

    default List<Entity> getEntities(@Nullable Entity p_45934_, AABB p_45935_) {
        return this.getEntities(p_45934_, p_45935_, EntitySelector.NO_SPECTATORS);
    }

    default boolean isUnobstructed(@Nullable Entity p_45939_, VoxelShape p_45940_) {
        if (p_45940_.isEmpty()) {
            return true;
        } else {
            Iterator var3 = this.getEntities(p_45939_, p_45940_.bounds()).iterator();

            Entity $$2;
            do {
                do {
                    do {
                        do {
                            if (!var3.hasNext()) {
                                return true;
                            }

                            $$2 = (Entity)var3.next();
                        } while($$2.isRemoved());
                    } while(!$$2.blocksBuilding);
                } while(p_45939_ != null && $$2.isPassengerOfSameVehicle(p_45939_));
            } while(!Shapes.joinIsNotEmpty(p_45940_, Shapes.create($$2.getBoundingBox()), BooleanOp.AND));

            return false;
        }
    }

    default <T extends Entity> List<T> getEntitiesOfClass(Class<T> p_45977_, AABB p_45978_) {
        return this.getEntitiesOfClass(p_45977_, p_45978_, EntitySelector.NO_SPECTATORS);
    }

    default List<VoxelShape> getEntityCollisions(@Nullable Entity p_186451_, AABB p_186452_) {
        if (p_186452_.getSize() < 1.0E-7) {
            return List.of();
        } else {
            Predicate var10000;
            if (p_186451_ == null) {
                var10000 = EntitySelector.CAN_BE_COLLIDED_WITH;
            } else {
                var10000 = EntitySelector.NO_SPECTATORS;
                Objects.requireNonNull(p_186451_);
                var10000 = var10000.and(p_186451_::canCollideWith);
            }

            Predicate<Entity> $$2 = var10000;
            List<Entity> $$3 = this.getEntities(p_186451_, p_186452_.inflate(1.0E-7), $$2);
            if ($$3.isEmpty()) {
                return List.of();
            } else {
                ImmutableList.Builder<VoxelShape> $$4 = ImmutableList.builderWithExpectedSize($$3.size());
                Iterator var6 = $$3.iterator();

                while(var6.hasNext()) {
                    Entity $$5 = (Entity)var6.next();
                    $$4.add(Shapes.create($$5.getBoundingBox()));
                }

                return $$4.build();
            }
        }
    }

    @Nullable
    default Player getNearestPlayer(double p_45919_, double p_45920_, double p_45921_, double p_45922_, @Nullable Predicate<Entity> p_45923_) {
        double $$5 = -1.0;
        Player $$6 = null;
        Iterator var13 = this.players().iterator();

        while(true) {
            Player $$7;
            double $$8;
            do {
                do {
                    do {
                        if (!var13.hasNext()) {
                            return $$6;
                        }

                        $$7 = (Player)var13.next();
                    } while(p_45923_ != null && !p_45923_.test($$7));

                    $$8 = $$7.distanceToSqr(p_45919_, p_45920_, p_45921_);
                } while(!(p_45922_ < 0.0) && !($$8 < p_45922_ * p_45922_));
            } while($$5 != -1.0 && !($$8 < $$5));

            $$5 = $$8;
            $$6 = $$7;
        }
    }

    @Nullable
    default Player getNearestPlayer(Entity p_45931_, double p_45932_) {
        return this.getNearestPlayer(p_45931_.getX(), p_45931_.getY(), p_45931_.getZ(), p_45932_, false);
    }

    @Nullable
    default Player getNearestPlayer(double p_45925_, double p_45926_, double p_45927_, double p_45928_, boolean p_45929_) {
        Predicate<Entity> $$5 = p_45929_ ? EntitySelector.NO_CREATIVE_OR_SPECTATOR : EntitySelector.NO_SPECTATORS;
        return this.getNearestPlayer(p_45925_, p_45926_, p_45927_, p_45928_, $$5);
    }

    default boolean hasNearbyAlivePlayer(double p_45915_, double p_45916_, double p_45917_, double p_45918_) {
        Iterator var9 = this.players().iterator();

        double $$5;
        do {
            Player $$4;
            do {
                do {
                    if (!var9.hasNext()) {
                        return false;
                    }

                    $$4 = (Player)var9.next();
                } while(!EntitySelector.NO_SPECTATORS.test($$4));
            } while(!EntitySelector.LIVING_ENTITY_STILL_ALIVE.test($$4));

            $$5 = $$4.distanceToSqr(p_45915_, p_45916_, p_45917_);
        } while(!(p_45918_ < 0.0) && !($$5 < p_45918_ * p_45918_));

        return true;
    }

    @Nullable
    default Player getNearestPlayer(TargetingConditions p_45947_, LivingEntity p_45948_) {
        return (Player)this.getNearestEntity(this.players(), p_45947_, p_45948_, p_45948_.getX(), p_45948_.getY(), p_45948_.getZ());
    }

    @Nullable
    default Player getNearestPlayer(TargetingConditions p_45950_, LivingEntity p_45951_, double p_45952_, double p_45953_, double p_45954_) {
        return (Player)this.getNearestEntity(this.players(), p_45950_, p_45951_, p_45952_, p_45953_, p_45954_);
    }

    @Nullable
    default Player getNearestPlayer(TargetingConditions p_45942_, double p_45943_, double p_45944_, double p_45945_) {
        return (Player)this.getNearestEntity(this.players(), p_45942_, (LivingEntity)null, p_45943_, p_45944_, p_45945_);
    }

    @Nullable
    default <T extends LivingEntity> T getNearestEntity(Class<? extends T> p_45964_, TargetingConditions p_45965_, @Nullable LivingEntity p_45966_, double p_45967_, double p_45968_, double p_45969_, AABB p_45970_) {
        return this.getNearestEntity(this.getEntitiesOfClass(p_45964_, p_45970_, (p_186454_) -> {
            return true;
        }), p_45965_, p_45966_, p_45967_, p_45968_, p_45969_);
    }

    @Nullable
    default <T extends LivingEntity> T getNearestEntity(List<? extends T> p_45983_, TargetingConditions p_45984_, @Nullable LivingEntity p_45985_, double p_45986_, double p_45987_, double p_45988_) {
        double $$6 = -1.0;
        T $$7 = null;
        Iterator var13 = p_45983_.iterator();

        while(true) {
            LivingEntity $$8;
            double $$9;
            do {
                do {
                    if (!var13.hasNext()) {
                        return $$7;
                    }

                    $$8 = (LivingEntity)var13.next();
                } while(!p_45984_.test(p_45985_, $$8));

                $$9 = $$8.distanceToSqr(p_45986_, p_45987_, p_45988_);
            } while($$6 != -1.0 && !($$9 < $$6));

            $$6 = $$9;
            $$7 = $$8;
        }
    }

    default List<Player> getNearbyPlayers(TargetingConditions p_45956_, LivingEntity p_45957_, AABB p_45958_) {
        List<Player> $$3 = Lists.newArrayList();
        Iterator var5 = this.players().iterator();

        while(var5.hasNext()) {
            Player $$4 = (Player)var5.next();
            if (p_45958_.contains($$4.getX(), $$4.getY(), $$4.getZ()) && p_45956_.test(p_45957_, $$4)) {
                $$3.add($$4);
            }
        }

        return $$3;
    }

    default <T extends LivingEntity> List<T> getNearbyEntities(Class<T> p_45972_, TargetingConditions p_45973_, LivingEntity p_45974_, AABB p_45975_) {
        List<T> $$4 = this.getEntitiesOfClass(p_45972_, p_45975_, (p_186450_) -> {
            return true;
        });
        List<T> $$5 = Lists.newArrayList();
        Iterator var7 = $$4.iterator();

        while(var7.hasNext()) {
            T $$6 = (LivingEntity)var7.next();
            if (p_45973_.test(p_45974_, $$6)) {
                $$5.add($$6);
            }
        }

        return $$5;
    }

    @Nullable
    default Player getPlayerByUUID(UUID p_46004_) {
        for(int $$1 = 0; $$1 < this.players().size(); ++$$1) {
            Player $$2 = (Player)this.players().get($$1);
            if (p_46004_.equals($$2.getUUID())) {
                return $$2;
            }
        }

        return null;
    }
}
