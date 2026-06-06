//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class CarvedPumpkinBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING;
    @Nullable
    private BlockPattern snowGolemBase;
    @Nullable
    private BlockPattern snowGolemFull;
    @Nullable
    private BlockPattern ironGolemBase;
    @Nullable
    private BlockPattern ironGolemFull;
    private static final Predicate<BlockState> PUMPKINS_PREDICATE;

    public CarvedPumpkinBlock(BlockBehaviour.Properties p_51375_) {
        super(p_51375_);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
    }

    public void onPlace(BlockState p_51387_, Level p_51388_, BlockPos p_51389_, BlockState p_51390_, boolean p_51391_) {
        if (!p_51390_.is(p_51387_.getBlock())) {
            this.trySpawnGolem(p_51388_, p_51389_);
        }
    }

    public boolean canSpawnGolem(LevelReader p_51382_, BlockPos p_51383_) {
        return this.getOrCreateSnowGolemBase().find(p_51382_, p_51383_) != null || this.getOrCreateIronGolemBase().find(p_51382_, p_51383_) != null;
    }

    private void trySpawnGolem(Level p_51379_, BlockPos p_51380_) {
        BlockPattern.BlockPatternMatch $$2 = this.getOrCreateSnowGolemFull().find(p_51379_, p_51380_);
        if ($$2 != null) {
            SnowGolem $$3 = (SnowGolem)EntityType.SNOW_GOLEM.create(p_51379_);
            if ($$3 != null) {
                spawnGolemInWorld(p_51379_, $$2, $$3, $$2.getBlock(0, 2, 0).getPos());
            }
        } else {
            BlockPattern.BlockPatternMatch $$4 = this.getOrCreateIronGolemFull().find(p_51379_, p_51380_);
            if ($$4 != null) {
                IronGolem $$5 = (IronGolem)EntityType.IRON_GOLEM.create(p_51379_);
                if ($$5 != null) {
                    $$5.setPlayerCreated(true);
                    spawnGolemInWorld(p_51379_, $$4, $$5, $$4.getBlock(1, 2, 0).getPos());
                }
            }
        }

    }

    private static void spawnGolemInWorld(Level p_249110_, BlockPattern.BlockPatternMatch p_251293_, Entity p_251251_, BlockPos p_251189_) {
        clearPatternBlocks(p_249110_, p_251293_);
        p_251251_.moveTo((double)p_251189_.getX() + 0.5, (double)p_251189_.getY() + 0.05, (double)p_251189_.getZ() + 0.5, 0.0F, 0.0F);
        p_249110_.addFreshEntity(p_251251_);
        Iterator var4 = p_249110_.getEntitiesOfClass(ServerPlayer.class, p_251251_.getBoundingBox().inflate(5.0)).iterator();

        while(var4.hasNext()) {
            ServerPlayer $$4 = (ServerPlayer)var4.next();
            CriteriaTriggers.SUMMONED_ENTITY.trigger($$4, p_251251_);
        }

        updatePatternBlocks(p_249110_, p_251293_);
    }

    public static void clearPatternBlocks(Level p_249604_, BlockPattern.BlockPatternMatch p_251190_) {
        for(int $$2 = 0; $$2 < p_251190_.getWidth(); ++$$2) {
            for(int $$3 = 0; $$3 < p_251190_.getHeight(); ++$$3) {
                BlockInWorld $$4 = p_251190_.getBlock($$2, $$3, 0);
                p_249604_.setBlock($$4.getPos(), Blocks.AIR.defaultBlockState(), 2);
                p_249604_.levelEvent(2001, $$4.getPos(), Block.getId($$4.getState()));
            }
        }

    }

    public static void updatePatternBlocks(Level p_248711_, BlockPattern.BlockPatternMatch p_251935_) {
        for(int $$2 = 0; $$2 < p_251935_.getWidth(); ++$$2) {
            for(int $$3 = 0; $$3 < p_251935_.getHeight(); ++$$3) {
                BlockInWorld $$4 = p_251935_.getBlock($$2, $$3, 0);
                p_248711_.blockUpdated($$4.getPos(), Blocks.AIR);
            }
        }

    }

    public BlockState getStateForPlacement(BlockPlaceContext p_51377_) {
        return (BlockState)this.defaultBlockState().setValue(FACING, p_51377_.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51385_) {
        p_51385_.add(FACING);
    }

    private BlockPattern getOrCreateSnowGolemBase() {
        if (this.snowGolemBase == null) {
            this.snowGolemBase = BlockPatternBuilder.start().aisle(" ", "#", "#").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }

        return this.snowGolemBase;
    }

    private BlockPattern getOrCreateSnowGolemFull() {
        if (this.snowGolemFull == null) {
            this.snowGolemFull = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }

        return this.snowGolemFull;
    }

    private BlockPattern getOrCreateIronGolemBase() {
        if (this.ironGolemBase == null) {
            this.ironGolemBase = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', (p_284869_) -> {
                return p_284869_.getState().isAir();
            }).build();
        }

        return this.ironGolemBase;
    }

    private BlockPattern getOrCreateIronGolemFull() {
        if (this.ironGolemFull == null) {
            this.ironGolemFull = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', (p_284868_) -> {
                return p_284868_.getState().isAir();
            }).build();
        }

        return this.ironGolemFull;
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        PUMPKINS_PREDICATE = (p_51396_) -> {
            return p_51396_ != null && (p_51396_.is(Blocks.CARVED_PUMPKIN) || p_51396_.is(Blocks.JACK_O_LANTERN));
        };
    }
}
