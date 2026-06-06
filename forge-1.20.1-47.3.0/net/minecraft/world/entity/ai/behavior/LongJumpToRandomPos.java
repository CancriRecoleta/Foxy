//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class LongJumpToRandomPos<E extends Mob> extends Behavior<E> {
    protected static final int FIND_JUMP_TRIES = 20;
    private static final int PREPARE_JUMP_DURATION = 40;
    protected static final int MIN_PATHFIND_DISTANCE_TO_VALID_JUMP = 8;
    private static final int TIME_OUT_DURATION = 200;
    private static final List<Integer> ALLOWED_ANGLES = Lists.newArrayList(new Integer[]{65, 70, 75, 80});
    private final UniformInt timeBetweenLongJumps;
    protected final int maxLongJumpHeight;
    protected final int maxLongJumpWidth;
    protected final float maxJumpVelocity;
    protected List<PossibleJump> jumpCandidates;
    protected Optional<Vec3> initialPosition;
    @Nullable
    protected Vec3 chosenJump;
    protected int findJumpTries;
    protected long prepareJumpStart;
    private final Function<E, SoundEvent> getJumpSound;
    private final BiPredicate<E, BlockPos> acceptableLandingSpot;

    public LongJumpToRandomPos(UniformInt p_147637_, int p_147638_, int p_147639_, float p_147640_, Function<E, SoundEvent> p_147641_) {
        this(p_147637_, p_147638_, p_147639_, p_147640_, p_147641_, LongJumpToRandomPos::defaultAcceptableLandingSpot);
    }

    public static <E extends Mob> boolean defaultAcceptableLandingSpot(E p_251540_, BlockPos p_248879_) {
        Level $$2 = p_251540_.level();
        BlockPos $$3 = p_248879_.below();
        return $$2.getBlockState($$3).isSolidRender($$2, $$3) && p_251540_.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic($$2, p_248879_.mutable())) == 0.0F;
    }

    public LongJumpToRandomPos(UniformInt p_251244_, int p_248763_, int p_251698_, float p_250165_, Function<E, SoundEvent> p_249738_, BiPredicate<E, BlockPos> p_249945_) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_ABSENT), 200);
        this.jumpCandidates = Lists.newArrayList();
        this.initialPosition = Optional.empty();
        this.timeBetweenLongJumps = p_251244_;
        this.maxLongJumpHeight = p_248763_;
        this.maxLongJumpWidth = p_251698_;
        this.maxJumpVelocity = p_250165_;
        this.getJumpSound = p_249738_;
        this.acceptableLandingSpot = p_249945_;
    }

    protected boolean checkExtraStartConditions(ServerLevel p_147650_, Mob p_147651_) {
        boolean $$2 = p_147651_.onGround() && !p_147651_.isInWater() && !p_147651_.isInLava() && !p_147650_.getBlockState(p_147651_.blockPosition()).is(Blocks.HONEY_BLOCK);
        if (!$$2) {
            p_147651_.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, (Object)(this.timeBetweenLongJumps.sample(p_147650_.random) / 2));
        }

        return $$2;
    }

    protected boolean canStillUse(ServerLevel p_147653_, Mob p_147654_, long p_147655_) {
        boolean $$3 = this.initialPosition.isPresent() && ((Vec3)this.initialPosition.get()).equals(p_147654_.position()) && this.findJumpTries > 0 && !p_147654_.isInWaterOrBubble() && (this.chosenJump != null || !this.jumpCandidates.isEmpty());
        if (!$$3 && p_147654_.getBrain().getMemory(MemoryModuleType.LONG_JUMP_MID_JUMP).isEmpty()) {
            p_147654_.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, (Object)(this.timeBetweenLongJumps.sample(p_147653_.random) / 2));
            p_147654_.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        }

        return $$3;
    }

    protected void start(ServerLevel p_147676_, E p_147677_, long p_147678_) {
        this.chosenJump = null;
        this.findJumpTries = 20;
        this.initialPosition = Optional.of(p_147677_.position());
        BlockPos $$3 = p_147677_.blockPosition();
        int $$4 = $$3.getX();
        int $$5 = $$3.getY();
        int $$6 = $$3.getZ();
        this.jumpCandidates = (List)BlockPos.betweenClosedStream($$4 - this.maxLongJumpWidth, $$5 - this.maxLongJumpHeight, $$6 - this.maxLongJumpWidth, $$4 + this.maxLongJumpWidth, $$5 + this.maxLongJumpHeight, $$6 + this.maxLongJumpWidth).filter((p_217317_) -> {
            return !p_217317_.equals($$3);
        }).map((p_217314_) -> {
            return new PossibleJump(p_217314_.immutable(), Mth.ceil($$3.distSqr(p_217314_)));
        }).collect(Collectors.toCollection(Lists::newArrayList));
    }

    protected void tick(ServerLevel p_147680_, E p_147681_, long p_147682_) {
        if (this.chosenJump != null) {
            if (p_147682_ - this.prepareJumpStart >= 40L) {
                p_147681_.setYRot(p_147681_.yBodyRot);
                p_147681_.setDiscardFriction(true);
                double $$3 = this.chosenJump.length();
                double $$4 = $$3 + (double)p_147681_.getJumpBoostPower();
                p_147681_.setDeltaMovement(this.chosenJump.scale($$4 / $$3));
                p_147681_.getBrain().setMemory(MemoryModuleType.LONG_JUMP_MID_JUMP, (Object)true);
                p_147680_.playSound((Player)null, p_147681_, (SoundEvent)this.getJumpSound.apply(p_147681_), SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        } else {
            --this.findJumpTries;
            this.pickCandidate(p_147680_, p_147681_, p_147682_);
        }

    }

    protected void pickCandidate(ServerLevel p_217319_, E p_217320_, long p_217321_) {
        while(true) {
            if (!this.jumpCandidates.isEmpty()) {
                Optional<PossibleJump> $$3 = this.getJumpCandidate(p_217319_);
                if ($$3.isEmpty()) {
                    continue;
                }

                PossibleJump $$4 = (PossibleJump)$$3.get();
                BlockPos $$5 = $$4.getJumpTarget();
                if (!this.isAcceptableLandingPosition(p_217319_, p_217320_, $$5)) {
                    continue;
                }

                Vec3 $$6 = Vec3.atCenterOf($$5);
                Vec3 $$7 = this.calculateOptimalJumpVector(p_217320_, $$6);
                if ($$7 == null) {
                    continue;
                }

                p_217320_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosTracker($$5)));
                PathNavigation $$8 = p_217320_.getNavigation();
                Path $$9 = $$8.createPath($$5, 0, 8);
                if ($$9 != null && $$9.canReach()) {
                    continue;
                }

                this.chosenJump = $$7;
                this.prepareJumpStart = p_217321_;
                return;
            }

            return;
        }
    }

    protected Optional<PossibleJump> getJumpCandidate(ServerLevel p_217299_) {
        Optional<PossibleJump> $$1 = WeightedRandom.getRandomItem(p_217299_.random, this.jumpCandidates);
        List var10001 = this.jumpCandidates;
        Objects.requireNonNull(var10001);
        $$1.ifPresent(var10001::remove);
        return $$1;
    }

    private boolean isAcceptableLandingPosition(ServerLevel p_217300_, E p_217301_, BlockPos p_217302_) {
        BlockPos $$3 = p_217301_.blockPosition();
        int $$4 = $$3.getX();
        int $$5 = $$3.getZ();
        return $$4 == p_217302_.getX() && $$5 == p_217302_.getZ() ? false : this.acceptableLandingSpot.test(p_217301_, p_217302_);
    }

    @Nullable
    protected Vec3 calculateOptimalJumpVector(Mob p_217304_, Vec3 p_217305_) {
        List<Integer> $$2 = Lists.newArrayList(ALLOWED_ANGLES);
        Collections.shuffle($$2);
        Iterator var4 = $$2.iterator();

        Vec3 $$4;
        do {
            if (!var4.hasNext()) {
                return null;
            }

            int $$3 = (Integer)var4.next();
            $$4 = this.calculateJumpVectorForAngle(p_217304_, p_217305_, $$3);
        } while($$4 == null);

        return $$4;
    }

    @Nullable
    private Vec3 calculateJumpVectorForAngle(Mob p_217307_, Vec3 p_217308_, int p_217309_) {
        Vec3 $$3 = p_217307_.position();
        Vec3 $$4 = (new Vec3(p_217308_.x - $$3.x, 0.0, p_217308_.z - $$3.z)).normalize().scale(0.5);
        p_217308_ = p_217308_.subtract($$4);
        Vec3 $$5 = p_217308_.subtract($$3);
        float $$6 = (float)p_217309_ * 3.1415927F / 180.0F;
        double $$7 = Math.atan2($$5.z, $$5.x);
        double $$8 = $$5.subtract(0.0, $$5.y, 0.0).lengthSqr();
        double $$9 = Math.sqrt($$8);
        double $$10 = $$5.y;
        double $$11 = Math.sin((double)(2.0F * $$6));
        double $$12 = 0.08;
        double $$13 = Math.pow(Math.cos((double)$$6), 2.0);
        double $$14 = Math.sin((double)$$6);
        double $$15 = Math.cos((double)$$6);
        double $$16 = Math.sin($$7);
        double $$17 = Math.cos($$7);
        double $$18 = $$8 * 0.08 / ($$9 * $$11 - 2.0 * $$10 * $$13);
        if ($$18 < 0.0) {
            return null;
        } else {
            double $$19 = Math.sqrt($$18);
            if ($$19 > (double)this.maxJumpVelocity) {
                return null;
            } else {
                double $$20 = $$19 * $$15;
                double $$21 = $$19 * $$14;
                int $$22 = Mth.ceil($$9 / $$20) * 2;
                double $$23 = 0.0;
                Vec3 $$24 = null;
                EntityDimensions $$25 = p_217307_.getDimensions(Pose.LONG_JUMPING);

                for(int $$26 = 0; $$26 < $$22 - 1; ++$$26) {
                    $$23 += $$9 / (double)$$22;
                    double $$27 = $$14 / $$15 * $$23 - Math.pow($$23, 2.0) * 0.08 / (2.0 * $$18 * Math.pow($$15, 2.0));
                    double $$28 = $$23 * $$17;
                    double $$29 = $$23 * $$16;
                    Vec3 $$30 = new Vec3($$3.x + $$28, $$3.y + $$27, $$3.z + $$29);
                    if ($$24 != null && !this.isClearTransition(p_217307_, $$25, $$24, $$30)) {
                        return null;
                    }

                    $$24 = $$30;
                }

                return (new Vec3($$20 * $$17, $$21, $$20 * $$16)).scale(0.949999988079071);
            }
        }
    }

    private boolean isClearTransition(Mob p_249070_, EntityDimensions p_250156_, Vec3 p_251660_, Vec3 p_250101_) {
        Vec3 $$4 = p_250101_.subtract(p_251660_);
        double $$5 = (double)Math.min(p_250156_.width, p_250156_.height);
        int $$6 = Mth.ceil($$4.length() / $$5);
        Vec3 $$7 = $$4.normalize();
        Vec3 $$8 = p_251660_;

        for(int $$9 = 0; $$9 < $$6; ++$$9) {
            $$8 = $$9 == $$6 - 1 ? p_250101_ : $$8.add($$7.scale($$5 * 0.8999999761581421));
            if (!p_249070_.level().noCollision(p_249070_, p_250156_.makeBoundingBox($$8))) {
                return false;
            }
        }

        return true;
    }

    public static class PossibleJump extends WeightedEntry.IntrusiveBase {
        private final BlockPos jumpTarget;

        public PossibleJump(BlockPos p_217323_, int p_217324_) {
            super(p_217324_);
            this.jumpTarget = p_217323_;
        }

        public BlockPos getJumpTarget() {
            return this.jumpTarget;
        }
    }
}
