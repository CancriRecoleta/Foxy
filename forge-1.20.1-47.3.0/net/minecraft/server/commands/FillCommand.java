//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FillCommand {
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_137392_, p_137393_) -> {
        return Component.translatable("commands.fill.toobig", p_137392_, p_137393_);
    });
    static final BlockInput HOLLOW_CORE;
    private static final SimpleCommandExceptionType ERROR_FAILED;

    public FillCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_214443_, CommandBuildContext p_214444_) {
        p_214443_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fill").requires((p_137384_) -> {
            return p_137384_.hasPermission(2);
        })).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", BlockStateArgument.block(p_214444_)).executes((p_137405_) -> {
            return fillBlocks((CommandSourceStack)p_137405_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137405_, "from"), BlockPosArgument.getLoadedBlockPos(p_137405_, "to")), BlockStateArgument.getBlock(p_137405_, "block"), net.minecraft.server.commands.FillCommand.Mode.REPLACE, (Predicate)null);
        })).then(((LiteralArgumentBuilder)Commands.literal("replace").executes((p_137403_) -> {
            return fillBlocks((CommandSourceStack)p_137403_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137403_, "from"), BlockPosArgument.getLoadedBlockPos(p_137403_, "to")), BlockStateArgument.getBlock(p_137403_, "block"), net.minecraft.server.commands.FillCommand.Mode.REPLACE, (Predicate)null);
        })).then(Commands.argument("filter", BlockPredicateArgument.blockPredicate(p_214444_)).executes((p_137401_) -> {
            return fillBlocks((CommandSourceStack)p_137401_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137401_, "from"), BlockPosArgument.getLoadedBlockPos(p_137401_, "to")), BlockStateArgument.getBlock(p_137401_, "block"), net.minecraft.server.commands.FillCommand.Mode.REPLACE, BlockPredicateArgument.getBlockPredicate(p_137401_, "filter"));
        })))).then(Commands.literal("keep").executes((p_137399_) -> {
            return fillBlocks((CommandSourceStack)p_137399_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137399_, "from"), BlockPosArgument.getLoadedBlockPos(p_137399_, "to")), BlockStateArgument.getBlock(p_137399_, "block"), net.minecraft.server.commands.FillCommand.Mode.REPLACE, (p_180225_) -> {
                return p_180225_.getLevel().isEmptyBlock(p_180225_.getPos());
            });
        }))).then(Commands.literal("outline").executes((p_137397_) -> {
            return fillBlocks((CommandSourceStack)p_137397_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137397_, "from"), BlockPosArgument.getLoadedBlockPos(p_137397_, "to")), BlockStateArgument.getBlock(p_137397_, "block"), net.minecraft.server.commands.FillCommand.Mode.OUTLINE, (Predicate)null);
        }))).then(Commands.literal("hollow").executes((p_137395_) -> {
            return fillBlocks((CommandSourceStack)p_137395_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137395_, "from"), BlockPosArgument.getLoadedBlockPos(p_137395_, "to")), BlockStateArgument.getBlock(p_137395_, "block"), net.minecraft.server.commands.FillCommand.Mode.HOLLOW, (Predicate)null);
        }))).then(Commands.literal("destroy").executes((p_137382_) -> {
            return fillBlocks((CommandSourceStack)p_137382_.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos(p_137382_, "from"), BlockPosArgument.getLoadedBlockPos(p_137382_, "to")), BlockStateArgument.getBlock(p_137382_, "block"), net.minecraft.server.commands.FillCommand.Mode.DESTROY, (Predicate)null);
        }))))));
    }

    private static int fillBlocks(CommandSourceStack p_137386_, BoundingBox p_137387_, BlockInput p_137388_, Mode p_137389_, @Nullable Predicate<BlockInWorld> p_137390_) throws CommandSyntaxException {
        int $$5 = p_137387_.getXSpan() * p_137387_.getYSpan() * p_137387_.getZSpan();
        int $$6 = p_137386_.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
        if ($$5 > $$6) {
            throw ERROR_AREA_TOO_LARGE.create($$6, $$5);
        } else {
            List<BlockPos> $$7 = Lists.newArrayList();
            ServerLevel $$8 = p_137386_.getLevel();
            int $$9 = 0;
            Iterator var10 = BlockPos.betweenClosed(p_137387_.minX(), p_137387_.minY(), p_137387_.minZ(), p_137387_.maxX(), p_137387_.maxY(), p_137387_.maxZ()).iterator();

            while(true) {
                BlockPos $$10;
                do {
                    if (!var10.hasNext()) {
                        var10 = $$7.iterator();

                        while(var10.hasNext()) {
                            $$10 = (BlockPos)var10.next();
                            Block $$14 = $$8.getBlockState($$10).getBlock();
                            $$8.blockUpdated($$10, $$14);
                        }

                        if ($$9 == 0) {
                            throw ERROR_FAILED.create();
                        }

                        p_137386_.sendSuccess(() -> {
                            return Component.translatable("commands.fill.success", $$9);
                        }, true);
                        return $$9;
                    }

                    $$10 = (BlockPos)var10.next();
                } while(p_137390_ != null && !p_137390_.test(new BlockInWorld($$8, $$10, true)));

                BlockInput $$11 = p_137389_.filter.filter(p_137387_, $$10, p_137388_, $$8);
                if ($$11 != null) {
                    BlockEntity $$12 = $$8.getBlockEntity($$10);
                    Clearable.tryClear($$12);
                    if ($$11.place($$8, $$10, 2)) {
                        $$7.add($$10.immutable());
                        ++$$9;
                    }
                }
            }
        }
    }

    static {
        HOLLOW_CORE = new BlockInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), (CompoundTag)null);
        ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.fill.failed"));
    }

    private static enum Mode {
        REPLACE((p_137433_, p_137434_, p_137435_, p_137436_) -> {
            return p_137435_;
        }),
        OUTLINE((p_137428_, p_137429_, p_137430_, p_137431_) -> {
            return p_137429_.getX() != p_137428_.minX() && p_137429_.getX() != p_137428_.maxX() && p_137429_.getY() != p_137428_.minY() && p_137429_.getY() != p_137428_.maxY() && p_137429_.getZ() != p_137428_.minZ() && p_137429_.getZ() != p_137428_.maxZ() ? null : p_137430_;
        }),
        HOLLOW((p_137423_, p_137424_, p_137425_, p_137426_) -> {
            return p_137424_.getX() != p_137423_.minX() && p_137424_.getX() != p_137423_.maxX() && p_137424_.getY() != p_137423_.minY() && p_137424_.getY() != p_137423_.maxY() && p_137424_.getZ() != p_137423_.minZ() && p_137424_.getZ() != p_137423_.maxZ() ? FillCommand.HOLLOW_CORE : p_137425_;
        }),
        DESTROY((p_137418_, p_137419_, p_137420_, p_137421_) -> {
            p_137421_.destroyBlock(p_137419_, true);
            return p_137420_;
        });

        public final SetBlockCommand.Filter filter;

        private Mode(SetBlockCommand.Filter p_137416_) {
            this.filter = p_137416_;
        }
    }
}
