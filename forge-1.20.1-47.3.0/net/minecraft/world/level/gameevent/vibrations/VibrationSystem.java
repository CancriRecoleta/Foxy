//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public interface VibrationSystem {
    GameEvent[] RESONANCE_EVENTS = new GameEvent[]{GameEvent.RESONATE_1, GameEvent.RESONATE_2, GameEvent.RESONATE_3, GameEvent.RESONATE_4, GameEvent.RESONATE_5, GameEvent.RESONATE_6, GameEvent.RESONATE_7, GameEvent.RESONATE_8, GameEvent.RESONATE_9, GameEvent.RESONATE_10, GameEvent.RESONATE_11, GameEvent.RESONATE_12, GameEvent.RESONATE_13, GameEvent.RESONATE_14, GameEvent.RESONATE_15};
    ToIntFunction<GameEvent> VIBRATION_FREQUENCY_FOR_EVENT = (ToIntFunction)Util.make(new Object2IntOpenHashMap(), (p_282267_) -> {
        p_282267_.defaultReturnValue(0);
        p_282267_.put(GameEvent.STEP, 1);
        p_282267_.put(GameEvent.SWIM, 1);
        p_282267_.put(GameEvent.FLAP, 1);
        p_282267_.put(GameEvent.PROJECTILE_LAND, 2);
        p_282267_.put(GameEvent.HIT_GROUND, 2);
        p_282267_.put(GameEvent.SPLASH, 2);
        p_282267_.put(GameEvent.ITEM_INTERACT_FINISH, 3);
        p_282267_.put(GameEvent.PROJECTILE_SHOOT, 3);
        p_282267_.put(GameEvent.INSTRUMENT_PLAY, 3);
        p_282267_.put(GameEvent.ENTITY_ROAR, 4);
        p_282267_.put(GameEvent.ENTITY_SHAKE, 4);
        p_282267_.put(GameEvent.ELYTRA_GLIDE, 4);
        p_282267_.put(GameEvent.ENTITY_DISMOUNT, 5);
        p_282267_.put(GameEvent.EQUIP, 5);
        p_282267_.put(GameEvent.ENTITY_INTERACT, 6);
        p_282267_.put(GameEvent.SHEAR, 6);
        p_282267_.put(GameEvent.ENTITY_MOUNT, 6);
        p_282267_.put(GameEvent.ENTITY_DAMAGE, 7);
        p_282267_.put(GameEvent.DRINK, 8);
        p_282267_.put(GameEvent.EAT, 8);
        p_282267_.put(GameEvent.CONTAINER_CLOSE, 9);
        p_282267_.put(GameEvent.BLOCK_CLOSE, 9);
        p_282267_.put(GameEvent.BLOCK_DEACTIVATE, 9);
        p_282267_.put(GameEvent.BLOCK_DETACH, 9);
        p_282267_.put(GameEvent.CONTAINER_OPEN, 10);
        p_282267_.put(GameEvent.BLOCK_OPEN, 10);
        p_282267_.put(GameEvent.BLOCK_ACTIVATE, 10);
        p_282267_.put(GameEvent.BLOCK_ATTACH, 10);
        p_282267_.put(GameEvent.PRIME_FUSE, 10);
        p_282267_.put(GameEvent.NOTE_BLOCK_PLAY, 10);
        p_282267_.put(GameEvent.BLOCK_CHANGE, 11);
        p_282267_.put(GameEvent.BLOCK_DESTROY, 12);
        p_282267_.put(GameEvent.FLUID_PICKUP, 12);
        p_282267_.put(GameEvent.BLOCK_PLACE, 13);
        p_282267_.put(GameEvent.FLUID_PLACE, 13);
        p_282267_.put(GameEvent.ENTITY_PLACE, 14);
        p_282267_.put(GameEvent.LIGHTNING_STRIKE, 14);
        p_282267_.put(GameEvent.TELEPORT, 14);
        p_282267_.put(GameEvent.ENTITY_DIE, 15);
        p_282267_.put(GameEvent.EXPLODE, 15);

        for(int $$1 = 1; $$1 <= 15; ++$$1) {
            p_282267_.put(getResonanceEventByFrequency($$1), $$1);
        }

    });

    Data getVibrationData();

    User getVibrationUser();

    static int getGameEventFrequency(GameEvent p_281355_) {
        return VIBRATION_FREQUENCY_FOR_EVENT.applyAsInt(p_281355_);
    }

    static GameEvent getResonanceEventByFrequency(int p_282105_) {
        return RESONANCE_EVENTS[p_282105_ - 1];
    }

    static int getRedstoneStrengthForDistance(float p_282483_, int p_282722_) {
        double $$2 = 15.0 / (double)p_282722_;
        return Math.max(1, 15 - Mth.floor($$2 * (double)p_282483_));
    }

    public interface User {
        int getListenerRadius();

        PositionSource getPositionSource();

        boolean canReceiveVibration(ServerLevel var1, BlockPos var2, GameEvent var3, GameEvent.Context var4);

        void onReceiveVibration(ServerLevel var1, BlockPos var2, GameEvent var3, @Nullable Entity var4, @Nullable Entity var5, float var6);

        default TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.VIBRATIONS;
        }

        default boolean canTriggerAvoidVibration() {
            return false;
        }

        default boolean requiresAdjacentChunksToBeTicking() {
            return false;
        }

        default int calculateTravelTimeInTicks(float p_281658_) {
            return Mth.floor(p_281658_);
        }

        default boolean isValidVibration(GameEvent p_282750_, GameEvent.Context p_283373_) {
            if (!p_282750_.is(this.getListenableEvents())) {
                return false;
            } else {
                Entity $$2 = p_283373_.sourceEntity();
                if ($$2 != null) {
                    if ($$2.isSpectator()) {
                        return false;
                    }

                    if ($$2.isSteppingCarefully() && p_282750_.is(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                        if (this.canTriggerAvoidVibration() && $$2 instanceof ServerPlayer) {
                            ServerPlayer $$3 = (ServerPlayer)$$2;
                            CriteriaTriggers.AVOID_VIBRATION.trigger($$3);
                        }

                        return false;
                    }

                    if ($$2.dampensVibrations()) {
                        return false;
                    }
                }

                if (p_283373_.affectedState() != null) {
                    return !p_283373_.affectedState().is(BlockTags.DAMPENS_VIBRATIONS);
                } else {
                    return true;
                }
            }
        }

        default void onDataChanged() {
        }
    }

    public interface Ticker {
        static void tick(Level p_281704_, Data p_282633_, User p_281564_) {
            if (p_281704_ instanceof ServerLevel $$4) {
                if (p_282633_.currentVibration == null) {
                    trySelectAndScheduleVibration($$4, p_282633_, p_281564_);
                }

                if (p_282633_.currentVibration != null) {
                    boolean $$5 = p_282633_.getTravelTimeInTicks() > 0;
                    tryReloadVibrationParticle($$4, p_282633_, p_281564_);
                    p_282633_.decrementTravelTime();
                    if (p_282633_.getTravelTimeInTicks() <= 0) {
                        $$5 = receiveVibration($$4, p_282633_, p_281564_, p_282633_.currentVibration);
                    }

                    if ($$5) {
                        p_281564_.onDataChanged();
                    }

                }
            }
        }

        private static void trySelectAndScheduleVibration(ServerLevel p_282775_, Data p_282792_, User p_281845_) {
            p_282792_.getSelectionStrategy().chosenCandidate(p_282775_.getGameTime()).ifPresent((p_282059_) -> {
                p_282792_.setCurrentVibration(p_282059_);
                Vec3 $$4 = p_282059_.pos();
                p_282792_.setTravelTimeInTicks(p_281845_.calculateTravelTimeInTicks(p_282059_.distance()));
                p_282775_.sendParticles(new VibrationParticleOption(p_281845_.getPositionSource(), p_282792_.getTravelTimeInTicks()), $$4.x, $$4.y, $$4.z, 1, 0.0, 0.0, 0.0, 0.0);
                p_281845_.onDataChanged();
                p_282792_.getSelectionStrategy().startOver();
            });
        }

        private static void tryReloadVibrationParticle(ServerLevel p_282010_, Data p_282354_, User p_282958_) {
            if (p_282354_.shouldReloadVibrationParticle()) {
                if (p_282354_.currentVibration == null) {
                    p_282354_.setReloadVibrationParticle(false);
                } else {
                    Vec3 $$3 = p_282354_.currentVibration.pos();
                    PositionSource $$4 = p_282958_.getPositionSource();
                    Vec3 $$5 = (Vec3)$$4.getPosition(p_282010_).orElse($$3);
                    int $$6 = p_282354_.getTravelTimeInTicks();
                    int $$7 = p_282958_.calculateTravelTimeInTicks(p_282354_.currentVibration.distance());
                    double $$8 = 1.0 - (double)$$6 / (double)$$7;
                    double $$9 = Mth.lerp($$8, $$3.x, $$5.x);
                    double $$10 = Mth.lerp($$8, $$3.y, $$5.y);
                    double $$11 = Mth.lerp($$8, $$3.z, $$5.z);
                    boolean $$12 = p_282010_.sendParticles(new VibrationParticleOption($$4, $$6), $$9, $$10, $$11, 1, 0.0, 0.0, 0.0, 0.0) > 0;
                    if ($$12) {
                        p_282354_.setReloadVibrationParticle(false);
                    }

                }
            }
        }

        private static boolean receiveVibration(ServerLevel p_282967_, Data p_283447_, User p_282301_, VibrationInfo p_281498_) {
            BlockPos $$4 = BlockPos.containing(p_281498_.pos());
            BlockPos $$5 = (BlockPos)p_282301_.getPositionSource().getPosition(p_282967_).map(BlockPos::containing).orElse($$4);
            if (p_282301_.requiresAdjacentChunksToBeTicking() && !areAdjacentChunksTicking(p_282967_, $$5)) {
                return false;
            } else {
                p_282301_.onReceiveVibration(p_282967_, $$4, p_281498_.gameEvent(), (Entity)p_281498_.getEntity(p_282967_).orElse((Object)null), (Entity)p_281498_.getProjectileOwner(p_282967_).orElse((Object)null), net.minecraft.world.level.gameevent.vibrations.VibrationSystem.Listener.distanceBetweenInBlocks($$4, $$5));
                p_283447_.setCurrentVibration((VibrationInfo)null);
                return true;
            }
        }

        private static boolean areAdjacentChunksTicking(Level p_282735_, BlockPos p_281722_) {
            ChunkPos $$2 = new ChunkPos(p_281722_);

            for(int $$3 = $$2.x - 1; $$3 < $$2.x + 1; ++$$3) {
                for(int $$4 = $$2.z - 1; $$4 < $$2.z + 1; ++$$4) {
                    ChunkAccess $$5 = p_282735_.getChunkSource().getChunkNow($$3, $$4);
                    if ($$5 == null || !p_282735_.shouldTickBlocksAt($$5.getPos().toLong())) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public static class Listener implements GameEventListener {
        private final VibrationSystem system;

        public Listener(VibrationSystem p_281843_) {
            this.system = p_281843_;
        }

        public PositionSource getListenerSource() {
            return this.system.getVibrationUser().getPositionSource();
        }

        public int getListenerRadius() {
            return this.system.getVibrationUser().getListenerRadius();
        }

        public boolean handleGameEvent(ServerLevel p_282254_, GameEvent p_283599_, GameEvent.Context p_283664_, Vec3 p_282426_) {
            Data $$4 = this.system.getVibrationData();
            User $$5 = this.system.getVibrationUser();
            if ($$4.getCurrentVibration() != null) {
                return false;
            } else if (!$$5.isValidVibration(p_283599_, p_283664_)) {
                return false;
            } else {
                Optional<Vec3> $$6 = $$5.getPositionSource().getPosition(p_282254_);
                if ($$6.isEmpty()) {
                    return false;
                } else {
                    Vec3 $$7 = (Vec3)$$6.get();
                    if (!$$5.canReceiveVibration(p_282254_, BlockPos.containing(p_282426_), p_283599_, p_283664_)) {
                        return false;
                    } else if (isOccluded(p_282254_, p_282426_, $$7)) {
                        return false;
                    } else {
                        this.scheduleVibration(p_282254_, $$4, p_283599_, p_283664_, p_282426_, $$7);
                        return true;
                    }
                }
            }
        }

        public void forceScheduleVibration(ServerLevel p_282808_, GameEvent p_281875_, GameEvent.Context p_281652_, Vec3 p_281530_) {
            this.system.getVibrationUser().getPositionSource().getPosition(p_282808_).ifPresent((p_281936_) -> {
                this.scheduleVibration(p_282808_, this.system.getVibrationData(), p_281875_, p_281652_, p_281530_, p_281936_);
            });
        }

        private void scheduleVibration(ServerLevel p_282037_, Data p_283229_, GameEvent p_281778_, GameEvent.Context p_283344_, Vec3 p_281758_, Vec3 p_282990_) {
            p_283229_.selectionStrategy.addCandidate(new VibrationInfo(p_281778_, (float)p_281758_.distanceTo(p_282990_), p_281758_, p_283344_.sourceEntity()), p_282037_.getGameTime());
        }

        public static float distanceBetweenInBlocks(BlockPos p_282413_, BlockPos p_281960_) {
            return (float)Math.sqrt(p_282413_.distSqr(p_281960_));
        }

        private static boolean isOccluded(Level p_283225_, Vec3 p_283328_, Vec3 p_283163_) {
            Vec3 $$3 = new Vec3((double)Mth.floor(p_283328_.x) + 0.5, (double)Mth.floor(p_283328_.y) + 0.5, (double)Mth.floor(p_283328_.z) + 0.5);
            Vec3 $$4 = new Vec3((double)Mth.floor(p_283163_.x) + 0.5, (double)Mth.floor(p_283163_.y) + 0.5, (double)Mth.floor(p_283163_.z) + 0.5);
            Direction[] var5 = Direction.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Direction $$5 = var5[var7];
                Vec3 $$6 = $$3.relative($$5, 9.999999747378752E-6);
                if (p_283225_.isBlockInLine(new ClipBlockStateContext($$6, $$4, (p_283608_) -> {
                    return p_283608_.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS);
                })).getType() != Type.BLOCK) {
                    return false;
                }
            }

            return true;
        }
    }

    public static final class Data {
        public static Codec<Data> CODEC = RecordCodecBuilder.create((p_283387_) -> {
            return p_283387_.group(VibrationInfo.CODEC.optionalFieldOf("event").forGetter((p_281665_) -> {
                return Optional.ofNullable(p_281665_.currentVibration);
            }), VibrationSelector.CODEC.fieldOf("selector").forGetter(Data::getSelectionStrategy), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter(Data::getTravelTimeInTicks)).apply(p_283387_, (p_281934_, p_282381_, p_282931_) -> {
                return new Data((VibrationInfo)p_281934_.orElse((Object)null), p_282381_, p_282931_, true);
            });
        });
        public static final String NBT_TAG_KEY = "listener";
        @Nullable
        VibrationInfo currentVibration;
        private int travelTimeInTicks;
        final VibrationSelector selectionStrategy;
        private boolean reloadVibrationParticle;

        private Data(@Nullable VibrationInfo p_281967_, VibrationSelector p_283036_, int p_283607_, boolean p_282438_) {
            this.currentVibration = p_281967_;
            this.travelTimeInTicks = p_283607_;
            this.selectionStrategy = p_283036_;
            this.reloadVibrationParticle = p_282438_;
        }

        public Data() {
            this((VibrationInfo)null, new VibrationSelector(), 0, false);
        }

        public VibrationSelector getSelectionStrategy() {
            return this.selectionStrategy;
        }

        @Nullable
        public VibrationInfo getCurrentVibration() {
            return this.currentVibration;
        }

        public void setCurrentVibration(@Nullable VibrationInfo p_282049_) {
            this.currentVibration = p_282049_;
        }

        public int getTravelTimeInTicks() {
            return this.travelTimeInTicks;
        }

        public void setTravelTimeInTicks(int p_282973_) {
            this.travelTimeInTicks = p_282973_;
        }

        public void decrementTravelTime() {
            this.travelTimeInTicks = Math.max(0, this.travelTimeInTicks - 1);
        }

        public boolean shouldReloadVibrationParticle() {
            return this.reloadVibrationParticle;
        }

        public void setReloadVibrationParticle(boolean p_281702_) {
            this.reloadVibrationParticle = p_281702_;
        }
    }
}
