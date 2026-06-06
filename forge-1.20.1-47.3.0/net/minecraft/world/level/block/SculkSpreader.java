//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class SculkSpreader {
    public static final int MAX_GROWTH_RATE_RADIUS = 24;
    public static final int MAX_CHARGE = 1000;
    public static final float MAX_DECAY_FACTOR = 0.5F;
    private static final int MAX_CURSORS = 32;
    public static final int SHRIEKER_PLACEMENT_RATE = 11;
    final boolean isWorldGeneration;
    private final TagKey<Block> replaceableBlocks;
    private final int growthSpawnCost;
    private final int noGrowthRadius;
    private final int chargeDecayRate;
    private final int additionalDecayRate;
    private List<ChargeCursor> cursors = new ArrayList();
    private static final Logger LOGGER = LogUtils.getLogger();

    public SculkSpreader(boolean p_222248_, TagKey<Block> p_222249_, int p_222250_, int p_222251_, int p_222252_, int p_222253_) {
        this.isWorldGeneration = p_222248_;
        this.replaceableBlocks = p_222249_;
        this.growthSpawnCost = p_222250_;
        this.noGrowthRadius = p_222251_;
        this.chargeDecayRate = p_222252_;
        this.additionalDecayRate = p_222253_;
    }

    public static SculkSpreader createLevelSpreader() {
        return new SculkSpreader(false, BlockTags.SCULK_REPLACEABLE, 10, 4, 10, 5);
    }

    public static SculkSpreader createWorldGenSpreader() {
        return new SculkSpreader(true, BlockTags.SCULK_REPLACEABLE_WORLD_GEN, 50, 1, 5, 10);
    }

    public TagKey<Block> replaceableBlocks() {
        return this.replaceableBlocks;
    }

    public int growthSpawnCost() {
        return this.growthSpawnCost;
    }

    public int noGrowthRadius() {
        return this.noGrowthRadius;
    }

    public int chargeDecayRate() {
        return this.chargeDecayRate;
    }

    public int additionalDecayRate() {
        return this.additionalDecayRate;
    }

    public boolean isWorldGeneration() {
        return this.isWorldGeneration;
    }

    @VisibleForTesting
    public List<ChargeCursor> getCursors() {
        return this.cursors;
    }

    public void clear() {
        this.cursors.clear();
    }

    public void load(CompoundTag p_222270_) {
        if (p_222270_.contains("cursors", 9)) {
            this.cursors.clear();
            DataResult var10000 = net.minecraft.world.level.block.SculkSpreader.ChargeCursor.CODEC.listOf().parse(new Dynamic(NbtOps.INSTANCE, p_222270_.getList("cursors", 10)));
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            List<ChargeCursor> $$1 = (List)var10000.resultOrPartial(var10001::error).orElseGet(ArrayList::new);
            int $$2 = Math.min($$1.size(), 32);

            for(int $$3 = 0; $$3 < $$2; ++$$3) {
                this.addCursor((ChargeCursor)$$1.get($$3));
            }
        }

    }

    public void save(CompoundTag p_222276_) {
        DataResult var10000 = net.minecraft.world.level.block.SculkSpreader.ChargeCursor.CODEC.listOf().encodeStart(NbtOps.INSTANCE, this.cursors);
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((p_222273_) -> {
            p_222276_.put("cursors", p_222273_);
        });
    }

    public void addCursors(BlockPos p_222267_, int p_222268_) {
        while(p_222268_ > 0) {
            int $$2 = Math.min(p_222268_, 1000);
            this.addCursor(new ChargeCursor(p_222267_, $$2));
            p_222268_ -= $$2;
        }

    }

    private void addCursor(ChargeCursor p_222261_) {
        if (this.cursors.size() < 32) {
            this.cursors.add(p_222261_);
        }
    }

    public void updateCursors(LevelAccessor p_222256_, BlockPos p_222257_, RandomSource p_222258_, boolean p_222259_) {
        if (!this.cursors.isEmpty()) {
            List<ChargeCursor> $$4 = new ArrayList();
            Map<BlockPos, ChargeCursor> $$5 = new HashMap();
            Object2IntMap<BlockPos> $$6 = new Object2IntOpenHashMap();
            Iterator var8 = this.cursors.iterator();

            while(true) {
                BlockPos $$8;
                while(var8.hasNext()) {
                    ChargeCursor $$7 = (ChargeCursor)var8.next();
                    $$7.update(p_222256_, p_222257_, p_222258_, this, p_222259_);
                    if ($$7.charge <= 0) {
                        p_222256_.levelEvent(3006, $$7.getPos(), 0);
                    } else {
                        $$8 = $$7.getPos();
                        $$6.computeInt($$8, (p_222264_, p_222265_) -> {
                            return (p_222265_ == null ? 0 : p_222265_) + $$7.charge;
                        });
                        ChargeCursor $$9 = (ChargeCursor)$$5.get($$8);
                        if ($$9 == null) {
                            $$5.put($$8, $$7);
                            $$4.add($$7);
                        } else if (!this.isWorldGeneration() && $$7.charge + $$9.charge <= 1000) {
                            $$9.mergeWith($$7);
                        } else {
                            $$4.add($$7);
                            if ($$7.charge < $$9.charge) {
                                $$5.put($$8, $$7);
                            }
                        }
                    }
                }

                ObjectIterator var16 = $$6.object2IntEntrySet().iterator();

                while(var16.hasNext()) {
                    Object2IntMap.Entry<BlockPos> $$10 = (Object2IntMap.Entry)var16.next();
                    $$8 = (BlockPos)$$10.getKey();
                    int $$12 = $$10.getIntValue();
                    ChargeCursor $$13 = (ChargeCursor)$$5.get($$8);
                    Collection<Direction> $$14 = $$13 == null ? null : $$13.getFacingData();
                    if ($$12 > 0 && $$14 != null) {
                        int $$15 = (int)(Math.log1p((double)$$12) / 2.299999952316284) + 1;
                        int $$16 = ($$15 << 6) + MultifaceBlock.pack($$14);
                        p_222256_.levelEvent(3006, $$8, $$16);
                    }
                }

                this.cursors = $$4;
                return;
            }
        }
    }

    public static class ChargeCursor {
        private static final ObjectArrayList<Vec3i> NON_CORNER_NEIGHBOURS = (ObjectArrayList)Util.make(new ObjectArrayList(18), (p_222338_) -> {
            Stream var10000 = BlockPos.betweenClosedStream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1)).filter((p_222336_) -> {
                return (p_222336_.getX() == 0 || p_222336_.getY() == 0 || p_222336_.getZ() == 0) && !p_222336_.equals(BlockPos.ZERO);
            }).map(BlockPos::immutable);
            Objects.requireNonNull(p_222338_);
            var10000.forEach(p_222338_::add);
        });
        public static final int MAX_CURSOR_DECAY_DELAY = 1;
        private BlockPos pos;
        int charge;
        private int updateDelay;
        private int decayDelay;
        @Nullable
        private Set<Direction> facings;
        private static final Codec<Set<Direction>> DIRECTION_SET;
        public static final Codec<ChargeCursor> CODEC;

        private ChargeCursor(BlockPos p_222299_, int p_222300_, int p_222301_, int p_222302_, Optional<Set<Direction>> p_222303_) {
            this.pos = p_222299_;
            this.charge = p_222300_;
            this.decayDelay = p_222301_;
            this.updateDelay = p_222302_;
            this.facings = (Set)p_222303_.orElse((Object)null);
        }

        public ChargeCursor(BlockPos p_222296_, int p_222297_) {
            this(p_222296_, p_222297_, 1, 0, Optional.empty());
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public int getCharge() {
            return this.charge;
        }

        public int getDecayDelay() {
            return this.decayDelay;
        }

        @Nullable
        public Set<Direction> getFacingData() {
            return this.facings;
        }

        private boolean shouldUpdate(LevelAccessor p_222326_, BlockPos p_222327_, boolean p_222328_) {
            if (this.charge <= 0) {
                return false;
            } else if (p_222328_) {
                return true;
            } else if (p_222326_ instanceof ServerLevel) {
                ServerLevel $$3 = (ServerLevel)p_222326_;
                return $$3.shouldTickBlocksAt(p_222327_);
            } else {
                return false;
            }
        }

        public void update(LevelAccessor p_222312_, BlockPos p_222313_, RandomSource p_222314_, SculkSpreader p_222315_, boolean p_222316_) {
            if (this.shouldUpdate(p_222312_, p_222313_, p_222315_.isWorldGeneration)) {
                if (this.updateDelay > 0) {
                    --this.updateDelay;
                } else {
                    BlockState $$5 = p_222312_.getBlockState(this.pos);
                    SculkBehaviour $$6 = getBlockBehaviour($$5);
                    if (p_222316_ && $$6.attemptSpreadVein(p_222312_, this.pos, $$5, this.facings, p_222315_.isWorldGeneration())) {
                        if ($$6.canChangeBlockStateOnSpread()) {
                            $$5 = p_222312_.getBlockState(this.pos);
                            $$6 = getBlockBehaviour($$5);
                        }

                        p_222312_.playSound((Player)null, this.pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    this.charge = $$6.attemptUseCharge(this, p_222312_, p_222313_, p_222314_, p_222315_, p_222316_);
                    if (this.charge <= 0) {
                        $$6.onDischarged(p_222312_, $$5, this.pos, p_222314_);
                    } else {
                        BlockPos $$7 = getValidMovementPos(p_222312_, this.pos, p_222314_);
                        if ($$7 != null) {
                            $$6.onDischarged(p_222312_, $$5, this.pos, p_222314_);
                            this.pos = $$7.immutable();
                            if (p_222315_.isWorldGeneration() && !this.pos.closerThan(new Vec3i(p_222313_.getX(), this.pos.getY(), p_222313_.getZ()), 15.0)) {
                                this.charge = 0;
                                return;
                            }

                            $$5 = p_222312_.getBlockState($$7);
                        }

                        if ($$5.getBlock() instanceof SculkBehaviour) {
                            this.facings = MultifaceBlock.availableFaces($$5);
                        }

                        this.decayDelay = $$6.updateDecayDelay(this.decayDelay);
                        this.updateDelay = $$6.getSculkSpreadDelay();
                    }
                }
            }
        }

        void mergeWith(ChargeCursor p_222332_) {
            this.charge += p_222332_.charge;
            p_222332_.charge = 0;
            this.updateDelay = Math.min(this.updateDelay, p_222332_.updateDelay);
        }

        private static SculkBehaviour getBlockBehaviour(BlockState p_222334_) {
            Block var2 = p_222334_.getBlock();
            SculkBehaviour var10000;
            if (var2 instanceof SculkBehaviour $$1) {
                var10000 = $$1;
            } else {
                var10000 = SculkBehaviour.DEFAULT;
            }

            return var10000;
        }

        private static List<Vec3i> getRandomizedNonCornerNeighbourOffsets(RandomSource p_222306_) {
            return Util.shuffledCopy(NON_CORNER_NEIGHBOURS, p_222306_);
        }

        @Nullable
        private static BlockPos getValidMovementPos(LevelAccessor p_222308_, BlockPos p_222309_, RandomSource p_222310_) {
            BlockPos.MutableBlockPos $$3 = p_222309_.mutable();
            BlockPos.MutableBlockPos $$4 = p_222309_.mutable();
            Iterator var5 = getRandomizedNonCornerNeighbourOffsets(p_222310_).iterator();

            while(var5.hasNext()) {
                Vec3i $$5 = (Vec3i)var5.next();
                $$4.setWithOffset(p_222309_, (Vec3i)$$5);
                BlockState $$6 = p_222308_.getBlockState($$4);
                if ($$6.getBlock() instanceof SculkBehaviour && isMovementUnobstructed(p_222308_, p_222309_, $$4)) {
                    $$3.set($$4);
                    if (SculkVeinBlock.hasSubstrateAccess(p_222308_, $$6, $$4)) {
                        break;
                    }
                }
            }

            return $$3.equals(p_222309_) ? null : $$3;
        }

        private static boolean isMovementUnobstructed(LevelAccessor p_222318_, BlockPos p_222319_, BlockPos p_222320_) {
            if (p_222319_.distManhattan(p_222320_) == 1) {
                return true;
            } else {
                BlockPos $$3 = p_222320_.subtract(p_222319_);
                Direction $$4 = Direction.fromAxisAndDirection(Axis.X, $$3.getX() < 0 ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
                Direction $$5 = Direction.fromAxisAndDirection(Axis.Y, $$3.getY() < 0 ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
                Direction $$6 = Direction.fromAxisAndDirection(Axis.Z, $$3.getZ() < 0 ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
                if ($$3.getX() == 0) {
                    return isUnobstructed(p_222318_, p_222319_, $$5) || isUnobstructed(p_222318_, p_222319_, $$6);
                } else if ($$3.getY() == 0) {
                    return isUnobstructed(p_222318_, p_222319_, $$4) || isUnobstructed(p_222318_, p_222319_, $$6);
                } else {
                    return isUnobstructed(p_222318_, p_222319_, $$4) || isUnobstructed(p_222318_, p_222319_, $$5);
                }
            }
        }

        private static boolean isUnobstructed(LevelAccessor p_222322_, BlockPos p_222323_, Direction p_222324_) {
            BlockPos $$3 = p_222323_.relative(p_222324_);
            return !p_222322_.getBlockState($$3).isFaceSturdy(p_222322_, $$3, p_222324_.getOpposite());
        }

        static {
            DIRECTION_SET = Direction.CODEC.listOf().xmap((p_222340_) -> {
                return Sets.newEnumSet(p_222340_, Direction.class);
            }, Lists::newArrayList);
            CODEC = RecordCodecBuilder.create((p_222330_) -> {
                return p_222330_.group(BlockPos.CODEC.fieldOf("pos").forGetter(ChargeCursor::getPos), Codec.intRange(0, 1000).fieldOf("charge").orElse(0).forGetter(ChargeCursor::getCharge), Codec.intRange(0, 1).fieldOf("decay_delay").orElse(1).forGetter(ChargeCursor::getDecayDelay), Codec.intRange(0, Integer.MAX_VALUE).fieldOf("update_delay").orElse(0).forGetter((p_222346_) -> {
                    return p_222346_.updateDelay;
                }), DIRECTION_SET.optionalFieldOf("facings").forGetter((p_222343_) -> {
                    return Optional.ofNullable(p_222343_.getFacingData());
                })).apply(p_222330_, ChargeCursor::new);
            });
        }
    }
}
