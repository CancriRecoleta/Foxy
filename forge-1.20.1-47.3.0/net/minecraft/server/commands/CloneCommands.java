//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class CloneCommands {
    private static final SimpleCommandExceptionType ERROR_OVERLAP = new SimpleCommandExceptionType(Component.translatable("commands.clone.overlap"));
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_136743_, p_136744_) -> {
        return Component.translatable("commands.clone.toobig", p_136743_, p_136744_);
    });
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.clone.failed"));
    public static final Predicate<BlockInWorld> FILTER_AIR = (p_284652_) -> {
        return !p_284652_.getState().isAir();
    };

    public CloneCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_214424_, CommandBuildContext p_214425_) {
        p_214424_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clone").requires((p_136734_) -> {
            return p_136734_.hasPermission(2);
        })).then(beginEndDestinationAndModeSuffix(p_214425_, (p_264757_) -> {
            return ((CommandSourceStack)p_264757_.getSource()).getLevel();
        }))).then(Commands.literal("from").then(Commands.argument("sourceDimension", DimensionArgument.dimension()).then(beginEndDestinationAndModeSuffix(p_214425_, (p_264743_) -> {
            return DimensionArgument.getDimension(p_264743_, "sourceDimension");
        })))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> beginEndDestinationAndModeSuffix(CommandBuildContext p_265681_, CommandFunction<CommandContext<CommandSourceStack>, ServerLevel> p_265514_) {
        return Commands.argument("begin", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("end", BlockPosArgument.blockPos()).then(destinationAndModeSuffix(p_265681_, p_265514_, (p_264751_) -> {
            return ((CommandSourceStack)p_264751_.getSource()).getLevel();
        }))).then(Commands.literal("to").then(Commands.argument("targetDimension", DimensionArgument.dimension()).then(destinationAndModeSuffix(p_265681_, p_265514_, (p_264756_) -> {
            return DimensionArgument.getDimension(p_264756_, "targetDimension");
        })))));
    }

    private static DimensionAndPosition getLoadedDimensionAndPosition(CommandContext<CommandSourceStack> p_265513_, ServerLevel p_265183_, String p_265511_) throws CommandSyntaxException {
        BlockPos $$3 = BlockPosArgument.getLoadedBlockPos(p_265513_, p_265183_, p_265511_);
        return new DimensionAndPosition(p_265183_, $$3);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> destinationAndModeSuffix(CommandBuildContext p_265238_, CommandFunction<CommandContext<CommandSourceStack>, ServerLevel> p_265621_, CommandFunction<CommandContext<CommandSourceStack>, ServerLevel> p_265296_) {
        CommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> $$3 = (p_264737_) -> {
            return getLoadedDimensionAndPosition(p_264737_, (ServerLevel)p_265621_.apply(p_264737_), "begin");
        };
        CommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> $$4 = (p_264735_) -> {
            return getLoadedDimensionAndPosition(p_264735_, (ServerLevel)p_265621_.apply(p_264735_), "end");
        };
        CommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> $$5 = (p_264768_) -> {
            return getLoadedDimensionAndPosition(p_264768_, (ServerLevel)p_265296_.apply(p_264768_), "destination");
        };
        return ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).executes((p_264761_) -> {
            return clone((CommandSourceStack)p_264761_.getSource(), (DimensionAndPosition)$$3.apply(p_264761_), (DimensionAndPosition)$$4.apply(p_264761_), (DimensionAndPosition)$$5.apply(p_264761_), (p_180033_) -> {
                return true;
            }, net.minecraft.server.commands.CloneCommands.Mode.NORMAL);
        })).then(wrapWithCloneMode($$3, $$4, $$5, (p_264738_) -> {
            return (p_180041_) -> {
                return true;
            };
        }, Commands.literal("replace").executes((p_264755_) -> {
            return clone((CommandSourceStack)p_264755_.getSource(), (DimensionAndPosition)$$3.apply(p_264755_), (DimensionAndPosition)$$4.apply(p_264755_), (DimensionAndPosition)$$5.apply(p_264755_), (p_180039_) -> {
                return true;
            }, net.minecraft.server.commands.CloneCommands.Mode.NORMAL);
        })))).then(wrapWithCloneMode($$3, $$4, $$5, (p_264744_) -> {
            return FILTER_AIR;
        }, Commands.literal("masked").executes((p_264742_) -> {
            return clone((CommandSourceStack)p_264742_.getSource(), (DimensionAndPosition)$$3.apply(p_264742_), (DimensionAndPosition)$$4.apply(p_264742_), (DimensionAndPosition)$$5.apply(p_264742_), FILTER_AIR, net.minecraft.server.commands.CloneCommands.Mode.NORMAL);
        })))).then(Commands.literal("filtered").then(wrapWithCloneMode($$3, $$4, $$5, (p_264745_) -> {
            return BlockPredicateArgument.getBlockPredicate(p_264745_, "filter");
        }, Commands.argument("filter", BlockPredicateArgument.blockPredicate(p_265238_)).executes((p_264733_) -> {
            return clone((CommandSourceStack)p_264733_.getSource(), (DimensionAndPosition)$$3.apply(p_264733_), (DimensionAndPosition)$$4.apply(p_264733_), (DimensionAndPosition)$$5.apply(p_264733_), BlockPredicateArgument.getBlockPredicate(p_264733_, "filter"), net.minecraft.server.commands.CloneCommands.Mode.NORMAL);
        }))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> wrapWithCloneMode(CommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> p_265374_, CommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> p_265134_, CommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> p_265546_, CommandFunction<CommandContext<CommandSourceStack>, Predicate<BlockInWorld>> p_265798_, ArgumentBuilder<CommandSourceStack, ?> p_265069_) {
        return p_265069_.then(Commands.literal("force").executes((p_264773_) -> {
            return clone((CommandSourceStack)p_264773_.getSource(), (DimensionAndPosition)p_265374_.apply(p_264773_), (DimensionAndPosition)p_265134_.apply(p_264773_), (DimensionAndPosition)p_265546_.apply(p_264773_), (Predicate)p_265798_.apply(p_264773_), net.minecraft.server.commands.CloneCommands.Mode.FORCE);
        })).then(Commands.literal("move").executes((p_264766_) -> {
            return clone((CommandSourceStack)p_264766_.getSource(), (DimensionAndPosition)p_265374_.apply(p_264766_), (DimensionAndPosition)p_265134_.apply(p_264766_), (DimensionAndPosition)p_265546_.apply(p_264766_), (Predicate)p_265798_.apply(p_264766_), net.minecraft.server.commands.CloneCommands.Mode.MOVE);
        })).then(Commands.literal("normal").executes((p_264750_) -> {
            return clone((CommandSourceStack)p_264750_.getSource(), (DimensionAndPosition)p_265374_.apply(p_264750_), (DimensionAndPosition)p_265134_.apply(p_264750_), (DimensionAndPosition)p_265546_.apply(p_264750_), (Predicate)p_265798_.apply(p_264750_), net.minecraft.server.commands.CloneCommands.Mode.NORMAL);
        }));
    }

    private static int clone(CommandSourceStack p_265047_, DimensionAndPosition p_265232_, DimensionAndPosition p_265188_, DimensionAndPosition p_265594_, Predicate<BlockInWorld> p_265585_, Mode p_265530_) throws CommandSyntaxException {
        BlockPos $$6 = p_265232_.position();
        BlockPos $$7 = p_265188_.position();
        BoundingBox $$8 = BoundingBox.fromCorners($$6, $$7);
        BlockPos $$9 = p_265594_.position();
        BlockPos $$10 = $$9.offset($$8.getLength());
        BoundingBox $$11 = BoundingBox.fromCorners($$9, $$10);
        ServerLevel $$12 = p_265232_.dimension();
        ServerLevel $$13 = p_265594_.dimension();
        if (!p_265530_.canOverlap() && $$12 == $$13 && $$11.intersects($$8)) {
            throw ERROR_OVERLAP.create();
        } else {
            int $$14 = $$8.getXSpan() * $$8.getYSpan() * $$8.getZSpan();
            int $$15 = p_265047_.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
            if ($$14 > $$15) {
                throw ERROR_AREA_TOO_LARGE.create($$15, $$14);
            } else if ($$12.hasChunksAt($$6, $$7) && $$13.hasChunksAt($$9, $$10)) {
                List<CloneBlockInfo> $$16 = Lists.newArrayList();
                List<CloneBlockInfo> $$17 = Lists.newArrayList();
                List<CloneBlockInfo> $$18 = Lists.newArrayList();
                Deque<BlockPos> $$19 = Lists.newLinkedList();
                BlockPos $$20 = new BlockPos($$11.minX() - $$8.minX(), $$11.minY() - $$8.minY(), $$11.minZ() - $$8.minZ());

                int $$37;
                for(int $$21 = $$8.minZ(); $$21 <= $$8.maxZ(); ++$$21) {
                    for(int $$22 = $$8.minY(); $$22 <= $$8.maxY(); ++$$22) {
                        for($$37 = $$8.minX(); $$37 <= $$8.maxX(); ++$$37) {
                            BlockPos $$24 = new BlockPos($$37, $$22, $$21);
                            BlockPos $$25 = $$24.offset($$20);
                            BlockInWorld $$26 = new BlockInWorld($$12, $$24, false);
                            BlockState $$27 = $$26.getState();
                            if (p_265585_.test($$26)) {
                                BlockEntity $$28 = $$12.getBlockEntity($$24);
                                if ($$28 != null) {
                                    CompoundTag $$29 = $$28.saveWithoutMetadata();
                                    $$17.add(new CloneBlockInfo($$25, $$27, $$29));
                                    $$19.addLast($$24);
                                } else if (!$$27.isSolidRender($$12, $$24) && !$$27.isCollisionShapeFullBlock($$12, $$24)) {
                                    $$18.add(new CloneBlockInfo($$25, $$27, (CompoundTag)null));
                                    $$19.addFirst($$24);
                                } else {
                                    $$16.add(new CloneBlockInfo($$25, $$27, (CompoundTag)null));
                                    $$19.addLast($$24);
                                }
                            }
                        }
                    }
                }

                if (p_265530_ == net.minecraft.server.commands.CloneCommands.Mode.MOVE) {
                    Iterator var30 = $$19.iterator();

                    BlockPos $$32;
                    while(var30.hasNext()) {
                        $$32 = (BlockPos)var30.next();
                        BlockEntity $$31 = $$12.getBlockEntity($$32);
                        Clearable.tryClear($$31);
                        $$12.setBlock($$32, Blocks.BARRIER.defaultBlockState(), 2);
                    }

                    var30 = $$19.iterator();

                    while(var30.hasNext()) {
                        $$32 = (BlockPos)var30.next();
                        $$12.setBlock($$32, Blocks.AIR.defaultBlockState(), 3);
                    }
                }

                List<CloneBlockInfo> $$33 = Lists.newArrayList();
                $$33.addAll($$16);
                $$33.addAll($$17);
                $$33.addAll($$18);
                List<CloneBlockInfo> $$34 = Lists.reverse($$33);
                Iterator var35 = $$34.iterator();

                while(var35.hasNext()) {
                    CloneBlockInfo $$35 = (CloneBlockInfo)var35.next();
                    BlockEntity $$36 = $$13.getBlockEntity($$35.pos);
                    Clearable.tryClear($$36);
                    $$13.setBlock($$35.pos, Blocks.BARRIER.defaultBlockState(), 2);
                }

                $$37 = 0;
                Iterator var37 = $$33.iterator();

                CloneBlockInfo $$41;
                while(var37.hasNext()) {
                    $$41 = (CloneBlockInfo)var37.next();
                    if ($$13.setBlock($$41.pos, $$41.state, 2)) {
                        ++$$37;
                    }
                }

                for(var37 = $$17.iterator(); var37.hasNext(); $$13.setBlock($$41.pos, $$41.state, 2)) {
                    $$41 = (CloneBlockInfo)var37.next();
                    BlockEntity $$40 = $$13.getBlockEntity($$41.pos);
                    if ($$41.tag != null && $$40 != null) {
                        $$40.load($$41.tag);
                        $$40.setChanged();
                    }
                }

                var37 = $$34.iterator();

                while(var37.hasNext()) {
                    $$41 = (CloneBlockInfo)var37.next();
                    $$13.blockUpdated($$41.pos, $$41.state.getBlock());
                }

                $$13.getBlockTicks().copyAreaFrom($$12.getBlockTicks(), $$8, $$20);
                if ($$37 == 0) {
                    throw ERROR_FAILED.create();
                } else {
                    p_265047_.sendSuccess(() -> {
                        return Component.translatable("commands.clone.success", $$37);
                    }, true);
                    return $$37;
                }
            } else {
                throw BlockPosArgument.ERROR_NOT_LOADED.create();
            }
        }
    }

    @FunctionalInterface
    interface CommandFunction<T, R> {
        R apply(T var1) throws CommandSyntaxException;
    }

    private static record DimensionAndPosition(ServerLevel dimension, BlockPos position) {
        DimensionAndPosition(ServerLevel dimension, BlockPos position) {
            this.dimension = dimension;
            this.position = position;
        }

        public ServerLevel dimension() {
            return this.dimension;
        }

        public BlockPos position() {
            return this.position;
        }
    }

    private static enum Mode {
        FORCE(true),
        MOVE(true),
        NORMAL(false);

        private final boolean canOverlap;

        private Mode(boolean p_136795_) {
            this.canOverlap = p_136795_;
        }

        public boolean canOverlap() {
            return this.canOverlap;
        }
    }

    static class CloneBlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        @Nullable
        public final CompoundTag tag;

        public CloneBlockInfo(BlockPos p_136783_, BlockState p_136784_, @Nullable CompoundTag p_136785_) {
            this.pos = p_136783_;
            this.state = p_136784_;
            this.tag = p_136785_;
        }
    }
}
