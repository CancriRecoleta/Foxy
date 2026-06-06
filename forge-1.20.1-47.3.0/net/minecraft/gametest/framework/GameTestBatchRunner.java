//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.gametest.framework;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class GameTestBatchRunner {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BlockPos firstTestNorthWestCorner;
    final ServerLevel level;
    private final GameTestTicker testTicker;
    private final int testsPerRow;
    private final List<GameTestInfo> allTestInfos;
    private final List<Pair<GameTestBatch, Collection<GameTestInfo>>> batches;
    private final BlockPos.MutableBlockPos nextTestNorthWestCorner;

    public GameTestBatchRunner(Collection<GameTestBatch> p_127563_, BlockPos p_127564_, Rotation p_127565_, ServerLevel p_127566_, GameTestTicker p_127567_, int p_127568_) {
        this.nextTestNorthWestCorner = p_127564_.mutable();
        this.firstTestNorthWestCorner = p_127564_;
        this.level = p_127566_;
        this.testTicker = p_127567_;
        this.testsPerRow = p_127568_;
        this.batches = (List)p_127563_.stream().map((p_177068_) -> {
            Collection<GameTestInfo> $$3 = (Collection)p_177068_.getTestFunctions().stream().map((p_177072_) -> {
                return new GameTestInfo(p_177072_, p_127565_, p_127566_);
            }).collect(ImmutableList.toImmutableList());
            return Pair.of(p_177068_, $$3);
        }).collect(ImmutableList.toImmutableList());
        this.allTestInfos = (List)this.batches.stream().flatMap((p_177074_) -> {
            return ((Collection)p_177074_.getSecond()).stream();
        }).collect(ImmutableList.toImmutableList());
    }

    public List<GameTestInfo> getTestInfos() {
        return this.allTestInfos;
    }

    public void start() {
        this.runBatch(0);
    }

    void runBatch(final int p_127571_) {
        if (p_127571_ < this.batches.size()) {
            Pair<GameTestBatch, Collection<GameTestInfo>> $$1 = (Pair)this.batches.get(p_127571_);
            final GameTestBatch $$2 = (GameTestBatch)$$1.getFirst();
            Collection<GameTestInfo> $$3 = (Collection)$$1.getSecond();
            Map<GameTestInfo, BlockPos> $$4 = this.createStructuresForBatch($$3);
            String $$5 = $$2.getName();
            LOGGER.info("Running test batch '{}' ({} tests)...", $$5, $$3.size());
            $$2.runBeforeBatchFunction(this.level);
            final MultipleTestTracker $$6 = new MultipleTestTracker();
            Objects.requireNonNull($$6);
            $$3.forEach($$6::addTestToTrack);
            $$6.addListener(new GameTestListener() {
                private void testCompleted() {
                    if ($$6.isDone()) {
                        $$2.runAfterBatchFunction(GameTestBatchRunner.this.level);
                        GameTestBatchRunner.this.runBatch(p_127571_ + 1);
                    }

                }

                public void testStructureLoaded(GameTestInfo p_127590_) {
                }

                public void testPassed(GameTestInfo p_177090_) {
                    this.testCompleted();
                }

                public void testFailed(GameTestInfo p_127592_) {
                    this.testCompleted();
                }
            });
            $$3.forEach((p_177079_) -> {
                BlockPos $$2 = (BlockPos)$$4.get(p_177079_);
                GameTestRunner.runTest(p_177079_, $$2, this.testTicker);
            });
        }
    }

    private Map<GameTestInfo, BlockPos> createStructuresForBatch(Collection<GameTestInfo> p_177076_) {
        Map<GameTestInfo, BlockPos> $$1 = Maps.newHashMap();
        int $$2 = 0;
        AABB $$3 = new AABB(this.nextTestNorthWestCorner);
        Iterator var5 = p_177076_.iterator();

        while(var5.hasNext()) {
            GameTestInfo $$4 = (GameTestInfo)var5.next();
            BlockPos $$5 = new BlockPos(this.nextTestNorthWestCorner);
            StructureBlockEntity $$6 = StructureUtils.spawnStructure($$4.getStructureName(), $$5, $$4.getRotation(), 2, this.level, true);
            AABB $$7 = StructureUtils.getStructureBounds($$6);
            $$4.setStructureBlockPos($$6.getBlockPos());
            $$1.put($$4, new BlockPos(this.nextTestNorthWestCorner));
            $$3 = $$3.minmax($$7);
            this.nextTestNorthWestCorner.move((int)$$7.getXsize() + 5, 0, 0);
            if ($$2++ % this.testsPerRow == this.testsPerRow - 1) {
                this.nextTestNorthWestCorner.move(0, 0, (int)$$3.getZsize() + 6);
                this.nextTestNorthWestCorner.setX(this.firstTestNorthWestCorner.getX());
                $$3 = new AABB(this.nextTestNorthWestCorner);
            }
        }

        return $$1;
    }
}
